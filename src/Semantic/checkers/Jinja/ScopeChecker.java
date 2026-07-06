package Semantic.checkers.Jinja;

import AstHtml.*;
import Semantic.errors.ScopeError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.JinjaTypeInference;

import symbol_table.SymbolTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ScopeChecker (Jinja) — يفحص أخطاء Scope في قوالب Jinja2
 *
 * الخطأ الحقيقي: UnboundLocalError
 *
 * الحالات التي يفحصها:
 *   1) متغير الـ for loop مستخدم بعد {% endfor %}:
 *        {% for p in products %}
 *        {% endfor %}
 *        {{ p }}    → UnboundLocalError: 'p' is not defined outside loop
 *
 *   2) متغير set داخل if بعد endif (لو الـ if ما تنفذ):
 *        {% if x %}{% set y = 1 %}{% endif %}
 *        {{ y }}    → قد يكون غير معرف
 *
 * ⚠️ الفرق عن UndefinedVariableChecker (غالية):
 *   - UndefinedVariableChecker = المتغير غير موجود أصلاً
 *   - ScopeChecker               = المتغير موجود في scope آخر (محذوف من الحالي)
 *
 * ⚠️ المنطق:
 *   نحتفظ بـ Set من المتغيرات المتاحة في الـ scope الحالي.
 *   لما نخرج من for/if → نحذف متغيراتها.
 *   لما نستخدم متغير → نتأكد إنو موجود في الـ Set الحالي.
 */
public class ScopeChecker {

    private final SymbolTable          jinjaST;
    private final SemanticErrorHandler handler;

    /** المتغيرات المتاحة في الـ scope الحالي */
    private final Set<String> currentScopeVars = new HashSet<>();

    /** كل المتغيرات المعرفة بأي scope (للتمييز عن UndefinedVarError) */
    private final Set<String> allDefinedVars   = new HashSet<>();

    public ScopeChecker(SymbolTable jinjaST, SemanticErrorHandler handler) {
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
    //  Dispatcher
    // ═══════════════════════════════════════════════════════════════════
    private void checkNode(AstNode node) {
        if (node == null) return;

        if (node instanceof TemplateNode) {
            checkTemplate((TemplateNode) node);
        }
        else if (node instanceof BlockNode) {
            checkBlock((BlockNode) node);
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
        else if (node instanceof SetNode) {
            checkSetNode((SetNode) node);
        }
        else if (node instanceof ElementNode) {
            checkElementNode((ElementNode) node);
        }
        else if (node instanceof StyleElementNode) {
            for (CssNode css : ((StyleElementNode) node).getStatements()) checkNode(css);
        }
        else if (node instanceof CssJinjaValueNode) {
            checkNode(((CssJinjaValueNode) node).getExpression());
        }
        else if (node instanceof JinjaVarOutputNode) {
            checkNode(((JinjaVarOutputNode) node).getExpression());
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
        else if (node instanceof AttributeAccessNode) {
            checkNode(((AttributeAccessNode) node).getObject());
        }
        else if (node instanceof VariableNode) {
            checkVariable((VariableNode) node);
        }
        // NumberLiteral, StringLiteral, BooleanLiteral, NoneLiteral — لا فحص
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Compound Nodes
    // ═══════════════════════════════════════════════════════════════════
    private void checkTemplate(TemplateNode node) {
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    private void checkBlock(BlockNode node) {
        // Block يفتح scope جديد (اسم البلوك نفسه قد يكون متاح للوراثة)
        Set<String> outerScope = new HashSet<>(currentScopeVars);
        currentScopeVars.add(node.getName());
        allDefinedVars.add(node.getName());

        for (AstNode child : node.getBody()) checkNode(child);

        // نرجع للـ scope الخارجي
        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }

    /**
     * ★ IfNode — كل branch يفتح scope جديد
     * متغيرات set داخل if ما تكون متاحة برا الـ if (لو الـ condition ما تحقق)
     */
    private void checkIfNode(IfNode node) {
        // افحص الشروط
        for (ExpressionNode cond : node.getConditions()) checkNode(cond);

        // افحص كل branch (if + elifs)
        for (List<AstNode> body : node.getBodies()) {
            Set<String> outerScope = new HashSet<>(currentScopeVars);
            for (AstNode child : body) checkNode(child);
            // بعد الخروج من الـ branch → نشيل المتغيرات يلي اتنضافت
            currentScopeVars.clear();
            currentScopeVars.addAll(outerScope);
        }

        // افحص else body
        if (node.hasElse()) {
            Set<String> outerScope = new HashSet<>(currentScopeVars);
            for (AstNode child : node.getElseBody()) checkNode(child);
            currentScopeVars.clear();
            currentScopeVars.addAll(outerScope);
        }
    }

    /**
     * ★ ForNode — الـ loop variables متاحة بس جوا الـ for
     *
     * {% for p in products %}
     *     {{ p }}     ← تمام، p متاح
     * {% endfor %}
     * {{ p }}         ← خطأ! p مش متاح برا الـ for
     */
    private void checkForNode(ForNode node) {
        // افحص الـ iterable (مثلاً: products)
        checkNode(node.getIterable());

        // احفظ الـ scope الخارجي
        Set<String> outerScope = new HashSet<>(currentScopeVars);

        // أضف الـ loop variables للـ scope
        for (String target : node.getTargets()) {
            currentScopeVars.add(target);
            allDefinedVars.add(target);
        }

        // افحص جسم الـ for
        for (AstNode child : node.getBody()) checkNode(child);

        // افحص else body (إن وجد)
        if (node.hasElse()) {
            for (AstNode child : node.getElseBody()) checkNode(child);
        }

        // بعد الخروج من الـ for → نرجع للـ scope الخارجي
        // (نشيل الـ loop variables)
        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }

    /**
     * WithNode — يفتح scope جديد للمتغيرات المعرفة بـ {% with %}
     */
    private void checkWithNode(WithNode node) {
        Set<String> outerScope = new HashSet<>(currentScopeVars);

        // أضف متغيرات الـ with
        for (WithNode.Assignment a : node.getAssignments()) {
            checkNode(a.value);
            currentScopeVars.add(a.name);
            allDefinedVars.add(a.name);
        }

        // افحص الجسم
        for (AstNode child : node.getBody()) checkNode(child);

        // نرجع للـ scope الخارجي
        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }

    /**
     * MacroNode — يفتح scope جديد للـ parameters
     */
    private void checkMacroNode(MacroNode node) {
        Set<String> outerScope = new HashSet<>(currentScopeVars);

        // أضف الـ parameters
        for (MacroNode.Param p : node.getParams()) {
            if (p.defaultValue != null) checkNode(p.defaultValue);
            currentScopeVars.add(p.name);
            allDefinedVars.add(p.name);
        }

        // افحص الجسم
        for (AstNode child : node.getBody()) checkNode(child);

        // نرجع للـ scope الخارجي
        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }

    /**
     * SetNode — تعريف متغير جديد
     */
    private void checkSetNode(SetNode node) {
        checkNode(node.getValue());
        currentScopeVars.add(node.getVariable());
        allDefinedVars.add(node.getVariable());
    }

    private void checkElementNode(ElementNode node) {
        for (AttributeNode attr : node.getAttributes()) {
            if (attr.getJinjaValue() != null) checkNode(attr.getJinjaValue());
        }
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Expressions
    // ═══════════════════════════════════════════════════════════════════

    /**
     * ★ VariableNode — الفحص الرئيسي
     *
     * لو المتغير معرف بـ scope تاني (موجود بـ allDefinedVars بس مش بـ currentScopeVars)
     * → ScopeError
     */
    private void checkVariable(VariableNode node) {
        if (node.getName() == null) return;

        // 1) لو متاح في الـ scope الحالي → تمام
        if (currentScopeVars.contains(node.getName())) return;

        // 2) ★ لو معرّف بـ scope تاني (for loop انتهى) → ScopeError
        if (allDefinedVars.contains(node.getName())) {
            ScopeError error = ScopeError.loopVarOutsideLoop(
                    node.getName(), node.getLine(), "JINJA");
            handler.report(error);
            return;  // ← أهم سطر: ما نخلي UndefinedVariableChecker يمسكو
        }

        // 3) لو موجود في Symbol Table (ممرر من Flask) → تمام
        if (jinjaST.lookupInAllScopes(node.getName()) != null) return;
    }

    private void checkBinaryOp(BinaryOpNode node) {
        checkNode(node.getLeft());
        checkNode(node.getRight());
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
}
