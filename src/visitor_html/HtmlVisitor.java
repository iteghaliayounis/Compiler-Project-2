package visitor_html;


import AstHtml.AstNode;
import AstHtml.TemplateNode;
import AstHtml.*;
import antlr.product_htmlParser;
import antlr.product_htmlParserBaseVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

import symbol_table.SymbolTable;
import symbol_table.SymbolTable.Kind;
import symbol_table.SymbolTable.Symbol;

/**
 * 🔥 محدّث: مدمج مع Symbol Table بالكامل (كل عقد Jinja تدخل الرموز المناسبة)
 */
public class HtmlVisitor extends product_htmlParserBaseVisitor<AstNode> {

    // ====== Symbol Table Integration ======

    private final SymbolTable st = SymbolTable.getInstance();

    private String currentTemplateName = "default_template";

    public void setCurrentTemplateName(String name) {
        this.currentTemplateName = name;
    }

    private void recordTemplateVariableUsage(String varName, int line) {
        if (st.isFlaskGlobal(varName)) return;

        Symbol existing = st.lookup(varName);
        if (existing != null) {
            Kind k = existing.getKind();
            if (k == Kind.LOOP_VAR || k == Kind.SET_VAR
                    || k == Kind.MACRO_PARAM || k == Kind.MACRO) {
                return;
            }
        }

        Symbol tmplSym = st.lookup(currentTemplateName);
        if (tmplSym != null) {
            tmplSym.addUsedVariable(varName, line);
        }
    }


    // ====== Utility: إرجاع line و column من token ======
    private static int[] pos(Token t) {
        return new int[]{t.getLine(), t.getCharPositionInLine()};
    }

    private static int[] pos(ParserRuleContext ctx) {
        return new int[]{ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine()};
    }

    /** يزور قائمة children ويرجع قائمة AstNode (متجاهلاً nulls). */
    private List<AstNode> visitAll(List<? extends ParserRuleContext> contexts) {
        List<AstNode> result = new ArrayList<>();
        for (ParserRuleContext ctx : contexts) {
            AstNode n = visit(ctx);
            if (n != null) result.add(n);
        }
        return result;
    }

    // ================================================================
    // ====== Top-level ======
    // ================================================================
    @Override
    public AstNode visitProgram(product_htmlParser.ProgramContext ctx) {
        int[] p = pos(ctx);
        TemplateNode template = new TemplateNode(p[0], p[1]);

        //  NEW: إذا الاسم من الخارج مو الافتراضي (يعني ملف حقيقي)، نحافظ عليه
        boolean useRealName = !currentTemplateName.equals("default_template")
                && !currentTemplateName.equals("htmlTest");

        int templateIndex = 0;

        for (ParseTree child : ctx.children) {
            if (child instanceof TerminalNode) continue;
            if (!(child instanceof ParserRuleContext)) continue;
            ParserRuleContext prc = (ParserRuleContext) child;
            int[] cp = pos(prc);

            if (child instanceof product_htmlParser.PrologContext) {
                templateIndex++;
                if (!useRealName) currentTemplateName = "base_" + templateIndex;
                st.insert(currentTemplateName, Kind.TEMPLATE, "TEMPLATE", null, cp[0]);

            } else if (isExtendsBlock(child)) {
                templateIndex++;
                if (!useRealName) currentTemplateName = "child_" + templateIndex;
                st.insert(currentTemplateName, Kind.TEMPLATE, "TEMPLATE", null, cp[0]);

            } else if (templateIndex == 0) {
                templateIndex++;
                if (!useRealName) currentTemplateName = "template_" + templateIndex;
                st.insert(currentTemplateName, Kind.TEMPLATE, "TEMPLATE", null, cp[0]);
            }

            AstNode node = visit(child);
            if (node != null) {
                template.addChild(node);
            }
        }
        return template;
    }

    /** فحص هل العنصر هو {% extends "..." %} */
    private boolean isExtendsBlock(ParseTree child) {
        if (child instanceof product_htmlParser.PrologContext) return false;
        String text = child.getText().toLowerCase();
        return text.contains("extends");
    }
    @Override
    public AstNode visitProlog(product_htmlParser.PrologContext ctx) {
        int[] p = pos(ctx);
        return new TextNode(ctx.DOCTYPE().getText(), p[0], p[1]);
    }

    // ================================================================
    // ====== Content (المحتوى بين التاغات) ======
    // ================================================================

    @Override
    public AstNode visitTextContent(product_htmlParser.TextContentContext ctx) {
        int[] p = pos(ctx);
        StringBuilder sb = new StringBuilder();
        for (TerminalNode tn : ctx.text().TEXT()) {
            sb.append(tn.getText());
        }
        return new TextNode(sb.toString(), p[0], p[1]);
    }

    @Override
    public AstNode visitElementContent(product_htmlParser.ElementContentContext ctx) {
        return visit(ctx.element());
    }

    @Override
    public AstNode visitJinjaVarContent(product_htmlParser.JinjaVarContentContext ctx) {
        return visit(ctx.jinja_var());
    }

    @Override
    public AstNode visitJinjaBlockContent(product_htmlParser.JinjaBlockContentContext ctx) {
        return visit(ctx.jinja_block());
    }

    // ================================================================
    // ====== HTML Elements ======
    // ================================================================

    @Override
    public AstNode visitStyleElemAlt(product_htmlParser.StyleElemAltContext ctx) {
        return visit(ctx.styleElement());
    }

    @Override
    public AstNode visitScriptElemAlt(product_htmlParser.ScriptElemAltContext ctx) {
        return visit(ctx.scriptElement());
    }

    @Override
    public AstNode visitVoidElemAlt(product_htmlParser.VoidElemAltContext ctx) {
        return visit(ctx.voidElement());
    }

    @Override
    public AstNode visitContainerElemAlt(product_htmlParser.ContainerElemAltContext ctx) {
        return visit(ctx.containerElement());
    }

    @Override
    public AstNode visitStyleElement(product_htmlParser.StyleElementContext ctx) {
        int[] p = pos(ctx);
        StyleElementNode node = new StyleElementNode(p[0], p[1]);
        for (product_htmlParser.CssStatementContext stmt : ctx.cssStatement()) {
            AstNode stmtNode = visit(stmt);
            if (stmtNode instanceof CssNode) {
                node.addStatement((CssNode) stmtNode);
            }
        }
        return node;
    }

    @Override
    public AstNode visitScriptElement(product_htmlParser.ScriptElementContext ctx) {
        int[] p = pos(ctx);
        String content = ctx.SCRIPT_TEXT() != null ? ctx.SCRIPT_TEXT().getText() : null;
        return new ScriptElementNode(content, p[0], p[1]);
    }

    @Override
    public AstNode visitVoidTag(product_htmlParser.VoidTagContext ctx) {
        int[] p = pos(ctx);
        String tagName = ctx.TAG_VOID_NAME().getText();
        VoidElementNode node = new VoidElementNode(tagName, true, false, p[0], p[1]);

        for (product_htmlParser.AttributeContext attr : ctx.attribute()) {
            AstNode attrNode = visit(attr);
            if (attrNode instanceof AttributeNode) {
                node.addAttribute((AttributeNode) attrNode);
            } else if (attrNode instanceof StyleAttributeNode) {
                node.addStyleAttribute((StyleAttributeNode) attrNode);
            }
        }
        return node;
    }

    @Override
    public AstNode visitSelfClosingTag(product_htmlParser.SelfClosingTagContext ctx) {
        int[] p = pos(ctx);
        String tagName = ctx.TAG_NAME().getText();
        VoidElementNode node = new VoidElementNode(tagName, false, true, p[0], p[1]);

        for (product_htmlParser.AttributeContext attr : ctx.attribute()) {
            AstNode attrNode = visit(attr);
            if (attrNode instanceof AttributeNode) {
                node.addAttribute((AttributeNode) attrNode);
            } else if (attrNode instanceof StyleAttributeNode) {
                node.addStyleAttribute((StyleAttributeNode) attrNode);
            }
        }
        return node;
    }

    @Override
    public AstNode visitContainerElement(product_htmlParser.ContainerElementContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.OpenTagContext openCtx = ctx.openTag();
        String tagName = openCtx.TAG_NAME().getText();
        ElementNode node = new ElementNode(tagName, p[0], p[1]);

        for (product_htmlParser.AttributeContext attr : openCtx.attribute()) {
            AstNode attrNode = visit(attr);
            if (attrNode instanceof AttributeNode) {
                node.addAttribute((AttributeNode) attrNode);
            } else if (attrNode instanceof StyleAttributeNode) {
                node.addStyleAttribute((StyleAttributeNode) attrNode);
            }
        }

        for (product_htmlParser.ContentContext content : ctx.content()) {
            AstNode child = visit(content);
            if (child != null) {
                node.addChild(child);
            }
        }

        return node;
    }

    @Override
    public AstNode visitOpenTag(product_htmlParser.OpenTagContext ctx) {
        return null;  // معالج ضمن visitContainerElement
    }

    @Override
    public AstNode visitCloseTag(product_htmlParser.CloseTagContext ctx) {
        return null;  // معالج ضمن visitContainerElement
    }

    // ================================================================
    // ====== Attributes ======
    // ================================================================

    @Override
    public AstNode visitStyleAttr(product_htmlParser.StyleAttrContext ctx) {
        int[] p = pos(ctx);
        StyleAttributeNode node = new StyleAttributeNode(p[0], p[1]);
        product_htmlParser.StyleAttributeContext saCtx = ctx.styleAttribute();
        for (product_htmlParser.CssDeclarationContext decl : saCtx.cssDeclaration()) {
            AstNode declNode = visit(decl);
            if (declNode instanceof CssDeclarationNode) {
                node.addDeclaration((CssDeclarationNode) declNode);
            }
        }
        return node;
    }

    @Override
    public AstNode visitNormalAttr(product_htmlParser.NormalAttrContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.NormalAttributeContext naCtx = ctx.normalAttribute();
        String name = naCtx.TAG_NAME().getText();

        if (naCtx.attributeValue() == null) {
            // boolean attribute
            return new AttributeNode(name, p[0], p[1]);
        }
        // visit attributeValue
        AstNode valNode = visit(naCtx.attributeValue());
        if (valNode instanceof AttributeNode) {
            return valNode;
        }
        return new AttributeNode(name, p[0], p[1]);
    }

    @Override
    public AstNode visitAttrStringValue(product_htmlParser.AttrStringValueContext ctx) {
        int[] p = pos(ctx);
        String raw = ctx.TAG_STRING().getText();
        String value = raw.substring(1, raw.length() - 1);
        String name = getAttrNameFromParent(ctx);
        if (name == null) name = "?";
        return new AttributeNode(name, value, p[0], p[1]);
    }

    @Override
    public AstNode visitAttrJinjaVarValue(product_htmlParser.AttrJinjaVarValueContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.Jinja_varContext jvCtx = ctx.jinja_var();
        ExpressionNode expr = null;
        if (jvCtx.jinjaExpression() != null) {
            expr = (ExpressionNode) visit(jvCtx.jinjaExpression());
        }
        String name = getAttrNameFromParent(ctx);
        if (name == null) name = "?";
        return new AttributeNode(name, expr, p[0], p[1]);
    }

    @Override
    public AstNode visitAttrJinjaBlockValue(product_htmlParser.AttrJinjaBlockValueContext ctx) {
        int[] p = pos(ctx);
        String name = getAttrNameFromParent(ctx);
        if (name == null) name = "?";
        return new AttributeNode(name, p[0], p[1]);
    }

    /** يجيب اسم الـ attribute من الـ parent NormalAttributeContext. */
    private String getAttrNameFromParent(ParserRuleContext ctx) {
        ParserRuleContext p = ctx.getParent();
        while (p != null) {
            if (p instanceof product_htmlParser.NormalAttributeContext) {
                product_htmlParser.NormalAttributeContext na =
                        (product_htmlParser.NormalAttributeContext) p;
                if (na.TAG_NAME() != null) return na.TAG_NAME().getText();
            }
            p = p.getParent();
        }
        return null;
    }

    // ================================================================
    // ====== Jinja Variables ({{ ... }}) ======
    // ================================================================

    @Override
    public AstNode visitJinja_var(product_htmlParser.Jinja_varContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode expr = null;
        if (ctx.jinjaExpression() != null) {
            expr = (ExpressionNode) visit(ctx.jinjaExpression());
        }
        return new JinjaVarOutputNode(expr, p[0], p[1]);
    }

    // ================================================================
    // ====== Jinja Block Statements ({% ... %}) ======
    // ================================================================

    @Override
    public AstNode visitJinjaExtendsStmt(product_htmlParser.JinjaExtendsStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaExtendsContext eCtx = ctx.jinjaExtends();
        String raw = eCtx.JINJA_STRING().getText();
        String template = raw.substring(1, raw.length() - 1);

        // سجّل الـ extends على القالب الحالي
        Symbol tmplSym = st.lookup(currentTemplateName);
        if (tmplSym != null) {
            tmplSym.setExtendsTemplate(template);
        }

        // اسم فريد لكل extends (بسطره)
        st.insert("extends_" + template + "_L" + p[0], Kind.EXTENDS, "String", template, p[0]);

        return new ExtendsNode(template, p[0], p[1]);
    }
    @Override
    public AstNode visitJinjaBlockStmt(product_htmlParser.JinjaBlockStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaBlockContext blockCtx = ctx.jinjaBlock();
        String name = blockCtx.JINJA_ID(0).getText();
        BlockNode node = new BlockNode(name, p[0], p[1]);

        // اسم الـ scope يبيّن لف أي قالب هاد البلوك
        st.allocate("block_" + name + " (" + currentTemplateName + ")");
        st.insert(name, Kind.BLOCK, "Block", null, p[0]);

        for (product_htmlParser.ContentContext c : blockCtx.content()) {
            AstNode child = visit(c);
            if (child != null) node.addBodyItem(child);
        }

        st.free();
        return node;
    }

    @Override
    public AstNode visitJinjaIfStmt(product_htmlParser.JinjaIfStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaIfContext ifCtx = ctx.jinjaIf();
        IfNode node = new IfNode(p[0], p[1]);

        List<product_htmlParser.JinjaExpressionContext> conds = ifCtx.jinjaExpression();
        List<product_htmlParser.ContentContext> contents = ifCtx.content();
        boolean hasElse = ifCtx.JINJA_ELSE() != null;

        int numConds = conds.size();
        for (int i = 0; i < numConds; i++) {
            // ====== Symbol Table: فتح scope لكل فرع if/elif ======
            st.allocate("if_branch_" + p[0] + "_" + i);

            ExpressionNode cond = (ExpressionNode) visit(conds.get(i));
            List<AstNode> body = new ArrayList<>();
            if (i < contents.size()) {
                AstNode n = visit(contents.get(i));
                if (n != null) body.add(n);
            }
            node.addBranch(cond, body);

            st.free();
        }

        if (hasElse && contents.size() > numConds) {
            // ====== Symbol Table: فتح scope للـ else ======
            st.allocate("else_branch_" + p[0]);

            List<AstNode> elseBody = new ArrayList<>();
            AstNode n = visit(contents.get(numConds));
            if (n != null) elseBody.add(n);
            node.setElseBody(elseBody);

            st.free();
        }

        return node;
    }

    @Override
    public AstNode visitJinjaForStmt(product_htmlParser.JinjaForStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaForContext forCtx = ctx.jinjaFor();
        ExpressionNode iterable = (ExpressionNode) visit(forCtx.jinjaExpression());
        ForNode node = new ForNode(iterable, p[0], p[1]);

        // ====== Symbol Table: فتح scope للحلقة ======
        st.allocate("For_Loop_Line_" + p[0]);

        // ====== إدخال متغيرات الحلقة كـ LOOP_VAR ======
        for (TerminalNode id : forCtx.forTarget().JINJA_ID()) {
            String varName = id.getText();
            node.addTarget(varName);
            st.insert(varName, Kind.LOOP_VAR, "Unknown", null, p[0]);
        }

        // زيارة محتويات الحلقة (داخل الـ scope الجديد)
        List<product_htmlParser.ContentContext> allContent = forCtx.content();
        boolean hasElse = forCtx.JINJA_ELSE() != null;

        if (hasElse && !allContent.isEmpty()) {
            for (int i = 0; i < allContent.size() - 1; i++) {
                AstNode n = visit(allContent.get(i));
                if (n != null) node.addBodyItem(n);
            }
            // ====== فتح scope لـ for-else ======
            st.allocate("for_else_" + p[0]);
            List<AstNode> elseBody = new ArrayList<>();
            AstNode n = visit(allContent.get(allContent.size() - 1));
            if (n != null) elseBody.add(n);
            node.setElseBody(elseBody);
            st.free();
        } else {
            for (product_htmlParser.ContentContext c : allContent) {
                AstNode n = visit(c);
                if (n != null) node.addBodyItem(n);
            }
        }

        // ====== إغلاق scope الحلقة ======
        st.free();

        return node;
    }

    @Override
    public AstNode visitForTarget(product_htmlParser.ForTargetContext ctx) {
        return null;
    }

    @Override
    public AstNode visitJinjaSetStmt(product_htmlParser.JinjaSetStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaSetContext setCtx = ctx.jinjaSet();
        String var = setCtx.JINJA_ID().getText();
        ExpressionNode value = (ExpressionNode) visit(setCtx.jinjaExpression());

        // ====== Symbol Table: إدخال متغير الـ set ======
        st.insert(var, Kind.SET_VAR, "Unknown", null, p[0]);

        return new SetNode(var, value, p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaIncludeStmt(product_htmlParser.JinjaIncludeStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaIncludeContext incCtx = ctx.jinjaInclude();
        String raw = incCtx.JINJA_STRING().getText();
        String tmpl = raw.substring(1, raw.length() - 1);
        String alias = null;
        if (incCtx.JINJA_ID() != null) {
            alias = incCtx.JINJA_ID().getText();
        }

        // ====== Symbol Table: تسجيل القالب المُضمَّن ======
        st.insert("include_" + tmpl + "_" + p[0], Kind.INCLUDE, "String", tmpl, p[0]);
        Symbol tmplSym = st.lookup(currentTemplateName);
        if (tmplSym != null) {
            tmplSym.addIncludedTemplate(tmpl);
        }

        return new IncludeNode(tmpl, alias, p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaImportStmt(product_htmlParser.JinjaImportStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaImportContext impCtx = ctx.jinjaImport();
        String raw = impCtx.JINJA_STRING().getText();
        String tmpl = raw.substring(1, raw.length() - 1);
        String alias = impCtx.JINJA_ID().getText();

        // ====== Symbol Table: تسجيل الاستيراد + الاسم المستعار ======
        st.insert(alias, Kind.IMPORT, "Module", tmpl, p[0]);

        return new ImportNode(tmpl, alias, p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaFromImportStmt(product_htmlParser.JinjaFromImportStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaFromImportContext fCtx = ctx.jinjaFromImport();
        String raw = fCtx.JINJA_STRING().getText();
        String tmpl = raw.substring(1, raw.length() - 1);
        FromImportNode node = new FromImportNode(tmpl, p[0], p[1]);

        for (product_htmlParser.JinjaImportNameContext nameCtx :
                fCtx.jinjaImportNames().jinjaImportName()) {
            String name = nameCtx.JINJA_ID(0).getText();
            String alias = null;
            if (nameCtx.JINJA_ID().size() > 1) {
                alias = nameCtx.JINJA_ID(1).getText();
            }
            node.addImport(name, alias);

            // ====== Symbol Table: إدخال الاسم المستورد ======
            st.insert(alias != null ? alias : name, Kind.IMPORT, "Symbol", tmpl + "::" + name, p[0]);
        }
        return node;
    }

    @Override
    public AstNode visitJinjaRawStmt(product_htmlParser.JinjaRawStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaRawContext rawCtx = ctx.jinjaRaw();
        RawNode node = new RawNode(p[0], p[1]);
        for (product_htmlParser.ContentContext c : rawCtx.content()) {
            AstNode n = visit(c);
            if (n != null) node.addBodyItem(n);
        }
        return node;
    }

    @Override
    public AstNode visitJinjaMacroStmt(product_htmlParser.JinjaMacroStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaMacroContext mCtx = ctx.jinjaMacro();
        String name = mCtx.JINJA_ID().getText();
        MacroNode node = new MacroNode(name, p[0], p[1]);

        // ====== Symbol Table: فتح scope للماكرو + إدخال الماكرو ======
        st.allocate("macro_" + name);
        st.insert(name, Kind.MACRO, "Macro", null, p[0]);

        // إدخال وسائط الماكرو كـ MACRO_PARAM
        List<String> macroParams = new ArrayList<>();
        if (mCtx.jinjaMacroParams() != null) {
            for (product_htmlParser.JinjaMacroParamContext paramCtx :
                    mCtx.jinjaMacroParams().jinjaMacroParam()) {
                String pname = paramCtx.JINJA_ID().getText();
                macroParams.add(pname);
                st.insert(pname, Kind.MACRO_PARAM, "Unknown", null, p[0]);

                ExpressionNode def = null;
                if (paramCtx.jinjaExpression() != null) {
                    def = (ExpressionNode) visit(paramCtx.jinjaExpression());
                }
                node.addParam(pname, def);
            }
        }

        // ربط وسائط الماكرو برمزه
        Symbol macroSym = st.lookup(name);
        if (macroSym != null) {
            macroSym.setMacroParameters(macroParams);
        }

        for (product_htmlParser.ContentContext c : mCtx.content()) {
            AstNode n = visit(c);
            if (n != null) node.addBodyItem(n);
        }

        // ====== إغلاق scope الماكرو ======
        st.free();

        return node;
    }

    @Override
    public AstNode visitJinjaWithStmt(product_htmlParser.JinjaWithStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaWithContext wCtx = ctx.jinjaWith();
        WithNode node = new WithNode(p[0], p[1]);

        // ====== Symbol Table: فتح scope للـ with ======
        st.allocate("with_" + p[0]);

        for (product_htmlParser.JinjaSetExprContext setCtx : wCtx.jinjaSetExpr()) {
            String name = setCtx.JINJA_ID().getText();
            ExpressionNode value = (ExpressionNode) visit(setCtx.jinjaExpression());
            node.addAssignment(name, value);

            // ====== إدخال متغيرات الـ with كـ SET_VAR ======
            st.insert(name, Kind.SET_VAR, "Unknown", null, p[0]);
        }

        for (product_htmlParser.ContentContext c : wCtx.content()) {
            AstNode n = visit(c);
            if (n != null) node.addBodyItem(n);
        }

        // ====== إغلاق scope الـ with ======
        st.free();

        return node;
    }

    @Override
    public AstNode visitJinjaFilterBlockStmt(product_htmlParser.JinjaFilterBlockStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.JinjaFilterBlockContext fCtx = ctx.jinjaFilterBlock();
        String filterName = fCtx.JINJA_ID().getText();
        FilterBlockNode node = new FilterBlockNode(filterName, p[0], p[1]);

        // ====== Symbol Table: تسجيل الفلتر المستخدم ======
        if (st.lookup("filter_" + filterName) == null) {
            st.insert("filter_" + filterName, Kind.FILTER, "Filter", filterName, p[0]);
        }

        if (fCtx.jinjaExpressionList() != null) {
            for (product_htmlParser.JinjaExpressionContext exprCtx :
                    fCtx.jinjaExpressionList().jinjaExpression()) {
                node.addFilterArg((ExpressionNode) visit(exprCtx));
            }
        }

        // ====== فتح scope لجسم الـ filter block ======
        st.allocate("filter_block_" + filterName + "_" + p[0]);

        for (product_htmlParser.ContentContext c : fCtx.content()) {
            AstNode n = visit(c);
            if (n != null) node.addBodyItem(n);
        }

        st.free();
        return node;
    }

    @Override
    public AstNode visitJinjaGenericStmt(product_htmlParser.JinjaGenericStmtContext ctx) {
        int[] p = pos(ctx);
        return new TextNode(ctx.getText(), p[0], p[1]);
    }

    // ================================================================
    // ====== Jinja Expressions ======
    // ================================================================

    @Override
    public AstNode visitJinjaExpression(product_htmlParser.JinjaExpressionContext ctx) {
        return visit(ctx.jinjaTernary());
    }

    @Override
    public AstNode visitJinjaTernary(product_htmlParser.JinjaTernaryContext ctx) {
        if (ctx.jinjaTernary() != null) {
            int[] p = pos(ctx);
            ExpressionNode value = (ExpressionNode) visit(ctx.jinjaOr(0));
            ExpressionNode condition = (ExpressionNode) visit(ctx.jinjaOr(1));
            ExpressionNode alternative = (ExpressionNode) visit(ctx.jinjaTernary());
            return new TernaryNode(value, condition, alternative, p[0], p[1]);
        }
        return visit(ctx.jinjaOr(0));
    }

    @Override
    public AstNode visitJinjaOr(product_htmlParser.JinjaOrContext ctx) {
        if (ctx.jinjaAnd().size() == 1) {
            return visit(ctx.jinjaAnd(0));
        }
        int[] p = pos(ctx);
        ExpressionNode left = (ExpressionNode) visit(ctx.jinjaAnd(0));
        for (int i = 1; i < ctx.jinjaAnd().size(); i++) {
            ExpressionNode right = (ExpressionNode) visit(ctx.jinjaAnd(i));
            left = new BinaryOpNode(BinaryOpNode.Operator.OR, left, right, p[0], p[1]);
        }
        return left;
    }

    @Override
    public AstNode visitJinjaAnd(product_htmlParser.JinjaAndContext ctx) {
        if (ctx.jinjaNot().size() == 1) {
            return visit(ctx.jinjaNot(0));
        }
        int[] p = pos(ctx);
        ExpressionNode left = (ExpressionNode) visit(ctx.jinjaNot(0));
        for (int i = 1; i < ctx.jinjaNot().size(); i++) {
            ExpressionNode right = (ExpressionNode) visit(ctx.jinjaNot(i));
            left = new BinaryOpNode(BinaryOpNode.Operator.AND, left, right, p[0], p[1]);
        }
        return left;
    }

    @Override
    public AstNode visitJinjaUnaryNot(product_htmlParser.JinjaUnaryNotContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode operand = (ExpressionNode) visit(ctx.jinjaNot());
        return new UnaryOpNode(UnaryOpNode.Operator.NOT, operand, p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaCmpExpr(product_htmlParser.JinjaCmpExprContext ctx) {
        return visit(ctx.jinjaComparison());
    }

    @Override
    public AstNode visitJinjaComparison(product_htmlParser.JinjaComparisonContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode left = (ExpressionNode) visit(ctx.jinjaConcat(0));

        for (int i = 0; i < ctx.comparisonOp().size(); i++) {
            product_htmlParser.ComparisonOpContext opCtx = ctx.comparisonOp(i);
            ExpressionNode right = (ExpressionNode) visit(ctx.jinjaConcat(i + 1));
            BinaryOpNode.Operator op = mapComparisonOp(opCtx);
            left = new BinaryOpNode(op, left, right, p[0], p[1]);
        }
        return left;
    }

    private BinaryOpNode.Operator mapComparisonOp(product_htmlParser.ComparisonOpContext opCtx) {
        if (opCtx instanceof product_htmlParser.OpEqContext) return BinaryOpNode.Operator.EQ;
        if (opCtx instanceof product_htmlParser.OpNeqContext) return BinaryOpNode.Operator.NEQ;
        if (opCtx instanceof product_htmlParser.OpLtContext) return BinaryOpNode.Operator.LT;
        if (opCtx instanceof product_htmlParser.OpGtContext) return BinaryOpNode.Operator.GT;
        if (opCtx instanceof product_htmlParser.OpLteContext) return BinaryOpNode.Operator.LTE;
        if (opCtx instanceof product_htmlParser.OpGteContext) return BinaryOpNode.Operator.GTE;
        if (opCtx instanceof product_htmlParser.OpInContext) return BinaryOpNode.Operator.IN;
        if (opCtx instanceof product_htmlParser.OpNotInContext) return BinaryOpNode.Operator.NOT_IN;
        if (opCtx instanceof product_htmlParser.OpIsContext) return BinaryOpNode.Operator.IS;
        if (opCtx instanceof product_htmlParser.OpIsNotContext) return BinaryOpNode.Operator.IS_NOT;
        throw new IllegalArgumentException("Unknown comparison op: " + opCtx.getClass());
    }

    @Override
    public AstNode visitOpEq(product_htmlParser.OpEqContext ctx) { return null; }
    @Override
    public AstNode visitOpNeq(product_htmlParser.OpNeqContext ctx) { return null; }
    @Override
    public AstNode visitOpLt(product_htmlParser.OpLtContext ctx) { return null; }
    @Override
    public AstNode visitOpGt(product_htmlParser.OpGtContext ctx) { return null; }
    @Override
    public AstNode visitOpLte(product_htmlParser.OpLteContext ctx) { return null; }
    @Override
    public AstNode visitOpGte(product_htmlParser.OpGteContext ctx) { return null; }
    @Override
    public AstNode visitOpIn(product_htmlParser.OpInContext ctx) { return null; }
    @Override
    public AstNode visitOpNotIn(product_htmlParser.OpNotInContext ctx) { return null; }
    @Override
    public AstNode visitOpIs(product_htmlParser.OpIsContext ctx) { return null; }
    @Override
    public AstNode visitOpIsNot(product_htmlParser.OpIsNotContext ctx) { return null; }

    @Override
    public AstNode visitJinjaConcat(product_htmlParser.JinjaConcatContext ctx) {
        if (ctx.jinjaAddSub().size() == 1) {
            return visit(ctx.jinjaAddSub(0));
        }
        int[] p = pos(ctx);
        ExpressionNode left = (ExpressionNode) visit(ctx.jinjaAddSub(0));
        for (int i = 1; i < ctx.jinjaAddSub().size(); i++) {
            ExpressionNode right = (ExpressionNode) visit(ctx.jinjaAddSub(i));
            left = new BinaryOpNode(BinaryOpNode.Operator.CONCAT, left, right, p[0], p[1]);
        }
        return left;
    }

    @Override
    public AstNode visitJinjaAddSub(product_htmlParser.JinjaAddSubContext ctx) {
        if (ctx.jinjaMulDiv().size() == 1) {
            return visit(ctx.jinjaMulDiv(0));
        }
        int[] p = pos(ctx);
        ExpressionNode left = (ExpressionNode) visit(ctx.jinjaMulDiv(0));
        for (int i = 1; i < ctx.jinjaMulDiv().size(); i++) {
            ExpressionNode right = (ExpressionNode) visit(ctx.jinjaMulDiv(i));
            ParseTree opTree = ctx.getChild(i * 2 - 1);
            String opText = opTree.getText();
            BinaryOpNode.Operator opType = opText.equals("+")
                    ? BinaryOpNode.Operator.ADD : BinaryOpNode.Operator.SUB;
            left = new BinaryOpNode(opType, left, right, p[0], p[1]);
        }
        return left;
    }

    @Override
    public AstNode visitJinjaMulDiv(product_htmlParser.JinjaMulDivContext ctx) {
        if (ctx.jinjaFilter().size() == 1) {
            return visit(ctx.jinjaFilter(0));
        }
        int[] p = pos(ctx);
        ExpressionNode left = (ExpressionNode) visit(ctx.jinjaFilter(0));
        for (int i = 1; i < ctx.jinjaFilter().size(); i++) {
            ExpressionNode right = (ExpressionNode) visit(ctx.jinjaFilter(i));
            ParseTree opTree = ctx.getChild(i * 2 - 1);
            String opText = opTree.getText();
            BinaryOpNode.Operator opType;
            switch (opText) {
                case "*": opType = BinaryOpNode.Operator.MUL; break;
                case "/": opType = BinaryOpNode.Operator.DIV; break;
                case "%": opType = BinaryOpNode.Operator.MOD; break;
                default: throw new IllegalArgumentException("Unknown mul/div op: " + opText);
            }
            left = new BinaryOpNode(opType, left, right, p[0], p[1]);
        }
        return left;
    }

    @Override
    public AstNode visitJinjaFilter(product_htmlParser.JinjaFilterContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode operand = (ExpressionNode) visit(ctx.jinjaPostfix());

        for (int i = 0; i < ctx.JINJA_ID().size(); i++) {
            String filterName = ctx.JINJA_ID(i).getText();
            FilterNode filterNode = new FilterNode(operand, filterName, p[0], p[1]);

            // ====== Symbol Table: تسجيل الفلتر المستخدم ======
            if (st.lookup("filter_" + filterName) == null) {
                st.insert("filter_" + filterName, Kind.FILTER, "Filter", filterName, p[0]);
            }

            if (i < ctx.jinjaExpressionList().size() && ctx.jinjaExpressionList(i) != null) {
                for (product_htmlParser.JinjaExpressionContext exprCtx :
                        ctx.jinjaExpressionList(i).jinjaExpression()) {
                    filterNode.addArgument((ExpressionNode) visit(exprCtx));
                }
            }
            operand = filterNode;
        }
        return operand;
    }

    @Override
    public AstNode visitJinjaPostfixBase(product_htmlParser.JinjaPostfixBaseContext ctx) {
        return visit(ctx.jinjaPrimary());
    }

    @Override
    public AstNode visitJinjaIndex(product_htmlParser.JinjaIndexContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode base = (ExpressionNode) visit(ctx.jinjaPostfix());
        product_htmlParser.JinjaSliceContext sliceCtx = ctx.jinjaSlice();

        boolean hasColon = !sliceCtx.JINJA_COLON().isEmpty();
        if (hasColon) {
            List<product_htmlParser.JinjaExpressionContext> exprs = sliceCtx.jinjaExpression();
            int numColons = sliceCtx.JINJA_COLON().size();

            ExpressionNode start = null, stop = null, step = null;
            int childCount = sliceCtx.getChildCount();

            if (numColons == 1) {
                int colonPos = -1;
                for (int i = 0; i < childCount; i++) {
                    if (sliceCtx.getChild(i).getText().equals(":")) {
                        colonPos = i;
                        break;
                    }
                }
                for (product_htmlParser.JinjaExpressionContext e : exprs) {
                    int ePos = indexOfChild(sliceCtx, e);
                    if (ePos < colonPos) start = (ExpressionNode) visit(e);
                    else stop = (ExpressionNode) visit(e);
                }
            } else if (numColons >= 2) {
                int firstColonPos = -1, secondColonPos = -1;
                for (int i = 0; i < childCount; i++) {
                    if (sliceCtx.getChild(i).getText().equals(":")) {
                        if (firstColonPos == -1) firstColonPos = i;
                        else if (secondColonPos == -1) secondColonPos = i;
                    }
                }
                for (product_htmlParser.JinjaExpressionContext e : exprs) {
                    int ePos = indexOfChild(sliceCtx, e);
                    if (ePos < firstColonPos) start = (ExpressionNode) visit(e);
                    else if (ePos < secondColonPos) stop = (ExpressionNode) visit(e);
                    else step = (ExpressionNode) visit(e);
                }
            }
            return new SliceNode(base, start, stop, step, p[0], p[1]);
        }

        ExpressionNode index = (ExpressionNode) visit(sliceCtx.jinjaExpression(0));
        return new IndexNode(base, index, p[0], p[1]);
    }

    private int indexOfChild(ParserRuleContext ctx, ParserRuleContext target) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) == target) return i;
            if (ctx.getChild(i) instanceof ParserRuleContext) {
                ParserRuleContext pc = (ParserRuleContext) ctx.getChild(i);
                if (pc.getStart().getStartIndex() == target.getStart().getStartIndex()
                        && pc.getStop().getStopIndex() == target.getStop().getStopIndex()) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public AstNode visitJinjaAttr(product_htmlParser.JinjaAttrContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode obj = (ExpressionNode) visit(ctx.jinjaPostfix());
        String attr = ctx.JINJA_ID().getText();

        if (obj instanceof AstHtml.VariableNode) {
            String varName = ((AstHtml.VariableNode) obj).getName();

            if (!st.isFlaskGlobal(varName)) {

                Symbol tmplSym = st.lookup(currentTemplateName);
                if (tmplSym != null) {
                    tmplSym.addVariableAttribute(varName, attr);
                }
            }
        }

        return new AttributeAccessNode(obj, attr, p[0], p[1]);
    }
    @Override
    public AstNode visitJinjaCall(product_htmlParser.JinjaCallContext ctx) {
        int[] p = pos(ctx);
        ExpressionNode callee = (ExpressionNode) visit(ctx.jinjaPostfix());
        CallNode call = new CallNode(callee, p[0], p[1]);
        if (ctx.jinjaExpressionList() != null) {
            for (product_htmlParser.JinjaExpressionContext exprCtx :
                    ctx.jinjaExpressionList().jinjaExpression()) {
                call.addArgument((ExpressionNode) visit(exprCtx));
            }
        }
        return call;
    }

    @Override
    public AstNode visitJinjaNum(product_htmlParser.JinjaNumContext ctx) {
        int[] p = pos(ctx);
        return new NumberLiteral(ctx.JINJA_NUMBER().getText(), p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaStr(product_htmlParser.JinjaStrContext ctx) {
        int[] p = pos(ctx);
        return new StringLiteral(ctx.JINJA_STRING().getText(), p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaTrueLit(product_htmlParser.JinjaTrueLitContext ctx) {
        int[] p = pos(ctx);
        return new BooleanLiteral(true, p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaFalseLit(product_htmlParser.JinjaFalseLitContext ctx) {
        int[] p = pos(ctx);
        return new BooleanLiteral(false, p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaNoneLit(product_htmlParser.JinjaNoneLitContext ctx) {
        int[] p = pos(ctx);
        return new NoneLiteral(p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaNullLit(product_htmlParser.JinjaNullLitContext ctx) {
        int[] p = pos(ctx);
        return new NoneLiteral(p[0], p[1]);
    }

    @Override
    public AstNode visitJinjaVar(product_htmlParser.JinjaVarContext ctx) {
        int[] p = pos(ctx);
        String varName = ctx.JINJA_ID().getText();

        recordTemplateVariableUsage(varName, p[0]);

        return new VariableNode(varName, p[0], p[1]);
    }
    @Override
    public AstNode visitJinjaParen(product_htmlParser.JinjaParenContext ctx) {
        return visit(ctx.jinjaExpression());
    }

    @Override
    public AstNode visitJinjaExpressionList(product_htmlParser.JinjaExpressionListContext ctx) {
        return null;
    }

    // ================================================================
    //  CSS
    // ================================================================

    @Override
    public AstNode visitCssRule(product_htmlParser.CssRuleContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.CssRuleSetContext ruleCtx = ctx.cssRuleSet();
        CssRuleSetNode node = new CssRuleSetNode(p[0], p[1]);
        for (product_htmlParser.CssSelectorContext selCtx :
                ruleCtx.cssSelectorList().cssSelector()) {
            node.addSelector(new CssSelectorNode(selCtx.getText(), p[0], p[1]));
        }
        for (product_htmlParser.CssDeclarationContext decl : ruleCtx.cssDeclaration()) {
            node.addDeclaration((CssDeclarationNode) visit(decl));
        }
        return node;
    }

    @Override
    public AstNode visitCssAtRuleStmt(product_htmlParser.CssAtRuleStmtContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.CssAtRuleContext atCtx = ctx.cssAtRule();
        String keyword = atCtx.CSS_AT_KEYWORD().getText().substring(1);  // remove @
        String prelude = atCtx.cssAtRulePrelude() != null ?
                atCtx.cssAtRulePrelude().getText() : "";
        boolean hasBlock = (atCtx.cssAtRuleBody() instanceof product_htmlParser.AtRuleBlockContext);
        CssAtRuleNode node = new CssAtRuleNode(keyword, prelude, hasBlock, p[0], p[1]);

        if (hasBlock) {
            product_htmlParser.AtRuleBlockContext block =
                    (product_htmlParser.AtRuleBlockContext) atCtx.cssAtRuleBody();
            for (product_htmlParser.CssStatementContext stmt : block.cssStatement()) {
                AstNode n = visit(stmt);
                if (n instanceof CssNode) node.addBodyStatement((CssNode) n);
            }
        }
        return node;
    }

    @Override
    public AstNode visitCssDeclaration(product_htmlParser.CssDeclarationContext ctx) {
        int[] p = pos(ctx);
        String property = ctx.CSS_IDENT().getText();
        boolean important = ctx.CSS_IMPORTANT() != null;
        CssDeclarationNode node = new CssDeclarationNode(property, important, p[0], p[1]);

        for (product_htmlParser.CssValueContext valCtx : ctx.cssValueList().cssValue()) {
            AstNode valNode = visit(valCtx);
            if (valNode instanceof CssValueNode) {
                node.addValue((CssValueNode) valNode);
            }
        }
        return node;
    }

    @Override
    public AstNode visitCssNumValue(product_htmlParser.CssNumValueContext ctx) {
        int[] p = pos(ctx);
        return new CssNumberValueNode(ctx.CSS_NUMBER().getText(), p[0], p[1]);
    }

    @Override
    public AstNode visitCssHashValue(product_htmlParser.CssHashValueContext ctx) {
        int[] p = pos(ctx);
        return new CssHashValueNode(ctx.CSS_HASH().getText(), p[0], p[1]);
    }

    @Override
    public AstNode visitCssStrValue(product_htmlParser.CssStrValueContext ctx) {
        int[] p = pos(ctx);
        return new CssStringValueNode(ctx.CSS_STRING().getText(), p[0], p[1]);
    }

    @Override
    public AstNode visitCssIdentValue(product_htmlParser.CssIdentValueContext ctx) {
        int[] p = pos(ctx);
        return new CssIdentValueNode(ctx.CSS_IDENT().getText(), p[0], p[1]);
    }

    @Override
    public AstNode visitCssFuncValue(product_htmlParser.CssFuncValueContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.CssFunctionContext funcCtx = ctx.cssFunction();
        String name = funcCtx.CSS_IDENT().getText();
        CssFunctionValueNode node = new CssFunctionValueNode(name, p[0], p[1]);

        if (funcCtx.cssFunctionArgList() != null) {
            for (product_htmlParser.CssFunctionArgContext argCtx :
                    funcCtx.cssFunctionArgList().cssFunctionArg()) {
                for (product_htmlParser.CssValueContext vCtx : argCtx.cssValue()) {
                    AstNode v = visit(vCtx);
                    if (v instanceof CssValueNode) node.addArgument((CssValueNode) v);
                }
            }
        }
        return node;
    }
    @Override
    public AstNode visitCssJinjaValue(product_htmlParser.CssJinjaValueContext ctx) {
        int[] p = pos(ctx);
        product_htmlParser.Jinja_varContext jvCtx = ctx.jinja_var();
        ExpressionNode expr = null;

        if (jvCtx.jinjaExpression() != null) {
            expr = (ExpressionNode) visit(jvCtx.jinjaExpression());
            String exprText = jvCtx.jinjaExpression().getText();
            String baseVar = exprText.contains(".") ? exprText.split("\\.")[0] : exprText;
            recordTemplateVariableUsage(baseVar, p[0]);
        }

        return new CssJinjaValueNode(expr, p[0], p[1]);
    }
}