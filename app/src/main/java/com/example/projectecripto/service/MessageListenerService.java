package com.example.projectecripto.service;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.projectecripto.DatabaseHelper;
import com.example.projectecripto.SocketClient;
import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;
import com.example.projectecripto.model.SocketMessage;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageListenerService extends Service {
    private static final String LAST_MESSAGE_ID = "last_message_id";
    private boolean isRunning = false;
    private static int messageId = 0;
    private SocketClient socketClient;
    private SharedPreferences sharedPreferences;
    private Thread thread;
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        messageId = sharedPreferences.getInt(LAST_MESSAGE_ID, 0);
        Log.v("MessageListenerService", "Service created");
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startListeningForMessages();
            }
        });
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "SEND_MESSAGE":
                    Message message = (Message) intent.getSerializableExtra("message");
                    if (message != null) {
                        sendMessage(message);
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startListeningForMessages() {
        Log.v("MessageListenerService", "Listening for messages");
        // Check for new messages here
        socketClient = new SocketClient("   192.168.123.43", 8123, v ->{
            DatabaseHelper db = new DatabaseHelper(this);
            //Desencripta v amb la clau simetrica
            SocketMessage socketMessage = SocketMessage.fromJson(v);
            if(!socketMessage.getType().equals("message")) return;
            Message message = Message.fromJson(socketMessage.getContent());
            messageId++;
            message.setId(messageId);
            message.setSent(!message.isSent());
            if (!db.contactExists(message.getContactId())) {
                Contact newContact = new Contact(message.getContactId(), null, "Contact " + message.getContactId(), message.getShortContent(), 1, message.getDate());
                db.addContact(newContact);
            }
            db.addMessage(message);
            sharedPreferences.edit().putInt(LAST_MESSAGE_ID, messageId).apply();
            sendNewMessageBroadcast();
        });
    }

    public void sendMessage(Message message) {
        if (socketClient != null) {
            socketClient.sendMessage(message);
        }
    }
    public static void addTestMessages(DatabaseHelper db) {
        for (int i = 0; i < 1; i++) {
            Message message = new Message("Message " + (i + 1), false, LocalDateTime.now(), 2,1);
            if (!db.contactExists(message.getContactId())) {
                Contact newContact = new Contact(message.getContactId(), null, "Contact " + message.getContactId(), null, 0, null);
                db.addContact(newContact);
            }
            db.addMessage(message);
            messageId = i;
        }
    }

    private void sendNewMessageBroadcast() {
        Log.v("MessageListenerService", "Sending broadcast");
        Intent intent = new Intent("com.example.projectecripto.NEW_MESSAGE");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        socketClient.close();
    }
}