package com.example.renovi.model;


public class MChatMessage {

    private String message;
    private String messageFrom;
    private String messageSenderName;
    private String messageTo;
    private com.google.firebase.Timestamp timestamp;


    public MChatMessage(String message, String messageFrom, String senderName, String messageTo, com.google.firebase.Timestamp timestamp) {
        this.message = message;
        this.messageFrom = messageFrom;
        this.messageSenderName = senderName;
        this.messageTo = messageTo;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public String getMessageSenderName() {
        return messageSenderName;
    }

    public String getMessageTo() {
        return messageTo;
    }
    public com.google.firebase.Timestamp getTimestamp(){return  timestamp;}
}