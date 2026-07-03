package Semantic.analyzers;

import AST.ASTNode;
import Semantic.checkers.Python.UndefinedVariableChecker;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

public class PythonSemanticAnalyzer {

    private final SymbolTable symbolTable;
    private final UndefinedVariableChecker undefinedChecker;

    public PythonSemanticAnalyzer(SymbolTable symbolTable, SemanticErrorHandler sharedHandler) {
        this.symbolTable = symbolTable;

        this.undefinedChecker = new UndefinedVariableChecker(symbolTable, sharedHandler);
    }

    public void analyze(ASTNode root) {
        System.out.println("\n[Semantic Analysis] Running Python checks...");
        undefinedChecker.check(root);
    }
}