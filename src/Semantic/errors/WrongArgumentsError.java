package Semantic.errors;

public class WrongArgumentsError extends SemanticError {

    public WrongArgumentsError(String funcName, int expected, int actual, int line, String source) {
        super("TypeError: " + funcName + "() takes " + expected
                        + " positional arguments but " + actual + " were given",
                line, source);
    }
}