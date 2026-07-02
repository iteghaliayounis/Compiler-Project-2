package AST.Expressions.CallSuffixes;

import AST.ASTNode;

import java.util.List;

public class CallChainExpr extends ASTNode {

    public ASTNode base;
    public List<CallSuffix> suffixes;

    public CallChainExpr(ASTNode base, List<CallSuffix> suffixes, int line) {
        super("CallChainExpr", line);
        this.base = base;
        this.suffixes = suffixes;
    }
    public ASTNode getBase() {
        return base;
    }

    public List<CallSuffix> getSuffixes() {
        return suffixes;
    }
    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        sb.append(base.toString(indent + 1));
        for (CallSuffix s : suffixes)
            sb.append(s.toString(indent + 1));
        return sb.toString();
    }
}