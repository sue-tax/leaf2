package application;
/* import 文はここに書く */
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.CharArrayReader;
import java.io.StringReader;


%%

// 生成するクラスの名前
%class LeafLexer

%implements LeafTokens

// yylex メソッドの戻り値型
%int
%unicode
%line
%column

%eofval{
return EOS;
%eofval}
%eofclose

%{
	int token;
	Expr yylval;

	int nextToken() {
		try {
			token = yylex();
			if (token == EOF) {
//				D.dprint("+++EOF+++");
				return token = ENDINPUT;
			}
			return token;
		} catch(IOException e) {
			return token = ENDINPUT;
		}
	}

	int getToken() {
		return token;
	}

	Expr getSemantic() {
		return yylval;
	}

//#     'AMPERSAND',

//  'SIBLING',	0x30
 // 'TITLE',
  //'TIPS',
//  'ROW',
  //'COLUMN',
//  'GENERATION',
 // 'PATH',

//	public static final int NANDEMO = 0x40;

//#     'SUMMATCH',
//    'COUNT',
//    'COUNTIF',

//    'EXISTS',
//    'EVAL',
//    'LISTUP',
//    'PICKUP',
//    'DATETOSTR',
//    'TIMETOSTR',

//    'VLOOKUP',

//    'JOIN',

    public static void main(String argv[]) throws java.io.IOException {
//    	InputStreamReader in = new InputStreamReader(System.in);
//==================================================
//    	char[] ach = "123+45*2.34-123_456{adab}|123|456".toCharArray();
//    	CharArrayReader in = new CharArrayReader(ach);
//    	Reader in = new CharArrayReader(ach);
    	String str = "123+45*2.34-123_456{adab}|123|456";
    	StringReader in = new StringReader(str);
    	LeafLexer yy = new LeafLexer(in);
		int t;
		// きちんと動作していない
		// Pareserでは、OK
		while ((t = yy.yylex()) != ENDINPUT) {
		    System.out.println(t);
	    }
//    	ach = "\"abc\"".toCharArray();
//    	in = new CharArrayReader(ach);
  //  	System.out.println(in);
    //	yy = new LeafLexer(in);
//		Yytoken t;
		//while ((t = yy.yylex()) != ENDINPUT) {
//		    System.out.println(t);
//	    }
//==================================================
	}

%}

%state NAMIKAKKONAI, XPATH

%%

<YYINITIAL><\~[^\~<>]*\~>
	{}

<YYINITIAL>\+
	{yylval = new OpExpr(yytext(),PLUS); return PLUS;}
<YYINITIAL>-
	{yylval = new OpExpr(yytext(),MINUS); return MINUS;}
<YYINITIAL>\*
	{yylval = new OpExpr(yytext(),MULT); return MULT;}
<YYINITIAL>\/
	{yylval = new OpExpr(yytext(),DIV); return DIV;}
<YYINITIAL>\^
	{yylval = new OpExpr(yytext(),RUIJOU); return RUIJOU;}

<YYINITIAL>AND
	{yylval = new OpExpr(yytext(),AND); return AND;}
<YYINITIAL>OR
	{yylval = new OpExpr(yytext(),OR); return OR;}
<YYINITIAL>NOT
	{yylval = new OpExpr(yytext(),NOT); return NOT;}

<YYINITIAL>=
	{yylval = new OpExpr(yytext(),EQUAL); return EQUAL;}
<YYINITIAL><>
	{yylval = new OpExpr(yytext(),NOT_EQUAL); return NOT_EQUAL;}
<YYINITIAL>>
	{yylval = new OpExpr(yytext(),DAINARI); return DAINARI;}
<YYINITIAL>>=
	{yylval = new OpExpr(yytext(),DAINARI_EQUAL); return DAINARI_EQUAL;}
<YYINITIAL><
	{yylval = new OpExpr(yytext(),SHOUNARI); return SHOUNARI;}
<YYINITIAL><=
	{yylval = new OpExpr(yytext(),SHOUNARI_EQUAL); return SHOUNARI_EQUAL;}

<YYINITIAL>\(
	{yylval = new OpExpr(yytext(),L_KAKKO); return L_KAKKO;}
<YYINITIAL>\)
	{yylval = new OpExpr(yytext(),R_KAKKO); return R_KAKKO;}
<YYINITIAL>\[
	{yylval = new OpExpr(yytext(),L_KAGI); return L_KAGI;}
<YYINITIAL>\]
	{yylval = new OpExpr(yytext(),R_KAGI); return R_KAGI;}
<YYINITIAL>\:
	{yylval = new OpExpr(yytext(),COLON); return COLON;}
<YYINITIAL>#
	{yylval = new OpExpr(yytext(),SHARP); return SHARP;}

<YYINITIAL>SUM
	{yylval = new OpExpr(yytext(),SUM); return SUM;}
<YYINITIAL>MIN
	{yylval = new OpExpr(yytext(),MIN); return MIN;}
<YYINITIAL>MAX
	{yylval = new OpExpr(yytext(),MAX); return MAX;}
<YYINITIAL>TRUE
	{yylval = new OpExpr(yytext(),TRUE); return TRUE;}
<YYINITIAL>FALSE
	{yylval = new OpExpr(yytext(),FALSE); return FALSE;}

<YYINITIAL>IF
	{yylval = new OpExpr(yytext(),IF); return IF;}
<YYINITIAL>IFS
	{yylval = new OpExpr(yytext(),IFS); return IFS;}
<YYINITIAL>INT
	{yylval = new OpExpr(yytext(),INT); return INT;}
<YYINITIAL>ROUNDUP
	{yylval = new OpExpr(yytext(),ROUNDUP); return ROUNDUP;}
<YYINITIAL>ROUNDDOWN
	{yylval = new OpExpr(yytext(),ROUNDDOWN); return ROUNDDOWN;}
<YYINITIAL>LOOKUP
	{yylval = new OpExpr(yytext(),LOOKUP); return LOOKUP;}

<YYINITIAL>,
	{yylval = new OpExpr(yytext(),COMMA); return COMMA;}


	/** ,の代わりに、_を使う */
<YYINITIAL>[0123456789._]+
	{yylval = new NumberExpr(yytext()); return NUMBER;}

<YYINITIAL>\"[^\"]*\"
	{
	String str = yytext().substring(1,yytext().length()-1);
	yylval = new StrExpr(str);
//	D.dprint("STRING");
//	D.dprint(yytext());
	 return STRING;}

<YYINITIAL>@
	{yylval = new ChildAtExpr(); return CHILD_AT;}


<YYINITIAL>[ \t]+
	{}

<YYINITIAL>\{
	{yybegin(NAMIKAKKONAI);
	 yylval = new OpExpr(yytext(),L_NAMI); return L_NAMI;}

<YYINITIAL>\|
	{yybegin(XPATH);
	 yylval = new OpExpr(yytext(),PIPE); return PIPE;}

<NAMIKAKKONAI>\}
	{yybegin(YYINITIAL);
	 yylval = new OpExpr(yytext(),R_NAMI); return R_NAMI;}

//<NAMIKAKKONAI>[123456789]":"
//	{D.dprint("T"+yytext()); yylval = new StrExpr(yytext()); return MARKDEF;}

<NAMIKAKKONAI>[123456789][0123456789]*":"
	{yylval = new StrExpr(yytext()); return MARKDEF;}

<NAMIKAKKONAI>":"[123456789][0123456789]*
	{yylval = new StrExpr(yytext()); return MARKREF;}

<NAMIKAKKONAI>[^123456789}:][^}:]*
	{yylval = new StrExpr(yytext()); return LPATH;}

<NAMIKAKKONAI>.
	{ throw new Error("NAMIKAKKONAI不正な文字です <"+ yytext()+">"); }

//t_namikakkonai_ignore = ""



<XPATH>\|
	{yybegin(YYINITIAL);
	 yylval = new OpExpr(yytext(),PIPE); return PIPE;}

<XPATH>[^|]+
	{yylval = new StrExpr(yytext()); return NANDEMO;}

//def t_xpath_error(t):
//    print(("{}内　エラー　'| |'" .format(t.value[0])))
//    t.lexer.skip(1)

//t_xpath_ignore = ""

<YYINITIAL>\n		{ yylval = null; return RETURN; }
<YYINITIAL>\r	{}
<YYINITIAL>\f	{}
<YYINITIAL><<EOF>>	{ return EOF; }


<YYINITIAL>.
	{ throw new Error("不正な文字です <"+ yytext()+">"); }

