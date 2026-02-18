package com.example.foodorderingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.foodorderingapp.models.FoodModel;

import java.util.ArrayList;
import java.util.List;


/**
 * DatabaseHelper for Menu Management.
 * This class handles only the FoodItem table as per the project requirements.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FoodOrder.db";
    private static final int DATABASE_VERSION = 1;

    // FoodItem table name and columns
    public static final String TABLE_FOOD = "FoodItem";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image";

    // CREATE TABLE SQL
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_FOOD + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_IMAGE + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(TABLE_CREATE);
        // Insert initial data
        insertInitialFoodData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }

    /**
     * Method to insert initial food data: Pizza, Burger, Rice.
     */
    private void insertInitialFoodData(SQLiteDatabase db) {
        insertFoodItem(db, "Pizza", "Delicious cheese pizza with fresh toppings", 12.99, "pizza_image");
        insertFoodItem(db, "Burger", "Juicy beef burger with cheese and lettuce", 8.50, "burger_image");
        insertFoodItem(db, "Rice", "Steamed basmati rice with mixed vegetables", 7.00, "rice_image");
    }

    // Helper method for insertion
    private void insertFoodItem(SQLiteDatabase db, String name, String description, double price, String image) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, image);
        db.insert(TABLE_FOOD, null, values);
    }

    /**
     * Method to fetch all food items from the database.
     * @return List of FoodModel objects.
     */
    public List<FoodModel> getAllFood() {
        List<FoodModel> foodList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOOD, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

                FoodModel food = new FoodModel(id, name, description, price, image);
                foodList.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return foodList;
    }
}

