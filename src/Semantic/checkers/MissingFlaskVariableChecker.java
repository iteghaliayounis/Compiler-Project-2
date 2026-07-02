package Semantic.checkers;

import Semantic.errors.MissingFlaskVarError;
import Semantic.handlers.SemanticErrorHandler;

import java.util.List;

public class MissingFlaskVariableChecker {

    private final SymbolTable.SymbolTable          pythonST;
    private final symbol_table.SymbolTable         jinjaST;
    private final SemanticErrorHandler             handler;

    public MissingFlaskVariableChecker(
            SymbolTable.SymbolTable pythonST,
            symbol_table.SymbolTable jinjaST,
            SemanticErrorHandler handler) {
        this.pythonST = pythonST;
        this.jinjaST  = jinjaST;
        this.handler  = handler;
    }

    public void check() {
        for (SymbolTable.SymbolTable.ScopeEntry scope : pythonST.getAllScopes()) {
            for (SymbolTable.SymbolTable.Symbol sym : scope.symbols.values()) {

                if (sym.getKind() != SymbolTable.SymbolTable.Symbol.Kind.TEMPLATE) continue;

                String       templateName = sym.getTemplateName();
                List<String> passed       = sym.getTemplateVariables();

                symbol_table.SymbolTable.Symbol jinjaTemplate = findJinjaTemplate(templateName);
                if (jinjaTemplate == null) continue;

                for (String usedVar : jinjaTemplate.getUsedVariables()) {
                    if (!passed.contains(usedVar)) {
                        handler.report(new MissingFlaskVarError(usedVar, templateName));
                    } else {
                        // هان بنعمل نقل الأنواع والقيم (Type Propagation)
                        propagateTypeAndValue(usedVar);
                    }
                }
            }
        }
    }

    private void propagateTypeAndValue(String varName) {
        SymbolTable.SymbolTable.Symbol pyVar = pythonST.lookupInAllScopes(varName);
        if (pyVar == null) return;

        String type  = pyVar.getType();
        Object value = pyVar.getValue();

        for (symbol_table.SymbolTable.ScopeEntry scope : jinjaST.getAllScopes()) {
            for (symbol_table.SymbolTable.Symbol jinjaSym : scope.getSymbols()) {
                if (jinjaSym.getName().equals(varName)) {
                    if (type != null && !"UNKNOWN".equals(type)) {
                        jinjaSym.setType(type);
                    }
                    if (value != null) {
                        jinjaSym.setValue(value);
                    }
                    return;
                }
            }
        }
    }
    private symbol_table.SymbolTable.Symbol findJinjaTemplate(String templateName) {
        // 1. جرب القبض بالاسم الحقيقي (لو الملفات متعددة ومعروفة)
        for (symbol_table.SymbolTable.ScopeEntry scope : jinjaST.getAllScopes()) {
            for (symbol_table.SymbolTable.Symbol sym : scope.getSymbols()) {
                if ("TEMPLATE".equals(sym.getKind()) && templateName.equals(sym.getName())) {
                    return sym;
                }
            }
        }

        // 2. Fallback ذكي للتيستات: لو ما لقى، دور على unknown.html
        // (لأن الـ Parser بيعطي هيك اسم لما يقرأ ملف واحد مو معروف اسمه)
        for (symbol_table.SymbolTable.ScopeEntry scope : jinjaST.getAllScopes()) {
            for (symbol_table.SymbolTable.Symbol sym : scope.getSymbols()) {
                if ("TEMPLATE".equals(sym.getKind()) && "unknown.html".equals(sym.getName())) {
                    return sym;
                }
            }
        }

        return null;
    }}