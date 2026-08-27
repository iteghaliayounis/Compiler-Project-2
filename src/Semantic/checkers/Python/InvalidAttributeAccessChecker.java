package Semantic.checkers.Python;

import AST.ASTNode;
import AST.Program;
import AST.CompoundStmt.FuncDef;
import AST.CompoundStmt.FlowStmt.ForStmt;
import AST.CompoundStmt.FlowStmt.IfStmt;
import AST.CompoundStmt.FlowStmt.TryStmt.TryStmt;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.CallSuffixes.*;
import AST.Expressions.Expr.ComparisonExpr;
import AST.GeneratorExpr.ArithExpr;
import AST.GeneratorExpr.GenExpr;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.DictLiteral;
import AST.ListDictPair.Pair;
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

import Semantic.errors.InvalidAttributeAccessError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.PythonTypeInference;

import SymbolTable.SymbolTable;

import java.util.Map;
import java.util.Set;

/**
 * InvalidAttributeAccessChecker (Python) — يفحص AttributeError الحقيقي
 *
 *   x = "hi";  x.push("!")   ->  AttributeError: 'str' object has no attribute 'push'
 *   y = 5;     y.upper()     ->  AttributeError: 'int' object has no attribute 'upper'
 *   z = None;  z.upper()     ->  AttributeError: 'NoneType' object has no attribute 'upper'
 *
 * القاعدة الذهبية: أي x.attr نتحقق منها دائمًا -- حتى لو x = None.
 * (عمليات زي + - [] () for len هي مسؤولية TypeErrorChecker مش هون).
 *
 * إذا نوع x غير معروف (UNKNOWN) أو نوع مش موجود أصلًا بالجدول (FUNCTION/RANGE/TUPLE/SET)
 * لا نُبلغ عن خطأ -- "False Positive أسوأ من False Negative".
 */
public class InvalidAttributeAccessChecker {

    private final SymbolTable          symbolTable;
    private final SemanticErrorHandler handler;

    private static final Map<String, Set<String>> ALLOWED_ATTRS = Map.of(

        "STRING", Set.of(
            "capitalize","casefold","center","count","encode","endswith","expandtabs",
            "find","format","format_map","index","isalnum","isalpha","isascii","isdecimal",
            "isdigit","isidentifier","islower","isnumeric","isprintable","isspace","istitle",
            "isupper","join","ljust","lower","lstrip","maketrans","partition","removeprefix",
            "removesuffix","replace","rfind","rindex","rjust","rpartition","rsplit","rstrip",
            "split","splitlines","startswith","strip","swapcase","title","translate","upper","zfill"
        ),

        "LIST", Set.of(
            "append","clear","copy","count","extend","index","insert",
            "pop","remove","reverse","sort"
        ),

        "DICT", Set.of(
            "clear","copy","fromkeys","get","items","keys",
            "pop","popitem","setdefault","update","values"
        ),

        "INT", Set.of(
            "bit_length","bit_count","to_bytes","from_bytes",
            "conjugate","as_integer_ratio","is_integer","numerator","denominator","real","imag"
        ),

        "FLOAT", Set.of(
            "is_integer","hex","fromhex","as_integer_ratio","conjugate","real","imag"
        ),

        "BOOL", Set.of(
            "bit_length","bit_count","to_bytes","from_bytes",
            "conjugate","as_integer_ratio","is_integer","numerator","denominator","real","imag"
        ),

        "NONE", Set.of()
    );

    private static final Set<String> STRING_RETURNING_METHODS = Set.of(
        "upper","lower","strip","lstrip","rstrip","replace","format","format_map",
        "capitalize","casefold","center","expandtabs","join","ljust","rjust",
        "swapcase","title","translate","removeprefix","removesuffix","zfill"
    );

    public InvalidAttributeAccessChecker(SymbolTable symbolTable, SemanticErrorHandler handler) {
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
        else if (node instanceof GenExpr)         checkGenExpr((GenExpr) node);
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
        else if (node instanceof TargetID)        { /* لا فحص */ }
    }

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

        if (node.suffixes == null || node.suffixes.isEmpty()) return;

        String currentType = PythonTypeInference.inferType(node.base, symbolTable);

        for (CallSuffix suffix : node.suffixes) {

            if (suffix instanceof AttributeAccess) {
                AttributeAccess attrNode = (AttributeAccess) suffix;
                currentType = checkAttribute(currentType, attrNode, node.getLineNumber());
            }
            else if (suffix instanceof FunctionCall) {
                FunctionCall fc = (FunctionCall) suffix;
                if (fc.args != null) checkArgList(fc.args);
                // ما بنغيّر currentType هون: النوع الفعلي بعد الاستدعاء
                // تحدد أصلاً بالـ AttributeAccess السابق (لو STRING_RETURNING_METHODS)
            }
            else if (suffix instanceof IndexAccess) {
                checkNode(((IndexAccess) suffix).index);
                currentType = "UNKNOWN";
            }
        }
    }

    private String checkAttribute(String currentType, AttributeAccess attrNode, int line) {
        String type = PythonTypeInference.normalizeType(currentType);
        String attr = attrNode.attribute;

        if (!ALLOWED_ATTRS.containsKey(type)) {
            return "UNKNOWN";
        }

        Set<String> allowed = ALLOWED_ATTRS.get(type);
        if (!allowed.contains(attr)) {
            String pyType = PythonTypeInference.toPythonTypeName(type);
            handler.report(new InvalidAttributeAccessError(pyType, attr, line, "PYTHON"));
            return "UNKNOWN";
        }

        if ("STRING".equals(type) && STRING_RETURNING_METHODS.contains(attr)) {
            return "STRING";
        }
        return "UNKNOWN";
    }

    private void checkArithExpr(ArithExpr node) {
        if (node.terms == null) return;
        for (ASTNode term : node.terms) checkNode(term);
    }

    private void checkComparisonExpr(ComparisonExpr node) {
        checkNode(node.first);
        if (node.rest != null) for (ASTNode r : node.rest) checkNode(r);
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
