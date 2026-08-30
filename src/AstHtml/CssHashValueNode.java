package AstHtml;


public class CssHashValueNode extends CssValueNode {

    private final String hash;
    private final boolean isColor;

    public CssHashValueNode(String hash, int line, int column) {
        super(line, column);
        this.hash = hash;
        String h = hash.substring(1);
        this.isColor = (h.length() == 3 || h.length() == 6) && h.matches("[0-9a-fA-F]+");
    }

    public String getHash() { return hash; }
    public boolean isColor() { return isColor; }
    public boolean isIdSelector() { return !isColor; }

    @Override
    public String name() { return "CssHashValueNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + hash + (isColor ? " color" : " id") + ")";
    }

    @Override
    public String toCssString() { return hash; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return hash; }
}
