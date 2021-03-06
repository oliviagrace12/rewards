package com.example.rewards.runnable;

import android.net.Uri;
import android.util.Log;

import com.example.rewards.CreateProfileActivity;
import com.example.rewards.domain.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class CreateProfileAPIRunnable implements Runnable {

    private static final String TAG = "CreateProfileAPIRunnable";

    private final Profile profile;
    private final String apiKey;
    private final CreateProfileActivity createProfileActivity;

    public CreateProfileAPIRunnable(Profile profile, String apiKey,
                                    CreateProfileActivity createProfileActivity) {
        this.profile = profile;
        this.apiKey = apiKey;
        this.createProfileActivity = createProfileActivity;
    }

    @Override
    public void run() {
        String urlString = createUrlString();
        try {
            saveProfile(urlString);
        } catch (IOException e) {
            String error = e.getLocalizedMessage();
            createProfileActivity.runOnUiThread(
                    () ->createProfileActivity.displayErrorDialogue(error));
            return;
        }
        createProfileActivity.runOnUiThread(() -> createProfileActivity.displayProfile(profile));
    }

    private String createUrlString() {
        return new Uri.Builder()
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
                .appendQueryParameter("remainingPointsToAward", profile.getRemainingPointsToAward() + "")
                .appendQueryParameter("location", profile.getLocation())
                .build().toString();
    }

    private void saveProfile(String urlString) throws IOException {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn;
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
            throw new IOException(errorMessage);
        } else {
            String responseMessage = readFromStream(conn.getInputStream());
            Log.i(TAG, "Response: " + responseMessage);
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
