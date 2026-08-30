package Semantic.errors;


public class ScopeError extends SemanticError {


    public ScopeError(String varName, int line, String source) {
        super("UnboundLocalError: local variable '" + varName
                + "' referenced before assignment", line, source);
    }


    public static ScopeError loopVarOutsideLoop(String varName, int line, String source) {
        return new ScopeError(varName, line, source) {
            @Override
            public String toString() {
                return "[" + "JINJA" + "] UnboundLocalError: '"
                        + varName + "' is not defined outside loop"
                        + (line > 0 ? " (line " + line + ")" : "");
            }
        };
    }
}
