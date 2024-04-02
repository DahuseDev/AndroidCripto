package com.example.projectecripto.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Contact implements Serializable {
    private int id;
    private String photoUrl;
    private String name;
    private String lastMessage;
    private int unreadedMessages;
    private LocalDateTime date;

    public Contact(int id,String photoUrl, String name, String lastMessage, int unreadedMessages, LocalDateTime date) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.name = name;
        this.lastMessage = lastMessage;
        this.unreadedMessages = unreadedMessages;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getId() {
        return id;
    }

    public int getUnreadedMessages() {
        return unreadedMessages;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setUnreadedMessages(int unreadedMessages) {
        this.unreadedMessages = unreadedMessages;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public LocalDateTime getLastMessageTime() {
        return date;
    }

    public void setLastMessageTime(LocalDateTime date) {
        this.date = date;
    }

}