package com.example.rewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rewards.domain.Profile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder> {

    private final List<Profile> profiles;
    private final LeaderboardActivity leaderboardActivity;

    public ProfileAdapter(List<Profile> profiles, LeaderboardActivity leaderboardActivity) {
        this.profiles = profiles;
        this.leaderboardActivity = leaderboardActivity;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_row, parent, false);
        itemView.setOnClickListener(leaderboardActivity);
        itemView.setOnLongClickListener(leaderboardActivity);

        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profiles.get(position);

        holder.profileImage.setImageBitmap(textToImage(profile.getBit46EncodedPhoto()));
        holder.firstLastName.setText(leaderboardActivity.getString(R.string.last_first_name,
                profile.getLastName(), profile.getFirstName()));
        holder.titleDept.setText(leaderboardActivity.getString(R.string.last_first_name,
                profile.getPosition(), profile.getDepartment()));
        holder.points.setText(profile.getPointsAwarded() + "");

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public Bitmap textToImage(String imageString64) {
        if (imageString64 == null) {
            return null;
        }
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
