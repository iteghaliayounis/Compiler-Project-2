package AstHtml;


public class CssNumberValueNode extends CssValueNode {

    private final String rawValue;
    private final double number;
    private final String unit;

    public CssNumberValueNode(String rawValue, int line, int column) {
        super(line, column);
        this.rawValue = rawValue;

        int i = 0;
        while (i < rawValue.length() && (Character.isDigit(rawValue.charAt(i))
                || rawValue.charAt(i) == '.' || rawValue.charAt(i) == '-')) {
            i++;
        }
        this.number = Double.parseDouble(rawValue.substring(0, i));
        this.unit = rawValue.substring(i);
    }

    public double getNumber() { return number; }
    public String getUnit() { return unit; }
    public String getRawValue() { return rawValue; }

    @Override
    public String name() { return "CssNumberValueNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + rawValue + ")";
    }

    @Override
    public String toCssString() { return rawValue; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return rawValue; }
}
