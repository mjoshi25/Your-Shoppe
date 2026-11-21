package com.asmj.marketplace.masterdata;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MasterDataImportLogRepository extends MongoRepository<MasterDataImportLog,String>{List<MasterDataImportLog> findTop10ByOrderByImportedAtDesc();}
