package Generator;

import AST.ASTNode;
import AstHtml.AstNode;
import AstHtml.TemplateNode;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class AstJsonSerializer {


    public static String pythonTreeToJson(ASTNode root) {
        return valueToJson(root, 0);
    }

    private static String objectToJson(Object obj, int indent) {
        String ind = "  ".repeat(indent);
        String childInd = "  ".repeat(indent + 1);
        StringBuilder sb = new StringBuilder("{\n");

        if (obj instanceof ASTNode) {
            ASTNode n = (ASTNode) obj;
            sb.append(childInd).append("\"type\": \"").append(n.getNodeName()).append("\",\n");
            sb.append(childInd).append("\"line\": ").append(n.getLineNumber());
        } else {
            sb.append(childInd).append("\"type\": \"").append(obj.getClass().getSimpleName()).append("\"");
        }

        for (Field f : collectFields(obj.getClass())) {
            String fname = f.getName();
            if (fname.equals("nodeName") || fname.equals("lineNumber")) continue;
            f.setAccessible(true);
            Object val;
            try {
                val = f.get(obj);
            } catch (Exception e) {
                continue;
            }
            sb.append(",\n").append(childInd)
                    .append("\"").append(fname).append("\": ")
                    .append(valueToJson(val, indent + 1));
        }
        sb.append("\n").append(ind).append("}");
        return sb.toString();
    }

    private static List<Field> collectFields(Class<?> clazz) {
        List<Field> result = new java.util.ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) result.add(f);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    private static String valueToJson(Object val, int indent) {
        if (val == null) return "null";
        if (val instanceof String) return "\"" + escape((String) val) + "\"";
        if (val instanceof Number || val instanceof Boolean) return val.toString();

        if (val instanceof List) {
            List<?> list = (List<?>) val;
            if (list.isEmpty()) return "[]";
            String childInd = "  ".repeat(indent + 1);
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < list.size(); i++) {
                sb.append(childInd).append(valueToJson(list.get(i), indent + 1));
                if (i < list.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ".repeat(indent)).append("]");
            return sb.toString();
        }


        return objectToJson(val, indent);
    }


    public static String jinjaTreesToJson(Map<String, TemplateNode> templateRoots) {
        StringBuilder sb = new StringBuilder("{\n");
        int i = 0;
        for (Map.Entry<String, TemplateNode> entry : templateRoots.entrySet()) {
            sb.append("  \"").append(escape(entry.getKey())).append("\": ")
                    .append(jinjaNodeToJson(entry.getValue(), 1));
            if (++i < templateRoots.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String jinjaNodeToJson(AstNode node, int indent) {
        String ind = "  ".repeat(indent);
        String childInd = "  ".repeat(indent + 1);
        StringBuilder sb = new StringBuilder("{\n");

        sb.append(childInd).append("\"type\": \"").append(node.name()).append("\",\n");
        sb.append(childInd).append("\"line\": ").append(node.getLine()).append(",\n");
        sb.append(childInd).append("\"label\": \"").append(escape(node.label())).append("\",\n");

        List<AstNode> children = node.children();
        if (children.isEmpty()) {
            sb.append(childInd).append("\"children\": []\n");
        } else {
            sb.append(childInd).append("\"children\": [\n");
            for (int i = 0; i < children.size(); i++) {
                sb.append(childInd).append("  ").append(jinjaNodeToJson(children.get(i), indent + 2));
                if (i < children.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(childInd).append("]\n");
        }
        sb.append(ind).append("}");
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}