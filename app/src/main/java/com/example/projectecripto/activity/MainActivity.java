package com.example.projectecripto.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectecripto.DatabaseHelper;
import com.example.projectecripto.R;
import com.example.projectecripto.adapter.ContactAdapter;
import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;
import com.example.projectecripto.service.MessageListenerService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private FloatingActionButton fab;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        contactList = new ArrayList<>();
//        contactList.add(new Contact(1,"","Alice", "Hi!",0));
//        contactList.add(new Contact(2,"","Bob", "Hello!",0));
//        contactList.add(new Contact(3,"","Charlie", "Hey!",0));
        contactAdapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(contactAdapter);
        fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            MessageListenerService.addTestMessages(db);
            onNewMessageReceived();
        });
        refreshList();
        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.projectecripto.NEW_MESSAGE");
        registerReceiver(messageReceiver, intentFilter, Context.RECEIVER_EXPORTED);

        Intent intent = new Intent(this, MessageListenerService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    private void refreshList() {
        contactList = db.getAllContacts();
        contactAdapter.updateData(contactList);
        contactAdapter.notifyDataSetChanged();
    }

    public void onNewMessageReceived() {
        Log.v("MainActivity", "New message received");
        refreshList();
    }
//    public void updateList(Message newMessage) {
//        Contact contact = null;
//        for (Contact c : contactList) {
//            if (c.getId() == newMessage.getId()) {
//                contactList.remove(c); // Remove the old contact
//                contact = c;
//                contact.setLastMessage(newMessage.getContent());
//                contact.setUnreadedMessages(contact.getUnreadedMessages() + 1);
//                contact.setLastMessageTime(newMessage.getDate());
//                break;
//            }
//        }
//        if(contact == null){
//            contact = new Contact(newMessage.getId(),"", "Unknown Contact", newMessage.getContent(), 1, newMessage.getDate());
//            db.addContact(contact);
//        }
//        contactList.add(0, contact); // Add the new contact at the beginning of the list
//        contactAdapter.updateData(contactList);
//    }
//    public boolean checkContactExists(int id){
//        for (Contact c : contactList) {
//            if (c.getId() == id) {
//                return true;
//            }
//        }
//        return false;
//    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract the message from the intent
            // Call the method
            onNewMessageReceived();
        }
    }
}
