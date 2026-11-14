package com.asmj.marketplace.advertising;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class AdCampaignLifecycleTest {
    @Test void campaignIsLiveOnlyWhenPaymentCreativeAndDatesAreApproved() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        AdCampaign campaign = new AdCampaign();
        campaign.setPaymentStatus("VERIFIED");
        campaign.setApprovalStatus("APPROVED");
        campaign.setStatus("APPROVED");
        campaign.setStartDate(today);
        campaign.setEndDate(today.plusDays(6));
        assertTrue(campaign.isLive());

        campaign.setPaymentStatus("SUBMITTED");
        assertFalse(campaign.isLive(), "Unverified UPI payment must never be served");
        campaign.setPaymentStatus("VERIFIED");
        campaign.setApprovalStatus("PENDING");
        assertFalse(campaign.isLive(), "Creative approval is independent from payment verification");
        campaign.setApprovalStatus("APPROVED");
        campaign.setStatus("SUSPENDED");
        assertFalse(campaign.isLive(), "Suspended campaigns must not be served");
        campaign.setStatus("APPROVED");
        campaign.setPaymentStatus("REFUNDED");
        assertFalse(campaign.isLive(), "Refunded campaigns must not be served");
    }

    @Test void campaignDoesNotGoLiveBeforeItsScheduledStartOrAfterExpiry() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        AdCampaign campaign = new AdCampaign();
        campaign.setPaymentStatus("VERIFIED");
        campaign.setApprovalStatus("APPROVED");
        campaign.setStatus("APPROVED");
        campaign.setStartDate(today.plusDays(1));
        campaign.setEndDate(today.plusDays(7));
        assertFalse(campaign.isLive());
        campaign.setStartDate(today.minusDays(7));
        campaign.setEndDate(today.minusDays(1));
        assertFalse(campaign.isLive());
    }
}
