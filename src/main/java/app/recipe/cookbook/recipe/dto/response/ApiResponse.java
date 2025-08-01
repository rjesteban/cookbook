package app.recipe.cookbook.recipe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;

    @Builder.Default
    private boolean success = true;

    private Integer errorCode;

    private String errorMessage;

    public static<T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static<T> ApiResponse<T> error(Integer errorCode, String message) {
        return ApiResponse.<T>builder()
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}
