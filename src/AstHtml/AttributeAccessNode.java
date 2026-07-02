package AstHtml;

import java.util.List;

/**
 * وصول لخاصية: object.attribute
 */
public class AttributeAccessNode extends ExpressionNode {

    private final ExpressionNode object;
    private final String attributeName;

    public AttributeAccessNode(ExpressionNode object, String attributeName,
                               int line, int column) {
        super(line, column);
        this.object = object;
        this.attributeName = attributeName;
    }

    public ExpressionNode getObject() { return object; }
    public String getAttributeName() { return attributeName; }

    @Override
    public String name() { return "AttributeAccessNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (." + attributeName + ")";
    }

    @Override
    public List<AstNode> children() { return list(object); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return object + "." + attributeName;
    }
}
