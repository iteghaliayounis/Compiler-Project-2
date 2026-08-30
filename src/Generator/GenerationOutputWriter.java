package Generator;

import AST.Program;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GenerationOutputWriter {

    public static void writeAstJson(String jsonContent, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("ast_python.json"), jsonContent, StandardCharsets.UTF_8);
    }

    public static void writeJinjaAstJson(String jsonContent, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("ast_jinja.json"), jsonContent, StandardCharsets.UTF_8);
    }

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
