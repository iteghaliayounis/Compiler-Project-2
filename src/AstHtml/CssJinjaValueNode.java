package AstHtml;

import java.util.List;

/**
 * قيمة CSS عبارة عن تعبير Jinja.
 * مثال:  color: {{ theme.color }};
 */
public class CssJinjaValueNode extends CssValueNode {

    private final ExpressionNode expression;

    public CssJinjaValueNode(ExpressionNode expression, int line, int column) {
        super(line, column);
        this.expression = expression;
    }

    public ExpressionNode getExpression() { return expression; }

    @Override
    public String name() { return "CssJinjaValueNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] ({{ " + expression + " }})";
    }

    @Override
    public List<AstNode> children() { return list(expression); }

    @Override
    public String toCssString() { return "{{ " + expression + " }}"; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return toCssString(); }
}
