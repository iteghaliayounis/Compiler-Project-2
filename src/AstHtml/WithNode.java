package AstHtml;

import java.util.ArrayList;
import java.util.List;


public class WithNode extends StatementNode {

    public static class Assignment {
        public final String name;
        public final ExpressionNode value;
        public Assignment(String name, ExpressionNode value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return name + "=" + value;
        }
    }

    private final List<Assignment> assignments = new ArrayList<>();
    private final List<AstNode> body = new ArrayList<>();

    public WithNode(int line, int column) {
        super(line, column);
    }

    public void addAssignment(String name, ExpressionNode value) {
        assignments.add(new Assignment(name, value));
    }
    public void addBodyItem(AstNode node) { if (node != null) body.add(node); }

    public List<Assignment> getAssignments() { return assignments; }
    public List<AstNode> getBody() { return body; }

    @Override
    public String name() { return "WithNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (assignments=" + assignments + ")";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        for (Assignment a : assignments) all.add(a.value);
        all.addAll(body);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
