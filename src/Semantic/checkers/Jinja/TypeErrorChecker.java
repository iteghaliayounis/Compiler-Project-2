package Semantic.checkers.Jinja;

import AstHtml.*;

import Semantic.errors.TypeError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.JinjaTypeInference;
import Semantic.util.PythonTypeInference;
import Semantic.util.TypeCompatibility;

import symbol_table.SymbolTable;

import java.util.List;

/**
 * TypeErrorChecker (Jinja) — يفحص أخطاء النوع في قوالب Jinja2
 *
 * الحالات التي يفحصها:
 *   1) {% for i in 5 %}            →  'int' object is not iterable
 *   2) {{ age + name }}            →  unsupported operand type(s) for +: 'int' and 'str'
 *                                    (حيث age=int, name=str)
 *   3) {{ x[0] }} حيث x=int        →  'int' object is not subscriptable
 *   4) {{ "a" * "b" }}             →  can't multiply sequence by non-int of type 'str'
 *
 * ⚠️ لا يفحص: الفلاتر (age|upper) — هذا عمل FilterTypeMismatchChecker (Type Mismatch)
 * ⚠️ لا يفحص: المتغيرات غير المعرفة — هذا عمل UndefinedVariableChecker (غالية)
 */
public class TypeErrorChecker {

    private final symbol_table.SymbolTable jinjaST;
    private final SemanticErrorHandler    handler;

    public TypeErrorChecker(symbol_table.SymbolTable jinjaST, SemanticErrorHandler handler) {
        this.jinjaST = jinjaST;
        this.handler = handler;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Entry Point
    // ═══════════════════════════════════════════════════════════════════
    public void check(AstNode root) {
        if (root != null) checkNode(root);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Dispatcher
    // ═══════════════════════════════════════════════════════════════════
    private void checkNode(AstNode node) {
        if (node == null) return;

        // ── Structural nodes ───────────────────────────────────────
        if      (node instanceof TemplateNode)    checkTemplate((TemplateNode) node);
        else if (node instanceof ForNode)         checkFor((ForNode) node);         // ← Type Error: iteration
        else if (node instanceof IfNode)          checkIf((IfNode) node);
        else if (node instanceof SetNode)         checkSet((SetNode) node);
        else if (node instanceof WithNode)        checkWith((WithNode) node);
        else if (node instanceof BlockNode)       checkBlock((BlockNode) node);
        else if (node instanceof MacroNode)       checkMacro((MacroNode) node);
        else if (node instanceof FilterBlockNode) checkFilterBlock((FilterBlockNode) node);
        else if (node instanceof RawNode)         { /* نص خام */ }

        // ── Output: {{ expr }} ────────────────────────────────────
        else if (node instanceof JinjaVarOutputNode) checkNode(((JinjaVarOutputNode) node).getExpression());

            // ── Expressions ───────────────────────────────────────────
        else if (node instanceof BinaryOpNode)      checkBinaryOp((BinaryOpNode) node);       // ← Type Error: arithmetic
        else if (node instanceof IndexNode)         checkIndex((IndexNode) node);             // ← Type Error: subscriptable
        else if (node instanceof SliceNode)         checkSlice((SliceNode) node);
        else if (node instanceof UnaryOpNode)       checkNode(((UnaryOpNode) node).getOperand());
        else if (node instanceof TernaryNode)       checkTernary((TernaryNode) node);
        else if (node instanceof FilterNode)        checkFilter((FilterNode) node);
        else if (node instanceof CallNode)          checkCall((CallNode) node);
        else if (node instanceof AttributeAccessNode) checkNode(((AttributeAccessNode) node).getObject());
        else if (node instanceof VariableNode)      { /* لا فحص — المتغير نفسه ليس عملية */ }

        // ── HTML Elements ─────────────────────────────────────────
        else if (node instanceof ElementNode)       checkElement((ElementNode) node);
        else if (node instanceof VoidElementNode)   checkVoidElement((VoidElementNode) node);
        else if (node instanceof StyleElementNode)  checkStyleElement((StyleElementNode) node);
        else if (node instanceof CssJinjaValueNode) checkNode(((CssJinjaValueNode) node).getExpression());
        else if (node instanceof CssRuleSetNode)    checkCssRuleSet((CssRuleSetNode) node);
        else if (node instanceof CssDeclarationNode) checkCssDeclaration((CssDeclarationNode) node);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Structural Checks
    // ═══════════════════════════════════════════════════════════════════
    private void checkTemplate(TemplateNode node) {
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    // ★ ForNode — فحص التكرار على نوع غير قابل للتكرار
    // {% for i in 5 %}  →  'int' object is not iterable
    private void checkFor(ForNode node) {
        String iterType = JinjaTypeInference.inferType(node.getIterable(), jinjaST);

        if (TypeCompatibility.bothKnown(iterType, iterType)
                && !TypeCompatibility.isIterable(iterType)) {
            String pyType = PythonTypeInference.toPythonTypeName(iterType);
            handler.report(TypeError.notIterable(pyType, node.getLine(), "JINJA"));
        }

        // افحص جسم الحلقة
        for (AstNode child : node.getBody()) checkNode(child);
        if (node.getElseBody() != null) {
            for (AstNode child : node.getElseBody()) checkNode(child);
        }
    }

    private void checkIf(IfNode node) {
        List<ExpressionNode> conditions = node.getConditions();
        List<List<AstNode>> bodies = node.getBodies();

        for (int i = 0; i < conditions.size(); i++) {
            checkNode(conditions.get(i));
            if (i < bodies.size()) {
                for (AstNode child : bodies.get(i)) checkNode(child);
            }
        }
        if (node.getElseBody() != null) {
            for (AstNode child : node.getElseBody()) checkNode(child);
        }
    }

    private void checkSet(SetNode node) {
        checkNode(node.getValue());
    }

    private void checkWith(WithNode node) {
        for (WithNode.Assignment assign : node.getAssignments()) {
            checkNode(assign.value);
        }
        for (AstNode child : node.getBody()) checkNode(child);
    }

    private void checkBlock(BlockNode node) {
        for (AstNode child : node.getBody()) checkNode(child);
    }

    private void checkMacro(MacroNode node) {
        for (MacroNode.Param param : node.getParams()) {
            if (param.defaultValue != null) checkNode(param.defaultValue);
        }
        for (AstNode child : node.getBody()) checkNode(child);
    }

    private void checkFilterBlock(FilterBlockNode node) {
        for (ExpressionNode arg : node.getFilterArgs()) checkNode(arg);
        for (AstNode child : node.getBody()) checkNode(child);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ BinaryOpNode — فحص العمليات الحسابية بين أنواع غير متوافقة
    //  {{ age + name }}  →  unsupported operand type(s) for +: 'int' and 'str'
    // ═══════════════════════════════════════════════════════════════════
    private void checkBinaryOp(BinaryOpNode node) {
        // افحص المعاملات أولاً
        checkNode(node.getLeft());
        checkNode(node.getRight());

        BinaryOpNode.Operator op = node.getOperator();
        String leftType  = JinjaTypeInference.inferType(node.getLeft(),  jinjaST);
        String rightType = JinjaTypeInference.inferType(node.getRight(), jinjaST);

        // لا نعرف نوع أحد المعاملين → لا نبلغ (تجنب False Positives)
        if (!TypeCompatibility.bothKnown(leftType, rightType)) return;

        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);
        String plt = PythonTypeInference.toPythonTypeName(lt);
        String prt = PythonTypeInference.toPythonTypeName(rt);

        int line = node.getLine();

        // ── الجمع (+) ──
        if (op == BinaryOpNode.Operator.ADD) {
            if (!TypeCompatibility.isAddCompatible(lt, rt)) {
                if ("STRING".equals(lt) && !"STRING".equals(rt)) {
                    handler.report(TypeError.concatMismatch(plt, prt, line, "JINJA"));
                } else if ("LIST".equals(lt) && !"LIST".equals(rt)) {
                    handler.report(TypeError.concatMismatch(plt, prt, line, "JINJA"));
                } else {
                    handler.report(TypeError.unsupportedOperand("+", plt, prt, line, "JINJA"));
                }
            }
            return;
        }

        // ── الطرح (-) ──
        if (op == BinaryOpNode.Operator.SUB) {
            if (!TypeCompatibility.isSubCompatible(lt, rt)) {
                handler.report(TypeError.unsupportedOperand("-", plt, prt, line, "JINJA"));
            }
            return;
        }

        // ── الضرب (*) ──
        if (op == BinaryOpNode.Operator.MUL) {
            if (!TypeCompatibility.isMulCompatible(lt, rt)) {
                if (("STRING".equals(lt) || "LIST".equals(lt)) && !"INT".equals(rt) && !"FLOAT".equals(rt)) {
                    handler.report(TypeError.multiplySequenceByNonInt(plt, prt, line, "JINJA"));
                } else {
                    handler.report(TypeError.unsupportedOperand("*", plt, prt, line, "JINJA"));
                }
            }
            return;
        }

        // ── القسمة (/) ──
        if (op == BinaryOpNode.Operator.DIV) {
            if (!TypeCompatibility.isDivCompatible(lt, rt)) {
                handler.report(TypeError.unsupportedOperand("/", plt, prt, line, "JINJA"));
            }
            return;
        }

        // ── Modulo (%) ──
        if (op == BinaryOpNode.Operator.MOD) {
            if (!TypeCompatibility.isModCompatible(lt, rt)) {
                handler.report(TypeError.unsupportedOperand("%", plt, prt, line, "JINJA"));
            }
            return;
        }

        // ── Concat (~) ──
        if (op == BinaryOpNode.Operator.CONCAT) {
            // العامل ~ في Jinja2 يحوّل أي شيء إلى string → دائماً OK
            return;
        }

        // ── المقارنة الترتيبية (<, >, <=, >=) ──
        if (op == BinaryOpNode.Operator.LT || op == BinaryOpNode.Operator.GT ||
                op == BinaryOpNode.Operator.LTE || op == BinaryOpNode.Operator.GTE) {
            if (!TypeCompatibility.isComparisonCompatible(lt, rt)) {
                String opStr = op == BinaryOpNode.Operator.LT  ? "<"  :
                        op == BinaryOpNode.Operator.GT  ? ">"  :
                                op == BinaryOpNode.Operator.LTE ? "<=" : ">=";
                handler.report(TypeError.comparisonNotSupported(opStr, plt, prt, line, "JINJA"));
            }
            return;
        }

        // EQ, NEQ, IN, NOT_IN, IS, IS_NOT, AND, OR → دائماً OK في Jinja2
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ IndexNode — فحص الفهرسة على نوع غير قابل للفهرسة
    //  {{ x[0] }} حيث x=int  →  'int' object is not subscriptable
    // ═══════════════════════════════════════════════════════════════════
    private void checkIndex(IndexNode node) {
        checkNode(node.getArray());
        checkNode(node.getIndex());

        String arrType = JinjaTypeInference.inferType(node.getArray(), jinjaST);
        if (TypeCompatibility.bothKnown(arrType, arrType)
                && !TypeCompatibility.isSubscriptable(arrType)) {
            String pyType = PythonTypeInference.toPythonTypeName(arrType);
            handler.report(TypeError.notSubscriptable(pyType, node.getLine(), "JINJA"));
        }
    }

    private void checkSlice(SliceNode node) {
        checkNode(node.getArray());
        checkNode(node.getStart());
        checkNode(node.getStop());
        checkNode(node.getStep());

        // Slice يتطلب أن يكون الـ array قابل للفهرسة
        String arrType = JinjaTypeInference.inferType(node.getArray(), jinjaST);
        if (TypeCompatibility.bothKnown(arrType, arrType)
                && !TypeCompatibility.isSubscriptable(arrType)) {
            String pyType = PythonTypeInference.toPythonTypeName(arrType);
            handler.report(TypeError.notSubscriptable(pyType, node.getLine(), "JINJA"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Expressions (لا فحص نوع عليها لكن نستمر في التصفح)
    // ═══════════════════════════════════════════════════════════════════
    private void checkTernary(TernaryNode node) {
        checkNode(node.getValue());
        checkNode(node.getCondition());
        checkNode(node.getAlternative());
    }

    /**
     * FilterNode — لا فحص Type Error هنا.
     * فحص توافق الفلاتر مع الأنواع يتم في FilterTypeMismatchChecker (Type Mismatch).
     * هنا نستمر في فحص المعامل فقط.
     */
    private void checkFilter(FilterNode node) {
        checkNode(node.getOperand());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);
    }

    private void checkCall(CallNode node) {
        checkNode(node.getCallee());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HTML / CSS
    // ═══════════════════════════════════════════════════════════════════
    private void checkElement(ElementNode node) {
        for (AttributeNode attr : node.getAttributes()) checkAttribute(attr);
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    private void checkVoidElement(VoidElementNode node) {
        for (AttributeNode attr : node.getAttributes()) checkAttribute(attr);
    }

    private void checkAttribute(AttributeNode node) {
        if (node.getJinjaValue() != null) {
            checkNode(node.getJinjaValue());
        }
    }

    private void checkStyleElement(StyleElementNode node) {
        for (CssNode stmt : node.getStatements()) checkNode(stmt);
    }

    private void checkCssRuleSet(CssRuleSetNode node) {
        for (CssDeclarationNode decl : node.getDeclarations()) checkNode(decl);
    }

    private void checkCssDeclaration(CssDeclarationNode node) {
        for (CssValueNode val : node.getValues()) checkNode(val);
    }
}
