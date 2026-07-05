package Semantic.util;

import AstHtml.*;
import symbol_table.SymbolTable;

import java.lang.reflect.Method;

/**
 * JinjaTypeInference — أداة استنتاج نوع التعابير في Jinja2 AST
 *
 * تعطي نوع التعبير كـ String بأحد الأشكال التالية (موحدة مع Python ST):
 *   "INT", "FLOAT", "STRING", "BOOL", "LIST", "DICT", "NONE", "FUNCTION", "UNKNOWN"
 *
 * ملاحظة: المتغيرات الممررة من Flask قد تكون أنواعها بنفس صيغة Python ST
 * (لأن MissingFlaskVariableChecker.propagateTypeAndValue ينسخ النوع من Python ST إلى Jinja ST).
 */
public class JinjaTypeInference {

    private JinjaTypeInference() {} // utility class

    // ═══════════════════════════════════════════════════════════════════
    //  الدالة الرئيسية
    // ═══════════════════════════════════════════════════════════════════
    public static String inferType(AstNode node, SymbolTable jinjaST) {
        if (node == null) return "UNKNOWN";

        // 1) VariableNode → ابحث في Jinja Symbol Table
        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).getName();
            symbol_table.SymbolTable.Symbol sym = jinjaST.lookupInAllScopes(name);
            if (sym != null) {
                return PythonTypeInference.normalizeType(sym.getType());
            }
            return "UNKNOWN";
        }

        // 2) Literals
        if (node instanceof NumberLiteral) {
            String text = getLiteralText(node);
            if (text != null && text.contains(".")) return "FLOAT";
            return "INT";
        }
        if (node instanceof StringLiteral)  return "STRING";
        if (node instanceof BooleanLiteral) return "BOOL";
        if (node instanceof NoneLiteral)    return "NONE";

        // 3) BinaryOpNode → نستنتج من المعاملات والعامل
        if (node instanceof BinaryOpNode) {
            return inferBinaryOpType((BinaryOpNode) node, jinjaST);
        }

        // 4) UnaryOpNode → نفس نوع المعامل
        if (node instanceof UnaryOpNode) {
            return inferType(((UnaryOpNode) node).getOperand(), jinjaST);
        }

        // 5) TernaryNode → نوع القيمة أو البديل
        if (node instanceof TernaryNode) {
            String valueType = inferType(((TernaryNode) node).getValue(), jinjaST);
            if (!"UNKNOWN".equals(valueType)) return valueType;
            return inferType(((TernaryNode) node).getAlternative(), jinjaST);
        }

        // 6) FilterNode → نوع الناتج يعتمد على الفلتر
        if (node instanceof FilterNode) {
            return inferFilterReturnType((FilterNode) node, jinjaST);
        }

        // 7) IndexNode → نوع العنصر (UNKNOWN غالباً)
        if (node instanceof IndexNode) {
            String arrType = inferType(((IndexNode) node).getArray(), jinjaST);
            if ("STRING".equals(arrType)) return "STRING";
            return "UNKNOWN";
        }

        // 8) AttributeAccessNode → UNKNOWN (لا نعرف نوع الخاصية)
        if (node instanceof AttributeAccessNode) {
            return "UNKNOWN";
        }

        // 9) CallNode → UNKNOWN (لا نعرف نوع الإرجاع)
        if (node instanceof CallNode) {
            return "UNKNOWN";
        }

        // 10) JinjaVarOutputNode → نوع التعبير الداخلي
        if (node instanceof JinjaVarOutputNode) {
            return inferType(((JinjaVarOutputNode) node).getExpression(), jinjaST);
        }

        return "UNKNOWN";
    }

    // ═══════════════════════════════════════════════════════════════════
    //  استنتاج نوع BinaryOpNode
    // ═══════════════════════════════════════════════════════════════════
    private static String inferBinaryOpType(BinaryOpNode node, SymbolTable jinjaST) {
        BinaryOpNode.Operator op = node.getOperator();
        String leftType  = inferType(node.getLeft(),  jinjaST);
        String rightType = inferType(node.getRight(), jinjaST);

        // العمليات المنطقية → BOOL
        if (op == BinaryOpNode.Operator.AND || op == BinaryOpNode.Operator.OR) return "BOOL";

        // عمليات المقارنة → BOOL
        if (op == BinaryOpNode.Operator.EQ  || op == BinaryOpNode.Operator.NEQ ||
                op == BinaryOpNode.Operator.LT  || op == BinaryOpNode.Operator.GT  ||
                op == BinaryOpNode.Operator.LTE || op == BinaryOpNode.Operator.GTE ||
                op == BinaryOpNode.Operator.IN  || op == BinaryOpNode.Operator.NOT_IN ||
                op == BinaryOpNode.Operator.IS  || op == BinaryOpNode.Operator.IS_NOT) {
            return "BOOL";
        }

        // CONCAT (~) → STRING دائماً
        if (op == BinaryOpNode.Operator.CONCAT) return "STRING";

        // العمليات الحسابية
        if (op == BinaryOpNode.Operator.ADD || op == BinaryOpNode.Operator.SUB ||
                op == BinaryOpNode.Operator.MUL || op == BinaryOpNode.Operator.DIV ||
                op == BinaryOpNode.Operator.MOD) {

            // إذا أحد المعاملات STRING والعملية ADD → STRING (concatenation)
            if (op == BinaryOpNode.Operator.ADD && "STRING".equals(leftType)) return "STRING";
            if (op == BinaryOpNode.Operator.ADD && "STRING".equals(rightType) && "STRING".equals(leftType)) return "STRING";

            // إذا أحد المعاملات FLOAT → FLOAT
            if ("FLOAT".equals(leftType) || "FLOAT".equals(rightType)) return "FLOAT";

            // إذا كلاهما INT → INT
            if ("INT".equals(leftType) && "INT".equals(rightType)) return "INT";

            // إذا MUL و أحد المعاملات STRING والآخر INT → STRING (repetition)
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

    // ═══════════════════════════════════════════════════════════════════
    //  استنتاج نوع ناتج الفلتر
    // ═══════════════════════════════════════════════════════════════════
    private static String inferFilterReturnType(FilterNode node, SymbolTable jinjaST) {
        String filterName = getFilterName(node);
        if (filterName == null) return "UNKNOWN";

        switch (filterName) {
            // فلاتر ترجع STRING
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

            // فلاتر ترجع INT
            case "length":
            case "count":
            case "wordcount":
            case "abs":
            case "int":
                return "INT";

            // فلاتر ترجع FLOAT
            case "float":
            case "round":
                return "FLOAT";

            // فلاتر ترجع LIST
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

            // فلاتر ترجع BOOL
            case "boolean":
            case "bool":
                return "BOOL";

            default:
                return "UNKNOWN";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Helper: استخراج اسم الفلتر من FilterNode
    //  (يستخدم reflection لتجنب الاعتماد على getter محدد)
    // ═══════════════════════════════════════════════════════════════════
    public static String getFilterName(FilterNode node) {
        // جرّب getFilterName() أولاً
        try {
            Method m = FilterNode.class.getMethod("getFilterName");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        // جرّب getName()
        try {
            Method m = FilterNode.class.getMethod("getName");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        // جرّب الحقل filterName مباشرة
        try {
            java.lang.reflect.Field f = FilterNode.class.getDeclaredField("filterName");
            f.setAccessible(true);
            Object result = f.get(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        return "unknown_filter";
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Helper: استخراج النص من literal (للتمييز بين int و float)
    // ═══════════════════════════════════════════════════════════════════
    private static String getLiteralText(AstNode node) {
        // جرّب getValue()
        try {
            Method m = node.getClass().getMethod("getValue");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        // جرّب getText()
        try {
            Method m = node.getClass().getMethod("getText");
            Object result = m.invoke(node);
            if (result != null) return result.toString();
        } catch (Exception ignored) {}

        // جرّب toString()
        return node.toString();
    }
}
