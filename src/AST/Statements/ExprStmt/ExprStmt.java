package AST.Statements.ExprStmt;

import AST.ASTNode;
import AST.Statements.SmallStmt.SmallStmt;

public class ExprStmt extends SmallStmt {
    public ASTNode target;
    public ASTNode value;

    public ExprStmt(ASTNode target, ASTNode value, int lineNumber) {
        super("ExprStmt", lineNumber);
        this.target = target;
        this.value = value;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        sb.append(target.toString(indent + 1));
        if (value != null) sb.append(value.toString(indent + 1));
        return sb.toString();
    }
}