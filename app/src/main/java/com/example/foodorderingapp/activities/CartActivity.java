package com.example.foodorderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.utils.SessionManager;

public class CartActivity extends AppCompatActivity {

    TextView foodName, foodPrice, subTotal, serviceCharge, total;
    Button placeOrderBtn;

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
    }
}