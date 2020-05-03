package com.example.simplechatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private TextView toolbarTitle;
    private ImageView toolbarImg;
    private Toolbar toolbar;
    private User opponentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        toolbarTitle = findViewById(R.id.text_toolbar_title);
        toolbarImg = findViewById(R.id.icon_toolbar_left);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getOpponent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater infalter = getMenuInflater();
        infalter.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                try {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.logoutSuc), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.GREEN);
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.logoutFail), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.RED);
                    toast.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getOpponent() {
        DatabaseReference ref = firebaseDatabase.getReference("Users");
        Log.d("MASUK", " INI JALAN");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    user.setUserId(snapshot.getKey());
                    if (!user.getUserId().equals(firebaseUser.getUid())) {
                        opponentUser = user;
                        getSupportActionBar().setTitle(opponentUser.getName());
                        Glide.with(getApplicationContext()).load(opponentUser.getAvatar()).into(toolbarImg);
                        toolbarTitle.setText(opponentUser.getName());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
