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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;
import java.util.ArrayList;

public class SocialHabitList extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private ArrayList<Habit> habitsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_habit_list);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_social);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        Intent home = new Intent(SocialHabitList.this, UserHome.class);
                        startActivity(home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_social:
                        finish();
                        return true;

                    case R.id.page_settings:
                        Intent settings = new Intent(SocialHabitList.this, UserSettings.class);
                        startActivity(settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;
                }
                return false;
            }
        });

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            habitsList = (ArrayList<Habit>) bundle.getSerializable("userHabits");
            recyclerView = findViewById(R.id.recyclerViewSocialHabits);
            setAdapter();
        }
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
                Intent socialHabit = new Intent(SocialHabitList.this, SocialHabit.class);
                socialHabit.putExtra("habit", (Serializable) habitsList.get(position));
                startActivity(socialHabit);
            }
        };
    }
}