package app.recipe.cookbook.recipe.db.repository;

import app.recipe.cookbook.recipe.db.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    Optional<Ingredient> findByNameIgnoreCase(String name);
}
