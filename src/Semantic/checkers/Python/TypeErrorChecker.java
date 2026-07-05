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
import AST.GeneratorExpr.GenExpr;
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

import Semantic.errors.TypeError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.PythonTypeInference;
import Semantic.util.TypeCompatibility;

import SymbolTable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * TypeErrorChecker (Python) — يفحص أخطاء النوع في العملية نفسها
 *
 * الحالات التي يفحصها (المتاحة في الـ Parser الحالي):
 *   1) for i in 5:           →  'int' object is not iterable
 *   2) len(10)               →  object of type 'int' has no len()
 *   3) x = 5; x[0]           →  'int' object is not subscriptable
 *   4) "Sara" + 4            →  can only concatenate str (not "int") to str
 *   5) "Sara" - 4            →  unsupported operand type(s) for -: 'str' and 'int'
 *   6) "Sara" < 4            →  '<' not supported between instances of 'str' and 'int'
 *   7) x = None; x + 5       →  unsupported operand type(s) for +: 'NoneType' and 'int'
 *
 * ⚠️ لا يفحص: الإسناد الخاطئ (c: int = "hello") — هذا عمل TypeMismatchChecker
 * ⚠️ لا يفحص: استدعاء غير قابل للاستدعاء (x()) — هذا عمل InvalidFunctionCallChecker
 * ⚠️ ملاحظة: الـ Parser الحالي يدعم + و - فقط (لا يدعم * / % **)
 */
public class TypeErrorChecker {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    public TypeErrorChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
        this.symbolTable = symbolTable;
        this.handler     = handler;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Entry Point
    // ═══════════════════════════════════════════════════════════════════
    public void check(ASTNode root) {
        checkNode(root);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Dispatcher — يوجه كل عقدة للـ method المناسبة
    // ═══════════════════════════════════════════════════════════════════
    private void checkNode(ASTNode node) {
        if (node == null) return;

        if      (node instanceof Program)         checkProgram((Program) node);
        else if (node instanceof FuncDef)         checkFuncDef((FuncDef) node);
        else if (node instanceof GenExpr)         checkGenExpr((GenExpr) node);
        else if (node instanceof SimpleStmt)      checkNode(((SimpleStmt) node).smallStmt);
        else if (node instanceof ExprStmt)        checkExprStmt((ExprStmt) node);
        else if (node instanceof ReturnStmt)      checkNode(((ReturnStmt) node).value);
        else if (node instanceof RaiseStmt)       checkNode(((RaiseStmt) node).exception);
        else if (node instanceof IfStmt)          checkIfStmt((IfStmt) node);
        else if (node instanceof ForStmt)         checkForStmt((ForStmt) node);       // ← Type Error: iteration
        else if (node instanceof TryStmt)         checkTryStmt((TryStmt) node);
        else if (node instanceof CallChainExpr)   checkCallChain((CallChainExpr) node); // ← Type Error: len(), indexing
        else if (node instanceof ArithExpr)       checkArithExpr((ArithExpr) node);     // ← Type Error: + / -
        else if (node instanceof ComparisonExpr)  checkComparisonExpr((ComparisonExpr) node); // ← Type Error: comparison
        else if (node instanceof ParenExpr)       checkNode(((ParenExpr) node).expr);
        else if (node instanceof ListAtom)        checkNode(((ListAtom) node).listLiteral);
        else if (node instanceof ListLiteral)     checkListLiteral((ListLiteral) node);
        else if (node instanceof DictAtom)        checkNode(((DictAtom) node).dictLiteral);
        else if (node instanceof DictLiteral)     checkDictLiteral((DictLiteral) node);
        else if (node instanceof TargetCall)      checkNode(((TargetCall) node).callChain);
        else if (node instanceof TargetID)        { /* لا فحص — المتغير نفسه ليس عملية */ }
        // Identifier و Literals لا تحتاج فحص هنا
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Program / FuncDef / ExprStmt / IfStmt / TryStmt
    // ═══════════════════════════════════════════════════════════════════
    private void checkProgram(Program node) {
        for (ASTNode child : node.elements) checkNode(child);
    }

    private void checkFuncDef(FuncDef node) {
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    private void checkExprStmt(ExprStmt node) {
        if (node.value != null) checkNode(node.value);
        else if (node.target != null) checkNode(node.target);
    }

    private void checkIfStmt(IfStmt node) {
        checkNode(node.condition);
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    private void checkTryStmt(TryStmt node) {
        for (ASTNode stmt : node.tryBlock)     checkNode(stmt);
        for (TryStmt.CatchBlock cb : node.catches)
            for (ASTNode stmt : cb.body)       checkNode(stmt);
        for (ASTNode stmt : node.finallyBlock) checkNode(stmt);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ ForStmt — فحص التكرار على نوع غير قابل للتكرار
    //  for i in 5:  →  'int' object is not iterable
    // ═══════════════════════════════════════════════════════════════════
    private void checkForStmt(ForStmt node) {
        String iterType = PythonTypeInference.inferType(node.iterable, symbolTable);

        if (TypeCompatibility.bothKnown(iterType, iterType)
                && !TypeCompatibility.isIterable(iterType)) {
            String pyType = PythonTypeInference.toPythonTypeName(iterType);
            handler.report(TypeError.notIterable(pyType, node.getLineNumber(), "PYTHON"));
        }

        // تابع الفحص للجسم
        for (ASTNode stmt : node.body) checkNode(stmt);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ CallChainExpr — فحص:
    //    1) len(10)                    →  object of type 'int' has no len()
    //    2) x[0] حيث x ليس قابل للفهرسة  →  'int' object is not subscriptable
    // ═══════════════════════════════════════════════════════════════════
    private void checkCallChain(CallChainExpr node) {
        checkNode(node.base);

        if (node.suffixes == null) return;

        for (CallSuffix suffix : node.suffixes) {
            if (suffix instanceof FunctionCall) {
                FunctionCall fc = (FunctionCall) suffix;
                checkFunctionCallArgs(node, fc);
                if (fc.args != null) checkArgList(fc.args);
            }
            if (suffix instanceof IndexAccess) {
                checkIndexAccess(node, (IndexAccess) suffix);
                checkNode(((IndexAccess) suffix).index);
            }
        }
    }

    /** فحص استدعاءات الدوال المدمجة — مثل len(10) */
    private void checkFunctionCallArgs(CallChainExpr node, FunctionCall fc) {
        if (!(node.base instanceof Identifier)) return;
        String funcName = ((Identifier) node.base).name;

        if ("len".equals(funcName)) {
            if (fc.args == null || fc.args.args == null || fc.args.args.isEmpty()) return;
            Arg firstArg = fc.args.args.get(0);
            ASTNode argExpr = null;
            if (firstArg instanceof ExprArg)   argExpr = ((ExprArg) firstArg).expr;
            if (firstArg instanceof AssignArg) argExpr = ((AssignArg) firstArg).value;
            if (argExpr == null) return;

            String argType = PythonTypeInference.inferType(argExpr, symbolTable);
            if (TypeCompatibility.bothKnown(argType, argType)
                    && !TypeCompatibility.hasLen(argType)) {
                String pyType = PythonTypeInference.toPythonTypeName(argType);
                handler.report(TypeError.noLen(pyType, node.getLineNumber(), "PYTHON"));
            }
        }
    }

    /** فحص الفهرسة — x[0] حيث x ليس قابل للفهرسة */
    private void checkIndexAccess(CallChainExpr node, IndexAccess suffix) {
        String baseType = PythonTypeInference.inferType(node.base, symbolTable);
        if (TypeCompatibility.bothKnown(baseType, baseType)
                && !TypeCompatibility.isSubscriptable(baseType)) {
            String pyType = PythonTypeInference.toPythonTypeName(baseType);
            handler.report(TypeError.notSubscriptable(pyType, node.getLineNumber(), "PYTHON"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ ArithExpr — فحص الجمع (+) والطرح (-) باستخدام operators الفعلي
    //  "Sara" + 4  →  can only concatenate str (not "int") to str
    //  "Sara" - 4  →  unsupported operand type(s) for -: 'str' and 'int'
    // ═══════════════════════════════════════════════════════════════════
    private void checkArithExpr(ArithExpr node) {
        if (node.terms == null || node.terms.isEmpty()) return;

        // افحص كل term على حدة (لأي أخطاء متداخلة بداخلها)
        for (ASTNode term : node.terms) checkNode(term);

        // إذا لا يوجد operators (operand واحد) → لا فحص
        if (node.operators == null || node.operators.isEmpty()) return;

        // افحص كل زوج (terms[i], terms[i+1]) مع العامل operators[i]
        for (int i = 0; i < node.operators.size() && (i + 1) < node.terms.size(); i++) {
            String op = node.operators.get(i);  // "PLUS" أو "MINUS"
            ASTNode leftNode  = node.terms.get(i);
            ASTNode rightNode = node.terms.get(i + 1);

            String leftType  = PythonTypeInference.inferType(leftNode,  symbolTable);
            String rightType = PythonTypeInference.inferType(rightNode, symbolTable);

            checkArithPair(op, leftType, rightType, node.getLineNumber());
        }
    }

    /**
     * فحص زوج واحد من ArithExpr مع العامل الفعلي
     * الـ Parser يدعم + و - فقط
     * الـ operator قد يكون "PLUS"/"MINUS" أو "+"/"-" حسب الـ Visitor
     */
    private void checkArithPair(String op, String leftType, String rightType, int line) {
        if (!TypeCompatibility.bothKnown(leftType, rightType)) return;

        String lt  = PythonTypeInference.normalizeType(leftType);
        String rt  = PythonTypeInference.normalizeType(rightType);
        String plt = PythonTypeInference.toPythonTypeName(lt);
        String prt = PythonTypeInference.toPythonTypeName(rt);

        // ── تطبيع العامل: قد يكون "PLUS"/"MINUS" أو "+"/"-" ──
        boolean isPlus  = op != null && (op.equalsIgnoreCase("PLUS")  || "+".equals(op));
        boolean isMinus = op != null && (op.equalsIgnoreCase("MINUS") || "-".equals(op));
        String opStr = isPlus ? "+" : (isMinus ? "-" : op);

        // ── None + أي شيء (كل العمليات تفشل على None) ──
        if ("NONE".equals(lt) || "NONE".equals(rt)) {
            handler.report(TypeError.unsupportedOperand(opStr, plt, prt, line, "PYTHON"));
            return;
        }

        // ── الجمع (+) ──
        if (isPlus) {
            if (!TypeCompatibility.isAddCompatible(lt, rt)) {
                if ("STRING".equals(lt) && !"STRING".equals(rt)) {
                    handler.report(TypeError.concatMismatch(plt, prt, line, "PYTHON"));
                } else if ("LIST".equals(lt) && !"LIST".equals(rt)) {
                    handler.report(TypeError.concatMismatch(plt, prt, line, "PYTHON"));
                } else {
                    handler.report(TypeError.unsupportedOperand("+", plt, prt, line, "PYTHON"));
                }
            }
            return;
        }

        // ── الطرح (-) ──
        if (isMinus) {
            if (!TypeCompatibility.isSubCompatible(lt, rt)) {
                handler.report(TypeError.unsupportedOperand("-", plt, prt, line, "PYTHON"));
            }
            return;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ ComparisonExpr — فحص المقارنة بين أنواع غير متوافقة
    //  "Sara" < 4  →  '<' not supported between instances of 'str' and 'int'
    // ═══════════════════════════════════════════════════════════════════
    private void checkComparisonExpr(ComparisonExpr node) {
        checkNode(node.first);
        if (node.rest == null || node.rest.isEmpty()) return;

        String leftType = PythonTypeInference.inferType(node.first, symbolTable);

        for (int i = 0; i < node.rest.size(); i++) {
            ASTNode rightNode = node.rest.get(i);
            checkNode(rightNode);
            String rightType = PythonTypeInference.inferType(rightNode, symbolTable);

            String operator = (node.operators != null && i < node.operators.size())
                    ? node.operators.get(i) : null;

            if (isOrderingOperator(operator)
                    && TypeCompatibility.bothKnown(leftType, rightType)
                    && !TypeCompatibility.isComparisonCompatible(leftType, rightType)) {

                String plt = PythonTypeInference.toPythonTypeName(leftType);
                String prt = PythonTypeInference.toPythonTypeName(rightType);

                handler.report(TypeError.comparisonNotSupported(operator, plt, prt,
                        node.getLineNumber(), "PYTHON"));
            }
            leftType = rightType;
        }
    }

    /** فقط عوامل الترتيب (<, >, <=, >=) تثير TypeError */
    private boolean isOrderingOperator(String operator) {
        return "<".equals(operator) || ">".equals(operator)
                || "<=".equals(operator) || ">=".equals(operator);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  GenExpr / ArgList / ListLiteral / DictLiteral
    // ═══════════════════════════════════════════════════════════════════
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
