package Semantic.checkers.Jinja;

import AstHtml.*;

import Semantic.errors.UndefinedVarError;
import Semantic.handlers.SemanticErrorHandler;

import java.util.*;


public class UndefinedVariableChecker {

    // ── Scope Stack مستقل ──────────────────────────────────────────────
    private final Deque<Set<String>> scopeStack = new ArrayDeque<>();
    private final SemanticErrorHandler handler;

    private final Set<String> flaskPassedVars = new HashSet<>();

    public void addFlaskPassedVariable(String varName) {
        flaskPassedVars.add(varName);
    }
    // Flask globals — متوفرة دائماً في قوالب Jinja2
    private static final Set<String> FLASK_GLOBALS = new HashSet<>(Arrays.asList(
            "request", "session", "g", "config", "url_for",
            "get_flashed_messages", "range", "dict", "joiner",
            "namespace", "lipsum", "cycler"
    ));

    public UndefinedVariableChecker(SemanticErrorHandler handler) {
        this.handler = handler;
        // Global scope يلي فيه Flask globals
        scopeStack.push(new LinkedHashSet<>(FLASK_GLOBALS));
    }


    // ── Scope Management ────────────────────────────────────────────────
    private void pushScope() {
        scopeStack.push(new LinkedHashSet<>());
    }

    private void popScope() {
        if (scopeStack.size() > 1) scopeStack.pop();
    }

    private void define(String name) {
        scopeStack.peek().add(name);
    }


    private boolean isDefined(String name) {
        for (Set<String> scope : scopeStack) {
            if (scope.contains(name)) return true;
        }
        return false;
    }


    // ── Entry Point ─────────────────────────────────────────────────────
    public void check(AstNode node) {
        if (node != null) checkNode(node);
    }


    // ── Dispatcher ──────────────────────────────────────────────────────
    private void checkNode(AstNode node) {
        if (node == null) return;

        if      (node instanceof TemplateNode)        checkTemplate((TemplateNode) node);
        else if (node instanceof ForNode)             checkFor((ForNode) node);
        else if (node instanceof IfNode)              checkIf((IfNode) node);
        else if (node instanceof SetNode)             checkSet((SetNode) node);
        else if (node instanceof WithNode)            checkWith((WithNode) node);
        else if (node instanceof BlockNode)           checkBlock((BlockNode) node);
        else if (node instanceof MacroNode)           checkMacro((MacroNode) node);
        else if (node instanceof ImportNode)          checkImport((ImportNode) node);
        else if (node instanceof FromImportNode)      checkFromImport((FromImportNode) node);
        else if (node instanceof FilterBlockNode)     checkFilterBlock((FilterBlockNode) node);
        else if (node instanceof RawNode)             { /* نص خام — لا فحص */ }
        else if (node instanceof ExtendsNode)         { /* مجرد اسم قالب */ }
        else if (node instanceof IncludeNode)         { /* مجرد اسم قالب */ }

        else if (node instanceof JinjaVarOutputNode) checkNode(((JinjaVarOutputNode) node).getExpression());

            // ── Expressions ─────────────────────────────────────────────
        else if (node instanceof VariableNode)        checkVariable((VariableNode) node);
        else if (node instanceof AttributeAccessNode) checkNode(((AttributeAccessNode) node).getObject());
        else if (node instanceof CallNode)            checkCall((CallNode) node);
        else if (node instanceof IndexNode)           { checkNode(((IndexNode) node).getArray()); checkNode(((IndexNode) node).getIndex()); }
        else if (node instanceof SliceNode)           checkSlice((SliceNode) node);
        else if (node instanceof BinaryOpNode)        { checkNode(((BinaryOpNode) node).getLeft()); checkNode(((BinaryOpNode) node).getRight()); }
        else if (node instanceof UnaryOpNode)         checkNode(((UnaryOpNode) node).getOperand());
        else if (node instanceof TernaryNode)        { checkNode(((TernaryNode) node).getValue()); checkNode(((TernaryNode) node).getCondition()); checkNode(((TernaryNode) node).getAlternative()); }
        else if (node instanceof FilterNode)          checkFilter((FilterNode) node);

            // ── HTML Elements ───────────────────────────────────────────
        else if (node instanceof ElementNode)         checkElement((ElementNode) node);
        else if (node instanceof VoidElementNode)     checkVoidElement((VoidElementNode) node);
        else if (node instanceof TextNode)            { /* نص عادي */ }
        else if (node instanceof AttributeNode)       checkAttribute((AttributeNode) node);
        else if (node instanceof ScriptElementNode)   { /* JavaScript — لا فحص */ }
        else if (node instanceof StyleElementNode)    checkStyleElement((StyleElementNode) node);
        else if (node instanceof StyleAttributeNode)  { /* CSS inline — لا فحص متغيرات */ }

        // ── CSS مع Jinja ────────────────────────────────────────────
        else if (node instanceof CssJinjaValueNode)   checkNode(((CssJinjaValueNode) node).getExpression());


        else if (node instanceof CssRuleSetNode)     checkCssRuleSet((CssRuleSetNode) node);
        else if (node instanceof CssDeclarationNode) checkCssDeclaration((CssDeclarationNode) node);


    }

    /* ═══════════════════════════════════════════════════════════════════
     الفحص الأساسي
     * ═══════════════════════════════════════════════════════════════════ */
    private void checkVariable(VariableNode node) {
        String name = node.getName();
        if (!isDefined(name) && !flaskPassedVars.contains(name)) {

            handler.report(new UndefinedVarError(name, node.getLine(), "JINJA"));
        }
    }

    /* ═══════════════════════════════════════════════════════════════════
     *  إدارة الـ Scopes
     * ═══════════════════════════════════════════════════════════════════ */

    private void checkTemplate(TemplateNode node) {
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    private void checkFor(ForNode node) {
        checkNode(node.getIterable());


        pushScope();


        for (String target : node.getTargets()) {
            define(target);
        }
        define("loop"); // loop.index, loop.first...


        for (AstNode child : node.getBody()) checkNode(child);

        if (node.getElseBody() != null && !node.getElseBody().isEmpty()) {
            pushScope();
            for (AstNode child : node.getElseBody()) checkNode(child);
            popScope();
        }


        popScope();
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
        define(node.getVariable());
    }


    private void checkWith(WithNode node) {

        for (WithNode.Assignment assign : node.getAssignments()) {
            checkNode(assign.value);
        }

        pushScope();

        for (WithNode.Assignment assign : node.getAssignments()) {
            define(assign.name);
        }

        for (AstNode child : node.getBody()) checkNode(child);


        popScope();
    }


    private void checkBlock(BlockNode node) {
        pushScope();
        define(node.getName());
        for (AstNode child : node.getBody()) checkNode(child);
        popScope();
    }

    private void checkMacro(MacroNode node) {

        define(node.getName());


        pushScope();


        for (MacroNode.Param param : node.getParams()) {
            define(param.name);
            if (param.defaultValue != null) {
                checkNode(param.defaultValue);
            }
        }


        define("varargs");
        define("kwargs");
        define("caller");


        for (AstNode child : node.getBody()) checkNode(child);

        popScope();
    }

    private void checkImport(ImportNode node) {
        define(node.getAlias());
    }

    private void checkFromImport(FromImportNode node) {
        for (FromImportNode.ImportName imp : node.getImports()) {
            String usedName = (imp.alias != null) ? imp.alias : imp.name;
            define(usedName);
        }
    }

    private void checkFilterBlock(FilterBlockNode node) {

        for (ExpressionNode arg : node.getFilterArgs()) checkNode(arg);

        pushScope();
        for (AstNode child : node.getBody()) checkNode(child);
        popScope();
    }


    /* ═══════════════════════════════════════════════════════════════════
     *  تعابير Jinja
     * ═══════════════════════════════════════════════════════════════════ */

    private void checkCall(CallNode node) {
        checkNode(node.getCallee());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);
    }

    private void checkSlice(SliceNode node) {
        checkNode(node.getArray());
        checkNode(node.getStart());
        checkNode(node.getStop());
        checkNode(node.getStep());
    }

    /**
     * {{ x | upper }}
     * نتحقق من x فقط — upper فيلتر مش متغير
     */
    private void checkFilter(FilterNode node) {
        checkNode(node.getOperand());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);
    }

    /* ═══════════════════════════════════════════════════════════════════
     *  CSS Nodes — للوصول للجينجا جوه الـ style
     * ═══════════════════════════════════════════════════════════════════ */
    private void checkCssRuleSet(CssRuleSetNode node) {
        for (CssDeclarationNode decl : node.getDeclarations()) {
            checkNode(decl);
        }
    }

    private void checkCssDeclaration(CssDeclarationNode node) {
        for (CssValueNode val : node.getValues()) {
            checkNode(val);
        }
    }
    /* ═══════════════════════════════════════════════════════════════════
     *  عناصر HTML
     * ═══════════════════════════════════════════════════════════════════ */

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
}