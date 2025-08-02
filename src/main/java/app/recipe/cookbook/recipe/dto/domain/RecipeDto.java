package app.recipe.cookbook.recipe.dto.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Recipe data transfer object")
public class RecipeDto {

    @Schema(description = "Unique recipe identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Recipe title", example = "Breakfast Omelette")
    private String title;
    
    @Schema(description = "Recipe description", example = "Omelette is a breakfast staple in Bangkok for people on the go.")
    private String description;
    
    @Schema(description = "Number of servings", example = "1")
    private Integer servings;
    
    @Schema(description = "Whether the recipe is vegetarian", example = "false")
    private boolean isVegetarian;

    @Schema(description = "List of ingredients with quantities")
    private List<RecipeIngredientDto> ingredients;
    
    @Schema(description = "List of cooking instructions")
    private List<InstructionDto> instructions;

    @Schema(description = "Recipe creation timestamp", example = "2024-01-15T10:30:00Z")
    private Instant createdAt;
    
    @Schema(description = "Recipe last update timestamp", example = "2024-01-15T10:30:00Z")
    private Instant updatedAt;
}
