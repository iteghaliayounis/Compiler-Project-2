package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * استدعاء (function/macro call): name(arg1, arg2, ...)
 */
public class CallNode extends ExpressionNode {

    private final ExpressionNode callee;
    private final List<ExpressionNode> arguments = new ArrayList<>();

    public CallNode(ExpressionNode callee, int line, int column) {
        super(line, column);
        this.callee = callee;
    }

    public void addArgument(ExpressionNode arg) {
        if (arg != null) arguments.add(arg);
    }

    public ExpressionNode getCallee() { return callee; }
    public List<ExpressionNode> getArguments() { return arguments; }

    @Override
    public String name() { return "CallNode"; }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.add(callee);
        all.addAll(arguments);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return callee + "(" + String.join(", ",
                arguments.stream().map(Object::toString).toList()) + ")";
    }
}
