package Semantic.errors;

public class UndefinedVarError extends SemanticError {
    public UndefinedVarError(String varName, int line) {
        super("NameError: name '" + varName + "' is not defined", line);
    }
}