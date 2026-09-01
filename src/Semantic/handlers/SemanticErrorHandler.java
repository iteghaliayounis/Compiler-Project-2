package Semantic.handlers;

import Semantic.errors.SemanticError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SemanticErrorHandler {

    // ★ تعديل: كانت هون بس عشان تلوّن الطباعة بالكونسول - ما عادت مستخدمة
    // بعد ما صار الإخراج يروح لملف بدل الكونسول (ألوان ANSI بتبين كرموز
    // غريبة جوا ملف نصي عادي).
    private static final Path REPORT_FILE = Path.of("compiler_output", "semantic_report.txt");

    private final List<SemanticError> errors = new ArrayList<>();

    public void report(SemanticError error) {
        errors.add(error);
    }

    public boolean hasErrors() { return !errors.isEmpty(); }

    public List<SemanticError> getErrors() { return errors; }

    /**
     * ★ تعديل: بدل ما تطبع أخطاء السيمانتك بالكونسول، هلأ بتكتبها بملف
     * compiler_output/semantic_report.txt (نفس الملف يلي MainPipeline أصلًا
     * بيكتب فيه تقريره النهائي). باقي كل الطباعات التانية بالمشروع (شجرة AST،
     * رسائل الـ Flask Linker، عنوان "STARTING SEMANTIC ANALYSIS"...) ضلّت متل
     * ما هي بالكونسول - ما تغيّر منهم شي.
     */
    public void printAll() {
        StringBuilder sb = new StringBuilder();
        if (errors.isEmpty()) {
            sb.append("  [OK] No semantic errors found.\n");
        } else {
            sb.append("\n").append("═".repeat(85)).append("\n");
            sb.append("  SEMANTIC ERRORS\n");
            sb.append("═".repeat(85)).append("\n");
            for (SemanticError e : errors) {
                sb.append("  ✖ ").append(e).append("\n");
            }
            sb.append("═".repeat(85)).append("\n");
        }

        try {
            Files.createDirectories(REPORT_FILE.getParent());
            Files.writeString(REPORT_FILE, sb.toString(), StandardCharsets.UTF_8);
            System.out.println("  [Semantic] تقرير الأخطاء انكتب بـ " + REPORT_FILE);
        } catch (IOException e) {
            System.err.println("  [Semantic] تعذّر كتابة " + REPORT_FILE + ": " + e.getMessage());
        }
    }
}
