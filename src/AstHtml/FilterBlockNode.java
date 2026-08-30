package AstHtml;

import java.util.ArrayList;
import java.util.List;


public class FilterBlockNode extends StatementNode {

    private final String filterName;
    private final List<ExpressionNode> filterArgs = new ArrayList<>();
    private final List<AstNode> body = new ArrayList<>();

    public FilterBlockNode(String filterName, int line, int column) {
        super(line, column);
        this.filterName = filterName;
    }

    public void addFilterArg(ExpressionNode arg) { if (arg != null) filterArgs.add(arg); }
    public void addBodyItem(AstNode node) { if (node != null) body.add(node); }

    public String getFilterName() { return filterName; }
    public List<ExpressionNode> getFilterArgs() { return filterArgs; }
    public List<AstNode> getBody() { return body; }

    @Override
    public String name() { return "FilterBlockNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (filter=" + filterName + ", args=" + filterArgs + ")";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(filterArgs);
        all.addAll(body);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
