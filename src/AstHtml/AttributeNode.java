package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * خاصية HTML (attribute): name="value" أو name='value' أو name (boolean)
 *
 * القيمة ممكن تكون:
 *   - String (TAG_STRING)
 *   - Jinja expression ({{ }} داخل القيمة)
 *   - null (boolean attribute مثل disabled, checked)
 */
public class AttributeNode extends AstNode {

    private final String name;
    private final String stringValue;
    private final ExpressionNode jinjaValue;
    private final boolean isBoolean;

    // Constructor للـ string value
    public AttributeNode(String name, String stringValue, int line, int column) {
        super(line, column);
        this.name = name;
        this.stringValue = stringValue;
        this.jinjaValue = null;
        this.isBoolean = false;
    }

    // Constructor للـ jinja value
    public AttributeNode(String name, ExpressionNode jinjaValue, int line, int column) {
        super(line, column);
        this.name = name;
        this.stringValue = null;
        this.jinjaValue = jinjaValue;
        this.isBoolean = false;
    }

    // Constructor للـ boolean attribute
    public AttributeNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
        this.stringValue = null;
        this.jinjaValue = null;
        this.isBoolean = true;
    }

    public String getName() { return name; }
    public String getStringValue() { return stringValue; }
    public ExpressionNode getJinjaValue() { return jinjaValue; }
    public boolean isBoolean() { return isBoolean; }
    public boolean hasValue() { return !isBoolean; }

    @Override
    public String name() { return "AttributeNode"; }

    @Override
    public String label() {
        if (isBoolean) {
            return "[line=" + getLine() + "] " + name + " (boolean)";
        }
        if (stringValue != null) {
            return "[line=" + getLine() + "] " + name + "=\"" + stringValue + "\"";
        }
        return "[line=" + getLine() + "] " + name + "={{ " + jinjaValue + " }}";
    }

    @Override
    public List<AstNode> children() {
        return jinjaValue != null ? list(jinjaValue) : list();
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (isBoolean) return name;
        if (stringValue != null) return name + "=\"" + stringValue + "\"";
        return name + "={{ " + jinjaValue + " }}";
    }
}
