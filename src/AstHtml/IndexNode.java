package AstHtml;

import java.util.List;

/**
 * وصول بالفهرس: array[index]
 */
public class IndexNode extends ExpressionNode {

    private final ExpressionNode array;
    private final ExpressionNode index;

    public IndexNode(ExpressionNode array, ExpressionNode index, int line, int column) {
        super(line, column);
        this.array = array;
        this.index = index;
    }

    public ExpressionNode getArray() { return array; }
    public ExpressionNode getIndex() { return index; }

    @Override
    public String name() { return "IndexNode"; }

    @Override
    public List<AstNode> children() { return list(array, index); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return array + "[" + index + "]";
    }
}
