// Generated from src/antlr/product_htmlParser.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link product_htmlParser}.
 */
public interface product_htmlParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(product_htmlParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(product_htmlParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#prolog}.
	 * @param ctx the parse tree
	 */
	void enterProlog(product_htmlParser.PrologContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#prolog}.
	 * @param ctx the parse tree
	 */
	void exitProlog(product_htmlParser.PrologContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TextContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void enterTextContent(product_htmlParser.TextContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TextContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void exitTextContent(product_htmlParser.TextContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ElementContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void enterElementContent(product_htmlParser.ElementContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ElementContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void exitElementContent(product_htmlParser.ElementContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaVarContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void enterJinjaVarContent(product_htmlParser.JinjaVarContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaVarContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void exitJinjaVarContent(product_htmlParser.JinjaVarContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBlockContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlockContent(product_htmlParser.JinjaBlockContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBlockContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlockContent(product_htmlParser.JinjaBlockContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#text}.
	 * @param ctx the parse tree
	 */
	void enterText(product_htmlParser.TextContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#text}.
	 * @param ctx the parse tree
	 */
	void exitText(product_htmlParser.TextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StyleElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void enterStyleElemAlt(product_htmlParser.StyleElemAltContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void exitStyleElemAlt(product_htmlParser.StyleElemAltContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ScriptElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void enterScriptElemAlt(product_htmlParser.ScriptElemAltContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ScriptElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void exitScriptElemAlt(product_htmlParser.ScriptElemAltContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VoidElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void enterVoidElemAlt(product_htmlParser.VoidElemAltContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VoidElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void exitVoidElemAlt(product_htmlParser.VoidElemAltContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContainerElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void enterContainerElemAlt(product_htmlParser.ContainerElemAltContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContainerElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 */
	void exitContainerElemAlt(product_htmlParser.ContainerElemAltContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#styleElement}.
	 * @param ctx the parse tree
	 */
	void enterStyleElement(product_htmlParser.StyleElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#styleElement}.
	 * @param ctx the parse tree
	 */
	void exitStyleElement(product_htmlParser.StyleElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#scriptElement}.
	 * @param ctx the parse tree
	 */
	void enterScriptElement(product_htmlParser.ScriptElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#scriptElement}.
	 * @param ctx the parse tree
	 */
	void exitScriptElement(product_htmlParser.ScriptElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VoidTag}
	 * labeled alternative in {@link product_htmlParser#voidElement}.
	 * @param ctx the parse tree
	 */
	void enterVoidTag(product_htmlParser.VoidTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VoidTag}
	 * labeled alternative in {@link product_htmlParser#voidElement}.
	 * @param ctx the parse tree
	 */
	void exitVoidTag(product_htmlParser.VoidTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelfClosingTag}
	 * labeled alternative in {@link product_htmlParser#voidElement}.
	 * @param ctx the parse tree
	 */
	void enterSelfClosingTag(product_htmlParser.SelfClosingTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelfClosingTag}
	 * labeled alternative in {@link product_htmlParser#voidElement}.
	 * @param ctx the parse tree
	 */
	void exitSelfClosingTag(product_htmlParser.SelfClosingTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#containerElement}.
	 * @param ctx the parse tree
	 */
	void enterContainerElement(product_htmlParser.ContainerElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#containerElement}.
	 * @param ctx the parse tree
	 */
	void exitContainerElement(product_htmlParser.ContainerElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#openTag}.
	 * @param ctx the parse tree
	 */
	void enterOpenTag(product_htmlParser.OpenTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#openTag}.
	 * @param ctx the parse tree
	 */
	void exitOpenTag(product_htmlParser.OpenTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#closeTag}.
	 * @param ctx the parse tree
	 */
	void enterCloseTag(product_htmlParser.CloseTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#closeTag}.
	 * @param ctx the parse tree
	 */
	void exitCloseTag(product_htmlParser.CloseTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StyleAttr}
	 * labeled alternative in {@link product_htmlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterStyleAttr(product_htmlParser.StyleAttrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleAttr}
	 * labeled alternative in {@link product_htmlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitStyleAttr(product_htmlParser.StyleAttrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NormalAttr}
	 * labeled alternative in {@link product_htmlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterNormalAttr(product_htmlParser.NormalAttrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NormalAttr}
	 * labeled alternative in {@link product_htmlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitNormalAttr(product_htmlParser.NormalAttrContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#styleAttribute}.
	 * @param ctx the parse tree
	 */
	void enterStyleAttribute(product_htmlParser.StyleAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#styleAttribute}.
	 * @param ctx the parse tree
	 */
	void exitStyleAttribute(product_htmlParser.StyleAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#normalAttribute}.
	 * @param ctx the parse tree
	 */
	void enterNormalAttribute(product_htmlParser.NormalAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#normalAttribute}.
	 * @param ctx the parse tree
	 */
	void exitNormalAttribute(product_htmlParser.NormalAttributeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttrStringValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void enterAttrStringValue(product_htmlParser.AttrStringValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttrStringValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void exitAttrStringValue(product_htmlParser.AttrStringValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttrJinjaVarValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void enterAttrJinjaVarValue(product_htmlParser.AttrJinjaVarValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttrJinjaVarValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void exitAttrJinjaVarValue(product_htmlParser.AttrJinjaVarValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttrJinjaBlockValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void enterAttrJinjaBlockValue(product_htmlParser.AttrJinjaBlockValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttrJinjaBlockValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void exitAttrJinjaBlockValue(product_htmlParser.AttrJinjaBlockValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinja_var}.
	 * @param ctx the parse tree
	 */
	void enterJinja_var(product_htmlParser.Jinja_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinja_var}.
	 * @param ctx the parse tree
	 */
	void exitJinja_var(product_htmlParser.Jinja_varContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExtendsStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExtendsStmt(product_htmlParser.JinjaExtendsStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExtendsStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExtendsStmt(product_htmlParser.JinjaExtendsStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBlockStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlockStmt(product_htmlParser.JinjaBlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBlockStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlockStmt(product_htmlParser.JinjaBlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaIfStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaIfStmt(product_htmlParser.JinjaIfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaIfStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaIfStmt(product_htmlParser.JinjaIfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaForStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaForStmt(product_htmlParser.JinjaForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaForStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaForStmt(product_htmlParser.JinjaForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaSetStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaSetStmt(product_htmlParser.JinjaSetStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaSetStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaSetStmt(product_htmlParser.JinjaSetStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaIncludeStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaIncludeStmt(product_htmlParser.JinjaIncludeStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaIncludeStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaIncludeStmt(product_htmlParser.JinjaIncludeStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaImportStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaImportStmt(product_htmlParser.JinjaImportStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaImportStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaImportStmt(product_htmlParser.JinjaImportStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaFromImportStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFromImportStmt(product_htmlParser.JinjaFromImportStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaFromImportStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFromImportStmt(product_htmlParser.JinjaFromImportStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaRawStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaRawStmt(product_htmlParser.JinjaRawStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaRawStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaRawStmt(product_htmlParser.JinjaRawStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaMacroStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaMacroStmt(product_htmlParser.JinjaMacroStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaMacroStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaMacroStmt(product_htmlParser.JinjaMacroStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaWithStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaWithStmt(product_htmlParser.JinjaWithStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaWithStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaWithStmt(product_htmlParser.JinjaWithStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaFilterBlockStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFilterBlockStmt(product_htmlParser.JinjaFilterBlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaFilterBlockStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFilterBlockStmt(product_htmlParser.JinjaFilterBlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaGenericStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void enterJinjaGenericStmt(product_htmlParser.JinjaGenericStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaGenericStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 */
	void exitJinjaGenericStmt(product_htmlParser.JinjaGenericStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaExtends}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExtends(product_htmlParser.JinjaExtendsContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaExtends}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExtends(product_htmlParser.JinjaExtendsContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaBlock}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlock(product_htmlParser.JinjaBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaBlock}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlock(product_htmlParser.JinjaBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaIf}.
	 * @param ctx the parse tree
	 */
	void enterJinjaIf(product_htmlParser.JinjaIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaIf}.
	 * @param ctx the parse tree
	 */
	void exitJinjaIf(product_htmlParser.JinjaIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaFor}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFor(product_htmlParser.JinjaForContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaFor}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFor(product_htmlParser.JinjaForContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#forTarget}.
	 * @param ctx the parse tree
	 */
	void enterForTarget(product_htmlParser.ForTargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#forTarget}.
	 * @param ctx the parse tree
	 */
	void exitForTarget(product_htmlParser.ForTargetContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaSet}.
	 * @param ctx the parse tree
	 */
	void enterJinjaSet(product_htmlParser.JinjaSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaSet}.
	 * @param ctx the parse tree
	 */
	void exitJinjaSet(product_htmlParser.JinjaSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaInclude}.
	 * @param ctx the parse tree
	 */
	void enterJinjaInclude(product_htmlParser.JinjaIncludeContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaInclude}.
	 * @param ctx the parse tree
	 */
	void exitJinjaInclude(product_htmlParser.JinjaIncludeContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaImport}.
	 * @param ctx the parse tree
	 */
	void enterJinjaImport(product_htmlParser.JinjaImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaImport}.
	 * @param ctx the parse tree
	 */
	void exitJinjaImport(product_htmlParser.JinjaImportContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaFromImport}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFromImport(product_htmlParser.JinjaFromImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaFromImport}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFromImport(product_htmlParser.JinjaFromImportContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaImportNames}.
	 * @param ctx the parse tree
	 */
	void enterJinjaImportNames(product_htmlParser.JinjaImportNamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaImportNames}.
	 * @param ctx the parse tree
	 */
	void exitJinjaImportNames(product_htmlParser.JinjaImportNamesContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaImportName}.
	 * @param ctx the parse tree
	 */
	void enterJinjaImportName(product_htmlParser.JinjaImportNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaImportName}.
	 * @param ctx the parse tree
	 */
	void exitJinjaImportName(product_htmlParser.JinjaImportNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaRaw}.
	 * @param ctx the parse tree
	 */
	void enterJinjaRaw(product_htmlParser.JinjaRawContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaRaw}.
	 * @param ctx the parse tree
	 */
	void exitJinjaRaw(product_htmlParser.JinjaRawContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaMacro}.
	 * @param ctx the parse tree
	 */
	void enterJinjaMacro(product_htmlParser.JinjaMacroContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaMacro}.
	 * @param ctx the parse tree
	 */
	void exitJinjaMacro(product_htmlParser.JinjaMacroContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaMacroParams}.
	 * @param ctx the parse tree
	 */
	void enterJinjaMacroParams(product_htmlParser.JinjaMacroParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaMacroParams}.
	 * @param ctx the parse tree
	 */
	void exitJinjaMacroParams(product_htmlParser.JinjaMacroParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaMacroParam}.
	 * @param ctx the parse tree
	 */
	void enterJinjaMacroParam(product_htmlParser.JinjaMacroParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaMacroParam}.
	 * @param ctx the parse tree
	 */
	void exitJinjaMacroParam(product_htmlParser.JinjaMacroParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaWith}.
	 * @param ctx the parse tree
	 */
	void enterJinjaWith(product_htmlParser.JinjaWithContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaWith}.
	 * @param ctx the parse tree
	 */
	void exitJinjaWith(product_htmlParser.JinjaWithContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaSetExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaSetExpr(product_htmlParser.JinjaSetExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaSetExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaSetExpr(product_htmlParser.JinjaSetExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaFilterBlock}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFilterBlock(product_htmlParser.JinjaFilterBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaFilterBlock}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFilterBlock(product_htmlParser.JinjaFilterBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaGenericBlock}.
	 * @param ctx the parse tree
	 */
	void enterJinjaGenericBlock(product_htmlParser.JinjaGenericBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaGenericBlock}.
	 * @param ctx the parse tree
	 */
	void exitJinjaGenericBlock(product_htmlParser.JinjaGenericBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaExpression}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExpression(product_htmlParser.JinjaExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaExpression}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExpression(product_htmlParser.JinjaExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaTernary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaTernary(product_htmlParser.JinjaTernaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaTernary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaTernary(product_htmlParser.JinjaTernaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaOr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaOr(product_htmlParser.JinjaOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaOr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaOr(product_htmlParser.JinjaOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaAnd}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAnd(product_htmlParser.JinjaAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaAnd}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAnd(product_htmlParser.JinjaAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaUnaryNot}
	 * labeled alternative in {@link product_htmlParser#jinjaNot}.
	 * @param ctx the parse tree
	 */
	void enterJinjaUnaryNot(product_htmlParser.JinjaUnaryNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaUnaryNot}
	 * labeled alternative in {@link product_htmlParser#jinjaNot}.
	 * @param ctx the parse tree
	 */
	void exitJinjaUnaryNot(product_htmlParser.JinjaUnaryNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaCmpExpr}
	 * labeled alternative in {@link product_htmlParser#jinjaNot}.
	 * @param ctx the parse tree
	 */
	void enterJinjaCmpExpr(product_htmlParser.JinjaCmpExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaCmpExpr}
	 * labeled alternative in {@link product_htmlParser#jinjaNot}.
	 * @param ctx the parse tree
	 */
	void exitJinjaCmpExpr(product_htmlParser.JinjaCmpExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaComparison}.
	 * @param ctx the parse tree
	 */
	void enterJinjaComparison(product_htmlParser.JinjaComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaComparison}.
	 * @param ctx the parse tree
	 */
	void exitJinjaComparison(product_htmlParser.JinjaComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpEq}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpEq(product_htmlParser.OpEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpEq}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpEq(product_htmlParser.OpEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpNeq}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpNeq(product_htmlParser.OpNeqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpNeq}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpNeq(product_htmlParser.OpNeqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpLt}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpLt(product_htmlParser.OpLtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpLt}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpLt(product_htmlParser.OpLtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpGt}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpGt(product_htmlParser.OpGtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpGt}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpGt(product_htmlParser.OpGtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpLte}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpLte(product_htmlParser.OpLteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpLte}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpLte(product_htmlParser.OpLteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpGte}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpGte(product_htmlParser.OpGteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpGte}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpGte(product_htmlParser.OpGteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpIn}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpIn(product_htmlParser.OpInContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpIn}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpIn(product_htmlParser.OpInContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpNotIn}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpNotIn(product_htmlParser.OpNotInContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpNotIn}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpNotIn(product_htmlParser.OpNotInContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpIs}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpIs(product_htmlParser.OpIsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpIs}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpIs(product_htmlParser.OpIsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OpIsNot}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void enterOpIsNot(product_htmlParser.OpIsNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OpIsNot}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 */
	void exitOpIsNot(product_htmlParser.OpIsNotContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaConcat}.
	 * @param ctx the parse tree
	 */
	void enterJinjaConcat(product_htmlParser.JinjaConcatContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaConcat}.
	 * @param ctx the parse tree
	 */
	void exitJinjaConcat(product_htmlParser.JinjaConcatContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaAddSub}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAddSub(product_htmlParser.JinjaAddSubContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaAddSub}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAddSub(product_htmlParser.JinjaAddSubContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaMulDiv}.
	 * @param ctx the parse tree
	 */
	void enterJinjaMulDiv(product_htmlParser.JinjaMulDivContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaMulDiv}.
	 * @param ctx the parse tree
	 */
	void exitJinjaMulDiv(product_htmlParser.JinjaMulDivContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaFilter}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFilter(product_htmlParser.JinjaFilterContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaFilter}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFilter(product_htmlParser.JinjaFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaIndex}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void enterJinjaIndex(product_htmlParser.JinjaIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaIndex}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void exitJinjaIndex(product_htmlParser.JinjaIndexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaCall}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void enterJinjaCall(product_htmlParser.JinjaCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaCall}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void exitJinjaCall(product_htmlParser.JinjaCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaPostfixBase}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void enterJinjaPostfixBase(product_htmlParser.JinjaPostfixBaseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaPostfixBase}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void exitJinjaPostfixBase(product_htmlParser.JinjaPostfixBaseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaAttr}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAttr(product_htmlParser.JinjaAttrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaAttr}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAttr(product_htmlParser.JinjaAttrContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaCallArgList}.
	 * @param ctx the parse tree
	 */
	void enterJinjaCallArgList(product_htmlParser.JinjaCallArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaCallArgList}.
	 * @param ctx the parse tree
	 */
	void exitJinjaCallArgList(product_htmlParser.JinjaCallArgListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaKwArg}
	 * labeled alternative in {@link product_htmlParser#jinjaCallArg}.
	 * @param ctx the parse tree
	 */
	void enterJinjaKwArg(product_htmlParser.JinjaKwArgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaKwArg}
	 * labeled alternative in {@link product_htmlParser#jinjaCallArg}.
	 * @param ctx the parse tree
	 */
	void exitJinjaKwArg(product_htmlParser.JinjaKwArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaPosArg}
	 * labeled alternative in {@link product_htmlParser#jinjaCallArg}.
	 * @param ctx the parse tree
	 */
	void enterJinjaPosArg(product_htmlParser.JinjaPosArgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaPosArg}
	 * labeled alternative in {@link product_htmlParser#jinjaCallArg}.
	 * @param ctx the parse tree
	 */
	void exitJinjaPosArg(product_htmlParser.JinjaPosArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaSlice}.
	 * @param ctx the parse tree
	 */
	void enterJinjaSlice(product_htmlParser.JinjaSliceContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaSlice}.
	 * @param ctx the parse tree
	 */
	void exitJinjaSlice(product_htmlParser.JinjaSliceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaNum}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNum(product_htmlParser.JinjaNumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaNum}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNum(product_htmlParser.JinjaNumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaStr}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaStr(product_htmlParser.JinjaStrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaStr}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaStr(product_htmlParser.JinjaStrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaTrueLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaTrueLit(product_htmlParser.JinjaTrueLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaTrueLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaTrueLit(product_htmlParser.JinjaTrueLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaFalseLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFalseLit(product_htmlParser.JinjaFalseLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaFalseLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFalseLit(product_htmlParser.JinjaFalseLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaNoneLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNoneLit(product_htmlParser.JinjaNoneLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaNoneLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNoneLit(product_htmlParser.JinjaNoneLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaNullLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNullLit(product_htmlParser.JinjaNullLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaNullLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNullLit(product_htmlParser.JinjaNullLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaVar}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaVar(product_htmlParser.JinjaVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaVar}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaVar(product_htmlParser.JinjaVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaParen}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaParen(product_htmlParser.JinjaParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaParen}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaParen(product_htmlParser.JinjaParenContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#jinjaExpressionList}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExpressionList(product_htmlParser.JinjaExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#jinjaExpressionList}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExpressionList(product_htmlParser.JinjaExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link product_htmlParser#cssStatement}.
	 * @param ctx the parse tree
	 */
	void enterCssRule(product_htmlParser.CssRuleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link product_htmlParser#cssStatement}.
	 * @param ctx the parse tree
	 */
	void exitCssRule(product_htmlParser.CssRuleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssAtRuleStmt}
	 * labeled alternative in {@link product_htmlParser#cssStatement}.
	 * @param ctx the parse tree
	 */
	void enterCssAtRuleStmt(product_htmlParser.CssAtRuleStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssAtRuleStmt}
	 * labeled alternative in {@link product_htmlParser#cssStatement}.
	 * @param ctx the parse tree
	 */
	void exitCssAtRuleStmt(product_htmlParser.CssAtRuleStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssRuleSet}.
	 * @param ctx the parse tree
	 */
	void enterCssRuleSet(product_htmlParser.CssRuleSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssRuleSet}.
	 * @param ctx the parse tree
	 */
	void exitCssRuleSet(product_htmlParser.CssRuleSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssSelectorList}.
	 * @param ctx the parse tree
	 */
	void enterCssSelectorList(product_htmlParser.CssSelectorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssSelectorList}.
	 * @param ctx the parse tree
	 */
	void exitCssSelectorList(product_htmlParser.CssSelectorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssSelector(product_htmlParser.CssSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssSelector(product_htmlParser.CssSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssCompoundSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssCompoundSelector(product_htmlParser.CssCompoundSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssCompoundSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssCompoundSelector(product_htmlParser.CssCompoundSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssClassSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssClassSelector(product_htmlParser.CssClassSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssClassSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssClassSelector(product_htmlParser.CssClassSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssAttributeSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssAttributeSelector(product_htmlParser.CssAttributeSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssAttributeSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssAttributeSelector(product_htmlParser.CssAttributeSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PseudoClass}
	 * labeled alternative in {@link product_htmlParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 */
	void enterPseudoClass(product_htmlParser.PseudoClassContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PseudoClass}
	 * labeled alternative in {@link product_htmlParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 */
	void exitPseudoClass(product_htmlParser.PseudoClassContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PseudoElement}
	 * labeled alternative in {@link product_htmlParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 */
	void enterPseudoElement(product_htmlParser.PseudoElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PseudoElement}
	 * labeled alternative in {@link product_htmlParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 */
	void exitPseudoElement(product_htmlParser.PseudoElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssPseudoArg}.
	 * @param ctx the parse tree
	 */
	void enterCssPseudoArg(product_htmlParser.CssPseudoArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssPseudoArg}.
	 * @param ctx the parse tree
	 */
	void exitCssPseudoArg(product_htmlParser.CssPseudoArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ChildCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void enterChildCombinator(product_htmlParser.ChildCombinatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ChildCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void exitChildCombinator(product_htmlParser.ChildCombinatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AdjacentCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void enterAdjacentCombinator(product_htmlParser.AdjacentCombinatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AdjacentCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void exitAdjacentCombinator(product_htmlParser.AdjacentCombinatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GeneralSiblingCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void enterGeneralSiblingCombinator(product_htmlParser.GeneralSiblingCombinatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GeneralSiblingCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void exitGeneralSiblingCombinator(product_htmlParser.GeneralSiblingCombinatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssAtRule}.
	 * @param ctx the parse tree
	 */
	void enterCssAtRule(product_htmlParser.CssAtRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssAtRule}.
	 * @param ctx the parse tree
	 */
	void exitCssAtRule(product_htmlParser.CssAtRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssAtRulePrelude}.
	 * @param ctx the parse tree
	 */
	void enterCssAtRulePrelude(product_htmlParser.CssAtRulePreludeContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssAtRulePrelude}.
	 * @param ctx the parse tree
	 */
	void exitCssAtRulePrelude(product_htmlParser.CssAtRulePreludeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtRuleBlock}
	 * labeled alternative in {@link product_htmlParser#cssAtRuleBody}.
	 * @param ctx the parse tree
	 */
	void enterAtRuleBlock(product_htmlParser.AtRuleBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtRuleBlock}
	 * labeled alternative in {@link product_htmlParser#cssAtRuleBody}.
	 * @param ctx the parse tree
	 */
	void exitAtRuleBlock(product_htmlParser.AtRuleBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtRuleSimple}
	 * labeled alternative in {@link product_htmlParser#cssAtRuleBody}.
	 * @param ctx the parse tree
	 */
	void enterAtRuleSimple(product_htmlParser.AtRuleSimpleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtRuleSimple}
	 * labeled alternative in {@link product_htmlParser#cssAtRuleBody}.
	 * @param ctx the parse tree
	 */
	void exitAtRuleSimple(product_htmlParser.AtRuleSimpleContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterCssDeclaration(product_htmlParser.CssDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitCssDeclaration(product_htmlParser.CssDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssValueList}.
	 * @param ctx the parse tree
	 */
	void enterCssValueList(product_htmlParser.CssValueListContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssValueList}.
	 * @param ctx the parse tree
	 */
	void exitCssValueList(product_htmlParser.CssValueListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssNumValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void enterCssNumValue(product_htmlParser.CssNumValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssNumValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void exitCssNumValue(product_htmlParser.CssNumValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssHashValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void enterCssHashValue(product_htmlParser.CssHashValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssHashValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void exitCssHashValue(product_htmlParser.CssHashValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssStrValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void enterCssStrValue(product_htmlParser.CssStrValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssStrValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void exitCssStrValue(product_htmlParser.CssStrValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssIdentValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void enterCssIdentValue(product_htmlParser.CssIdentValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssIdentValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void exitCssIdentValue(product_htmlParser.CssIdentValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssFuncValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void enterCssFuncValue(product_htmlParser.CssFuncValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssFuncValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void exitCssFuncValue(product_htmlParser.CssFuncValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssJinjaValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void enterCssJinjaValue(product_htmlParser.CssJinjaValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssJinjaValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 */
	void exitCssJinjaValue(product_htmlParser.CssJinjaValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssFunction}.
	 * @param ctx the parse tree
	 */
	void enterCssFunction(product_htmlParser.CssFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssFunction}.
	 * @param ctx the parse tree
	 */
	void exitCssFunction(product_htmlParser.CssFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssFunctionArgList}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionArgList(product_htmlParser.CssFunctionArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssFunctionArgList}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionArgList(product_htmlParser.CssFunctionArgListContext ctx);
	/**
	 * Enter a parse tree produced by {@link product_htmlParser#cssFunctionArg}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionArg(product_htmlParser.CssFunctionArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link product_htmlParser#cssFunctionArg}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionArg(product_htmlParser.CssFunctionArgContext ctx);
}