package Semantic.analyzers;

import AstHtml.AstNode;
import Semantic.checkers.Jinja.DivisionByZeroChecker;
import Semantic.checkers.Jinja.ScopeChecker;
import Semantic.checkers.Jinja.UndefinedVariableChecker;
import Semantic.checkers.Jinja.TypeErrorChecker;
import Semantic.handlers.SemanticErrorHandler;
import java.util.Set;
import symbol_table.SymbolTable;

public class JinjaSemanticAnalyzer {

    private final UndefinedVariableChecker undefinedChecker;
    private final TypeErrorChecker         typeErrorChecker;
    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;

    private final SymbolTable jinjaST;

    public JinjaSemanticAnalyzer(SymbolTable jinjaST, SemanticErrorHandler sharedHandler) {
        this.jinjaST = jinjaST;

        this.undefinedChecker = new UndefinedVariableChecker(sharedHandler);
        this.typeErrorChecker = new TypeErrorChecker(jinjaST, sharedHandler);

        this.scopeChecker = new ScopeChecker(jinjaST, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(jinjaST, sharedHandler);
    }

    public JinjaSemanticAnalyzer(SemanticErrorHandler sharedHandler) {
        this(SymbolTable.getInstance(), sharedHandler);
    }

    public void analyze(AstNode root) {
        System.out.println("\n[Semantic Analysis] Running Jinja checks...");

        undefinedChecker.check(root);
        typeErrorChecker.check(root);
        scopeChecker.check(root);
        divisionByZeroChecker.check(root);
    }

    public void addFlaskPassedVariable(String varName) {
        undefinedChecker.addFlaskPassedVariable(varName);
    }
    public void addFlaskMissingVariables(Set<String> varNames) {
        if (varNames == null) {
            return;
        }

        for (String varName : varNames) {
            if (varName != null && !varName.isEmpty()) {
                undefinedChecker.addFlaskMissingVariable(varName);
            }
        }
    }
}
