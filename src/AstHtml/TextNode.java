package AstHtml;

import java.util.List;

/**
 * نص HTML خام بين التاغات.
 */
public class TextNode extends StatementNode {

    private final String value;

    public TextNode(String value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    public String getValue() { return value; }

    @Override
    public String name() { return "TextNode"; }

    @Override
    public String label() {
        String display = value.replaceAll("\\s+", " ").trim();
        if (display.length() > 40) display = display.substring(0, 37) + "...";
        return "[line=" + getLine() + "] (\"" + display + "\")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
