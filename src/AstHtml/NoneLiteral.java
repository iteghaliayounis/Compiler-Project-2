package AstHtml;

import java.util.List;

/**
 * none / null
 */
public class NoneLiteral extends ExpressionNode {

    public NoneLiteral(int line, int column) {
        super(line, column);
    }

    @Override
    public String name() { return "NoneLiteral"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (none)";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "none"; }
}
