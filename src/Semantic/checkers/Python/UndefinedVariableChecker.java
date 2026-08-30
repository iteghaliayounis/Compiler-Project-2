package Semantic.checkers.Python;

import AST.*;
import AST.Arg.*;
import AST.CompoundStmt.FuncDef;
import AST.CompoundStmt.FlowStmt.ForStmt;
import AST.CompoundStmt.FlowStmt.IfStmt;
import AST.CompoundStmt.FlowStmt.TryStmt.TryStmt;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.CallSuffixes.*;
import AST.Expressions.Expr.*;
import AST.GeneratorExpr.ArithExpr;
import AST.GeneratorExpr.GenExpr;
import AST.ListDictPair.*;
import AST.Statements.ExprStmt.ExprStmt;
import AST.Statements.SimpleStmt;
import AST.Statements.SmallStmt.ReturnStmt;
import AST.Statements.SmallStmt.RaiseStmt;
import AST.Target.*;
import Semantic.errors.UndefinedVarError;
import Semantic.handlers.SemanticErrorHandler;
import SymbolTable.SymbolTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UndefinedVariableChecker {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    private static final Set<String> BUILTINS = new HashSet<>(Arrays.asList(
            "print", "len", "range", "int", "float", "str", "bool", "list", "dict",
            "tuple", "set", "type", "isinstance", "hasattr", "getattr", "setattr",
            "enumerate", "zip", "map", "filter", "sorted", "reversed", "sum",
            "min", "max", "abs", "round", "open", "next", "iter", "super",
            "object", "Exception", "ValueError", "TypeError", "KeyError",
            "IndexError", "True", "False", "None", "__name__", "__main__",
            "global" , "nonlocal" , "input", "abort"
    ));

    public UndefinedVariableChecker(SymbolTable symbolTable,
                                    SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler     = handler;
    }

    public void check(ASTNode node) {
        checkNode(node);
    }

    private void checkNode(ASTNode node) {
        if (node == null) return;

        if      (node instanceof Program)         checkProgram((Program) node);
        else if (node instanceof FuncDef)         checkFuncDef((FuncDef) node);
        else if (node instanceof GeneratorExpr) checkNode(((GeneratorExpr) node).genExpr);
        else if (node instanceof GenExpr)         checkGenExpr((GenExpr) node);
        else if (node instanceof SimpleStmt)      checkNode(((SimpleStmt) node).smallStmt);
        else if (node instanceof ExprStmt)        checkExprStmt((ExprStmt) node);
        else if (node instanceof ReturnStmt)  checkNode(((ReturnStmt) node).value);
        else if (node instanceof RaiseStmt)       checkNode(((RaiseStmt) node).exception);
        else if (node instanceof IfStmt)          checkIfStmt((IfStmt) node);
        else if (node instanceof ForStmt)         checkForStmt((ForStmt) node);
        else if (node instanceof TryStmt)         checkTryStmt((TryStmt) node);
        else if (node instanceof CallChainExpr)   checkCallChain((CallChainExpr) node);
        else if (node instanceof Identifier)      checkIdentifier((Identifier) node);
        else if (node instanceof ArithExpr)       checkArithExpr((ArithExpr) node);
        else if (node instanceof ComparisonExpr)  checkComparisonExpr((ComparisonExpr) node);

        else if (node instanceof ArgList)         checkArgList((ArgList) node);
        else if (node instanceof ParenExpr)       checkNode(((ParenExpr) node).expr);
        else if (node instanceof ListAtom)    checkNode(((ListAtom) node).listLiteral);
        else if (node instanceof ListLiteral)     checkListLiteral((ListLiteral) node);
        else if (node instanceof TargetID) {
            String name = ((TargetID) node).name;
            if (!BUILTINS.contains(name) && symbolTable.lookupInAllScopes(name) == null) {

                handler.report(new UndefinedVarError(name, node.getLineNumber(), "PYTHON"));
            }
        }
        else if (node instanceof TargetCall) {
            checkNode(((TargetCall) node).callChain);
        }
        else if (node instanceof DictAtom)     checkNode(((DictAtom) node).dictLiteral);
        else if (node instanceof DictLiteral)  checkDictLiteral((DictLiteral) node);

    }

    private void checkProgram(Program node) {
        for (ASTNode child : node.elements) checkNode(child);
    }

    private void checkFuncDef(FuncDef node) {
        for (Decorator d : node.decorators) {
            if (d.name != null && !d.name.getParts().isEmpty()) {
                String firstName = d.name.getParts().get(0);
                if (!BUILTINS.contains(firstName) && symbolTable.lookupInAllScopes(firstName) == null) {

                    handler.report(new UndefinedVarError(firstName, node.getLineNumber(), "PYTHON"));
                }
            }
            if (d.args != null) checkArgList(d.args);
        }
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    private void checkExprStmt(ExprStmt node) {
        if (node.value != null) {
            checkNode(node.value);
        } else {
            checkNode(node.target);
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

    private void checkCallChain(CallChainExpr node) {

        checkNode(node.base);
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

    private void checkIdentifier(Identifier node) {
        String name = node.name;
        if (BUILTINS.contains(name)) return;
        if (symbolTable.lookupInAllScopes(name) == null) {
            handler.report(new UndefinedVarError(name, node.getLineNumber(), "PYTHON"));
        }
    }

    private void checkArithExpr(ArithExpr node) {
        for (ASTNode term : node.terms) checkNode(term);
    }

    private void checkComparisonExpr(ComparisonExpr node) {
        checkNode(node.first);
        for (ASTNode right : node.rest) checkNode(right);
    }

    private void checkGenExpr(GenExpr node) {
        checkNode(node.iterable);
        checkNode(node.expr);
        if (node.condition != null) checkNode(node.condition);
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