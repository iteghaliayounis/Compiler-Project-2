package AstHtml;

/**
 * قاعدة مجردة لقيم CSS.
 */
public abstract class CssValueNode extends CssNode {

    protected CssValueNode(int line, int column) {
        super(line, column);
    }

    /** القيمة النصية للـ code generation. */
    public abstract String toCssString();
}
