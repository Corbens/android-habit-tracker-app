package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserHome extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddNewHabit;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private ArrayList<Habit> habitsList;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        return true;

                    case R.id.page_social:
                        Intent social = new Intent(UserHome.this, SocialList.class);
                        startActivity(social);
                        finish();
                        return true;

                    case R.id.page_settings:
                        //maybe use flag thing.
                        Intent settings = new Intent(UserHome.this, UserSettings.class);
                        startActivity(settings);
                        finish();
                        return true;
                }
                return false;
            }
        });

        fabAddNewHabit = findViewById(R.id.fabAddNewHabit);
        fabAddNewHabit.setOnClickListener(new View.OnClickListener() { //on fab click go to add new habit activity
            @Override
            public void onClick(View view) {
                Intent addNewHabit = new Intent(UserHome.this, AddNewHabit.class);
                startActivity(addNewHabit);
            }
        });

        recyclerView = findViewById(R.id.recyclerViewHabits);
        habitsList = new ArrayList<>();
        getHabitList();
        setAdapter();
    }

    private void setAdapter() {
        setOnClickListener();
        RecyclerAdapter adapter = new RecyclerAdapter(habitsList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() { //go to that specific habit page when user clicks the habit in the recycler view
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent userHabit = new Intent(UserHome.this, UserHabit.class);
                userHabit.putExtra("habit", (Serializable) habitsList.get(position));
                userHabit.putExtra("pos", position);
                startActivity(userHabit);
            }
        };
    }

    public void getHabitList() { //retrieve the arraylist of the users habits

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("users/" + user.getUid() + "/habits");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Habit>> t = new GenericTypeIndicator<List<Habit>>() {
                };
                List<Habit> retrievedHabitList = snapshot.getValue(t);
                habitsList = (ArrayList<Habit>) retrievedHabitList;
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() { //stops app closing on back button
        return;
    }
}