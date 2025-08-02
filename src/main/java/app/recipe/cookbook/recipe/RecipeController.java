package app.recipe.cookbook.recipe;

import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.dto.request.RecipeSearchCriteria;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import app.recipe.cookbook.common.dto.response.ProjectApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Recipe Management", description = "APIs for managing cooking recipes, ingredients, and instructions")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new recipe",
            description = "Creates a new recipe with ingredients and instructions. The recipe will be automatically marked as vegetarian if all ingredients are vegetarian."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class)))
    })
    public ProjectApiResponse<RecipeDto> createRecipe(
            @Parameter(description = "Recipe data to create", required = true)
            @Valid @RequestBody SaveRecipeRequestDto saveRecipeRequestDto) {
        final RecipeDto createdRecipe = recipeService.createRecipe(saveRecipeRequestDto);
        return ProjectApiResponse.success(createdRecipe);
    }

    @GetMapping
    @Operation(
            summary = "Search recipes",
            description = "Search recipes with various filtering options. Multiple filters can be combined for advanced search."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class)))
    })
    public ProjectApiResponse<List<RecipeDto>> getRecipes(
            @Parameter(description = "Exact number of servings", example = "4")
            @RequestParam(required = false) @Min(1) Integer servings,
            @Parameter(description = "Minimum number of servings", example = "2")
            @RequestParam(required = false) @Min(1) Integer minServings,
            @Parameter(description = "Maximum number of servings", example = "6")
            @RequestParam(required = false) @Min(1) Integer maxServings,
            @Parameter(description = "Filter by vegetarian recipes", example = "true")
            @RequestParam(required = false) Boolean isVegetarian,
            @Parameter(description = "Include recipes containing these ingredients", example = "[\"tomato\", \"basil\"]")
            @RequestParam(required = false) List<String> includeIngredients,
            @Parameter(description = "Exclude recipes containing these ingredients", example = "[\"pork\", \"chicken\"]")
            @RequestParam(required = false) List<String> excludeIngredients,
            @Parameter(description = "Search within instruction content", example = "cook in wok")
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
    @Operation(
            summary = "Get recipe by ID",
            description = "Retrieve a specific recipe by its unique identifier including all ingredients and instructions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class)))
    })
    public ProjectApiResponse<RecipeDto> getRecipeById(
            @Parameter(description = "Recipe unique identifier", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Fetching recipe with ID: {}", id);
        final RecipeDto recipe = recipeService.getRecipeById(id);
        return ProjectApiResponse.success(recipe);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update an existing recipe",
            description = "Update a recipe by replacing all its data with the provided information. This will update ingredients and instructions as well."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class)))
    })
    public void updateRecipe(
            @Parameter(description = "Recipe unique identifier", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated recipe data", required = true)
            @RequestBody SaveRecipeRequestDto requestDto) {
        recipeService.updateRecipe(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a recipe",
            description = "Permanently delete a recipe and all its associated ingredients and instructions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipe deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectApiResponse.class)))
    })
    public void deleteRecipe(
            @Parameter(description = "Recipe unique identifier", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        recipeService.deleteRecipe(id);
    }
}
