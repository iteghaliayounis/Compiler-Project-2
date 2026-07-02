package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * {% block name %} ... {% endblock %}
 */
public class BlockNode extends StatementNode {

    private final String name;
    private final List<AstNode> body = new ArrayList<>();

    public BlockNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    public String getName() { return name; }
    public List<AstNode> getBody() { return body; }
    public void addBodyItem(AstNode node) { if (node != null) body.add(node); }

    @Override
    public String name() { return "BlockNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (name=\"" + name + "\")";
    }

    @Override
    public List<AstNode> children() { return body; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
