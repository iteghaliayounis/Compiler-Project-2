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
public class EndToEndDemo {

    public static void main(String[] args) {

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


        product_htmlLexer lexer = new product_htmlLexer(CharStreams.fromString(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        product_htmlParser parser = new product_htmlParser(tokens);

        HtmlVisitor htmlVisitor = new HtmlVisitor();
        htmlVisitor.setCurrentTemplateName("index.html");
        TemplateNode root = (TemplateNode) htmlVisitor.visit(parser.program());


        Map<String, Object> context = new LinkedHashMap<>();
        context.put("page_title", "All Products");
        context.put("products", List.of(
                Map.of("id", 1, "name", "Laptop", "price", 750),
                Map.of("id", 2, "name", "Dress", "price", 20),
                Map.of("id", 3, "name", "Wool Jacket", "price", 100)
        ));


        JinjaRenderer renderer = new JinjaRenderer();
        String html = renderer.render(root, context);

        System.out.println("===== HTML النهائي =====");
        System.out.println(html);
    }
}