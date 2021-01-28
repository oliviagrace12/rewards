package com.example.rewards.runnable;

import android.net.Uri;
import android.util.Log;

import com.example.rewards.ViewProfileActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteProfileAPIRunnable implements Runnable {

    private static final String TAG = "DeleteProfileAPIRunnabl";

    private final String apiKey;
    private final String username;
    private final ViewProfileActivity viewProfileActivity;

    public DeleteProfileAPIRunnable(String apiKey, String username, ViewProfileActivity viewProfileActivity) {
        this.apiKey = apiKey;
        this.username = username;
        this.viewProfileActivity = viewProfileActivity;
    }

    @Override
    public void run() {
        deleteProfile();
        viewProfileActivity.finishAffinity();
    }

    private String createUrlString() {
        return new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Profile")
                .appendPath("DeleteProfile")
                .appendQueryParameter("username", username)
                .build().toString();
    }

    private String deleteProfile() {
        String urlString = createUrlString();
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("ApiKey", apiKey);
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                String errorMessage = readFromStream(conn.getErrorStream());
                Log.i(TAG, "Error message: " + errorMessage);
                return "";
            } else {
                String responseMessage = readFromStream(conn.getInputStream());
                Log.i(TAG, "Response: " + responseMessage);
                return responseMessage;
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error in getting info: " + ex.getLocalizedMessage(), ex);
        }
        return "";
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
