package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge; // NEW: Imported for complete full-screen logic
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // NEW: Imported for correct full-screen handling
import androidx.core.view.ViewCompat; // NEW: Imported for correct full-screen handling
import androidx.core.view.WindowInsetsCompat; // NEW: Imported for correct full-screen handling

import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.utils.SessionManager;

public class WelcomeActivity extends AppCompatActivity {

    SessionManager sessionManager;
    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Enable Edge-To-Edge for complete full-screen logic ---
        EdgeToEdge.enable(this);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Auto-login check: If user is already logged in, skip this screen and go to MainActivity
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close WelcomeActivity
            return;
        }

        // If not logged in, show the welcome screen
        setContentView(R.layout.activity_welcome);

        // Hide the default purple Action Bar for professional design
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnGetStarted = findViewById(R.id.btnGetStarted);

        // Navigate to LoginActivity when "Get Started" is clicked
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent user from returning to Welcome screen using the back button
        });

        // Window Insets Logic to ensure clean full-screen layout without bottom white space
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_welcome_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}