package app.recipe.cookbook.common;

public abstract class DomainLogicException extends RuntimeException {

    public DomainLogicException(String message) {
        super(message);
    }

    public abstract int getCode();

}
