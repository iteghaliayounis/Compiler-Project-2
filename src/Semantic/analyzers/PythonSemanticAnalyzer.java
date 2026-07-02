package Semantic.analyzers;

import AST.ASTNode;
import Semantic.checkers.MissingFlaskVariableChecker;
import Semantic.checkers.UndefinedVariableChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

public class PythonSemanticAnalyzer {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    private final UndefinedVariableChecker    undefinedChecker;
    private MissingFlaskVariableChecker       missingFlaskChecker;

    public PythonSemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable      = symbolTable;
        this.handler          = new SemanticErrorHandler();
        this.undefinedChecker = new UndefinedVariableChecker(symbolTable, handler);
    }

    // يُستدعى بعد تشغيل الـ Jinja Visitor
    public void setJinjaSymbolTable(symbol_table.SymbolTable jinjaST) {
        this.missingFlaskChecker =
                new MissingFlaskVariableChecker(symbolTable, jinjaST, handler);
    }

    public void analyze(ASTNode root) {
        System.out.println("\n[Semantic Analysis] Running Python checks...");
        undefinedChecker.check(root);
        if (missingFlaskChecker != null) {
            missingFlaskChecker.check();
        }
    }

    public void printResults() { handler.printAll(); }
    public SemanticErrorHandler getHandler() { return handler; }
}