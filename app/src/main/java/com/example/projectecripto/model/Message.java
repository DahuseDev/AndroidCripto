package com.example.projectecripto.model;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.time.LocalDateTime;
public class Message implements Serializable {
    private int id;
    private String content;
    private boolean isSent;
    private LocalDateTime date;
    private int contactId;
    private int receiverId;
    private boolean dateSeparator;

    public Message(int id,String content, boolean isSent, LocalDateTime date, int contactId) {
        this.id = id;
        this.content = content;
        this.isSent = isSent;
        this.date = date;
        this.contactId = contactId;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public int getContactId() {
        return contactId;
    }
    public int getReceiverId() {
        return receiverId;
    }
    public String toJson() {
        return new Gson().toJson(this);
    }
    public static Message fromJson(String json) {
        return new Gson().fromJson(json, Message.class);
    }
    public boolean isDateSeparator() {
        return dateSeparator;
    }

    public void setDateSeparator(boolean dateSeparator) {
        this.dateSeparator = dateSeparator;
    }
}