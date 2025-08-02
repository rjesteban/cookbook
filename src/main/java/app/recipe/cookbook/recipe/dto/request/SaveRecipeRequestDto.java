package app.recipe.cookbook.recipe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request body to be used when saving (create, update) a recipe.
 * This will collectively save other relating entities (instruction, ingredient) as well.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a recipe")
public class SaveRecipeRequestDto {

    @Schema(description = "Recipe title", example = "Breakfast Omelette")
    @NotBlank(message = "Recipe title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Schema(description = "Recipe description", example = "Omelette is a breakfast staple in Bangkok for people on the go.")
    @Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    private String description;

    @Schema(description = "Number of servings (1 serving = serves 1 adult)", example = "4")
    @NotNull(message = "Serving size is required. 1 serving size serves 1 adult")
    @Positive(message = "Servings size must be a positive number")
    private Integer servingSize;

    @Schema(description = "List of ingredients with quantities")
    @Valid
    @Size(min = 1, message = "At least one ingredient is required")
    private List<IngredientRequestDto> ingredients;

    @Schema(description = "List of cooking instructions")
    @Valid
    @Size(min = 1, message = "At least one instruction is required")
    private List<InstructionRequestDto> instructions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Ingredient information with quantity and measurement unit")
    public static class IngredientRequestDto {
        @Schema(description = "Ingredient name", example = "egg")
        @NotBlank(message = "Ingredient name is required")
        private String name;

        @Schema(description = "Quantity needed", example = "0.25")
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private BigDecimal quantity;

        /*
         * TODO: add validation, have a new table called "measurement_unit".
         *  Unit should also be dependent on the state (we can't say liters on a solid ingredient).
         *  With this as well we can have a lookup endpoint for measurements that client code can use
         *  to manipulate the units as desired (imperial vs metric).
         *  In the interest of time, I prefer to fall it as further enhancement.
         */
        @Schema(description = "Measurement unit", example = "dozen", nullable = true)
        private String unit;

        @Schema(description = "Whether the ingredient is vegetarian", example = "true", defaultValue = "false")
        @Builder.Default
        private Boolean isVegetarian = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Cooking instruction step")
    public static class InstructionRequestDto {
        @Schema(description = "Instruction content", example = "Add salt and pepper to taste.")
        @NotBlank(message = "Instruction content is required")
        private String content;
    }
}
