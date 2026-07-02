// Generated from E:/Compiler/Project Compiler_2/src/antlr/ProductParser.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ProductParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ProductParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ProductParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(ProductParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ImportStmt}
	 * labeled alternative in {@link ProductParser#import_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportStmt(ProductParser.ImportStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FromImportStmt}
	 * labeled alternative in {@link ProductParser#import_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFromImportStmt(ProductParser.FromImportStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#decorator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecorator(ProductParser.DecoratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#module_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_name(ProductParser.Module_nameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleStmt}
	 * labeled alternative in {@link ProductParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleStmt(ProductParser.SimpleStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CompoundStmt}
	 * labeled alternative in {@link ProductParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundStmt(ProductParser.CompoundStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#simple_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_stmt(ProductParser.Simple_stmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncDef}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(ProductParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FlowStmt}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlowStmt(ProductParser.FlowStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TryStmt}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTryStmt(ProductParser.TryStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#func_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_def(ProductParser.Func_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(ProductParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(ProductParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(ProductParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RaiseStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRaiseStmt(ProductParser.RaiseStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#return_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_stmt(ProductParser.Return_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#raise_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRaise_stmt(ProductParser.Raise_stmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GeneratorExpr}
	 * labeled alternative in {@link ProductParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGeneratorExpr(ProductParser.GeneratorExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComparisonExp}
	 * labeled alternative in {@link ProductParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExp(ProductParser.ComparisonExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#comparisonExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpr(ProductParser.ComparisonExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithExpr(ProductParser.ArithExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#call_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall_chain(ProductParser.Call_chainContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(ProductParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AttributeAccess}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeAccess(ProductParser.AttributeAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IndexAccess}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexAccess(ProductParser.IndexAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Identifier}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(ProductParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LiteralAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralAtom(ProductParser.LiteralAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListAtom(ProductParser.ListAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DictAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictAtom(ProductParser.DictAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(ProductParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#generator_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerator_expr(ProductParser.Generator_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#gen_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGen_expr(ProductParser.Gen_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#expr_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_stmt(ProductParser.Expr_stmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TargetID}
	 * labeled alternative in {@link ProductParser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTargetID(ProductParser.TargetIDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TargetCall}
	 * labeled alternative in {@link ProductParser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTargetCall(ProductParser.TargetCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(ProductParser.Arg_listContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprArg}
	 * labeled alternative in {@link ProductParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprArg(ProductParser.ExprArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignArg}
	 * labeled alternative in {@link ProductParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignArg(ProductParser.AssignArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(ProductParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntegerLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(ProductParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(ProductParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(ProductParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NoneLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNoneLiteral(ProductParser.NoneLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#list_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_literal(ProductParser.List_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#dict_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDict_literal(ProductParser.Dict_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(ProductParser.PairContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link ProductParser#flow_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(ProductParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link ProductParser#flow_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(ProductParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProductParser#try_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTry_stmt(ProductParser.Try_stmtContext ctx);
}