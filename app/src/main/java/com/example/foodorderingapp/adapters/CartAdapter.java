package com.example.foodorderingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.database.DBHelper;
import com.example.foodorderingapp.models.CartModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartModel> cartList;
    DBHelper dbHelper;
    CartUpdateListener listener;

    // Interface to dynamically update the total bill in CartActivity
    public interface CartUpdateListener {
        void onCartUpdated();
    }

    // Constructor
    public CartAdapter(Context context, List<CartModel> cartList, DBHelper dbHelper, CartUpdateListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Connect the item_cart.xml design
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel cartItem = cartList.get(position);

        holder.tvCartFoodName.setText(cartItem.getFoodName());

        // FIXED: Changed from $ to Rs.
        holder.tvCartFoodPrice.setText(String.format("Rs. %.2f", cartItem.getPrice()));

        // Format quantity to show two digits like "01", "02"
        holder.tvCartQuantity.setText(String.format("%02d", cartItem.getQuantity()));

        // Set dynamic image
        holder.imgCartFood.setImageResource(getImageResource(cartItem.getFoodName()));

        // --- Increase Quantity Logic ---
        holder.btnCartPlus.setOnClickListener(v -> {
            int currentQty = cartItem.getQuantity();
            currentQty++;
            cartItem.setQuantity(currentQty);

            // Update Database
            dbHelper.updateCartQuantity(cartItem.getCartId(), currentQty);

            // Update UI & Total Bill
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        // --- Decrease Quantity / Remove Logic ---
        holder.btnCartMinus.setOnClickListener(v -> {
            int currentQty = cartItem.getQuantity();
            if (currentQty > 1) {
                currentQty--;
                cartItem.setQuantity(currentQty);

                // Update Database
                dbHelper.updateCartQuantity(cartItem.getCartId(), currentQty);

                // Update UI & Total Bill
                notifyItemChanged(position);
                listener.onCartUpdated();
            } else {
                // If quantity is 1 and user clicks minus, remove the item entirely
                dbHelper.removeFromCart(cartItem.getCartId());
                cartList.remove(position);

                // Refresh list
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());
                Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();

                // Update Total Bill & check if cart is empty
                listener.onCartUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCartFood;
        TextView tvCartFoodName, tvCartFoodPrice, tvCartQuantity;
        TextView btnCartMinus, btnCartPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCartFood = itemView.findViewById(R.id.imgCartFood);
            tvCartFoodName = itemView.findViewById(R.id.tvCartFoodName);
            tvCartFoodPrice = itemView.findViewById(R.id.tvCartFoodPrice);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnCartMinus = itemView.findViewById(R.id.btnCartMinus);
            btnCartPlus = itemView.findViewById(R.id.btnCartPlus);
        }
    }

    // Helper method to dynamically map food names to their images
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
            default: return R.drawable.ic_logo; // Fallback image
        }
    }
}