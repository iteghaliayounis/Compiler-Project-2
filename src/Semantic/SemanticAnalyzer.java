package Semantic;

import AST.ASTNode;
// لا نستخدم as في الجافا، نكتب الاسم الكامل للكلاس مباشرة عند الحاجة لتجنب التعارض
import Semantic.analyzers.PythonSemanticAnalyzer;
import Semantic.analyzers.JinjaSemanticAnalyzer;
import Semantic.checkers.flask.MissingFlaskVariableChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

public class SemanticAnalyzer {

    private final SemanticErrorHandler handler;

    private final PythonSemanticAnalyzer pythonAnalyzer;
    private final JinjaSemanticAnalyzer jinjaAnalyzer;

    private MissingFlaskVariableChecker flaskLinker;

    public SemanticAnalyzer(SymbolTable pythonST, symbol_table.SymbolTable jinjaST) {
        // 1. ننشئ الـ Handler الوحيد الذي سيجمع كل الأخطاء
        this.handler = new SemanticErrorHandler();

        // 2. ننشئ المحللين ونمرر لهم نفس الـ Handler
        this.pythonAnalyzer = new PythonSemanticAnalyzer(pythonST, handler);
        this.jinjaAnalyzer  = new JinjaSemanticAnalyzer(handler);

        // 3. ننشئ الرابط بين الفلاسك والجينجا (إذا الجداول جاهزة)
        if (pythonST != null && jinjaST != null) {
            this.flaskLinker = new MissingFlaskVariableChecker(pythonST, jinjaST, handler);
        }
    }

    // إذا كنتوا تضيفون المتغيرات الممررة من الفلاسك يدوياً من الـ Main
    public void addFlaskPassedVariable(String varName) {
        jinjaAnalyzer.addFlaskPassedVariable(varName);
    }

    // نقطة الدخول الرئيسية التي تُستدعى من Main.java
    // استخدمنا AstHtml.AstNode مباشرة بدل الـ Alias
    public void runAnalysis(ASTNode pythonRoot, AstHtml.AstNode jinjaRoot) {
        System.out.println("\n==========================================================");
        System.out.println("           STARTING SEMANTIC ANALYSIS");
        System.out.println("==========================================================");

        // تشغيل فحص البايثون
        if (pythonRoot != null) {
            pythonAnalyzer.analyze(pythonRoot);
        }

        // تشغيل فحص الجينجا
        if (jinjaRoot != null) {
            jinjaAnalyzer.analyze(jinjaRoot);
        }

        // تشغيل فحص الـ Bridge (الفلاسك)
        if (flaskLinker != null) {
            flaskLinker.check();
        }

        // طباعة النتائج النهائية مرة واحدة فقط
        handler.printAll();

        System.out.println("==========================================================\n");
    }

    // في حال أردتم كتابة الأخطاء بملف خارجي مستقبلاً
    public SemanticErrorHandler getHandler() {
        return handler;
    }
}