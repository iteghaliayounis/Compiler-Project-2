package Generator;

import java.util.HashMap;
import java.util.Map;

/**
 * الشخص 2 — بيئة متغيرات (Variable Scope) للـ Jinja Renderer.
 *
 * سلسلة نطاقات (scope chain): كل {% for %} أو {% set %} بيفتح نطاق جديد
 * (child) يورث المتغيرات من أبوه، بالضبط متل ما بشتغل Symbol Table
 * بمرحلة التحليل الدلالي — بس هون الهدف مختلف: مش فحص أخطاء، هون تخزين
 * القيم الحقيقية وقت التنفيذ (رندر فعلي).
 */
public class Environment {

    private final Map<String, Object> variables = new HashMap<>();
    private final Environment parent;

    private Environment(Environment parent) {
        this.parent = parent;
    }

    /** بيئة الجذر: القيم القادمة من Context Data تبع الشخص 1. */
    public static Environment root(Map<String, Object> initial) {
        Environment env = new Environment(null);
        if (initial != null) env.variables.putAll(initial);
        return env;
    }

    /** بيئة فرعية جديدة (لجسم for/if/set) بترث من هاي البيئة. */
    public Environment child() {
        return new Environment(this);
    }

    public void define(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {
        if (variables.containsKey(name)) return variables.get(name);
        return parent != null ? parent.get(name) : null;
    }
}