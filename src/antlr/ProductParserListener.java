// Generated from D:/combiler-2/Compiler-Project-2/src/antlr/ProductParser.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ProductParser}.
 */
public interface ProductParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ProductParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(ProductParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(ProductParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ImportStmt}
	 * labeled alternative in {@link ProductParser#import_stmt}.
	 * @param ctx the parse tree
	 */
	void enterImportStmt(ProductParser.ImportStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ImportStmt}
	 * labeled alternative in {@link ProductParser#import_stmt}.
	 * @param ctx the parse tree
	 */
	void exitImportStmt(ProductParser.ImportStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FromImportStmt}
	 * labeled alternative in {@link ProductParser#import_stmt}.
	 * @param ctx the parse tree
	 */
	void enterFromImportStmt(ProductParser.FromImportStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FromImportStmt}
	 * labeled alternative in {@link ProductParser#import_stmt}.
	 * @param ctx the parse tree
	 */
	void exitFromImportStmt(ProductParser.FromImportStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#decorator}.
	 * @param ctx the parse tree
	 */
	void enterDecorator(ProductParser.DecoratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#decorator}.
	 * @param ctx the parse tree
	 */
	void exitDecorator(ProductParser.DecoratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#module_name}.
	 * @param ctx the parse tree
	 */
	void enterModule_name(ProductParser.Module_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#module_name}.
	 * @param ctx the parse tree
	 */
	void exitModule_name(ProductParser.Module_nameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleStmt}
	 * labeled alternative in {@link ProductParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSimpleStmt(ProductParser.SimpleStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleStmt}
	 * labeled alternative in {@link ProductParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSimpleStmt(ProductParser.SimpleStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CompoundStmt}
	 * labeled alternative in {@link ProductParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStmt(ProductParser.CompoundStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CompoundStmt}
	 * labeled alternative in {@link ProductParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStmt(ProductParser.CompoundStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimple_stmt(ProductParser.Simple_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimple_stmt(ProductParser.Simple_stmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FuncDef}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(ProductParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FuncDef}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(ProductParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FlowStmt}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterFlowStmt(ProductParser.FlowStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FlowStmt}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitFlowStmt(ProductParser.FlowStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TryStmt}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterTryStmt(ProductParser.TryStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TryStmt}
	 * labeled alternative in {@link ProductParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitTryStmt(ProductParser.TryStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#func_def}.
	 * @param ctx the parse tree
	 */
	void enterFunc_def(ProductParser.Func_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#func_def}.
	 * @param ctx the parse tree
	 */
	void exitFunc_def(ProductParser.Func_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(ProductParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(ProductParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(ProductParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(ProductParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(ProductParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(ProductParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RaiseStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void enterRaiseStmt(ProductParser.RaiseStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RaiseStmt}
	 * labeled alternative in {@link ProductParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void exitRaiseStmt(ProductParser.RaiseStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void enterReturn_stmt(ProductParser.Return_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void exitReturn_stmt(ProductParser.Return_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#raise_stmt}.
	 * @param ctx the parse tree
	 */
	void enterRaise_stmt(ProductParser.Raise_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#raise_stmt}.
	 * @param ctx the parse tree
	 */
	void exitRaise_stmt(ProductParser.Raise_stmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GeneratorExpr}
	 * labeled alternative in {@link ProductParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterGeneratorExpr(ProductParser.GeneratorExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GeneratorExpr}
	 * labeled alternative in {@link ProductParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitGeneratorExpr(ProductParser.GeneratorExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComparisonExp}
	 * labeled alternative in {@link ProductParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterComparisonExp(ProductParser.ComparisonExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComparisonExp}
	 * labeled alternative in {@link ProductParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitComparisonExp(ProductParser.ComparisonExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#comparisonExpr}.
	 * @param ctx the parse tree
	 */
	void enterComparisonExpr(ProductParser.ComparisonExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#comparisonExpr}.
	 * @param ctx the parse tree
	 */
	void exitComparisonExpr(ProductParser.ComparisonExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterArithExpr(ProductParser.ArithExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitArithExpr(ProductParser.ArithExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#call_chain}.
	 * @param ctx the parse tree
	 */
	void enterCall_chain(ProductParser.Call_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#call_chain}.
	 * @param ctx the parse tree
	 */
	void exitCall_chain(ProductParser.Call_chainContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(ProductParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(ProductParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttributeAccess}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 */
	void enterAttributeAccess(ProductParser.AttributeAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttributeAccess}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 */
	void exitAttributeAccess(ProductParser.AttributeAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IndexAccess}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 */
	void enterIndexAccess(ProductParser.IndexAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IndexAccess}
	 * labeled alternative in {@link ProductParser#call_suffix}.
	 * @param ctx the parse tree
	 */
	void exitIndexAccess(ProductParser.IndexAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Identifier}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(ProductParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Identifier}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(ProductParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterLiteralAtom(ProductParser.LiteralAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitLiteralAtom(ProductParser.LiteralAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterListAtom(ProductParser.ListAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitListAtom(ProductParser.ListAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DictAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterDictAtom(ProductParser.DictAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DictAtom}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitDictAtom(ProductParser.DictAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(ProductParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link ProductParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(ProductParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#generator_expr}.
	 * @param ctx the parse tree
	 */
	void enterGenerator_expr(ProductParser.Generator_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#generator_expr}.
	 * @param ctx the parse tree
	 */
	void exitGenerator_expr(ProductParser.Generator_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#gen_expr}.
	 * @param ctx the parse tree
	 */
	void enterGen_expr(ProductParser.Gen_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#gen_expr}.
	 * @param ctx the parse tree
	 */
	void exitGen_expr(ProductParser.Gen_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#expr_stmt}.
	 * @param ctx the parse tree
	 */
	void enterExpr_stmt(ProductParser.Expr_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#expr_stmt}.
	 * @param ctx the parse tree
	 */
	void exitExpr_stmt(ProductParser.Expr_stmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TargetCall}
	 * labeled alternative in {@link ProductParser#target}.
	 * @param ctx the parse tree
	 */
	void enterTargetCall(ProductParser.TargetCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TargetCall}
	 * labeled alternative in {@link ProductParser#target}.
	 * @param ctx the parse tree
	 */
	void exitTargetCall(ProductParser.TargetCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TargetID}
	 * labeled alternative in {@link ProductParser#target}.
	 * @param ctx the parse tree
	 */
	void enterTargetID(ProductParser.TargetIDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TargetID}
	 * labeled alternative in {@link ProductParser#target}.
	 * @param ctx the parse tree
	 */
	void exitTargetID(ProductParser.TargetIDContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void enterArg_list(ProductParser.Arg_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void exitArg_list(ProductParser.Arg_listContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprArg}
	 * labeled alternative in {@link ProductParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterExprArg(ProductParser.ExprArgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprArg}
	 * labeled alternative in {@link ProductParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitExprArg(ProductParser.ExprArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignArg}
	 * labeled alternative in {@link ProductParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterAssignArg(ProductParser.AssignArgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignArg}
	 * labeled alternative in {@link ProductParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitAssignArg(ProductParser.AssignArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(ProductParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(ProductParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IntegerLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(ProductParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntegerLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(ProductParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(ProductParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(ProductParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterBoolLiteral(ProductParser.BoolLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitBoolLiteral(ProductParser.BoolLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NoneLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterNoneLiteral(ProductParser.NoneLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NoneLiteral}
	 * labeled alternative in {@link ProductParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitNoneLiteral(ProductParser.NoneLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#list_literal}.
	 * @param ctx the parse tree
	 */
	void enterList_literal(ProductParser.List_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#list_literal}.
	 * @param ctx the parse tree
	 */
	void exitList_literal(ProductParser.List_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#dict_literal}.
	 * @param ctx the parse tree
	 */
	void enterDict_literal(ProductParser.Dict_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#dict_literal}.
	 * @param ctx the parse tree
	 */
	void exitDict_literal(ProductParser.Dict_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(ProductParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(ProductParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link ProductParser#flow_stmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(ProductParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link ProductParser#flow_stmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(ProductParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link ProductParser#flow_stmt}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(ProductParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link ProductParser#flow_stmt}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(ProductParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProductParser#try_stmt}.
	 * @param ctx the parse tree
	 */
	void enterTry_stmt(ProductParser.Try_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProductParser#try_stmt}.
	 * @param ctx the parse tree
	 */
	void exitTry_stmt(ProductParser.Try_stmtContext ctx);
}