package com.asmj.marketplace.parameters;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceParameterRepository extends MongoRepository<MarketplaceParameter,String>{ Optional<MarketplaceParameter> findByKey(String key); List<MarketplaceParameter> findAllByOrderByGroupNameAscLabelAsc(); }
