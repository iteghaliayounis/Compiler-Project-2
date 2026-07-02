package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * قاعدة CSS كاملة:
 *   selector, selector { declaration; declaration; }
 */
public class CssRuleSetNode extends CssNode {

    private final List<CssSelectorNode> selectors = new ArrayList<>();
    private final List<CssDeclarationNode> declarations = new ArrayList<>();

    public CssRuleSetNode(int line, int column) {
        super(line, column);
    }

    public void addSelector(CssSelectorNode selector) { if (selector != null) selectors.add(selector); }
    public void addDeclaration(CssDeclarationNode decl) { if (decl != null) declarations.add(decl); }

    public List<CssSelectorNode> getSelectors() { return selectors; }
    public List<CssDeclarationNode> getDeclarations() { return declarations; }

    @Override
    public String name() { return "CssRuleSetNode"; }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(selectors);
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
        for (int i = 0; i < selectors.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(selectors.get(i));
        }
        return sb.toString();
    }
}
