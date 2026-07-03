package Visitor;

import AST.*;
import AST.Arg.Arg;
import AST.Arg.ArgList;
import AST.Arg.AssignArg;
import AST.Arg.ExprArg;
import AST.CompoundStmt.FlowStmt.ForStmt;
import AST.CompoundStmt.FlowStmt.IfStmt;
import AST.CompoundStmt.FlowStmt.TryStmt.TryStmt;
import AST.CompoundStmt.FuncDef;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.CallSuffixes.*;
import AST.Expressions.Expr.ComparisonExpr;
import AST.Expressions.Expr.GeneratorExpr;
import AST.GeneratorExpr.ArithExpr;
import AST.GeneratorExpr.GenExpr;
import AST.Import.FromImportStmt;
import AST.Import.ImportStmt;
import AST.ListDictPair.DictLiteral;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.Pair;
import AST.Literal.*;
import AST.Statements.ExprStmt.ExprStmt;
import AST.Statements.SimpleStmt;
import AST.Statements.SmallStmt.RaiseStmt;
import AST.Statements.SmallStmt.ReturnStmt;
import AST.Target.TargetCall;
import AST.Target.TargetID;
import SymbolTable.SymbolTable;
import SymbolTable.SymbolTable.Symbol;
import SymbolTable.SymbolTable.Symbol.Kind;
import antlr.ProductLexer;
import antlr.ProductParser;
import antlr.ProductParserBaseVisitor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;


public class PythonVisitor extends ProductParserBaseVisitor<ASTNode> {

    // ── Symbol table (stack-based) ───────────────────────────────────────────
    private final SymbolTable symbolTable = new SymbolTable();

    public SymbolTable getSymbolTable() { return symbolTable; }

    // ═══════════════════════════════════════════════════════════════════════
    //  program
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitProgram(ProductParser.ProgramContext ctx) {
        List<ASTNode> elements = new ArrayList<>();

        for (ProductParser.Import_stmtContext imp : ctx.import_stmt()) {
            ASTNode node = visit(imp);
            if (node != null) elements.add(node);
        }
        for (ProductParser.StatementContext stmt : ctx.statement()) {
            ASTNode node = visit(stmt);
            if (node != null) elements.add(node);
        }
        return new Program(elements, 1);
    }

    @Override
    public ASTNode visit(ParseTree tree) { return super.visit(tree); }

    // ═══════════════════════════════════════════════════════════════════════
    //  import_stmt  ── fills symbol table
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitImportStmt(ProductParser.ImportStmtContext ctx) {
        List<String> modules = new ArrayList<>();
        for (ProductParser.Module_nameContext m : ctx.module_name()) {
            String mod = moduleNameStr(m);
            modules.add(mod);
            // insert: name=module, kind=IMPORT, type=MODULE, value=null
            symbolTable.insert(mod, Kind.IMPORT, "MODULE", null, m.getStart().getLine());
        }
        return new ImportStmt(modules, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitFromImportStmt(ProductParser.FromImportStmtContext ctx) {
        String module = moduleNameStr(ctx.module_name());
        List<String> ids = new ArrayList<>();
        for (TerminalNode id : ctx.ID()) {
            String name = id.getText();
            ids.add(name);
            // insert: each imported name, kind=IMPORT, type=from <module>
            symbolTable.insert(name, Kind.IMPORT, "from " + module, null, id.getSymbol().getLine());
        }
        return new FromImportStmt(module, ids, ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  statement delegates
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitSimpleStmt(ProductParser.SimpleStmtContext ctx)   { return visit(ctx.simple_stmt()); }

    @Override
    public ASTNode visitCompoundStmt(ProductParser.CompoundStmtContext ctx) { return visit(ctx.compound_stmt()); }

    @Override
    public ASTNode visitSimple_stmt(ProductParser.Simple_stmtContext ctx) {
        ASTNode inner = visit(ctx.small_stmt());
        return new SimpleStmt(inner, ctx.getStart().getLine());
    }

    // ─── compound_stmt ───────────────────────────────────────────────────
    @Override
    public ASTNode visitFuncDef(ProductParser.FuncDefContext ctx)   { return visit(ctx.func_def()); }
    @Override
    public ASTNode visitFlowStmt(ProductParser.FlowStmtContext ctx) { return visit(ctx.flow_stmt()); }
    @Override
    public ASTNode visitTryStmt(ProductParser.TryStmtContext ctx)   { return visit(ctx.try_stmt()); }

    // ═══════════════════════════════════════════════════════════════════════
    //  func_def  ── push/pop scope, insert function + parameters
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitFunc_def(ProductParser.Func_defContext ctx) {
        List<Decorator> decorators = new ArrayList<>();
        for (ProductParser.DecoratorContext d : ctx.decorator()) {
            decorators.add((Decorator) visit(d));
        }

        String funcName = ctx.ID().getText();
        int    line     = ctx.getStart().getLine();

        // ── insert function symbol in the CURRENT (outer) scope ──────────
        symbolTable.insert(funcName, Kind.FUNCTION, "FUNCTION", null, line);

        // ──  Dynamic Route Detection (Flask Specific Semantic) ─────────
        for (Decorator d : decorators) {
            if (d.name != null && d.name.getParts().size() >= 2) {
                String decoratorAction = d.name.getParts().get(d.name.getParts().size() - 1);
                if ("route".equals(decoratorAction)) {
                    Symbol funcSym = symbolTable.lookup(funcName);
                    if (funcSym != null) {
                        funcSym.setKind(Kind.ROUTE_FUNCTION); // تغيير النوع


                        if (d.args != null && !d.args.args.isEmpty()) {
                            Arg firstArg = d.args.args.get(0);
                            if (firstArg instanceof ExprArg) {
                                ASTNode expr = ((ExprArg) firstArg).expr;
                                String routePath = null;

                                // 🚀 Unwrap الـ AST عشان نوصل للنص الحقيقي
                                if (expr instanceof StringLiteral) {
                                    routePath = ((StringLiteral) expr).value;
                                } else if (expr instanceof CallChainExpr) {
                                    ASTNode base = ((CallChainExpr) expr).base;
                                    if (base instanceof StringLiteral) {
                                        routePath = ((StringLiteral) base).value;
                                    }
                                }

                                if (routePath != null) {
                                    funcSym.setValue(routePath);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        // ── open a new scope for this function ───────────────────────────
        symbolTable.pushScope(funcName);
        // ── parameters ───────────────────────────────────────────────────
        Parameters params = ctx.parameters() != null
                ? (Parameters) visit(ctx.parameters())
                : new Parameters(new ArrayList<>(), line);

        // ── body ─────────────────────────────────────────────────────────
        List<ASTNode> body = new ArrayList<>();
        for (ProductParser.StatementContext s : ctx.statement()) {
            ASTNode n = visit(s);
            if (n != null) body.add(n);
        }

        // ── close scope ──────────────────────────────────────────────────
        symbolTable.popScope();

        return new FuncDef(funcName, params, decorators, body, line);
    }

    @Override
    public ASTNode visitDecorator(ProductParser.DecoratorContext ctx) {
        ModuleName mn   = (ModuleName) visit(ctx.module_name());
        ArgList    args = ctx.arg_list() != null ? (ArgList) visit(ctx.arg_list()) : null;
        return new Decorator(mn, args, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitModule_name(ProductParser.Module_nameContext ctx) {
        List<String> parts = new ArrayList<>();
        for (TerminalNode id : ctx.ID()) parts.add(id.getText());
        return new ModuleName(parts, ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  parameters  ── insert each param as PARAMETER
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitParameters(ProductParser.ParametersContext ctx) {
        List<String> names = new ArrayList<>();

        // نمر على الأبناء بشكل متسلسل عشان نلتقط Type Hints (مثل pid: int)
        for (int i = 0; i < ctx.children.size(); i++) {
            ParseTree child = ctx.children.get(i);

            if (child instanceof TerminalNode) {
                int tokenType = ((TerminalNode) child).getSymbol().getType();

                // إذا كان الابن حرف عادي (اسم المتغير)
                if (tokenType == ProductLexer.ID) {
                    String paramName = child.getText();
                    names.add(paramName);

                    String inferredType = "UNKNOWN";


                    if (i + 1 < ctx.children.size() && ctx.children.get(i + 1).getText().equals(":")) {

                        if (i + 2 < ctx.children.size()) {
                            String typeHint = ctx.children.get(i + 2).getText();
                            inferredType = typeHint.toUpperCase();
                        }
                    }

                    symbolTable.insert(paramName, Kind.PARAMETER, inferredType, null, ((TerminalNode) child).getSymbol().getLine());
                }
            }
        }
        return new Parameters(names, ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  small_stmt
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitReturnStmt(ProductParser.ReturnStmtContext ctx) { return visit(ctx.return_stmt()); }
    @Override
    public ASTNode visitExprStmt(ProductParser.ExprStmtContext ctx)     { return visit(ctx.expr_stmt()); }
    @Override
    public ASTNode visitRaiseStmt(ProductParser.RaiseStmtContext ctx)   { return visit(ctx.raise_stmt()); }

    @Override
    public ASTNode visitReturn_stmt(ProductParser.Return_stmtContext ctx) {
        ASTNode expr = ctx.expr() != null ? visit(ctx.expr()) : null;

        // ── اكتشف render_template ──
        if (expr instanceof CallChainExpr) {
            tryRegisterRenderTemplate((CallChainExpr) expr, ctx.getStart().getLine());
        }

        return new ReturnStmt(expr, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitRaise_stmt(ProductParser.Raise_stmtContext ctx) {
        ASTNode expr = ctx.expr() != null ? visit(ctx.expr()) : null;
        return new RaiseStmt(expr, ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  expr_stmt  ── detect assignments and insert/update symbol table
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitExpr_stmt(ProductParser.Expr_stmtContext ctx) {
        ASTNode target = visit(ctx.target());
        ASTNode value  = ctx.expr() != null ? visit(ctx.expr()) : null;
        if (value instanceof CallChainExpr) {
            tryRegisterRenderTemplate((CallChainExpr) value, ctx.getStart().getLine());
        }

        if (value != null) {
            String varName = extractVarName(target);
            if (varName != null) {
                String inferredType = inferType(value);
                Object inferredValue = inferValue(value);
                int line = ctx.getStart().getLine();

                if (symbolTable.lookup(varName) == null) {
                    symbolTable.insert(varName, Kind.VARIABLE, inferredType, inferredValue, line);
                } else {
                    symbolTable.update(varName, inferredValue);
                    symbolTable.updateType(varName, inferredType);
                }
            }
        }

        return new ExprStmt(target, value, ctx.getStart().getLine());
    }

    private String extractVarName(ASTNode target) {
        if (target instanceof TargetID) {
            return ((TargetID) target).name;
        }
        if (target instanceof TargetCall) {
            ASTNode call = ((TargetCall) target).callChain;  // تأكد من اسم الحقل في كلاس TargetCall
            if (call instanceof CallChainExpr) {
                CallChainExpr cc = (CallChainExpr) call;
                if (cc.base instanceof Identifier) {
                    return ((Identifier) cc.base).name;
                }
            }
        }
        return null;
    }

    @Override
    public ASTNode visitTargetID(ProductParser.TargetIDContext ctx) {
        return new TargetID(ctx.ID().getText(), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitTargetCall(ProductParser.TargetCallContext ctx) {
        return new TargetCall(visit(ctx.call_chain()), ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  flow_stmt  ── for-loop variable
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitIfStmt(ProductParser.IfStmtContext ctx) {
        ASTNode cond = visit(ctx.expr());
        List<ASTNode> body = new ArrayList<>();
        for (ProductParser.StatementContext s : ctx.statement()) {
            ASTNode n = visit(s);
            if (n != null) body.add(n);
        }
        return new IfStmt(cond, body, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitForStmt(ProductParser.ForStmtContext ctx) {
        String    var      = ctx.ID().getText();
        ASTNode   iterable = visit(ctx.expr());
        int       line     = ctx.getStart().getLine();

        // insert loop variable
        if (symbolTable.lookup(var) == null) {
            symbolTable.insert(var, Kind.VARIABLE, "UNKNOWN", null, line);
        }

        List<ASTNode> body = new ArrayList<>();
        for (ProductParser.StatementContext s : ctx.statement()) {
            ASTNode n = visit(s);
            if (n != null) body.add(n);
        }
        return new ForStmt(var, iterable, body, line);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  try_stmt
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitTry_stmt(ProductParser.Try_stmtContext ctx) {
        List<ASTNode>            tryBlock     = new ArrayList<>();
        List<TryStmt.CatchBlock> catches      = new ArrayList<>();
        List<ASTNode>            finallyBlock = new ArrayList<>();

        String        currentSection = "try";
        String        currentExcName = null;
        List<ASTNode> currentBody    = tryBlock;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            String    text  = child.getText();

            if (child instanceof TerminalNode) {
                int type = ((TerminalNode) child).getSymbol().getType();
                if (type == ProductLexer.EXCEPT) {
                    if (!currentSection.equals("try"))
                        catches.add(new TryStmt.CatchBlock(currentExcName, currentBody));
                    currentSection = "except";
                    currentExcName = null;
                    currentBody    = new ArrayList<>();
                } else if (type == ProductLexer.FINALLY) {
                    if (currentSection.equals("except"))
                        catches.add(new TryStmt.CatchBlock(currentExcName, currentBody));
                    currentSection = "finally";
                    currentBody    = finallyBlock;
                } else if (type == ProductLexer.ID
                        && currentSection.equals("except")) {
                    if (currentExcName == null) {
                        currentExcName = text;
                        symbolTable.insert(currentExcName, Kind.VARIABLE, "EXCEPTION", null,
                                ((TerminalNode) child).getSymbol().getLine());
                    } else {
                        symbolTable.insert(text, Kind.VARIABLE, "EXCEPTION", null,
                                ((TerminalNode) child).getSymbol().getLine());
                    }
                }
            } else if (child instanceof ProductParser.StatementContext) {
                ASTNode n = visit((ProductParser.StatementContext) child);
                if (n != null) currentBody.add(n);
            }
        }
        if (currentSection.equals("except"))
            catches.add(new TryStmt.CatchBlock(currentExcName, currentBody));

        return new TryStmt(tryBlock, catches, finallyBlock, ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  expr
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public ASTNode visitGeneratorExpr(ProductParser.GeneratorExprContext ctx) { return visit(ctx.generator_expr()); }

    @Override
    public ASTNode visitComparisonExp(ProductParser.ComparisonExpContext ctx) { return visit(ctx.comparisonExpr()); }

    @Override
    public ASTNode visitComparisonExpr(ProductParser.ComparisonExprContext ctx) {
        List<ProductParser.ArithExprContext> arithList = ctx.arithExpr();
        ASTNode       first = visit(arithList.get(0));
        List<String>  ops   = new ArrayList<>();
        List<ASTNode> rest  = new ArrayList<>();
        for (int i = 1; i < arithList.size(); i++) {
            Token opTok = findOpBetween(ctx, i - 1, i);
            ops.add(opTok != null ? opTok.getText() : "?");
            rest.add(visit(arithList.get(i)));
        }
        if (rest.isEmpty()) return first;
        return new ComparisonExpr(first, ops, rest, ctx.getStart().getLine());
    }

    private Token findOpBetween(ProductParser.ComparisonExprContext ctx, int bIdx, int aIdx) {
        int arithSeen = 0;
        for (int c = 0; c < ctx.getChildCount(); c++) {
            ParseTree child = ctx.getChild(c);
            if (child instanceof ProductParser.ArithExprContext) {
                arithSeen++;
                if (arithSeen == bIdx + 1 && c + 1 < ctx.getChildCount()) {
                    ParseTree op = ctx.getChild(c + 1);
                    if (op instanceof TerminalNode) return ((TerminalNode) op).getSymbol();
                }
            }
        }
        return null;
    }

    @Override
    public ASTNode visitArithExpr(ProductParser.ArithExprContext ctx) {
        List<ASTNode> terms = new ArrayList<>();
        List<String>  ops   = new ArrayList<>();
        for (int c = 0; c < ctx.getChildCount(); c++) {
            ParseTree child = ctx.getChild(c);
            if (child instanceof ProductParser.Call_chainContext) {
                terms.add(visit((ProductParser.Call_chainContext) child));
            } else if (child instanceof TerminalNode) {
                ops.add(child.getText());
            }
        }
        if (terms.size() == 1) return terms.get(0);
        return new ArithExpr(terms, ops, ctx.getStart().getLine());
    }

    // ─── call_chain ──────────────────────────────────────────────────────
    @Override
    public ASTNode visitCall_chain(ProductParser.Call_chainContext ctx) {
        ASTNode base = visit(ctx.atom());
        List<CallSuffix> suffixes = new ArrayList<>();
        for (ProductParser.Call_suffixContext s : ctx.call_suffix())
            suffixes.add((CallSuffix) visit(s));
        return new CallChainExpr(base, suffixes, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitFunctionCall(ProductParser.FunctionCallContext ctx) {
        ArgList args = ctx.arg_list() != null ? (ArgList) visit(ctx.arg_list()) : null;
        return new FunctionCall(args, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitAttributeAccess(ProductParser.AttributeAccessContext ctx) {
        return new AttributeAccess(ctx.ID().getText(), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitIndexAccess(ProductParser.IndexAccessContext ctx) {
        return new IndexAccess(visit(ctx.expr()), ctx.getStart().getLine());
    }

    // ─── atom ────────────────────────────────────────────────────────────
    @Override
    public ASTNode visitIdentifier(ProductParser.IdentifierContext ctx) {
        return new Identifier(ctx.ID().getText(), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitLiteralAtom(ProductParser.LiteralAtomContext ctx) { return visit(ctx.literal()); }

    @Override
    public ASTNode visitListAtom(ProductParser.ListAtomContext ctx) {
        return new ListAtom(visit(ctx.list_literal()), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitDictAtom(ProductParser.DictAtomContext ctx) {
        return new DictAtom(visit(ctx.dict_literal()), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitParenExpr(ProductParser.ParenExprContext ctx) {
        return new ParenExpr(visit(ctx.expr()), ctx.getStart().getLine());
    }

    // ─── generator_expr ──────────────────────────────────────────────────
    @Override
    public ASTNode visitGenerator_expr(ProductParser.Generator_exprContext ctx) {
        return new GeneratorExpr((GenExpr) visit(ctx.gen_expr()), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitGen_expr(ProductParser.Gen_exprContext ctx) {
        List<ProductParser.ExprContext> exprs = ctx.expr();
        ASTNode expr     = visit(exprs.get(0));
        String  var      = ctx.ID().getText();
        ASTNode iterable = visit(exprs.get(1));
        ASTNode cond     = exprs.size() > 2 ? visit(exprs.get(2)) : null;

        symbolTable.insert(var, Kind.VARIABLE, "UNKNOWN", null, ctx.ID().getSymbol().getLine());

        return new GenExpr(expr, var, iterable, cond, ctx.getStart().getLine());
    }
    // ─── arg_list / arg ──────────────────────────────────────────────────
    @Override
    public ASTNode visitArg_list(ProductParser.Arg_listContext ctx) {
        List<Arg> args = new ArrayList<>();
        for (ProductParser.ArgContext a : ctx.arg()) args.add((Arg) visit(a));
        return new ArgList(args, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitExprArg(ProductParser.ExprArgContext ctx) {
        return new ExprArg(visit(ctx.expr()), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitAssignArg(ProductParser.AssignArgContext ctx) {
        return new AssignArg(ctx.ID().getText(), visit(ctx.expr()), ctx.getStart().getLine());
    }

    // ─── literals ────────────────────────────────────────────────────────
    @Override
    public ASTNode visitStringLiteral(ProductParser.StringLiteralContext ctx) {
        String raw = ctx.STRING().getText();
        raw = raw.substring(1, raw.length()-1);
        return new StringLiteral(raw, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitIntegerLiteral(ProductParser.IntegerLiteralContext ctx) {
        return new IntegerLiteral(Integer.parseInt(ctx.INT().getText()), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitFloatLiteral(ProductParser.FloatLiteralContext ctx) {
        return new FloatLiteral(Double.parseDouble(ctx.FLOAT().getText()), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitBoolLiteral(ProductParser.BoolLiteralContext ctx) {
        return new BoolLiteral(ctx.getText().equals("True"), ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitNoneLiteral(ProductParser.NoneLiteralContext ctx) {
        return new NoneLiteral(ctx.getStart().getLine());
    }

    // ─── containers ──────────────────────────────────────────────────────
    @Override
    public ASTNode visitList_literal(ProductParser.List_literalContext ctx) {
        for (int i = 0; i < ctx.children.size(); i++) {
            ParseTree child = ctx.children.get(i);
            if (child instanceof TerminalNode) {
                if (((TerminalNode) child).getSymbol().getType() == ProductLexer.FOR) {
                    if (i + 1 < ctx.children.size() && ctx.children.get(i + 1) instanceof TerminalNode) {
                        TerminalNode varNode = (TerminalNode) ctx.children.get(i + 1);
                        symbolTable.insert(varNode.getText(), Kind.VARIABLE, "UNKNOWN", null, varNode.getSymbol().getLine());
                    }
                    break;
                }
            }
        }
        List<ASTNode> elements = new ArrayList<>();
        for (ProductParser.ExprContext e : ctx.expr()) elements.add(visit(e));
        return new ListLiteral(elements, ctx.getStart().getLine());
    }

    @Override
    public ASTNode visitDict_literal(ProductParser.Dict_literalContext ctx) {
        List<Pair> pairs = new ArrayList<>();
        for (ProductParser.PairContext p : ctx.pair()) pairs.add((Pair) visit(p));
        return new DictLiteral(pairs, ctx.getStart().getLine());
    }
    private void tryRegisterRenderTemplate(CallChainExpr callExpr, int line) {
        // لازم base يكون render_template
        if (!(callExpr.base instanceof Identifier)) return;
        if (!"render_template".equals(((Identifier) callExpr.base).name)) return;
        if (callExpr.suffixes.isEmpty()) return;

        CallSuffix first = callExpr.suffixes.get(0);
        if (!(first instanceof FunctionCall)) return;

        FunctionCall fc = (FunctionCall) first;
        if (fc.args == null || fc.args.args.isEmpty()) return;

        // ── اسم القالب (أول arg) ──
// ── اسم القالب (أول arg) ──
        String templateName = null;
        Arg firstArg = fc.args.args.get(0);
        if (firstArg instanceof ExprArg) {
            ASTNode argExpr = ((ExprArg) firstArg).expr;
            // unwrap CallChainExpr → StringLiteral
            if (argExpr instanceof CallChainExpr) {
                ASTNode base = ((CallChainExpr) argExpr).base;
                if (base instanceof StringLiteral) templateName = ((StringLiteral) base).value;
            } else if (argExpr instanceof StringLiteral) {
                templateName = ((StringLiteral) argExpr).value;
            }
        }

        if (templateName == null) return;

        // ── المتغيرات الممررة (AssignArg مثل products=products) ──
        List<String> passedVars = new ArrayList<>();
        for (int i = 1; i < fc.args.args.size(); i++) {
            Arg arg = fc.args.args.get(i);
            if (arg instanceof AssignArg) {
                passedVars.add(((AssignArg) arg).name);
            }
        }

        // ── تخزين في الـ Symbol Table ──
        String key = "render_" + templateName;
        if (symbolTable.lookup(key) == null) {
            Symbol sym = new Symbol(key, templateName, passedVars, line);
            symbolTable.insert(sym);
            System.out.println("[TEMPLATE] " + templateName + " → vars: " + passedVars);
        }
    }
    @Override
    public ASTNode visitPair(ProductParser.PairContext ctx) {
        return new Pair(ctx.STRING().getText(), visit(ctx.expr()), ctx.getStart().getLine());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Type / Value inference helpers
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Tries to infer a type string from a visited AST node.
     * Returns "UNKNOWN" when the type cannot be statically determined.
     */
    private String inferType(ASTNode node) {
        if (node == null) return "UNKNOWN";

        if (node instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) node;
            if (cc.suffixes == null || cc.suffixes.isEmpty())
                return inferType(cc.base);

            // ── تحقق من آخر suffix ──
            CallSuffix lastSuffix = cc.suffixes.get(cc.suffixes.size() - 1);

            // إذا آخر حاجة .strip() أو .get() → STRING
            if (lastSuffix instanceof AttributeAccess) {
                String attr = ((AttributeAccess) lastSuffix).attribute;
                if ("strip".equals(attr) || "get".equals(attr) || "lower".equals(attr)
                        || "upper".equals(attr) || "format".equals(attr)) return "STRING";
            }

            // إذا آخر حاجة FunctionCall وقبله attribute access
            if (lastSuffix instanceof FunctionCall && cc.suffixes.size() >= 2) {
                CallSuffix prev = cc.suffixes.get(cc.suffixes.size() - 2);
                if (prev instanceof AttributeAccess) {
                    String attr = ((AttributeAccess) prev).attribute;
                    if ("strip".equals(attr) || "get".equals(attr)) return "STRING";
                    if ("append".equals(attr) || "pop".equals(attr)) return "NONE";
                }
            }

            // دوال معروفة في الـ base
            if (cc.base instanceof Identifier) {
                String name = ((Identifier) cc.base).name;
                if ("int".equals(name))   return "INT";
                if ("float".equals(name)) return "FLOAT";
                if ("str".equals(name))   return "STRING";
                if ("bool".equals(name))  return "BOOL";
                if ("list".equals(name))  return "LIST";
                if ("dict".equals(name))  return "DICT";
                if ("Flask".equals(name)) return "FLASK_APP";
            }
            return "UNKNOWN";
        }

        if (node instanceof IntegerLiteral) return "INT";
        if (node instanceof FloatLiteral)   return "FLOAT";
        if (node instanceof StringLiteral)  return "STRING";
        if (node instanceof BoolLiteral)    return "BOOL";
        if (node instanceof NoneLiteral)    return "NONE";
        if (node instanceof ListLiteral || node instanceof ListAtom) return "LIST";
        if (node instanceof DictLiteral || node instanceof DictAtom) return "DICT";
        if (node instanceof ArithExpr)      return "INT";

        return "UNKNOWN";
    }

    /**
     * Tries to extract a literal value for simple cases; returns null otherwise.
     */
    private Object inferValue(ASTNode node) {
        if (node == null) return null;

        if (node instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) node;
            if (cc.suffixes == null || cc.suffixes.isEmpty())
                return inferValue(cc.base);
            return null;
        }
        if (node instanceof IntegerLiteral) return ((IntegerLiteral) node).value;
        if (node instanceof FloatLiteral)   return ((FloatLiteral)   node).value;
        if (node instanceof StringLiteral)  return ((StringLiteral)  node).value;
        if (node instanceof BoolLiteral)    return ((BoolLiteral)    node).value;
        if (node instanceof NoneLiteral)    return "None";
        if (node instanceof ListLiteral)    return "[…]";
        if (node instanceof DictLiteral || node instanceof DictAtom) return "{…}";
        if (node instanceof ListAtom)       return "[…]";

        return null;
    }
    private void registerRenderTemplate(CallChainExpr callExpr, int line) {
        if (!(callExpr.base instanceof Identifier)) return;

        Identifier id = (Identifier) callExpr.base;
        if (!"render_template".equals(id.name)) return;

        // استخراج اسم القالب (أول argument)
        String templateName = null;
        List<String> passedVars = new ArrayList<>();

        // نفترض أن أول Arg هو اسم القالب (StringLiteral)
        if (!callExpr.suffixes.isEmpty()) {
            CallSuffix suffix = callExpr.suffixes.get(0);
            if (suffix instanceof FunctionCall) {
                FunctionCall fc = (FunctionCall) suffix;
                if (fc.args != null && !fc.args.args.isEmpty()) {
                    Arg firstArg = fc.args.args.get(0);
                    if (firstArg instanceof ExprArg) {
                        ASTNode expr = ((ExprArg) firstArg).expr;
                        if (expr instanceof StringLiteral) {
                            templateName = ((StringLiteral) expr).value;
                        }
                    }
                }
            }
        }

        if (templateName != null) {
            // جمع المتغيرات الممررة (AssignArg مثل products=products)
            // ... (يمكن توسيعه لاحقاً)
            symbolTable.insert(new Symbol("render_" + templateName, templateName, passedVars, line));
            System.out.println("[INFO] Registered Template: " + templateName + " with vars: " + passedVars);
        }
    }
    // ─── helper ──────────────────────────────────────────────────────────
    private String moduleNameStr(ProductParser.Module_nameContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (TerminalNode id : ctx.ID()) {
            if (sb.length() > 0) sb.append(".");
            sb.append(id.getText());
        }
        return sb.toString();
    }

}