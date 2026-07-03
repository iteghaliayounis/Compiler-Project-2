package symbol_table;

import java.util.*;

/**
 * Stack-based Symbol Table for HTML / Jinja
 * يدعم عدّة قوالب بنفس الوقت كل واحد فيه متغيراتو المستقلة
 */
public class SymbolTable {
    private int scopeCounter = 0;

    // ═══════════════════════════════════════════════════════════════════════════
    //  Kind Enum
    // ═══════════════════════════════════════════════════════════════════════════
    public enum Kind {
        VARIABLE, LOOP_VAR, BLOCK, MACRO, MACRO_PARAM,
        TEMPLATE, EXTENDS, INCLUDE, SET_VAR, FILTER,
        GLOBAL, ATTRIBUTE, IMPORT
    }

    private Kind parseKind(String kind) {
        if (kind == null) return Kind.VARIABLE;
        try { return Kind.valueOf(kind.toUpperCase().replace(" ", "_")); }
        catch (IllegalArgumentException e) { return Kind.VARIABLE; }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  متغيرات Flask — بس للفحص، ما بنحطها بالجدول
    // ═══════════════════════════════════════════════════════════════════════════
    private static final Set<String> FLASK_GLOBALS = new HashSet<>(Arrays.asList(
            "request", "session", "g", "config", "url_for",
            "get_flashed_messages", "range", "dict", "joiner",
            "namespace", "lipsum", "cycler"
    ));

    public static boolean isFlaskGlobal(String name) {
        return FLASK_GLOBALS.contains(name);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Symbol
    // ═══════════════════════════════════════════════════════════════════════════
    public static class Symbol {
        private final String name;
        private       Kind   kind;
        private       String type;
        private       Object value;
        private final int    line;
        private       int    scopeLevel;


        private String extendsTemplate = "None";

        private List<String> includedTemplates = new ArrayList<>();


        private List<String> macroParameters = new ArrayList<>();
        private List<String> usedVariables = new ArrayList<>();

        //  تتبع سمات المتغيرات: product → [image, name, price, description]
        private Map<String, List<String>> variableAttributes = new LinkedHashMap<>();

        public Symbol(String name, Kind kind, String type, Object value, int line) {
            this.name = name; this.kind = kind; this.type = type;
            this.value = value; this.line = line;
        }

        public String getName()               { return name; }
        public Kind   getKind()               { return kind; }
        public String getType()               { return type; }
        public Object getValue()              { return value; }
        public int    getLine()               { return line; }
        public int    getScopeLevel()         { return scopeLevel; }
        public String getExtendsTemplate()    { return extendsTemplate; }
        public List<String> getUsedVariables()     { return usedVariables; }
        public Map<String, List<String>> getVariableAttributes() { return variableAttributes; }

        public void setKind(Kind k)              { kind = k; }
        public void setType(String t)            { type = t; }
        public void setValue(Object v)           { value = v; }
        public void setScopeLevel(int l)         { scopeLevel = l; }
        public void setExtendsTemplate(String p) { extendsTemplate = p; }


        private Map<String, Integer> usedVariableLines = new LinkedHashMap<>();

        public Map<String, Integer> getUsedVariableLines() { return usedVariableLines; }
        public void addUsedVariable(String varName, int line) {
            if (!usedVariables.contains(varName)) {
                usedVariables.add(varName);
                usedVariableLines.put(varName, line);
            }
        }

        public void addUsedVariable(String varName) {
            addUsedVariable(varName, -1);
        }
        public List<String> getIncludedTemplates() { return includedTemplates; }

        public void addIncludedTemplate(String tmpl) {
            if (!this.includedTemplates.contains(tmpl)) {
                this.includedTemplates.add(tmpl);
            }
        }

        public List<String> getMacroParameters() { return macroParameters; }

        public void setMacroParameters(List<String> params) {
            this.macroParameters = params != null ? params : new ArrayList<>();
        }
        public void addVariableAttribute(String varName, String attr) {
            variableAttributes.computeIfAbsent(varName, k -> new ArrayList<>());
            List<String> attrs = variableAttributes.get(varName);
            if (!attrs.contains(attr)) attrs.add(attr);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Scope Entry
    // ═══════════════════════════════════════════════════════════════════════════
    public static class ScopeEntry {
        final String scopeName;
        final Map<String, Symbol> symbols = new LinkedHashMap<>();
        ScopeEntry(String name) { this.scopeName = name; }
        public Collection<Symbol> getSymbols()  { return symbols.values(); }
        public String getScopeName()            { return scopeName; }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  State
    // ═══════════════════════════════════════════════════════════════════════════
    private final Deque<ScopeEntry> stack     = new ArrayDeque<>();
    private final List<ScopeEntry>  allScopes = new ArrayList<>();

    public  boolean hasSemanticError = false;
    private static SymbolTable instance;

    private SymbolTable() { allocate("global"); }

    public static SymbolTable getInstance() {
        if (instance == null) instance = new SymbolTable();
        return instance;
    }

    public static void reset() { instance = null; }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Scope management
    // ═══════════════════════════════════════════════════════════════════════════
    public void allocate(String name) {
        ScopeEntry entry = new ScopeEntry(name);
        stack.push(entry);
        allScopes.add(entry);
    }

    public void allocate() { allocate("scope_" + (++scopeCounter)); }

    public void free() { if (stack.size() > 1) stack.pop(); }

    private int depth() { return stack.size() - 1; }

    // ═══════════════════════════════════════════════════════════════════════════
    //  insert
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean insert(String name, Kind kind, String type, Object value, int line) {
        ScopeEntry top = stack.peek();
        if (top.symbols.containsKey(name)) {
            top.symbols.get(name).setValue(value);
            return false;
        }
        Symbol sym = new Symbol(name, kind, type, value, line);
        sym.setScopeLevel(depth());
        top.symbols.put(name, sym);
        return true;
    }

    public boolean insert(String name, String kind, String type, Object value, int line) {
        return insert(name, parseKind(kind), type, value, line);
    }

    public void insert(String name, String kind, Object value, int line) {
        insert(name, parseKind(kind), "Unknown", value, line);
    }

    public void insert(String name, String kind, Object value) {
        insert(name, parseKind(kind), "Unknown", value, -1);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  lookup
    // ═══════════════════════════════════════════════════════════════════════════
    public Symbol lookup(String name) {
        for (ScopeEntry entry : stack) {
            Symbol s = entry.symbols.get(name);
            if (s != null) return s;
        }
        return null;
    }

    public Symbol lookupInAllScopes(String name) {
        for (ScopeEntry entry : allScopes) {
            Symbol s = entry.symbols.get(name);
            if (s != null) return s;
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  update / delete
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean update(String name, Object newValue) {
        Symbol s = lookup(name);
        if (s == null) return false;
        s.setValue(newValue);
        return true;
    }

    public boolean delete(String name) {
        for (ScopeEntry entry : stack) {
            if (entry.symbols.containsKey(name)) { entry.symbols.remove(name); return true; }
        }
        return false;
    }

    public boolean containsLocal(String name) {
        return !stack.isEmpty() && stack.peek().symbols.containsKey(name);
    }

    public List<ScopeEntry> getAllScopes() { return Collections.unmodifiableList(allScopes); }

    public Collection<Symbol> getAllSymbols() {
        List<Symbol> all = new ArrayList<>();
        for (ScopeEntry entry : allScopes) all.addAll(entry.symbols.values());
        return all;
    }

    /**
     * كل المتغيرات يلي بيحتاجها كل قالب من render_template
     * (منحذف: Flask globals, loop vars, set vars, macro params)
     */
    public Set<String> getAllUsedVariableNames() {
        Set<String> used = new LinkedHashSet<>();
        for (ScopeEntry entry : allScopes) {
            for (Symbol s : entry.symbols.values()) {
                if (s.getKind() == Kind.VARIABLE) used.add(s.getName());
                for (String v : s.getUsedVariables()) {
                    if (!isFlaskGlobal(v)) used.add(v);
                }
            }
        }

        for (Symbol s : getAllSymbols()) {
            if (s.getKind() == Kind.LOOP_VAR || s.getKind() == Kind.SET_VAR
                    || s.getKind() == Kind.MACRO_PARAM || s.getKind() == Kind.MACRO) {
                used.remove(s.getName());
            }
        }
        return used;
    }

    public List<Symbol> getTemplateSymbols() {
        List<Symbol> templates = new ArrayList<>();
        for (ScopeEntry entry : allScopes) {
            for (Symbol s : entry.symbols.values()) {
                if (s.getKind() == Kind.TEMPLATE) templates.add(s);
            }
        }
        return templates;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  printTable
    // ═══════════════════════════════════════════════════════════════════════════
    private String clip(String s, int max) {
        if (s == null) return "—";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    public void printTable() {
        System.out.println("\n" + "═".repeat(85));
        System.out.println("  HTML / JINJA SYMBOL TABLE");
        System.out.println("═".repeat(85));

        System.out.printf("  %-20s %-14s %-10s %-20s %-7s %-6s%n",
                "Name", "Kind", "Type", "Value", "Line", "Scope");
        System.out.println("  " + "─".repeat(81));

        Set<String> locallyDefined = new HashSet<>();
        for (Symbol s : getAllSymbols()) {
            if (s.getKind() == Kind.LOOP_VAR || s.getKind() == Kind.SET_VAR
                    || s.getKind() == Kind.MACRO_PARAM || s.getKind() == Kind.MACRO) {
                locallyDefined.add(s.getName());
            }
        }

        for (ScopeEntry entry : allScopes) {
            if (entry.symbols.isEmpty()) continue;

            System.out.println("\n  ▶ Scope: " + entry.scopeName);

            for (Symbol s : entry.symbols.values()) {
                String valStr = s.getValue() == null ? "—" : s.getValue().toString();

                System.out.printf("    %-18s %-14s %-10s %-20s %-7d %-6d%n",
                        clip(s.getName(), 18),
                        s.getKind(),
                        s.getType(),
                        clip(valStr, 20),
                        s.getLine() == -1 ? 0 : s.getLine(),
                        s.getScopeLevel());


                if (s.getKind() == Kind.TEMPLATE) {
                    System.out.println("      → extends : " + s.getExtendsTemplate());

                    List<String> filteredVars = new ArrayList<>();
                    for (String v : s.getUsedVariables()) {
                        if (!isFlaskGlobal(v) && !locallyDefined.contains(v)) {
                            filteredVars.add(v);
                        }
                    }
                    System.out.println("      → vars    : " + (filteredVars.isEmpty() ? "[]" : filteredVars));

                    for (Map.Entry<String, List<String>> attrEntry : s.getVariableAttributes().entrySet()) {
                        System.out.println("      → " + attrEntry.getKey() + " → " + attrEntry.getValue());
                    }
                }
            }
        }

        System.out.println("\n  " + "─".repeat(81));
        int total = allScopes.stream().mapToInt(e -> e.symbols.size()).sum();
        System.out.println("  Total scopes : " + allScopes.size());
        System.out.println("  Total symbols: " + total);
        System.out.println("═".repeat(85) + "\n");
    }
}