package AstHtml;

import java.util.ArrayList;
import java.util.List;


public class CssFunctionValueNode extends CssValueNode {

    private final String name;
    private final List<CssValueNode> arguments = new ArrayList<>();

    public CssFunctionValueNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    public void addArgument(CssValueNode arg) { if (arg != null) arguments.add(arg); }

    public String getName() { return name; }
    public List<CssValueNode> getArguments() { return arguments; }

    @Override
    public String name() { return "CssFunctionValueNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + name + "(...))";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(arguments);
        return all;
    }

    @Override
    public String toCssString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(arguments.get(i).toCssString());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return toCssString(); }
}
