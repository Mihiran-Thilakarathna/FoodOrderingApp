package com.example.foodorderingapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {

    // UI Components and Database Helper Declaration
    EditText etUsername, etEmail, etPhone, etPassword, etConfirmPass;
    Button btnRegister;
    TextView tvLoginLink;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Database Helper
        dbHelper = new DBHelper(this);

        // Map UI components to their respective IDs in the layout
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Handle Register Button Click Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input as strings
                String user = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String pass = etPassword.getText().toString();
                String confirmPass = etConfirmPass.getText().toString();

                // 1. Input Validation: Check for empty fields
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if password and confirm password match
                    if (pass.equals(confirmPass)) {
                        // Check if the username already exists in the database
                        if (!dbHelper.checkUsername(user)) {

                            // 2. Encrypt Password (MANDATORY Guideline using SHA-256)
                            String encryptedPass = hashPassword(pass);

                            // 3. Save the new user details to SQLite Database
                            boolean insert = dbHelper.registerUser(user, email, encryptedPass, phone);
                            if (insert) {
                                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                // Navigate back to Login Activity upon successful registration
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Navigate back to Login Activity when the user clicks the login link
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Since LoginActivity started this Activity, calling finish() will close it
                // and return the user to the underlying Login screen without creating a new instance
                finish();
            }
        });
    }

    /**
     * SHA-256 Hashing Method (Security Feature)
     * Encrypts the plain text password before storing it in the database.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}