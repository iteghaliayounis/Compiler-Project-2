package AST.Statements.SmallStmt;
import AST.ASTNode;

public class RaiseStmt extends SmallStmt{

    public  ASTNode exception;

    public RaiseStmt(ASTNode exception, int lineNumber) {
        super("RaiseStmt", lineNumber);
        this.exception = exception;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        if (exception != null) {
            sb.append(exception.toString(indent + 1));
        }
        return sb.toString();
    }
}