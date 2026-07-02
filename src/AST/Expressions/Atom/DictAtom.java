package AST.Expressions.Atom;

import AST.ASTNode;

public class DictAtom extends Atom {
    public ASTNode dictLiteral;

    public DictAtom(ASTNode dictLiteral, int lineNumber) {
        super("DictAtom", lineNumber);
        this.dictLiteral = dictLiteral;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + dictLiteral.toString(indent + 1);
    }
}