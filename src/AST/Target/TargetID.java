package AST.Target;

public class TargetID  extends Target {
    public String name;

    public TargetID(String name, int lineNumber) {
        super("TargetID", lineNumber);
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