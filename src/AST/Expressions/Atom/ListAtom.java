package AST.Expressions.Atom;

import AST.ASTNode;

public class ListAtom extends Atom{
    public ASTNode listLiteral;

    public ListAtom(ASTNode listLiteral, int lineNumber) {
        super("ListAtom", lineNumber);
        this.listLiteral = listLiteral;
    }


    @Override
    public String toString(int indent) {
        return formatNode(indent) + listLiteral.toString(indent + 1);
    }
}