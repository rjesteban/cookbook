package app.recipe.cookbook.recipe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSearchCriteria {

    private Boolean isVegetarian;
    private Integer servings;
    private Integer minServings;
    private Integer maxServings;
    private List<String> includeIngredients;
    private List<String> excludeIngredients;
    private String instructionsContent;

//    TODO later: Pagination parameters
//    private Integer page = 0;
//    private Integer size = 20;
//    private String sortBy = "createdAt";
//    private String sortDirection = "desc";

    public boolean hasMultipleFilters() {
        int filterCount = 0;
        if (isVegetarian != null) filterCount++;
        if (servings != null) filterCount++;
        if (minServings != null) filterCount++;
        if (maxServings != null) filterCount++;
        if (StringUtils.hasText(instructionsContent)) filterCount++;
        if (excludeIngredients != null && !excludeIngredients.isEmpty()) filterCount++;
        if (includeIngredients != null && !includeIngredients.isEmpty()) filterCount++;
        return filterCount > 1;
    }
}
