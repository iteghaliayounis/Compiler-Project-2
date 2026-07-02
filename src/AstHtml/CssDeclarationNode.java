package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * تعريف CSS:  property: value [!important]
 * مثال:  color: red !important
 *        background: linear-gradient(45deg, rgba(0,0,0,0.5), blue)
 */
public class CssDeclarationNode extends CssNode {

    private final String property;
    private final List<CssValueNode> values = new ArrayList<>();
    private final boolean important;

    public CssDeclarationNode(String property, boolean important, int line, int column) {
        super(line, column);
        this.property = property;
        this.important = important;
    }

    public void addValue(CssValueNode value) { if (value != null) values.add(value); }

    public String getProperty() { return property; }
    public List<CssValueNode> getValues() { return values; }
    public boolean isImportant() { return important; }

    @Override
    public String name() { return "CssDeclarationNode"; }

    @Override
    public String label() {
        StringBuilder sb = new StringBuilder();
        sb.append("[line=").append(getLine()).append("] (").append(property).append(": ");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(values.get(i));
        }
        if (important) sb.append(" !important");
        sb.append(")");
        return sb.toString();
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(values);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(property).append(": ");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(values.get(i));
        }
        if (important) sb.append(" !important");
        return sb.toString();
    }
}
