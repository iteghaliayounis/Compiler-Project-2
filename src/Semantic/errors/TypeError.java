package Semantic.errors;

public class TypeError extends SemanticError {

    /**
     * @param fullMessage الرسالة الكاملة كما يظهرها Python/Jinja2
     * @param line        رقم السطر
     * @param source      "PYTHON" أو "JINJA" أو "FLASK-BRIDGE"
     */
    public TypeError(String fullMessage, int line, String source) {
        super("TypeError: " + fullMessage, line, source);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Factory Methods — تبني الرسالة بالصيغة الحقيقية لبايثون
    // ═══════════════════════════════════════════════════════════════════

    /** for i in 5:  →  'int' object is not iterable */
    public static TypeError notIterable(String type, int line, String source) {
        return new TypeError("'" + type + "' object is not iterable", line, source);
    }

    /** len(10)  →  object of type 'int' has no len() */
    public static TypeError noLen(String type, int line, String source) {
        return new TypeError("object of type '" + type + "' has no len()", line, source);
    }

    /** x=5; x[0]  →  'int' object is not subscriptable */
    public static TypeError notSubscriptable(String type, int line, String source) {
        return new TypeError("'" + type + "' object is not subscriptable", line, source);
    }

    /** "a" + 4  →  can only concatenate str (not "int") to str */
    public static TypeError concatMismatch(String leftType, String rightType, int line, String source) {
        return new TypeError("can only concatenate " + leftType + " (not \"" + rightType + "\") to " + leftType, line, source);
    }

    /** "a" * "b"  →  can't multiply sequence by non-int of type 'str' */
    public static TypeError multiplySequenceByNonInt(String seqType, String nonIntType, int line, String source) {
        return new TypeError("can't multiply sequence by non-int of type '" + nonIntType + "'", line, source);
    }

    /** 5 / "hello"  →  unsupported operand type(s) for /: 'int' and 'str' */
    public static TypeError unsupportedOperand(String op, String leftType, String rightType, int line, String source) {
        return new TypeError("unsupported operand type(s) for " + op + ": '" + leftType + "' and '" + rightType + "'", line, source);
    }

    /** "a" < 4  →  '<' not supported between instances of 'str' and 'int' */
    public static TypeError comparisonNotSupported(String op, String leftType, String rightType, int line, String source) {
        return new TypeError("'" + op + "' not supported between instances of '" + leftType + "' and '" + rightType + "'", line, source);
    }

    /** x = None; x.upper()  →  'NoneType' object has no attribute 'upper' */
    public static TypeError noneAttribute(String attrName, int line, String source) {
        return new TypeError("'NoneType' object has no attribute '" + attrName + "'", line, source);
    }
}
