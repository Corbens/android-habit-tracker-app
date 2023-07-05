package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText username, password;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.editTextSignupUsername);
        password = findViewById(R.id.editTextSignupPassword);
    }

    public void onButtonClickCreateAccount(View view) {

        if (TextUtils.isEmpty(username.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_LONG).show();
            return;

        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).show();
            return;
        }

        String emailStr = username.getText().toString() + "@asdasd546.gmail.com"; //random constant email attached to meet firebase email authentication
        String passwordStr = password.getText().toString() + "eee"; //3 extras characters added to help reach minimum 6 character limit for firebase

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    createUser();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Account Creation Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void createUser() { //creates a user object and saves it in firebase realtime database
        user = FirebaseAuth.getInstance().getCurrentUser();
        User userObj = new User();
        userObj.setUsername(username.getText().toString());
        userObj.setAccountName(username.getText().toString());

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("users/" + user.getUid());

        ref.setValue(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUp.this, "Account Setup Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}