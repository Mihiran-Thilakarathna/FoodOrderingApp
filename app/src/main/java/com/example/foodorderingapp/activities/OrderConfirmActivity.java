package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge; // NEW
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // NEW
import androidx.core.view.ViewCompat; // NEW
import androidx.core.view.WindowInsetsCompat; // NEW

import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;

public class OrderConfirmActivity extends AppCompatActivity {

    Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Enable Edge-To-Edge ---
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_confirm);

        // --- Hide the default purple Action Bar ---
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize the button
        btnGoHome = findViewById(R.id.btnGoHome);

        // Click listener to navigate back to Home Screen
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderConfirmActivity.this, MainActivity.class);
            // Clear the back stack so the user cannot press 'Back' to return to the confirmation screen
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // --- Window Insets Logic to remove bottom white space ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_order_confirm_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}