package Semantic;

import AST.ASTNode;
// لا نستخدم as في الجافا، نكتب الاسم الكامل للكلاس مباشرة عند الحاجة لتجنب التعارض
import Semantic.analyzers.PythonSemanticAnalyzer;
import Semantic.analyzers.JinjaSemanticAnalyzer;
import Semantic.checkers.flask.FilterTypeMismatchChecker;
import Semantic.checkers.flask.InvalidAttributeAccessChecker;
import Semantic.checkers.flask.MissingFlaskVariableChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;


public class SemanticAnalyzer {

    private final SemanticErrorHandler handler;

    private final PythonSemanticAnalyzer pythonAnalyzer;
    private final JinjaSemanticAnalyzer jinjaAnalyzer;

<<<<<<< HEAD
    private MissingFlaskVariableChecker  flaskLinker;
    private FilterTypeMismatchChecker filterTypeChecker;
=======
    private MissingFlaskVariableChecker  flaskLinker;// غالية ✅
    private FilterTypeMismatchChecker filterTypeChecker;    // 🆕 جديد
    private InvalidAttributeAccessChecker invalidAttributeAccessChecker; // 🆕 جديد (Bridge)
>>>>>>> 731c849680d74f791c31c0eeb1b74682e91a867e


    public SemanticAnalyzer(SymbolTable pythonST, symbol_table.SymbolTable jinjaST) {
        this.handler = new SemanticErrorHandler();


        this.pythonAnalyzer = new PythonSemanticAnalyzer(pythonST, handler);
        this.jinjaAnalyzer  = new JinjaSemanticAnalyzer(jinjaST, pythonST, handler);

        if (pythonST != null && jinjaST != null) {
            this.flaskLinker       = new MissingFlaskVariableChecker(pythonST, jinjaST, handler);
<<<<<<< HEAD
            this.filterTypeChecker = new FilterTypeMismatchChecker(pythonST, jinjaST, handler);
=======
           this.filterTypeChecker = new FilterTypeMismatchChecker(pythonST, jinjaST, handler);
           this.invalidAttributeAccessChecker = new InvalidAttributeAccessChecker(pythonST, jinjaST, handler);
>>>>>>> 731c849680d74f791c31c0eeb1b74682e91a867e
        }
    }


    public void addFlaskPassedVariable(String varName) {
        jinjaAnalyzer.addFlaskPassedVariable(varName);
    }


    public void runAnalysis(ASTNode pythonRoot, AstHtml.AstNode jinjaRoot) {
        System.out.println("\n==========================================================");
        System.out.println("           STARTING SEMANTIC ANALYSIS");
        System.out.println("==========================================================");

        // 1. تشغيل فحص البايثون (Undefined + TypeError + TypeMismatch)
        if (pythonRoot != null) {
            pythonAnalyzer.analyze(pythonRoot);
        }


        if (flaskLinker != null) {
            flaskLinker.check();
            jinjaAnalyzer.addFlaskMissingVariables(
                    flaskLinker.getMissingFlaskVariables()
            );
        }


        if (jinjaRoot != null) {
            jinjaAnalyzer.analyze(jinjaRoot);
        }


        if (filterTypeChecker != null && jinjaRoot != null) {
            filterTypeChecker.check(jinjaRoot);
        }

<<<<<<< HEAD
=======
        // 4.5. 🆕 تشغيل فحص الـ Bridge — Invalid Attribute Access (AttributeError)
        //    نفس متطلبات filterTypeChecker: يحتاج pythonST كـ fallback
        //    لمعرفة أنواع المتغيرات الممرّرة مباشرة من Flask
        if (invalidAttributeAccessChecker != null && jinjaRoot != null) {
            invalidAttributeAccessChecker.check(jinjaRoot);
        }

        // 5. طباعة النتائج النهائية مرة واحدة فقط
>>>>>>> 731c849680d74f791c31c0eeb1b74682e91a867e
        handler.printAll();

        System.out.println("==========================================================\n");
    }

    public SemanticErrorHandler getHandler() {
        return handler;
    }
}