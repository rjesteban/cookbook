package app.recipe.cookbook.recipe.mappers;

import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.db.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
