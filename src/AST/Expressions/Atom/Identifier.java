package AST.Expressions.Atom;

public class Identifier extends Atom{
    public String name;

    public Identifier(String name, int lineNumber) {
        super("Identifier", lineNumber);
        this.name = name;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString(int indent) {
        return formatNode(indent) + nodeName + ": " + name + "\n";
    }
}