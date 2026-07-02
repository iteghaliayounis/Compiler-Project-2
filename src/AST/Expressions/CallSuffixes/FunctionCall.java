package AST.Expressions.CallSuffixes;

import AST.Arg.ArgList;

public class FunctionCall extends CallSuffix {

    public ArgList args;

    public FunctionCall(ArgList args, int line) {
        super("FunctionCall", line);
        this.args = args;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(indent(indent) + nodeName + "\n");
        if (args != null)
            sb.append(args.toString(indent + 1));
        return sb.toString();
    }
}
