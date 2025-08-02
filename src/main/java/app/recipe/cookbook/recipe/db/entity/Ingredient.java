package app.recipe.cookbook.recipe.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    // Main fields
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    @NotNull
    private String name;

    @Column(name = "is_vegetarian")
    @Builder.Default
    private Boolean isVegetarian = false;

    // Relationship fields
    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
}
