package AstHtml;

import java.util.List;

/**
 * نص (string literal).
 */
public class StringLiteral extends ExpressionNode {

    private final String value;

    public StringLiteral(String rawValue, int line, int column) {
        super(line, column);
        // إزالة علامات التنصيص
        this.value = rawValue.substring(1, rawValue.length() - 1);
    }

    public String getValue() { return value; }

    @Override
    public String name() { return "StringLiteral"; }

    @Override
    public String label() {
        String display = value.length() > 40 ? value.substring(0, 37) + "..." : value;
        return "[line=" + getLine() + "] (\"" + display + "\")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "\"" + value + "\""; }
}
