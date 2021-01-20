package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.rewards.domain.Profile;
import com.example.rewards.runnable.CreateProfileAPIRunnable;

public class CreateProfileActivity extends AppCompatActivity {

    @SuppressLint("InflateParams")
    private View view;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText departmentEditText;
    private EditText titleEditText;
    private EditText storyEditText;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        apiKey = this.getIntent().getStringExtra(this.getString(R.string.api_key));

        LayoutInflater inflater = LayoutInflater.from(this);
        view = inflater.inflate(R.layout.activity_create_profile, null);
        usernameEditText = view.findViewById(R.id.inputUsername);
        passwordEditText = view.findViewById(R.id.inputPassword);
        firstNameEditText = view.findViewById(R.id.inputFirstName);
        lastNameEditText = view.findViewById(R.id.inputLastName);
        departmentEditText = view.findViewById(R.id.inputDepartment);
        titleEditText = view.findViewById(R.id.inputTitle);
        storyEditText = view.findViewById(R.id.yourStoryInput);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            displaySaveDialogue();
        }
        return true;
    }

    private void displaySaveDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("OK", (dialog, id) -> doSave());
        builder.setNegativeButton("CANCEL", (dialog, id) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSave() {
        Profile profile = new Profile();
        profile.setUsername(usernameEditText.getText().toString());
        profile.setPassword(passwordEditText.getText().toString());
        profile.setFirstName(firstNameEditText.getText().toString());
        profile.setLastName(lastNameEditText.getText().toString());
        profile.setDepartment(departmentEditText.getText().toString());
        profile.setPosition(titleEditText.getText().toString());
        profile.setStory(storyEditText.getText().toString());

        new Thread(new CreateProfileAPIRunnable(profile, apiKey)).start();
    }
}