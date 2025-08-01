package app.recipe.cookbook.recipe.dto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructionDto {

    private UUID id;
    private UUID recipeId;

    private Integer stepNumber;
    private String content;

}
