package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.utils.SessionManager;

public class WelcomeActivity extends AppCompatActivity {

    SessionManager sessionManager;
    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        btnGetStarted = findViewById(R.id.btnGetStarted);

        // Navigate to LoginActivity when "Get Started" is clicked
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent user from returning to Welcome screen using the back button
        });
    }
}