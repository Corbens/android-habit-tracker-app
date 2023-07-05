package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SocialHabit extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Boolean completedToday;
    private TextView habitName, doneToday, bestStreak, currentStreak;
    private Habit thisHabit;
    private CustomCalendar customCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_habit);

        completedToday = false; //variable to determine if habit has been done today
        habitName = findViewById(R.id.textViewSocialHabitName);
        doneToday = findViewById(R.id.textViewDoneTodaySocial);
        bestStreak = findViewById(R.id.textViewBestStreakSocial);
        currentStreak = findViewById(R.id.textViewCurrentStreakSocial);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_social);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        Intent home = new Intent(SocialHabit.this, UserHome.class);
                        startActivity(home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_social:
                        Intent social = new Intent(SocialHabit.this, SocialList.class);
                        startActivity(social.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_settings:
                        Intent settings = new Intent(SocialHabit.this, UserSettings.class);
                        startActivity(settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;
                }
                return false;
            }
        });

        if (getIntent().getExtras() != null) { //check you're able to get the extras from the intent
            thisHabit = (Habit) getIntent().getExtras().getSerializable("habit");

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

            //initialise calendar values
            customCalendar = findViewById(R.id.custom_calendar_social);

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
            customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this::onNavigationButtonClicked);
            customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this::onNavigationButtonClicked);

        } else { //unable to get the extras from intent.
            Toast.makeText(getApplicationContext(), "Error loading habit", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) { //handle when the calendar changes month
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