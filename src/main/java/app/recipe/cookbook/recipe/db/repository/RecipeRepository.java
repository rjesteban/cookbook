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
     * Advanced search with multiple optional filters.
     * Other complex filters will be done in application side (after these records are retrieved).
     */
    @Query("""
        SELECT DISTINCT r FROM Recipe r
        LEFT JOIN FETCH r.ingredients ri
        LEFT JOIN FETCH ri.ingredient
        LEFT JOIN r.instructions inst
        WHERE
            (:isVegetarian IS NULL OR r.isVegetarian = :isVegetarian)
            AND (:servings IS NULL OR r.servings = :servings)
            AND (:minServings IS NULL OR r.servings >= :minServings)
            AND (:maxServings IS NULL OR r.servings <= :maxServings)
            AND (:instructionsContent IS NULL OR inst.content LIKE CONCAT('%', :instructionsContent, '%'))
        """)
    List<Recipe> findRecipesWithFilters(
            @Param("isVegetarian") Boolean isVegetarian,
            @Param("servings") Integer servings,
            @Param("minServings") Integer minServings,
            @Param("maxServings") Integer maxServings,
            @Param("instructionsContent") String instructionsContent
    );
}
