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

        // Create Food Items Table
        MyDB.execSQL("create Table " + TABLE_FOOD + "(" +
                "id INTEGER primary key autoincrement, " +
                "name TEXT, " +
                "description TEXT, " +
                "price DOUBLE, " +
                "image_resource INTEGER)"); // We can store drawable ID for simplicity

        // Create Orders Table WITH FOREIGN KEY
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

        // Pre-insert 20 Sri Lankan & Popular food items (Seeding)
        seedFoodItems(MyDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists " + TABLE_USERS);
        MyDB.execSQL("drop Table if exists " + TABLE_FOOD);
        MyDB.execSQL("drop Table if exists " + TABLE_ORDERS);
        onCreate(MyDB);
    }

    // Method to insert initial 20 food data items
    private void seedFoodItems(SQLiteDatabase db) {
        // Sri Lankan and Popular Items with prices in Rs.
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chicken Kottu', 'Spicy Sri Lankan street food with chicken', 850.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Cheese Kottu', 'Creamy cheese kottu with roasted chicken', 1200.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chicken Fried Rice', 'Basmati rice wok-tossed with chicken and veggies', 900.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Nasi Goreng', 'Indonesian style spicy rice topped with a fried egg', 1100.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chicken Biryani', 'Aromatic spiced rice served with chicken and egg', 1350.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('String Hoppers (10 pcs)', 'Served with dhal, coconut sambol and chicken curry', 450.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Plain Hoppers (5 pcs)', 'Crispy plain hoppers served with lunu miris', 250.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Egg Hopper', 'Crispy hopper with a soft baked egg inside', 100.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Lamprais', 'Traditional Dutch burgher meal wrapped in banana leaf', 1500.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Roast Paan & Curry', 'Wood-fired roast bread with spicy pork or chicken curry', 650.00)");

        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chicken Burger', 'Crispy chicken patty with fresh lettuce and mayo', 650.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Double Cheese Burger', 'Double beef patty with melted cheese and pickles', 1250.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Spicy Chicken Sub', 'Footlong submarine sandwich with spicy chicken fillings', 950.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('BBQ Chicken Pizza', 'Large pizza topped with BBQ chicken, onions and cheese', 2200.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Margherita Pizza', 'Classic medium pizza with fresh tomatoes and mozzarella', 1500.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Fish Roll', 'Crispy crumbed roll stuffed with spicy fish and potato', 120.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Vegetable Roti', 'Spicy vegetable mix folded into a triangular flatbread', 80.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chicken Samosa', 'Crispy pastry filled with minced chicken and peas', 100.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Isso Vade', 'Crispy lentil patty topped with spicy fried prawns', 150.00)");
        db.execSQL("INSERT INTO " + TABLE_FOOD + " (name, description, price) VALUES ('Chocolate Biscuit Pudding', 'Classic Sri Lankan dessert layered with chocolate', 400.00)");
    }

    // Insert User Logic
    public boolean registerUser(String username, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put("email", email);
        values.put("phone", phone);

        long result = db.insert(TABLE_USERS, null, values);
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
        Cursor cursor = db.rawQuery("Select * from " + TABLE_USERS + " where username = ? and password = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get User Details by Username
    public android.database.Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_USERS + " where username = ?", new String[]{username});
        return cursor;
    }

    // Method to retrieve all food items from the 'food_items' table
    public Cursor getAllFoodItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FOOD, null);
    }

    // Insert Order
    public boolean insertOrder(String username, int foodId, int quantity, double totalPrice, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("food_id", foodId);
        values.put("quantity", quantity);
        values.put("total_price", totalPrice);
        values.put("status", status);

        long result = db.insert(TABLE_ORDERS, null, values);
        return result != -1;
    }

    // Fetch User Orders with Food Name (Fixed Null Error)
    public Cursor getUserOrders(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT f.name, o.status FROM " + TABLE_ORDERS + " o " +
                "INNER JOIN " + TABLE_FOOD + " f ON o.food_id = f.id " +
                "WHERE o.username = ?";
        return db.rawQuery(query, new String[]{username});
    }
}