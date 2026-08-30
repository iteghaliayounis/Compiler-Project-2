package Generator;

import AST.Program;
import Visitor.PythonVisitor;
import antlr.ProductLexer;
import antlr.ProductParser;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import Semantic.errors.SemanticError;
import Semantic.handlers.SemanticErrorHandler;
import Semantic.SemanticAnalyzer;
import AstHtml.TemplateNode;
import visitor_html.HtmlVisitor;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPipeline {


    private static final Path INPUT_DIR = Path.of("input");
    private static final Path TEMPLATES_DIR = INPUT_DIR.resolve("templates");
    private static final Path OUTPUT_DIR = Path.of("output");
    private static final Path COMPILER_OUTPUT_DIR = Path.of("compiler_output");

    public static void main(String[] args) {
        runPipelineOnce();
        watchForChanges();
    }

    private static void runPipelineOnce() {
        try {
            System.out.println(" Starting Compiler Pipeline...");


            prepareOutputDirectories();


            String appPyContent = Files.readString(INPUT_DIR.resolve("app.py"), java.nio.charset.StandardCharsets.UTF_8);


            ProductLexer lexer = new ProductLexer(CharStreams.fromString(appPyContent));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ProductParser parser = new ProductParser(tokens);

            PythonVisitor visitor = new PythonVisitor();
            Program pythonRoot = (Program) visitor.visit(parser.program());
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(
                    visitor.getSymbolTable(),
                    symbol_table.SymbolTable.getInstance()
            );

            PythonContextGenerator pyGenerator = new PythonContextGenerator();
            pyGenerator.generate(pythonRoot);


            GenerationOutputWriter.writeAstJson(AstJsonSerializer.pythonTreeToJson(pythonRoot), COMPILER_OUTPUT_DIR);
            GenerationOutputWriter.writeGenerationLog(pyGenerator.getLog(), COMPILER_OUTPUT_DIR, false, "Python Generator Log");

            JinjaRenderer jinjaRenderer = new JinjaRenderer();
            jinjaRenderer.setRoutes(pyGenerator.getRoutes());


            Map<String, String> templateToEndpoint = new java.util.LinkedHashMap<>();
            for (Map.Entry<String, String> e : pyGenerator.getEndpointToTemplate().entrySet()) {
                templateToEndpoint.put(e.getValue(), e.getKey());
            }
            Map<String, String> endpointFileMap = new java.util.LinkedHashMap<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : pyGenerator.getTemplateContexts().entrySet()) {
                String tName = entry.getKey();
                List<Map<String, Object>> tVariants = entry.getValue();
                String endpointName = templateToEndpoint.get(tName);
                if (endpointName == null) continue;

                for (int i = 0; i < tVariants.size(); i++) {
                    String outFile = (tVariants.size() == 1)
                            ? tName
                            : tName.replace(".html", "_" + extractVariantSuffix(tVariants.get(i), i) + ".html");

                    if (tVariants.size() == 1) {
                        endpointFileMap.put(endpointName, outFile);
                    } else {
                        endpointFileMap.put(endpointName + ":" + extractVariantSuffix(tVariants.get(i), i), outFile);
                    }
                }
            }
            jinjaRenderer.setEndpointFileMap(endpointFileMap);

            Map<String, AstHtml.TemplateNode> jinjaTemplateRoots = new java.util.LinkedHashMap<>();


            for (Map.Entry<String, List<Map<String, Object>>> entry : pyGenerator.getTemplateContexts().entrySet()) {
                String templateName = entry.getKey();
                List<Map<String, Object>> variants = entry.getValue();

                Path templateFile = TEMPLATES_DIR.resolve(templateName);


                if (!Files.exists(templateFile)) {
                    System.out.println(" Warning: Template " + templateName + " not found in input/templates/");
                    continue;
                }

                System.out.println(" Rendering template: " + templateName + " (" + variants.size() + " variant(s))");
                String jinjaContent = Files.readString(templateFile, java.nio.charset.StandardCharsets.UTF_8);

                product_htmlLexer htmlLexer = new product_htmlLexer(CharStreams.fromString(jinjaContent));
                CommonTokenStream htmlTokens = new CommonTokenStream(htmlLexer);
                product_htmlParser htmlParser = new product_htmlParser(htmlTokens);

                HtmlVisitor htmlVisitor = new HtmlVisitor();
                htmlVisitor.setCurrentTemplateName(templateName);
                TemplateNode jinjaRoot = (TemplateNode) htmlVisitor.visit(htmlParser.program());
                TemplateProcessor templateProcessor = new TemplateProcessor(TEMPLATES_DIR);
                jinjaRoot = templateProcessor.resolve(jinjaRoot, templateName);


                if (!variants.isEmpty()) {
                    for (String varName : variants.get(0).keySet()) {
                        semanticAnalyzer.addFlaskPassedVariable(varName);
                    }
                }
                semanticAnalyzer.runAnalysis(pythonRoot, jinjaRoot);

                jinjaTemplateRoots.put(templateName, jinjaRoot);

                for (int i = 0; i < variants.size(); i++) {
                    Map<String, Object> contextData = variants.get(i);


                    String finalHtml = jinjaRenderer.render(jinjaRoot, contextData);

                    String outputFileName = (variants.size() == 1)
                            ? templateName
                            : templateName.replace(".html", "_" + extractVariantSuffix(contextData, i) + ".html");


                    Files.writeString(OUTPUT_DIR.resolve(outputFileName), finalHtml, java.nio.charset.StandardCharsets.UTF_8);
                    System.out.println(" Generated: output/" + outputFileName);
                }
            }


            GenerationOutputWriter.writeJinjaAstJson(AstJsonSerializer.jinjaTreesToJson(jinjaTemplateRoots), COMPILER_OUTPUT_DIR);

            GenerationOutputWriter.writeGenerationLog(jinjaRenderer.getLog(), COMPILER_OUTPUT_DIR, true, "Jinja Renderer Log");

            copySupportFile("app.py");
            copySupportFile("style.css");
            copySupportFile("script.js");
            copyStaticFolder();

            SemanticErrorHandler handler = semanticAnalyzer.getHandler();
            List<SemanticError> errors = handler.getErrors();

            StringBuilder report = new StringBuilder();
            report.append("===== Semantic Analysis Report =====\n\n");
            if (errors.isEmpty()) {
                report.append("لا يوجد أخطاء دلالية.\n");
            } else {
                report.append("عدد الأخطاء المكتشفة: ").append(errors.size()).append("\n\n");
                for (SemanticError err : errors) {
                    report.append(err.toString()).append("\n");
                }
            }

            Files.writeString(COMPILER_OUTPUT_DIR.resolve("semantic_report.txt"),
                    report.toString(), java.nio.charset.StandardCharsets.UTF_8);

            System.out.println("\n Pipeline finished successfully! Check 'output/' and 'compiler_output/' directories.");

        } catch (Exception e) {
            System.err.println(" Pipeline Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void watchForChanges() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            INPUT_DIR.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            TEMPLATES_DIR.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            System.out.println("\n Watching 'input/' for changes... (Ctrl+C to stop)");

            while (true) {
                WatchKey key = watchService.take();
                List<WatchEvent<?>> events = new ArrayList<>(key.pollEvents());
                key.reset();

                Thread.sleep(300);
                WatchKey extraKey;
                while ((extraKey = watchService.poll()) != null) {
                    events.addAll(extraKey.pollEvents());
                    extraKey.reset();
                }

                if (!events.isEmpty()) {
                    System.out.println("\n  تعديل انكتشف بملفات input/ — إعادة توليد...");
                    runPipelineOnce();
                    System.out.println(" Watching 'input/' for changes... (Ctrl+C to stop)");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println(" Watch Error: " + e.getMessage());
        }
    }


    private static void prepareOutputDirectories() throws IOException {

        deleteDirectory(OUTPUT_DIR);
        deleteDirectory(COMPILER_OUTPUT_DIR);


        Files.createDirectories(OUTPUT_DIR);
        Files.createDirectories(COMPILER_OUTPUT_DIR);
        System.out.println(" Output directories prepared.");
    }

    private static void copySupportFile(String fileName) throws IOException {
        Path source = INPUT_DIR.resolve(fileName);
        if (Files.exists(source)) {
            Files.copy(source, OUTPUT_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(" Copied: " + fileName + " → output/");
        }
    }

    private static void copyStaticFolder() throws IOException {
        Path staticSource = INPUT_DIR.resolve("static");
        if (!Files.exists(staticSource)) return;

        Path staticDest = OUTPUT_DIR.resolve("static");
        Files.walk(staticSource).forEach(source -> {
            try {
                Path dest = staticDest.resolve(staticSource.relativize(source));
                if (Files.isDirectory(source)) {
                    Files.createDirectories(dest);
                } else {
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                System.out.println(" Warning: couldn't copy " + source + ": " + e.getMessage());
            }
        });
        System.out.println("📎 Copied: static/ → output/static/");
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try { Files.delete(path); } catch (IOException ignored) {}
                    });
        }
    }


    private static String extractVariantSuffix(Map<String, Object> ctx, int fallbackIndex) {
        for (Object v : ctx.values()) {
            if (v instanceof Map) {
                Object id = ((Map<?, ?>) v).get("id");
                if (id != null) return String.valueOf(id);
            }
        }
        return String.valueOf(fallbackIndex);
    }
}