package app.recipe.cookbook.recipe;

import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.db.entity.Recipe;
import app.recipe.cookbook.recipe.dto.request.RecipeSearchCriteria;
import app.recipe.cookbook.recipe.exception.RecipeNotFoundException;
import app.recipe.cookbook.recipe.mappers.RecipeMapper;
import app.recipe.cookbook.recipe.db.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    // TODO: Worry about pagination later
    public List<RecipeDto> searchRecipes(RecipeSearchCriteria searchCriteria) {
        final List<Recipe> resultsFromQuery;
        if (searchCriteria.shouldUseAdvancedSearch()) {
            resultsFromQuery = recipeRepository.findRecipesWithFilters(
                    searchCriteria.getIsVegetarian(),
                    searchCriteria.getEffectiveServingSize(),
                    searchCriteria.getMinServingSize(),
                    searchCriteria.getMaxServingSize(),
                    searchCriteria.getInstructionsContent()
            );
        } else {
            resultsFromQuery = recipeRepository.findAll();
        }

        return resultsFromQuery.stream()
                .filter(recipe -> matchesIngredientCriteria(recipe, searchCriteria))
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean matchesIngredientCriteria(Recipe recipe, RecipeSearchCriteria searchCriteria) {
        return matchesIncludeIngredients(recipe, searchCriteria.getIncludeIngredients())
                && matchesExcludeIngredients(recipe, searchCriteria.getExcludeIngredients());
    }

    /**
     * Recipe must contain ALL specified ingredients.
     *
     * @param recipe recipe being inspected.
     * @param includeIngredients list of ingredients that must all be in the recipe.
     * @return true if all ingredients are in the recipe. false otherwise.
     */
    private boolean matchesIncludeIngredients(Recipe recipe, List<String> includeIngredients) {
        if (CollectionUtils.isEmpty(includeIngredients)) {
            return true; // No filter = match all
        }
        Set<String> recipeIngredientNames = recipe.getIngredients().stream()
                .map(ri -> ri.getIngredient().getName().toLowerCase())
                .collect(Collectors.toSet());
        return includeIngredients.stream()
                .allMatch(required ->
                        recipeIngredientNames.stream()
                                .anyMatch(recipeName -> recipeName.contains(required.toLowerCase().trim()))
                );
    }

    /**
     * Recipe must NOT contain ANY specified ingredients.
     *
     * @param recipe recipe being inspected
     * @param excludeIngredients list of ingredients (any of them) must not be in the recipe.
     * @return true if any of the ingredients is in the recipe. false otherwise.
     */
    private boolean matchesExcludeIngredients(Recipe recipe, List<String> excludeIngredients) {
        if (CollectionUtils.isEmpty(excludeIngredients)) {
            return true; // No filter = nothing to exclude
        }
        Set<String> recipeIngredientNames = recipe.getIngredients().stream()
                .map(ri -> ri.getIngredient().getName().toLowerCase())
                .collect(Collectors.toSet());
        return excludeIngredients.stream()
                .noneMatch(excluded ->
                        recipeIngredientNames.stream()
                                .anyMatch(recipeName -> recipeName.contains(excluded.toLowerCase().trim()))
                );
    }

    public RecipeDto getRecipeById(UUID id) {
        final Recipe recipe = recipeRepository
                .findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with ID: " + id));
        return recipeMapper.toDto(recipe);
    }

    @Transactional
    public void deleteRecipe(UUID id) {
        log.info("Deleting recipe with ID: {}", id);
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException("Recipe not found with ID: " + id);
        }
        recipeRepository.deleteById(id);
        log.info("Successfully deleted recipe with ID: {}", id);
    }
}
