package com.example.foodorderingapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.utils.SessionManager;

public class MyOrderActivity extends AppCompatActivity {

    TextView foodName, status;
    DBHelper dbHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);

        foodName = findViewById(R.id.textView9);
        status = findViewById(R.id.textView10);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        String username = sessionManager.getUsername();

        Cursor cursor = dbHelper.getUserOrders(username);

        // Added null check
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String orderStatus = cursor.getString(1);

            foodName.setText(name);
            status.setText(orderStatus);
        }

        // Safely close the cursor
        if (cursor != null) {
            cursor.close();
        }
    }
}