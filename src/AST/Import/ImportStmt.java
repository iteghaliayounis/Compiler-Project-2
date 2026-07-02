package AST.Import;

import AST.ASTNode;

import java.util.List;

public class ImportStmt extends ASTNode {
    public List<String> modules;

    public ImportStmt(List<String> modules,int lineNumber) {
        super("ImportStmt", lineNumber);
        this.modules = modules;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(formatNode(indent));
        for (String m : modules) sb.append(indent(indent + 1)).append(m).append("\n");
        return sb.toString();
    }
}


//package AST.Import;
//
//import AST.ASTNode;
//import AST.ModuleName;
//
//import java.util.List;
//
//public class ImportStmt extends ASTNode {
//
//    private final List<ModuleName> modules;
//
//    public ImportStmt(List<ModuleName> modules, int line) {
//        super("ImportStmt", line);
//        this.modules = modules;
//    }
//
//    @Override
//    public String toString(int indent) {
//        StringBuilder sb = new StringBuilder(formatNode(indent));
//        for (ModuleName m : modules) sb.append(m.toString(indent + 1));
//        return sb.toString();
//    }
//}
