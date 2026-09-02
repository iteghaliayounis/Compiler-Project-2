package Semantic.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class TypeCompatibility {

    private TypeCompatibility() {} // utility class

    private static final Set<String> ITERABLE_TYPES = new HashSet<>(Arrays.asList(
            "STRING", "LIST", "DICT", "TUPLE", "SET", "RANGE"
    ));

    private static final Set<String> SUBSCRIPTABLE_TYPES = new HashSet<>(Arrays.asList(
            "STRING", "LIST", "DICT", "TUPLE"
    ));

    private static final Set<String> HAS_LEN_TYPES = new HashSet<>(Arrays.asList(
            "STRING", "LIST", "DICT", "TUPLE", "SET", "RANGE"
    ));


    public static boolean isIterable(String type) {
        return ITERABLE_TYPES.contains(PythonTypeInference.normalizeType(type));
    }

    public static boolean isSubscriptable(String type) {
        return SUBSCRIPTABLE_TYPES.contains(PythonTypeInference.normalizeType(type));
    }

    public static boolean hasLen(String type) {
        return HAS_LEN_TYPES.contains(PythonTypeInference.normalizeType(type));
    }

    public static boolean isNumeric(String type) {
        String t = PythonTypeInference.normalizeType(type);
        return "INT".equals(t) || "FLOAT".equals(t) || "BOOL".equals(t);
    }

    public static boolean isNone(String type) {
        return "NONE".equals(PythonTypeInference.normalizeType(type));
    }

    public static boolean bothKnown(String type1, String type2) {
        String t1 = PythonTypeInference.normalizeType(type1);
        String t2 = PythonTypeInference.normalizeType(type2);
        return !"UNKNOWN".equals(t1) && !"UNKNOWN".equals(t2);
    }

  
    public static boolean isAddCompatible(String leftType, String rightType) {
        if (!bothKnown(leftType, rightType)) return true; // لا نعرف → لا نبلغ

        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);

        // None + أي شيء → خطأ
        if ("NONE".equals(lt) || "NONE".equals(rt)) return false;

        // رقمي + رقمي → OK
        if (isNumeric(lt) && isNumeric(rt)) return true;

        // str + str → OK
        if ("STRING".equals(lt) && "STRING".equals(rt)) return true;

        // list + list → OK
        if ("LIST".equals(lt) && "LIST".equals(rt)) return true;

        // tuple + tuple → OK
        if ("TUPLE".equals(lt) && "TUPLE".equals(rt)) return true;

        // كل ما سبق غير متوافق
        return false;
    }

    /**
     * هل النوعان متوافقان لعملية الطرح (-)؟
     * في Python: فقط الأنواع الرقمية (int, float) تدعم الطرح.
     */
    public static boolean isSubCompatible(String leftType, String rightType) {
        if (!bothKnown(leftType, rightType)) return true;
        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);
        if ("NONE".equals(lt) || "NONE".equals(rt)) return false;
        return isNumeric(lt) && isNumeric(rt);
    }

    /**
     * هل النوعان متوافقان لعملية الضرب (*)؟
     * في Python:
     *   - int * int → OK
     *   - int * float → OK
     *   - str * int → OK (repetition)
     *   - int * str → OK
     *   - list * int → OK
     *   - str * str → خطأ (can't multiply sequence by non-int of type 'str')
     */
    public static boolean isMulCompatible(String leftType, String rightType) {
        if (!bothKnown(leftType, rightType)) return true;
        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);
        if ("NONE".equals(lt) || "NONE".equals(rt)) return false;

        if (isNumeric(lt) && isNumeric(rt)) return true;

        // str/list/tuple * int أو bool (وليس float) → تكرار متسلسل صحيح
        if ("STRING".equals(lt) && isIntLike(rt)) return true;
        if (isIntLike(lt) && "STRING".equals(rt)) return true;
        if ("LIST".equals(lt) && isIntLike(rt)) return true;
        if (isIntLike(lt) && "LIST".equals(rt)) return true;
        if ("TUPLE".equals(lt) && isIntLike(rt)) return true;   // 🆕 كانت ناقصة تمامًا
        if (isIntLike(lt) && "TUPLE".equals(rt)) return true;   // 🆕 كانت ناقصة تمامًا

        return false;
    }

    /** INT أو BOOL فقط (وليس FLOAT) — لأن التكرار str*2.5 غير مسموح حتى لو numeric */
    private static boolean isIntLike(String type) {
        return "INT".equals(type) || "BOOL".equals(type);
    }

    /**
     * هل النوعان متوافقان لعملية القسمة (/)؟
     * في Python: فقط الأنواع الرقمية تدعم القسمة.
     */
    public static boolean isDivCompatible(String leftType, String rightType) {
        if (!bothKnown(leftType, rightType)) return true;
        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);
        if ("NONE".equals(lt) || "NONE".equals(rt)) return false;
        return isNumeric(lt) && isNumeric(rt);
    }

    /**
     * هل النوعان متوافقان لعملية Modulo (%)؟
     * في Python: int % int → OK، str % any → OK (string formatting)
     */
    public static boolean isModCompatible(String leftType, String rightType) {
        if (!bothKnown(leftType, rightType)) return true;
        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);
        if ("NONE".equals(lt) || "NONE".equals(rt)) return false;
        // int % int → OK
        if (isNumeric(lt) && isNumeric(rt)) return true;
        // str % any → OK (string formatting)
        if ("STRING".equals(lt)) return true;

        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  توافق عمليات المقارنة
    // ═══════════════════════════════════════════════════════════════════

    /**
     * هل النوعان متوافقان للمقارنة الترتيبية (<, >, <=, >=)؟
     * في Python:
     *   - int < int → OK
     *   - float < float → OK
     *   - int < float → OK
     *   - str < str → OK (lexicographic)
     *   - str < int → خطأ ('<' not supported between instances of 'str' and 'int')
     *   - None < أي شيء → خطأ (في Python 3)
     */
    private static final Set<String> ORDERABLE_SAME_TYPE = new HashSet<>(Arrays.asList(
            "STRING", "LIST", "TUPLE", "SET"   // هدول فقط بيدعموا < بين نفس النوع ببايثون
    ));

    public static boolean isComparisonCompatible(String leftType, String rightType) {
        if (!bothKnown(leftType, rightType)) return true;
        String lt = PythonTypeInference.normalizeType(leftType);
        String rt = PythonTypeInference.normalizeType(rightType);
        if ("NONE".equals(lt) || "NONE".equals(rt)) return false;

        if (isNumeric(lt) && isNumeric(rt)) return true;   // الآن بيشمل bool تلقائياً

        if (lt.equals(rt) && ORDERABLE_SAME_TYPE.contains(lt)) return true;

        return false;
    }
}
