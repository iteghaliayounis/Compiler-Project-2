package AST.ListDictPair;

import AST.ASTNode;

import java.util.List;

public class ListLiteral extends ASTNode {
    public List<ASTNode> elements;

    public ListLiteral(List<ASTNode> elements, int lineNumber) {
        super("ListLiteral", lineNumber);
        this.elements = elements;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (ASTNode n : elements) sb.append(n.toString(indent + 1));
        return sb.toString();
    }
}
