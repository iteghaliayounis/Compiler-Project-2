package AST;

public abstract class ASTNode {

    protected final String nodeName;
    protected final int lineNumber;

    protected ASTNode(String nodeName, int lineNumber) {
        this.nodeName = nodeName;
        this.lineNumber = lineNumber;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public abstract String toString(int indent);

    @Override
    public String toString() {
        return toString(0);
    }

    protected String indent(int n) {
        if (n <= 0) return "";
        return "   ".repeat(n - 1) + "└── ";
    }

    protected String formatNode(int indent) {
        return indent(indent)  + nodeName + " (line " + lineNumber + ")\n";
    }
}