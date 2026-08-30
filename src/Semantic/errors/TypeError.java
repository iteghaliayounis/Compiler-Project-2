package Semantic.errors;

public class TypeError extends SemanticError {

    public TypeError(String fullMessage, int line, String source) {
        super("TypeError: " + fullMessage, line, source);
    }




    public static TypeError notIterable(String type, int line, String source) {
        return new TypeError("'" + type + "' object is not iterable", line, source);
    }


    public static TypeError noLen(String type, int line, String source) {
        return new TypeError("object of type '" + type + "' has no len()", line, source);
    }


    public static TypeError notSubscriptable(String type, int line, String source) {
        return new TypeError("'" + type + "' object is not subscriptable", line, source);
    }


    public static TypeError concatMismatch(String leftType, String rightType, int line, String source) {
        return new TypeError("can only concatenate " + leftType + " (not \"" + rightType + "\") to " + leftType, line, source);
    }

    public static TypeError multiplySequenceByNonInt(String seqType, String nonIntType, int line, String source) {
        return new TypeError("can't multiply sequence by non-int of type '" + nonIntType + "'", line, source);
    }

    public static TypeError unsupportedOperand(String op, String leftType, String rightType, int line, String source) {
        return new TypeError("unsupported operand type(s) for " + op + ": '" + leftType + "' and '" + rightType + "'", line, source);
    }

    public static TypeError comparisonNotSupported(String op, String leftType, String rightType, int line, String source) {
        return new TypeError("'" + op + "' not supported between instances of '" + leftType + "' and '" + rightType + "'", line, source);
    }


    public static TypeError noneAttribute(String attrName, int line, String source) {
        return new TypeError("'NoneType' object has no attribute '" + attrName + "'", line, source);
    }
}
