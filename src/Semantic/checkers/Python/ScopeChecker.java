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
import AST.Parameters;
import AST.Arg.ArgList;
import AST.Arg.Arg;
import AST.Arg.ExprArg;
import AST.Arg.AssignArg;
import AST.Target.TargetID;
import AST.Target.TargetCall;
import AST.ListDictPair.Pair;

import Semantic.errors.ScopeError;
import Semantic.handlers.SemanticErrorHandler;

import SymbolTable.SymbolTable;

import java.util.*;

<<<<<<< HEAD

=======
/**
 * ScopeChecker (Python) — يفحص أخطاء Scope
 *
 * ⚠️ المنهجية:
 *   الـ Checker يدير scopes بنفسه أثناء traversal (مو يعتمد على SymbolTable).
 *   لما يدخل FuncDef → push scope + define params
 *   لما يخرج من FuncDef → pop scope
 *   لما يلاقي assignment → define المتغير في current scope
 *   لما يلاقي Identifier usage → يتأكد إنو موجود بـ current scope stack
 *
 *   لو المتغير مش متاح بـ current scope stack → ما منبلّغ إشي من هون (منترك
 *   المجال لتشيكرز تانية مختصة، شوفي الملاحظة تحت).
 *
 * ⚠️ ملاحظة عن Python:
 *   Python عندها function-level scoping (مو block-level):
 *     - المتغيرات بـ if/for/while بتضل متاحة بعد البلوك
 *     - بس FuncDef بتنشئ scope جديد
 *   فإحنا بن push/pop بس مع FuncDef.
 *
 * ★ تصحيح (كان فيه bug اكتشفته غالية): الفحص القديم كان فيه خطوة تانية بعد
 *   isAccessible — لو المتغير "معرّف بمكان ما" (allDefinedVars عالمي بيتراكم
 *   عبر كل دوال الملف من غير ما ينمسح) كان يطلّع ScopeError (UnboundLocalError).
 *   المشكلة: أي متغير محلي بأي دالة، حتى لو بعيدة تمامًا ومالها علاقة، كان يخلي
 *   الفحص يعتقد إنو الاسم "معرّف بمكان ما" ويطلّع ScopeError غلط.
 *
 *   لما جربنا نصلّحها بحيث تحسب locals كل دالة لحالها (بمسح مسبق لجسمها، نفس
 *   منطق بايثون الحقيقي بإنو أي اسم بينعيّن بمكان ما بجسم الدالة بيصير محلي
 *   لكامل الدالة)، اكتشفنا إنو هاد بالضبط نفس الشغل يلي عم يعمله بشكل صحيح
 *   ومن غير تكرار [[UseBeforeAssignmentChecker]] (تشيكر مستقل تبع راما).
 *   فبدل ما نكرر نفس التقرير مرتين لنفس السطر، خلّينا ScopeChecker يقتصر بس
 *   على isAccessible ويسكت لما يفشل — UseBeforeAssignmentChecker مسؤولة عن
 *   الاستخدام-قبل-التعريف بنفس الدالة، وUndefinedVariableChecker مسؤولة عن
 *   المتغير غير المعرّف إطلاقًا.
 */
>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
public class ScopeChecker {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    private final List<Set<String>> scopeStack = new ArrayList<>();

<<<<<<< HEAD
    private final Set<String> allDefinedVars = new HashSet<>();

=======
    /** Built-in functions في Python */
>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
    private static final Set<String> BUILTINS = new HashSet<>(Arrays.asList(
            "print", "len", "range", "int", "float", "str", "bool", "list", "dict",
            "tuple", "set", "type", "isinstance", "hasattr", "getattr", "setattr",
            "enumerate", "zip", "map", "filter", "sorted", "reversed", "sum",
            "min", "max", "abs", "round", "open", "next", "iter", "super",
            "object", "Exception", "ValueError", "TypeError", "KeyError",
            "IndexError", "True", "False", "None", "__name__", "__main__",
            "global", "nonlocal", "input", "Flask", "render_template",
            "request", "session", "redirect", "url_for", "abort"
    ));

    public ScopeChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler     = handler;

        //Global scope
        scopeStack.add(new HashSet<>());
        // builtins
        scopeStack.get(0).addAll(BUILTINS);
    }

    public void check(ASTNode root) {
        checkNode(root);
    }


    private void pushScope() {
        scopeStack.add(new HashSet<>());
    }

    private void popScope() {
        if (scopeStack.size() > 1) scopeStack.remove(scopeStack.size() - 1);
    }

    private void define(String name) {
        if (name == null) return;
        scopeStack.get(scopeStack.size() - 1).add(name);
    }

    private boolean isAccessible(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            if (scopeStack.get(i).contains(name)) return true;
        }
        return false;
    }
<<<<<<< HEAD
    private boolean wasEverDefined(String name) {
        if (allDefinedVars.contains(name)) return true;
        // fallback: SymbolTable (imports, etc.)
        return symbolTable.lookupInAllScopes(name) != null;
    }


=======

    // ═══════════════════════════════════════════════════════════════════
    //  Dispatcher
    // ═══════════════════════════════════════════════════════════════════
>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
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
        else if (node instanceof Identifier)      checkIdentifier((Identifier) node);
    }


    private void checkProgram(Program node) {
        for (ASTNode child : node.elements) checkNode(child);
    }


    private void checkFuncDef(FuncDef node) {
        define(node.name);

        pushScope();

        if (node.parameters != null && node.parameters.names != null) {
            for (String param : node.parameters.names) {
                define(param);
            }
        }

        for (ASTNode stmt : node.body) checkNode(stmt);

        boolean isRouteFunction = false;
        if (node.decorators != null) {
            for (AST.Decorator d : node.decorators) {
                if (d.name != null && d.name.getParts() != null
                        && !d.name.getParts().isEmpty()) {
                    String lastPart = d.name.getParts().get(d.name.getParts().size() - 1);
                    if ("route".equals(lastPart)) {
                        isRouteFunction = true;
                        break;
                    }
                }
            }
        }

        if (!isRouteFunction) {
            popScope();
        }
    }

    private void checkExprStmt(ExprStmt node) {
        // 1) افحص القيمة (استخدام)
        if (node.value != null) checkNode(node.value);
        // 2) عرّف الـ target (تعريف)
        if (node.target instanceof TargetID) {
            define(((TargetID) node.target).name);
        } else if (node.target != null) {
            checkNode(node.target);
        }
    }


    private void checkIfStmt(IfStmt node) {
        checkNode(node.condition);
        for (ASTNode stmt : node.body) checkNode(stmt);
    }


    private void checkForStmt(ForStmt node) {
        checkNode(node.iterable);

        if (node.var != null) {
            define(node.var);
        }
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    private void checkTryStmt(TryStmt node) {
        for (ASTNode stmt : node.tryBlock)     checkNode(stmt);
        for (TryStmt.CatchBlock cb : node.catches) {
            // Exception variable بـ Python بتضيف متاح بعد except
            if (cb.exceptionName != null) define(cb.exceptionName);
            for (ASTNode stmt : cb.body)  checkNode(stmt);
        }
        for (ASTNode stmt : node.finallyBlock) checkNode(stmt);
    }


<<<<<<< HEAD
    private void checkIdentifier(Identifier node) {
        if (node.name == null) return;

        for (int i = 0; i < scopeStack.size(); i++) {
        }
        if (isAccessible(node.name)) {
            return;
        }

        if (wasEverDefined(node.name)) {
            handler.report(new ScopeError(node.name, node.getLineNumber(), "PYTHON"));
        }
=======
    /**
     * ★ checkIdentifier
     *
     *  لو isAccessible(name) → تمام (موجود بـ current scope stack)
     *  غير هيك → منسكت، ما منبلّغ إشي من هون. الاستخدام-قبل-التعريف بنفس
     *  الدالة مسؤولية [[UseBeforeAssignmentChecker]]، والمتغير غير المعرّف
     *  إطلاقًا مسؤولية UndefinedVariableChecker (شغل غالية) — تجنّبًا لتكرار
     *  نفس الخطأ من تشيكرين مختلفين لنفس السطر.
     */
    private void checkIdentifier(Identifier node) {
        if (node.name == null) return;
        if (isAccessible(node.name)) return;  // ✓ تمام — غير هيك منسكت
>>>>>>> d3db31e57ef184a9ccb975529d83323ce81cf1f1
    }

    //  Expressions
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
        for (ASTNode term : node.terms) checkNode(term);
    }

    private void checkComparisonExpr(ComparisonExpr node) {
        checkNode(node.first);
        if (node.rest == null) return;
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
