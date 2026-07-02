package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * جذر شجرة الـ AST. يمثّل ملف القالب الكامل.
 */
public class TemplateNode extends AstNode {

    private final List<AstNode> children = new ArrayList<>();

    public TemplateNode(int line, int column) {
        super(line, column);
    }

    public List<AstNode> getChildren() { return children; }

    public void addChild(AstNode node) {
        if (node != null) children.add(node);
    }

    @Override
    public String name() { return "TemplateNode"; }

    @Override
    public List<AstNode> children() { return children; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
