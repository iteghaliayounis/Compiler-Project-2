package AstHtml;

/**
 * قاعدة مجردة لعقد CSS.
 */
public abstract class CssNode extends AstNode {

    protected CssNode(int line, int column) {
        super(line, column);
    }
}
