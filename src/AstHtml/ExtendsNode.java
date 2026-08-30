package AstHtml;

import java.util.List;

public class ExtendsNode extends StatementNode {

    private final String parentTemplate;

    public ExtendsNode(String parentTemplate, int line, int column) {
        super(line, column);
        this.parentTemplate = parentTemplate;
    }

    public String getParentTemplate() { return parentTemplate; }

    @Override
    public String name() { return "ExtendsNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (\"" + parentTemplate + "\")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
