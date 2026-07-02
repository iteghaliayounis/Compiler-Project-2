lexer grammar ProductLexer;
@members {
    private java.util.LinkedList<Token> pending = new java.util.LinkedList<>();
    private java.util.Stack<Integer> indents = new java.util.Stack<>();
    private int opened = 0;
    private Token lastToken = null;

    @Override
    public void emit(Token t) {
        super.setToken(t);
        pending.offer(t);
    }

    @Override
    public Token nextToken() {

        // EOF handling
        if (_input.LA(1) == EOF && !indents.isEmpty()) {

            // remove EOF
            for (int i = pending.size() - 1; i >= 0; i--) {
                if (pending.get(i).getType() == EOF) {
                    pending.remove(i);
                }
            }

            // force newline
            emit(new CommonToken(NEWLINE, "\n"));

            // emit all remaining DEDENTs
            while (!indents.isEmpty()) {
                emit(new CommonToken(DEDENT, "<DEDENT>"));
                indents.pop();
            }

            emit(new CommonToken(EOF, "<EOF>"));
        }

        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            lastToken = next;
        }

        return pending.isEmpty() ? next : pending.poll();
    }

    private int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            if (ch == '\t') {
                count += 8 - (count % 8);
            } else {
                count++;
            }
        }
        return count;
    }
}
// Keywords (Flask basics)
TRUE    : 'True';
FALSE   : 'False';
NONE    : 'None';
IF      : 'if';
ELSE    : 'else';
ELIF    : 'elif';
FOR     : 'for';
WHILE   : 'while';
DEF     : 'def';
RETURN  : 'return';
IMPORT  : 'import';
FROM    : 'from';
AS      : 'as';
CLASS   : 'class';
PASS    : 'pass';
WITH    : 'with';
TRY     : 'try';
EXCEPT  : 'except';
FINALLY : 'finally';
RAISE   : 'raise';
BREAK   : 'break';
CONTINUE: 'continue';


// Operators
AT      : '@';
ASSIGN  : '=';
PLUS    : '+';
MINUS   : '-';
MUL     : '*';
DIV     : '/';
MOD     : '%';
POW     : '**';
LT      : '<';
LE      : '<=';
GT      : '>';
GE      : '>=';
EQ      : '==';
NEQ     : '!=';
AND     : 'and';
OR      : 'or';
NOT     : 'not';
IN      : 'in';
IS      : 'is';


// Delimiters

LPAR    : '(' {opened++;} ;
RPAR    : ')' {opened--;} ;
LBRACK  : '[' {opened++;} ;
RBRACK  : ']' {opened--;} ;
LCURL   : '{' {opened++;} ;
RCURL   : '}' {opened--;} ;
COMMA   : ',';
COLON   : ':';
DOT     : '.';
SEMI    : ';';
UNDERSQ: '_';

// Identifiers

ID      : [a-zA-Z_] [a-zA-Z_0-9]*;
INT     : [0-9]+;
FLOAT   : [0-9]+ '.' [0-9]* ([eE][+-]?[0-9]+)?;
STRING  : '"' (~["\\] | ESC)* '"' | '\'' (~['\\] | ESC)* '\'';

fragment ESC : '\\' [btnrf"'\\];


// Comments

LINE_COMMENT    : '#' ~[\r\n]* -> skip ;

// NEWLINE
NEWLINE
 : ('\r'? '\n' | '\r') [ \t]*
   {
     String text = getText();
     String newLine = text.replaceAll("[^\r\n]+", "");
     String spaces  = text.replaceAll("[\r\n]+", "");

     int next = _input.LA(1);

     if (opened > 0 || next == '\r' || next == '\n' || next == '#') {
         skip();
     } else {
         emit(new CommonToken(NEWLINE, newLine));

         int indent = getIndentationCount(spaces);
         int previous = indents.isEmpty() ? 0 : indents.peek();

         //status indentation


         if (indent > previous) {
             indents.push(indent);
             emit(new CommonToken(INDENT, "<INDENT>"));
         }
         else if (indent < previous) {
             while (!indents.isEmpty() && indents.peek() > indent) {
                 indents.pop();
                 emit(new CommonToken(DEDENT, "<DEDENT>"));
             }
         }
     }
   }
 ;


WS: [ ]+ -> skip;
TAB_WS: '\t' -> skip;

INDENT: ;
DEDENT: ;