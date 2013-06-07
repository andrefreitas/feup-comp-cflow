/* Generated By:JJTree&JavaCC: Do not edit this line. RegexParser.java */
package parser;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.ArrayList;
import enfa.ENFA;
import java.util.Iterator;

public class RegexParser/*@bgen(jjtree)*/implements RegexParserTreeConstants, RegexParserConstants {/*@bgen(jjtree)*/
  protected static JJTRegexParserState jjtree = new JJTRegexParserState();public static void main(String args [])
        {
                new RegexParser(System.in);
                try
                {
                        System.out.print("Give the expression: ");

                        SimpleNode root = RegexParser.Start();

                        root.dump("");

                        ENFA parser = root.parseENFA();
                        if (parser.match("a.b.b.b.b.b.d.d"))
                                 System.out.println("YAYAYAYAYAY");
                         else
                                 System.out.println("ohohohoho");
                        Set< String > alphabet = new TreeSet<String>();
                        Set< String > states = new TreeSet<String>();
                        HashMap< String, String > transitions = new HashMap<String, String>();
                        Set< String > accept_states = new TreeSet<String>();
                        String initialState = "q0";
                        states.add(initialState);
                        root.parseDFA(alphabet, states, transitions, accept_states);

                        System.out.println("Alphabet:");
                        for (Iterator<String> it = alphabet.iterator(); it.hasNext(); ) {
                        String f = it.next();
                        System.out.println(f);
                    }
                }
                catch (Exception e)
                {
                        System.out.println("Oops.");
                        System.out.println(e.getMessage());
                }
        }
        public ENFA getENFA(){
                try  {
                        SimpleNode root = RegexParser.Start();
            return root.parseENFA();
          }
          catch (Exception e){
                  System.out.println(e.getMessage());
                  return null;

          }
       }

  static final public SimpleNode Start() throws ParseException {
 /*@bgen(jjtree) Start */
  SimpleNode jjtn000 = new SimpleNode(JJTSTART);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Expr_Main();
      jj_consume_token(NLINE);
           jjtree.closeNodeScope(jjtn000, true);
           jjtc000 = false;
                {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

  static final public void Expr_Main() throws ParseException {
    Expr_Concat();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OR:
      jj_consume_token(OR);
      Expr_Main();
          SimpleNode jjtn001 = new SimpleNode(JJTOR);
          boolean jjtc001 = true;
          jjtree.openNodeScope(jjtn001);
      try {
          jjtree.closeNodeScope(jjtn001,  2);
          jjtc001 = false;
                jjtn001.op = SimpleNode.OR;
      } finally {
          if (jjtc001) {
            jjtree.closeNodeScope(jjtn001,  2);
          }
      }
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
  }

  static final public void Expr_Concat() throws ParseException {
    Expr_Node();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SEPARATOR:
      jj_consume_token(SEPARATOR);
      Expr_Main();
          SimpleNode jjtn001 = new SimpleNode(JJTAND);
          boolean jjtc001 = true;
          jjtree.openNodeScope(jjtn001);
      try {
          jjtree.closeNodeScope(jjtn001,  2);
          jjtc001 = false;
                jjtn001.op = SimpleNode.AND;
      } finally {
          if (jjtc001) {
            jjtree.closeNodeScope(jjtn001,  2);
          }
      }
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
  }

  static final public void Expr_Node() throws ParseException {
 Token t; SimpleNode temp = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEFT_PAR:
      jj_consume_token(LEFT_PAR);
      Expr_Main();
      jj_consume_token(RIGHT_PAR);
      break;
    case IDENTIFIER:
      t = jj_consume_token(IDENTIFIER);
          SimpleNode jjtn001 = new SimpleNode(JJTID);
          boolean jjtc001 = true;
          jjtree.openNodeScope(jjtn001);
      try {
          jjtree.closeNodeScope(jjtn001, true);
          jjtc001 = false;
                        temp = jjtn001;
                jjtn001.op = SimpleNode.ID;
                jjtn001.identifier = t.image;
      } finally {
          if (jjtc001) {
            jjtree.closeNodeScope(jjtn001, true);
          }
      }
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ONE_OR_MORE:
    case ZERO_OR_MORE:
    case ZERO_OR_ONE:
    case 14:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ONE_OR_MORE:
        jj_consume_token(ONE_OR_MORE);
                        temp.timesType = SimpleNode.PLUS;
        break;
      case ZERO_OR_MORE:
        jj_consume_token(ZERO_OR_MORE);
                        temp.timesType = SimpleNode.STAR;
        break;
      case ZERO_OR_ONE:
        jj_consume_token(ZERO_OR_ONE);
                        temp.timesType = SimpleNode.QUESTIONM;
        break;
      case 14:
        jj_consume_token(14);
        t = jj_consume_token(NUMBER);
                                        temp.timesType = SimpleNode.TIMES;
                                        temp.timLeft = Integer.parseInt(t.image);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 15:
          jj_consume_token(15);
                                        temp.timesType = SimpleNode.TIMESLEFT;
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case NUMBER:
            t = jj_consume_token(NUMBER);
                                                temp.timesType = SimpleNode.TIMESINT;
                                                temp.timRight = Integer.parseInt(t.image);
            break;
          default:
            jj_la1[3] = jj_gen;
            ;
          }
          break;
        default:
          jj_la1[4] = jj_gen;
          ;
        }
        jj_consume_token(16);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public RegexParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[7];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x80,0x40,0x820,0x10,0x8000,0x4700,0x4700,};
   }

  /** Constructor with InputStream. */
  public RegexParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public RegexParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new RegexParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public RegexParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new RegexParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public RegexParser(RegexParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(RegexParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[17];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 7; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 17; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}