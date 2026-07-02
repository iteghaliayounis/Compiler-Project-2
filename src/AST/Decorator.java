package AST;

import AST.Arg.ArgList;

public class Decorator extends ASTNode {

    public ModuleName name;
    public  ArgList args;

    public Decorator(ModuleName name, ArgList args, int lineNumber) {
        super("Decorator", lineNumber);
        this.name = name;
        this.args = args;
    }

    public ModuleName getName() {
        return name;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        sb.append(indent(indent + 1)).append("NameNode\n");
        sb.append(name.toString(indent + 2));
        if (args != null) {
            sb.append(indent(indent + 1)).append("ArgsNode\n");
            sb.append(args.toString(indent + 2));
        }
        return sb.toString();
    }
}