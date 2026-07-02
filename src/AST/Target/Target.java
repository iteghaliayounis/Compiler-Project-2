package AST.Target;

import AST.ASTNode;

public abstract class Target extends ASTNode {
    protected Target(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}
