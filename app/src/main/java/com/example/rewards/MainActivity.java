package com.example.rewards;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.rewards.runnable.GetStudentApiKeyRunnable;
import com.example.rewards.runnable.LoginAPIRunnable;

public class MainActivity extends AppCompatActivity {

    MyProjectSharedPreference sharedPreferences;

    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        sharedPreferences = new MyProjectSharedPreference(this);

        if (sharedPreferences.getValue(this.getString(R.string.api_key)).isEmpty()) {
            runApiKeyDialogue();
        }

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        if (hasSavedLoginInfo()) {
            usernameInput.setText(sharedPreferences.getValue(getString(R.string.username)));
            passwordInput.setText(sharedPreferences.getValue(getString(R.string.password)));
        }
    }

    private boolean hasSavedLoginInfo() {
        return sharedPreferences.hasValue(getString(R.string.username)) &&
                sharedPreferences.hasValue(getString(R.string.password));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login(View view) {

        CheckBox rememberLogin = findViewById(R.id.loginCheckBox);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (rememberLogin.isChecked()) {
            sharedPreferences.save(getString(R.string.username), username);
            sharedPreferences.save(getString(R.string.password), password);
        }

        new Thread(new LoginAPIRunnable(username, password,
                sharedPreferences.getValue(this.getString(R.string.api_key)), this))
                .start();
    }

    public void displayLoginErrorDialogue(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unable To Login");
        builder.setIcon(R.drawable.icon);
        builder.setMessage("Reason: " + errorMessage);
        builder.setPositiveButton("OK", (dialogue, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createNewProfile(View view) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra(this.getString(R.string.api_key), sharedPreferences.getValue(this.getString(R.string.api_key)));
        startActivity(intent);
    }

    public void runApiKeyDialogue() {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.api_key_dialogue, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to request an API key:");
        builder.setTitle("API Key Needed");
        builder.setIcon(R.drawable.logo);

        // Set the inflated view to be the builder's view
        builder.setView(view);

        builder.setPositiveButton("OK", (dialog, id) -> {

            EditText firstName = view.findViewById(R.id.apiDialogueFirstName);
            EditText lastName = view.findViewById(R.id.apiDialogueLastName);
            EditText email = view.findViewById(R.id.apiDialogueEmail);
            EditText studentId = view.findViewById(R.id.apiDialogueId);

            GetStudentApiKeyRunnable runnable = new GetStudentApiKeyRunnable(this,
                    firstName.getText().toString(), lastName.getText().toString(),
                    studentId.getText().toString(), email.getText().toString());

            new Thread(runnable).start();

        });

        builder.setNegativeButton("CANCEL", (dialog, id) -> {
            runApiKeyDialogue();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setApiKey(String apiKey) {
        sharedPreferences.save(this.getString(R.string.api_key), apiKey);
    }

    public void runApiKeyConfirmationDialogue(String firstName, String lastName, String email,
                                              String studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.logo);
        builder.setTitle("API Key Received And Stored");
        builder.setMessage("Name: " + firstName + " " + lastName + "\n"
                + "Student ID: " + studentId + "\n"
                + "Email: " + email + "\n"
                + "API Key: " + sharedPreferences.getValue(this.getString(R.string.api_key)));
        builder.setPositiveButton("OK", (dialog, id) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void runApiKeyErrorDialogue(String localizedMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.logo);
        builder.setTitle("Unable To Retrieve API Key");
        builder.setMessage("Error encountered in requesting API key: " + localizedMessage);
        builder.setPositiveButton("OK", (dialog, id) -> {
            runApiKeyDialogue();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clearApiKey(View view) {
        sharedPreferences.removeValue(this.getString(R.string.api_key));
        runApiKeyDialogue();
    }
}