package com.example.foodorderingapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog; // NEW: Imported for Notification Popup Dialog
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.activities.CartActivity;
import com.example.foodorderingapp.activities.FoodDetailActivity; // NEW: Imported to navigate to Detail page
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
    List<FoodModel> originalFoodList; // Master copy of all foods for filtering
    FoodAdapter adapter;
    BottomNavigationView bottomNavigationView;

    // UI Components for Search & Filtering
    EditText etSearchFood;
    TextView tvNoFoodFound;
    TextView tvCatAll, tvCatBurger, tvCatPizza, tvCatRice, tvSeeAll;

    // Flag to prevent searching conflicts when clicking categories
    boolean isCategoryFiltering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // --- SESSION VERIFICATION ---
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // --- SET DYNAMIC USERNAME IN HEADER ---
        TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        String currentUsername = sessionManager.getUsername();
        if(currentUsername != null) {
            tvWelcomeName.setText(currentUsername);
        }

        // --- INIT TOP BUTTONS & SEARCH BAR ---
        ImageView imgTopProfile = findViewById(R.id.imgTopProfile);
        imgTopProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // --- UPDATED: Notification Button to show a popup with Promo Codes ---
        ImageView imgNotification = findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Offers & Promotions 🎉")
                    .setMessage("1. 50% OFF on Chicken Burger!\nUse Promo Code: BURGER50\n\n" +
                            "2. Free Delivery on orders over Rs. 2000.\nUse Promo Code: FREEDEL\n\n" +
                            "3. 10% OFF on your first order.\nUse Promo Code: WELCOME10")
                    .setPositiveButton("Got it!", null)
                    .show();
        });

        // --- UPDATED: Shop Now Button navigates directly to FoodDetailActivity with 50% discount ---
        Button btnShopNow = findViewById(R.id.btnShopNow);
        btnShopNow.setOnClickListener(v -> {
            FoodModel promoFood = null;

            // Find the Chicken Burger in the original list
            for (FoodModel food : originalFoodList) {
                if (food.getName().equals("Chicken Burger")) {
                    promoFood = food;
                    break;
                }
            }

            if (promoFood != null) {
                // Navigate directly to FoodDetailActivity and pass the discounted details
                Intent intent = new Intent(MainActivity.this, FoodDetailActivity.class);
                intent.putExtra("FOOD_ID", promoFood.getId());
                intent.putExtra("FOOD_NAME", promoFood.getName() + " (50% OFF)");
                intent.putExtra("FOOD_DESC", promoFood.getDescription() + "\n\n🔥 Hot Deal Promo Applied!");
                intent.putExtra("FOOD_PRICE", promoFood.getPrice() / 2.0); // 50% Discount applied here
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Promo item not found!", Toast.LENGTH_SHORT).show();
            }
        });

        etSearchFood = findViewById(R.id.etSearchFood);
        tvNoFoodFound = findViewById(R.id.tvNoFoodFound);

        // --- RECYCLER VIEW SETUP ---
        recyclerView = findViewById(R.id.recycler_view_food);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        dbHelper = new DBHelper(this);
        foodList = new ArrayList<>();
        originalFoodList = new ArrayList<>();

        loadFoodData();

        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        // --- SETUP CATEGORIES & SEARCH ---
        initializeCategoryViews();
        setupCategoryClickListeners();
        setupSearchListener(); // NEW: Setup the real-time search functionality

        // --- BOTTOM NAVIGATION BAR SETUP ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_cart) {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_orders) {
                    Intent intent = new Intent(MainActivity.this, MyOrderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_home_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFoodData() {
        Cursor cursor = dbHelper.getAllFoodItems();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                FoodModel food = new FoodModel(id, name, desc, price);
                foodList.add(food);
                originalFoodList.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    // --- NEW METHOD: Real-time Search Listener ---
    private void setupSearchListener() {
        etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ignore if the text change was caused by clicking a category button
                if (!isCategoryFiltering) {
                    filterSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    // --- NEW METHOD: Filter the list based on Search Bar input ---
    private void filterSearch(String query) {
        // Visually reset category selection to "All" when user starts typing
        updateCategoryStyles(tvCatAll);

        List<FoodModel> filteredList = new ArrayList<>();
        for (FoodModel food : originalFoodList) {
            // Check if food name or description contains the search text
            if (food.getName().toLowerCase().contains(query.toLowerCase()) ||
                    food.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(food);
            }
        }

        // Send updated list to the adapter
        adapter.updateList(filteredList);

        // Show/Hide the "Not Found" message depending on results
        if (filteredList.isEmpty()) {
            tvNoFoodFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoFoodFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initializeCategoryViews() {
        tvCatAll = findViewById(R.id.tvCatAll);
        tvCatBurger = findViewById(R.id.tvCatBurger);
        tvCatPizza = findViewById(R.id.tvCatPizza);
        tvCatRice = findViewById(R.id.tvCatRice);
        tvSeeAll = findViewById(R.id.tvSeeAll);
    }

    private void setupCategoryClickListeners() {
        tvCatAll.setOnClickListener(v -> filterCategory("All", tvCatAll));
        tvSeeAll.setOnClickListener(v -> filterCategory("All", tvCatAll));

        tvCatBurger.setOnClickListener(v -> filterCategory("Burger", tvCatBurger));
        tvCatPizza.setOnClickListener(v -> filterCategory("Pizza", tvCatPizza));
        tvCatRice.setOnClickListener(v -> filterCategory("Rice", tvCatRice));
    }

    private void filterCategory(String categoryName, TextView selectedView) {
        updateCategoryStyles(selectedView);

        // Temporarily flag that we are filtering by category so the search listener doesn't trigger twice
        isCategoryFiltering = true;
        etSearchFood.setText(""); // Clear the search bar
        etSearchFood.clearFocus();
        isCategoryFiltering = false;

        List<FoodModel> filteredList = new ArrayList<>();

        if (categoryName.equals("All")) {
            filteredList.addAll(originalFoodList);
        } else {
            for (FoodModel food : originalFoodList) {
                String name = food.getName().toLowerCase();
                if (categoryName.equals("Burger") && (name.contains("burger") || name.contains("sub"))) {
                    filteredList.add(food);
                } else if (categoryName.equals("Pizza") && name.contains("pizza")) {
                    filteredList.add(food);
                } else if (categoryName.equals("Rice") && (name.contains("rice") || name.contains("goreng") || name.contains("biryani"))) {
                    filteredList.add(food);
                }
            }
        }

        adapter.updateList(filteredList);

        // Handle empty states for category filters just in case
        if (filteredList.isEmpty()) {
            tvNoFoodFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoFoodFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateCategoryStyles(TextView selectedView) {
        TextView[] allCategories = {tvCatAll, tvCatBurger, tvCatPizza, tvCatRice};
        for (TextView tv : allCategories) {
            tv.setBackgroundTintList(null);
            tv.setTextColor(Color.parseColor("#000000"));
        }

        selectedView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
        selectedView.setTextColor(Color.parseColor("#FFFFFF"));
    }
}