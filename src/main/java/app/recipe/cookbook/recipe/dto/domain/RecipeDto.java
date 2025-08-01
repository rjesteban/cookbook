package app.recipe.cookbook.recipe.dto.domain;

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
public class RecipeDto {

    private UUID id;
    private String title;
    private String description;
    private Integer servings;
    private boolean isVegetarian;

    private List<RecipeIngredientDto> ingredients;
    private List<InstructionDto> instructions;

    private Instant createdAt;
    private Instant updatedAt;
}
