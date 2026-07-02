package AST.CompoundStmt.FlowStmt;

import AST.ASTNode;

import java.util.List;

public class ForStmt extends FlowStmt {
    public String var;
    public ASTNode iterable;
    public List<ASTNode> body;

    public ForStmt(String var, ASTNode iterable, List<ASTNode> body, int lineNumber) {
        super("ForStmt", lineNumber);
        this.var = var;
        this.iterable = iterable;
        this.body = body;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent).replace("\n", " (for " + var + ")\n"));
        sb.append(indent(indent + 1)).append("Iterable:\n").append(iterable.toString(indent + 2));
        for (ASTNode stmt : body)
            sb.append(stmt.toString(indent + 1));
        return sb.toString();
    }
}