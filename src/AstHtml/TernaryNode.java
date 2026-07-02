package AstHtml;

import java.util.List;

/**
 * تعبير شرطي (ternary): value if condition else alternative
 */
public class TernaryNode extends ExpressionNode {

    private final ExpressionNode value;
    private final ExpressionNode condition;
    private final ExpressionNode alternative;

    public TernaryNode(ExpressionNode value, ExpressionNode condition, ExpressionNode alternative,
                       int line, int column) {
        super(line, column);
        this.value = value;
        this.condition = condition;
        this.alternative = alternative;
    }

    public ExpressionNode getValue() { return value; }
    public ExpressionNode getCondition() { return condition; }
    public ExpressionNode getAlternative() { return alternative; }

    @Override
    public String name() { return "TernaryNode"; }

    @Override
    public List<AstNode> children() { return list(value, condition, alternative); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return value + " if " + condition + " else " + alternative;
    }
}
