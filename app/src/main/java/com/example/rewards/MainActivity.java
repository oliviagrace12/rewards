package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rewards.runnable.GetStudentApiKeyRunnable;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runApiKeyDialogue();
    }

    public void createNewProfile(View view) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }

    public void runApiKeyDialogue() {
        // Dialog with a layout

        // Inflate the dialog's layout
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.api_key_dialogue, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to request an API key:");
        builder.setTitle("API Key Needed");
        builder.setIcon(R.drawable.logo);

        // Set the inflated view to be the builder's view
        builder.setView(view);

        builder.setPositiveButton("OK", (dialog, id) -> {

            // Multiply the 2 values together and display the results
            EditText firstName = view.findViewById(R.id.apiDialogueFirstName);
            EditText lastName = view.findViewById(R.id.apiDialogueLastName);
            EditText email = view.findViewById(R.id.apiDialogueEmail);
            EditText studentId = view.findViewById(R.id.apiDialogueId);

            GetStudentApiKeyRunnable runnable = new GetStudentApiKeyRunnable(
                    firstName.getText().toString(), lastName.getText().toString(),
                    studentId.getText().toString(), email.getText().toString());

            Toast.makeText(MainActivity.this, "You entered: " + firstName.getText().toString() +
                            lastName.getText().toString() + studentId.getText().toString() + email.getText().toString(),
                    Toast.LENGTH_SHORT).show();
//            new Thread(runnable).start();
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> {
            // todo quit app?
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}