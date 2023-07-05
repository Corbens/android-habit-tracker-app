package com.example.courseworkhabittracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserHabit extends AppCompatActivity implements OnNavigationButtonClickedListener {

    private Boolean completedToday;
    private TextView habitName, doneToday, bestStreak, currentStreak;
    private Habit thisHabit;
    private int position;
    private FloatingActionButton fabEditHabit;
    private BottomNavigationView bottomNavigationView;
    private CustomCalendar customCalendar;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_habit);

        completedToday = false; //variable to determine whether a habit has been completed today
        habitName = findViewById(R.id.textViewUserHabitName);
        doneToday = findViewById(R.id.textViewDoneToday);
        bestStreak = findViewById(R.id.textViewBestStreak);
        currentStreak = findViewById(R.id.textViewCurrentStreak);
        fabEditHabit = findViewById(R.id.fabEditHabit);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        finish();
                        return true;

                    case R.id.page_social:
                        Intent social = new Intent(UserHabit.this, SocialList.class);
                        startActivity(social.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_settings:
                        Intent settings = new Intent(UserHabit.this, UserSettings.class);
                        startActivity(settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;
                }
                return false;
            }
        });

        if (getIntent().getExtras() != null) { //check you're able to get the extras from the intent
            thisHabit = (Habit) getIntent().getExtras().getSerializable("habit");
            position = getIntent().getIntExtra("pos", 0);

            habitName.setText(thisHabit.getName());
            bestStreak.setText(String.format("Best Streak: %d", thisHabit.getBestStreak()));
            currentStreak.setText(String.format("Current Streak: %d", thisHabit.getCurrentStreak()));

            Calendar thisHabitLastUpdated = Calendar.getInstance();
            thisHabitLastUpdated.setTime(thisHabit.getLastUpdatedDate());
            Calendar thisHabitCreatedOn = Calendar.getInstance();
            thisHabitCreatedOn.setTime(thisHabit.getCreatedOnDate());
            Calendar today = Calendar.getInstance();
            if (thisHabitLastUpdated.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && thisHabitLastUpdated.get(Calendar.YEAR) == today.get(Calendar.YEAR)) { //check if habit was updated today
                if ((thisHabitCreatedOn.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && thisHabitCreatedOn.get(Calendar.YEAR) == today.get(Calendar.YEAR))) { //if was last updated today, check if created today
                    if (thisHabit.getHabitData().get(today.get(Calendar.MONTH)).containsKey(String.valueOf(today.get(Calendar.DAY_OF_MONTH)))) { //check if the habit has been done today
                        if (thisHabit.getHabitData().get(today.get(Calendar.MONTH)).get(String.valueOf(today.get(Calendar.DAY_OF_MONTH))).equals("present")) {
                            doneToday.setText(R.string.habit_done);
                            completedToday = true;
                        }
                    }
                } else {
                    doneToday.setText(R.string.habit_done);
                    completedToday = true;
                }
            }

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            if (!completedToday && (thisHabitLastUpdated.get(Calendar.DAY_OF_YEAR) != yesterday.get(Calendar.DAY_OF_YEAR) || thisHabitLastUpdated.get(Calendar.YEAR) != yesterday.get(Calendar.YEAR))) { //if was last updated neither today or yesterday, set currentStreak to 0.
                thisHabit.setCurrentStreak(0);
                currentStreak.setText(String.format("Current Streak: %d", thisHabit.getCurrentStreak()));
            }
            fabEditHabit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { //if click on fab, go to edit habit
                    Intent editHabit = new Intent(UserHabit.this, EditHabit.class);
                    editHabit.putExtra("habit", (Serializable) thisHabit);
                    editHabit.putExtra("pos", position);
                    startActivity(editHabit);
                }
            });

            //initialise calendar values
            customCalendar = findViewById(R.id.custom_calendar);

            HashMap<Object, Property> descHashMap = new HashMap<>();
            Property defaultProperty = new Property();
            defaultProperty.layoutResource = R.layout.default_view;
            defaultProperty.dateTextViewResource = R.id.text_view;
            descHashMap.put("default", defaultProperty);
            Property currentProperty = new Property();
            currentProperty.layoutResource = R.layout.current_view;
            currentProperty.dateTextViewResource = R.id.text_view;
            descHashMap.put("current", currentProperty);
            Property presentProperty = new Property();
            presentProperty.layoutResource = R.layout.present_view;
            presentProperty.dateTextViewResource = R.id.text_view;
            descHashMap.put("present", presentProperty);
            customCalendar.setMapDescToProp(descHashMap);

            Calendar calendar = Calendar.getInstance();
            HashMap<Integer, Object> usableHashmap = new HashMap<>();
            usableHashmap.put(calendar.get(Calendar.DAY_OF_MONTH), "current");
            int month = calendar.get(Calendar.MONTH);
            for (Map.Entry<String, Object> set : thisHabit.getHabitData().get(month).entrySet()) {
                if (!(Integer.parseInt(set.getKey()) == calendar.get(Calendar.DAY_OF_MONTH))) {
                    usableHashmap.put(Integer.parseInt(set.getKey()), set.getValue());
                }
            }

            customCalendar.setDate(calendar, usableHashmap);
            customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
            customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        } else { //unable to get the extras from intent.
            Toast.makeText(getApplicationContext(), "Error loading habit", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //get share message and use intent to go to outside app
        if (item.getItemId() == R.id.share_menu) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareStr = "I'm on a " + thisHabit.getCurrentStreak() + " day streak of my habit " + thisHabit.getName();
            intent.putExtra(Intent.EXTRA_TEXT, shareStr);
            startActivity(intent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onButtonClickMarkHabitComplete(View view) {
        if (!completedToday) { //if hasn't been completed today, update to mark as done today
            AlertDialog.Builder builder = new AlertDialog.Builder(UserHabit.this);
            builder.setMessage("This action cannot be undone, are you sure you want to mark today complete?");
            builder.setTitle("Warning!");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doneToday.setText(R.string.habit_done);
                    completedToday = true;

                    Calendar thisHabitLastUpdated = Calendar.getInstance();
                    thisHabitLastUpdated.setTime(thisHabit.getLastUpdatedDate());
                    Calendar thisHabitCreatedOn = Calendar.getInstance();
                    thisHabitCreatedOn.setTime(thisHabit.getCreatedOnDate());
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DAY_OF_YEAR, -1);
                    //check if was last updated yesterday or if was last updated when it was created
                    if ((thisHabitLastUpdated.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) && thisHabitLastUpdated.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR))
                            || (thisHabitLastUpdated.get(Calendar.DAY_OF_YEAR) == thisHabitCreatedOn.get(Calendar.DAY_OF_YEAR) && thisHabitLastUpdated.get(Calendar.YEAR) == thisHabitCreatedOn.get(Calendar.YEAR))) {
                        thisHabit.setCurrentStreak(thisHabit.getCurrentStreak() + 1); //update current streak
                        if (thisHabit.getCurrentStreak() >= thisHabit.getBestStreak()) { //if current streak is greater than best streak
                            thisHabit.setBestStreak(thisHabit.getCurrentStreak()); //update best streak
                        }
                    } else { //if wasn't updated yesterday or when it was created
                        thisHabit.setCurrentStreak(1); //update current streak
                    }
                    thisHabit.getHabitData().get(Calendar.getInstance().get(Calendar.MONTH)).put(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), "present");
                    thisHabit.setLastUpdatedDate(Calendar.getInstance().getTime());

                    bestStreak.setText(String.format("Best Streak: %d", thisHabit.getBestStreak()));
                    currentStreak.setText(String.format("Current Streak: %d", thisHabit.getCurrentStreak()));

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    database = FirebaseDatabase.getInstance();
                    ref = database.getReference().child("users/" + user.getUid() + "/habits/" + position);

                    ref.setValue(thisHabit).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Successfully did habit!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error recording your habit progress. Try again later.", Toast.LENGTH_LONG).show();
                                Intent userHome = new Intent(UserHabit.this, UserHome.class);
                                startActivity(userHome);
                                finish();
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
        } else { //if already done today, just run a toast informing user of this
            Toast.makeText(getApplicationContext(), "Already done today!", Toast.LENGTH_LONG).show();
        }
    }

    public void onButtonClickAccountabilityGallery(View view) {
        Intent intent = new Intent(UserHabit.this, AccountabilityGallery.class);
        intent.putExtra("habitName", thisHabit.getName());
        startActivity(intent);
    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) { //manage when the calendar changes month
        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        boolean setCurrentDay = false;
        if (newMonth.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) { //if the month is the current month, have today marked blue
            arr[0].put(calendar.get(Calendar.DAY_OF_MONTH), "current");
            setCurrentDay = true;
        }
        for (Map.Entry<String, Object> set : thisHabit.getHabitData().get(newMonth.get(Calendar.MONTH)).entrySet()) {
            if (!(setCurrentDay && (Integer.parseInt(set.getKey()) == calendar.get(Calendar.DAY_OF_MONTH)))) { //it habit has been done today, keep blue not green
                arr[0].put(Integer.parseInt(set.getKey()), set.getValue());
            }
        }
        return arr;
    }

}