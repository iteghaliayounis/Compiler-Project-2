package AstHtml;

import java.util.ArrayList;
import java.util.List;


public class MacroNode extends StatementNode {

    public static class Param {
        public final String name;
        public final ExpressionNode defaultValue;
        public Param(String name, ExpressionNode defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
        @Override
        public String toString() {
            return defaultValue == null ? name : name + "=" + defaultValue;
        }
    }

    private final String name;
    private final List<Param> params = new ArrayList<>();
    private final List<AstNode> body = new ArrayList<>();

    public MacroNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    public void addParam(String name, ExpressionNode defaultValue) {
        params.add(new Param(name, defaultValue));
    }
    public void addBodyItem(AstNode node) { if (node != null) body.add(node); }

    public String getName() { return name; }
    public List<Param> getParams() { return params; }
    public List<AstNode> getBody() { return body; }

    @Override
    public String name() { return "MacroNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (name=" + name + ", params=" + params + ")";
    }

    @Override
    public List<AstNode> children() {
        // الـ params مع default values + body
        List<AstNode> all = new ArrayList<>();
        for (Param p : params) {
            if (p.defaultValue != null) all.add(p.defaultValue);
        }
        all.addAll(body);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
