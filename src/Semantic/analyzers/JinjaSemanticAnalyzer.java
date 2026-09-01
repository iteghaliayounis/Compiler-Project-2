package Semantic.analyzers;

import AstHtml.AstNode;
import Semantic.checkers.Jinja.DivisionByZeroChecker;
import Semantic.checkers.Jinja.ScopeChecker;
import Semantic.checkers.Jinja.UndefinedVariableChecker;
import Semantic.checkers.Jinja.TypeErrorChecker;
import Semantic.checkers.Jinja.WrongArgumentsChecker;
import Semantic.checkers.Jinja.InvalidFunctionCallChecker;
import Semantic.handlers.SemanticErrorHandler;
import java.util.Set;

public class JinjaSemanticAnalyzer {

    private final UndefinedVariableChecker undefinedChecker;
    private final TypeErrorChecker         typeErrorChecker;

    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;

    private final WrongArgumentsChecker       wrongArgumentsChecker;
    private final InvalidFunctionCallChecker  invalidFunctionCallChecker;

    private final symbol_table.SymbolTable jinjaST;
    private final SymbolTable.SymbolTable pythonST;
    public JinjaSemanticAnalyzer(symbol_table.SymbolTable jinjaST,
                                 SymbolTable.SymbolTable pythonST,
                                 SemanticErrorHandler sharedHandler) {
        this.jinjaST = jinjaST;
        this.pythonST = pythonST;

        this.undefinedChecker = new UndefinedVariableChecker(sharedHandler);
        this.typeErrorChecker = new TypeErrorChecker(jinjaST, sharedHandler);

        this.scopeChecker = new ScopeChecker(jinjaST, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(jinjaST, sharedHandler);


        this.wrongArgumentsChecker        = new WrongArgumentsChecker(sharedHandler);
        this.invalidFunctionCallChecker   = new InvalidFunctionCallChecker(jinjaST, pythonST, sharedHandler);
    }

    public JinjaSemanticAnalyzer(symbol_table.SymbolTable jinjaST, SemanticErrorHandler sharedHandler) {
        this(jinjaST, (SymbolTable.SymbolTable) null, sharedHandler);
    }

    public JinjaSemanticAnalyzer(SemanticErrorHandler sharedHandler) {
        this(symbol_table.SymbolTable.getInstance(), (SymbolTable.SymbolTable) null, sharedHandler);
    }

    public void analyze(AstNode root) {
        System.out.println("\n[Semantic Analysis] Running Jinja checks...");

        undefinedChecker.check(root);
        typeErrorChecker.check(root);
        scopeChecker.check(root);
        divisionByZeroChecker.check(root);


        wrongArgumentsChecker.check(root);


        invalidFunctionCallChecker.check(root);
        // ───────────────────────────────────────────────────────────────
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
