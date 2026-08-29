import AstHtml.AstNode;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import visitor_html.HtmlVisitor;

import java.io.FileInputStream;
import java.io.InputStream;

public class HtmlTest {
    public static void main(String[] args) throws Exception {
        String fileName = "base.html";
        InputStream is = new FileInputStream(fileName);

        CharStream input = CharStreams.fromStream(is);
        product_htmlLexer lexer = new product_htmlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        product_htmlParser parser = new product_htmlParser(tokens);



        product_htmlParser.ProgramContext tree = parser.program();


        HtmlVisitor visitor = new HtmlVisitor();
        AstNode ast = visitor.visit(tree);

        System.out.println(ast);
    }
}
