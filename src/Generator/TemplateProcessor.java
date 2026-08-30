package Generator;

import AstHtml.*;
import antlr.product_htmlLexer;
import antlr.product_htmlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import visitor_html.HtmlVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class TemplateProcessor {

    private final Path templatesDir;
    private final List<String> log = new ArrayList<>();

    public TemplateProcessor(Path templatesDir) {
        this.templatesDir = templatesDir;
    }

    public List<String> getLog() { return log; }


    public TemplateNode resolve(TemplateNode childTree, String childTemplateName) throws IOException {
        ExtendsNode extendsNode = findExtends(childTree);
        if (extendsNode == null) {
            return childTree;
        }

        String parentName = extendsNode.getParentTemplate();
        Path parentFile = templatesDir.resolve(parentName);

        if (!Files.exists(parentFile)) {
            log.add("[WARNING] Base template not found: '" + parentName
                    + "' (مطلوب من '" + childTemplateName + "') — لا دمج، رح نرندر الطفل لحاله.");
            return childTree;
        }


        String parentContent = Files.readString(parentFile);
        product_htmlLexer lexer = new product_htmlLexer(CharStreams.fromString(parentContent));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        product_htmlParser parser = new product_htmlParser(tokens);
        HtmlVisitor visitor = new HtmlVisitor();
        visitor.setCurrentTemplateName(parentName);
        TemplateNode parentTree = (TemplateNode) visitor.visit(parser.program());

        Map<String, BlockNode> childBlocks = collectBlocks(childTree);
        Set<String> replaced = new HashSet<>();
        replaceBlocks(parentTree, childBlocks, replaced);

        for (String name : childBlocks.keySet()) {
            if (!replaced.contains(name)) {
                log.add("[WARNING] Block '" + name + "' موجود بـ '" + childTemplateName
                        + "' بس مش موجود بالقالب الأب '" + parentName + "'.");
            }
        }
        return parentTree;
    }

    private ExtendsNode findExtends(AstNode node) {
        if (node instanceof ExtendsNode) return (ExtendsNode) node;
        for (AstNode child : node.children()) {
            ExtendsNode found = findExtends(child);
            if (found != null) return found;
        }
        return null;
    }

    private Map<String, BlockNode> collectBlocks(AstNode node) {
        Map<String, BlockNode> result = new LinkedHashMap<>();
        collectBlocksRec(node, result);
        return result;
    }

    private void collectBlocksRec(AstNode node, Map<String, BlockNode> result) {
        if (node instanceof BlockNode) {
            result.put(((BlockNode) node).getName(), (BlockNode) node);
        }
        for (AstNode child : node.children()) {
            collectBlocksRec(child, result);
        }
    }

    private void replaceBlocks(AstNode node, Map<String, BlockNode> childBlocks, Set<String> replaced) {
        if (node instanceof BlockNode) {
            BlockNode parentBlock = (BlockNode) node;
            BlockNode childBlock = childBlocks.get(parentBlock.getName());
            if (childBlock != null) {
                parentBlock.getBody().clear();
                parentBlock.getBody().addAll(childBlock.getBody());
                replaced.add(parentBlock.getName());
            }
        }
        for (AstNode child : node.children()) {
            replaceBlocks(child, childBlocks, replaced);
        }
    }
}