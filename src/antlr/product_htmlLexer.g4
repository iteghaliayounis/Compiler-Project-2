
lexer grammar product_htmlLexer;

// ============================================================
// DEFAULT MODE — HTML body / template content
// (خارج أي تاغ — نص HTML عادي + بدايات التاغات + Jinja)
// ============================================================

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

// <style ...> — يفتح CSS_MODE لمعالجة محتوى CSS
STYLE_OPEN
    : '<' 'style' ([ \t\r\n]+ ~'>'*)? '>'
        -> pushMode(CSS_MODE)
    ;

// <script ...> — يفتح SCRIPT_MODE لمعالجة المحتوى كنص خام
SCRIPT_OPEN
    : '<' 'script' ([ \t\r\n]+ ~'>'*)? '>'
        -> pushMode(SCRIPT_MODE)
    ;

// بداية أي تاغ آخر — ننتقل لـ TAG_MODE
LT
    : '<' -> pushMode(TAG_MODE)
    ;

// فتح Jinja
JINJA_VAR_OPEN
    : '{{' -> pushMode(JINJA_MODE)
    ;

JINJA_BLOCK_OPEN
    : '{%' -> pushMode(JINJA_MODE)
    ;

// نص HTML عادي — أي شيء لا يبدأ تاغ أو Jinja
// (يشمل المسافات لأنها significant في HTML)
TEXT
    : ( ~[<{]
      | '{' ~[<{%]
      )+
    ;


// ============================================================
// TAG MODE — داخل < ... >
// (هون بناكون داخل التاغ: اسم التاغ، الخصائص، القيم)
// ============================================================
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

// عناصر void — ما إلها تاغ إغلاق (قائمة HTML5 الرسمية)
TAG_VOID_NAME
    : 'area' | 'base' | 'br' | 'col' | 'embed' | 'hr' | 'img'
    | 'input' | 'link' | 'meta' | 'param' | 'source' | 'track' | 'wbr'
    ;

// معرّف عام داخل التاغ: يصلح لاسم التاغ أو اسم الخاصية
// (الـ parser بيميز بينهم حسب الموقع - متل الصورة المرجعية)
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

// السماح بـ Jinja داخل قيم الخصائص
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

// style=" — بداية خاصية style inline، ننتقل لـ CSS_MODE
TAG_STYLE_ATTR_OPEN
    : 'style="' -> pushMode(CSS_MODE)
    ;


// ============================================================
// JINJA MODE — داخل {{ ... }} أو {% ... %}
// ============================================================
mode JINJA_MODE;

JINJA_VAR_CLOSE
    : '}}' '"'? -> popMode
    ;

JINJA_BLOCK_CLOSE
    : '%}' '"'? -> popMode
    ;

// الكلمات المفتاحية لـ Jinja2
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

// المعاملات (الأطول أولاً لمنع الـ ambiguity)
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


// ============================================================
// CSS MODE — داخل <style>...</style> أو style="..."
// ============================================================
mode CSS_MODE;

// إغلاق <style> (للـ block style)
STYLE_CLOSE
    : '<' '/' [Ss][Tt][Yy][Ll][Ee] [ \t\r\n]* '>' -> popMode
    ;

// إغلاق " (للـ inline style)
CSS_ATTR_CLOSE
    : '"' -> popMode
    ;

CSS_COMMENT
    : '/*' .*? '*/' -> skip
    ;

// الرموز الأساسية لـ CSS
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

// أرقام مع وحدات CSS
CSS_NUMBER
    : '-'? [0-9]+ ('.' [0-9]+)?
      ( 'px' | 'em' | 'rem' | 'ex' | 'ch' | 'vh' | 'vw' | 'vmin' | 'vmax'
      | 'fr' | 'pt' | 'pc' | 'in' | 'cm' | 'mm' | 'deg' | 'rad' | 'turn'
      | 's' | 'ms' | 'Hz' | 'kHz' | 'dpi' | 'dpcm' | 'dppx' | '%' )?
    ;

// هِش (لون hex أو ID selector)
CSS_HASH
    : '#' [a-zA-Z0-9_-]+
    ;

// سلاسل نصية
CSS_STRING
    : '"' (~["\r\n] | '\\' .)* '"'
    | '\'' (~['\r\n] | '\\' .)* '\''
    ;

// معرّف عام (أسماء الخصائص، القيم الكلمية، أسماء الـ selectors)
CSS_IDENT
    : '-'? [a-zA-Z_][a-zA-Z0-9_-]*
    ;

// @keyword (للـ at-rules مثل @media, @keyframes, @import)
CSS_AT_KEYWORD
    : '@' [a-zA-Z-]+
    ;

// !important
CSS_IMPORTANT
    : '!' [ \t\r\n]* 'important'
    ;

CSS_WS
    : [ \t\r\n]+ -> skip
    ;

// السماح بـ Jinja داخل CSS
CSS_JINJA_VAR_OPEN
    : '{{' -> type(JINJA_VAR_OPEN), pushMode(JINJA_MODE)
    ;

CSS_JINJA_BLOCK_OPEN
    : '{%' -> type(JINJA_BLOCK_OPEN), pushMode(JINJA_MODE)
    ;

CSS_JINJA_COMMENT
    : '{#' .*? '#}' -> skip
    ;


// ============================================================
// SCRIPT MODE — داخل <script>...</script>
// (محتوى خام بدون parsing — يحمي من < و > داخل كود JavaScript)
// ============================================================
mode SCRIPT_MODE;

SCRIPT_CLOSE
    : '<' '/' [Ss][Cc][Rr][Ii][Pp][Tt] [ \t\r\n]* '>' -> popMode
    ;

// نص خام — أي شيء ما يبدأ بـ </script>
SCRIPT_TEXT
    : (~[<] | '<' ~[/])+
    ;

