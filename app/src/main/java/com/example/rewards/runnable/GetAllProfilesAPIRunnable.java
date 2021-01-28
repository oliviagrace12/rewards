package com.example.rewards.runnable;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.rewards.LeaderboardActivity;
import com.example.rewards.domain.Profile;
import com.example.rewards.domain.Reward;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GetAllProfilesAPIRunnable implements Runnable {

    private static final String TAG = "GetAllProfilesAPIRunnable";

    private final String apiKey;
    private final LeaderboardActivity leaderboardActivity;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public GetAllProfilesAPIRunnable(String apiKey, LeaderboardActivity leaderboardActivity) {
        this.apiKey = apiKey;
        this.leaderboardActivity = leaderboardActivity;
    }

    @Override
    public void run() {
        String jsonResponse = requestData(createUrlString());
        try {
            List<Profile> profiles = parse(jsonResponse);
            leaderboardActivity.runOnUiThread(() -> leaderboardActivity.setProfiles(profiles));
        } catch (JSONException e) {
            Log.w(TAG, "Could not parse profiles: " + e.getMessage());
        }
    }

    private List<Profile> parse(String jsonResponse) throws JSONException {
        JSONArray jsonProfiles = new JSONArray(jsonResponse);
        List<Profile> profiles = new ArrayList<>();
        for (int i = 0; i < jsonProfiles.length(); i++) {
            JSONObject jsonProfile = jsonProfiles.getJSONObject(i);

            Profile profile = new Profile();
            profile.setFirstName(jsonProfile.getString("firstName"));
            profile.setLastName(jsonProfile.getString("lastName"));
            profile.setUsername(jsonProfile.getString("userName"));
            profile.setDepartment(jsonProfile.getString("department"));
            profile.setStory(jsonProfile.getString("story"));
            profile.setPosition(jsonProfile.getString("position"));
            profile.setBit46EncodedPhoto(jsonProfile.getString("imageBytes"));

            JSONArray jsonRewards = jsonProfile.getJSONArray("rewardRecordViews");
            List<Reward> rewards = new ArrayList<>();
            for (int j = 0; j < jsonRewards.length(); j++) {
                JSONObject rewardJson = jsonRewards.getJSONObject(j);
                Reward reward = new Reward();
                reward.setGiverName(rewardJson.getString("giverName"));
                reward.setAmount(rewardJson.getInt("amount"));
                reward.setNote(rewardJson.getString("note"));
                reward.setAwardDate(getDate(rewardJson.getString("awardDate")));
                rewards.add(reward);
            }
            profile.setRewards(rewards);

            profiles.add(profile);
        }

        return profiles;
    }

    private LocalDate getDate(String awardDate) {
        return LocalDate.parse(awardDate, dateTimeFormatter);
    }

    private String createUrlString() {
        return new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Profile")
                .appendPath("GetAllProfiles")
                .build().toString();
    }

    private String requestData(String urlString) {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("ApiKey", apiKey);

            conn.connect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return "";
            }
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            Log.d(TAG, "Response: " + stringBuilder.toString());
        } catch (IOException ex) {
            Log.e(TAG, "Error in getting info: " + ex.getLocalizedMessage(), ex);
            return "";
        }

        return stringBuilder.toString();
    }
}
