package AST.Expressions.Atom;


import AST.ASTNode;

public class ParenExpr extends Atom {
    public ASTNode expr;

    public ParenExpr(ASTNode expr, int lineNumber) {
        super("ParenExpr", lineNumber);
        this.expr = expr;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + expr.toString(indent + 1);
    }
}