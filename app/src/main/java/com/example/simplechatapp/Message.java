package com.example.simplechatapp;

public class Message {

    private String sender;
    private String receiver;
    private String message;
    private String time;

    public Message(){ }

    public Message(String sender, String message, String time, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
