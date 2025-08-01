package app.recipe.cookbook.recipe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Junction table between recipe and ingredient (many-to-many)
 */
@Entity
@Table(name = "recipe_ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RecipeIngredient.RecipeIngredientId.class)
public class RecipeIngredient {

    // Main fields
    @Id
    @Column(name = "recipe_id", columnDefinition = "UUID")
    private UUID recipeId;

    @Id
    @Column(name = "ingredient_id", columnDefinition = "UUID")
    private UUID ingredientId;

    @Column(nullable = false)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false) // Must be positive
    private BigDecimal quantity;

    @Column(length = 50)
    private String unit;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", insertable = false, updatable = false)
    private Ingredient ingredient;

    // Composite primary key definition
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RecipeIngredientId implements Serializable {
        private UUID recipeId;
        private UUID ingredientId;
    }
}
