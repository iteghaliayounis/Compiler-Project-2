package AST.Expressions.Atom;

import AST.ASTNode;

public abstract class Atom extends ASTNode {
    protected Atom(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}