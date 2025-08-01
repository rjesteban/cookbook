package app.recipe.cookbook.recipe.dto.domain;

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
public class RecipeIngredientDto {

    private UUID recipeId;
    private UUID ingredientId;

    private String name;
    private Boolean isVegetarian;

    private BigDecimal quantity;
    private String unit;

}
