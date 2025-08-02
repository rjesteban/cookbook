package app.recipe.cookbook.recipe.dto.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cooking instruction step")
public class InstructionDto {

    @Schema(description = "Instruction identifier", example = "770e8400-e29b-41d4-a716-446655440002")
    private UUID id;
    
    @Schema(description = "Recipe identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID recipeId;

    @Schema(description = "Step number in the cooking process", example = "1")
    private Integer stepNumber;
    
    @Schema(description = "Instruction content", example = "Add salt and pepper to taste.")
    private String content;

}
