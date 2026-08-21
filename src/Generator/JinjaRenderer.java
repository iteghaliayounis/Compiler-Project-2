package Generator;

import AstHtml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * الشخص 2 — Jinja Generator (القلب الرئيسي)
 *
 * يمشي على شجرة AstHtml (نفس شجرة HtmlVisitor بدون أي تعديل عليها) ويستبدل:
 *   {{ var }}          → القيمة الحقيقية من الـ Context Data
 *   {% for x in y %}   → تكرار فعلي للعناصر
 *   {% if cond %}      → تقييم الشرط واختيار الفرع الصحيح
 * وينتج بالنهاية HTML نهائي جاهز.
 *
 * الاستخدام:
 *   JinjaRenderer renderer = new JinjaRenderer();
 *   String html = renderer.render(templateRoot, contextData);
 *
 * ملاحظة: implements AstVisitor<Object> مش <String> لأنه عقد التعابير
 * (VariableNode, BinaryOpNode...) لازم ترجع قيمة حقيقية (رقم/نص/قائمة)
 * مو نص جاهز، عشان نقدر نستخدمها بمقارنات {% if %} وحسابات. عقد الـ HTML
 * (ElementNode, TextNode...) بترجع String (نص الـ HTML الناتج) ملفوف كـ Object.
 */
public class JinjaRenderer implements AstVisitor<Object> {

    private Environment env;

    // ★ جديد: خريطة الـ routes القادمة من PythonContextGenerator.getRoutes()
    private Map<String, String> routes = new java.util.HashMap<>();
    // ★ جديد: خريطة "endpoint" أو "endpoint:argValue" → اسم ملف الـ output الحقيقي
    // (مبنية بـ MainPipeline قبل أي رندرة، عشان url_for يشير لاسم الملف الصحيح مش لمسار Flask الخام)
    private Map<String, String> endpointFileMap = new java.util.HashMap<>();
    // ★ جديد: سجل تحذيرات مرحلة الـ Jinja rendering
    private final List<String> log = new ArrayList<>();

    public void setRoutes(Map<String, String> routes) {
        if (routes != null) this.routes = routes;
    }

    public void setEndpointFileMap(Map<String, String> endpointFileMap) {
        if (endpointFileMap != null) this.endpointFileMap = endpointFileMap;
    }

    public List<String> getLog() { return log; }

    /** نقطة الدخول الرئيسية. */
    public String render(TemplateNode root, Map<String, Object> context) {
        this.env = Environment.root(context);
        return stringify(root.accept(this));
    }

    // ====================================================================
    //  Top-level / Statements
    // ====================================================================

    @Override
    public Object visit(TemplateNode node) {
        return renderChildren(node.getChildren());
    }

    @Override
    public Object visit(TextNode node) {
        return node.getValue();
    }

    @Override
    public Object visit(JinjaVarOutputNode node) {
        Object value = evalExpr(node.getExpression());
        // Autoescape افتراضي (متل Jinja2/Flask) — أمان أساسي ضد HTML injection
        return escapeHtml(stringify(value));
    }

    @Override
    public Object visit(IfNode node) {
        List<ExpressionNode> conditions = node.getConditions();
        List<List<AstNode>> bodies = node.getBodies();

        for (int i = 0; i < conditions.size(); i++) {
            if (truthy(evalExpr(conditions.get(i)))) {
                return renderChildren(bodies.get(i));
            }
        }
        if (node.hasElse()) {
            return renderChildren(node.getElseBody());
        }
        return "";
    }

    @Override
    public Object visit(ForNode node) {
        Object iterableValue = evalExpr(node.getIterable());
        List<Object> items = toIterableList(iterableValue);

        if (items.isEmpty()) {
            return node.hasElse() ? renderChildren(node.getElseBody()) : "";
        }

        StringBuilder sb = new StringBuilder();
        Environment savedEnv = env;
        List<String> targets = node.getTargets();

        for (Object item : items) {
            env = savedEnv.child();
            bindForTargets(targets, item);
            sb.append(stringify(renderChildren(node.getBody())));
        }
        env = savedEnv;
        return sb.toString();
    }

    @Override
    public Object visit(SetNode node) {
        Object value = evalExpr(node.getValue());
        env.define(node.getVariable(), value);
        return "";
    }

    @Override
    public Object visit(WithNode node) {
        Environment savedEnv = env;
        env = savedEnv.child();
        for (WithNode.Assignment a : node.getAssignments()) {
            env.define(a.name, evalExpr(a.value));
        }
        Object result = renderChildren(node.getBody());
        env = savedEnv;
        return result;
    }

    @Override
    public Object visit(BlockNode node) {
        // بدون دعم توريث قوالب (extends) — منعرض جسم الـ block متل ما هو
        return renderChildren(node.getBody());
    }

    @Override
    public Object visit(RawNode node) {
        return renderChildren(node.getBody());
    }

    @Override
    public Object visit(FilterBlockNode node) {
        String bodyText = stringify(renderChildren(node.getBody()));
        List<Object> args = new ArrayList<>();
        for (ExpressionNode a : node.getFilterArgs()) args.add(evalExpr(a));
        return JinjaFilters.apply(node.getFilterName(), bodyText, args);
    }

    // ── ميزات متقدمة مش مستخدمة بقوالب مشروعنا (index/add/edit) — دعم بسيط بدون كسر التوليد ──
    @Override
    public Object visit(ExtendsNode node) { return ""; }
    @Override
    public Object visit(IncludeNode node) { return ""; }
    @Override
    public Object visit(ImportNode node) { return ""; }
    @Override
    public Object visit(FromImportNode node) { return ""; }
    @Override
    public Object visit(MacroNode node) { return ""; }

    // ====================================================================
    //  HTML
    // ====================================================================

    @Override
    public Object visit(ElementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(node.getTagName());
        sb.append(buildAttributes(node.getAttributes(), node.getStyleAttributes()));
        sb.append(">");
        sb.append(stringify(renderChildren(node.getChildren())));
        sb.append("</").append(node.getTagName()).append(">");
        return sb.toString();
    }

    @Override
    public Object visit(VoidElementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(node.getTagName());
        sb.append(buildAttributes(node.getAttributes(), node.getStyleAttributes()));
        sb.append(node.isSelfClosing() ? " />" : ">");
        return sb.toString();
    }

    @Override
    public Object visit(StyleElementNode node) {
        StringBuilder sb = new StringBuilder("<style>");
        for (CssNode stmt : node.getStatements()) {
            sb.append(stringify(stmt.accept(this))).append("\n");
        }
        sb.append("</style>");
        return sb.toString();
    }

    @Override
    public Object visit(ScriptElementNode node) {
        StringBuilder sb = new StringBuilder("<script>");
        if (node.hasContent()) sb.append(node.getRawContent());
        sb.append("</script>");
        return sb.toString();
    }

    @Override
    public Object visit(AttributeNode node) {
        // بتترسم عبر buildAttributes مباشرة، هاي بس fallback
        return node.toString();
    }

    @Override
    public Object visit(StyleAttributeNode node) {
        return node.toString();
    }

    // ====================================================================
    //  Expressions — بترجع قيمة حقيقية (Object) مو نص HTML
    // ====================================================================

    @Override
    public Object visit(NumberLiteral node) { return node.getValue(); }

    @Override
    public Object visit(StringLiteral node) { return node.getValue(); }

    @Override
    public Object visit(BooleanLiteral node) { return node.getValue(); }

    @Override
    public Object visit(NoneLiteral node) { return null; }

    @Override
    public Object visit(VariableNode node) {
        Object value = env.get(node.getName());
        if (value == null) {
            log.add("[WARNING] المتغير '" + node.getName() + "' غير موجود بالـ Context (سطر " + node.getLine() + ")");
        }
        return value;
    }

    @Override
    public Object visit(AttributeAccessNode node) {
        Object obj = evalExpr(node.getObject());
        String attr = node.getAttributeName();
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).get(attr);
        }
        return null; // كائن مش Map (زي كائن Python حقيقي) — خارج نطاق مشروعنا الحالي
    }

    @Override
    public Object visit(IndexNode node) {
        Object array = evalExpr(node.getArray());
        Object index = evalExpr(node.getIndex());
        if (array instanceof List && index instanceof Number) {
            List<?> list = (List<?>) array;
            int i = ((Number) index).intValue();
            return (i >= 0 && i < list.size()) ? list.get(i) : null;
        }
        if (array instanceof Map) {
            return ((Map<?, ?>) array).get(String.valueOf(index));
        }
        return null;
    }

    @Override
    public Object visit(SliceNode node) {
        Object arrayObj = evalExpr(node.getArray());
        if (!(arrayObj instanceof List)) return arrayObj;
        List<?> list = (List<?>) arrayObj;

        int size = list.size();
        int start = node.getStart() != null ? asInt(evalExpr(node.getStart()), 0) : 0;
        int stop = node.getStop() != null ? asInt(evalExpr(node.getStop()), size) : size;
        if (start < 0) start += size;
        if (stop < 0) stop += size;
        start = Math.max(0, Math.min(size, start));
        stop = Math.max(0, Math.min(size, stop));
        if (start >= stop) return new ArrayList<>();
        return new ArrayList<>(list.subList(start, stop));
    }

    @Override
    public Object visit(CallNode node) {
        if (node.getCallee() instanceof VariableNode) {
            String name = ((VariableNode) node.getCallee()).getName();

            if ("range".equals(name)) {
                List<ExpressionNode> args = node.getArguments();
                int start = 0, end;
                if (args.size() == 1) {
                    end = asInt(evalExpr(args.get(0)), 0);
                } else {
                    start = asInt(evalExpr(args.get(0)), 0);
                    end = asInt(evalExpr(args.get(1)), 0);
                }
                List<Object> result = new ArrayList<>();
                for (int i = start; i < end; i++) result.add(i);
                return result;
            }

            // ★ جديد: دعم url_for فعليًا
            if ("url_for".equals(name)) {
                return resolveUrlFor(node);
            }
        }
        return null;
    }

    // ★ تابع جديد كامل
    private String resolveUrlFor(CallNode node) {
        List<ExpressionNode> posArgs = node.getArguments();
        if (posArgs.isEmpty()) {
            log.add("[WARNING] url_for() استُدعي بدون اسم endpoint (سطر " + node.getLine() + ")");
            return "#";
        }

        Object endpointVal = evalExpr(posArgs.get(0));
        String endpoint = endpointVal == null ? null : endpointVal.toString();

        // ── حالة خاصة: url_for('static', filename='images/x.jpg') ──
        if ("static".equals(endpoint)) {
            Object filenameVal = null;
            for (Map.Entry<String, ExpressionNode> e : node.getNamedArguments().entrySet()) {
                if ("filename".equals(e.getKey())) {
                    filenameVal = evalExpr(e.getValue());
                }
            }
            return "static/" + stringify(filenameVal);
        }

        // ── جديد: أول شي نجرب نلاقي اسم ملف الـ output الحقيقي لهاد الـ endpoint ──
        Map<String, ExpressionNode> namedArgsForFile = node.getNamedArguments();
        if (namedArgsForFile.isEmpty()) {
            String direct = endpointFileMap.get(endpoint);
            if (direct != null) return direct;
        } else {
            for (Map.Entry<String, ExpressionNode> e : namedArgsForFile.entrySet()) {
                String argValue = stringify(evalExpr(e.getValue()));
                String withArg = endpointFileMap.get(endpoint + ":" + argValue);
                if (withArg != null) return withArg;
            }
        }

        // ── حالة عادية (fallback): endpoint موجود بجدول الـ routes ──
        String pattern = routes.get(endpoint);
        if (pattern == null) {
            log.add("[WARNING] url_for: endpoint غير معروف: '" + endpoint + "' (سطر " + node.getLine() + ")");
            return "#";
        }

        String result = pattern;
        for (Map.Entry<String, ExpressionNode> e : node.getNamedArguments().entrySet()) {
            String argName = e.getKey();
            String argValue = stringify(evalExpr(e.getValue()));
            // بيغطي شكلين: <int:pid> و <pid>
            result = result.replace("<int:" + argName + ">", argValue);
            result = result.replace("<" + argName + ">", argValue);
        }
        return result;
    }

    @Override
    public Object visit(FilterNode node) {
        Object operand = evalExpr(node.getOperand());
        List<Object> args = new ArrayList<>();
        for (ExpressionNode a : node.getArguments()) args.add(evalExpr(a));
        return JinjaFilters.apply(node.getFilterName(), operand, args);
    }

    @Override
    public Object visit(BinaryOpNode node) {
        if (node.getOperator() == BinaryOpNode.Operator.AND) {
            Object l = evalExpr(node.getLeft());
            return truthy(l) ? truthy(evalExpr(node.getRight())) : false;
        }
        if (node.getOperator() == BinaryOpNode.Operator.OR) {
            Object l = evalExpr(node.getLeft());
            return truthy(l) ? true : truthy(evalExpr(node.getRight()));
        }

        Object left = evalExpr(node.getLeft());
        Object right = evalExpr(node.getRight());

        switch (node.getOperator()) {
            case ADD:
                if (left instanceof String || right instanceof String) return stringify(left) + stringify(right);
                return numOp(left, right, '+');
            case SUB: return numOp(left, right, '-');
            case MUL: return numOp(left, right, '*');
            case DIV: return numOp(left, right, '/');
            case MOD: return numOp(left, right, '%');
            case CONCAT: return stringify(left) + stringify(right);
            case EQ: return looseEquals(left, right);
            case NEQ: return !looseEquals(left, right);
            case LT: return compareNum(left, right) < 0;
            case GT: return compareNum(left, right) > 0;
            case LTE: return compareNum(left, right) <= 0;
            case GTE: return compareNum(left, right) >= 0;
            case IN: return containsValue(right, left);
            case NOT_IN: return !containsValue(right, left);
            case IS: return looseEquals(left, right);
            case IS_NOT: return !looseEquals(left, right);
            default: return null;
        }
    }

    @Override
    public Object visit(UnaryOpNode node) {
        Object operand = evalExpr(node.getOperand());
        if (node.getOperator() == UnaryOpNode.Operator.NOT) return !truthy(operand);
        // NEG
        double d = toDouble(operand);
        return (d == Math.floor(d)) ? (Object) (long) -d : (Object) (-d);
    }

    @Override
    public Object visit(TernaryNode node) {
        boolean cond = truthy(evalExpr(node.getCondition()));
        return cond ? evalExpr(node.getValue()) : evalExpr(node.getAlternative());
    }

    // ====================================================================
    //  CSS
    // ====================================================================

    @Override
    public Object visit(CssRuleSetNode node) {
        StringBuilder sb = new StringBuilder();
        List<CssSelectorNode> sels = node.getSelectors();
        for (int i = 0; i < sels.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(sels.get(i).getSelector());
        }
        sb.append(" {\n");
        for (CssDeclarationNode decl : node.getDeclarations()) {
            sb.append("  ").append(stringify(decl.accept(this))).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Object visit(CssAtRuleNode node) {
        StringBuilder sb = new StringBuilder("@").append(node.getKeyword());
        if (!node.getPrelude().isEmpty()) sb.append(" ").append(node.getPrelude());
        if (node.hasBlock()) {
            sb.append(" {\n");
            for (CssNode stmt : node.getBody()) {
                sb.append(stringify(stmt.accept(this))).append("\n");
            }
            sb.append("}");
        } else {
            sb.append(";");
        }
        return sb.toString();
    }

    @Override
    public Object visit(CssSelectorNode node) { return node.getSelector(); }

    @Override
    public Object visit(CssDeclarationNode node) {
        StringBuilder sb = new StringBuilder(node.getProperty()).append(": ");
        List<CssValueNode> values = node.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(stringify(values.get(i).accept(this)));
        }
        if (node.isImportant()) sb.append(" !important");
        sb.append(";");
        return sb.toString();
    }

    @Override
    public Object visit(CssNumberValueNode node) { return node.toCssString(); }
    @Override
    public Object visit(CssHashValueNode node) { return node.toCssString(); }
    @Override
    public Object visit(CssStringValueNode node) { return node.toCssString(); }
    @Override
    public Object visit(CssIdentValueNode node) { return node.toCssString(); }

    @Override
    public Object visit(CssFunctionValueNode node) {
        StringBuilder sb = new StringBuilder(node.getName()).append("(");
        List<CssValueNode> args = node.getArguments();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(stringify(args.get(i).accept(this)));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Object visit(CssJinjaValueNode node) {
        return stringify(evalExpr(node.getExpression()));
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    private Object renderChildren(List<AstNode> nodes) {
        StringBuilder sb = new StringBuilder();
        for (AstNode n : nodes) {
            sb.append(stringify(n.accept(this)));
        }
        return sb.toString();
    }

    private Object evalExpr(ExpressionNode expr) {
        return expr == null ? null : expr.accept(this);
    }

    private String buildAttributes(List<AttributeNode> attrs, List<StyleAttributeNode> styleAttrs) {
        StringBuilder sb = new StringBuilder();
        for (AttributeNode attr : attrs) {
            sb.append(" ");
            if (attr.isBoolean()) {
                sb.append(attr.getName());
            } else if (attr.getStringValue() != null) {
                sb.append(attr.getName()).append("=\"").append(attr.getStringValue()).append("\"");
            } else if (attr.getJinjaValue() != null) {
                Object val = evalExpr(attr.getJinjaValue());
                sb.append(attr.getName()).append("=\"").append(escapeHtml(stringify(val))).append("\"");
            }
        }
        for (StyleAttributeNode styleAttr : styleAttrs) {
            sb.append(" style=\"");
            List<CssDeclarationNode> decls = styleAttr.getDeclarations();
            for (int i = 0; i < decls.size(); i++) {
                if (i > 0) sb.append(" ");
                sb.append(stringify(decls.get(i).accept(this)));
            }
            sb.append("\"");
        }
        return sb.toString();
    }

    private void bindForTargets(List<String> targets, Object item) {
        if (targets.size() == 1) {
            env.define(targets.get(0), item);
            return;
        }
        if (item instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) item;
            if (targets.size() >= 1) env.define(targets.get(0), entry.getKey());
            if (targets.size() >= 2) env.define(targets.get(1), entry.getValue());
            return;
        }
        // حالة عامة: أول target ياخد العنصر كامل، الباقي null (كافي لقوالبنا الحالية)
        if (!targets.isEmpty()) env.define(targets.get(0), item);
    }

    @SuppressWarnings("unchecked")
    private List<Object> toIterableList(Object value) {
        if (value == null) return new ArrayList<>();
        if (value instanceof List) return (List<Object>) value;
        if (value instanceof Map) return new ArrayList<>(((Map<?, ?>) value).entrySet());
        List<Object> single = new ArrayList<>();
        single.add(value);
        return single;
    }

    private boolean truthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        if (value instanceof List) return !((List<?>) value).isEmpty();
        if (value instanceof Map) return !((Map<?, ?>) value).isEmpty();
        return true;
    }

    private boolean looseEquals(Object a, Object b) {
        if (a == null || b == null) return a == b;
        if (a instanceof Number && b instanceof Number) {
            return ((Number) a).doubleValue() == ((Number) b).doubleValue();
        }
        return a.equals(b);
    }

    private int compareNum(Object a, Object b) {
        return Double.compare(toDouble(a), toDouble(b));
    }

    private boolean containsValue(Object container, Object value) {
        if (container instanceof List) {
            for (Object o : (List<?>) container) if (looseEquals(o, value)) return true;
            return false;
        }
        if (container instanceof Map) return ((Map<?, ?>) container).containsKey(value);
        if (container instanceof String) return ((String) container).contains(stringify(value));
        return false;
    }

    private Object numOp(Object a, Object b, char op) {
        double x = toDouble(a), y = toDouble(b);
        double result;
        switch (op) {
            case '+': result = x + y; break;
            case '-': result = x - y; break;
            case '*': result = x * y; break;
            case '/': result = y != 0 ? x / y : 0; break;
            case '%': result = y != 0 ? x % y : 0; break;
            default: result = 0;
        }
        // رجّعي عدد صحيح إذا الطرفين أعداد صحيحة وناتج بدون كسور
        if (isWhole(a) && isWhole(b) && result == Math.floor(result)) return (long) result;
        return result;
    }

    private boolean isWhole(Object o) {
        return o instanceof Integer || o instanceof Long || o == null && false;
    }

    private double toDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; }
    }

    private int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        return def;
    }

    private String stringify(Object value) {
        if (value == null) return "";
        if (value instanceof Double) {
            double d = (Double) value;
            if (d == Math.floor(d) && !Double.isInfinite(d)) return String.valueOf((long) d);
        }
        return String.valueOf(value);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}