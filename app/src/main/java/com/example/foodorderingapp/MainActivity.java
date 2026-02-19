package com.example.foodorderingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderingapp.activities.LoginActivity;
import com.example.foodorderingapp.activities.ProfileActivity;
import com.example.foodorderingapp.adapters.FoodAdapter;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.models.FoodModel;
import com.example.foodorderingapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI and Core Components Declaration
    Button btnProfile;
    SessionManager sessionManager;
    RecyclerView recyclerView;
    DBHelper dbHelper;
    List<FoodModel> foodList;
    FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set the integrated home layout containing the RecyclerView
        setContentView(R.layout.activity_home);

        // 1. Session check: Redirect to Login screen if the user is not authenticated
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent user from going back to MainActivity
            return;
        }

        // 2. Setup Profile Navigation Button
        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // 3. Setup RecyclerView for Food Menu display
        recyclerView = findViewById(R.id.recycler_view_food);
        // Use LinearLayoutManager to display items in a standard vertical list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database helper and the array list for storing food data
        dbHelper = new DBHelper(this);
        foodList = new ArrayList<>();

        // 4. Fetch data from SQLite Database
        loadFoodData();

        // 5. Initialize the custom adapter and attach it to the RecyclerView
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        // Handle system UI padding (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_home_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Custom method to fetch food items from DB and populate the ArrayList
    private void loadFoodData() {
        Cursor cursor = dbHelper.getAllFoodItems();

        // Move cursor to the first row and iterate through all rows
        if (cursor.moveToFirst()) {
            do {
                // Extract data from the current row in the cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                // Create a new FoodModel object and add it to the list
                foodList.add(new FoodModel(id, name, desc, price));
            } while (cursor.moveToNext());
        }
        // Close the cursor to prevent memory leaks
        cursor.close();
    }
}