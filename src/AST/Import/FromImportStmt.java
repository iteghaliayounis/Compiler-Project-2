package AST.Import;

import AST.ASTNode;

import java.util.List;

public class FromImportStmt extends ASTNode {
    public String module;
    public List<String> identifiers;

    public FromImportStmt(String module, List<String> identifiers, int lineNumber) {
        super("FromImportStmt", lineNumber);
        this.module = module;
        this.identifiers = identifiers;
    }


    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        sb.append(indent(indent + 1)).append("Module: ").append(module).append("\n");
        sb.append(indent(indent + 1)).append("Identifiers:\n");
        for (String id : identifiers)
            sb.append(indent(indent + 2)).append(id).append("\n");
        return sb.toString();
    }
}


//public class FromImportStmt extends ASTNode {
//
//    private final ModuleName module;
//    private final List<String>   names;
//
//    public FromImportStmt(ModuleName module, List<String> names, int line) {
//        super("FromImportStmt", line);
//        this.module = module;
//        this.names  = names;
//    }
//
//    @Override
//    public String toString(int indent) {
//        StringBuilder sb = new StringBuilder(formatNode(indent));
//        sb.append(module.toString(indent + 1));
//        for (String n : names) {
//            sb.append(indent(indent + 1)).append("Name: ").append(n).append("\n");
//        }
//        return sb.toString();
//    }
//}