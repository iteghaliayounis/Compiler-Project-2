package AstHtml;

/**
 * قاعدة مجردة لكل الـ statements (اللي بتنفّذ ولا تُنتج قيمة).
 */
public abstract class StatementNode extends AstNode {

    protected StatementNode(int line, int column) {
        super(line, column);
    }
}
