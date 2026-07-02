package AST.Expressions.CallSuffixes;

import AST.ASTNode;

public class IndexAccess extends CallSuffix {

    public ASTNode index;

    public IndexAccess(ASTNode index, int line) {
        super("IndexAccess", line);
        this.index = index;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + index.toString(indent + 1);
    }
}