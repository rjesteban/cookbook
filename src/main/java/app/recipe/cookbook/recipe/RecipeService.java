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

import java.util.List;
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

        final List<Recipe> results;
        if (searchCriteria.hasMultipleFilters()) {
            results = recipeRepository.findRecipesWithFilters(
                    searchCriteria.getIsVegetarian(),
                    searchCriteria.getServings(),
                    searchCriteria.getMinServings(),
                    searchCriteria.getMaxServings(),
                    searchCriteria.getIncludeIngredients(),
                    searchCriteria.getExcludeIngredients(),
                    searchCriteria.getInstructionsContent()
            );
        } else {
            results = recipeRepository.findAll();
        }
        return results.stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecipeDto getRecipeById(UUID id) {
        final Recipe recipe =  recipeRepository
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
