package app.recipe.cookbook.recipe.exception;

import app.recipe.cookbook.recipe.dto.response.ProjectApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The default Exception handler for the Cookbook API: provides
 * consistent error response structure across all endpoints.
 */
@RestControllerAdvice
@Slf4j
public class DomainExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ProjectApiResponse<Void>> handleRecipeNotFoundException(RecipeNotFoundException ex) {
        log.error("Recipe not found: {}", ex.getMessage());

        final ProjectApiResponse<Void> errorResponse = ProjectApiResponse.error(ex.getCode(), ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProjectApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        final ProjectApiResponse<Void> errorResponse = ProjectApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProjectApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        final ProjectApiResponse<Void> errorResponse = ProjectApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
