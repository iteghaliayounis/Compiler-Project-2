package Semantic.errors;

public abstract class SemanticError {
    private final String message;
    private final int    line;
    private final String source;

    protected SemanticError(String message, int line, String source) {
        this.message = message;
        this.line    = line;
        this.source  = source;
    }

    public String getMessage() { return message; }
    public int    getLine()    { return line;    }

    @Override
    public String toString() {

        return "[" + source + "] " + message + (line > 0 ? " (line " + line + ")" : "");
    }
}