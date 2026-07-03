package Semantic.analyzers;

import AstHtml.AstNode;
import Semantic.checkers.Jinja.UndefinedVariableChecker;
import Semantic.handlers.SemanticErrorHandler;

public class JinjaSemanticAnalyzer {

    private final UndefinedVariableChecker checker;

    public JinjaSemanticAnalyzer(SemanticErrorHandler sharedHandler) {
        this.checker = new UndefinedVariableChecker(sharedHandler);
    }

    public void analyze(AstNode root) {
        System.out.println("\n[Semantic Analysis] Running Jinja checks...");
        checker.check(root);
    }

    public void addFlaskPassedVariable(String varName) {
        checker.addFlaskPassedVariable(varName);
    }
}