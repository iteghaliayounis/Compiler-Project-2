package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * عنصر HTML حاوي (مع تاغ فتح وإغلاق): <div>...</div>
 */
public class ElementNode extends HtmlNode {

    private final String tagName;
    private final List<AttributeNode> attributes = new ArrayList<>();
    private final List<StyleAttributeNode> styleAttributes = new ArrayList<>();
    private final List<AstNode> children = new ArrayList<>();

    public ElementNode(String tagName, int line, int column) {
        super(line, column);
        this.tagName = tagName;
    }

    public void addAttribute(AttributeNode attr) { if (attr != null) attributes.add(attr); }
    public void addStyleAttribute(StyleAttributeNode attr) { if (attr != null) styleAttributes.add(attr); }
    public void addChild(AstNode node) { if (node != null) children.add(node); }

    public String getTagName() { return tagName; }
    public List<AttributeNode> getAttributes() { return attributes; }
    public List<StyleAttributeNode> getStyleAttributes() { return styleAttributes; }
    public List<AstNode> getChildren() { return children; }

    public AttributeNode getAttribute(String name) {
        for (AttributeNode attr : attributes) {
            if (attr.getName().equalsIgnoreCase(name)) return attr;
        }
        return null;
    }

    public boolean isVoidBySpec() {
        switch (tagName.toLowerCase()) {
            case "area": case "base": case "br": case "col": case "embed":
            case "hr": case "img": case "input": case "link": case "meta":
            case "param": case "source": case "track": case "wbr":
                return true;
            default: return false;
        }
    }

    @Override
    public String name() { return "ElementNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (<" + tagName + ">)";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(attributes);
        all.addAll(styleAttributes);
        all.addAll(children);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "<" + tagName + ">"; }
}
