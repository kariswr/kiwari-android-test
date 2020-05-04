package com.example.simplechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null ){
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(intent);
        }

        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPassword = (EditText) findViewById(R.id.editTextPassword);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.emptyCred), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundColor(getResources().getColor(R.color.lightRed));
                    toast.show();
                } else {
                    showLoading(true);
                    validate(etEmail.getText().toString(), etPassword.getText().toString());
                }
            }
        });
    }

    private void validate(String email, String password){
        //validate the email and password to firebase
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginSuc), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                    toast.show();
                    showLoading(false);
                } else {
                    showLoading(false);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginFail), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.lightRed));
                    toast.show();
                }
            }
        });
    }

    private void showLoading(boolean show){
        //show progress bar (loading)
        if (show){
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            btnLogin.setEnabled(true);
            etEmail.setEnabled(true);
            etPassword.setEnabled(true);
            etPassword.setText("");
        }
    }
}
