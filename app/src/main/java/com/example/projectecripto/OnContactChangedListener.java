package com.example.projectecripto;

import com.example.projectecripto.model.Contact;
import com.google.firebase.database.DatabaseError;

public interface OnContactChangedListener {
    void onContactChanged(Contact contact);

    void onCancelled(DatabaseError databaseError);
}
