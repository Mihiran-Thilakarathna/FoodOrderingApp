package com.example.foodorderingapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.R; // R file import එක වැදගත්
import com.example.foodorderingapp.database.DBHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPhone, etPassword, etConfirmPass;
    Button btnRegister;
    TextView tvLoginLink;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        // UI Initialization
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String pass = etPassword.getText().toString();
                String confirmPass = etConfirmPass.getText().toString();

                // 1. Input Validation
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(confirmPass)) {
                        // Check if user already exists
                        if (!dbHelper.checkUsername(user)) {

                            // 2. Encrypt Password (MANDATORY Guideline)
                            String encryptedPass = hashPassword(pass);

                            // 3. Save to SQLite
                            boolean insert = dbHelper.registerUser(user, email, encryptedPass, phone);
                            if (insert) {
                                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to Login Activity (Will create later)
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
    }

    // SHA-256 Hashing Method (Security Feature)
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