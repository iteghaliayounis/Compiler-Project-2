package AstHtml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class CallNode extends ExpressionNode {

    private final ExpressionNode callee;
    private final List<ExpressionNode> arguments = new ArrayList<>();

    private final Map<String, ExpressionNode> namedArguments = new LinkedHashMap<>();

    public CallNode(ExpressionNode callee, int line, int column) {
        super(line, column);
        this.callee = callee;
    }

    public void addArgument(ExpressionNode arg) {
        if (arg != null) arguments.add(arg);
    }


    public void addNamedArgument(String name, ExpressionNode value) {
        if (name != null && value != null) namedArguments.put(name, value);
    }

    public ExpressionNode getCallee() { return callee; }
    public List<ExpressionNode> getArguments() { return arguments; }
    public Map<String, ExpressionNode> getNamedArguments() { return namedArguments; }

    @Override
    public String name() { return "CallNode"; }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.add(callee);
        all.addAll(arguments);
        all.addAll(namedArguments.values());
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        List<String> parts = new ArrayList<>();
        for (ExpressionNode a : arguments) parts.add(a.toString());
        for (Map.Entry<String, ExpressionNode> e : namedArguments.entrySet()) {
            parts.add(e.getKey() + "=" + e.getValue());
        }
        return callee + "(" + String.join(", ", parts) + ")";
    }
}