package AST.Expressions.Expr;

import AST.ASTNode;

import java.util.List;

public class ComparisonExpr extends Expr {

    public ASTNode first;
    public List<String> operators;
    public List<ASTNode> rest;

    public ComparisonExpr(ASTNode first,
                          List<String> operators,
                          List<ASTNode> rest,
                          int line) {
        super("ComparisonExpr", line);
        this.first = first;
        this.operators = operators;
        this.rest = rest;
    }
    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        sb.append(first.toString(indent + 1));
        for (int i = 0; i < rest.size(); i++) {
            sb.append(indent(indent + 1))
                    .append(operators.get(i)).append("\n");
            sb.append(rest.get(i).toString(indent + 2));
        }
        return sb.toString();
    }
}