package AST.Literal;

public class BoolLiteral extends LiteralAtom{
    public boolean value;

    public BoolLiteral(boolean value, int lineNumber) {
        super("BoolLiteral", lineNumber);
        this.value = value;
    }


    @Override
    public String toString(int indent) {
        return formatNode(indent) + nodeName + ": " + value + "\n";
    }
}