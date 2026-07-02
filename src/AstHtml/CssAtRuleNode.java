package AstHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * At-rule: @media, @keyframes, @import, @font-face
 */
public class CssAtRuleNode extends CssNode {

    private final String keyword;
    private final String prelude;
    private final boolean hasBlock;
    private final List<CssNode> body = new ArrayList<>();

    public CssAtRuleNode(String keyword, String prelude, boolean hasBlock,
                         int line, int column) {
        super(line, column);
        this.keyword = keyword;
        this.prelude = prelude == null ? "" : prelude;
        this.hasBlock = hasBlock;
    }

    public void addBodyStatement(CssNode stmt) { if (stmt != null) body.add(stmt); }

    public String getKeyword() { return keyword; }
    public String getPrelude() { return prelude; }
    public boolean hasBlock() { return hasBlock; }
    public List<CssNode> getBody() { return body; }

    @Override
    public String name() { return "CssAtRuleNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (@" + keyword + (prelude.isEmpty() ? "" : " " + prelude) + ")";
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> all = new ArrayList<>();
        all.addAll(body);
        return all;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() { return "@" + keyword; }
}
