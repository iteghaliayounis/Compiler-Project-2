package Semantic.checkers.Python;

import AST.ASTNode;
import AST.Program;
import AST.CompoundStmt.FuncDef;
import AST.CompoundStmt.FlowStmt.ForStmt;
import AST.CompoundStmt.FlowStmt.IfStmt;
import AST.CompoundStmt.FlowStmt.TryStmt.TryStmt;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.CallSuffixes.*;
import AST.Expressions.Expr.ComparisonExpr;
import AST.GeneratorExpr.ArithExpr;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.DictLiteral;
import AST.Statements.ExprStmt.ExprStmt;
import AST.Statements.SimpleStmt;
import AST.Statements.SmallStmt.ReturnStmt;
import AST.Statements.SmallStmt.RaiseStmt;
import AST.Arg.ArgList;
import AST.Arg.Arg;
import AST.Arg.ExprArg;
import AST.Arg.AssignArg;
import AST.Target.TargetID;
import AST.Target.TargetCall;
import AST.ListDictPair.Pair;
import AST.Literal.*;  // ← مهم! عشان نقدر نوصل لـ IntegerLiteral.value

import Semantic.errors.DivisionByZeroError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.PythonTypeInference;

import SymbolTable.SymbolTable;

import java.util.HashMap;
import java.util.Map;


public class DivisionByZeroChecker {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    private final Map<String, Object> knownConstants = new HashMap<>();

    public DivisionByZeroChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler     = handler;
    }


    public void check(ASTNode root) {
        checkNode(root);
    }


    private void checkNode(ASTNode node) {
        if (node == null) return;

        if      (node instanceof Program)         checkProgram((Program) node);
        else if (node instanceof FuncDef)         checkFuncDef((FuncDef) node);
        else if (node instanceof SimpleStmt)      checkNode(((SimpleStmt) node).smallStmt);
        else if (node instanceof ExprStmt)        checkExprStmt((ExprStmt) node);
        else if (node instanceof ReturnStmt)      checkNode(((ReturnStmt) node).value);
        else if (node instanceof RaiseStmt)       checkNode(((RaiseStmt) node).exception);
        else if (node instanceof IfStmt)          checkIfStmt((IfStmt) node);
        else if (node instanceof ForStmt)         checkForStmt((ForStmt) node);
        else if (node instanceof TryStmt)         checkTryStmt((TryStmt) node);
        else if (node instanceof CallChainExpr)   checkCallChain((CallChainExpr) node);
        else if (node instanceof ArithExpr)       checkArithExpr((ArithExpr) node);
        else if (node instanceof ComparisonExpr)  checkComparisonExpr((ComparisonExpr) node);
        else if (node instanceof ParenExpr)       checkNode(((ParenExpr) node).expr);
        else if (node instanceof ListAtom)        checkNode(((ListAtom) node).listLiteral);
        else if (node instanceof ListLiteral)     checkListLiteral((ListLiteral) node);
        else if (node instanceof DictAtom)        checkNode(((DictAtom) node).dictLiteral);
        else if (node instanceof DictLiteral)     checkDictLiteral((DictLiteral) node);
        else if (node instanceof TargetCall)      checkNode(((TargetCall) node).callChain);
        else if (node instanceof TargetID)        {  }
    }

    private void checkProgram(Program node) {
        for (ASTNode child : node.elements) checkNode(child);
    }

    private void checkFuncDef(FuncDef node) {
        Map<String, Object> outerConstants = new HashMap<>(knownConstants);
        for (ASTNode stmt : node.body) checkNode(stmt);
        knownConstants.clear();
        knownConstants.putAll(outerConstants);
    }

    private void checkExprStmt(ExprStmt node) {
        if (node.value != null) checkNode(node.value);
        if (node.target != null && node.value != null) {
            storeConstant(node.target, node.value);
        }
    }

    private void storeConstant(ASTNode target, ASTNode value) {
        if (!(target instanceof TargetID)) return;
        String varName = ((TargetID) target).name;
        if (varName == null) return;

        Object constValue = extractValue(value);
        if (constValue instanceof Number) {
            knownConstants.put(varName, constValue);
        } else {
            knownConstants.remove(varName);
        }
    }

    private void checkIfStmt(IfStmt node) {
        checkNode(node.condition);
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    private void checkForStmt(ForStmt node) {
        checkNode(node.iterable);
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    private void checkTryStmt(TryStmt node) {
        for (ASTNode stmt : node.tryBlock)     checkNode(stmt);
        for (TryStmt.CatchBlock cb : node.catches)
            for (ASTNode stmt : cb.body)       checkNode(stmt);
        for (ASTNode stmt : node.finallyBlock) checkNode(stmt);
    }

    //  CallChainExpr
    private void checkCallChain(CallChainExpr node) {
        checkNode(node.base);
        if (node.suffixes == null) return;
        for (CallSuffix suffix : node.suffixes) {
            if (suffix instanceof FunctionCall) {
                FunctionCall fc = (FunctionCall) suffix;
                if (fc.args != null) checkArgList(fc.args);
            }
            if (suffix instanceof IndexAccess) {
                checkNode(((IndexAccess) suffix).index);
            }
        }
    }

    private void checkArithExpr(ArithExpr node) {
        if (node.terms == null || node.terms.isEmpty()) return;

        for (ASTNode term : node.terms) checkNode(term);

        if (node.operators == null || node.operators.isEmpty()) return;

        for (int i = 0; i < node.operators.size() && (i + 1) < node.terms.size(); i++) {
            String op = node.operators.get(i);
            ASTNode rightNode = node.terms.get(i + 1);
            checkDivisionOperand(op, rightNode, node.getLineNumber());
        }
    }


    private void checkDivisionOperand(String op, ASTNode rightNode, int line) {
        if (rightNode == null || op == null) return;

        boolean isDivision = "/".equals(op) || "DIV".equalsIgnoreCase(op) || "SLASH".equalsIgnoreCase(op);
        boolean isModulo   = "%".equals(op) || "MOD".equalsIgnoreCase(op) || "PERCENT".equalsIgnoreCase(op);

        if (!isDivision && !isModulo) return;

        Object value = extractValue(rightNode);
        if (value == null) return;

        Number numValue = toNumber(value);
        if (numValue == null) return;

        double doubleValue = numValue.doubleValue();
        if (doubleValue == 0.0) {
            if (isModulo) {
                handler.report(DivisionByZeroError.moduloByZero(line, "PYTHON"));
            } else {
                handler.report(new DivisionByZeroError(line, "PYTHON"));
            }
        }
    }


    private Object extractValue(ASTNode node) {
        if (node == null) return null;

        if (node instanceof Identifier) {
            String name = ((Identifier) node).name;

            if (knownConstants.containsKey(name)) {
                return knownConstants.get(name);
            }

            SymbolTable.Symbol sym = symbolTable.lookupInAllScopes(name);
            if (sym != null) {
                return sym.getValue();
            }
            return null;
        }

        if (node instanceof ParenExpr) {
            return extractValue(((ParenExpr) node).expr);
        }

        if (node instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) node;
            if (cc.suffixes == null || cc.suffixes.isEmpty()) {

                return extractValue(cc.base);
            }
            return null;
        }

        if (node instanceof ArithExpr) {
            ArithExpr arith = (ArithExpr) node;
            if (arith.terms != null && arith.terms.size() == 1) {
                return extractValue(arith.terms.get(0));
            }
        }

        return extractLiteralValue(node);
    }


    private Object extractLiteralValue(ASTNode node) {
        if (node == null) return null;

        if (node instanceof IntegerLiteral) {
            return ((IntegerLiteral) node).value;
        }
        if (node instanceof FloatLiteral) {
            return ((FloatLiteral) node).value;
        }
        if (node instanceof BoolLiteral) {
            return ((BoolLiteral) node).value ? 1 : 0;
        }
        return null;
    }

    /** تحويل أي Object إلى Number */
    private Number toNumber(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return (Number) value;
        if (value instanceof String) {
            try {
                String s = (String) value;
                if (s.contains(".")) return Double.parseDouble(s);
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }


    private void checkComparisonExpr(ComparisonExpr node) {
        checkNode(node.first);
        if (node.rest == null || node.rest.isEmpty()) return;
        for (ASTNode rightNode : node.rest) checkNode(rightNode);
    }

    private void checkArgList(ArgList node) {
        for (Arg arg : node.args) {
            if (arg instanceof ExprArg)   checkNode(((ExprArg) arg).expr);
            if (arg instanceof AssignArg) checkNode(((AssignArg) arg).value);
        }
    }

    private void checkListLiteral(ListLiteral node) {
        for (ASTNode elem : node.elements) checkNode(elem);
    }

    private void checkDictLiteral(DictLiteral node) {
        for (Pair p : node.pairs) checkNode(p.value);
    }
}
