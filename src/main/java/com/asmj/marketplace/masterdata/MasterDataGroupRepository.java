package com.asmj.marketplace.masterdata;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MasterDataGroupRepository extends MongoRepository<MasterDataGroup,String> {
 List<MasterDataGroup> findAllByOrderByDisplayNameAsc();
 List<MasterDataGroup> findByActiveTrueOrderByDisplayNameAsc();
 Optional<MasterDataGroup> findByGroupKey(String groupKey);
}
