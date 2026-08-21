package Generator;

import AST.ASTNode;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.CallSuffixes.CallChainExpr;
import AST.GeneratorExpr.ArithExpr;
import AST.ListDictPair.DictLiteral;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.Pair;
import AST.Literal.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * الشخص 1 — جزء من Python Generator
 *
 * يحوّل عقدة AST تمثل قيمة (expression) إلى كائن Java حقيقي وقابل للاستخدام:
 *   StringLiteral   -> String
 *   IntegerLiteral   -> Integer
 *   FloatLiteral     -> Double
 *   BoolLiteral      -> Boolean
 *   NoneLiteral      -> null
 *   ListLiteral      -> List<Object>
 *   DictLiteral      -> Map<String,Object>   (المفاتيح كلها String لأن Pair.key نص)
 *   Identifier       -> يبحث عن قيمته بجدول المتغيرات المعروفة (knownVariables)
 *
 * أي عقدة تانية (نتيجة استدعاء دالة مثلاً، لسا ما قدرنا نحسبها بشكل ثابت)
 * بترجع null بدل ما تكسر البرنامج — هيك منطقي لأنه مش كل قيمة ممكن تُحسب
 * وقت التوليد الثابت (static generation).
 */
public class LiteralEvaluator {

    /** قيم المتغيرات المعروفة لحد هلق (المتغيرات اللي فسرناها قبل هاي النقطة). */
    private final Map<String, Object> knownVariables;

    public LiteralEvaluator(Map<String, Object> knownVariables) {
        this.knownVariables = knownVariables;
    }

    public Object evaluate(ASTNode node) {
        if (node == null) return null;

        // ── القيم الأدبية المباشرة ──
        if (node instanceof StringLiteral)  return ((StringLiteral) node).value;
        if (node instanceof IntegerLiteral) return ((IntegerLiteral) node).value;
        if (node instanceof FloatLiteral)   return ((FloatLiteral) node).value;
        if (node instanceof BoolLiteral)    return ((BoolLiteral) node).value;
        if (node instanceof NoneLiteral)    return null;

        // ── الحاويات (تحتاج تفريغ للطبقة الداخلية) ──
        if (node instanceof ListAtom) return evaluate(((ListAtom) node).listLiteral);
        if (node instanceof DictAtom) return evaluate(((DictAtom) node).dictLiteral);

        if (node instanceof ListLiteral) {
            List<Object> list = new ArrayList<>();
            for (ASTNode element : ((ListLiteral) node).elements) {
                list.add(evaluate(element));
            }
            return list;
        }

        if (node instanceof DictLiteral) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Pair pair : ((DictLiteral) node).pairs) {
                map.put(stripQuotes(pair.key), evaluate(pair.value));
            }
            return map;
        }

        // ── معرّف بسيط (اسم متغير) ──
        if (node instanceof Identifier) {
            return knownVariables.get(((Identifier) node).name);
        }

        // ── تعبير بين قوسين: (expr) — فك التغليف ومتابعة التقييم ──
        if (node instanceof ParenExpr) {
            return evaluate(((ParenExpr) node).expr);
        }

        // ── عملية حسابية ثابتة: a + b، a - b، a * b، a / b (لو الطرفين أرقام) ──
        if (node instanceof ArithExpr) {
            return evaluateArithExpr((ArithExpr) node);
        }

        // ── كل تعبير بيمر عبر call_chain بيترجم CallChainExpr حتى لو ما في استدعاء فعلي ──
        if (node instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) node;
            if (cc.suffixes == null || cc.suffixes.isEmpty()) {
                // ما في استدعاء/attribute حقيقي — بس تغليف، فك التغليف ومتابعة التقييم
                return evaluate(cc.base);
            }
            // فيه استدعاء دالة أو وصول attribute → قيمة ديناميكية، ما منقدر نحسبها بشكل ثابت
            return null;
        }

        return null;
    }

    /**
     * يحسب عملية حسابية ثابتة (+ - * / %) بس لو كل الأطراف أرقام حقيقية
     * (Integer أو Double). لو أي طرف مش رقم (نتيجة دالة مثلاً) بيرجع null
     * بدل ما يكسر البرنامج — منطقي لأنه هاي القيمة مش قابلة للحساب الثابت.
     */
    private Object evaluateArithExpr(ArithExpr node) {
        List<Object> values = new ArrayList<>();
        for (ASTNode term : node.terms) {
            Object v = evaluate(term);
            if (!(v instanceof Number)) return null; // أي طرف غير رقمي → نوقف الحساب الثابت
            values.add(v);
        }

        double result = ((Number) values.get(0)).doubleValue();
        boolean isFloat = values.get(0) instanceof Double;

        for (int i = 0; i < node.operators.size(); i++) {
            double next = ((Number) values.get(i + 1)).doubleValue();
            isFloat = isFloat || values.get(i + 1) instanceof Double;

            switch (node.operators.get(i)) {
                case "+": result += next; break;
                case "-": result -= next; break;
                case "*": result *= next; break;
                case "/": result /= next; isFloat = true; break;
                case "%": result %= next; break;
                default: return null; // عملية غير مدعومة (** مثلاً) — نتجاهلها بأمان
            }
        }

        return isFloat ? (Object) result : (Object) (int) result;
    }

    private String stripQuotes(String raw) {
        if (raw != null && raw.length() >= 2
                && (raw.charAt(0) == '"' || raw.charAt(0) == '\'')) {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }
}