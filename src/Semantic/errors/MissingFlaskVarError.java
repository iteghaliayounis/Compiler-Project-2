package Semantic.errors;

public class MissingFlaskVarError extends SemanticError {
    public MissingFlaskVarError(String varName, String templateName, int line) {

        super("jinja2.exceptions.UndefinedError: '" + varName
                + "' is undefined in template '" + templateName + "'", line, "FLASK-BRIDGE");
    }
}