package com.example.courseworkhabittracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterSocial extends RecyclerView.Adapter<RecyclerAdapterSocial.MyViewHolder> { //adapter for showing lists of users
    private ArrayList<User> usersList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapterSocial(ArrayList<User> usersList, RecyclerViewClickListener listener) {
        this.usersList = usersList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameText;
        private TextView totalText;

        public MyViewHolder(final View view) {
            super(view);
            nameText = view.findViewById(R.id.textViewUsernameVal);
            totalText = view.findViewById(R.id.textViewTotalHabits);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public RecyclerAdapterSocial.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users, parent, false);
        return new RecyclerAdapterSocial.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = usersList.get(position).getUsername();
        String total = "Total Habits: " + usersList.get(position).getHabits().size();
        holder.nameText.setText(name);
        holder.totalText.setText(total);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}
