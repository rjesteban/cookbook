package app.recipe.cookbook.recipe.mapper;

import app.recipe.cookbook.recipe.db.entity.Instruction;
import app.recipe.cookbook.recipe.dto.domain.InstructionDto;
import app.recipe.cookbook.recipe.dto.request.SaveRecipeRequestDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class InstructionMapper {

    public List<InstructionDto> toDto(List<Instruction> recipeInstructions) {
        return recipeInstructions
                .stream()
                .map(this::toDto)
                .sorted(Comparator.comparingInt(InstructionDto::getStepNumber))
                .toList();
    }

    private InstructionDto toDto(Instruction instruction) {
        return InstructionDto.builder()
                .id(instruction.getId())
                .recipeId(instruction.getRecipeId())
                .content(instruction.getContent())
                .stepNumber(instruction.getStepNumber())
                .build();
    }

    public List<Instruction> fromRequestDto(List<SaveRecipeRequestDto.InstructionRequestDto> instructionDtos, UUID recipeId) {
        final List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < instructionDtos.size(); i++) {
            instructions.add(
                    fromRequestDto(instructionDtos.get(i), recipeId, i + 1)
            );
        }
        return instructions;
    }

    private Instruction fromRequestDto(SaveRecipeRequestDto.InstructionRequestDto dto, UUID recipeId, int stepNumber) {
        return Instruction.builder()
                .recipeId(recipeId)
                .stepNumber(stepNumber)
                .content(dto.getContent())
                .build();
    }
}
