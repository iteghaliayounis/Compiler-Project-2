package AST.GeneratorExpr;

import AST.ASTNode;

import java.util.List;

public class ArithExpr extends ASTNode {
    public List<ASTNode> terms;
    public List<String> operators; // PLUS, MINUS

    public ArithExpr(List<ASTNode> terms, List<String> operators, int lineNumber) {
        super("ArithExpr", lineNumber);
        this.terms = terms;
        this.operators = operators;
    }


    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (int i = 0; i < terms.size(); i++) {
            sb.append(terms.get(i).toString(indent + 1));
            if (i < operators.size())
                sb.append(indent(indent + 1)).append("Operator: ").append(operators.get(i)).append("\n");
        }
        return sb.toString();
    }
}