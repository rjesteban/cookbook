package app.recipe.cookbook.recipe.mappers;

import app.recipe.cookbook.recipe.dto.domain.RecipeIngredientDto;
import app.recipe.cookbook.recipe.db.entity.RecipeIngredient;

import java.util.List;
import java.util.stream.Collectors;

class IngredientMapper {

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
                .isVegetarian(recipeIngredientEntity.getIngredient().getIsVegetarian())
                .unit(recipeIngredientEntity.getUnit())
                .quantity(recipeIngredientEntity.getQuantity())
                .build();
    }
}
