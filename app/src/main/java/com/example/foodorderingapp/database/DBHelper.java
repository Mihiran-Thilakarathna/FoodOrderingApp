package com.example.foodorderingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "FoodApp.db";

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_FOOD = "food_items";
    public static final String TABLE_ORDERS = "orders";

    // User Table Columns
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password"; // Must be encrypted

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        // 1. Create Users Table
        MyDB.execSQL("create Table " + TABLE_USERS + "(" +
                "username TEXT primary key, " +
                "password TEXT, " +
                "email TEXT, " +
                "phone TEXT)");

        // 2. Create Food Items Table (For Member 02)
        MyDB.execSQL("create Table " + TABLE_FOOD + "(" +
                "id INTEGER primary key autoincrement, " +
                "name TEXT, " +
                "description TEXT, " +
                "price DOUBLE, " +
                "image_resource INTEGER)"); // We can store drawable ID for simplicity

        // 3. Create Orders Table (For Member 03) - WITH FOREIGN KEY
        // Linking orders to a specific user (username)
        MyDB.execSQL("create Table " + TABLE_ORDERS + "(" +
                "order_id INTEGER primary key autoincrement, " +
                "username TEXT, " +
                "food_id INTEGER, " +
                "quantity INTEGER, " +
                "total_price DOUBLE, " +
                "status TEXT, " +
                "FOREIGN KEY(username) REFERENCES " + TABLE_USERS + "(username), " +
                "FOREIGN KEY(food_id) REFERENCES " + TABLE_FOOD + "(id))");

        // Optional: Pre-insert some food items (Seeding)
        seedFoodItems(MyDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists " + TABLE_USERS);
        MyDB.execSQL("drop Table if exists " + TABLE_FOOD);
        MyDB.execSQL("drop Table if exists " + TABLE_ORDERS);
        onCreate(MyDB);
    }

    // Method to insert initial food data
    private void seedFoodItems(SQLiteDatabase db) {
        // Member 02 will customize this later, but let's add one example
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chicken Burger', 'Spicy chicken with cheese', 450.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Veg Pizza', 'Large cheese pizza with olives', 1200.00)");
    }
}