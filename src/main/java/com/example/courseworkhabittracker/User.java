package com.example.courseworkhabittracker;

import java.util.ArrayList;

public class User {
    private String accountName, username;
    private boolean visible;
    private ArrayList<Habit> habits;

    public User(String accountName, String username, ArrayList<Habit> habits) {
        this.accountName = accountName;
        this.username = username;
        this.habits = habits;
    }

    public User() {
        this.visible = false; //by default make account not visible to public
        ArrayList<Habit> habitList = new ArrayList<>(); //give the user two habits by default
        Habit habit1 = new Habit("Meditate");
        Habit habit2 = new Habit("Water Plants");
        habitList.add(habit1);
        habitList.add(habit2);
        setHabits(habitList);
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public ArrayList<Habit> getHabits() {
        return habits;
    }

    public void setHabits(ArrayList<Habit> habits) {
        this.habits = habits;
    }
}
