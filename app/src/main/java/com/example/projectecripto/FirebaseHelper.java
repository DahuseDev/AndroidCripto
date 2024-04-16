package com.example.projectecripto;

import android.util.Log;

import com.example.projectecripto.model.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;

public class FirebaseHelper {
    DatabaseReference db;
    public FirebaseHelper() {
        db = FirebaseDatabase.getInstance().getReference("users");
    }
    public void addUser(String name,String password,OnContactChangedListener listener) {
        checkUser(name, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    getLastId(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int id = 1;
                            if(!dataSnapshot.exists()){
                                db.child("last_id").setValue(id);
                            }else{
                                id = dataSnapshot.getValue(Integer.class);
                            }
                            db.child(name).child("id").setValue(id);
                            db.child(name).child("password").setValue(Xifrador.hashPassword(password));
                            db.child("last_id").setValue(id + 1);
                            Contact contact = new Contact(id, "", name, "", 0, LocalDateTime.now());
                            listener.onContactChanged(contact);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("FirebaseHelper", "Error getting last id");
                        }
                    });
                }else{
                    listener.onContactChanged(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("FirebaseHelper", "Error checking user");
            }
        });
    }
    public void checkUser(String name, ValueEventListener listener) {
        db.child(name).addListenerForSingleValueEvent(listener);
    }
    public void login(String name, String password, OnContactChangedListener listener) {
        checkUser(name, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedPassword = dataSnapshot.child("password").getValue(String.class);
                    if (Xifrador.verifyPassword(password, storedPassword)) {
                        Log.v("FirebaseHelper", "User " + name + " logged in");
                        Contact contact = new Contact(dataSnapshot.child("id").getValue(Integer.class), "", name, "", 0, LocalDateTime.now());
                        listener.onContactChanged(contact);
                    } else {
                        Log.v("FirebaseHelper", "User " + name + " failed to log in");
                        listener.onContactChanged(null);
                    }
                } else {
                    Log.v("FirebaseHelper", "User " + name + " does not exist");
                    listener.onContactChanged(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }
    public void getLastId(ValueEventListener listener) {
        db.child("last_id").addListenerForSingleValueEvent(listener);
    }
}

