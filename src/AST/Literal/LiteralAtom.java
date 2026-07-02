package AST.Literal;

import AST.Expressions.Atom.Atom;

public abstract class LiteralAtom extends Atom {
    protected LiteralAtom(String nodeName, int lineNumber) {
        super(nodeName, lineNumber);
    }
}
