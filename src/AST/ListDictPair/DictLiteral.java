package AST.ListDictPair;


import AST.ASTNode;

import java.util.List;


public class DictLiteral extends ASTNode {
    public List<Pair> pairs;

    public DictLiteral(List<Pair> pairs, int lineNumber) {
        super("DictLiteral", lineNumber);
        this.pairs = pairs;
    }


    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (Pair p : pairs) sb.append(p.toString(indent + 1));
        return sb.toString();
    }
}