package Semantic.checkers.Python;

import AST.ASTNode;
import AST.CompoundStmt.FuncDef;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.CallSuffixes.CallChainExpr;
import AST.ListDictPair.DictLiteral;
import AST.ListDictPair.ListLiteral;
import AST.Literal.*;
import AST.Statements.SmallStmt.ReturnStmt;
import Semantic.errors.ReturnTypeMismatchError;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;
import SymbolTable.SymbolTable.Symbol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ReturnTypeMismatchChecker {

    private final SymbolTable symbolTable;
    private final SemanticErrorHandler handler;

    private static final String SOURCE = "PYTHON";

    public ReturnTypeMismatchChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler = handler;
    }

    public void check(ASTNode root) {
        findFunctions(root);
    }

    private void findFunctions(ASTNode node) {
        if (node == null) return;

        if (node instanceof FuncDef) {
            checkFunction((FuncDef) node);
        }

        for (ASTNode child : getChildren(node)) {
            findFunctions(child);
        }
    }

    private void checkFunction(FuncDef funcDef) {
        List<ReturnStmt> returns = new ArrayList<>();
        if (funcDef.body != null) {
            for (ASTNode stmt : funcDef.body) {
                collectReturns(stmt, returns);
            }
        }

        if (returns.size() < 2) return;

        String expectedType = null;

        for (ReturnStmt r : returns) {
            String type = inferReturnType(r.value);
            if ("UNKNOWN".equals(type)) continue;

            if (expectedType == null) {
                expectedType = type;
            } else if (!expectedType.equals(type)) {
                handler.report(new ReturnTypeMismatchError(funcDef.name, expectedType, type,
                        r.getLineNumber(), SOURCE));
            }
        }

        if (funcDef.body != null) {
            for (ASTNode stmt : funcDef.body) {
                findNestedFunctionsOnly(stmt);
            }
        }
    }

    private void findNestedFunctionsOnly(ASTNode node) {
        if (node == null) return;
        if (node instanceof FuncDef) {
            checkFunction((FuncDef) node);
            return;
        }
        for (ASTNode child : getChildren(node)) {
            findNestedFunctionsOnly(child);
        }
    }

    private void collectReturns(ASTNode node, List<ReturnStmt> returns) {
        if (node == null) return;
        if (node instanceof FuncDef) return;

        if (node instanceof ReturnStmt) {
            returns.add((ReturnStmt) node);
        }

        for (ASTNode child : getChildren(node)) {
            collectReturns(child, returns);
        }
    }

    private String inferReturnType(ASTNode expr) {
        if (expr == null) return "NoneType";

        if (expr instanceof IntegerLiteral) return "int";
        if (expr instanceof FloatLiteral)   return "float";
        if (expr instanceof StringLiteral)  return "str";
        if (expr instanceof BoolLiteral)    return "bool";
        if (expr instanceof NoneLiteral)    return "NoneType";
        if (expr instanceof ListLiteral || expr instanceof ListAtom) return "list";
        if (expr instanceof DictLiteral || expr instanceof DictAtom) return "dict";

        if (expr instanceof Identifier) {
            Symbol symbol = symbolTable.lookup(((Identifier) expr).getName());
            return symbol != null ? normalizeType(symbol.getType()) : "UNKNOWN";
        }

        if (expr instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) expr;
            if (cc.suffixes == null || cc.suffixes.isEmpty()) {
                return inferReturnType(cc.base);
            }
            return "UNKNOWN";
        }

        return "UNKNOWN";
    }

    private String normalizeType(String type) {
        if (type == null) return "UNKNOWN";
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
            default:         return "UNKNOWN";
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
                }
            }
            clazz = clazz.getSuperclass();
        }
        return children;
    }
}