package com.example.projectecripto.activity;

import android.os.Bundle;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private Button btnSend;
    private EditText etMessage;
    private SocketClient socketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        DatabaseHelper db = new DatabaseHelper(this);
        Contact contact = (Contact) getIntent().getSerializableExtra("contact");
        if (contact == null) {
            Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(contact.getName());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = db.getMessagesFromContact(contact.getId());
        List<Message> messages = db.getMessagesFromContact(contact.getId());
        if (messages != null) {
            LocalDate previousDate = null;
            for (Message message : messages) {
                LocalDate currentDate = message.getDate().toLocalDate();
                if (!currentDate.equals(previousDate)) {
                    message.setDateSeparator(true);
                    previousDate = currentDate;
                }
            }
            messageList = messages;
        }
        if (messageList == null) {
            messageList = new ArrayList<>();
        }


//        messageList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            messageList.add(new Message(i,"Message " + (i + 1), i % 2 == 0));
//        }

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
            String message = etMessage.getText().toString();
            if (!message.isEmpty()) {
                Message newMessage = new Message(messageList.size(), message, true, LocalDateTime.now(), contact.getId());
                messageList.add(newMessage);
                messageAdapter.updateData(messageList);
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
                etMessage.setText("");
                db.addMessage(newMessage);
                //socketClient.sendMessage(newMessage);
            }
        });
        if (!messageList.isEmpty()) {
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socketClient != null) {
            socketClient.close();
        }
    }
}