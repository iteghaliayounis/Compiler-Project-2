package AstHtml;

/**
 * واجهة الـ Visitor لشجرة الـ AST.
 *
 * كل تحليل (semantic, codegen, linter) بيعمل implements لهاد الـ interface.
 *
 * <T> هو نوع القيمة المرجعة:
 *   - Void للـ semantic analysis
 *   - String للـ code generation
 */
public interface AstVisitor<T> {

    // ====== Top-level ======
    T visit(TemplateNode node);

    // ====== Statements ======
    T visit(TextNode node);
    T visit(ExtendsNode node);
    T visit(BlockNode node);
    T visit(IfNode node);
    T visit(ForNode node);
    T visit(SetNode node);
    T visit(IncludeNode node);
    T visit(ImportNode node);
    T visit(FromImportNode node);
    T visit(MacroNode node);
    T visit(WithNode node);
    T visit(RawNode node);
    T visit(FilterBlockNode node);
    T visit(JinjaVarOutputNode node);

    // ====== HTML ======
    T visit(ElementNode node);
    T visit(VoidElementNode node);
    T visit(StyleElementNode node);
    T visit(ScriptElementNode node);
    T visit(AttributeNode node);
    T visit(StyleAttributeNode node);

    // ====== Expressions ======
    T visit(BinaryOpNode node);
    T visit(UnaryOpNode node);
    T visit(TernaryNode node);
    T visit(NumberLiteral node);
    T visit(StringLiteral node);
    T visit(BooleanLiteral node);
    T visit(NoneLiteral node);
    T visit(VariableNode node);
    T visit(AttributeAccessNode node);
    T visit(IndexNode node);
    T visit(SliceNode node);
    T visit(CallNode node);
    T visit(FilterNode node);

    // ====== CSS ======
    T visit(CssRuleSetNode node);
    T visit(CssAtRuleNode node);
    T visit(CssSelectorNode node);
    T visit(CssDeclarationNode node);
    T visit(CssNumberValueNode node);
    T visit(CssHashValueNode node);
    T visit(CssStringValueNode node);
    T visit(CssIdentValueNode node);
    T visit(CssFunctionValueNode node);
    T visit(CssJinjaValueNode node);
}
