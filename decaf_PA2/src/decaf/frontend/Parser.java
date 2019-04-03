//### This file created by BYACC 1.8(/Java extension  1.13)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//###           14 Sep 06  -- Keltin Leung-- ReduceListener support, eliminate underflow report in error recovery
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 11 "Parser.y"
package decaf.frontend;

import decaf.tree.Tree;
import decaf.tree.Tree.*;
import decaf.error.*;
import java.util.*;
//#line 25 "Parser.java"
interface ReduceListener {
  public boolean onReduce(String rule);
}




public class Parser
             extends BaseParser
             implements ReduceListener
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

ReduceListener reduceListener = null;
void yyclearin ()       {yychar = (-1);}
void yyerrok ()         {yyerrflag=0;}
void addReduceListener(ReduceListener l) {
  reduceListener = l;}


//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:SemValue
String   yytext;//user variable to return contextual strings
SemValue yyval; //used to return semantic vals from action routines
SemValue yylval;//the 'lval' (result) I got from yylex()
SemValue valstk[] = new SemValue[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new SemValue();
  yylval=new SemValue();
  valptr=-1;
}
final void val_push(SemValue val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    SemValue[] newstack = new SemValue[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final SemValue val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final SemValue val_peek(int relative)
{
  return valstk[valptr-relative];
}
//#### end semantic value section ####
public final static short VOID=257;
public final static short BOOL=258;
public final static short INT=259;
public final static short STRING=260;
public final static short CLASS=261;
public final static short VARIFIC=262;
public final static short NULL=263;
public final static short EXTENDS=264;
public final static short THIS=265;
public final static short WHILE=266;
public final static short FOR=267;
public final static short SEALED=268;
public final static short IF=269;
public final static short ELSE=270;
public final static short RETURN=271;
public final static short BREAK=272;
public final static short NEW=273;
public final static short SCOPY=274;
public final static short PRINT=275;
public final static short READ_INTEGER=276;
public final static short READ_LINE=277;
public final static short LITERAL=278;
public final static short IDENTIFIER=279;
public final static short AND=280;
public final static short OR=281;
public final static short STATIC=282;
public final static short INSTANCEOF=283;
public final static short GUARD=284;
public final static short LESS_EQUAL=285;
public final static short GREATER_EQUAL=286;
public final static short EQUAL=287;
public final static short NOT_EQUAL=288;
public final static short REPEAT=289;
public final static short CONCAT=290;
public final static short DEFT=291;
public final static short IN=292;
public final static short FOREACH=293;
public final static short LG=294;
public final static short RG=295;
public final static short UMINUS=296;
public final static short EMPTY=297;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    5,    5,    5,    5,    5,
    5,    2,    2,    6,    6,    7,    7,    7,    9,    9,
   10,   10,    8,    8,   11,   12,   12,   13,   13,   13,
   13,   13,   13,   13,   13,   13,   13,   13,   13,   19,
   24,   24,   24,   25,   23,   14,   14,   14,   29,   29,
   27,   27,   27,   28,   26,   26,   26,   26,   26,   26,
   26,   26,   26,   26,   26,   26,   26,   26,   26,   26,
   26,   26,   26,   26,   26,   26,   26,   26,   26,   26,
   26,   26,   31,   31,   30,   30,   32,   32,   16,   17,
   18,   18,   33,   22,   15,   34,   34,   20,   20,   21,
};
final static short yylen[] = {                            2,
    1,    2,    1,    2,    2,    1,    1,    1,    1,    2,
    3,    6,    7,    2,    0,    2,    2,    0,    1,    0,
    3,    1,    7,    6,    3,    2,    0,    1,    2,    1,
    1,    1,    1,    1,    2,    2,    2,    1,    2,    4,
    3,    1,    0,    3,    6,    3,    1,    0,    2,    0,
    2,    4,    2,    5,    1,    1,    1,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    2,    2,    3,    3,    1,    4,    5,    6,    5,
    3,    6,    1,    1,    1,    0,    3,    1,    5,    9,
    9,    9,    2,    1,    6,    2,    0,    2,    1,    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    3,    0,    0,    2,    0,    0,
    0,   14,   18,    0,    0,   18,    7,    8,    6,    9,
    0,    0,   12,   16,    0,    0,   17,    0,   10,    0,
    4,    0,    0,   13,    0,    0,   11,    0,   22,    0,
    0,    0,    0,    5,    0,    0,    0,   27,   24,   21,
   23,    0,    0,   84,   76,    0,    0,    0,    0,   94,
    0,    0,    0,    0,    0,   83,    0,    0,    0,    0,
    0,   25,   28,   38,   26,    0,   30,   31,   32,   33,
   34,    0,    0,    0,    0,    0,    0,    0,    0,   57,
   53,    0,    0,    0,    0,    0,   55,   56,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   29,   35,   36,   37,   39,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   49,
    0,    0,    0,    0,    0,    0,    0,   42,    0,    0,
    0,    0,    0,    0,   74,   75,    0,    0,    0,    0,
   71,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   40,    0,   77,    0,    0,    0,  100,    0,
    0,    0,    0,    0,    0,    0,   89,    0,    0,   41,
   44,   78,    0,    0,    0,    0,    0,   80,    0,   54,
    0,    0,   95,   45,   79,    0,    0,    0,    0,   96,
    0,    0,    0,    0,    0,    0,    0,   90,   92,   91,
};
final static short yydgoto[] = {                          3,
    4,    5,   73,   25,   40,   10,   15,   27,   41,   42,
   74,   52,   75,   76,   77,   78,   79,   80,   81,   82,
   83,   84,   85,  137,  138,   86,   97,   98,   89,  185,
   90,  144,  212,  203,
};
final static short yysindex[] = {                      -240,
 -252, -214,    0, -240,    0, -210, -222,    0, -216,  -50,
 -210,    0,    0,  -48, -116,    0,    0,    0,    0,    0,
 -213,  198,    0,    0,   18,  -90,    0,  -51,    0,  -87,
    0,   32,  -15,    0,   47,  198,    0,  198,    0,  -86,
   48,   51,   62,    0,  -18,  198,  -18,    0,    0,    0,
    0,   -4, -166,    0,    0,   74,   75,  -27,  719,    0,
  106,   77,   78,   80,   82,    0,   88,   91,  719,  719,
  681,    0,    0,    0,    0,   81,    0,    0,    0,    0,
    0,   87,   90,   94,   98,  815,   86,    0, -115,    0,
    0,  719,  719,  719,  719,  815,    0,    0,  131,   83,
  719,  719,  127,  132,  719,  164,  -29,  -29, -100,  431,
    0,    0,    0,    0,    0,  719,  719,  719,  719,  719,
  719,  719,  719,  719,  719,  719,  719,  719,  719,    0,
  719,  719,  141,  442,  123,  469, -117,    0,  481,  142,
  700,  140,  815,    8,    0,    0,  550,  -91,  -85,  149,
    0,  741,  877,  -23,  -23,  888,  888,   -3,   13,   13,
  -29,  -29,  -29,  -23,  -23,  528,  815,  719,   25,  719,
   25,  719,    0,   25,    0,  562,  719,  719,    0,  -79,
  -89,  -88,  719,  -76,  161,  173,    0,  583,  -65,    0,
    0,    0,  624,  815,  177,  719,  719,    0,  719,    0,
  719,   25,    0,    0,    0,  734,  734,  -29,  178,    0,
  719,  179,  194,   25,  815,   25,   25,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,  237,    0,  129,    0,    0,    0,    0,
  129,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  205,    0,    0,    0,  234,    0,  234,    0,    0,
    0,  235,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -57,    0,    0,    0,    0,    0,    0,  -56,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   -2,   -2,
   -2,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  836,  270,    0,    0,
    0,   -2,  -57,   -2, -109,  219,    0,    0,    0,    0,
   -2,   -2,    0,    0,   -2,    0,   92,  118,    0,    0,
    0,    0,    0,    0,    0,   -2,   -2,   -2,   -2,   -2,
   -2,   -2,   -2,   -2,   -2,   -2,   -2,   -2,   -2,    0,
   -2,   -2,   39,    0,    0,    0,    0,    0,    0,    0,
   -2,  836,   12,    0,    0,    0,    0,    0,    0,    0,
    0, 1086,  128,  352, 1026, 1090, 1094, 1000,  945,  990,
  154,  297,  371, 1050, 1074,    0,  -26,  -31,  -57,   -2,
  -57,   -2,    0,  -57,    0,    0,   -2,   -2,    0,    0,
    0,    0,   -2,   65,    0,  239,    0,    0,  -33,    0,
    0,    0,    0,   20,    0,   -2,   -2,    0,   -2,    0,
  -30,  -57,    0,    0,    0,    0,    0,  407,    0,    0,
   -2,    0,    0,  -57,  240,  -57,  -57,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  289,    3,   -1,   10,  284,  288,    0,  268,    0,
   22,    0, -123,  -63,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  137, 1288,  987, 1021,    0,    0,
    0,  146,  103,    0,
};
final static int YYTABLESIZE=1499;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         97,
   33,   48,   99,   33,   33,   33,   97,  173,   23,   86,
   48,   97,   94,  127,   46,   43,  130,   24,  125,  123,
    1,  124,  130,  126,   26,   97,    6,    2,   70,  135,
   24,   30,   46,  127,   39,   71,   39,   26,  125,  123,
   69,  124,  130,  126,   50,  187,    7,  189,  179,  127,
  191,  178,   88,    9,  125,   88,   11,   70,  130,  126,
   87,  131,   12,   87,   71,   29,   49,  131,   51,   69,
  100,   36,   13,   34,   16,   51,   31,   37,  210,   51,
   51,   51,   51,   51,   51,   51,   38,  131,   45,   97,
  218,   97,  219,  220,   46,   95,   51,   51,   51,   51,
   51,   52,   47,  131,   48,   52,   52,   52,   52,   52,
   52,   52,   91,   92,   93,  149,  101,  102,   48,  103,
   72,  104,   52,   52,   52,   52,   52,  105,   72,   51,
  106,   51,   72,   72,   72,   72,   72,  209,   72,  111,
   17,   18,   19,   20,   21,  112,  132,   48,  113,   72,
   72,   72,  114,   72,   73,   52,  115,   52,   73,   73,
   73,   73,   73,  133,   73,   22,  172,  145,   70,   50,
  140,   70,  146,  141,   43,   73,   73,   73,  150,   73,
  168,  170,  175,  177,   72,   70,   70,  181,   32,  183,
   60,   35,   44,  182,   60,   60,   60,   60,   60,  195,
   60,  200,  196,  197,  202,   17,   18,   19,   20,   21,
   73,   60,   60,   60,  199,   60,  178,  205,  214,  216,
   70,   50,   50,   97,   97,   97,   97,   97,   97,   97,
   22,   97,   97,   97,  217,   97,    1,   97,   97,   97,
   97,   97,   97,   97,   97,   97,   60,   50,   50,   97,
   97,   15,   17,   18,   19,   20,   21,   53,   54,   97,
   55,   56,   57,    5,   58,  122,   59,   60,   61,   62,
   63,   64,   65,   66,   20,   19,   50,   98,   67,   85,
   93,   17,   18,   19,   20,   21,   53,   54,   68,   55,
   56,   57,    8,   58,   14,   59,   60,   61,   62,   63,
   64,   65,   66,   28,   51,   43,   56,   67,  190,  213,
   47,   56,   56,  186,   56,   56,   56,   68,   51,   51,
    0,    0,    0,   51,   51,   51,   51,   51,   47,   56,
   52,   56,    0,   61,    0,    0,    0,   61,   61,   61,
   61,   61,    0,   61,   52,   52,    0,    0,    0,   52,
   52,   52,   52,   52,   61,   61,   61,   72,   61,    0,
   56,    0,   17,   18,   19,   20,   21,    0,    0,    0,
    0,   72,   72,    0,    0,    0,   72,   72,   72,   72,
   72,    0,    0,   73,   99,    0,    0,    0,    0,   61,
    0,    0,   67,   70,    0,   67,    0,   73,   73,    0,
    0,    0,   73,   73,   73,   73,   73,   62,   70,   67,
   67,   62,   62,   62,   62,   62,    0,   62,    0,   60,
   17,   18,   19,   20,   21,  148,    0,    0,   62,   62,
   62,    0,   62,   60,   60,    0,    0,    0,   60,   60,
   60,   60,   60,   82,   67,    0,    0,   82,   82,   82,
   82,   82,    0,   82,   17,   18,   19,   20,   21,    0,
    0,    0,    0,   62,   82,   82,   82,  127,   82,    0,
    0,  151,  125,  123,    0,  124,  130,  126,  127,    0,
    0,    0,  169,  125,  123,    0,  124,  130,  126,    0,
  129,    0,  128,    0,    0,    0,    0,    0,    0,   82,
    0,  129,    0,  128,    0,  127,    0,    0,    0,  171,
  125,  123,    0,  124,  130,  126,    0,  127,    0,    0,
    0,  131,  125,  123,    0,  124,  130,  126,  129,    0,
  128,    0,  131,    0,    0,    0,    0,    0,  174,    0,
  129,    0,  128,    0,    0,    0,    0,    0,    0,   56,
   56,    0,    0,    0,   56,   56,   56,   56,   56,  131,
    0,    0,   61,    0,  127,    0,    0,    0,    0,  125,
  123,  131,  124,  130,  126,    0,   61,   61,    0,    0,
    0,   61,   61,   61,   61,   61,  127,  129,    0,  128,
    0,  125,  123,  180,  124,  130,  126,    0,  127,    0,
    0,    0,    0,  125,  123,    0,  124,  130,  126,  129,
    0,  128,    0,    0,    0,    0,    0,   67,  131,  127,
  184,  129,    0,  128,  125,  123,    0,  124,  130,  126,
    0,   67,   67,    0,    0,    0,   62,    0,   67,   67,
  131,  201,  129,    0,  128,    0,    0,    0,    0,    0,
   62,   62,  131,    0,  192,   62,   62,   62,   62,   62,
  127,    0,    0,    0,  204,  125,  123,    0,  124,  130,
  126,    0,   82,  131,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  129,    0,  128,   82,   82,    0,    0,
    0,   82,   82,   82,   82,   82,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  116,  117,    0,   70,  131,  118,  119,  120,  121,  122,
   71,  116,  117,    0,    0,   69,  118,  119,  120,  121,
  122,    0,   70,    0,    0,    0,    0,    0,    0,   71,
    0,    0,    0,    0,   69,    0,    0,    0,  116,  117,
    0,   70,    0,  118,  119,  120,  121,  122,   71,    0,
  116,  117,    0,   69,    0,  118,  119,  120,  121,  122,
  127,    0,    0,    0,    0,  125,  123,  127,  124,  130,
  126,    0,  125,  123,    0,  124,  130,  126,    0,    0,
    0,    0,   37,  129,    0,  128,    0,    0,    0,    0,
  129,    0,  128,    0,    0,    0,    0,  116,  117,    0,
    0,    0,  118,  119,  120,  121,  122,    0,    0,    0,
    0,    0,    0,    0,  131,    0,    0,    0,    0,  116,
  117,  131,    0,    0,  118,  119,  120,  121,  122,    0,
    0,  116,  117,    0,    0,    0,  118,  119,  120,  121,
  122,  127,    0,    0,    0,    0,  125,  123,    0,  124,
  130,  126,  116,  117,    0,    0,    0,  118,  119,  120,
  121,  122,   55,    0,  129,    0,  128,   55,   55,    0,
   55,   55,   55,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   55,    0,   55,    0,    0,
    0,    0,    0,  116,  117,  131,    0,    0,  118,  119,
  120,  121,  122,  127,    0,    0,    0,    0,  125,  123,
    0,  124,  130,  126,  127,    0,   55,    0,    0,  125,
  123,    0,  124,  130,  126,    0,  129,    0,  128,    0,
    0,  109,   53,   54,    0,   55,    0,  129,    0,  128,
    0,    0,    0,   61,    0,    0,   64,   65,   66,    0,
    0,   53,   54,   67,   55,    0,    0,  131,    0,    0,
    0,    0,   61,    0,    0,   64,   65,   66,  131,    0,
   53,   54,   67,   55,    0,   58,    0,   58,   58,   58,
    0,   61,    0,    0,   64,   65,   66,    0,    0,  211,
    0,   67,   58,   58,   58,    0,   58,    0,    0,    0,
    0,    0,    0,  116,  117,    0,    0,    0,  118,  119,
  120,  121,  122,    0,    0,  118,  119,  120,  121,  122,
   59,    0,   59,   59,   59,    0,    0,   58,   87,    0,
   81,    0,    0,   81,    0,    0,    0,   59,   59,   59,
    0,   59,    0,    0,    0,    0,    0,   81,   81,   81,
    0,   81,    0,    0,    0,    0,   68,    0,    0,   68,
    0,    0,   88,    0,    0,    0,    0,    0,    0,   87,
    0,    0,   59,   68,   68,    0,    0,  142,    0,    0,
   66,    0,   81,   66,  116,  117,    0,    0,    0,  118,
  119,  120,  121,  122,    0,    0,    0,   66,   66,    0,
    0,    0,    0,   88,   65,   55,   55,   65,   68,    0,
   55,   55,   55,   55,   55,    0,   69,    0,    0,   69,
   63,   65,   65,   63,   64,    0,    0,   64,    0,    0,
    0,    0,   66,   69,   69,    0,    0,   63,   63,    0,
    0,   64,   64,    0,    0,   87,  116,   87,    0,    0,
   87,  118,  119,  120,  121,  122,   65,    0,    0,    0,
    0,    0,  118,  119,    0,    0,  122,    0,   69,    0,
    0,    0,   63,    0,    0,    0,   64,   87,   87,   88,
    0,   88,    0,    0,   88,    0,    0,    0,    0,    0,
   87,    0,   87,   87,    0,    0,    0,    0,    0,    0,
   58,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   88,   88,    0,   58,   58,    0,    0,    0,   58,
   58,   58,   58,   58,   88,    0,   88,   88,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   59,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   81,    0,    0,    0,   59,
   59,    0,    0,    0,   59,   59,   59,   59,   59,   81,
   81,    0,    0,    0,   81,   81,   81,   81,   81,    0,
    0,   68,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   68,   68,    0,    0,    0,
    0,    0,   68,   68,    0,   66,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   66,
   66,    0,    0,    0,    0,    0,   66,   66,    0,   65,
    0,    0,    0,    0,    0,    0,   96,    0,    0,    0,
    0,   69,    0,   65,   65,   63,  107,  108,  110,   64,
   65,   65,    0,    0,    0,   69,   69,    0,    0,   63,
   63,    0,    0,   64,   64,    0,    0,    0,    0,  134,
    0,  136,  139,    0,    0,    0,    0,    0,    0,  143,
    0,    0,  147,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  152,  153,  154,  155,  156,  157,  158,
  159,  160,  161,  162,  163,  164,  165,    0,  166,  167,
    0,    0,    0,    0,    0,    0,    0,    0,  176,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  143,    0,  188,    0,  139,
    0,    0,    0,    0,  193,  194,    0,    0,    0,    0,
  198,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  206,  207,    0,  208,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  215,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
   91,   59,   59,   91,   91,   91,   40,  125,  125,   41,
   41,   45,   40,   37,   41,  125,   46,   15,   42,   43,
  261,   45,   46,   47,   15,   59,  279,  268,   33,   93,
   28,   22,   59,   37,   36,   40,   38,   28,   42,   43,
   45,   45,   46,   47,   46,  169,  261,  171,   41,   37,
  174,   44,   41,  264,   42,   44,  279,   33,   46,   47,
   41,   91,  279,   44,   40,  279,   45,   91,   47,   45,
   61,   40,  123,  125,  123,   37,   59,   93,  202,   41,
   42,   43,   44,   45,   46,   47,   40,   91,   41,  123,
  214,  125,  216,  217,   44,  123,   58,   59,   60,   61,
   62,   37,   41,   91,  123,   41,   42,   43,   44,   45,
   46,   47,  279,   40,   40,  106,   40,   40,  123,   40,
  125,   40,   58,   59,   60,   61,   62,   40,   37,   91,
   40,   93,   41,   42,   43,   44,   45,  201,   47,   59,
  257,  258,  259,  260,  261,   59,   61,  123,   59,   58,
   59,   60,   59,   62,   37,   91,   59,   93,   41,   42,
   43,   44,   45,  279,   47,  282,  284,   41,   41,  279,
   40,   44,   41,   91,  284,   58,   59,   60,  279,   62,
   40,   59,   41,   44,   93,   58,   59,  279,  279,   41,
   37,  279,  279,  279,   41,   42,   43,   44,   45,  279,
   47,   41,  292,  292,  270,  257,  258,  259,  260,  261,
   93,   58,   59,   60,  291,   62,   44,   41,   41,   41,
   93,  279,  279,  257,  258,  259,  260,  261,  262,  263,
  282,  265,  266,  267,   41,  269,    0,  271,  272,  273,
  274,  275,  276,  277,  278,  279,   93,  279,  279,  283,
  284,  123,  257,  258,  259,  260,  261,  262,  263,  293,
  265,  266,  267,   59,  269,  289,  271,  272,  273,  274,
  275,  276,  277,  278,   41,   41,  279,   59,  283,   41,
   41,  257,  258,  259,  260,  261,  262,  263,  293,  265,
  266,  267,    4,  269,   11,  271,  272,  273,  274,  275,
  276,  277,  278,   16,  266,   38,   37,  283,  172,  207,
   41,   42,   43,  168,   45,   46,   47,  293,  280,  281,
   -1,   -1,   -1,  285,  286,  287,  288,  289,   59,   60,
  266,   62,   -1,   37,   -1,   -1,   -1,   41,   42,   43,
   44,   45,   -1,   47,  280,  281,   -1,   -1,   -1,  285,
  286,  287,  288,  289,   58,   59,   60,  266,   62,   -1,
   91,   -1,  257,  258,  259,  260,  261,   -1,   -1,   -1,
   -1,  280,  281,   -1,   -1,   -1,  285,  286,  287,  288,
  289,   -1,   -1,  266,  279,   -1,   -1,   -1,   -1,   93,
   -1,   -1,   41,  266,   -1,   44,   -1,  280,  281,   -1,
   -1,   -1,  285,  286,  287,  288,  289,   37,  281,   58,
   59,   41,   42,   43,   44,   45,   -1,   47,   -1,  266,
  257,  258,  259,  260,  261,  262,   -1,   -1,   58,   59,
   60,   -1,   62,  280,  281,   -1,   -1,   -1,  285,  286,
  287,  288,  289,   37,   93,   -1,   -1,   41,   42,   43,
   44,   45,   -1,   47,  257,  258,  259,  260,  261,   -1,
   -1,   -1,   -1,   93,   58,   59,   60,   37,   62,   -1,
   -1,   41,   42,   43,   -1,   45,   46,   47,   37,   -1,
   -1,   -1,   41,   42,   43,   -1,   45,   46,   47,   -1,
   60,   -1,   62,   -1,   -1,   -1,   -1,   -1,   -1,   93,
   -1,   60,   -1,   62,   -1,   37,   -1,   -1,   -1,   41,
   42,   43,   -1,   45,   46,   47,   -1,   37,   -1,   -1,
   -1,   91,   42,   43,   -1,   45,   46,   47,   60,   -1,
   62,   -1,   91,   -1,   -1,   -1,   -1,   -1,   58,   -1,
   60,   -1,   62,   -1,   -1,   -1,   -1,   -1,   -1,  280,
  281,   -1,   -1,   -1,  285,  286,  287,  288,  289,   91,
   -1,   -1,  266,   -1,   37,   -1,   -1,   -1,   -1,   42,
   43,   91,   45,   46,   47,   -1,  280,  281,   -1,   -1,
   -1,  285,  286,  287,  288,  289,   37,   60,   -1,   62,
   -1,   42,   43,   44,   45,   46,   47,   -1,   37,   -1,
   -1,   -1,   -1,   42,   43,   -1,   45,   46,   47,   60,
   -1,   62,   -1,   -1,   -1,   -1,   -1,  266,   91,   37,
   93,   60,   -1,   62,   42,   43,   -1,   45,   46,   47,
   -1,  280,  281,   -1,   -1,   -1,  266,   -1,  287,  288,
   91,   59,   60,   -1,   62,   -1,   -1,   -1,   -1,   -1,
  280,  281,   91,   -1,   93,  285,  286,  287,  288,  289,
   37,   -1,   -1,   -1,   41,   42,   43,   -1,   45,   46,
   47,   -1,  266,   91,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   60,   -1,   62,  280,  281,   -1,   -1,
   -1,  285,  286,  287,  288,  289,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  280,  281,   -1,   33,   91,  285,  286,  287,  288,  289,
   40,  280,  281,   -1,   -1,   45,  285,  286,  287,  288,
  289,   -1,   33,   -1,   -1,   -1,   -1,   -1,   -1,   40,
   -1,   -1,   -1,   -1,   45,   -1,   -1,   -1,  280,  281,
   -1,   33,   -1,  285,  286,  287,  288,  289,   40,   -1,
  280,  281,   -1,   45,   -1,  285,  286,  287,  288,  289,
   37,   -1,   -1,   -1,   -1,   42,   43,   37,   45,   46,
   47,   -1,   42,   43,   -1,   45,   46,   47,   -1,   -1,
   -1,   -1,   93,   60,   -1,   62,   -1,   -1,   -1,   -1,
   60,   -1,   62,   -1,   -1,   -1,   -1,  280,  281,   -1,
   -1,   -1,  285,  286,  287,  288,  289,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   91,   -1,   -1,   -1,   -1,  280,
  281,   91,   -1,   -1,  285,  286,  287,  288,  289,   -1,
   -1,  280,  281,   -1,   -1,   -1,  285,  286,  287,  288,
  289,   37,   -1,   -1,   -1,   -1,   42,   43,   -1,   45,
   46,   47,  280,  281,   -1,   -1,   -1,  285,  286,  287,
  288,  289,   37,   -1,   60,   -1,   62,   42,   43,   -1,
   45,   46,   47,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   60,   -1,   62,   -1,   -1,
   -1,   -1,   -1,  280,  281,   91,   -1,   -1,  285,  286,
  287,  288,  289,   37,   -1,   -1,   -1,   -1,   42,   43,
   -1,   45,   46,   47,   37,   -1,   91,   -1,   -1,   42,
   43,   -1,   45,   46,   47,   -1,   60,   -1,   62,   -1,
   -1,  261,  262,  263,   -1,  265,   -1,   60,   -1,   62,
   -1,   -1,   -1,  273,   -1,   -1,  276,  277,  278,   -1,
   -1,  262,  263,  283,  265,   -1,   -1,   91,   -1,   -1,
   -1,   -1,  273,   -1,   -1,  276,  277,  278,   91,   -1,
  262,  263,  283,  265,   -1,   41,   -1,   43,   44,   45,
   -1,  273,   -1,   -1,  276,  277,  278,   -1,   -1,  266,
   -1,  283,   58,   59,   60,   -1,   62,   -1,   -1,   -1,
   -1,   -1,   -1,  280,  281,   -1,   -1,   -1,  285,  286,
  287,  288,  289,   -1,   -1,  285,  286,  287,  288,  289,
   41,   -1,   43,   44,   45,   -1,   -1,   93,   52,   -1,
   41,   -1,   -1,   44,   -1,   -1,   -1,   58,   59,   60,
   -1,   62,   -1,   -1,   -1,   -1,   -1,   58,   59,   60,
   -1,   62,   -1,   -1,   -1,   -1,   41,   -1,   -1,   44,
   -1,   -1,   52,   -1,   -1,   -1,   -1,   -1,   -1,   93,
   -1,   -1,   93,   58,   59,   -1,   -1,  101,   -1,   -1,
   41,   -1,   93,   44,  280,  281,   -1,   -1,   -1,  285,
  286,  287,  288,  289,   -1,   -1,   -1,   58,   59,   -1,
   -1,   -1,   -1,   93,   41,  280,  281,   44,   93,   -1,
  285,  286,  287,  288,  289,   -1,   41,   -1,   -1,   44,
   41,   58,   59,   44,   41,   -1,   -1,   44,   -1,   -1,
   -1,   -1,   93,   58,   59,   -1,   -1,   58,   59,   -1,
   -1,   58,   59,   -1,   -1,  169,  280,  171,   -1,   -1,
  174,  285,  286,  287,  288,  289,   93,   -1,   -1,   -1,
   -1,   -1,  285,  286,   -1,   -1,  289,   -1,   93,   -1,
   -1,   -1,   93,   -1,   -1,   -1,   93,  201,  202,  169,
   -1,  171,   -1,   -1,  174,   -1,   -1,   -1,   -1,   -1,
  214,   -1,  216,  217,   -1,   -1,   -1,   -1,   -1,   -1,
  266,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  201,  202,   -1,  280,  281,   -1,   -1,   -1,  285,
  286,  287,  288,  289,  214,   -1,  216,  217,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  266,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  266,   -1,   -1,   -1,  280,
  281,   -1,   -1,   -1,  285,  286,  287,  288,  289,  280,
  281,   -1,   -1,   -1,  285,  286,  287,  288,  289,   -1,
   -1,  266,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  280,  281,   -1,   -1,   -1,
   -1,   -1,  287,  288,   -1,  266,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  280,
  281,   -1,   -1,   -1,   -1,   -1,  287,  288,   -1,  266,
   -1,   -1,   -1,   -1,   -1,   -1,   59,   -1,   -1,   -1,
   -1,  266,   -1,  280,  281,  266,   69,   70,   71,  266,
  287,  288,   -1,   -1,   -1,  280,  281,   -1,   -1,  280,
  281,   -1,   -1,  280,  281,   -1,   -1,   -1,   -1,   92,
   -1,   94,   95,   -1,   -1,   -1,   -1,   -1,   -1,  102,
   -1,   -1,  105,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  116,  117,  118,  119,  120,  121,  122,
  123,  124,  125,  126,  127,  128,  129,   -1,  131,  132,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  141,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  168,   -1,  170,   -1,  172,
   -1,   -1,   -1,   -1,  177,  178,   -1,   -1,   -1,   -1,
  183,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  196,  197,   -1,  199,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  211,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=297;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"VOID","BOOL","INT","STRING",
"CLASS","VARIFIC","NULL","EXTENDS","THIS","WHILE","FOR","SEALED","IF","ELSE",
"RETURN","BREAK","NEW","SCOPY","PRINT","READ_INTEGER","READ_LINE","LITERAL",
"IDENTIFIER","AND","OR","STATIC","INSTANCEOF","GUARD","LESS_EQUAL",
"GREATER_EQUAL","EQUAL","NOT_EQUAL","REPEAT","CONCAT","DEFT","IN","FOREACH",
"LG","RG","UMINUS","EMPTY",
};
final static String yyrule[] = {
"$accept : Program",
"Program : ClassList",
"ClassList : ClassList ClassDef",
"ClassList : ClassDef",
"VariableDef : Variable ';'",
"Variable : Type IDENTIFIER",
"Type : INT",
"Type : VOID",
"Type : BOOL",
"Type : STRING",
"Type : CLASS IDENTIFIER",
"Type : Type '[' ']'",
"ClassDef : CLASS IDENTIFIER ExtendsClause '{' FieldList '}'",
"ClassDef : SEALED CLASS IDENTIFIER ExtendsClause '{' FieldList '}'",
"ExtendsClause : EXTENDS IDENTIFIER",
"ExtendsClause :",
"FieldList : FieldList VariableDef",
"FieldList : FieldList FunctionDef",
"FieldList :",
"Formals : VariableList",
"Formals :",
"VariableList : VariableList ',' Variable",
"VariableList : Variable",
"FunctionDef : STATIC Type IDENTIFIER '(' Formals ')' StmtBlock",
"FunctionDef : Type IDENTIFIER '(' Formals ')' StmtBlock",
"StmtBlock : '{' StmtList '}'",
"StmtList : StmtList Stmt",
"StmtList :",
"Stmt : VariableDef",
"Stmt : SimpleStmt ';'",
"Stmt : IfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"Stmt : ForeachStmt",
"Stmt : GuardedStmt",
"Stmt : ReturnStmt ';'",
"Stmt : PrintStmt ';'",
"Stmt : BreakStmt ';'",
"Stmt : StmtBlock",
"Stmt : Scopy ';'",
"GuardedStmt : IF '{' BranchList '}'",
"BranchList : BranchList GUARD IFSubStmt",
"BranchList : IFSubStmt",
"BranchList :",
"IFSubStmt : Expr ':' Stmt",
"Scopy : SCOPY '(' LValue ',' Expr ')'",
"SimpleStmt : LValue '=' Expr",
"SimpleStmt : Call",
"SimpleStmt :",
"Receiver : Expr '.'",
"Receiver :",
"LValue : Receiver IDENTIFIER",
"LValue : Expr '[' Expr ']'",
"LValue : VARIFIC IDENTIFIER",
"Call : Receiver IDENTIFIER '(' Actuals ')'",
"Expr : LValue",
"Expr : Call",
"Expr : Constant",
"Expr : Expr '+' Expr",
"Expr : Expr '-' Expr",
"Expr : Expr '*' Expr",
"Expr : Expr '/' Expr",
"Expr : Expr '%' Expr",
"Expr : Expr EQUAL Expr",
"Expr : Expr NOT_EQUAL Expr",
"Expr : Expr '<' Expr",
"Expr : Expr '>' Expr",
"Expr : Expr LESS_EQUAL Expr",
"Expr : Expr GREATER_EQUAL Expr",
"Expr : Expr AND Expr",
"Expr : Expr OR Expr",
"Expr : '(' Expr ')'",
"Expr : '-' Expr",
"Expr : '!' Expr",
"Expr : READ_INTEGER '(' ')'",
"Expr : READ_LINE '(' ')'",
"Expr : THIS",
"Expr : NEW IDENTIFIER '(' ')'",
"Expr : NEW Type '[' Expr ']'",
"Expr : INSTANCEOF '(' Expr ',' IDENTIFIER ')'",
"Expr : '(' CLASS IDENTIFIER ')' Expr",
"Expr : Expr REPEAT Expr",
"Expr : Expr '[' Expr ']' DEFT Expr",
"Constant : LITERAL",
"Constant : NULL",
"Actuals : ExprList",
"Actuals :",
"ExprList : ExprList ',' Expr",
"ExprList : Expr",
"WhileStmt : WHILE '(' Expr ')' Stmt",
"ForStmt : FOR '(' SimpleStmt ';' Expr ';' SimpleStmt ')' Stmt",
"ForeachStmt : FOREACH '(' Type IDENTIFIER IN Expr WHILECOND ')' Stmt",
"ForeachStmt : FOREACH '(' VARIFIC IDENTIFIER IN Expr WHILECOND ')' Stmt",
"WHILECOND : WHILE Expr",
"BreakStmt : BREAK",
"IfStmt : IF '(' Expr ')' Stmt ElseClause",
"ElseClause : ELSE Stmt",
"ElseClause :",
"ReturnStmt : RETURN Expr",
"ReturnStmt : RETURN",
"PrintStmt : PRINT '(' ExprList ')'",
};

//#line 513 "Parser.y"
    
	/**
	 * 打印当前归约所用的语法规则<br>
	 * 请勿修改。
	 */
    public boolean onReduce(String rule) {
		if (rule.startsWith("$$"))
			return false;
		else
			rule = rule.replaceAll(" \\$\\$\\d+", "");

   	    if (rule.endsWith(":"))
    	    System.out.println(rule + " <empty>");
   	    else
			System.out.println(rule);
		return false;
    }
    
    public void diagnose() {
		addReduceListener(this);
		yyparse();
	}
//#line 720 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    //if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      //if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        //if (yychar < 0)    //it it didn't work/error
        //  {
        //  yychar = 0;      //change it to default string (no -1!)
          //if (yydebug)
          //  yylexdebug(yystate,yychar);
        //  }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        //if (yydebug)
          //debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      //if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0 || valptr<0)   //check for under & overflow here
            {
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            //if (yydebug)
              //debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            //if (yydebug)
              //debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0 || valptr<0)   //check for under & overflow here
              {
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        //if (yydebug)
          //{
          //yys = null;
          //if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          //if (yys == null) yys = "illegal-symbol";
          //debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          //}
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    //if (yydebug)
      //debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    if (reduceListener == null || reduceListener.onReduce(yyrule[yyn])) // if intercepted!
      switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 58 "Parser.y"
{
						tree = new Tree.TopLevel(val_peek(0).clist, val_peek(0).loc);
					}
break;
case 2:
//#line 64 "Parser.y"
{
						yyval.clist.add(val_peek(0).cdef);
					}
break;
case 3:
//#line 68 "Parser.y"
{
                		yyval.clist = new ArrayList<Tree.ClassDef>();
                		yyval.clist.add(val_peek(0).cdef);
                	}
break;
case 5:
//#line 78 "Parser.y"
{
						yyval.vdef = new Tree.VarDef(val_peek(0).ident, val_peek(1).type, val_peek(0).loc);
					}
break;
case 6:
//#line 84 "Parser.y"
{
						yyval.type = new Tree.TypeIdent(Tree.INT, val_peek(0).loc);
					}
break;
case 7:
//#line 88 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.VOID, val_peek(0).loc);
                	}
break;
case 8:
//#line 92 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.BOOL, val_peek(0).loc);
                	}
break;
case 9:
//#line 96 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.STRING, val_peek(0).loc);
                	}
break;
case 10:
//#line 100 "Parser.y"
{
                		yyval.type = new Tree.TypeClass(val_peek(0).ident, val_peek(1).loc);
                	}
break;
case 11:
//#line 104 "Parser.y"
{
                		yyval.type = new Tree.TypeArray(val_peek(2).type, val_peek(2).loc);
                	}
break;
case 12:
//#line 110 "Parser.y"
{
						yyval.cdef = new Tree.ClassDef(false,val_peek(4).ident, val_peek(3).ident, val_peek(1).flist, val_peek(5).loc);
					}
break;
case 13:
//#line 114 "Parser.y"
{
						yyval.cdef = new Tree.ClassDef(true,val_peek(4).ident, val_peek(3).ident, val_peek(1).flist, val_peek(5).loc);
					}
break;
case 14:
//#line 120 "Parser.y"
{
						yyval.ident = val_peek(0).ident;
					}
break;
case 15:
//#line 124 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 16:
//#line 130 "Parser.y"
{
						yyval.flist.add(val_peek(0).vdef);
					}
break;
case 17:
//#line 134 "Parser.y"
{
						yyval.flist.add(val_peek(0).fdef);
					}
break;
case 18:
//#line 138 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.flist = new ArrayList<Tree>();
                	}
break;
case 20:
//#line 146 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.vlist = new ArrayList<Tree.VarDef>(); 
                	}
break;
case 21:
//#line 153 "Parser.y"
{
						yyval.vlist.add(val_peek(0).vdef);
					}
break;
case 22:
//#line 157 "Parser.y"
{
                		yyval.vlist = new ArrayList<Tree.VarDef>();
						yyval.vlist.add(val_peek(0).vdef);
                	}
break;
case 23:
//#line 164 "Parser.y"
{
						yyval.fdef = new MethodDef(true, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 24:
//#line 168 "Parser.y"
{
						yyval.fdef = new MethodDef(false, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 25:
//#line 174 "Parser.y"
{
						yyval.slist = val_peek(1).slist;
						yyval.stmt = new Block(val_peek(1).slist, val_peek(2).loc);
					}
break;
case 26:
//#line 181 "Parser.y"
{
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 27:
//#line 185 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.slist = new ArrayList<Tree>();
                	}
break;
case 28:
//#line 192 "Parser.y"
{
						yyval.stmt = val_peek(0).vdef;
					}
break;
case 29:
//#line 197 "Parser.y"
{
                		if (yyval.stmt == null) {
                			yyval.stmt = new Tree.Skip(val_peek(0).loc);
                		}
                	}
break;
case 40:
//#line 215 "Parser.y"
{
						yyval.stmt = new Tree.GuardStmt(val_peek(1).blist, val_peek(3).loc);/**/
					}
break;
case 41:
//#line 221 "Parser.y"
{
						yyval.blist.add(val_peek(0).substmt);/*取到的东西在第三个地方*/
					}
break;
case 42:
//#line 225 "Parser.y"
{
						yyval.blist = new ArrayList<Tree.SubStmt>();
						yyval.blist.add(val_peek(0).substmt);	
					}
break;
case 43:
//#line 230 "Parser.y"
{
						yyval = new SemValue();
						yyval.blist = new ArrayList<Tree.SubStmt>(); 
					}
break;
case 44:
//#line 237 "Parser.y"
{
						yyval.substmt = new Tree.SubStmt(val_peek(2).expr,val_peek(0).stmt,val_peek(1).loc);
					}
break;
case 45:
//#line 243 "Parser.y"
{
                		yyval.stmt = new Scopy( val_peek(3).lvalue, val_peek(1).expr, val_peek(5).loc ,val_peek(3).loc, val_peek(1).loc );
                	}
break;
case 46:
//#line 249 "Parser.y"
{
						yyval.stmt = new Tree.Assign(val_peek(2).lvalue, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 47:
//#line 253 "Parser.y"
{
                		yyval.stmt = new Tree.Exec(val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 48:
//#line 257 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 50:
//#line 264 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 51:
//#line 270 "Parser.y"
{
						yyval.lvalue = new Tree.Ident(val_peek(1).expr, val_peek(0).ident, val_peek(0).loc);
						if (val_peek(1).loc == null) {
							yyval.loc = val_peek(0).loc;
						}
					}
break;
case 52:
//#line 277 "Parser.y"
{
                		yyval.lvalue = new Tree.Indexed(val_peek(3).expr, val_peek(1).expr, val_peek(3).loc);
                	}
break;
case 53:
//#line 281 "Parser.y"
{
                		yyval.lvalue = new Tree.VarValue(val_peek(0).ident,val_peek(0).loc);
                	}
break;
case 54:
//#line 287 "Parser.y"
{
						yyval.expr = new Tree.CallExpr(val_peek(4).expr, val_peek(3).ident, val_peek(1).elist, val_peek(3).loc);
						if (val_peek(4).loc == null) {
							yyval.loc = val_peek(3).loc;
						}
					}
break;
case 55:
//#line 296 "Parser.y"
{
						yyval.expr = val_peek(0).lvalue;
					}
break;
case 58:
//#line 302 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.PLUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 59:
//#line 306 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MINUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 60:
//#line 310 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MUL, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 61:
//#line 314 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.DIV, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 62:
//#line 318 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MOD, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 63:
//#line 322 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.EQ, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 64:
//#line 326 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.NE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 65:
//#line 330 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 66:
//#line 334 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 67:
//#line 338 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 68:
//#line 342 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 69:
//#line 346 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.AND, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 70:
//#line 350 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.OR, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 71:
//#line 354 "Parser.y"
{
                		yyval = val_peek(1);
                	}
break;
case 72:
//#line 358 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NEG, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 73:
//#line 362 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NOT, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 74:
//#line 366 "Parser.y"
{
                		yyval.expr = new Tree.ReadIntExpr(val_peek(2).loc);
                	}
break;
case 75:
//#line 370 "Parser.y"
{
                		yyval.expr = new Tree.ReadLineExpr(val_peek(2).loc);
                	}
break;
case 76:
//#line 374 "Parser.y"
{
                		yyval.expr = new Tree.ThisExpr(val_peek(0).loc);
                	}
break;
case 77:
//#line 378 "Parser.y"
{
                		yyval.expr = new Tree.NewClass(val_peek(2).ident, val_peek(3).loc);
                	}
break;
case 78:
//#line 382 "Parser.y"
{
                		yyval.expr = new Tree.NewArray(val_peek(3).type, val_peek(1).expr, val_peek(4).loc);
                	}
break;
case 79:
//#line 386 "Parser.y"
{
                		yyval.expr = new Tree.TypeTest(val_peek(3).expr, val_peek(1).ident, val_peek(5).loc);
                	}
break;
case 80:
//#line 390 "Parser.y"
{
                		yyval.expr = new Tree.TypeCast(val_peek(2).ident, val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 81:
//#line 394 "Parser.y"
{
                		yyval.expr = new Tree.TimeArrayConst(val_peek(2).expr, val_peek(0).expr, val_peek(2).loc);
                	}
break;
case 82:
//#line 398 "Parser.y"
{
                		yyval.expr = new Tree.DefaultConst(val_peek(5).expr,val_peek(3).expr,val_peek(0).expr,val_peek(5).loc);
                	}
break;
case 83:
//#line 404 "Parser.y"
{
						yyval.expr = new Tree.Literal(val_peek(0).typeTag, val_peek(0).literal, val_peek(0).loc);
					}
break;
case 84:
//#line 408 "Parser.y"
{
						yyval.expr = new Null(val_peek(0).loc);
					}
break;
case 86:
//#line 415 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.elist = new ArrayList<Tree.Expr>();
                	}
break;
case 87:
//#line 422 "Parser.y"
{
						yyval.elist.add(val_peek(0).expr);
					}
break;
case 88:
//#line 426 "Parser.y"
{
                		yyval.elist = new ArrayList<Tree.Expr>();
						yyval.elist.add(val_peek(0).expr);
                	}
break;
case 89:
//#line 433 "Parser.y"
{
						yyval.stmt = new Tree.WhileLoop(val_peek(2).expr, val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 90:
//#line 439 "Parser.y"
{
						yyval.stmt = new Tree.ForLoop(val_peek(6).stmt, val_peek(4).expr, val_peek(2).stmt, val_peek(0).stmt, val_peek(8).loc);
					}
break;
case 91:
//#line 445 "Parser.y"
{
						if(val_peek(0).stmt.tag != Tree.BLOCK){/*如果不是BLOCK*/
							val_peek(0).slist = new ArrayList<Tree>();
							val_peek(0).slist.add(val_peek(0).stmt);
							yyval.stmt = new Tree.ForeachLoop(new Tree.VarDef(val_peek(5).ident,val_peek(6).type, val_peek(6).loc),null, val_peek(3).expr, val_peek(2).expr, new Tree.Block(val_peek(0).slist,val_peek(0).stmt.loc), val_peek(8).loc);/*转化成block*/
						}/*那么转换到block中去*/
						else{
							yyval.stmt = new Tree.ForeachLoop(new Tree.VarDef(val_peek(5).ident,val_peek(6).type, val_peek(6).loc),null, val_peek(3).expr, val_peek(2).expr, new Tree.Block(val_peek(0).slist,val_peek(0).stmt.loc), val_peek(8).loc);
						}
					}
break;
case 92:
//#line 456 "Parser.y"
{
						if(val_peek(0).stmt.tag != Tree.BLOCK){/*如果不是BLOCK*/
							val_peek(0).slist = new ArrayList<Tree>();
							val_peek(0).slist.add(val_peek(0).stmt);
							yyval.stmt = new Tree.ForeachLoop(null,new Tree.VarValue(val_peek(5).ident,val_peek(6).loc), val_peek(3).expr, val_peek(2).expr, new Tree.Block(val_peek(0).slist, val_peek(0).stmt.loc), val_peek(8).loc);/*转化成block*/
						}/*那么转换到block中去*/
						else{
							yyval.stmt = new Tree.ForeachLoop(null,new Tree.VarValue(val_peek(5).ident,val_peek(6).loc), val_peek(3).expr, val_peek(2).expr, new Tree.Block(val_peek(0).slist,val_peek(0).stmt.loc), val_peek(8).loc);
						}
					}
break;
case 93:
//#line 469 "Parser.y"
{
						yyval.expr = val_peek(0).expr;
					}
break;
case 94:
//#line 475 "Parser.y"
{
						yyval.stmt = new Tree.Break(val_peek(0).loc);
					}
break;
case 95:
//#line 481 "Parser.y"
{
						yyval.stmt = new Tree.If(val_peek(3).expr, val_peek(1).stmt, val_peek(0).stmt, val_peek(5).loc);
					}
break;
case 96:
//#line 487 "Parser.y"
{
						yyval.stmt = val_peek(0).stmt;
					}
break;
case 97:
//#line 491 "Parser.y"
{
						yyval = new SemValue();
					}
break;
case 98:
//#line 497 "Parser.y"
{
						yyval.stmt = new Tree.Return(val_peek(0).expr, val_peek(1).loc);
					}
break;
case 99:
//#line 501 "Parser.y"
{
                		yyval.stmt = new Tree.Return(null, val_peek(0).loc);
                	}
break;
case 100:
//#line 507 "Parser.y"
{
						yyval.stmt = new Print(val_peek(1).elist, val_peek(3).loc);
					}
break;
//#line 1402 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    //if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      //if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        //if (yychar<0) yychar=0;  //clean, if necessary
        //if (yydebug)
          //yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      //if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
