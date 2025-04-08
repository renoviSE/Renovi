package com.example.renovi.model;

public class MChatMessage {

    private String message;
    private String messageFrom;
    private String messageTo;


    public MChatMessage(String message, String messageFrom, String messageTo) {
        this.message = message;
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public String getMessageTo() {
        return messageTo;
    }
}
