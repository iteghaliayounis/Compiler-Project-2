package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * <style> ... CSS ... </style>
 */
public class StyleElementNode extends HtmlNode {

    private final List<CssNode> statements = new ArrayList<>();

    public StyleElementNode(int line, int column) {
        super(line, column);
    }

    public void addStatement(CssNode stmt) {
        if (stmt != null) statements.add(stmt);
    }

    public List<CssNode> getStatements() { return statements; }

    @Override
    public String name() { return "StyleElementNode"; }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(statements);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "<style>"; }
}
