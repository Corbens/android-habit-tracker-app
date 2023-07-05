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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SocialList extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView textViewNoUsers;
    private RecyclerView recyclerView;
    private RecyclerAdapterSocial.RecyclerViewClickListener listener;
    private ArrayList<User> usersList;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_list);

        textViewNoUsers = findViewById(R.id.textViewNoUsers);
        recyclerView = findViewById(R.id.recyclerViewSocial);
        usersList = new ArrayList<>();
        getUsersList();
        setAdapter();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_social);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        Intent home = new Intent(SocialList.this, UserHome.class);
                        startActivity(home);
                        finish();
                        return true;

                    case R.id.page_social:
                        return true;

                    case R.id.page_settings:
                        Intent settings = new Intent(SocialList.this, UserSettings.class);
                        startActivity(settings);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void setAdapter() {
        setOnClickListener();
        RecyclerAdapterSocial adapter = new RecyclerAdapterSocial(usersList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() { //go to that specific habit page when user clicks the habit in the recycler view
        listener = new RecyclerAdapterSocial.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent socialHabitList = new Intent(SocialList.this, SocialHabitList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userHabits", usersList.get(position).getHabits());
                System.out.println(usersList.get(position).getHabits().size());
                socialHabitList.putExtras(bundle);
                startActivity(socialHabitList);
            }
        };
    }

    private void getUsersList() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    System.out.println(user.getUid());
                    System.out.println(child.getKey());
                    if (!user.getUid().equals(child.getKey())) {
                        User user = child.getValue(User.class);
                        if (user.isVisible()) {
                            usersList.add(user);
                        }
                    }
                }
                if (usersList.isEmpty()) { //check if there's any users to display
                    textViewNoUsers.setVisibility(View.VISIBLE);
                } else {
                    textViewNoUsers.setVisibility(View.INVISIBLE);
                }
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
