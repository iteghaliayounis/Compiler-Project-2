package Generator;

import AST.Program;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * الشخص 1 — مسؤول عن تصدير ملفي compiler_output الخاصين بـ Python:
 *   - ast_python.json     (تمثيل الشجرة)
 *   - generation_log.txt  (سجل خطوات الاستخراج، جزء Python منه)
 *
 * ملاحظة: هون بنعتمد على toString(0) الموجودة أصلاً بكل عقدة AST (شجرة
 * مقروءة بالـ └── ) وبنغلفها كـ JSON بسيط، بدل ما نبني JSON tree كامل
 * بالتفصيل (يحتاج تعديل كل كلاسات AST لإضافة children()) وهاد مش من مهمتنا
 * كشخص 1 — إذا حبيتوا لاحقًا نطورها لشكل JSON متداخل بالكامل ممكن، بس
 * هاي كافية وصحيحة لمتطلب "توليد ast_python.json من الشجرة".
 */
public class GenerationOutputWriter {

    /** يكتب ast_python.json داخل مجلد compiler_output. */
    public static void writeAstJson(String jsonContent, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("ast_python.json"), jsonContent, StandardCharsets.UTF_8);
    }

    /** يكتب ast_jinja.json داخل مجلد compiler_output (شجرة أو أكثر من قالب مدموجة). */
    public static void writeJinjaAstJson(String jsonContent, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("ast_jinja.json"), jsonContent, StandardCharsets.UTF_8);
    }

    /**
     * يكتب (أو يلحق بـ) generation_log.txt بجزء Python.
     * append=true عشان الشخص 3 (أو الشخص 2) يقدر يضيف جزء Jinja بنفس الملف بعدين.
     */
    public static void writeGenerationLog(List<String> logLines, Path outputDir, boolean append, String sectionTitle) throws IOException {
        Files.createDirectories(outputDir);
        Path logFile = outputDir.resolve("generation_log.txt");

        StringBuilder sb = new StringBuilder();
        sb.append("===== ").append(sectionTitle).append(" =====\n");
        for (String line : logLines) {
            sb.append(line).append("\n");
        }
        sb.append("\n");

        if (append && Files.exists(logFile)) {
            Files.writeString(logFile, sb.toString(), StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.APPEND);
        } else {
            Files.writeString(logFile, sb.toString(), StandardCharsets.UTF_8);
        }
    }
}
