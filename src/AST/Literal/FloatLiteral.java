package AST.Literal;


public class FloatLiteral extends LiteralAtom {
    public double value;

    public FloatLiteral(double value, int lineNumber) {
        super("FloatLiteral", lineNumber);
        this.value = value;
    }


    @Override
    public String toString(int indent) {
        return formatNode(indent) + nodeName + ": " + value + "\n";
    }
}