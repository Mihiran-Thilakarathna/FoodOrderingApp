package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin;
    TextView tvRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        etUser = findViewById(R.id.etLoginUsername);
        etPass = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegisterLink);

        // Login Button Click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etUser.getText().toString();
                String pass = etPass.getText().toString();

                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    // 1. Encrypt the entered password to match DB
                    String encryptedPass = hashPassword(pass);

                    // 2. Check DB
                    boolean check = dbHelper.checkUser(user, encryptedPass);
                    if (check) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to Main Activity (Home)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close login activity
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Register Link Click
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // SHA-256 Hashing (Same as Register Activity)
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