package AST;

import java.util.List;

public class Parameters extends ASTNode {

    public List<String> names;

    public Parameters(List<String> names, int line) {
        super("Parameters", line);
        this.names = names;


    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (String n : names)
            sb.append(indent(indent + 1)).append(n).append("\n");
        return sb.toString();
    }
}