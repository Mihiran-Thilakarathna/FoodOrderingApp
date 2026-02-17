package com.example.foodorderingapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUsername, tvEmail, tvPhone;
    Button btnLogout;
    DBHelper dbHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Database and Session
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        // Initialize UI Components
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvPhone = findViewById(R.id.tvProfilePhone);
        btnLogout = findViewById(R.id.btnLogout);

        // Get the logged-in username from Session
        String username = sessionManager.getUsername();

        // Load details from Database
        loadUserProfile(username);

        // Logout Button Click Listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear session and go back to Login
                sessionManager.logoutUser();
                finish();
            }
        });
    }

    private void loadUserProfile(String username) {
        Cursor cursor = dbHelper.getUserDetails(username);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Error: No details found", Toast.LENGTH_SHORT).show();
        } else {
            // Move cursor to first row
            if (cursor.moveToFirst()) {
                // Assuming columns are: username, password, email, phone
                // We use getColumnIndex to be safe
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

                // Set data to TextViews
                tvUsername.setText(username);
                tvEmail.setText(email);
                tvPhone.setText(phone);
            }
        }
    }
}