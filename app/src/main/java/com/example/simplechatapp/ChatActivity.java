package com.example.simplechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView toolbarTitle;
    private ImageView toolbarImg;
    private Toolbar toolbar;
    private User opponentUser;
    private String keyChat;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        //get 1 other user, there is 2 hardcoded user in firebase
        getOpponent();

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbarTitle = findViewById(R.id.text_toolbar_title);
        toolbarImg = findViewById(R.id.icon_toolbar_left);
        etMessage = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.buttonSend);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                if (!message.equals("")){
                    sendMessage(message);
                    etMessage.setText("");
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.emptyMessage), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundColor(getResources().getColor(R.color.lightRed));
                    toast.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                try {
                    firebaseAuth.signOut();
                    finish();
//                    Intent intent = new Intent(ChatActivity.this, MainActivity.class);
//                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.logoutSuc), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.logoutFail), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.lightRed));
                    toast.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getOpponent() {
        //get a user that is not the current user
        DatabaseReference ref = firebaseDatabase.getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null){
                        user.setUserId(snapshot.getKey());
                        if (!user.getUserId().equals(firebaseUser.getUid())) {
                            opponentUser = user;

                            Objects.requireNonNull(getSupportActionBar()).setTitle(opponentUser.getName());
                            Glide.with(getApplicationContext()).load(opponentUser.getAvatar()).placeholder(getResources().getDrawable(R.drawable.ic_action_profile)).into(toolbarImg);

                            if (firebaseUser.getUid().toCharArray()[0] < opponentUser.getUserId().toCharArray()[0] ){
                                keyChat = firebaseUser.getUid() + opponentUser.getUserId();
                            } else {
                                keyChat = opponentUser.getUserId() + firebaseUser.getUid();
                            }

                            toolbarTitle.setText(opponentUser.getName());
                            getChat();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message){
        //send messages to firebase
        DatabaseReference ref = firebaseDatabase.getReference("Messages");
        HashMap<String, Object> map = new HashMap<>();

        String time = getCurrentTimeStamp();

        map.put("sender", firebaseUser.getUid());
        map.put("receiver", opponentUser.getUserId());
        map.put("message", message);
        map.put("time", time);

        ref.child(keyChat).push().setValue(map);
    }

    private String getCurrentTimeStamp(){
        try {
            return new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault()).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getChat(){
        //get messages from firebase and put them in recycleView
        DatabaseReference ref = firebaseDatabase.getReference("Messages");
        ref.child(keyChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (i > messageList.size()){
                        Message message = snapshot.getValue(Message.class);
                        messageList.add(message);
                    }
                    i++;
                    messageAdapter = new MessageAdapter(getApplicationContext(), messageList);
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());
                        }
                    }, 100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
