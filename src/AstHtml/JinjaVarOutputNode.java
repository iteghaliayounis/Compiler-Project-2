package AstHtml;

import java.util.List;

/**
 * {{ expression }}
 */
public class JinjaVarOutputNode extends StatementNode {

    private final ExpressionNode expression;

    public JinjaVarOutputNode(ExpressionNode expression, int line, int column) {
        super(line, column);
        this.expression = expression;
    }

    public ExpressionNode getExpression() { return expression; }

    @Override
    public String name() { return "JinjaVarOutputNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] ({{ " + expression + " }})";
    }

    @Override
    public List<AstNode> children() { return list(expression); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
