package Semantic.checkers.Jinja;

import AstHtml.*;
import Semantic.errors.ScopeError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.JinjaTypeInference;

import symbol_table.SymbolTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ScopeChecker {

    private final SymbolTable          jinjaST;
    private final SemanticErrorHandler handler;

    private final Set<String> currentScopeVars = new HashSet<>();

    private final Set<String> allDefinedVars   = new HashSet<>();

    public ScopeChecker(SymbolTable jinjaST, SemanticErrorHandler handler) {
        this.jinjaST  = jinjaST;
        this.handler  = handler;
    }

    //  Entry Point
    public void check(AstNode root) {
        checkNode(root);
    }

    //  Dispatcher
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
    }

    //  Compound Nodes
    private void checkTemplate(TemplateNode node) {
        for (AstNode child : node.getChildren()) checkNode(child);
    }

    private void checkBlock(BlockNode node) {
        Set<String> outerScope = new HashSet<>(currentScopeVars);
        currentScopeVars.add(node.getName());
        allDefinedVars.add(node.getName());

        for (AstNode child : node.getBody()) checkNode(child);

        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }

    private void checkIfNode(IfNode node) {
        for (ExpressionNode cond : node.getConditions()) checkNode(cond);

        for (List<AstNode> body : node.getBodies()) {
            Set<String> outerScope = new HashSet<>(currentScopeVars);
            for (AstNode child : body) checkNode(child);
            currentScopeVars.clear();
            currentScopeVars.addAll(outerScope);
        }

        if (node.hasElse()) {
            Set<String> outerScope = new HashSet<>(currentScopeVars);
            for (AstNode child : node.getElseBody()) checkNode(child);
            currentScopeVars.clear();
            currentScopeVars.addAll(outerScope);
        }
    }

    private void checkForNode(ForNode node) {
        checkNode(node.getIterable());

        Set<String> outerScope = new HashSet<>(currentScopeVars);

        for (String target : node.getTargets()) {
            currentScopeVars.add(target);
            allDefinedVars.add(target);
        }

        for (AstNode child : node.getBody()) checkNode(child);

        if (node.hasElse()) {
            for (AstNode child : node.getElseBody()) checkNode(child);
        }

        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }


    private void checkWithNode(WithNode node) {
        Set<String> outerScope = new HashSet<>(currentScopeVars);

        for (WithNode.Assignment a : node.getAssignments()) {
            checkNode(a.value);
            currentScopeVars.add(a.name);
            allDefinedVars.add(a.name);
        }
        for (AstNode child : node.getBody()) checkNode(child);

        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }


    private void checkMacroNode(MacroNode node) {
        Set<String> outerScope = new HashSet<>(currentScopeVars);

        for (MacroNode.Param p : node.getParams()) {
            if (p.defaultValue != null) checkNode(p.defaultValue);
            currentScopeVars.add(p.name);
            allDefinedVars.add(p.name);
        }

        for (AstNode child : node.getBody()) checkNode(child);

        currentScopeVars.clear();
        currentScopeVars.addAll(outerScope);
    }

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

    //  Expressions

    private void checkVariable(VariableNode node) {
        if (node.getName() == null) return;

        if (currentScopeVars.contains(node.getName())) return;

        if (allDefinedVars.contains(node.getName())) {
            ScopeError error = ScopeError.loopVarOutsideLoop(
                    node.getName(), node.getLine(), "JINJA");
            handler.report(error);
            return;
        }
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
