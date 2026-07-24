package Semantic.errors;

public class ReturnTypeMismatchError extends SemanticError {

    public ReturnTypeMismatchError(String funcName, String expectedType, String actualType, int line, String source) {
        super("TypeError: function '" + funcName + "' has inconsistent return types: expected '"
                        + expectedType + "' but got '" + actualType + "'",
                line, source);
    }
}