package Semantic.errors;

public class InvalidAttributeAccessError extends SemanticError {

    public InvalidAttributeAccessError(String type, String attribute, int line, String source) {
        super("AttributeError: '" + type + "' object has no attribute '" + attribute + "'",
                line, source);
    }
}