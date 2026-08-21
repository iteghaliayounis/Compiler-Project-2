package Semantic.checkers.Python;

import AST.ASTNode;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.CallSuffixes.AttributeAccess;
import AST.Expressions.CallSuffixes.CallChainExpr;
import AST.Expressions.CallSuffixes.CallSuffix;
import AST.Expressions.CallSuffixes.FunctionCall;
import Semantic.errors.InvalidFunctionCallError;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;
import SymbolTable.SymbolTable.Symbol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * InvalidFunctionCallChecker — راما
 * يفحص: TypeError: 'X' object is not callable
 */
public class InvalidFunctionCallChecker {

    private final SymbolTable symbolTable;
    private final SemanticErrorHandler handler;

    private static final String SOURCE = "PYTHON";

    public InvalidFunctionCallChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler = handler;
    }

    public void check(ASTNode root) {
        walk(root);
    }

    private void walk(ASTNode node) {
        if (node == null) return;

        if (node instanceof CallChainExpr) {
            checkCall((CallChainExpr) node);
        }

        for (ASTNode child : getChildren(node)) {
            walk(child);
        }
    }

    private void checkCall(CallChainExpr callChain) {
        ASTNode base = callChain.getBase();
        List<CallSuffix> suffixes = callChain.getSuffixes();

        if (!(base instanceof Identifier) || suffixes == null) return;

        String name = ((Identifier) base).getName();
        Symbol symbol = symbolTable.lookup(name);

        if (symbol == null) return;

        Symbol.Kind kind = symbol.getKind();

        // لو المتغير عادي (مش دالة)
        if (kind == Symbol.Kind.VARIABLE) {
            for (int i = 0; i < suffixes.size(); i++) {
                CallSuffix suffix = suffixes.get(i);

                // يلي جاي استدعاء دالة؟
                if (suffix instanceof FunctionCall) {
                    // ★ التعديل هنا: نتأكد إنه ما فيش نقطة (AttributeAccess) قبل الأقواس
                    boolean isMethodCall = (i > 0 && suffixes.get(i - 1) instanceof AttributeAccess);

                    // لو ما فيش نقطة قبل الأقواس، فالمتغير عم يستدعى كأنه دالة (زي products())
                    if (!isMethodCall) {
                        String pythonType = normalizeType(symbol.getType());
                        handler.report(new InvalidFunctionCallError(pythonType,
                                suffix.getLineNumber(), SOURCE));
                    }
                }
            }
        }
    }

    private String normalizeType(String type) {
        if (type == null) return "object";
        switch (type.toUpperCase()) {
            case "INT":      return "int";
            case "FLOAT":    return "float";
            case "STRING":
            case "STR":      return "str";
            case "BOOL":     return "bool";
            case "NONE":
            case "NONETYPE": return "NoneType";
            case "LIST":     return "list";
            case "DICT":     return "dict";
            default:         return "object";
        }
    }

    private List<ASTNode> getChildren(ASTNode node) {
        List<ASTNode> children = new ArrayList<>();
        if (node == null) return children;

        Class<?> clazz = node.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(node);
                    if (value instanceof ASTNode) {
                        children.add((ASTNode) value);
                    } else if (value instanceof List<?>) {
                        for (Object item : (List<?>) value) {
                            if (item instanceof ASTNode) {
                                children.add((ASTNode) item);
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    // تجاهل
                }
            }
            clazz = clazz.getSuperclass();
        }
        return children;
    }
}