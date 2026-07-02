package AST.Expressions.Expr;


import AST.GeneratorExpr.GenExpr;

public class GeneratorExpr extends Expr {
    public GenExpr genExpr;

    public GeneratorExpr(GenExpr genExpr, int lineNumber) {
        super("GeneratorExpr", lineNumber);
        this.genExpr = genExpr;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + genExpr.toString(indent + 1);
    }
}
