package AST.Target;

import AST.ASTNode;

public class TargetCall  extends Target{
    public ASTNode callChain;

    public TargetCall(ASTNode callChain, int lineNumber) {
        super("TargetCall", lineNumber);
        this.callChain = callChain;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + callChain.toString(indent + 1);
    }
}
