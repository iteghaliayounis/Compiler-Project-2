package AST.CompoundStmt.FlowStmt.TryStmt;

import AST.ASTNode;
import AST.CompoundStmt.CompoundStmt;

import java.util.List;

public class TryStmt extends CompoundStmt {
    public List<ASTNode> tryBlock;
    public List<CatchBlock> catches;
    public List<ASTNode> finallyBlock;

    public TryStmt(List<ASTNode> tryBlock,
                   List<CatchBlock> catches,
                   List<ASTNode> finallyBlock,
                   int lineNumber) {
        super("TryStmt", lineNumber);
        this.tryBlock = tryBlock;
        this.catches = catches;
        this.finallyBlock = finallyBlock;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));

        sb.append(indent(indent + 1)).append("TryBlock:\n");
        for (ASTNode stmt : tryBlock)
            sb.append(stmt.toString(indent + 2));

        for (CatchBlock c : catches)
            sb.append(c.toString(indent + 1));

        if (finallyBlock != null && !finallyBlock.isEmpty()) {
            sb.append(indent(indent + 1)).append("Finally:\n");
            for (ASTNode stmt : finallyBlock)
                sb.append(stmt.toString(indent + 2));
        }

        return sb.toString();
    }
    public static class CatchBlock {
        public String exceptionName;
        public List<ASTNode> body;

        public CatchBlock(String exceptionName, List<ASTNode> body) {
            this.exceptionName = exceptionName;
            this.body = body;
        }

        public String toString(int indent) {
            StringBuilder sb = new StringBuilder();
            sb.append("      ".repeat(indent))
                    .append("Visiting CatchBlock")
                    .append(exceptionName != null ? " (" + exceptionName + ")" : "")
                    .append("\n");

            for (ASTNode stmt : body) {
                sb.append(stmt.toString(indent + 1));
            }

            return sb.toString();
        }
    }
}
