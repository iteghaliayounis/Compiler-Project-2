package AstHtml;

import java.util.List;


public class IncludeNode extends StatementNode {

    private final String template;
    private final String alias;

    public IncludeNode(String template, String alias, int line, int column) {
        super(line, column);
        this.template = template;
        this.alias = alias;
    }

    public String getTemplate() { return template; }
    public String getAlias() { return alias; }
    public boolean hasAlias() { return alias != null; }

    @Override
    public String name() { return "IncludeNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (\"" + template + "\"" + (hasAlias() ? " as " + alias : "") + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
