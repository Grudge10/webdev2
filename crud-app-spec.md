# Recipe Manager — CRUD Application Specification

## Project Description

Implementation of a basic online recipe management system. The system is built with the following core features: creation, viewing, editing, and deletion of recipes via server-side rendered web pages. The application uses Spring MVC and Thymeleaf as the main technologies, in-memory storage, and the Controller-Service-Repository pattern.

## Functional Requirements

REQ-1: Create new recipe: User fills out form with recipe name, prep time, number of servings, and description. The system saves the recipe if all validations pass.

REQ-2: Read all recipes: The system displays all previously created recipes as a list with name, prep time, number of servings, and links to the operations to add, edit, or delete a recipe.

REQ-3: Update a recipe: User fills out a recipe edit form with new values. The system updates the recipe if all validations pass, otherwise it restores previous values.

REQ-4: Delete a recipe: User selects a recipe from the recipe list. The system deletes the selected recipe.

REQ-5: Inline validation error messages: The system displays error messages next to invalid fields after the user submits the form. The system does not save the recipe if any validation fails.

## Entity Model

Recipe: id (unique identifier), name, prepTimeMinutes, servings, description
