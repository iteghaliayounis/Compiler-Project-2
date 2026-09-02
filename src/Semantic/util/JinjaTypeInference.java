package Semantic.util;

import AstHtml.*;
import symbol_table.SymbolTable;

import java.lang.reflect.Method;


public class JinjaTypeInference {

    private JinjaTypeInference() {} // utility class


    public static String inferType(AstNode node, SymbolTable jinjaST) {
        if (node == null) return "UNKNOWN";

        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).getName();
            symbol_table.SymbolTable.Symbol sym = jinjaST.lookupInAllScopes(name);
            if (sym != null) {
                return PythonTypeInference.normalizeType(sym.getType());
            }
            return "UNKNOWN";
        }

        if (node instanceof NumberLiteral) {
            String text = getLiteralText(node);
            if (text != null && text.contains(".")) return "FLOAT";
            return "INT";
        }
        if (node instanceof StringLiteral)  return "STRING";
        if (node instanceof BooleanLiteral) return "BOOL";
        if (node instanceof NoneLiteral)    return "NONE";

        if (node instanceof BinaryOpNode) {
            return inferBinaryOpType((BinaryOpNode) node, jinjaST);
        }

        if (node instanceof UnaryOpNode) {
            return inferType(((UnaryOpNode) node).getOperand(), jinjaST);
        }

        if (node instanceof TernaryNode) {
            String valueType = inferType(((TernaryNode) node).getValue(), jinjaST);
            if (!"UNKNOWN".equals(valueType)) return valueType;
            return inferType(((TernaryNode) node).getAlternative(), jinjaST);
        }

        if (node instanceof FilterNode) {
            return inferFilterReturnType((FilterNode) node, jinjaST);
        }

        if (node instanceof IndexNode) {
            String arrType = inferType(((IndexNode) node).getArray(), jinjaST);
            if ("STRING".equals(arrType)) return "STRING";
            return "UNKNOWN";
        }

        if (node instanceof AttributeAccessNode) {
            return "UNKNOWN";
        }

        if (node instanceof CallNode) {
            return "UNKNOWN";
        }

        if (node instanceof JinjaVarOutputNode) {
            return inferType(((JinjaVarOutputNode) node).getExpression(), jinjaST);
        }

        return "UNKNOWN";
    }


    private static String inferBinaryOpType(BinaryOpNode node, SymbolTable jinjaST) {
        BinaryOpNode.Operator op = node.getOperator();
        String leftType  = inferType(node.getLeft(),  jinjaST);
        String rightType = inferType(node.getRight(), jinjaST);

        if (op == BinaryOpNode.Operator.AND || op == BinaryOpNode.Operator.OR) return "BOOL";

        if (op == BinaryOpNode.Operator.EQ  || op == BinaryOpNode.Operator.NEQ ||
                op == BinaryOpNode.Operator.LT  || op == BinaryOpNode.Operator.GT  ||
                op == BinaryOpNode.Operator.LTE || op == BinaryOpNode.Operator.GTE ||
                op == BinaryOpNode.Operator.IN  || op == BinaryOpNode.Operator.NOT_IN ||
                op == BinaryOpNode.Operator.IS  || op == BinaryOpNode.Operator.IS_NOT) {
            return "BOOL";
        }

        if (op == BinaryOpNode.Operator.CONCAT) return "STRING";

        if (op == BinaryOpNode.Operator.ADD || op == BinaryOpNode.Operator.SUB ||
                op == BinaryOpNode.Operator.MUL || op == BinaryOpNode.Operator.DIV ||
                op == BinaryOpNode.Operator.MOD) {

            if (op == BinaryOpNode.Operator.ADD && "STRING".equals(leftType)) return "STRING";
            if (op == BinaryOpNode.Operator.ADD && "STRING".equals(rightType) && "STRING".equals(leftType)) return "STRING";

            if ("FLOAT".equals(leftType) || "FLOAT".equals(rightType)) return "FLOAT";

            if ("INT".equals(leftType) && "INT".equals(rightType)) return "INT";

            if (op == BinaryOpNode.Operator.MUL) {
                if ("STRING".equals(leftType) && "INT".equals(rightType)) return "STRING";
                if ("INT".equals(leftType) && "STRING".equals(rightType)) return "STRING";
                if ("LIST".equals(leftType) && "INT".equals(rightType))   return "LIST";
                if ("INT".equals(leftType) && "LIST".equals(rightType))   return "LIST";
            }

            return "UNKNOWN";
        }

        return "UNKNOWN";
    }


    private static String inferFilterReturnType(FilterNode node, SymbolTable jinjaST) {
        String filterName = getFilterName(node);
        if (filterName == null) return "UNKNOWN";

        switch (filterName) {
            case "upper":
            case "lower":
            case "capitalize":
            case "title":
            case "trim":
            case "striptags":
            case "escape":
            case "e":
            case "safe":
            case "truncate":
            case "replace":
            case "wordwrap":
            case "indent":
            case "center":
            case "default":
            case "d":
            case "tojson":
            case "string":
                return "STRING";

            case "length":
            case "count":
            case "wordcount":
            case "abs":
            case "int":
                return "INT";

            case "float":
            case "round":
                return "FLOAT";

            case "list":
            case "sort":
            case "sorted":
            case "reverse":
            case "reversed":
            case "batch":
            case "slice":
            case "groupby":
            case "unique":
            case "select":
            case "reject":
            case "selectattr":
            case "rejectattr":
                return "LIST";

            case "boolean":
            case "bool":
                return "BOOL";

            default:
                return "UNKNOWN";
        }
    }


    public static String getFilterName(FilterNode node) {
        try {
            Method m = FilterNode.class.getMethod("getFilterName");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        try {
            Method m = FilterNode.class.getMethod("getName");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Field f = FilterNode.class.getDeclaredField("filterName");
            f.setAccessible(true);
            Object result = f.get(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        return "unknown_filter";
    }


    private static String getLiteralText(AstNode node) {
        try {
            Method m = node.getClass().getMethod("getValue");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        try {
            Method m = node.getClass().getMethod("getText");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        return node.toString();
    }
}
