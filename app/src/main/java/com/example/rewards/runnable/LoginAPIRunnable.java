package com.example.rewards.runnable;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.rewards.MainActivity;
import com.example.rewards.R;
import com.example.rewards.ViewProfileActivity;
import com.example.rewards.domain.Profile;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginAPIRunnable implements Runnable {

    private static final String TAG = "LoginAPIRunnable";

    private final String username;
    private final String password;
    private final String apiKey;
    private final MainActivity mainActivity;

    public LoginAPIRunnable(String username, String password, String apiKey,
                            MainActivity mainActivity) {
        this.apiKey = apiKey;
        this.username = username;
        this.password = password;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        String responseJson = requestLoginData(createUrlString());
        Profile profile = null;
        try {
            profile = parseData(responseJson);
        } catch (JSONException e) {
            Log.w(TAG, "Could not parse login response to profile: " + e.getMessage());
        }
        displayProfile(profile);
    }

    private void displayProfile(Profile profile) {
        Intent intent = new Intent(
                mainActivity.getApplicationContext(), ViewProfileActivity.class);
        Gson gson = new Gson();
        intent.putExtra(mainActivity.getString(R.string.profile), gson.toJson(profile));
        mainActivity.startActivity(intent);
    }

    private Profile parseData(String responseJson) throws JSONException {
        JSONObject object = new JSONObject(responseJson);
        Profile profile = new Profile();
        profile.setFirstName(object.getString("firstName"));
        profile.setLastName(object.getString("lastName"));
        profile.setUsername(object.getString("userName"));
        profile.setPassword(object.getString("password"));
        profile.setDepartment(object.getString("department"));
        profile.setPosition(object.getString("position"));
        profile.setRemainingPointsToAward(object.getInt("remainingPointsToAward"));
        profile.setLocation(object.getString("location"));
        profile.setStory(object.getString("story"));
        profile.setBit46EncodedPhoto(object.getString("imageBytes"));

        return profile;
    }

    private String createUrlString() {
        return new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Profile")
                .appendPath("Login")
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", password)
                .build().toString();
    }

    private String requestLoginData(String urlString) {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
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
