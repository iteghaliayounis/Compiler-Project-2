package AstHtml;

import java.util.ArrayList;
import java.util.List;

public class StyleAttributeNode extends AstNode {

    private final List<CssDeclarationNode> declarations = new ArrayList<>();

    public StyleAttributeNode(int line, int column) {
        super(line, column);
    }

    public void addDeclaration(CssDeclarationNode decl) {
        if (decl != null) declarations.add(decl);
    }

    public List<CssDeclarationNode> getDeclarations() { return declarations; }

    @Override
    public String name() { return "StyleAttributeNode"; }

    @Override
    public String label() {
        StringBuilder sb = new StringBuilder();
        sb.append("[line=").append(getLine()).append("] (\"");
        for (int i = 0; i < declarations.size(); i++) {
            if (i > 0) sb.append("; ");
            sb.append(declarations.get(i));
        }
        sb.append("\")");
        return sb.toString();
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(declarations);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("style=\"");
        for (int i = 0; i < declarations.size(); i++) {
            if (i > 0) sb.append("; ");
            sb.append(declarations.get(i));
        }
        sb.append("\"");
        return sb.toString();
    }
}
