package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.graphics.Color; // NEW: Imported for heart color change
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge; // NEW
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // NEW
import androidx.core.view.ViewCompat; // NEW
import androidx.core.view.WindowInsetsCompat; // NEW

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper; // NEW: Imported for Cart Database
import com.example.foodorderingapp.utils.SessionManager; // NEW: Imported for User Session

// Activity to show detailed information of a selected food item
public class FoodDetailActivity extends AppCompatActivity {

    // Declare UI components
    TextView tvName, tvPrice, tvDesc, tvQuantity;
    TextView btnMinus, btnPlus; // btnBack removed to prevent crashes
    TextView btnHeart; // NEW: Heart icon textview
    ImageView imgDetailFood;
    Button btnAddToCart;

    int quantity = 1;
    double basePrice = 0.0; // Store base price to calculate total
    boolean isFavorite = false; // NEW: Flag to keep track of favorite status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- FIXED: Enable Edge-To-Edge ---
        EdgeToEdge.enable(this);
        // Set the corresponding XML layout
        setContentView(R.layout.activity_food_detail);

        // --- FIXED: Hide the default purple Action Bar ---
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI components by finding their IDs
        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        tvQuantity = findViewById(R.id.tvQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        imgDetailFood = findViewById(R.id.imgDetailFood);
        btnHeart = findViewById(R.id.btnHeart); // Initialize heart icon

        // btnBack initialization removed

        // Retrieve the data passed from the FoodAdapter or MainActivity via Intent Extras
        Intent intent = getIntent();
        String name = intent.getStringExtra("FOOD_NAME");
        String descFromIntent = intent.getStringExtra("FOOD_DESC"); // Retrieve the short desc or promo text
        basePrice = intent.getDoubleExtra("FOOD_PRICE", 0.0);
        // int foodId = intent.getIntExtra("FOOD_ID", -1); // Unnecessary for Database Cart, but kept for reference

        // Set the retrieved name to the TextView
        tvName.setText(name);

        // FIX: Set the Top Base Price ONLY ONCE here. It will NOT change when quantity changes.
        tvPrice.setText(String.format("Rs. %.2f", basePrice));

        // Load the correct dynamic image based on food name
        if(name != null) {
            imgDetailFood.setImageResource(getImageResource(name));
        }

        // --- NEW: Dynamic Three-Line Description Logic ---
        // If it's a "Promo Applied" description, append it to a default 3-line text.
        // Otherwise, create a clean 3-line description from scratch.
        String finalDescription = "";

        // Base 3-line description for all items
        String baseDescription = "A perfect blend of fresh, high-quality ingredients.\n" +
                "Prepared with love for a unique and rich taste experience.\n" +
                "Perfect for any meal, this dish guarantees satisfaction.";

        if (descFromIntent != null && descFromIntent.contains("Hot Deal Promo Applied!")) {
            // It's a promo item, so combine the base text with the promo text
            // This ensures it has enough lines to fill white space
            finalDescription = baseDescription + "\n\n" + descFromIntent;
        } else {
            // It's a normal item, so just use the base 3-line text
            finalDescription = baseDescription;
        }

        // Set the final description to the TextView
        tvDesc.setText(finalDescription);

        // Format the initial price for the Add to Cart button
        updateButtonPriceDisplay();

        // btnBack onClickListener removed

        // --- NEW: Heart Icon Toggle Logic ---
        btnHeart.setOnClickListener(v -> {
            isFavorite = !isFavorite; // Toggle the favorite status

            if(isFavorite) {
                btnHeart.setText("❤"); // Set to filled red heart
                btnHeart.setTextColor(Color.parseColor("#FF0000")); // Red color
                Toast.makeText(FoodDetailActivity.this, "Added to favorites!", Toast.LENGTH_SHORT).show();
            } else {
                btnHeart.setText("♡"); // Set back to outlined black heart
                btnHeart.setTextColor(Color.parseColor("#000000")); // Black color
                Toast.makeText(FoodDetailActivity.this, "Removed from favorites.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Quantity Increase/Decrease Logic ---
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateButtonPriceDisplay(); // Update ONLY the button price dynamically
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) { // Prevent quantity from going below 1
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateButtonPriceDisplay(); // Update ONLY the button price dynamically
            }
        });

        // --- UPDATED: Save to SQLite Cart Table instead of passing via Intent ---
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize Database and Session Manager
                SessionManager sessionManager = new SessionManager(FoodDetailActivity.this);
                DBHelper dbHelper = new DBHelper(FoodDetailActivity.this);

                String username = sessionManager.getUsername();

                if(username != null) {
                    // Save item directly to the Database Cart table
                    boolean isAdded = dbHelper.addToCart(username, name, basePrice, quantity);

                    if(isAdded) {
                        Toast.makeText(FoodDetailActivity.this, quantity + "x " + name + " added to cart!", Toast.LENGTH_SHORT).show();

                        // Navigate to CartActivity. No Intent Extras needed anymore since it loads from DB!
                        Intent cartIntent = new Intent(FoodDetailActivity.this, CartActivity.class);
                        startActivity(cartIntent);

                        // Close the current detail activity
                        finish();
                    } else {
                        Toast.makeText(FoodDetailActivity.this, "Failed to add to cart.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- FIXED: Window Insets Logic ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_food_detail_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to Top, Left, Right for root
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

            // Apply Bottom padding directly to the bottom cart layout so its background extends down
            View bottomLayout = findViewById(R.id.bottom_cart_layout);
            int originalPaddingDp = (int) (15 * getResources().getDisplayMetrics().density); // convert 15dp to px
            bottomLayout.setPadding(originalPaddingDp, originalPaddingDp, originalPaddingDp, originalPaddingDp + systemBars.bottom);

            return insets;
        });
    }

    // --- FIXED METHOD: Only updates the button text, NOT the top static price ---
    private void updateButtonPriceDisplay() {
        double total = basePrice * quantity;
        btnAddToCart.setText(String.format("Add to Cart - Rs. %.2f", total));
    }

    // --- Helper method to dynamically map food names to their images ---
    private int getImageResource(String foodName) {
        // Clean the name in case it has the "(50% OFF)" tag from the Promo
        String cleanName = foodName.replace(" (50% OFF)", "");

        switch (cleanName) {
            case "Chicken Kottu": return R.drawable.food_chicken_kottu;
            case "Cheese Kottu": return R.drawable.food_cheese_kottu;
            case "Chicken Fried Rice": return R.drawable.food_chicken_fried_rice;
            case "Nasi Goreng": return R.drawable.food_nasi_goreng;
            case "Chicken Biryani": return R.drawable.food_chicken_biryani;
            case "String Hoppers (10 pcs)": return R.drawable.food_string_hoppers;
            case "Plain Hoppers (5 pcs)": return R.drawable.food_plain_hoppers;
            case "Egg Hopper": return R.drawable.food_egg_hopper;
            case "Lamprais": return R.drawable.food_lamprais;
            case "Roast Paan & Curry": return R.drawable.food_roast_paan;
            case "Chicken Burger": return R.drawable.food_chicken_burger;
            case "Double Cheese Burger": return R.drawable.food_double_cheese_burger;
            case "Spicy Chicken Sub": return R.drawable.food_spicy_chicken_sub;
            case "BBQ Chicken Pizza": return R.drawable.food_bbq_chicken_pizza;
            case "Margherita Pizza": return R.drawable.food_margherita_pizza;
            case "Fish Roll": return R.drawable.food_fish_roll;
            case "Vegetable Roti": return R.drawable.food_vegetable_roti;
            case "Chicken Samosa": return R.drawable.food_chicken_samosa;
            case "Isso Vade": return R.drawable.food_isso_vade;
            case "Chocolate Biscuit Pudding": return R.drawable.food_chocolate_pudding;
            default: return R.drawable.ic_logo; // Fallback image
        }
    }
}