package AstHtml;


public abstract class ExpressionNode extends AstNode {

    private String inferredType;

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
