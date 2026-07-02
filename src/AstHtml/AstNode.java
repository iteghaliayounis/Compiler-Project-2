package AstHtml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * القاعدة المجردة لكل عقدة في شجرة الـ AST.
 *
 * كل subclass بيعمل override لـ:
 *   - name()     : اسم الـ node (متل "IfNode", "ElementNode")
 *   - label()    : معلومات إضافية (متل سطر + قيمة)
 *   - children() : قائمة الأبناء
 *
 * الـ toString بيولّد الشكل المرئي تلقائياً:
 *   IfNode [line=5]
 *   ├── BinaryOp [user.age > 18]
 *   └── BlockContent
 *       ├── TextNode ("Welcome!")
 *       └── ElementNode [div]
 */
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

    // ====== Methods لازم أي subclass يعمل override لها ======

    /** اسم الـ node للعرض (متل "IfNode", "ElementNode"). */
    public abstract String name();

    /** معلومات إضافية للعرض (متل "[line=5]" أو "(\"hello\")"). ممكن تكون فاضية. */
    public String label() {
        return "[line=" + line + "]";
    }

    /** قائمة الأبناء للـ tree printing. */
    public List<AstNode> children() {
        return Collections.emptyList();
    }

    /** تنفيذ الـ Visitor Pattern. */
    public abstract <T> T accept(AstVisitor<T> visitor);

    // ====== Tree printing تلقائي ======

    /**
     * يولّد الشكل المرئي للشجرة الكاملة (متبوع بـ ├── و └──).
     * مثال:
     *   └── IfNode [line=5]
     *       ├── BinaryOp [a > b]
     *       └── TextNode ("hi")
     */
    public String toTreeString() {
        StringBuilder sb = new StringBuilder();
        buildTree(sb, "", true);
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * toString() الافتراضي يطبع الشجرة الكاملة.
     * أي subclass ممكن يعمل override ليعطي عرض مختصر (متل "user.name" أو "42").
     */
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

    /** مساعدة لبناء قائمة أبناء من عدد من الـ nodes. */
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
