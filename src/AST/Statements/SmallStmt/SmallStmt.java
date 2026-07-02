package AST.Statements.SmallStmt;

import AST.ASTNode;

public abstract class SmallStmt extends ASTNode {
    protected SmallStmt(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}