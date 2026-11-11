package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.*;
import com.asmj.marketplace.product.ProductReview;
import org.junit.jupiter.api.Test;

class ProductReviewTest {
 @Test void newReviewsAwaitModeration(){ProductReview review=new ProductReview();assertEquals(ProductReview.ReviewStatus.PENDING,review.getStatus());assertTrue(review.getCreatedAt()!=null);}
}
