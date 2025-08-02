package app.recipe.cookbook.recipe.mapper;

import app.recipe.cookbook.recipe.db.entity.Ingredient;
import app.recipe.cookbook.recipe.dto.domain.RecipeIngredientDto;
import app.recipe.cookbook.recipe.db.entity.RecipeIngredient;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class IngredientMapper {

    /**
     * Converts recipeIngredient entities into their DTO equivalent.
     *
     * @param recipeIngredientEntities recipe ingredient entities to be converted
     * @return list of mapped recipe ingredient DTO
     */
    public List<RecipeIngredientDto> toDto(List<RecipeIngredient> recipeIngredientEntities) {
        return recipeIngredientEntities
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RecipeIngredientDto toDto(RecipeIngredient recipeIngredientEntity) {
        return RecipeIngredientDto.builder()
                .ingredientId(recipeIngredientEntity.getIngredientId())
                .recipeId(recipeIngredientEntity.getRecipeId())
                .name(recipeIngredientEntity.getIngredient().getName())
                .isVegetarian(recipeIngredientEntity.getIngredient().getIsVegetarian())
                .unit(recipeIngredientEntity.getUnit())
                .quantity(recipeIngredientEntity.getQuantity())
                .build();
    }

    /**
     * Converts processed ingredients to RecipeIngredient entities
     */
    public List<RecipeIngredient> fromIngredientsAndDtos(
            List<Ingredient> ingredients,
            List<SaveRecipeRequestDto.IngredientRequestDto> ingredientDtos,
            UUID recipeId) {

        if (ingredients.size() != ingredientDtos.size()) {
            throw new IllegalArgumentException("Ingredients and DTOs lists must have the same size");
        }

        List<RecipeIngredient> result = new ArrayList<>(); // needs to be in order
        for (int i = 0; i < ingredients.size(); i++) {
            result.add(fromIngredientAndDto(ingredients.get(i), ingredientDtos.get(i), recipeId));
        }
        return result;
    }

    /**
     * Creates RecipeIngredient from existing Ingredient and DTO
     */
    public RecipeIngredient fromIngredientAndDto(
            Ingredient ingredient,
            SaveRecipeRequestDto.IngredientRequestDto dto,
            UUID recipeId) {

        return RecipeIngredient.builder()
                .recipeId(recipeId)
                .ingredientId(ingredient.getId())
                .ingredient(ingredient)
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .build();
    }
}
