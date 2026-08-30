package AstHtml;


public abstract class CssValueNode extends CssNode {

    protected CssValueNode(int line, int column) {
        super(line, column);
    }


    public abstract String toCssString();
}
