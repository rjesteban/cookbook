package app.recipe.cookbook.recipe;

import app.recipe.cookbook.recipe.db.entity.Ingredient;
import app.recipe.cookbook.recipe.db.entity.Recipe;
import app.recipe.cookbook.recipe.db.repository.IngredientRepository;
import app.recipe.cookbook.recipe.db.repository.RecipeRepository;
import app.recipe.cookbook.recipe.dto.domain.RecipeDto;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import app.recipe.cookbook.recipe.mapper.RecipeMapper;
import app.recipe.cookbook.common.exception.RecipeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeService Tests")
class RecipeServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    // Reusable fields throughout the test suite
    private UUID recipeId;
    private Recipe mockRecipe;
    private RecipeDto mockRecipeDto;
    private SaveRecipeRequestDto mockRequestDto;
    private Ingredient vegetarianIngredient;
    private Ingredient nonVegetarianIngredient;

    @BeforeEach
    void setUp() {
        recipeId = UUID.randomUUID();
        
        // Mock ingredients
        vegetarianIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .name("tomato")
                .isVegetarian(true)
                .build();
        
        nonVegetarianIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .name("chicken")
                .isVegetarian(false)
                .build();

        // Mock recipe
        mockRecipe = Recipe.builder()
                .id(recipeId)
                .title("Dummy Recipe Title")
                .description("Dummy Recipe Description")
                .servings(4)
                .isVegetarian(true)
                .build();

        // Mock DTO
        mockRecipeDto = RecipeDto.builder()
                .id(recipeId)
                .title("Dummy Recipe Title")
                .description("Dummy Recipe Description")
                .servings(4)
                .isVegetarian(true)
                .build();

        // Mock request DTO
        mockRequestDto = SaveRecipeRequestDto.builder()
                .title("Dummy Recipe Title")
                .description("Dummy Recipe Description")
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
                                .content("Slice and dice tomatoes.")
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("Should get recipe by ID successfully")
    void shouldGetRecipeByIdSuccessfully() {
        // Given
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(recipeMapper.toDto(mockRecipe)).thenReturn(mockRecipeDto);

        // When
        RecipeDto result = recipeService.getRecipeById(recipeId);

        // Then
        assertThat(result).isEqualTo(mockRecipeDto);
        verify(recipeRepository).findById(recipeId);
        verify(recipeMapper).toDto(mockRecipe);
    }

    @Test
    @DisplayName("Should throw RecipeNotFoundException when recipe not found")
    void shouldThrowExceptionWhenRecipeNotFound() {
        // Given
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recipeService.getRecipeById(recipeId))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("Recipe not found with ID: " + recipeId);
        
        verify(recipeRepository).findById(recipeId);
        verifyNoInteractions(recipeMapper);
    }

    @Test
    @DisplayName("Should delete recipe successfully")
    void shouldDeleteRecipeSuccessfully() {
        // Given
        when(recipeRepository.existsById(recipeId)).thenReturn(true);

        // When
        recipeService.deleteRecipe(recipeId);

        // Then
        verify(recipeRepository).existsById(recipeId);
        verify(recipeRepository).deleteById(recipeId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent recipe")
    void shouldThrowExceptionWhenDeletingNonExistentRecipe() {
        // Given
        when(recipeRepository.existsById(recipeId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> recipeService.deleteRecipe(recipeId))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("Recipe not found with ID: " + recipeId);
        
        verify(recipeRepository).existsById(recipeId);
        verify(recipeRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should create recipe with vegetarian ingredients")
    void shouldCreateRecipeWithVegetarianIngredients() {
        // Given
        List<Ingredient> processedIngredients = Collections.singletonList(vegetarianIngredient);
        when(ingredientRepository.findByNameIgnoreCase("tomato"))
                .thenReturn(Optional.of(vegetarianIngredient));
        when(recipeMapper.fromCreateRequestDto(mockRequestDto, processedIngredients))
                .thenReturn(mockRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);
        when(recipeMapper.toDto(mockRecipe)).thenReturn(mockRecipeDto);

        // When
        RecipeDto result = recipeService.createRecipe(mockRequestDto);

        // Then
        assertThat(result).isEqualTo(mockRecipeDto);
        verify(ingredientRepository).findByNameIgnoreCase("tomato");
        verify(recipeMapper).fromCreateRequestDto(mockRequestDto, processedIngredients);
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(mockRecipe);
    }

    @Test
    @DisplayName("Should create new ingredient when it doesn't exist")
    void shouldCreateNewIngredientWhenItDoesntExist() {
        // Given
        String newIngredientName = "newIngredient";
        SaveRecipeRequestDto.IngredientRequestDto newIngredientDto = 
                SaveRecipeRequestDto.IngredientRequestDto.builder()
                        .name(newIngredientName)
                        .quantity(BigDecimal.valueOf(1))
                        .unit("piece")
                        .isVegetarian(true)
                        .build();
        
        SaveRecipeRequestDto requestWithNewIngredient = SaveRecipeRequestDto.builder()
                .title("Dummy Recipe Title")
                .description("Dummy Recipe Description")
                .servingSize(4)
                .ingredients(Arrays.asList(newIngredientDto))
                .instructions(Arrays.asList(
                        SaveRecipeRequestDto.InstructionRequestDto.builder()
                                .content("Test instruction")
                                .build()
                ))
                .build();

        Ingredient newIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .name(newIngredientName)
                .isVegetarian(true)
                .build();

        when(ingredientRepository.findByNameIgnoreCase(newIngredientName))
                .thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);
        when(recipeMapper.fromCreateRequestDto(eq(requestWithNewIngredient), any()))
                .thenReturn(mockRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);
        when(recipeMapper.toDto(mockRecipe)).thenReturn(mockRecipeDto);

        // When
        RecipeDto result = recipeService.createRecipe(requestWithNewIngredient);

        // Then
        assertThat(result).isEqualTo(mockRecipeDto);
        verify(ingredientRepository).findByNameIgnoreCase(newIngredientName);
        verify(ingredientRepository).save(argThat(ingredient -> 
                ingredient.getName().equals(newIngredientName) && 
                ingredient.getIsVegetarian().equals(true)
        ));
    }

    @Test
    @DisplayName("Should update recipe successfully")
    void shouldUpdateRecipeSuccessfully() {
        // Given
        List<Ingredient> processedIngredients = Arrays.asList(vegetarianIngredient);
        when(recipeRepository.existsById(recipeId)).thenReturn(true);
        when(ingredientRepository.findByNameIgnoreCase("tomato"))
                .thenReturn(Optional.of(vegetarianIngredient));
        when(recipeMapper.fromUpdateRequestDto(mockRequestDto, recipeId, processedIngredients))
                .thenReturn(mockRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);

        // When
        recipeService.updateRecipe(recipeId, mockRequestDto);

        // Then
        verify(recipeRepository).existsById(recipeId);
        verify(ingredientRepository).findByNameIgnoreCase("tomato");
        verify(recipeMapper).fromUpdateRequestDto(mockRequestDto, recipeId, processedIngredients);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent recipe")
    void shouldThrowExceptionWhenUpdatingNonExistentRecipe() {
        // Given
        when(recipeRepository.existsById(recipeId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> recipeService.updateRecipe(recipeId, mockRequestDto))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("Recipe not found with ID: " + recipeId);
        
        verify(recipeRepository).existsById(recipeId);
        verifyNoInteractions(ingredientRepository, recipeMapper);
        verify(recipeRepository, never()).save(any());
    }
}