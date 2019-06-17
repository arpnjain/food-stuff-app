package com.foodstuff;

public class FoodDataGet {
    String food_name;
    String food_kcal;
    String food_carbs;
    String food_fat;
    String food_protein;


    public FoodDataGet() {
    }

    public String getFood_name() {
        return food_name;
    }

    public String getFood_kcal() {
        return food_kcal;
    }

    public String getFood_carbs() {
        return food_carbs;
    }

    public String getFood_fat() {
        return food_fat;
    }

    public String getFood_protein() {
        return food_protein;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public void setFood_carbs(String food_carbs) {
        this.food_carbs = food_carbs;
    }

    public void setFood_fat(String food_fat) {
        this.food_fat = food_fat;
    }

    public void setFood_kcal(String food_kcal) {
        this.food_kcal = food_kcal;
    }

    public void setFood_protein(String food_protein) {
        this.food_protein = food_protein;
    }
}
