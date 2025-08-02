package app.recipe.cookbook.recipe.mapper;

import app.recipe.cookbook.recipe.db.entity.Ingredient;
import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.db.entity.Recipe;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Helper methods that allows us to map from entities "to Dto" and vice versa ("from dto" to entity)
 */
@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final IngredientMapper ingredientMapper;
    private final InstructionMapper instructionMapper;

    public RecipeDto toDto(Recipe recipeEntity) {
        return RecipeDto.builder()
                .id(recipeEntity.getId())
                .title(recipeEntity.getTitle())
                .description(recipeEntity.getDescription())
                .servings(recipeEntity.getServings())
                .isVegetarian(recipeEntity.getIsVegetarian())
                .createdAt(recipeEntity.getCreatedAt())
                .updatedAt(recipeEntity.getUpdatedAt())
                .ingredients(ingredientMapper.toDto(recipeEntity.getIngredients()))
                .instructions(instructionMapper.toDto(recipeEntity.getInstructions()))
                .build();
    }

    public Recipe fromCreateRequestDto(
            SaveRecipeRequestDto requestDto,
            List<Ingredient> processedIngredients) {
        return fromRequestDto(requestDto, null, processedIngredients);
    }

    public Recipe fromUpdateRequestDto(
            SaveRecipeRequestDto requestDto,
            UUID recipeId,
            List<Ingredient> processedIngredients) {
        return fromRequestDto(requestDto, recipeId, processedIngredients);
    }

    private Recipe fromRequestDto(
            SaveRecipeRequestDto requestDto,
            @Nullable UUID recipeId,
            List<Ingredient> processedIngredients) {
        // Calculate if recipe is vegetarian based on processed ingredients (from DB)
        boolean isVegetarian = processedIngredients.stream()
                .allMatch(ingredient -> Boolean.TRUE.equals(ingredient.getIsVegetarian()));

        Recipe recipe = Recipe.builder()
                .id(recipeId) // null for create, actual ID for update
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .servings(requestDto.getServingSize())
                .isVegetarian(isVegetarian)
                .build();

        // Generate a temporary UUID for create operations if null
        UUID tempRecipeId = recipeId != null ? recipeId : UUID.randomUUID();

        recipe.setIngredients(
                ingredientMapper.fromIngredientsAndDtos(
                        processedIngredients,
                        requestDto.getIngredients(),
                        tempRecipeId
                )
        );
        recipe.setInstructions(
                instructionMapper.fromRequestDto(
                        requestDto.getInstructions(),
                        tempRecipeId
                )
        );
        return recipe;
    }
}
