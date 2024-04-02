package com.example.projectecripto.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectecripto.R;
import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateData(List<Message> newMessageList) {
        this.messageList = newMessageList;
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView messageTime;
        LinearLayout linearLayout;
        TextView dateSeparator;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            messageTime = itemView.findViewById(R.id.message_time);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            dateSeparator = itemView.findViewById(R.id.date_separator);
        }

        public void bind(Message message) {
            messageContent.setText(message.getContent());
            messageTime.setText(message.getDate().format(DateTimeFormatter.ofPattern("HH:mm")));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            if (message.isSent()) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.removeRule(RelativeLayout.ALIGN_PARENT_START);
                linearLayout.setBackground(ContextCompat.getDrawable(linearLayout.getContext(), R.drawable.rounded_border_sent));
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.removeRule(RelativeLayout.ALIGN_PARENT_END);
                linearLayout.setBackground(ContextCompat.getDrawable(linearLayout.getContext(), R.drawable.rounded_border_received));
            }
            linearLayout.setLayoutParams(params);
            if (message.isDateSeparator()) {
                dateSeparator.setVisibility(View.VISIBLE);
                dateSeparator.setText(message.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                dateSeparator.setVisibility(View.GONE);
            }
        }
    }
}