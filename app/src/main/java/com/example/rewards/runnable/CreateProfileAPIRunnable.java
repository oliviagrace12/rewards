package com.example.rewards.runnable;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.rewards.CreateProfileActivity;
import com.example.rewards.R;
import com.example.rewards.ViewProfileActivity;
import com.example.rewards.domain.Profile;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CreateProfileAPIRunnable implements Runnable {

    private static final String TAG = "CreateProfileAPIRunnable";

    private final Profile profile;
    private final String apiKey;
    private final CreateProfileActivity createProfileActivity;

    public CreateProfileAPIRunnable(Profile profile, String apiKey, CreateProfileActivity createProfileActivity) {
        this.profile = profile;
        this.apiKey = apiKey;
        this.createProfileActivity = createProfileActivity;
    }

    @Override
    public void run() {
        String urlString = new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Profile")
                .appendPath("CreateProfile")
                .appendQueryParameter("firstName", profile.getFirstName())
                .appendQueryParameter("lastname", profile.getLastName())
                .appendQueryParameter("username", profile.getUsername())
                .appendQueryParameter("department", profile.getDepartment())
                .appendQueryParameter("story", profile.getStory())
                .appendQueryParameter("position", profile.getPosition())
                .appendQueryParameter("password", profile.getPassword())
                .appendQueryParameter("remainingPointsToAward", "1000")
                .appendQueryParameter("location", "todo") //todo
                .build().toString();

        saveProfile(urlString);
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
            conn.setDoOutput(true);
            conn.connect();

            try (OutputStream os = conn.getOutputStream()) {

                byte[] input = profile.getBit46EncodedPhoto().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                Log.i(TAG, "HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                String errorMessage = readFromStream(conn.getErrorStream());
                Log.i(TAG, "Error message: " + errorMessage);
            } else {
                String responseMessage = readFromStream(conn.getInputStream());
                Log.i(TAG, "Response: " + responseMessage);
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
