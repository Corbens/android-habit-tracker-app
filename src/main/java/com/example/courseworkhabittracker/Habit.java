package com.example.courseworkhabittracker;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Habit implements Serializable {
    private String name;
    private ArrayList<HashMap<String, Object>> habitData;
    private int bestStreak, currentStreak;
    private long lastUpdated, createdOn;

    public Habit(String name) {
        this.name = name;
        this.habitData = new ArrayList<>();
        for (int i = 0; i < 12; i++) { //get hashmap for each month
            HashMap<String, Object> sampleData = new HashMap<>();
            sampleData.put("32", "default"); //give sample data out of range so hashmap is actually instantiated in firebase
            this.habitData.add(sampleData);
        }
        this.bestStreak = 0;
        this.currentStreak = 0;
        this.lastUpdated = Calendar.getInstance().getTime().getTime();
        this.createdOn = Calendar.getInstance().getTime().getTime();
    }

    public Habit() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<HashMap<String, Object>> getHabitData() {
        return habitData;
    }

    public void setHabitData(ArrayList<HashMap<String, Object>> habitData) {
        this.habitData = habitData;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    //methods to convert long to date as firebase cannot store date objects
    @Exclude
    public Date getLastUpdatedDate() {
        return new Date(lastUpdated);
    }

    @Exclude
    public void setLastUpdatedDate(Date lastUpdated) {
        this.lastUpdated = lastUpdated.getTime();
    }

    @Exclude
    public Date getCreatedOnDate() {
        return new Date(createdOn);
    }

    @Exclude
    public void setCreatedOnDate(Date createdOn) {
        this.createdOn = createdOn.getTime();
    }
}
