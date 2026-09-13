package com.webdev2;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecipeRepository {
    private final List<Recipe> recipes = new ArrayList<>();
    private Long nextId = 1L;

    public RecipeRepository() {
        recipes.add(new Recipe(nextId++, "Pasta Carbonara", 30, 4, "Classic Italian pasta with eggs and bacon."));
        recipes.add(new Recipe(nextId++, "Chocolate Cake", 60, 8, "Rich and moist chocolate cake."));
    }

    public List<Recipe> findAll() {
        return recipes;
    }

    public Recipe findById(Long id) {
        for (Recipe r : recipes) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public Recipe save(Recipe recipe) {
        if (recipe.getId() == null) {
            recipe.setId(nextId++);
            recipes.add(recipe);
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                if (recipes.get(i).getId().equals(recipe.getId())) {
                    recipes.set(i, recipe);
                    break;
                }
            }
        }
        return recipe;
    }

    public void deleteById(Long id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId().equals(id)) {
                recipes.remove(i);
                break;
            }
        }
    }
}