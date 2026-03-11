package com.example.foodorderingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.activities.FoodDetailActivity;
import com.example.foodorderingapp.models.FoodModel;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<FoodModel> foodList;

    // ViewHolder class - Moved to the top to avoid any resolution issues
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
            imgFood = itemView.findViewById(R.id.img_food);
        }
    }

    // Constructor
    public FoodAdapter(Context context, List<FoodModel> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    // --- Dynamically update the list when a category is filtered ---
    public void updateList(List<FoodModel> filteredList) {
        this.foodList = filteredList;
        notifyDataSetChanged(); // Refresh the RecyclerView with new data
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodModel food = foodList.get(position);

        holder.tvName.setText(food.getName());
        holder.tvPrice.setText(String.format("Rs. %.2f", food.getPrice()));

        // Set image dynamically
        holder.imgFood.setImageResource(getImageResource(food.getName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        return foodList.size();
    }

    // Helper method to map food names to drawable images
    private int getImageResource(String foodName) {
        switch (foodName) {
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
            default: return R.drawable.ic_logo;
        }
    }
}