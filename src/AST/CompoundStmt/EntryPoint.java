package AST.CompoundStmt;

import AST.ASTNode;

import java.util.List;

public class EntryPoint extends ASTNode {

    public List<ASTNode> body;

    public EntryPoint(List<ASTNode> body, int lineNumber) {
        super("EntryPoint", lineNumber);
        this.body = body;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (ASTNode stmt : body) {
            if (stmt != null)
                sb.append(stmt.toString(indent + 1));
        }
        return sb.toString();
    }
}