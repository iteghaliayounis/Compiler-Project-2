package Semantic.checkers.Jinja;

import AstHtml.*;
import Semantic.errors.WrongArgumentsError;
import Semantic.handlers.SemanticErrorHandler;

import java.util.*;


public class WrongArgumentsChecker {

    private final SemanticErrorHandler handler;

    private static final String SOURCE = "JINJA";


    private final Map<String, MacroInfo> macros = new HashMap<>();


    private static final Set<String> JINJA_GLOBALS_CALLABLE = new HashSet<>(Arrays.asList(
            "url_for", "get_flashed_messages", "range", "dict",
            "joiner", "namespace", "lipsum", "cycler"
    ));

    private static class MacroInfo {
        final String name;
        final List<ParamInfo> params;
        MacroInfo(String name, List<ParamInfo> params) {
            this.name = name;
            this.params = params;
        }
        int requiredCount() {
            int c = 0;
            for (ParamInfo p : params) if (!p.hasDefault) c++;
            return c;
        }
        int total() { return params.size(); }
        boolean hasParam(String n) {
            for (ParamInfo p : params) if (p.name.equals(n)) return true;
            return false;
        }
    }

    private static class ParamInfo {
        final String name;
        final boolean hasDefault;
        ParamInfo(String name, boolean hasDefault) {
            this.name = name;
            this.hasDefault = hasDefault;
        }
    }

    public WrongArgumentsChecker(SemanticErrorHandler handler) {
        this.handler = handler;
    }

    public void check(AstNode root) {
        if (root == null) return;

        collectMacros(root);

        checkCalls(root);
    }


    private void collectMacros(AstNode node) {
        if (node == null) return;

        if (node instanceof MacroNode) {
            MacroNode m = (MacroNode) node;
            List<ParamInfo> params = new ArrayList<>();
            for (MacroNode.Param p : m.getParams()) {
                params.add(new ParamInfo(p.name, p.defaultValue != null));
            }

            macros.putIfAbsent(m.getName(), new MacroInfo(m.getName(), params));

        }

        for (AstNode child : childrenOf(node)) {
            collectMacros(child);
        }
    }


    private void checkCalls(AstNode node) {
        if (node == null) return;

        if (node instanceof CallNode) {
            checkCall((CallNode) node);

        }

        for (AstNode child : childrenOf(node)) {
            checkCalls(child);
        }
    }

    private void checkCall(CallNode call) {
        ExpressionNode callee = call.getCallee();
        if (!(callee instanceof VariableNode)) {
            return;
        }
        String name = ((VariableNode) callee).getName();

        if (JINJA_GLOBALS_CALLABLE.contains(name)) return;

        MacroInfo macro = macros.get(name);
        if (macro == null) {

            return;
        }

        List<ExpressionNode> positional = call.getArguments();
        Map<String, ExpressionNode> kwargs = call.getNamedArguments();

        int givenPositional = positional != null ? positional.size() : 0;
        int givenKwargs     = kwargs != null ? kwargs.size() : 0;
        int givenTotal      = givenPositional + givenKwargs;

        int expectedTotal    = macro.total();
        int expectedRequired = macro.requiredCount();


        if (kwargs != null) {
            for (String kwName : kwargs.keySet()) {
                if (!macro.hasParam(kwName)) {
                    handler.report(new WrongArgumentsError(
                            "macro '" + name + "' (unknown keyword argument '" + kwName + "')",
                            expectedTotal, givenTotal,
                            call.getLine(), SOURCE));
                    return;
                }
            }
        }


        if (givenPositional > expectedTotal) {
            handler.report(new WrongArgumentsError(
                    "macro '" + name + "'",
                    expectedTotal, givenPositional,
                    call.getLine(), SOURCE));
            return;
        }


        if (givenTotal < expectedRequired) {
            handler.report(new WrongArgumentsError(
                    "macro '" + name + "' (missing " + (expectedRequired - givenTotal) + " required argument(s))",
                    expectedRequired, givenTotal,
                    call.getLine(), SOURCE));
            return;
        }


        if (givenTotal > expectedTotal) {
            handler.report(new WrongArgumentsError(
                    "macro '" + name + "'",
                    expectedTotal, givenTotal,
                    call.getLine(), SOURCE));
        }
    }


    private List<AstNode> childrenOf(AstNode node) {
        if (node == null) return Collections.emptyList();
        List<AstNode> kids = node.children();
        return kids != null ? kids : Collections.emptyList();
    }
}