package com.example.projectecripto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.view.ViewPropertyAnimatorListener;

import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 12;
    private static final String TABLE_CONTACTS = "contacts";
    private static final String KEY_ID = "id";
    private static final String KEY_PHOTO = "photo_url";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_MESSAGE = "last_message";
    private static final String KEY_UNREAD_MESSAGES = "unread_messages";
    private static final String KEY_ONLINE = "online";
    private static final String TABLE_MESSAGES = "messages";
    private static final String KEY_MESSAGE_ID = "id";
    private static final String KEY_SENDER_ID = "sender_id";
    private static final String KEY_RECEIVER_ID = "receiver_id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_IS_SENT = "is_sent";
    private static final String KEY_DATE = "date";
    private static ArrayList<Contact> contactList;
    private static ArrayList<Message> messageList;
    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PHOTO + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_LAST_MESSAGE + " TEXT,"
                + KEY_ONLINE + " INTEGER,"
                + KEY_UNREAD_MESSAGES + " INTEGER, " + KEY_DATE + " DATETIME)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_MESSAGE_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE + " TEXT,"
                + KEY_IS_SENT + " INTEGER," + KEY_DATE + " DATETIME,"
                + KEY_SENDER_ID + " INTEGER," + KEY_RECEIVER_ID + " INTEGER)";
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
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String photoUrl = cursor.getString(1);
                String name = cursor.getString(2);
                String lastMessage = cursor.getString(3);
                boolean online = Integer.parseInt(cursor.getString(4)) == 1;
                int unreadMessages = Integer.parseInt(cursor.getString(5));
                LocalDateTime date = LocalDateTime.parse(cursor.getString(6));
                Contact contact = new Contact(id, photoUrl, name, lastMessage, unreadMessages, date);
                contact.setOnline(online);
                if(id != Contact.getCurrentContact().getId()){
                    contactList.add(contact);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return contactList;
    }
    public List<Message> getMessagesFromContact(int contactId) {
        List<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES + " WHERE " + KEY_SENDER_ID + " = " + contactId + " OR "+ KEY_RECEIVER_ID+ " = "+ contactId + " ORDER BY " + KEY_DATE + " ASC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.v("DatabaseHelper", "Query: " + selectQuery);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String content = cursor.getString(1);
                boolean isSent = Integer.parseInt(cursor.getString(2)) == 1;
                LocalDateTime date = LocalDateTime.parse(cursor.getString(3));
                int senderId = Integer.parseInt(cursor.getString(4));
                int receiverId = Integer.parseInt(cursor.getString(5));
                Message message = new Message(content, isSent, date, senderId, receiverId);
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return messages;
    }
    // Add a new contact to the database
    public void addContact(Contact contact) {
        Log.v("DatabaseHelper", "Adding contact: " + contact.getName());
        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getId());
        values.put(KEY_PHOTO, contact.getPhotoUrl());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_LAST_MESSAGE, contact.getLastMessage());
        values.put(KEY_UNREAD_MESSAGES, contact.getUnreadedMessages());
        values.put(KEY_ONLINE, contact.isOnline() ? 1 : 0);
        //handle date null
        if(contact.getLastMessageTime() == null){
            contact.setLastMessageTime(LocalDateTime.now());
        }
        values.put(KEY_DATE, contact.getLastMessageTime().toString());

        db.insert(TABLE_CONTACTS, null, values);
    }
    // Add a new message to the database
    public void addMessage(Message message) {
        Log.v("DatabaseHelper", "Adding message: " + message.getContent());
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message.getContent());
        values.put(KEY_IS_SENT, message.isSent() ? 1 : 0);
        values.put(KEY_DATE, message.getDate().toString());
        values.put(KEY_SENDER_ID, message.getContactId());
        values.put(KEY_RECEIVER_ID, message.getReceiverId());

        db.insert(TABLE_MESSAGES, null, values);

        updateLastMessage(message.getContactId(), message.getShortContent(), message.getDate(), message.isSent());
    }
    // Update the last message of a contact
    public void updateLastMessage(int contactId, String lastMessage, LocalDateTime date, boolean isSent) {
        ContentValues values = new ContentValues();
        values.put(KEY_LAST_MESSAGE, lastMessage);
        values.put(KEY_DATE, date.toString());
        if (isSent){
            values.put(KEY_UNREAD_MESSAGES, 0);
            db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{String.valueOf(contactId)});
        }else{
            values.put(KEY_UNREAD_MESSAGES, getUnreadMessages(contactId) + 1);
            db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{String.valueOf(contactId)});
        }
    }

    private int getUnreadMessages(int contactId) {
        String selectQuery = "SELECT " + KEY_UNREAD_MESSAGES + " FROM " + TABLE_CONTACTS + " WHERE " + KEY_ID + " = " + contactId;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int unreadMessages = 0;
        if (cursor.moveToFirst()) {
            unreadMessages = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        return unreadMessages;
    }
    public void resetUnreadMessages(int contactId) {
        ContentValues values = new ContentValues();
        values.put(KEY_UNREAD_MESSAGES, 0);
        db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{String.valueOf(contactId)});
    }

    public boolean contactExists(int contactId) {

        String selection = KEY_ID + " = ?";
        String[] selectionArgs = { String.valueOf(contactId) };

        Cursor cursor = db.query(TABLE_CONTACTS, null, selection, selectionArgs, null, null, null);

        boolean exists = (cursor.getCount() > 0);

        cursor.close();

        return exists;
    }

    public void updateContact(Contact contact) {
        if(contactExists(contact.getId())){
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, contact.getName());
            values.put(KEY_PHOTO, contact.getPhotoUrl());
            values.put(KEY_ONLINE, contact.isOnline() ? 1 : 0);
            db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        }
    }
    public void setContactsOffline() {
        ContentValues values = new ContentValues();
        values.put(KEY_ONLINE, 0);
        db.update(TABLE_CONTACTS, values, null, null);
    }
}