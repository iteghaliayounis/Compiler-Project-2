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

/**
 * ScopeChecker (Python) — يفحص أخطاء Scope
 *
 * الخطأ الحقيقي: UnboundLocalError
 *
 * ⚠️ المنهجية:
 *   الـ Checker يدير scopes بنفسه أثناء traversal (مو يعتمد على SymbolTable).
 *   لما يدخل FuncDef → push scope + define params
 *   لما يخرج من FuncDef → pop scope
 *   لما يلاقي assignment → define المتغير في current scope
 *   لما يلاقي Identifier usage → يتأكد إنو موجود بـ current scope stack
 *
 *   لو المتغير معرف بـ scope تاني (popped) → ScopeError
 *   لو المتغير مش معرف أبداً → UndefinedVarError (شغل غالية)
 *
 * ⚠️ ملاحظة عن Python:
 *   Python عندها function-level scoping (مو block-level):
 *     - المتغيرات بـ if/for/while بتضل متاحة بعد البلوك
 *     - بس FuncDef بتنشئ scope جديد
 *   فإحنا بن push/pop بس مع FuncDef.
 */
public class ScopeChecker {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    /** Stack of scopes — كل scope عبارة عن Set من المتغيرات */
    private final List<Set<String>> scopeStack = new ArrayList<>();

    /** كل المتغيرات المعرفة بأي scope (حتى المحذوفة) — للتمييز عن UndefinedVarError */
    private final Set<String> allDefinedVars = new HashSet<>();

    /** Built-in functions في Python */
    private static final Set<String> BUILTINS = new HashSet<>(Arrays.asList(
            "print", "len", "range", "int", "float", "str", "bool", "list", "dict",
            "tuple", "set", "type", "isinstance", "hasattr", "getattr", "setattr",
            "enumerate", "zip", "map", "filter", "sorted", "reversed", "sum",
            "min", "max", "abs", "round", "open", "next", "iter", "super",
            "object", "Exception", "ValueError", "TypeError", "KeyError",
            "IndexError", "True", "False", "None", "__name__", "__main__",
            "global", "nonlocal", "input", "Flask", "render_template",
            "request", "session", "redirect", "url_for"
    ));

    public ScopeChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler     = handler;

        //Global scope
        scopeStack.add(new HashSet<>());
        // builtins
        scopeStack.get(0).addAll(BUILTINS);
        allDefinedVars.addAll(BUILTINS);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Entry Point
    // ═══════════════════════════════════════════════════════════════════
    public void check(ASTNode root) {
        checkNode(root);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Scope helpers
    // ═══════════════════════════════════════════════════════════════════
    private void pushScope() {
        scopeStack.add(new HashSet<>());
    }

    private void popScope() {
        if (scopeStack.size() > 1) scopeStack.remove(scopeStack.size() - 1);
    }

    /** تعريف متغير في current scope */
    private void define(String name) {
        if (name == null) return;
        scopeStack.get(scopeStack.size() - 1).add(name);
        allDefinedVars.add(name);
    }

    /** هل المتغير متاح بـ current scope stack (من current لـ global)؟ */
    private boolean isAccessible(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            if (scopeStack.get(i).contains(name)) return true;
        }
        return false;
    }

    /** هل المتغير معرف بأي scope (حتى المحذوفة) أو بـ SymbolTable؟ */
    private boolean wasEverDefined(String name) {
        if (allDefinedVars.contains(name)) return true;
        // fallback: SymbolTable (imports, etc.)
        return symbolTable.lookupInAllScopes(name) != null;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Dispatcher
    // ═══════════════════════════════════════════════════════════════════
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
        else if (node instanceof TargetID)        { /* التعريف ليس استخدام */ }
        else if (node instanceof Identifier)      checkIdentifier((Identifier) node);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Program / FuncDef / ExprStmt / IfStmt / ForStmt / TryStmt
    // ═══════════════════════════════════════════════════════════════════
    private void checkProgram(Program node) {
        for (ASTNode child : node.elements) checkNode(child);
    }

    /**
     * ★ FuncDef — push scope + define function name + params + check body + pop
     *
     *  ⚠️ ملاحظة مهمة عن Route Functions:
     *  الـ route functions (يلي فيها @app.route) بـ AST تبعهم بيتقطع الـ body.
     *  فإحنا ما بنعمل pop للـ route functions عشان متغيراتها تضل متاحة.
     */
    private void checkFuncDef(FuncDef node) {
        // 1) Define function name في CURRENT scope (قبل push)
        define(node.name);

        // 2) Push new scope للـ function
        pushScope();

        // 3) Define parameters
        if (node.parameters != null && node.parameters.names != null) {
            for (String param : node.parameters.names) {
                define(param);
            }
        }

        // 4) Check body
        for (ASTNode stmt : node.body) checkNode(stmt);

        // 5) ★ Pop scope - بس للـ functions العادية (مو route functions)
        // لو فيها decorator route → ما نعمل pop (لأنو الـ AST بيتقطع)
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
        // لو route function → ما نعمل pop (المتغيرات تضل متاحة)
    }

    /**
     * ★ ExprStmt — افحص value ثم define target
     *
     *  y = 0        →  check(0), define("y")
     *  z = 10 / y   →  check(10/y), define("z")
     */
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

    /**
     * IfStmt — Python ما عندها block scoping، فما بن push/pop
     */
    private void checkIfStmt(IfStmt node) {
        checkNode(node.condition);
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    /**
     * ★ ForStmt — Python بتحتفظ بـ loop variable بعد الحلقة!
     *  for i in range(5): pass
     *  print(i)   ← صحيح بـ Python! (i = 4)
     *
     *  فإحنا بن define المتغير بـ current scope (مو بن push/pop)
     */
    private void checkForStmt(ForStmt node) {
        checkNode(node.iterable);

        // ★ تعريف متغير الـ loop بـ current scope
        if (node.var != null) {
            define(node.var);
        }

        // فحص جسم الـ for
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

    // ═══════════════════════════════════════════════════════════════════
    //  ★ الفحص الفعلي
    // ═══════════════════════════════════════════════════════════════════

    /**
     * ★ checkIdentifier — الفحص الرئيسي
     *
     *  1) لو isAccessible(name) → تمام (موجود بـ current scope stack)
     *  2) لو wasEverDefined(name) → ScopeError (موجود بـ scope تاني)
     *  3) لو مش معرف → UndefinedVarError (شغل غالية)
     */
    private void checkIdentifier(Identifier node) {
        if (node.name == null) return;

        // ★ DEBUG: شوفي حالة الـ scope وقت فحص كل identifier
     //   System.out.println("[DEBUG checkIdentifier] checking '" + node.name + "' at line " + node.getLineNumber());
       // System.out.println("[DEBUG checkIdentifier] scopeStack size: " + scopeStack.size());
        for (int i = 0; i < scopeStack.size(); i++) {
           // System.out.println("[DEBUG checkIdentifier] scope " + i + ": " + scopeStack.get(i));
        }
        //System.out.println("[DEBUG checkIdentifier] isAccessible('" + node.name + "'): " + isAccessible(node.name));

        // 1) هل المتغير متاح بـ current scope stack؟
        if (isAccessible(node.name)) {
            return;  // ✓ تمام
        }

        // 2) هل المتغير معرف بـ scope تاني (popped)؟
        if (wasEverDefined(node.name)) {
            // → ScopeError!
            handler.report(new ScopeError(node.name, node.getLineNumber(), "PYTHON"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Expressions
    // ═══════════════════════════════════════════════════════════════════
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
