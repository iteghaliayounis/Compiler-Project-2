package AST.Literal;

public class StringLiteral extends LiteralAtom {
    public String value;

    public StringLiteral(String value, int lineNumber) {
        super("StringLiteral", lineNumber);
        this.value = value;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent).replace("\n", ": " + value + "\n");
    }
}