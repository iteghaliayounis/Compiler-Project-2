package AstHtml;

import java.util.List;

/**
 * true / false
 */
public class BooleanLiteral extends ExpressionNode {

    private final boolean value;

    public BooleanLiteral(boolean value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    public boolean getValue() { return value; }

    @Override
    public String name() { return "BooleanLiteral"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + value + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
