package app.recipe.cookbook.common.exception;

import app.recipe.cookbook.common.DomainLogicException;

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
