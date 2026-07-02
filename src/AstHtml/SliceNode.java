package AstHtml;

import java.util.List;

/**
 * slicing: array[start:stop:step]
 */
public class SliceNode extends ExpressionNode {

    private final ExpressionNode array;
    private final ExpressionNode start;
    private final ExpressionNode stop;
    private final ExpressionNode step;

    public SliceNode(ExpressionNode array, ExpressionNode start, ExpressionNode stop,
                     ExpressionNode step, int line, int column) {
        super(line, column);
        this.array = array;
        this.start = start;
        this.stop = stop;
        this.step = step;
    }

    public ExpressionNode getArray() { return array; }
    public ExpressionNode getStart() { return start; }
    public ExpressionNode getStop() { return stop; }
    public ExpressionNode getStep() { return step; }

    @Override
    public String name() { return "SliceNode"; }

    @Override
    public List<AstNode> children() { return list(array, start, stop, step); }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String s = (start == null ? "" : start.toString());
        String e = (stop == null ? "" : stop.toString());
        String st = (step == null ? "" : ":" + step);
        return array + "[" + s + ":" + e + st + "]";
    }
}
