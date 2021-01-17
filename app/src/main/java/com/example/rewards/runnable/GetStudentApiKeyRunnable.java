package com.example.rewards.runnable;

import android.net.Uri;
import android.util.Log;

import com.example.rewards.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStudentApiKeyRunnable implements Runnable {

    private static final String TAG = "GetStudentApiKeyRunnable";

    private final String firstName;
    private final String lastName;
    private final String studentId;
    private final String email;
    private final MainActivity mainActivity;

    public GetStudentApiKeyRunnable(MainActivity mainActivity, String firstName, String lastName, String studentId, String email) {
        this.mainActivity = mainActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.email = email;
    }

    @Override
    public void run() {
        String urlString = new Uri.Builder()
                .scheme("http")
                .authority("christopherhield.org")
                .appendPath("api")
                .appendPath("Profile")
                .appendPath("GetStudentApiKey")
                .appendQueryParameter("firstName", firstName)
                .appendQueryParameter("lastname", lastName)
                .appendQueryParameter("studentId", studentId)
                .appendQueryParameter("email", email)
                .build().toString();

        String responseJson = requestData(urlString);
        try {
            String apiKey = parse(responseJson);
            mainActivity.runOnUiThread(() -> {
                mainActivity.setApiKey(apiKey);
                mainActivity.runApiKeyConfirmationDialogue(firstName, lastName, email, studentId);
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error in parsing info: " + e.getLocalizedMessage(), e);
            mainActivity.runOnUiThread(() -> {
                mainActivity.setApiKey("invalid");
                mainActivity.runApiKeyErrorDialogue(e.getLocalizedMessage());
            });
        }
    }

    private String parse(String responseJson) throws JSONException {
        Log.i(TAG, "parse response json: " + responseJson);
        JSONObject object = new JSONObject(responseJson);
        return object.getString("apiKey");
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
