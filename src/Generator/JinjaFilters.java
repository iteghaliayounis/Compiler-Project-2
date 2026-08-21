package Generator;

import java.util.List;
import java.util.Map;

/**
 * الشخص 2 — تطبيق أشهر Jinja filters (المستخدمة عادة بمشاريع Flask بسيطة).
 * أي فلتر مش معروف: بترجع القيمة متل ما هي بدل ما تكسّري التوليد بالكامل.
 */
public class JinjaFilters {

    public static Object apply(String name, Object value, List<Object> args) {
        switch (name) {
            case "upper":
                return value == null ? "" : value.toString().toUpperCase();

            case "lower":
                return value == null ? "" : value.toString().toLowerCase();

            case "capitalize": {
                String s = value == null ? "" : value.toString();
                return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
            }

            case "trim":
                return value == null ? "" : value.toString().trim();

            case "length":
            case "count":
                if (value instanceof List) return ((List<?>) value).size();
                if (value instanceof Map) return ((Map<?, ?>) value).size();
                if (value instanceof String) return ((String) value).length();
                return 0;

            case "default": {
                Object def = (args != null && !args.isEmpty()) ? args.get(0) : "";
                return (value == null || "".equals(value)) ? def : value;
            }

            case "round": {
                double d = toDouble(value);
                int digits = (args != null && !args.isEmpty()) ? (int) toDouble(args.get(0)) : 0;
                double factor = Math.pow(10, digits);
                return Math.round(d * factor) / factor;
            }

            case "e":
            case "escape":
                return escapeHtml(value == null ? "" : value.toString());

            case "join": {
                String sep = (args != null && !args.isEmpty()) ? args.get(0).toString() : "";
                if (value instanceof List) {
                    StringBuilder sb = new StringBuilder();
                    List<?> list = (List<?>) value;
                    for (int i = 0; i < list.size(); i++) {
                        if (i > 0) sb.append(sep);
                        sb.append(list.get(i));
                    }
                    return sb.toString();
                }
                return value;
            }

            case "first":
                return (value instanceof List && !((List<?>) value).isEmpty())
                        ? ((List<?>) value).get(0) : null;

            case "last":
                if (value instanceof List && !((List<?>) value).isEmpty()) {
                    List<?> l = (List<?>) value;
                    return l.get(l.size() - 1);
                }
                return null;

            default:
                return value;
        }
    }

    private static double toDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(o));
        } catch (Exception e) {
            return 0;
        }
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}