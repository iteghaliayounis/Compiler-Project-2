package AST.Expressions.CallSuffixes;
import AST.ASTNode;

public abstract class CallSuffix extends ASTNode {

    protected CallSuffix(String name, int line) {
        super(name, line);
    }
}
