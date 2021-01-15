package com.example.rewards.runnable;

import android.net.Uri;
import android.util.Log;

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

    public GetStudentApiKeyRunnable(String firstName, String lastName, String studentId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.email = email;
    }

    @Override
    public void run() {
        String urlString = new Uri.Builder()
                .path("http://christopherhield.org/api/Profile/GetStudentApiKey")
                .appendQueryParameter("firstName=", firstName)
                .appendQueryParameter("lastname=", lastName)
                .appendQueryParameter("studentId=", studentId)
                .appendQueryParameter("email=", email)
                .build().toString();

        String responseJson = requestData(urlString);

        try {
            parse(responseJson);
        } catch (JSONException e) {
            Log.e(TAG, "Error in parsing info: " + e.getLocalizedMessage(), e);
        }
    }

    private void parse(String responseJson) throws JSONException {
        JSONObject object = new JSONObject(responseJson);
        String apiKey = object.getString("apiKey");
    }

    private String requestData(String urlString) {
        Log.i(TAG, "Requesting data using URL: " + urlString);
        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
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
