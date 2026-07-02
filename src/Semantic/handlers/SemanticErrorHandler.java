package Semantic.handlers;

import Semantic.errors.SemanticError;
import java.util.ArrayList;
import java.util.List;

public class SemanticErrorHandler {

    private static final String RED   = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private final List<SemanticError> errors = new ArrayList<>();

    public void report(SemanticError error) {
        errors.add(error);
    }

    public boolean hasErrors() { return !errors.isEmpty(); }

    public List<SemanticError> getErrors() { return errors; }

    public void printAll() {
        if (errors.isEmpty()) {
            System.out.println("  [OK] No semantic errors found.");
            return;
        }
        System.out.println("\n" + "═".repeat(85));
        System.out.println("  SEMANTIC ERRORS");
        System.out.println("═".repeat(85));
        for (SemanticError e : errors) {
            System.out.println(RED + "  ✖ " + e + RESET);
        }
        System.out.println("═".repeat(85) + "\n");
    }
}