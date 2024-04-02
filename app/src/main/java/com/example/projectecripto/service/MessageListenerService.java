package com.example.projectecripto.service;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.projectecripto.DatabaseHelper;
import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageListenerService extends Service {
    private static final String LAST_MESSAGE_ID = "last_message_id";
    private boolean isRunning = false;
    private static int messageId = 0;
    private SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        messageId = sharedPreferences.getInt(LAST_MESSAGE_ID, 0);
        startListeningForMessages();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startListeningForMessages() {
        new Thread(() -> {
            while (isRunning) {
                // Check for new messages here
                if (checkForNewMessages()) {
                    sendNewMessageBroadcast();
                }
                try {
                    Thread.sleep(1000); // Sleep for a while before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void addTestMessages(DatabaseHelper db) {
        for (int i = 0; i < 5; i++) {
            Message message = new Message(i, "Message " + (i + 1), false, LocalDateTime.now(), 123);
            if (!db.contactExists(message.getContactId())) {
                Contact newContact = new Contact(message.getContactId(), null, "Contact " + message.getContactId(), null, 0, null);
                db.addContact(newContact);
            }
            db.addMessage(message);
            messageId = i;
        }
    }

    private boolean checkForNewMessages() {
        int lastMessageId = sharedPreferences.getInt(LAST_MESSAGE_ID, 0);
        // Simulate new messages
        if(messageId == lastMessageId){
            return false;
        }
        DatabaseHelper db = new DatabaseHelper(this);
        for (int i = lastMessageId + 1; i <= messageId; i++) {
            Message newMessage = new Message(i, "New message " + i, false, LocalDateTime.now(), 123);
            lastMessageId = i;
            if (!db.contactExists(newMessage.getContactId())) {
                Contact newContact = new Contact(newMessage.getContactId(), null, "Contact " + newMessage.getContactId(), null, 0, null);
                db.addContact(newContact);
            }
            db.addMessage(newMessage);
        }
        sharedPreferences.edit().putInt(LAST_MESSAGE_ID, lastMessageId).apply();
        messageId = lastMessageId;
        return true;
    }
    private void sendNewMessageBroadcast() {
        Intent intent = new Intent("com.example.projectecripto.NEW_MESSAGE");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}