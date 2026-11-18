package com.asmj.marketplace.masterdata;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MasterDataOptionRepository extends MongoRepository<MasterDataOption,String>{
 List<MasterDataOption> findByGroupKeyAndActiveTrueOrderBySortOrderAscLabelAsc(String groupKey);
 List<MasterDataOption> findAllByOrderByGroupKeyAscSortOrderAscLabelAsc();
 Optional<MasterDataOption> findByGroupKeyAndCode(String groupKey,String code);
}
