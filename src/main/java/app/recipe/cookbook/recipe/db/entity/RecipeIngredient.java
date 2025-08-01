package app.recipe.cookbook.recipe.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Junction table between recipe and ingredient (many-to-many)
 */
@Entity
@Table(name = "recipes_ingredients")
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

    // Audit trail fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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
