package com.example.rewards.runnable;

import android.net.Uri;
import android.util.Log;

import com.example.rewards.domain.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CreateProfileAPIRunnable implements Runnable{

    private static final String TAG = "CreateProfileAPIRunnable";

    private final Profile profile;
    private final String apiKey;

    public CreateProfileAPIRunnable(Profile profile, String apiKey) {
        this.profile = profile;
        this.apiKey = apiKey;
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

    }

    private String requestData(String urlString) {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("ApiKey", apiKey);
            conn.setDoOutput(true);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = profile.getBit46EncodedPhoto().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
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
