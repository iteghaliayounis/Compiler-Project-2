package Semantic.checkers.Jinja;

import AstHtml.*;
import Semantic.errors.InvalidFunctionCallError;
import Semantic.handlers.SemanticErrorHandler;

import java.util.*;


public class InvalidFunctionCallChecker {

    private final SemanticErrorHandler handler;
    private final symbol_table.SymbolTable jinjaST;
    private final SymbolTable.SymbolTable pythonST;

    private static final String SOURCE = "JINJA";


    private final Set<String> macroNames = new HashSet<>();


    private static final Set<String> JINJA_GLOBALS_CALLABLE = new HashSet<>(Arrays.asList(
            "url_for", "get_flashed_messages", "range", "dict",
            "joiner", "namespace", "lipsum", "cycler"
    ));

    public InvalidFunctionCallChecker(symbol_table.SymbolTable jinjaST,
                                      SymbolTable.SymbolTable pythonST,
                                      SemanticErrorHandler handler) {
        this.jinjaST = jinjaST;
        this.pythonST = pythonST;
        this.handler = handler;
    }


    public InvalidFunctionCallChecker(symbol_table.SymbolTable jinjaST,
                                      SemanticErrorHandler handler) {
        this(jinjaST, null, handler);
    }

    public void check(AstNode root) {
        if (root == null) return;

        collectMacroNames(root);

        walk(root);
    }

    private void collectMacroNames(AstNode node) {
        if (node == null) return;
        if (node instanceof MacroNode) {
            macroNames.add(((MacroNode) node).getName());
        }
        for (AstNode child : childrenOf(node)) {
            collectMacroNames(child);
        }
    }

    private void walk(AstNode node) {
        if (node == null) return;

        if (node instanceof CallNode) {
            checkCall((CallNode) node);
        }

        for (AstNode child : childrenOf(node)) {
            walk(child);
        }
    }

    private void checkCall(CallNode call) {
        ExpressionNode callee = call.getCallee();
        if (!(callee instanceof VariableNode)) {

            return;
        }
        String name = ((VariableNode) callee).getName();


        if (macroNames.contains(name)) return;


        if (JINJA_GLOBALS_CALLABLE.contains(name)) return;

        String type = resolveType(name);
        if (type == null) return;

        String pyType = normalizeType(type);


        if (isNonCallableType(pyType)) {
            handler.report(new InvalidFunctionCallError(pyType,
                    call.getLine(), SOURCE));
        }

    }


    private String resolveType(String varName) {
        // 1) جرب jinjaST
        if (jinjaST != null) {
            symbol_table.SymbolTable.Symbol sym = jinjaST.lookup(varName);
            if (sym != null && sym.getKind() != symbol_table.SymbolTable.Kind.MACRO) {
                return sym.getType();
            }
        }

        if (pythonST != null) {
            SymbolTable.SymbolTable.Symbol pySym = pythonST.lookupInAllScopes(varName);
            if (pySym != null) {
                SymbolTable.SymbolTable.Symbol.Kind k = pySym.getKind();

                if (k == SymbolTable.SymbolTable.Symbol.Kind.FUNCTION
                        || k == SymbolTable.SymbolTable.Symbol.Kind.ROUTE_FUNCTION
                        || k == SymbolTable.SymbolTable.Symbol.Kind.TEMPLATE
                        || k == SymbolTable.SymbolTable.Symbol.Kind.IMPORT) {
                    return null;
                }
                return pySym.getType();
            }
        }
        return null;
    }


    private String normalizeType(String type) {
        if (type == null) return "Unknown";
        switch (type.toUpperCase()) {
            case "INT":      return "int";
            case "FLOAT":    return "float";
            case "STRING":
            case "STR":      return "str";
            case "BOOL":     return "bool";
            case "NONE":
            case "NONETYPE": return "NoneType";
            case "LIST":     return "list";
            case "DICT":     return "dict";
            case "UNKNOWN":  return "Unknown";
            default:         return "Unknown";
        }
    }

    private boolean isNonCallableType(String pyType) {
        switch (pyType) {
            case "int":
            case "float":
            case "str":
            case "bool":
            case "NoneType":
            case "list":
            case "dict":
                return true;
            default:
                return false;
        }
    }

    private List<AstNode> childrenOf(AstNode node) {
        if (node == null) return Collections.emptyList();
        List<AstNode> kids = node.children();
        return kids != null ? kids : Collections.emptyList();
    }
}