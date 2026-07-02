package AST.Arg;

import AST.ASTNode;

public class ExprArg extends Arg {

    public ASTNode expr;

    public ExprArg(ASTNode expr, int line) {
        super("ExprArg", line);
        this.expr = expr;
    }

    @Override
    public String toString(int indent) {
        if (expr == null)
            return formatNode(indent) + nodeName + " (null)\n";

        return formatNode(indent) + expr.toString(indent + 1);
    }
}