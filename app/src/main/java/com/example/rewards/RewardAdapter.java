package com.example.rewards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rewards.domain.Reward;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardViewHolder>  {

    private final List<Reward> rewards;
    private final ViewProfileActivity viewProfileActivity;

    public RewardAdapter(List<Reward> rewards, ViewProfileActivity viewProfileActivity) {
        this.rewards = rewards;
        this.viewProfileActivity = viewProfileActivity;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_row, parent, false);
        itemView.setOnClickListener(viewProfileActivity);
        itemView.setOnLongClickListener(viewProfileActivity);

        return new RewardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewards.get(position);

        holder.date.setText(reward.getAwardDate().toString().substring(0,10));
        holder.giverName.setText(reward.getGiverName());
        holder.points.setText(reward.getAmount());
        holder.note.setText(reward.getNote());
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }
}
