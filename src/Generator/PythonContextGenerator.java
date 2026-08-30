package Generator;

import AST.ASTNode;
import AST.Arg.Arg;
import AST.Arg.AssignArg;
import AST.Arg.ExprArg;
import AST.CompoundStmt.FlowStmt.ForStmt;
import AST.CompoundStmt.FlowStmt.IfStmt;
import AST.CompoundStmt.FlowStmt.TryStmt.TryStmt;
import AST.CompoundStmt.FuncDef;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.CallSuffixes.CallChainExpr;
import AST.Expressions.CallSuffixes.FunctionCall;
import AST.Expressions.Expr.GeneratorExpr;
import AST.GeneratorExpr.GenExpr;
import AST.Literal.StringLiteral;
import AST.Program;
import AST.Statements.ExprStmt.ExprStmt;
import AST.Statements.SimpleStmt;
import AST.Statements.SmallStmt.ReturnStmt;
import AST.Target.TargetID;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PythonContextGenerator {


    private final Map<String, Object> globalVariables = new LinkedHashMap<>();
    private final Map<String, List<Map<String, Object>>> templateContexts = new LinkedHashMap<>();
    private final List<String> log = new ArrayList<>();


    private final Map<String, String> singleItemFromList = new LinkedHashMap<>();


    private final Map<String, String> routes = new LinkedHashMap<>();


    private final Map<String, String> endpointToTemplate = new LinkedHashMap<>();

    private Program rootNode;

    private LiteralEvaluator evaluator;

    public Map<String, Object> getGlobalVariables() { return globalVariables; }
    public Map<String, List<Map<String, Object>>> getTemplateContexts() { return templateContexts; }
    public Map<String, String> getRoutes() { return routes; }
    public Map<String, String> getEndpointToTemplate() { return endpointToTemplate; }
    public List<String> getLog() { return log; }
    public String getAstTreeString() { return rootNode != null ? rootNode.toString(0) : ""; }


    public List<Map<String, Object>> getContextFor(String templateName) {
        List<Map<String, Object>> ctx = templateContexts.get(templateName);
        if (ctx == null) {
            log.add("[WARNING] لا يوجد Context مسجل للقالب: " + templateName);
            return new ArrayList<>();
        }
        return ctx;
    }

    public void generate(Program root) {
        this.rootNode = root;
        evaluator = new LiteralEvaluator(globalVariables);

        collectTopLevelVariables(root.elements);
        collectRoutes(root.elements);
        collectRenderCalls(root.elements);

        log.add("[DONE] تم استخراج " + globalVariables.size() + " متغير عام و"
                + templateContexts.size() + " قالب مرتبط بـ render_template.");
    }

    private void collectTopLevelVariables(List<ASTNode> elements) {
        for (ASTNode el : elements) {
            ExprStmt exprStmt = unwrapExprStmt(el);
            if (exprStmt == null || exprStmt.value == null) continue;
            if (!(exprStmt.target instanceof TargetID)) continue;

            String varName = ((TargetID) exprStmt.target).name;
            Object value = evaluator.evaluate(exprStmt.value);
            globalVariables.put(varName, value);

            log.add("[EXTRACT] المتغير '" + varName + "' من سطر "
                    + exprStmt.getLineNumber() + " — القيمة: " + preview(value));
        }
    }

    private void collectRenderCalls(List<ASTNode> elements) {
        for (ASTNode el : elements) {
            if (el instanceof FuncDef) {
                FuncDef fd = (FuncDef) el;
                inspectBody(fd.body, fd.name);
            }
        }
    }

    private void collectRoutes(List<ASTNode> elements) {
        for (ASTNode el : elements) {
            if (!(el instanceof FuncDef)) continue;
            FuncDef fd = (FuncDef) el;

            for (AST.Decorator d : fd.decorators) {
                if (d.name == null || d.name.getParts().isEmpty()) continue;

                String lastPart = d.name.getParts().get(d.name.getParts().size() - 1);
                if (!"route".equals(lastPart)) continue;
                if (d.args == null || d.args.args.isEmpty()) continue;

                Arg firstArg = d.args.args.get(0);
                if (!(firstArg instanceof ExprArg)) continue;

                String routePath = extractStringValue(((ExprArg) firstArg).expr);
                if (routePath != null) {
                    routes.put(fd.name, routePath);
                    log.add("[ROUTE] '" + fd.name + "' → " + routePath);
                }
            }
        }
    }


    private String extractStringValue(ASTNode expr) {
        if (expr instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) expr;
            if (cc.suffixes.isEmpty()) expr = cc.base;
        }
        if (expr instanceof StringLiteral) return ((StringLiteral) expr).value;
        return null;
    }

    private void inspectBody(List<ASTNode> body, String endpointName) {
        if (body == null) return;
        for (ASTNode stmt : body) {


            ExprStmt assign = unwrapExprStmt(stmt);
            if (assign != null && assign.target instanceof TargetID && assign.value != null) {
                String sourceList = detectSingleItemSource(assign.value);
                if (sourceList != null) {
                    String localVarName = ((TargetID) assign.target).name;
                    singleItemFromList.put(localVarName, sourceList);
                    log.add("[DETECT] المتغير '" + localVarName
                            + "' مشتق من عنصر واحد بقائمة '" + sourceList + "'");
                }
            }

            inspectStatementForRender(stmt, endpointName);

            if (stmt instanceof IfStmt) {
                inspectBody(((IfStmt) stmt).body, endpointName);
            } else if (stmt instanceof ForStmt) {
                inspectBody(((ForStmt) stmt).body, endpointName);
            } else if (stmt instanceof TryStmt) {
                TryStmt t = (TryStmt) stmt;
                inspectBody(t.tryBlock, endpointName);
                for (TryStmt.CatchBlock cb : t.catches) inspectBody(cb.body, endpointName);
                inspectBody(t.finallyBlock, endpointName);
            }
        }
    }


    private String detectSingleItemSource(ASTNode valueNode) {
        if (!(valueNode instanceof CallChainExpr)) return null;
        CallChainExpr cc = (CallChainExpr) valueNode;

        if (cc.suffixes.isEmpty() || !(cc.suffixes.get(0) instanceof FunctionCall)) return null;
        FunctionCall fc = (FunctionCall) cc.suffixes.get(0);
        if (fc.args == null) return null;

        for (Arg arg : fc.args.args) {
            ASTNode expr = (arg instanceof ExprArg) ? ((ExprArg) arg).expr : null;
            if (expr instanceof GeneratorExpr) {
                GenExpr gen = ((GeneratorExpr) expr).genExpr;
                if (gen.iterable instanceof CallChainExpr) {
                    CallChainExpr iterCc = (CallChainExpr) gen.iterable;
                    if (iterCc.suffixes.isEmpty() && iterCc.base instanceof Identifier) {
                        String listName = ((Identifier) iterCc.base).name;
                        if (globalVariables.get(listName) instanceof List) return listName;
                    }
                }
            }
        }
        return null;
    }


    private void inspectStatementForRender(ASTNode stmt, String endpointName) {
        ReturnStmt returnStmt = unwrapReturnStmt(stmt);
        if (returnStmt == null || returnStmt.value == null) return;
        if (!(returnStmt.value instanceof CallChainExpr)) return;

        CallChainExpr call = (CallChainExpr) returnStmt.value;
        if (!(call.base instanceof Identifier)) return;
        if (!"render_template".equals(((Identifier) call.base).name)) return;
        if (call.suffixes.isEmpty() || !(call.suffixes.get(0) instanceof FunctionCall)) return;

        FunctionCall fc = (FunctionCall) call.suffixes.get(0);
        if (fc.args == null || fc.args.args.isEmpty()) return;

        String templateName = extractTemplateName(fc.args.args.get(0));
        if (templateName == null) return;

        if (endpointName != null) {
            endpointToTemplate.put(endpointName, templateName);
        }

        Map<String, Object> staticArgs = new LinkedHashMap<>();
        String dynamicArgName = null;
        String dynamicSourceList = null;

        for (int i = 1; i < fc.args.args.size(); i++) {
            Arg arg = fc.args.args.get(i);
            if (!(arg instanceof AssignArg)) continue;
            AssignArg aa = (AssignArg) arg;

            if (aa.value instanceof CallChainExpr) {
                CallChainExpr vcc = (CallChainExpr) aa.value;
                if (vcc.suffixes.isEmpty() && vcc.base instanceof Identifier) {
                    String refName = ((Identifier) vcc.base).name;
                    if (singleItemFromList.containsKey(refName)) {
                        dynamicArgName = aa.name;
                        dynamicSourceList = singleItemFromList.get(refName);
                        continue;
                    }
                }
            }
            staticArgs.put(aa.name, evaluator.evaluate(aa.value));
        }

        List<Map<String, Object>> variants = templateContexts
                .computeIfAbsent(templateName, k -> new ArrayList<>());

        if (dynamicArgName != null) {
            List<?> sourceList = (List<?>) globalVariables.get(dynamicSourceList);
            for (Object item : sourceList) {
                Map<String, Object> ctx = new LinkedHashMap<>(staticArgs);
                ctx.put(dynamicArgName, item);
                variants.add(ctx);
            }
            log.add("[CONTEXT] القالب '" + templateName + "' ← تم توليد " + sourceList.size()
                    + " نسخة (واحدة لكل " + dynamicArgName + ")");
        } else {
            variants.add(staticArgs);
            log.add("[CONTEXT] القالب '" + templateName + "' ← context واحد ثابت");
        }
    }

    private String extractTemplateName(Arg firstArg) {
        if (!(firstArg instanceof ExprArg)) return null;
        ASTNode expr = ((ExprArg) firstArg).expr;

        if (expr instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) expr;
            if (cc.suffixes.isEmpty()) expr = cc.base;
        }
        if (expr instanceof StringLiteral) return ((StringLiteral) expr).value;
        return null;
    }

    private ExprStmt unwrapExprStmt(ASTNode node) {
        if (node instanceof SimpleStmt && ((SimpleStmt) node).smallStmt instanceof ExprStmt) {
            return (ExprStmt) ((SimpleStmt) node).smallStmt;
        }
        return null;
    }

    private ReturnStmt unwrapReturnStmt(ASTNode node) {
        if (node instanceof SimpleStmt && ((SimpleStmt) node).smallStmt instanceof ReturnStmt) {
            return (ReturnStmt) ((SimpleStmt) node).smallStmt;
        }
        return null;
    }

    private String preview(Object value) {
        if (value == null) return "null";
        String s = value.toString();
        return s.length() > 80 ? s.substring(0, 77) + "..." : s;
    }
}