import antlr.ProductLexer;
import antlr.ProductParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import Visitor.PythonVisitor;
import AST.ASTNode;
import Semantic.analyzers.PythonSemanticAnalyzer;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestSemantic {

    public static void main(String[] args) throws Exception {
        // 1. قراءة ملف الإدخال
        String input = Files.readString(Paths.get("pythonTest2.txt"));
        CharStream charStream = CharStreams.fromString(input);

        // 2. Lexer + TokenStream
        ProductLexer lexer = new ProductLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 3. Parser
        ProductParser parser = new ProductParser(tokens);
        ParseTree tree = parser.program();

        // 4. بناء الـ AST + Symbol Table عبر PythonVisitor
        PythonVisitor visitor = new PythonVisitor();
        ASTNode root = visitor.visit(tree);
        SymbolTable symbolTable = visitor.getSymbolTable();

        // 5. طباعة الـ Symbol Table للتأكد
        symbolTable.printSymbolTable();

        // 6. تشغيل التحليل الدلالي
        SemanticErrorHandler handler = new SemanticErrorHandler();
        PythonSemanticAnalyzer analyzer = new PythonSemanticAnalyzer(symbolTable, handler);
        analyzer.analyze(root);

        // 7. طباعة الأخطاء (أو رسالة OK)
        handler.printAll();
    }
}