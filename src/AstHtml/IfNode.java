package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * {% if cond %} ... {% elif cond2 %} ... {% else %} ... {% endif %}
 *
 * - conditions: قائمة الشروط (الأول للـ if، الباقي للـ elif)
 * - bodies: قائمة الأجساد بنفس ترتيب الشروط
 * - elseBody: جسم الـ else (ممكن null)
 */
public class IfNode extends StatementNode {

    private final List<ExpressionNode> conditions = new ArrayList<>();
    private final List<List<AstNode>> bodies = new ArrayList<>();
    private List<AstNode> elseBody = null;

    public IfNode(int line, int column) {
        super(line, column);
    }

    public void addBranch(ExpressionNode condition, List<AstNode> body) {
        conditions.add(condition);
        bodies.add(body);
    }

    public void setElseBody(List<AstNode> elseBody) { this.elseBody = elseBody; }

    public List<ExpressionNode> getConditions() { return conditions; }
    public List<List<AstNode>> getBodies() { return bodies; }
    public List<AstNode> getElseBody() { return elseBody; }
    public boolean hasElse() { return elseBody != null; }

    @Override
    public String name() { return "IfNode"; }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        for (int i = 0; i < conditions.size(); i++) {
            all.add(conditions.get(i));
            all.addAll(bodies.get(i));
        }
        if (elseBody != null) all.addAll(elseBody);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
