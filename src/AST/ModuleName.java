package AST;

import java.util.List;

public class ModuleName extends ASTNode {

    public final List<String> name;

    public ModuleName(List<String> parts, int lineNumber) {
        super("ModuleName", lineNumber);
        this.name = parts;
    }

    public List<String> getParts() {
        return name;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (String part : name) {
            sb.append(indent(indent + 1)).append("PartNode: ").append(part).append("\n");
        }
        return sb.toString();
    }
}