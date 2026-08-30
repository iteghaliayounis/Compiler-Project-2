package AstHtml;

import java.util.List;


public class SetNode extends StatementNode {

    private final String variable;
    private final ExpressionNode value;

    public SetNode(String variable, ExpressionNode value, int line, int column) {
        super(line, column);
        this.variable = variable;
        this.value = value;
    }

    public String getVariable() { return variable; }
    public ExpressionNode getValue() { return value; }

    @Override
    public String name() { return "SetNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + variable + " = ...)";
    }

    @Override
    public List<AstNode> children() { return list(value); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
