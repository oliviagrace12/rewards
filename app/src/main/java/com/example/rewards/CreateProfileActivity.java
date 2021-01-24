package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rewards.domain.Profile;
import com.example.rewards.runnable.CreateProfileAPIRunnable;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity {

    private static final int MAX_LEN = 360;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText departmentEditText;
    private EditText titleEditText;
    private EditText storyEditText;
    private TextView storyLabel;
    private ImageButton imageButton;
    private String apiKey;

    private final int REQUEST_IMAGE_GALLERY = 1;
    private final int REQUEST_IMAGE_CAPTURE = 2;

    private File currentImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        apiKey = getIntent().getStringExtra(this.getString(R.string.api_key));

        usernameEditText = findViewById(R.id.inputUsername);
        passwordEditText = findViewById(R.id.inputPassword);
        firstNameEditText = findViewById(R.id.inputFirstName);
        lastNameEditText = findViewById(R.id.inputLastName);
        departmentEditText = findViewById(R.id.inputDepartment);
        titleEditText = findViewById(R.id.inputTitle);
        storyEditText = findViewById(R.id.yourStoryInput);
        storyLabel = findViewById(R.id.yourStoryLabel);
        storyLabel.setText(getString(R.string.your_story, 0, MAX_LEN));
        setupStoryEditText();
        imageButton = findViewById(R.id.profileImage);
    }

    private void setupStoryEditText() {
        storyEditText.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        storyEditText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        int len = s.toString().length();
                        String countText = getString(R.string.your_story, len, MAX_LEN);
                        storyLabel.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
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

    public void selectPhoto(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");
        builder.setNegativeButton("GALLERY", (dialog, id) -> doGallery());
        builder.setPositiveButton("CAMERA", (dialog, id) -> doCamera());
        builder.setNeutralButton("CANCEL", (dialog, id) -> {});
        builder.setIcon(R.drawable.icon);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doCamera() {
        try {
            currentImageFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(
                this, "com.example.android.fileprovider", currentImageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processFullCameraImage();
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processFullCameraImage() {
        Uri selectedImage = Uri.fromFile(currentImageFile);
        imageButton.setImageURI(selectedImage);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "image+";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }

    private void verifyOrObtainGalleryPermisson() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("User Denied Gallery Access");
                builder.setIcon(R.drawable.icon);
                builder.setMessage(
                        "You must allow Rewards to access you Gallery in order to choose a photo");
                builder.setPositiveButton("OK", (dialogue, id) -> {
                });
            }
        }
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null) {
            return;
        }
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        imageButton.setImageBitmap(selectedImage);
    }

    private void displaySaveDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("OK", (dialog, id) -> {
            Profile profile = createProfile();
            saveProfile(profile);
            displayProfile(profile);
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayProfile(Profile profile) {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        Gson gson = new Gson();
        intent.putExtra(this.getString(R.string.profile), gson.toJson(profile));
        this.startActivity(intent);
    }

    private void saveProfile(Profile profile) {
        new Thread(new CreateProfileAPIRunnable(profile, apiKey)).start();
    }

    private Profile createProfile() {
        Profile profile = new Profile();
        profile.setUsername(usernameEditText.getText().toString());
        profile.setPassword(passwordEditText.getText().toString());
        profile.setFirstName(firstNameEditText.getText().toString());
        profile.setLastName(lastNameEditText.getText().toString());
        profile.setDepartment(departmentEditText.getText().toString());
        profile.setPosition(titleEditText.getText().toString());
        profile.setStory(storyEditText.getText().toString());
        profile.setBit46EncodedPhoto(getImageInBase64(imageButton));
        profile.setRemainingPointsToAward(1000);

        return profile;
    }

    public String getImageInBase64(ImageButton imageButton) {
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        int quality = 50;
        String base64 = encodeBase64(bitmap, quality);

        while (base64.length() > 100000) {
            quality -= 5;
            base64 = encodeBase64(bitmap, quality);
        }

        return base64;
    }

    private String encodeBase64(Bitmap origBitmap, int quality) {
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        origBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bitmapAsByteArrayStream);

        return Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
    }
}