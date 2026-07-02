package AST.CompoundStmt.FlowStmt;

import AST.ASTNode;

import java.util.List;

public class IfStmt  extends FlowStmt{
    public ASTNode condition;
    public List<ASTNode> body;

    public IfStmt(ASTNode condition, List<ASTNode> body, int lineNumber) {
        super("IfStmt", lineNumber);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        sb.append(indent(indent + 1)).append("Condition:\n").append(condition.toString(indent + 2));
        for (ASTNode stmt : body)
            sb.append(stmt.toString(indent + 1));
        return sb.toString();
    }
}