package Semantic.checkers.Python;

import AST.ASTNode;
import AST.CompoundStmt.FuncDef;
import AST.CompoundStmt.FlowStmt.ForStmt;
import AST.Expressions.Atom.Identifier;
import AST.Statements.ExprStmt.ExprStmt;
import AST.Target.TargetID;
import Semantic.errors.UseBeforeAssignmentError;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * UseBeforeAssignmentChecker — راما
 * يفحص: UnboundLocalError: local variable 'x' referenced before assignment
 *
 * إصلاحات في النسخة 2:
 *   1. منع تكرار نفس الخطأ (نفس المتغير + نفس السطر)
 *   2. استبعاد المتغيرات الـ global (موجودة في scope 0 ولا تُسند في الدالة)
 *   3. التأكد من عدم الإبلاغ عن متغير في نفس السطر الذي يُسند فيه
 */
public class UseBeforeAssignmentChecker {

    private final SymbolTable symbolTable;
    private final SemanticErrorHandler handler;

    // لمنع التكرار: نخزن "varName|line" لكل خطأ تم الإبلاغ عنه
    private final Set<String> reportedErrors = new HashSet<>();

    private static final String SOURCE = "PYTHON";

    public UseBeforeAssignmentChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler = handler;
    }

    public void check(ASTNode root) {
        findAndCheckFunctions(root);
    }

    private void findAndCheckFunctions(ASTNode node) {
        if (node == null) return;

        if (node instanceof FuncDef) {
            checkFunction((FuncDef) node);
            return;
        }

        for (ASTNode child : getChildren(node)) {
            findAndCheckFunctions(child);
        }
    }

    private void checkFunction(FuncDef funcDef) {
        Set<String> allAssignedInFunc = new HashSet<>();
        if (funcDef.parameters != null && funcDef.parameters.names != null) {
            allAssignedInFunc.addAll(funcDef.parameters.names);
        }
        if (funcDef.body != null) {
            for (ASTNode stmt : funcDef.body) {
                collectAssignments(stmt, allAssignedInFunc);
            }
        }

        Set<String> assignedSoFar = new HashSet<>();
        if (funcDef.parameters != null && funcDef.parameters.names != null) {
            assignedSoFar.addAll(funcDef.parameters.names);
        }

        if (funcDef.body != null) {
            for (ASTNode stmt : funcDef.body) {
                checkNode(stmt, assignedSoFar, allAssignedInFunc);
            }
        }

        // فحص الدوال المتداخلة — لكن دون إعادة فحص جسم الدالة الأب
        if (funcDef.body != null) {
            for (ASTNode stmt : funcDef.body) {
                findNestedFunctionsOnly(stmt);
            }
        }
    }

    private void collectAssignments(ASTNode node, Set<String> assigned) {
        if (node == null) return;
        if (node instanceof FuncDef) return;

        if (node instanceof ExprStmt) {
            ExprStmt exprStmt = (ExprStmt) node;
            if (exprStmt.value != null && exprStmt.target instanceof TargetID) {
                assigned.add(((TargetID) exprStmt.target).name);
            }
        }

        if (node instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) node;
            if (forStmt.var != null) {
                assigned.add(forStmt.var);
            }
        }

        for (ASTNode child : getChildren(node)) {
            collectAssignments(child, assigned);
        }
    }

    private void checkNode(ASTNode node, Set<String> assignedSoFar, Set<String> allAssignedInFunc) {
        if (node == null) return;
        if (node instanceof FuncDef) return;

        if (node instanceof ExprStmt) {
            ExprStmt exprStmt = (ExprStmt) node;
            if (exprStmt.value != null) {
                checkNode(exprStmt.value, assignedSoFar, allAssignedInFunc);
                if (exprStmt.target instanceof TargetID) {
                    assignedSoFar.add(((TargetID) exprStmt.target).name);
                } else {
                    checkNode(exprStmt.target, assignedSoFar, allAssignedInFunc);
                }
                return;
            } else {
                checkNode(exprStmt.target, assignedSoFar, allAssignedInFunc);
                return;
            }
        }

        if (node instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) node;
            checkNode(forStmt.iterable, assignedSoFar, allAssignedInFunc);
            if (forStmt.var != null) {
                assignedSoFar.add(forStmt.var);
            }
            if (forStmt.body != null) {
                for (ASTNode stmt : forStmt.body) {
                    checkNode(stmt, assignedSoFar, allAssignedInFunc);
                }
            }
            return;
        }

        if (node instanceof Identifier) {
            String name = ((Identifier) node).getName();

            // تحقق فقط إن كان المتغير مسنداً في هذه الدالة (local)
            // ولم يُسند بعد
            if (allAssignedInFunc.contains(name) && !assignedSoFar.contains(name)) {
                String key = name + "|" + node.getLineNumber();
                if (!reportedErrors.contains(key)) {
                    reportedErrors.add(key);
                    handler.report(new UseBeforeAssignmentError(name,
                            node.getLineNumber(), SOURCE));
                }
            }
            return;
        }

        for (ASTNode child : getChildren(node)) {
            checkNode(child, assignedSoFar, allAssignedInFunc);
        }
    }

    /**
     * يبحث عن FuncDef متداخلة فقط (دون إعادة فحص أي شيء آخر)
     * هذا يمنع التكرار.
     */
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