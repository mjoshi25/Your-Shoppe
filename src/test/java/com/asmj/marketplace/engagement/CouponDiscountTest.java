package com.asmj.marketplace.engagement;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
class CouponDiscountTest {
 @Test void percentageDiscountHonoursMaximumAndSubtotal(){DiscountCoupon c=new DiscountCoupon();c.setDiscountType(DiscountCoupon.DiscountType.PERCENT);c.setValue(new BigDecimal("20"));c.setMinimumOrder(new BigDecimal("100"));c.setMaximumDiscount(new BigDecimal("50"));assertEquals(new BigDecimal("50.00"),CouponController.discount(c,new BigDecimal("500")));assertEquals(BigDecimal.ZERO,CouponController.discount(c,new BigDecimal("99")));}
 @Test void fixedDiscountCannotExceedSubtotal(){DiscountCoupon c=new DiscountCoupon();c.setDiscountType(DiscountCoupon.DiscountType.FIXED);c.setValue(new BigDecimal("150"));c.setMinimumOrder(BigDecimal.ZERO);assertEquals(new BigDecimal("40"),CouponController.discount(c,new BigDecimal("40")));}
}
