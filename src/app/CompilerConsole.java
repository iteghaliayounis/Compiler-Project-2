package app;

import antlr.ProductParser;
import antlr.ProductLexer;
import AST.Program;
import Semantic.SemanticAnalyzer;
import Visitor.PythonVisitor;
import AstHtml.AstNode;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import SymbolTable.SymbolTable;
import visitor_html.HtmlVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class CompilerConsole {

    private static final Path TESTS_DIR = Path.of("Tests");

    private static Program pythonAST;
    private static SymbolTable pythonST;
    private static AstNode jinjaAST;
    private static symbol_table.SymbolTable jinjaST;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.println("        COMPILER 2 — Interactive Menu");
            System.out.println("=".repeat(60));
            System.out.println("  1. Semantic Analysis  (choose a test)");
            System.out.println("  2. Code Generation   (Full pipeline + HTTP Server)");
            System.out.println("  3. Run All Semantic Tests");
            System.out.println("  0. Exit");
            System.out.println("=".repeat(60));
            System.out.print("  Choose [0-3]: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> runSemanticMenu(scanner);
                case "2" -> Generator.MainPipeline.main(new String[0]);
                case "3" -> runAllSemanticTests();
                case "0" -> {
                    System.out.println("\n  Goodbye!");
                    return;
                }
                default -> System.out.println("  Invalid choice. Please enter 0-3.");
            }
        }
    }


    private static List<Path> discoverTestFolders() throws Exception {
        List<Path> result = new ArrayList<>();
        if (!Files.isDirectory(TESTS_DIR)) return result;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(TESTS_DIR)) {
            for (Path p : stream) {
                if (!Files.isDirectory(p)) continue;
                // ★ جديد: بنيتين مقبولتين - python_test.py+jinja_test.html (العادية)
                // أو app.py + مجلد templates (زي missing_flask_variable، بيلوّع على كذا قالب)
                if (Files.exists(p.resolve("python_test.py")) || Files.exists(p.resolve("app.py"))) {
                    result.add(p);
                }
            }
        }
        result.sort(Comparator.comparing(p -> p.getFileName().toString()));
        return result;
    }

    /** بيدور عن مجلد templates جوا مجلد التيست (بغض النظر عن مسافات زايدة بالاسم). */
    private static Path findTemplatesDir(Path folder) throws Exception {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path p : stream) {
                if (Files.isDirectory(p) && p.getFileName().toString().trim().equalsIgnoreCase("templates")) {
                    return p;
                }
            }
        }
        return null;
    }


    private static void runSemanticMenu(Scanner scanner) throws Exception {
        List<Path> folders = discoverTestFolders();
        if (folders.isEmpty()) {
            System.out.println("  No test folders found under Tests/.");
            return;
        }

        System.out.println("\n  Available Semantic Tests:");
        System.out.println("  " + "-".repeat(56));
        for (int i = 0; i < folders.size(); i++) {
            System.out.printf("  %2d. %s%n", (i + 1), folders.get(i).getFileName());
        }
        System.out.println("  " + "-".repeat(56));
        System.out.print("  Enter test number (or 0 to go back): ");

        String input = scanner.nextLine().trim();
        if (input.equals("0")) return;

        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= folders.size()) {
                System.out.println("  Invalid test number.");
                return;
            }
            runOneSemanticTest(folders.get(idx));
        } catch (NumberFormatException e) {
            System.out.println("  Please enter a valid number.");
        }
    }

    private static void runAllSemanticTests() throws Exception {
        List<Path> folders = discoverTestFolders();

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("        RUNNING ALL SEMANTIC TESTS");
        System.out.println("=".repeat(80));


        StringBuilder combinedReport = new StringBuilder();
        int totalErrors = 0;

        for (int i = 0; i < folders.size(); i++) {
            String folderName = folders.get(i).getFileName().toString();
            System.out.println("\n  >>> TEST " + (i + 1) + "/" + folders.size()
                    + " (" + folderName + ") <<<");

            List<Semantic.errors.SemanticError> errors = runOneSemanticTest(folders.get(i));

            combinedReport.append("── ").append(folderName).append(" ──\n");
            if (errors.isEmpty()) {
                combinedReport.append("  [OK] No semantic errors found.\n");
            } else {
                for (Semantic.errors.SemanticError e : errors) {
                    combinedReport.append("  ✖ ").append(e).append("\n");
                }
            }
            combinedReport.append("\n");
            totalErrors += errors.size();
        }

        Path reportFile = Path.of("compiler_output", "semantic_report.txt");
        Files.createDirectories(reportFile.getParent());
        Files.writeString(reportFile, combinedReport.toString(), java.nio.charset.StandardCharsets.UTF_8);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("  All tests completed (" + totalErrors + " total errors).");
        System.out.println("  Combined report written to: " + reportFile);
        System.out.println("=".repeat(80));
    }


    private static List<Semantic.errors.SemanticError> runOneSemanticTest(Path folder) throws Exception {
        pythonAST = null;
        pythonST = null;
        jinjaAST = null;
        jinjaST = null;

        Path pythonFile = folder.resolve("python_test.py");
        Path jinjaFile = folder.resolve("jinja_test.html");

        // ★ جديد: البنية المختلفة (زي missing_flask_variable) - app.py + مجلد
        // templates فيه كذا قالب. هاي بتحتاج تلويع على كل قالب مرتبط بـ
        // render_template، مش ملف HTML وحد بس - آلية مختلفة كليًا.
        if (!Files.exists(pythonFile) && Files.exists(folder.resolve("app.py"))) {
            Path appPy = folder.resolve("app.py");
            Path templatesDir = findTemplatesDir(folder);
            if (templatesDir == null) {
                System.out.println("[SKIP] ما لقيت مجلد templates جوا " + folder);
                return List.of();
            }
            return runMultiTemplateTest(appPy, templatesDir);
        }

        if (Files.exists(pythonFile)) {
            runProductFrom(pythonFile.toString());
        } else {
            System.out.println("[SKIP] " + pythonFile + " غير موجود");
        }

        System.out.println("\n==============================\n");

        if (Files.exists(jinjaFile)) {
            runHtmlFrom(jinjaFile.toString());
        } else {
            System.out.println("[SKIP] " + jinjaFile + " غير موجود");
        }

        if (pythonST != null && jinjaST != null) {
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(pythonST, jinjaST);

            for (SymbolTable.ScopeEntry scope : pythonST.getAllScopes()) {
                for (SymbolTable.Symbol sym : scope.symbols.values()) {
                    if (sym.getKind() == SymbolTable.Symbol.Kind.TEMPLATE) {
                        for (String var : sym.getTemplateVariables()) {
                            semanticAnalyzer.addFlaskPassedVariable(var);
                        }
                    }
                }
            }

            semanticAnalyzer.runAnalysis(pythonAST, jinjaAST);
            return semanticAnalyzer.getHandler().getErrors();
        } else if (pythonST != null) {
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(pythonST, null);
            semanticAnalyzer.runAnalysis(pythonAST, null);
            return semanticAnalyzer.getHandler().getErrors();
        }
        return List.of();
    }

    /**
     * ★ جديد: لتيست زي missing_flask_variable - ملف app.py وحد بيرندر كذا
     * قالب. نفس آلية Generator.MainPipeline بالضبط: نستخرج كل render_template
     * calls عن طريق PythonContextGenerator (نفس الأداة الحقيقية المستخدمة
     * بالتوليد)، وبعدين نلوّع على كل قالب موجود فعليًا ونحلله - عشان الـ
     * Flask Linker يقدر يقارن عبر كل القوالب مع بعض، مش قالب واحد بمعزل.
     */
    private static List<Semantic.errors.SemanticError> runMultiTemplateTest(Path pythonFile, Path templatesDir) throws Exception {
        System.out.println("Running Product Compiler...\n");
        String appPyContent = Files.readString(pythonFile, java.nio.charset.StandardCharsets.UTF_8);

        ProductLexer lexer = new ProductLexer(CharStreams.fromString(appPyContent));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ProductParser parser = new ProductParser(tokens);

        PythonVisitor visitor = new PythonVisitor();
        Program pyRoot = (Program) visitor.visit(parser.program());
        System.out.println(pyRoot);

        SymbolTable pyST = visitor.getSymbolTable();
        pyST.printSymbolTable();

        Generator.PythonContextGenerator pyGenerator = new Generator.PythonContextGenerator();
        pyGenerator.generate(pyRoot);

        // ★ reset مرة وحدة بس قبل ما نبلش - عشان ما يتسرّب أي رمز من تيست سابق
        symbol_table.SymbolTable.reset();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(pyST, symbol_table.SymbolTable.getInstance());

        System.out.println("\n==============================\n");
        System.out.println("Running HTML Compiler...\n");

        for (Map.Entry<String, List<Map<String, Object>>> entry : pyGenerator.getTemplateContexts().entrySet()) {
            String templateName = entry.getKey();
            List<Map<String, Object>> variants = entry.getValue();

            Path templateFile = templatesDir.resolve(templateName);
            if (!Files.exists(templateFile)) {
                System.out.println("[SKIP] القالب " + templateName + " مش موجود جوا " + templatesDir);
                continue;
            }

            String templateContent = Files.readString(templateFile, java.nio.charset.StandardCharsets.UTF_8);
            product_htmlLexer htmlLexer = new product_htmlLexer(CharStreams.fromString(templateContent));
            CommonTokenStream htmlTokens = new CommonTokenStream(htmlLexer);
            product_htmlParser htmlParser = new product_htmlParser(htmlTokens);

            HtmlVisitor htmlVisitor = new HtmlVisitor();
            htmlVisitor.setCurrentTemplateName(templateName.replace(".html", ""));
            AstNode jinjaRoot = htmlVisitor.visit(htmlParser.program());
            System.out.println(jinjaRoot);

            if (!variants.isEmpty()) {
                for (String varName : variants.get(0).keySet()) {
                    semanticAnalyzer.addFlaskPassedVariable(varName);
                }
            }
            semanticAnalyzer.runAnalysis(pyRoot, jinjaRoot);
        }

        return semanticAnalyzer.getHandler().getErrors();
    }

    private static void runProductFrom(String path) {
        try {
            System.out.println("Running Product Compiler...\n");
            CharStream input = CharStreams.fromFileName(path);
            ProductLexer lexer = new ProductLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ProductParser parser = new ProductParser(tokens);
            ParseTree tree = parser.program();

            PythonVisitor visitor = new PythonVisitor();
            pythonAST = (Program) visitor.visit(tree);
            System.out.println(pythonAST);

            pythonST = visitor.getSymbolTable();
            pythonST.printSymbolTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runHtmlFrom(String fileName) {
        try {
            System.out.println("Running HTML Compiler...\n");
            InputStream is = new FileInputStream(fileName);
            CharStream input = CharStreams.fromStream(is);
            product_htmlLexer lexer = new product_htmlLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            product_htmlParser parser = new product_htmlParser(tokens);
            product_htmlParser.ProgramContext tree = parser.program();

            symbol_table.SymbolTable.reset();
            HtmlVisitor visitor = new HtmlVisitor();

            String templateName = fileName;
            if (templateName.contains("/")) {
                templateName = templateName.substring(templateName.lastIndexOf('/') + 1);
            }
            if (templateName.contains(".")) {
                templateName = templateName.substring(0, templateName.lastIndexOf('.'));
            }
            visitor.setCurrentTemplateName(templateName);

            jinjaAST = visitor.visit(tree);

            jinjaST = symbol_table.SymbolTable.getInstance();
            jinjaST.printTable();
            System.out.println(jinjaAST);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
