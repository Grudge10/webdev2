package com.webdev2;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public String listRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        return "recipes";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "recipe-form";
    }

    @PostMapping
    public String createRecipe(@Valid @ModelAttribute Recipe recipe, BindingResult result) {
        if (result.hasErrors()) {
            return "recipe-form";
        }
        recipeService.saveRecipe(recipe);
        return "redirect:/recipes";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return "redirect:/recipes";
        }
        model.addAttribute("recipe", recipe);
        return "recipe-form";
    }

    @PostMapping("/{id}")
    public String updateRecipe(@PathVariable Long id, @Valid @ModelAttribute Recipe recipe, BindingResult result) {
        if (result.hasErrors()) {
            return "recipe-form";
        }
        recipe.setId(id);
        recipeService.saveRecipe(recipe);
        return "redirect:/recipes";
    }

    @PostMapping("/{id}/delete")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/recipes";
    }
}