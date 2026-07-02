package AST.Expressions.CallSuffixes;

public class AttributeAccess extends CallSuffix {

    public String attribute;

    public AttributeAccess(String attribute, int line) {
        super("AttributeAccess", line);
        this.attribute = attribute;
    }
    @Override
    public String toString(int indent) {
        return formatNode(indent) + nodeName + " (" + attribute + ")\n";
    }
}