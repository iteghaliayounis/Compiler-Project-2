package Semantic.checkers.flask;

import Semantic.errors.MissingFlaskVarError;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;


import java.util.*;

public class MissingFlaskVariableChecker {

    private final SymbolTable pythonST;
    private final symbol_table.SymbolTable jinjaST;
    private final SemanticErrorHandler handler;
    private final Set<String> missingFlaskVariables = new HashSet<>();
    public MissingFlaskVariableChecker(
            SymbolTable pythonST,
            symbol_table.SymbolTable jinjaST,
            SemanticErrorHandler handler) {
        this.pythonST = pythonST;
        this.jinjaST  = jinjaST;
        this.handler  = handler;
    }


    private static class PyTemplate {
        final String name;
        final List<String> passedVars;
        PyTemplate(String name, List<String> passedVars) {
            this.name = name;
            this.passedVars = passedVars != null ? passedVars : new ArrayList<>();
        }
    }

    private static class JinjaTpl {
        final String name;
        final List<String> usedVars;
        final Map<String, Integer> usedVarLines;
        JinjaTpl(String name, List<String> usedVars, Map<String, Integer> usedVarLines) {
            this.name = name;
            this.usedVars = usedVars != null ? usedVars : new ArrayList<>();
            this.usedVarLines = usedVarLines != null ? usedVarLines : new HashMap<>();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  check — نقطة الدخول
    // ═══════════════════════════════════════════════════════════════════════

    public void check() {
        missingFlaskVariables.clear();
        int errorsBefore = handler.getErrors().size();
        System.out.println("  [Flask Linker] Starting Missing Flask Variable Check...");

        List<PyTemplate> pyTemplates = collectPythonTemplates();
        List<JinjaTpl> jinjaTemplates = collectJinjaChildTemplates();

        if (pyTemplates.isEmpty() || jinjaTemplates.isEmpty()) {
            System.out.println("  [Flask Linker] ⚠ No templates to link.");
            return;
        }

        System.out.println("  [Flask Linker] Python render_templates found : " + pyTemplates.size());
        for (PyTemplate p : pyTemplates) {
            System.out.println("                 → " + p.name + " passes: " + p.passedVars);
        }
        System.out.println("  [Flask Linker] Jinja child templates found  : " + jinjaTemplates.size());
        for (JinjaTpl j : jinjaTemplates) {
            System.out.println("                 → " + j.name + " uses: " + j.usedVars);
        }
        System.out.println();

        // ── ربط Python Template مع Jinja Template بالاسم مباشرة ──
        for (PyTemplate py : pyTemplates) {

            for (JinjaTpl jinja : jinjaTemplates) {

                // py.name مثل: profile.html
                // jinja.name قد يكون: profile أو profile.html
                if (sameTemplateName(py.name, jinja.name)) {
                    linkAndCompare(py, jinja);
                    break;
                }
            }
        }

        int newErrors = handler.getErrors().size() - errorsBefore;
        if (newErrors == 0) {
            System.out.println("  [Flask Linker]  All Flask variables are correctly passed!");
        } else {
            System.out.println("  [Flask Linker]  Found " + newErrors + " missing variable(s)."); // <- عدلي الطباعة
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  جمع البيانات
    // ═══════════════════════════════════════════════════════════════════════

    private List<PyTemplate> collectPythonTemplates() {
        List<PyTemplate> list = new ArrayList<>();
        for (SymbolTable.ScopeEntry scope : pythonST.getAllScopes()) {
            for (SymbolTable.Symbol sym : scope.symbols.values()) {
                if (sym.getKind() == SymbolTable.Symbol.Kind.TEMPLATE) {
                    list.add(new PyTemplate(sym.getTemplateName(), sym.getTemplateVariables()));
                }
            }
        }
        return list;
    }

    private List<JinjaTpl> collectJinjaChildTemplates() {
        List<JinjaTpl> list = new ArrayList<>();
        for (symbol_table.SymbolTable.ScopeEntry scope : jinjaST.getAllScopes()) {
            for (symbol_table.SymbolTable.Symbol sym : scope.getSymbols()) {
                if (sym.getKind() == symbol_table.SymbolTable.Kind.TEMPLATE) {
                    list.add(new JinjaTpl(
                            sym.getName(),
                            sym.getUsedVariables(),
                            sym.getUsedVariableLines()
                    ));
                }
            }
        }
        return list;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  الربط والمقارنة
    // ═══════════════════════════════════════════════════════════════════════

    private int matchScore(List<String> passed, List<String> used) {
        if (used.isEmpty()) return 0;
        int found = 0;
        for (String u : used) {
            if (passed.contains(u)) found++;
        }

        return found > 0 ? found : -1;
    }
    private boolean sameTemplateName(String pyName, String jinjaName) {

        if (pyName == null || jinjaName == null) {
            return false;
        }

        // إزالة امتداد .html من الاسم إن وجد
        String pyBase = pyName.replaceAll("\\.html$", "");
        String jinjaBase = jinjaName.replaceAll("\\.html$", "");

        return pyBase.equals(jinjaBase);
    }
    private void linkAndCompare(PyTemplate py, JinjaTpl jinja) {
        System.out.println("  [Flask Linker] Linking: " + py.name + " ↔ " + jinja.name);

        for (String usedVar : jinja.usedVars) {
            if (!py.passedVars.contains(usedVar)) {
                missingFlaskVariables.add(usedVar);
                int line = jinja.usedVarLines.getOrDefault(usedVar, -1);
                handler.report(new MissingFlaskVarError(usedVar, py.name, line));
            } else {
                propagateTypeAndValue(usedVar);
            }
        }

    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Type Propagation
    // ═══════════════════════════════════════════════════════════════════════

    private void propagateTypeAndValue(String varName) {
        SymbolTable.Symbol pyVar = pythonST.lookupInAllScopes(varName);
        if (pyVar == null) return;

        String type  = pyVar.getType();
        Object value = pyVar.getValue();

        for (symbol_table.SymbolTable.ScopeEntry scope : jinjaST.getAllScopes()) {
            for (symbol_table.SymbolTable.Symbol jinjaSym : scope.getSymbols()) {
                if (varName.equals(jinjaSym.getName())) {
                    if (type != null && !"Unknown".equalsIgnoreCase(type)
                            && "Unknown".equals(jinjaSym.getType())) {
                        jinjaSym.setType(type);
                    }
                    if (value != null && jinjaSym.getValue() == null) {
                        jinjaSym.setValue(value);
                    }
                    return;
                }
            }
        }
    }
    public Set<String> getMissingFlaskVariables() {
        return Collections.unmodifiableSet(missingFlaskVariables);
    }
}