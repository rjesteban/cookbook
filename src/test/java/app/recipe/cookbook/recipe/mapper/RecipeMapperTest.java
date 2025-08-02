package app.recipe.cookbook.recipe.mapper;

import app.recipe.cookbook.recipe.db.entity.Ingredient;
import app.recipe.cookbook.recipe.db.entity.Recipe;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeMapper Tests")
class RecipeMapperTest {

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private InstructionMapper instructionMapper;

    @InjectMocks
    private RecipeMapper recipeMapper;

    private SaveRecipeRequestDto requestDto;
    private UUID recipeId;

    @BeforeEach
    void setUp() {
        recipeId = UUID.randomUUID();
        
        requestDto = SaveRecipeRequestDto.builder()
                .title("Test Recipe")
                .description("Test Description")
                .servingSize(4)
                .ingredients(Arrays.asList(
                        SaveRecipeRequestDto.IngredientRequestDto.builder()
                                .name("tomato")
                                .quantity(BigDecimal.valueOf(2))
                                .unit("pieces")
                                .isVegetarian(true)
                                .build()
                ))
                .instructions(Arrays.asList(
                        SaveRecipeRequestDto.InstructionRequestDto.builder()
                                .content("Chop tomatoes")
                                .build()
                ))
                .build();

        // Mock the mapper methods to return empty lists for simplicity
        when(ingredientMapper.fromIngredientsAndDtos(any(), any(), any())).thenReturn(Collections.emptyList());
        when(instructionMapper.fromRequestDto(any(), any())).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("Should create vegetarian recipe when all ingredients are vegetarian")
    void shouldCreateVegetarianRecipeWhenAllIngredientsAreVegetarian() {
        // Given
        List<Ingredient> allVegetarianIngredients = Arrays.asList(
                createIngredient("tomato", true),
                createIngredient("lettuce", true),
                createIngredient("carrot", true)
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, allVegetarianIngredients);

        // Then
        assertThat(result.getIsVegetarian()).isTrue();
        assertThat(result.getTitle()).isEqualTo("Test Recipe");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getServings()).isEqualTo(4);
        assertThat(result.getId()).isNull(); // Should be null for create
    }

    @Test
    @DisplayName("Should create non-vegetarian recipe when any ingredient is non-vegetarian")
    void shouldCreateNonVegetarianRecipeWhenAnyIngredientIsNonVegetarian() {
        // Given
        List<Ingredient> mixedIngredients = Arrays.asList(
                createIngredient("tomato", true),
                createIngredient("chicken", false), // Non-vegetarian ingredient
                createIngredient("lettuce", true)
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, mixedIngredients);

        // Then
        assertThat(result.getIsVegetarian()).isFalse();
        assertThat(result.getTitle()).isEqualTo("Test Recipe");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getServings()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should create non-vegetarian recipe when ingredient has null vegetarian status")
    void shouldCreateNonVegetarianRecipeWhenIngredientHasNullVegetarianStatus() {
        // Given
        List<Ingredient> ingredientsWithNull = Arrays.asList(
                createIngredient("tomato", true),
                createIngredient("unknownIngredient", null) // Null vegetarian status
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, ingredientsWithNull);

        // Then
        assertThat(result.getIsVegetarian()).isFalse();
    }

    @Test
    @DisplayName("Should create non-vegetarian recipe when ingredient has false vegetarian status")
    void shouldCreateNonVegetarianRecipeWhenIngredientHasFalseVegetarianStatus() {
        // Given
        List<Ingredient> ingredientsWithFalse = Arrays.asList(
                createIngredient("tomato", true),
                createIngredient("beef", false)
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, ingredientsWithFalse);

        // Then
        assertThat(result.getIsVegetarian()).isFalse();
    }

    @Test
    @DisplayName("Should handle empty ingredients list")
    void shouldHandleEmptyIngredientsList() {
        // Given
        List<Ingredient> emptyIngredients = Collections.emptyList();

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, emptyIngredients);

        // Then
        assertThat(result.getIsVegetarian()).isTrue(); // Empty list should be considered vegetarian
    }

    @Test
    @DisplayName("Should update recipe with correct ID and vegetarian status")
    void shouldUpdateRecipeWithCorrectIdAndVegetarianStatus() {
        // Given
        List<Ingredient> vegetarianIngredients = Arrays.asList(
                createIngredient("spinach", true),
                createIngredient("mushroom", true)
        );

        // When
        Recipe result = recipeMapper.fromUpdateRequestDto(requestDto, recipeId, vegetarianIngredients);

        // Then
        assertThat(result.getId()).isEqualTo(recipeId);
        assertThat(result.getIsVegetarian()).isTrue();
        assertThat(result.getTitle()).isEqualTo("Test Recipe");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getServings()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should handle mixed vegetarian status in update")
    void shouldHandleMixedVegetarianStatusInUpdate() {
        // Given
        List<Ingredient> mixedIngredients = Arrays.asList(
                createIngredient("spinach", true),
                createIngredient("salmon", false)
        );

        // When
        Recipe result = recipeMapper.fromUpdateRequestDto(requestDto, recipeId, mixedIngredients);

        // Then
        assertThat(result.getId()).isEqualTo(recipeId);
        assertThat(result.getIsVegetarian()).isFalse();
    }

    @Test
    @DisplayName("Should handle single vegetarian ingredient")
    void shouldHandleSingleVegetarianIngredient() {
        // Given
        List<Ingredient> singleVegetarianIngredient = Arrays.asList(
                createIngredient("apple", true)
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, singleVegetarianIngredient);

        // Then
        assertThat(result.getIsVegetarian()).isTrue();
    }

    @Test
    @DisplayName("Should handle single non-vegetarian ingredient")
    void shouldHandleSingleNonVegetarianIngredient() {
        // Given
        List<Ingredient> singleNonVegetarianIngredient = Arrays.asList(
                createIngredient("pork", false)
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, singleNonVegetarianIngredient);

        // Then
        assertThat(result.getIsVegetarian()).isFalse();
    }

    @Test
    @DisplayName("Should handle ingredients with mixed Boolean values including null")
    void shouldHandleIngredientsWithMixedBooleanValuesIncludingNull() {
        // Given - Testing edge case with multiple null values
        List<Ingredient> mixedWithNulls = Arrays.asList(
                createIngredient("tomato", true),
                createIngredient("mystery1", null),
                createIngredient("lettuce", true),
                createIngredient("mystery2", null)
        );

        // When
        Recipe result = recipeMapper.fromCreateRequestDto(requestDto, mixedWithNulls);

        // Then
        assertThat(result.getIsVegetarian()).isFalse(); // Any null should make it non-vegetarian
    }

    /**
     * Helper method to create test ingredients
     */
    private Ingredient createIngredient(String name, Boolean isVegetarian) {
        return Ingredient.builder()
                .id(UUID.randomUUID())
                .name(name)
                .isVegetarian(isVegetarian)
                .build();
    }
}