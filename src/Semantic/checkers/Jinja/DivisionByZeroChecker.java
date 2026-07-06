package Semantic.checkers.Jinja;

import AstHtml.*;
import Semantic.errors.DivisionByZeroError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.JinjaTypeInference;
import Semantic.util.PythonTypeInference;

import symbol_table.SymbolTable;

/**
 * DivisionByZeroChecker (Jinja) — يفحص أخطاء القسمة على صفر في قوالب Jinja2
 *
 * الحالات التي يفحصها:
 *   1) {{ 10 / 0 }}          → ZeroDivisionError: division by zero
 *   2) {{ 10 % 0 }}          → ZeroDivisionError: integer division or modulo by zero
 *   3) {# count = 0 #}
 *      {{ price / count }}   → ZeroDivisionError: division by zero
 *
 * ⚠️ القاعدة الذهبية:
 *   لو قيمة المقسوم عليه UNKNOWN (ما معروفش وقت الـ compile)
 *   → ما نبلّغ عن خطأ (تجنب False Positives).
 */
public class DivisionByZeroChecker {

    private final SymbolTable          jinjaST;
    private final SemanticErrorHandler handler;

    public DivisionByZeroChecker(SymbolTable jinjaST, SemanticErrorHandler handler) {
        this.jinjaST  = jinjaST;
        this.handler  = handler;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Entry Point
    // ═══════════════════════════════════════════════════════════════════
    public void check(AstNode root) {
        checkNode(root);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Dispatcher — يوجّه كل عقدة للـ method المناسبة
    // ═══════════════════════════════════════════════════════════════════
    private void checkNode(AstNode node) {
        if (node == null) return;

        if (node instanceof TemplateNode) {
            for (AstNode child : ((TemplateNode) node).getChildren()) checkNode(child);
        }
        else if (node instanceof BlockNode) {
            for (AstNode child : ((BlockNode) node).getBody()) checkNode(child);
        }
        else if (node instanceof IfNode) {
            checkIfNode((IfNode) node);
        }
        else if (node instanceof ForNode) {
            checkForNode((ForNode) node);
        }
        else if (node instanceof WithNode) {
            checkWithNode((WithNode) node);
        }
        else if (node instanceof MacroNode) {
            checkMacroNode((MacroNode) node);
        }
        else if (node instanceof ElementNode) {
            checkElementNode((ElementNode) node);
        }
        else if (node instanceof StyleElementNode) {
            for (CssNode css : ((StyleElementNode) node).getStatements()) checkNode(css);
        }
        // CSS nodes
        else if (node instanceof CssJinjaValueNode) {
            checkNode(((CssJinjaValueNode) node).getExpression());
        }
        // Statements مع expressions
        else if (node instanceof JinjaVarOutputNode) {
            checkNode(((JinjaVarOutputNode) node).getExpression());
        }
        else if (node instanceof SetNode) {
            checkNode(((SetNode) node).getValue());
        }
        // Expressions
        else if (node instanceof BinaryOpNode) {
            checkBinaryOp((BinaryOpNode) node);
        }
        else if (node instanceof UnaryOpNode) {
            checkNode(((UnaryOpNode) node).getOperand());
        }
        else if (node instanceof TernaryNode) {
            checkTernary((TernaryNode) node);
        }
        else if (node instanceof FilterNode) {
            checkFilter((FilterNode) node);
        }
        else if (node instanceof CallNode) {
            checkCall((CallNode) node);
        }
        else if (node instanceof IndexNode) {
            checkNode(((IndexNode) node).getArray());
            checkNode(((IndexNode) node).getIndex());
        }
        else if (node instanceof SliceNode) {
            checkNode(((SliceNode) node).getArray());
        }
        // Literals و VariableNode ما يحتاجوا فحص للقسمة
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Compound Nodes
    // ═══════════════════════════════════════════════════════════════════
    private void checkIfNode(IfNode node) {
        for (ExpressionNode cond : node.getConditions()) checkNode(cond);
        for (java.util.List<AstNode> body : node.getBodies()) {
            for (AstNode child : body) checkNode(child);
        }
        if (node.hasElse()) {
            for (AstNode child : node.getElseBody()) checkNode(child);
        }
    }

    private void checkForNode(ForNode node) {
        checkNode(node.getIterable());
        for (AstNode child : node.getBody()) checkNode(child);
        if (node.hasElse()) {
            for (AstNode child : node.getElseBody()) checkNode(child);
        }
    }

    private void checkWithNode(WithNode node) {
        for (WithNode.Assignment a : node.getAssignments()) checkNode(a.value);
        for (AstNode child : node.getBody()) checkNode(child);
    }

    private void checkMacroNode(MacroNode node) {
        for (MacroNode.Param p : node.getParams()) {
            if (p.defaultValue != null) checkNode(p.defaultValue);
        }
        for (AstNode child : node.getBody()) checkNode(child);
    }

    private void checkElementNode(ElementNode node) {
        for (AttributeNode attr : node.getAttributes()) {
            if (attr.getJinjaValue() != null) checkNode(attr.getJinjaValue());
        }
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    private void checkTernary(TernaryNode node) {
        checkNode(node.getValue());
        checkNode(node.getCondition());
        checkNode(node.getAlternative());
    }

    private void checkFilter(FilterNode node) {
        checkNode(node.getOperand());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);
    }

    private void checkCall(CallNode node) {
        checkNode(node.getCallee());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ★ BinaryOpNode — الفحص الرئيسي للقسمة على صفر
    //
    //  {{ 10 / 0 }}      → ZeroDivisionError: division by zero
    //  {{ 10 % 0 }}      → ZeroDivisionError: integer division or modulo by zero
    // ═══════════════════════════════════════════════════════════════════
    private void checkBinaryOp(BinaryOpNode node) {
        // افحص المعاملات أولاً (لأي أخطاء متداخلة)
        checkNode(node.getLeft());
        checkNode(node.getRight());

        BinaryOpNode.Operator op = node.getOperator();

        // فقط فحص القسمة و Modulo
        if (op != BinaryOpNode.Operator.DIV && op != BinaryOpNode.Operator.MOD) {
            return;
        }

        // استخراج قيمة المقسوم عليه (operand اليمين)
        Object rightValue = extractValue(node.getRight());

        if (rightValue == null) return;  // UNKNOWN → لا نبلغ

        Number numValue = toNumber(rightValue);
        if (numValue == null) return;

        double doubleValue = numValue.doubleValue();

        if (doubleValue == 0.0) {
            if (op == BinaryOpNode.Operator.MOD) {
                handler.report(DivisionByZeroError.moduloByZero(node.getLine(), "JINJA"));
            } else {
                handler.report(new DivisionByZeroError(node.getLine(), "JINJA"));
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════════════════════════

    /**
     * استخراج القيمة من AstNode
     * - VariableNode → ابحث في Jinja Symbol Table
     * - NumberLiteral → رجّع القيمة
     */
    private Object extractValue(AstNode node) {
        if (node == null) return null;

        // VariableNode → ابحث في Jinja ST
        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).getName();
            symbol_table.SymbolTable.Symbol sym = jinjaST.lookupInAllScopes(name);
            if (sym != null) {
                return sym.getValue();
            }
            return null;
        }

        // NumberLiteral → رجّع القيمة
        if (node instanceof NumberLiteral) {
            return ((NumberLiteral) node).getValue();
        }

        // ParenExpr-like (لو في) → فك القوسين
        // (ما في ParenExpr بـ AST تبع Jinja، بس نتركها احتياطاً)

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
}
