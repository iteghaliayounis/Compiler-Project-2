package symbol_table;

import java.util.*;

/**
 * Stack-based Symbol Table for HTML / Jinja2
 * Operations: insert, lookup, update, delete + scope management (allocate/free)
 *
 * محدّث ليتطابق مع مستوى Python SymbolTable + إضافات احترافية لمرحلة Semantic
 */
public class SymbolTable {
    private int scopeCounter = 0;

    // ═══════════════════════════════════════════════════════════════════════════
    //  Kind Enum — Type Safety بدلاً من String
    // ═══════════════════════════════════════════════════════════════════════════
    public enum Kind {
        VARIABLE,       // {{ x }} — متغير عادي
        LOOP_VAR,       // {% for x in ... %} — متغير حلقة
        BLOCK,          // {% block name %} — بلوك Jinja
        MACRO,          // {% macro name() %} — ماكرو
        MACRO_PARAM,    // وسائط الماكرو
        TEMPLATE,       // القالب نفسه
        EXTENDS,        // {% extends "..." %}
        INCLUDE,        // {% include "..." %}
        SET_VAR,        // {% set x = ... %}
        FILTER,         // {{ x | filter }} — الفلاتر المستخدمة
        GLOBAL,         // request, session, g, config, url_for — متغيرات Flask العامة
        ATTRIBUTE,      // obj.attr — وصول خاصية
        IMPORT          // {% import "..." as x %}
    }

    /** تحويل String إلى Kind (للتوافق مع الكود القديم) */
    private Kind parseKind(String kind) {
        if (kind == null) return Kind.VARIABLE;
        try {
            return Kind.valueOf(kind.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return Kind.VARIABLE;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Symbol
    // ═══════════════════════════════════════════════════════════════════════════
    public static class Symbol {
        private final String name;
        private       Kind   kind;
        private       String type;   // "String", "Number", "Bool", "List", "Dict", "Function", "Unknown"
        private       Object value;
        private final int    line;
        private       int    scopeLevel;

        // 🚀 حقول القوالب (Templates)
        private String extendsTemplate = "None";
        private List<String> usedVariables     = new ArrayList<>();
        private List<String> includedTemplates = new ArrayList<>();

        // 🚀 حقول الماكرو (Macros)
        private List<String> macroParameters = new ArrayList<>();

        // 🚀 حقول المتغيرات (Variables)
        private List<String> accessedAttributes = new ArrayList<>();  // مثلاً: product → [name, price, image]

        public Symbol(String name, Kind kind, String type, Object value, int line) {
            this.name       = name;
            this.kind       = kind;
            this.type       = type;
            this.value      = value;
            this.line       = line;
            this.scopeLevel = 0;
        }

        // ── getters ────────────────────────────────────────────────────────────
        public String getName()             { return name;             }
        public Kind   getKind()             { return kind;             }
        public String getType()             { return type;             }
        public Object getValue()            { return value;            }
        public int    getLine()             { return line;             }
        public int    getScopeLevel()       { return scopeLevel;       }
        public String getExtendsTemplate()  { return extendsTemplate;  }
        public List<String> getUsedVariables()     { return usedVariables;     }
        public List<String> getIncludedTemplates() { return includedTemplates; }
        public List<String> getMacroParameters()   { return macroParameters;   }
        public List<String> getAccessedAttributes() { return accessedAttributes; }

        // ── setters ────────────────────────────────────────────────────────────
        public void setKind(Kind k)              { kind  = k; }
        public void setType(String t)            { type  = t; }
        public void setValue(Object v)           { value = v; }
        public void setScopeLevel(int l)         { scopeLevel = l; }
        public void setExtendsTemplate(String p) { this.extendsTemplate = p; }
        public void setMacroParameters(List<String> params) {
            this.macroParameters = params != null ? params : new ArrayList<>();
        }

        // ── adders (مع منع التكرار) ────────────────────────────────────────────
        public void addUsedVariable(String varName) {
            if (!this.usedVariables.contains(varName)) this.usedVariables.add(varName);
        }
        public void addIncludedTemplate(String template) {
            if (!this.includedTemplates.contains(template)) this.includedTemplates.add(template);
        }
        public void addMacroParameter(String param) {
            if (!this.macroParameters.contains(param)) this.macroParameters.add(param);
        }
        public void addAccessedAttribute(String attr) {
            if (!this.accessedAttributes.contains(attr)) this.accessedAttributes.add(attr);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Scope Entry
    // ═══════════════════════════════════════════════════════════════════════════
    public static class ScopeEntry {
        final String scopeName;
        final Map<String, Symbol> symbols = new LinkedHashMap<>();

        ScopeEntry(String name) {
            this.scopeName = name;
        }

        public Collection<Symbol> getSymbols() { return symbols.values(); }
        public String getScopeName()           { return scopeName;       }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  State
    // ═══════════════════════════════════════════════════════════════════════════
    private final Deque<ScopeEntry> stack     = new ArrayDeque<>();
    private final List<ScopeEntry>  allScopes = new ArrayList<>();

    public  boolean hasSemanticError = false;
    private static SymbolTable instance;

    private SymbolTable() {
        allocate("global");
        insertFlaskGlobals();   // 🚀 إدخال متغيرات Flask العامة تلقائياً
    }

    public static SymbolTable getInstance() {
        if (instance == null) instance = new SymbolTable();
        return instance;
    }

    public static void reset() { instance = null; }

    /** إدخال متغيرات Flask العامة في الـ Global Scope */
    private void insertFlaskGlobals() {
        // هذه المتغيرات متاحة دائماً في قوالب Jinja2 ولا يجب اعتبارها Missing
        String[][] globals = {
                {"request",                "Dict"},
                {"session",                "Dict"},
                {"g",                      "Dict"},
                {"config",                 "Dict"},
                {"url_for",                "Function"},
                {"get_flashed_messages",   "Function"},
                {"range",                  "Function"},
                {"dict",                   "Function"},
                {"joiner",                 "Function"},
                {"namespace",              "Function"},
                {"lipsum",                 "Function"},
                {"cycler",                 "Function"},
        };
        for (String[] g : globals) {
            insert(g[0], Kind.GLOBAL, g[1], null, 0);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Scope management
    // ═══════════════════════════════════════════════════════════════════════════
    public void allocate(String name) {
        ScopeEntry entry = new ScopeEntry(name);
        stack.push(entry);
        allScopes.add(entry);
    }

    public void allocate() {
        allocate("scope_" + (++scopeCounter));
    }

    public void free() {
        if (stack.size() > 1) stack.pop();
    }

    private int depth() { return stack.size() - 1; }

    // ═══════════════════════════════════════════════════════════════════════════
    //  insert (مع Enum)
    // ═══════════════════════════════════════════════════════════════════════════
    public boolean insert(String name, Kind kind, String type, Object value, int line) {
        ScopeEntry top = stack.peek();
        if (top.symbols.containsKey(name)) {
            // إعادة تعريف في نفس الـ scope → تحديث القيمة فقط
            Symbol s = top.symbols.get(name);
            s.setValue(value);
            return false;
        }
        Symbol sym = new Symbol(name, kind, type, value, line);
        sym.setScopeLevel(depth());
        top.symbols.put(name, sym);
        return true;
    }

    /** Overload مع String kind (للتوافق مع الكود القديم) */
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
    //  lookup (في الـ stack الحالي فقط)
    // ═══════════════════════════════════════════════════════════════════════════
    public Symbol lookup(String name) {
        for (ScopeEntry entry : stack) {
            Symbol s = entry.symbols.get(name);
            if (s != null) return s;
        }
        return null;
    }

    // 🚀 NEW: lookup في كل الـ scopes (حتى المنتهية) — مهم لـ extends/include
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

    // 🚀 NEW: updateType — لـ Type Inference
    public boolean updateType(String name, String newType) {
        Symbol s = lookup(name);
        if (s == null) return false;
        s.setType(newType);
        return true;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  delete
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

    public boolean containsLocal(String name) {
        return !stack.isEmpty() && stack.peek().symbols.containsKey(name);
    }

    public List<ScopeEntry> getAllScopes() {
        return Collections.unmodifiableList(allScopes);
    }

    // 🚀 NEW: getAllSymbols — لـ MissingFlaskVariableChecker
    public Collection<Symbol> getAllSymbols() {
        List<Symbol> all = new ArrayList<>();
        for (ScopeEntry entry : allScopes) {
            all.addAll(entry.symbols.values());
        }
        return all;
    }

    // 🚀 NEW: getAllUsedVariableNames — كل المتغيرات المستخدمة (ناقص Globals و LoopVars)
    // هذا هو ما سيتم مقارنته مع متغيرات render_template
    public Set<String> getAllUsedVariableNames() {
        Set<String> used = new LinkedHashSet<>();

        // اجمع كل المتغيرات المسجلة كـ VARIABLE أو من usedVariables
        for (ScopeEntry entry : allScopes) {
            for (Symbol s : entry.symbols.values()) {
                if (s.getKind() == Kind.VARIABLE) {
                    used.add(s.getName());
                }
                used.addAll(s.getUsedVariables());
            }
        }

        // احذف المتغيرات المعرفة محلياً (LoopVar, SetVar, MacroParam, Global)
        Set<String> locallyDefined = new HashSet<>();
        for (Symbol s : getAllSymbols()) {
            if (s.getKind() == Kind.GLOBAL
                    || s.getKind() == Kind.LOOP_VAR
                    || s.getKind() == Kind.SET_VAR
                    || s.getKind() == Kind.MACRO_PARAM
                    || s.getKind() == Kind.MACRO) {
                locallyDefined.add(s.getName());
            }
        }
        used.removeAll(locallyDefined);

        return used;
    }

    // 🚀 NEW: getTemplateSymbols — كل القوالب المكتشفة
    public List<Symbol> getTemplateSymbols() {
        List<Symbol> templates = new ArrayList<>();
        for (ScopeEntry entry : allScopes) {
            for (Symbol s : entry.symbols.values()) {
                if (s.getKind() == Kind.TEMPLATE) {
                    templates.add(s);
                }
            }
        }
        return templates;
    }

    // 🚀 NEW: getSymbolsByKind — symbols حسب النوع
    public List<Symbol> getSymbolsByKind(Kind kind) {
        List<Symbol> result = new ArrayList<>();
        for (ScopeEntry entry : allScopes) {
            for (Symbol s : entry.symbols.values()) {
                if (s.getKind() == kind) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  printTable
    // ═══════════════════════════════════════════════════════════════════════════
    private String clip(String s, int max) {
        if (s == null) return "—";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }

    public void printTable() {
        System.out.println("\n" + "═".repeat(85));
        System.out.println("  HTML / JINJA SYMBOL TABLE");
        System.out.println("═".repeat(85));

        String header = String.format("  %-20s %-14s %-10s %-20s %-7s %-6s",
                "Name", "Kind", "Type", "Value", "Line", "Scope");
        System.out.println(header);
        System.out.println("  " + "─".repeat(81));

        for (ScopeEntry entry : allScopes) {
            if (entry.symbols.isEmpty()) continue;

            System.out.println("\n  ▶ Scope: " + entry.scopeName);

            for (Symbol s : entry.symbols.values()) {
                String valStr = s.getValue() == null ? "—" : s.getValue().toString();

                System.out.println(String.format("    %-18s %-14s %-10s %-20s %-7d %-6d",
                        clip(s.getName(), 18),
                        s.getKind(),
                        s.getType(),
                        clip(valStr, 20),
                        s.getLine() == -1 ? 0 : s.getLine(),
                        s.getScopeLevel()));

                // معلومات القالب
                if (s.getKind() == Kind.TEMPLATE) {
                    System.out.println("      → extends : " + s.getExtendsTemplate());
                    System.out.println("      → vars    : " + s.getUsedVariables());
                    System.out.println("      → includes: " + s.getIncludedTemplates());
                }

                // معلومات الماكرو
                if (s.getKind() == Kind.MACRO) {
                    System.out.println("      → params  : " + s.getMacroParameters());
                }

                // السمات المستخدمة (Attribute Access)
                if (!s.getAccessedAttributes().isEmpty()) {
                    System.out.println("      → attrs   : " + s.getAccessedAttributes());
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
