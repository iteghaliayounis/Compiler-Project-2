package AstHtml;

/**
 * قاعدة مجردة لعناصر HTML.
 */
public abstract class HtmlNode extends AstNode {

    protected HtmlNode(int line, int column) {
        super(line, column);
    }
}
