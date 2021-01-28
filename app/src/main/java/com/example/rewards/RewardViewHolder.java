package com.example.rewards;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RewardViewHolder extends RecyclerView.ViewHolder  {

    TextView date;
    TextView giverName;
    TextView points;
    TextView note;


    public RewardViewHolder(@NonNull View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.rowRewardDate);
        giverName = itemView.findViewById(R.id.rowRewardGiverName);
        points = itemView.findViewById(R.id.rowRewardPoints);
        note = itemView.findViewById(R.id.rowRewardNote);
    }
}
