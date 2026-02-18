package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.R;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood;
    private TextView tvName, tvPrice, tvDescription;
    private Button btnAddToCart;

    private int foodId;
    private String foodName, foodDescription, foodImage;
    private double foodPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Initialize UI components
        imgFood = findViewById(R.id.img_food_detail);
        tvName = findViewById(R.id.tv_food_name_detail);
        tvPrice = findViewById(R.id.tv_food_price_detail);
        tvDescription = findViewById(R.id.tv_food_description_detail);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // Receive data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            foodId = intent.getIntExtra("food_id", -1);
            foodName = intent.getStringExtra("food_name");
            foodDescription = intent.getStringExtra("food_description");
            foodPrice = intent.getDoubleExtra("food_price", 0.0);
            foodImage = intent.getStringExtra("food_image");

            // Set data to views
            tvName.setText(foodName);
            tvPrice.setText("$" + String.format("%.2f", foodPrice));
            tvDescription.setText(foodDescription);
            
            // Note: In a student project, you'd typically load the image from R.drawable 
            // using the resource name or ID. Keeping it simple here.
            imgFood.setImageResource(R.drawable.ic_launcher_background); // Placeholder
        }

        // Handle Add to Cart button
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we would prepare data for CartActivity
                // Since cart logic is handled by another member, we just show a message
                Toast.makeText(FoodDetailActivity.this, foodName + " added to selection!", Toast.LENGTH_SHORT).show();
                
                /* 
                   Example of how to pass data to Cart later:
                   Intent cartIntent = new Intent(FoodDetailActivity.this, CartActivity.class);
                   cartIntent.putExtra("item_id", foodId);
                   startActivity(cartIntent);
                */
            }
        });
    }
}
