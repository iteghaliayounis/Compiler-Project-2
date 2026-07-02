package AST.Statements;

import AST.ASTNode;

public class SimpleStmt extends Statement {

    public ASTNode smallStmt;

    public SimpleStmt(ASTNode smallStmt, int lineNumber) {
        super("SimpleStmt", lineNumber);
        this.smallStmt = smallStmt;
    }


    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        if (smallStmt != null) {
            sb.append(smallStmt.toString(indent + 1));
        }
        return sb.toString();
    }
}