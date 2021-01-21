package com.example.rewards;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.rewards.domain.Profile;
import com.google.gson.Gson;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView firstLastNameView;
    private TextView usernameView;
    private TextView locationView;
    private TextView pointsAwardedView;
    private TextView departmentView;
    private TextView positionView;
    private TextView pointsToAwardView;
    private TextView storyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        String profileJson = getIntent().getStringExtra(getString(R.string.profile));
        Gson gson = new Gson();
        Profile profile = gson.fromJson(profileJson, Profile.class);

        firstLastNameView = findViewById(R.id.profileFirstLastName);
        usernameView = findViewById(R.id.profileUsername);
        locationView = findViewById(R.id.profileLocation);
        pointsAwardedView = findViewById(R.id.pointsAwardedValue);
        departmentView = findViewById(R.id.departmentValue);
        positionView = findViewById(R.id.positionValue);
        pointsToAwardView = findViewById(R.id.pointsToAwardValue);
        storyView = findViewById(R.id.profileStoryValue);

        setFields(profile);
    }

    private void setFields(Profile profile) {
        firstLastNameView.setText(getString(
                R.string.last_first_name, profile.getLastName(), profile.getFirstName()));
        usernameView.setText(getString(R.string.username_in_parens, profile.getUsername()));
        locationView.setText("todo location"); //todo
        pointsAwardedView.setText(String.valueOf(profile.getPointsAwarded()));
        departmentView.setText(profile.getDepartment());
        positionView.setText(profile.getPosition());
        pointsToAwardView.setText(String.valueOf(profile.getPointsToAward()));
        storyView.setText(profile.getStory());
    }


}