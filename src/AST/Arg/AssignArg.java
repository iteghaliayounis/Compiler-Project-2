package AST.Arg;

import AST.ASTNode;

public class AssignArg extends Arg {

    public String name;
    public ASTNode value;

    public AssignArg(String name, ASTNode value, int line) {
        super("AssignArg", line);
        this.name = name;
        this.value = value;
    }


    @Override
    public String toString(int indent) {
        return formatNode(indent) + nodeName + " (" + name + ")\n" +
                value.toString(indent + 1);
    }
}