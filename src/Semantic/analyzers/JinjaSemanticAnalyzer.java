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

<<<<<<< HEAD
=======

>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
public class JinjaSemanticAnalyzer {

    private final UndefinedVariableChecker undefinedChecker;
    private final TypeErrorChecker         typeErrorChecker;

    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;

<<<<<<< HEAD
    private final SymbolTable jinjaST;
=======
>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1

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
<<<<<<< HEAD
=======

>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
        this.typeErrorChecker = new TypeErrorChecker(jinjaST, sharedHandler);

        this.scopeChecker = new ScopeChecker(jinjaST, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(jinjaST, sharedHandler);


        this.wrongArgumentsChecker        = new WrongArgumentsChecker(sharedHandler);
        this.invalidFunctionCallChecker   = new InvalidFunctionCallChecker(jinjaST, pythonST, sharedHandler);
    }

<<<<<<< HEAD
=======

    public JinjaSemanticAnalyzer(symbol_table.SymbolTable jinjaST, SemanticErrorHandler sharedHandler) {
        this(jinjaST, (SymbolTable.SymbolTable) null, sharedHandler);
    }


>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
    public JinjaSemanticAnalyzer(SemanticErrorHandler sharedHandler) {
        this(symbol_table.SymbolTable.getInstance(), (SymbolTable.SymbolTable) null, sharedHandler);
    }

    public void analyze(AstNode root) {
        System.out.println("\n[Semantic Analysis] Running Jinja checks...");

<<<<<<< HEAD
        undefinedChecker.check(root);
=======

        undefinedChecker.check(root);

>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
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