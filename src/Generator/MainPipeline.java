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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * الشخص 3 — Pipeline Integration & Output Structure
 *
 * السكربت الرئيسي للمشروع: يربط بين Person 1 و Person 2،
 * ويولد ملفات الخرج النهائية بالمجلدات المطلوبة (output/ و compiler_output/).
 */
public class MainPipeline {

    // مسارات المجلدات الأساسية
    private static final Path INPUT_DIR = Path.of("input");
    private static final Path TEMPLATES_DIR = INPUT_DIR.resolve("templates");
    private static final Path OUTPUT_DIR = Path.of("output");
    private static final Path COMPILER_OUTPUT_DIR = Path.of("compiler_output");

    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting Compiler Pipeline...");

            // 1. تنظيف وإنشاء مجلدات الخرج
            prepareOutputDirectories();

            // 2. قراءة ملف app.py
            String appPyContent = Files.readString(INPUT_DIR.resolve("app.py"), java.nio.charset.StandardCharsets.UTF_8);

            // ───────────────────────────────────────────────────────────
            // مرحلة الشخص 1: Python AST + Context Data
            // ───────────────────────────────────────────────────────────
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

            // تصدير ast_python.json و generation_log.txt
            GenerationOutputWriter.writeAstJson(AstJsonSerializer.pythonTreeToJson(pythonRoot), COMPILER_OUTPUT_DIR);
            GenerationOutputWriter.writeGenerationLog(pyGenerator.getLog(), COMPILER_OUTPUT_DIR, false, "Python Generator Log");
            // ───────────────────────────────────────────────────────────
            // مرحلة الشخص 2: Jinja Rendering
            // ───────────────────────────────────────────────────────────
            JinjaRenderer jinjaRenderer = new JinjaRenderer();
            jinjaRenderer.setRoutes(pyGenerator.getRoutes());

            // ★ جديد: قبل أي رندرة، نحسب مسبقًا اسم ملف الـ output الحقيقي لكل (endpoint, id)
            // بنفس منطق تسمية الملفات اللي تحت (extractVariantSuffix)، عشان url_for يقدر
            // يترجم لاسم الملف الصحيح حتى لو الصفحة يلي فيها الرابط بترندر قبل الصفحة الهدف.
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

            // المرور على كل قالب مسجل بـ render_template بملف app.py
            for (Map.Entry<String, List<Map<String, Object>>> entry : pyGenerator.getTemplateContexts().entrySet()) {
                String templateName = entry.getKey();
                List<Map<String, Object>> variants = entry.getValue();

                Path templateFile = TEMPLATES_DIR.resolve(templateName);

                // إذا القالب موجود فعلياً بـ input/templates
                if (!Files.exists(templateFile)) {
                    System.out.println("⚠️ Warning: Template " + templateName + " not found in input/templates/");
                    continue;
                }

                System.out.println("📄 Rendering template: " + templateName + " (" + variants.size() + " variant(s))");
                String jinjaContent = Files.readString(templateFile, java.nio.charset.StandardCharsets.UTF_8);

                // بناء شجرة Jinja AST (مرة وحدة بس لكل قالب، مش لكل variant)
                product_htmlLexer htmlLexer = new product_htmlLexer(CharStreams.fromString(jinjaContent));
                CommonTokenStream htmlTokens = new CommonTokenStream(htmlLexer);
                product_htmlParser htmlParser = new product_htmlParser(htmlTokens);

                HtmlVisitor htmlVisitor = new HtmlVisitor();
                htmlVisitor.setCurrentTemplateName(templateName);
                TemplateNode jinjaRoot = (TemplateNode) htmlVisitor.visit(htmlParser.program());
                TemplateProcessor templateProcessor = new TemplateProcessor(TEMPLATES_DIR);
                jinjaRoot = templateProcessor.resolve(jinjaRoot, templateName);

                // ★ نبلّغ الفاحص الدلالي بأسماء المتغيرات الحقيقية الممررة لهاد القالب
                if (!variants.isEmpty()) {
                    for (String varName : variants.get(0).keySet()) {
                        semanticAnalyzer.addFlaskPassedVariable(varName);
                    }
                }
                semanticAnalyzer.runAnalysis(pythonRoot, jinjaRoot);

                // تجميع الأشجار لملف ast_jinja.json (مرة وحدة بس لكل قالب)
                jinjaTemplateRoots.put(templateName, jinjaRoot);

                // ★ نمشي على كل variant (كل عنصر بالقائمة) ونولد ملف HTML مستقل له
                for (int i = 0; i < variants.size(); i++) {
                    Map<String, Object> contextData = variants.get(i);

                    // توليد الـ HTML النهائي لهاد الـ variant
                    String finalHtml = jinjaRenderer.render(jinjaRoot, contextData);

                    // ★ لو variant وحيد بس (متل index.html) → نفس اسم القالب الأصلي
                    // ★ لو أكثر من variant (متل product_details.html) → نضيف رقم للتمييز
                    String outputFileName = (variants.size() == 1)
                            ? templateName
                            : templateName.replace(".html", "_" + extractVariantSuffix(contextData, i) + ".html");


                    Files.writeString(OUTPUT_DIR.resolve(outputFileName), finalHtml, java.nio.charset.StandardCharsets.UTF_8);
                    System.out.println("✅ Generated: output/" + outputFileName);
                }
            }

            // تصدير ast_jinja.json
            GenerationOutputWriter.writeJinjaAstJson(AstJsonSerializer.jinjaTreesToJson(jinjaTemplateRoots), COMPILER_OUTPUT_DIR);
            // ★ جديد: إلحاق لوغ الـ Jinja Renderer (الـ Warnings) بنفس generation_log.txt
            GenerationOutputWriter.writeGenerationLog(jinjaRenderer.getLog(), COMPILER_OUTPUT_DIR, true, "Jinja Renderer Log");

            // ───────────────────────────────────────────────────────────
            // مرحلة الشخص 3: نسخ الملفات المرافقة (app.py, style.css, script.js)
            // ───────────────────────────────────────────────────────────
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

            System.out.println("\n🎉 Pipeline finished successfully! Check 'output/' and 'compiler_output/' directories.");

        } catch (Exception e) {
            System.err.println("❌ Pipeline Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ====================================================================
    //  Helper Methods
    // ====================================================================

    private static void prepareOutputDirectories() throws IOException {
        // حذف المجلدات القديمة إن وجدت عشان نضمن Output نظيف
        deleteDirectory(OUTPUT_DIR);
        deleteDirectory(COMPILER_OUTPUT_DIR);

        // إنشاء المجلدات من جديد
        Files.createDirectories(OUTPUT_DIR);
        Files.createDirectories(COMPILER_OUTPUT_DIR);
        System.out.println("📁 Output directories prepared.");
    }

    private static void copySupportFile(String fileName) throws IOException {
        Path source = INPUT_DIR.resolve(fileName);
        if (Files.exists(source)) {
            Files.copy(source, OUTPUT_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📎 Copied: " + fileName + " → output/");
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
                System.out.println("⚠️ Warning: couldn't copy " + source + ": " + e.getMessage());
            }
        });
        System.out.println("📎 Copied: static/ → output/static/");
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> -a.compareTo(b)) // حذف من الداخل للخارج
                    .forEach(path -> {
                        try { Files.delete(path); } catch (IOException ignored) {}
                    });
        }
    }


// ============================================================================
// ضيفي هاد التابع بملف MainPipeline.java، جنب باقي الـ Helper Methods
// (متل copySupportFile و deleteDirectory) — بس قبل القوس } الأخير للكلاس.
// ============================================================================

    /**
     * بيدور جوا الـ Context Data عن أي عنصر فيه "id" (زي {"product": {id:1, ...}})
     * ويرجّع قيمته كنص، عشان نسمي الملف حسب الـ id الحقيقي تبع المنتج
     * مش حسب ترتيبه بالقائمة (fallbackIndex بيستخدم بس لو ما لقينا id لأي سبب).
     */
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