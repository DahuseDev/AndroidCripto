package com.example.projectecripto.model;

import com.example.projectecripto.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
public class Message implements Serializable {
    private static final String TAG = "Message";
    private int id;
    private String content;
    private boolean isSent;
    private LocalDateTime date;
    private int contactId;
    private int receiverId;
    private boolean dateSeparator;

    public Message(String content, boolean isSent, LocalDateTime date, int contactId, int receiverId) {
        this.content = content;
        this.isSent = isSent;
        this.date = date;
        this.contactId = contactId;
        this.receiverId = receiverId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }
    public String getShortContent() {
        String content = this.content;
        content = content.replace("\n", " ");
        if (content.length() > 20) {
            return content.substring(0, 20) + "...";
        }
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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        return gson.toJson(this);
    }
    public static Message fromJson(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.fromJson(json, Message.class);
    }
    public boolean isDateSeparator() {
        return dateSeparator;
    }

    public void setDateSeparator(boolean dateSeparator) {
        this.dateSeparator = dateSeparator;
    }
}