package Semantic.analyzers;

import AST.ASTNode;
import Semantic.checkers.Python.DivisionByZeroChecker;
import Semantic.checkers.Python.ScopeChecker;
import Semantic.checkers.Python.UndefinedVariableChecker;
import Semantic.checkers.Python.TypeErrorChecker;
//import Semantic.checkers.Python.TypeMismatchChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

/**
 * PythonSemanticAnalyzer — مدير أخطاء Python
 *
 * يدير الـ Checkers التالية:
 *   1. UndefinedVariableChecker  ← غالية ✅ (موجود مسبقاً)
 *   2. TypeErrorChecker          ← رؤى (جديد)
 *   3. TypeMismatchChecker       ← رؤى (جديد)
 */
public class PythonSemanticAnalyzer {

    private final SymbolTable symbolTable;
    private final UndefinedVariableChecker undefinedChecker;
    private final TypeErrorChecker         typeErrorChecker;        // ← جديد
    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;
    //private final TypeMismatchChecker      typeMismatchChecker;     // ← جديد

    public PythonSemanticAnalyzer(SymbolTable symbolTable, SemanticErrorHandler sharedHandler) {
        this.symbolTable = symbolTable;

        // Existing checker
        this.undefinedChecker = new UndefinedVariableChecker(symbolTable, sharedHandler);

        // New checkers — رؤى
        this.typeErrorChecker     = new TypeErrorChecker(symbolTable, sharedHandler);
       // this.typeMismatchChecker  = new TypeMismatchChecker(symbolTable, sharedHandler);

        this.scopeChecker = new ScopeChecker(symbolTable, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(symbolTable, sharedHandler);
    }

    public void analyze(ASTNode root) {
        System.out.println("\n[Semantic Analysis] Running Python checks...");

        // 1. Undefined Variable (غالية)
        undefinedChecker.check(root);

        // 2. Type Error (رؤى) — العمليات على أنواع خاطئة
        typeErrorChecker.check(root);

        scopeChecker.check(root);
        divisionByZeroChecker.check(root);

        // 3. Type Mismatch (رؤى) — الإسناد مع type annotation
       // typeMismatchChecker.check(root);
    }
}
