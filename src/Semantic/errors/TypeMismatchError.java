package Semantic.errors;

/**
 * TypeMismatchError — خطأ في توافق الأنواع (Type Mismatch)
 *
 * يُستخدم في حالتين فقط (مختلفتين عن Type Error):
 *
 * 1) إسناد نوع خاطئ عند وجود type annotation في Python:
 *    c: int = "hello"   →  expected int, got str
 *
 * 2) فلتر Jinja2 يتوقع نوعاً معيناً والمتغير الممرر من Flask نوعه مختلف:
 *    {{ age|upper }}  حيث age=int  →  filter 'upper' expects str, got int
 */
public class TypeMismatchError extends SemanticError {

    /**
     * Constructor عام يقبل رسالة كاملة
     * @param fullMessage الرسالة الكاملة
     * @param line        رقم السطر
     * @param source      "PYTHON" أو "FLASK-BRIDGE"
     */
    public TypeMismatchError(String fullMessage, int line, String source) {
        super(fullMessage, line, source);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Factory Methods
    // ═══════════════════════════════════════════════════════════════════

    /** c: int = "hello"  →  TypeError: expected int, got str */
    public static TypeMismatchError annotationMismatch(String expected, String actual, int line, String source) {
        return new TypeMismatchError("TypeError: expected " + expected + ", got " + actual, line, source);
    }

    /** {{ age|upper }} حيث age=int  →  TypeError: filter 'upper' expects str, got int in template 'page.html' */
    public static TypeMismatchError filterTypeMismatch(String filterName, String expected, String actual,
                                                       String templateName, int line) {
        String message = "TypeError: filter '" + filterName + "' expects " + expected + ", got " + actual
                + " in template '" + templateName + "'";
        return new TypeMismatchError(message, line, "FLASK-BRIDGE");
    }
}
