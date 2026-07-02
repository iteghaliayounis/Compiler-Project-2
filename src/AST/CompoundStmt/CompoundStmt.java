package AST.CompoundStmt;


import AST.Statements.Statement;

public abstract class CompoundStmt extends Statement {

    protected CompoundStmt(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}