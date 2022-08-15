package ru.yandex.practicum.stellaburgers.api.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<String> ingredients = new ArrayList<String>();

    public List<String> getIngredients() {
        return ingredients;
    }

    public void addIngredient(String ingredient) { ingredients.add(ingredient); }

    public void clearIngredients() {ingredients.clear(); }
}
