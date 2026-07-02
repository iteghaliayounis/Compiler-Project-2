package app;

import AST.Program;
import Semantic.analyzers.PythonSemanticAnalyzer;
import Semantic.checkers.MissingFlaskVariableChecker;
import Semantic.handlers.SemanticErrorHandler;
import Visitor.PythonVisitor;
import antlr.ProductLexer;
import antlr.ProductParser;
import AstHtml.AstNode;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import SymbolTable.SymbolTable; // استوردنا بايثون عادي
import visitor_html.HtmlVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    private static SymbolTable pythonST;

    public static void main(String[] args) {
        runProduct();
        System.out.println("\n==============================\n");
        runHtml();
    }

    // ================= PRODUCT =================
    private static void runProduct() {
        try {
            System.out.println("Running Product Compiler...\n");
            String path = "app.txt";
            CharStream input = CharStreams.fromFileName(path);
            ProductLexer lexer = new ProductLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ProductParser parser = new ProductParser(tokens);
            ParseTree tree = parser.program();
            PythonVisitor visitor = new PythonVisitor();
            Program program = (Program) visitor.visit(tree);
            System.out.println(program);
            visitor.getSymbolTable().printSymbolTable();

            PythonSemanticAnalyzer semanticAnalyzer =
                    new PythonSemanticAnalyzer(visitor.getSymbolTable());
            semanticAnalyzer.analyze(program);
            semanticAnalyzer.printResults();

            pythonST = visitor.getSymbolTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= HTML =================
    private static void runHtml() {
        try {
            System.out.println("Running HTML Compiler...\n");
            String fileName = "htmlTest.txt";
            InputStream is = new FileInputStream(fileName);
            CharStream input = CharStreams.fromStream(is);
            product_htmlLexer lexer = new product_htmlLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            product_htmlParser parser = new product_htmlParser(tokens);
            product_htmlParser.ProgramContext tree = parser.program();

            HtmlVisitor visitor = new HtmlVisitor();

            // 🚀 جديد: ضبط اسم القالب قبل visit (مهم!)
            // يُفضّل جلبه من اسم الملف نفسه
            String templateName = fileName;
            if (templateName.contains("/")) {
                templateName = templateName.substring(templateName.lastIndexOf('/') + 1);
            }
            if (templateName.contains(".")) {
                templateName = templateName.substring(0, templateName.lastIndexOf('.'));
            }
            visitor.setCurrentTemplateName(templateName);

            AstNode ast = visitor.visit(tree);

            symbol_table.SymbolTable jinjaST = symbol_table.SymbolTable.getInstance();

            // 🚀 الآن الجدول لن يكون فارغاً!
            jinjaST.printTable();

            System.out.println(ast);

            if (pythonST != null) {
                System.out.println("\n[Semantic Analysis] Running Flask-Jinja Linking checks...");
                SemanticErrorHandler flaskHandler = new SemanticErrorHandler();
                MissingFlaskVariableChecker flaskChecker =
                        new MissingFlaskVariableChecker(pythonST, jinjaST, flaskHandler);
                flaskChecker.check();
                flaskHandler.printAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}