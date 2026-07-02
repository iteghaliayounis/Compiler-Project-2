//parser grammar product_htmlParser;
//
//options { tokenVocab=product_htmlLexer; }
//
//program
//   : (start)* EOF
//   ;
//start
//    : prolog       #ProgramProlog
//    | extendsStmt  #ProgramExtends
//    | blockStmt    #ProgramBlock
//    | htmlElement  #ProgramHtml
//    | jinja_expr   #ProgramJinja
//    ;
//
//prolog
//    : DOCTYPE
//    ;
//
//textNode
//    : TEXT
//    ;
//
//// -----------------------------
//extendsStmt
//    : JINJA_STMT_OPEN JINJA_EXTENDS JINJA_STRING JINJA_STMT_CLOSE
//    ;
//
//blockStmt
//    : blockStart textNode? blockContent blockEnd
//    ;
//
//blockStart
//    : JINJA_STMT_OPEN JINJA_BLOCK JINJA_ID JINJA_STMT_CLOSE
//    ;
//
//
//
//blockContent
//    : blockItem*
//    ;
//
//blockItem
//    : divElement        #DivContent
//    | aTag              #ATagContent
//    | imgTag            #ImgContent
//    | formElement       #FormContent
//    | inputElement      #InputContent
//    | textareaElement   #TextareaContent
//    | buttonElement     #ButtonContent
//    | labelElement      #LabelContent
//    | jinja_expr        #JinjaExprContent
//    | forStmt           #ForContent
//    | h2Element         #H2Content
//    | pElement          #PContent
//    | blockStmt         #BlockStmtContent
//    | textNode          #TextContent
//    | ATTR_NAME         #AttrNameContent
//    ;
//
//blockEnd
//    : JINJA_STMT_OPEN JINJA_ENDBLOCK JINJA_STMT_CLOSE
//    ;
//
//
//// -----------------------------
//htmlElement
//    : LT HTML_HTML htmlRule* GT htmlContent LT SLASH HTML_HTML GT
//    ;
//
//htmlContent
//    : ( headElement
//      | bodyElement
//      | textNode
//      | jinja_expr
//      | blockStmt
//      )*
//    ;
//
//headElement
//    : LT HTML_HEAD htmlRule* GT headContent LT SLASH HTML_HEAD GT
//    ;
//
//headContent
//    : headItem*
//    ;
//
//headItem
//    : metaElement     #MetaHeadContent
//    | titleElement    #TitleHeadContent
//    | styleElement    #StyleHeadContent
//    | textNode        #TextHeadContent
//    | jinja_expr      #JinjaHeadContent
//    | blockStmt       #BlockHeadContent
//    ;
//bodyElement
//    : LT HTML_BODY  attribute* GT blockContent LT SLASH HTML_BODY GT
//    ;
//
//
//// -----------------------------
//htmlRule
//   :  styleAttribute
//   |  attribute
//   ;
//attribute
//    : ATTR_NAME EQUALS (STRING | jinja_expr  )
//    ;
//styleAttribute
//    : CSS_STYLE propertyList CSS_CLOSE_STYLE
//
//    ;
//divElement
//    : LT HTML_DIV htmlRule* GT blockContent LT SLASH HTML_DIV GT
//
//    ;
//imgTag
//    : LT HTML_IMG htmlRule* GT (LT SLASH HTML_IMG GT)?
//    ;
//
//aTag
//    : LT HTML_A htmlRule* GT aContent LT SLASH HTML_A GT
//    ;
//aContent
//    : aItem*
//    ;
//
//aItem
//    : textNode        #TextAContent
//    | jinja_expr      #JinjaAContent
//    | divElement      #DivAContent
//    | aTag            #NestedAContent
//    | imgTag          #ImgAContent
//    | ATTR_NAME       #AttrNameAContent
//    ;
//
//
//formElement
//    : LT HTML_FORM htmlRule* GT blockContent LT SLASH HTML_FORM GT
//    ;
//
//inputElement
//    : LT HTML_INPUT htmlRule* GT
//    ;
//
//textareaElement
//    : LT HTML_TEXTAREA htmlRule* GT blockContent LT SLASH HTML_TEXTAREA GT
//    ;
//
//buttonElement
//    : LT HTML_BUTTON htmlRule* GT blockContent LT SLASH HTML_BUTTON GT
//    ;
//
//labelElement
//    : OPEN_LABEL textNode? CLOSE_LABEL
//    ;
//
//titleElement
//    : LT HTML_TITLE GT (blockStmt | jinja_expr | textNode)* LT SLASH HTML_TITLE GT
//    ;
//
//metaElement
//    : LT HTML_META htmlRule* GT
//    ;
//
//
//// -----------------------------
//
//forStmt
//    : JINJA_STMT_OPEN JINJA_FOR jinjaInner JINJA_IN jinjaInner JINJA_STMT_CLOSE
//      blockContent
//      JINJA_STMT_OPEN JINJA_ENDFOR JINJA_STMT_CLOSE
//    ;
//
//jinja_expr
//    : JINJA_EXPR_OPEN jinjaInner? JINJA_EXPR_CLOSE
//    ;
//
//jinjaInner
//    : JINJA_ID ( JINJA_DOT JINJA_ID )*
//    ;
//
//h2Element
//    : LT HTML_H2 htmlRule* GT blockContent LT SLASH HTML_H2 GT
//    ;
//
//pElement
//    : LT HTML_P htmlRule* GT blockContent LT SLASH HTML_P GT
//    ;
//
//// -----------------------------
//
//styleElement
//    : STYLE_OPEN cssBlock STYLE_CLOSE ;
//cssBlock
//    : cssRule+ ;
//cssRule
//    : selector LCURL_CSS propertyList RCURL_CSS;
//
//propertyList
//    : (cssProperty SEMI_CSS)+ ;
//selector
//    : CSS_UNIVERSAL_SELECTOR    #SelectorUniversal
//    | CSS_BODY                  #SelectorBody
//    | CSS_CONTAINER             #SelectorContainer
//    | CSS_HEADER                #SelectorHeader
//    | CSS_BTN                   #SelectorBtn
//    | CSS_BTN_SECONDARY         #SelectorBtnSecondary ;
//
//cssProperty
//    : cssPropertyName COLON_CSS cssValue+ ;
//
//cssPropertyName
//    : CSS_MARGIN               #CssPropertyMargin
//    | CSS_PADDING              #CssPropertyPadding
//    | CSS_BOX_SIZING           #CssPropertyBoxSizing
//    | CSS_DISPLAY              #CssPropertyDisplay
//    | CSS_JUSTIFY_CONTENT      #CssPropertyJustifyContent
//    | CSS_MAX_WIDTH            #CssPropertyMaxWidth
//    | CSS_BORDER_RADIUS        #CssPropertyBorderRadius
//    | CSS_TEXT_DECORATION      #CssPropertyTextDecoration
//    | CSS_COLOR                #CssPropertyColor
//    | CSS_BACKGROUND           #CssPropertyBackground
//    | CSS_BORDER               #CssPropertyBorder
//    | CSS_FONT_FAMILY          #CssPropertyFontFamily
//    | CSS_MARGIN_BOTTOM        #CssPropertyMarginBottom
//    | CSS_GAP                  #CssPropertyGap
//    | CSS_GRID                 #CssPropertyGrid
//    | CSS_WIDTH                #CssPropertyWidth
//    | CSS_BOX_SHADOW           #CssPropertyBoxShadow
//    | CSS_VALUE                #CssPropertyValue;
//
//cssValue
//    : CSS_FLEX                  #CssValueFlex
//    | CSS_SPACE_BETWEEN         #CssValueSpaceBetween
//    | CSS_AUTO                  #CssValueAuto
//    | CSS_NONE                  #CssValueNone
//    | CSS_WHITE                 #CssValueWhite
//    | CSS_BORDER_BOX            #CssValueBorderBox
//    | COMMA_CSS                 #CssValueComma
//    | CSS_RGBA                  #CssValueRgba
//    | CSS_FUNCTION              #CssValueFunction
//    | CSS_VALUE                 #CssValueKeyword
//    | CSS_NUMBER                #CssValueNumber
//    | CSS_DECIMAL               #CssValueDecimal
//    ;

parser grammar product_htmlParser;

options { tokenVocab = product_htmlLexer; }

// ============================================================
// القاعدة العليا
// ============================================================

program
    : (prolog | content)* EOF
    ;

prolog
    : DOCTYPE
    ;

// ============================================================
// المحتوى (أي شيء يظهر بين/خارج التاغات)
// ============================================================

content
    : text                 #TextContent
    | element              #ElementContent
    | jinja_var            #JinjaVarContent
    | jinja_block          #JinjaBlockContent
    ;

text
    : TEXT+
    ;

// ============================================================
// العناصر (تاغات HTML) — عام لكل التاغات
// ============================================================

element
    : styleElement         #StyleElemAlt
    | scriptElement        #ScriptElemAlt
    | voidElement          #VoidElemAlt
    | containerElement     #ContainerElemAlt
    ;

// <style>...</style>  مع CSS حقيقي
styleElement
    : STYLE_OPEN cssStatement* STYLE_CLOSE
    ;

// <script>...</script>  مع نص خام
scriptElement
    : SCRIPT_OPEN SCRIPT_TEXT? SCRIPT_CLOSE
    ;

// العناصر void (مثل <br>, <img>, <input>) أو self-closing (<div/>)
voidElement
    : LT TAG_VOID_NAME attribute* (TAG_CLOSE | TAG_SELF_CLOSE)   #VoidTag
    | LT TAG_NAME attribute* TAG_SELF_CLOSE                       #SelfClosingTag
    ;

// العناصر الحاويةة: <div> ... </div>
containerElement
    : openTag content* closeTag
    ;

openTag
    : LT TAG_NAME attribute* TAG_CLOSE
    ;

closeTag
    : LT TAG_SLASH TAG_NAME TAG_CLOSE
    ;

// ============================================================
// الخصائص (Attributes)
// ============================================================

attribute
    : styleAttribute       #StyleAttr
    | normalAttribute      #NormalAttr
    ;

// style="..."  — خاصية inline style مع CSS
styleAttribute
    : TAG_STYLE_ATTR_OPEN cssDeclaration* CSS_ATTR_CLOSE
    ;

// خاصية عادية: name="value" | name='value' | name={{ jinja }} | name (boolean)
normalAttribute
    : TAG_NAME (TAG_EQUAL attributeValue)?
    ;

attributeValue
    : TAG_STRING           #AttrStringValue
    | jinja_var            #AttrJinjaVarValue
    | jinja_block          #AttrJinjaBlockValue
    ;

// ============================================================
// Jinja — المتغيرات {{ ... }}
// ============================================================

jinja_var
    : JINJA_VAR_OPEN jinjaExpression? JINJA_VAR_CLOSE
    ;

// ============================================================
// Jinja — الأوامر {% ... %}
// ============================================================

jinja_block
    : jinjaExtends         #JinjaExtendsStmt
    | jinjaBlock           #JinjaBlockStmt
    | jinjaIf              #JinjaIfStmt
    | jinjaFor             #JinjaForStmt
    | jinjaSet             #JinjaSetStmt
    | jinjaInclude         #JinjaIncludeStmt
    | jinjaImport          #JinjaImportStmt
    | jinjaFromImport      #JinjaFromImportStmt
    | jinjaRaw             #JinjaRawStmt
    | jinjaMacro           #JinjaMacroStmt
    | jinjaWith            #JinjaWithStmt
    | jinjaFilterBlock     #JinjaFilterBlockStmt
    | jinjaGenericBlock    #JinjaGenericStmt
    ;

jinjaExtends
    : JINJA_BLOCK_OPEN JINJA_EXTENDS JINJA_STRING JINJA_BLOCK_CLOSE
    ;

jinjaBlock
    : JINJA_BLOCK_OPEN JINJA_BLOCK_KW JINJA_ID JINJA_BLOCK_CLOSE
      content*
      JINJA_BLOCK_OPEN JINJA_ENDBLOCK JINJA_ID? JINJA_BLOCK_CLOSE
    ;

jinjaIf
    : JINJA_BLOCK_OPEN JINJA_IF jinjaExpression JINJA_BLOCK_CLOSE
      content*
      ( JINJA_BLOCK_OPEN JINJA_ELIF jinjaExpression JINJA_BLOCK_CLOSE content* )*
      ( JINJA_BLOCK_OPEN JINJA_ELSE JINJA_BLOCK_CLOSE content* )?
      JINJA_BLOCK_OPEN JINJA_ENDIF JINJA_BLOCK_CLOSE
    ;

jinjaFor
    : JINJA_BLOCK_OPEN JINJA_FOR forTarget JINJA_IN jinjaExpression JINJA_BLOCK_CLOSE
      content*
      ( JINJA_BLOCK_OPEN JINJA_ELSE JINJA_BLOCK_CLOSE content* )?
      JINJA_BLOCK_OPEN JINJA_ENDFOR JINJA_BLOCK_CLOSE
    ;

forTarget
    : JINJA_ID (JINJA_COMMA JINJA_ID)*
    ;

jinjaSet
    : JINJA_BLOCK_OPEN JINJA_SET JINJA_ID JINJA_ASSIGN jinjaExpression JINJA_BLOCK_CLOSE
    ;

jinjaInclude
    : JINJA_BLOCK_OPEN JINJA_INCLUDE JINJA_STRING (JINJA_AS JINJA_ID)? JINJA_BLOCK_CLOSE
    ;

jinjaImport
    : JINJA_BLOCK_OPEN JINJA_IMPORT JINJA_STRING JINJA_AS JINJA_ID JINJA_BLOCK_CLOSE
    ;

jinjaFromImport
    : JINJA_BLOCK_OPEN JINJA_FROM JINJA_STRING JINJA_IMPORT jinjaImportNames JINJA_BLOCK_CLOSE
    ;

jinjaImportNames
    : jinjaImportName (JINJA_COMMA jinjaImportName)*
    ;

jinjaImportName
    : JINJA_ID (JINJA_AS JINJA_ID)?
    ;

jinjaRaw
    : JINJA_BLOCK_OPEN JINJA_RAW JINJA_BLOCK_CLOSE
      content*
      JINJA_BLOCK_OPEN JINJA_ENDRAW JINJA_BLOCK_CLOSE
    ;

jinjaMacro
    : JINJA_BLOCK_OPEN JINJA_MACRO JINJA_ID JINJA_LPAR jinjaMacroParams? JINJA_RPAR JINJA_BLOCK_CLOSE
      content*
      JINJA_BLOCK_OPEN JINJA_ENDMACRO JINJA_BLOCK_CLOSE
    ;

jinjaMacroParams
    : jinjaMacroParam (JINJA_COMMA jinjaMacroParam)*
    ;

jinjaMacroParam
    : JINJA_ID (JINJA_ASSIGN jinjaExpression)?     // قيمة افتراضية اختيارية
    ;

jinjaWith
    : JINJA_BLOCK_OPEN JINJA_WITH jinjaSetExpr (JINJA_COMMA jinjaSetExpr)* JINJA_BLOCK_CLOSE
      content*
      JINJA_BLOCK_OPEN JINJA_ENDWITH JINJA_BLOCK_CLOSE
    ;

jinjaSetExpr
    : JINJA_ID JINJA_ASSIGN jinjaExpression
    ;

jinjaFilterBlock
    : JINJA_BLOCK_OPEN JINJA_FILTER JINJA_ID
      (JINJA_LPAR jinjaExpressionList? JINJA_RPAR)? JINJA_BLOCK_CLOSE
      content*
      JINJA_BLOCK_OPEN JINJA_ENDFILTER JINJA_BLOCK_CLOSE
    ;

// أي أمر Jinja آخر — قبول عام
jinjaGenericBlock
    : JINJA_BLOCK_OPEN jinjaExpression? JINJA_BLOCK_CLOSE
    ;

// ============================================================
// تعابير Jinja — مع ترتيب الأسبقية الكامل
// ============================================================

jinjaExpression
    : jinjaTernary
    ;

jinjaTernary
    : jinjaOr (JINJA_IF jinjaOr JINJA_ELSE jinjaTernary)?
    ;

jinjaOr
    : jinjaAnd (JINJA_OR jinjaAnd)*
    ;

jinjaAnd
    : jinjaNot (JINJA_AND jinjaNot)*
    ;

jinjaNot
    : JINJA_NOT jinjaNot                    #JinjaUnaryNot
    | jinjaComparison                       #JinjaCmpExpr
    ;

jinjaComparison
    : jinjaConcat ( comparisonOp jinjaConcat )*
    ;

comparisonOp
    : JINJA_EQ                  #OpEq
    | JINJA_NEQ                 #OpNeq
    | JINJA_LT                  #OpLt
    | JINJA_GT                  #OpGt
    | JINJA_LTE                 #OpLte
    | JINJA_GTE                 #OpGte
    | JINJA_IN                  #OpIn
    | JINJA_NOT JINJA_IN        #OpNotIn
    | JINJA_IS                  #OpIs
    | JINJA_IS JINJA_NOT        #OpIsNot
    ;

jinjaConcat
    : jinjaAddSub (JINJA_TILDE jinjaAddSub)*
    ;

jinjaAddSub
    : jinjaMulDiv ((JINJA_PLUS | JINJA_MINUS) jinjaMulDiv)*
    ;

jinjaMulDiv
    : jinjaFilter ((JINJA_STAR | JINJA_SLASH | JINJA_PERCENT) jinjaFilter)*
    ;

jinjaFilter
    : jinjaPostfix (JINJA_PIPE JINJA_ID (JINJA_LPAR jinjaExpressionList? JINJA_RPAR)?)*
    ;

jinjaPostfix
    : jinjaPrimary                                                      #JinjaPostfixBase
    | jinjaPostfix JINJA_LBRACKET jinjaSlice JINJA_RBRACKET             #JinjaIndex
    | jinjaPostfix JINJA_DOT JINJA_ID                                   #JinjaAttr
    | jinjaPostfix JINJA_LPAR jinjaExpressionList? JINJA_RPAR           #JinjaCall
    ;

// يدعم الوصول بالفهرس والـ slicing: arr[0], arr[1:5], arr[:n], arr[::2]
jinjaSlice
    : jinjaExpression? (JINJA_COLON jinjaExpression?)*
    ;

jinjaPrimary
    : JINJA_NUMBER                              #JinjaNum
    | JINJA_STRING                              #JinjaStr
    | JINJA_TRUE                                #JinjaTrueLit
    | JINJA_FALSE                               #JinjaFalseLit
    | JINJA_NONE                                #JinjaNoneLit
    | JINJA_NULL                                #JinjaNullLit
    | JINJA_ID                                  #JinjaVar
    | JINJA_LPAR jinjaExpression JINJA_RPAR     #JinjaParen
    ;

jinjaExpressionList
    : jinjaExpression (JINJA_COMMA jinjaExpression)*
    ;

// ============================================================
// CSS — داخل <style> و style="..."
// ============================================================

cssStatement
    : cssRuleSet                                #CssRule
    | cssAtRule                                 #CssAtRuleStmt
    ;

// قاعدة CSS كاملة: selector, selector { declarations }
cssRuleSet
    : cssSelectorList CSS_LBRACE cssDeclaration* CSS_RBRACE
    ;

cssSelectorList
    : cssSelector (CSS_COMMA cssSelector)*
    ;

// selector معقد: compound (combinator compound)*
cssSelector
    : cssCompoundSelector (cssCombinator? cssCompoundSelector)*
    ;

// compound selector: سلسلة من selectors بسيطة بدون combinator
// مثال: div.foo#bar:hover  — ويدعم 0%, 100% لـ @keyframes
cssCompoundSelector
    : ( CSS_IDENT
        | CSS_STAR
        | CSS_HASH
        | CSS_NUMBER
        | cssClassSelector
        | cssAttributeSelector
        | cssPseudoSelector
      )+
    ;

cssClassSelector
    : CSS_DOT CSS_IDENT
    ;

cssAttributeSelector
    : CSS_LBRACKET CSS_IDENT
      ( (CSS_EQUAL | CSS_TILDE CSS_EQUAL | CSS_PIPE CSS_EQUAL
         | CSS_PLUS CSS_EQUAL | CSS_STAR CSS_EQUAL)
        (CSS_STRING | CSS_IDENT) )?
      CSS_RBRACKET
    ;

cssPseudoSelector
    : CSS_COLON CSS_IDENT (CSS_LPAREN cssPseudoArg? CSS_RPAREN)?    #PseudoClass
    | CSS_COLON CSS_COLON CSS_IDENT                                 #PseudoElement
    ;

cssPseudoArg
    : (CSS_IDENT | CSS_NUMBER | CSS_STRING | CSS_PLUS | CSS_MINUS)
      (CSS_IDENT | CSS_NUMBER | CSS_STRING | CSS_PLUS | CSS_MINUS)*
    ;

cssCombinator
    : CSS_GT                #ChildCombinator
    | CSS_PLUS              #AdjacentCombinator
    | CSS_TILDE             #GeneralSiblingCombinator
    ;

// At-rules: @media, @keyframes, @import, @font-face, ...
cssAtRule
    : CSS_AT_KEYWORD cssAtRulePrelude? cssAtRuleBody
    ;

cssAtRulePrelude
    : ( CSS_IDENT | CSS_STRING | CSS_NUMBER | CSS_LPAREN | CSS_RPAREN
        | CSS_LBRACKET | CSS_RBRACKET | CSS_COLON | CSS_COMMA | CSS_DOT
        | CSS_HASH | CSS_PLUS | CSS_MINUS | CSS_STAR | CSS_SLASH
        | CSS_TILDE | CSS_GT | CSS_PIPE )+
    ;

cssAtRuleBody
    : CSS_LBRACE cssStatement* CSS_RBRACE       #AtRuleBlock
    | CSS_SEMI                                   #AtRuleSimple
    ;

// تعريف CSS: property: value;
cssDeclaration
    : CSS_IDENT CSS_COLON cssValueList CSS_IMPORTANT? CSS_SEMI?
    ;

cssValueList
    : cssValue (CSS_COMMA? cssValue)*
    ;

cssValue
    : CSS_NUMBER                                #CssNumValue
    | CSS_HASH                                  #CssHashValue
    | CSS_STRING                                #CssStrValue
    | CSS_IDENT                                 #CssIdentValue
    | cssFunction                               #CssFuncValue
    | jinja_var                                 #CssJinjaValue
    ;

cssFunction
    : CSS_IDENT CSS_LPAREN cssFunctionArgList? CSS_RPAREN
    ;

cssFunctionArgList
    : cssFunctionArg (CSS_COMMA? cssFunctionArg)*
    ;

cssFunctionArg
    : ( cssValue
        | CSS_PLUS | CSS_MINUS | CSS_STAR | CSS_SLASH
        | CSS_LBRACKET | CSS_RBRACKET | CSS_LPAREN | CSS_RPAREN
        | CSS_STRING | CSS_HASH
      )+
    ;
