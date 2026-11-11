package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.*;
import com.asmj.marketplace.order.OrderStatus;
import com.asmj.marketplace.order.OrderStatusEvent;
import org.junit.jupiter.api.Test;

class OrderStatusEventTest {
 @Test void capturesStatusActorAndTimestamp(){OrderStatusEvent event=new OrderStatusEvent(OrderStatus.ACCEPTED,"Vendor accepted","Shop owner","VENDOR");assertEquals(OrderStatus.ACCEPTED,event.getStatus());assertEquals("Shop owner",event.getUpdatedBy());assertEquals("VENDOR",event.getUpdatedByRole());assertNotNull(event.getCreatedAt());}
}
