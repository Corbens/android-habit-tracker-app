package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class EditHabit extends AppCompatActivity {

    private TextView textViewEditHabit;
    private EditText editTextHabitName;
    private Habit habit;
    private int habitPosition;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        textViewEditHabit = findViewById(R.id.textViewEditHabit);
        editTextHabitName = findViewById(R.id.editTextHabitName);

        if(getIntent().getExtras() != null){ //check if can get extras from intent
            habit = (Habit) getIntent().getExtras().getSerializable("habit");
            habitPosition = getIntent().getIntExtra("pos", 0);
            textViewEditHabit.setText(String.format("EDIT %s", habit.getName().toUpperCase()));
            editTextHabitName.setText(habit.getName());
        }else{ //if no extras, finish this activity as you can't do anything
            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void onButtonClickEditHabit(View view) {

        if (editTextHabitName.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter habit name", Toast.LENGTH_LONG).show();
            return;
        }
        Habit editedHabit = new Habit(editTextHabitName.getText().toString()); //create habit based on users edit
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("users/" + user.getUid() + "/habits");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() { //get data from firebase
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error editing habit. Try again later.", Toast.LENGTH_LONG).show();
                }
                else {
                    GenericTypeIndicator<List<Habit>> t = new GenericTypeIndicator<List<Habit>>() {};
                    List<Habit> habitList = task.getResult().getValue(t);
                    editedHabit.setHabitData(habitList.get(habitPosition).getHabitData());
                    editedHabit.setBestStreak(habitList.get(habitPosition).getBestStreak());
                    editedHabit.setCurrentStreak(habitList.get(habitPosition).getCurrentStreak());
                    editedHabit.setLastUpdated(habitList.get(habitPosition).getLastUpdated());
                    editedHabit.setCreatedOn(habitList.get(habitPosition).getCreatedOn());
                    habitList.set(habitPosition, editedHabit);

                    ref.setValue(habitList).addOnCompleteListener(new OnCompleteListener<Void>() { //update data to firebase
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Successfully edited habit", Toast.LENGTH_LONG).show();
                                Intent userHome = new Intent(EditHabit.this, UserHome.class);
                                startActivity(userHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Error adding habit. Try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void onButtonClickDeleteHabit(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditHabit.this);
        builder.setMessage("Are you sure you want to delete this habit?");
        builder.setTitle("Warning!");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //let user confirm their decision through dialog
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                database = FirebaseDatabase.getInstance();
                ref = database.getReference().child("users/" + user.getUid() + "/habits");

                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() { //get current data from firebase
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Error deleting habit. Try again later.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            GenericTypeIndicator<List<Habit>> t = new GenericTypeIndicator<List<Habit>>() {};
                            List<Habit> habitList = task.getResult().getValue(t);
                            if(habitList.size() == 1){
                                Toast.makeText(EditHabit.this, "Cannot delete your last habit!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            habitList.remove(habitPosition);

                            ref.setValue(habitList).addOnCompleteListener(new OnCompleteListener<Void>() { //save data to firebase
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Successfully deleted habit", Toast.LENGTH_LONG).show();
                                        Intent userHome = new Intent(EditHabit.this, UserHome.class);
                                        startActivity(userHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Error deleting habit. Try again later.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}