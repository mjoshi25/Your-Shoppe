package com.asmj.marketplace.engagement;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/** Atomic coupon usage reservation. A reservation is created when an order is placed. */
@Service
public class CouponUsageService {
    private final MongoTemplate mongo;
    public CouponUsageService(MongoTemplate mongo) { this.mongo = mongo; }

    public Optional<DiscountCoupon> reserve(String code) {
        Instant now = Instant.now();
        Document query = new Document("$and", List.of(
            new Document("code", new Document("$regex", "^" + Pattern.quote(code) + "$").append("$options", "i")),
            new Document("active", true),
            new Document("$or", List.of(new Document("startsAt", new Document("$exists", false)), new Document("startsAt", null), new Document("startsAt", new Document("$lte", now)))),
            new Document("$or", List.of(new Document("endsAt", new Document("$exists", false)), new Document("endsAt", null), new Document("endsAt", new Document("$gte", now)))),
            new Document("$or", List.of(
                new Document("usageLimit", null),
                new Document("usageLimit", new Document("$exists", false)),
                new Document("$expr", new Document("$lt", List.of(new Document("$ifNull", List.of("$usageCount", 0)), "$usageLimit")))
            ))
        ));
        DiscountCoupon updated = mongo.findAndModify(new BasicQuery(query), new Update().inc("usageCount", 1),
            FindAndModifyOptions.options().returnNew(true), DiscountCoupon.class);
        return Optional.ofNullable(updated);
    }

    public void release(String code) {
        if (code == null || code.isBlank()) return;
        Query query = new Query(org.springframework.data.mongodb.core.query.Criteria.where("code").is(code).and("usageCount").gt(0));
        mongo.updateFirst(query, new Update().inc("usageCount", -1), DiscountCoupon.class);
    }
}
