package AstHtml;

/**
 * قاعدة مجردة لكل التعابير (expressions) في Jinja.
 * أي شيء يُنتج قيمة.
 */
public abstract class ExpressionNode extends AstNode {

    private String inferredType;  // النوع المستنتج بعد type checking

    protected ExpressionNode(int line, int column) {
        super(line, column);
    }

    public String getInferredType() { return inferredType; }
    public void setInferredType(String inferredType) { this.inferredType = inferredType; }

    @Override
    public String label() {
        return "[line=" + getLine() + "]";
    }
}
