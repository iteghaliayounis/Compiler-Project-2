package AST.GeneratorExpr;

import AST.ASTNode;

public class GenExpr extends ASTNode {
    public ASTNode expr;
    public String var;
    public ASTNode iterable;
    public ASTNode condition;

    public GenExpr(ASTNode expr, String var, ASTNode iterable, ASTNode condition, int lineNumber) {
        super("GenExpr", lineNumber);
        this.expr = expr;
        this.var = var;
        this.iterable = iterable;
        this.condition = condition;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(
                formatNode(indent).replace("\n", " (for " + var + ")\n")
        );
        sb.append(indent(indent + 1)).append("Expression:\n").append(expr.toString(indent + 2));
        sb.append(indent(indent + 1)).append("Iterable:\n").append(iterable.toString(indent + 2));
        if (condition != null)
            sb.append(indent(indent + 1)).append("Condition:\n").append(condition.toString(indent + 2));
        return sb.toString();
    }
}