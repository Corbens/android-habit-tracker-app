package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class UserSettings extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference refUsername, refVisible;
    private SharedPreferences sp;
    private BottomNavigationView bottomNavigationView;
    private EditText editTextUsername;
    private Switch switchVisible, switchNotifications;
    private TextView textViewNotiTime;
    private TimePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        refUsername = database.getReference().child("users/" + user.getUid() + "/username");
        refVisible = database.getReference().child("users/" + user.getUid() + "/visible");
        sp = getSharedPreferences("NotiSharedPref", MODE_PRIVATE);
        createNotificationChannel();

        editTextUsername = findViewById(R.id.editTextUsername);
        switchVisible = findViewById(R.id.switchVisible);
        getUserDetails();

        textViewNotiTime = findViewById(R.id.textViewNotiTime);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchNotifications.setChecked(sp.getBoolean("Notifications", false)); //check from sharedPreferences if notifications are on
        if (sp.contains("Hour") && sp.contains("Minute")) { //update the time of notifications from sharedPreferences
            textViewNotiTime.setText(String.format("%02d", sp.getInt("Hour", 8)) + ":" + String.format("%02d", sp.getInt("Minute", 0)));
        }
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //sets notifications to true in sharedPreferences then runs start notifications
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("Notifications", true);
                    editor.commit();
                    startNotifications();
                } else { //sets notifications to false in sharedPreferences then runs stop notifications
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("Notifications", false);
                    editor.commit();
                    stopNotifications();
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_settings);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        Intent home = new Intent(UserSettings.this, UserHome.class);
                        startActivity(home);
                        finish();
                        return true;

                    case R.id.page_social:
                        Intent social = new Intent(UserSettings.this, SocialList.class);
                        startActivity(social);
                        finish();
                        return true;

                    case R.id.page_settings:
                        return true;
                }
                return false;
            }
        });
    }

    public void onButtonClickUserGuide(View view){
        Intent intent = new Intent(UserSettings.this, UserGuide.class);
        startActivity(intent);
    }


    public void onButtonClickUpdateUsername(View view) {
        if (editTextUsername.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_LONG).show();
            return;
        }
        refUsername.setValue(editTextUsername.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Updated username.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error username. Try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void onButtonClickPickTime(View view) { //time picker for selecting notification time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        picker = new TimePickerDialog(UserSettings.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker tp, int setHour, int setMinute) { //update what time is displayed and update sharedPreferences
                textViewNotiTime.setText(String.format("%02d", setHour) + ":" + String.format("%02d", setMinute));
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("Hour", setHour);
                editor.putInt("Minute", setMinute);
                editor.commit();
                if (switchNotifications.isChecked()) { //start notifications
                    stopNotifications();
                    startNotifications();
                }
            }
        }, hour, minutes, true);
        picker.show();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HabitReminderChannel";
            String description = "Channel for Habit Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyHabit", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void getUserDetails() { //update username field and visibility switch based on user data
        refUsername.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String username = (String) task.getResult().getValue();
                editTextUsername.setText(username);
            }
        });
        refVisible.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                boolean visible = (Boolean) task.getResult().getValue();
                switchVisible.setChecked(visible);
                switchVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        refVisible.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() { //update firebase when user changes visibility settings
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Updated account visibility.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error updating account visibility. Try again later.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }


    public void startNotifications() { //gets the time of the notification and tells alarm manager to repeat it every day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = sp.getInt("Hour", 8);
        int minute = sp.getInt("Minute", 0);
        System.out.println(hour);
        System.out.println(minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getApplicationContext(), NotificationHelper.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getApplicationContext(), "Notification turned on.", Toast.LENGTH_LONG).show();

    }


    public void stopNotifications() { //tells the alarms manager to cancel the notification
        Intent intent = new Intent(getApplicationContext(), NotificationHelper.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Notification turned off.", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() { //stops app closing on back button
        return;
    }

}