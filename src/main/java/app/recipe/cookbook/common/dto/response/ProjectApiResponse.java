package app.recipe.cookbook.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ProjectApiResponse<T> {

    @Schema(description = "Response data payload", nullable = true)
    private T data;

    @Schema(description = "Indicates if the request was successful", example = "true", defaultValue = "true")
    @Builder.Default
    private boolean success = true;

    @Schema(description = "Error code for failed requests", example = "400", nullable = true)
    private Integer errorCode;

    @Schema(description = "Error message for failed requests", example = "Recipe not found", nullable = true)
    private String errorMessage;

    public static<T> ProjectApiResponse<T> success(T data) {
        return ProjectApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static<T> ProjectApiResponse<T> error(Integer errorCode, String message) {
        return ProjectApiResponse.<T>builder()
                .errorCode(errorCode)
                .errorMessage(message)
                .success(false)
                .build();
    }

    public static<T> ProjectApiResponse<T> error(String message) {
        return ProjectApiResponse.<T>builder()
                .errorMessage(message)
                .success(false)
                .build();
    }
}
