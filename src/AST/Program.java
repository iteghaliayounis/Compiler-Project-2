
package AST;

import java.util.List;

public class Program extends ASTNode {

    public List<ASTNode> elements;

    public Program(List<ASTNode> elements) {
        this(elements, 1);
    }

    public Program(List<ASTNode> elements, int lineNumber) {
        super("Program", lineNumber);
        this.elements = elements;
    }

    public List<ASTNode> getElements() { return elements; }

    @Override
    public String toString(int indent) {
        StringBuilder sb;
        if (indent == 0) {
            sb = new StringBuilder("Program\n");
        } else {
            sb = new StringBuilder(formatNode(indent));
        }
        for (ASTNode n : elements) {
            sb.append(n.toString(indent + 1));
        }
        return sb.toString();
    }
}