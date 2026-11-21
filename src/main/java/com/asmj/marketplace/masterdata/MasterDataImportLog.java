package com.asmj.marketplace.masterdata;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="marketplace_master_data_import_logs")
public class MasterDataImportLog {
 @Id private String id; private String fileName; private String importedBy; private Instant importedAt; private int rowCount; private int insertedCount; private int updatedCount;
 public String getId(){return id;} public void setId(String v){id=v;} public String getFileName(){return fileName;} public void setFileName(String v){fileName=v;} public String getImportedBy(){return importedBy;} public void setImportedBy(String v){importedBy=v;} public Instant getImportedAt(){return importedAt;} public void setImportedAt(Instant v){importedAt=v;} public int getRowCount(){return rowCount;} public void setRowCount(int v){rowCount=v;} public int getInsertedCount(){return insertedCount;} public void setInsertedCount(int v){insertedCount=v;} public int getUpdatedCount(){return updatedCount;} public void setUpdatedCount(int v){updatedCount=v;}
}
