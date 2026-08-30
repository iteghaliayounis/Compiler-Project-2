package Semantic.errors;


public class DivisionByZeroError extends SemanticError {


    public DivisionByZeroError(int line, String source) {
        super("ZeroDivisionError: division by zero", line, source);
    }


    public static DivisionByZeroError moduloByZero(int line, String source) {
        return new DivisionByZeroError(line, source) {
            @Override
            public String toString() {
                return "[" + source + "] ZeroDivisionError: integer division or modulo by zero"
                        + (line > 0 ? " (line " + line + ")" : "");
            }
        };
    }
}
