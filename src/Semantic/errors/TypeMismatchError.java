package Semantic.errors;


public class TypeMismatchError extends SemanticError {


    public TypeMismatchError(String fullMessage, int line, String source) {
        super(fullMessage, line, source);
    }


    public static TypeMismatchError annotationMismatch(String expected, String actual, int line, String source) {
        return new TypeMismatchError("TypeError: expected " + expected + ", got " + actual, line, source);
    }

    public static TypeMismatchError filterTypeMismatch(String filterName, String expected, String actual,
                                                       String templateName, int line) {
        String message = "TypeError: filter '" + filterName + "' expects " + expected + ", got " + actual
                + " in template '" + templateName + "'";
        return new TypeMismatchError(message, line, "FLASK-BRIDGE");
    }
}
