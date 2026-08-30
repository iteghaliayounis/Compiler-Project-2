
parser grammar product_htmlParser;

options { tokenVocab = product_htmlLexer; }



program
    : (prolog | content)* EOF
    ;

prolog
    : DOCTYPE
    ;


content
    : text                 #TextContent
    | element              #ElementContent
    | jinja_var            #JinjaVarContent
    | jinja_block          #JinjaBlockContent
    ;

text
    : TEXT+
    ;


element
    : styleElement         #StyleElemAlt
    | scriptElement        #ScriptElemAlt
    | voidElement          #VoidElemAlt
    | containerElement     #ContainerElemAlt
    ;


styleElement
    : STYLE_OPEN cssStatement* STYLE_CLOSE
    ;


scriptElement
    : SCRIPT_OPEN SCRIPT_TEXT? SCRIPT_CLOSE
    ;


voidElement
    : LT TAG_VOID_NAME attribute* (TAG_CLOSE | TAG_SELF_CLOSE)   #VoidTag
    | LT TAG_NAME attribute* TAG_SELF_CLOSE                       #SelfClosingTag
    ;

containerElement
    : openTag content* closeTag
    ;

openTag
    : LT TAG_NAME attribute* TAG_CLOSE
    ;

closeTag
    : LT TAG_SLASH TAG_NAME TAG_CLOSE
    ;


attribute
    : styleAttribute       #StyleAttr
    | normalAttribute      #NormalAttr
    ;

styleAttribute
    : TAG_STYLE_ATTR_OPEN cssDeclaration* CSS_ATTR_CLOSE
    ;

normalAttribute
    : TAG_NAME (TAG_EQUAL attributeValue)?
    ;

attributeValue
    : TAG_STRING           #AttrStringValue
    | jinja_var            #AttrJinjaVarValue
    | jinja_block           #AttrJinjaBlockValue
    ;



jinja_var
    : JINJA_VAR_OPEN jinjaExpression? JINJA_VAR_CLOSE
    ;



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
    : JINJA_ID (JINJA_ASSIGN jinjaExpression)?
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


jinjaGenericBlock
    : JINJA_BLOCK_OPEN jinjaExpression? JINJA_BLOCK_CLOSE
    ;


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
    | jinjaPostfix JINJA_LPAR jinjaCallArgList? JINJA_RPAR           #JinjaCall
    ;
jinjaCallArgList
    : jinjaCallArg (JINJA_COMMA jinjaCallArg)*
    ;

jinjaCallArg
    : JINJA_ID JINJA_ASSIGN jinjaExpression    #JinjaKwArg
    | jinjaExpression                           #JinjaPosArg
    ;

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


cssStatement
    : cssRuleSet                                #CssRule
    | cssAtRule                                 #CssAtRuleStmt
    ;


cssRuleSet
    : cssSelectorList CSS_LBRACE cssDeclaration* CSS_RBRACE
    ;

cssSelectorList
    : cssSelector (CSS_COMMA cssSelector)*
    ;


cssSelector
    : cssCompoundSelector (cssCombinator? cssCompoundSelector)*
    ;


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
    : cssValue
    | ( CSS_PLUS | CSS_MINUS | CSS_STAR | CSS_SLASH | CSS_LBRACKET | CSS_RBRACKET )+
    ;
