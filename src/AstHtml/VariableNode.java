package AstHtml;

import java.util.List;

/**
 * متغير (identifier reference).
 */
public class VariableNode extends ExpressionNode {

    private final String name;

    public VariableNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public String name() { return "VariableNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + name + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return name; }
}
