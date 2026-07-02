package AstHtml;

/**
 * identifier CSS (keyword value).
 * مثال: red, auto, flex, solid, border-box, none
 */
public class CssIdentValueNode extends CssValueNode {

    private final String identifier;

    public CssIdentValueNode(String identifier, int line, int column) {
        super(line, column);
        this.identifier = identifier;
    }

    public String getIdentifier() { return identifier; }

    @Override
    public String name() { return "CssIdentValueNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + identifier + ")";
    }

    @Override
    public String toCssString() { return identifier; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return identifier; }
}
