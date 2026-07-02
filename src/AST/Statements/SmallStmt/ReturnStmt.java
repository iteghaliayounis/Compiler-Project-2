package AST.Statements.SmallStmt;
import AST.ASTNode;

public class ReturnStmt extends SmallStmt {

    public ASTNode value;

    public ReturnStmt(ASTNode value, int lineNumber) {
        super("ReturnStmt", lineNumber);
        this.value = value;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        if (value != null) {
            sb.append(value.toString(indent + 1));
        }
        return sb.toString();
    }
}