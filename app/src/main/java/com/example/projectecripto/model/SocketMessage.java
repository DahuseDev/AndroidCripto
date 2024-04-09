package com.example.projectecripto.model;

import com.google.gson.Gson;

public class SocketMessage {
    private String type;
    private String content;
    private String extra;
    private int senderId;
    private int receiverId;

    public SocketMessage(String type, String content, String extra, int senderId, int receiverId) {
        this.type = type;
        this.content = content;
        this.extra = extra;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public String getType() {
        return type;
    }

    public String getExtra() {
        return extra;
    }

    public String getContent() {
        return content;
    }
    public int getSenderId() {
        return senderId;
    }
    public int getReceiverId() {
        return receiverId;
    }
    public static SocketMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SocketMessage.class);
    }
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
