package AstHtml;

import java.util.List;

/**
 * عملية أحادية: OP expr  (مثل: -a, not a)
 */
public class UnaryOpNode extends ExpressionNode {

    public enum Operator {
        NEG("-"), NOT("not");
        public final String symbol;
        Operator(String s) { this.symbol = s; }
    }

    private final Operator operator;
    private final ExpressionNode operand;

    public UnaryOpNode(Operator operator, ExpressionNode operand, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() { return operator; }
    public ExpressionNode getOperand() { return operand; }

    @Override
    public String name() { return "UnaryOpNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + operator.symbol + ")";
    }

    @Override
    public List<AstNode> children() { return list(operand); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return operator.symbol + " " + operand;
    }
}
