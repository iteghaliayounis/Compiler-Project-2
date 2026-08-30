package AstHtml;

import java.util.ArrayList;
import java.util.List;

public class RawNode extends StatementNode {

    private final List<AstNode> body = new ArrayList<>();

    public RawNode(int line, int column) {
        super(line, column);
    }

    public void addBodyItem(AstNode node) { if (node != null) body.add(node); }
    public List<AstNode> getBody() { return body; }

    @Override
    public String name() { return "RawNode"; }

    @Override
    public List<AstNode> children() { return body; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
