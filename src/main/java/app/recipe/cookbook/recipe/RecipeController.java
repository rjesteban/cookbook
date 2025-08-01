package app.recipe.cookbook.recipe;

import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.dto.request.RecipeSearchCriteria;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import app.recipe.cookbook.recipe.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;


    @PostMapping
    public void createRecipe(@RequestBody SaveRecipeRequestDto saveRecipeRequestDto) {

    }


    @GetMapping
    public ApiResponse<List<RecipeDto>> getRecipes(
            @RequestParam(required = false) Integer servings,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings,
            @RequestParam(required = false) Boolean isVegetarian,
            @RequestParam(required = false) List<String> includeIngredients,
            @RequestParam(required = false) List<String> excludeIngredients,
            @RequestParam(required = false) String instructionContent
            // TODO: support pagination
    ) {
        final RecipeSearchCriteria searchCriteria = RecipeSearchCriteria.builder()
                .isVegetarian(isVegetarian)
                .servings(servings)
                .minServings(minServings)
                .maxServings(maxServings)
                .includeIngredients(includeIngredients)
                .excludeIngredients(excludeIngredients)
                .instructionsContent(instructionContent)
                .build();

        final List<RecipeDto> searchResult = recipeService.searchRecipes(searchCriteria);
        return ApiResponse.success(searchResult);
    }

    @GetMapping("/{id}")
    public ApiResponse<RecipeDto> getRecipeById(@PathVariable UUID id) {
        log.info("Fetching recipe with ID: {}", id);
        final RecipeDto recipe = recipeService.getRecipeById(id);
        return ApiResponse.success(recipe);
    }

    @PutMapping("/{id}")
    public void updateRecipe(
            @PathVariable UUID id,
            @RequestBody SaveRecipeRequestDto requestDto) {

    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
    }
}
