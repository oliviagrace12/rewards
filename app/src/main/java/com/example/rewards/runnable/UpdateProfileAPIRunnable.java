package com.example.rewards.runnable;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.rewards.CreateProfileActivity;
import com.example.rewards.domain.Profile;
import com.example.rewards.domain.Reward;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UpdateProfileAPIRunnable implements Runnable {

    private static final String TAG = "UpdateProfileAPIRunnabl";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Profile profile;
    private final String apiKey;
    private final CreateProfileActivity createProfileActivity;

    public UpdateProfileAPIRunnable(Profile profile, String apiKey, CreateProfileActivity createProfileActivity) {
        this.profile = profile;
        this.apiKey = apiKey;
        this.createProfileActivity = createProfileActivity;
    }

    @Override
    public void run() {
        try {
            Profile updatedProfile = saveProfile(createUrlString());
            createProfileActivity.runOnUiThread(
                    () -> createProfileActivity.displayProfile(updatedProfile));
        } catch (IOException | JSONException e) {
            String error = e.getLocalizedMessage();
            createProfileActivity.runOnUiThread(
                    () -> createProfileActivity.displayErrorDialogue(error));
            return;
        }

    }

    private String createUrlString() {
        return new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Profile")
                .appendPath("UpdateProfile")
                .appendQueryParameter("firstName", profile.getFirstName())
                .appendQueryParameter("lastname", profile.getLastName())
                .appendQueryParameter("username", profile.getUsername())
                .appendQueryParameter("department", profile.getDepartment())
                .appendQueryParameter("story", profile.getStory())
                .appendQueryParameter("position", profile.getPosition())
                .appendQueryParameter("password", profile.getPassword())
                .appendQueryParameter("location", profile.getLocation())
                .build().toString();
    }

    private Profile saveProfile(String urlString) throws IOException, JSONException {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn;
        conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("ApiKey", apiKey);
        conn.setDoOutput(true);
        conn.connect();

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = profile.getBit46EncodedPhoto().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.i(TAG, "HTTP ResponseCode NOT OK: " + conn.getResponseCode());
            String errorMessage = readFromStream(conn.getErrorStream());
            Log.i(TAG, "Error message: " + errorMessage);
            throw new IOException(errorMessage);
        } else {
            String responseMessage = readFromStream(conn.getInputStream());
            Log.i(TAG, "Response: " + responseMessage);
            return parseProfileResponse(responseMessage);
        }
    }

    private Profile parseProfileResponse(String responseMessage) throws JSONException {
        JSONObject jsonProfile = new JSONObject(responseMessage);

        Profile profile = new Profile();
        profile.setFirstName(jsonProfile.getString("firstName"));
        profile.setLastName(jsonProfile.getString("lastName"));
        profile.setUsername(jsonProfile.getString("userName"));
        profile.setDepartment(jsonProfile.getString("department"));
        profile.setStory(jsonProfile.getString("story"));
        profile.setPassword(jsonProfile.getString("password"));
        profile.setPosition(jsonProfile.getString("position"));
        profile.setRemainingPointsToAward(jsonProfile.getInt("remainingPointsToAward"));
        profile.setLocation(jsonProfile.getString("location"));
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

        return profile;
    }

    private LocalDate getDate(String awardDate) {
        return LocalDate.parse(awardDate.replace('T', ' '), dateTimeFormatter);
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
