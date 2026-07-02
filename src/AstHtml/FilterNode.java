package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * تطبيق filter: expression | filterName(args)
 */
public class FilterNode extends ExpressionNode {

    private final ExpressionNode operand;
    private final String filterName;
    private final List<ExpressionNode> arguments = new ArrayList<>();

    public FilterNode(ExpressionNode operand, String filterName, int line, int column) {
        super(line, column);
        this.operand = operand;
        this.filterName = filterName;
    }

    public void addArgument(ExpressionNode arg) {
        if (arg != null) arguments.add(arg);
    }

    public ExpressionNode getOperand() { return operand; }
    public String getFilterName() { return filterName; }
    public List<ExpressionNode> getArguments() { return arguments; }

    @Override
    public String name() { return "FilterNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (|" + filterName + ")";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.add(operand);
        all.addAll(arguments);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return operand + " | " + filterName + (arguments.isEmpty() ? "" : "(" + arguments + ")");
    }
}
