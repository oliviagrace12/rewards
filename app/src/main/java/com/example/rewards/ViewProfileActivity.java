package com.example.rewards;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rewards.domain.Profile;
import com.example.rewards.domain.Reward;
import com.example.rewards.runnable.DeleteProfileAPIRunnable;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener  {

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

    private Profile profile;

    private List<Reward> rewards = new ArrayList<>();
    private RecyclerView recyclerView;
    private RewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.your_profile));

        profile = (Profile) getIntent().getSerializableExtra(getString(R.string.profile));
        apiKey = getIntent().getStringExtra(getString(R.string.api_key));

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

        recyclerView = findViewById(R.id.profileRewardsRecyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        adapter = new RewardAdapter(profile.getRewards(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rewards.addAll(profile.getRewards());
        adapter.notifyDataSetChanged();

        setFields();
    }

    private void setFields() {
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
            intent.putExtra(getString(R.string.profile), profile);
            startActivity(intent);
        } else if (item.getItemId() == R.id.leaderboardOption) {
            Intent intent = new Intent(this, LeaderboardActivity.class);
            intent.putExtra(getString(R.string.api_key), apiKey);
            intent.putExtra(getString(R.string.profile), profile);
            startActivity(intent);
        } else if (item.getItemId() == R.id.deleteProfileOption) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.icon);
            builder.setTitle("Delete Profile?");
            builder.setMessage(
                    "Delete Profile for " + profile.getFirstName() + " " + profile.getLastName()
                            + "? (The Rewards app will be closed upon deletion)");
            builder.setPositiveButton("OK", (dialogue, id) -> {
                new Thread(
                        new DeleteProfileAPIRunnable(apiKey, profile.getUsername(), this))
                        .start();
            });
            builder.setNegativeButton("CANCEL", (dialogue, id) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}