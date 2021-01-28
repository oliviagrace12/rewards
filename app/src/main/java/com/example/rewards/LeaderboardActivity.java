package com.example.rewards;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.rewards.domain.Profile;
import com.example.rewards.runnable.GetAllProfilesAPIRunnable;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private List<Profile> profiles = new ArrayList<>();
    private ProfileAdapter adapter;
    private String apiKey;
    private Profile currentUserProfile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        apiKey = getIntent().getStringExtra(getString(R.string.api_key));
        currentUserProfile = (Profile) getIntent().getSerializableExtra(getString(R.string.profile));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.leaderboard));

        recyclerView = findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        adapter = new ProfileAdapter(profiles, this, currentUserProfile.getUsername());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(new GetAllProfilesAPIRunnable(apiKey, this)).start();
    }

    @Override
    public void onClick(View v) {
        if (profiles.isEmpty()) {
            return;
        }
        int position = recyclerView.getChildLayoutPosition(v);
        Profile profile = profiles.get(position);

        if (profile.getUsername().equals(currentUserProfile.getUsername())) {
            return;
        }

        Intent intent = new Intent(this, RewardActivity.class);
        intent.putExtra(getString(R.string.profile), profile);
        intent.putExtra(getString(R.string.current_user_profile), currentUserProfile);
        intent.putExtra(getString(R.string.api_key), apiKey);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void updateProfiles(List<Profile> newProfiles) {
        profiles.clear();
        profiles.addAll(newProfiles);
        profiles.sort((p1, p2) -> p1.getPointsAwarded().compareTo(p2.getPointsAwarded()));
        adapter.notifyDataSetChanged();
    }
}