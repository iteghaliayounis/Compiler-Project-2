package AstHtml;

/**
 * قيمة نصية CSS (entre quotes).
 * مثال: 'Helvetica Neue', "Arial"
 */
public class CssStringValueNode extends CssValueNode {

    private final String value;

    public CssStringValueNode(String rawValue, int line, int column) {
        super(line, column);
        this.value = rawValue.substring(1, rawValue.length() - 1);
    }

    public String getValue() { return value; }

    @Override
    public String name() { return "CssStringValueNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (\"" + value + "\")";
    }

    @Override
    public String toCssString() { return "\"" + value + "\""; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "\"" + value + "\""; }
}
