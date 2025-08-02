package app.recipe.cookbook.recipe.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main entity - most of the business logic goes around here
 */
@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    // Main fields
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotNull
    @Size(min = 3, max = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    @Size(min = 3, max = 1000)
    private String description;

    // 1 serving size = serves 1 adult
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private Integer servings;

    /**
     * This field will be precomputed on save based on ingredients
     */
    @Column(name = "is_vegetarian")
    @Builder.Default
    private Boolean isVegetarian = false;

    // Audit trail fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Relationship fields
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Instruction> instructions = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();
}
