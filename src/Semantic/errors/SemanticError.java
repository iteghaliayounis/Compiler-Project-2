package Semantic.errors;

public abstract class SemanticError {
    private final String message;
    private final int    line;

    protected SemanticError(String message, int line) {
        this.message = message;
        this.line    = line;
    }

    public String getMessage() { return message; }
    public int    getLine()    { return line;    }

    @Override
    public String toString() {
        return message + (line > 0 ? " (line " + line + ")" : "");
    }
}