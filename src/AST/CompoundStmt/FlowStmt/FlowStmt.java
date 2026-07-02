package AST.CompoundStmt.FlowStmt;

import AST.CompoundStmt.CompoundStmt;

public abstract class FlowStmt extends CompoundStmt {

    protected FlowStmt(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}
