package AST.Literal;

public class IntegerLiteral extends LiteralAtom {
    public int value;

    public IntegerLiteral(int value, int lineNumber) {
        super("IntegerLiteral", lineNumber);
        this.value = value;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent).replace("\n", ": " + value + "\n");
    }
}