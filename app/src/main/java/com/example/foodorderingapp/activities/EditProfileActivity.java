package com.example.foodorderingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge; // NEW
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // NEW
import androidx.core.view.ViewCompat; // NEW
import androidx.core.view.WindowInsetsCompat; // NEW

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    ImageView imgEditProfile;
    EditText etEditEmail, etEditPhone, etEditAddress;
    Button btnSaveProfile;
    // REMOVED: btnBackEdit

    DBHelper dbHelper;
    SessionManager sessionManager;

    String currentUsername;
    String selectedImageUriString = ""; // To store the image path

    // Launcher to pick image from gallery
    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            // Take persistable URI permission so app can load it later even after restart
                            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            // Save URI string and show image
                            selectedImageUriString = selectedImageUri.toString();
                            imgEditProfile.setImageURI(selectedImageUri);
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to pick image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);
        currentUsername = sessionManager.getUsername();

        imgEditProfile = findViewById(R.id.imgEditProfile);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditAddress = findViewById(R.id.etEditAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // REMOVED: btnBackEdit references

        // Load existing details
        loadExistingData();

        // Image Click -> Open Gallery
        imgEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        // Save Button Click
        btnSaveProfile.setOnClickListener(v -> {
            String newEmail = etEditEmail.getText().toString().trim();
            String newPhone = etEditPhone.getText().toString().trim();
            String newAddress = etEditAddress.getText().toString().trim();

            if (newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Email and Phone cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Database
            boolean isUpdated = dbHelper.updateUserProfile(currentUsername, newEmail, newPhone, newAddress, selectedImageUriString);

            if (isUpdated) {
                Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity and go back to Profile Activity
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });

        // Window Insets Logic to remove bottom white space
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_edit_profile_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadExistingData() {
        Cursor cursor = dbHelper.getUserDetails(currentUsername);
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

            int addressIndex = cursor.getColumnIndex("address");
            String address = (addressIndex != -1) ? cursor.getString(addressIndex) : "";

            int imgIndex = cursor.getColumnIndex("profile_image");
            selectedImageUriString = (imgIndex != -1) ? cursor.getString(imgIndex) : "";

            etEditEmail.setText(email);
            etEditPhone.setText(phone);
            if (!address.equals("Not Set")) {
                etEditAddress.setText(address);
            }

            if (selectedImageUriString != null && !selectedImageUriString.isEmpty()) {
                try {
                    imgEditProfile.setImageURI(Uri.parse(selectedImageUriString));
                } catch (Exception e) {
                    imgEditProfile.setImageResource(R.drawable.ic_dummy_profile);
                }
            }
        }
        if (cursor != null) cursor.close();
    }
}