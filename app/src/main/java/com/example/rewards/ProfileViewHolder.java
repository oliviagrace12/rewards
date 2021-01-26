package com.example.rewards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    ImageView profileImage;
    TextView firstLastName;
    TextView titleDept;
    TextView points;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.row_photo);
        firstLastName = itemView.findViewById(R.id.row_name);
        titleDept = itemView.findViewById(R.id.row_title);
        points = itemView.findViewById(R.id.row_points);
    }
}
