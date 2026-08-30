package AstHtml;

import java.util.ArrayList;
import java.util.List;


public class VoidElementNode extends HtmlNode {

    private final String tagName;
    private final boolean isVoidBySpec;
    private final boolean isSelfClosing;
    private final List<AttributeNode> attributes = new ArrayList<>();
    private final List<StyleAttributeNode> styleAttributes = new ArrayList<>();

    public VoidElementNode(String tagName, boolean isVoidBySpec, boolean isSelfClosing,
                           int line, int column) {
        super(line, column);
        this.tagName = tagName;
        this.isVoidBySpec = isVoidBySpec;
        this.isSelfClosing = isSelfClosing;
    }

    public void addAttribute(AttributeNode attr) { if (attr != null) attributes.add(attr); }
    public void addStyleAttribute(StyleAttributeNode attr) { if (attr != null) styleAttributes.add(attr); }

    public String getTagName() { return tagName; }
    public boolean isVoidBySpec() { return isVoidBySpec; }
    public boolean isSelfClosing() { return isSelfClosing; }
    public List<AttributeNode> getAttributes() { return attributes; }
    public List<StyleAttributeNode> getStyleAttributes() { return styleAttributes; }

    @Override
    public String name() { return "VoidElementNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (<" + tagName + (isSelfClosing ? "/" : "") + ">)";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(attributes);
        all.addAll(styleAttributes);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "<" + tagName + (isSelfClosing ? "/" : "") + ">"; }
}
