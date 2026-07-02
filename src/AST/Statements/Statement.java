package AST.Statements;
import AST.ASTNode;

public abstract class Statement extends ASTNode {
    protected Statement(String name, int line) {
        super(name, line);
    }
}
