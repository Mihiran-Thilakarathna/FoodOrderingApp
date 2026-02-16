package com.example.foodorderingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

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

    // --- NEW METHODS FOR USER REGISTRATION ---

    // Insert User Logic
    public boolean registerUser(String username, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password); // This should be the encrypted password
        values.put("email", email);
        values.put("phone", phone);

        long result = db.insert(TABLE_USERS, null, values);

        // If result is -1, insertion failed
        return result != -1;
    }

    // Check if user already exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_USERS + " where username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check Username and Password (Login Logic)
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Querying with the password
        Cursor cursor = db.rawQuery("Select * from " + TABLE_USERS + " where username = ? and password = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}