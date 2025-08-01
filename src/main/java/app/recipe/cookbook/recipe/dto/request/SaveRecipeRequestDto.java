package app.recipe.cookbook.recipe.dto.request;

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
public class SaveRecipeRequestDto {

    @NotBlank(message = "Recipe title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    private String description;

    @NotNull(message = "Serving size is required. 1 serving size serves 1 adult")
    @Positive(message = "Servings size must be a positive number")
    private Integer servingSize;

    @Valid
    @Size(min = 1, message = "At least one ingredient is required")
    private List<IngredientRequestDto> ingredients;

    @Valid
    @Size(min = 1, message = "At least one instruction is required")
    private List<InstructionRequestDto> instructions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientRequestDto {
        @NotBlank(message = "Ingredient name is required")
        private String name;

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
        private String unit;

        @Builder.Default
        private Boolean isVegetarian = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InstructionRequestDto {
        @NotBlank(message = "Instruction content is required")
        private String content;
    }
}
