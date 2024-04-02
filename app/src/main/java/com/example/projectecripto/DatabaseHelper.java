package com.example.projectecripto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 6;
    private static final String TABLE_CONTACTS = "contacts";
    private static final String KEY_ID = "id";
    private static final String KEY_PHOTO = "photo_url";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_MESSAGE = "last_message";
    private static final String KEY_UNREAD_MESSAGES = "unread_messages";
    private static final String TABLE_MESSAGES = "messages";
    private static final String KEY_MESSAGE_ID = "id";
    private static final String KEY_CONTACT_ID = "contact_id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_IS_SENT = "is_sent";
    private static final String KEY_DATE = "date";
    private static ArrayList<Contact> contactList;
    private static ArrayList<Message> messageList;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PHOTO + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_LAST_MESSAGE + " TEXT,"
                + KEY_UNREAD_MESSAGES + " INTEGER, " + KEY_DATE + " DATETIME)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_MESSAGE_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE + " TEXT,"
                + KEY_IS_SENT + " INTEGER," + KEY_DATE + " DATETIME,"
                + KEY_CONTACT_ID + " INTEGER)";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_DATE + " DESC";
        Log.v("DatabaseHelper", "Query: " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String photoUrl = cursor.getString(1);
                String name = cursor.getString(2);
                String lastMessage = cursor.getString(3);
                int unreadMessages = Integer.parseInt(cursor.getString(4));
                LocalDateTime date = LocalDateTime.parse(cursor.getString(5));
                Contact contact = new Contact(id, photoUrl, name, lastMessage, unreadMessages, date);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contactList;
    }
    public List<Message> getMessagesFromContact(int contactId) {
        List<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES + " WHERE " + KEY_CONTACT_ID + " = " + contactId + " ORDER BY " + KEY_DATE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String content = cursor.getString(1);
                boolean isSent = Integer.parseInt(cursor.getString(2)) == 1;
                LocalDateTime date = LocalDateTime.parse(cursor.getString(3));
                Message message = new Message(id, content, isSent, date, contactId);
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }
    // Add a new contact to the database
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v("DatabaseHelper", "Adding contact: " + contact.getName());
        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getId());
        values.put(KEY_PHOTO, contact.getPhotoUrl());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_LAST_MESSAGE, contact.getLastMessage());
        values.put(KEY_UNREAD_MESSAGES, contact.getUnreadedMessages());
        //handle date null
        if(contact.getLastMessageTime() == null){
            contact.setLastMessageTime(LocalDateTime.now());
        }
        values.put(KEY_DATE, contact.getLastMessageTime().toString());

        db.insert(TABLE_CONTACTS, null, values);
    }
    // Add a new message to the database
    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v("DatabaseHelper", "Adding message: " + message.getContent());
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message.getContent());
        values.put(KEY_IS_SENT, message.isSent() ? 1 : 0);
        values.put(KEY_DATE, message.getDate().toString());
        values.put(KEY_CONTACT_ID, message.getContactId());

        db.insert(TABLE_MESSAGES, null, values);
        updateLastMessage(message.getContactId(), message.getContent(), message.getDate());
    }
    // Update the last message of a contact
    public void updateLastMessage(int contactId, String lastMessage, LocalDateTime date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_CONTACTS + " SET " + KEY_LAST_MESSAGE + " = '" + lastMessage + "', " + KEY_DATE + " = '" + date + "' WHERE " + KEY_ID + " = " + contactId;
        db.execSQL(query);
        db.close();
    }

    public boolean contactExists(int contactId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_ID + " = ?";
        String[] selectionArgs = { String.valueOf(contactId) };

        Cursor cursor = db.query(TABLE_CONTACTS, null, selection, selectionArgs, null, null, null);

        boolean exists = (cursor.getCount() > 0);

        cursor.close();

        return exists;
    }

}