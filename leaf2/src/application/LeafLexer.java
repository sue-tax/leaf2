// DO NOT EDIT
// Generated by JFlex 1.9.1 http://jflex.de/
// source: leaf.jflex

package application;
import java.io.StringReader;



@SuppressWarnings("fallthrough")
class LeafLexer implements LeafTokens {

  /** This character denotes the end of file. */
  public static final int YYEOF = -1;

  /** Initial size of the lookahead buffer. */
  private static final int ZZ_BUFFERSIZE = 16384;

  // Lexical states.
  public static final int YYINITIAL = 0;
  public static final int NAMIKAKKONAI = 2;
  public static final int XPATH = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1,  1,  2, 2
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\37\u0100\1\u0200\267\u0100\10\u0300\u1020\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\1\3\2\4\22\0\1\1\1\0"+
    "\1\5\1\6\4\0\1\7\1\10\1\11\1\12\1\13"+
    "\1\14\1\15\1\16\1\17\11\20\1\21\1\0\1\22"+
    "\1\23\1\24\1\0\1\25\1\26\2\0\1\27\1\30"+
    "\1\31\2\0\1\32\1\0\1\33\1\34\1\35\1\36"+
    "\1\37\1\40\1\0\1\41\1\42\1\43\1\44\1\0"+
    "\1\45\1\46\2\0\1\47\1\0\1\50\1\51\1\15"+
    "\33\0\1\52\1\53\1\54\1\55\6\0\1\3\u01a2\0"+
    "\2\3\326\0\u0100\3";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[1024];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\1\2\1\3\1\2\1\1\1\4\1\5"+
    "\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\12\1\1\22\1\23\1\24"+
    "\1\25\1\26\1\27\2\30\1\31\1\32\1\33\1\0"+
    "\1\34\1\35\1\36\1\0\1\37\2\0\1\40\5\0"+
    "\1\41\4\0\1\42\1\43\1\0\1\44\1\0\1\45"+
    "\1\46\1\0\1\47\1\50\1\51\1\0\1\52\4\0"+
    "\1\53\1\54\2\0\1\55\3\0\1\56\1\0\1\57";

  private static int [] zzUnpackAction() {
    int [] result = new int[90];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\56\0\134\0\212\0\270\0\212\0\212\0\346"+
    "\0\212\0\212\0\212\0\212\0\212\0\212\0\212\0\u0114"+
    "\0\212\0\212\0\u0142\0\212\0\u0170\0\212\0\u019e\0\u01cc"+
    "\0\u01fa\0\u0228\0\u0256\0\u0284\0\u02b2\0\u02e0\0\u030e\0\u033c"+
    "\0\212\0\212\0\212\0\212\0\212\0\u036a\0\u0398\0\u03c6"+
    "\0\212\0\u03f4\0\212\0\346\0\212\0\212\0\212\0\u0422"+
    "\0\212\0\u0450\0\u047e\0\u04ac\0\u04da\0\u0508\0\u0536\0\u0564"+
    "\0\u0592\0\212\0\u05c0\0\u05ee\0\u061c\0\u0398\0\212\0\u064a"+
    "\0\u0678\0\212\0\u06a6\0\212\0\212\0\u06d4\0\212\0\212"+
    "\0\212\0\u0702\0\212\0\u0730\0\u075e\0\u078c\0\u07ba\0\212"+
    "\0\212\0\u07e8\0\u0816\0\212\0\u0844\0\u0872\0\u08a0\0\212"+
    "\0\u08ce\0\212";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[90];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\1\6\1\0\1\7\1\10\1\11\1\12"+
    "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\2\20"+
    "\1\22\1\23\1\24\1\25\1\26\1\27\2\4\1\30"+
    "\1\31\1\4\1\32\1\33\1\34\1\35\1\4\1\36"+
    "\1\37\1\40\3\4\1\41\1\42\1\43\1\44\1\45"+
    "\2\4\20\46\1\47\1\50\32\46\1\51\1\46\53\52"+
    "\1\53\2\52\57\0\1\5\54\0\5\54\1\55\50\54"+
    "\15\0\1\20\1\0\2\20\60\0\1\56\1\57\30\0"+
    "\1\60\23\0\1\61\70\0\1\62\45\0\1\63\60\0"+
    "\1\64\4\0\1\65\56\0\1\66\44\0\1\67\3\0"+
    "\1\70\62\0\1\71\57\0\1\72\53\0\1\73\62\0"+
    "\1\74\52\0\1\75\14\0\21\46\1\0\32\46\1\0"+
    "\1\46\17\0\2\76\1\77\54\0\1\100\35\0\53\52"+
    "\1\0\2\52\22\60\1\0\1\60\1\0\30\60\1\101"+
    "\27\0\1\102\62\0\1\103\63\0\1\104\56\0\1\105"+
    "\51\0\1\106\64\0\1\107\45\0\1\110\62\0\1\111"+
    "\56\0\1\112\46\0\1\113\64\0\1\114\30\0\2\100"+
    "\61\0\1\7\73\0\1\115\46\0\1\116\60\0\1\117"+
    "\47\0\1\120\55\0\1\121\71\0\1\122\40\0\1\123"+
    "\66\0\1\124\44\0\1\125\14\0\1\126\50\0\1\127"+
    "\56\0\1\130\62\0\1\131\46\0\1\132\17\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[2300];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** Error code for "Unknown internal scanner error". */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  /** Error code for "could not match input". */
  private static final int ZZ_NO_MATCH = 1;
  /** Error code for "pushback value was too large". */
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /**
   * Error messages for {@link #ZZ_UNKNOWN_ERROR}, {@link #ZZ_NO_MATCH}, and
   * {@link #ZZ_PUSHBACK_2BIG} respectively.
   */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\1\1\2\11\1\1\7\11\1\1\2\11"+
    "\1\1\1\11\1\1\1\11\12\1\5\11\3\1\1\11"+
    "\1\1\1\11\1\0\3\11\1\0\1\11\2\0\1\1"+
    "\5\0\1\11\4\0\1\11\1\1\1\0\1\11\1\0"+
    "\2\11\1\0\3\11\1\0\1\11\4\0\2\11\2\0"+
    "\1\11\3\0\1\11\1\0\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[90];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** Input device. */
  private java.io.Reader zzReader;

  /** Current state of the DFA. */
  private int zzState;

  /** Current lexical state. */
  private int zzLexicalState = YYINITIAL;

  /**
   * This buffer contains the current text to be matched and is the source of the {@link #yytext()}
   * string.
   */
  private char zzBuffer[] = new char[Math.min(ZZ_BUFFERSIZE, zzMaxBufferLen())];

  /** Text position at the last accepting state. */
  private int zzMarkedPos;

  /** Current text position in the buffer. */
  private int zzCurrentPos;

  /** Marks the beginning of the {@link #yytext()} string in the buffer. */
  private int zzStartRead;

  /** Marks the last character in the buffer, that has been read from input. */
  private int zzEndRead;

  /**
   * Whether the scanner is at the end of file.
   * @see #yyatEOF
   */
  private boolean zzAtEOF;

  /**
   * The number of occupied positions in {@link #zzBuffer} beyond {@link #zzEndRead}.
   *
   * <p>When a lead/high surrogate has been read from the input stream into the final
   * {@link #zzBuffer} position, this will have a value of 1; otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /** Number of newlines encountered up to the start of the matched text. */
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  private int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  private boolean zzEOFDone;

  /* user code: */
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
		} catch(Exception e) {
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

    public static void main(String argv[]) throws Exception {
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



  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  LeafLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  /**
   * Refills the input buffer.
   *
   * @return {@code false} iff there was new input.
   * @exception java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead - zzStartRead);

      /* translate stored positions */
      zzEndRead -= zzStartRead;
      zzCurrentPos -= zzStartRead;
      zzMarkedPos -= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate && zzCanGrow()) {
      /* if not, and it can grow: blow it up */
      char newBuffer[] = new char[Math.min(zzBuffer.length * 2, zzMaxBufferLen())];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      if (requested == 0) {
        throw new java.io.EOFException("Scan buffer limit reached ["+zzBuffer.length+"]");
      }
      else {
        throw new java.io.IOException(
            "Reader returned 0 characters. See JFlex examples/zero-reader for a workaround.");
      }
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
        if (numRead == requested) { // We requested too few chars to encode a full Unicode character
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        } else {                    // There is room in the buffer for at least one more char
          int c = zzReader.read();  // Expecting to read a paired low surrogate char
          if (c == -1) {
            return true;
          } else {
            zzBuffer[zzEndRead++] = (char)c;
          }
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }


  /**
   * Closes the input reader.
   *
   * @throws java.io.IOException if the reader could not be closed.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true; // indicate end of file
    zzEndRead = zzStartRead; // invalidate buffer

    if (zzReader != null) {
      zzReader.close();
    }
  }


  /**
   * Resets the scanner to read from a new input stream.
   *
   * <p>Does not close the old reader.
   *
   * <p>All internal variables are reset, the old input stream <b>cannot</b> be reused (internal
   * buffer is discarded and lost). Lexical state is set to {@code ZZ_INITIAL}.
   *
   * <p>Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader The new input stream.
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzEOFDone = false;
    yyResetPosition();
    zzLexicalState = YYINITIAL;
    int initBufferSize = Math.min(ZZ_BUFFERSIZE, zzMaxBufferLen());
    if (zzBuffer.length > initBufferSize) {
      zzBuffer = new char[initBufferSize];
    }
  }

  /**
   * Resets the input position.
   */
  private final void yyResetPosition() {
      zzAtBOL  = true;
      zzAtEOF  = false;
      zzCurrentPos = 0;
      zzMarkedPos = 0;
      zzStartRead = 0;
      zzEndRead = 0;
      zzFinalHighSurrogate = 0;
      yyline = 0;
      yycolumn = 0;
      yychar = 0L;
  }


  /**
   * Returns whether the scanner has reached the end of the reader it reads from.
   *
   * @return whether the scanner has reached EOF.
   */
  public final boolean yyatEOF() {
    return zzAtEOF;
  }


  /**
   * Returns the current lexical state.
   *
   * @return the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state.
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   *
   * @return the matched text.
   */
  public final String yytext() {
    return new String(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
  }


  /**
   * Returns the character at the given position from the matched text.
   *
   * <p>It is equivalent to {@code yytext().charAt(pos)}, but faster.
   *
   * @param position the position of the character to fetch. A value from 0 to {@code yylength()-1}.
   *
   * @return the character at {@code position}.
   */
  public final char yycharat(int position) {
    return zzBuffer[zzStartRead + position];
  }


  /**
   * How many characters were matched.
   *
   * @return the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * <p>In a well-formed scanner (no or only correct usage of {@code yypushback(int)} and a
   * match-all fallback rule) this method will only be called with things that
   * "Can't Possibly Happen".
   *
   * <p>If this method is called, something is seriously wrong (e.g. a JFlex bug producing a faulty
   * scanner etc.).
   *
   * <p>Usual syntax/scanner level error handling should be done in error fallback rules.
   *
   * @param errorCode the code of the error message to display.
   */
  private static void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    } catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * <p>They will be read again by then next call of the scanning method.
   *
   * @param number the number of characters to be read again. This number must not be greater than
   *     {@link #yylength()}.
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;

  yyclose();    }
  }




  /**
   * Resumes scanning until the next regular expression is matched, the end of input is encountered
   * or an I/O-Error occurs.
   *
   * @return the next token.
 * @throws Exception
   */
  public int yylex() throws Exception
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char[] zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is
        // (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof)
            zzPeek = false;
          else
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
            switch (zzLexicalState) {
            case YYINITIAL: {
              return EOF;
            }  // fall though
            case 91: break;
            default:
          { return EOS;
 }
        }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { throw new Error("不正な文字です <"+ yytext()+">");
            }
          // fall through
          case 48: break;
          case 2:
            {
            }
          // fall through
          case 49: break;
          case 3:
            { yylval = null; return RETURN;
            }
          // fall through
          case 50: break;
          case 4:
            { yylval = new OpExpr(yytext(),SHARP); return SHARP;
            }
          // fall through
          case 51: break;
          case 5:
            { yylval = new OpExpr(yytext(),L_KAKKO); return L_KAKKO;
            }
          // fall through
          case 52: break;
          case 6:
            { yylval = new OpExpr(yytext(),R_KAKKO); return R_KAKKO;
            }
          // fall through
          case 53: break;
          case 7:
            { yylval = new OpExpr(yytext(),MULT); return MULT;
            }
          // fall through
          case 54: break;
          case 8:
            { yylval = new OpExpr(yytext(),PLUS); return PLUS;
            }
          // fall through
          case 55: break;
          case 9:
            { yylval = new OpExpr(yytext(),COMMA); return COMMA;
            }
          // fall through
          case 56: break;
          case 10:
            { yylval = new OpExpr(yytext(),MINUS); return MINUS;
            }
          // fall through
          case 57: break;
          case 11:
            { yylval = new NumberExpr(yytext()); return NUMBER;
            }
          // fall through
          case 58: break;
          case 12:
            { yylval = new OpExpr(yytext(),DIV); return DIV;
            }
          // fall through
          case 59: break;
          case 13:
            { yylval = new OpExpr(yytext(),COLON); return COLON;
            }
          // fall through
          case 60: break;
          case 14:
            { yylval = new OpExpr(yytext(),SHOUNARI); return SHOUNARI;
            }
          // fall through
          case 61: break;
          case 15:
            { yylval = new OpExpr(yytext(),EQUAL); return EQUAL;
            }
          // fall through
          case 62: break;
          case 16:
            { yylval = new OpExpr(yytext(),DAINARI); return DAINARI;
            }
          // fall through
          case 63: break;
          case 17:
            { yylval = new ChildAtExpr(); return CHILD_AT;
            }
          // fall through
          case 64: break;
          case 18:
            { yylval = new OpExpr(yytext(),L_KAGI); return L_KAGI;
            }
          // fall through
          case 65: break;
          case 19:
            { yylval = new OpExpr(yytext(),R_KAGI); return R_KAGI;
            }
          // fall through
          case 66: break;
          case 20:
            { yylval = new OpExpr(yytext(),RUIJOU); return RUIJOU;
            }
          // fall through
          case 67: break;
          case 21:
            { yybegin(NAMIKAKKONAI);
	 yylval = new OpExpr(yytext(),L_NAMI); return L_NAMI;
            }
          // fall through
          case 68: break;
          case 22:
            { yybegin(XPATH);
	 yylval = new OpExpr(yytext(),PIPE); return PIPE;
            }
          // fall through
          case 69: break;
          case 23:
            { yylval = new StrExpr(yytext()); return LPATH;
            }
          // fall through
          case 70: break;
          case 24:
            { throw new Exception("{}内に不正な文字です <"+ yytext()+">");
            }
          // fall through
          case 71: break;
          case 25:
            { yybegin(YYINITIAL);
	 yylval = new OpExpr(yytext(),R_NAMI); return R_NAMI;
            }
          // fall through
          case 72: break;
          case 26:
            { yylval = new StrExpr(yytext()); return NANDEMO;
            }
          // fall through
          case 73: break;
          case 27:
            { yybegin(YYINITIAL);
	 yylval = new OpExpr(yytext(),PIPE); return PIPE;
            }
          // fall through
          case 74: break;
          case 28:
            { String str = yytext().substring(1,yytext().length()-1);
	yylval = new StrExpr(str);
//	D.dprint("STRING");
//	D.dprint(yytext());
	 return STRING;
            }
          // fall through
          case 75: break;
          case 29:
            { yylval = new OpExpr(yytext(),SHOUNARI_EQUAL); return SHOUNARI_EQUAL;
            }
          // fall through
          case 76: break;
          case 30:
            { yylval = new OpExpr(yytext(),NOT_EQUAL); return NOT_EQUAL;
            }
          // fall through
          case 77: break;
          case 31:
            { yylval = new OpExpr(yytext(),DAINARI_EQUAL); return DAINARI_EQUAL;
            }
          // fall through
          case 78: break;
          case 32:
            { yylval = new OpExpr(yytext(),IF); return IF;
            }
          // fall through
          case 79: break;
          case 33:
            { yylval = new OpExpr(yytext(),OR); return OR;
            }
          // fall through
          case 80: break;
          case 34:
            { yylval = new StrExpr(yytext()); return MARKDEF;
            }
          // fall through
          case 81: break;
          case 35:
            { yylval = new StrExpr(yytext()); return MARKREF;
            }
          // fall through
          case 82: break;
          case 36:
            { yylval = new OpExpr(yytext(),AND); return AND;
            }
          // fall through
          case 83: break;
          case 37:
            { yylval = new OpExpr(yytext(),IFS); return IFS;
            }
          // fall through
          case 84: break;
          case 38:
            { yylval = new OpExpr(yytext(),INT); return INT;
            }
          // fall through
          case 85: break;
          case 39:
            { yylval = new OpExpr(yytext(),MAX); return MAX;
            }
          // fall through
          case 86: break;
          case 40:
            { yylval = new OpExpr(yytext(),MIN); return MIN;
            }
          // fall through
          case 87: break;
          case 41:
            { yylval = new OpExpr(yytext(),NOT); return NOT;
            }
          // fall through
          case 88: break;
          case 42:
            { yylval = new OpExpr(yytext(),SUM); return SUM;
            }
          // fall through
          case 89: break;
          case 43:
            { yylval = new OpExpr(yytext(),TRUE); return TRUE;
            }
          // fall through
          case 90: break;
          case 44:
            { yylval = new OpExpr(yytext(),FALSE); return FALSE;
            }
          // fall through
          case 91: break;
          case 45:
            { yylval = new OpExpr(yytext(),LOOKUP); return LOOKUP;
            }
          // fall through
          case 92: break;
          case 46:
            { yylval = new OpExpr(yytext(),ROUNDUP); return ROUNDUP;
            }
          // fall through
          case 93: break;
          case 47:
            { yylval = new OpExpr(yytext(),ROUNDDOWN); return ROUNDDOWN;
            }
          // fall through
          case 94: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
