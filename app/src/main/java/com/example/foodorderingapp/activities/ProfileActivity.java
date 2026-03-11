package com.example.foodorderingapp.activities;

import android.app.AlertDialog; // NEW: Imported for Logout Confirmation Dialog
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge; // NEW
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // NEW
import androidx.core.view.ViewCompat; // NEW
import androidx.core.view.WindowInsetsCompat; // NEW

import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity {

    TextView tvProfileName, tvShowEmail, tvShowPhone, tvShowAddress;
    ImageView imgProfilePic;
    Button btnLogout, btnEditProfile;
    DBHelper dbHelper;
    SessionManager sessionManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Enable Edge-To-Edge ---
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Database and Session
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        // Initialize UI Components
        tvProfileName = findViewById(R.id.tvProfileName);
        tvShowEmail = findViewById(R.id.tvShowEmail);
        tvShowPhone = findViewById(R.id.tvShowPhone);
        tvShowAddress = findViewById(R.id.tvShowAddress);
        imgProfilePic = findViewById(R.id.imgProfilePic);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Get the logged-in username from Session
        String username = sessionManager.getUsername();

        // Load details from Database
        loadUserProfile(username);

        // Edit Profile Button Logic
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // --- Logout Button Click Listener with Confirmation Dialog ---
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure want to Log Out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear session and go back to Login if Yes is clicked
                        sessionManager.logoutUser();

                        // Navigate back to LoginActivity and clear back stack
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Just dismiss the dialog if No is clicked
                        dialog.dismiss();
                    })
                    .show();
        });

        // --- BOTTOM NAVIGATION BAR SETUP ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Highlight Profile Icon

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_cart) {
                    Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_orders) {
                    Intent intent = new Intent(ProfileActivity.this, MyOrderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });

        // --- Window Insets Logic to remove bottom white space ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_profile_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            bottomNavigationView.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
    }

    // Refresh data when returning from Edit Screen
    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(sessionManager.getUsername());
    }

    private void loadUserProfile(String username) {
        Cursor cursor = dbHelper.getUserDetails(username);

        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

            // Load Address and Image
            int addressIndex = cursor.getColumnIndex("address");
            String address = (addressIndex != -1) ? cursor.getString(addressIndex) : "Not Set";

            int imgIndex = cursor.getColumnIndex("profile_image");
            String imgPath = (imgIndex != -1) ? cursor.getString(imgIndex) : "";

            // Set data to TextViews
            tvProfileName.setText(username);
            tvShowEmail.setText(email);
            tvShowPhone.setText(phone);
            tvShowAddress.setText(address);

            // Load Profile Picture if exists
            if(imgPath != null && !imgPath.isEmpty()){
                try {
                    imgProfilePic.setImageURI(Uri.parse(imgPath));
                } catch (Exception e) {
                    imgProfilePic.setImageResource(R.drawable.ic_dummy_profile);
                }
            } else {
                imgProfilePic.setImageResource(R.drawable.ic_dummy_profile);
            }
        } else {
            Toast.makeText(this, "Error: No details found", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}