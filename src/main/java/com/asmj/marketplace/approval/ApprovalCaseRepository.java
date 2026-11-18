package com.asmj.marketplace.approval;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ApprovalCaseRepository extends MongoRepository<ApprovalCase,String> {
 List<ApprovalCase> findTop300ByOrderByUpdatedAtDesc();
 List<ApprovalCase> findByOwnerUserIdOrderByUpdatedAtDesc(String ownerUserId);
}
