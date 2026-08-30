package AstHtml;

import java.util.List;


public class BinaryOpNode extends ExpressionNode {

    public enum Operator {
        ADD("+"), SUB("-"), MUL("*"), DIV("/"), MOD("%"),
        EQ("=="), NEQ("!="), LT("<"), GT(">"), LTE("<="), GTE(">="),
        AND("and"), OR("or"),
        CONCAT("~"),
        IN("in"), NOT_IN("not in"),
        IS("is"), IS_NOT("is not");

        public final String symbol;
        Operator(String s) { this.symbol = s; }
    }

    private final Operator operator;
    private final ExpressionNode left;
    private final ExpressionNode right;

    public BinaryOpNode(Operator operator, ExpressionNode left, ExpressionNode right,
                        int line, int column) {
        super(line, column);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() { return operator; }
    public ExpressionNode getLeft() { return left; }
    public ExpressionNode getRight() { return right; }

    @Override
    public String name() { return "BinaryOpNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + operator.symbol + ")";
    }

    @Override
    public List<AstNode> children() { return list(left, right); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator.symbol + " " + right + ")";
    }
}
