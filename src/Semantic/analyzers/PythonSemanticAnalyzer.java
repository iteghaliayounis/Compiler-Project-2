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

/**
 * PythonSemanticAnalyzer — مدير أخطاء Python
 *
 * يدير الـ Checkers التالية:
 *   1. UndefinedVariableChecker       ← غالية ✅
 *   2. InvalidAttributeAccessChecker  ← جديد (احتياطي)
 *   3. TypeErrorChecker               ← رؤى
 *   4. TypeMismatchChecker            ← رؤى (معطل مؤقتاً)
 *   5. ScopeChecker                   ← رغد
 *   6. DivisionByZeroChecker          ← رغد
 *   7. WrongArgumentsChecker          ← راما
 *   8. InvalidFunctionCallChecker     ← راما
 *   9. UseBeforeAssignmentChecker     ← راما
 *  10. ReturnTypeMismatchChecker      ← راما
 */
public class PythonSemanticAnalyzer {

    private final SymbolTable symbolTable;
    private final UndefinedVariableChecker undefinedChecker;
    private final InvalidAttributeAccessChecker invalidAttributeAccessChecker; // ← تعريف الحقل
    private final TypeErrorChecker         typeErrorChecker;
    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;
    //private final TypeMismatchChecker      typeMismatchChecker;

    // ← راما
    private final WrongArgumentsChecker      wrongArgumentsChecker;
    private final InvalidFunctionCallChecker invalidFunctionCallChecker;
    private final UseBeforeAssignmentChecker useBeforeAssignmentChecker;

    // ← راما
    private final ReturnTypeMismatchChecker returnTypeMismatchChecker;

    public PythonSemanticAnalyzer(SymbolTable symbolTable, SemanticErrorHandler sharedHandler) {
        this.symbolTable = symbolTable;

        // Existing checkers
        this.undefinedChecker = new UndefinedVariableChecker(symbolTable, sharedHandler);

        // ← تهيئة الـ Checker الجديد
        this.invalidAttributeAccessChecker = new InvalidAttributeAccessChecker(symbolTable, sharedHandler);

        // رؤى
        this.typeErrorChecker     = new TypeErrorChecker(symbolTable, sharedHandler);
        // this.typeMismatchChecker  = new TypeMismatchChecker(symbolTable, sharedHandler);

        // رغد
        this.scopeChecker = new ScopeChecker(symbolTable, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(symbolTable, sharedHandler);

        // راما
        this.wrongArgumentsChecker      = new WrongArgumentsChecker(symbolTable, sharedHandler);
        this.invalidFunctionCallChecker = new InvalidFunctionCallChecker(symbolTable, sharedHandler);
        this.useBeforeAssignmentChecker = new UseBeforeAssignmentChecker(symbolTable, sharedHandler);

        // ← راما
        this.returnTypeMismatchChecker = new ReturnTypeMismatchChecker(symbolTable, sharedHandler);
    }

    public void analyze(ASTNode root) {
        System.out.println("\n[Semantic Analysis] Running Python checks...");

        // 1. Undefined Variable (غالية)
        undefinedChecker.check(root);

        // 2. Invalid Attribute Access (بعد الـ Undefined Variable مباشرة)
        invalidAttributeAccessChecker.check(root);

        // 3. Type Error (رؤى)
        typeErrorChecker.check(root);

        // 4. Scope Error (رغد)
        scopeChecker.check(root);

        // 5. Division By Zero (رغد)
        divisionByZeroChecker.check(root);

        // 6. Type Mismatch (رؤى) — معطل مؤقتاً
        // typeMismatchChecker.check(root);

        // راما ─────────────────────────────────────────────
        // 7. Wrong Arguments Count
        wrongArgumentsChecker.check(root);

        // 8. Invalid Function Call
        invalidFunctionCallChecker.check(root);

        // 9. Use Before Assignment
        useBeforeAssignmentChecker.check(root);

        // 10. Return Type Mismatch
        returnTypeMismatchChecker.check(root);
        // ─────────────────────────────────────────────────────────
    }
}