package AstHtml;

/**
 * selector CSS (متل "div.foo > p + span:nth-child(odd)").
 * نحتفظ فيه كـ string للـ code generation.
 */
public class CssSelectorNode extends CssNode {

    private final String selector;

    public CssSelectorNode(String selector, int line, int column) {
        super(line, column);
        this.selector = selector;
    }

    public String getSelector() { return selector; }

    @Override
    public String name() { return "CssSelectorNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (" + selector + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return selector; }
}
