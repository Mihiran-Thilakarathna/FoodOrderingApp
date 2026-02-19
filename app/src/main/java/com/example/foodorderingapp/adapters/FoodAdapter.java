package com.example.foodorderingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.activities.FoodDetailActivity;
import com.example.foodorderingapp.models.FoodModel;
import java.util.List;

// Adapter class to bind FoodModel data to the RecyclerView in the Home Page
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<FoodModel> foodList;

    // Constructor to pass application context and the data list
    public FoodAdapter(Context context, List<FoodModel> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the single item UI layout (item_food.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // Get the current food item based on its position in the list
        FoodModel food = foodList.get(position);

        // Set data to the respective TextViews
        holder.tvName.setText(food.getName());
        holder.tvPrice.setText("Rs. " + food.getPrice());

        // Handle click event on the entire list item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to FoodDetailActivity and pass the selected food's details via Intent
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("FOOD_ID", food.getId());
                intent.putExtra("FOOD_NAME", food.getName());
                intent.putExtra("FOOD_DESC", food.getDescription());
                intent.putExtra("FOOD_PRICE", food.getPrice());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list to be displayed
        return foodList.size();
    }

    // ViewHolder class to hold and reuse UI references for each item
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components from item_food.xml
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
        }
    }
}