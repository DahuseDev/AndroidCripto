package com.example.projectecripto.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectecripto.DatabaseHelper;
import com.example.projectecripto.R;
import com.example.projectecripto.adapter.ContactAdapter;
import com.example.projectecripto.model.Contact;
import com.example.projectecripto.service.MessageListenerService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class OnlineUsersActivity extends AppCompatActivity {
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
        contactAdapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(contactAdapter);
        fab = findViewById(R.id.fabAdd);
        fab.setVisibility(View.GONE);
        setTitle("Usuaris en línia");
        refreshList();
        OnlineUsersActivity.MessageReceiver messageReceiver = new OnlineUsersActivity.MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.projectecripto.NEW_MESSAGE");
        registerReceiver(messageReceiver, intentFilter, Context.RECEIVER_EXPORTED);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    private void refreshList() {
        contactList = MessageListenerService.getOnlineUsers();
        contactAdapter.updateData(contactList);
    }

    public void onNewMessageReceived() {
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