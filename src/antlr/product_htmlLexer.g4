//lexer grammar product_htmlLexer;
//
//
//LT              : '<' ;
//GT              : '>' ;
//SLASH           : '/' ;
//EQUALS          : '=' ;
//COMMA           : ',' ;
//DOT             : '.' ;
//SEMI            : ';' ;
//LPAR            : '(' ;
//RPAR            : ')' ;
//LCURL           : '{' ;
//RCURL           : '}' ;
//COLON           : ':' ;
//
//
//DOCTYPE
//    : '<' '!' 'DOCTYPE' ~'>'* '>' ;
//HTML_COMMENT
//    : '<' '!' '--' .*? '--' '>' -> skip ;
//
//
//HTML_DIV    : 'div' ;
//HTML_HTML   : 'html' ;
//HTML_HEAD   : 'head' ;
//HTML_TITLE  : 'title' ;
//HTML_META   : 'meta' ;
//HTML_IMG    : 'img' ;
//HTML_BODY   : 'body' ;
//HTML_FORM   : 'form' ;
//HTML_INPUT     : 'input' ;
//HTML_TEXTAREA  : 'textarea' ;
//HTML_BUTTON    : 'button' ;
//HTML_H2     : 'h2' ;
//HTML_P      : 'p' ;
//HTML_A      : 'a' ;
//OPEN_LABEL    : '<label>' -> pushMode(TAG_CONTENT_MODE) ;
//CSS_STYLE        : 'style="'  -> pushMode(CSS_MODE);
//
//STYLE_OPEN  : '<style>'      -> pushMode(CSS_MODE);
//
//
//ATTR_NAME
//    : [a-zA-Z_:] [a-zA-Z0-9_$:-]* ;
//STRING
//    : '"' (~["\\] | '\\' .)* '"'
//    | '\'' (~['\\] | '\\' .)* '\''
//    ;
//
//WS : [ \t\r\n]+ -> skip ;
//
//// Jinja tokens
//JINJA_STMT_OPEN    : '{%' -> pushMode(JINJA) ;
//JINJA_EXPR_OPEN    : '{{' -> pushMode(JINJA) ;
//
//// mode TAG_CONTENT_MODE;
//mode TAG_CONTENT_MODE;
//TEXT
//    : (~[<{] | '<' ~'/')+
//    ;
//
//CLOSE_LABEL   : '</label>'-> popMode;
//
//
//// mode JINJA
//mode JINJA;
//
//JINJA_STMT_CLOSE : '%}' -> popMode ;
//JINJA_EXPR_CLOSE : '}}' -> popMode ;
//
//// keywords
//JINJA_BLOCK     : 'block' ;
//JINJA_ENDBLOCK  : 'endblock' ;
//JINJA_EXTENDS   : 'extends' ;
//JINJA_IF        : 'if' ;
//JINJA_IN        : 'in' ;
//JINJA_ELSE      : 'else' ;
//JINJA_ELIF      : 'elif' ;
//JINJA_FOR       : 'for' ;
//JINJA_ENDFOR    : 'endfor' ;
//JINJA_RAW       : 'raw' ;
//JINJA_ENDRAW    : 'endraw' ;
//
//// operators
//JINJA_DOT : '.' ;
//JINJA_COMMA : ',' ;
//JINJA_COLON : ':' ;
//JINJA_LPAR : '(' ;
//JINJA_RPAR : ')' ;
//JINJA_EQ : '==' ;
//JINJA_NEQ : '!=' ;
//
//JINJA_STRING
//    : '"' (~["\\] | '\\' .)* '"'
//    | '\'' (~['\\] | '\\' .)* '\''
//    ;
//
//JINJA_NUMBER
//    : [0-9]+ ('.' [0-9]+)? ;
//
//JINJA_ID
//    : [a-zA-Z_][a-zA-Z0-9_]* ;
//
//JINJA_WS : [ \t\r\n]+ -> skip ;
//
//JINJA_OTHER : . ;
//
//
//mode CSS_MODE;
//CSS_CLOSE_STYLE : '"' -> popMode;
//STYLE_CLOSE : '</style>' -> popMode;
//
//LCURL_CSS  : '{' ;
//RCURL_CSS  : '}' ;
//COLON_CSS  : ':' ;
//SEMI_CSS   : ';' ;
//COMMA_CSS  : ',' ;
//// selectors
//CSS_UNIVERSAL_SELECTOR : '*' ;
//CSS_BODY       : 'body' ;
//CSS_CONTAINER  : '.container' ;
//CSS_HEADER     : '.header' ;
//CSS_BTN_SECONDARY : '.btn.secondary' ;
//CSS_BTN        : '.btn' ;
//
//// property names
//CSS_MARGIN            : 'margin' ;
//CSS_PADDING           : 'padding' ;
//CSS_BOX_SIZING        : 'box-sizing' ;
//CSS_DISPLAY           : 'display' ;
//CSS_JUSTIFY_CONTENT   : 'justify-content' ;
//CSS_MAX_WIDTH         : 'max-width' ;
//CSS_BORDER_RADIUS     : 'border-radius' ;
//CSS_TEXT_DECORATION   : 'text-decoration' ;
//CSS_COLOR             : 'color' ;
//CSS_BACKGROUND        : 'background' ;
//CSS_BORDER            : 'border' ;
//CSS_FONT_FAMILY       : 'font-family';
//CSS_MARGIN_BOTTOM     : 'margin-bottom';
//CSS_GAP               : 'gap';
//CSS_GRID              : 'grid-template-columns' ;
//CSS_WIDTH             :'width';
//CSS_BOX_SHADOW        :'box-shadow';
//
//// property values keywords
//CSS_FLEX           : 'flex' ;
//CSS_SPACE_BETWEEN  : 'space-between' ;
//CSS_AUTO           : 'auto' ;
//CSS_NONE           : 'none' ;
//CSS_WHITE          : 'white' ;
//CSS_BORDER_BOX     : 'border-box' ;
//CSS_RGBA
//    : 'rgba' '('
//      CSS_NUMBER ',' CSS_NUMBER ',' CSS_NUMBER ',' CSS_DECIMAL
//      ')' ;
//CSS_DECIMAL
//    : [0-9]+ '.' [0-9]+
//    ;
//CSS_NUMBER
//    :[0-9]+
//    ;
//CSS_FUNCTION
//    : [a-zA-Z_-]+ '(' ( ~[()] | '(' .*? ')' )* ')'
//    ;
//CSS_VALUE
//    :  [0-9]+ ('px'|'fr')? ( [ \t]+ [0-9]+ ('px')? )?
//    | [0-9]+ ('px')? [ \t]+ ('solid'|'dashed'|'dotted') [ \t]+ '#' [0-9a-fA-F]+
//    | '#' [0-9a-fA-F]+
//    | '\'' (~['\\])* '\'' (',' [ \t]* '\'' (~['\\])* '\'')*
//    | [a-zA-Z_-]+ (',' [ \t]* [a-zA-Z_-]+)*
//    | [a-zA-Z0-9#.%\-]+
//    ;
//
//CSS_WS : [ \t\r\n]+ -> skip ;

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
    : '"' (~["\r\n] | '\\' .)* '"'
    | '\'' (~['\r\n] | '\\' .)* '\''
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
    : '}}' -> popMode
    ;

JINJA_BLOCK_CLOSE
    : '%}' -> popMode
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

