package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class CartActivity extends AppCompatActivity {

    TextView foodName, foodPrice, subTotal, serviceCharge, total;
    Button placeOrderBtn;
    BottomNavigationView bottomNavigationView;

    DBHelper dbHelper;
    SessionManager sessionManager;

    int foodId;
    double grandTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        foodName = findViewById(R.id.textView3);
        foodPrice = findViewById(R.id.textView4);
        subTotal = findViewById(R.id.subtotal);
        serviceCharge = findViewById(R.id.serviceCharge);
        total = findViewById(R.id.total);
        placeOrderBtn = findViewById(R.id.button);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("FOOD_NAME");
        double price = intent.getDoubleExtra("FOOD_PRICE", 0.0);
        foodId = intent.getIntExtra("FOOD_ID", -1);

        foodName.setText(name);
        foodPrice.setText("Rs. " + price);

        double sub = price;
        double service = price * 0.10;
        grandTotal = sub + service;

        subTotal.setText("Sub Total: Rs. " + sub);
        serviceCharge.setText("Service Charge: Rs. " + service);
        total.setText("Total: Rs. " + grandTotal);

        placeOrderBtn.setOnClickListener(v -> {

            String username = sessionManager.getUsername();

            boolean inserted = dbHelper.insertOrder(
                    username,
                    foodId,
                    1,
                    grandTotal,
                    "Completed"
            );

            if (inserted) {
                Intent confirmIntent = new Intent(CartActivity.this, OrderConfirmActivity.class);
                startActivity(confirmIntent);
                finish();
            } else {
                Toast.makeText(this, "Order Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // --- BOTTOM NAVIGATION BAR SETUP ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart); // Highlight Cart Icon

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    // Clear the back stack to avoid multiple main activities
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_cart) {
                    return true;
                } else if (id == R.id.nav_orders) {
                    Intent intent = new Intent(CartActivity.this, MyOrderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish(); // Optional: finish current activity so back stack doesn't grow infinitely
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}