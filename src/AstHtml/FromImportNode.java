package AstHtml;

import java.util.ArrayList;
import java.util.List;


public class FromImportNode extends StatementNode {

    public static class ImportName {
        public final String name;
        public final String alias;
        public ImportName(String name, String alias) {
            this.name = name;
            this.alias = alias;
        }
        @Override
        public String toString() {
            return alias == null ? name : name + " as " + alias;
        }
    }

    private final String template;
    private final List<ImportName> imports = new ArrayList<>();

    public FromImportNode(String template, int line, int column) {
        super(line, column);
        this.template = template;
    }

    public void addImport(String name, String alias) {
        imports.add(new ImportName(name, alias));
    }

    public String getTemplate() { return template; }
    public List<ImportName> getImports() { return imports; }

    @Override
    public String name() { return "FromImportNode"; }

    @Override
    public String label() {
        return "[line=" + getLine() + "] (\"" + template + "\" import " + imports + ")";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
