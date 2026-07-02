package AST.Literal;

public class NoneLiteral extends LiteralAtom{

    public NoneLiteral(int lineNumber) {
        super("NoneLiteral", lineNumber);
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + nodeName + ": None\n";
    }
}