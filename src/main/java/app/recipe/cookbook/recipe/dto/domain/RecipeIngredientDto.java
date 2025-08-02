package app.recipe.cookbook.recipe.dto.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Recipe ingredient with quantity and unit information")
public class RecipeIngredientDto {

    @Schema(description = "Recipe identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID recipeId;
    
    @Schema(description = "Ingredient identifier", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID ingredientId;

    @Schema(description = "Ingredient name", example = "egg")
    private String name;
    
    @Schema(description = "Whether the ingredient is vegetarian", example = "true")
    private Boolean isVegetarian;

    @Schema(description = "Quantity needed", example = "0.25")
    private BigDecimal quantity;
    
    @Schema(description = "Measurement unit", example = "dozen", nullable = true)
    private String unit;

}
