package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge; // NEW
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // NEW
import androidx.core.view.ViewCompat; // NEW
import androidx.core.view.WindowInsetsCompat; // NEW
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.MainActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.adapters.OrderAdapter;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.models.OrderModel;
import com.example.foodorderingapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends AppCompatActivity {

    RecyclerView rvOrders;
    LinearLayout layoutEmptyOrders;
    OrderAdapter orderAdapter;
    List<OrderModel> orderList;

    DBHelper dbHelper;
    SessionManager sessionManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Enable Edge-To-Edge ---
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myorder);

        // --- Hide the default purple Action Bar ---
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        rvOrders = findViewById(R.id.rvOrders);
        layoutEmptyOrders = findViewById(R.id.layoutEmptyOrders);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(orderAdapter);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        String username = sessionManager.getUsername();

        loadAllOrders(username);

        // --- BOTTOM NAVIGATION BAR SETUP ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(MyOrderActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_cart) {
                    Intent intent = new Intent(MyOrderActivity.this, CartActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_orders) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(MyOrderActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });

        // --- Window Insets Logic to remove bottom white space ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_orders_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            bottomNavigationView.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
    }

    // --- Fetching exact logic from both tables ---
    private void loadAllOrders(String username) {
        orderList.clear();

        // 1. Get Completed Orders (From Orders Table)
        Cursor orderCursor = dbHelper.getUserOrders(username);
        if (orderCursor != null && orderCursor.moveToFirst()) {
            do {
                String name = orderCursor.getString(0);
                String orderStatus = orderCursor.getString(1);
                int qty = orderCursor.getInt(2);
                double price = orderCursor.getDouble(3); // This now has delivery fee included from CartActivity
                String date = orderCursor.getString(4);

                orderList.add(new OrderModel(name, orderStatus, price, qty, date));
            } while (orderCursor.moveToNext());
            orderCursor.close();
        }

        // 2. Get Pending Orders (From Cart Table)
        Cursor cartCursor = dbHelper.getCartItems(username);
        if (cartCursor != null && cartCursor.moveToFirst()) {
            do {
                String name = cartCursor.getString(cartCursor.getColumnIndexOrThrow("food_name"));

                // --- Add delivery fee to the pending items to match the Total Payment ---
                double unitPrice = cartCursor.getDouble(cartCursor.getColumnIndexOrThrow("price"));
                int qty = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("quantity"));
                double deliveryFee = 300.00; // Constant delivery fee
                double totalPrice = (unitPrice * qty) + deliveryFee;

                orderList.add(new OrderModel(name, "Pending", totalPrice, qty, ""));
            } while (cartCursor.moveToNext());
            cartCursor.close();
        }

        // 3. Update UI based on list size
        if (orderList.isEmpty()) {
            rvOrders.setVisibility(View.GONE);
            layoutEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            rvOrders.setVisibility(View.VISIBLE);
            layoutEmptyOrders.setVisibility(View.GONE);
            orderAdapter.notifyDataSetChanged();
        }
    }
}