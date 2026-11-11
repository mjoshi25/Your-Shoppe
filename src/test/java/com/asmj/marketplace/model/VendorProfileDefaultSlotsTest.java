package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.List;
import org.junit.jupiter.api.Test;

class VendorProfileDefaultSlotsTest {
    @Test
    void newProfilesHaveThreeDeliveryAndPickupSlotsForEveryDay() {
        VendorProfile profile = new VendorProfile();

        assertEquals(21, profile.getDeliverySlotDefinitions().size());
        assertEquals(21, profile.getPickupSlotDefinitions().size());
        for (DayOfWeek day : DayOfWeek.values()) {
            assertEquals(3, profile.getDeliverySlotDefinitions().stream()
                    .filter(slot -> slot.startsWith(day.name() + "|")).count());
            assertEquals(3, profile.getPickupSlotDefinitions().stream()
                    .filter(slot -> slot.startsWith(day.name() + "|")).count());
        }
    }

    @Test
    void explicitlyConfiguredSlotsOverrideDefaults() {
        VendorProfile profile = new VendorProfile();
        List<String> custom = List.of("MONDAY|10:00|11:00|4");
        profile.setDeliverySlotDefinitions(custom);
        assertEquals(custom, profile.getDeliverySlotDefinitions());
        assertTrue(profile.getPickupSlotDefinitions().size() > 1);
    }
}
