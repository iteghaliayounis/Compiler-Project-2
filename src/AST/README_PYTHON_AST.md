# Python AST (Java Inheritance Design)

This folder implements the Python-side Abstract Syntax Tree (AST) used by:
- `src/antlr/ProductParser.g4`
- `src/Visitor/ProductVisitor.java`

The design follows inheritance so every syntactic construct is represented as a typed node.

## 1) Root

- `ASTNode` (abstract): base node for all AST nodes.
  - Shared fields: `nodeName`, `lineNumber`
  - Shared behavior: indentation and tree-format printing through `toString(int indent)`

- `Program extends ASTNode`
  - Holds top-level elements (`import_stmt` and `statement`).

## 2) Statements Hierarchy

- `Statement extends ASTNode` (abstract)
  - `CompoundStmt extends Statement` (abstract)
    - `FuncDef`
    - `FlowStmt extends CompoundStmt` (abstract)
      - `IfStmt`
      - `ForStmt`
    - `EntryPoint` (special `if __name__ == "__main__"` block)
    - `TryStmt`

- `SmallStmt extends Statement` (abstract)
  - `ReturnStmt`
  - `RaiseStmt`
  - `ExprStmt`

## 3) Expressions Hierarchy

- `Expr extends ASTNode` (abstract)
  - `ComparisonExpr`
  - `ArithExpr`
  - `CallChainExpr`
  - `GenExpr`

- Atom layer (`AST/Expressions/Atom`)
  - `Atom extends ASTNode` (abstract)
  - `Identifier`
  - `Literal extends Atom` (abstract)
    - `StringLiteral`, `IntegerLiteral`, `FloatLiteral`, `BoolLiteral`, `NoneLiteral`
  - `ListLiteral extends Atom`
  - `DictLiteral extends Atom`
  - `ParenExpr`

- Call-chain layer (`AST/Expressions/CallSuffixes`)
  - `CallSuffix extends ASTNode` (abstract)
    - `FunctionCall`
    - `AttributeAccess`
    - `IndexAccess`
  - `CallChainExpr extends Expr`

## 4) Data and Structural Nodes

- Collection-like nodes
  - `ListLiteral` (direct atom node)
  - `DictLiteral` (direct atom node)
  - `Pair`

## 5) Python Structural Nodes

- Imports
  - `ImportStmt`
  - `FromImportStmt`
  - `ModuleName`

- Function helpers
  - `Parameters`
  - `Decorator`
  - `Arg`, `ArgList`, `ExprArg`, `AssignArg`

- Assignment targets
  - `Target` (abstract)
  - `TargetID`
  - `TargetCall`

## 6) Visitor Mapping

`ProductVisitor` builds these nodes directly:
- `visitProgram -> Program`
- `visitImport_stmt -> ImportStmt | FromImportStmt`
- `visitFuncdef -> FuncDef`
- `visitReturn_stmt -> ReturnStmt`
- `visitRaise_stmt -> RaiseStmt`
- `visitExpr_stmt -> ExprStmt`
- `visitFlow_stmt -> IfStmt | ForStmt | EntryPoint`
- `visitTry_stmt -> TryStmt`
- `visitComparisonExpr -> ComparisonExpr`
- `visitArithExpr -> ArithExpr`
- `visitCall_chain -> CallChainExpr`
- `visitGenerator_expr -> GenExpr`
- `visitCall_suffix -> FunctionCall | AttributeAccess | IndexAccess`
- `visitLiteral -> *Literal subclasses`
- `visitList_literal -> ListLiteral (direct Atom)`
- `visitDict_literal -> DictLiteral (direct Atom)`
- `visitPair -> Pair`

This keeps the AST aligned with grammar rules while preserving a clear inheritance model for future semantic analysis and code generation.
