package Semantic.checkers.Python;

import AST.ASTNode;
import AST.Arg.ArgList;
import AST.CompoundStmt.FuncDef;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.CallSuffixes.CallChainExpr;
import AST.Expressions.CallSuffixes.CallSuffix;
import AST.Expressions.CallSuffixes.FunctionCall;
import Semantic.errors.WrongArgumentsError;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

import java.lang.reflect.Field;
import java.util.*;

public class WrongArgumentsChecker {

    private final SymbolTable symbolTable;
    private final SemanticErrorHandler handler;
    private final Map<String, Integer> functionParamCounts = new HashMap<>();

    private static final String SOURCE = "PYTHON";

    public WrongArgumentsChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler = handler;
    }

    public void check(ASTNode root) {
        collectFunctions(root);
        checkCalls(root);
    }

    private void collectFunctions(ASTNode node) {
        if (node == null) return;

        if (node instanceof FuncDef) {
            FuncDef funcDef = (FuncDef) node;
            int paramCount = (funcDef.parameters != null && funcDef.parameters.names != null)
                    ? funcDef.parameters.names.size()
                    : 0;
            functionParamCounts.put(funcDef.name, paramCount);
        }

        for (ASTNode child : getChildren(node)) {
            collectFunctions(child);
        }
    }

    private void checkCalls(ASTNode node) {
        if (node == null) return;

        if (node instanceof CallChainExpr) {
            checkCall((CallChainExpr) node);
        }

        for (ASTNode child : getChildren(node)) {
            checkCalls(child);
        }
    }

    private void checkCall(CallChainExpr callChain) {
        ASTNode base = callChain.getBase();
        List<CallSuffix> suffixes = callChain.getSuffixes();

        if (!(base instanceof Identifier) || suffixes == null) return;

        String funcName = ((Identifier) base).getName();

        if (!functionParamCounts.containsKey(funcName)) return;

        int expected = functionParamCounts.get(funcName);

        for (CallSuffix suffix : suffixes) {
            if (suffix instanceof FunctionCall) {
                FunctionCall call = (FunctionCall) suffix;
                int actual = countArguments(call.args);
                if (actual != expected) {
                    handler.report(new WrongArgumentsError(funcName, expected, actual,
                            call.getLineNumber(), SOURCE));
                }
            }
        }
    }

    private int countArguments(ArgList argList) {
        if (argList == null || argList.args == null) return 0;
        return argList.args.size();
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