package com.example.foodorderingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.models.OrderModel;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    List<OrderModel> orderList;

    // Constructor
    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_order.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.tvOrderFoodName.setText(order.getFoodName());
        holder.tvOrderPriceQty.setText("Rs. " + order.getPrice() + "  |  Qty: " + order.getQuantity());

        // Set image dynamically based on food name
        holder.imgOrderFood.setImageResource(getImageResource(order.getFoodName()));

        // Change Badge Color and Show Date based on Status
        holder.tvOrderStatus.setText(order.getStatus());

        if (order.getStatus().equalsIgnoreCase("Completed")) {
            // Green color for Completed
            holder.tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

            // Show date only for completed orders
            holder.tvOrderDate.setText(order.getOrderDate());
            holder.tvOrderDate.setVisibility(View.VISIBLE);

        } else {
            // Orange color for Pending
            holder.tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));

            // Hide date for pending items (since they are still in cart)
            holder.tvOrderDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgOrderFood;
        TextView tvOrderFoodName, tvOrderPriceQty, tvOrderStatus, tvOrderDate; // Added tvOrderDate

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOrderFood = itemView.findViewById(R.id.imgOrderFood);
            tvOrderFoodName = itemView.findViewById(R.id.tvOrderFoodName);
            tvOrderPriceQty = itemView.findViewById(R.id.tvOrderPriceQty);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate); // Initialize the date TextView
        }
    }

    // Helper method to get the correct image
    private int getImageResource(String foodName) {
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
            default: return R.drawable.ic_logo;
        }
    }
}