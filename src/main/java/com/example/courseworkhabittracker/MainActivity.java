package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.editTextLoginUsername);
        password = findViewById(R.id.editTextLoginPassword);
    }

    public void onButtonClickLogin(View view) {

        if (TextUtils.isEmpty(username.getText().toString())) {
            Toast.makeText(getApplicationContext(), "@string/", Toast.LENGTH_LONG).show();
            return;

        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).show();
            return;
        }

        String emailStr = username.getText().toString() + "@asdasd546.gmail.com"; //random constant email attached to meet firebase email authentication
        String passwordStr = password.getText().toString() + "eee"; //3 extras characters added to help reach minimum 6 character limit for firebase

        mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                    Intent home = new Intent(MainActivity.this, UserHome.class);
                    startActivity(home);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onButtonClickSignup(View view) {
        Intent signup = new Intent(MainActivity.this, SignUp.class);
        startActivity(signup);
    }

    public void onBackPressed() { //stops app closing on back button
        return;
    }
}