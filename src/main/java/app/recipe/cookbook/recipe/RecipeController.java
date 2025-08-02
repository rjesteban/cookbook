package app.recipe.cookbook.recipe;

import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.dto.request.RecipeSearchCriteria;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import app.recipe.cookbook.recipe.dto.response.ProjectApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectApiResponse<RecipeDto> createRecipe(@Valid @RequestBody SaveRecipeRequestDto saveRecipeRequestDto) {
        final RecipeDto createdRecipe = recipeService.createRecipe(saveRecipeRequestDto);
        return ProjectApiResponse.success(createdRecipe);
    }

    @GetMapping
    public ProjectApiResponse<List<RecipeDto>> getRecipes(
            @RequestParam(required = false) @Min(1) Integer servings,
            @RequestParam(required = false) @Min(1) Integer minServings,
            @RequestParam(required = false) @Min(1) Integer maxServings,
            @RequestParam(required = false) Boolean isVegetarian,
            @RequestParam(required = false) List<String> includeIngredients,
            @RequestParam(required = false) List<String> excludeIngredients,
            @RequestParam(required = false) String instructionContent
            // TODO: support pagination
    ) {
        final RecipeSearchCriteria searchCriteria = RecipeSearchCriteria.builder()
                .isVegetarian(isVegetarian)
                .servingSize(servings)
                .minServingSize(minServings)
                .maxServingSize(maxServings)
                .includeIngredients(includeIngredients)
                .excludeIngredients(excludeIngredients)
                .instructionsContent(instructionContent)
                .build();

        searchCriteria.validate();

        final List<RecipeDto> searchResult = recipeService.searchRecipes(searchCriteria);
        return ProjectApiResponse.success(searchResult);
    }

    @GetMapping("/{id}")
    public ProjectApiResponse<RecipeDto> getRecipeById(@PathVariable UUID id) {
        log.info("Fetching recipe with ID: {}", id);
        final RecipeDto recipe = recipeService.getRecipeById(id);
        return ProjectApiResponse.success(recipe);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(
            @PathVariable UUID id,
            @RequestBody SaveRecipeRequestDto requestDto) {
        recipeService.updateRecipe(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
    }
}
