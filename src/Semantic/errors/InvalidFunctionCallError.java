package Semantic.errors;

public class InvalidFunctionCallError extends SemanticError {

    public InvalidFunctionCallError(String typeName, int line, String source) {
        super("TypeError: '" + typeName + "' object is not callable", line, source);
    }
}