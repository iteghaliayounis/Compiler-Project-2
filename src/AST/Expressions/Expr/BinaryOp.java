package AST.Expressions.Expr;

import AST.ASTNode;

public class BinaryOp extends Expr {

    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public BinaryOp(ASTNode left, String operator, ASTNode right, int lineNumber) {
        super("BinaryOp", lineNumber);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRight() {
        return right;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(indent));
        sb.append(indent(indent + 1)).append("operator: ").append(operator).append("\n");
        sb.append(indent(indent + 1)).append("left:\n").append(left.toString(indent + 2));
        sb.append(indent(indent + 1)).append("right:\n").append(right.toString(indent + 2));
        return sb.toString();
    }
}
