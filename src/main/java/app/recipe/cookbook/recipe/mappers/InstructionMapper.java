package app.recipe.cookbook.recipe.mappers;

import app.recipe.cookbook.recipe.dto.domain.InstructionDto;
import app.recipe.cookbook.recipe.db.entity.Instruction;

import java.util.List;
import java.util.stream.Collectors;

class InstructionMapper {

    public List<InstructionDto> toDto(List<Instruction> recipeInstructions) {
        return recipeInstructions
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private InstructionDto toDto(Instruction instruction) {
        return InstructionDto.builder()
                .id(instruction.getId())
                .recipeId(instruction.getRecipeId())
                .content(instruction.getContent())
                .stepNumber(instruction.getStepNumber())
                .build();
    }
}
