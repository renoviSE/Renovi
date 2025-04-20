package com.example.renovi.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.firebase.Timestamp;

public class MChatMessageTest {

    @Test
    void testMessageWithValidInputs() {
        String message = "abc";
        String messageFrom = "123";
        String senderName = "John Doe";
        String messageTo = "Jane Doe";
        Timestamp timestamp = Timestamp.now();

        MChatMessage mChatMessage = new MChatMessage(message, messageFrom, senderName, messageTo, timestamp);

        assertNotNull(mChatMessage);
        assertEquals(message, mChatMessage.getMessage());
        assertEquals(messageFrom, mChatMessage.getMessageFrom());
        assertEquals(senderName, mChatMessage.getMessageSenderName());
        assertEquals(messageTo, mChatMessage.getMessageTo());
        assertEquals(timestamp, mChatMessage.getTimestamp());
    }

    @Test
    void testMessageWithEmptyStringsAndTimestampNull() {
        String message = "";
        String messageFrom = "";
        String senderName = "";
        String messageTo = "";
        Timestamp timestamp = null;

        MChatMessage mChatMessage = new MChatMessage(message, messageFrom, senderName, messageTo, timestamp);

        assertNotNull(mChatMessage);
        assertEquals(message, mChatMessage.getMessage());
        assertEquals(messageFrom, mChatMessage.getMessageFrom());
        assertEquals(senderName, mChatMessage.getMessageSenderName());
        assertEquals(messageTo, mChatMessage.getMessageTo());
        assertEquals(timestamp, mChatMessage.getTimestamp());
    }

    @Test
    void testMessageWithLongStrings() {
        String message = "abc".repeat(1000);
        String messageFrom = "123".repeat(1000);
        String senderName = "John Doe".repeat(1000);
        String messageTo = "Jane Doe".repeat(1000);
        Timestamp timestamp = Timestamp.now();

        MChatMessage mChatMessage = new MChatMessage(message, messageFrom, senderName, messageTo, timestamp);

        assertNotNull(mChatMessage);
        assertEquals(message, mChatMessage.getMessage());
        assertEquals(messageFrom, mChatMessage.getMessageFrom());
        assertEquals(senderName, mChatMessage.getMessageSenderName());
        assertEquals(messageTo, mChatMessage.getMessageTo());
        assertEquals(timestamp, mChatMessage.getTimestamp());
    }

    @Test
    void testMessageWithSpecialCharacters() {
        String message = "abc!@#$%^&*()";
        String messageFrom = "123-456_ABC";
        String senderName = "Jöhn Doe";
        String messageTo = "Jane Dœ";
        Timestamp timestamp = Timestamp.now();

        MChatMessage mChatMessage = new MChatMessage(message, messageFrom, senderName, messageTo, timestamp);

        assertNotNull(mChatMessage);
        assertEquals(message, mChatMessage.getMessage());
        assertEquals(messageFrom, mChatMessage.getMessageFrom());
        assertEquals(senderName, mChatMessage.getMessageSenderName());
        assertEquals(messageTo, mChatMessage.getMessageTo());
        assertEquals(timestamp, mChatMessage.getTimestamp());
    }
}
