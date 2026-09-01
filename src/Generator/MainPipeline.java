package Generator;

import AST.ASTNode;
import AST.Program;
import AST.Statements.SimpleStmt;
import AST.Statements.ExprStmt.ExprStmt;
import AST.Target.TargetID;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.DictAtom;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.DictLiteral;
import AST.ListDictPair.Pair;
import AST.Literal.IntegerLiteral;
import AST.Literal.FloatLiteral;
import AST.Literal.StringLiteral;
import AST.Literal.BoolLiteral;
import AST.Expressions.CallSuffixes.CallChainExpr;
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

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainPipeline {


    private static final Path INPUT_DIR = Path.of("input");
    private static final Path TEMPLATES_DIR = INPUT_DIR.resolve("templates");
    private static final Path OUTPUT_DIR = Path.of("output");
    private static final Path COMPILER_OUTPUT_DIR = Path.of("compiler_output");

    private static final int SERVER_PORT = 8080;

    private static final AtomicBoolean skipNextWatch = new AtomicBoolean(false);

    public static void main(String[] args) throws IOException {
        runPipelineOnce();
        startHttpServer();
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

                    if (skipNextWatch.getAndSet(false)) {
                        continue;
                    }
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



    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        server.createContext("/", (HttpExchange exchange) -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            try {
                if (method.equals("POST") && path.equals("/add_product.html")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseFormData(body);
                    handleAddProduct(params);
                    sendRedirect(exchange, "/");
                    return;
                }

                if (method.equals("POST") && path.startsWith("/delete/")) {
                    String idStr = path.substring("/delete/".length());
                    handleDeleteProduct(idStr);
                    sendRedirect(exchange, "/");
                    return;
                }

                if (method.equals("GET") && (path.equals("/") || path.isEmpty())) {
                    serveStaticFile(exchange, "index.html");
                    return;
                }

                serveStaticFile(exchange, path);

            } catch (Exception e) {
                e.printStackTrace();
                String msg = "500 Internal Server Error: " + e.getMessage();
                exchange.sendResponseHeaders(500, msg.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg.getBytes(StandardCharsets.UTF_8));
                }
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("\n Server -> http://localhost:" + SERVER_PORT);

    }

    private static void serveStaticFile(HttpExchange exchange, String requestPath) throws IOException {
        String filePath = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
        if (filePath.isEmpty()) filePath = "index.html";

        Path file = OUTPUT_DIR.resolve(filePath);
        if (Files.exists(file) && Files.isRegularFile(file)) {
            byte[] bytes = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", getContentType(filePath));
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            String msg = "404 Not Found: " + filePath;
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    private static void sendRedirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }

    private static Map<String, String> parseFormData(String body) {
        Map<String, String> params = new LinkedHashMap<>();
        if (body == null || body.isEmpty()) return params;
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                } catch (Exception e) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif"))  return "image/gif";
        if (path.endsWith(".svg"))  return "image/svg+xml";
        return "application/octet-stream";
    }


    private static class ProductsListLocation {
        final int startLineIndex;
        final int endLineIndex;
        final List<Map<String, Object>> products;

        ProductsListLocation(int startLineIndex, int endLineIndex, List<Map<String, Object>> products) {
            this.startLineIndex = startLineIndex;
            this.endLineIndex = endLineIndex;
            this.products = products;
        }
    }

    private static ExprStmt unwrapExprStmt(ASTNode node) {
        if (node instanceof SimpleStmt && ((SimpleStmt) node).smallStmt instanceof ExprStmt) {
            return (ExprStmt) ((SimpleStmt) node).smallStmt;
        }
        return null;
    }


    private static ASTNode unwrapCallChain(ASTNode node) {
        if (node instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) node;
            if (cc.suffixes == null || cc.suffixes.isEmpty()) return cc.base;
        }
        return node;
    }

    private static String stripPyQuotes(String raw) {
        if (raw != null && raw.length() >= 2
                && (raw.charAt(0) == '"' || raw.charAt(0) == '\'')) {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }

    private static Object literalValue(ASTNode node) {
        node = unwrapCallChain(node);
        if (node instanceof IntegerLiteral) return ((IntegerLiteral) node).value;
        if (node instanceof FloatLiteral)   return ((FloatLiteral) node).value;
        if (node instanceof StringLiteral)  return ((StringLiteral) node).value;
        if (node instanceof BoolLiteral)    return ((BoolLiteral) node).value;
        return null;
    }


    private static ProductsListLocation locateProductsList(String content) throws Exception {
        ProductLexer lexer = new ProductLexer(CharStreams.fromString(content));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ProductParser parser = new ProductParser(tokens);
        PythonVisitor visitor = new PythonVisitor();
        Program root = (Program) visitor.visit(parser.program());

        for (ASTNode el : root.elements) {
            ExprStmt es = unwrapExprStmt(el);
            if (es == null || !(es.target instanceof TargetID)) continue;
            if (!"products".equals(((TargetID) es.target).name)) continue;

            ASTNode value = unwrapCallChain(es.value);
            if (value instanceof ListAtom) value = ((ListAtom) value).listLiteral;
            if (!(value instanceof ListLiteral)) continue;

            List<Map<String, Object>> current = new ArrayList<>();
            for (ASTNode elem : ((ListLiteral) value).elements) {
                ASTNode dictNode = unwrapCallChain(elem);
                if (dictNode instanceof DictAtom) dictNode = ((DictAtom) dictNode).dictLiteral;
                if (!(dictNode instanceof DictLiteral)) continue;

                Map<String, Object> m = new LinkedHashMap<>();
                for (Pair p : ((DictLiteral) dictNode).pairs) {
                    m.put(stripPyQuotes(p.key), literalValue(p.value));
                }
                current.add(m);
            }

            int startLineIndex = es.getLineNumber() - 1; // الـ AST بيعتبر lineNumber يبلش من 1
            int endLineIndex = findMatchingBracketLine(content, startLineIndex);
            return new ProductsListLocation(startLineIndex, endLineIndex, current);
        }

        throw new IOException("لا يوجد قائمة 'products' بملف app.py");
    }

    private static int findMatchingBracketLine(String content, int startLineIndex) {
        String[] lines = content.split("\n", -1);
        int depth = 0;
        boolean seenOpen = false;
        boolean inString = false;
        char stringQuote = 0;

        for (int li = startLineIndex; li < lines.length; li++) {
            String line = lines[li];
            for (int ci = 0; ci < line.length(); ci++) {
                char c = line.charAt(ci);
                if (inString) {
                    if (c == '\\') { ci++; continue; } // تخطي الحرف بعد backslash
                    if (c == stringQuote) inString = false;
                    continue;
                }
                if (c == '"' || c == '\'') { inString = true; stringQuote = c; continue; }
                if (c == '[') { depth++; seenOpen = true; }
                else if (c == ']') depth--;
            }
            if (seenOpen && depth <= 0) return li;
        }
        return lines.length - 1; // fallback (ما لازم يصير)
    }

    private static String escapePythonString(Object value) {
        String s = value == null ? "" : String.valueOf(value);
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String formatNumber(Object value) {
        if (value instanceof Double || value instanceof Float) {
            double d = ((Number) value).doubleValue();
            return (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
        }
        return String.valueOf(value);
    }

    private static String serializeProductsBlock(List<Map<String, Object>> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("products = [\n");
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> p = products.get(i);
            sb.append("    {");
            sb.append("\"id\": ").append(formatNumber(p.get("id"))).append(", ");
            sb.append("\"name\": ").append(escapePythonString(p.get("name"))).append(", ");
            sb.append("\"price\": ").append(formatNumber(p.get("price"))).append(", ");
            sb.append("\"image\": ").append(escapePythonString(p.get("image"))).append(", ");
            sb.append("\"description\": ").append(escapePythonString(p.get("description")));
            sb.append("}");
            if (i < products.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void replaceProductsBlock(Path pythonFile, ProductsListLocation loc,
                                             List<Map<String, Object>> newProducts) throws IOException {
        List<String> lines = Files.readAllLines(pythonFile, StandardCharsets.UTF_8);
        List<String> result = new ArrayList<>();
        result.addAll(lines.subList(0, loc.startLineIndex));
        result.addAll(Arrays.asList(serializeProductsBlock(newProducts).split("\n", -1)));
        result.addAll(lines.subList(loc.endLineIndex + 1, lines.size()));
        Files.write(pythonFile, result, StandardCharsets.UTF_8);
    }

    private static void handleAddProduct(Map<String, String> params) throws Exception {
        Path pythonFile = INPUT_DIR.resolve("app.py");
        String content = Files.readString(pythonFile, StandardCharsets.UTF_8);
        ProductsListLocation loc = locateProductsList(content);

        int newId = 0;
        for (Map<String, Object> p : loc.products) {
            Object idVal = p.get("id");
            if (idVal instanceof Number) newId = Math.max(newId, ((Number) idVal).intValue());
        }
        newId += 1;

        Map<String, Object> newProduct = new LinkedHashMap<>();
        newProduct.put("id", newId);
        newProduct.put("name", params.getOrDefault("name", ""));
        String priceStr = params.getOrDefault("price", "0");
        Object priceVal;
        try {
            priceVal = priceStr.contains(".") ? (Object) Double.parseDouble(priceStr) : (Object) Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            priceVal = 0;
        }
        newProduct.put("price", priceVal);
        newProduct.put("image", params.getOrDefault("image", ""));
        newProduct.put("description", params.getOrDefault("description", ""));

        loc.products.add(newProduct);

        skipNextWatch.set(true);
        replaceProductsBlock(pythonFile, loc, loc.products);
        System.out.println("  [API] Product added: " + newProduct.get("name") + " (id=" + newId + ")");
        runPipelineOnce();
    }

    private static void handleDeleteProduct(String idStr) throws Exception {
        int targetId;
        try {
            targetId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("  [API] Invalid delete id: " + idStr);
            return;
        }

        Path pythonFile = INPUT_DIR.resolve("app.py");
        String content = Files.readString(pythonFile, StandardCharsets.UTF_8);
        ProductsListLocation loc = locateProductsList(content);

        boolean removed = loc.products.removeIf(p -> {
            Object idVal = p.get("id");
            return idVal instanceof Number && ((Number) idVal).intValue() == targetId;
        });

        if (!removed) {
            System.out.println("  [API] Product id=" + targetId + " not found — nothing deleted.");
            return;
        }

        skipNextWatch.set(true);
        replaceProductsBlock(pythonFile, loc, loc.products);
        System.out.println("  [API] Product deleted: id=" + targetId);
        runPipelineOnce();
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