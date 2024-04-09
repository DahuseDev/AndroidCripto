package com.example.projectecripto.model;

import com.example.projectecripto.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Contact implements Serializable {

    private static final String TAG = "Contact";
    private int id;
    private String photoUrl;
    private String name;
    private String lastMessage;
    private int unreadedMessages;
    private LocalDateTime date;
    private boolean online;
    private static Contact currentContact;

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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
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
    public String toJson(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.toJson(this);
    }
    public static Contact fromJson(String json){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.fromJson(json, Contact.class);
    }
    public static Contact getCurrentContact() {
        return currentContact;
    }

    public static void setCurrentContact(Contact currentContact) {
        Contact.currentContact = currentContact;
    }

    public static String ArrayToJson(HashMap<Integer, Contact> contacts){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.toJson(contacts);
    }
    public static HashMap<Integer, Contact> ArrayFromJson(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        Type type = new TypeToken<HashMap<Integer, Contact>>(){}.getType();
        return gson.fromJson(json, type);
    }
}