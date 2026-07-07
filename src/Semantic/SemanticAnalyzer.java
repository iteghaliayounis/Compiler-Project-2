package Semantic;

import AST.ASTNode;
// لا نستخدم as في الجافا، نكتب الاسم الكامل للكلاس مباشرة عند الحاجة لتجنب التعارض
import Semantic.analyzers.PythonSemanticAnalyzer;
import Semantic.analyzers.JinjaSemanticAnalyzer;
import Semantic.checkers.flask.FilterTypeMismatchChecker;
import Semantic.checkers.flask.MissingFlaskVariableChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;


public class SemanticAnalyzer {

    private final SemanticErrorHandler handler;

    private final PythonSemanticAnalyzer pythonAnalyzer;
    private final JinjaSemanticAnalyzer jinjaAnalyzer;

    private MissingFlaskVariableChecker  flaskLinker;// غالية ✅
    private FilterTypeMismatchChecker filterTypeChecker;    // 🆕 جديد

    //private FilterTypeMismatchChecker    filterTypeChecker;    // رؤى ← جديد

    public SemanticAnalyzer(SymbolTable pythonST, symbol_table.SymbolTable jinjaST) {
        // 1. ننشئ الـ Handler الوحيد الذي سيجمع كل الأخطاء
        this.handler = new SemanticErrorHandler();

        // 2. ننشئ المحللين ونمرر لهم نفس الـ Handler
        //    ⚠️ تعديل: jinjaAnalyzer الآن يحتاج jinjaST (لأن TypeErrorChecker الجديد يحتاجه)
        this.pythonAnalyzer = new PythonSemanticAnalyzer(pythonST, handler);
        this.jinjaAnalyzer  = new JinjaSemanticAnalyzer(jinjaST, handler);

        // 3. ننشئ الرابط بين الفلاسك والجينجا (إذا الجداول جاهزة)
        if (pythonST != null && jinjaST != null) {
            this.flaskLinker       = new MissingFlaskVariableChecker(pythonST, jinjaST, handler);
           this.filterTypeChecker = new FilterTypeMismatchChecker(pythonST, jinjaST, handler);
        }
    }

    // إذا كنتم تضيفون المتغيرات الممررة من الفلاسك يدوياً من الـ Main
    public void addFlaskPassedVariable(String varName) {
        jinjaAnalyzer.addFlaskPassedVariable(varName);
    }

    // نقطة الدخول الرئيسية التي تُستدعى من Main.java
    // استخدمنا AstHtml.AstNode مباشرة بدل الـ Alias
    public void runAnalysis(ASTNode pythonRoot, AstHtml.AstNode jinjaRoot) {
        System.out.println("\n==========================================================");
        System.out.println("           STARTING SEMANTIC ANALYSIS");
        System.out.println("==========================================================");

        // 1. تشغيل فحص البايثون (Undefined + TypeError + TypeMismatch)
        if (pythonRoot != null) {
            pythonAnalyzer.analyze(pythonRoot);
        }

        // 2. تشغيل فحص الـ Bridge — Missing Variables (غالية)
        //    مهم: يجب أن يأتي BEFORE Jinja checks
        //    لأن هذا الفحص يقوم بـ propagateTypeAndValue()
        //    (ينسخ أنواع المتغيرات من Python ST إلى Jinja ST)
        //    بدون هذا، سترى Jinja ST كل المتغيرات كـ "Unknown"
        if (flaskLinker != null) {
            flaskLinker.check();
        }

        // 3. تشغيل فحص الجينجا (Undefined + TypeError)
        //    الآن أنواع المتغيرات متوفرة في Jinja ST (تم نسخها في الخطوة 2)
        if (jinjaRoot != null) {
            jinjaAnalyzer.analyze(jinjaRoot);
        }

        // 4. 🆕 تشغيل فحص الـ Bridge — Filter Type Mismatch (رؤى)
        //    يجب أن يأتي بعد MissingFlaskVariableChecker
        //    لأنه يحتاج الأنواع المنسوخة من Python ST
        if (filterTypeChecker != null && jinjaRoot != null) {
            filterTypeChecker.check(jinjaRoot);
        }

        // 5. طباعة النتائج النهائية مرة واحدة فقط
        handler.printAll();

        System.out.println("==========================================================\n");
    }

    // في حال أردتم كتابة الأخطاء بملف خارجي مستقبلاً
    public SemanticErrorHandler getHandler() {
        return handler;
    }
}
