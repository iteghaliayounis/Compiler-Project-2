package AST.Expressions.Expr;

import AST.ASTNode;

public abstract class Expr extends ASTNode {

    protected Expr(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}
