package AstHtml;

/**
 * <script> ... JavaScript ... </script>
 * المحتوى يخزن كـ raw text بدون parsing.
 */
public class ScriptElementNode extends HtmlNode {

    private final String rawContent;

    public ScriptElementNode(String rawContent, int line, int column) {
        super(line, column);
        this.rawContent = rawContent;
    }

    public String getRawContent() { return rawContent; }
    public boolean hasContent() { return rawContent != null && !rawContent.isEmpty(); }

    @Override
    public String name() { return "ScriptElementNode"; }

    @Override
    public String label() {
        if (rawContent == null) return "[line=" + getLine() + "] (empty)";
        String preview = rawContent.length() > 40 ? rawContent.substring(0, 37) + "..." : rawContent;
        return "[line=" + getLine() + "] (\"" + preview + "\")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "<script>"; }
}
