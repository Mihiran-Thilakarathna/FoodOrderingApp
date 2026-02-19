package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.R;

// Activity to show detailed information of a selected food item
public class FoodDetailActivity extends AppCompatActivity {

    // Declare UI components
    TextView tvName, tvPrice, tvDesc;
    Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the corresponding XML layout
        setContentView(R.layout.activity_food_detail);

        // Initialize UI components by finding their IDs
        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Retrieve the data passed from the FoodAdapter via Intent Extras
        Intent intent = getIntent();
        String name = intent.getStringExtra("FOOD_NAME");
        String desc = intent.getStringExtra("FOOD_DESC");
        double price = intent.getDoubleExtra("FOOD_PRICE", 0.0);

        // Set the retrieved data to the respective TextViews to display to the user
        tvName.setText(name);
        tvDesc.setText(desc);
        tvPrice.setText("Rs. " + price);

        // Setup click listener for the 'Add to Cart' button (Future Task for Member 03)
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Currently displaying a Toast message. Actual cart logic will be implemented here later.
                Toast.makeText(FoodDetailActivity.this, name + " added to cart!", Toast.LENGTH_SHORT).show();

                // Close the current detail activity and return to the main menu
                finish();
            }
        });
    }
}