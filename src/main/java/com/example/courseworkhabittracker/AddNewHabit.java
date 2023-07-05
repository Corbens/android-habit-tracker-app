package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.List;

public class AddNewHabit extends AppCompatActivity {

    private EditText editTextHabitName;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_habit);

        editTextHabitName = findViewById(R.id.editTextHabitName);
    }

    public void onButtonClickCreateHabit(View view) { //create the new habit

        if (editTextHabitName.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter habit name", Toast.LENGTH_LONG).show();
            return;
        }
        Habit newHabit = new Habit(editTextHabitName.getText().toString()); //create new habit based on user input

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("users/" + user.getUid() + "/habits");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() { //retrieve current data from firebase
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error adding habit. Try again later.", Toast.LENGTH_LONG).show();
                } else {
                    GenericTypeIndicator<List<Habit>> t = new GenericTypeIndicator<List<Habit>>() {
                    };
                    List<Habit> habitList = task.getResult().getValue(t);
                    habitList.add(newHabit);

                    ref.setValue(habitList).addOnCompleteListener(new OnCompleteListener<Void>() { //save new data to firebase
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Successfully added habit", Toast.LENGTH_LONG).show();
                                Intent userHome = new Intent(AddNewHabit.this, UserHome.class);
                                startActivity(userHome);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error adding habit. Try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
}