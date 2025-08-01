package app.recipe.cookbook.recipe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSearchCriteria {

    private Boolean isVegetarian;
    private Integer servingSize;
    private Integer minServingSize;
    private Integer maxServingSize;
    private List<String> includeIngredients;
    private List<String> excludeIngredients;
    private String instructionsContent;

//    TODO later: Pagination parameters
//    private Integer page = 0;
//    private Integer size = 20;
//    private String sortBy = "createdAt";
//    private String sortDirection = "desc";

    public Integer getEffectiveServingSize() {
        if (minServingSize != null && maxServingSize != null)
            return null;
        else
            return servingSize;
    }

    /**
     * Checks whether multiple filters were defined in the criteria.
     * Special filters use some advanced query (not directly provided by JPA repository).
     *
     * @return true if multiple filters were defined. false otherwise
     */
    public boolean shouldUseAdvancedSearch() {
        int filterCount = 0;
        if (isVegetarian != null) filterCount++;
        if (servingSize != null) filterCount++;
        if (minServingSize != null) filterCount++;
        if (maxServingSize != null) filterCount++;
        if (StringUtils.hasText(instructionsContent)) filterCount++;
        if (excludeIngredients != null && !excludeIngredients.isEmpty()) filterCount++;
        if (includeIngredients != null && !includeIngredients.isEmpty()) filterCount++;
        return filterCount > 1;
    }

    /**
     * Validates whether the combination of filters passed are valid and
     * do not conflict with each other.
     *
     * @throws IllegalArgumentException - when any of the search criteria
     * parameters were invalid.
     */
    public void validate() {
        validateServingsRange();
        validateIngredientsCriteria();
    }

    /**
     * Validates whether minServingSize and maxServingSize creates a range or not.
     * A value is in range when it is between minServingSize and maxServingSize.
     */
    private void validateServingsRange() {
        if (minServingSize != null && maxServingSize != null && minServingSize > maxServingSize) {
            throw new IllegalArgumentException(
                    String.format(
                            "minServingSize (%d) cannot be greater than maxServingSize (%d)",
                            minServingSize,
                            maxServingSize
                    )
            );
        }
    }

    /**
     * Validates whether {@code includeIngredients} and {@code excludeIngredients} do not have
     * "overlapping" (through "naive" inspection on names) items.
     */
    private void validateIngredientsCriteria() {
        if (CollectionUtils.isEmpty(includeIngredients) || CollectionUtils.isEmpty(excludeIngredients)) {
            return;
        }

        final Set<String> includeSetNames = includeIngredients.stream()
                .filter(StringUtils::hasText)
                .map(s -> s.toLowerCase().trim())
                .collect(Collectors.toUnmodifiableSet());

        final Set<String> excludeSetNames = excludeIngredients.stream()
                .filter(StringUtils::hasText)
                .map(s -> s.toLowerCase().trim())
                .collect(Collectors.toUnmodifiableSet());

        final Set<String> intersection = new HashSet<>(includeSetNames);
        intersection.retainAll(excludeSetNames);

        if (!intersection.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format(
                            "The following should not be in both " +
                                    "includeIngredients and excludeIngredients filter: %s",
                            intersection)
            );
        }

    }
}
