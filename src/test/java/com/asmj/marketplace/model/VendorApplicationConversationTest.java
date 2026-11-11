package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.*;
import com.asmj.marketplace.chat.ChatMessage;
import com.asmj.marketplace.chat.VendorApplicationConversation;
import org.junit.jupiter.api.Test;

class VendorApplicationConversationTest {
    @Test void conversationStartsWithAnEmptyMessageListAndAcceptsBothSides() {
        VendorApplicationConversation conversation = new VendorApplicationConversation();
        conversation.setVendorUserId("vendor-123");
        assertNotNull(conversation.getMessages());
        conversation.getMessages().add(new ChatMessage("ADMIN", "Marketplace Admin", "Please upload your shop document."));
        conversation.getMessages().add(new ChatMessage("VENDOR", "Vendor", "Attached as requested."));
        assertEquals(2, conversation.getMessages().size());
        assertEquals("ADMIN", conversation.getMessages().get(0).getSenderType());
        assertEquals("VENDOR", conversation.getMessages().get(1).getSenderType());
    }
}
