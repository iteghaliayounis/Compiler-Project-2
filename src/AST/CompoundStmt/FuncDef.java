package AST.CompoundStmt;

import AST.ASTNode;
import AST.Decorator;
import AST.Parameters;

import java.util.List;

public class FuncDef extends CompoundStmt {

    public String name;
    public Parameters parameters;
    public List<Decorator> decorators;
    public List<ASTNode> body;

    public FuncDef(
            String name,
            Parameters parameters,
            List<Decorator> decorators,
            List<ASTNode> body,
            int lineNumber
    ) {
        super("FuncDef", lineNumber);
        this.name = name;
        this.parameters = parameters;
        this.decorators = decorators;
        this.body = body;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent).replace("\n", ": " + name + "\n"));


        if (decorators != null && !decorators.isEmpty()) {
            sb.append(indent(indent + 1)).append("Decorators:\n");
            for (Decorator d : decorators) {
                if (d != null) {
                    sb.append(d.toString(indent + 2));
                }
            }
        }
        if (parameters != null) {
            sb.append(parameters.toString(indent + 1));
        }

        sb.append(indent(indent + 1)).append("Body:\n");
        for (ASTNode stmt : body) {
            if (stmt != null)
                sb.append(stmt.toString(indent + 2));
        }

        return sb.toString();
    }
}