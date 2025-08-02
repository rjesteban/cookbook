package app.recipe.cookbook.recipe.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "instructions", uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_id", "step_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instruction {

    // Main Fields
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recipe_id", nullable = false)
    private UUID recipeId;

    @Min(value = 1)
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String content;

    // Relationship fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private Recipe recipe;
}
