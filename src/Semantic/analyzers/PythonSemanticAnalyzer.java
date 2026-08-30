package Semantic.analyzers;

import AST.ASTNode;
import Semantic.checkers.Python.DivisionByZeroChecker;
import Semantic.checkers.Python.ScopeChecker;
import Semantic.checkers.Python.UndefinedVariableChecker;
import Semantic.checkers.Python.TypeErrorChecker;
import Semantic.checkers.Python.WrongArgumentsChecker;
import Semantic.checkers.Python.InvalidFunctionCallChecker;
import Semantic.checkers.Python.UseBeforeAssignmentChecker;
import Semantic.checkers.Python.ReturnTypeMismatchChecker;
import Semantic.checkers.Python.InvalidAttributeAccessChecker; // ← استيراد الـ Checker الجديد
//import Semantic.checkers.Python.TypeMismatchChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

public class PythonSemanticAnalyzer {

    private final SymbolTable symbolTable;
    private final UndefinedVariableChecker undefinedChecker;
    private final InvalidAttributeAccessChecker invalidAttributeAccessChecker;
    private final TypeErrorChecker         typeErrorChecker;
    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;

    private final WrongArgumentsChecker      wrongArgumentsChecker;
    private final InvalidFunctionCallChecker invalidFunctionCallChecker;
    private final UseBeforeAssignmentChecker useBeforeAssignmentChecker;

    private final ReturnTypeMismatchChecker returnTypeMismatchChecker;

    public PythonSemanticAnalyzer(SymbolTable symbolTable, SemanticErrorHandler sharedHandler) {
        this.symbolTable = symbolTable;

        this.undefinedChecker = new UndefinedVariableChecker(symbolTable, sharedHandler);
        this.invalidAttributeAccessChecker = new InvalidAttributeAccessChecker(symbolTable, sharedHandler);
        this.typeErrorChecker     = new TypeErrorChecker(symbolTable, sharedHandler);
        this.scopeChecker = new ScopeChecker(symbolTable, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(symbolTable, sharedHandler);

        this.wrongArgumentsChecker      = new WrongArgumentsChecker(symbolTable, sharedHandler);
        this.invalidFunctionCallChecker = new InvalidFunctionCallChecker(symbolTable, sharedHandler);
        this.useBeforeAssignmentChecker = new UseBeforeAssignmentChecker(symbolTable, sharedHandler);

        this.returnTypeMismatchChecker = new ReturnTypeMismatchChecker(symbolTable, sharedHandler);
    }

    public void analyze(ASTNode root) {
        System.out.println("\n[Semantic Analysis] Running Python checks...");

        undefinedChecker.check(root);

        invalidAttributeAccessChecker.check(root);

        typeErrorChecker.check(root);

        scopeChecker.check(root);

        divisionByZeroChecker.check(root);

        // typeMismatchChecker.check(root);

        wrongArgumentsChecker.check(root);

        invalidFunctionCallChecker.check(root);

        useBeforeAssignmentChecker.check(root);

        returnTypeMismatchChecker.check(root);
    }
}