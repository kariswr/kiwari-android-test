package com.example.simplechatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public final int MSG_LEFT = 0;
    public final int MSG_RIGHT = 1;

    private Context context;
    private List<Message> messageList;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context,List<Message> messageList){
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        Message message = messageList.get(i);

        viewHolder.tvMessage.setText(message.getMessage());
        viewHolder.tvTime.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.textViewMessage);
            tvTime = itemView.findViewById(R.id.textViewTime);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }
}
