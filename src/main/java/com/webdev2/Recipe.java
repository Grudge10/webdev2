package com.webdev2;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Recipe {
    private Long id;

    @NotBlank(message = "Recipe name is required.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @NotNull(message = "Prep time is required.")
    @Min(value = 1, message = "Prep time must be at least 1 minute.")
    private Integer prepTimeMinutes;

    @NotNull(message = "Servings is required.")
    @Min(value = 1, message = "Servings must be at least 1.")
    @Max(value = 100, message = "Servings cannot be more than 100.")
    private Integer servings;

    @NotBlank(message = "Description is required.")
    private String description;

    public Recipe() {
    }

    public Recipe(Long id, String name, Integer prepTimeMinutes, Integer servings, String description) {
        this.id = id;
        this.name = name;
        this.prepTimeMinutes = prepTimeMinutes;
        this.servings = servings;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}