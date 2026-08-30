package Semantic.checkers.flask;

import AstHtml.*;
import Semantic.errors.InvalidAttributeAccessError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.util.JinjaTypeInference;
import Semantic.util.PythonTypeInference;

import java.util.*;


public class InvalidAttributeAccessChecker {

    private final SymbolTable.SymbolTable  pythonST;
    private final symbol_table.SymbolTable jinjaST;
    private final SemanticErrorHandler     handler;

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

    public InvalidAttributeAccessChecker(
            SymbolTable.SymbolTable  pythonST,
            symbol_table.SymbolTable jinjaST,
            SemanticErrorHandler     handler) {
        this.pythonST = pythonST;
        this.jinjaST  = jinjaST;
        this.handler  = handler;
    }

    //  Entry Point
    public void check(AstNode jinjaRoot) {
        if (jinjaRoot != null) checkNode(jinjaRoot);
    }

    //  Dispatcher
    private void checkNode(AstNode node) {
        if (node == null) return;
        if (node instanceof AttributeAccessNode) {
            checkAttributeAccess((AttributeAccessNode) node);
            return;
        }

        // Structural nodes
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
        else if (node instanceof BinaryOpNode)  { checkNode(((BinaryOpNode) node).getLeft()); checkNode(((BinaryOpNode) node).getRight()); }
        else if (node instanceof IndexNode)     { checkNode(((IndexNode) node).getArray()); checkNode(((IndexNode) node).getIndex()); }
        else if (node instanceof SliceNode)     { checkNode(((SliceNode) node).getArray()); checkNode(((SliceNode) node).getStart()); checkNode(((SliceNode) node).getStop()); checkNode(((SliceNode) node).getStep()); }
        else if (node instanceof UnaryOpNode)   { checkNode(((UnaryOpNode) node).getOperand()); }
        else if (node instanceof TernaryNode)   { checkNode(((TernaryNode) node).getValue()); checkNode(((TernaryNode) node).getCondition()); checkNode(((TernaryNode) node).getAlternative()); }
        else if (node instanceof FilterNode)    { checkNode(((FilterNode) node).getOperand()); for (ExpressionNode a : ((FilterNode) node).getArguments()) checkNode(a); }
        else if (node instanceof CallNode)      { checkNode(((CallNode) node).getCallee()); for (ExpressionNode a : ((CallNode) node).getArguments()) checkNode(a); }
        else if (node instanceof VariableNode)  {  }

        // HTML / CSS
        else if (node instanceof ElementNode)        { for (AttributeNode a : ((ElementNode) node).getAttributes()) checkAttribute(a); for (AstNode c : ((ElementNode) node).getChildren()) checkNode(c); }
        else if (node instanceof VoidElementNode)    { for (AttributeNode a : ((VoidElementNode) node).getAttributes()) checkAttribute(a); }
        else if (node instanceof StyleElementNode)   { for (CssNode s : ((StyleElementNode) node).getStatements()) checkNode(s); }
        else if (node instanceof CssJinjaValueNode)  { checkNode(((CssJinjaValueNode) node).getExpression()); }
        else if (node instanceof CssRuleSetNode)     { for (CssNode s : ((CssRuleSetNode) node).getDeclarations()) checkNode(s); }
        else if (node instanceof CssDeclarationNode) { for (CssValueNode v : ((CssDeclarationNode) node).getValues()) checkNode(v); }
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

    //    — AttributeAccessNode
    private void checkAttributeAccess(AttributeAccessNode node) {
        checkNode(node.getObject());

        String objType = JinjaTypeInference.inferType(node.getObject(), jinjaST);

        if ("UNKNOWN".equals(objType) && node.getObject() instanceof VariableNode) {
            String varName = ((VariableNode) node.getObject()).getName();
            objType = lookupPythonType(varName);
        }

        String type = PythonTypeInference.normalizeType(objType);

        if (!ALLOWED_ATTRS.containsKey(type)) {
            return;
        }

        Set<String> allowed = ALLOWED_ATTRS.get(type);
        String attr = node.getAttributeName();

        if (!allowed.contains(attr)) {
            String pyType = PythonTypeInference.toPythonTypeName(type);
            handler.report(new InvalidAttributeAccessError(pyType, attr, node.getLine(), "JINJA"));
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
}
