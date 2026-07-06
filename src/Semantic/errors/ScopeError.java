package Semantic.errors;

/**
 * ScopeError — يمثّل خطأ Scope في Python و Jinja2
 *
 * الخطأ الحقيقي في Python:  UnboundLocalError
 *
 * حالات الاستخدام:
 *   1) متغير محلي داخل function، مستخدم براها
 *   2) متغير الـ for loop بعد انتهاء الحلقة
 *   3) متغير الـ for loop في Jinja بعد {% endfor %}
 *
 * الفرق عن UndefinedVarError (غالية):
 *   - UndefinedVarError = المتغير غير موجود أصلاً في أي scope
 *   - ScopeError         = المتغير موجود في scope آخر (محذوف من الـ scope الحالي)
 */
public class ScopeError extends SemanticError {

    /**
     * Constructor أساسي — يطابق رسالة Python الأصلية
     *
     * @param varName اسم المتغير
     * @param line    رقم السطر
     * @param source  "PYTHON" أو "JINJA"
     */
    public ScopeError(String varName, int line, String source) {
        super("UnboundLocalError: local variable '" + varName
                + "' referenced before assignment", line, source);
    }

    /**
     * Factory method لحالة Jinja:
     * متغير الـ for loop مستخدم بعد {% endfor %}
     *
     * مثال:
     *   {% for p in products %}{% endfor %}
     *   {{ p }}    ← خطأ!
     *
     * الرسالة: UnboundLocalError: 'p' is not defined outside loop
     */
    public static ScopeError loopVarOutsideLoop(String varName, int line, String source) {
        return new ScopeError(varName, line, source) {
            @Override
            public String toString() {
                return "[" + "JINJA" + "] UnboundLocalError: '"
                        + varName + "' is not defined outside loop"
                        + (line > 0 ? " (line " + line + ")" : "");
            }
        };
    }
}
