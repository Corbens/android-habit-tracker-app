package com.example.courseworkhabittracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { //create notification

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notifyHabit")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Habit Tracker")
                .setContentText("Remember to log your habits!");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(111, mBuilder.build());
    }
}