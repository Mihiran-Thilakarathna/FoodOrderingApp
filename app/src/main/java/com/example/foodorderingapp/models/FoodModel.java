package com.example.foodorderingapp.models;

public class FoodModel {
    private int id;
    private String name;
    private String description;  // Short description of the food
    private double price;

    // Constructor to initialize the FoodModel object
    public FoodModel(int id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getter methods to access the private variables outside this class
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}