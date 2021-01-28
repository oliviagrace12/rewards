package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rewards.domain.Profile;
import com.example.rewards.domain.Reward;
import com.example.rewards.runnable.RewardsAPIRunnable;

import java.time.LocalDate;

public class RewardActivity extends AppCompatActivity {

    private static final int MAX_LEN = 80;

    private Profile profile;
    private Profile currentUserProfile;
    private String apiKey;

    private ImageView profileImageView;
    private TextView profileNameView;
    private TextView pointsView;
    private TextView departmentView;
    private TextView positionView;
    private TextView storyView;
    private TextView commentLabelView;

    private EditText pointsToSendEditText;
    private EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        profile = (Profile) getIntent().getSerializableExtra(getString(R.string.profile));
        currentUserProfile = (Profile) getIntent().getSerializableExtra(getString(R.string.current_user_profile));
        apiKey = getIntent().getStringExtra(getString(R.string.api_key));

        profileImageView = findViewById(R.id.rewardPhoto);
        profileNameView = findViewById(R.id.rewardName);
        pointsView = findViewById(R.id.rewardPointsAwardedValue);
        departmentView = findViewById(R.id.rewardDepartmentValue);
        positionView = findViewById(R.id.rewardPositionValue);
        storyView = findViewById(R.id.rewardStoryValue);
        pointsToSendEditText = findViewById(R.id.rewardPointsToSendValue);
        commentEditText = findViewById(R.id.rewardCommentValue);
        commentLabelView = findViewById(R.id.rewardCommentLabel);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(profile.getFirstName() + " " + profile.getLastName());

        fillInProfileValues();
        setupStoryEditText();
    }

    private void fillInProfileValues() {
        profileImageView.setImageBitmap(textToImage(profile.getBit46EncodedPhoto()));
        profileNameView.setText(
                getString(R.string.last_first_name, profile.getLastName(), profile.getFirstName()));
        pointsView.setText(String.valueOf(profile.getPointsAwarded()));
        departmentView.setText(profile.getDepartment());
        positionView.setText(profile.getPosition());
        storyView.setText(profile.getStory());
        commentLabelView.setText(getString(R.string.comment_label, 0, MAX_LEN));
    }

    private void setupStoryEditText() {
        commentEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        commentEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        int len = s.toString().length();
                        String countText = getString(R.string.comment_label, len, MAX_LEN);
                        commentLabelView.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            displaySaveDialogue();
        }
        return true;
    }

    private void displaySaveDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Rewards Points?");
        builder.setMessage(
                "Add rewards for " + profile.getFirstName() + " " + profile.getLastName() + "?");
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("OK", (dialog, id) -> {
            Reward reward = createReward();

            new Thread(new RewardsAPIRunnable(
                            apiKey, reward, profile.getUsername(), currentUserProfile.getUsername()))
                    .start();

            currentUserProfile.setRemainingPointsToAward(
                    currentUserProfile.getRemainingPointsToAward()
                            - Integer.parseInt(pointsToSendEditText.getText().toString()));

            profile.addReward(reward);

            Intent intent = new Intent(this, LeaderboardActivity.class);
            intent.putExtra(getString(R.string.api_key), apiKey);
            intent.putExtra(getString(R.string.profile), currentUserProfile);
            startActivity(intent);

        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Reward createReward() {
        Reward reward = new Reward();
        reward.setGiverName(
                currentUserProfile.getFirstName() + " " + currentUserProfile.getLastName());
        reward.setAmount(Integer.parseInt(pointsToSendEditText.getText().toString()));
        reward.setNote(commentEditText.getText().toString());

        return reward;
    }

    public Bitmap textToImage(String imageString64) {
        if (imageString64 == null) {
            return null;
        }
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}