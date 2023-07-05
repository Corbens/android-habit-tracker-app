package com.example.courseworkhabittracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> { //adapter for showing habits
    private ArrayList<Habit> habitsList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapter(ArrayList<Habit> habitsList, RecyclerViewClickListener listener) {
        this.habitsList = habitsList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameText;
        private TextView streakText;

        public MyViewHolder(final View view) {
            super(view);
            nameText = view.findViewById(R.id.textViewHabitVal);
            streakText = view.findViewById(R.id.textViewStreakVal);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String name = habitsList.get(position).getName();
        String currentStreak = "Current Streak: " + habitsList.get(position).getCurrentStreak();
        holder.nameText.setText(name);
        holder.streakText.setText(currentStreak);
    }

    @Override
    public int getItemCount() {
        return habitsList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}
