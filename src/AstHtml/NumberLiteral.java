package AstHtml;

import java.util.List;

/**
 * عدد (int أو float).
 */
public class NumberLiteral extends ExpressionNode {

    private final String rawValue;
    private final boolean isFloat;

    public NumberLiteral(String rawValue, int line, int column) {
        super(line, column);
        this.rawValue = rawValue;
        this.isFloat = rawValue.contains(".");
    }

    public String getRawValue() { return rawValue; }
    public boolean isFloat() { return isFloat; }

    public Number getValue() {
        return isFloat ? Double.parseDouble(rawValue) : Long.parseLong(rawValue);
    }

    @Override
    public String name() { return "NumberLiteral"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + rawValue + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return rawValue; }
}
