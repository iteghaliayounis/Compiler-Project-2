parser grammar ProductParser;

options { tokenVocab=ProductLexer; }

program
    : (import_stmt | statement)* EOF
    ;

// Imports

import_stmt
    : IMPORT module_name (COMMA module_name)* NEWLINE* #ImportStmt
    | FROM module_name IMPORT ID (COMMA ID)* NEWLINE*     #FromImportStmt
    ;

// Decorators

decorator
    : AT module_name (LPAR arg_list? RPAR)? NEWLINE*
    ;

module_name
    : ID (DOT ID)*
    ;

// Statements

statement
    : simple_stmt   #SimpleStmt
    | compound_stmt  #CompoundStmt
    ;

simple_stmt
    : small_stmt NEWLINE*
    ;


compound_stmt
    : func_def  #FuncDef
    | flow_stmt  #FlowStmt
    | try_stmt   #TryStmt
    ;


// Function definition

func_def

    : decorator* DEF ID LPAR parameters? RPAR COLON NEWLINE INDENT statement+ DEDENT
    ;


parameters
    : ID (COMMA ID)* (COMMA)?
    ;

// Small statements

small_stmt
    : return_stmt   #ReturnStmt
    | expr_stmt   #ExprStmt
    | raise_stmt  #RaiseStmt
    ;

// Return / Raise

return_stmt
    : RETURN expr?
    ;

raise_stmt
    : RAISE expr?
    ;

// Expressions

expr
    : generator_expr #GeneratorExpr
    | comparisonExpr #ComparisonExp
    ;


comparisonExpr
    : arithExpr ((EQ | NEQ | LT | LE | GT | GE | IS) arithExpr)*
    ;

arithExpr
    : call_chain ((PLUS | MINUS) call_chain)*
    ;

// Call / Attribute / Index

call_chain
    : atom (call_suffix)*
    ;

call_suffix
    : LPAR arg_list? RPAR    #FunctionCall
    | DOT ID  #AttributeAccess
    | LBRACK expr RBRACK  #IndexAccess
    ;


// Atom

atom
    : ID   #Identifier
    | literal   #LiteralAtom
    | list_literal   #ListAtom
    | dict_literal  #DictAtom
    | LPAR expr RPAR    #ParenExpr
    ;


// Generator Expression

generator_expr
    : LPAR gen_expr RPAR
    ;

gen_expr
    : expr FOR ID IN expr (IF expr)?
    ;



// Expression statement

expr_stmt
    : target (ASSIGN expr)?
    ;

target
    : ID    #TargetID
    | call_chain    #TargetCall
    ;

// Arguments

arg_list
    : arg (COMMA arg)* (COMMA)?
    ;

arg
    : expr  #ExprArg
    | ID ASSIGN expr #AssignArg
    ;

// Literals

literal
    : STRING     #StringLiteral
    | INT   #IntegerLiteral
    | FLOAT   #FloatLiteral
    | TRUE   #BoolLiteral
    | FALSE  #BoolLiteral
    | NONE    #NoneLiteral
    ;


// Containers

list_literal
    : LBRACK (expr (COMMA expr)*)? RBRACK
       | LBRACK expr FOR ID IN expr (IF expr)? RBRACK
    ;

dict_literal
    : LCURL (pair (COMMA pair)*)? RCURL
    ;

pair
    : STRING COLON expr
    ;


// Flow statements

flow_stmt
    : IF expr COLON NEWLINE INDENT statement+ DEDENT  #IfStmt
    | FOR ID IN expr COLON NEWLINE INDENT statement+ DEDENT  #ForStmt
    ;


// Try / Except

try_stmt
    : TRY COLON NEWLINE INDENT statement+ DEDENT
      (EXCEPT (ID)? COLON NEWLINE INDENT statement+ DEDENT)*
      (FINALLY COLON NEWLINE INDENT statement+ DEDENT)?
    ;