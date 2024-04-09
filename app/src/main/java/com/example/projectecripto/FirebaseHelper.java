package com.example.projectecripto;

import com.example.projectecripto.model.Contact;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class FirebaseHelper {
    DatabaseReference db;
    public FirebaseHelper() {
        db = FirebaseDatabase.getInstance().getReference("users");
    }
    public boolean addUser(String name,String password) {
        if(!checkUser(name)) {
            db.child(name).child("id").setValue(getLastId());
            db.child(name).child("name").setValue(name);
            db.child(name).child("password").setValue(password);
            db.child("last_id").setValue(getLastId() + 1);
            return true;
        } else {
            // User already exists
            return false;
        }
    }
    public boolean checkUser(String name) {
        return db.child(name).child("password").get().isSuccessful();
    }
    public Contact login(String name,String password) {
        Contact contact = null;
        if (checkUser(name)) {
            if (db.child(name).child("password").get().getResult().getValue().equals(password)) {
                // Correct password
                contact = new Contact(db.child(name).child("id").get().getResult().getValue(Integer.class), "", name, "", 0, LocalDateTime.now());
            } else {
                // Wrong password
            }
        } else {
            // User does not exist
        }
        return contact;
    }
    public int getLastId() {
        if (!db.child("last_id").get().isSuccessful()) {
            db.child("last_id").setValue(0);
        }
        return db.child("last_id").get().getResult().getValue(Integer.class);
    }
}
