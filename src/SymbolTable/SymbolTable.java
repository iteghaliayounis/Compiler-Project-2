package SymbolTable;

import java.util.*;

/**
Symbol Table for Python
 */
public class SymbolTable {

    // ─── Symbol ──────────────────────────────────────────────────────────────────
    public static class Symbol {
        public enum Kind { VARIABLE, FUNCTION, ROUTE_FUNCTION, PARAMETER, IMPORT ,TEMPLATE }

        private final String name;
        private Kind         kind;
        private String       type;
        private Object       value;
        private final int    line;
        private int          scopeLevel;



        private String templateName;
        private List<String> templateVariables;
        public Symbol(String name, Kind kind, String type, Object value, int line) {
            this.name       = name;
            this.kind       = kind;
            this.type       = type;
            this.value      = value;
            this.line       = line;
            this.scopeLevel = 0;
            this.templateVariables = new ArrayList<>();
        }

        public Symbol(String name, String templateName, List<String> variables, int line) {
            this(name, Kind.TEMPLATE, "TEMPLATE", null, line);
            this.templateName = templateName;
            this.templateVariables = variables != null ? variables : new ArrayList<>();
        }


        public String getTemplateName() { return templateName; }
        public List<String> getTemplateVariables() { return templateVariables; }

        public void setTemplateVariables(List<String> vars) {
            this.templateVariables = vars != null ? vars : new ArrayList<>();
        }
        // ── getters / setters ────────────────────────────────────────────────────
        public String getName()  { return name;  }
        public Kind   getKind()  { return kind;  }
        public String getType()  { return type;  }
        public Object getValue() { return value; }
        public int    getLine()  { return line;  }
        public int    getScopeLevel() { return scopeLevel; }

        public void setKind(Kind k)    { kind  = k; }
        public void setType(String t)  { type  = t; }
        public void setValue(Object v) { value = v; }
        public void setScopeLevel(int l) { scopeLevel = l; }
    }

    // ─── Scope entry ─────────────────────────────────────────────────────────────

    public static class ScopeEntry {
        public final String              scopeName;
        public final Map<String, Symbol> symbols = new LinkedHashMap<>();
        ScopeEntry(String name) { this.scopeName = name; }
    }


    public List<ScopeEntry> getAllScopes() {
        return Collections.unmodifiableList(allScopes);
    }
    // ─── Stack ───────────────────────────────────────────────────────────────────
    private final Deque<ScopeEntry> stack = new ArrayDeque<>();
    // keep a flat snapshot of every scope ever opened (for printing)
    private final List<ScopeEntry>  allScopes = new ArrayList<>();

    // ─── Constructor ─────────────────────────────────────────────────────────────
    public SymbolTable() {
        pushScope("global");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Scope management
    // ═══════════════════════════════════════════════════════════════════════════


    public void pushScope(String name) {
        ScopeEntry entry = new ScopeEntry(name);
        stack.push(entry);
        allScopes.add(entry);
    }

    public void popScope() {
        if (stack.size() > 1) stack.pop();
    }

    private int depth() { return stack.size() - 1; }

    // ═══════════════════════════════════════════════════════════════════════════
    //  insert
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean insert(Symbol symbol) {
        ScopeEntry top = stack.peek();
        if (top.symbols.containsKey(symbol.getName())) return false; // already declared
        symbol.setScopeLevel(depth());
        top.symbols.put(symbol.getName(), symbol);
        return true;
    }


    public boolean insert(String name, Symbol.Kind kind, String type, Object value, int line) {
        return insert(new Symbol(name, kind, type, value, line));
    }


    public Symbol lookup(String name) {
        for (ScopeEntry entry : stack) {
            Symbol s = entry.symbols.get(name);
            if (s != null) return s;
        }
        return null;
    }
    // بتدور في كل الـ scopes المحفوظة مش بس الـ stack الحالي
    public Symbol lookupInAllScopes(String name) {
        for (ScopeEntry entry : allScopes) {
            Symbol s = entry.symbols.get(name);
            if (s != null) return s;
        }
        return null;
    }
    // ═══════════════════════════════════════════════════════════════════════════
    //  update
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean update(String name, Object newValue) {
        Symbol s = lookup(name);
        if (s == null) return false;
        s.setValue(newValue);
        return true;
    }

    public boolean updateType(String name, String newType) {
        Symbol s = lookup(name);
        if (s == null) return false;
        s.setType(newType);
        return true;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  delete  (only in the innermost scope where it was declared)
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean delete(String name) {
        for (ScopeEntry entry : stack) {
            if (entry.symbols.containsKey(name)) {
                entry.symbols.remove(name);
                return true;
            }
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  containsLocal – check only the current (top) scope
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean containsLocal(String name) {
        return !stack.isEmpty() && stack.peek().symbols.containsKey(name);
    }
    private String clip(String s, int max) {
        if (s == null) return "—";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }
    // ═══════════════════════════════════════════════════════════════════════════
    //  printSymbolTable
    // ═══════════════════════════════════════════════════════════════════════════
    public void printSymbolTable() {

        System.out.println("\n" + "═".repeat(85));
        System.out.println("  PYTHON SYMBOL TABLE");
        System.out.println("═".repeat(85));

        String header = String.format("  %-20s %-12s %-10s %-20s %-7s %-6s",
                "Name", "Kind", "Type", "Value", "Line", "Scope");
        System.out.println(header);
        System.out.println("  " + "─".repeat(81));

        // ── FIX: iterate scopes correctly ──
        for (ScopeEntry entry : allScopes) {

            if (entry.symbols.isEmpty()) continue;

            System.out.println("\n  ▶ Scope: " + entry.scopeName);

            for (Symbol s : entry.symbols.values()) {

                String valStr = s.getValue() == null ? "—" : s.getValue().toString();
                if (valStr.length() > 18) valStr = valStr.substring(0, 15) + "...";

                System.out.println(String.format("    %-18s %-12s %-10s %-20s %-7d %-6d",
                        clip(s.getName(), 18),
                        s.getKind(),
                        s.getType(),
                        clip(valStr, 20),
                        s.getLine(),
                        s.getScopeLevel()));

                if (s.getKind() == Symbol.Kind.TEMPLATE
                        && s.getTemplateVariables() != null
                        && !s.getTemplateVariables().isEmpty()) {

                    System.out.println("      → vars: " + s.getTemplateVariables());
                }
            }
        }

        System.out.println("\n  " + "─".repeat(81));

        int total = allScopes.stream().mapToInt(e -> e.symbols.size()).sum();
        System.out.println("  Total scopes : " + allScopes.size());
        System.out.println("  Total symbols: " + total);
        System.out.println("═".repeat(85) + "\n");
    }

    public Collection<Symbol> getAllSymbols() {
        List<Symbol> allSymbols = new ArrayList<>();
        for (ScopeEntry entry : allScopes) {
            allSymbols.addAll(entry.symbols.values());
        }
        return allSymbols;
    }

}