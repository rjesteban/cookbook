package app.recipe.cookbook.recipe;

import app.recipe.cookbook.recipe.db.entity.Ingredient;
import app.recipe.cookbook.recipe.db.repository.IngredientRepository;
import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.db.entity.Recipe;
import app.recipe.cookbook.recipe.dto.request.RecipeSearchCriteria;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
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

    private final IngredientRepository ingredientRepository;
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

    @Transactional
    public RecipeDto createRecipe(SaveRecipeRequestDto requestDto) {
        log.info("Creating new recipe with title: {}", requestDto.getTitle());

        final List<Ingredient> processedIngredients = upsertIngredients(requestDto.getIngredients());
        final Recipe recipe = recipeMapper.fromCreateRequestDto(requestDto, processedIngredients);
        final UUID recipeId = recipe.getId() != null ? recipe.getId() : UUID.randomUUID();
        recipe.setId(recipeId);

        // Update all ingredients and instructions with the correct recipe ID
        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipeId(recipeId));
        recipe.getInstructions().forEach(instruction -> instruction.setRecipeId(recipeId));

        // Save the updated recipe: cascade will handle ingredients and instructions
        final Recipe savedRecipe = recipeRepository.save(recipe);

        log.info("Successfully created recipe with ID: {}", savedRecipe.getId());
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional
    public void updateRecipe(UUID id, SaveRecipeRequestDto requestDto) {
        log.info("Updating recipe with ID: {}", id);

        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException("Recipe not found with ID: " + id);
        }

        final List<Ingredient> processedIngredients = upsertIngredients(requestDto.getIngredients());
        final Recipe recipe = recipeMapper.fromUpdateRequestDto(requestDto, id, processedIngredients);

        // Update all ingredients and instructions with the correct recipe ID
        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipeId(id));
        recipe.getInstructions().forEach(instruction -> instruction.setRecipeId(id));

        // Save the updated recipe: cascade will handle ingredients and instructions
        final Recipe savedRecipe = recipeRepository.save(recipe);

        log.info("Successfully updated recipe with ID: {}", savedRecipe.getId());
    }

    /**
     * Business logic: Process ingredients by finding existing ones or creating new ones
     */
    private List<Ingredient> upsertIngredients(List<SaveRecipeRequestDto.IngredientRequestDto> ingredientDtos) {
        // N + 1 problem, although there are a few steps so no need to further optimize for now.
        return ingredientDtos.stream()
                .map(this::upsertIngredient)
                .collect(Collectors.toList());
    }

    /**
     * Business logic Find existing ingredient by name or create a new one
     */
    private Ingredient upsertIngredient(SaveRecipeRequestDto.IngredientRequestDto dto) {
        return ingredientRepository.findByNameIgnoreCase(dto.getName())
                .orElseGet(() -> {
                    log.debug("Creating new ingredient: {}", dto.getName());
                    Ingredient newIngredient = Ingredient.builder()
                            .name(dto.getName())
                            .isVegetarian(dto.getIsVegetarian())
                            .build();
                    return ingredientRepository.save(newIngredient);
                });
    }
}
