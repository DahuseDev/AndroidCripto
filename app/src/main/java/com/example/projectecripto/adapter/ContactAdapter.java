package com.example.projectecripto.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectecripto.R;
import com.example.projectecripto.activity.ChatActivity;
import com.example.projectecripto.model.Contact;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactList;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateData(List<Contact> newContactList) {
        this.contactList = newContactList;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name;
        TextView lastMessage;
        View unreadIndicator;
        View onlineIndicator;
        TextView unreadCount;
        TextView date;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.contact_photo);
            name = itemView.findViewById(R.id.contact_name);
            lastMessage = itemView.findViewById(R.id.contact_last_message);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            unreadCount = itemView.findViewById(R.id.unread_count);
            date = itemView.findViewById(R.id.contact_last_time);
            onlineIndicator = itemView.findViewById(R.id.online_indicator);
        }

        public void bind(Contact contact) {
            // Use a library like Picasso or Glide to load the image from the URL
            // Picasso.get().load(contact.getPhotoUrl()).into(photo);
            name.setText(contact.getName());
            lastMessage.setText(contact.getLastMessage());
            //check if is the same day as now
            if (contact.getLastMessageTime().toLocalDate().equals(java.time.LocalDate.now())) {
                date.setText(contact.getLastMessageTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                date.setText(contact.getLastMessageTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            if (contact.getUnreadedMessages() >= 1) {
                unreadIndicator.setVisibility(View.VISIBLE);
                unreadCount.setVisibility(View.VISIBLE);
                unreadCount.setText(String.valueOf(contact.getUnreadedMessages()));
            } else {
                unreadIndicator.setVisibility(View.GONE);
                unreadCount.setVisibility(View.GONE);
            }
            if(contact.isOnline()){
                onlineIndicator.setVisibility(View.VISIBLE);
            } else {
                onlineIndicator.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("contact", contact);
                v.getContext().startActivity(intent);
            });
        }
    }
}