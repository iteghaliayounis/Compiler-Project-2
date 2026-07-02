package AST.Arg;

import AST.ASTNode;

import java.util.List;

public class ArgList extends ASTNode {

    public List<Arg> args;

    public ArgList(List<Arg> args, int line) {
        super("ArgList", line);
        this.args = args;
    }


    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (Arg a : args)
            sb.append(a.toString(indent + 1));
        return sb.toString();
    }
}