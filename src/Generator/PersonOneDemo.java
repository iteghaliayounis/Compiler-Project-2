package Generator;

import AST.Program;
import Visitor.PythonVisitor;
import antlr.ProductLexer;
import antlr.ProductParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * الشخص 1 — Demo لاختبار السيناريو كامل بمعزل عن باقي الفريق:
 *   app.py (نص)  →  Lexer/Parser  →  PythonVisitor (AST جاهزة)
 *                →  PythonContextGenerator (Context Data)
 *                →  ast_python.json + generation_log.txt
 *
 * هاد الملف بس للاختبار المحلي عندك، مش جزء من نظام التشغيل النهائي
 * (الشخص 3 رح يستخدم PythonContextGenerator مباشرة بالـ pipeline الرئيسي).
 */
public class PersonOneDemo {

    public static void main(String[] args) throws IOException {

        // ── مثال app.py مطابق تمامًا لمثال الدكتورة بالتوضيح ──
        String sampleAppPy =
                "from flask import Flask, render_template, request, redirect, url_for, abort\n" +
                        "\n" +
                        "# تعريف مصفوفة المنتجات\n" +
                        "products = [\n" +
                        "    {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"Laptop\",\n" +
                        "        \"price\": 750,\n" +
                        "        \"image\": \"baby.jpg\",\n" +
                        "        \"description\": \"High performance laptop.\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"id\": 2,\n" +
                        "        \"name\": \"Dress\",\n" +
                        "        \"price\": 20,\n" +
                        "        \"image\": \"dress.jpg\",\n" +
                        "        \"description\": \"Dress Baby Red.\"\n" +
                        "    },\n" +
                        "      {\n" +
                        "            \"id\": 3,\n" +
                        "            \"name\": \"Wool Jacket\",\n" +
                        "            \"price\": 100,\n" +
                        "            \"image\": \"Coat.jpg\",\n" +
                        "            \"description\": \"navy wool jacket.\"\n" +
                        "        },\n" +
                        "         {\n" +
                        "                    \"id\": 4,\n" +
                        "                    \"name\": \"Baby Overalls\",\n" +
                        "                    \"price\": 700,\n" +
                        "                    \"image\": \"overalls.jpg\",\n" +
                        "                    \"description\": \"burnt brown baby overalls.\"\n" +
                        "                }\n" +
                        "    ]\n" +
                        "\n" +
                        "\n" +
                        "app = Flask(name)\n" +
                        "\n" +
                        "@app.route(\"/\")\n" +
                        "def products_page():\n" +
                        "    return render_template(\"index.html\", products=products)\n" +
                        "\n" +
                        "@app.route(\"/product/<int:pid>\")\n" +
                        "def product_detail(pid):\n" +
                        "    product = next((p for p in products if p.get(\"id\") == pid), None)\n" +
                        "    if product is None:\n" +
                        "        abort(404)\n" +
                        "    return render_template(\n" +
                        "        \"product_details.html\",\n" +
                        "        product=product,\n" +
                        "    )\n" +
                        "\n" +
                        "@app.route(\"/add\", methods=[\"GET\", \"POST\"])\n" +
                        "def add_product():\n" +
                        "    if request.method == \"POST\":\n" +
                        "        name = request.form.get(\"name\", \"\").strip()\n" +
                        "        price_raw = request.form.get(\"price\", \"\").strip()\n" +
                        "        image = request.form.get(\"image\", \"\").strip()\n" +
                        "        description = request.form.get(\"description\", \"\").strip()\n" +
                        "\n" +
                        "        try:\n" +
                        "            price = int(price_raw)\n" +
                        "        except Exception:\n" +
                        "            try:\n" +
                        "                price = int(float(price_raw))\n" +
                        "            except Exception:\n" +
                        "                price = 0\n" +
                        "\n" +
                        "        new = {\n" +
                        "            \"id\": (max((p.get(\"id\", 0) for p in products), default=0) + 1),\n" +
                        "            \"name\": name,\n" +
                        "            \"price\": price,\n" +
                        "            \"image\": image,\n" +
                        "            \"description\": description\n" +
                        "        }\n" +
                        "        products.append(new)\n" +
                        "        return redirect(url_for(\"products_page\"))\n" +
                        "\n" +
                        "    return render_template(\"add_product.html\")\n" +
                        "\n" +
                        "# \uD83D\uDEA8 Route حذف المنتج\n" +
                        "@app.route(\"/delete/<int:pid>\", methods=[\"POST\"])\n" +
                        "def delete_product(pid):\n" +
                        "    global products\n" +
                        "    products = [p for p in products if p.get(\"id\") != pid]\n" +
                        "    return redirect(url_for(\"products_page\"))\n" +
                        "\n" +
                        "# تشغيل السيرفر\n" +
                        "if name == \"main\":\n" +
                        "    app.run(debug=True)";

        // ── 1) Lexer + Parser (ANTLR) ──
        ProductLexer lexer = new ProductLexer(CharStreams.fromString(sampleAppPy));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ProductParser parser = new ProductParser(tokens);

        // ── 2) بناء الـ AST عبر الـ Visitor الجاهز (بدون أي تعديل عليه) ──
        PythonVisitor visitor = new PythonVisitor();
        Program root = (Program) visitor.visit(parser.program());

        System.out.println("===== AST (Pretty Print) =====");
        System.out.println(root.toString(0));

        // ── 3) تشغيل الـ Generator تبعنا (الشخص 1) ──
        PythonContextGenerator generator = new PythonContextGenerator();
        generator.generate(root);

        System.out.println("===== Global Variables =====");
        for (Map.Entry<String, Object> e : generator.getGlobalVariables().entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

        System.out.println("\n===== Context Data per Template =====");
        for (Map.Entry<String, List<Map<String, Object>>> e : generator.getTemplateContexts().entrySet()) {
            System.out.println("Template: " + e.getKey() + "  (" + e.getValue().size() + " variant(s))");
            for (int i = 0; i < e.getValue().size(); i++) {
                System.out.println("  [" + i + "] " + e.getValue().get(i));
            }
        }

        // ── 4) تصدير compiler_output/ast_python.json و generation_log.txt ──
        Path compilerOutput = Path.of("compiler_output");
        GenerationOutputWriter.writeAstJson(AstJsonSerializer.pythonTreeToJson(root), compilerOutput);
        GenerationOutputWriter.writeGenerationLog(generator.getLog(), compilerOutput, false, "Python Generator Log");
        System.out.println("\n✅ تم إنشاء compiler_output/ast_python.json و generation_log.txt");

        // ── هيك بيوصل الشخص 2 على الـ Context Data الجاهز لأي قالب ──
      //  Map<String, Object> indexContext = generator.getContextFor("index.jinja");
        // ── هيك بيوصل الشخص 2 على الـ Context Data الجاهز لأي قالب ──
        List<Map<String, Object>> indexContexts = generator.getContextFor("index.html");
        System.out.println("\n===== Context جاهز لتسليمه للشخص 2 (index.html) =====");
        for (Map<String, Object> ctx : indexContexts) {
            System.out.println(ctx);
        }
    }
}