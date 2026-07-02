import antlr.ProductLexer;
import antlr.ProductParser;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class TestParser {

    public static void main(String[] args) throws Exception {

        // 1. قراءة ملف الإدخال
        String input = Files.readString(Paths.get("app.txt"));

        // 2. إنشاء CharStream
        CharStream charStream = CharStreams.fromString(input);

        // 3. إنشاء Lexer
        ProductLexer lexer = new ProductLexer(charStream);

        // 4. إنشاء TokenStream
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // (اختياري) طباعة التوكنات
        tokens.fill();
        System.out.println("TOKENS:");
        for (Token t : tokens.getTokens()) {
            String name = ProductLexer.VOCABULARY.getSymbolicName(t.getType());
            System.out.printf(
                    "line %d:%d %-10s => %s%n",
                    t.getLine(),
                    t.getCharPositionInLine(),
                    name,
                    t.getText().replace("\n", "\\n")
            );
        }

        // 5. إنشاء Parser
        ProductParser parser = new ProductParser(tokens);

        // 6. نقطة البدء (start rule)
        ParseTree tree = parser.program();

        // 7. طباعة الشجرة بشكل هرمي في Console
        System.out.println("\nPARSE TREE (pretty):");
        printTree(tree, parser);

        // 8. عرض الشجرة باستخدام TreeViewer في نافذة Swing
        SwingUtilities.invokeLater(() -> showTreeViewer(tree, parser));
    }

    // =======================
    // Pretty Print Parse Tree
    // =======================
    static void printTree(ParseTree tree, Parser parser) {
        printTree(tree, parser, 0);
    }

    static void printTree(ParseTree tree, Parser parser, int indent) {
        String indentation = "  ".repeat(indent);

        String text;
        if (tree instanceof TerminalNode) {
            Token t = ((TerminalNode) tree).getSymbol();
            String name = parser.getVocabulary().getSymbolicName(t.getType());
            text = name + " : '" + t.getText().replace("\n", "\\n") + "'";
        } else {
            text = parser.getRuleNames()[((RuleContext) tree).getRuleIndex()];
        }

        System.out.println(indentation + text);

        for (int i = 0; i < tree.getChildCount(); i++) {
            printTree(tree.getChild(i), parser, indent + 1);
        }
    }

    // =======================
    // TreeViewer GUI
    // =======================
    static void showTreeViewer(ParseTree tree, Parser parser) {
        // إنشاء JFrame
        JFrame frame = new JFrame("ANTLR Parse Tree Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // إنشاء TreeViewer
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.setScale(1.0); // حجم الشجرة (يمكن تغييره لتكبير/تصغير)

        // إضافة TreeViewer داخل JScrollPane لتفعيل الـScroll
        JScrollPane scrollPane = new JScrollPane(viewer);
        frame.add(scrollPane);

        frame.setVisible(true);
    }
}