package app;
import antlr.ProductParser;
import antlr.ProductLexer;
import AST.Program;
import Semantic.SemanticAnalyzer;
import Visitor.PythonVisitor;
import AstHtml.AstNode;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import SymbolTable.SymbolTable;
import visitor_html.HtmlVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    private static Program pythonAST;
    private static SymbolTable pythonST;

    private static AstNode jinjaAST;
    private static symbol_table.SymbolTable jinjaST;

    public static void main(String[] args) {
        runProduct();
        System.out.println("\n==============================\n");
        runHtml();


        if (pythonST != null && jinjaST != null) {

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(pythonST, jinjaST);

            for (SymbolTable.ScopeEntry scope : pythonST.getAllScopes()) {
                for (SymbolTable.Symbol sym : scope.symbols.values()) {
                    if (sym.getKind() == SymbolTable.Symbol.Kind.TEMPLATE) {
                        for (String var : sym.getTemplateVariables()) {
                            semanticAnalyzer.addFlaskPassedVariable(var);
                        }
                    }
                }
            }

            semanticAnalyzer.runAnalysis(pythonAST, jinjaAST);
        }
    }

    // ================= PRODUCT =================
    private static void runProduct() {
        try {
            System.out.println("Running Product Compiler...\n");
            String path = "Tests/undefined_variable/python_test.py";
            CharStream input = CharStreams.fromFileName(path);
            ProductLexer lexer = new ProductLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ProductParser parser = new ProductParser(tokens);
            ParseTree tree = parser.program();

            PythonVisitor visitor = new PythonVisitor();
            pythonAST = (Program) visitor.visit(tree);
            System.out.println(pythonAST);

            pythonST = visitor.getSymbolTable();
            pythonST.printSymbolTable();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= HTML =================
    private static void runHtml() {
        try {
            System.out.println("Running HTML Compiler...\n");
            // String fileName = "Tests/missing_flask_variable/ templates/base.html";
            String fileName = "Tests/undefined_variable/jinja_test.html";
            InputStream is = new FileInputStream(fileName);
            CharStream input = CharStreams.fromStream(is);
            product_htmlLexer lexer = new product_htmlLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            product_htmlParser parser = new product_htmlParser(tokens);
            product_htmlParser.ProgramContext tree = parser.program();

            symbol_table.SymbolTable.reset();
            HtmlVisitor visitor = new HtmlVisitor();

            String templateName = fileName;
            if (templateName.contains("/")) {
                templateName = templateName.substring(templateName.lastIndexOf('/') + 1);
            }
            if (templateName.contains(".")) {
                templateName = templateName.substring(0, templateName.lastIndexOf('.'));
            }
            visitor.setCurrentTemplateName(templateName);

            jinjaAST = visitor.visit(tree);

            jinjaST = symbol_table.SymbolTable.getInstance();
            jinjaST.printTable();
            System.out.println(jinjaAST);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}