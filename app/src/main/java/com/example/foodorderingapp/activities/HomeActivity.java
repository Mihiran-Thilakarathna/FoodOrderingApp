package com.example.foodorderingapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.adapters.FoodAdapter;
import com.example.foodorderingapp.database.DatabaseHelper;
import com.example.foodorderingapp.models.FoodModel;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        recyclerView = findViewById(R.id.recycler_view_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        
        // Fetch all food items from the database
        List<FoodModel> foodList = dbHelper.getAllFood();

        // Pass the list to the adapter
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);
    }
}
