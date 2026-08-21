// Generated from src/antlr/product_htmlParser.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link product_htmlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface product_htmlParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(product_htmlParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#prolog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProlog(product_htmlParser.PrologContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TextContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextContent(product_htmlParser.TextContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ElementContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementContent(product_htmlParser.ElementContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaVarContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaVarContent(product_htmlParser.JinjaVarContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBlockContent}
	 * labeled alternative in {@link product_htmlParser#content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlockContent(product_htmlParser.JinjaBlockContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText(product_htmlParser.TextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StyleElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleElemAlt(product_htmlParser.StyleElemAltContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ScriptElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScriptElemAlt(product_htmlParser.ScriptElemAltContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VoidElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidElemAlt(product_htmlParser.VoidElemAltContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContainerElemAlt}
	 * labeled alternative in {@link product_htmlParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainerElemAlt(product_htmlParser.ContainerElemAltContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#styleElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleElement(product_htmlParser.StyleElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#scriptElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScriptElement(product_htmlParser.ScriptElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VoidTag}
	 * labeled alternative in {@link product_htmlParser#voidElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidTag(product_htmlParser.VoidTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SelfClosingTag}
	 * labeled alternative in {@link product_htmlParser#voidElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelfClosingTag(product_htmlParser.SelfClosingTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#containerElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainerElement(product_htmlParser.ContainerElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#openTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpenTag(product_htmlParser.OpenTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#closeTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCloseTag(product_htmlParser.CloseTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StyleAttr}
	 * labeled alternative in {@link product_htmlParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleAttr(product_htmlParser.StyleAttrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NormalAttr}
	 * labeled alternative in {@link product_htmlParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalAttr(product_htmlParser.NormalAttrContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#styleAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleAttribute(product_htmlParser.StyleAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#normalAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalAttribute(product_htmlParser.NormalAttributeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AttrStringValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttrStringValue(product_htmlParser.AttrStringValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AttrJinjaVarValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttrJinjaVarValue(product_htmlParser.AttrJinjaVarValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AttrJinjaBlockValue}
	 * labeled alternative in {@link product_htmlParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttrJinjaBlockValue(product_htmlParser.AttrJinjaBlockValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinja_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinja_var(product_htmlParser.Jinja_varContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExtendsStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExtendsStmt(product_htmlParser.JinjaExtendsStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBlockStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlockStmt(product_htmlParser.JinjaBlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaIfStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaIfStmt(product_htmlParser.JinjaIfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaForStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaForStmt(product_htmlParser.JinjaForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaSetStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaSetStmt(product_htmlParser.JinjaSetStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaIncludeStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaIncludeStmt(product_htmlParser.JinjaIncludeStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaImportStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaImportStmt(product_htmlParser.JinjaImportStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaFromImportStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFromImportStmt(product_htmlParser.JinjaFromImportStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaRawStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaRawStmt(product_htmlParser.JinjaRawStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaMacroStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaMacroStmt(product_htmlParser.JinjaMacroStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaWithStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaWithStmt(product_htmlParser.JinjaWithStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaFilterBlockStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFilterBlockStmt(product_htmlParser.JinjaFilterBlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaGenericStmt}
	 * labeled alternative in {@link product_htmlParser#jinja_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaGenericStmt(product_htmlParser.JinjaGenericStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaExtends}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExtends(product_htmlParser.JinjaExtendsContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlock(product_htmlParser.JinjaBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaIf(product_htmlParser.JinjaIfContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaFor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFor(product_htmlParser.JinjaForContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#forTarget}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForTarget(product_htmlParser.ForTargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaSet(product_htmlParser.JinjaSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaInclude}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaInclude(product_htmlParser.JinjaIncludeContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaImport(product_htmlParser.JinjaImportContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaFromImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFromImport(product_htmlParser.JinjaFromImportContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaImportNames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaImportNames(product_htmlParser.JinjaImportNamesContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaImportName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaImportName(product_htmlParser.JinjaImportNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaRaw}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaRaw(product_htmlParser.JinjaRawContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaMacro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaMacro(product_htmlParser.JinjaMacroContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaMacroParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaMacroParams(product_htmlParser.JinjaMacroParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaMacroParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaMacroParam(product_htmlParser.JinjaMacroParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaWith}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaWith(product_htmlParser.JinjaWithContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaSetExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaSetExpr(product_htmlParser.JinjaSetExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaFilterBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFilterBlock(product_htmlParser.JinjaFilterBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaGenericBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaGenericBlock(product_htmlParser.JinjaGenericBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExpression(product_htmlParser.JinjaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaTernary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaTernary(product_htmlParser.JinjaTernaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaOr(product_htmlParser.JinjaOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAnd(product_htmlParser.JinjaAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaUnaryNot}
	 * labeled alternative in {@link product_htmlParser#jinjaNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaUnaryNot(product_htmlParser.JinjaUnaryNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaCmpExpr}
	 * labeled alternative in {@link product_htmlParser#jinjaNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaCmpExpr(product_htmlParser.JinjaCmpExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaComparison(product_htmlParser.JinjaComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpEq}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpEq(product_htmlParser.OpEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpNeq}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpNeq(product_htmlParser.OpNeqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpLt}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpLt(product_htmlParser.OpLtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpGt}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpGt(product_htmlParser.OpGtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpLte}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpLte(product_htmlParser.OpLteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpGte}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpGte(product_htmlParser.OpGteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpIn}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpIn(product_htmlParser.OpInContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpNotIn}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpNotIn(product_htmlParser.OpNotInContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpIs}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpIs(product_htmlParser.OpIsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OpIsNot}
	 * labeled alternative in {@link product_htmlParser#comparisonOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpIsNot(product_htmlParser.OpIsNotContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaConcat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaConcat(product_htmlParser.JinjaConcatContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaAddSub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAddSub(product_htmlParser.JinjaAddSubContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaMulDiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaMulDiv(product_htmlParser.JinjaMulDivContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFilter(product_htmlParser.JinjaFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaIndex}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaIndex(product_htmlParser.JinjaIndexContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaCall}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaCall(product_htmlParser.JinjaCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaPostfixBase}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaPostfixBase(product_htmlParser.JinjaPostfixBaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaAttr}
	 * labeled alternative in {@link product_htmlParser#jinjaPostfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAttr(product_htmlParser.JinjaAttrContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaCallArgList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaCallArgList(product_htmlParser.JinjaCallArgListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaKwArg}
	 * labeled alternative in {@link product_htmlParser#jinjaCallArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaKwArg(product_htmlParser.JinjaKwArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaPosArg}
	 * labeled alternative in {@link product_htmlParser#jinjaCallArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaPosArg(product_htmlParser.JinjaPosArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaSlice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaSlice(product_htmlParser.JinjaSliceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaNum}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNum(product_htmlParser.JinjaNumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaStr}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaStr(product_htmlParser.JinjaStrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaTrueLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaTrueLit(product_htmlParser.JinjaTrueLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaFalseLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFalseLit(product_htmlParser.JinjaFalseLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaNoneLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNoneLit(product_htmlParser.JinjaNoneLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaNullLit}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNullLit(product_htmlParser.JinjaNullLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaVar}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaVar(product_htmlParser.JinjaVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaParen}
	 * labeled alternative in {@link product_htmlParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaParen(product_htmlParser.JinjaParenContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#jinjaExpressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExpressionList(product_htmlParser.JinjaExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link product_htmlParser#cssStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssRule(product_htmlParser.CssRuleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssAtRuleStmt}
	 * labeled alternative in {@link product_htmlParser#cssStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAtRuleStmt(product_htmlParser.CssAtRuleStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssRuleSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssRuleSet(product_htmlParser.CssRuleSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssSelectorList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelectorList(product_htmlParser.CssSelectorListContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelector(product_htmlParser.CssSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssCompoundSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssCompoundSelector(product_htmlParser.CssCompoundSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssClassSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssClassSelector(product_htmlParser.CssClassSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssAttributeSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAttributeSelector(product_htmlParser.CssAttributeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PseudoClass}
	 * labeled alternative in {@link product_htmlParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPseudoClass(product_htmlParser.PseudoClassContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PseudoElement}
	 * labeled alternative in {@link product_htmlParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPseudoElement(product_htmlParser.PseudoElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssPseudoArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssPseudoArg(product_htmlParser.CssPseudoArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ChildCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChildCombinator(product_htmlParser.ChildCombinatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AdjacentCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdjacentCombinator(product_htmlParser.AdjacentCombinatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GeneralSiblingCombinator}
	 * labeled alternative in {@link product_htmlParser#cssCombinator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGeneralSiblingCombinator(product_htmlParser.GeneralSiblingCombinatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssAtRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAtRule(product_htmlParser.CssAtRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssAtRulePrelude}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAtRulePrelude(product_htmlParser.CssAtRulePreludeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtRuleBlock}
	 * labeled alternative in {@link product_htmlParser#cssAtRuleBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtRuleBlock(product_htmlParser.AtRuleBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtRuleSimple}
	 * labeled alternative in {@link product_htmlParser#cssAtRuleBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtRuleSimple(product_htmlParser.AtRuleSimpleContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssDeclaration(product_htmlParser.CssDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssValueList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssValueList(product_htmlParser.CssValueListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssNumValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssNumValue(product_htmlParser.CssNumValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssHashValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssHashValue(product_htmlParser.CssHashValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssStrValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssStrValue(product_htmlParser.CssStrValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssIdentValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssIdentValue(product_htmlParser.CssIdentValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssFuncValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFuncValue(product_htmlParser.CssFuncValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssJinjaValue}
	 * labeled alternative in {@link product_htmlParser#cssValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssJinjaValue(product_htmlParser.CssJinjaValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunction(product_htmlParser.CssFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssFunctionArgList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionArgList(product_htmlParser.CssFunctionArgListContext ctx);
	/**
	 * Visit a parse tree produced by {@link product_htmlParser#cssFunctionArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionArg(product_htmlParser.CssFunctionArgContext ctx);
}