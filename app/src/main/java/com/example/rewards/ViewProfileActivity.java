package com.example.rewards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView imageView;
    private TextView rewardsLabel;
    private String apiKey;

    private String profileJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.your_profile));

        profileJson = getIntent().getStringExtra(getString(R.string.profile));
        apiKey = getIntent().getStringExtra(getString(R.string.api_key));
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
        imageView = findViewById(R.id.profilePhoto);
        rewardsLabel = findViewById(R.id.profileRewardsLabel);

        setFields(profile);
    }

    private void setFields(Profile profile) {
        firstLastNameView.setText(getString(
                R.string.last_first_name, profile.getLastName(), profile.getFirstName()));
        usernameView.setText(getString(R.string.username_in_parens, profile.getUsername()));
        locationView.setText(profile.getLocation());
        pointsAwardedView.setText(String.valueOf(profile.getPointsAwarded()));
        departmentView.setText(profile.getDepartment());
        positionView.setText(profile.getPosition());
        pointsToAwardView.setText(String.valueOf(profile.getRemainingPointsToAward()));
        storyView.setText(profile.getStory());
        imageView.setImageBitmap(textToImage(profile.getBit46EncodedPhoto()));
        rewardsLabel.setText(getString(R.string.rewards_profile, profile.getRewards().size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editProfileOption) {
            Intent intent = new Intent(this, CreateProfileActivity.class);
            intent.putExtra(getString(R.string.api_key), apiKey);
            intent.putExtra(getString(R.string.is_edit), true);
            intent.putExtra(getString(R.string.profile), profileJson);
            startActivity(intent);
        } else if (item.getItemId() == R.id.leaderboardOption) {
            Intent intent = new Intent(this, LeaderboardActivity.class);
            intent.putExtra(getString(R.string.api_key), apiKey);
            startActivity(intent);
        }
        return true;
    }

    public Bitmap textToImage(String imageString64) {
        if (imageString64 == null) {
            return null;
        }
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}