package Semantic.analyzers;

import AstHtml.AstNode;
import Semantic.checkers.JinjaSemanticChecker;
import Semantic.handlers.SemanticErrorHandler;


public class JinjaSemanticAnalyzer {

    private final SemanticErrorHandler handler;
    private final JinjaSemanticChecker checker;

    public JinjaSemanticAnalyzer() {
        this.handler = new SemanticErrorHandler();
        this.checker = new JinjaSemanticChecker(handler);
    }

    public void analyze(AstNode root) {
        System.out.println("\n[Semantic Analysis] Running Jinja checks...");
        checker.check(root);
    }
    public void addFlaskPassedVariable(String varName) {
        checker.addFlaskPassedVariable(varName);
    }
    public void printResults() { handler.printAll(); }
    public SemanticErrorHandler getHandler() { return handler; }
}