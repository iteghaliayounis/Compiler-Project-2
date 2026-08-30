
lexer grammar product_htmlLexer;



DOCTYPE
    : '<' '!' [Dd][Oo][Cc][Tt][Yy][Pp][Ee] ~'>'* '>'
    ;

HTML_COMMENT
    : '<' '!' '--' .*? '--' '>' -> skip
    ;

CDATA
    : '<' '!' '[' [Cc][Dd][Aa][Tt][Aa] '[' .*? ']' ']' '>'
    ;

JINJA_COMMENT
    : '{#' .*? '#}' -> skip
    ;


STYLE_OPEN
    : '<' 'style' ([ \t\r\n]+ ~'>'*)? '>'
        -> pushMode(CSS_MODE)
    ;


SCRIPT_OPEN
    : '<' 'script' ([ \t\r\n]+ ~'>'*)? '>'
        -> pushMode(SCRIPT_MODE)
    ;


LT
    : '<' -> pushMode(TAG_MODE)
    ;


JINJA_VAR_OPEN
    : '{{' -> pushMode(JINJA_MODE)
    ;

JINJA_BLOCK_OPEN
    : '{%' -> pushMode(JINJA_MODE)
    ;


TEXT
    : ( ~[<{]
      | '{' ~[<{%]
      )+
    ;



mode TAG_MODE;

TAG_SELF_CLOSE
    : '/>' -> popMode
    ;

TAG_CLOSE
    : '>' -> popMode
    ;

TAG_SLASH
    : '/'
    ;


TAG_VOID_NAME
    : 'area' | 'base' | 'br' | 'col' | 'embed' | 'hr' | 'img'
    | 'input' | 'link' | 'meta' | 'param' | 'source' | 'track' | 'wbr'
    ;


TAG_NAME
    : [a-zA-Z_][a-zA-Z0-9_:.-]*
    ;

TAG_EQUAL
    : '='
    ;

TAG_STRING
    : '"' (~["{\r\n] | '\\' .)* '"'
    | '\'' (~['{\r\n] | '\\' .)* '\''
    ;

TAG_WS
    : [ \t\r\n]+ -> skip
    ;

TAG_JINJA_VAR_OPEN
    : '{{' -> type(JINJA_VAR_OPEN), pushMode(JINJA_MODE)
    ;

TAG_JINJA_BLOCK_OPEN
    : '{%' -> type(JINJA_BLOCK_OPEN), pushMode(JINJA_MODE)
    ;

TAG_ATTR_JINJA_VAR_OPEN
    : '"' '{' '{' -> type(JINJA_VAR_OPEN), pushMode(JINJA_MODE)
    ;

TAG_ATTR_JINJA_BLOCK_OPEN
    : '"' '{' '%' -> type(JINJA_BLOCK_OPEN), pushMode(JINJA_MODE)
    ;

TAG_JINJA_COMMENT
    : '{#' .*? '#}' -> skip
    ;

TAG_STYLE_ATTR_OPEN
    : 'style="' -> pushMode(CSS_MODE)
    ;



mode JINJA_MODE;

JINJA_VAR_CLOSE
    : '}}' '"'? -> popMode
    ;

JINJA_BLOCK_CLOSE
    : '%}' '"'? -> popMode
    ;


JINJA_EXTENDS    : 'extends' ;
JINJA_INCLUDE    : 'include' ;
JINJA_IMPORT     : 'import' ;
JINJA_FROM       : 'from' ;
JINJA_AS         : 'as' ;
JINJA_SET        : 'set' ;
JINJA_BLOCK_KW   : 'block' ;
JINJA_ENDBLOCK   : 'endblock' ;
JINJA_MACRO      : 'macro' ;
JINJA_ENDMACRO   : 'endmacro' ;
JINJA_IF         : 'if' ;
JINJA_ELIF       : 'elif' ;
JINJA_ELSE       : 'else' ;
JINJA_ENDIF      : 'endif' ;
JINJA_FOR        : 'for' ;
JINJA_ENDFOR     : 'endfor' ;
JINJA_IN         : 'in' ;
JINJA_IS         : 'is' ;
JINJA_NOT        : 'not' ;
JINJA_AND        : 'and' ;
JINJA_OR         : 'or' ;
JINJA_TRUE       : 'true' ;
JINJA_FALSE      : 'false' ;
JINJA_NONE       : 'none' ;
JINJA_NULL       : 'null' ;
JINJA_RAW        : 'raw' ;
JINJA_ENDRAW     : 'endraw' ;
JINJA_WITH       : 'with' ;
JINJA_ENDWITH    : 'endwith' ;
JINJA_FILTER     : 'filter' ;
JINJA_ENDFILTER  : 'endfilter' ;
JINJA_DO         : 'do' ;
JINJA_RECURSIVE  : 'recursive' ;


JINJA_EQ         : '==' ;
JINJA_NEQ        : '!=' ;
JINJA_LTE        : '<=' ;
JINJA_GTE        : '>=' ;
JINJA_LT         : '<' ;
JINJA_GT         : '>' ;
JINJA_ASSIGN     : '=' ;
JINJA_PLUS       : '+' ;
JINJA_MINUS      : '-' ;
JINJA_STAR       : '*' ;
JINJA_SLASH      : '/' ;
JINJA_PERCENT    : '%' ;
JINJA_PIPE       : '|' ;
JINJA_TILDE      : '~' ;
JINJA_DOT        : '.' ;
JINJA_COMMA      : ',' ;
JINJA_COLON      : ':' ;
JINJA_LPAR       : '(' ;
JINJA_RPAR       : ')' ;
JINJA_LBRACKET   : '[' ;
JINJA_RBRACKET   : ']' ;

JINJA_STRING
    : '"' (~["\\] | '\\' .)* '"'
    | '\'' (~['\\] | '\\' .)* '\''
    ;

JINJA_NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;

JINJA_ID
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

JINJA_WS
    : [ \t\r\n]+ -> skip
    ;



mode CSS_MODE;


STYLE_CLOSE
    : '<' '/' [Ss][Tt][Yy][Ll][Ee] [ \t\r\n]* '>' -> popMode
    ;


CSS_ATTR_CLOSE
    : '"' -> popMode
    ;

CSS_COMMENT
    : '/*' .*? '*/' -> skip
    ;


CSS_LBRACE     : '{' ;
CSS_RBRACE     : '}' ;
CSS_LPAREN     : '(' ;
CSS_RPAREN     : ')' ;
CSS_LBRACKET   : '[' ;
CSS_RBRACKET   : ']' ;
CSS_COLON      : ':' ;
CSS_SEMI       : ';' ;
CSS_COMMA      : ',' ;
CSS_EQUAL      : '=' ;
CSS_PLUS       : '+' ;
CSS_MINUS      : '-' ;
CSS_STAR       : '*' ;
CSS_SLASH      : '/' ;
CSS_TILDE      : '~' ;
CSS_GT         : '>' ;
CSS_DOT        : '.' ;
CSS_PIPE       : '|' ;


CSS_NUMBER
    : '-'? [0-9]+ ('.' [0-9]+)?
      ( 'px' | 'em' | 'rem' | 'ex' | 'ch' | 'vh' | 'vw' | 'vmin' | 'vmax'
      | 'fr' | 'pt' | 'pc' | 'in' | 'cm' | 'mm' | 'deg' | 'rad' | 'turn'
      | 's' | 'ms' | 'Hz' | 'kHz' | 'dpi' | 'dpcm' | 'dppx' | '%' )?
    ;


CSS_HASH
    : '#' [a-zA-Z0-9_-]+
    ;


CSS_STRING
    : '"' (~["\r\n] | '\\' .)* '"'
    | '\'' (~['\r\n] | '\\' .)* '\''
    ;


CSS_IDENT
    : '-'? [a-zA-Z_][a-zA-Z0-9_-]*
    ;


CSS_AT_KEYWORD
    : '@' [a-zA-Z-]+
    ;


CSS_IMPORTANT
    : '!' [ \t\r\n]* 'important'
    ;

CSS_WS
    : [ \t\r\n]+ -> skip
    ;


CSS_JINJA_VAR_OPEN
    : '{{' -> type(JINJA_VAR_OPEN), pushMode(JINJA_MODE)
    ;

CSS_JINJA_BLOCK_OPEN
    : '{%' -> type(JINJA_BLOCK_OPEN), pushMode(JINJA_MODE)
    ;

CSS_JINJA_COMMENT
    : '{#' .*? '#}' -> skip
    ;



mode SCRIPT_MODE;

SCRIPT_CLOSE
    : '<' '/' [Ss][Cc][Rr][Ii][Pp][Tt] [ \t\r\n]* '>' -> popMode
    ;


SCRIPT_TEXT
    : (~[<] | '<' ~[/])+
    ;

