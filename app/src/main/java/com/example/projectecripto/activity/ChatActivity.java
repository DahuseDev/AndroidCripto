package com.example.projectecripto.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectecripto.DatabaseHelper;
import com.example.projectecripto.R;
import com.example.projectecripto.SocketClient;
import com.example.projectecripto.adapter.MessageAdapter;
import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;
import com.example.projectecripto.service.MessageListenerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String LAST_MESSAGE_ID = "last_message_id";
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private Button btnSend;
    private EditText etMessage;
    private DatabaseHelper db;
    private Contact contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        db = new DatabaseHelper(this);
        contact = (Contact) getIntent().getSerializableExtra("contact");
        if (contact == null) {
            Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(contact.getName());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = db.getMessagesFromContact(contact.getId());
        Log.v("ChatActivity", "Messages: " + messageList.size());
        if (messageList != null) {
            LocalDate previousDate = null;
            for (Message message : messageList) {
                LocalDate currentDate = message.getDate().toLocalDate();
                if (!currentDate.equals(previousDate)) {
                    message.setDateSeparator(true);
                    previousDate = currentDate;
                }
            }
        }else{
            messageList = new ArrayList<>();
        }

        db.resetUnreadMessages(contact.getId());
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        btnSend = findViewById(R.id.button_send);
        etMessage = findViewById(R.id.input_message);

//        socketClient = new SocketClient("host", 5000, message -> runOnUiThread(() -> {
//            Message newMessage = new Message(messageList.size(), message, false, LocalDateTime.now(), contact.getId());
//            messageList.add(newMessage);
//            messageAdapter.updateData(messageList);
//            db.addMessage(newMessage);
//        }));

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                int currentId = Contact.getCurrentContact().getId();
                Message newMessage = new Message(message, true, LocalDateTime.now(), currentId, contact.getId());
                Log.v("ChatActivity", "Sending message: " + newMessage.toJson());
                messageList.add(newMessage);
                messageAdapter.updateData(messageList);
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
                etMessage.setText("");
                db.addMessage(newMessage);
                Intent intent = new Intent(this, MessageListenerService.class);
                intent.setAction("SEND_MESSAGE");
                intent.putExtra("message", newMessage);
                startService(intent);
            }
        });

        ChatActivity.MessageReceiver messageReceiver = new ChatActivity.MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.projectecripto.NEW_MESSAGE");
        registerReceiver(messageReceiver, intentFilter, Context.RECEIVER_EXPORTED);

        if (!messageList.isEmpty()) {
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
    }
    private void refreshList() {
        messageList = db.getMessagesFromContact(contact.getId());
        messageAdapter.updateData(messageList);
        messageAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    public void onNewMessageReceived() {
        Log.v("MainActivity", "New message received");
        refreshList();
    }
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract the message from the intent
            // Call the method
            onNewMessageReceived();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}