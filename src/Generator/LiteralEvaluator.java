package Generator;

import AST.ASTNode;
import AST.Expressions.Atom.DictAtom;
import AST.Expressions.Atom.Identifier;
import AST.Expressions.Atom.ListAtom;
import AST.Expressions.Atom.ParenExpr;
import AST.Expressions.CallSuffixes.CallChainExpr;
import AST.GeneratorExpr.ArithExpr;
import AST.ListDictPair.DictLiteral;
import AST.ListDictPair.ListLiteral;
import AST.ListDictPair.Pair;
import AST.Literal.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiteralEvaluator {

    private final Map<String, Object> knownVariables;

    public LiteralEvaluator(Map<String, Object> knownVariables) {
        this.knownVariables = knownVariables;
    }

    public Object evaluate(ASTNode node) {
        if (node == null) return null;


        if (node instanceof StringLiteral)  return ((StringLiteral) node).value;
        if (node instanceof IntegerLiteral) return ((IntegerLiteral) node).value;
        if (node instanceof FloatLiteral)   return ((FloatLiteral) node).value;
        if (node instanceof BoolLiteral)    return ((BoolLiteral) node).value;
        if (node instanceof NoneLiteral)    return null;

        if (node instanceof ListAtom) return evaluate(((ListAtom) node).listLiteral);
        if (node instanceof DictAtom) return evaluate(((DictAtom) node).dictLiteral);

        if (node instanceof ListLiteral) {
            List<Object> list = new ArrayList<>();
            for (ASTNode element : ((ListLiteral) node).elements) {
                list.add(evaluate(element));
            }
            return list;
        }

        if (node instanceof DictLiteral) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Pair pair : ((DictLiteral) node).pairs) {
                map.put(stripQuotes(pair.key), evaluate(pair.value));
            }
            return map;
        }


        if (node instanceof Identifier) {
            return knownVariables.get(((Identifier) node).name);
        }


        if (node instanceof ParenExpr) {
            return evaluate(((ParenExpr) node).expr);
        }

        if (node instanceof ArithExpr) {
            return evaluateArithExpr((ArithExpr) node);
        }

        if (node instanceof CallChainExpr) {
            CallChainExpr cc = (CallChainExpr) node;
            if (cc.suffixes == null || cc.suffixes.isEmpty()) {

                return evaluate(cc.base);
            }

            return null;
        }

        return null;
    }

    private Object evaluateArithExpr(ArithExpr node) {
        List<Object> values = new ArrayList<>();
        for (ASTNode term : node.terms) {
            Object v = evaluate(term);
            if (!(v instanceof Number)) return null;
            values.add(v);
        }

        double result = ((Number) values.get(0)).doubleValue();
        boolean isFloat = values.get(0) instanceof Double;

        for (int i = 0; i < node.operators.size(); i++) {
            double next = ((Number) values.get(i + 1)).doubleValue();
            isFloat = isFloat || values.get(i + 1) instanceof Double;

            switch (node.operators.get(i)) {
                case "+": result += next; break;
                case "-": result -= next; break;
                case "*": result *= next; break;
                case "/": result /= next; isFloat = true; break;
                case "%": result %= next; break;
                default: return null;
            }
        }

        return isFloat ? (Object) result : (Object) (int) result;
    }

    private String stripQuotes(String raw) {
        if (raw != null && raw.length() >= 2
                && (raw.charAt(0) == '"' || raw.charAt(0) == '\'')) {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }
}