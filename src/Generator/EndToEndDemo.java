package Generator;

import AstHtml.TemplateNode;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import visitor_html.HtmlVisitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * دليل نهائي (End-to-End Demo): يوصل مخرجات الشخص 1 (Context Data)
 * مع الشخص 2 (Jinja AST + Renderer) وينتج HTML نهائي حقيقي.
 *
 *   Context Data (متل يلي طالعة من PythonContextGenerator)
 *        +
 *   قالب index.html (Jinja) → HtmlVisitor → TemplateNode (AST)
 *        ↓
 *   JinjaRenderer.render(...)
 *        ↓
 *   HTML نهائي
 */
public class EndToEndDemo {

    public static void main(String[] args) {

        // ── 1) قالب index.html — نفس الأسلوب يلي رح يكون بمشروعكن الحقيقي ──
        String template =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head><title>{{ page_title }}</title></head>\n" +
                        "<body>\n" +
                        "  <h1>{{ page_title }}</h1>\n" +
                        "  <ul>\n" +
                        "  {% for product in products %}\n" +
                        "    <li>\n" +
                        "      <span>{{ product.name }}</span> — {{ product.price }}$\n" +
                        "      {% if product.price > 500 %}\n" +
                        "        <strong>(غالي)</strong>\n" +
                        "      {% endif %}\n" +
                        "    </li>\n" +
                        "  {% endfor %}\n" +
                        "  </ul>\n" +
                        "</body>\n" +
                        "</html>";

        // ── 2) بناء الـ Jinja AST من القالب (نفس HtmlVisitor الجاهز، بدون تعديل) ──
        product_htmlLexer lexer = new product_htmlLexer(CharStreams.fromString(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        product_htmlParser parser = new product_htmlParser(tokens);

        HtmlVisitor htmlVisitor = new HtmlVisitor();
        htmlVisitor.setCurrentTemplateName("index.html");
        TemplateNode root = (TemplateNode) htmlVisitor.visit(parser.program());

        // ── 3) Context Data — بنفس الشكل يلي بيطلع فعليًا من PythonContextGenerator ──
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("page_title", "All Products");
        context.put("products", List.of(
                Map.of("id", 1, "name", "Laptop", "price", 750),
                Map.of("id", 2, "name", "Dress", "price", 20),
                Map.of("id", 3, "name", "Wool Jacket", "price", 100)
        ));

        // ── 4) الرندر النهائي ──
        JinjaRenderer renderer = new JinjaRenderer();
        String html = renderer.render(root, context);

        System.out.println("===== HTML النهائي =====");
        System.out.println(html);
    }
}