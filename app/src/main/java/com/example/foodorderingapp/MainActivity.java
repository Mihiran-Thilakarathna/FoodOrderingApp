package com.example.foodorderingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.activities.CartActivity;
import com.example.foodorderingapp.activities.LoginActivity;
import com.example.foodorderingapp.activities.MyOrderActivity;
import com.example.foodorderingapp.activities.ProfileActivity;
import com.example.foodorderingapp.adapters.FoodAdapter;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.models.FoodModel;
import com.example.foodorderingapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Component Declarations
    SessionManager sessionManager;
    RecyclerView recyclerView;
    DBHelper dbHelper;
    List<FoodModel> foodList;
    FoodAdapter adapter;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Link the activity to the modernized home layout
        setContentView(R.layout.activity_home);

        // --- SESSION VERIFICATION ---
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            // Redirect to LoginActivity if the user is not authenticated
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent returning to this screen via back button
            return;
        }

        // --- TOP RIGHT PROFILE ICON CLICK LISTENER ---
        ImageView imgTopProfile = findViewById(R.id.imgTopProfile);
        imgTopProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // --- RECYCLER VIEW SETUP ---
        // Initialize RecyclerView to display the list of food items
        recyclerView = findViewById(R.id.recycler_view_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Database Helper and the list to hold food data
        dbHelper = new DBHelper(this);
        foodList = new ArrayList<>();

        // Fetch data from SQLite and populate the list
        loadFoodData();

        // Attach the custom adapter to the RecyclerView
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        // --- BOTTOM NAVIGATION BAR SETUP ---
        // Initialize the bottom navigation menu
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the Home item as explicitly selected when activity loads
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Handle clicks on different menu items (Home, Cart, Orders, Profile)
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Already on the Home screen, do nothing
                    return true;
                } else if (id == R.id.nav_cart) {
                    // Navigate to CartActivity
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0); // Remove animation for smooth tab switching
                    return true;
                } else if (id == R.id.nav_orders) {
                    Intent intent = new Intent(MainActivity.this, MyOrderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Navigate to the User Profile screen
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        // Handle system UI insets (status bar, navigation bar) to avoid overlapping
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_home_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Helper method to fetch food items from the SQLite database
     * and add them to the foodList array.
     */
    private void loadFoodData() {
        Cursor cursor = dbHelper.getAllFoodItems();

        // Ensure the cursor is at the first row before extracting data
        if (cursor.moveToFirst()) {
            do {
                // Extract values from the current row
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                // Add a new FoodModel object to the list
                foodList.add(new FoodModel(id, name, desc, price));
            } while (cursor.moveToNext()); // Move to the next row until all are read
        }

        // Close the cursor to free up memory resources
        cursor.close();
    }
}