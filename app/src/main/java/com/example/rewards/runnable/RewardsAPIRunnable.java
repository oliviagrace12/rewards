package com.example.rewards.runnable;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.rewards.LeaderboardActivity;
import com.example.rewards.R;
import com.example.rewards.RewardActivity;
import com.example.rewards.domain.Reward;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RewardsAPIRunnable implements Runnable {

    private static final String TAG = "RewardsAPIRunnable";

    private final String apiKey;
    private final Reward reward;
    private final String receiverUsername;
    private final String giverUsername;
    private final RewardActivity rewardActivity;

    public RewardsAPIRunnable(String apiKey, Reward reward, String receiverUsername,
                              String giverUsername, RewardActivity rewardActivity) {
        this.apiKey = apiKey;
        this.reward = reward;
        this.receiverUsername = receiverUsername;
        this.giverUsername = giverUsername;
        this.rewardActivity = rewardActivity;
    }

    @Override
    public void run() {
        saveProfile(createUrlString());
    }

    private String createUrlString() {
        return new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Rewards")
                .appendPath("AddRewardRecord")
                .appendQueryParameter("receiverUser", receiverUsername)
                .appendQueryParameter("giverUser", giverUsername)
                .appendQueryParameter("giverName", reward.getGiverName())
                .appendQueryParameter("amount", reward.getAmount().toString())
                .appendQueryParameter("note", reward.getNote())
                .build().toString();
    }

    private void saveProfile(String urlString) {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("ApiKey", apiKey);
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                Log.i(TAG, "HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                String errorMessage = readFromStream(conn.getErrorStream());
                Log.i(TAG, "Error message: " + errorMessage);
                rewardActivity.runOnUiThread(
                        () -> rewardActivity.displayRewardErrorDialogue(errorMessage));
            } else {
                String responseMessage = readFromStream(conn.getInputStream());
                Log.i(TAG, "Response: " + responseMessage);
                Intent intent = new Intent(rewardActivity, LeaderboardActivity.class);
                intent.putExtra(rewardActivity.getString(R.string.api_key), apiKey);
                intent.putExtra(rewardActivity.getString(R.string.profile),
                        rewardActivity.getCurrentUserProfile());
                rewardActivity.startActivity(intent);
             }
        } catch (IOException ex) {
            Log.e(TAG, "Error in getting info: " + ex.getLocalizedMessage(), ex);
        }
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader =
                new BufferedReader((new InputStreamReader(inputStream)));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }

        return stringBuilder.toString();
    }
}
