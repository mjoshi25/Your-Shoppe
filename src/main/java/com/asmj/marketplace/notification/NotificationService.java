package com.asmj.marketplace.notification;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
 private final NotificationRepository notifications; private final UserAccountRepository users;
 public NotificationService(NotificationRepository notifications,UserAccountRepository users){this.notifications=notifications;this.users=users;}
 public void notifyUser(String userId,String title,String message,String targetType,String targetId){if(userId==null||userId.isBlank())return;MarketplaceNotification n=new MarketplaceNotification();n.setRecipientUserId(userId);n.setTitle(title);n.setMessage(message);n.setTargetType(targetType);n.setTargetId(targetId);n.setCreatedAt(Instant.now());notifications.save(n);}
 public void notifyAdmins(String title,String message,String targetType,String targetId){for(UserAccount u:users.findByRoleOrderByCreatedAtDesc(Role.ROLE_ADMIN))notifyUser(u.getId(),title,message,targetType,targetId);}
 public List<MarketplaceNotification> recent(String userId){return notifications.findTop100ByRecipientUserIdOrderByCreatedAtDesc(userId);}
 public long unread(String userId){return notifications.countByRecipientUserIdAndReadFalse(userId);}
 public MarketplaceNotification owned(String id,String userId){return notifications.findById(id).filter(n->userId.equals(n.getRecipientUserId())).orElseThrow();}
 public void markRead(MarketplaceNotification n){if(!n.isRead()){n.setRead(true);notifications.save(n);}}
 public void markAllRead(String userId){for(MarketplaceNotification n:recent(userId))if(!n.isRead()){n.setRead(true);notifications.save(n);}}
}
