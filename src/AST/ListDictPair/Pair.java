package AST.ListDictPair;

import AST.ASTNode;

public class Pair extends ASTNode {
    public String key;
    public ASTNode value;

    public Pair(String key, ASTNode value, int lineNumber) {
        super("Pair", lineNumber);
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));

        sb.append(indent(indent + 1))
                .append("Key: ").append(key).append('\n');

        sb.append(value.toString(indent + 1));

        return sb.toString();
    }

}