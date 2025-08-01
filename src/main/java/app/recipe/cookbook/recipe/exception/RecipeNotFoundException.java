package app.recipe.cookbook.recipe.exception;

public class RecipeNotFoundException extends DomainLogicException {

    private static final int ERROR_CODE = 1;
    public RecipeNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getCode() {
        return ERROR_CODE;
    }

}
