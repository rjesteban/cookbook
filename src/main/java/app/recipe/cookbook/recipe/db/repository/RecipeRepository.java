package app.recipe.cookbook.recipe.db.repository;

import app.recipe.cookbook.recipe.db.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    /**
     * Advanced search with multiple optional filters
     * TODO: Update query
     */
    @Query("TODO: UPDATE QUERY")
    List<Recipe> findRecipesWithFilters(
            @Param("isVegetarian") Boolean isVegetarian,
            @Param("servings") Integer servings,
            @Param("minServings") Integer minServings,
            @Param("maxServings") Integer maxServings,
            @Param("includeIngredients") List<String> includeIngredients,
            @Param("excludeIngredients") List<String> excludeIngredients,
            @Param("instructionsContent") String instructionsContent
    );
}
