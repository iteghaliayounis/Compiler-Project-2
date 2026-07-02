package AstHtml;

import java.util.List;

/**
 * {% import "macros.html" as forms %}
 */
public class ImportNode extends StatementNode {

    private final String template;
    private final String alias;

    public ImportNode(String template, String alias, int line, int column) {
        super(line, column);
        this.template = template;
        this.alias = alias;
    }

    public String getTemplate() { return template; }
    public String getAlias() { return alias; }

    @Override
    public String name() { return "ImportNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (\"" + template + "\" as " + alias + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
