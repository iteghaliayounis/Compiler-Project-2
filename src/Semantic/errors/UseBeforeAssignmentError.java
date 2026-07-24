package Semantic.errors;

public class UseBeforeAssignmentError extends SemanticError {

    public UseBeforeAssignmentError(String varName, int line, String source) {
        super("UnboundLocalError: local variable '" + varName
                + "' referenced before assignment", line, source);
    }
}