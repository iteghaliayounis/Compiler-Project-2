package Semantic.analyzers;

import AstHtml.AstNode;
import Semantic.checkers.Jinja.DivisionByZeroChecker;
import Semantic.checkers.Jinja.ScopeChecker;
import Semantic.checkers.Jinja.UndefinedVariableChecker;
import Semantic.checkers.Jinja.TypeErrorChecker;
import Semantic.handlers.SemanticErrorHandler;
import java.util.Set;
import symbol_table.SymbolTable;

/**
 * JinjaSemanticAnalyzer — مدير أخطاء Jinja2
 *
 * يدير الـ Checkers التالية:
 *   1. UndefinedVariableChecker  ← غالية ✅ (موجود مسبقاً)
 *   2. TypeErrorChecker          ← رؤى (جديد)
 *
 * ملاحظة: Type Mismatch للجينجا (فلاتر) يتم في طبقة الـ Bridge
 *         (FilterTypeMismatchChecker) وليس هنا.
 *
 * ملاحظة إضافية: Invalid Attribute Access (AttributeError) للجينجا
 *         يتم أيضاً في طبقة الـ Bridge (InvalidAttributeAccessChecker
 *         تحت checkers/flask) وليس هنا — لنفس سبب FilterTypeMismatchChecker:
 *         يحتاج fallback لجدول رموز بايثون (pythonST) لمعرفة أنواع
 *         المتغيرات الممرّرة مباشرة من Flask.
 */
public class JinjaSemanticAnalyzer {

    private final UndefinedVariableChecker undefinedChecker;
    private final TypeErrorChecker         typeErrorChecker;
    private final ScopeChecker             scopeChecker;
    private final DivisionByZeroChecker    divisionByZeroChecker;

    // نحتاج Jinja ST لتمريرها لـ TypeErrorChecker
    private final SymbolTable jinjaST;

    public JinjaSemanticAnalyzer(SymbolTable jinjaST, SemanticErrorHandler sharedHandler) {
        this.jinjaST = jinjaST;

        // Existing checker (غالية) — لا يحتاج jinjaST لأنه يدير scopes داخلياً
        this.undefinedChecker = new UndefinedVariableChecker(sharedHandler);

        // New checker (رؤى) — يحتاج jinjaST للاستعلام عن أنواع المتغيرات
        this.typeErrorChecker = new TypeErrorChecker(jinjaST, sharedHandler);

        this.scopeChecker = new ScopeChecker(jinjaST, sharedHandler);
        this.divisionByZeroChecker = new DivisionByZeroChecker(jinjaST, sharedHandler);
    }

    /**
     * Constructor قديم محافظ على التوافق الخلفي
     * (يستخدم SymbolTable.getInstance() داخلياً)
     */
    public JinjaSemanticAnalyzer(SemanticErrorHandler sharedHandler) {
        this(SymbolTable.getInstance(), sharedHandler);
    }

    public void analyze(AstNode root) {
        System.out.println("\n[Semantic Analysis] Running Jinja checks...");

        // 1. Undefined Variable (غالية)
        undefinedChecker.check(root);

        // 2. Type Error (رؤى) — العمليات على أنواع خاطئة في القوالب
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
