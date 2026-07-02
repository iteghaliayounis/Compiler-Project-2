package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * {% for x in items %} ... {% else %} ... {% endfor %}
 */
public class ForNode extends StatementNode {

    private final List<String> targets = new ArrayList<>();
    private final ExpressionNode iterable;
    private final List<AstNode> body = new ArrayList<>();
    private List<AstNode> elseBody = null;

    public ForNode(ExpressionNode iterable, int line, int column) {
        super(line, column);
        this.iterable = iterable;
    }

    public void addTarget(String target) { targets.add(target); }
    public void addBodyItem(AstNode node) { if (node != null) body.add(node); }
    public void setElseBody(List<AstNode> elseBody) { this.elseBody = elseBody; }

    public List<String> getTargets() { return targets; }
    public ExpressionNode getIterable() { return iterable; }
    public List<AstNode> getBody() { return body; }
    public List<AstNode> getElseBody() { return elseBody; }
    public boolean hasElse() { return elseBody != null; }

    @Override
    public String name() { return "ForNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (targets=" + targets + ")";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.add(iterable);
        all.addAll(body);
        if (elseBody != null) all.addAll(elseBody);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
