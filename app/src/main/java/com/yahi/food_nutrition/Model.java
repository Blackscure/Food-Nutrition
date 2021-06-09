package com.yahi.food_nutrition;

public class Model {
    private String meal,brand, id, date,calories;

    public Model(){
    }

    public Model(String meal, String brand, String id, String date, String calories) {
        this.meal = meal;
        this.brand = brand;
        this.id = id;
        this.date = date;
        this.calories = calories;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
