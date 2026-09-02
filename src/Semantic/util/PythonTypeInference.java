package Semantic.util;

import AST.ASTNode;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.CallSuffixes.*;
import AST.Expressions.Expr.ComparisonExpr;
import AST.GeneratorExpr.ArithExpr;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.DictLiteral;
import SymbolTable.SymbolTable;

import java.util.List;


public class PythonTypeInference {

    private PythonTypeInference() {} // utility class


    public static String inferType(ASTNode node, SymbolTable st) {
        if (node == null) return "UNKNOWN";

        // 1) Identifier → ابحث في Symbol Table
        if (node instanceof Identifier) {
            String name = ((Identifier) node).name;
            SymbolTable.Symbol sym = st.lookupInAllScopes(name);
            if (sym != null) return normalizeType(sym.getType());
            return "UNKNOWN";
        }

        // 2) ParenExpr → فك القوسين
        if (node instanceof ParenExpr) {
            return inferType(((ParenExpr) node).expr, st);
        }

        // 3) List literal → LIST
        if (node instanceof ListAtom)  return inferType(((ListAtom) node).listLiteral, st);
        if (node instanceof ListLiteral) return "LIST";

        // 4) Dict literal → DICT
        if (node instanceof DictAtom)  return inferType(((DictAtom) node).dictLiteral, st);
        if (node instanceof DictLiteral) return "DICT";

        // 5) ArithExpr (+, -) → نحدد نوعه من نوع المعاملات والعامل
        if (node instanceof ArithExpr) {
            return inferArithType((ArithExpr) node, st);
        }

        // 6) ComparisonExpr → BOOL دائماً
        if (node instanceof ComparisonExpr) return "BOOL";

        // 7) CallChainExpr → قد يكون استدعاء دالة أو وصول لعنصر
        if (node instanceof CallChainExpr) {
            return inferCallChainType((CallChainExpr) node, st);
        }

        // 8) Literals — استنتاج من اسم الكلاس (النمط الدفاعي)
        String className = node.getClass().getSimpleName();
        return inferFromClassName(className);
    }


    private static String inferArithType(ArithExpr node, SymbolTable st) {
        if (node.terms == null || node.terms.isEmpty()) return "UNKNOWN";

        // إذا كان هناك operand واحد فقط (بدون operators) → نوعه هو النوع
        if (node.operators == null || node.operators.isEmpty() || node.terms.size() == 1) {
            return inferType(node.terms.get(0), st);
        }

        // ابدأ بنوع المعامل الأول
        String resultType = inferType(node.terms.get(0), st);

        // ادمج كل زوج معاملات حسب العامل بينهما
        for (int i = 0; i < node.operators.size() && (i + 1) < node.terms.size(); i++) {
            String op = node.operators.get(i);           // "PLUS" أو "MINUS"
            String rightType = inferType(node.terms.get(i + 1), st);
            resultType = inferArithPair(resultType, op, rightType);
        }
        return resultType;
    }

 
    private static String inferArithPair(String leftType, String op, String rightType) {
        String lt = normalizeType(leftType);
        String rt = normalizeType(rightType);
        boolean isPlus = "PLUS".equalsIgnoreCase(op) || "+".equals(op);

        // الجمع (+)
        if (isPlus) {
            // str + str → str
            if ("STRING".equals(lt) && "STRING".equals(rt)) return "STRING";
            // list + list → list
            if ("LIST".equals(lt) && "LIST".equals(rt)) return "LIST";
            // tuple + tuple → tuple
            if ("TUPLE".equals(lt) && "TUPLE".equals(rt)) return "TUPLE";
            // رقمي + رقمي → FLOAT إذا أحدهما FLOAT، وإلا INT
            if (isNumeric(lt) && isNumeric(rt)) {
                return isFloat(lt) || isFloat(rt) ? "FLOAT" : "INT";
            }
            return "UNKNOWN";
        }

        // الطرح (-) — فقط للأرقام
        if (isNumeric(lt) && isNumeric(rt)) {
            return isFloat(lt) || isFloat(rt) ? "FLOAT" : "INT";
        }
        // set - set → set (في Python)
        if ("SET".equals(lt) && "SET".equals(rt)) return "SET";
        return "UNKNOWN";
    }

    private static boolean isNumeric(String type) {
        String t = normalizeType(type);
        return "INT".equals(t) || "FLOAT".equals(t);
    }

    /** Helper: هل النوع float؟ */
    private static boolean isFloat(String type) {
        return "FLOAT".equals(normalizeType(type));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  استنتاج نوع الـ CallChainExpr
    // ═══════════════════════════════════════════════════════════════════
    private static String inferCallChainType(CallChainExpr node, SymbolTable st) {
        if (node.base == null) return "UNKNOWN";

        // إذا لا يوجد suffixes → هو مجرد متغير
        if (node.suffixes == null || node.suffixes.isEmpty()) {
            return inferType(node.base, st);
        }

        // خذ آخر suffix لتحديد النوع الناتج
        CallSuffix lastSuffix = node.suffixes.get(node.suffixes.size() - 1);

        // استدعاء دالة
        if (lastSuffix instanceof FunctionCall) {
            if (node.base instanceof Identifier) {
                String funcName = ((Identifier) node.base).name;
                return inferBuiltinReturnType(funcName);
            }
            return "UNKNOWN";
        }

        // فهرسة arr[0] → نوع العنصر
        if (lastSuffix instanceof IndexAccess) {
            String baseType = inferType(node.base, st);
            if ("STRING".equals(baseType)) return "STRING";
            return "UNKNOWN";
        }

        return "UNKNOWN";
    }

    /** أنواع إرجاع الدوال المدمجة في بايثون */
    private static String inferBuiltinReturnType(String funcName) {
        if (funcName == null) return "UNKNOWN";
        switch (funcName) {
            case "len":   return "INT";
            case "range": return "RANGE";
            case "str":   return "STRING";
            case "int":   return "INT";
            case "float": return "FLOAT";
            case "bool":  return "BOOL";
            case "list":  return "LIST";
            case "dict":  return "DICT";
            case "tuple": return "TUPLE";
            case "set":   return "SET";
            case "type":  return "STRING";
            case "isinstance": return "BOOL";
            case "abs":   return "INT";
            case "round": return "INT";
            case "sum":   return "INT";
            case "min":
            case "max":   return "UNKNOWN";
            case "sorted": return "LIST";
            case "reversed": return "RANGE";
            case "enumerate": return "RANGE";
            case "zip":   return "RANGE";
            case "map":
            case "filter": return "RANGE";
            case "open":  return "UNKNOWN";
            case "next":  return "UNKNOWN";
            case "iter":  return "RANGE";
            case "print": return "NONE";
            default:      return "UNKNOWN";
        }
    }


    private static String inferFromClassName(String className) {
        if (className == null || className.isEmpty()) return "UNKNOWN";
        String lower = className.toLowerCase();

        if (lower.contains("int") || lower.contains("integer")) return "INT";
        if (lower.contains("float") || lower.contains("double")) return "FLOAT";
        if (lower.contains("str")  || lower.contains("string")) return "STRING";
        if (lower.contains("bool"))  return "BOOL";
        if (lower.contains("none") || lower.contains("null")) return "NONE";
        if (lower.contains("list") || lower.contains("array")) return "LIST";
        if (lower.contains("dict") || lower.contains("map"))  return "DICT";
        if (lower.contains("tuple")) return "TUPLE";
        if (lower.contains("set"))   return "SET";
        return "UNKNOWN";
    }


    public static String normalizeType(String type) {
        if (type == null) return "UNKNOWN";
        String upper = type.toUpperCase().trim();
        switch (upper) {
            case "STR":
            case "STRING":
            case "TEXT":
                return "STRING";
            case "INT":
            case "INTEGER":
                return "INT";
            case "FLOAT":
            case "DOUBLE":
            case "REAL":
                return "FLOAT";
            case "BOOL":
            case "BOOLEAN":
                return "BOOL";
            case "LIST":
            case "ARRAY":
                return "LIST";
            case "DICT":
            case "MAP":
            case "HASHMAP":
                return "DICT";
            case "TUPLE":
                return "TUPLE";
            case "SET":
                return "SET";
            case "NONE":
            case "NULL":
            case "NONETYPE":
                return "NONE";
            case "FUNCTION":
            case "FUNC":
            case "METHOD":
                return "FUNCTION";
            case "TEMPLATE":
                return "TEMPLATE";
            case "RANGE":
                return "RANGE";
            default:
                return upper;
        }
    }

    /** تحويل من الصيغة الداخلية إلى صيغة Python المعروضة للخطأ */
    public static String toPythonTypeName(String internalType) {
        String t = normalizeType(internalType);
        switch (t) {
            case "STRING":   return "str";
            case "INT":      return "int";
            case "FLOAT":    return "float";
            case "BOOL":     return "bool";
            case "LIST":     return "list";
            case "DICT":     return "dict";
            case "TUPLE":    return "tuple";
            case "SET":      return "set";
            case "NONE":     return "NoneType";
            case "FUNCTION": return "function";
            case "RANGE":    return "range";
            default:         return t.toLowerCase();
        }
    }
}
