package Semantic.checkers.flask;

import AstHtml.*;
import Semantic.errors.TypeMismatchError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.JinjaTypeInference;
import Semantic.util.PythonTypeInference;

import java.util.*;


public class FilterTypeMismatchChecker {

    private final SymbolTable.SymbolTable    pythonST;
    private final symbol_table.SymbolTable   jinjaST;
    private final SemanticErrorHandler       handler;

    public FilterTypeMismatchChecker(
            SymbolTable.SymbolTable    pythonST,
            symbol_table.SymbolTable   jinjaST,
            SemanticErrorHandler       handler) {
        this.pythonST = pythonST;
        this.jinjaST  = jinjaST;
        this.handler  = handler;
    }

    private static final Map<String, Set<String>> FILTER_EXPECTED_TYPES;
    static {
        FILTER_EXPECTED_TYPES = new HashMap<>();

        Set<String> stringFilters = new HashSet<>(Arrays.asList(
                "upper", "lower", "capitalize", "title", "trim", "striptags",
                "escape", "e", "truncate", "replace", "wordwrap", "indent",
                "center", "string"
        ));
        for (String f : stringFilters) FILTER_EXPECTED_TYPES.put(f, new HashSet<>(Collections.singleton("STRING")));

        Set<String> sizedTypes = new HashSet<>(Arrays.asList(
                "STRING", "LIST", "DICT", "TUPLE", "SET", "RANGE"
        ));
        FILTER_EXPECTED_TYPES.put("length", sizedTypes);
        FILTER_EXPECTED_TYPES.put("count",  new HashSet<>(sizedTypes));
        FILTER_EXPECTED_TYPES.put("wordcount", new HashSet<>(Collections.singleton("STRING")));

        Set<String> numericTypes = new HashSet<>(Arrays.asList("INT", "FLOAT"));
        FILTER_EXPECTED_TYPES.put("abs",   new HashSet<>(numericTypes));
        FILTER_EXPECTED_TYPES.put("round", new HashSet<>(numericTypes));


        Set<String> iterableTypes = new HashSet<>(Arrays.asList(
                "STRING", "LIST", "DICT", "TUPLE", "SET", "RANGE"
        ));
        FILTER_EXPECTED_TYPES.put("sort",      new HashSet<>(Collections.singleton("LIST")));
        FILTER_EXPECTED_TYPES.put("sorted",    new HashSet<>(Collections.singleton("LIST")));
        FILTER_EXPECTED_TYPES.put("reverse",   new HashSet<>(Collections.singleton("LIST")));
        FILTER_EXPECTED_TYPES.put("reversed",  new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("batch",     new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("slice",     new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("groupby",   new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("unique",    new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("select",    new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("reject",    new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("selectattr", new HashSet<>(iterableTypes));
        FILTER_EXPECTED_TYPES.put("rejectattr", new HashSet<>(iterableTypes));
    }

    public void check(AstNode jinjaRoot) {
        if (jinjaRoot != null) checkNode(jinjaRoot);
    }

    private void checkNode(AstNode node) {
        if (node == null) return;

        if (node instanceof FilterNode) {
            checkFilter((FilterNode) node);
            return;
        }

        //  Structural nodes
        if      (node instanceof TemplateNode)    { for (AstNode c : ((TemplateNode) node).getChildren()) checkNode(c); }
        else if (node instanceof ForNode)         { checkNode(((ForNode) node).getIterable()); for (AstNode c : ((ForNode) node).getBody()) checkNode(c); if (((ForNode) node).getElseBody() != null) for (AstNode c : ((ForNode) node).getElseBody()) checkNode(c); }
        else if (node instanceof IfNode)          { checkIf((IfNode) node); }
        else if (node instanceof SetNode)         { checkNode(((SetNode) node).getValue()); }
        else if (node instanceof WithNode)        { for (WithNode.Assignment a : ((WithNode) node).getAssignments()) checkNode(a.value); for (AstNode c : ((WithNode) node).getBody()) checkNode(c); }
        else if (node instanceof BlockNode)       { for (AstNode c : ((BlockNode) node).getBody()) checkNode(c); }
        else if (node instanceof MacroNode)       { for (MacroNode.Param p : ((MacroNode) node).getParams()) if (p.defaultValue != null) checkNode(p.defaultValue); for (AstNode c : ((MacroNode) node).getBody()) checkNode(c); }
        else if (node instanceof FilterBlockNode) { for (ExpressionNode a : ((FilterBlockNode) node).getFilterArgs()) checkNode(a); for (AstNode c : ((FilterBlockNode) node).getBody()) checkNode(c); }
        else if (node instanceof JinjaVarOutputNode) { checkNode(((JinjaVarOutputNode) node).getExpression()); }

        //  Expressions
        else if (node instanceof BinaryOpNode)      { checkNode(((BinaryOpNode) node).getLeft()); checkNode(((BinaryOpNode) node).getRight()); }
        else if (node instanceof IndexNode)         { checkNode(((IndexNode) node).getArray()); checkNode(((IndexNode) node).getIndex()); }
        else if (node instanceof SliceNode)         { checkNode(((SliceNode) node).getArray()); checkNode(((SliceNode) node).getStart()); checkNode(((SliceNode) node).getStop()); checkNode(((SliceNode) node).getStep()); }
        else if (node instanceof UnaryOpNode)       { checkNode(((UnaryOpNode) node).getOperand()); }
        else if (node instanceof TernaryNode)       { checkNode(((TernaryNode) node).getValue()); checkNode(((TernaryNode) node).getCondition()); checkNode(((TernaryNode) node).getAlternative()); }
        else if (node instanceof CallNode)          { checkNode(((CallNode) node).getCallee()); for (ExpressionNode a : ((CallNode) node).getArguments()) checkNode(a); }
        else if (node instanceof AttributeAccessNode) { checkNode(((AttributeAccessNode) node).getObject()); }

        //  HTML / CSS
        else if (node instanceof ElementNode)       { for (AttributeNode a : ((ElementNode) node).getAttributes()) checkAttribute(a); for (AstNode c : ((ElementNode) node).getChildren()) checkNode(c); }
        else if (node instanceof VoidElementNode)   { for (AttributeNode a : ((VoidElementNode) node).getAttributes()) checkAttribute(a); }
        else if (node instanceof StyleElementNode)  { for (CssNode s : ((StyleElementNode) node).getStatements()) checkNode(s); }
        else if (node instanceof CssJinjaValueNode) { checkNode(((CssJinjaValueNode) node).getExpression()); }
        else if (node instanceof CssRuleSetNode)    { for (CssNode s : ((CssRuleSetNode) node).getDeclarations()) checkNode(s); }
        else if (node instanceof CssDeclarationNode){ for (CssValueNode v : ((CssDeclarationNode) node).getValues()) checkNode(v); }
    }

    private void checkIf(IfNode node) {
        List<ExpressionNode> conditions = node.getConditions();
        List<List<AstNode>> bodies = node.getBodies();
        for (int i = 0; i < conditions.size(); i++) {
            checkNode(conditions.get(i));
            if (i < bodies.size()) for (AstNode c : bodies.get(i)) checkNode(c);
        }
        if (node.getElseBody() != null) for (AstNode c : node.getElseBody()) checkNode(c);
    }

    private void checkAttribute(AttributeNode node) {
        if (node.getJinjaValue() != null) checkNode(node.getJinjaValue());
    }

    //   FilterNode

    private void checkFilter(FilterNode node) {
        checkNode(node.getOperand());
        for (ExpressionNode arg : node.getArguments()) checkNode(arg);

        String filterName = JinjaTypeInference.getFilterName(node);
        if (filterName == null || "unknown_filter".equals(filterName)) return;

        Set<String> expectedTypes = FILTER_EXPECTED_TYPES.get(filterName);
        if (expectedTypes == null) return;

        String operandType = JinjaTypeInference.inferType(node.getOperand(), jinjaST);

        if ("UNKNOWN".equals(operandType) && node.getOperand() instanceof VariableNode) {
            String varName = ((VariableNode) node.getOperand()).getName();
            operandType = lookupPythonType(varName);
        }

        if ("UNKNOWN".equals(operandType)) return;

        String normalizedType = PythonTypeInference.normalizeType(operandType);

        if (!expectedTypes.contains(normalizedType)) {
            String expPy = formatExpectedTypes(expectedTypes);
            String actPy = PythonTypeInference.toPythonTypeName(normalizedType);

            String templateName = getCurrentTemplateName();

            handler.report(TypeMismatchError.filterTypeMismatch(
                    filterName, expPy, actPy, templateName, node.getLine()
            ));
        }
    }

    private String lookupPythonType(String varName) {
        SymbolTable.SymbolTable.Symbol sym = pythonST.lookupInAllScopes(varName);
        if (sym != null) {
            String type = sym.getType();
            if (type != null && !"Unknown".equalsIgnoreCase(type)) {
                return type;
            }
        }
        return "UNKNOWN";
    }

    private String formatExpectedTypes(Set<String> expectedTypes) {
        List<String> pyNames = new ArrayList<>();
        for (String t : expectedTypes) {
            pyNames.add(PythonTypeInference.toPythonTypeName(t));
        }
        Collections.sort(pyNames);
        return String.join(" or ", pyNames);
    }

    private String getCurrentTemplateName() {
        for (symbol_table.SymbolTable.ScopeEntry scope : jinjaST.getAllScopes()) {
            for (symbol_table.SymbolTable.Symbol sym : scope.getSymbols()) {
                if (sym.getKind() == symbol_table.SymbolTable.Kind.TEMPLATE) {
                    return sym.getName();
                }
            }
        }
        return "unknown_template";
    }
}
