// $ANTLR 3.1.2 /Users/marko/git/jnome/src/jnome/input/parser/Java.g 2009-06-18 18:07:49

package jnome.input.parser;

import chameleon.core.MetamodelException;

import chameleon.core.context.ContextFactory;

import chameleon.core.compilationunit.CompilationUnit;

import chameleon.core.declaration.SimpleNameSignature;

import chameleon.core.element.ChameleonProgrammerException;

import chameleon.core.expression.ActualParameter;
import chameleon.core.expression.Expression;

import chameleon.core.language.Language;

import chameleon.core.member.Member;

import chameleon.core.method.Method;
import chameleon.core.method.Implementation;
import chameleon.core.method.RegularImplementation;

import chameleon.core.method.exception.ExceptionClause;
import chameleon.core.method.exception.TypeExceptionDeclaration;

import chameleon.core.modifier.Modifier;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;

import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.Import;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.namespacepart.DemandImport;

import chameleon.core.statement.Block;

import chameleon.core.type.ClassBody;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.TypeElement;

import chameleon.core.type.generics.GenericParameter;
import chameleon.core.type.generics.TypeConstraint;
import chameleon.core.type.generics.ExtendsConstraint;

import chameleon.core.type.inheritance.SubtypeRelation;

import chameleon.core.variable.Variable;
import chameleon.core.variable.FormalParameter;

import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.SimpleNameMethodHeader;
import chameleon.support.member.simplename.variable.VariableDeclaration;
import chameleon.support.member.simplename.variable.VariableDeclarator;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Protected;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Enum;
import chameleon.support.modifier.Interface;

import chameleon.support.type.EmptyTypeElement;
import chameleon.support.type.StaticInitializer;

import jnome.core.language.Java;

import jnome.core.type.JavaTypeReference;

import jnome.core.modifier.StrictFP;
import jnome.core.modifier.Transient;
import jnome.core.modifier.Volatile;
import jnome.core.modifier.Synchronized;

import jnome.core.enumeration.EnumConstant;

import jnome.core.variable.JavaVariableDeclaration;

import java.util.List;
import java.util.ArrayList;




import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */
public class JavaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Identifier", "ENUM", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "ASSERT", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'package'", "';'", "'import'", "'static'", "'.'", "'*'", "'public'", "'protected'", "'private'", "'abstract'", "'final'", "'strictfp'", "'class'", "'extends'", "'implements'", "'<'", "','", "'>'", "'&'", "'{'", "'}'", "'interface'", "'void'", "'['", "']'", "'throws'", "'='", "'native'", "'synchronized'", "'transient'", "'volatile'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'?'", "'super'", "'('", "')'", "'...'", "'this'", "'null'", "'true'", "'false'", "'@'", "'default'", "':'", "'if'", "'else'", "'for'", "'while'", "'do'", "'try'", "'finally'", "'switch'", "'return'", "'throw'", "'break'", "'continue'", "'catch'", "'case'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'||'", "'&&'", "'|'", "'^'", "'=='", "'!='", "'instanceof'", "'+'", "'-'", "'/'", "'%'", "'++'", "'--'", "'~'", "'!'", "'new'"
    };
    public static final int T__42=42;
    public static final int HexDigit=13;
    public static final int T__47=47;
    public static final int T__109=109;
    public static final int T__73=73;
    public static final int T__72=72;
    public static final int T__70=70;
    public static final int T__39=39;
    public static final int Letter=20;
    public static final int T__30=30;
    public static final int T__46=46;
    public static final int T__96=96;
    public static final int T__49=49;
    public static final int ASSERT=12;
    public static final int T__108=108;
    public static final int T__112=112;
    public static final int T__54=54;
    public static final int T__48=48;
    public static final int FloatTypeSuffix=16;
    public static final int T__113=113;
    public static final int IntegerTypeSuffix=14;
    public static final int Identifier=4;
    public static final int T__89=89;
    public static final int WS=22;
    public static final int T__79=79;
    public static final int T__64=64;
    public static final int T__44=44;
    public static final int T__66=66;
    public static final int T__92=92;
    public static final int T__88=88;
    public static final int LINE_COMMENT=24;
    public static final int T__90=90;
    public static final int UnicodeEscape=18;
    public static final int HexLiteral=9;
    public static final int T__63=63;
    public static final int T__110=110;
    public static final int T__43=43;
    public static final int T__91=91;
    public static final int T__40=40;
    public static final int DecimalLiteral=11;
    public static final int T__85=85;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__60=60;
    public static final int T__41=41;
    public static final int T__93=93;
    public static final int T__86=86;
    public static final int T__28=28;
    public static final int OctalLiteral=10;
    public static final int T__57=57;
    public static final int T__94=94;
    public static final int T__51=51;
    public static final int T__80=80;
    public static final int T__100=100;
    public static final int T__69=69;
    public static final int T__95=95;
    public static final int T__50=50;
    public static final int T__65=65;
    public static final int T__101=101;
    public static final int T__104=104;
    public static final int T__107=107;
    public static final int T__67=67;
    public static final int T__87=87;
    public static final int T__106=106;
    public static final int T__74=74;
    public static final int T__52=52;
    public static final int T__68=68;
    public static final int T__62=62;
    public static final int EscapeSequence=17;
    public static final int T__27=27;
    public static final int T__61=61;
    public static final int T__59=59;
    public static final int T__34=34;
    public static final int FloatingPointLiteral=6;
    public static final int T__98=98;
    public static final int T__56=56;
    public static final int ENUM=5;
    public static final int T__35=35;
    public static final int Exponent=15;
    public static final int T__78=78;
    public static final int T__36=36;
    public static final int CharacterLiteral=7;
    public static final int T__58=58;
    public static final int COMMENT=23;
    public static final int T__99=99;
    public static final int StringLiteral=8;
    public static final int T__33=33;
    public static final int T__77=77;
    public static final int T__55=55;
    public static final int T__45=45;
    public static final int T__29=29;
    public static final int T__103=103;
    public static final int JavaIDDigit=21;
    public static final int T__84=84;
    public static final int T__97=97;
    public static final int T__75=75;
    public static final int T__105=105;
    public static final int T__111=111;
    public static final int T__31=31;
    public static final int EOF=-1;
    public static final int T__53=53;
    public static final int T__32=32;
    public static final int T__38=38;
    public static final int T__37=37;
    public static final int T__76=76;
    public static final int T__82=82;
    public static final int OctalEscape=19;
    public static final int T__81=81;
    public static final int T__83=83;
    public static final int T__71=71;
    public static final int T__102=102;

    // delegates
    // delegators

    protected static class MethodScope_scope {
        Method method;
    }
    protected Stack MethodScope_stack = new Stack();


        public JavaParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JavaParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[404+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return JavaParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/marko/git/jnome/src/jnome/input/parser/Java.g"; }



      public static class StupidVariableDeclaratorId {
           public StupidVariableDeclaratorId(String name, int dimension) {
             _name = name;
             _dimension = dimension;
           }
           private final String _name;
           private final int _dimension;
           
           public String name() {
             return _name;
           }
           
           public int dimension() {
             return _dimension;
           }
      }

      Language lang;
      
      public Language language() {
        return lang;
      }
      
      public void setLanguage(Java language) {
        lang = language;
      }
      
      RootNamespace root = lang.defaultNamespace();
      ContextFactory contextFactory = root.language().contextFactory();

      public Namespace getDefaultNamespace() {
        return root;
      }

      public void processImport(CompilationUnit cu, Import imp){
        if(imp instanceof TypeImport){cu.getDefaultNamespacePart().addImport((TypeImport)imp);}
        else if(imp instanceof DemandImport){cu.getDefaultNamespacePart().addImport((DemandImport)imp);}
      }
      public void processType(CompilationUnit cu, Type type){
        cu.getDefaultNamespacePart().addType(type);
      }
      public void processPackageDeclaration(CompilationUnit cu, NamespacePart np){
        if(np!=null){
          cu.getDefaultNamespacePart().addNamespacePart(np);
        }
      }
      
      public TypeReference typeRef(String qn) {
        return new JavaTypeReference(qn);
      }



    public static class compilationUnit_return extends ParserRuleReturnScope {
        public CompilationUnit element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compilationUnit"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:340:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );
    public final JavaParser.compilationUnit_return compilationUnit() throws RecognitionException {
        JavaParser.compilationUnit_return retval = new JavaParser.compilationUnit_return();
        retval.start = input.LT(1);
        int compilationUnit_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.packageDeclaration_return np = null;

        JavaParser.importDeclaration_return imp = null;

        JavaParser.typeDeclaration_return typech = null;

        JavaParser.classOrInterfaceDeclaration_return cd = null;

        JavaParser.annotations_return annotations1 = null;



         retval.element = new CompilationUnit(new NamespacePart(language().defaultNamespace()));
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:5: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotations_in_compilationUnit96);
                    annotations1=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==25) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==ENUM||LA4_0==28||(LA4_0>=31 && LA4_0<=37)||LA4_0==46||LA4_0==73) ) {
                        alt4=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit112);
                            np=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, np.getTree());
                            if ( state.backtracking==0 ) {
                              processPackageDeclaration(retval.element,np.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:90: (imp= importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==27) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:91: imp= importDeclaration
                            	    {
                            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit118);
                            	    imp=importDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, imp.getTree());
                            	    if ( state.backtracking==0 ) {
                            	      processImport(retval.element,imp.element);
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop1;
                                }
                            } while (true);

                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:159: (typech= typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==ENUM||LA2_0==26||LA2_0==28||(LA2_0>=31 && LA2_0<=37)||LA2_0==46||LA2_0==73) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:160: typech= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit126);
                            	    typech=typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typech.getTree());
                            	    if ( state.backtracking==0 ) {
                            	      processType(retval.element,typech.element);
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop2;
                                }
                            } while (true);


                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:344:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit145);
                            cd=classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                            if ( state.backtracking==0 ) {
                              processType(retval.element,cd.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:344:85: (typech= typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==ENUM||LA3_0==26||LA3_0==28||(LA3_0>=31 && LA3_0<=37)||LA3_0==46||LA3_0==73) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:344:86: typech= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit151);
                            	    typech=typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typech.getTree());
                            	    if ( state.backtracking==0 ) {
                            	      processType(retval.element,typech.element);
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop3;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:9: (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )*
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:9: (np= packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==25) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:10: np= packageDeclaration
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit177);
                            np=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, np.getTree());
                            if ( state.backtracking==0 ) {
                              processPackageDeclaration(retval.element,np.element);
                            }

                            }
                            break;

                    }

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:89: (imp= importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==27) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:90: imp= importDeclaration
                    	    {
                    	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit185);
                    	    imp=importDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, imp.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      processImport(retval.element,imp.element);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:158: (typech= typeDeclaration )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ENUM||LA7_0==26||LA7_0==28||(LA7_0>=31 && LA7_0<=37)||LA7_0==46||LA7_0==73) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:159: typech= typeDeclaration
                    	    {
                    	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit193);
                    	    typech=typeDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typech.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      processType(retval.element,typech.element);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "compilationUnit"

    public static class packageDeclaration_return extends ParserRuleReturnScope {
        public NamespacePart element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "packageDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:349:1: packageDeclaration returns [NamespacePart element] : 'package' qn= qualifiedName ';' ;
    public final JavaParser.packageDeclaration_return packageDeclaration() throws RecognitionException {
        JavaParser.packageDeclaration_return retval = new JavaParser.packageDeclaration_return();
        retval.start = input.LT(1);
        int packageDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal2=null;
        Token char_literal3=null;
        JavaParser.qualifiedName_return qn = null;


        Object string_literal2_tree=null;
        Object char_literal3_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:5: ( 'package' qn= qualifiedName ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:9: 'package' qn= qualifiedName ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal2=(Token)match(input,25,FOLLOW_25_in_packageDeclaration219); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal2_tree = (Object)adaptor.create(string_literal2);
            adaptor.addChild(root_0, string_literal2_tree);
            }
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration223);
            qn=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qn.getTree());
            char_literal3=(Token)match(input,26,FOLLOW_26_in_packageDeclaration225); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal3_tree = (Object)adaptor.create(char_literal3);
            adaptor.addChild(root_0, char_literal3_tree);
            }
            if ( state.backtracking==0 ) {
              try{
                         retval.element = new NamespacePart(root.getOrCreateNamespace((qn!=null?input.toString(qn.start,qn.stop):null)));
                       }
                       catch(MetamodelException exc) {
                         //this should not happen, something is wrong with the tree parser
                         throw new ChameleonProgrammerException(exc);
                       }
                      
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "packageDeclaration"

    public static class importDeclaration_return extends ParserRuleReturnScope {
        public Import element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "importDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:361:1: importDeclaration returns [Import element] : 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' ;
    public final JavaParser.importDeclaration_return importDeclaration() throws RecognitionException {
        JavaParser.importDeclaration_return retval = new JavaParser.importDeclaration_return();
        retval.start = input.LT(1);
        int importDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token st=null;
        Token star=null;
        Token string_literal4=null;
        Token char_literal5=null;
        Token char_literal6=null;
        Token char_literal7=null;
        JavaParser.qualifiedName_return qn = null;


        Object st_tree=null;
        Object star_tree=null;
        Object string_literal4_tree=null;
        Object char_literal5_tree=null;
        Object char_literal6_tree=null;
        Object char_literal7_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:362:5: ( 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:362:9: 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal4=(Token)match(input,27,FOLLOW_27_in_importDeclaration263); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal4_tree = (Object)adaptor.create(string_literal4);
            adaptor.addChild(root_0, string_literal4_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:362:20: (st= 'static' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==28) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: st= 'static'
                    {
                    st=(Token)match(input,28,FOLLOW_28_in_importDeclaration267); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    st_tree = (Object)adaptor.create(st);
                    adaptor.addChild(root_0, st_tree);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_qualifiedName_in_importDeclaration272);
            qn=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qn.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:362:52: (star= ( '.' '*' ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==29) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: star= ( '.' '*' )
                    {
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:362:53: ( '.' '*' )
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:362:54: '.' '*'
                    {
                    char_literal5=(Token)match(input,29,FOLLOW_29_in_importDeclaration277); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal5_tree = (Object)adaptor.create(char_literal5);
                    adaptor.addChild(root_0, char_literal5_tree);
                    }
                    char_literal6=(Token)match(input,30,FOLLOW_30_in_importDeclaration279); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal6_tree = (Object)adaptor.create(char_literal6);
                    adaptor.addChild(root_0, char_literal6_tree);
                    }

                    }


                    }
                    break;

            }

            char_literal7=(Token)match(input,26,FOLLOW_26_in_importDeclaration283); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal7_tree = (Object)adaptor.create(char_literal7);
            adaptor.addChild(root_0, char_literal7_tree);
            }
            if ( state.backtracking==0 ) {

                       if(star==null) {
                         retval.element = new TypeImport(typeRef((qn!=null?input.toString(qn.start,qn.stop):null)));
                         // type import
                       } else {
                         // demand import
                         retval.element = new DemandImport(typeRef((qn!=null?input.toString(qn.start,qn.stop):null)));
                       }
                  
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "importDeclaration"

    public static class typeDeclaration_return extends ParserRuleReturnScope {
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:375:1: typeDeclaration returns [Type element] : (cd= classOrInterfaceDeclaration | ';' );
    public final JavaParser.typeDeclaration_return typeDeclaration() throws RecognitionException {
        JavaParser.typeDeclaration_return retval = new JavaParser.typeDeclaration_return();
        retval.start = input.LT(1);
        int typeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal8=null;
        JavaParser.classOrInterfaceDeclaration_return cd = null;


        Object char_literal8_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:376:5: (cd= classOrInterfaceDeclaration | ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ENUM||LA11_0==28||(LA11_0>=31 && LA11_0<=37)||LA11_0==46||LA11_0==73) ) {
                alt11=1;
            }
            else if ( (LA11_0==26) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:376:9: cd= classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration319);
                    cd=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = cd.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal8=(Token)match(input,26,FOLLOW_26_in_typeDeclaration331); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal8_tree = (Object)adaptor.create(char_literal8);
                    adaptor.addChild(root_0, char_literal8_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, typeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeDeclaration"

    public static class classOrInterfaceDeclaration_return extends ParserRuleReturnScope {
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:383:1: classOrInterfaceDeclaration returns [Type element] : mods= classOrInterfaceModifiers (cd= classDeclaration | id= interfaceDeclaration ) ;
    public final JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        JavaParser.classOrInterfaceDeclaration_return retval = new JavaParser.classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int classOrInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifiers_return mods = null;

        JavaParser.classDeclaration_return cd = null;

        JavaParser.interfaceDeclaration_return id = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:5: (mods= classOrInterfaceModifiers (cd= classDeclaration | id= interfaceDeclaration ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:9: mods= classOrInterfaceModifiers (cd= classDeclaration | id= interfaceDeclaration )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration363);
            mods=classOrInterfaceModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, mods.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:40: (cd= classDeclaration | id= interfaceDeclaration )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ENUM||LA12_0==37) ) {
                alt12=1;
            }
            else if ( (LA12_0==46||LA12_0==73) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:41: cd= classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration368);
                    cd=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=cd.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:92: id= interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration376);
                    id=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=id.element;
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              for(Modifier mod:mods.element) {
                        retval.element.addModifier(mod);
                      }
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 5, classOrInterfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceDeclaration"

    public static class classOrInterfaceModifiers_return extends ParserRuleReturnScope {
        public List<Modifier> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceModifiers"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:390:1: classOrInterfaceModifiers returns [List<Modifier> element] : (mod= classOrInterfaceModifier )* ;
    public final JavaParser.classOrInterfaceModifiers_return classOrInterfaceModifiers() throws RecognitionException {
        JavaParser.classOrInterfaceModifiers_return retval = new JavaParser.classOrInterfaceModifiers_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifier_return mod = null;



        retval.element = new ArrayList<Modifier>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:392:5: ( (mod= classOrInterfaceModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:392:9: (mod= classOrInterfaceModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:392:9: (mod= classOrInterfaceModifier )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==73) ) {
                    int LA13_2 = input.LA(2);

                    if ( (LA13_2==Identifier) ) {
                        alt13=1;
                    }


                }
                else if ( (LA13_0==28||(LA13_0>=31 && LA13_0<=36)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:392:10: mod= classOrInterfaceModifier
            	    {
            	    pushFollow(FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers425);
            	    mod=classOrInterfaceModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, mod.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(mod.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, classOrInterfaceModifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceModifiers"

    public static class classOrInterfaceModifier_return extends ParserRuleReturnScope {
        public Modifier element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceModifier"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:395:1: classOrInterfaceModifier returns [Modifier element] : ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' );
    public final JavaParser.classOrInterfaceModifier_return classOrInterfaceModifier() throws RecognitionException {
        JavaParser.classOrInterfaceModifier_return retval = new JavaParser.classOrInterfaceModifier_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal10=null;
        Token string_literal11=null;
        Token string_literal12=null;
        Token string_literal13=null;
        Token string_literal14=null;
        Token string_literal15=null;
        Token string_literal16=null;
        JavaParser.annotation_return annotation9 = null;


        Object string_literal10_tree=null;
        Object string_literal11_tree=null;
        Object string_literal12_tree=null;
        Object string_literal13_tree=null;
        Object string_literal14_tree=null;
        Object string_literal15_tree=null;
        Object string_literal16_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:396:5: ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' )
            int alt14=8;
            switch ( input.LA(1) ) {
            case 73:
                {
                alt14=1;
                }
                break;
            case 31:
                {
                alt14=2;
                }
                break;
            case 32:
                {
                alt14=3;
                }
                break;
            case 33:
                {
                alt14=4;
                }
                break;
            case 34:
                {
                alt14=5;
                }
                break;
            case 28:
                {
                alt14=6;
                }
                break;
            case 35:
                {
                alt14=7;
                }
                break;
            case 36:
                {
                alt14=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:396:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_classOrInterfaceModifier452);
                    annotation9=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation9.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:397:9: 'public'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal10=(Token)match(input,31,FOLLOW_31_in_classOrInterfaceModifier465); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal10_tree = (Object)adaptor.create(string_literal10);
                    adaptor.addChild(root_0, string_literal10_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Public();
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:398:9: 'protected'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal11=(Token)match(input,32,FOLLOW_32_in_classOrInterfaceModifier481); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal11_tree = (Object)adaptor.create(string_literal11);
                    adaptor.addChild(root_0, string_literal11_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Protected();
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:9: 'private'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal12=(Token)match(input,33,FOLLOW_33_in_classOrInterfaceModifier494); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal12_tree = (Object)adaptor.create(string_literal12);
                    adaptor.addChild(root_0, string_literal12_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Private();
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:400:9: 'abstract'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal13=(Token)match(input,34,FOLLOW_34_in_classOrInterfaceModifier509); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal13_tree = (Object)adaptor.create(string_literal13);
                    adaptor.addChild(root_0, string_literal13_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Abstract();
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:401:9: 'static'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal14=(Token)match(input,28,FOLLOW_28_in_classOrInterfaceModifier523); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal14_tree = (Object)adaptor.create(string_literal14);
                    adaptor.addChild(root_0, string_literal14_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Static();
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal15=(Token)match(input,35,FOLLOW_35_in_classOrInterfaceModifier539); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal15_tree = (Object)adaptor.create(string_literal15);
                    adaptor.addChild(root_0, string_literal15_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Final();
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:403:9: 'strictfp'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal16=(Token)match(input,36,FOLLOW_36_in_classOrInterfaceModifier556); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal16_tree = (Object)adaptor.create(string_literal16);
                    adaptor.addChild(root_0, string_literal16_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new StrictFP();
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, classOrInterfaceModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceModifier"

    public static class modifiers_return extends ParserRuleReturnScope {
        public List<Modifier> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifiers"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:1: modifiers returns [List<Modifier> element] : (mod= modifier )* ;
    public final JavaParser.modifiers_return modifiers() throws RecognitionException {
        JavaParser.modifiers_return retval = new JavaParser.modifiers_return();
        retval.start = input.LT(1);
        int modifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifier_return mod = null;



        retval.element = new ArrayList<Modifier>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:408:5: ( (mod= modifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:408:9: (mod= modifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:408:9: (mod= modifier )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==73) ) {
                    int LA15_2 = input.LA(2);

                    if ( (LA15_2==Identifier) ) {
                        alt15=1;
                    }


                }
                else if ( (LA15_0==28||(LA15_0>=31 && LA15_0<=36)||(LA15_0>=52 && LA15_0<=55)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:408:10: mod= modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_modifiers591);
            	    mod=modifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, mod.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(mod.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, modifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "modifiers"

    public static class classDeclaration_return extends ParserRuleReturnScope {
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:411:1: classDeclaration returns [Type element] : (cd= normalClassDeclaration | ed= enumDeclaration );
    public final JavaParser.classDeclaration_return classDeclaration() throws RecognitionException {
        JavaParser.classDeclaration_return retval = new JavaParser.classDeclaration_return();
        retval.start = input.LT(1);
        int classDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalClassDeclaration_return cd = null;

        JavaParser.enumDeclaration_return ed = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:412:5: (cd= normalClassDeclaration | ed= enumDeclaration )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==37) ) {
                alt16=1;
            }
            else if ( (LA16_0==ENUM) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:412:9: cd= normalClassDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration619);
                    cd=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                    if ( state.backtracking==0 ) {
                       retval.element = cd.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:413:9: ed= enumDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration633);
                    ed=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ed.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = ed.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, classDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classDeclaration"

    public static class normalClassDeclaration_return extends ParserRuleReturnScope {
        public RegularType element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "normalClassDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:416:1: normalClassDeclaration returns [RegularType element] : 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' trefs= typeList )? body= classBody ;
    public final JavaParser.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        JavaParser.normalClassDeclaration_return retval = new JavaParser.normalClassDeclaration_return();
        retval.start = input.LT(1);
        int normalClassDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token string_literal17=null;
        Token string_literal18=null;
        Token string_literal19=null;
        JavaParser.typeParameters_return params = null;

        JavaParser.type_return sc = null;

        JavaParser.typeList_return trefs = null;

        JavaParser.classBody_return body = null;


        Object name_tree=null;
        Object string_literal17_tree=null;
        Object string_literal18_tree=null;
        Object string_literal19_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:5: ( 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' trefs= typeList )? body= classBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:9: 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' trefs= typeList )? body= classBody
            {
            root_0 = (Object)adaptor.nil();

            string_literal17=(Token)match(input,37,FOLLOW_37_in_normalClassDeclaration662); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal17_tree = (Object)adaptor.create(string_literal17);
            adaptor.addChild(root_0, string_literal17_tree);
            }
            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration666); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new RegularType(new SimpleNameSignature((name!=null?name.getText():null)));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:106: (params= typeParameters )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==40) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:107: params= typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration673);
                    params=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, params.getTree());
                    if ( state.backtracking==0 ) {
                      for(GenericParameter par: params.element){retval.element.add(par);}
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:9: ( 'extends' sc= type )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==38) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:10: 'extends' sc= type
                    {
                    string_literal18=(Token)match(input,38,FOLLOW_38_in_normalClassDeclaration687); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal18_tree = (Object)adaptor.create(string_literal18);
                    adaptor.addChild(root_0, string_literal18_tree);
                    }
                    pushFollow(FOLLOW_type_in_normalClassDeclaration691);
                    sc=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, sc.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.addInheritanceRelation(new SubtypeRelation(sc.element));
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:9: ( 'implements' trefs= typeList )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==39) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:10: 'implements' trefs= typeList
                    {
                    string_literal19=(Token)match(input,39,FOLLOW_39_in_normalClassDeclaration705); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal19_tree = (Object)adaptor.create(string_literal19);
                    adaptor.addChild(root_0, string_literal19_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration709);
                    trefs=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, trefs.getTree());
                    if ( state.backtracking==0 ) {
                      for(TypeReference ref: trefs.element){retval.element.addInheritanceRelation(new SubtypeRelation(ref));} 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration726);
            body=classBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());
            if ( state.backtracking==0 ) {
              retval.element.setBody(body.element);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, normalClassDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "normalClassDeclaration"

    public static class typeParameters_return extends ParserRuleReturnScope {
        public List<GenericParameter> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeParameters"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:423:1: typeParameters returns [List<GenericParameter> element] : '<' par= typeParameter ( ',' par= typeParameter )* '>' ;
    public final JavaParser.typeParameters_return typeParameters() throws RecognitionException {
        JavaParser.typeParameters_return retval = new JavaParser.typeParameters_return();
        retval.start = input.LT(1);
        int typeParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal20=null;
        Token char_literal21=null;
        Token char_literal22=null;
        JavaParser.typeParameter_return par = null;


        Object char_literal20_tree=null;
        Object char_literal21_tree=null;
        Object char_literal22_tree=null;

        retval.element = new ArrayList<GenericParameter>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:5: ( '<' par= typeParameter ( ',' par= typeParameter )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:9: '<' par= typeParameter ( ',' par= typeParameter )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal20=(Token)match(input,40,FOLLOW_40_in_typeParameters759); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal20_tree = (Object)adaptor.create(char_literal20);
            adaptor.addChild(root_0, char_literal20_tree);
            }
            pushFollow(FOLLOW_typeParameter_in_typeParameters763);
            par=typeParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par.getTree());
            if ( state.backtracking==0 ) {
              retval.element.add(par.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:65: ( ',' par= typeParameter )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==41) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:66: ',' par= typeParameter
            	    {
            	    char_literal21=(Token)match(input,41,FOLLOW_41_in_typeParameters767); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal21_tree = (Object)adaptor.create(char_literal21);
            	    adaptor.addChild(root_0, char_literal21_tree);
            	    }
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters771);
            	    par=typeParameter();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, par.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(par.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            char_literal22=(Token)match(input,42,FOLLOW_42_in_typeParameters776); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal22_tree = (Object)adaptor.create(char_literal22);
            adaptor.addChild(root_0, char_literal22_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, typeParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameters"

    public static class typeParameter_return extends ParserRuleReturnScope {
        public GenericParameter element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeParameter"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:428:1: typeParameter returns [GenericParameter element] : name= Identifier ( 'extends' bound= typeBound )? ;
    public final JavaParser.typeParameter_return typeParameter() throws RecognitionException {
        JavaParser.typeParameter_return retval = new JavaParser.typeParameter_return();
        retval.start = input.LT(1);
        int typeParameter_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token string_literal23=null;
        JavaParser.typeBound_return bound = null;


        Object name_tree=null;
        Object string_literal23_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:429:5: (name= Identifier ( 'extends' bound= typeBound )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:429:9: name= Identifier ( 'extends' bound= typeBound )?
            {
            root_0 = (Object)adaptor.nil();

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeParameter801); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new GenericParameter(new SimpleNameSignature((name!=null?name.getText():null)));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:429:102: ( 'extends' bound= typeBound )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==38) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:429:103: 'extends' bound= typeBound
                    {
                    string_literal23=(Token)match(input,38,FOLLOW_38_in_typeParameter805); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal23_tree = (Object)adaptor.create(string_literal23);
                    adaptor.addChild(root_0, string_literal23_tree);
                    }
                    pushFollow(FOLLOW_typeBound_in_typeParameter809);
                    bound=typeBound();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bound.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.addConstraint(bound.element);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 12, typeParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameter"

    public static class typeBound_return extends ParserRuleReturnScope {
        public ExtendsConstraint element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeBound"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:432:1: typeBound returns [ExtendsConstraint element] : tp= type ( '&' tpp= type )* ;
    public final JavaParser.typeBound_return typeBound() throws RecognitionException {
        JavaParser.typeBound_return retval = new JavaParser.typeBound_return();
        retval.start = input.LT(1);
        int typeBound_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal24=null;
        JavaParser.type_return tp = null;

        JavaParser.type_return tpp = null;


        Object char_literal24_tree=null;

        retval.element = new ExtendsConstraint();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:434:5: (tp= type ( '&' tpp= type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:434:9: tp= type ( '&' tpp= type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeBound849);
            tp=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, tp.getTree());
            if ( state.backtracking==0 ) {
              retval.element.add(tp.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:434:50: ( '&' tpp= type )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==43) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:434:51: '&' tpp= type
            	    {
            	    char_literal24=(Token)match(input,43,FOLLOW_43_in_typeBound853); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal24_tree = (Object)adaptor.create(char_literal24);
            	    adaptor.addChild(root_0, char_literal24_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeBound857);
            	    tpp=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, tpp.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(tpp.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 13, typeBound_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeBound"

    protected static class enumDeclaration_scope {
        Type enumType;
    }
    protected Stack enumDeclaration_stack = new Stack();

    public static class enumDeclaration_return extends ParserRuleReturnScope {
        public RegularType element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:437:1: enumDeclaration returns [RegularType element] : ENUM name= Identifier ( 'implements' trefs= typeList )? body= enumBody ;
    public final JavaParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        enumDeclaration_stack.push(new enumDeclaration_scope());
        JavaParser.enumDeclaration_return retval = new JavaParser.enumDeclaration_return();
        retval.start = input.LT(1);
        int enumDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token ENUM25=null;
        Token string_literal26=null;
        JavaParser.typeList_return trefs = null;

        JavaParser.enumBody_return body = null;


        Object name_tree=null;
        Object ENUM25_tree=null;
        Object string_literal26_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:5: ( ENUM name= Identifier ( 'implements' trefs= typeList )? body= enumBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:9: ENUM name= Identifier ( 'implements' trefs= typeList )? body= enumBody
            {
            root_0 = (Object)adaptor.nil();

            ENUM25=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration887); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ENUM25_tree = (Object)adaptor.create(ENUM25);
            adaptor.addChild(root_0, ENUM25_tree);
            }
            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration891); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new RegularType(new SimpleNameSignature((name!=null?name.getText():null))); retval.element.addModifier(new Enum()); ((enumDeclaration_scope)enumDeclaration_stack.peek()).enumType =retval.element;
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:185: ( 'implements' trefs= typeList )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==39) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:186: 'implements' trefs= typeList
                    {
                    string_literal26=(Token)match(input,39,FOLLOW_39_in_enumDeclaration895); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal26_tree = (Object)adaptor.create(string_literal26);
                    adaptor.addChild(root_0, string_literal26_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_enumDeclaration899);
                    trefs=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, trefs.getTree());
                    if ( state.backtracking==0 ) {
                      for(TypeReference ref: trefs.element){retval.element.addInheritanceRelation(new SubtypeRelation(ref));} 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration908);
            body=enumBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());
            if ( state.backtracking==0 ) {
              retval.element.setBody(body.element);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 14, enumDeclaration_StartIndex); }
            enumDeclaration_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "enumDeclaration"

    public static class enumBody_return extends ParserRuleReturnScope {
        public ClassBody element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:445:1: enumBody returns [ClassBody element] : '{' (csts= enumConstants )? ( ',' )? (decls= enumBodyDeclarations )? '}' ;
    public final JavaParser.enumBody_return enumBody() throws RecognitionException {
        JavaParser.enumBody_return retval = new JavaParser.enumBody_return();
        retval.start = input.LT(1);
        int enumBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal27=null;
        Token char_literal28=null;
        Token char_literal29=null;
        JavaParser.enumConstants_return csts = null;

        JavaParser.enumBodyDeclarations_return decls = null;


        Object char_literal27_tree=null;
        Object char_literal28_tree=null;
        Object char_literal29_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:5: ( '{' (csts= enumConstants )? ( ',' )? (decls= enumBodyDeclarations )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:9: '{' (csts= enumConstants )? ( ',' )? (decls= enumBodyDeclarations )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal27=(Token)match(input,44,FOLLOW_44_in_enumBody934); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal27_tree = (Object)adaptor.create(char_literal27);
            adaptor.addChild(root_0, char_literal27_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:13: (csts= enumConstants )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==Identifier||LA24_0==73) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:14: csts= enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody939);
                    csts=enumConstants();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, csts.getTree());
                    if ( state.backtracking==0 ) {
                      for(EnumConstant el: csts.element){retval.element.add(el);}
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:96: ( ',' )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==41) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ','
                    {
                    char_literal28=(Token)match(input,41,FOLLOW_41_in_enumBody944); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal28_tree = (Object)adaptor.create(char_literal28);
                    adaptor.addChild(root_0, char_literal28_tree);
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:101: (decls= enumBodyDeclarations )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==26) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:102: decls= enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody950);
                    decls=enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decls.getTree());
                    if ( state.backtracking==0 ) {
                      for(TypeElement el: decls.element){retval.element.add(el);}
                    }

                    }
                    break;

            }

            char_literal29=(Token)match(input,45,FOLLOW_45_in_enumBody956); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal29_tree = (Object)adaptor.create(char_literal29);
            adaptor.addChild(root_0, char_literal29_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 15, enumBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumBody"

    public static class enumConstants_return extends ParserRuleReturnScope {
        public List<EnumConstant> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstants"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:449:1: enumConstants returns [List<EnumConstant> element] : ct= enumConstant ( ',' cst= enumConstant )* ;
    public final JavaParser.enumConstants_return enumConstants() throws RecognitionException {
        JavaParser.enumConstants_return retval = new JavaParser.enumConstants_return();
        retval.start = input.LT(1);
        int enumConstants_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal30=null;
        JavaParser.enumConstant_return ct = null;

        JavaParser.enumConstant_return cst = null;


        Object char_literal30_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:450:5: (ct= enumConstant ( ',' cst= enumConstant )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:450:9: ct= enumConstant ( ',' cst= enumConstant )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enumConstant_in_enumConstants981);
            ct=enumConstant();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ct.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new ArrayList<EnumConstant>(); retval.element.add(ct.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:450:107: ( ',' cst= enumConstant )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==41) ) {
                    int LA27_1 = input.LA(2);

                    if ( (LA27_1==Identifier||LA27_1==73) ) {
                        alt27=1;
                    }


                }


                switch (alt27) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:450:108: ',' cst= enumConstant
            	    {
            	    char_literal30=(Token)match(input,41,FOLLOW_41_in_enumConstants986); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal30_tree = (Object)adaptor.create(char_literal30);
            	    adaptor.addChild(root_0, char_literal30_tree);
            	    }
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants990);
            	    cst=enumConstant();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, cst.getTree());
            	    if ( state.backtracking==0 ) {
            	      ((enumDeclaration_scope)enumDeclaration_stack.peek()).enumType.add(cst.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, enumConstants_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstants"

    public static class enumConstant_return extends ParserRuleReturnScope {
        public EnumConstant element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstant"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:1: enumConstant returns [EnumConstant element] : ( annotations )? name= Identifier (args= arguments )? (body= classBody )? ;
    public final JavaParser.enumConstant_return enumConstant() throws RecognitionException {
        JavaParser.enumConstant_return retval = new JavaParser.enumConstant_return();
        retval.start = input.LT(1);
        int enumConstant_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        JavaParser.arguments_return args = null;

        JavaParser.classBody_return body = null;

        JavaParser.annotations_return annotations31 = null;


        Object name_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:5: ( ( annotations )? name= Identifier (args= arguments )? (body= classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:9: ( annotations )? name= Identifier (args= arguments )? (body= classBody )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:9: ( annotations )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==73) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant1020);
                    annotations31=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations31.getTree());

                    }
                    break;

            }

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstant1025); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new EnumConstant(new SimpleNameSignature((name!=null?name.getText():null)));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:112: (args= arguments )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==66) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:113: args= arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant1032);
                    args=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, args.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.addAllParameters(args.element);
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:179: (body= classBody )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==44) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:180: body= classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant1041);
                    body=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.setBody(body.element);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 17, enumConstant_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstant"

    public static class enumBodyDeclarations_return extends ParserRuleReturnScope {
        public List<TypeElement> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumBodyDeclarations"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:457:1: enumBodyDeclarations returns [List<TypeElement> element] : ';' (decl= classBodyDeclaration )* ;
    public final JavaParser.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        JavaParser.enumBodyDeclarations_return retval = new JavaParser.enumBodyDeclarations_return();
        retval.start = input.LT(1);
        int enumBodyDeclarations_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal32=null;
        JavaParser.classBodyDeclaration_return decl = null;


        Object char_literal32_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:458:5: ( ';' (decl= classBodyDeclaration )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:458:9: ';' (decl= classBodyDeclaration )*
            {
            root_0 = (Object)adaptor.nil();

            char_literal32=(Token)match(input,26,FOLLOW_26_in_enumBodyDeclarations1072); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal32_tree = (Object)adaptor.create(char_literal32);
            adaptor.addChild(root_0, char_literal32_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element= new ArrayList<TypeElement>();
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:458:61: (decl= classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=Identifier && LA31_0<=ENUM)||LA31_0==26||LA31_0==28||(LA31_0>=31 && LA31_0<=37)||LA31_0==40||LA31_0==44||(LA31_0>=46 && LA31_0<=47)||(LA31_0>=52 && LA31_0<=63)||LA31_0==73) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:458:62: decl= classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1079);
            	    decl=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(decl.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 18, enumBodyDeclarations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumBodyDeclarations"

    public static class interfaceDeclaration_return extends ParserRuleReturnScope {
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:1: interfaceDeclaration returns [Type element] : (id= normalInterfaceDeclaration | ad= annotationTypeDeclaration );
    public final JavaParser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        JavaParser.interfaceDeclaration_return retval = new JavaParser.interfaceDeclaration_return();
        retval.start = input.LT(1);
        int interfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalInterfaceDeclaration_return id = null;

        JavaParser.annotationTypeDeclaration_return ad = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:462:5: (id= normalInterfaceDeclaration | ad= annotationTypeDeclaration )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==46) ) {
                alt32=1;
            }
            else if ( (LA32_0==73) ) {
                alt32=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:462:9: id= normalInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1112);
                    id=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = id.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:463:9: ad= annotationTypeDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1126);
                    ad=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ad.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = ad.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, interfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceDeclaration"

    public static class normalInterfaceDeclaration_return extends ParserRuleReturnScope {
        public RegularType element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "normalInterfaceDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:466:1: normalInterfaceDeclaration returns [RegularType element] : 'interface' name= Identifier (params= typeParameters )? ( 'extends' trefs= typeList )? body= classBody ;
    public final JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        JavaParser.normalInterfaceDeclaration_return retval = new JavaParser.normalInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int normalInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token string_literal33=null;
        Token string_literal34=null;
        JavaParser.typeParameters_return params = null;

        JavaParser.typeList_return trefs = null;

        JavaParser.classBody_return body = null;


        Object name_tree=null;
        Object string_literal33_tree=null;
        Object string_literal34_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:5: ( 'interface' name= Identifier (params= typeParameters )? ( 'extends' trefs= typeList )? body= classBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:9: 'interface' name= Identifier (params= typeParameters )? ( 'extends' trefs= typeList )? body= classBody
            {
            root_0 = (Object)adaptor.nil();

            string_literal33=(Token)match(input,46,FOLLOW_46_in_normalInterfaceDeclaration1155); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal33_tree = (Object)adaptor.create(string_literal33);
            adaptor.addChild(root_0, string_literal33_tree);
            }
            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration1159); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new RegularType(new SimpleNameSignature((name!=null?name.getText():null))); retval.element.addModifier(new Interface());
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:155: (params= typeParameters )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==40) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:156: params= typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1166);
                    params=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, params.getTree());
                    if ( state.backtracking==0 ) {
                      for(GenericParameter par: params.element){retval.element.add(par);}
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:249: ( 'extends' trefs= typeList )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==38) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:250: 'extends' trefs= typeList
                    {
                    string_literal34=(Token)match(input,38,FOLLOW_38_in_normalInterfaceDeclaration1172); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal34_tree = (Object)adaptor.create(string_literal34);
                    adaptor.addChild(root_0, string_literal34_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1176);
                    trefs=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, trefs.getTree());
                    if ( state.backtracking==0 ) {
                      for(TypeReference ref: trefs.element){retval.element.addInheritanceRelation(new SubtypeRelation(ref));} 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalInterfaceDeclaration1185);
            body=classBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());
            if ( state.backtracking==0 ) {
              retval.element.setBody(body.element);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, normalInterfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "normalInterfaceDeclaration"

    public static class typeList_return extends ParserRuleReturnScope {
        public List<TypeReference> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeList"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:470:1: typeList returns [List<TypeReference> element] : tp= type ( ',' tpp= type )* ;
    public final JavaParser.typeList_return typeList() throws RecognitionException {
        JavaParser.typeList_return retval = new JavaParser.typeList_return();
        retval.start = input.LT(1);
        int typeList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal35=null;
        JavaParser.type_return tp = null;

        JavaParser.type_return tpp = null;


        Object char_literal35_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:5: (tp= type ( ',' tpp= type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:9: tp= type ( ',' tpp= type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeList1216);
            tp=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, tp.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new ArrayList<TypeReference>(); retval.element.add(tp.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:99: ( ',' tpp= type )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==41) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:100: ',' tpp= type
            	    {
            	    char_literal35=(Token)match(input,41,FOLLOW_41_in_typeList1220); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal35_tree = (Object)adaptor.create(char_literal35);
            	    adaptor.addChild(root_0, char_literal35_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeList1224);
            	    tpp=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, tpp.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(tpp.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, typeList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeList"

    public static class classBody_return extends ParserRuleReturnScope {
        public ClassBody element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:1: classBody returns [ClassBody element] : '{' (decl= classBodyDeclaration )* '}' ;
    public final JavaParser.classBody_return classBody() throws RecognitionException {
        JavaParser.classBody_return retval = new JavaParser.classBody_return();
        retval.start = input.LT(1);
        int classBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal36=null;
        Token char_literal37=null;
        JavaParser.classBodyDeclaration_return decl = null;


        Object char_literal36_tree=null;
        Object char_literal37_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:475:5: ( '{' (decl= classBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:475:9: '{' (decl= classBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal36=(Token)match(input,44,FOLLOW_44_in_classBody1255); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal36_tree = (Object)adaptor.create(char_literal36);
            adaptor.addChild(root_0, char_literal36_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new ClassBody();
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:475:49: (decl= classBodyDeclaration )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=Identifier && LA36_0<=ENUM)||LA36_0==26||LA36_0==28||(LA36_0>=31 && LA36_0<=37)||LA36_0==40||LA36_0==44||(LA36_0>=46 && LA36_0<=47)||(LA36_0>=52 && LA36_0<=63)||LA36_0==73) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:475:50: decl= classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody1262);
            	    decl=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(decl.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            char_literal37=(Token)match(input,45,FOLLOW_45_in_classBody1268); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal37_tree = (Object)adaptor.create(char_literal37);
            adaptor.addChild(root_0, char_literal37_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 22, classBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classBody"

    public static class interfaceBody_return extends ParserRuleReturnScope {
        public ClassBody element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:1: interfaceBody returns [ClassBody element] : '{' (decl= interfaceBodyDeclaration )* '}' ;
    public final JavaParser.interfaceBody_return interfaceBody() throws RecognitionException {
        JavaParser.interfaceBody_return retval = new JavaParser.interfaceBody_return();
        retval.start = input.LT(1);
        int interfaceBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal38=null;
        Token char_literal39=null;
        JavaParser.interfaceBodyDeclaration_return decl = null;


        Object char_literal38_tree=null;
        Object char_literal39_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:479:5: ( '{' (decl= interfaceBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:479:9: '{' (decl= interfaceBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal38=(Token)match(input,44,FOLLOW_44_in_interfaceBody1295); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal38_tree = (Object)adaptor.create(char_literal38);
            adaptor.addChild(root_0, char_literal38_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new ClassBody();
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:479:49: (decl= interfaceBodyDeclaration )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=Identifier && LA37_0<=ENUM)||LA37_0==26||LA37_0==28||(LA37_0>=31 && LA37_0<=37)||LA37_0==40||(LA37_0>=46 && LA37_0<=47)||(LA37_0>=52 && LA37_0<=63)||LA37_0==73) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:479:50: decl= interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1302);
            	    decl=interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(decl.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            char_literal39=(Token)match(input,45,FOLLOW_45_in_interfaceBody1308); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal39_tree = (Object)adaptor.create(char_literal39);
            adaptor.addChild(root_0, char_literal39_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, interfaceBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceBody"

    public static class classBodyDeclaration_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classBodyDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:482:1: classBodyDeclaration returns [TypeElement element] : ( ';' | ( 'static' )? bl= block | mods= modifiers decl= memberDecl );
    public final JavaParser.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        JavaParser.classBodyDeclaration_return retval = new JavaParser.classBodyDeclaration_return();
        retval.start = input.LT(1);
        int classBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal40=null;
        Token string_literal41=null;
        JavaParser.block_return bl = null;

        JavaParser.modifiers_return mods = null;

        JavaParser.memberDecl_return decl = null;


        Object char_literal40_tree=null;
        Object string_literal41_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:483:5: ( ';' | ( 'static' )? bl= block | mods= modifiers decl= memberDecl )
            int alt39=3;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt39=1;
                }
                break;
            case 28:
                {
                int LA39_2 = input.LA(2);

                if ( (LA39_2==44) ) {
                    alt39=2;
                }
                else if ( ((LA39_2>=Identifier && LA39_2<=ENUM)||LA39_2==28||(LA39_2>=31 && LA39_2<=37)||LA39_2==40||(LA39_2>=46 && LA39_2<=47)||(LA39_2>=52 && LA39_2<=63)||LA39_2==73) ) {
                    alt39=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 39, 2, input);

                    throw nvae;
                }
                }
                break;
            case 44:
                {
                alt39=2;
                }
                break;
            case Identifier:
            case ENUM:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 40:
            case 46:
            case 47:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 73:
                {
                alt39=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:483:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal40=(Token)match(input,26,FOLLOW_26_in_classBodyDeclaration1331); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal40_tree = (Object)adaptor.create(char_literal40);
                    adaptor.addChild(root_0, char_literal40_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new EmptyTypeElement();
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:9: ( 'static' )? bl= block
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:9: ( 'static' )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==28) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: 'static'
                            {
                            string_literal41=(Token)match(input,28,FOLLOW_28_in_classBodyDeclaration1343); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal41_tree = (Object)adaptor.create(string_literal41);
                            adaptor.addChild(root_0, string_literal41_tree);
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration1348);
                    bl=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bl.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = new StaticInitializer(bl.element);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:485:9: mods= modifiers decl= memberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_classBodyDeclaration1362);
                    mods=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, mods.getTree());
                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1366);
                    decl=memberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl.element; retval.element.addModifiers(mods.element);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, classBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classBodyDeclaration"

    public static class memberDecl_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "memberDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:488:1: memberDecl returns [TypeElement element] : (gen= genericMethodOrConstructorDecl | mem= memberDeclaration | vmd= voidMethodDeclaration | cs= constructorDeclaration | id= interfaceDeclaration | cd= classDeclaration );
    public final JavaParser.memberDecl_return memberDecl() throws RecognitionException {
        JavaParser.memberDecl_return retval = new JavaParser.memberDecl_return();
        retval.start = input.LT(1);
        int memberDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.genericMethodOrConstructorDecl_return gen = null;

        JavaParser.memberDeclaration_return mem = null;

        JavaParser.voidMethodDeclaration_return vmd = null;

        JavaParser.constructorDeclaration_return cs = null;

        JavaParser.interfaceDeclaration_return id = null;

        JavaParser.classDeclaration_return cd = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:489:5: (gen= genericMethodOrConstructorDecl | mem= memberDeclaration | vmd= voidMethodDeclaration | cs= constructorDeclaration | id= interfaceDeclaration | cd= classDeclaration )
            int alt40=6;
            switch ( input.LA(1) ) {
            case 40:
                {
                alt40=1;
                }
                break;
            case Identifier:
                {
                int LA40_2 = input.LA(2);

                if ( (LA40_2==66) ) {
                    alt40=4;
                }
                else if ( (LA40_2==Identifier||LA40_2==29||LA40_2==40||LA40_2==48) ) {
                    alt40=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 2, input);

                    throw nvae;
                }
                }
                break;
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                {
                alt40=2;
                }
                break;
            case 47:
                {
                alt40=3;
                }
                break;
            case 46:
            case 73:
                {
                alt40=5;
                }
                break;
            case ENUM:
            case 37:
                {
                alt40=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:489:9: gen= genericMethodOrConstructorDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl1397);
                    gen=genericMethodOrConstructorDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, gen.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = gen.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:490:9: mem= memberDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_memberDeclaration_in_memberDecl1411);
                    mem=memberDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, mem.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = mem.element;
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:9: vmd= voidMethodDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_voidMethodDeclaration_in_memberDecl1425);
                    vmd=voidMethodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, vmd.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = vmd.element;
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:492:9: cs= constructorDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_constructorDeclaration_in_memberDecl1439);
                    cs=constructorDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cs.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = cs.element;
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:493:9: id= interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1453);
                    id=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=id.element;
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:494:9: cd= classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1467);
                    cd=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=cd.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, memberDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "memberDecl"

    public static class voidMethodDeclaration_return extends ParserRuleReturnScope {
        public Method element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "voidMethodDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:497:1: voidMethodDeclaration returns [Method element] : 'void' methodname= Identifier voidMethodDeclaratorRest ;
    public final JavaParser.voidMethodDeclaration_return voidMethodDeclaration() throws RecognitionException {
        MethodScope_stack.push(new MethodScope_scope());

        JavaParser.voidMethodDeclaration_return retval = new JavaParser.voidMethodDeclaration_return();
        retval.start = input.LT(1);
        int voidMethodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token methodname=null;
        Token string_literal42=null;
        JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest43 = null;


        Object methodname_tree=null;
        Object string_literal42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:499:6: ( 'void' methodname= Identifier voidMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:499:8: 'void' methodname= Identifier voidMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            string_literal42=(Token)match(input,47,FOLLOW_47_in_voidMethodDeclaration1500); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal42_tree = (Object)adaptor.create(string_literal42);
            adaptor.addChild(root_0, string_literal42_tree);
            }
            methodname=(Token)match(input,Identifier,FOLLOW_Identifier_in_voidMethodDeclaration1504); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            methodname_tree = (Object)adaptor.create(methodname);
            adaptor.addChild(root_0, methodname_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new NormalMethod(new SimpleNameMethodHeader((methodname!=null?methodname.getText():null)), new JavaTypeReference("void")); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
            }
            pushFollow(FOLLOW_voidMethodDeclaratorRest_in_voidMethodDeclaration1508);
            voidMethodDeclaratorRest43=voidMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, voidMethodDeclaratorRest43.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 26, voidMethodDeclaration_StartIndex); }
            MethodScope_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "voidMethodDeclaration"

    public static class constructorDeclaration_return extends ParserRuleReturnScope {
        public Method element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructorDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:502:1: constructorDeclaration returns [Method element] : consname= Identifier constructorDeclaratorRest ;
    public final JavaParser.constructorDeclaration_return constructorDeclaration() throws RecognitionException {
        MethodScope_stack.push(new MethodScope_scope());

        JavaParser.constructorDeclaration_return retval = new JavaParser.constructorDeclaration_return();
        retval.start = input.LT(1);
        int constructorDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token consname=null;
        JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest44 = null;


        Object consname_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:9: (consname= Identifier constructorDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:11: consname= Identifier constructorDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            consname=(Token)match(input,Identifier,FOLLOW_Identifier_in_constructorDeclaration1547); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            consname_tree = (Object)adaptor.create(consname);
            adaptor.addChild(root_0, consname_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new NormalMethod(new SimpleNameMethodHeader((consname!=null?consname.getText():null)), new JavaTypeReference((consname!=null?consname.getText():null))); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
            }
            pushFollow(FOLLOW_constructorDeclaratorRest_in_constructorDeclaration1551);
            constructorDeclaratorRest44=constructorDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorDeclaratorRest44.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, constructorDeclaration_StartIndex); }
            MethodScope_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "constructorDeclaration"

    public static class memberDeclaration_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "memberDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:507:1: memberDeclaration returns [TypeElement element] : (method= methodDeclaration | field= fieldDeclaration );
    public final JavaParser.memberDeclaration_return memberDeclaration() throws RecognitionException {
        JavaParser.memberDeclaration_return retval = new JavaParser.memberDeclaration_return();
        retval.start = input.LT(1);
        int memberDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.methodDeclaration_return method = null;

        JavaParser.fieldDeclaration_return field = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:5: (method= methodDeclaration | field= fieldDeclaration )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==Identifier) ) {
                int LA41_1 = input.LA(2);

                if ( (synpred53_Java()) ) {
                    alt41=1;
                }
                else if ( (true) ) {
                    alt41=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA41_0>=56 && LA41_0<=63)) ) {
                int LA41_2 = input.LA(2);

                if ( (synpred53_Java()) ) {
                    alt41=1;
                }
                else if ( (true) ) {
                    alt41=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:9: method= methodDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_methodDeclaration_in_memberDeclaration1577);
                    method=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, method.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=method.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:509:9: field= fieldDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_fieldDeclaration_in_memberDeclaration1591);
                    field=fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, field.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=field.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 28, memberDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "memberDeclaration"

    public static class genericMethodOrConstructorDecl_return extends ParserRuleReturnScope {
        public Member element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "genericMethodOrConstructorDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:1: genericMethodOrConstructorDecl returns [Member element] : params= typeParameters rest= genericMethodOrConstructorRest ;
    public final JavaParser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl() throws RecognitionException {
        JavaParser.genericMethodOrConstructorDecl_return retval = new JavaParser.genericMethodOrConstructorDecl_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.typeParameters_return params = null;

        JavaParser.genericMethodOrConstructorRest_return rest = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:514:5: (params= typeParameters rest= genericMethodOrConstructorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:514:9: params= typeParameters rest= genericMethodOrConstructorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1619);
            params=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, params.getTree());
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1623);
            rest=genericMethodOrConstructorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rest.getTree());
            if ( state.backtracking==0 ) {
              retval.element = rest.element;
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 29, genericMethodOrConstructorDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "genericMethodOrConstructorDecl"

    public static class genericMethodOrConstructorRest_return extends ParserRuleReturnScope {
        public Method element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "genericMethodOrConstructorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:517:1: genericMethodOrConstructorRest returns [Method element] : ( (t= type | 'void' ) name= Identifier methodDeclaratorRest | name= Identifier constructorDeclaratorRest );
    public final JavaParser.genericMethodOrConstructorRest_return genericMethodOrConstructorRest() throws RecognitionException {
        MethodScope_stack.push(new MethodScope_scope());

        JavaParser.genericMethodOrConstructorRest_return retval = new JavaParser.genericMethodOrConstructorRest_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorRest_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token string_literal45=null;
        JavaParser.type_return t = null;

        JavaParser.methodDeclaratorRest_return methodDeclaratorRest46 = null;

        JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest47 = null;


        Object name_tree=null;
        Object string_literal45_tree=null;

        TypeReference tref = null;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:5: ( (t= type | 'void' ) name= Identifier methodDeclaratorRest | name= Identifier constructorDeclaratorRest )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==Identifier) ) {
                int LA43_1 = input.LA(2);

                if ( (LA43_1==Identifier||LA43_1==29||LA43_1==40||LA43_1==48) ) {
                    alt43=1;
                }
                else if ( (LA43_1==66) ) {
                    alt43=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 43, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA43_0==47||(LA43_0>=56 && LA43_0<=63)) ) {
                alt43=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:9: (t= type | 'void' ) name= Identifier methodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:9: (t= type | 'void' )
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==Identifier||(LA42_0>=56 && LA42_0<=63)) ) {
                        alt42=1;
                    }
                    else if ( (LA42_0==47) ) {
                        alt42=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 42, 0, input);

                        throw nvae;
                    }
                    switch (alt42) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:10: t= type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest1664);
                            t=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, t.getTree());
                            if ( state.backtracking==0 ) {
                              tref=t.element;
                            }

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:36: 'void'
                            {
                            string_literal45=(Token)match(input,47,FOLLOW_47_in_genericMethodOrConstructorRest1669); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal45_tree = (Object)adaptor.create(string_literal45);
                            adaptor.addChild(root_0, string_literal45_tree);
                            }
                            if ( state.backtracking==0 ) {
                              tref = new JavaTypeReference("void");
                            }

                            }
                            break;

                    }

                    name=(Token)match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1676); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    name_tree = (Object)adaptor.create(name);
                    adaptor.addChild(root_0, name_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new NormalMethod(new SimpleNameMethodHeader((name!=null?name.getText():null)),tref); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
                    }
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1680);
                    methodDeclaratorRest46=methodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaratorRest46.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:521:9: name= Identifier constructorDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    name=(Token)match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1692); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    name_tree = (Object)adaptor.create(name);
                    adaptor.addChild(root_0, name_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new NormalMethod(new SimpleNameMethodHeader((name!=null?name.getText():null)),new JavaTypeReference((name!=null?name.getText():null))); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
                    }
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1696);
                    constructorDeclaratorRest47=constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorDeclaratorRest47.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 30, genericMethodOrConstructorRest_StartIndex); }
            MethodScope_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "genericMethodOrConstructorRest"

    public static class methodDeclaration_return extends ParserRuleReturnScope {
        public Method element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:524:1: methodDeclaration returns [Method element] : t= type name= Identifier methodDeclaratorRest ;
    public final JavaParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        MethodScope_stack.push(new MethodScope_scope());

        JavaParser.methodDeclaration_return retval = new JavaParser.methodDeclaration_return();
        retval.start = input.LT(1);
        int methodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        JavaParser.type_return t = null;

        JavaParser.methodDeclaratorRest_return methodDeclaratorRest48 = null;


        Object name_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:526:5: (t= type name= Identifier methodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:526:9: t= type name= Identifier methodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_methodDeclaration1726);
            t=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, t.getTree());
            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration1730); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new NormalMethod(new SimpleNameMethodHeader((name!=null?name.getText():null)),t.element); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
            }
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration1734);
            methodDeclaratorRest48=methodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaratorRest48.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 31, methodDeclaration_StartIndex); }
            MethodScope_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "methodDeclaration"

    public static class fieldDeclaration_return extends ParserRuleReturnScope {
        public MemberVariableDeclarator element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fieldDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:529:1: fieldDeclaration returns [MemberVariableDeclarator element] : ref= type decls= variableDeclarators ';' ;
    public final JavaParser.fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        JavaParser.fieldDeclaration_return retval = new JavaParser.fieldDeclaration_return();
        retval.start = input.LT(1);
        int fieldDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal49=null;
        JavaParser.type_return ref = null;

        JavaParser.variableDeclarators_return decls = null;


        Object char_literal49_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:530:5: (ref= type decls= variableDeclarators ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:530:9: ref= type decls= variableDeclarators ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_fieldDeclaration1759);
            ref=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ref.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new MemberVariableDeclarator(ref.element);
            }
            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration1765);
            decls=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, decls.getTree());
            if ( state.backtracking==0 ) {
              for(VariableDeclaration decl: decls.element) {retval.element.add(decl);}
            }
            char_literal49=(Token)match(input,26,FOLLOW_26_in_fieldDeclaration1769); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal49_tree = (Object)adaptor.create(char_literal49);
            adaptor.addChild(root_0, char_literal49_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 32, fieldDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "fieldDeclaration"

    public static class interfaceBodyDeclaration_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceBodyDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:1: interfaceBodyDeclaration returns [TypeElement element] : (mods= modifiers decl= interfaceMemberDecl | ';' );
    public final JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        JavaParser.interfaceBodyDeclaration_return retval = new JavaParser.interfaceBodyDeclaration_return();
        retval.start = input.LT(1);
        int interfaceBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal50=null;
        JavaParser.modifiers_return mods = null;

        JavaParser.interfaceMemberDecl_return decl = null;


        Object char_literal50_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:534:5: (mods= modifiers decl= interfaceMemberDecl | ';' )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( ((LA44_0>=Identifier && LA44_0<=ENUM)||LA44_0==28||(LA44_0>=31 && LA44_0<=37)||LA44_0==40||(LA44_0>=46 && LA44_0<=47)||(LA44_0>=52 && LA44_0<=63)||LA44_0==73) ) {
                alt44=1;
            }
            else if ( (LA44_0==26) ) {
                alt44=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:534:9: mods= modifiers decl= interfaceMemberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_interfaceBodyDeclaration1802);
                    mods=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, mods.getTree());
                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1806);
                    decl=interfaceMemberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl.element; for(Modifier mod: mods.element){retval.element.addModifier(mod);}
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:535:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal50=(Token)match(input,26,FOLLOW_26_in_interfaceBodyDeclaration1818); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal50_tree = (Object)adaptor.create(char_literal50);
                    adaptor.addChild(root_0, char_literal50_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, interfaceBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceBodyDeclaration"

    public static class interfaceMemberDecl_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMemberDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:538:1: interfaceMemberDecl returns [TypeElement element] : (decl= interfaceMethodOrFieldDecl | decl2= interfaceGenericMethodDecl | decl5= voidInterfaceMethodDeclaration | decl3= interfaceDeclaration | decl4= classDeclaration );
    public final JavaParser.interfaceMemberDecl_return interfaceMemberDecl() throws RecognitionException {
        JavaParser.interfaceMemberDecl_return retval = new JavaParser.interfaceMemberDecl_return();
        retval.start = input.LT(1);
        int interfaceMemberDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.interfaceMethodOrFieldDecl_return decl = null;

        JavaParser.interfaceGenericMethodDecl_return decl2 = null;

        JavaParser.voidInterfaceMethodDeclaration_return decl5 = null;

        JavaParser.interfaceDeclaration_return decl3 = null;

        JavaParser.classDeclaration_return decl4 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:539:5: (decl= interfaceMethodOrFieldDecl | decl2= interfaceGenericMethodDecl | decl5= voidInterfaceMethodDeclaration | decl3= interfaceDeclaration | decl4= classDeclaration )
            int alt45=5;
            switch ( input.LA(1) ) {
            case Identifier:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                {
                alt45=1;
                }
                break;
            case 40:
                {
                alt45=2;
                }
                break;
            case 47:
                {
                alt45=3;
                }
                break;
            case 46:
            case 73:
                {
                alt45=4;
                }
                break;
            case ENUM:
            case 37:
                {
                alt45=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:539:9: decl= interfaceMethodOrFieldDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1843);
                    decl=interfaceMethodOrFieldDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:540:9: decl2= interfaceGenericMethodDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1857);
                    decl2=interfaceGenericMethodDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl2.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl2.element;
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:9: decl5= voidInterfaceMethodDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_voidInterfaceMethodDeclaration_in_interfaceMemberDecl1871);
                    decl5=voidInterfaceMethodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl5.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl5.element;
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:9: decl3= interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1885);
                    decl3=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl3.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl3.element;
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:543:9: decl4= classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl1899);
                    decl4=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, decl4.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = decl4.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, interfaceMemberDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMemberDecl"

    public static class voidInterfaceMethodDeclaration_return extends ParserRuleReturnScope {
        public Method element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "voidInterfaceMethodDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:1: voidInterfaceMethodDeclaration returns [Method element] : 'void' methodname= Identifier voidInterfaceMethodDeclaratorRest ;
    public final JavaParser.voidInterfaceMethodDeclaration_return voidInterfaceMethodDeclaration() throws RecognitionException {
        MethodScope_stack.push(new MethodScope_scope());

        JavaParser.voidInterfaceMethodDeclaration_return retval = new JavaParser.voidInterfaceMethodDeclaration_return();
        retval.start = input.LT(1);
        int voidInterfaceMethodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token methodname=null;
        Token string_literal51=null;
        JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest52 = null;


        Object methodname_tree=null;
        Object string_literal51_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:548:6: ( 'void' methodname= Identifier voidInterfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:548:8: 'void' methodname= Identifier voidInterfaceMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            string_literal51=(Token)match(input,47,FOLLOW_47_in_voidInterfaceMethodDeclaration1933); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal51_tree = (Object)adaptor.create(string_literal51);
            adaptor.addChild(root_0, string_literal51_tree);
            }
            methodname=(Token)match(input,Identifier,FOLLOW_Identifier_in_voidInterfaceMethodDeclaration1937); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            methodname_tree = (Object)adaptor.create(methodname);
            adaptor.addChild(root_0, methodname_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new NormalMethod(new SimpleNameMethodHeader((methodname!=null?methodname.getText():null)), new JavaTypeReference("void")); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
            }
            pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_voidInterfaceMethodDeclaration1941);
            voidInterfaceMethodDeclaratorRest52=voidInterfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, voidInterfaceMethodDeclaratorRest52.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 35, voidInterfaceMethodDeclaration_StartIndex); }
            MethodScope_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "voidInterfaceMethodDeclaration"

    public static class interfaceMethodOrFieldDecl_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMethodOrFieldDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:551:1: interfaceMethodOrFieldDecl returns [TypeElement element] : (cst= interfaceConstant | m= interfaceMethod );
    public final JavaParser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl() throws RecognitionException {
        JavaParser.interfaceMethodOrFieldDecl_return retval = new JavaParser.interfaceMethodOrFieldDecl_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.interfaceConstant_return cst = null;

        JavaParser.interfaceMethod_return m = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:552:5: (cst= interfaceConstant | m= interfaceMethod )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==Identifier) ) {
                int LA46_1 = input.LA(2);

                if ( (synpred61_Java()) ) {
                    alt46=1;
                }
                else if ( (true) ) {
                    alt46=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 46, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA46_0>=56 && LA46_0<=63)) ) {
                int LA46_2 = input.LA(2);

                if ( (synpred61_Java()) ) {
                    alt46=1;
                }
                else if ( (true) ) {
                    alt46=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 46, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:552:9: cst= interfaceConstant
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceConstant_in_interfaceMethodOrFieldDecl1975);
                    cst=interfaceConstant();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cst.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = cst.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:553:9: m= interfaceMethod
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethod_in_interfaceMethodOrFieldDecl1989);
                    m=interfaceMethod();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, m.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = m.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 36, interfaceMethodOrFieldDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodOrFieldDecl"

    public static class interfaceConstant_return extends ParserRuleReturnScope {
        public MemberVariableDeclarator element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceConstant"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:557:1: interfaceConstant returns [MemberVariableDeclarator element] : ref= type decl= constantDeclarator ( ',' dec= constantDeclarator )* ';' ;
    public final JavaParser.interfaceConstant_return interfaceConstant() throws RecognitionException {
        JavaParser.interfaceConstant_return retval = new JavaParser.interfaceConstant_return();
        retval.start = input.LT(1);
        int interfaceConstant_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal53=null;
        Token char_literal54=null;
        JavaParser.type_return ref = null;

        JavaParser.constantDeclarator_return decl = null;

        JavaParser.constantDeclarator_return dec = null;


        Object char_literal53_tree=null;
        Object char_literal54_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:5: (ref= type decl= constantDeclarator ( ',' dec= constantDeclarator )* ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:9: ref= type decl= constantDeclarator ( ',' dec= constantDeclarator )* ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_interfaceConstant2025);
            ref=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ref.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new MemberVariableDeclarator(ref.element);
            }
            pushFollow(FOLLOW_constantDeclarator_in_interfaceConstant2031);
            decl=constantDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
            if ( state.backtracking==0 ) {
              retval.element.add(decl.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:139: ( ',' dec= constantDeclarator )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==41) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:140: ',' dec= constantDeclarator
            	    {
            	    char_literal53=(Token)match(input,41,FOLLOW_41_in_interfaceConstant2035); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal53_tree = (Object)adaptor.create(char_literal53);
            	    adaptor.addChild(root_0, char_literal53_tree);
            	    }
            	    pushFollow(FOLLOW_constantDeclarator_in_interfaceConstant2039);
            	    dec=constantDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, dec.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(dec.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            char_literal54=(Token)match(input,26,FOLLOW_26_in_interfaceConstant2045); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal54_tree = (Object)adaptor.create(char_literal54);
            adaptor.addChild(root_0, char_literal54_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, interfaceConstant_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceConstant"

    public static class interfaceMethod_return extends ParserRuleReturnScope {
        public Method element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMethod"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:561:1: interfaceMethod returns [Method element] : tref= type methodname= Identifier interfaceMethodDeclaratorRest ;
    public final JavaParser.interfaceMethod_return interfaceMethod() throws RecognitionException {
        MethodScope_stack.push(new MethodScope_scope());

        JavaParser.interfaceMethod_return retval = new JavaParser.interfaceMethod_return();
        retval.start = input.LT(1);
        int interfaceMethod_StartIndex = input.index();
        Object root_0 = null;

        Token methodname=null;
        JavaParser.type_return tref = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest55 = null;


        Object methodname_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:2: (tref= type methodname= Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:4: tref= type methodname= Identifier interfaceMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_interfaceMethod2070);
            tref=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, tref.getTree());
            methodname=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceMethod2074); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            methodname_tree = (Object)adaptor.create(methodname);
            adaptor.addChild(root_0, methodname_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new NormalMethod(new SimpleNameMethodHeader((methodname!=null?methodname.getText():null)), tref.element); ((MethodScope_scope)MethodScope_stack.peek()).method = retval.element;
            }
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethod2078);
            interfaceMethodDeclaratorRest55=interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest55.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, interfaceMethod_StartIndex); }
            MethodScope_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "interfaceMethod"

    public static class methodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:1: methodDeclaratorRest : pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' ) ;
    public final JavaParser.methodDeclaratorRest_return methodDeclaratorRest() throws RecognitionException {
        JavaParser.methodDeclaratorRest_return retval = new JavaParser.methodDeclaratorRest_return();
        retval.start = input.LT(1);
        int methodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal56=null;
        Token char_literal57=null;
        Token string_literal58=null;
        Token char_literal59=null;
        JavaParser.formalParameters_return pars = null;

        JavaParser.qualifiedNameList_return names = null;

        JavaParser.methodBody_return body = null;


        Object char_literal56_tree=null;
        Object char_literal57_tree=null;
        Object string_literal58_tree=null;
        Object char_literal59_tree=null;

        int count = 0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:569:5: (pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:569:9: pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest2105);
            pars=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, pars.getTree());
            if ( state.backtracking==0 ) {
              for(FormalParameter par: pars.element){((MethodScope_scope)MethodScope_stack.peek()).method.header().addParameter(par);}
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:569:122: ( '[' ']' )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==48) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:569:123: '[' ']'
            	    {
            	    char_literal56=(Token)match(input,48,FOLLOW_48_in_methodDeclaratorRest2110); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal56_tree = (Object)adaptor.create(char_literal56);
            	    adaptor.addChild(root_0, char_literal56_tree);
            	    }
            	    char_literal57=(Token)match(input,49,FOLLOW_49_in_methodDeclaratorRest2112); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal57_tree = (Object)adaptor.create(char_literal57);
            	    adaptor.addChild(root_0, char_literal57_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      count++;
            	    }

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              ((JavaTypeReference)((MethodScope_scope)MethodScope_stack.peek()).method.getReturnTypeReference()).setArrayDimension(count);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:570:9: ( 'throws' names= qualifiedNameList )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==50) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:570:10: 'throws' names= qualifiedNameList
                    {
                    string_literal58=(Token)match(input,50,FOLLOW_50_in_methodDeclaratorRest2129); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal58_tree = (Object)adaptor.create(string_literal58);
                    adaptor.addChild(root_0, string_literal58_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest2133);
                    names=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, names.getTree());
                    if ( state.backtracking==0 ) {
                       ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(new JavaTypeReference(name)));}
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:571:9: (body= methodBody | ';' )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==44) ) {
                alt50=1;
            }
            else if ( (LA50_0==26) ) {
                alt50=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:571:13: body= methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest2153);
                    body=methodBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());
                    if ( state.backtracking==0 ) {
                      ((MethodScope_scope)MethodScope_stack.peek()).method.setImplementation(new RegularImplementation(body.element));
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:572:13: ';'
                    {
                    char_literal59=(Token)match(input,26,FOLLOW_26_in_methodDeclaratorRest2169); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal59_tree = (Object)adaptor.create(char_literal59);
                    adaptor.addChild(root_0, char_literal59_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 39, methodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodDeclaratorRest"

    public static class voidMethodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "voidMethodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:576:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidMethodDeclaratorRest_return retval = new JavaParser.voidMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal61=null;
        Token char_literal64=null;
        JavaParser.formalParameters_return formalParameters60 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList62 = null;

        JavaParser.methodBody_return methodBody63 = null;


        Object string_literal61_tree=null;
        Object char_literal64_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:5: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:9: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest2202);
            formalParameters60=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters60.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:26: ( 'throws' qualifiedNameList )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==50) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:27: 'throws' qualifiedNameList
                    {
                    string_literal61=(Token)match(input,50,FOLLOW_50_in_voidMethodDeclaratorRest2205); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal61_tree = (Object)adaptor.create(string_literal61);
                    adaptor.addChild(root_0, string_literal61_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest2207);
                    qualifiedNameList62=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList62.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:578:9: ( methodBody | ';' )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==44) ) {
                alt52=1;
            }
            else if ( (LA52_0==26) ) {
                alt52=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:578:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest2223);
                    methodBody63=methodBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodBody63.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:579:13: ';'
                    {
                    char_literal64=(Token)match(input,26,FOLLOW_26_in_voidMethodDeclaratorRest2237); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal64_tree = (Object)adaptor.create(char_literal64);
                    adaptor.addChild(root_0, char_literal64_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 40, voidMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "voidMethodDeclaratorRest"

    public static class interfaceMethodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMethodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:583:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.interfaceMethodDeclaratorRest_return retval = new JavaParser.interfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal66=null;
        Token char_literal67=null;
        Token string_literal68=null;
        Token char_literal70=null;
        JavaParser.formalParameters_return formalParameters65 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList69 = null;


        Object char_literal66_tree=null;
        Object char_literal67_tree=null;
        Object string_literal68_tree=null;
        Object char_literal70_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest2270);
            formalParameters65=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters65.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:26: ( '[' ']' )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==48) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:27: '[' ']'
            	    {
            	    char_literal66=(Token)match(input,48,FOLLOW_48_in_interfaceMethodDeclaratorRest2273); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal66_tree = (Object)adaptor.create(char_literal66);
            	    adaptor.addChild(root_0, char_literal66_tree);
            	    }
            	    char_literal67=(Token)match(input,49,FOLLOW_49_in_interfaceMethodDeclaratorRest2275); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal67_tree = (Object)adaptor.create(char_literal67);
            	    adaptor.addChild(root_0, char_literal67_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:37: ( 'throws' qualifiedNameList )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==50) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:38: 'throws' qualifiedNameList
                    {
                    string_literal68=(Token)match(input,50,FOLLOW_50_in_interfaceMethodDeclaratorRest2280); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal68_tree = (Object)adaptor.create(string_literal68);
                    adaptor.addChild(root_0, string_literal68_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest2282);
                    qualifiedNameList69=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList69.getTree());

                    }
                    break;

            }

            char_literal70=(Token)match(input,26,FOLLOW_26_in_interfaceMethodDeclaratorRest2286); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal70_tree = (Object)adaptor.create(char_literal70);
            adaptor.addChild(root_0, char_literal70_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 41, interfaceMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodDeclaratorRest"

    public static class interfaceGenericMethodDecl_return extends ParserRuleReturnScope {
        public TypeElement element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceGenericMethodDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:587:1: interfaceGenericMethodDecl returns [TypeElement element] : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final JavaParser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl() throws RecognitionException {
        JavaParser.interfaceGenericMethodDecl_return retval = new JavaParser.interfaceGenericMethodDecl_return();
        retval.start = input.LT(1);
        int interfaceGenericMethodDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal73=null;
        Token Identifier74=null;
        JavaParser.typeParameters_return typeParameters71 = null;

        JavaParser.type_return type72 = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest75 = null;


        Object string_literal73_tree=null;
        Object Identifier74_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:9: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl2313);
            typeParameters71=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters71.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:24: ( type | 'void' )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==Identifier||(LA55_0>=56 && LA55_0<=63)) ) {
                alt55=1;
            }
            else if ( (LA55_0==47) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:25: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl2316);
                    type72=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type72.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:32: 'void'
                    {
                    string_literal73=(Token)match(input,47,FOLLOW_47_in_interfaceGenericMethodDecl2320); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal73_tree = (Object)adaptor.create(string_literal73);
                    adaptor.addChild(root_0, string_literal73_tree);
                    }

                    }
                    break;

            }

            Identifier74=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl2323); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier74_tree = (Object)adaptor.create(Identifier74);
            adaptor.addChild(root_0, Identifier74_tree);
            }
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl2333);
            interfaceMethodDeclaratorRest75=interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest75.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, interfaceGenericMethodDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceGenericMethodDecl"

    public static class voidInterfaceMethodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "voidInterfaceMethodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:592:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidInterfaceMethodDeclaratorRest_return retval = new JavaParser.voidInterfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal77=null;
        Token char_literal79=null;
        JavaParser.formalParameters_return formalParameters76 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList78 = null;


        Object string_literal77_tree=null;
        Object char_literal79_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:5: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:9: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest2356);
            formalParameters76=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters76.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:26: ( 'throws' qualifiedNameList )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==50) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:27: 'throws' qualifiedNameList
                    {
                    string_literal77=(Token)match(input,50,FOLLOW_50_in_voidInterfaceMethodDeclaratorRest2359); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal77_tree = (Object)adaptor.create(string_literal77);
                    adaptor.addChild(root_0, string_literal77_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest2361);
                    qualifiedNameList78=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList78.getTree());

                    }
                    break;

            }

            char_literal79=(Token)match(input,26,FOLLOW_26_in_voidInterfaceMethodDeclaratorRest2365); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal79_tree = (Object)adaptor.create(char_literal79);
            adaptor.addChild(root_0, char_literal79_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 43, voidInterfaceMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "voidInterfaceMethodDeclaratorRest"

    public static class constructorDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructorDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:596:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? constructorBody ;
    public final JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest() throws RecognitionException {
        JavaParser.constructorDeclaratorRest_return retval = new JavaParser.constructorDeclaratorRest_return();
        retval.start = input.LT(1);
        int constructorDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal81=null;
        JavaParser.formalParameters_return formalParameters80 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList82 = null;

        JavaParser.constructorBody_return constructorBody83 = null;


        Object string_literal81_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:5: ( formalParameters ( 'throws' qualifiedNameList )? constructorBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:9: formalParameters ( 'throws' qualifiedNameList )? constructorBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest2388);
            formalParameters80=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters80.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:26: ( 'throws' qualifiedNameList )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==50) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:27: 'throws' qualifiedNameList
                    {
                    string_literal81=(Token)match(input,50,FOLLOW_50_in_constructorDeclaratorRest2391); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal81_tree = (Object)adaptor.create(string_literal81);
                    adaptor.addChild(root_0, string_literal81_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest2393);
                    qualifiedNameList82=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList82.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_constructorBody_in_constructorDeclaratorRest2397);
            constructorBody83=constructorBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorBody83.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 44, constructorDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constructorDeclaratorRest"

    public static class constantDeclarator_return extends ParserRuleReturnScope {
        public JavaVariableDeclaration element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constantDeclarator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:600:1: constantDeclarator returns [JavaVariableDeclaration element] : name= Identifier ( ( '[' ']' )* '=' init= variableInitializer ) ;
    public final JavaParser.constantDeclarator_return constantDeclarator() throws RecognitionException {
        JavaParser.constantDeclarator_return retval = new JavaParser.constantDeclarator_return();
        retval.start = input.LT(1);
        int constantDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token char_literal84=null;
        Token char_literal85=null;
        Token char_literal86=null;
        JavaParser.variableInitializer_return init = null;


        Object name_tree=null;
        Object char_literal84_tree=null;
        Object char_literal85_tree=null;
        Object char_literal86_tree=null;

        int count = 0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:5: (name= Identifier ( ( '[' ']' )* '=' init= variableInitializer ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:9: name= Identifier ( ( '[' ']' )* '=' init= variableInitializer )
            {
            root_0 = (Object)adaptor.nil();

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator2426); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:25: ( ( '[' ']' )* '=' init= variableInitializer )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:26: ( '[' ']' )* '=' init= variableInitializer
            {
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:26: ( '[' ']' )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==48) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:27: '[' ']'
            	    {
            	    char_literal84=(Token)match(input,48,FOLLOW_48_in_constantDeclarator2430); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal84_tree = (Object)adaptor.create(char_literal84);
            	    adaptor.addChild(root_0, char_literal84_tree);
            	    }
            	    char_literal85=(Token)match(input,49,FOLLOW_49_in_constantDeclarator2432); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal85_tree = (Object)adaptor.create(char_literal85);
            	    adaptor.addChild(root_0, char_literal85_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      count++;
            	    }

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            char_literal86=(Token)match(input,51,FOLLOW_51_in_constantDeclarator2438); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal86_tree = (Object)adaptor.create(char_literal86);
            adaptor.addChild(root_0, char_literal86_tree);
            }
            pushFollow(FOLLOW_variableInitializer_in_constantDeclarator2442);
            init=variableInitializer();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, init.getTree());

            }

            if ( state.backtracking==0 ) {
              retval.element = new JavaVariableDeclaration((name!=null?name.getText():null)); retval.element.setArrayDimension(count); retval.element.setExpression(init.element);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, constantDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclarator"

    public static class variableDeclarators_return extends ParserRuleReturnScope {
        public List<VariableDeclaration> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableDeclarators"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:606:1: variableDeclarators returns [List<VariableDeclaration> element] : decl= variableDeclarator ( ',' decll= variableDeclarator )* ;
    public final JavaParser.variableDeclarators_return variableDeclarators() throws RecognitionException {
        JavaParser.variableDeclarators_return retval = new JavaParser.variableDeclarators_return();
        retval.start = input.LT(1);
        int variableDeclarators_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal87=null;
        JavaParser.variableDeclarator_return decl = null;

        JavaParser.variableDeclarator_return decll = null;


        Object char_literal87_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:5: (decl= variableDeclarator ( ',' decll= variableDeclarator )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:9: decl= variableDeclarator ( ',' decll= variableDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators2482);
            decl=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new ArrayList<VariableDeclaration>(); retval.element.add(decl.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:123: ( ',' decll= variableDeclarator )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:124: ',' decll= variableDeclarator
            	    {
            	    char_literal87=(Token)match(input,41,FOLLOW_41_in_variableDeclarators2486); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal87_tree = (Object)adaptor.create(char_literal87);
            	    adaptor.addChild(root_0, char_literal87_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators2490);
            	    decll=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, decll.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add(decll.element);
            	    }

            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 46, variableDeclarators_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclarators"

    public static class variableDeclarator_return extends ParserRuleReturnScope {
        public JavaVariableDeclaration element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableDeclarator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:610:1: variableDeclarator returns [JavaVariableDeclaration element] : id= variableDeclaratorId ( '=' init= variableInitializer )? ;
    public final JavaParser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        JavaParser.variableDeclarator_return retval = new JavaParser.variableDeclarator_return();
        retval.start = input.LT(1);
        int variableDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal88=null;
        JavaParser.variableDeclaratorId_return id = null;

        JavaParser.variableInitializer_return init = null;


        Object char_literal88_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:5: (id= variableDeclaratorId ( '=' init= variableInitializer )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:9: id= variableDeclaratorId ( '=' init= variableInitializer )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclaratorId_in_variableDeclarator2519);
            id=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new JavaVariableDeclaration(id.element.name()); retval.element.setArrayDimension(id.element.dimension());
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:158: ( '=' init= variableInitializer )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==51) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:159: '=' init= variableInitializer
                    {
                    char_literal88=(Token)match(input,51,FOLLOW_51_in_variableDeclarator2524); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal88_tree = (Object)adaptor.create(char_literal88);
                    adaptor.addChild(root_0, char_literal88_tree);
                    }
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2528);
                    init=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, init.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.setExpression(init.element);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 47, variableDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclarator"

    public static class variableDeclaratorId_return extends ParserRuleReturnScope {
        public StupidVariableDeclaratorId element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableDeclaratorId"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:616:1: variableDeclaratorId returns [StupidVariableDeclaratorId element] : name= Identifier ( '[' ']' )* ;
    public final JavaParser.variableDeclaratorId_return variableDeclaratorId() throws RecognitionException {
        JavaParser.variableDeclaratorId_return retval = new JavaParser.variableDeclaratorId_return();
        retval.start = input.LT(1);
        int variableDeclaratorId_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token char_literal89=null;
        Token char_literal90=null;

        Object name_tree=null;
        Object char_literal89_tree=null;
        Object char_literal90_tree=null;

        int count = 0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:5: (name= Identifier ( '[' ']' )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:9: name= Identifier ( '[' ']' )*
            {
            root_0 = (Object)adaptor.nil();

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId2571); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:25: ( '[' ']' )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==48) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:26: '[' ']'
            	    {
            	    char_literal89=(Token)match(input,48,FOLLOW_48_in_variableDeclaratorId2574); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal89_tree = (Object)adaptor.create(char_literal89);
            	    adaptor.addChild(root_0, char_literal89_tree);
            	    }
            	    char_literal90=(Token)match(input,49,FOLLOW_49_in_variableDeclaratorId2576); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal90_tree = (Object)adaptor.create(char_literal90);
            	    adaptor.addChild(root_0, char_literal90_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      count++;
            	    }

            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               retval.element = new StupidVariableDeclaratorId((name!=null?name.getText():null), count);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 48, variableDeclaratorId_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclaratorId"

    public static class variableInitializer_return extends ParserRuleReturnScope {
        public Expression element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:621:1: variableInitializer returns [Expression element] : (init= arrayInitializer | expr= expression );
    public final JavaParser.variableInitializer_return variableInitializer() throws RecognitionException {
        JavaParser.variableInitializer_return retval = new JavaParser.variableInitializer_return();
        retval.start = input.LT(1);
        int variableInitializer_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arrayInitializer_return init = null;

        JavaParser.expression_return expr = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:5: (init= arrayInitializer | expr= expression )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==44) ) {
                alt62=1;
            }
            else if ( (LA62_0==Identifier||(LA62_0>=FloatingPointLiteral && LA62_0<=DecimalLiteral)||LA62_0==47||(LA62_0>=56 && LA62_0<=63)||(LA62_0>=65 && LA62_0<=66)||(LA62_0>=69 && LA62_0<=72)||(LA62_0>=105 && LA62_0<=106)||(LA62_0>=109 && LA62_0<=113)) ) {
                alt62=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: init= arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2607);
                    init=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, init.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = init.element;
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:623:9: expr= expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer2621);
                    expr=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = expr.element;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 49, variableInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableInitializer"

    public static class arrayInitializer_return extends ParserRuleReturnScope {
        public Expression element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:626:1: arrayInitializer returns [Expression element] : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final JavaParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        JavaParser.arrayInitializer_return retval = new JavaParser.arrayInitializer_return();
        retval.start = input.LT(1);
        int arrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal91=null;
        Token char_literal93=null;
        Token char_literal95=null;
        Token char_literal96=null;
        JavaParser.variableInitializer_return variableInitializer92 = null;

        JavaParser.variableInitializer_return variableInitializer94 = null;


        Object char_literal91_tree=null;
        Object char_literal93_tree=null;
        Object char_literal95_tree=null;
        Object char_literal96_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:5: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:9: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal91=(Token)match(input,44,FOLLOW_44_in_arrayInitializer2654); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal91_tree = (Object)adaptor.create(char_literal91);
            adaptor.addChild(root_0, char_literal91_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:13: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==Identifier||(LA65_0>=FloatingPointLiteral && LA65_0<=DecimalLiteral)||LA65_0==44||LA65_0==47||(LA65_0>=56 && LA65_0<=63)||(LA65_0>=65 && LA65_0<=66)||(LA65_0>=69 && LA65_0<=72)||(LA65_0>=105 && LA65_0<=106)||(LA65_0>=109 && LA65_0<=113)) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:14: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2657);
                    variableInitializer92=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer92.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:34: ( ',' variableInitializer )*
                    loop63:
                    do {
                        int alt63=2;
                        int LA63_0 = input.LA(1);

                        if ( (LA63_0==41) ) {
                            int LA63_1 = input.LA(2);

                            if ( (LA63_1==Identifier||(LA63_1>=FloatingPointLiteral && LA63_1<=DecimalLiteral)||LA63_1==44||LA63_1==47||(LA63_1>=56 && LA63_1<=63)||(LA63_1>=65 && LA63_1<=66)||(LA63_1>=69 && LA63_1<=72)||(LA63_1>=105 && LA63_1<=106)||(LA63_1>=109 && LA63_1<=113)) ) {
                                alt63=1;
                            }


                        }


                        switch (alt63) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:35: ',' variableInitializer
                    	    {
                    	    char_literal93=(Token)match(input,41,FOLLOW_41_in_arrayInitializer2660); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal93_tree = (Object)adaptor.create(char_literal93);
                    	    adaptor.addChild(root_0, char_literal93_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2662);
                    	    variableInitializer94=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer94.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:61: ( ',' )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==41) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:62: ','
                            {
                            char_literal95=(Token)match(input,41,FOLLOW_41_in_arrayInitializer2667); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal95_tree = (Object)adaptor.create(char_literal95);
                            adaptor.addChild(root_0, char_literal95_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            char_literal96=(Token)match(input,45,FOLLOW_45_in_arrayInitializer2674); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal96_tree = (Object)adaptor.create(char_literal96);
            adaptor.addChild(root_0, char_literal96_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 50, arrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arrayInitializer"

    public static class modifier_return extends ParserRuleReturnScope {
        public Modifier element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifier"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:630:1: modifier returns [Modifier element] : ( annotation | mod= classOrInterfaceModifier | 'native' | 'synchronized' | 'transient' | 'volatile' );
    public final JavaParser.modifier_return modifier() throws RecognitionException {
        JavaParser.modifier_return retval = new JavaParser.modifier_return();
        retval.start = input.LT(1);
        int modifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal98=null;
        Token string_literal99=null;
        Token string_literal100=null;
        Token string_literal101=null;
        JavaParser.classOrInterfaceModifier_return mod = null;

        JavaParser.annotation_return annotation97 = null;


        Object string_literal98_tree=null;
        Object string_literal99_tree=null;
        Object string_literal100_tree=null;
        Object string_literal101_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:631:5: ( annotation | mod= classOrInterfaceModifier | 'native' | 'synchronized' | 'transient' | 'volatile' )
            int alt66=6;
            switch ( input.LA(1) ) {
            case 73:
                {
                int LA66_1 = input.LA(2);

                if ( (LA66_1==Identifier) ) {
                    int LA66_7 = input.LA(3);

                    if ( (synpred81_Java()) ) {
                        alt66=1;
                    }
                    else if ( (synpred82_Java()) ) {
                        alt66=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 66, 7, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 1, input);

                    throw nvae;
                }
                }
                break;
            case 28:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                {
                alt66=2;
                }
                break;
            case 52:
                {
                alt66=3;
                }
                break;
            case 53:
                {
                alt66=4;
                }
                break;
            case 54:
                {
                alt66=5;
                }
                break;
            case 55:
                {
                alt66=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:631:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_modifier2697);
                    annotation97=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation97.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:632:9: mod= classOrInterfaceModifier
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceModifier_in_modifier2709);
                    mod=classOrInterfaceModifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, mod.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element = mod.element;
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:9: 'native'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal98=(Token)match(input,52,FOLLOW_52_in_modifier2721); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal98_tree = (Object)adaptor.create(string_literal98);
                    adaptor.addChild(root_0, string_literal98_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Native();
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:634:9: 'synchronized'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal99=(Token)match(input,53,FOLLOW_53_in_modifier2733); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal99_tree = (Object)adaptor.create(string_literal99);
                    adaptor.addChild(root_0, string_literal99_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Synchronized();
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:635:9: 'transient'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal100=(Token)match(input,54,FOLLOW_54_in_modifier2745); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal100_tree = (Object)adaptor.create(string_literal100);
                    adaptor.addChild(root_0, string_literal100_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Transient();
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:636:9: 'volatile'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal101=(Token)match(input,55,FOLLOW_55_in_modifier2757); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal101_tree = (Object)adaptor.create(string_literal101);
                    adaptor.addChild(root_0, string_literal101_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Volatile();
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 51, modifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "modifier"

    public static class packageOrTypeName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "packageOrTypeName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:639:1: packageOrTypeName : qualifiedName ;
    public final JavaParser.packageOrTypeName_return packageOrTypeName() throws RecognitionException {
        JavaParser.packageOrTypeName_return retval = new JavaParser.packageOrTypeName_return();
        retval.start = input.LT(1);
        int packageOrTypeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName102 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:640:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:640:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_packageOrTypeName2778);
            qualifiedName102=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName102.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 52, packageOrTypeName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "packageOrTypeName"

    public static class enumConstantName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstantName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:643:1: enumConstantName : Identifier ;
    public final JavaParser.enumConstantName_return enumConstantName() throws RecognitionException {
        JavaParser.enumConstantName_return retval = new JavaParser.enumConstantName_return();
        retval.start = input.LT(1);
        int enumConstantName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier103=null;

        Object Identifier103_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:644:5: ( Identifier )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:644:9: Identifier
            {
            root_0 = (Object)adaptor.nil();

            Identifier103=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstantName2797); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier103_tree = (Object)adaptor.create(Identifier103);
            adaptor.addChild(root_0, Identifier103_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 53, enumConstantName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstantName"

    public static class typeName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:647:1: typeName : qualifiedName ;
    public final JavaParser.typeName_return typeName() throws RecognitionException {
        JavaParser.typeName_return retval = new JavaParser.typeName_return();
        retval.start = input.LT(1);
        int typeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName104 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:648:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:648:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_typeName2816);
            qualifiedName104=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName104.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 54, typeName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeName"

    public static class type_return extends ParserRuleReturnScope {
        public TypeReference element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:651:1: type returns [TypeReference element] : (cd= classOrInterfaceType ( '[' ']' )* | pt= primitiveType ( '[' ']' )* );
    public final JavaParser.type_return type() throws RecognitionException {
        JavaParser.type_return retval = new JavaParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal105=null;
        Token char_literal106=null;
        Token char_literal107=null;
        Token char_literal108=null;
        JavaParser.classOrInterfaceType_return cd = null;

        JavaParser.primitiveType_return pt = null;


        Object char_literal105_tree=null;
        Object char_literal106_tree=null;
        Object char_literal107_tree=null;
        Object char_literal108_tree=null;

        int dimension=0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:653:2: (cd= classOrInterfaceType ( '[' ']' )* | pt= primitiveType ( '[' ']' )* )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==Identifier) ) {
                alt69=1;
            }
            else if ( ((LA69_0>=56 && LA69_0<=63)) ) {
                alt69=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:653:4: cd= classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_type2840);
                    cd=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:653:28: ( '[' ']' )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==48) ) {
                            alt67=1;
                        }


                        switch (alt67) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:653:29: '[' ']'
                    	    {
                    	    char_literal105=(Token)match(input,48,FOLLOW_48_in_type2843); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal105_tree = (Object)adaptor.create(char_literal105);
                    	    adaptor.addChild(root_0, char_literal105_tree);
                    	    }
                    	    char_literal106=(Token)match(input,49,FOLLOW_49_in_type2845); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal106_tree = (Object)adaptor.create(char_literal106);
                    	    adaptor.addChild(root_0, char_literal106_tree);
                    	    }
                    	    if ( state.backtracking==0 ) {
                    	      dimension++;
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop67;
                        }
                    } while (true);

                    if ( state.backtracking==0 ) {
                      retval.element = cd.element.toArray(dimension);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:4: pt= primitiveType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_type2858);
                    pt=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, pt.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:21: ( '[' ']' )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( (LA68_0==48) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:22: '[' ']'
                    	    {
                    	    char_literal107=(Token)match(input,48,FOLLOW_48_in_type2861); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal107_tree = (Object)adaptor.create(char_literal107);
                    	    adaptor.addChild(root_0, char_literal107_tree);
                    	    }
                    	    char_literal108=(Token)match(input,49,FOLLOW_49_in_type2863); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal108_tree = (Object)adaptor.create(char_literal108);
                    	    adaptor.addChild(root_0, char_literal108_tree);
                    	    }
                    	    if ( state.backtracking==0 ) {
                    	      dimension++;
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop68;
                        }
                    } while (true);

                    if ( state.backtracking==0 ) {
                      retval.element = pt.element.toArray(dimension);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 55, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class classOrInterfaceType_return extends ParserRuleReturnScope {
        public JavaTypeReference element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceType"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:657:1: classOrInterfaceType returns [JavaTypeReference element] : Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ;
    public final JavaParser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        JavaParser.classOrInterfaceType_return retval = new JavaParser.classOrInterfaceType_return();
        retval.start = input.LT(1);
        int classOrInterfaceType_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier109=null;
        Token char_literal111=null;
        Token Identifier112=null;
        JavaParser.typeArguments_return typeArguments110 = null;

        JavaParser.typeArguments_return typeArguments113 = null;


        Object Identifier109_tree=null;
        Object char_literal111_tree=null;
        Object Identifier112_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier109=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2883); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier109_tree = (Object)adaptor.create(Identifier109);
            adaptor.addChild(root_0, Identifier109_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:15: ( typeArguments )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==40) ) {
                int LA70_1 = input.LA(2);

                if ( (LA70_1==Identifier||(LA70_1>=56 && LA70_1<=64)) ) {
                    alt70=1;
                }
            }
            switch (alt70) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2885);
                    typeArguments110=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments110.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:30: ( '.' Identifier ( typeArguments )? )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==29) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:31: '.' Identifier ( typeArguments )?
            	    {
            	    char_literal111=(Token)match(input,29,FOLLOW_29_in_classOrInterfaceType2889); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal111_tree = (Object)adaptor.create(char_literal111);
            	    adaptor.addChild(root_0, char_literal111_tree);
            	    }
            	    Identifier112=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2891); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier112_tree = (Object)adaptor.create(Identifier112);
            	    adaptor.addChild(root_0, Identifier112_tree);
            	    }
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:46: ( typeArguments )?
            	    int alt71=2;
            	    int LA71_0 = input.LA(1);

            	    if ( (LA71_0==40) ) {
            	        int LA71_1 = input.LA(2);

            	        if ( (LA71_1==Identifier||(LA71_1>=56 && LA71_1<=64)) ) {
            	            alt71=1;
            	        }
            	    }
            	    switch (alt71) {
            	        case 1 :
            	            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2893);
            	            typeArguments113=typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments113.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop72;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 56, classOrInterfaceType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceType"

    public static class primitiveType_return extends ParserRuleReturnScope {
        public JavaTypeReference element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primitiveType"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:661:1: primitiveType returns [JavaTypeReference element] : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final JavaParser.primitiveType_return primitiveType() throws RecognitionException {
        JavaParser.primitiveType_return retval = new JavaParser.primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        Object root_0 = null;

        Token set114=null;

        Object set114_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set114=(Token)input.LT(1);
            if ( (input.LA(1)>=56 && input.LA(1)<=63) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set114));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 57, primitiveType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primitiveType"

    public static class variableModifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableModifier"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:672:1: variableModifier : ( 'final' | annotation );
    public final JavaParser.variableModifier_return variableModifier() throws RecognitionException {
        JavaParser.variableModifier_return retval = new JavaParser.variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal115=null;
        JavaParser.annotation_return annotation116 = null;


        Object string_literal115_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:5: ( 'final' | annotation )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==35) ) {
                alt73=1;
            }
            else if ( (LA73_0==73) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal115=(Token)match(input,35,FOLLOW_35_in_variableModifier3006); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal115_tree = (Object)adaptor.create(string_literal115);
                    adaptor.addChild(root_0, string_literal115_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:674:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_variableModifier3016);
                    annotation116=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation116.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 58, variableModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifier"

    public static class typeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeArguments"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:677:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final JavaParser.typeArguments_return typeArguments() throws RecognitionException {
        JavaParser.typeArguments_return retval = new JavaParser.typeArguments_return();
        retval.start = input.LT(1);
        int typeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal117=null;
        Token char_literal119=null;
        Token char_literal121=null;
        JavaParser.typeArgument_return typeArgument118 = null;

        JavaParser.typeArgument_return typeArgument120 = null;


        Object char_literal117_tree=null;
        Object char_literal119_tree=null;
        Object char_literal121_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:678:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:678:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal117=(Token)match(input,40,FOLLOW_40_in_typeArguments3035); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal117_tree = (Object)adaptor.create(char_literal117);
            adaptor.addChild(root_0, char_literal117_tree);
            }
            pushFollow(FOLLOW_typeArgument_in_typeArguments3037);
            typeArgument118=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument118.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:678:26: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==41) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:678:27: ',' typeArgument
            	    {
            	    char_literal119=(Token)match(input,41,FOLLOW_41_in_typeArguments3040); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal119_tree = (Object)adaptor.create(char_literal119);
            	    adaptor.addChild(root_0, char_literal119_tree);
            	    }
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments3042);
            	    typeArgument120=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument120.getTree());

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            char_literal121=(Token)match(input,42,FOLLOW_42_in_typeArguments3046); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal121_tree = (Object)adaptor.create(char_literal121);
            adaptor.addChild(root_0, char_literal121_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 59, typeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeArguments"

    public static class typeArgument_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeArgument"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:681:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final JavaParser.typeArgument_return typeArgument() throws RecognitionException {
        JavaParser.typeArgument_return retval = new JavaParser.typeArgument_return();
        retval.start = input.LT(1);
        int typeArgument_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal123=null;
        Token set124=null;
        JavaParser.type_return type122 = null;

        JavaParser.type_return type125 = null;


        Object char_literal123_tree=null;
        Object set124_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:682:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==Identifier||(LA76_0>=56 && LA76_0<=63)) ) {
                alt76=1;
            }
            else if ( (LA76_0==64) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:682:9: type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_typeArgument3069);
                    type122=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type122.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:683:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal123=(Token)match(input,64,FOLLOW_64_in_typeArgument3079); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal123_tree = (Object)adaptor.create(char_literal123);
                    adaptor.addChild(root_0, char_literal123_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:683:13: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==38||LA75_0==65) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:683:14: ( 'extends' | 'super' ) type
                            {
                            set124=(Token)input.LT(1);
                            if ( input.LA(1)==38||input.LA(1)==65 ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set124));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument3090);
                            type125=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type125.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 60, typeArgument_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeArgument"

    public static class qualifiedNameList_return extends ParserRuleReturnScope {
        public List<String> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedNameList"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:686:1: qualifiedNameList returns [List<String> element] : q= qualifiedName ( ',' qn= qualifiedName )* ;
    public final JavaParser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        JavaParser.qualifiedNameList_return retval = new JavaParser.qualifiedNameList_return();
        retval.start = input.LT(1);
        int qualifiedNameList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal126=null;
        JavaParser.qualifiedName_return q = null;

        JavaParser.qualifiedName_return qn = null;


        Object char_literal126_tree=null;

        retval.element = new ArrayList<String>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:5: (q= qualifiedName ( ',' qn= qualifiedName )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:9: q= qualifiedName ( ',' qn= qualifiedName )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3124);
            q=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, q.getTree());
            if ( state.backtracking==0 ) {
              retval.element.add((q!=null?input.toString(q.start,q.stop):null));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:56: ( ',' qn= qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==41) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:57: ',' qn= qualifiedName
            	    {
            	    char_literal126=(Token)match(input,41,FOLLOW_41_in_qualifiedNameList3129); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal126_tree = (Object)adaptor.create(char_literal126);
            	    adaptor.addChild(root_0, char_literal126_tree);
            	    }
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3133);
            	    qn=qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, qn.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.element.add((qn!=null?input.toString(qn.start,qn.stop):null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 61, qualifiedNameList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedNameList"

    public static class formalParameters_return extends ParserRuleReturnScope {
        public List<FormalParameter> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameters"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:691:1: formalParameters returns [List<FormalParameter> element] : '(' (pars= formalParameterDecls )? ')' ;
    public final JavaParser.formalParameters_return formalParameters() throws RecognitionException {
        JavaParser.formalParameters_return retval = new JavaParser.formalParameters_return();
        retval.start = input.LT(1);
        int formalParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal127=null;
        Token char_literal128=null;
        JavaParser.formalParameterDecls_return pars = null;


        Object char_literal127_tree=null;
        Object char_literal128_tree=null;

        retval.element = new ArrayList<FormalParameter>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:5: ( '(' (pars= formalParameterDecls )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:9: '(' (pars= formalParameterDecls )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal127=(Token)match(input,66,FOLLOW_66_in_formalParameters3164); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal127_tree = (Object)adaptor.create(char_literal127);
            adaptor.addChild(root_0, char_literal127_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:13: (pars= formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==35||(LA78_0>=56 && LA78_0<=63)||LA78_0==73) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:14: pars= formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3169);
                    pars=formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, pars.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element=pars.element;
                    }

                    }
                    break;

            }

            char_literal128=(Token)match(input,67,FOLLOW_67_in_formalParameters3175); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal128_tree = (Object)adaptor.create(char_literal128);
            adaptor.addChild(root_0, char_literal128_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 62, formalParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameters"

    public static class formalParameterDecls_return extends ParserRuleReturnScope {
        public List<FormalParameter> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameterDecls"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:696:1: formalParameterDecls returns [List<FormalParameter> element] : variableModifiers type formalParameterDeclsRest ;
    public final JavaParser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        JavaParser.formalParameterDecls_return retval = new JavaParser.formalParameterDecls_return();
        retval.start = input.LT(1);
        int formalParameterDecls_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers129 = null;

        JavaParser.type_return type130 = null;

        JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest131 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:5: ( variableModifiers type formalParameterDeclsRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:9: variableModifiers type formalParameterDeclsRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameterDecls3202);
            variableModifiers129=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers129.getTree());
            pushFollow(FOLLOW_type_in_formalParameterDecls3204);
            type130=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type130.getTree());
            pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls3206);
            formalParameterDeclsRest131=formalParameterDeclsRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDeclsRest131.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 63, formalParameterDecls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameterDecls"

    public static class formalParameterDeclsRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameterDeclsRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:700:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest() throws RecognitionException {
        JavaParser.formalParameterDeclsRest_return retval = new JavaParser.formalParameterDeclsRest_return();
        retval.start = input.LT(1);
        int formalParameterDeclsRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal133=null;
        Token string_literal135=null;
        JavaParser.variableDeclaratorId_return variableDeclaratorId132 = null;

        JavaParser.formalParameterDecls_return formalParameterDecls134 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId136 = null;


        Object char_literal133_tree=null;
        Object string_literal135_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:701:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==Identifier) ) {
                alt80=1;
            }
            else if ( (LA80_0==68) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:701:9: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3229);
                    variableDeclaratorId132=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId132.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:701:30: ( ',' formalParameterDecls )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==41) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:701:31: ',' formalParameterDecls
                            {
                            char_literal133=(Token)match(input,41,FOLLOW_41_in_formalParameterDeclsRest3232); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal133_tree = (Object)adaptor.create(char_literal133);
                            adaptor.addChild(root_0, char_literal133_tree);
                            }
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest3234);
                            formalParameterDecls134=formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls134.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:702:9: '...' variableDeclaratorId
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal135=(Token)match(input,68,FOLLOW_68_in_formalParameterDeclsRest3246); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal135_tree = (Object)adaptor.create(string_literal135);
                    adaptor.addChild(root_0, string_literal135_tree);
                    }
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3248);
                    variableDeclaratorId136=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId136.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 64, formalParameterDeclsRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameterDeclsRest"

    public static class methodBody_return extends ParserRuleReturnScope {
        public Block element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:705:1: methodBody returns [Block element] : block ;
    public final JavaParser.methodBody_return methodBody() throws RecognitionException {
        JavaParser.methodBody_return retval = new JavaParser.methodBody_return();
        retval.start = input.LT(1);
        int methodBody_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.block_return block137 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:706:5: ( block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:706:9: block
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_block_in_methodBody3275);
            block137=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block137.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 65, methodBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodBody"

    public static class constructorBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructorBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:709:1: constructorBody : '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' ;
    public final JavaParser.constructorBody_return constructorBody() throws RecognitionException {
        JavaParser.constructorBody_return retval = new JavaParser.constructorBody_return();
        retval.start = input.LT(1);
        int constructorBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal138=null;
        Token char_literal141=null;
        JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation139 = null;

        JavaParser.blockStatement_return blockStatement140 = null;


        Object char_literal138_tree=null;
        Object char_literal141_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:710:5: ( '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:710:9: '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal138=(Token)match(input,44,FOLLOW_44_in_constructorBody3294); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal138_tree = (Object)adaptor.create(char_literal138);
            adaptor.addChild(root_0, char_literal138_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:710:13: ( explicitConstructorInvocation )?
            int alt81=2;
            alt81 = dfa81.predict(input);
            switch (alt81) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: explicitConstructorInvocation
                    {
                    pushFollow(FOLLOW_explicitConstructorInvocation_in_constructorBody3296);
                    explicitConstructorInvocation139=explicitConstructorInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitConstructorInvocation139.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:710:44: ( blockStatement )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( ((LA82_0>=Identifier && LA82_0<=ASSERT)||LA82_0==26||LA82_0==28||(LA82_0>=31 && LA82_0<=37)||LA82_0==44||(LA82_0>=46 && LA82_0<=47)||LA82_0==53||(LA82_0>=56 && LA82_0<=63)||(LA82_0>=65 && LA82_0<=66)||(LA82_0>=69 && LA82_0<=73)||LA82_0==76||(LA82_0>=78 && LA82_0<=81)||(LA82_0>=83 && LA82_0<=87)||(LA82_0>=105 && LA82_0<=106)||(LA82_0>=109 && LA82_0<=113)) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_constructorBody3299);
            	    blockStatement140=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement140.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            char_literal141=(Token)match(input,45,FOLLOW_45_in_constructorBody3302); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal141_tree = (Object)adaptor.create(char_literal141);
            adaptor.addChild(root_0, char_literal141_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 66, constructorBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constructorBody"

    public static class explicitConstructorInvocation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitConstructorInvocation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        JavaParser.explicitConstructorInvocation_return retval = new JavaParser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);
        int explicitConstructorInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token set143=null;
        Token char_literal145=null;
        Token char_literal147=null;
        Token string_literal149=null;
        Token char_literal151=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments142 = null;

        JavaParser.arguments_return arguments144 = null;

        JavaParser.primary_return primary146 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments148 = null;

        JavaParser.arguments_return arguments150 = null;


        Object set143_tree=null;
        Object char_literal145_tree=null;
        Object char_literal147_tree=null;
        Object string_literal149_tree=null;
        Object char_literal151_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:714:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt85=2;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:714:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:714:9: ( nonWildcardTypeArguments )?
                    int alt83=2;
                    int LA83_0 = input.LA(1);

                    if ( (LA83_0==40) ) {
                        alt83=1;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3321);
                            nonWildcardTypeArguments142=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments142.getTree());

                            }
                            break;

                    }

                    set143=(Token)input.LT(1);
                    if ( input.LA(1)==65||input.LA(1)==69 ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set143));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3332);
                    arguments144=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments144.getTree());
                    char_literal145=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation3334); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal145_tree = (Object)adaptor.create(char_literal145);
                    adaptor.addChild(root_0, char_literal145_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:715:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3344);
                    primary146=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary146.getTree());
                    char_literal147=(Token)match(input,29,FOLLOW_29_in_explicitConstructorInvocation3346); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal147_tree = (Object)adaptor.create(char_literal147);
                    adaptor.addChild(root_0, char_literal147_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:715:21: ( nonWildcardTypeArguments )?
                    int alt84=2;
                    int LA84_0 = input.LA(1);

                    if ( (LA84_0==40) ) {
                        alt84=1;
                    }
                    switch (alt84) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3348);
                            nonWildcardTypeArguments148=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments148.getTree());

                            }
                            break;

                    }

                    string_literal149=(Token)match(input,65,FOLLOW_65_in_explicitConstructorInvocation3351); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal149_tree = (Object)adaptor.create(string_literal149);
                    adaptor.addChild(root_0, string_literal149_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3353);
                    arguments150=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments150.getTree());
                    char_literal151=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation3355); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal151_tree = (Object)adaptor.create(char_literal151);
                    adaptor.addChild(root_0, char_literal151_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 67, explicitConstructorInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "explicitConstructorInvocation"

    public static class qualifiedName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:719:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final JavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
        JavaParser.qualifiedName_return retval = new JavaParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier152=null;
        Token char_literal153=null;
        Token Identifier154=null;

        Object Identifier152_tree=null;
        Object char_literal153_tree=null;
        Object Identifier154_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:720:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:720:9: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier152=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName3375); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier152_tree = (Object)adaptor.create(Identifier152);
            adaptor.addChild(root_0, Identifier152_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:720:20: ( '.' Identifier )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==29) ) {
                    int LA86_2 = input.LA(2);

                    if ( (LA86_2==Identifier) ) {
                        alt86=1;
                    }


                }


                switch (alt86) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:720:21: '.' Identifier
            	    {
            	    char_literal153=(Token)match(input,29,FOLLOW_29_in_qualifiedName3378); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal153_tree = (Object)adaptor.create(char_literal153);
            	    adaptor.addChild(root_0, char_literal153_tree);
            	    }
            	    Identifier154=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName3380); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier154_tree = (Object)adaptor.create(Identifier154);
            	    adaptor.addChild(root_0, Identifier154_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 68, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedName"

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:723:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final JavaParser.literal_return literal() throws RecognitionException {
        JavaParser.literal_return retval = new JavaParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token FloatingPointLiteral156=null;
        Token CharacterLiteral157=null;
        Token StringLiteral158=null;
        Token string_literal160=null;
        JavaParser.integerLiteral_return integerLiteral155 = null;

        JavaParser.booleanLiteral_return booleanLiteral159 = null;


        Object FloatingPointLiteral156_tree=null;
        Object CharacterLiteral157_tree=null;
        Object StringLiteral158_tree=null;
        Object string_literal160_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:724:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
            int alt87=6;
            switch ( input.LA(1) ) {
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt87=1;
                }
                break;
            case FloatingPointLiteral:
                {
                alt87=2;
                }
                break;
            case CharacterLiteral:
                {
                alt87=3;
                }
                break;
            case StringLiteral:
                {
                alt87=4;
                }
                break;
            case 71:
            case 72:
                {
                alt87=5;
                }
                break;
            case 70:
                {
                alt87=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }

            switch (alt87) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:724:9: integerLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_integerLiteral_in_literal3406);
                    integerLiteral155=integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, integerLiteral155.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:9: FloatingPointLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    FloatingPointLiteral156=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal3416); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FloatingPointLiteral156_tree = (Object)adaptor.create(FloatingPointLiteral156);
                    adaptor.addChild(root_0, FloatingPointLiteral156_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:726:9: CharacterLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    CharacterLiteral157=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal3426); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CharacterLiteral157_tree = (Object)adaptor.create(CharacterLiteral157);
                    adaptor.addChild(root_0, CharacterLiteral157_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:727:9: StringLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    StringLiteral158=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal3436); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    StringLiteral158_tree = (Object)adaptor.create(StringLiteral158);
                    adaptor.addChild(root_0, StringLiteral158_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:728:9: booleanLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_booleanLiteral_in_literal3446);
                    booleanLiteral159=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, booleanLiteral159.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: 'null'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal160=(Token)match(input,70,FOLLOW_70_in_literal3456); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal160_tree = (Object)adaptor.create(string_literal160);
                    adaptor.addChild(root_0, string_literal160_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 69, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class integerLiteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "integerLiteral"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:732:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final JavaParser.integerLiteral_return integerLiteral() throws RecognitionException {
        JavaParser.integerLiteral_return retval = new JavaParser.integerLiteral_return();
        retval.start = input.LT(1);
        int integerLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set161=null;

        Object set161_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set161=(Token)input.LT(1);
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set161));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 70, integerLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "integerLiteral"

    public static class booleanLiteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "booleanLiteral"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:738:1: booleanLiteral : ( 'true' | 'false' );
    public final JavaParser.booleanLiteral_return booleanLiteral() throws RecognitionException {
        JavaParser.booleanLiteral_return retval = new JavaParser.booleanLiteral_return();
        retval.start = input.LT(1);
        int booleanLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set162=null;

        Object set162_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:739:5: ( 'true' | 'false' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set162=(Token)input.LT(1);
            if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set162));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 71, booleanLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "booleanLiteral"

    public static class annotations_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotations"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:1: annotations : ( annotation )+ ;
    public final JavaParser.annotations_return annotations() throws RecognitionException {
        JavaParser.annotations_return retval = new JavaParser.annotations_return();
        retval.start = input.LT(1);
        int annotations_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotation_return annotation163 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:5: ( ( annotation )+ )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:9: ( annotation )+
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:9: ( annotation )+
            int cnt88=0;
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==73) ) {
                    int LA88_2 = input.LA(2);

                    if ( (LA88_2==Identifier) ) {
                        int LA88_3 = input.LA(3);

                        if ( (synpred123_Java()) ) {
                            alt88=1;
                        }


                    }


                }


                switch (alt88) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations3545);
            	    annotation163=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation163.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt88 >= 1 ) break loop88;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(88, input);
                        throw eee;
                }
                cnt88++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 72, annotations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotations"

    public static class annotation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:1: annotation : '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final JavaParser.annotation_return annotation() throws RecognitionException {
        JavaParser.annotation_return retval = new JavaParser.annotation_return();
        retval.start = input.LT(1);
        int annotation_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal164=null;
        Token char_literal166=null;
        Token char_literal169=null;
        JavaParser.annotationName_return annotationName165 = null;

        JavaParser.elementValuePairs_return elementValuePairs167 = null;

        JavaParser.elementValue_return elementValue168 = null;


        Object char_literal164_tree=null;
        Object char_literal166_tree=null;
        Object char_literal169_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:5: ( '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:9: '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (Object)adaptor.nil();

            char_literal164=(Token)match(input,73,FOLLOW_73_in_annotation3565); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal164_tree = (Object)adaptor.create(char_literal164);
            adaptor.addChild(root_0, char_literal164_tree);
            }
            pushFollow(FOLLOW_annotationName_in_annotation3567);
            annotationName165=annotationName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationName165.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:28: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==66) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:30: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal166=(Token)match(input,66,FOLLOW_66_in_annotation3571); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal166_tree = (Object)adaptor.create(char_literal166);
                    adaptor.addChild(root_0, char_literal166_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:34: ( elementValuePairs | elementValue )?
                    int alt89=3;
                    int LA89_0 = input.LA(1);

                    if ( (LA89_0==Identifier) ) {
                        int LA89_1 = input.LA(2);

                        if ( (LA89_1==51) ) {
                            alt89=1;
                        }
                        else if ( ((LA89_1>=29 && LA89_1<=30)||LA89_1==40||(LA89_1>=42 && LA89_1<=43)||LA89_1==48||LA89_1==64||(LA89_1>=66 && LA89_1<=67)||(LA89_1>=98 && LA89_1<=110)) ) {
                            alt89=2;
                        }
                    }
                    else if ( ((LA89_0>=FloatingPointLiteral && LA89_0<=DecimalLiteral)||LA89_0==44||LA89_0==47||(LA89_0>=56 && LA89_0<=63)||(LA89_0>=65 && LA89_0<=66)||(LA89_0>=69 && LA89_0<=73)||(LA89_0>=105 && LA89_0<=106)||(LA89_0>=109 && LA89_0<=113)) ) {
                        alt89=2;
                    }
                    switch (alt89) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:36: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation3575);
                            elementValuePairs167=elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePairs167.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:56: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation3579);
                            elementValue168=elementValue();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue168.getTree());

                            }
                            break;

                    }

                    char_literal169=(Token)match(input,67,FOLLOW_67_in_annotation3584); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal169_tree = (Object)adaptor.create(char_literal169);
                    adaptor.addChild(root_0, char_literal169_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 73, annotation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotation"

    public static class annotationName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:753:1: annotationName : Identifier ( '.' Identifier )* ;
    public final JavaParser.annotationName_return annotationName() throws RecognitionException {
        JavaParser.annotationName_return retval = new JavaParser.annotationName_return();
        retval.start = input.LT(1);
        int annotationName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier170=null;
        Token char_literal171=null;
        Token Identifier172=null;

        Object Identifier170_tree=null;
        Object char_literal171_tree=null;
        Object Identifier172_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:7: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier170=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName3608); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier170_tree = (Object)adaptor.create(Identifier170);
            adaptor.addChild(root_0, Identifier170_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:18: ( '.' Identifier )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==29) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:19: '.' Identifier
            	    {
            	    char_literal171=(Token)match(input,29,FOLLOW_29_in_annotationName3611); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal171_tree = (Object)adaptor.create(char_literal171);
            	    adaptor.addChild(root_0, char_literal171_tree);
            	    }
            	    Identifier172=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName3613); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier172_tree = (Object)adaptor.create(Identifier172);
            	    adaptor.addChild(root_0, Identifier172_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 74, annotationName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationName"

    public static class elementValuePairs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValuePairs"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:757:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final JavaParser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        JavaParser.elementValuePairs_return retval = new JavaParser.elementValuePairs_return();
        retval.start = input.LT(1);
        int elementValuePairs_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal174=null;
        JavaParser.elementValuePair_return elementValuePair173 = null;

        JavaParser.elementValuePair_return elementValuePair175 = null;


        Object char_literal174_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:5: ( elementValuePair ( ',' elementValuePair )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3634);
            elementValuePair173=elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair173.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:26: ( ',' elementValuePair )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==41) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:27: ',' elementValuePair
            	    {
            	    char_literal174=(Token)match(input,41,FOLLOW_41_in_elementValuePairs3637); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal174_tree = (Object)adaptor.create(char_literal174);
            	    adaptor.addChild(root_0, char_literal174_tree);
            	    }
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3639);
            	    elementValuePair175=elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair175.getTree());

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 75, elementValuePairs_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValuePairs"

    public static class elementValuePair_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValuePair"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:761:1: elementValuePair : Identifier '=' elementValue ;
    public final JavaParser.elementValuePair_return elementValuePair() throws RecognitionException {
        JavaParser.elementValuePair_return retval = new JavaParser.elementValuePair_return();
        retval.start = input.LT(1);
        int elementValuePair_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier176=null;
        Token char_literal177=null;
        JavaParser.elementValue_return elementValue178 = null;


        Object Identifier176_tree=null;
        Object char_literal177_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:5: ( Identifier '=' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:9: Identifier '=' elementValue
            {
            root_0 = (Object)adaptor.nil();

            Identifier176=(Token)match(input,Identifier,FOLLOW_Identifier_in_elementValuePair3660); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier176_tree = (Object)adaptor.create(Identifier176);
            adaptor.addChild(root_0, Identifier176_tree);
            }
            char_literal177=(Token)match(input,51,FOLLOW_51_in_elementValuePair3662); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal177_tree = (Object)adaptor.create(char_literal177);
            adaptor.addChild(root_0, char_literal177_tree);
            }
            pushFollow(FOLLOW_elementValue_in_elementValuePair3664);
            elementValue178=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue178.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 76, elementValuePair_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValuePair"

    public static class elementValue_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValue"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:765:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final JavaParser.elementValue_return elementValue() throws RecognitionException {
        JavaParser.elementValue_return retval = new JavaParser.elementValue_return();
        retval.start = input.LT(1);
        int elementValue_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression179 = null;

        JavaParser.annotation_return annotation180 = null;

        JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer181 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt93=3;
            switch ( input.LA(1) ) {
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 47:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 65:
            case 66:
            case 69:
            case 70:
            case 71:
            case 72:
            case 105:
            case 106:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
                {
                alt93=1;
                }
                break;
            case 73:
                {
                alt93=2;
                }
                break;
            case 44:
                {
                alt93=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }

            switch (alt93) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:9: conditionalExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3687);
                    conditionalExpression179=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression179.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:767:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_elementValue3697);
                    annotation180=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation180.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:9: elementValueArrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue3707);
                    elementValueArrayInitializer181=elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer181.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 77, elementValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValue"

    public static class elementValueArrayInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValueArrayInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:771:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        JavaParser.elementValueArrayInitializer_return retval = new JavaParser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);
        int elementValueArrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal182=null;
        Token char_literal184=null;
        Token char_literal186=null;
        Token char_literal187=null;
        JavaParser.elementValue_return elementValue183 = null;

        JavaParser.elementValue_return elementValue185 = null;


        Object char_literal182_tree=null;
        Object char_literal184_tree=null;
        Object char_literal186_tree=null;
        Object char_literal187_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal182=(Token)match(input,44,FOLLOW_44_in_elementValueArrayInitializer3730); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal182_tree = (Object)adaptor.create(char_literal182);
            adaptor.addChild(root_0, char_literal182_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:13: ( elementValue ( ',' elementValue )* )?
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==Identifier||(LA95_0>=FloatingPointLiteral && LA95_0<=DecimalLiteral)||LA95_0==44||LA95_0==47||(LA95_0>=56 && LA95_0<=63)||(LA95_0>=65 && LA95_0<=66)||(LA95_0>=69 && LA95_0<=73)||(LA95_0>=105 && LA95_0<=106)||(LA95_0>=109 && LA95_0<=113)) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:14: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3733);
                    elementValue183=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue183.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:27: ( ',' elementValue )*
                    loop94:
                    do {
                        int alt94=2;
                        int LA94_0 = input.LA(1);

                        if ( (LA94_0==41) ) {
                            int LA94_1 = input.LA(2);

                            if ( (LA94_1==Identifier||(LA94_1>=FloatingPointLiteral && LA94_1<=DecimalLiteral)||LA94_1==44||LA94_1==47||(LA94_1>=56 && LA94_1<=63)||(LA94_1>=65 && LA94_1<=66)||(LA94_1>=69 && LA94_1<=73)||(LA94_1>=105 && LA94_1<=106)||(LA94_1>=109 && LA94_1<=113)) ) {
                                alt94=1;
                            }


                        }


                        switch (alt94) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:28: ',' elementValue
                    	    {
                    	    char_literal184=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer3736); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal184_tree = (Object)adaptor.create(char_literal184);
                    	    adaptor.addChild(root_0, char_literal184_tree);
                    	    }
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3738);
                    	    elementValue185=elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue185.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop94;
                        }
                    } while (true);


                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:49: ( ',' )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==41) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:50: ','
                    {
                    char_literal186=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer3745); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal186_tree = (Object)adaptor.create(char_literal186);
                    adaptor.addChild(root_0, char_literal186_tree);
                    }

                    }
                    break;

            }

            char_literal187=(Token)match(input,45,FOLLOW_45_in_elementValueArrayInitializer3749); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal187_tree = (Object)adaptor.create(char_literal187);
            adaptor.addChild(root_0, char_literal187_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 78, elementValueArrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValueArrayInitializer"

    public static class annotationTypeDeclaration_return extends ParserRuleReturnScope {
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:775:1: annotationTypeDeclaration returns [Type element] : '@' 'interface' Identifier annotationTypeBody ;
    public final JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        JavaParser.annotationTypeDeclaration_return retval = new JavaParser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal188=null;
        Token string_literal189=null;
        Token Identifier190=null;
        JavaParser.annotationTypeBody_return annotationTypeBody191 = null;


        Object char_literal188_tree=null;
        Object string_literal189_tree=null;
        Object Identifier190_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:776:5: ( '@' 'interface' Identifier annotationTypeBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:776:9: '@' 'interface' Identifier annotationTypeBody
            {
            root_0 = (Object)adaptor.nil();

            char_literal188=(Token)match(input,73,FOLLOW_73_in_annotationTypeDeclaration3776); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal188_tree = (Object)adaptor.create(char_literal188);
            adaptor.addChild(root_0, char_literal188_tree);
            }
            string_literal189=(Token)match(input,46,FOLLOW_46_in_annotationTypeDeclaration3778); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal189_tree = (Object)adaptor.create(string_literal189);
            adaptor.addChild(root_0, string_literal189_tree);
            }
            Identifier190=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration3780); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier190_tree = (Object)adaptor.create(Identifier190);
            adaptor.addChild(root_0, Identifier190_tree);
            }
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3782);
            annotationTypeBody191=annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody191.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 79, annotationTypeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeDeclaration"

    public static class annotationTypeBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:779:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final JavaParser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        JavaParser.annotationTypeBody_return retval = new JavaParser.annotationTypeBody_return();
        retval.start = input.LT(1);
        int annotationTypeBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal192=null;
        Token char_literal194=null;
        JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration193 = null;


        Object char_literal192_tree=null;
        Object char_literal194_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal192=(Token)match(input,44,FOLLOW_44_in_annotationTypeBody3805); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal192_tree = (Object)adaptor.create(char_literal192);
            adaptor.addChild(root_0, char_literal192_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:13: ( annotationTypeElementDeclaration )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( ((LA97_0>=Identifier && LA97_0<=ENUM)||LA97_0==28||(LA97_0>=31 && LA97_0<=37)||LA97_0==40||(LA97_0>=46 && LA97_0<=47)||(LA97_0>=52 && LA97_0<=63)||LA97_0==73) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:14: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3808);
            	    annotationTypeElementDeclaration193=annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration193.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            char_literal194=(Token)match(input,45,FOLLOW_45_in_annotationTypeBody3812); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal194_tree = (Object)adaptor.create(char_literal194);
            adaptor.addChild(root_0, char_literal194_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 80, annotationTypeBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeBody"

    public static class annotationTypeElementDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeElementDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:783:1: annotationTypeElementDeclaration : modifiers annotationTypeElementRest ;
    public final JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        JavaParser.annotationTypeElementDeclaration_return retval = new JavaParser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeElementDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifiers_return modifiers195 = null;

        JavaParser.annotationTypeElementRest_return annotationTypeElementRest196 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:784:5: ( modifiers annotationTypeElementRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:784:9: modifiers annotationTypeElementRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationTypeElementDeclaration3835);
            modifiers195=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers195.getTree());
            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3837);
            annotationTypeElementRest196=annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementRest196.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 81, annotationTypeElementDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementDeclaration"

    public static class annotationTypeElementRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeElementRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:787:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final JavaParser.annotationTypeElementRest_return annotationTypeElementRest() throws RecognitionException {
        JavaParser.annotationTypeElementRest_return retval = new JavaParser.annotationTypeElementRest_return();
        retval.start = input.LT(1);
        int annotationTypeElementRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal199=null;
        Token char_literal201=null;
        Token char_literal203=null;
        Token char_literal205=null;
        Token char_literal207=null;
        JavaParser.type_return type197 = null;

        JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest198 = null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration200 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration202 = null;

        JavaParser.enumDeclaration_return enumDeclaration204 = null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration206 = null;


        Object char_literal199_tree=null;
        Object char_literal201_tree=null;
        Object char_literal203_tree=null;
        Object char_literal205_tree=null;
        Object char_literal207_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:5: ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
            int alt102=5;
            switch ( input.LA(1) ) {
            case Identifier:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                {
                alt102=1;
                }
                break;
            case 37:
                {
                alt102=2;
                }
                break;
            case 46:
                {
                alt102=3;
                }
                break;
            case ENUM:
                {
                alt102=4;
                }
                break;
            case 73:
                {
                alt102=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;
            }

            switch (alt102) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: type annotationMethodOrConstantRest ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_annotationTypeElementRest3860);
                    type197=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type197.getTree());
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3862);
                    annotationMethodOrConstantRest198=annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodOrConstantRest198.getTree());
                    char_literal199=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3864); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal199_tree = (Object)adaptor.create(char_literal199);
                    adaptor.addChild(root_0, char_literal199_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:789:9: normalClassDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3874);
                    normalClassDeclaration200=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration200.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:789:32: ( ';' )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==26) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal201=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3876); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal201_tree = (Object)adaptor.create(char_literal201);
                            adaptor.addChild(root_0, char_literal201_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:790:9: normalInterfaceDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3887);
                    normalInterfaceDeclaration202=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration202.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:790:36: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==26) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal203=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3889); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal203_tree = (Object)adaptor.create(char_literal203);
                            adaptor.addChild(root_0, char_literal203_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:791:9: enumDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest3900);
                    enumDeclaration204=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration204.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:791:25: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==26) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal205=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3902); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal205_tree = (Object)adaptor.create(char_literal205);
                            adaptor.addChild(root_0, char_literal205_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:9: annotationTypeDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3913);
                    annotationTypeDeclaration206=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration206.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:35: ( ';' )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==26) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal207=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3915); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal207_tree = (Object)adaptor.create(char_literal207);
                            adaptor.addChild(root_0, char_literal207_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 82, annotationTypeElementRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementRest"

    public static class annotationMethodOrConstantRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationMethodOrConstantRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:795:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest() throws RecognitionException {
        JavaParser.annotationMethodOrConstantRest_return retval = new JavaParser.annotationMethodOrConstantRest_return();
        retval.start = input.LT(1);
        int annotationMethodOrConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotationMethodRest_return annotationMethodRest208 = null;

        JavaParser.annotationConstantRest_return annotationConstantRest209 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:796:5: ( annotationMethodRest | annotationConstantRest )
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==Identifier) ) {
                int LA103_1 = input.LA(2);

                if ( (LA103_1==66) ) {
                    alt103=1;
                }
                else if ( (LA103_1==26||LA103_1==41||LA103_1==48||LA103_1==51) ) {
                    alt103=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 103, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 103, 0, input);

                throw nvae;
            }
            switch (alt103) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:796:9: annotationMethodRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3939);
                    annotationMethodRest208=annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodRest208.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:797:9: annotationConstantRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3949);
                    annotationConstantRest209=annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationConstantRest209.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 83, annotationMethodOrConstantRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationMethodOrConstantRest"

    public static class annotationMethodRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationMethodRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final JavaParser.annotationMethodRest_return annotationMethodRest() throws RecognitionException {
        JavaParser.annotationMethodRest_return retval = new JavaParser.annotationMethodRest_return();
        retval.start = input.LT(1);
        int annotationMethodRest_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier210=null;
        Token char_literal211=null;
        Token char_literal212=null;
        JavaParser.defaultValue_return defaultValue213 = null;


        Object Identifier210_tree=null;
        Object char_literal211_tree=null;
        Object char_literal212_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:801:5: ( Identifier '(' ')' ( defaultValue )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:801:9: Identifier '(' ')' ( defaultValue )?
            {
            root_0 = (Object)adaptor.nil();

            Identifier210=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest3972); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier210_tree = (Object)adaptor.create(Identifier210);
            adaptor.addChild(root_0, Identifier210_tree);
            }
            char_literal211=(Token)match(input,66,FOLLOW_66_in_annotationMethodRest3974); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal211_tree = (Object)adaptor.create(char_literal211);
            adaptor.addChild(root_0, char_literal211_tree);
            }
            char_literal212=(Token)match(input,67,FOLLOW_67_in_annotationMethodRest3976); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal212_tree = (Object)adaptor.create(char_literal212);
            adaptor.addChild(root_0, char_literal212_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:801:28: ( defaultValue )?
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==74) ) {
                alt104=1;
            }
            switch (alt104) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest3978);
                    defaultValue213=defaultValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, defaultValue213.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 84, annotationMethodRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationMethodRest"

    public static class annotationConstantRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationConstantRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:1: annotationConstantRest : variableDeclarators ;
    public final JavaParser.annotationConstantRest_return annotationConstantRest() throws RecognitionException {
        JavaParser.annotationConstantRest_return retval = new JavaParser.annotationConstantRest_return();
        retval.start = input.LT(1);
        int annotationConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableDeclarators_return variableDeclarators214 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:5: ( variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:9: variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest4002);
            variableDeclarators214=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators214.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 85, annotationConstantRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationConstantRest"

    public static class defaultValue_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "defaultValue"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:808:1: defaultValue : 'default' elementValue ;
    public final JavaParser.defaultValue_return defaultValue() throws RecognitionException {
        JavaParser.defaultValue_return retval = new JavaParser.defaultValue_return();
        retval.start = input.LT(1);
        int defaultValue_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal215=null;
        JavaParser.elementValue_return elementValue216 = null;


        Object string_literal215_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:809:5: ( 'default' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:809:9: 'default' elementValue
            {
            root_0 = (Object)adaptor.nil();

            string_literal215=(Token)match(input,74,FOLLOW_74_in_defaultValue4025); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal215_tree = (Object)adaptor.create(string_literal215);
            adaptor.addChild(root_0, string_literal215_tree);
            }
            pushFollow(FOLLOW_elementValue_in_defaultValue4027);
            elementValue216=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue216.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 86, defaultValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "defaultValue"

    public static class block_return extends ParserRuleReturnScope {
        public Block element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:814:1: block returns [Block element] : '{' ( blockStatement )* '}' ;
    public final JavaParser.block_return block() throws RecognitionException {
        JavaParser.block_return retval = new JavaParser.block_return();
        retval.start = input.LT(1);
        int block_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal217=null;
        Token char_literal219=null;
        JavaParser.blockStatement_return blockStatement218 = null;


        Object char_literal217_tree=null;
        Object char_literal219_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:815:5: ( '{' ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:815:9: '{' ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal217=(Token)match(input,44,FOLLOW_44_in_block4052); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal217_tree = (Object)adaptor.create(char_literal217);
            adaptor.addChild(root_0, char_literal217_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:815:13: ( blockStatement )*
            loop105:
            do {
                int alt105=2;
                int LA105_0 = input.LA(1);

                if ( ((LA105_0>=Identifier && LA105_0<=ASSERT)||LA105_0==26||LA105_0==28||(LA105_0>=31 && LA105_0<=37)||LA105_0==44||(LA105_0>=46 && LA105_0<=47)||LA105_0==53||(LA105_0>=56 && LA105_0<=63)||(LA105_0>=65 && LA105_0<=66)||(LA105_0>=69 && LA105_0<=73)||LA105_0==76||(LA105_0>=78 && LA105_0<=81)||(LA105_0>=83 && LA105_0<=87)||(LA105_0>=105 && LA105_0<=106)||(LA105_0>=109 && LA105_0<=113)) ) {
                    alt105=1;
                }


                switch (alt105) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block4054);
            	    blockStatement218=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement218.getTree());

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);

            char_literal219=(Token)match(input,45,FOLLOW_45_in_block4057); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal219_tree = (Object)adaptor.create(char_literal219);
            adaptor.addChild(root_0, char_literal219_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 87, block_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class blockStatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blockStatement"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:818:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final JavaParser.blockStatement_return blockStatement() throws RecognitionException {
        JavaParser.blockStatement_return retval = new JavaParser.blockStatement_return();
        retval.start = input.LT(1);
        int blockStatement_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement220 = null;

        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration221 = null;

        JavaParser.statement_return statement222 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:819:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt106=3;
            alt106 = dfa106.predict(input);
            switch (alt106) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:819:9: localVariableDeclarationStatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement4080);
                    localVariableDeclarationStatement220=localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement220.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:820:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement4090);
                    classOrInterfaceDeclaration221=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration221.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:9: statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statement_in_blockStatement4100);
                    statement222=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement222.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 88, blockStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "blockStatement"

    public static class localVariableDeclarationStatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "localVariableDeclarationStatement"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:824:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        JavaParser.localVariableDeclarationStatement_return retval = new JavaParser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);
        int localVariableDeclarationStatement_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal224=null;
        JavaParser.localVariableDeclaration_return localVariableDeclaration223 = null;


        Object char_literal224_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:5: ( localVariableDeclaration ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:10: localVariableDeclaration ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4124);
            localVariableDeclaration223=localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration223.getTree());
            char_literal224=(Token)match(input,26,FOLLOW_26_in_localVariableDeclarationStatement4126); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal224_tree = (Object)adaptor.create(char_literal224);
            adaptor.addChild(root_0, char_literal224_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 89, localVariableDeclarationStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "localVariableDeclarationStatement"

    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "localVariableDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:828:1: localVariableDeclaration : variableModifiers type variableDeclarators ;
    public final JavaParser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        JavaParser.localVariableDeclaration_return retval = new JavaParser.localVariableDeclaration_return();
        retval.start = input.LT(1);
        int localVariableDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers225 = null;

        JavaParser.type_return type226 = null;

        JavaParser.variableDeclarators_return variableDeclarators227 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:829:5: ( variableModifiers type variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:829:9: variableModifiers type variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4145);
            variableModifiers225=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers225.getTree());
            pushFollow(FOLLOW_type_in_localVariableDeclaration4147);
            type226=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type226.getTree());
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration4149);
            variableDeclarators227=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators227.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 90, localVariableDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "localVariableDeclaration"

    public static class variableModifiers_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableModifiers"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:832:1: variableModifiers : ( variableModifier )* ;
    public final JavaParser.variableModifiers_return variableModifiers() throws RecognitionException {
        JavaParser.variableModifiers_return retval = new JavaParser.variableModifiers_return();
        retval.start = input.LT(1);
        int variableModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifier_return variableModifier228 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:5: ( ( variableModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:9: ( variableModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:9: ( variableModifier )*
            loop107:
            do {
                int alt107=2;
                int LA107_0 = input.LA(1);

                if ( (LA107_0==35||LA107_0==73) ) {
                    alt107=1;
                }


                switch (alt107) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_variableModifiers4172);
            	    variableModifier228=variableModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifier228.getTree());

            	    }
            	    break;

            	default :
            	    break loop107;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 91, variableModifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifiers"

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statement"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:836:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );
    public final JavaParser.statement_return statement() throws RecognitionException {
        JavaParser.statement_return retval = new JavaParser.statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        Object root_0 = null;

        Token ASSERT230=null;
        Token char_literal232=null;
        Token char_literal234=null;
        Token string_literal235=null;
        Token string_literal238=null;
        Token string_literal240=null;
        Token char_literal241=null;
        Token char_literal243=null;
        Token string_literal245=null;
        Token string_literal248=null;
        Token string_literal250=null;
        Token char_literal252=null;
        Token string_literal253=null;
        Token string_literal256=null;
        Token string_literal259=null;
        Token string_literal261=null;
        Token char_literal263=null;
        Token char_literal265=null;
        Token string_literal266=null;
        Token string_literal269=null;
        Token char_literal271=null;
        Token string_literal272=null;
        Token char_literal274=null;
        Token string_literal275=null;
        Token Identifier276=null;
        Token char_literal277=null;
        Token string_literal278=null;
        Token Identifier279=null;
        Token char_literal280=null;
        Token char_literal281=null;
        Token char_literal283=null;
        Token Identifier284=null;
        Token char_literal285=null;
        JavaParser.block_return block229 = null;

        JavaParser.expression_return expression231 = null;

        JavaParser.expression_return expression233 = null;

        JavaParser.parExpression_return parExpression236 = null;

        JavaParser.statement_return statement237 = null;

        JavaParser.statement_return statement239 = null;

        JavaParser.forControl_return forControl242 = null;

        JavaParser.statement_return statement244 = null;

        JavaParser.parExpression_return parExpression246 = null;

        JavaParser.statement_return statement247 = null;

        JavaParser.statement_return statement249 = null;

        JavaParser.parExpression_return parExpression251 = null;

        JavaParser.block_return block254 = null;

        JavaParser.catches_return catches255 = null;

        JavaParser.block_return block257 = null;

        JavaParser.catches_return catches258 = null;

        JavaParser.block_return block260 = null;

        JavaParser.parExpression_return parExpression262 = null;

        JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups264 = null;

        JavaParser.parExpression_return parExpression267 = null;

        JavaParser.block_return block268 = null;

        JavaParser.expression_return expression270 = null;

        JavaParser.expression_return expression273 = null;

        JavaParser.statementExpression_return statementExpression282 = null;

        JavaParser.statement_return statement286 = null;


        Object ASSERT230_tree=null;
        Object char_literal232_tree=null;
        Object char_literal234_tree=null;
        Object string_literal235_tree=null;
        Object string_literal238_tree=null;
        Object string_literal240_tree=null;
        Object char_literal241_tree=null;
        Object char_literal243_tree=null;
        Object string_literal245_tree=null;
        Object string_literal248_tree=null;
        Object string_literal250_tree=null;
        Object char_literal252_tree=null;
        Object string_literal253_tree=null;
        Object string_literal256_tree=null;
        Object string_literal259_tree=null;
        Object string_literal261_tree=null;
        Object char_literal263_tree=null;
        Object char_literal265_tree=null;
        Object string_literal266_tree=null;
        Object string_literal269_tree=null;
        Object char_literal271_tree=null;
        Object string_literal272_tree=null;
        Object char_literal274_tree=null;
        Object string_literal275_tree=null;
        Object Identifier276_tree=null;
        Object char_literal277_tree=null;
        Object string_literal278_tree=null;
        Object Identifier279_tree=null;
        Object char_literal280_tree=null;
        Object char_literal281_tree=null;
        Object char_literal283_tree=null;
        Object Identifier284_tree=null;
        Object char_literal285_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:5: ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement )
            int alt114=16;
            alt114 = dfa114.predict(input);
            switch (alt114) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:7: block
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_block_in_statement4190);
                    block229=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block229.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:838:9: ASSERT expression ( ':' expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    ASSERT230=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement4200); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSERT230_tree = (Object)adaptor.create(ASSERT230);
                    adaptor.addChild(root_0, ASSERT230_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement4202);
                    expression231=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression231.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:838:27: ( ':' expression )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==75) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:838:28: ':' expression
                            {
                            char_literal232=(Token)match(input,75,FOLLOW_75_in_statement4205); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal232_tree = (Object)adaptor.create(char_literal232);
                            adaptor.addChild(root_0, char_literal232_tree);
                            }
                            pushFollow(FOLLOW_expression_in_statement4207);
                            expression233=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression233.getTree());

                            }
                            break;

                    }

                    char_literal234=(Token)match(input,26,FOLLOW_26_in_statement4211); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal234_tree = (Object)adaptor.create(char_literal234);
                    adaptor.addChild(root_0, char_literal234_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:9: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal235=(Token)match(input,76,FOLLOW_76_in_statement4221); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal235_tree = (Object)adaptor.create(string_literal235);
                    adaptor.addChild(root_0, string_literal235_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4223);
                    parExpression236=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression236.getTree());
                    pushFollow(FOLLOW_statement_in_statement4225);
                    statement237=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement237.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:38: ( options {k=1; } : 'else' statement )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==77) ) {
                        int LA109_1 = input.LA(2);

                        if ( (synpred152_Java()) ) {
                            alt109=1;
                        }
                    }
                    switch (alt109) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:54: 'else' statement
                            {
                            string_literal238=(Token)match(input,77,FOLLOW_77_in_statement4235); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal238_tree = (Object)adaptor.create(string_literal238);
                            adaptor.addChild(root_0, string_literal238_tree);
                            }
                            pushFollow(FOLLOW_statement_in_statement4237);
                            statement239=statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, statement239.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:840:9: 'for' '(' forControl ')' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal240=(Token)match(input,78,FOLLOW_78_in_statement4249); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal240_tree = (Object)adaptor.create(string_literal240);
                    adaptor.addChild(root_0, string_literal240_tree);
                    }
                    char_literal241=(Token)match(input,66,FOLLOW_66_in_statement4251); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal241_tree = (Object)adaptor.create(char_literal241);
                    adaptor.addChild(root_0, char_literal241_tree);
                    }
                    pushFollow(FOLLOW_forControl_in_statement4253);
                    forControl242=forControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, forControl242.getTree());
                    char_literal243=(Token)match(input,67,FOLLOW_67_in_statement4255); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal243_tree = (Object)adaptor.create(char_literal243);
                    adaptor.addChild(root_0, char_literal243_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4257);
                    statement244=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement244.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:9: 'while' parExpression statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal245=(Token)match(input,79,FOLLOW_79_in_statement4267); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal245_tree = (Object)adaptor.create(string_literal245);
                    adaptor.addChild(root_0, string_literal245_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4269);
                    parExpression246=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression246.getTree());
                    pushFollow(FOLLOW_statement_in_statement4271);
                    statement247=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement247.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:842:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal248=(Token)match(input,80,FOLLOW_80_in_statement4281); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal248_tree = (Object)adaptor.create(string_literal248);
                    adaptor.addChild(root_0, string_literal248_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4283);
                    statement249=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement249.getTree());
                    string_literal250=(Token)match(input,79,FOLLOW_79_in_statement4285); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal250_tree = (Object)adaptor.create(string_literal250);
                    adaptor.addChild(root_0, string_literal250_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4287);
                    parExpression251=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression251.getTree());
                    char_literal252=(Token)match(input,26,FOLLOW_26_in_statement4289); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal252_tree = (Object)adaptor.create(char_literal252);
                    adaptor.addChild(root_0, char_literal252_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:843:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal253=(Token)match(input,81,FOLLOW_81_in_statement4299); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal253_tree = (Object)adaptor.create(string_literal253);
                    adaptor.addChild(root_0, string_literal253_tree);
                    }
                    pushFollow(FOLLOW_block_in_statement4301);
                    block254=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block254.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:9: ( catches 'finally' block | catches | 'finally' block )
                    int alt110=3;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==88) ) {
                        int LA110_1 = input.LA(2);

                        if ( (synpred157_Java()) ) {
                            alt110=1;
                        }
                        else if ( (synpred158_Java()) ) {
                            alt110=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 110, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA110_0==82) ) {
                        alt110=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 110, 0, input);

                        throw nvae;
                    }
                    switch (alt110) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:11: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement4313);
                            catches255=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches255.getTree());
                            string_literal256=(Token)match(input,82,FOLLOW_82_in_statement4315); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal256_tree = (Object)adaptor.create(string_literal256);
                            adaptor.addChild(root_0, string_literal256_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement4317);
                            block257=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block257.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:11: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement4329);
                            catches258=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches258.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:846:13: 'finally' block
                            {
                            string_literal259=(Token)match(input,82,FOLLOW_82_in_statement4343); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal259_tree = (Object)adaptor.create(string_literal259);
                            adaptor.addChild(root_0, string_literal259_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement4345);
                            block260=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block260.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:848:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal261=(Token)match(input,83,FOLLOW_83_in_statement4365); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal261_tree = (Object)adaptor.create(string_literal261);
                    adaptor.addChild(root_0, string_literal261_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4367);
                    parExpression262=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression262.getTree());
                    char_literal263=(Token)match(input,44,FOLLOW_44_in_statement4369); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal263_tree = (Object)adaptor.create(char_literal263);
                    adaptor.addChild(root_0, char_literal263_tree);
                    }
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4371);
                    switchBlockStatementGroups264=switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups264.getTree());
                    char_literal265=(Token)match(input,45,FOLLOW_45_in_statement4373); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal265_tree = (Object)adaptor.create(char_literal265);
                    adaptor.addChild(root_0, char_literal265_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:9: 'synchronized' parExpression block
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal266=(Token)match(input,53,FOLLOW_53_in_statement4383); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal266_tree = (Object)adaptor.create(string_literal266);
                    adaptor.addChild(root_0, string_literal266_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4385);
                    parExpression267=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression267.getTree());
                    pushFollow(FOLLOW_block_in_statement4387);
                    block268=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block268.getTree());

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:850:9: 'return' ( expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal269=(Token)match(input,84,FOLLOW_84_in_statement4397); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal269_tree = (Object)adaptor.create(string_literal269);
                    adaptor.addChild(root_0, string_literal269_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:850:18: ( expression )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==Identifier||(LA111_0>=FloatingPointLiteral && LA111_0<=DecimalLiteral)||LA111_0==47||(LA111_0>=56 && LA111_0<=63)||(LA111_0>=65 && LA111_0<=66)||(LA111_0>=69 && LA111_0<=72)||(LA111_0>=105 && LA111_0<=106)||(LA111_0>=109 && LA111_0<=113)) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement4399);
                            expression270=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression270.getTree());

                            }
                            break;

                    }

                    char_literal271=(Token)match(input,26,FOLLOW_26_in_statement4402); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal271_tree = (Object)adaptor.create(char_literal271);
                    adaptor.addChild(root_0, char_literal271_tree);
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:851:9: 'throw' expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal272=(Token)match(input,85,FOLLOW_85_in_statement4412); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal272_tree = (Object)adaptor.create(string_literal272);
                    adaptor.addChild(root_0, string_literal272_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement4414);
                    expression273=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression273.getTree());
                    char_literal274=(Token)match(input,26,FOLLOW_26_in_statement4416); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal274_tree = (Object)adaptor.create(char_literal274);
                    adaptor.addChild(root_0, char_literal274_tree);
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:852:9: 'break' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal275=(Token)match(input,86,FOLLOW_86_in_statement4426); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal275_tree = (Object)adaptor.create(string_literal275);
                    adaptor.addChild(root_0, string_literal275_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:852:17: ( Identifier )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier276=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement4428); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier276_tree = (Object)adaptor.create(Identifier276);
                            adaptor.addChild(root_0, Identifier276_tree);
                            }

                            }
                            break;

                    }

                    char_literal277=(Token)match(input,26,FOLLOW_26_in_statement4431); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal277_tree = (Object)adaptor.create(char_literal277);
                    adaptor.addChild(root_0, char_literal277_tree);
                    }

                    }
                    break;
                case 13 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:9: 'continue' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal278=(Token)match(input,87,FOLLOW_87_in_statement4441); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal278_tree = (Object)adaptor.create(string_literal278);
                    adaptor.addChild(root_0, string_literal278_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:20: ( Identifier )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==Identifier) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier279=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement4443); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier279_tree = (Object)adaptor.create(Identifier279);
                            adaptor.addChild(root_0, Identifier279_tree);
                            }

                            }
                            break;

                    }

                    char_literal280=(Token)match(input,26,FOLLOW_26_in_statement4446); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal280_tree = (Object)adaptor.create(char_literal280);
                    adaptor.addChild(root_0, char_literal280_tree);
                    }

                    }
                    break;
                case 14 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:854:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal281=(Token)match(input,26,FOLLOW_26_in_statement4456); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal281_tree = (Object)adaptor.create(char_literal281);
                    adaptor.addChild(root_0, char_literal281_tree);
                    }

                    }
                    break;
                case 15 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:9: statementExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statementExpression_in_statement4467);
                    statementExpression282=statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementExpression282.getTree());
                    char_literal283=(Token)match(input,26,FOLLOW_26_in_statement4469); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal283_tree = (Object)adaptor.create(char_literal283);
                    adaptor.addChild(root_0, char_literal283_tree);
                    }

                    }
                    break;
                case 16 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:856:9: Identifier ':' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier284=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement4479); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier284_tree = (Object)adaptor.create(Identifier284);
                    adaptor.addChild(root_0, Identifier284_tree);
                    }
                    char_literal285=(Token)match(input,75,FOLLOW_75_in_statement4481); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal285_tree = (Object)adaptor.create(char_literal285);
                    adaptor.addChild(root_0, char_literal285_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4483);
                    statement286=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement286.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 92, statement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statement"

    public static class catches_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "catches"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:859:1: catches : catchClause ( catchClause )* ;
    public final JavaParser.catches_return catches() throws RecognitionException {
        JavaParser.catches_return retval = new JavaParser.catches_return();
        retval.start = input.LT(1);
        int catches_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.catchClause_return catchClause287 = null;

        JavaParser.catchClause_return catchClause288 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:5: ( catchClause ( catchClause )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:9: catchClause ( catchClause )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_catchClause_in_catches4506);
            catchClause287=catchClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause287.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:21: ( catchClause )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==88) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:22: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches4509);
            	    catchClause288=catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause288.getTree());

            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 93, catches_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "catches"

    public static class catchClause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "catchClause"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:863:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final JavaParser.catchClause_return catchClause() throws RecognitionException {
        JavaParser.catchClause_return retval = new JavaParser.catchClause_return();
        retval.start = input.LT(1);
        int catchClause_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal289=null;
        Token char_literal290=null;
        Token char_literal292=null;
        JavaParser.formalParameter_return formalParameter291 = null;

        JavaParser.block_return block293 = null;


        Object string_literal289_tree=null;
        Object char_literal290_tree=null;
        Object char_literal292_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:5: ( 'catch' '(' formalParameter ')' block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (Object)adaptor.nil();

            string_literal289=(Token)match(input,88,FOLLOW_88_in_catchClause4534); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal289_tree = (Object)adaptor.create(string_literal289);
            adaptor.addChild(root_0, string_literal289_tree);
            }
            char_literal290=(Token)match(input,66,FOLLOW_66_in_catchClause4536); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal290_tree = (Object)adaptor.create(char_literal290);
            adaptor.addChild(root_0, char_literal290_tree);
            }
            pushFollow(FOLLOW_formalParameter_in_catchClause4538);
            formalParameter291=formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameter291.getTree());
            char_literal292=(Token)match(input,67,FOLLOW_67_in_catchClause4540); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal292_tree = (Object)adaptor.create(char_literal292);
            adaptor.addChild(root_0, char_literal292_tree);
            }
            pushFollow(FOLLOW_block_in_catchClause4542);
            block293=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block293.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 94, catchClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "catchClause"

    public static class formalParameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameter"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:867:1: formalParameter : variableModifiers type variableDeclaratorId ;
    public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
        JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers294 = null;

        JavaParser.type_return type295 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId296 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:5: ( variableModifiers type variableDeclaratorId )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:9: variableModifiers type variableDeclaratorId
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameter4561);
            variableModifiers294=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers294.getTree());
            pushFollow(FOLLOW_type_in_formalParameter4563);
            type295=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type295.getTree());
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter4565);
            variableDeclaratorId296=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId296.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 95, formalParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameter"

    public static class switchBlockStatementGroups_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "switchBlockStatementGroups"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:871:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        JavaParser.switchBlockStatementGroups_return retval = new JavaParser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroups_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup297 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:5: ( ( switchBlockStatementGroup )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:9: ( switchBlockStatementGroup )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:9: ( switchBlockStatementGroup )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==74||LA116_0==89) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4593);
            	    switchBlockStatementGroup297=switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup297.getTree());

            	    }
            	    break;

            	default :
            	    break loop116;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 96, switchBlockStatementGroups_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroups"

    public static class switchBlockStatementGroup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "switchBlockStatementGroup"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:1: switchBlockStatementGroup : ( switchLabel )+ ( blockStatement )* ;
    public final JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        JavaParser.switchBlockStatementGroup_return retval = new JavaParser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroup_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchLabel_return switchLabel298 = null;

        JavaParser.blockStatement_return blockStatement299 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:5: ( ( switchLabel )+ ( blockStatement )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:9: ( switchLabel )+ ( blockStatement )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:9: ( switchLabel )+
            int cnt117=0;
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==89) ) {
                    int LA117_2 = input.LA(2);

                    if ( (synpred173_Java()) ) {
                        alt117=1;
                    }


                }
                else if ( (LA117_0==74) ) {
                    int LA117_3 = input.LA(2);

                    if ( (synpred173_Java()) ) {
                        alt117=1;
                    }


                }


                switch (alt117) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: switchLabel
            	    {
            	    pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4620);
            	    switchLabel298=switchLabel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchLabel298.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt117 >= 1 ) break loop117;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(117, input);
                        throw eee;
                }
                cnt117++;
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:22: ( blockStatement )*
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( ((LA118_0>=Identifier && LA118_0<=ASSERT)||LA118_0==26||LA118_0==28||(LA118_0>=31 && LA118_0<=37)||LA118_0==44||(LA118_0>=46 && LA118_0<=47)||LA118_0==53||(LA118_0>=56 && LA118_0<=63)||(LA118_0>=65 && LA118_0<=66)||(LA118_0>=69 && LA118_0<=73)||LA118_0==76||(LA118_0>=78 && LA118_0<=81)||(LA118_0>=83 && LA118_0<=87)||(LA118_0>=105 && LA118_0<=106)||(LA118_0>=109 && LA118_0<=113)) ) {
                    alt118=1;
                }


                switch (alt118) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4623);
            	    blockStatement299=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement299.getTree());

            	    }
            	    break;

            	default :
            	    break loop118;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 97, switchBlockStatementGroup_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroup"

    public static class switchLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "switchLabel"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final JavaParser.switchLabel_return switchLabel() throws RecognitionException {
        JavaParser.switchLabel_return retval = new JavaParser.switchLabel_return();
        retval.start = input.LT(1);
        int switchLabel_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal300=null;
        Token char_literal302=null;
        Token string_literal303=null;
        Token char_literal305=null;
        Token string_literal306=null;
        Token char_literal307=null;
        JavaParser.constantExpression_return constantExpression301 = null;

        JavaParser.enumConstantName_return enumConstantName304 = null;


        Object string_literal300_tree=null;
        Object char_literal302_tree=null;
        Object string_literal303_tree=null;
        Object char_literal305_tree=null;
        Object string_literal306_tree=null;
        Object char_literal307_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
            int alt119=3;
            int LA119_0 = input.LA(1);

            if ( (LA119_0==89) ) {
                int LA119_1 = input.LA(2);

                if ( ((LA119_1>=FloatingPointLiteral && LA119_1<=DecimalLiteral)||LA119_1==47||(LA119_1>=56 && LA119_1<=63)||(LA119_1>=65 && LA119_1<=66)||(LA119_1>=69 && LA119_1<=72)||(LA119_1>=105 && LA119_1<=106)||(LA119_1>=109 && LA119_1<=113)) ) {
                    alt119=1;
                }
                else if ( (LA119_1==Identifier) ) {
                    int LA119_4 = input.LA(3);

                    if ( ((LA119_4>=29 && LA119_4<=30)||LA119_4==40||(LA119_4>=42 && LA119_4<=43)||LA119_4==48||LA119_4==51||LA119_4==64||LA119_4==66||(LA119_4>=90 && LA119_4<=110)) ) {
                        alt119=1;
                    }
                    else if ( (LA119_4==75) ) {
                        int LA119_5 = input.LA(4);

                        if ( (synpred175_Java()) ) {
                            alt119=1;
                        }
                        else if ( (synpred176_Java()) ) {
                            alt119=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 119, 5, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 119, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 119, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA119_0==74) ) {
                alt119=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 119, 0, input);

                throw nvae;
            }
            switch (alt119) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:9: 'case' constantExpression ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal300=(Token)match(input,89,FOLLOW_89_in_switchLabel4647); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal300_tree = (Object)adaptor.create(string_literal300);
                    adaptor.addChild(root_0, string_literal300_tree);
                    }
                    pushFollow(FOLLOW_constantExpression_in_switchLabel4649);
                    constantExpression301=constantExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantExpression301.getTree());
                    char_literal302=(Token)match(input,75,FOLLOW_75_in_switchLabel4651); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal302_tree = (Object)adaptor.create(char_literal302);
                    adaptor.addChild(root_0, char_literal302_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:885:9: 'case' enumConstantName ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal303=(Token)match(input,89,FOLLOW_89_in_switchLabel4661); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal303_tree = (Object)adaptor.create(string_literal303);
                    adaptor.addChild(root_0, string_literal303_tree);
                    }
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel4663);
                    enumConstantName304=enumConstantName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstantName304.getTree());
                    char_literal305=(Token)match(input,75,FOLLOW_75_in_switchLabel4665); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal305_tree = (Object)adaptor.create(char_literal305);
                    adaptor.addChild(root_0, char_literal305_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:886:9: 'default' ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal306=(Token)match(input,74,FOLLOW_74_in_switchLabel4675); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal306_tree = (Object)adaptor.create(string_literal306);
                    adaptor.addChild(root_0, string_literal306_tree);
                    }
                    char_literal307=(Token)match(input,75,FOLLOW_75_in_switchLabel4677); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal307_tree = (Object)adaptor.create(char_literal307);
                    adaptor.addChild(root_0, char_literal307_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 98, switchLabel_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchLabel"

    public static class forControl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forControl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:889:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final JavaParser.forControl_return forControl() throws RecognitionException {
        JavaParser.forControl_return retval = new JavaParser.forControl_return();
        retval.start = input.LT(1);
        int forControl_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal310=null;
        Token char_literal312=null;
        JavaParser.enhancedForControl_return enhancedForControl308 = null;

        JavaParser.forInit_return forInit309 = null;

        JavaParser.expression_return expression311 = null;

        JavaParser.forUpdate_return forUpdate313 = null;


        Object char_literal310_tree=null;
        Object char_literal312_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:891:5: ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt123=2;
            alt123 = dfa123.predict(input);
            switch (alt123) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:891:9: enhancedForControl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enhancedForControl_in_forControl4708);
                    enhancedForControl308=enhancedForControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enhancedForControl308.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:9: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:9: ( forInit )?
                    int alt120=2;
                    int LA120_0 = input.LA(1);

                    if ( (LA120_0==Identifier||(LA120_0>=FloatingPointLiteral && LA120_0<=DecimalLiteral)||LA120_0==35||LA120_0==47||(LA120_0>=56 && LA120_0<=63)||(LA120_0>=65 && LA120_0<=66)||(LA120_0>=69 && LA120_0<=73)||(LA120_0>=105 && LA120_0<=106)||(LA120_0>=109 && LA120_0<=113)) ) {
                        alt120=1;
                    }
                    switch (alt120) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl4718);
                            forInit309=forInit();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forInit309.getTree());

                            }
                            break;

                    }

                    char_literal310=(Token)match(input,26,FOLLOW_26_in_forControl4721); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal310_tree = (Object)adaptor.create(char_literal310);
                    adaptor.addChild(root_0, char_literal310_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:22: ( expression )?
                    int alt121=2;
                    int LA121_0 = input.LA(1);

                    if ( (LA121_0==Identifier||(LA121_0>=FloatingPointLiteral && LA121_0<=DecimalLiteral)||LA121_0==47||(LA121_0>=56 && LA121_0<=63)||(LA121_0>=65 && LA121_0<=66)||(LA121_0>=69 && LA121_0<=72)||(LA121_0>=105 && LA121_0<=106)||(LA121_0>=109 && LA121_0<=113)) ) {
                        alt121=1;
                    }
                    switch (alt121) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl4723);
                            expression311=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression311.getTree());

                            }
                            break;

                    }

                    char_literal312=(Token)match(input,26,FOLLOW_26_in_forControl4726); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal312_tree = (Object)adaptor.create(char_literal312);
                    adaptor.addChild(root_0, char_literal312_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:38: ( forUpdate )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==47||(LA122_0>=56 && LA122_0<=63)||(LA122_0>=65 && LA122_0<=66)||(LA122_0>=69 && LA122_0<=72)||(LA122_0>=105 && LA122_0<=106)||(LA122_0>=109 && LA122_0<=113)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl4728);
                            forUpdate313=forUpdate();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forUpdate313.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 99, forControl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forControl"

    public static class forInit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forInit"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:895:1: forInit : ( localVariableDeclaration | expressionList );
    public final JavaParser.forInit_return forInit() throws RecognitionException {
        JavaParser.forInit_return retval = new JavaParser.forInit_return();
        retval.start = input.LT(1);
        int forInit_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclaration_return localVariableDeclaration314 = null;

        JavaParser.expressionList_return expressionList315 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:5: ( localVariableDeclaration | expressionList )
            int alt124=2;
            alt124 = dfa124.predict(input);
            switch (alt124) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: localVariableDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit4748);
                    localVariableDeclaration314=localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration314.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:897:9: expressionList
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expressionList_in_forInit4758);
                    expressionList315=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList315.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 100, forInit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forInit"

    public static class enhancedForControl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enhancedForControl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:1: enhancedForControl : variableModifiers type Identifier ':' expression ;
    public final JavaParser.enhancedForControl_return enhancedForControl() throws RecognitionException {
        JavaParser.enhancedForControl_return retval = new JavaParser.enhancedForControl_return();
        retval.start = input.LT(1);
        int enhancedForControl_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier318=null;
        Token char_literal319=null;
        JavaParser.variableModifiers_return variableModifiers316 = null;

        JavaParser.type_return type317 = null;

        JavaParser.expression_return expression320 = null;


        Object Identifier318_tree=null;
        Object char_literal319_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:901:5: ( variableModifiers type Identifier ':' expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:901:9: variableModifiers type Identifier ':' expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_enhancedForControl4781);
            variableModifiers316=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers316.getTree());
            pushFollow(FOLLOW_type_in_enhancedForControl4783);
            type317=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type317.getTree());
            Identifier318=(Token)match(input,Identifier,FOLLOW_Identifier_in_enhancedForControl4785); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier318_tree = (Object)adaptor.create(Identifier318);
            adaptor.addChild(root_0, Identifier318_tree);
            }
            char_literal319=(Token)match(input,75,FOLLOW_75_in_enhancedForControl4787); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal319_tree = (Object)adaptor.create(char_literal319);
            adaptor.addChild(root_0, char_literal319_tree);
            }
            pushFollow(FOLLOW_expression_in_enhancedForControl4789);
            expression320=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression320.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 101, enhancedForControl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enhancedForControl"

    public static class forUpdate_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forUpdate"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:904:1: forUpdate : expressionList ;
    public final JavaParser.forUpdate_return forUpdate() throws RecognitionException {
        JavaParser.forUpdate_return retval = new JavaParser.forUpdate_return();
        retval.start = input.LT(1);
        int forUpdate_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expressionList_return expressionList321 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:905:5: ( expressionList )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:905:9: expressionList
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expressionList_in_forUpdate4808);
            expressionList321=expressionList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList321.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 102, forUpdate_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forUpdate"

    public static class parExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "parExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:910:1: parExpression : '(' expression ')' ;
    public final JavaParser.parExpression_return parExpression() throws RecognitionException {
        JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal322=null;
        Token char_literal324=null;
        JavaParser.expression_return expression323 = null;


        Object char_literal322_tree=null;
        Object char_literal324_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:911:5: ( '(' expression ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:911:9: '(' expression ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal322=(Token)match(input,66,FOLLOW_66_in_parExpression4829); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal322_tree = (Object)adaptor.create(char_literal322);
            adaptor.addChild(root_0, char_literal322_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression4831);
            expression323=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression323.getTree());
            char_literal324=(Token)match(input,67,FOLLOW_67_in_parExpression4833); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal324_tree = (Object)adaptor.create(char_literal324);
            adaptor.addChild(root_0, char_literal324_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 103, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "parExpression"

    public static class expressionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expressionList"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:914:1: expressionList : expression ( ',' expression )* ;
    public final JavaParser.expressionList_return expressionList() throws RecognitionException {
        JavaParser.expressionList_return retval = new JavaParser.expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal326=null;
        JavaParser.expression_return expression325 = null;

        JavaParser.expression_return expression327 = null;


        Object char_literal326_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:5: ( expression ( ',' expression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:9: expression ( ',' expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList4856);
            expression325=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression325.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:20: ( ',' expression )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( (LA125_0==41) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:21: ',' expression
            	    {
            	    char_literal326=(Token)match(input,41,FOLLOW_41_in_expressionList4859); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal326_tree = (Object)adaptor.create(char_literal326);
            	    adaptor.addChild(root_0, char_literal326_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList4861);
            	    expression327=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression327.getTree());

            	    }
            	    break;

            	default :
            	    break loop125;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 104, expressionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    public static class statementExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statementExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:918:1: statementExpression : expression ;
    public final JavaParser.statementExpression_return statementExpression() throws RecognitionException {
        JavaParser.statementExpression_return retval = new JavaParser.statementExpression_return();
        retval.start = input.LT(1);
        int statementExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression328 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_statementExpression4882);
            expression328=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression328.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 105, statementExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statementExpression"

    public static class constantExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constantExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:922:1: constantExpression : expression ;
    public final JavaParser.constantExpression_return constantExpression() throws RecognitionException {
        JavaParser.constantExpression_return retval = new JavaParser.constantExpression_return();
        retval.start = input.LT(1);
        int constantExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression329 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:923:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:923:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_constantExpression4905);
            expression329=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression329.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 106, constantExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantExpression"

    public static class expression_return extends ParserRuleReturnScope {
        public Expression element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:926:1: expression returns [Expression element] : conditionalExpression ( assignmentOperator expression )? ;
    public final JavaParser.expression_return expression() throws RecognitionException {
        JavaParser.expression_return retval = new JavaParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression330 = null;

        JavaParser.assignmentOperator_return assignmentOperator331 = null;

        JavaParser.expression_return expression332 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:5: ( conditionalExpression ( assignmentOperator expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:9: conditionalExpression ( assignmentOperator expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression4932);
            conditionalExpression330=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression330.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:31: ( assignmentOperator expression )?
            int alt126=2;
            alt126 = dfa126.predict(input);
            switch (alt126) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:32: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression4935);
                    assignmentOperator331=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentOperator331.getTree());
                    pushFollow(FOLLOW_expression_in_expression4937);
                    expression332=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression332.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 107, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class assignmentOperator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignmentOperator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:930:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);
    public final JavaParser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        JavaParser.assignmentOperator_return retval = new JavaParser.assignmentOperator_return();
        retval.start = input.LT(1);
        int assignmentOperator_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token char_literal333=null;
        Token string_literal334=null;
        Token string_literal335=null;
        Token string_literal336=null;
        Token string_literal337=null;
        Token string_literal338=null;
        Token string_literal339=null;
        Token string_literal340=null;
        Token string_literal341=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object t3_tree=null;
        Object t4_tree=null;
        Object char_literal333_tree=null;
        Object string_literal334_tree=null;
        Object string_literal335_tree=null;
        Object string_literal336_tree=null;
        Object string_literal337_tree=null;
        Object string_literal338_tree=null;
        Object string_literal339_tree=null;
        Object string_literal340_tree=null;
        Object string_literal341_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:931:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?)
            int alt127=12;
            alt127 = dfa127.predict(input);
            switch (alt127) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:931:9: '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal333=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4962); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal333_tree = (Object)adaptor.create(char_literal333);
                    adaptor.addChild(root_0, char_literal333_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:932:9: '+='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal334=(Token)match(input,90,FOLLOW_90_in_assignmentOperator4972); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal334_tree = (Object)adaptor.create(string_literal334);
                    adaptor.addChild(root_0, string_literal334_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:933:9: '-='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal335=(Token)match(input,91,FOLLOW_91_in_assignmentOperator4982); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal335_tree = (Object)adaptor.create(string_literal335);
                    adaptor.addChild(root_0, string_literal335_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:9: '*='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal336=(Token)match(input,92,FOLLOW_92_in_assignmentOperator4992); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal336_tree = (Object)adaptor.create(string_literal336);
                    adaptor.addChild(root_0, string_literal336_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:935:9: '/='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal337=(Token)match(input,93,FOLLOW_93_in_assignmentOperator5002); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal337_tree = (Object)adaptor.create(string_literal337);
                    adaptor.addChild(root_0, string_literal337_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:936:9: '&='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal338=(Token)match(input,94,FOLLOW_94_in_assignmentOperator5012); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal338_tree = (Object)adaptor.create(string_literal338);
                    adaptor.addChild(root_0, string_literal338_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:937:9: '|='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal339=(Token)match(input,95,FOLLOW_95_in_assignmentOperator5022); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal339_tree = (Object)adaptor.create(string_literal339);
                    adaptor.addChild(root_0, string_literal339_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:9: '^='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal340=(Token)match(input,96,FOLLOW_96_in_assignmentOperator5032); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal340_tree = (Object)adaptor.create(string_literal340);
                    adaptor.addChild(root_0, string_literal340_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:939:9: '%='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal341=(Token)match(input,97,FOLLOW_97_in_assignmentOperator5042); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal341_tree = (Object)adaptor.create(string_literal341);
                    adaptor.addChild(root_0, string_literal341_tree);
                    }

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:940:9: ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_assignmentOperator5063); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_assignmentOperator5067); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5071); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() &&
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() && 
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() &&\n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && \n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:9: ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5105); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5109); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5113); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    t4=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5117); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t4_tree = (Object)adaptor.create(t4);
                    adaptor.addChild(root_0, t4_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() &&
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() &&
                              t3.getLine() == t4.getLine() && 
                              t3.getCharPositionInLine() + 1 == t4.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&\n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&\n          $t3.getLine() == $t4.getLine() && \n          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:9: ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5148); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5152); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5156); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() && 
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && \n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 108, assignmentOperator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:959:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final JavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JavaParser.conditionalExpression_return retval = new JavaParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal343=null;
        Token char_literal345=null;
        JavaParser.conditionalOrExpression_return conditionalOrExpression342 = null;

        JavaParser.expression_return expression344 = null;

        JavaParser.expression_return expression346 = null;


        Object char_literal343_tree=null;
        Object char_literal345_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:960:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:960:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5185);
            conditionalOrExpression342=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression342.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:960:33: ( '?' expression ':' expression )?
            int alt128=2;
            int LA128_0 = input.LA(1);

            if ( (LA128_0==64) ) {
                alt128=1;
            }
            switch (alt128) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:960:35: '?' expression ':' expression
                    {
                    char_literal343=(Token)match(input,64,FOLLOW_64_in_conditionalExpression5189); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal343_tree = (Object)adaptor.create(char_literal343);
                    adaptor.addChild(root_0, char_literal343_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression5191);
                    expression344=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression344.getTree());
                    char_literal345=(Token)match(input,75,FOLLOW_75_in_conditionalExpression5193); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal345_tree = (Object)adaptor.create(char_literal345);
                    adaptor.addChild(root_0, char_literal345_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression5195);
                    expression346=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression346.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 109, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final JavaParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JavaParser.conditionalOrExpression_return retval = new JavaParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal348=null;
        JavaParser.conditionalAndExpression_return conditionalAndExpression347 = null;

        JavaParser.conditionalAndExpression_return conditionalAndExpression349 = null;


        Object string_literal348_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:964:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:964:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5217);
            conditionalAndExpression347=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression347.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:964:34: ( '||' conditionalAndExpression )*
            loop129:
            do {
                int alt129=2;
                int LA129_0 = input.LA(1);

                if ( (LA129_0==98) ) {
                    alt129=1;
                }


                switch (alt129) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:964:36: '||' conditionalAndExpression
            	    {
            	    string_literal348=(Token)match(input,98,FOLLOW_98_in_conditionalOrExpression5221); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal348_tree = (Object)adaptor.create(string_literal348);
            	    adaptor.addChild(root_0, string_literal348_tree);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5223);
            	    conditionalAndExpression349=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression349.getTree());

            	    }
            	    break;

            	default :
            	    break loop129;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 110, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final JavaParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JavaParser.conditionalAndExpression_return retval = new JavaParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal351=null;
        JavaParser.inclusiveOrExpression_return inclusiveOrExpression350 = null;

        JavaParser.inclusiveOrExpression_return inclusiveOrExpression352 = null;


        Object string_literal351_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5245);
            inclusiveOrExpression350=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression350.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:31: ( '&&' inclusiveOrExpression )*
            loop130:
            do {
                int alt130=2;
                int LA130_0 = input.LA(1);

                if ( (LA130_0==99) ) {
                    alt130=1;
                }


                switch (alt130) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:33: '&&' inclusiveOrExpression
            	    {
            	    string_literal351=(Token)match(input,99,FOLLOW_99_in_conditionalAndExpression5249); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal351_tree = (Object)adaptor.create(string_literal351);
            	    adaptor.addChild(root_0, string_literal351_tree);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5251);
            	    inclusiveOrExpression352=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression352.getTree());

            	    }
            	    break;

            	default :
            	    break loop130;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 111, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inclusiveOrExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:971:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final JavaParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JavaParser.inclusiveOrExpression_return retval = new JavaParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal354=null;
        JavaParser.exclusiveOrExpression_return exclusiveOrExpression353 = null;

        JavaParser.exclusiveOrExpression_return exclusiveOrExpression355 = null;


        Object char_literal354_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:972:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:972:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5273);
            exclusiveOrExpression353=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression353.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:972:31: ( '|' exclusiveOrExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==100) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:972:33: '|' exclusiveOrExpression
            	    {
            	    char_literal354=(Token)match(input,100,FOLLOW_100_in_inclusiveOrExpression5277); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal354_tree = (Object)adaptor.create(char_literal354);
            	    adaptor.addChild(root_0, char_literal354_tree);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5279);
            	    exclusiveOrExpression355=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression355.getTree());

            	    }
            	    break;

            	default :
            	    break loop131;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 112, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exclusiveOrExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:975:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final JavaParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JavaParser.exclusiveOrExpression_return retval = new JavaParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal357=null;
        JavaParser.andExpression_return andExpression356 = null;

        JavaParser.andExpression_return andExpression358 = null;


        Object char_literal357_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:5: ( andExpression ( '^' andExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5301);
            andExpression356=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression356.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:23: ( '^' andExpression )*
            loop132:
            do {
                int alt132=2;
                int LA132_0 = input.LA(1);

                if ( (LA132_0==101) ) {
                    alt132=1;
                }


                switch (alt132) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:25: '^' andExpression
            	    {
            	    char_literal357=(Token)match(input,101,FOLLOW_101_in_exclusiveOrExpression5305); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal357_tree = (Object)adaptor.create(char_literal357);
            	    adaptor.addChild(root_0, char_literal357_tree);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5307);
            	    andExpression358=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression358.getTree());

            	    }
            	    break;

            	default :
            	    break loop132;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 113, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:979:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final JavaParser.andExpression_return andExpression() throws RecognitionException {
        JavaParser.andExpression_return retval = new JavaParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal360=null;
        JavaParser.equalityExpression_return equalityExpression359 = null;

        JavaParser.equalityExpression_return equalityExpression361 = null;


        Object char_literal360_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression5329);
            equalityExpression359=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression359.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:28: ( '&' equalityExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==43) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:30: '&' equalityExpression
            	    {
            	    char_literal360=(Token)match(input,43,FOLLOW_43_in_andExpression5333); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal360_tree = (Object)adaptor.create(char_literal360);
            	    adaptor.addChild(root_0, char_literal360_tree);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5335);
            	    equalityExpression361=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression361.getTree());

            	    }
            	    break;

            	default :
            	    break loop133;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 114, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:983:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final JavaParser.equalityExpression_return equalityExpression() throws RecognitionException {
        JavaParser.equalityExpression_return retval = new JavaParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set363=null;
        JavaParser.instanceOfExpression_return instanceOfExpression362 = null;

        JavaParser.instanceOfExpression_return instanceOfExpression364 = null;


        Object set363_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5357);
            instanceOfExpression362=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression362.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( ((LA134_0>=102 && LA134_0<=103)) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set363=(Token)input.LT(1);
            	    if ( (input.LA(1)>=102 && input.LA(1)<=103) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set363));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5369);
            	    instanceOfExpression364=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression364.getTree());

            	    }
            	    break;

            	default :
            	    break loop134;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 115, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "instanceOfExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:987:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final JavaParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JavaParser.instanceOfExpression_return retval = new JavaParser.instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal366=null;
        JavaParser.relationalExpression_return relationalExpression365 = null;

        JavaParser.type_return type367 = null;


        Object string_literal366_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:5: ( relationalExpression ( 'instanceof' type )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5391);
            relationalExpression365=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression365.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:30: ( 'instanceof' type )?
            int alt135=2;
            int LA135_0 = input.LA(1);

            if ( (LA135_0==104) ) {
                alt135=1;
            }
            switch (alt135) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:31: 'instanceof' type
                    {
                    string_literal366=(Token)match(input,104,FOLLOW_104_in_instanceOfExpression5394); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal366_tree = (Object)adaptor.create(string_literal366);
                    adaptor.addChild(root_0, string_literal366_tree);
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression5396);
                    type367=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type367.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 116, instanceOfExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:991:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final JavaParser.relationalExpression_return relationalExpression() throws RecognitionException {
        JavaParser.relationalExpression_return retval = new JavaParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.shiftExpression_return shiftExpression368 = null;

        JavaParser.relationalOp_return relationalOp369 = null;

        JavaParser.shiftExpression_return shiftExpression370 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression5417);
            shiftExpression368=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression368.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:25: ( relationalOp shiftExpression )*
            loop136:
            do {
                int alt136=2;
                int LA136_0 = input.LA(1);

                if ( (LA136_0==40) ) {
                    int LA136_2 = input.LA(2);

                    if ( (LA136_2==Identifier||(LA136_2>=FloatingPointLiteral && LA136_2<=DecimalLiteral)||LA136_2==47||LA136_2==51||(LA136_2>=56 && LA136_2<=63)||(LA136_2>=65 && LA136_2<=66)||(LA136_2>=69 && LA136_2<=72)||(LA136_2>=105 && LA136_2<=106)||(LA136_2>=109 && LA136_2<=113)) ) {
                        alt136=1;
                    }


                }
                else if ( (LA136_0==42) ) {
                    int LA136_3 = input.LA(2);

                    if ( (LA136_3==Identifier||(LA136_3>=FloatingPointLiteral && LA136_3<=DecimalLiteral)||LA136_3==47||LA136_3==51||(LA136_3>=56 && LA136_3<=63)||(LA136_3>=65 && LA136_3<=66)||(LA136_3>=69 && LA136_3<=72)||(LA136_3>=105 && LA136_3<=106)||(LA136_3>=109 && LA136_3<=113)) ) {
                        alt136=1;
                    }


                }


                switch (alt136) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression5421);
            	    relationalOp369=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp369.getTree());
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression5423);
            	    shiftExpression370=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression370.getTree());

            	    }
            	    break;

            	default :
            	    break loop136;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 117, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class relationalOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalOp"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:995:1: relationalOp : ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' );
    public final JavaParser.relationalOp_return relationalOp() throws RecognitionException {
        JavaParser.relationalOp_return retval = new JavaParser.relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token char_literal371=null;
        Token char_literal372=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object char_literal371_tree=null;
        Object char_literal372_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:5: ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' )
            int alt137=4;
            int LA137_0 = input.LA(1);

            if ( (LA137_0==40) ) {
                int LA137_1 = input.LA(2);

                if ( (LA137_1==51) && (synpred206_Java())) {
                    alt137=1;
                }
                else if ( (LA137_1==Identifier||(LA137_1>=FloatingPointLiteral && LA137_1<=DecimalLiteral)||LA137_1==47||(LA137_1>=56 && LA137_1<=63)||(LA137_1>=65 && LA137_1<=66)||(LA137_1>=69 && LA137_1<=72)||(LA137_1>=105 && LA137_1<=106)||(LA137_1>=109 && LA137_1<=113)) ) {
                    alt137=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 137, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA137_0==42) ) {
                int LA137_2 = input.LA(2);

                if ( (LA137_2==51) && (synpred207_Java())) {
                    alt137=2;
                }
                else if ( (LA137_2==Identifier||(LA137_2>=FloatingPointLiteral && LA137_2<=DecimalLiteral)||LA137_2==47||(LA137_2>=56 && LA137_2<=63)||(LA137_2>=65 && LA137_2<=66)||(LA137_2>=69 && LA137_2<=72)||(LA137_2>=105 && LA137_2<=106)||(LA137_2>=109 && LA137_2<=113)) ) {
                    alt137=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 137, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 137, 0, input);

                throw nvae;
            }
            switch (alt137) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:9: ( '<' '=' )=>t1= '<' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_relationalOp5458); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp5462); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "relationalOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:9: ( '>' '=' )=>t1= '>' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_relationalOp5492); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp5496); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "relationalOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1002:9: '<'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal371=(Token)match(input,40,FOLLOW_40_in_relationalOp5517); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal371_tree = (Object)adaptor.create(char_literal371);
                    adaptor.addChild(root_0, char_literal371_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1003:9: '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal372=(Token)match(input,42,FOLLOW_42_in_relationalOp5528); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal372_tree = (Object)adaptor.create(char_literal372);
                    adaptor.addChild(root_0, char_literal372_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 118, relationalOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalOp"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1006:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final JavaParser.shiftExpression_return shiftExpression() throws RecognitionException {
        JavaParser.shiftExpression_return retval = new JavaParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.additiveExpression_return additiveExpression373 = null;

        JavaParser.shiftOp_return shiftOp374 = null;

        JavaParser.additiveExpression_return additiveExpression375 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1007:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1007:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression5548);
            additiveExpression373=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression373.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1007:28: ( shiftOp additiveExpression )*
            loop138:
            do {
                int alt138=2;
                int LA138_0 = input.LA(1);

                if ( (LA138_0==40) ) {
                    int LA138_1 = input.LA(2);

                    if ( (LA138_1==40) ) {
                        int LA138_4 = input.LA(3);

                        if ( (LA138_4==Identifier||(LA138_4>=FloatingPointLiteral && LA138_4<=DecimalLiteral)||LA138_4==47||(LA138_4>=56 && LA138_4<=63)||(LA138_4>=65 && LA138_4<=66)||(LA138_4>=69 && LA138_4<=72)||(LA138_4>=105 && LA138_4<=106)||(LA138_4>=109 && LA138_4<=113)) ) {
                            alt138=1;
                        }


                    }


                }
                else if ( (LA138_0==42) ) {
                    int LA138_2 = input.LA(2);

                    if ( (LA138_2==42) ) {
                        int LA138_5 = input.LA(3);

                        if ( (LA138_5==42) ) {
                            int LA138_7 = input.LA(4);

                            if ( (LA138_7==Identifier||(LA138_7>=FloatingPointLiteral && LA138_7<=DecimalLiteral)||LA138_7==47||(LA138_7>=56 && LA138_7<=63)||(LA138_7>=65 && LA138_7<=66)||(LA138_7>=69 && LA138_7<=72)||(LA138_7>=105 && LA138_7<=106)||(LA138_7>=109 && LA138_7<=113)) ) {
                                alt138=1;
                            }


                        }
                        else if ( (LA138_5==Identifier||(LA138_5>=FloatingPointLiteral && LA138_5<=DecimalLiteral)||LA138_5==47||(LA138_5>=56 && LA138_5<=63)||(LA138_5>=65 && LA138_5<=66)||(LA138_5>=69 && LA138_5<=72)||(LA138_5>=105 && LA138_5<=106)||(LA138_5>=109 && LA138_5<=113)) ) {
                            alt138=1;
                        }


                    }


                }


                switch (alt138) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1007:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression5552);
            	    shiftOp374=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp374.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression5554);
            	    additiveExpression375=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression375.getTree());

            	    }
            	    break;

            	default :
            	    break loop138;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 119, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class shiftOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftOp"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1010:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);
    public final JavaParser.shiftOp_return shiftOp() throws RecognitionException {
        JavaParser.shiftOp_return retval = new JavaParser.shiftOp_return();
        retval.start = input.LT(1);
        int shiftOp_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object t3_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1011:5: ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?)
            int alt139=3;
            alt139 = dfa139.predict(input);
            switch (alt139) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1011:9: ( '<' '<' )=>t1= '<' t2= '<' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_shiftOp5585); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_shiftOp5589); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1014:9: ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp5621); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp5625); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_shiftOp5629); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() &&
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&\n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1019:9: ( '>' '>' )=>t1= '>' t2= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp5659); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp5663); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 120, shiftOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftOp"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1025:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final JavaParser.additiveExpression_return additiveExpression() throws RecognitionException {
        JavaParser.additiveExpression_return retval = new JavaParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set377=null;
        JavaParser.multiplicativeExpression_return multiplicativeExpression376 = null;

        JavaParser.multiplicativeExpression_return multiplicativeExpression378 = null;


        Object set377_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5693);
            multiplicativeExpression376=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression376.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop140:
            do {
                int alt140=2;
                int LA140_0 = input.LA(1);

                if ( ((LA140_0>=105 && LA140_0<=106)) ) {
                    alt140=1;
                }


                switch (alt140) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set377=(Token)input.LT(1);
            	    if ( (input.LA(1)>=105 && input.LA(1)<=106) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set377));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5705);
            	    multiplicativeExpression378=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression378.getTree());

            	    }
            	    break;

            	default :
            	    break loop140;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 121, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1029:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final JavaParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JavaParser.multiplicativeExpression_return retval = new JavaParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set380=null;
        JavaParser.unaryExpression_return unaryExpression379 = null;

        JavaParser.unaryExpression_return unaryExpression381 = null;


        Object set380_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5727);
            unaryExpression379=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression379.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop141:
            do {
                int alt141=2;
                int LA141_0 = input.LA(1);

                if ( (LA141_0==30||(LA141_0>=107 && LA141_0<=108)) ) {
                    alt141=1;
                }


                switch (alt141) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set380=(Token)input.LT(1);
            	    if ( input.LA(1)==30||(input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set380));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5745);
            	    unaryExpression381=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression381.getTree());

            	    }
            	    break;

            	default :
            	    break loop141;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 122, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1033:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final JavaParser.unaryExpression_return unaryExpression() throws RecognitionException {
        JavaParser.unaryExpression_return retval = new JavaParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal382=null;
        Token char_literal384=null;
        Token string_literal386=null;
        Token string_literal388=null;
        JavaParser.unaryExpression_return unaryExpression383 = null;

        JavaParser.unaryExpression_return unaryExpression385 = null;

        JavaParser.unaryExpression_return unaryExpression387 = null;

        JavaParser.unaryExpression_return unaryExpression389 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus390 = null;


        Object char_literal382_tree=null;
        Object char_literal384_tree=null;
        Object string_literal386_tree=null;
        Object string_literal388_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1034:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt142=5;
            switch ( input.LA(1) ) {
            case 105:
                {
                alt142=1;
                }
                break;
            case 106:
                {
                alt142=2;
                }
                break;
            case 109:
                {
                alt142=3;
                }
                break;
            case 110:
                {
                alt142=4;
                }
                break;
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 47:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 65:
            case 66:
            case 69:
            case 70:
            case 71:
            case 72:
            case 111:
            case 112:
            case 113:
                {
                alt142=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 142, 0, input);

                throw nvae;
            }

            switch (alt142) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1034:9: '+' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal382=(Token)match(input,105,FOLLOW_105_in_unaryExpression5771); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal382_tree = (Object)adaptor.create(char_literal382);
                    adaptor.addChild(root_0, char_literal382_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5773);
                    unaryExpression383=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression383.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1035:9: '-' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal384=(Token)match(input,106,FOLLOW_106_in_unaryExpression5783); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal384_tree = (Object)adaptor.create(char_literal384);
                    adaptor.addChild(root_0, char_literal384_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5785);
                    unaryExpression385=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression385.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1036:9: '++' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal386=(Token)match(input,109,FOLLOW_109_in_unaryExpression5795); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal386_tree = (Object)adaptor.create(string_literal386);
                    adaptor.addChild(root_0, string_literal386_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5797);
                    unaryExpression387=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression387.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1037:9: '--' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal388=(Token)match(input,110,FOLLOW_110_in_unaryExpression5807); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal388_tree = (Object)adaptor.create(string_literal388);
                    adaptor.addChild(root_0, string_literal388_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5809);
                    unaryExpression389=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression389.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1038:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5819);
                    unaryExpressionNotPlusMinus390=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus390.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 123, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1041:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JavaParser.unaryExpressionNotPlusMinus_return retval = new JavaParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal391=null;
        Token char_literal393=null;
        Token set398=null;
        JavaParser.unaryExpression_return unaryExpression392 = null;

        JavaParser.unaryExpression_return unaryExpression394 = null;

        JavaParser.castExpression_return castExpression395 = null;

        JavaParser.primary_return primary396 = null;

        JavaParser.selector_return selector397 = null;


        Object char_literal391_tree=null;
        Object char_literal393_tree=null;
        Object set398_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1042:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt145=4;
            alt145 = dfa145.predict(input);
            switch (alt145) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1042:9: '~' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal391=(Token)match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus5838); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal391_tree = (Object)adaptor.create(char_literal391);
                    adaptor.addChild(root_0, char_literal391_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5840);
                    unaryExpression392=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression392.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1043:9: '!' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal393=(Token)match(input,112,FOLLOW_112_in_unaryExpressionNotPlusMinus5850); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal393_tree = (Object)adaptor.create(char_literal393);
                    adaptor.addChild(root_0, char_literal393_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5852);
                    unaryExpression394=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression394.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1044:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5862);
                    castExpression395=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression395.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1045:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus5872);
                    primary396=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary396.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1045:17: ( selector )*
                    loop143:
                    do {
                        int alt143=2;
                        int LA143_0 = input.LA(1);

                        if ( (LA143_0==29||LA143_0==48) ) {
                            alt143=1;
                        }


                        switch (alt143) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus5874);
                    	    selector397=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector397.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop143;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1045:27: ( '++' | '--' )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( ((LA144_0>=109 && LA144_0<=110)) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
                            {
                            set398=(Token)input.LT(1);
                            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set398));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 124, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "castExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1048:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final JavaParser.castExpression_return castExpression() throws RecognitionException {
        JavaParser.castExpression_return retval = new JavaParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal399=null;
        Token char_literal401=null;
        Token char_literal403=null;
        Token char_literal406=null;
        JavaParser.primitiveType_return primitiveType400 = null;

        JavaParser.unaryExpression_return unaryExpression402 = null;

        JavaParser.type_return type404 = null;

        JavaParser.expression_return expression405 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus407 = null;


        Object char_literal399_tree=null;
        Object char_literal401_tree=null;
        Object char_literal403_tree=null;
        Object char_literal406_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1049:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
            int alt147=2;
            int LA147_0 = input.LA(1);

            if ( (LA147_0==66) ) {
                int LA147_1 = input.LA(2);

                if ( (synpred228_Java()) ) {
                    alt147=1;
                }
                else if ( (true) ) {
                    alt147=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 147, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 147, 0, input);

                throw nvae;
            }
            switch (alt147) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1049:8: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal399=(Token)match(input,66,FOLLOW_66_in_castExpression5900); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal399_tree = (Object)adaptor.create(char_literal399);
                    adaptor.addChild(root_0, char_literal399_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression5902);
                    primitiveType400=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType400.getTree());
                    char_literal401=(Token)match(input,67,FOLLOW_67_in_castExpression5904); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal401_tree = (Object)adaptor.create(char_literal401);
                    adaptor.addChild(root_0, char_literal401_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression5906);
                    unaryExpression402=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression402.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal403=(Token)match(input,66,FOLLOW_66_in_castExpression5915); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal403_tree = (Object)adaptor.create(char_literal403);
                    adaptor.addChild(root_0, char_literal403_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:12: ( type | expression )
                    int alt146=2;
                    alt146 = dfa146.predict(input);
                    switch (alt146) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression5918);
                            type404=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type404.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression5922);
                            expression405=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression405.getTree());

                            }
                            break;

                    }

                    char_literal406=(Token)match(input,67,FOLLOW_67_in_castExpression5925); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal406_tree = (Object)adaptor.create(char_literal406);
                    adaptor.addChild(root_0, char_literal406_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5927);
                    unaryExpressionNotPlusMinus407=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus407.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 125, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1053:1: primary : ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final JavaParser.primary_return primary() throws RecognitionException {
        JavaParser.primary_return retval = new JavaParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal409=null;
        Token char_literal410=null;
        Token Identifier411=null;
        Token string_literal413=null;
        Token string_literal416=null;
        Token Identifier418=null;
        Token char_literal419=null;
        Token Identifier420=null;
        Token char_literal423=null;
        Token char_literal424=null;
        Token char_literal425=null;
        Token string_literal426=null;
        Token string_literal427=null;
        Token char_literal428=null;
        Token string_literal429=null;
        JavaParser.parExpression_return parExpression408 = null;

        JavaParser.identifierSuffix_return identifierSuffix412 = null;

        JavaParser.superSuffix_return superSuffix414 = null;

        JavaParser.literal_return literal415 = null;

        JavaParser.creator_return creator417 = null;

        JavaParser.identifierSuffix_return identifierSuffix421 = null;

        JavaParser.primitiveType_return primitiveType422 = null;


        Object string_literal409_tree=null;
        Object char_literal410_tree=null;
        Object Identifier411_tree=null;
        Object string_literal413_tree=null;
        Object string_literal416_tree=null;
        Object Identifier418_tree=null;
        Object char_literal419_tree=null;
        Object Identifier420_tree=null;
        Object char_literal423_tree=null;
        Object char_literal424_tree=null;
        Object char_literal425_tree=null;
        Object string_literal426_tree=null;
        Object string_literal427_tree=null;
        Object char_literal428_tree=null;
        Object string_literal429_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1054:5: ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt153=8;
            switch ( input.LA(1) ) {
            case 66:
                {
                alt153=1;
                }
                break;
            case 69:
                {
                alt153=2;
                }
                break;
            case 65:
                {
                alt153=3;
                }
                break;
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 70:
            case 71:
            case 72:
                {
                alt153=4;
                }
                break;
            case 113:
                {
                alt153=5;
                }
                break;
            case Identifier:
                {
                alt153=6;
                }
                break;
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                {
                alt153=7;
                }
                break;
            case 47:
                {
                alt153=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 153, 0, input);

                throw nvae;
            }

            switch (alt153) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1054:9: parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary5946);
                    parExpression408=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression408.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal409=(Token)match(input,69,FOLLOW_69_in_primary5956); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal409_tree = (Object)adaptor.create(string_literal409);
                    adaptor.addChild(root_0, string_literal409_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:16: ( '.' Identifier )*
                    loop148:
                    do {
                        int alt148=2;
                        int LA148_0 = input.LA(1);

                        if ( (LA148_0==29) ) {
                            int LA148_2 = input.LA(2);

                            if ( (LA148_2==Identifier) ) {
                                int LA148_3 = input.LA(3);

                                if ( (synpred231_Java()) ) {
                                    alt148=1;
                                }


                            }


                        }


                        switch (alt148) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:17: '.' Identifier
                    	    {
                    	    char_literal410=(Token)match(input,29,FOLLOW_29_in_primary5959); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal410_tree = (Object)adaptor.create(char_literal410);
                    	    adaptor.addChild(root_0, char_literal410_tree);
                    	    }
                    	    Identifier411=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5961); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier411_tree = (Object)adaptor.create(Identifier411);
                    	    adaptor.addChild(root_0, Identifier411_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:34: ( identifierSuffix )?
                    int alt149=2;
                    alt149 = dfa149.predict(input);
                    switch (alt149) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5965);
                            identifierSuffix412=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix412.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1056:9: 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal413=(Token)match(input,65,FOLLOW_65_in_primary5976); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal413_tree = (Object)adaptor.create(string_literal413);
                    adaptor.addChild(root_0, string_literal413_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_primary5978);
                    superSuffix414=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix414.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1057:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary5988);
                    literal415=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal415.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1058:9: 'new' creator
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal416=(Token)match(input,113,FOLLOW_113_in_primary5998); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal416_tree = (Object)adaptor.create(string_literal416);
                    adaptor.addChild(root_0, string_literal416_tree);
                    }
                    pushFollow(FOLLOW_creator_in_primary6000);
                    creator417=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator417.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:9: Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier418=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary6010); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier418_tree = (Object)adaptor.create(Identifier418);
                    adaptor.addChild(root_0, Identifier418_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:20: ( '.' Identifier )*
                    loop150:
                    do {
                        int alt150=2;
                        int LA150_0 = input.LA(1);

                        if ( (LA150_0==29) ) {
                            int LA150_2 = input.LA(2);

                            if ( (LA150_2==Identifier) ) {
                                int LA150_3 = input.LA(3);

                                if ( (synpred237_Java()) ) {
                                    alt150=1;
                                }


                            }


                        }


                        switch (alt150) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:21: '.' Identifier
                    	    {
                    	    char_literal419=(Token)match(input,29,FOLLOW_29_in_primary6013); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal419_tree = (Object)adaptor.create(char_literal419);
                    	    adaptor.addChild(root_0, char_literal419_tree);
                    	    }
                    	    Identifier420=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary6015); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier420_tree = (Object)adaptor.create(Identifier420);
                    	    adaptor.addChild(root_0, Identifier420_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop150;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:38: ( identifierSuffix )?
                    int alt151=2;
                    alt151 = dfa151.predict(input);
                    switch (alt151) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6019);
                            identifierSuffix421=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix421.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1060:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary6030);
                    primitiveType422=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType422.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1060:23: ( '[' ']' )*
                    loop152:
                    do {
                        int alt152=2;
                        int LA152_0 = input.LA(1);

                        if ( (LA152_0==48) ) {
                            alt152=1;
                        }


                        switch (alt152) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1060:24: '[' ']'
                    	    {
                    	    char_literal423=(Token)match(input,48,FOLLOW_48_in_primary6033); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal423_tree = (Object)adaptor.create(char_literal423);
                    	    adaptor.addChild(root_0, char_literal423_tree);
                    	    }
                    	    char_literal424=(Token)match(input,49,FOLLOW_49_in_primary6035); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal424_tree = (Object)adaptor.create(char_literal424);
                    	    adaptor.addChild(root_0, char_literal424_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop152;
                        }
                    } while (true);

                    char_literal425=(Token)match(input,29,FOLLOW_29_in_primary6039); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal425_tree = (Object)adaptor.create(char_literal425);
                    adaptor.addChild(root_0, char_literal425_tree);
                    }
                    string_literal426=(Token)match(input,37,FOLLOW_37_in_primary6041); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal426_tree = (Object)adaptor.create(string_literal426);
                    adaptor.addChild(root_0, string_literal426_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1061:9: 'void' '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal427=(Token)match(input,47,FOLLOW_47_in_primary6051); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal427_tree = (Object)adaptor.create(string_literal427);
                    adaptor.addChild(root_0, string_literal427_tree);
                    }
                    char_literal428=(Token)match(input,29,FOLLOW_29_in_primary6053); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal428_tree = (Object)adaptor.create(char_literal428);
                    adaptor.addChild(root_0, char_literal428_tree);
                    }
                    string_literal429=(Token)match(input,37,FOLLOW_37_in_primary6055); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal429_tree = (Object)adaptor.create(string_literal429);
                    adaptor.addChild(root_0, string_literal429_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 126, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class identifierSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "identifierSuffix"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1064:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );
    public final JavaParser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        JavaParser.identifierSuffix_return retval = new JavaParser.identifierSuffix_return();
        retval.start = input.LT(1);
        int identifierSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal430=null;
        Token char_literal431=null;
        Token char_literal432=null;
        Token string_literal433=null;
        Token char_literal434=null;
        Token char_literal436=null;
        Token char_literal438=null;
        Token string_literal439=null;
        Token char_literal440=null;
        Token char_literal442=null;
        Token string_literal443=null;
        Token char_literal444=null;
        Token string_literal445=null;
        Token char_literal447=null;
        Token string_literal448=null;
        JavaParser.expression_return expression435 = null;

        JavaParser.arguments_return arguments437 = null;

        JavaParser.explicitGenericInvocation_return explicitGenericInvocation441 = null;

        JavaParser.arguments_return arguments446 = null;

        JavaParser.innerCreator_return innerCreator449 = null;


        Object char_literal430_tree=null;
        Object char_literal431_tree=null;
        Object char_literal432_tree=null;
        Object string_literal433_tree=null;
        Object char_literal434_tree=null;
        Object char_literal436_tree=null;
        Object char_literal438_tree=null;
        Object string_literal439_tree=null;
        Object char_literal440_tree=null;
        Object char_literal442_tree=null;
        Object string_literal443_tree=null;
        Object char_literal444_tree=null;
        Object string_literal445_tree=null;
        Object char_literal447_tree=null;
        Object string_literal448_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator )
            int alt156=8;
            alt156 = dfa156.predict(input);
            switch (alt156) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:9: ( '[' ']' )+
                    int cnt154=0;
                    loop154:
                    do {
                        int alt154=2;
                        int LA154_0 = input.LA(1);

                        if ( (LA154_0==48) ) {
                            alt154=1;
                        }


                        switch (alt154) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:10: '[' ']'
                    	    {
                    	    char_literal430=(Token)match(input,48,FOLLOW_48_in_identifierSuffix6075); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal430_tree = (Object)adaptor.create(char_literal430);
                    	    adaptor.addChild(root_0, char_literal430_tree);
                    	    }
                    	    char_literal431=(Token)match(input,49,FOLLOW_49_in_identifierSuffix6077); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal431_tree = (Object)adaptor.create(char_literal431);
                    	    adaptor.addChild(root_0, char_literal431_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt154 >= 1 ) break loop154;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(154, input);
                                throw eee;
                        }
                        cnt154++;
                    } while (true);

                    char_literal432=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6081); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal432_tree = (Object)adaptor.create(char_literal432);
                    adaptor.addChild(root_0, char_literal432_tree);
                    }
                    string_literal433=(Token)match(input,37,FOLLOW_37_in_identifierSuffix6083); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal433_tree = (Object)adaptor.create(string_literal433);
                    adaptor.addChild(root_0, string_literal433_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:9: ( '[' expression ']' )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:9: ( '[' expression ']' )+
                    int cnt155=0;
                    loop155:
                    do {
                        int alt155=2;
                        alt155 = dfa155.predict(input);
                        switch (alt155) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:10: '[' expression ']'
                    	    {
                    	    char_literal434=(Token)match(input,48,FOLLOW_48_in_identifierSuffix6094); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal434_tree = (Object)adaptor.create(char_literal434);
                    	    adaptor.addChild(root_0, char_literal434_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix6096);
                    	    expression435=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression435.getTree());
                    	    char_literal436=(Token)match(input,49,FOLLOW_49_in_identifierSuffix6098); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal436_tree = (Object)adaptor.create(char_literal436);
                    	    adaptor.addChild(root_0, char_literal436_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt155 >= 1 ) break loop155;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(155, input);
                                throw eee;
                        }
                        cnt155++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1067:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix6111);
                    arguments437=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments437.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1068:9: '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal438=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6121); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal438_tree = (Object)adaptor.create(char_literal438);
                    adaptor.addChild(root_0, char_literal438_tree);
                    }
                    string_literal439=(Token)match(input,37,FOLLOW_37_in_identifierSuffix6123); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal439_tree = (Object)adaptor.create(string_literal439);
                    adaptor.addChild(root_0, string_literal439_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1069:9: '.' explicitGenericInvocation
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal440=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6133); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal440_tree = (Object)adaptor.create(char_literal440);
                    adaptor.addChild(root_0, char_literal440_tree);
                    }
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix6135);
                    explicitGenericInvocation441=explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGenericInvocation441.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1070:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal442=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6145); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal442_tree = (Object)adaptor.create(char_literal442);
                    adaptor.addChild(root_0, char_literal442_tree);
                    }
                    string_literal443=(Token)match(input,69,FOLLOW_69_in_identifierSuffix6147); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal443_tree = (Object)adaptor.create(string_literal443);
                    adaptor.addChild(root_0, string_literal443_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:9: '.' 'super' arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal444=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6157); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal444_tree = (Object)adaptor.create(char_literal444);
                    adaptor.addChild(root_0, char_literal444_tree);
                    }
                    string_literal445=(Token)match(input,65,FOLLOW_65_in_identifierSuffix6159); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal445_tree = (Object)adaptor.create(string_literal445);
                    adaptor.addChild(root_0, string_literal445_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_identifierSuffix6161);
                    arguments446=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments446.getTree());

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1072:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal447=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6171); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal447_tree = (Object)adaptor.create(char_literal447);
                    adaptor.addChild(root_0, char_literal447_tree);
                    }
                    string_literal448=(Token)match(input,113,FOLLOW_113_in_identifierSuffix6173); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal448_tree = (Object)adaptor.create(string_literal448);
                    adaptor.addChild(root_0, string_literal448_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix6175);
                    innerCreator449=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator449.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 127, identifierSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "identifierSuffix"

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "creator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:1: creator : ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) );
    public final JavaParser.creator_return creator() throws RecognitionException {
        JavaParser.creator_return retval = new JavaParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments450 = null;

        JavaParser.createdName_return createdName451 = null;

        JavaParser.classCreatorRest_return classCreatorRest452 = null;

        JavaParser.createdName_return createdName453 = null;

        JavaParser.arrayCreatorRest_return arrayCreatorRest454 = null;

        JavaParser.classCreatorRest_return classCreatorRest455 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1076:5: ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) )
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==40) ) {
                alt158=1;
            }
            else if ( (LA158_0==Identifier||(LA158_0>=56 && LA158_0<=63)) ) {
                alt158=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 158, 0, input);

                throw nvae;
            }
            switch (alt158) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1076:9: nonWildcardTypeArguments createdName classCreatorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator6194);
                    nonWildcardTypeArguments450=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments450.getTree());
                    pushFollow(FOLLOW_createdName_in_creator6196);
                    createdName451=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName451.getTree());
                    pushFollow(FOLLOW_classCreatorRest_in_creator6198);
                    classCreatorRest452=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest452.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1077:9: createdName ( arrayCreatorRest | classCreatorRest )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_createdName_in_creator6208);
                    createdName453=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName453.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1077:21: ( arrayCreatorRest | classCreatorRest )
                    int alt157=2;
                    int LA157_0 = input.LA(1);

                    if ( (LA157_0==48) ) {
                        alt157=1;
                    }
                    else if ( (LA157_0==66) ) {
                        alt157=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 157, 0, input);

                        throw nvae;
                    }
                    switch (alt157) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1077:22: arrayCreatorRest
                            {
                            pushFollow(FOLLOW_arrayCreatorRest_in_creator6211);
                            arrayCreatorRest454=arrayCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreatorRest454.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1077:41: classCreatorRest
                            {
                            pushFollow(FOLLOW_classCreatorRest_in_creator6215);
                            classCreatorRest455=classCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest455.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 128, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class createdName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "createdName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1080:1: createdName : ( classOrInterfaceType | primitiveType );
    public final JavaParser.createdName_return createdName() throws RecognitionException {
        JavaParser.createdName_return retval = new JavaParser.createdName_return();
        retval.start = input.LT(1);
        int createdName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType456 = null;

        JavaParser.primitiveType_return primitiveType457 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1081:5: ( classOrInterfaceType | primitiveType )
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( (LA159_0==Identifier) ) {
                alt159=1;
            }
            else if ( ((LA159_0>=56 && LA159_0<=63)) ) {
                alt159=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 159, 0, input);

                throw nvae;
            }
            switch (alt159) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1081:9: classOrInterfaceType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName6235);
                    classOrInterfaceType456=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType456.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1082:9: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName6245);
                    primitiveType457=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType457.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 129, createdName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "createdName"

    public static class innerCreator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "innerCreator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1085:1: innerCreator : ( nonWildcardTypeArguments )? Identifier classCreatorRest ;
    public final JavaParser.innerCreator_return innerCreator() throws RecognitionException {
        JavaParser.innerCreator_return retval = new JavaParser.innerCreator_return();
        retval.start = input.LT(1);
        int innerCreator_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier459=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments458 = null;

        JavaParser.classCreatorRest_return classCreatorRest460 = null;


        Object Identifier459_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1086:5: ( ( nonWildcardTypeArguments )? Identifier classCreatorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1086:9: ( nonWildcardTypeArguments )? Identifier classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1086:9: ( nonWildcardTypeArguments )?
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==40) ) {
                alt160=1;
            }
            switch (alt160) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator6268);
                    nonWildcardTypeArguments458=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments458.getTree());

                    }
                    break;

            }

            Identifier459=(Token)match(input,Identifier,FOLLOW_Identifier_in_innerCreator6271); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier459_tree = (Object)adaptor.create(Identifier459);
            adaptor.addChild(root_0, Identifier459_tree);
            }
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator6273);
            classCreatorRest460=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest460.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 130, innerCreator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "innerCreator"

    public static class arrayCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayCreatorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1089:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final JavaParser.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        JavaParser.arrayCreatorRest_return retval = new JavaParser.arrayCreatorRest_return();
        retval.start = input.LT(1);
        int arrayCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal461=null;
        Token char_literal462=null;
        Token char_literal463=null;
        Token char_literal464=null;
        Token char_literal467=null;
        Token char_literal468=null;
        Token char_literal470=null;
        Token char_literal471=null;
        Token char_literal472=null;
        JavaParser.arrayInitializer_return arrayInitializer465 = null;

        JavaParser.expression_return expression466 = null;

        JavaParser.expression_return expression469 = null;


        Object char_literal461_tree=null;
        Object char_literal462_tree=null;
        Object char_literal463_tree=null;
        Object char_literal464_tree=null;
        Object char_literal467_tree=null;
        Object char_literal468_tree=null;
        Object char_literal470_tree=null;
        Object char_literal471_tree=null;
        Object char_literal472_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1090:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1090:9: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            root_0 = (Object)adaptor.nil();

            char_literal461=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6292); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal461_tree = (Object)adaptor.create(char_literal461);
            adaptor.addChild(root_0, char_literal461_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1091:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt164=2;
            int LA164_0 = input.LA(1);

            if ( (LA164_0==49) ) {
                alt164=1;
            }
            else if ( (LA164_0==Identifier||(LA164_0>=FloatingPointLiteral && LA164_0<=DecimalLiteral)||LA164_0==47||(LA164_0>=56 && LA164_0<=63)||(LA164_0>=65 && LA164_0<=66)||(LA164_0>=69 && LA164_0<=72)||(LA164_0>=105 && LA164_0<=106)||(LA164_0>=109 && LA164_0<=113)) ) {
                alt164=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 164, 0, input);

                throw nvae;
            }
            switch (alt164) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1091:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    char_literal462=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6306); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal462_tree = (Object)adaptor.create(char_literal462);
                    adaptor.addChild(root_0, char_literal462_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1091:17: ( '[' ']' )*
                    loop161:
                    do {
                        int alt161=2;
                        int LA161_0 = input.LA(1);

                        if ( (LA161_0==48) ) {
                            alt161=1;
                        }


                        switch (alt161) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1091:18: '[' ']'
                    	    {
                    	    char_literal463=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6309); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal463_tree = (Object)adaptor.create(char_literal463);
                    	    adaptor.addChild(root_0, char_literal463_tree);
                    	    }
                    	    char_literal464=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6311); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal464_tree = (Object)adaptor.create(char_literal464);
                    	    adaptor.addChild(root_0, char_literal464_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop161;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest6315);
                    arrayInitializer465=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer465.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest6329);
                    expression466=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression466.getTree());
                    char_literal467=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6331); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal467_tree = (Object)adaptor.create(char_literal467);
                    adaptor.addChild(root_0, char_literal467_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:28: ( '[' expression ']' )*
                    loop162:
                    do {
                        int alt162=2;
                        alt162 = dfa162.predict(input);
                        switch (alt162) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:29: '[' expression ']'
                    	    {
                    	    char_literal468=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6334); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal468_tree = (Object)adaptor.create(char_literal468);
                    	    adaptor.addChild(root_0, char_literal468_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest6336);
                    	    expression469=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression469.getTree());
                    	    char_literal470=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6338); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal470_tree = (Object)adaptor.create(char_literal470);
                    	    adaptor.addChild(root_0, char_literal470_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop162;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:50: ( '[' ']' )*
                    loop163:
                    do {
                        int alt163=2;
                        int LA163_0 = input.LA(1);

                        if ( (LA163_0==48) ) {
                            int LA163_2 = input.LA(2);

                            if ( (LA163_2==49) ) {
                                alt163=1;
                            }


                        }


                        switch (alt163) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:51: '[' ']'
                    	    {
                    	    char_literal471=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6343); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal471_tree = (Object)adaptor.create(char_literal471);
                    	    adaptor.addChild(root_0, char_literal471_tree);
                    	    }
                    	    char_literal472=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6345); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal472_tree = (Object)adaptor.create(char_literal472);
                    	    adaptor.addChild(root_0, char_literal472_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop163;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 131, arrayCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arrayCreatorRest"

    public static class classCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classCreatorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1096:1: classCreatorRest : arguments ( classBody )? ;
    public final JavaParser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        JavaParser.classCreatorRest_return retval = new JavaParser.classCreatorRest_return();
        retval.start = input.LT(1);
        int classCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arguments_return arguments473 = null;

        JavaParser.classBody_return classBody474 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1097:5: ( arguments ( classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1097:9: arguments ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest6376);
            arguments473=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments473.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1097:19: ( classBody )?
            int alt165=2;
            int LA165_0 = input.LA(1);

            if ( (LA165_0==44) ) {
                alt165=1;
            }
            switch (alt165) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest6378);
                    classBody474=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody474.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 132, classCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classCreatorRest"

    public static class explicitGenericInvocation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitGenericInvocation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1100:1: explicitGenericInvocation : nonWildcardTypeArguments Identifier arguments ;
    public final JavaParser.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        JavaParser.explicitGenericInvocation_return retval = new JavaParser.explicitGenericInvocation_return();
        retval.start = input.LT(1);
        int explicitGenericInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier476=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments475 = null;

        JavaParser.arguments_return arguments477 = null;


        Object Identifier476_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1101:5: ( nonWildcardTypeArguments Identifier arguments )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1101:9: nonWildcardTypeArguments Identifier arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6402);
            nonWildcardTypeArguments475=nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments475.getTree());
            Identifier476=(Token)match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocation6404); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier476_tree = (Object)adaptor.create(Identifier476);
            adaptor.addChild(root_0, Identifier476_tree);
            }
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation6406);
            arguments477=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments477.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 133, explicitGenericInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "explicitGenericInvocation"

    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nonWildcardTypeArguments"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1104:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        JavaParser.nonWildcardTypeArguments_return retval = new JavaParser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);
        int nonWildcardTypeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal478=null;
        Token char_literal480=null;
        JavaParser.typeList_return typeList479 = null;


        Object char_literal478_tree=null;
        Object char_literal480_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1105:5: ( '<' typeList '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1105:9: '<' typeList '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal478=(Token)match(input,40,FOLLOW_40_in_nonWildcardTypeArguments6429); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal478_tree = (Object)adaptor.create(char_literal478);
            adaptor.addChild(root_0, char_literal478_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments6431);
            typeList479=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList479.getTree());
            char_literal480=(Token)match(input,42,FOLLOW_42_in_nonWildcardTypeArguments6433); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal480_tree = (Object)adaptor.create(char_literal480);
            adaptor.addChild(root_0, char_literal480_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 134, nonWildcardTypeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "nonWildcardTypeArguments"

    public static class selector_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selector"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' );
    public final JavaParser.selector_return selector() throws RecognitionException {
        JavaParser.selector_return retval = new JavaParser.selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal481=null;
        Token Identifier482=null;
        Token char_literal484=null;
        Token string_literal485=null;
        Token char_literal486=null;
        Token string_literal487=null;
        Token char_literal489=null;
        Token string_literal490=null;
        Token char_literal492=null;
        Token char_literal494=null;
        JavaParser.arguments_return arguments483 = null;

        JavaParser.superSuffix_return superSuffix488 = null;

        JavaParser.innerCreator_return innerCreator491 = null;

        JavaParser.expression_return expression493 = null;


        Object char_literal481_tree=null;
        Object Identifier482_tree=null;
        Object char_literal484_tree=null;
        Object string_literal485_tree=null;
        Object char_literal486_tree=null;
        Object string_literal487_tree=null;
        Object char_literal489_tree=null;
        Object string_literal490_tree=null;
        Object char_literal492_tree=null;
        Object char_literal494_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1109:5: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' )
            int alt167=5;
            int LA167_0 = input.LA(1);

            if ( (LA167_0==29) ) {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    alt167=1;
                    }
                    break;
                case 69:
                    {
                    alt167=2;
                    }
                    break;
                case 65:
                    {
                    alt167=3;
                    }
                    break;
                case 113:
                    {
                    alt167=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 167, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA167_0==48) ) {
                alt167=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 167, 0, input);

                throw nvae;
            }
            switch (alt167) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1109:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal481=(Token)match(input,29,FOLLOW_29_in_selector6456); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal481_tree = (Object)adaptor.create(char_literal481);
                    adaptor.addChild(root_0, char_literal481_tree);
                    }
                    Identifier482=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector6458); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier482_tree = (Object)adaptor.create(Identifier482);
                    adaptor.addChild(root_0, Identifier482_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1109:24: ( arguments )?
                    int alt166=2;
                    int LA166_0 = input.LA(1);

                    if ( (LA166_0==66) ) {
                        alt166=1;
                    }
                    switch (alt166) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector6460);
                            arguments483=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments483.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1110:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal484=(Token)match(input,29,FOLLOW_29_in_selector6471); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal484_tree = (Object)adaptor.create(char_literal484);
                    adaptor.addChild(root_0, char_literal484_tree);
                    }
                    string_literal485=(Token)match(input,69,FOLLOW_69_in_selector6473); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal485_tree = (Object)adaptor.create(string_literal485);
                    adaptor.addChild(root_0, string_literal485_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1111:9: '.' 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal486=(Token)match(input,29,FOLLOW_29_in_selector6483); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal486_tree = (Object)adaptor.create(char_literal486);
                    adaptor.addChild(root_0, char_literal486_tree);
                    }
                    string_literal487=(Token)match(input,65,FOLLOW_65_in_selector6485); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal487_tree = (Object)adaptor.create(string_literal487);
                    adaptor.addChild(root_0, string_literal487_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_selector6487);
                    superSuffix488=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix488.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1112:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal489=(Token)match(input,29,FOLLOW_29_in_selector6497); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal489_tree = (Object)adaptor.create(char_literal489);
                    adaptor.addChild(root_0, char_literal489_tree);
                    }
                    string_literal490=(Token)match(input,113,FOLLOW_113_in_selector6499); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal490_tree = (Object)adaptor.create(string_literal490);
                    adaptor.addChild(root_0, string_literal490_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_selector6501);
                    innerCreator491=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator491.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1113:9: '[' expression ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal492=(Token)match(input,48,FOLLOW_48_in_selector6511); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal492_tree = (Object)adaptor.create(char_literal492);
                    adaptor.addChild(root_0, char_literal492_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector6513);
                    expression493=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression493.getTree());
                    char_literal494=(Token)match(input,49,FOLLOW_49_in_selector6515); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal494_tree = (Object)adaptor.create(char_literal494);
                    adaptor.addChild(root_0, char_literal494_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 135, selector_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "selector"

    public static class superSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "superSuffix"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1116:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final JavaParser.superSuffix_return superSuffix() throws RecognitionException {
        JavaParser.superSuffix_return retval = new JavaParser.superSuffix_return();
        retval.start = input.LT(1);
        int superSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal496=null;
        Token Identifier497=null;
        JavaParser.arguments_return arguments495 = null;

        JavaParser.arguments_return arguments498 = null;


        Object char_literal496_tree=null;
        Object Identifier497_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 136) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1117:5: ( arguments | '.' Identifier ( arguments )? )
            int alt169=2;
            int LA169_0 = input.LA(1);

            if ( (LA169_0==66) ) {
                alt169=1;
            }
            else if ( (LA169_0==29) ) {
                alt169=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 169, 0, input);

                throw nvae;
            }
            switch (alt169) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1117:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix6538);
                    arguments495=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments495.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1118:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal496=(Token)match(input,29,FOLLOW_29_in_superSuffix6548); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal496_tree = (Object)adaptor.create(char_literal496);
                    adaptor.addChild(root_0, char_literal496_tree);
                    }
                    Identifier497=(Token)match(input,Identifier,FOLLOW_Identifier_in_superSuffix6550); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier497_tree = (Object)adaptor.create(Identifier497);
                    adaptor.addChild(root_0, Identifier497_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1118:24: ( arguments )?
                    int alt168=2;
                    int LA168_0 = input.LA(1);

                    if ( (LA168_0==66) ) {
                        alt168=1;
                    }
                    switch (alt168) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix6552);
                            arguments498=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments498.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 136, superSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "superSuffix"

    public static class arguments_return extends ParserRuleReturnScope {
        public List<ActualParameter> element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arguments"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1121:1: arguments returns [List<ActualParameter> element] : '(' ( expressionList )? ')' ;
    public final JavaParser.arguments_return arguments() throws RecognitionException {
        JavaParser.arguments_return retval = new JavaParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal499=null;
        Token char_literal501=null;
        JavaParser.expressionList_return expressionList500 = null;


        Object char_literal499_tree=null;
        Object char_literal501_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 137) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1122:5: ( '(' ( expressionList )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1122:9: '(' ( expressionList )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal499=(Token)match(input,66,FOLLOW_66_in_arguments6576); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal499_tree = (Object)adaptor.create(char_literal499);
            adaptor.addChild(root_0, char_literal499_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1122:13: ( expressionList )?
            int alt170=2;
            int LA170_0 = input.LA(1);

            if ( (LA170_0==Identifier||(LA170_0>=FloatingPointLiteral && LA170_0<=DecimalLiteral)||LA170_0==47||(LA170_0>=56 && LA170_0<=63)||(LA170_0>=65 && LA170_0<=66)||(LA170_0>=69 && LA170_0<=72)||(LA170_0>=105 && LA170_0<=106)||(LA170_0>=109 && LA170_0<=113)) ) {
                alt170=1;
            }
            switch (alt170) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments6578);
                    expressionList500=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList500.getTree());

                    }
                    break;

            }

            char_literal501=(Token)match(input,67,FOLLOW_67_in_arguments6581); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal501_tree = (Object)adaptor.create(char_literal501);
            adaptor.addChild(root_0, char_literal501_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 137, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arguments"

    // $ANTLR start synpred5_Java
    public final void synpred5_Java_fragment() throws RecognitionException {   
        JavaParser.packageDeclaration_return np = null;

        JavaParser.importDeclaration_return imp = null;

        JavaParser.typeDeclaration_return typech = null;

        JavaParser.classOrInterfaceDeclaration_return cd = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:10: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
        {
        pushFollow(FOLLOW_annotations_in_synpred5_Java96);
        annotations();

        state._fsp--;
        if (state.failed) return ;
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
        int alt176=2;
        int LA176_0 = input.LA(1);

        if ( (LA176_0==25) ) {
            alt176=1;
        }
        else if ( (LA176_0==ENUM||LA176_0==28||(LA176_0>=31 && LA176_0<=37)||LA176_0==46||LA176_0==73) ) {
            alt176=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 176, 0, input);

            throw nvae;
        }
        switch (alt176) {
            case 1 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_packageDeclaration_in_synpred5_Java112);
                np=packageDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:90: (imp= importDeclaration )*
                loop173:
                do {
                    int alt173=2;
                    int LA173_0 = input.LA(1);

                    if ( (LA173_0==27) ) {
                        alt173=1;
                    }


                    switch (alt173) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:91: imp= importDeclaration
                	    {
                	    pushFollow(FOLLOW_importDeclaration_in_synpred5_Java118);
                	    imp=importDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop173;
                    }
                } while (true);

                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:159: (typech= typeDeclaration )*
                loop174:
                do {
                    int alt174=2;
                    int LA174_0 = input.LA(1);

                    if ( (LA174_0==ENUM||LA174_0==26||LA174_0==28||(LA174_0>=31 && LA174_0<=37)||LA174_0==46||LA174_0==73) ) {
                        alt174=1;
                    }


                    switch (alt174) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:160: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java126);
                	    typech=typeDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop174;
                    }
                } while (true);


                }
                break;
            case 2 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:344:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java145);
                cd=classOrInterfaceDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:344:85: (typech= typeDeclaration )*
                loop175:
                do {
                    int alt175=2;
                    int LA175_0 = input.LA(1);

                    if ( (LA175_0==ENUM||LA175_0==26||LA175_0==28||(LA175_0>=31 && LA175_0<=37)||LA175_0==46||LA175_0==73) ) {
                        alt175=1;
                    }


                    switch (alt175) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:344:86: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java151);
                	    typech=typeDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop175;
                    }
                } while (true);


                }
                break;

        }


        }
    }
    // $ANTLR end synpred5_Java

    // $ANTLR start synpred53_Java
    public final void synpred53_Java_fragment() throws RecognitionException {   
        JavaParser.methodDeclaration_return method = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:9: (method= methodDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:9: method= methodDeclaration
        {
        pushFollow(FOLLOW_methodDeclaration_in_synpred53_Java1577);
        method=methodDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred53_Java

    // $ANTLR start synpred61_Java
    public final void synpred61_Java_fragment() throws RecognitionException {   
        JavaParser.interfaceConstant_return cst = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:552:9: (cst= interfaceConstant )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:552:9: cst= interfaceConstant
        {
        pushFollow(FOLLOW_interfaceConstant_in_synpred61_Java1975);
        cst=interfaceConstant();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred61_Java

    // $ANTLR start synpred81_Java
    public final void synpred81_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:631:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:631:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred81_Java2697);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred81_Java

    // $ANTLR start synpred82_Java
    public final void synpred82_Java_fragment() throws RecognitionException {   
        JavaParser.classOrInterfaceModifier_return mod = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:632:9: (mod= classOrInterfaceModifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:632:9: mod= classOrInterfaceModifier
        {
        pushFollow(FOLLOW_classOrInterfaceModifier_in_synpred82_Java2709);
        mod=classOrInterfaceModifier();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred82_Java

    // $ANTLR start synpred108_Java
    public final void synpred108_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:710:13: ( explicitConstructorInvocation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:710:13: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred108_Java3296);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred108_Java

    // $ANTLR start synpred112_Java
    public final void synpred112_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:714:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:714:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:714:9: ( nonWildcardTypeArguments )?
        int alt184=2;
        int LA184_0 = input.LA(1);

        if ( (LA184_0==40) ) {
            alt184=1;
        }
        switch (alt184) {
            case 1 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred112_Java3321);
                nonWildcardTypeArguments();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        if ( input.LA(1)==65||input.LA(1)==69 ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_arguments_in_synpred112_Java3332);
        arguments();

        state._fsp--;
        if (state.failed) return ;
        match(input,26,FOLLOW_26_in_synpred112_Java3334); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred112_Java

    // $ANTLR start synpred123_Java
    public final void synpred123_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred123_Java3545);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred123_Java

    // $ANTLR start synpred146_Java
    public final void synpred146_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:819:9: ( localVariableDeclarationStatement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:819:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred146_Java4080);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred146_Java

    // $ANTLR start synpred147_Java
    public final void synpred147_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:820:9: ( classOrInterfaceDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:820:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred147_Java4090);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred147_Java

    // $ANTLR start synpred152_Java
    public final void synpred152_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:54: ( 'else' statement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:54: 'else' statement
        {
        match(input,77,FOLLOW_77_in_synpred152_Java4235); if (state.failed) return ;
        pushFollow(FOLLOW_statement_in_synpred152_Java4237);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred152_Java

    // $ANTLR start synpred157_Java
    public final void synpred157_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:11: ( catches 'finally' block )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:11: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred157_Java4313);
        catches();

        state._fsp--;
        if (state.failed) return ;
        match(input,82,FOLLOW_82_in_synpred157_Java4315); if (state.failed) return ;
        pushFollow(FOLLOW_block_in_synpred157_Java4317);
        block();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred157_Java

    // $ANTLR start synpred158_Java
    public final void synpred158_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:11: ( catches )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:11: catches
        {
        pushFollow(FOLLOW_catches_in_synpred158_Java4329);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred158_Java

    // $ANTLR start synpred173_Java
    public final void synpred173_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:9: ( switchLabel )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:9: switchLabel
        {
        pushFollow(FOLLOW_switchLabel_in_synpred173_Java4620);
        switchLabel();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred173_Java

    // $ANTLR start synpred175_Java
    public final void synpred175_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:9: ( 'case' constantExpression ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:9: 'case' constantExpression ':'
        {
        match(input,89,FOLLOW_89_in_synpred175_Java4647); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred175_Java4649);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred175_Java4651); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred175_Java

    // $ANTLR start synpred176_Java
    public final void synpred176_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:885:9: ( 'case' enumConstantName ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:885:9: 'case' enumConstantName ':'
        {
        match(input,89,FOLLOW_89_in_synpred176_Java4661); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred176_Java4663);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred176_Java4665); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred176_Java

    // $ANTLR start synpred177_Java
    public final void synpred177_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:891:9: ( enhancedForControl )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:891:9: enhancedForControl
        {
        pushFollow(FOLLOW_enhancedForControl_in_synpred177_Java4708);
        enhancedForControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred177_Java

    // $ANTLR start synpred181_Java
    public final void synpred181_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: ( localVariableDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred181_Java4748);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred181_Java

    // $ANTLR start synpred183_Java
    public final void synpred183_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:32: ( assignmentOperator expression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:32: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred183_Java4935);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred183_Java4937);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred183_Java

    // $ANTLR start synpred193_Java
    public final void synpred193_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:940:9: ( '<' '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:940:10: '<' '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred193_Java5053); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred193_Java5055); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred193_Java5057); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred193_Java

    // $ANTLR start synpred194_Java
    public final void synpred194_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:9: ( '>' '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:10: '>' '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred194_Java5093); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred194_Java5095); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred194_Java5097); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred194_Java5099); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred194_Java

    // $ANTLR start synpred195_Java
    public final void synpred195_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:9: ( '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:10: '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred195_Java5138); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred195_Java5140); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred195_Java5142); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred195_Java

    // $ANTLR start synpred206_Java
    public final void synpred206_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:9: ( '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:10: '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred206_Java5450); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred206_Java5452); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred206_Java

    // $ANTLR start synpred207_Java
    public final void synpred207_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:9: ( '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:10: '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred207_Java5484); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred207_Java5486); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred207_Java

    // $ANTLR start synpred210_Java
    public final void synpred210_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1011:9: ( '<' '<' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1011:10: '<' '<'
        {
        match(input,40,FOLLOW_40_in_synpred210_Java5577); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred210_Java5579); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred210_Java

    // $ANTLR start synpred211_Java
    public final void synpred211_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1014:9: ( '>' '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1014:10: '>' '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred211_Java5611); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred211_Java5613); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred211_Java5615); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred211_Java

    // $ANTLR start synpred212_Java
    public final void synpred212_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1019:9: ( '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1019:10: '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred212_Java5651); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred212_Java5653); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred212_Java

    // $ANTLR start synpred224_Java
    public final void synpred224_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1044:9: ( castExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1044:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred224_Java5862);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred224_Java

    // $ANTLR start synpred228_Java
    public final void synpred228_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1049:8: ( '(' primitiveType ')' unaryExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1049:8: '(' primitiveType ')' unaryExpression
        {
        match(input,66,FOLLOW_66_in_synpred228_Java5900); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred228_Java5902);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,67,FOLLOW_67_in_synpred228_Java5904); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred228_Java5906);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred228_Java

    // $ANTLR start synpred229_Java
    public final void synpred229_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:13: ( type )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:13: type
        {
        pushFollow(FOLLOW_type_in_synpred229_Java5918);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred229_Java

    // $ANTLR start synpred231_Java
    public final void synpred231_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:17: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:17: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred231_Java5959); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred231_Java5961); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred231_Java

    // $ANTLR start synpred232_Java
    public final void synpred232_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:34: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1055:34: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred232_Java5965);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred232_Java

    // $ANTLR start synpred237_Java
    public final void synpred237_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:21: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:21: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred237_Java6013); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred237_Java6015); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred237_Java

    // $ANTLR start synpred238_Java
    public final void synpred238_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:38: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:38: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred238_Java6019);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred238_Java

    // $ANTLR start synpred244_Java
    public final void synpred244_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:10: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:10: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred244_Java6094); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred244_Java6096);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred244_Java6098); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred244_Java

    // $ANTLR start synpred257_Java
    public final void synpred257_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:29: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:29: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred257_Java6334); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred257_Java6336);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred257_Java6338); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred257_Java

    // Delegated rules

    public final boolean synpred244_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred244_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred206_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred206_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred195_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred195_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred224_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred224_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred210_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred210_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred193_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred193_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred207_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred207_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred181_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred181_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred152_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred152_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred228_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred228_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred158_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred158_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred61_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred61_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred112_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred112_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred194_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred194_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred81_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred81_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred82_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred82_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred173_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred173_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred229_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred229_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred257_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred257_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred108_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred108_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred183_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred183_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred147_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred147_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred232_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred232_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred146_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred146_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred211_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred211_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred231_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred231_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred212_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred212_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred53_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred53_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred123_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred123_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred176_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred176_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred177_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred177_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred157_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred157_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred175_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred175_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred237_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred237_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred238_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred238_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA81 dfa81 = new DFA81(this);
    protected DFA85 dfa85 = new DFA85(this);
    protected DFA106 dfa106 = new DFA106(this);
    protected DFA114 dfa114 = new DFA114(this);
    protected DFA123 dfa123 = new DFA123(this);
    protected DFA124 dfa124 = new DFA124(this);
    protected DFA126 dfa126 = new DFA126(this);
    protected DFA127 dfa127 = new DFA127(this);
    protected DFA139 dfa139 = new DFA139(this);
    protected DFA145 dfa145 = new DFA145(this);
    protected DFA146 dfa146 = new DFA146(this);
    protected DFA149 dfa149 = new DFA149(this);
    protected DFA151 dfa151 = new DFA151(this);
    protected DFA156 dfa156 = new DFA156(this);
    protected DFA155 dfa155 = new DFA155(this);
    protected DFA162 dfa162 = new DFA162(this);
    static final String DFA8_eotS =
        "\21\uffff";
    static final String DFA8_eofS =
        "\1\2\20\uffff";
    static final String DFA8_minS =
        "\1\5\1\0\17\uffff";
    static final String DFA8_maxS =
        "\1\111\1\0\17\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\15\uffff\1\1";
    static final String DFA8_specialS =
        "\1\uffff\1\0\17\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\2\23\uffff\4\2\2\uffff\7\2\10\uffff\1\2\32\uffff\1\1",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "340:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA8_1 = input.LA(1);

                         
                        int index8_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_Java()) ) {s = 16;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index8_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA81_eotS =
        "\57\uffff";
    static final String DFA81_eofS =
        "\57\uffff";
    static final String DFA81_minS =
        "\1\4\1\uffff\15\0\40\uffff";
    static final String DFA81_maxS =
        "\1\161\1\uffff\15\0\40\uffff";
    static final String DFA81_acceptS =
        "\1\uffff\1\1\15\uffff\1\2\37\uffff";
    static final String DFA81_specialS =
        "\2\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\40\uffff}>";
    static final String[] DFA81_transitionS = {
            "\1\14\1\17\1\6\1\7\1\10\3\5\1\17\15\uffff\1\17\1\uffff\1\17"+
            "\2\uffff\7\17\2\uffff\1\1\3\uffff\3\17\1\16\5\uffff\1\17\2\uffff"+
            "\10\15\1\uffff\1\4\1\3\2\uffff\1\2\1\12\2\11\1\17\2\uffff\1"+
            "\17\1\uffff\4\17\1\uffff\5\17\21\uffff\2\17\2\uffff\4\17\1\13",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA81_eot = DFA.unpackEncodedString(DFA81_eotS);
    static final short[] DFA81_eof = DFA.unpackEncodedString(DFA81_eofS);
    static final char[] DFA81_min = DFA.unpackEncodedStringToUnsignedChars(DFA81_minS);
    static final char[] DFA81_max = DFA.unpackEncodedStringToUnsignedChars(DFA81_maxS);
    static final short[] DFA81_accept = DFA.unpackEncodedString(DFA81_acceptS);
    static final short[] DFA81_special = DFA.unpackEncodedString(DFA81_specialS);
    static final short[][] DFA81_transition;

    static {
        int numStates = DFA81_transitionS.length;
        DFA81_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA81_transition[i] = DFA.unpackEncodedString(DFA81_transitionS[i]);
        }
    }

    class DFA81 extends DFA {

        public DFA81(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 81;
            this.eot = DFA81_eot;
            this.eof = DFA81_eof;
            this.min = DFA81_min;
            this.max = DFA81_max;
            this.accept = DFA81_accept;
            this.special = DFA81_special;
            this.transition = DFA81_transition;
        }
        public String getDescription() {
            return "710:13: ( explicitConstructorInvocation )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA81_2 = input.LA(1);

                         
                        int index81_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA81_3 = input.LA(1);

                         
                        int index81_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA81_4 = input.LA(1);

                         
                        int index81_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA81_5 = input.LA(1);

                         
                        int index81_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA81_6 = input.LA(1);

                         
                        int index81_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA81_7 = input.LA(1);

                         
                        int index81_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA81_8 = input.LA(1);

                         
                        int index81_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA81_9 = input.LA(1);

                         
                        int index81_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA81_10 = input.LA(1);

                         
                        int index81_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA81_11 = input.LA(1);

                         
                        int index81_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA81_12 = input.LA(1);

                         
                        int index81_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA81_13 = input.LA(1);

                         
                        int index81_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_13);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA81_14 = input.LA(1);

                         
                        int index81_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_14);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 81, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA85_eotS =
        "\17\uffff";
    static final String DFA85_eofS =
        "\17\uffff";
    static final String DFA85_minS =
        "\1\4\1\uffff\1\0\1\uffff\1\0\12\uffff";
    static final String DFA85_maxS =
        "\1\161\1\uffff\1\0\1\uffff\1\0\12\uffff";
    static final String DFA85_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\13\uffff";
    static final String DFA85_specialS =
        "\2\uffff\1\0\1\uffff\1\1\12\uffff}>";
    static final String[] DFA85_transitionS = {
            "\1\3\1\uffff\6\3\34\uffff\1\1\6\uffff\1\3\10\uffff\10\3\1\uffff"+
            "\1\4\1\3\2\uffff\1\2\3\3\50\uffff\1\3",
            "",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA85_eot = DFA.unpackEncodedString(DFA85_eotS);
    static final short[] DFA85_eof = DFA.unpackEncodedString(DFA85_eofS);
    static final char[] DFA85_min = DFA.unpackEncodedStringToUnsignedChars(DFA85_minS);
    static final char[] DFA85_max = DFA.unpackEncodedStringToUnsignedChars(DFA85_maxS);
    static final short[] DFA85_accept = DFA.unpackEncodedString(DFA85_acceptS);
    static final short[] DFA85_special = DFA.unpackEncodedString(DFA85_specialS);
    static final short[][] DFA85_transition;

    static {
        int numStates = DFA85_transitionS.length;
        DFA85_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA85_transition[i] = DFA.unpackEncodedString(DFA85_transitionS[i]);
        }
    }

    class DFA85 extends DFA {

        public DFA85(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 85;
            this.eot = DFA85_eot;
            this.eof = DFA85_eof;
            this.min = DFA85_min;
            this.max = DFA85_max;
            this.accept = DFA85_accept;
            this.special = DFA85_special;
            this.transition = DFA85_transition;
        }
        public String getDescription() {
            return "713:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA85_2 = input.LA(1);

                         
                        int index85_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred112_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index85_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA85_4 = input.LA(1);

                         
                        int index85_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred112_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index85_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 85, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA106_eotS =
        "\56\uffff";
    static final String DFA106_eofS =
        "\56\uffff";
    static final String DFA106_minS =
        "\1\4\4\0\51\uffff";
    static final String DFA106_maxS =
        "\1\161\4\0\51\uffff";
    static final String DFA106_acceptS =
        "\5\uffff\1\2\10\uffff\1\3\36\uffff\1\1";
    static final String DFA106_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\51\uffff}>";
    static final String[] DFA106_transitionS = {
            "\1\3\1\5\7\16\15\uffff\1\16\1\uffff\1\5\2\uffff\4\5\1\1\2\5"+
            "\6\uffff\1\16\1\uffff\1\5\1\16\5\uffff\1\16\2\uffff\10\4\1\uffff"+
            "\2\16\2\uffff\4\16\1\2\2\uffff\1\16\1\uffff\4\16\1\uffff\5\16"+
            "\21\uffff\2\16\2\uffff\5\16",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA106_eot = DFA.unpackEncodedString(DFA106_eotS);
    static final short[] DFA106_eof = DFA.unpackEncodedString(DFA106_eofS);
    static final char[] DFA106_min = DFA.unpackEncodedStringToUnsignedChars(DFA106_minS);
    static final char[] DFA106_max = DFA.unpackEncodedStringToUnsignedChars(DFA106_maxS);
    static final short[] DFA106_accept = DFA.unpackEncodedString(DFA106_acceptS);
    static final short[] DFA106_special = DFA.unpackEncodedString(DFA106_specialS);
    static final short[][] DFA106_transition;

    static {
        int numStates = DFA106_transitionS.length;
        DFA106_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA106_transition[i] = DFA.unpackEncodedString(DFA106_transitionS[i]);
        }
    }

    class DFA106 extends DFA {

        public DFA106(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 106;
            this.eot = DFA106_eot;
            this.eof = DFA106_eof;
            this.min = DFA106_min;
            this.max = DFA106_max;
            this.accept = DFA106_accept;
            this.special = DFA106_special;
            this.transition = DFA106_transition;
        }
        public String getDescription() {
            return "818:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA106_1 = input.LA(1);

                         
                        int index106_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 45;}

                        else if ( (synpred147_Java()) ) {s = 5;}

                         
                        input.seek(index106_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA106_2 = input.LA(1);

                         
                        int index106_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 45;}

                        else if ( (synpred147_Java()) ) {s = 5;}

                         
                        input.seek(index106_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA106_3 = input.LA(1);

                         
                        int index106_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 45;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index106_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA106_4 = input.LA(1);

                         
                        int index106_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 45;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index106_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 106, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA114_eotS =
        "\22\uffff";
    static final String DFA114_eofS =
        "\22\uffff";
    static final String DFA114_minS =
        "\1\4\17\uffff\1\32\1\uffff";
    static final String DFA114_maxS =
        "\1\161\17\uffff\1\156\1\uffff";
    static final String DFA114_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\uffff\1\20";
    static final String DFA114_specialS =
        "\22\uffff}>";
    static final String[] DFA114_transitionS = {
            "\1\20\1\uffff\6\17\1\2\15\uffff\1\16\21\uffff\1\1\2\uffff\1"+
            "\17\5\uffff\1\11\2\uffff\10\17\1\uffff\2\17\2\uffff\4\17\3\uffff"+
            "\1\3\1\uffff\1\4\1\5\1\6\1\7\1\uffff\1\10\1\12\1\13\1\14\1\15"+
            "\21\uffff\2\17\2\uffff\5\17",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\17\2\uffff\2\17\11\uffff\1\17\1\uffff\2\17\4\uffff\1\17"+
            "\2\uffff\1\17\14\uffff\1\17\1\uffff\1\17\10\uffff\1\21\16\uffff"+
            "\25\17",
            ""
    };

    static final short[] DFA114_eot = DFA.unpackEncodedString(DFA114_eotS);
    static final short[] DFA114_eof = DFA.unpackEncodedString(DFA114_eofS);
    static final char[] DFA114_min = DFA.unpackEncodedStringToUnsignedChars(DFA114_minS);
    static final char[] DFA114_max = DFA.unpackEncodedStringToUnsignedChars(DFA114_maxS);
    static final short[] DFA114_accept = DFA.unpackEncodedString(DFA114_acceptS);
    static final short[] DFA114_special = DFA.unpackEncodedString(DFA114_specialS);
    static final short[][] DFA114_transition;

    static {
        int numStates = DFA114_transitionS.length;
        DFA114_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA114_transition[i] = DFA.unpackEncodedString(DFA114_transitionS[i]);
        }
    }

    class DFA114 extends DFA {

        public DFA114(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 114;
            this.eot = DFA114_eot;
            this.eof = DFA114_eof;
            this.min = DFA114_min;
            this.max = DFA114_max;
            this.accept = DFA114_accept;
            this.special = DFA114_special;
            this.transition = DFA114_transition;
        }
        public String getDescription() {
            return "836:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );";
        }
    }
    static final String DFA123_eotS =
        "\u0087\uffff";
    static final String DFA123_eofS =
        "\u0087\uffff";
    static final String DFA123_minS =
        "\5\4\22\uffff\7\4\4\uffff\1\4\24\uffff\1\32\1\61\1\uffff\1\32\22"+
        "\0\5\uffff\1\0\25\uffff\3\0\26\uffff\1\0\5\uffff";
    static final String DFA123_maxS =
        "\1\161\1\111\1\4\1\156\1\60\22\uffff\2\60\1\111\1\4\1\111\2\161"+
        "\4\uffff\1\161\24\uffff\1\113\1\61\1\uffff\1\113\22\0\5\uffff\1"+
        "\0\25\uffff\3\0\26\uffff\1\0\5\uffff";
    static final String DFA123_acceptS =
        "\5\uffff\1\2\166\uffff\1\1\12\uffff";
    static final String DFA123_specialS =
        "\73\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\5\uffff\1\22\25\uffff\1\23\1\24\1\25"+
        "\26\uffff\1\26\5\uffff}>";
    static final String[] DFA123_transitionS = {
            "\1\3\1\uffff\6\5\16\uffff\1\5\10\uffff\1\1\13\uffff\1\5\10\uffff"+
            "\10\4\1\uffff\2\5\2\uffff\4\5\1\2\37\uffff\2\5\2\uffff\5\5",
            "\1\27\36\uffff\1\31\24\uffff\10\30\11\uffff\1\32",
            "\1\33",
            "\1\67\25\uffff\1\5\2\uffff\1\34\1\5\11\uffff\1\42\3\5\4\uffff"+
            "\1\35\2\uffff\1\5\14\uffff\1\5\1\uffff\1\5\27\uffff\25\5",
            "\1\72\30\uffff\1\5\22\uffff\1\70",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\76\30\uffff\1\74\12\uffff\1\73\7\uffff\1\75",
            "\1\100\53\uffff\1\77",
            "\1\101\36\uffff\1\103\24\uffff\10\102\11\uffff\1\104",
            "\1\105",
            "\1\110\30\uffff\1\106\5\uffff\1\112\24\uffff\10\111\2\uffff"+
            "\1\107\6\uffff\1\113",
            "\1\114\40\uffff\1\5\2\uffff\1\5\30\uffff\1\5\3\uffff\1\5\53"+
            "\uffff\1\5",
            "\1\5\1\uffff\6\5\43\uffff\1\5\1\uffff\1\122\6\uffff\10\5\1"+
            "\uffff\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "",
            "",
            "",
            "",
            "\1\150\1\uffff\6\5\34\uffff\1\5\6\uffff\1\5\3\uffff\1\5\4\uffff"+
            "\10\151\1\152\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\5\16\uffff\1\5\6\uffff\1\5\2\uffff\1\5\27\uffff\1\174",
            "\1\u0081",
            "",
            "\1\5\16\uffff\1\5\6\uffff\1\5\2\uffff\1\5\27\uffff\1\174",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA123_eot = DFA.unpackEncodedString(DFA123_eotS);
    static final short[] DFA123_eof = DFA.unpackEncodedString(DFA123_eofS);
    static final char[] DFA123_min = DFA.unpackEncodedStringToUnsignedChars(DFA123_minS);
    static final char[] DFA123_max = DFA.unpackEncodedStringToUnsignedChars(DFA123_maxS);
    static final short[] DFA123_accept = DFA.unpackEncodedString(DFA123_acceptS);
    static final short[] DFA123_special = DFA.unpackEncodedString(DFA123_specialS);
    static final short[][] DFA123_transition;

    static {
        int numStates = DFA123_transitionS.length;
        DFA123_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA123_transition[i] = DFA.unpackEncodedString(DFA123_transitionS[i]);
        }
    }

    class DFA123 extends DFA {

        public DFA123(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 123;
            this.eot = DFA123_eot;
            this.eof = DFA123_eof;
            this.min = DFA123_min;
            this.max = DFA123_max;
            this.accept = DFA123_accept;
            this.special = DFA123_special;
            this.transition = DFA123_transition;
        }
        public String getDescription() {
            return "889:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA123_59 = input.LA(1);

                         
                        int index123_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_59);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA123_60 = input.LA(1);

                         
                        int index123_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_60);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA123_61 = input.LA(1);

                         
                        int index123_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_61);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA123_62 = input.LA(1);

                         
                        int index123_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_62);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA123_63 = input.LA(1);

                         
                        int index123_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_63);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA123_64 = input.LA(1);

                         
                        int index123_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_64);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA123_65 = input.LA(1);

                         
                        int index123_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_65);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA123_66 = input.LA(1);

                         
                        int index123_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_66);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA123_67 = input.LA(1);

                         
                        int index123_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_67);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA123_68 = input.LA(1);

                         
                        int index123_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_68);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA123_69 = input.LA(1);

                         
                        int index123_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_69);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA123_70 = input.LA(1);

                         
                        int index123_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_70);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA123_71 = input.LA(1);

                         
                        int index123_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_71);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA123_72 = input.LA(1);

                         
                        int index123_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_72);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA123_73 = input.LA(1);

                         
                        int index123_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_73);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA123_74 = input.LA(1);

                         
                        int index123_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_74);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA123_75 = input.LA(1);

                         
                        int index123_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_75);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA123_76 = input.LA(1);

                         
                        int index123_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_76);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA123_82 = input.LA(1);

                         
                        int index123_82 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_82);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA123_104 = input.LA(1);

                         
                        int index123_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_104);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA123_105 = input.LA(1);

                         
                        int index123_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_105);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA123_106 = input.LA(1);

                         
                        int index123_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_106);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA123_129 = input.LA(1);

                         
                        int index123_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_129);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 123, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA124_eotS =
        "\26\uffff";
    static final String DFA124_eofS =
        "\26\uffff";
    static final String DFA124_minS =
        "\1\4\2\uffff\2\0\21\uffff";
    static final String DFA124_maxS =
        "\1\161\2\uffff\2\0\21\uffff";
    static final String DFA124_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\20\uffff";
    static final String DFA124_specialS =
        "\3\uffff\1\0\1\1\21\uffff}>";
    static final String[] DFA124_transitionS = {
            "\1\3\1\uffff\6\5\27\uffff\1\1\13\uffff\1\5\10\uffff\10\4\1\uffff"+
            "\2\5\2\uffff\4\5\1\1\37\uffff\2\5\2\uffff\5\5",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA124_eot = DFA.unpackEncodedString(DFA124_eotS);
    static final short[] DFA124_eof = DFA.unpackEncodedString(DFA124_eofS);
    static final char[] DFA124_min = DFA.unpackEncodedStringToUnsignedChars(DFA124_minS);
    static final char[] DFA124_max = DFA.unpackEncodedStringToUnsignedChars(DFA124_maxS);
    static final short[] DFA124_accept = DFA.unpackEncodedString(DFA124_acceptS);
    static final short[] DFA124_special = DFA.unpackEncodedString(DFA124_specialS);
    static final short[][] DFA124_transition;

    static {
        int numStates = DFA124_transitionS.length;
        DFA124_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA124_transition[i] = DFA.unpackEncodedString(DFA124_transitionS[i]);
        }
    }

    class DFA124 extends DFA {

        public DFA124(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 124;
            this.eot = DFA124_eot;
            this.eof = DFA124_eof;
            this.min = DFA124_min;
            this.max = DFA124_max;
            this.accept = DFA124_accept;
            this.special = DFA124_special;
            this.transition = DFA124_transition;
        }
        public String getDescription() {
            return "895:1: forInit : ( localVariableDeclaration | expressionList );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA124_3 = input.LA(1);

                         
                        int index124_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred181_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA124_4 = input.LA(1);

                         
                        int index124_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred181_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 124, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA126_eotS =
        "\16\uffff";
    static final String DFA126_eofS =
        "\1\14\15\uffff";
    static final String DFA126_minS =
        "\1\32\13\0\2\uffff";
    static final String DFA126_maxS =
        "\1\141\13\0\2\uffff";
    static final String DFA126_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA126_specialS =
        "\1\uffff\1\12\1\1\1\4\1\5\1\7\1\3\1\0\1\6\1\10\1\11\1\2\2\uffff}>";
    static final String[] DFA126_transitionS = {
            "\1\14\15\uffff\1\12\1\14\1\13\2\uffff\1\14\3\uffff\1\14\1\uffff"+
            "\1\1\17\uffff\1\14\7\uffff\1\14\16\uffff\1\2\1\3\1\4\1\5\1\6"+
            "\1\7\1\10\1\11",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA126_eot = DFA.unpackEncodedString(DFA126_eotS);
    static final short[] DFA126_eof = DFA.unpackEncodedString(DFA126_eofS);
    static final char[] DFA126_min = DFA.unpackEncodedStringToUnsignedChars(DFA126_minS);
    static final char[] DFA126_max = DFA.unpackEncodedStringToUnsignedChars(DFA126_maxS);
    static final short[] DFA126_accept = DFA.unpackEncodedString(DFA126_acceptS);
    static final short[] DFA126_special = DFA.unpackEncodedString(DFA126_specialS);
    static final short[][] DFA126_transition;

    static {
        int numStates = DFA126_transitionS.length;
        DFA126_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA126_transition[i] = DFA.unpackEncodedString(DFA126_transitionS[i]);
        }
    }

    class DFA126 extends DFA {

        public DFA126(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 126;
            this.eot = DFA126_eot;
            this.eof = DFA126_eof;
            this.min = DFA126_min;
            this.max = DFA126_max;
            this.accept = DFA126_accept;
            this.special = DFA126_special;
            this.transition = DFA126_transition;
        }
        public String getDescription() {
            return "927:31: ( assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA126_7 = input.LA(1);

                         
                        int index126_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_7);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA126_2 = input.LA(1);

                         
                        int index126_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA126_11 = input.LA(1);

                         
                        int index126_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_11);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA126_6 = input.LA(1);

                         
                        int index126_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA126_3 = input.LA(1);

                         
                        int index126_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_3);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA126_4 = input.LA(1);

                         
                        int index126_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_4);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA126_8 = input.LA(1);

                         
                        int index126_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA126_5 = input.LA(1);

                         
                        int index126_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_5);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA126_9 = input.LA(1);

                         
                        int index126_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA126_10 = input.LA(1);

                         
                        int index126_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA126_1 = input.LA(1);

                         
                        int index126_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 126, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA127_eotS =
        "\17\uffff";
    static final String DFA127_eofS =
        "\17\uffff";
    static final String DFA127_minS =
        "\1\50\12\uffff\2\52\2\uffff";
    static final String DFA127_maxS =
        "\1\141\12\uffff\1\52\1\63\2\uffff";
    static final String DFA127_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA127_specialS =
        "\1\1\13\uffff\1\0\2\uffff}>";
    static final String[] DFA127_transitionS = {
            "\1\12\1\uffff\1\13\10\uffff\1\1\46\uffff\1\2\1\3\1\4\1\5\1\6"+
            "\1\7\1\10\1\11",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\15\10\uffff\1\16",
            "",
            ""
    };

    static final short[] DFA127_eot = DFA.unpackEncodedString(DFA127_eotS);
    static final short[] DFA127_eof = DFA.unpackEncodedString(DFA127_eofS);
    static final char[] DFA127_min = DFA.unpackEncodedStringToUnsignedChars(DFA127_minS);
    static final char[] DFA127_max = DFA.unpackEncodedStringToUnsignedChars(DFA127_maxS);
    static final short[] DFA127_accept = DFA.unpackEncodedString(DFA127_acceptS);
    static final short[] DFA127_special = DFA.unpackEncodedString(DFA127_specialS);
    static final short[][] DFA127_transition;

    static {
        int numStates = DFA127_transitionS.length;
        DFA127_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA127_transition[i] = DFA.unpackEncodedString(DFA127_transitionS[i]);
        }
    }

    class DFA127 extends DFA {

        public DFA127(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 127;
            this.eot = DFA127_eot;
            this.eof = DFA127_eof;
            this.min = DFA127_min;
            this.max = DFA127_max;
            this.accept = DFA127_accept;
            this.special = DFA127_special;
            this.transition = DFA127_transition;
        }
        public String getDescription() {
            return "930:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA127_12 = input.LA(1);

                         
                        int index127_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA127_12==42) && (synpred194_Java())) {s = 13;}

                        else if ( (LA127_12==51) && (synpred195_Java())) {s = 14;}

                         
                        input.seek(index127_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA127_0 = input.LA(1);

                         
                        int index127_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA127_0==51) ) {s = 1;}

                        else if ( (LA127_0==90) ) {s = 2;}

                        else if ( (LA127_0==91) ) {s = 3;}

                        else if ( (LA127_0==92) ) {s = 4;}

                        else if ( (LA127_0==93) ) {s = 5;}

                        else if ( (LA127_0==94) ) {s = 6;}

                        else if ( (LA127_0==95) ) {s = 7;}

                        else if ( (LA127_0==96) ) {s = 8;}

                        else if ( (LA127_0==97) ) {s = 9;}

                        else if ( (LA127_0==40) && (synpred193_Java())) {s = 10;}

                        else if ( (LA127_0==42) ) {s = 11;}

                         
                        input.seek(index127_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 127, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA139_eotS =
        "\30\uffff";
    static final String DFA139_eofS =
        "\30\uffff";
    static final String DFA139_minS =
        "\1\50\1\uffff\1\52\1\4\24\uffff";
    static final String DFA139_maxS =
        "\1\52\1\uffff\1\52\1\161\24\uffff";
    static final String DFA139_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\23\3";
    static final String DFA139_specialS =
        "\1\1\2\uffff\1\0\24\uffff}>";
    static final String[] DFA139_transitionS = {
            "\1\1\1\uffff\1\2",
            "",
            "\1\3",
            "\1\25\1\uffff\1\17\1\20\1\21\3\16\36\uffff\1\4\4\uffff\1\27"+
            "\10\uffff\10\26\1\uffff\1\15\1\13\2\uffff\1\14\1\23\2\22\40"+
            "\uffff\1\5\1\6\2\uffff\1\7\1\10\1\11\1\12\1\24",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA139_eot = DFA.unpackEncodedString(DFA139_eotS);
    static final short[] DFA139_eof = DFA.unpackEncodedString(DFA139_eofS);
    static final char[] DFA139_min = DFA.unpackEncodedStringToUnsignedChars(DFA139_minS);
    static final char[] DFA139_max = DFA.unpackEncodedStringToUnsignedChars(DFA139_maxS);
    static final short[] DFA139_accept = DFA.unpackEncodedString(DFA139_acceptS);
    static final short[] DFA139_special = DFA.unpackEncodedString(DFA139_specialS);
    static final short[][] DFA139_transition;

    static {
        int numStates = DFA139_transitionS.length;
        DFA139_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA139_transition[i] = DFA.unpackEncodedString(DFA139_transitionS[i]);
        }
    }

    class DFA139 extends DFA {

        public DFA139(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 139;
            this.eot = DFA139_eot;
            this.eof = DFA139_eof;
            this.min = DFA139_min;
            this.max = DFA139_max;
            this.accept = DFA139_accept;
            this.special = DFA139_special;
            this.transition = DFA139_transition;
        }
        public String getDescription() {
            return "1010:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA139_3 = input.LA(1);

                         
                        int index139_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA139_3==42) && (synpred211_Java())) {s = 4;}

                        else if ( (LA139_3==105) && (synpred212_Java())) {s = 5;}

                        else if ( (LA139_3==106) && (synpred212_Java())) {s = 6;}

                        else if ( (LA139_3==109) && (synpred212_Java())) {s = 7;}

                        else if ( (LA139_3==110) && (synpred212_Java())) {s = 8;}

                        else if ( (LA139_3==111) && (synpred212_Java())) {s = 9;}

                        else if ( (LA139_3==112) && (synpred212_Java())) {s = 10;}

                        else if ( (LA139_3==66) && (synpred212_Java())) {s = 11;}

                        else if ( (LA139_3==69) && (synpred212_Java())) {s = 12;}

                        else if ( (LA139_3==65) && (synpred212_Java())) {s = 13;}

                        else if ( ((LA139_3>=HexLiteral && LA139_3<=DecimalLiteral)) && (synpred212_Java())) {s = 14;}

                        else if ( (LA139_3==FloatingPointLiteral) && (synpred212_Java())) {s = 15;}

                        else if ( (LA139_3==CharacterLiteral) && (synpred212_Java())) {s = 16;}

                        else if ( (LA139_3==StringLiteral) && (synpred212_Java())) {s = 17;}

                        else if ( ((LA139_3>=71 && LA139_3<=72)) && (synpred212_Java())) {s = 18;}

                        else if ( (LA139_3==70) && (synpred212_Java())) {s = 19;}

                        else if ( (LA139_3==113) && (synpred212_Java())) {s = 20;}

                        else if ( (LA139_3==Identifier) && (synpred212_Java())) {s = 21;}

                        else if ( ((LA139_3>=56 && LA139_3<=63)) && (synpred212_Java())) {s = 22;}

                        else if ( (LA139_3==47) && (synpred212_Java())) {s = 23;}

                         
                        input.seek(index139_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA139_0 = input.LA(1);

                         
                        int index139_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA139_0==40) && (synpred210_Java())) {s = 1;}

                        else if ( (LA139_0==42) ) {s = 2;}

                         
                        input.seek(index139_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 139, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA145_eotS =
        "\21\uffff";
    static final String DFA145_eofS =
        "\21\uffff";
    static final String DFA145_minS =
        "\1\4\2\uffff\1\0\15\uffff";
    static final String DFA145_maxS =
        "\1\161\2\uffff\1\0\15\uffff";
    static final String DFA145_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\13\uffff\1\3";
    static final String DFA145_specialS =
        "\3\uffff\1\0\15\uffff}>";
    static final String[] DFA145_transitionS = {
            "\1\4\1\uffff\6\4\43\uffff\1\4\10\uffff\10\4\1\uffff\1\4\1\3"+
            "\2\uffff\4\4\46\uffff\1\1\1\2\1\4",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA145_eot = DFA.unpackEncodedString(DFA145_eotS);
    static final short[] DFA145_eof = DFA.unpackEncodedString(DFA145_eofS);
    static final char[] DFA145_min = DFA.unpackEncodedStringToUnsignedChars(DFA145_minS);
    static final char[] DFA145_max = DFA.unpackEncodedStringToUnsignedChars(DFA145_maxS);
    static final short[] DFA145_accept = DFA.unpackEncodedString(DFA145_acceptS);
    static final short[] DFA145_special = DFA.unpackEncodedString(DFA145_specialS);
    static final short[][] DFA145_transition;

    static {
        int numStates = DFA145_transitionS.length;
        DFA145_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA145_transition[i] = DFA.unpackEncodedString(DFA145_transitionS[i]);
        }
    }

    class DFA145 extends DFA {

        public DFA145(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 145;
            this.eot = DFA145_eot;
            this.eof = DFA145_eof;
            this.min = DFA145_min;
            this.max = DFA145_max;
            this.accept = DFA145_accept;
            this.special = DFA145_special;
            this.transition = DFA145_transition;
        }
        public String getDescription() {
            return "1041:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA145_3 = input.LA(1);

                         
                        int index145_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred224_Java()) ) {s = 16;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index145_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 145, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA146_eotS =
        "\7\uffff";
    static final String DFA146_eofS =
        "\7\uffff";
    static final String DFA146_minS =
        "\1\4\1\0\1\35\2\uffff\1\61\1\35";
    static final String DFA146_maxS =
        "\1\161\1\0\1\103\2\uffff\1\61\1\103";
    static final String DFA146_acceptS =
        "\3\uffff\1\2\1\1\2\uffff";
    static final String DFA146_specialS =
        "\1\uffff\1\0\5\uffff}>";
    static final String[] DFA146_transitionS = {
            "\1\1\1\uffff\6\3\43\uffff\1\3\10\uffff\10\2\1\uffff\2\3\2\uffff"+
            "\4\3\40\uffff\2\3\2\uffff\5\3",
            "\1\uffff",
            "\1\3\22\uffff\1\5\22\uffff\1\4",
            "",
            "",
            "\1\6",
            "\1\3\22\uffff\1\5\22\uffff\1\4"
    };

    static final short[] DFA146_eot = DFA.unpackEncodedString(DFA146_eotS);
    static final short[] DFA146_eof = DFA.unpackEncodedString(DFA146_eofS);
    static final char[] DFA146_min = DFA.unpackEncodedStringToUnsignedChars(DFA146_minS);
    static final char[] DFA146_max = DFA.unpackEncodedStringToUnsignedChars(DFA146_maxS);
    static final short[] DFA146_accept = DFA.unpackEncodedString(DFA146_acceptS);
    static final short[] DFA146_special = DFA.unpackEncodedString(DFA146_specialS);
    static final short[][] DFA146_transition;

    static {
        int numStates = DFA146_transitionS.length;
        DFA146_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA146_transition[i] = DFA.unpackEncodedString(DFA146_transitionS[i]);
        }
    }

    class DFA146 extends DFA {

        public DFA146(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 146;
            this.eot = DFA146_eot;
            this.eof = DFA146_eof;
            this.min = DFA146_min;
            this.max = DFA146_max;
            this.accept = DFA146_accept;
            this.special = DFA146_special;
            this.transition = DFA146_transition;
        }
        public String getDescription() {
            return "1050:12: ( type | expression )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA146_1 = input.LA(1);

                         
                        int index146_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred229_Java()) ) {s = 4;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 146, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA149_eotS =
        "\41\uffff";
    static final String DFA149_eofS =
        "\1\4\40\uffff";
    static final String DFA149_minS =
        "\1\32\1\0\1\uffff\1\0\35\uffff";
    static final String DFA149_maxS =
        "\1\156\1\0\1\uffff\1\0\35\uffff";
    static final String DFA149_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\34\uffff";
    static final String DFA149_specialS =
        "\1\uffff\1\0\1\uffff\1\1\35\uffff}>";
    static final String[] DFA149_transitionS = {
            "\1\4\2\uffff\1\3\1\4\11\uffff\4\4\1\uffff\1\4\2\uffff\1\1\1"+
            "\4\1\uffff\1\4\14\uffff\1\4\1\uffff\1\2\1\4\7\uffff\1\4\16\uffff"+
            "\25\4",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA149_eot = DFA.unpackEncodedString(DFA149_eotS);
    static final short[] DFA149_eof = DFA.unpackEncodedString(DFA149_eofS);
    static final char[] DFA149_min = DFA.unpackEncodedStringToUnsignedChars(DFA149_minS);
    static final char[] DFA149_max = DFA.unpackEncodedStringToUnsignedChars(DFA149_maxS);
    static final short[] DFA149_accept = DFA.unpackEncodedString(DFA149_acceptS);
    static final short[] DFA149_special = DFA.unpackEncodedString(DFA149_specialS);
    static final short[][] DFA149_transition;

    static {
        int numStates = DFA149_transitionS.length;
        DFA149_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA149_transition[i] = DFA.unpackEncodedString(DFA149_transitionS[i]);
        }
    }

    class DFA149 extends DFA {

        public DFA149(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 149;
            this.eot = DFA149_eot;
            this.eof = DFA149_eof;
            this.min = DFA149_min;
            this.max = DFA149_max;
            this.accept = DFA149_accept;
            this.special = DFA149_special;
            this.transition = DFA149_transition;
        }
        public String getDescription() {
            return "1055:34: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA149_1 = input.LA(1);

                         
                        int index149_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred232_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index149_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA149_3 = input.LA(1);

                         
                        int index149_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred232_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index149_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 149, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA151_eotS =
        "\41\uffff";
    static final String DFA151_eofS =
        "\1\4\40\uffff";
    static final String DFA151_minS =
        "\1\32\1\0\1\uffff\1\0\35\uffff";
    static final String DFA151_maxS =
        "\1\156\1\0\1\uffff\1\0\35\uffff";
    static final String DFA151_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\34\uffff";
    static final String DFA151_specialS =
        "\1\uffff\1\0\1\uffff\1\1\35\uffff}>";
    static final String[] DFA151_transitionS = {
            "\1\4\2\uffff\1\3\1\4\11\uffff\4\4\1\uffff\1\4\2\uffff\1\1\1"+
            "\4\1\uffff\1\4\14\uffff\1\4\1\uffff\1\2\1\4\7\uffff\1\4\16\uffff"+
            "\25\4",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA151_eot = DFA.unpackEncodedString(DFA151_eotS);
    static final short[] DFA151_eof = DFA.unpackEncodedString(DFA151_eofS);
    static final char[] DFA151_min = DFA.unpackEncodedStringToUnsignedChars(DFA151_minS);
    static final char[] DFA151_max = DFA.unpackEncodedStringToUnsignedChars(DFA151_maxS);
    static final short[] DFA151_accept = DFA.unpackEncodedString(DFA151_acceptS);
    static final short[] DFA151_special = DFA.unpackEncodedString(DFA151_specialS);
    static final short[][] DFA151_transition;

    static {
        int numStates = DFA151_transitionS.length;
        DFA151_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA151_transition[i] = DFA.unpackEncodedString(DFA151_transitionS[i]);
        }
    }

    class DFA151 extends DFA {

        public DFA151(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 151;
            this.eot = DFA151_eot;
            this.eof = DFA151_eof;
            this.min = DFA151_min;
            this.max = DFA151_max;
            this.accept = DFA151_accept;
            this.special = DFA151_special;
            this.transition = DFA151_transition;
        }
        public String getDescription() {
            return "1059:38: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA151_1 = input.LA(1);

                         
                        int index151_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index151_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA151_3 = input.LA(1);

                         
                        int index151_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index151_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 151, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA156_eotS =
        "\13\uffff";
    static final String DFA156_eofS =
        "\13\uffff";
    static final String DFA156_minS =
        "\1\35\1\4\1\uffff\1\45\7\uffff";
    static final String DFA156_maxS =
        "\1\102\1\161\1\uffff\1\161\7\uffff";
    static final String DFA156_acceptS =
        "\2\uffff\1\3\1\uffff\1\1\1\2\1\4\1\6\1\7\1\10\1\5";
    static final String DFA156_specialS =
        "\13\uffff}>";
    static final String[] DFA156_transitionS = {
            "\1\3\22\uffff\1\1\21\uffff\1\2",
            "\1\5\1\uffff\6\5\43\uffff\1\5\1\uffff\1\4\6\uffff\10\5\1\uffff"+
            "\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "",
            "\1\6\2\uffff\1\12\30\uffff\1\10\3\uffff\1\7\53\uffff\1\11",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA156_eot = DFA.unpackEncodedString(DFA156_eotS);
    static final short[] DFA156_eof = DFA.unpackEncodedString(DFA156_eofS);
    static final char[] DFA156_min = DFA.unpackEncodedStringToUnsignedChars(DFA156_minS);
    static final char[] DFA156_max = DFA.unpackEncodedStringToUnsignedChars(DFA156_maxS);
    static final short[] DFA156_accept = DFA.unpackEncodedString(DFA156_acceptS);
    static final short[] DFA156_special = DFA.unpackEncodedString(DFA156_specialS);
    static final short[][] DFA156_transition;

    static {
        int numStates = DFA156_transitionS.length;
        DFA156_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA156_transition[i] = DFA.unpackEncodedString(DFA156_transitionS[i]);
        }
    }

    class DFA156 extends DFA {

        public DFA156(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 156;
            this.eot = DFA156_eot;
            this.eof = DFA156_eof;
            this.min = DFA156_min;
            this.max = DFA156_max;
            this.accept = DFA156_accept;
            this.special = DFA156_special;
            this.transition = DFA156_transition;
        }
        public String getDescription() {
            return "1064:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );";
        }
    }
    static final String DFA155_eotS =
        "\41\uffff";
    static final String DFA155_eofS =
        "\1\1\40\uffff";
    static final String DFA155_minS =
        "\1\32\1\uffff\1\0\36\uffff";
    static final String DFA155_maxS =
        "\1\156\1\uffff\1\0\36\uffff";
    static final String DFA155_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA155_specialS =
        "\2\uffff\1\0\36\uffff}>";
    static final String[] DFA155_transitionS = {
            "\1\1\2\uffff\2\1\11\uffff\4\1\1\uffff\1\1\2\uffff\1\2\1\1\1"+
            "\uffff\1\1\14\uffff\1\1\2\uffff\1\1\7\uffff\1\1\16\uffff\25"+
            "\1",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA155_eot = DFA.unpackEncodedString(DFA155_eotS);
    static final short[] DFA155_eof = DFA.unpackEncodedString(DFA155_eofS);
    static final char[] DFA155_min = DFA.unpackEncodedStringToUnsignedChars(DFA155_minS);
    static final char[] DFA155_max = DFA.unpackEncodedStringToUnsignedChars(DFA155_maxS);
    static final short[] DFA155_accept = DFA.unpackEncodedString(DFA155_acceptS);
    static final short[] DFA155_special = DFA.unpackEncodedString(DFA155_specialS);
    static final short[][] DFA155_transition;

    static {
        int numStates = DFA155_transitionS.length;
        DFA155_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA155_transition[i] = DFA.unpackEncodedString(DFA155_transitionS[i]);
        }
    }

    class DFA155 extends DFA {

        public DFA155(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 155;
            this.eot = DFA155_eot;
            this.eof = DFA155_eof;
            this.min = DFA155_min;
            this.max = DFA155_max;
            this.accept = DFA155_accept;
            this.special = DFA155_special;
            this.transition = DFA155_transition;
        }
        public String getDescription() {
            return "()+ loopback of 1066:9: ( '[' expression ']' )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA155_2 = input.LA(1);

                         
                        int index155_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred244_Java()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index155_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 155, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA162_eotS =
        "\41\uffff";
    static final String DFA162_eofS =
        "\1\2\40\uffff";
    static final String DFA162_minS =
        "\1\32\1\0\37\uffff";
    static final String DFA162_maxS =
        "\1\156\1\0\37\uffff";
    static final String DFA162_acceptS =
        "\2\uffff\1\2\35\uffff\1\1";
    static final String DFA162_specialS =
        "\1\uffff\1\0\37\uffff}>";
    static final String[] DFA162_transitionS = {
            "\1\2\2\uffff\2\2\11\uffff\4\2\1\uffff\1\2\2\uffff\1\1\1\2\1"+
            "\uffff\1\2\14\uffff\1\2\2\uffff\1\2\7\uffff\1\2\16\uffff\25"+
            "\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA162_eot = DFA.unpackEncodedString(DFA162_eotS);
    static final short[] DFA162_eof = DFA.unpackEncodedString(DFA162_eofS);
    static final char[] DFA162_min = DFA.unpackEncodedStringToUnsignedChars(DFA162_minS);
    static final char[] DFA162_max = DFA.unpackEncodedStringToUnsignedChars(DFA162_maxS);
    static final short[] DFA162_accept = DFA.unpackEncodedString(DFA162_acceptS);
    static final short[] DFA162_special = DFA.unpackEncodedString(DFA162_specialS);
    static final short[][] DFA162_transition;

    static {
        int numStates = DFA162_transitionS.length;
        DFA162_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA162_transition[i] = DFA.unpackEncodedString(DFA162_transitionS[i]);
        }
    }

    class DFA162 extends DFA {

        public DFA162(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 162;
            this.eot = DFA162_eot;
            this.eof = DFA162_eof;
            this.min = DFA162_min;
            this.max = DFA162_max;
            this.accept = DFA162_accept;
            this.special = DFA162_special;
            this.transition = DFA162_transition;
        }
        public String getDescription() {
            return "()* loopback of 1092:28: ( '[' expression ']' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA162_1 = input.LA(1);

                         
                        int index162_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 32;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index162_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 162, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_annotations_in_compilationUnit96 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit112 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit118 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit126 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_compilationUnit145 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit151 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit177 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit185 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit193 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_25_in_packageDeclaration219 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration223 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_packageDeclaration225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_importDeclaration263 = new BitSet(new long[]{0x0000000010000010L});
    public static final BitSet FOLLOW_28_in_importDeclaration267 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_importDeclaration272 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration277 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_importDeclaration279 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_importDeclaration283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_typeDeclaration331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration363 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers425 = new BitSet(new long[]{0x0000001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotation_in_classOrInterfaceModifier452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_classOrInterfaceModifier465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_classOrInterfaceModifier481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_classOrInterfaceModifier494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_classOrInterfaceModifier509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classOrInterfaceModifier523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_classOrInterfaceModifier539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_classOrInterfaceModifier556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_modifiers591 = new BitSet(new long[]{0x00F0001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_normalClassDeclaration662 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration666 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration673 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_38_in_normalClassDeclaration687 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration691 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_39_in_normalClassDeclaration705 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration709 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeParameters759 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters763 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeParameters767 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters771 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeParameters776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter801 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_38_in_typeParameter805 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound849 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_typeBound853 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeBound857 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration887 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration891 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_39_in_enumDeclaration895 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration899 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_enumBody934 = new BitSet(new long[]{0x0000220004000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody939 = new BitSet(new long[]{0x0000220004000000L});
    public static final BitSet FOLLOW_41_in_enumBody944 = new BitSet(new long[]{0x0000200004000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody950 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_enumBody956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants981 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_enumConstants986 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants990 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant1020 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant1025 = new BitSet(new long[]{0x000011C000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_enumConstant1032 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant1041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_enumBodyDeclarations1072 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1079 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_normalInterfaceDeclaration1155 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration1159 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1166 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_38_in_normalInterfaceDeclaration1172 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1176 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_classBody_in_normalInterfaceDeclaration1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1216 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_typeList1220 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeList1224 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_44_in_classBody1255 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1262 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_classBody1268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_interfaceBody1295 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1302 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_interfaceBody1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_classBodyDeclaration1331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classBodyDeclaration1343 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classBodyDeclaration1362 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDeclaration_in_memberDecl1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_voidMethodDeclaration_in_memberDecl1425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorDeclaration_in_memberDecl1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_voidMethodDeclaration1500 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_voidMethodDeclaration1504 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_voidMethodDeclaration1508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constructorDeclaration1547 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_constructorDeclaration1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDeclaration1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDeclaration1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1619 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest1664 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_genericMethodOrConstructorRest1669 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1676 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_methodDeclaration1726 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration1730 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration1734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_fieldDeclaration1759 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration1765 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_fieldDeclaration1769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceBodyDeclaration1802 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_interfaceBodyDeclaration1818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaration_in_interfaceMemberDecl1871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl1899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_voidInterfaceMethodDeclaration1933 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_voidInterfaceMethodDeclaration1937 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_voidInterfaceMethodDeclaration1941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceConstant_in_interfaceMethodOrFieldDecl1975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethod_in_interfaceMethodOrFieldDecl1989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceConstant2025 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_interfaceConstant2031 = new BitSet(new long[]{0x0000020004000000L});
    public static final BitSet FOLLOW_41_in_interfaceConstant2035 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_interfaceConstant2039 = new BitSet(new long[]{0x0000020004000000L});
    public static final BitSet FOLLOW_26_in_interfaceConstant2045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethod2070 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethod2074 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethod2078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest2105 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_48_in_methodDeclaratorRest2110 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_methodDeclaratorRest2112 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_50_in_methodDeclaratorRest2129 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest2133 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest2153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_methodDeclaratorRest2169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest2202 = new BitSet(new long[]{0x0004100014000000L});
    public static final BitSet FOLLOW_50_in_voidMethodDeclaratorRest2205 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest2207 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest2223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_voidMethodDeclaratorRest2237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest2270 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_48_in_interfaceMethodDeclaratorRest2273 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_interfaceMethodDeclaratorRest2275 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_50_in_interfaceMethodDeclaratorRest2280 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest2282 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodDeclaratorRest2286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl2313 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl2316 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_interfaceGenericMethodDecl2320 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl2323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl2333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest2356 = new BitSet(new long[]{0x0004000004000000L});
    public static final BitSet FOLLOW_50_in_voidInterfaceMethodDeclaratorRest2359 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest2361 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_voidInterfaceMethodDeclaratorRest2365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest2388 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_50_in_constructorDeclaratorRest2391 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest2393 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_constructorBody_in_constructorDeclaratorRest2397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator2426 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_48_in_constantDeclarator2430 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_constantDeclarator2432 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_51_in_constantDeclarator2438 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclarator2442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators2482 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclarators2486 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators2490 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_variableDeclarator2519 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_variableDeclarator2524 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId2571 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_variableDeclaratorId2574 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_variableDeclaratorId2576 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_arrayInitializer2654 = new BitSet(new long[]{0xFF00B00000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2657 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer2660 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2662 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer2667 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_arrayInitializer2674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier2697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_modifier2709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier2721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier2733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier2745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_modifier2757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_packageOrTypeName2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName2797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_typeName2816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2840 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2843 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2845 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type2858 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2861 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2863 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2883 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2885 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_classOrInterfaceType2889 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2891 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2893 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_variableModifier3006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier3016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeArguments3035 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments3037 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeArguments3040 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments3042 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeArguments3046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument3069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_typeArgument3079 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument3082 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument3090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3124 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_qualifiedNameList3129 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3133 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_66_in_formalParameters3164 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000208L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3169 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_formalParameters3175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameterDecls3202 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls3204 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000010L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls3206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3229 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_formalParameterDeclsRest3232 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest3234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_formalParameterDeclsRest3246 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody3275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_constructorBody3294 = new BitSet(new long[]{0xFF20F13F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody3296 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_constructorBody3299 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_constructorBody3302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3321 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation3324 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3332 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation3334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3344 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_explicitConstructorInvocation3346 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3348 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_explicitConstructorInvocation3351 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3353 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation3355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName3375 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_qualifiedName3378 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName3380 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal3406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal3416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal3426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal3436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal3446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_literal3456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations3545 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_annotation3565 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation3567 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation3571 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation3575 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_elementValue_in_annotation3579 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotation3584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName3608 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_annotationName3611 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName3613 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3634 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_elementValuePairs3637 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3639 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair3660 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_elementValuePair3662 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue3707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_elementValueArrayInitializer3730 = new BitSet(new long[]{0xFF00B20000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3733 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer3736 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3738 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer3745 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_elementValueArrayInitializer3749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_annotationTypeDeclaration3776 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_annotationTypeDeclaration3778 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration3780 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_annotationTypeBody3805 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3808 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_annotationTypeBody3812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeElementDeclaration3835 = new BitSet(new long[]{0xFF00403F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest3860 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3862 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3874 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3887 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest3900 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3913 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest3972 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest3974 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotationMethodRest3976 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest3978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest4002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_defaultValue4025 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue4027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_block4052 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_block4054 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_block4057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement4080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement4090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement4100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4124 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_localVariableDeclarationStatement4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4145 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration4147 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration4149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_variableModifiers4172 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_block_in_statement4190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4200 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4202 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement4205 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4207 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_statement4221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4223 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4225 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_statement4235 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement4249 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement4251 = new BitSet(new long[]{0xFF00900804000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forControl_in_statement4253 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_statement4255 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement4267 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4269 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement4281 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement4285 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4287 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement4299 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4301 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_catches_in_statement4313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_statement4315 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement4329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement4343 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement4365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4367 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_statement4369 = new BitSet(new long[]{0x0000200000000000L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4371 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_statement4373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_statement4383 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4385 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement4397 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4399 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement4412 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4414 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement4426 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement4428 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_statement4441 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement4443 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_statement4456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement4467 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement4479 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement4481 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches4506 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_catchClause_in_catches4509 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_catchClause4534 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause4536 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause4538 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_catchClause4540 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_catchClause4542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter4561 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameter4563 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter4565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4593 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4620 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60002FBD7E6L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4623 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_89_in_switchLabel4647 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel4649 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_switchLabel4661 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel4663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_switchLabel4675 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_forControl4708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl4718 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl4721 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_forControl4723 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl4726 = new BitSet(new long[]{0xFF00900800000FD2L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forUpdate_in_forControl4728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit4748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit4758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_enhancedForControl4781 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_enhancedForControl4783 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enhancedForControl4785 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_enhancedForControl4787 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_enhancedForControl4789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate4808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_parExpression4829 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_parExpression4831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_parExpression4833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4856 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_expressionList4859 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expressionList4861 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression4882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression4905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression4932 = new BitSet(new long[]{0x0008050000000002L,0x00000003FC000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression4935 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expression4937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator4972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator4982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator4992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator5002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator5012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator5022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator5032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator5042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_assignmentOperator5063 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_assignmentOperator5067 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5105 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5109 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5113 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5148 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5152 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5185 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_conditionalExpression5189 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5191 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_conditionalExpression5193 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5217 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalOrExpression5221 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5223 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5245 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_conditionalAndExpression5249 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5251 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5273 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_inclusiveOrExpression5277 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5279 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5301 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_exclusiveOrExpression5305 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5307 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5329 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_andExpression5333 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5335 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5357 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression5361 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5369 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5391 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_instanceOfExpression5394 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression5396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5417 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression5421 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5423 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp5458 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp5462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp5492 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp5496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp5517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp5528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5548 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression5552 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5554 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_shiftOp5585 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_shiftOp5589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp5621 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5625 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp5659 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5693 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression5697 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5705 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5727 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression5731 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5745 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_105_in_unaryExpression5771 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_unaryExpression5783 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression5795 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpression5807 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus5838 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_unaryExpressionNotPlusMinus5850 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus5872 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus5874 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus5877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5900 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression5902 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5904 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression5906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5915 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_type_in_castExpression5918 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_castExpression5922 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5925 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary5946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_primary5956 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5959 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5961 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_primary5976 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_primary5978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary5988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary5998 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_creator_in_primary6000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary6010 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary6013 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary6015 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary6030 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_48_in_primary6033 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_primary6035 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_primary6039 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary6041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_primary6051 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_primary6053 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary6055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix6075 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix6077 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6081 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix6083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix6094 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix6096 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix6098 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6121 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix6123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6133 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix6135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6145 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_identifierSuffix6147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6157 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_identifierSuffix6159 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6171 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix6173 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix6175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator6194 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_createdName_in_creator6196 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator6198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createdName_in_creator6208 = new BitSet(new long[]{0x0001000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator6211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator6215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName6235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName6245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator6268 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator6271 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator6273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6292 = new BitSet(new long[]{0xFF02900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6306 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6309 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6311 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest6315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6329 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6331 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6334 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6336 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6338 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6343 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6345 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest6376 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest6378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6402 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocation6404 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation6406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_nonWildcardTypeArguments6429 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments6431 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_nonWildcardTypeArguments6433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6456 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector6458 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_selector6460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6471 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_selector6473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6483 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_selector6485 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_selector6487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6497 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector6499 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector6501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_selector6511 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_selector6513 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_selector6515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_superSuffix6548 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix6550 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_arguments6576 = new BitSet(new long[]{0xFF00900800000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_expressionList_in_arguments6578 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_arguments6581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred5_Java96 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred5_Java112 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_synpred5_Java118 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java126 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java145 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java151 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred53_Java1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceConstant_in_synpred61_Java1975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred81_Java2697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_synpred82_Java2709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred108_Java3296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred112_Java3321 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_synpred112_Java3324 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_synpred112_Java3332 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_synpred112_Java3334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred123_Java3545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred146_Java4080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred147_Java4090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_synpred152_Java4235 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_synpred152_Java4237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred157_Java4313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_synpred157_Java4315 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_synpred157_Java4317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred158_Java4329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchLabel_in_synpred173_Java4620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred175_Java4647 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_synpred175_Java4649 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred175_Java4651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred176_Java4661 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred176_Java4663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred176_Java4665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_synpred177_Java4708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred181_Java4748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred183_Java4935 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred183_Java4937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred193_Java5053 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred193_Java5055 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred193_Java5057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred194_Java5093 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred194_Java5095 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred194_Java5097 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred194_Java5099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred195_Java5138 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred195_Java5140 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred195_Java5142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred206_Java5450 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred206_Java5452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred207_Java5484 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred207_Java5486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred210_Java5577 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred210_Java5579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred211_Java5611 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred211_Java5613 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred211_Java5615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred212_Java5651 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred212_Java5653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred224_Java5862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_synpred228_Java5900 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred228_Java5902 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_synpred228_Java5904 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred228_Java5906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred229_Java5918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred231_Java5959 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred231_Java5961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred232_Java5965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred237_Java6013 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred237_Java6015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred238_Java6019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred244_Java6094 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred244_Java6096 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred244_Java6098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred257_Java6334 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred257_Java6336 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred257_Java6338 = new BitSet(new long[]{0x0000000000000002L});

}