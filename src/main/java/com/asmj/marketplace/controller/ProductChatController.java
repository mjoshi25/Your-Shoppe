package com.asmj.marketplace.controller;

import com.asmj.marketplace.chat.ChatMessage;
import com.asmj.marketplace.chat.ProductConversation;
import com.asmj.marketplace.chat.ProductConversationRepository;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.product.ProductStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductChatController {
    private final ProductRepository products; private final ProductConversationRepository conversations; private final UserAccountRepository users; private final CloudinaryImageService uploads;
    public ProductChatController(ProductRepository products, ProductConversationRepository conversations, UserAccountRepository users,CloudinaryImageService uploads){this.products=products;this.conversations=conversations;this.users=users;this.uploads=uploads;}

    @PostMapping("/chat/product/{productId}")
    public String start(@PathVariable String productId,@RequestParam(required=false) String buyerName,@RequestParam(required=false) String buyerMobile,
                       @RequestParam(required=false,defaultValue="") String message,@RequestParam(required=false) MultipartFile attachment,Principal principal,HttpSession session,RedirectAttributes ra){
        Product p=products.findById(productId).filter(x->x.getStatus()==ProductStatus.ACTIVE).orElseThrow(()->new IllegalArgumentException("This product is not available for enquiry."));
        if(((message==null||message.trim().isEmpty())&&(attachment==null||attachment.isEmpty()))||(message!=null&&message.trim().length()>2000)){ra.addFlashAttribute("errorMessage","Enter a message or attach a file. Message must be at most 2000 characters.");return "redirect:/products";}
        ProductConversation c=new ProductConversation();c.setProductId(p.getId());c.setProductName(p.getName());c.setProductImageUrl(p.getImageUrl());c.setVendorUserId(p.getVendorUserId());c.setVendorName(p.getVendorName());
        if(principal!=null){UserAccount buyer=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();if(buyer.getRole()!=Role.ROLE_CUSTOMER){ra.addFlashAttribute("errorMessage","Only buyers can start product enquiries.");return "redirect:/products";}c.setBuyerUserId(buyer.getId());c.setBuyerName(buyer.getFullName());c.setBuyerMobile(buyer.getMobile());c.setGuest(false);}
        else {String name=buyerName==null?"":buyerName.trim();String mobile=buyerMobile==null?"":buyerMobile.trim();int mobileDigits=mobile.replaceAll("\\D","").length();if(name.length()<2||name.length()>80||!mobile.matches("[+0-9() -]{8,20}")||mobileDigits<8||mobileDigits>15){ra.addFlashAttribute("errorMessage","Enter your name and a valid mobile number to start a guest enquiry.");return "redirect:/products";}String key=(String)session.getAttribute("asmjGuestChatKey");if(key==null){key=UUID.randomUUID().toString();session.setAttribute("asmjGuestChatKey",key);}c.setGuest(true);c.setGuestSessionKey(key);c.setBuyerName(name);c.setBuyerMobile(mobile);}
        ChatMessage first=new ChatMessage("BUYER",c.getBuyerName(),message==null?"":message.trim());if(attachment!=null&&!attachment.isEmpty()){try{Map<String,Object> f=uploads.uploadDocument(attachment,"asmj-marketplace/chat-attachments");first.getAttachments().add(new com.asmj.marketplace.chat.ChatAttachment(attachment.getOriginalFilename(),String.valueOf(f.get("secure_url")),attachment.getContentType()));}catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());return "redirect:/products";}}c.getMessages().add(first);c.setUpdatedAt(Instant.now());conversations.save(c);return "redirect:/chat/"+c.getId();
    }

    @GetMapping("/chat/{id}") public String view(@PathVariable String id,Principal principal,HttpSession session,Model model){ProductConversation c=conversations.findById(id).orElseThrow();String actor=actor(principal,session);if(!canView(c,actor,principal,(String)session.getAttribute("asmjGuestChatKey")))return "auth/access-denied";markRead(c,principal);model.addAttribute("conversation",c);model.addAttribute("chatMode",mode(principal));return "chat/conversation";}
    @GetMapping("/chat/{id}/messages")
    @ResponseBody
    public java.util.List<ChatMessage> messages(@PathVariable String id, Principal principal, HttpSession session) {
        ProductConversation c=conversations.findById(id).orElseThrow();
        if(!canView(c,actor(principal,session),principal,(String)session.getAttribute("asmjGuestChatKey")))
            throw new org.springframework.security.access.AccessDeniedException("You cannot view this conversation.");
        markRead(c,principal);
        return c.getMessages();
    }

    @PostMapping("/chat/{id}/message") public String send(@PathVariable String id,@RequestParam(required=false,defaultValue="") String message,@RequestParam(required=false) MultipartFile attachment,Principal principal,HttpSession session,RedirectAttributes ra){ProductConversation c=conversations.findById(id).orElseThrow();String actor=actor(principal,session);if(!canView(c,actor,principal,(String)session.getAttribute("asmjGuestChatKey")))return "auth/access-denied";String body=message==null?"":message.trim();if((body.isEmpty()&&(attachment==null||attachment.isEmpty()))||body.length()>2000){ra.addFlashAttribute("errorMessage","Enter a message or attach a file. Message must be at most 2000 characters.");return "redirect:/chat/"+id;}String type=principal==null?"BUYER":mode(principal);String name=principal==null?c.getBuyerName():users.findByEmailIgnoreCase(principal.getName()).map(UserAccount::getFullName).orElse("Marketplace user");ChatMessage reply=new ChatMessage(type,name,body);if(attachment!=null&&!attachment.isEmpty()){try{Map<String,Object> f=uploads.uploadDocument(attachment,"asmj-marketplace/chat-attachments");reply.getAttachments().add(new com.asmj.marketplace.chat.ChatAttachment(attachment.getOriginalFilename(),String.valueOf(f.get("secure_url")),attachment.getContentType()));}catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());return "redirect:/chat/"+id;}}c.getMessages().add(reply);c.setUpdatedAt(Instant.now());conversations.save(c);return "redirect:/chat/"+id;}

    @GetMapping("/vendor/enquiries") public String vendorInbox(Principal principal,Model model){UserAccount u=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();java.util.List<ProductConversation> list=conversations.findByVendorUserIdOrderByUpdatedAtDesc(u.getId());model.addAttribute("conversations",list);model.addAttribute("unreadCounts",unreadCounts(list,true));model.addAttribute("inboxTitle","Buyer enquiries");return "chat/inbox";}
    @GetMapping("/my-enquiries") public String buyerInbox(Principal principal,Model model){UserAccount u=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();java.util.List<ProductConversation> list=conversations.findByBuyerUserIdOrderByUpdatedAtDesc(u.getId());model.addAttribute("conversations",list);model.addAttribute("unreadCounts",unreadCounts(list,false));model.addAttribute("inboxTitle","My product enquiries");return "chat/inbox";}
    @GetMapping("/guest-enquiries") public String guestInbox(HttpSession session,Model model){String key=(String)session.getAttribute("asmjGuestChatKey");java.util.List<ProductConversation> list=key==null?java.util.List.of():conversations.findByGuestSessionKeyOrderByUpdatedAtDesc(key);model.addAttribute("conversations",list);model.addAttribute("unreadCounts",unreadCounts(list,false));model.addAttribute("inboxTitle","My guest enquiries");return "chat/inbox";}
    @GetMapping("/admin/enquiries") public String adminInbox(Model model){model.addAttribute("conversations",conversations.findAllByOrderByUpdatedAtDesc());model.addAttribute("inboxTitle","All product enquiries");return "chat/inbox";}
    private void markRead(ProductConversation c,Principal principal){
        if(c.getMessages()==null||c.getMessages().isEmpty())return;
        Instant latest=c.getMessages().get(c.getMessages().size()-1).getSentAt();
        if(latest==null)return;
        if(principal==null||mode(principal).equals("BUYER")){
            if(c.getBuyerLastReadAt()==null||latest.isAfter(c.getBuyerLastReadAt())){c.setBuyerLastReadAt(Instant.now());conversations.save(c);}
        }else if(mode(principal).equals("VENDOR")){
            if(c.getVendorLastReadAt()==null||latest.isAfter(c.getVendorLastReadAt())){c.setVendorLastReadAt(Instant.now());conversations.save(c);}
        }
    }
    private java.util.Map<String,Integer> unreadCounts(java.util.List<ProductConversation> list,boolean vendor){java.util.Map<String,Integer> counts=new java.util.HashMap<>();for(ProductConversation c:list){Instant lastRead=vendor?c.getVendorLastReadAt():c.getBuyerLastReadAt();int count=0;for(ChatMessage m:c.getMessages()){boolean fromOther=vendor?!"VENDOR".equals(m.getSenderType()):!"BUYER".equals(m.getSenderType());if(fromOther&&(lastRead==null||m.getSentAt().isAfter(lastRead)))count++;}counts.put(c.getId(),count);}return counts;}
    private String actor(Principal p,HttpSession s){if(p==null)return (String)s.getAttribute("asmjGuestChatKey");return users.findByEmailIgnoreCase(p.getName()).map(UserAccount::getId).orElse(null);}
    private boolean canView(ProductConversation c,String actor,Principal p,String guestKey){if(p==null)return c.isGuest()&&guestKey!=null&&guestKey.equals(c.getGuestSessionKey());UserAccount u=users.findByEmailIgnoreCase(p.getName()).orElse(null);if(u==null)return false;if(u.getRole()==Role.ROLE_ADMIN)return true;if(u.getRole()==Role.ROLE_VENDOR)return u.getId().equals(c.getVendorUserId());return u.getRole()==Role.ROLE_CUSTOMER&&(u.getId().equals(c.getBuyerUserId())||(c.isGuest()&&guestKey!=null&&guestKey.equals(c.getGuestSessionKey())));}
    private String mode(Principal p){if(p==null)return "BUYER";Role r=users.findByEmailIgnoreCase(p.getName()).map(UserAccount::getRole).orElse(Role.ROLE_CUSTOMER);return r==Role.ROLE_VENDOR?"VENDOR":r==Role.ROLE_ADMIN?"ADMIN":"BUYER";}
}
