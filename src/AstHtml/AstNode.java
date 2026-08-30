package AstHtml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AstNode {

    private final int line;
    private final int column;
    private String sourceFile;

    protected AstNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }


    public abstract String name();

    public String label() {
        return "[line=" + line + "]";
    }


    public List<AstNode> children() {
        return Collections.emptyList();
    }


    public abstract <T> T accept(AstVisitor<T> visitor);


    public String toTreeString() {
        StringBuilder sb = new StringBuilder();
        buildTree(sb, "", true);
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    @Override
    public String toString() {
        return toTreeString();
    }

    private void buildTree(StringBuilder sb, String prefix, boolean isLast) {
        sb.append(prefix);
        sb.append(isLast ? "└── " : "├── ");
        sb.append(name());

        String lbl = label();
        if (lbl != null && !lbl.isEmpty()) {
            sb.append(" ").append(lbl);
        }
        sb.append("\n");

        List<AstNode> kids = children();
        String childPrefix = prefix + (isLast ? "    " : "│   ");
        for (int i = 0; i < kids.size(); i++) {
            kids.get(i).buildTree(sb, childPrefix, i == kids.size() - 1);
        }
    }


    protected static List<AstNode> list(Object... items) {
        List<AstNode> result = new ArrayList<>();
        for (Object o : items) {
            if (o instanceof AstNode) {
                result.add((AstNode) o);
            } else if (o instanceof List) {
                for (Object inner : (List<?>) o) {
                    if (inner instanceof AstNode) {
                        result.add((AstNode) inner);
                    }
                }
            }
        }
        return result;
    }
}
