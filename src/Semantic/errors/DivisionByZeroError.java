package Semantic.errors;

/**
 * DivisionByZeroError — يمثّل خطأ القسمة على صفر في Python و Jinja2
 *
 * الخطأ الحقيقي في Python:  ZeroDivisionError
 *
 * حالات الاستخدام:
 *   1) قسمة مباشرة على صفر:    x = 10 / 0
 *   2) modulo على صفر:         x = 10 % 0
 *   3) قسمة على متغير قيمته 0 (معروفة من Symbol Table)
 *
 * ⚠️ ملاحظة ذهبية:
 *   لو قيمة المقسوم عليه UNKNOWN (ما معروفش وقت الـ compile)
 *   → ما تبلّغي عن خطأ (False Positive أسوأ من False Negative).
 */
public class DivisionByZeroError extends SemanticError {

    /**
     * Constructor أساسي — قسمة عادية على صفر
     *
     * الرسالة: ZeroDivisionError: division by zero
     */
    public DivisionByZeroError(int line, String source) {
        super("ZeroDivisionError: division by zero", line, source);
    }

    /**
     * Factory method لحالة modulo على صفر
     *
     * مثال: x = 10 % 0
     * الرسالة: ZeroDivisionError: integer division or modulo by zero
     */
    public static DivisionByZeroError moduloByZero(int line, String source) {
        return new DivisionByZeroError(line, source) {
            @Override
            public String toString() {
                return "[" + source + "] ZeroDivisionError: integer division or modulo by zero"
                        + (line > 0 ? " (line " + line + ")" : "");
            }
        };
    }
}
