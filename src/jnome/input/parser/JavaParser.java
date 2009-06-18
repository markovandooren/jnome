// $ANTLR 3.1.2 /Users/marko/git/jnome/src/jnome/input/parser/Java.g 2009-06-18 18:42:11

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

import chameleon.support.expression.RegularLiteral;

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

import chameleon.support.statement.StatementExpression;

import chameleon.support.type.EmptyTypeElement;
import chameleon.support.type.StaticInitializer;

import jnome.core.expression.ArrayInitializer;

import jnome.core.language.Java;

import jnome.core.modifier.StrictFP;
import jnome.core.modifier.Transient;
import jnome.core.modifier.Volatile;
import jnome.core.modifier.Synchronized;

import jnome.core.type.JavaTypeReference;

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:347:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:349:5: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:349:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotations_in_compilationUnit96);
                    annotations1=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit112);
                            np=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, np.getTree());
                            if ( state.backtracking==0 ) {
                              processPackageDeclaration(retval.element,np.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:90: (imp= importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==27) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:91: imp= importDeclaration
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

                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:159: (typech= typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==ENUM||LA2_0==26||LA2_0==28||(LA2_0>=31 && LA2_0<=37)||LA2_0==46||LA2_0==73) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:160: typech= typeDeclaration
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit145);
                            cd=classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                            if ( state.backtracking==0 ) {
                              processType(retval.element,cd.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:85: (typech= typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==ENUM||LA3_0==26||LA3_0==28||(LA3_0>=31 && LA3_0<=37)||LA3_0==46||LA3_0==73) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:86: typech= typeDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:9: (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )*
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:9: (np= packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==25) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:10: np= packageDeclaration
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

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:89: (imp= importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==27) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:90: imp= importDeclaration
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

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:158: (typech= typeDeclaration )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ENUM||LA7_0==26||LA7_0==28||(LA7_0>=31 && LA7_0<=37)||LA7_0==46||LA7_0==73) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:159: typech= typeDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:356:1: packageDeclaration returns [NamespacePart element] : 'package' qn= qualifiedName ';' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:5: ( 'package' qn= qualifiedName ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:9: 'package' qn= qualifiedName ';'
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:368:1: importDeclaration returns [Import element] : 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:5: ( 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:9: 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal4=(Token)match(input,27,FOLLOW_27_in_importDeclaration263); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal4_tree = (Object)adaptor.create(string_literal4);
            adaptor.addChild(root_0, string_literal4_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:20: (st= 'static' )?
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:52: (star= ( '.' '*' ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==29) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: star= ( '.' '*' )
                    {
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:53: ( '.' '*' )
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:54: '.' '*'
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:382:1: typeDeclaration returns [Type element] : (cd= classOrInterfaceDeclaration | ';' );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:383:5: (cd= classOrInterfaceDeclaration | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:383:9: cd= classOrInterfaceDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:9: ';'
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:390:1: classOrInterfaceDeclaration returns [Type element] : mods= classOrInterfaceModifiers (cd= classDeclaration | id= interfaceDeclaration ) ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:391:5: (mods= classOrInterfaceModifiers (cd= classDeclaration | id= interfaceDeclaration ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:391:9: mods= classOrInterfaceModifiers (cd= classDeclaration | id= interfaceDeclaration )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration363);
            mods=classOrInterfaceModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, mods.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:391:40: (cd= classDeclaration | id= interfaceDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:391:41: cd= classDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:391:92: id= interfaceDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:397:1: classOrInterfaceModifiers returns [List<Modifier> element] : (mod= classOrInterfaceModifier )* ;
    public final JavaParser.classOrInterfaceModifiers_return classOrInterfaceModifiers() throws RecognitionException {
        JavaParser.classOrInterfaceModifiers_return retval = new JavaParser.classOrInterfaceModifiers_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifier_return mod = null;



        retval.element = new ArrayList<Modifier>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:5: ( (mod= classOrInterfaceModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:9: (mod= classOrInterfaceModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:9: (mod= classOrInterfaceModifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:10: mod= classOrInterfaceModifier
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:1: classOrInterfaceModifier returns [Modifier element] : ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:403:5: ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:403:9: annotation
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:404:9: 'public'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:405:9: 'protected'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:9: 'private'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:407:9: 'abstract'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:408:9: 'static'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:409:9: 'final'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:410:9: 'strictfp'
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:413:1: modifiers returns [List<Modifier> element] : (mod= modifier )* ;
    public final JavaParser.modifiers_return modifiers() throws RecognitionException {
        JavaParser.modifiers_return retval = new JavaParser.modifiers_return();
        retval.start = input.LT(1);
        int modifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifier_return mod = null;



        retval.element = new ArrayList<Modifier>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:415:5: ( (mod= modifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:415:9: (mod= modifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:415:9: (mod= modifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:415:10: mod= modifier
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:1: classDeclaration returns [Type element] : (cd= normalClassDeclaration | ed= enumDeclaration );
    public final JavaParser.classDeclaration_return classDeclaration() throws RecognitionException {
        JavaParser.classDeclaration_return retval = new JavaParser.classDeclaration_return();
        retval.start = input.LT(1);
        int classDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalClassDeclaration_return cd = null;

        JavaParser.enumDeclaration_return ed = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:5: (cd= normalClassDeclaration | ed= enumDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:9: cd= normalClassDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:420:9: ed= enumDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:423:1: normalClassDeclaration returns [RegularType element] : 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' trefs= typeList )? body= classBody ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:5: ( 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' trefs= typeList )? body= classBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:9: 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' trefs= typeList )? body= classBody
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:106: (params= typeParameters )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==40) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:107: params= typeParameters
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:9: ( 'extends' sc= type )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==38) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:10: 'extends' sc= type
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:426:9: ( 'implements' trefs= typeList )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==39) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:426:10: 'implements' trefs= typeList
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:430:1: typeParameters returns [List<GenericParameter> element] : '<' par= typeParameter ( ',' par= typeParameter )* '>' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:432:5: ( '<' par= typeParameter ( ',' par= typeParameter )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:432:9: '<' par= typeParameter ( ',' par= typeParameter )* '>'
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:432:65: ( ',' par= typeParameter )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==41) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:432:66: ',' par= typeParameter
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:435:1: typeParameter returns [GenericParameter element] : name= Identifier ( 'extends' bound= typeBound )? ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:436:5: (name= Identifier ( 'extends' bound= typeBound )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:436:9: name= Identifier ( 'extends' bound= typeBound )?
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:436:102: ( 'extends' bound= typeBound )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==38) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:436:103: 'extends' bound= typeBound
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:439:1: typeBound returns [ExtendsConstraint element] : tp= type ( '&' tpp= type )* ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:5: (tp= type ( '&' tpp= type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:9: tp= type ( '&' tpp= type )*
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:50: ( '&' tpp= type )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==43) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:51: '&' tpp= type
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:444:1: enumDeclaration returns [RegularType element] : ENUM name= Identifier ( 'implements' trefs= typeList )? body= enumBody ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:448:5: ( ENUM name= Identifier ( 'implements' trefs= typeList )? body= enumBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:448:9: ENUM name= Identifier ( 'implements' trefs= typeList )? body= enumBody
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:448:185: ( 'implements' trefs= typeList )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==39) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:448:186: 'implements' trefs= typeList
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:452:1: enumBody returns [ClassBody element] : '{' (csts= enumConstants )? ( ',' )? (decls= enumBodyDeclarations )? '}' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:5: ( '{' (csts= enumConstants )? ( ',' )? (decls= enumBodyDeclarations )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:9: '{' (csts= enumConstants )? ( ',' )? (decls= enumBodyDeclarations )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal27=(Token)match(input,44,FOLLOW_44_in_enumBody934); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal27_tree = (Object)adaptor.create(char_literal27);
            adaptor.addChild(root_0, char_literal27_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:13: (csts= enumConstants )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==Identifier||LA24_0==73) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:14: csts= enumConstants
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:96: ( ',' )?
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:101: (decls= enumBodyDeclarations )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==26) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:102: decls= enumBodyDeclarations
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:456:1: enumConstants returns [List<EnumConstant> element] : ct= enumConstant ( ',' cst= enumConstant )* ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:457:5: (ct= enumConstant ( ',' cst= enumConstant )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:457:9: ct= enumConstant ( ',' cst= enumConstant )*
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:457:107: ( ',' cst= enumConstant )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:457:108: ',' cst= enumConstant
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:460:1: enumConstant returns [EnumConstant element] : ( annotations )? name= Identifier (args= arguments )? (body= classBody )? ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:5: ( ( annotations )? name= Identifier (args= arguments )? (body= classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:9: ( annotations )? name= Identifier (args= arguments )? (body= classBody )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:9: ( annotations )?
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:112: (args= arguments )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==66) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:113: args= arguments
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:179: (body= classBody )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==44) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:180: body= classBody
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:464:1: enumBodyDeclarations returns [List<TypeElement> element] : ';' (decl= classBodyDeclaration )* ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:465:5: ( ';' (decl= classBodyDeclaration )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:465:9: ';' (decl= classBodyDeclaration )*
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:465:61: (decl= classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=Identifier && LA31_0<=ENUM)||LA31_0==26||LA31_0==28||(LA31_0>=31 && LA31_0<=37)||LA31_0==40||LA31_0==44||(LA31_0>=46 && LA31_0<=47)||(LA31_0>=52 && LA31_0<=63)||LA31_0==73) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:465:62: decl= classBodyDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:468:1: interfaceDeclaration returns [Type element] : (id= normalInterfaceDeclaration | ad= annotationTypeDeclaration );
    public final JavaParser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        JavaParser.interfaceDeclaration_return retval = new JavaParser.interfaceDeclaration_return();
        retval.start = input.LT(1);
        int interfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalInterfaceDeclaration_return id = null;

        JavaParser.annotationTypeDeclaration_return ad = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:469:5: (id= normalInterfaceDeclaration | ad= annotationTypeDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:469:9: id= normalInterfaceDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:470:9: ad= annotationTypeDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:473:1: normalInterfaceDeclaration returns [RegularType element] : 'interface' name= Identifier (params= typeParameters )? ( 'extends' trefs= typeList )? body= classBody ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:5: ( 'interface' name= Identifier (params= typeParameters )? ( 'extends' trefs= typeList )? body= classBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:9: 'interface' name= Identifier (params= typeParameters )? ( 'extends' trefs= typeList )? body= classBody
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:155: (params= typeParameters )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==40) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:156: params= typeParameters
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:249: ( 'extends' trefs= typeList )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==38) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:474:250: 'extends' trefs= typeList
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:1: typeList returns [List<TypeReference> element] : tp= type ( ',' tpp= type )* ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:5: (tp= type ( ',' tpp= type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:9: tp= type ( ',' tpp= type )*
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:99: ( ',' tpp= type )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==41) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:100: ',' tpp= type
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:481:1: classBody returns [ClassBody element] : '{' (decl= classBodyDeclaration )* '}' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:482:5: ( '{' (decl= classBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:482:9: '{' (decl= classBodyDeclaration )* '}'
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:482:49: (decl= classBodyDeclaration )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=Identifier && LA36_0<=ENUM)||LA36_0==26||LA36_0==28||(LA36_0>=31 && LA36_0<=37)||LA36_0==40||LA36_0==44||(LA36_0>=46 && LA36_0<=47)||(LA36_0>=52 && LA36_0<=63)||LA36_0==73) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:482:50: decl= classBodyDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:485:1: interfaceBody returns [ClassBody element] : '{' (decl= interfaceBodyDeclaration )* '}' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:486:5: ( '{' (decl= interfaceBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:486:9: '{' (decl= interfaceBodyDeclaration )* '}'
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:486:49: (decl= interfaceBodyDeclaration )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=Identifier && LA37_0<=ENUM)||LA37_0==26||LA37_0==28||(LA37_0>=31 && LA37_0<=37)||LA37_0==40||(LA37_0>=46 && LA37_0<=47)||(LA37_0>=52 && LA37_0<=63)||LA37_0==73) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:486:50: decl= interfaceBodyDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:489:1: classBodyDeclaration returns [TypeElement element] : ( ';' | ( 'static' )? bl= block | mods= modifiers decl= memberDecl );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:490:5: ( ';' | ( 'static' )? bl= block | mods= modifiers decl= memberDecl )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:490:9: ';'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:9: ( 'static' )? bl= block
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:9: ( 'static' )?
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:492:9: mods= modifiers decl= memberDecl
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:1: memberDecl returns [TypeElement element] : (gen= genericMethodOrConstructorDecl | mem= memberDeclaration | vmd= voidMethodDeclaration | cs= constructorDeclaration | id= interfaceDeclaration | cd= classDeclaration );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:496:5: (gen= genericMethodOrConstructorDecl | mem= memberDeclaration | vmd= voidMethodDeclaration | cs= constructorDeclaration | id= interfaceDeclaration | cd= classDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:496:9: gen= genericMethodOrConstructorDecl
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:497:9: mem= memberDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:498:9: vmd= voidMethodDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:499:9: cs= constructorDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:9: id= interfaceDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:501:9: cd= classDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:1: voidMethodDeclaration returns [Method element] : 'void' methodname= Identifier voidMethodDeclaratorRest ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:506:6: ( 'void' methodname= Identifier voidMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:506:8: 'void' methodname= Identifier voidMethodDeclaratorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:509:1: constructorDeclaration returns [Method element] : consname= Identifier constructorDeclaratorRest ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:511:9: (consname= Identifier constructorDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:511:11: consname= Identifier constructorDeclaratorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:514:1: memberDeclaration returns [TypeElement element] : (method= methodDeclaration | field= fieldDeclaration );
    public final JavaParser.memberDeclaration_return memberDeclaration() throws RecognitionException {
        JavaParser.memberDeclaration_return retval = new JavaParser.memberDeclaration_return();
        retval.start = input.LT(1);
        int memberDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.methodDeclaration_return method = null;

        JavaParser.fieldDeclaration_return field = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:515:5: (method= methodDeclaration | field= fieldDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:515:9: method= methodDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:516:9: field= fieldDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:1: genericMethodOrConstructorDecl returns [Member element] : params= typeParameters rest= genericMethodOrConstructorRest ;
    public final JavaParser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl() throws RecognitionException {
        JavaParser.genericMethodOrConstructorDecl_return retval = new JavaParser.genericMethodOrConstructorDecl_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.typeParameters_return params = null;

        JavaParser.genericMethodOrConstructorRest_return rest = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:521:5: (params= typeParameters rest= genericMethodOrConstructorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:521:9: params= typeParameters rest= genericMethodOrConstructorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:524:1: genericMethodOrConstructorRest returns [Method element] : ( (t= type | 'void' ) name= Identifier methodDeclaratorRest | name= Identifier constructorDeclaratorRest );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:5: ( (t= type | 'void' ) name= Identifier methodDeclaratorRest | name= Identifier constructorDeclaratorRest )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==Identifier) ) {
                int LA43_1 = input.LA(2);

                if ( (LA43_1==66) ) {
                    alt43=2;
                }
                else if ( (LA43_1==Identifier||LA43_1==29||LA43_1==40||LA43_1==48) ) {
                    alt43=1;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:9: (t= type | 'void' ) name= Identifier methodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:9: (t= type | 'void' )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:10: t= type
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:36: 'void'
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:9: name= Identifier constructorDeclaratorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:531:1: methodDeclaration returns [Method element] : t= type name= Identifier methodDeclaratorRest ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:5: (t= type name= Identifier methodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:9: t= type name= Identifier methodDeclaratorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:536:1: fieldDeclaration returns [MemberVariableDeclarator element] : ref= type decls= variableDeclarators ';' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:5: (ref= type decls= variableDeclarators ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:9: ref= type decls= variableDeclarators ';'
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:540:1: interfaceBodyDeclaration returns [TypeElement element] : (mods= modifiers decl= interfaceMemberDecl | ';' );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:5: (mods= modifiers decl= interfaceMemberDecl | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:9: mods= modifiers decl= interfaceMemberDecl
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:9: ';'
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:545:1: interfaceMemberDecl returns [TypeElement element] : (decl= interfaceMethodOrFieldDecl | decl2= interfaceGenericMethodDecl | decl5= voidInterfaceMethodDeclaration | decl3= interfaceDeclaration | decl4= classDeclaration );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:5: (decl= interfaceMethodOrFieldDecl | decl2= interfaceGenericMethodDecl | decl5= voidInterfaceMethodDeclaration | decl3= interfaceDeclaration | decl4= classDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:9: decl= interfaceMethodOrFieldDecl
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:547:9: decl2= interfaceGenericMethodDecl
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:548:9: decl5= voidInterfaceMethodDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:549:9: decl3= interfaceDeclaration
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:550:9: decl4= classDeclaration
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:553:1: voidInterfaceMethodDeclaration returns [Method element] : 'void' methodname= Identifier voidInterfaceMethodDeclaratorRest ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:555:6: ( 'void' methodname= Identifier voidInterfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:555:8: 'void' methodname= Identifier voidInterfaceMethodDeclaratorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:1: interfaceMethodOrFieldDecl returns [TypeElement element] : (cst= interfaceConstant | m= interfaceMethod );
    public final JavaParser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl() throws RecognitionException {
        JavaParser.interfaceMethodOrFieldDecl_return retval = new JavaParser.interfaceMethodOrFieldDecl_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.interfaceConstant_return cst = null;

        JavaParser.interfaceMethod_return m = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:559:5: (cst= interfaceConstant | m= interfaceMethod )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:559:9: cst= interfaceConstant
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:560:9: m= interfaceMethod
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:564:1: interfaceConstant returns [MemberVariableDeclarator element] : ref= type decl= constantDeclarator ( ',' dec= constantDeclarator )* ';' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:565:5: (ref= type decl= constantDeclarator ( ',' dec= constantDeclarator )* ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:565:9: ref= type decl= constantDeclarator ( ',' dec= constantDeclarator )* ';'
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:565:139: ( ',' dec= constantDeclarator )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==41) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:565:140: ',' dec= constantDeclarator
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:1: interfaceMethod returns [Method element] : tref= type methodname= Identifier interfaceMethodDeclaratorRest ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:570:2: (tref= type methodname= Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:570:4: tref= type methodname= Identifier interfaceMethodDeclaratorRest
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:574:1: methodDeclaratorRest : pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' ) ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:576:5: (pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:576:9: pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' )
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:576:122: ( '[' ']' )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==48) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:576:123: '[' ']'
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:9: ( 'throws' names= qualifiedNameList )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==50) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:10: 'throws' names= qualifiedNameList
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:578:9: (body= methodBody | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:578:13: body= methodBody
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:579:13: ';'
                    {
                    char_literal59=(Token)match(input,26,FOLLOW_26_in_methodDeclaratorRest2169); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal59_tree = (Object)adaptor.create(char_literal59);
                    adaptor.addChild(root_0, char_literal59_tree);
                    }
                    if ( state.backtracking==0 ) {
                      ((MethodScope_scope)MethodScope_stack.peek()).method.setImplementation(null);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:583:1: voidMethodDeclaratorRest : pars= formalParameters ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' ) ;
    public final JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidMethodDeclaratorRest_return retval = new JavaParser.voidMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal60=null;
        Token char_literal61=null;
        JavaParser.formalParameters_return pars = null;

        JavaParser.qualifiedNameList_return names = null;

        JavaParser.methodBody_return body = null;


        Object string_literal60_tree=null;
        Object char_literal61_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:5: (pars= formalParameters ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:9: pars= formalParameters ( 'throws' names= qualifiedNameList )? (body= methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest2206);
            pars=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, pars.getTree());
            if ( state.backtracking==0 ) {
              for(FormalParameter par: pars.element){((MethodScope_scope)MethodScope_stack.peek()).method.header().addParameter(par);}
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:585:10: ( 'throws' names= qualifiedNameList )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==50) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:585:11: 'throws' names= qualifiedNameList
                    {
                    string_literal60=(Token)match(input,50,FOLLOW_50_in_voidMethodDeclaratorRest2220); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal60_tree = (Object)adaptor.create(string_literal60);
                    adaptor.addChild(root_0, string_literal60_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest2224);
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:586:9: (body= methodBody | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:586:13: body= methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest2244);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:587:13: ';'
                    {
                    char_literal61=(Token)match(input,26,FOLLOW_26_in_voidMethodDeclaratorRest2260); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal61_tree = (Object)adaptor.create(char_literal61);
                    adaptor.addChild(root_0, char_literal61_tree);
                    }
                    if ( state.backtracking==0 ) {
                      ((MethodScope_scope)MethodScope_stack.peek()).method.setImplementation(null);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:591:1: interfaceMethodDeclaratorRest : pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? ';' ;
    public final JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.interfaceMethodDeclaratorRest_return retval = new JavaParser.interfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal62=null;
        Token char_literal63=null;
        Token string_literal64=null;
        Token char_literal65=null;
        JavaParser.formalParameters_return pars = null;

        JavaParser.qualifiedNameList_return names = null;


        Object char_literal62_tree=null;
        Object char_literal63_tree=null;
        Object string_literal64_tree=null;
        Object char_literal65_tree=null;

        int count = 0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:5: (pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:9: pars= formalParameters ( '[' ']' )* ( 'throws' names= qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest2301);
            pars=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, pars.getTree());
            if ( state.backtracking==0 ) {
              for(FormalParameter par: pars.element){((MethodScope_scope)MethodScope_stack.peek()).method.header().addParameter(par);}
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:594:8: ( '[' ']' )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==48) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:594:9: '[' ']'
            	    {
            	    char_literal62=(Token)match(input,48,FOLLOW_48_in_interfaceMethodDeclaratorRest2313); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal62_tree = (Object)adaptor.create(char_literal62);
            	    adaptor.addChild(root_0, char_literal62_tree);
            	    }
            	    char_literal63=(Token)match(input,49,FOLLOW_49_in_interfaceMethodDeclaratorRest2315); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal63_tree = (Object)adaptor.create(char_literal63);
            	    adaptor.addChild(root_0, char_literal63_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      count++;
            	    }

            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              ((JavaTypeReference)((MethodScope_scope)MethodScope_stack.peek()).method.getReturnTypeReference()).setArrayDimension(count);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:595:8: ( 'throws' names= qualifiedNameList )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==50) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:595:9: 'throws' names= qualifiedNameList
                    {
                    string_literal64=(Token)match(input,50,FOLLOW_50_in_interfaceMethodDeclaratorRest2331); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal64_tree = (Object)adaptor.create(string_literal64);
                    adaptor.addChild(root_0, string_literal64_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest2335);
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

            char_literal65=(Token)match(input,26,FOLLOW_26_in_interfaceMethodDeclaratorRest2341); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal65_tree = (Object)adaptor.create(char_literal65);
            adaptor.addChild(root_0, char_literal65_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:598:1: interfaceGenericMethodDecl returns [TypeElement element] : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final JavaParser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl() throws RecognitionException {
        JavaParser.interfaceGenericMethodDecl_return retval = new JavaParser.interfaceGenericMethodDecl_return();
        retval.start = input.LT(1);
        int interfaceGenericMethodDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal68=null;
        Token Identifier69=null;
        JavaParser.typeParameters_return typeParameters66 = null;

        JavaParser.type_return type67 = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest70 = null;


        Object string_literal68_tree=null;
        Object Identifier69_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:9: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl2368);
            typeParameters66=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters66.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:24: ( type | 'void' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:25: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl2371);
                    type67=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type67.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:32: 'void'
                    {
                    string_literal68=(Token)match(input,47,FOLLOW_47_in_interfaceGenericMethodDecl2375); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal68_tree = (Object)adaptor.create(string_literal68);
                    adaptor.addChild(root_0, string_literal68_tree);
                    }

                    }
                    break;

            }

            Identifier69=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl2378); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier69_tree = (Object)adaptor.create(Identifier69);
            adaptor.addChild(root_0, Identifier69_tree);
            }
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl2388);
            interfaceMethodDeclaratorRest70=interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest70.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:1: voidInterfaceMethodDeclaratorRest : pars= formalParameters ( 'throws' names= qualifiedNameList )? ';' ;
    public final JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidInterfaceMethodDeclaratorRest_return retval = new JavaParser.voidInterfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal71=null;
        Token char_literal72=null;
        JavaParser.formalParameters_return pars = null;

        JavaParser.qualifiedNameList_return names = null;


        Object string_literal71_tree=null;
        Object char_literal72_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:604:5: (pars= formalParameters ( 'throws' names= qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:604:9: pars= formalParameters ( 'throws' names= qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest2413);
            pars=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, pars.getTree());
            if ( state.backtracking==0 ) {
              for(FormalParameter par: pars.element){((MethodScope_scope)MethodScope_stack.peek()).method.header().addParameter(par);}
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:605:6: ( 'throws' names= qualifiedNameList )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==50) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:605:7: 'throws' names= qualifiedNameList
                    {
                    string_literal71=(Token)match(input,50,FOLLOW_50_in_voidInterfaceMethodDeclaratorRest2423); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal71_tree = (Object)adaptor.create(string_literal71);
                    adaptor.addChild(root_0, string_literal71_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest2427);
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

            char_literal72=(Token)match(input,26,FOLLOW_26_in_voidInterfaceMethodDeclaratorRest2439); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal72_tree = (Object)adaptor.create(char_literal72);
            adaptor.addChild(root_0, char_literal72_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:609:1: constructorDeclaratorRest : pars= formalParameters ( 'throws' names= qualifiedNameList )? body= constructorBody ;
    public final JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest() throws RecognitionException {
        JavaParser.constructorDeclaratorRest_return retval = new JavaParser.constructorDeclaratorRest_return();
        retval.start = input.LT(1);
        int constructorDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal73=null;
        JavaParser.formalParameters_return pars = null;

        JavaParser.qualifiedNameList_return names = null;

        JavaParser.constructorBody_return body = null;


        Object string_literal73_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:610:5: (pars= formalParameters ( 'throws' names= qualifiedNameList )? body= constructorBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:610:9: pars= formalParameters ( 'throws' names= qualifiedNameList )? body= constructorBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest2464);
            pars=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, pars.getTree());
            if ( state.backtracking==0 ) {
              for(FormalParameter par: pars.element){((MethodScope_scope)MethodScope_stack.peek()).method.header().addParameter(par);}
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:5: ( 'throws' names= qualifiedNameList )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==50) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:6: 'throws' names= qualifiedNameList
                    {
                    string_literal73=(Token)match(input,50,FOLLOW_50_in_constructorDeclaratorRest2474); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal73_tree = (Object)adaptor.create(string_literal73);
                    adaptor.addChild(root_0, string_literal73_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest2478);
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

            pushFollow(FOLLOW_constructorBody_in_constructorDeclaratorRest2491);
            body=constructorBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());
            if ( state.backtracking==0 ) {
              ((MethodScope_scope)MethodScope_stack.peek()).method.setImplementation(new RegularImplementation(body.element));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:615:1: constantDeclarator returns [JavaVariableDeclaration element] : name= Identifier ( ( '[' ']' )* '=' init= variableInitializer ) ;
    public final JavaParser.constantDeclarator_return constantDeclarator() throws RecognitionException {
        JavaParser.constantDeclarator_return retval = new JavaParser.constantDeclarator_return();
        retval.start = input.LT(1);
        int constantDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token char_literal74=null;
        Token char_literal75=null;
        Token char_literal76=null;
        JavaParser.variableInitializer_return init = null;


        Object name_tree=null;
        Object char_literal74_tree=null;
        Object char_literal75_tree=null;
        Object char_literal76_tree=null;

        int count = 0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:5: (name= Identifier ( ( '[' ']' )* '=' init= variableInitializer ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:9: name= Identifier ( ( '[' ']' )* '=' init= variableInitializer )
            {
            root_0 = (Object)adaptor.nil();

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator2522); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:25: ( ( '[' ']' )* '=' init= variableInitializer )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:26: ( '[' ']' )* '=' init= variableInitializer
            {
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:26: ( '[' ']' )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==48) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:27: '[' ']'
            	    {
            	    char_literal74=(Token)match(input,48,FOLLOW_48_in_constantDeclarator2526); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal74_tree = (Object)adaptor.create(char_literal74);
            	    adaptor.addChild(root_0, char_literal74_tree);
            	    }
            	    char_literal75=(Token)match(input,49,FOLLOW_49_in_constantDeclarator2528); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal75_tree = (Object)adaptor.create(char_literal75);
            	    adaptor.addChild(root_0, char_literal75_tree);
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

            char_literal76=(Token)match(input,51,FOLLOW_51_in_constantDeclarator2534); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal76_tree = (Object)adaptor.create(char_literal76);
            adaptor.addChild(root_0, char_literal76_tree);
            }
            pushFollow(FOLLOW_variableInitializer_in_constantDeclarator2538);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:621:1: variableDeclarators returns [List<VariableDeclaration> element] : decl= variableDeclarator ( ',' decll= variableDeclarator )* ;
    public final JavaParser.variableDeclarators_return variableDeclarators() throws RecognitionException {
        JavaParser.variableDeclarators_return retval = new JavaParser.variableDeclarators_return();
        retval.start = input.LT(1);
        int variableDeclarators_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal77=null;
        JavaParser.variableDeclarator_return decl = null;

        JavaParser.variableDeclarator_return decll = null;


        Object char_literal77_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:5: (decl= variableDeclarator ( ',' decll= variableDeclarator )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: decl= variableDeclarator ( ',' decll= variableDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators2578);
            decl=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, decl.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new ArrayList<VariableDeclaration>(); retval.element.add(decl.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:123: ( ',' decll= variableDeclarator )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:124: ',' decll= variableDeclarator
            	    {
            	    char_literal77=(Token)match(input,41,FOLLOW_41_in_variableDeclarators2582); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal77_tree = (Object)adaptor.create(char_literal77);
            	    adaptor.addChild(root_0, char_literal77_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators2586);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:1: variableDeclarator returns [JavaVariableDeclaration element] : id= variableDeclaratorId ( '=' init= variableInitializer )? ;
    public final JavaParser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        JavaParser.variableDeclarator_return retval = new JavaParser.variableDeclarator_return();
        retval.start = input.LT(1);
        int variableDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal78=null;
        JavaParser.variableDeclaratorId_return id = null;

        JavaParser.variableInitializer_return init = null;


        Object char_literal78_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:626:5: (id= variableDeclaratorId ( '=' init= variableInitializer )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:626:9: id= variableDeclaratorId ( '=' init= variableInitializer )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclaratorId_in_variableDeclarator2615);
            id=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id.getTree());
            if ( state.backtracking==0 ) {
              retval.element = new JavaVariableDeclaration(id.element.name()); retval.element.setArrayDimension(id.element.dimension());
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:626:158: ( '=' init= variableInitializer )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==51) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:626:159: '=' init= variableInitializer
                    {
                    char_literal78=(Token)match(input,51,FOLLOW_51_in_variableDeclarator2620); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal78_tree = (Object)adaptor.create(char_literal78);
                    adaptor.addChild(root_0, char_literal78_tree);
                    }
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2624);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:631:1: variableDeclaratorId returns [StupidVariableDeclaratorId element] : name= Identifier ( '[' ']' )* ;
    public final JavaParser.variableDeclaratorId_return variableDeclaratorId() throws RecognitionException {
        JavaParser.variableDeclaratorId_return retval = new JavaParser.variableDeclaratorId_return();
        retval.start = input.LT(1);
        int variableDeclaratorId_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token char_literal79=null;
        Token char_literal80=null;

        Object name_tree=null;
        Object char_literal79_tree=null;
        Object char_literal80_tree=null;

        int count = 0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:5: (name= Identifier ( '[' ']' )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:9: name= Identifier ( '[' ']' )*
            {
            root_0 = (Object)adaptor.nil();

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId2667); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:25: ( '[' ']' )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==48) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:26: '[' ']'
            	    {
            	    char_literal79=(Token)match(input,48,FOLLOW_48_in_variableDeclaratorId2670); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal79_tree = (Object)adaptor.create(char_literal79);
            	    adaptor.addChild(root_0, char_literal79_tree);
            	    }
            	    char_literal80=(Token)match(input,49,FOLLOW_49_in_variableDeclaratorId2672); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal80_tree = (Object)adaptor.create(char_literal80);
            	    adaptor.addChild(root_0, char_literal80_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:636:1: variableInitializer returns [Expression element] : (init= arrayInitializer | expr= expression );
    public final JavaParser.variableInitializer_return variableInitializer() throws RecognitionException {
        JavaParser.variableInitializer_return retval = new JavaParser.variableInitializer_return();
        retval.start = input.LT(1);
        int variableInitializer_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arrayInitializer_return init = null;

        JavaParser.expression_return expr = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:637:5: (init= arrayInitializer | expr= expression )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:637:9: init= arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2703);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:638:9: expr= expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer2717);
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
        public ArrayInitializer element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:641:1: arrayInitializer returns [ArrayInitializer element] : '{' (init= variableInitializer ( ',' initt= variableInitializer )* ( ',' )? )? '}' ;
    public final JavaParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        JavaParser.arrayInitializer_return retval = new JavaParser.arrayInitializer_return();
        retval.start = input.LT(1);
        int arrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal81=null;
        Token char_literal82=null;
        Token char_literal83=null;
        Token char_literal84=null;
        JavaParser.variableInitializer_return init = null;

        JavaParser.variableInitializer_return initt = null;


        Object char_literal81_tree=null;
        Object char_literal82_tree=null;
        Object char_literal83_tree=null;
        Object char_literal84_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:5: ( '{' (init= variableInitializer ( ',' initt= variableInitializer )* ( ',' )? )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:9: '{' (init= variableInitializer ( ',' initt= variableInitializer )* ( ',' )? )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal81=(Token)match(input,44,FOLLOW_44_in_arrayInitializer2750); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal81_tree = (Object)adaptor.create(char_literal81);
            adaptor.addChild(root_0, char_literal81_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new ArrayInitializer();
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:56: (init= variableInitializer ( ',' initt= variableInitializer )* ( ',' )? )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==Identifier||(LA65_0>=FloatingPointLiteral && LA65_0<=DecimalLiteral)||LA65_0==44||LA65_0==47||(LA65_0>=56 && LA65_0<=63)||(LA65_0>=65 && LA65_0<=66)||(LA65_0>=69 && LA65_0<=72)||(LA65_0>=105 && LA65_0<=106)||(LA65_0>=109 && LA65_0<=113)) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:57: init= variableInitializer ( ',' initt= variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2757);
                    init=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, init.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.addInitializer(init.element);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:128: ( ',' initt= variableInitializer )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:129: ',' initt= variableInitializer
                    	    {
                    	    char_literal82=(Token)match(input,41,FOLLOW_41_in_arrayInitializer2761); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal82_tree = (Object)adaptor.create(char_literal82);
                    	    adaptor.addChild(root_0, char_literal82_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2765);
                    	    initt=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, initt.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      retval.element.addInitializer(initt.element);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:208: ( ',' )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==41) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:209: ','
                            {
                            char_literal83=(Token)match(input,41,FOLLOW_41_in_arrayInitializer2771); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal83_tree = (Object)adaptor.create(char_literal83);
                            adaptor.addChild(root_0, char_literal83_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            char_literal84=(Token)match(input,45,FOLLOW_45_in_arrayInitializer2778); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal84_tree = (Object)adaptor.create(char_literal84);
            adaptor.addChild(root_0, char_literal84_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:645:1: modifier returns [Modifier element] : ( annotation | mod= classOrInterfaceModifier | 'native' | 'synchronized' | 'transient' | 'volatile' );
    public final JavaParser.modifier_return modifier() throws RecognitionException {
        JavaParser.modifier_return retval = new JavaParser.modifier_return();
        retval.start = input.LT(1);
        int modifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal86=null;
        Token string_literal87=null;
        Token string_literal88=null;
        Token string_literal89=null;
        JavaParser.classOrInterfaceModifier_return mod = null;

        JavaParser.annotation_return annotation85 = null;


        Object string_literal86_tree=null;
        Object string_literal87_tree=null;
        Object string_literal88_tree=null;
        Object string_literal89_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:646:5: ( annotation | mod= classOrInterfaceModifier | 'native' | 'synchronized' | 'transient' | 'volatile' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:646:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_modifier2801);
                    annotation85=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation85.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:647:9: mod= classOrInterfaceModifier
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceModifier_in_modifier2813);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:648:9: 'native'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal86=(Token)match(input,52,FOLLOW_52_in_modifier2825); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal86_tree = (Object)adaptor.create(string_literal86);
                    adaptor.addChild(root_0, string_literal86_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Native();
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:649:9: 'synchronized'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal87=(Token)match(input,53,FOLLOW_53_in_modifier2837); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal87_tree = (Object)adaptor.create(string_literal87);
                    adaptor.addChild(root_0, string_literal87_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Synchronized();
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:650:9: 'transient'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal88=(Token)match(input,54,FOLLOW_54_in_modifier2849); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal88_tree = (Object)adaptor.create(string_literal88);
                    adaptor.addChild(root_0, string_literal88_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Transient();
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:651:9: 'volatile'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal89=(Token)match(input,55,FOLLOW_55_in_modifier2861); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal89_tree = (Object)adaptor.create(string_literal89);
                    adaptor.addChild(root_0, string_literal89_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:1: packageOrTypeName : qualifiedName ;
    public final JavaParser.packageOrTypeName_return packageOrTypeName() throws RecognitionException {
        JavaParser.packageOrTypeName_return retval = new JavaParser.packageOrTypeName_return();
        retval.start = input.LT(1);
        int packageOrTypeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName90 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:655:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:655:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_packageOrTypeName2882);
            qualifiedName90=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName90.getTree());

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
        public String element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstantName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:1: enumConstantName returns [String element] : id= Identifier ;
    public final JavaParser.enumConstantName_return enumConstantName() throws RecognitionException {
        JavaParser.enumConstantName_return retval = new JavaParser.enumConstantName_return();
        retval.start = input.LT(1);
        int enumConstantName_StartIndex = input.index();
        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:659:5: (id= Identifier )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:659:9: id= Identifier
            {
            root_0 = (Object)adaptor.nil();

            id=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstantName2907); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element=(id!=null?id.getText():null);
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
        public String element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:1: typeName returns [String element] : name= qualifiedName ;
    public final JavaParser.typeName_return typeName() throws RecognitionException {
        JavaParser.typeName_return retval = new JavaParser.typeName_return();
        retval.start = input.LT(1);
        int typeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return name = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:663:5: (name= qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:663:9: name= qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_typeName2934);
            name=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, name.getTree());
            if ( state.backtracking==0 ) {
              retval.element=name.element;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:666:1: type returns [TypeReference element] : (cd= classOrInterfaceType ( '[' ']' )* | pt= primitiveType ( '[' ']' )* );
    public final JavaParser.type_return type() throws RecognitionException {
        JavaParser.type_return retval = new JavaParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal91=null;
        Token char_literal92=null;
        Token char_literal93=null;
        Token char_literal94=null;
        JavaParser.classOrInterfaceType_return cd = null;

        JavaParser.primitiveType_return pt = null;


        Object char_literal91_tree=null;
        Object char_literal92_tree=null;
        Object char_literal93_tree=null;
        Object char_literal94_tree=null;

        int dimension=0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:2: (cd= classOrInterfaceType ( '[' ']' )* | pt= primitiveType ( '[' ']' )* )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:4: cd= classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_type2960);
                    cd=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:28: ( '[' ']' )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==48) ) {
                            alt67=1;
                        }


                        switch (alt67) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:29: '[' ']'
                    	    {
                    	    char_literal91=(Token)match(input,48,FOLLOW_48_in_type2963); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal91_tree = (Object)adaptor.create(char_literal91);
                    	    adaptor.addChild(root_0, char_literal91_tree);
                    	    }
                    	    char_literal92=(Token)match(input,49,FOLLOW_49_in_type2965); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal92_tree = (Object)adaptor.create(char_literal92);
                    	    adaptor.addChild(root_0, char_literal92_tree);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:669:4: pt= primitiveType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_type2978);
                    pt=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, pt.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:669:21: ( '[' ']' )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( (LA68_0==48) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:669:22: '[' ']'
                    	    {
                    	    char_literal93=(Token)match(input,48,FOLLOW_48_in_type2981); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal93_tree = (Object)adaptor.create(char_literal93);
                    	    adaptor.addChild(root_0, char_literal93_tree);
                    	    }
                    	    char_literal94=(Token)match(input,49,FOLLOW_49_in_type2983); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal94_tree = (Object)adaptor.create(char_literal94);
                    	    adaptor.addChild(root_0, char_literal94_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:672:1: classOrInterfaceType returns [JavaTypeReference element] : Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ;
    public final JavaParser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        JavaParser.classOrInterfaceType_return retval = new JavaParser.classOrInterfaceType_return();
        retval.start = input.LT(1);
        int classOrInterfaceType_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier95=null;
        Token char_literal97=null;
        Token Identifier98=null;
        JavaParser.typeArguments_return typeArguments96 = null;

        JavaParser.typeArguments_return typeArguments99 = null;


        Object Identifier95_tree=null;
        Object char_literal97_tree=null;
        Object Identifier98_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier95=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType3003); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier95_tree = (Object)adaptor.create(Identifier95);
            adaptor.addChild(root_0, Identifier95_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:15: ( typeArguments )?
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
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType3005);
                    typeArguments96=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments96.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:30: ( '.' Identifier ( typeArguments )? )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==29) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:31: '.' Identifier ( typeArguments )?
            	    {
            	    char_literal97=(Token)match(input,29,FOLLOW_29_in_classOrInterfaceType3009); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal97_tree = (Object)adaptor.create(char_literal97);
            	    adaptor.addChild(root_0, char_literal97_tree);
            	    }
            	    Identifier98=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType3011); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier98_tree = (Object)adaptor.create(Identifier98);
            	    adaptor.addChild(root_0, Identifier98_tree);
            	    }
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:46: ( typeArguments )?
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
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType3013);
            	            typeArguments99=typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments99.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:676:1: primitiveType returns [JavaTypeReference element] : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final JavaParser.primitiveType_return primitiveType() throws RecognitionException {
        JavaParser.primitiveType_return retval = new JavaParser.primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        Object root_0 = null;

        Token set100=null;

        Object set100_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:677:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set100=(Token)input.LT(1);
            if ( (input.LA(1)>=56 && input.LA(1)<=63) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set100));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:687:1: variableModifier : ( 'final' | annotation );
    public final JavaParser.variableModifier_return variableModifier() throws RecognitionException {
        JavaParser.variableModifier_return retval = new JavaParser.variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal101=null;
        JavaParser.annotation_return annotation102 = null;


        Object string_literal101_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:5: ( 'final' | annotation )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal101=(Token)match(input,35,FOLLOW_35_in_variableModifier3126); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal101_tree = (Object)adaptor.create(string_literal101);
                    adaptor.addChild(root_0, string_literal101_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:689:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_variableModifier3136);
                    annotation102=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation102.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final JavaParser.typeArguments_return typeArguments() throws RecognitionException {
        JavaParser.typeArguments_return retval = new JavaParser.typeArguments_return();
        retval.start = input.LT(1);
        int typeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal103=null;
        Token char_literal105=null;
        Token char_literal107=null;
        JavaParser.typeArgument_return typeArgument104 = null;

        JavaParser.typeArgument_return typeArgument106 = null;


        Object char_literal103_tree=null;
        Object char_literal105_tree=null;
        Object char_literal107_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal103=(Token)match(input,40,FOLLOW_40_in_typeArguments3155); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal103_tree = (Object)adaptor.create(char_literal103);
            adaptor.addChild(root_0, char_literal103_tree);
            }
            pushFollow(FOLLOW_typeArgument_in_typeArguments3157);
            typeArgument104=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument104.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:26: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==41) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:27: ',' typeArgument
            	    {
            	    char_literal105=(Token)match(input,41,FOLLOW_41_in_typeArguments3160); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal105_tree = (Object)adaptor.create(char_literal105);
            	    adaptor.addChild(root_0, char_literal105_tree);
            	    }
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments3162);
            	    typeArgument106=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument106.getTree());

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            char_literal107=(Token)match(input,42,FOLLOW_42_in_typeArguments3166); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal107_tree = (Object)adaptor.create(char_literal107);
            adaptor.addChild(root_0, char_literal107_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:696:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final JavaParser.typeArgument_return typeArgument() throws RecognitionException {
        JavaParser.typeArgument_return retval = new JavaParser.typeArgument_return();
        retval.start = input.LT(1);
        int typeArgument_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal109=null;
        Token set110=null;
        JavaParser.type_return type108 = null;

        JavaParser.type_return type111 = null;


        Object char_literal109_tree=null;
        Object set110_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:9: type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_typeArgument3189);
                    type108=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type108.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal109=(Token)match(input,64,FOLLOW_64_in_typeArgument3199); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal109_tree = (Object)adaptor.create(char_literal109);
                    adaptor.addChild(root_0, char_literal109_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:13: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==38||LA75_0==65) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:14: ( 'extends' | 'super' ) type
                            {
                            set110=(Token)input.LT(1);
                            if ( input.LA(1)==38||input.LA(1)==65 ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set110));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument3210);
                            type111=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type111.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:701:1: qualifiedNameList returns [List<String> element] : q= qualifiedName ( ',' qn= qualifiedName )* ;
    public final JavaParser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        JavaParser.qualifiedNameList_return retval = new JavaParser.qualifiedNameList_return();
        retval.start = input.LT(1);
        int qualifiedNameList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal112=null;
        JavaParser.qualifiedName_return q = null;

        JavaParser.qualifiedName_return qn = null;


        Object char_literal112_tree=null;

        retval.element = new ArrayList<String>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:5: (q= qualifiedName ( ',' qn= qualifiedName )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:9: q= qualifiedName ( ',' qn= qualifiedName )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3244);
            q=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, q.getTree());
            if ( state.backtracking==0 ) {
              retval.element.add((q!=null?input.toString(q.start,q.stop):null));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:56: ( ',' qn= qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==41) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:57: ',' qn= qualifiedName
            	    {
            	    char_literal112=(Token)match(input,41,FOLLOW_41_in_qualifiedNameList3249); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal112_tree = (Object)adaptor.create(char_literal112);
            	    adaptor.addChild(root_0, char_literal112_tree);
            	    }
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3253);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:706:1: formalParameters returns [List<FormalParameter> element] : '(' (pars= formalParameterDecls )? ')' ;
    public final JavaParser.formalParameters_return formalParameters() throws RecognitionException {
        JavaParser.formalParameters_return retval = new JavaParser.formalParameters_return();
        retval.start = input.LT(1);
        int formalParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal113=null;
        Token char_literal114=null;
        JavaParser.formalParameterDecls_return pars = null;


        Object char_literal113_tree=null;
        Object char_literal114_tree=null;

        retval.element = new ArrayList<FormalParameter>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:708:5: ( '(' (pars= formalParameterDecls )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:708:9: '(' (pars= formalParameterDecls )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal113=(Token)match(input,66,FOLLOW_66_in_formalParameters3284); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal113_tree = (Object)adaptor.create(char_literal113);
            adaptor.addChild(root_0, char_literal113_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:708:13: (pars= formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==35||(LA78_0>=56 && LA78_0<=63)||LA78_0==73) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:708:14: pars= formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3289);
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

            char_literal114=(Token)match(input,67,FOLLOW_67_in_formalParameters3295); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal114_tree = (Object)adaptor.create(char_literal114);
            adaptor.addChild(root_0, char_literal114_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:711:1: formalParameterDecls returns [List<FormalParameter> element] : variableModifiers type formalParameterDeclsRest ;
    public final JavaParser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        JavaParser.formalParameterDecls_return retval = new JavaParser.formalParameterDecls_return();
        retval.start = input.LT(1);
        int formalParameterDecls_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers115 = null;

        JavaParser.type_return type116 = null;

        JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest117 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:712:5: ( variableModifiers type formalParameterDeclsRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:712:9: variableModifiers type formalParameterDeclsRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameterDecls3322);
            variableModifiers115=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers115.getTree());
            pushFollow(FOLLOW_type_in_formalParameterDecls3324);
            type116=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type116.getTree());
            pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls3326);
            formalParameterDeclsRest117=formalParameterDeclsRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDeclsRest117.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:715:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest() throws RecognitionException {
        JavaParser.formalParameterDeclsRest_return retval = new JavaParser.formalParameterDeclsRest_return();
        retval.start = input.LT(1);
        int formalParameterDeclsRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal119=null;
        Token string_literal121=null;
        JavaParser.variableDeclaratorId_return variableDeclaratorId118 = null;

        JavaParser.formalParameterDecls_return formalParameterDecls120 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId122 = null;


        Object char_literal119_tree=null;
        Object string_literal121_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:716:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:716:9: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3349);
                    variableDeclaratorId118=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId118.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:716:30: ( ',' formalParameterDecls )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==41) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:716:31: ',' formalParameterDecls
                            {
                            char_literal119=(Token)match(input,41,FOLLOW_41_in_formalParameterDeclsRest3352); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal119_tree = (Object)adaptor.create(char_literal119);
                            adaptor.addChild(root_0, char_literal119_tree);
                            }
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest3354);
                            formalParameterDecls120=formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls120.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:717:9: '...' variableDeclaratorId
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal121=(Token)match(input,68,FOLLOW_68_in_formalParameterDeclsRest3366); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal121_tree = (Object)adaptor.create(string_literal121);
                    adaptor.addChild(root_0, string_literal121_tree);
                    }
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3368);
                    variableDeclaratorId122=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId122.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:720:1: methodBody returns [Block element] : b= block ;
    public final JavaParser.methodBody_return methodBody() throws RecognitionException {
        JavaParser.methodBody_return retval = new JavaParser.methodBody_return();
        retval.start = input.LT(1);
        int methodBody_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.block_return b = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:721:5: (b= block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:721:9: b= block
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_block_in_methodBody3397);
            b=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, b.getTree());
            if ( state.backtracking==0 ) {
              retval.element = b.element;
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
            if ( state.backtracking>0 ) { memoize(input, 65, methodBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodBody"

    public static class constructorBody_return extends ParserRuleReturnScope {
        public Block element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructorBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:724:1: constructorBody returns [Block element] : '{' (inv= explicitConstructorInvocation )? ( blockStatement )* '}' ;
    public final JavaParser.constructorBody_return constructorBody() throws RecognitionException {
        JavaParser.constructorBody_return retval = new JavaParser.constructorBody_return();
        retval.start = input.LT(1);
        int constructorBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal123=null;
        Token char_literal125=null;
        JavaParser.explicitConstructorInvocation_return inv = null;

        JavaParser.blockStatement_return blockStatement124 = null;


        Object char_literal123_tree=null;
        Object char_literal125_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:5: ( '{' (inv= explicitConstructorInvocation )? ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:9: '{' (inv= explicitConstructorInvocation )? ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal123=(Token)match(input,44,FOLLOW_44_in_constructorBody3422); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal123_tree = (Object)adaptor.create(char_literal123);
            adaptor.addChild(root_0, char_literal123_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new Block();
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:45: (inv= explicitConstructorInvocation )?
            int alt81=2;
            alt81 = dfa81.predict(input);
            switch (alt81) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:46: inv= explicitConstructorInvocation
                    {
                    pushFollow(FOLLOW_explicitConstructorInvocation_in_constructorBody3429);
                    inv=explicitConstructorInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inv.getTree());
                    if ( state.backtracking==0 ) {
                      retval.element.addStatement(new StatementExpression(inv.element));
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:151: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_constructorBody3435);
            	    blockStatement124=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement124.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            char_literal125=(Token)match(input,45,FOLLOW_45_in_constructorBody3438); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal125_tree = (Object)adaptor.create(char_literal125);
            adaptor.addChild(root_0, char_literal125_tree);
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
        public Expression element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitConstructorInvocation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:728:1: explicitConstructorInvocation returns [Expression element] : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        JavaParser.explicitConstructorInvocation_return retval = new JavaParser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);
        int explicitConstructorInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token set127=null;
        Token char_literal129=null;
        Token char_literal131=null;
        Token string_literal133=null;
        Token char_literal135=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments126 = null;

        JavaParser.arguments_return arguments128 = null;

        JavaParser.primary_return primary130 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments132 = null;

        JavaParser.arguments_return arguments134 = null;


        Object set127_tree=null;
        Object char_literal129_tree=null;
        Object char_literal131_tree=null;
        Object string_literal133_tree=null;
        Object char_literal135_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt85=2;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: ( nonWildcardTypeArguments )?
                    int alt83=2;
                    int LA83_0 = input.LA(1);

                    if ( (LA83_0==40) ) {
                        alt83=1;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3461);
                            nonWildcardTypeArguments126=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments126.getTree());

                            }
                            break;

                    }

                    set127=(Token)input.LT(1);
                    if ( input.LA(1)==65||input.LA(1)==69 ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set127));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3472);
                    arguments128=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments128.getTree());
                    char_literal129=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation3474); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal129_tree = (Object)adaptor.create(char_literal129);
                    adaptor.addChild(root_0, char_literal129_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:730:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3484);
                    primary130=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary130.getTree());
                    char_literal131=(Token)match(input,29,FOLLOW_29_in_explicitConstructorInvocation3486); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal131_tree = (Object)adaptor.create(char_literal131);
                    adaptor.addChild(root_0, char_literal131_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:730:21: ( nonWildcardTypeArguments )?
                    int alt84=2;
                    int LA84_0 = input.LA(1);

                    if ( (LA84_0==40) ) {
                        alt84=1;
                    }
                    switch (alt84) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3488);
                            nonWildcardTypeArguments132=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments132.getTree());

                            }
                            break;

                    }

                    string_literal133=(Token)match(input,65,FOLLOW_65_in_explicitConstructorInvocation3491); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal133_tree = (Object)adaptor.create(string_literal133);
                    adaptor.addChild(root_0, string_literal133_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3493);
                    arguments134=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments134.getTree());
                    char_literal135=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation3495); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal135_tree = (Object)adaptor.create(char_literal135);
                    adaptor.addChild(root_0, char_literal135_tree);
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
        public String element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:734:1: qualifiedName returns [String element] : id= Identifier ( '.' idx= Identifier )* ;
    public final JavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
        JavaParser.qualifiedName_return retval = new JavaParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token id=null;
        Token idx=null;
        Token char_literal136=null;

        Object id_tree=null;
        Object idx_tree=null;
        Object char_literal136_tree=null;

        StringBuffer buffer = new StringBuffer();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:736:5: (id= Identifier ( '.' idx= Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:736:9: id= Identifier ( '.' idx= Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            id=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName3525); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }
            if ( state.backtracking==0 ) {
              buffer.append((id!=null?id.getText():null));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:736:49: ( '.' idx= Identifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:736:50: '.' idx= Identifier
            	    {
            	    char_literal136=(Token)match(input,29,FOLLOW_29_in_qualifiedName3529); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal136_tree = (Object)adaptor.create(char_literal136);
            	    adaptor.addChild(root_0, char_literal136_tree);
            	    }
            	    idx=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName3533); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    idx_tree = (Object)adaptor.create(idx);
            	    adaptor.addChild(root_0, idx_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      buffer.append((idx!=null?idx.getText():null));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:739:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final JavaParser.literal_return literal() throws RecognitionException {
        JavaParser.literal_return retval = new JavaParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token FloatingPointLiteral138=null;
        Token CharacterLiteral139=null;
        Token StringLiteral140=null;
        Token string_literal142=null;
        JavaParser.integerLiteral_return integerLiteral137 = null;

        JavaParser.booleanLiteral_return booleanLiteral141 = null;


        Object FloatingPointLiteral138_tree=null;
        Object CharacterLiteral139_tree=null;
        Object StringLiteral140_tree=null;
        Object string_literal142_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:740:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:740:9: integerLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_integerLiteral_in_literal3561);
                    integerLiteral137=integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, integerLiteral137.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:741:9: FloatingPointLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    FloatingPointLiteral138=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal3571); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FloatingPointLiteral138_tree = (Object)adaptor.create(FloatingPointLiteral138);
                    adaptor.addChild(root_0, FloatingPointLiteral138_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:742:9: CharacterLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    CharacterLiteral139=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal3581); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CharacterLiteral139_tree = (Object)adaptor.create(CharacterLiteral139);
                    adaptor.addChild(root_0, CharacterLiteral139_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:743:9: StringLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    StringLiteral140=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal3591); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    StringLiteral140_tree = (Object)adaptor.create(StringLiteral140);
                    adaptor.addChild(root_0, StringLiteral140_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:744:9: booleanLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_booleanLiteral_in_literal3601);
                    booleanLiteral141=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, booleanLiteral141.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:9: 'null'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal142=(Token)match(input,70,FOLLOW_70_in_literal3611); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal142_tree = (Object)adaptor.create(string_literal142);
                    adaptor.addChild(root_0, string_literal142_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:748:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final JavaParser.integerLiteral_return integerLiteral() throws RecognitionException {
        JavaParser.integerLiteral_return retval = new JavaParser.integerLiteral_return();
        retval.start = input.LT(1);
        int integerLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set143=null;

        Object set143_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set143=(Token)input.LT(1);
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set143));
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
        public Expression element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "booleanLiteral"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:1: booleanLiteral returns [Expression element] : ( 'true' | 'false' );
    public final JavaParser.booleanLiteral_return booleanLiteral() throws RecognitionException {
        JavaParser.booleanLiteral_return retval = new JavaParser.booleanLiteral_return();
        retval.start = input.LT(1);
        int booleanLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal144=null;
        Token string_literal145=null;

        Object string_literal144_tree=null;
        Object string_literal145_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:755:5: ( 'true' | 'false' )
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==71) ) {
                alt88=1;
            }
            else if ( (LA88_0==72) ) {
                alt88=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }
            switch (alt88) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:755:9: 'true'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal144=(Token)match(input,71,FOLLOW_71_in_booleanLiteral3673); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal144_tree = (Object)adaptor.create(string_literal144);
                    adaptor.addChild(root_0, string_literal144_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new RegularLiteral(new JavaTypeReference("boolean"),"true");
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:756:9: 'false'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal145=(Token)match(input,72,FOLLOW_72_in_booleanLiteral3685); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal145_tree = (Object)adaptor.create(string_literal145);
                    adaptor.addChild(root_0, string_literal145_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new RegularLiteral(new JavaTypeReference("boolean"),"false");
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:761:1: annotations : ( annotation )+ ;
    public final JavaParser.annotations_return annotations() throws RecognitionException {
        JavaParser.annotations_return retval = new JavaParser.annotations_return();
        retval.start = input.LT(1);
        int annotations_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotation_return annotation146 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:5: ( ( annotation )+ )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:9: ( annotation )+
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:9: ( annotation )+
            int cnt89=0;
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==73) ) {
                    int LA89_2 = input.LA(2);

                    if ( (LA89_2==Identifier) ) {
                        int LA89_3 = input.LA(3);

                        if ( (synpred123_Java()) ) {
                            alt89=1;
                        }


                    }


                }


                switch (alt89) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations3708);
            	    annotation146=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation146.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt89 >= 1 ) break loop89;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(89, input);
                        throw eee;
                }
                cnt89++;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:765:1: annotation : '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final JavaParser.annotation_return annotation() throws RecognitionException {
        JavaParser.annotation_return retval = new JavaParser.annotation_return();
        retval.start = input.LT(1);
        int annotation_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal147=null;
        Token char_literal149=null;
        Token char_literal152=null;
        JavaParser.annotationName_return annotationName148 = null;

        JavaParser.elementValuePairs_return elementValuePairs150 = null;

        JavaParser.elementValue_return elementValue151 = null;


        Object char_literal147_tree=null;
        Object char_literal149_tree=null;
        Object char_literal152_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:5: ( '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:9: '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (Object)adaptor.nil();

            if ( state.backtracking==0 ) {
              throw new Error("annotations are not yet supported");
            }
            char_literal147=(Token)match(input,73,FOLLOW_73_in_annotation3730); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal147_tree = (Object)adaptor.create(char_literal147);
            adaptor.addChild(root_0, char_literal147_tree);
            }
            pushFollow(FOLLOW_annotationName_in_annotation3732);
            annotationName148=annotationName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationName148.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:84: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt91=2;
            int LA91_0 = input.LA(1);

            if ( (LA91_0==66) ) {
                alt91=1;
            }
            switch (alt91) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:86: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal149=(Token)match(input,66,FOLLOW_66_in_annotation3736); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal149_tree = (Object)adaptor.create(char_literal149);
                    adaptor.addChild(root_0, char_literal149_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:90: ( elementValuePairs | elementValue )?
                    int alt90=3;
                    int LA90_0 = input.LA(1);

                    if ( (LA90_0==Identifier) ) {
                        int LA90_1 = input.LA(2);

                        if ( (LA90_1==51) ) {
                            alt90=1;
                        }
                        else if ( ((LA90_1>=29 && LA90_1<=30)||LA90_1==40||(LA90_1>=42 && LA90_1<=43)||LA90_1==48||LA90_1==64||(LA90_1>=66 && LA90_1<=67)||(LA90_1>=98 && LA90_1<=110)) ) {
                            alt90=2;
                        }
                    }
                    else if ( ((LA90_0>=FloatingPointLiteral && LA90_0<=DecimalLiteral)||LA90_0==44||LA90_0==47||(LA90_0>=56 && LA90_0<=63)||(LA90_0>=65 && LA90_0<=66)||(LA90_0>=69 && LA90_0<=73)||(LA90_0>=105 && LA90_0<=106)||(LA90_0>=109 && LA90_0<=113)) ) {
                        alt90=2;
                    }
                    switch (alt90) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:92: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation3740);
                            elementValuePairs150=elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePairs150.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:766:112: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation3744);
                            elementValue151=elementValue();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue151.getTree());

                            }
                            break;

                    }

                    char_literal152=(Token)match(input,67,FOLLOW_67_in_annotation3749); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal152_tree = (Object)adaptor.create(char_literal152);
                    adaptor.addChild(root_0, char_literal152_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:769:1: annotationName : Identifier ( '.' Identifier )* ;
    public final JavaParser.annotationName_return annotationName() throws RecognitionException {
        JavaParser.annotationName_return retval = new JavaParser.annotationName_return();
        retval.start = input.LT(1);
        int annotationName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier153=null;
        Token char_literal154=null;
        Token Identifier155=null;

        Object Identifier153_tree=null;
        Object char_literal154_tree=null;
        Object Identifier155_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:770:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:770:7: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier153=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName3773); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier153_tree = (Object)adaptor.create(Identifier153);
            adaptor.addChild(root_0, Identifier153_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:770:18: ( '.' Identifier )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==29) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:770:19: '.' Identifier
            	    {
            	    char_literal154=(Token)match(input,29,FOLLOW_29_in_annotationName3776); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal154_tree = (Object)adaptor.create(char_literal154);
            	    adaptor.addChild(root_0, char_literal154_tree);
            	    }
            	    Identifier155=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName3778); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier155_tree = (Object)adaptor.create(Identifier155);
            	    adaptor.addChild(root_0, Identifier155_tree);
            	    }

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:773:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final JavaParser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        JavaParser.elementValuePairs_return retval = new JavaParser.elementValuePairs_return();
        retval.start = input.LT(1);
        int elementValuePairs_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal157=null;
        JavaParser.elementValuePair_return elementValuePair156 = null;

        JavaParser.elementValuePair_return elementValuePair158 = null;


        Object char_literal157_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:774:5: ( elementValuePair ( ',' elementValuePair )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:774:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3799);
            elementValuePair156=elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair156.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:774:26: ( ',' elementValuePair )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==41) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:774:27: ',' elementValuePair
            	    {
            	    char_literal157=(Token)match(input,41,FOLLOW_41_in_elementValuePairs3802); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal157_tree = (Object)adaptor.create(char_literal157);
            	    adaptor.addChild(root_0, char_literal157_tree);
            	    }
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3804);
            	    elementValuePair158=elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair158.getTree());

            	    }
            	    break;

            	default :
            	    break loop93;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:777:1: elementValuePair : Identifier '=' elementValue ;
    public final JavaParser.elementValuePair_return elementValuePair() throws RecognitionException {
        JavaParser.elementValuePair_return retval = new JavaParser.elementValuePair_return();
        retval.start = input.LT(1);
        int elementValuePair_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier159=null;
        Token char_literal160=null;
        JavaParser.elementValue_return elementValue161 = null;


        Object Identifier159_tree=null;
        Object char_literal160_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:778:5: ( Identifier '=' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:778:9: Identifier '=' elementValue
            {
            root_0 = (Object)adaptor.nil();

            Identifier159=(Token)match(input,Identifier,FOLLOW_Identifier_in_elementValuePair3825); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier159_tree = (Object)adaptor.create(Identifier159);
            adaptor.addChild(root_0, Identifier159_tree);
            }
            char_literal160=(Token)match(input,51,FOLLOW_51_in_elementValuePair3827); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal160_tree = (Object)adaptor.create(char_literal160);
            adaptor.addChild(root_0, char_literal160_tree);
            }
            pushFollow(FOLLOW_elementValue_in_elementValuePair3829);
            elementValue161=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue161.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:781:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final JavaParser.elementValue_return elementValue() throws RecognitionException {
        JavaParser.elementValue_return retval = new JavaParser.elementValue_return();
        retval.start = input.LT(1);
        int elementValue_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression162 = null;

        JavaParser.annotation_return annotation163 = null;

        JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer164 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:782:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt94=3;
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
                alt94=1;
                }
                break;
            case 73:
                {
                alt94=2;
                }
                break;
            case 44:
                {
                alt94=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }

            switch (alt94) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:782:9: conditionalExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3852);
                    conditionalExpression162=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression162.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:783:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_elementValue3862);
                    annotation163=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation163.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:784:9: elementValueArrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue3872);
                    elementValueArrayInitializer164=elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer164.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:787:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        JavaParser.elementValueArrayInitializer_return retval = new JavaParser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);
        int elementValueArrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal165=null;
        Token char_literal167=null;
        Token char_literal169=null;
        Token char_literal170=null;
        JavaParser.elementValue_return elementValue166 = null;

        JavaParser.elementValue_return elementValue168 = null;


        Object char_literal165_tree=null;
        Object char_literal167_tree=null;
        Object char_literal169_tree=null;
        Object char_literal170_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal165=(Token)match(input,44,FOLLOW_44_in_elementValueArrayInitializer3895); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal165_tree = (Object)adaptor.create(char_literal165);
            adaptor.addChild(root_0, char_literal165_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:13: ( elementValue ( ',' elementValue )* )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==Identifier||(LA96_0>=FloatingPointLiteral && LA96_0<=DecimalLiteral)||LA96_0==44||LA96_0==47||(LA96_0>=56 && LA96_0<=63)||(LA96_0>=65 && LA96_0<=66)||(LA96_0>=69 && LA96_0<=73)||(LA96_0>=105 && LA96_0<=106)||(LA96_0>=109 && LA96_0<=113)) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:14: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3898);
                    elementValue166=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue166.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:27: ( ',' elementValue )*
                    loop95:
                    do {
                        int alt95=2;
                        int LA95_0 = input.LA(1);

                        if ( (LA95_0==41) ) {
                            int LA95_1 = input.LA(2);

                            if ( (LA95_1==Identifier||(LA95_1>=FloatingPointLiteral && LA95_1<=DecimalLiteral)||LA95_1==44||LA95_1==47||(LA95_1>=56 && LA95_1<=63)||(LA95_1>=65 && LA95_1<=66)||(LA95_1>=69 && LA95_1<=73)||(LA95_1>=105 && LA95_1<=106)||(LA95_1>=109 && LA95_1<=113)) ) {
                                alt95=1;
                            }


                        }


                        switch (alt95) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:28: ',' elementValue
                    	    {
                    	    char_literal167=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer3901); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal167_tree = (Object)adaptor.create(char_literal167);
                    	    adaptor.addChild(root_0, char_literal167_tree);
                    	    }
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3903);
                    	    elementValue168=elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue168.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop95;
                        }
                    } while (true);


                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:49: ( ',' )?
            int alt97=2;
            int LA97_0 = input.LA(1);

            if ( (LA97_0==41) ) {
                alt97=1;
            }
            switch (alt97) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:50: ','
                    {
                    char_literal169=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer3910); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal169_tree = (Object)adaptor.create(char_literal169);
                    adaptor.addChild(root_0, char_literal169_tree);
                    }

                    }
                    break;

            }

            char_literal170=(Token)match(input,45,FOLLOW_45_in_elementValueArrayInitializer3914); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal170_tree = (Object)adaptor.create(char_literal170);
            adaptor.addChild(root_0, char_literal170_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:791:1: annotationTypeDeclaration returns [Type element] : '@' 'interface' Identifier annotationTypeBody ;
    public final JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        JavaParser.annotationTypeDeclaration_return retval = new JavaParser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal171=null;
        Token string_literal172=null;
        Token Identifier173=null;
        JavaParser.annotationTypeBody_return annotationTypeBody174 = null;


        Object char_literal171_tree=null;
        Object string_literal172_tree=null;
        Object Identifier173_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:5: ( '@' 'interface' Identifier annotationTypeBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:9: '@' 'interface' Identifier annotationTypeBody
            {
            root_0 = (Object)adaptor.nil();

            char_literal171=(Token)match(input,73,FOLLOW_73_in_annotationTypeDeclaration3941); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal171_tree = (Object)adaptor.create(char_literal171);
            adaptor.addChild(root_0, char_literal171_tree);
            }
            string_literal172=(Token)match(input,46,FOLLOW_46_in_annotationTypeDeclaration3943); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal172_tree = (Object)adaptor.create(string_literal172);
            adaptor.addChild(root_0, string_literal172_tree);
            }
            Identifier173=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration3945); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier173_tree = (Object)adaptor.create(Identifier173);
            adaptor.addChild(root_0, Identifier173_tree);
            }
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3947);
            annotationTypeBody174=annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody174.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:795:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final JavaParser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        JavaParser.annotationTypeBody_return retval = new JavaParser.annotationTypeBody_return();
        retval.start = input.LT(1);
        int annotationTypeBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal175=null;
        Token char_literal177=null;
        JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration176 = null;


        Object char_literal175_tree=null;
        Object char_literal177_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:796:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:796:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal175=(Token)match(input,44,FOLLOW_44_in_annotationTypeBody3970); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal175_tree = (Object)adaptor.create(char_literal175);
            adaptor.addChild(root_0, char_literal175_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:796:13: ( annotationTypeElementDeclaration )*
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( ((LA98_0>=Identifier && LA98_0<=ENUM)||LA98_0==28||(LA98_0>=31 && LA98_0<=37)||LA98_0==40||(LA98_0>=46 && LA98_0<=47)||(LA98_0>=52 && LA98_0<=63)||LA98_0==73) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:796:14: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3973);
            	    annotationTypeElementDeclaration176=annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration176.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);

            char_literal177=(Token)match(input,45,FOLLOW_45_in_annotationTypeBody3977); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal177_tree = (Object)adaptor.create(char_literal177);
            adaptor.addChild(root_0, char_literal177_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:799:1: annotationTypeElementDeclaration : modifiers annotationTypeElementRest ;
    public final JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        JavaParser.annotationTypeElementDeclaration_return retval = new JavaParser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeElementDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifiers_return modifiers178 = null;

        JavaParser.annotationTypeElementRest_return annotationTypeElementRest179 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:5: ( modifiers annotationTypeElementRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:9: modifiers annotationTypeElementRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationTypeElementDeclaration4000);
            modifiers178=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers178.getTree());
            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration4002);
            annotationTypeElementRest179=annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementRest179.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:803:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final JavaParser.annotationTypeElementRest_return annotationTypeElementRest() throws RecognitionException {
        JavaParser.annotationTypeElementRest_return retval = new JavaParser.annotationTypeElementRest_return();
        retval.start = input.LT(1);
        int annotationTypeElementRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal182=null;
        Token char_literal184=null;
        Token char_literal186=null;
        Token char_literal188=null;
        Token char_literal190=null;
        JavaParser.type_return type180 = null;

        JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest181 = null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration183 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration185 = null;

        JavaParser.enumDeclaration_return enumDeclaration187 = null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration189 = null;


        Object char_literal182_tree=null;
        Object char_literal184_tree=null;
        Object char_literal186_tree=null;
        Object char_literal188_tree=null;
        Object char_literal190_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:5: ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
            int alt103=5;
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
                alt103=1;
                }
                break;
            case 37:
                {
                alt103=2;
                }
                break;
            case 46:
                {
                alt103=3;
                }
                break;
            case ENUM:
                {
                alt103=4;
                }
                break;
            case 73:
                {
                alt103=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 103, 0, input);

                throw nvae;
            }

            switch (alt103) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:9: type annotationMethodOrConstantRest ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_annotationTypeElementRest4025);
                    type180=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type180.getTree());
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest4027);
                    annotationMethodOrConstantRest181=annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodOrConstantRest181.getTree());
                    char_literal182=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest4029); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal182_tree = (Object)adaptor.create(char_literal182);
                    adaptor.addChild(root_0, char_literal182_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:9: normalClassDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementRest4039);
                    normalClassDeclaration183=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration183.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:32: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==26) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal184=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest4041); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal184_tree = (Object)adaptor.create(char_literal184);
                            adaptor.addChild(root_0, char_literal184_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:806:9: normalInterfaceDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest4052);
                    normalInterfaceDeclaration185=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration185.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:806:36: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==26) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal186=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest4054); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal186_tree = (Object)adaptor.create(char_literal186);
                            adaptor.addChild(root_0, char_literal186_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:807:9: enumDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest4065);
                    enumDeclaration187=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration187.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:807:25: ( ';' )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==26) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal188=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest4067); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal188_tree = (Object)adaptor.create(char_literal188);
                            adaptor.addChild(root_0, char_literal188_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:808:9: annotationTypeDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest4078);
                    annotationTypeDeclaration189=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration189.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:808:35: ( ';' )?
                    int alt102=2;
                    int LA102_0 = input.LA(1);

                    if ( (LA102_0==26) ) {
                        alt102=1;
                    }
                    switch (alt102) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal190=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest4080); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal190_tree = (Object)adaptor.create(char_literal190);
                            adaptor.addChild(root_0, char_literal190_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:811:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest() throws RecognitionException {
        JavaParser.annotationMethodOrConstantRest_return retval = new JavaParser.annotationMethodOrConstantRest_return();
        retval.start = input.LT(1);
        int annotationMethodOrConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotationMethodRest_return annotationMethodRest191 = null;

        JavaParser.annotationConstantRest_return annotationConstantRest192 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:812:5: ( annotationMethodRest | annotationConstantRest )
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==Identifier) ) {
                int LA104_1 = input.LA(2);

                if ( (LA104_1==66) ) {
                    alt104=1;
                }
                else if ( (LA104_1==26||LA104_1==41||LA104_1==48||LA104_1==51) ) {
                    alt104=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 104, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 104, 0, input);

                throw nvae;
            }
            switch (alt104) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:812:9: annotationMethodRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest4104);
                    annotationMethodRest191=annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodRest191.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:9: annotationConstantRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest4114);
                    annotationConstantRest192=annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationConstantRest192.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:816:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final JavaParser.annotationMethodRest_return annotationMethodRest() throws RecognitionException {
        JavaParser.annotationMethodRest_return retval = new JavaParser.annotationMethodRest_return();
        retval.start = input.LT(1);
        int annotationMethodRest_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier193=null;
        Token char_literal194=null;
        Token char_literal195=null;
        JavaParser.defaultValue_return defaultValue196 = null;


        Object Identifier193_tree=null;
        Object char_literal194_tree=null;
        Object char_literal195_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:5: ( Identifier '(' ')' ( defaultValue )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:9: Identifier '(' ')' ( defaultValue )?
            {
            root_0 = (Object)adaptor.nil();

            Identifier193=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest4137); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier193_tree = (Object)adaptor.create(Identifier193);
            adaptor.addChild(root_0, Identifier193_tree);
            }
            char_literal194=(Token)match(input,66,FOLLOW_66_in_annotationMethodRest4139); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal194_tree = (Object)adaptor.create(char_literal194);
            adaptor.addChild(root_0, char_literal194_tree);
            }
            char_literal195=(Token)match(input,67,FOLLOW_67_in_annotationMethodRest4141); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal195_tree = (Object)adaptor.create(char_literal195);
            adaptor.addChild(root_0, char_literal195_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:28: ( defaultValue )?
            int alt105=2;
            int LA105_0 = input.LA(1);

            if ( (LA105_0==74) ) {
                alt105=1;
            }
            switch (alt105) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest4143);
                    defaultValue196=defaultValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, defaultValue196.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:820:1: annotationConstantRest : variableDeclarators ;
    public final JavaParser.annotationConstantRest_return annotationConstantRest() throws RecognitionException {
        JavaParser.annotationConstantRest_return retval = new JavaParser.annotationConstantRest_return();
        retval.start = input.LT(1);
        int annotationConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableDeclarators_return variableDeclarators197 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:5: ( variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:9: variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest4167);
            variableDeclarators197=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators197.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:824:1: defaultValue : 'default' elementValue ;
    public final JavaParser.defaultValue_return defaultValue() throws RecognitionException {
        JavaParser.defaultValue_return retval = new JavaParser.defaultValue_return();
        retval.start = input.LT(1);
        int defaultValue_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal198=null;
        JavaParser.elementValue_return elementValue199 = null;


        Object string_literal198_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:5: ( 'default' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:9: 'default' elementValue
            {
            root_0 = (Object)adaptor.nil();

            string_literal198=(Token)match(input,74,FOLLOW_74_in_defaultValue4190); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal198_tree = (Object)adaptor.create(string_literal198);
            adaptor.addChild(root_0, string_literal198_tree);
            }
            pushFollow(FOLLOW_elementValue_in_defaultValue4192);
            elementValue199=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue199.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:830:1: block returns [Block element] : '{' ( blockStatement )* '}' ;
    public final JavaParser.block_return block() throws RecognitionException {
        JavaParser.block_return retval = new JavaParser.block_return();
        retval.start = input.LT(1);
        int block_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal200=null;
        Token char_literal202=null;
        JavaParser.blockStatement_return blockStatement201 = null;


        Object char_literal200_tree=null;
        Object char_literal202_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:831:5: ( '{' ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:831:9: '{' ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal200=(Token)match(input,44,FOLLOW_44_in_block4217); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal200_tree = (Object)adaptor.create(char_literal200);
            adaptor.addChild(root_0, char_literal200_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:831:13: ( blockStatement )*
            loop106:
            do {
                int alt106=2;
                int LA106_0 = input.LA(1);

                if ( ((LA106_0>=Identifier && LA106_0<=ASSERT)||LA106_0==26||LA106_0==28||(LA106_0>=31 && LA106_0<=37)||LA106_0==44||(LA106_0>=46 && LA106_0<=47)||LA106_0==53||(LA106_0>=56 && LA106_0<=63)||(LA106_0>=65 && LA106_0<=66)||(LA106_0>=69 && LA106_0<=73)||LA106_0==76||(LA106_0>=78 && LA106_0<=81)||(LA106_0>=83 && LA106_0<=87)||(LA106_0>=105 && LA106_0<=106)||(LA106_0>=109 && LA106_0<=113)) ) {
                    alt106=1;
                }


                switch (alt106) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block4219);
            	    blockStatement201=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement201.getTree());

            	    }
            	    break;

            	default :
            	    break loop106;
                }
            } while (true);

            char_literal202=(Token)match(input,45,FOLLOW_45_in_block4222); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal202_tree = (Object)adaptor.create(char_literal202);
            adaptor.addChild(root_0, char_literal202_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:834:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final JavaParser.blockStatement_return blockStatement() throws RecognitionException {
        JavaParser.blockStatement_return retval = new JavaParser.blockStatement_return();
        retval.start = input.LT(1);
        int blockStatement_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement203 = null;

        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration204 = null;

        JavaParser.statement_return statement205 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt107=3;
            alt107 = dfa107.predict(input);
            switch (alt107) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:9: localVariableDeclarationStatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement4245);
                    localVariableDeclarationStatement203=localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement203.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:836:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement4255);
                    classOrInterfaceDeclaration204=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration204.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:9: statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statement_in_blockStatement4265);
                    statement205=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement205.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:840:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        JavaParser.localVariableDeclarationStatement_return retval = new JavaParser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);
        int localVariableDeclarationStatement_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal207=null;
        JavaParser.localVariableDeclaration_return localVariableDeclaration206 = null;


        Object char_literal207_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:5: ( localVariableDeclaration ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:10: localVariableDeclaration ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4289);
            localVariableDeclaration206=localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration206.getTree());
            char_literal207=(Token)match(input,26,FOLLOW_26_in_localVariableDeclarationStatement4291); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal207_tree = (Object)adaptor.create(char_literal207);
            adaptor.addChild(root_0, char_literal207_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:1: localVariableDeclaration : variableModifiers type variableDeclarators ;
    public final JavaParser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        JavaParser.localVariableDeclaration_return retval = new JavaParser.localVariableDeclaration_return();
        retval.start = input.LT(1);
        int localVariableDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers208 = null;

        JavaParser.type_return type209 = null;

        JavaParser.variableDeclarators_return variableDeclarators210 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:5: ( variableModifiers type variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:9: variableModifiers type variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4310);
            variableModifiers208=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers208.getTree());
            pushFollow(FOLLOW_type_in_localVariableDeclaration4312);
            type209=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type209.getTree());
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration4314);
            variableDeclarators210=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators210.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:848:1: variableModifiers : ( variableModifier )* ;
    public final JavaParser.variableModifiers_return variableModifiers() throws RecognitionException {
        JavaParser.variableModifiers_return retval = new JavaParser.variableModifiers_return();
        retval.start = input.LT(1);
        int variableModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifier_return variableModifier211 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:5: ( ( variableModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:9: ( variableModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:9: ( variableModifier )*
            loop108:
            do {
                int alt108=2;
                int LA108_0 = input.LA(1);

                if ( (LA108_0==35||LA108_0==73) ) {
                    alt108=1;
                }


                switch (alt108) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_variableModifiers4337);
            	    variableModifier211=variableModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifier211.getTree());

            	    }
            	    break;

            	default :
            	    break loop108;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:852:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );
    public final JavaParser.statement_return statement() throws RecognitionException {
        JavaParser.statement_return retval = new JavaParser.statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        Object root_0 = null;

        Token ASSERT213=null;
        Token char_literal215=null;
        Token char_literal217=null;
        Token string_literal218=null;
        Token string_literal221=null;
        Token string_literal223=null;
        Token char_literal224=null;
        Token char_literal226=null;
        Token string_literal228=null;
        Token string_literal231=null;
        Token string_literal233=null;
        Token char_literal235=null;
        Token string_literal236=null;
        Token string_literal239=null;
        Token string_literal242=null;
        Token string_literal244=null;
        Token char_literal246=null;
        Token char_literal248=null;
        Token string_literal249=null;
        Token string_literal252=null;
        Token char_literal254=null;
        Token string_literal255=null;
        Token char_literal257=null;
        Token string_literal258=null;
        Token Identifier259=null;
        Token char_literal260=null;
        Token string_literal261=null;
        Token Identifier262=null;
        Token char_literal263=null;
        Token char_literal264=null;
        Token char_literal266=null;
        Token Identifier267=null;
        Token char_literal268=null;
        JavaParser.block_return block212 = null;

        JavaParser.expression_return expression214 = null;

        JavaParser.expression_return expression216 = null;

        JavaParser.parExpression_return parExpression219 = null;

        JavaParser.statement_return statement220 = null;

        JavaParser.statement_return statement222 = null;

        JavaParser.forControl_return forControl225 = null;

        JavaParser.statement_return statement227 = null;

        JavaParser.parExpression_return parExpression229 = null;

        JavaParser.statement_return statement230 = null;

        JavaParser.statement_return statement232 = null;

        JavaParser.parExpression_return parExpression234 = null;

        JavaParser.block_return block237 = null;

        JavaParser.catches_return catches238 = null;

        JavaParser.block_return block240 = null;

        JavaParser.catches_return catches241 = null;

        JavaParser.block_return block243 = null;

        JavaParser.parExpression_return parExpression245 = null;

        JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups247 = null;

        JavaParser.parExpression_return parExpression250 = null;

        JavaParser.block_return block251 = null;

        JavaParser.expression_return expression253 = null;

        JavaParser.expression_return expression256 = null;

        JavaParser.statementExpression_return statementExpression265 = null;

        JavaParser.statement_return statement269 = null;


        Object ASSERT213_tree=null;
        Object char_literal215_tree=null;
        Object char_literal217_tree=null;
        Object string_literal218_tree=null;
        Object string_literal221_tree=null;
        Object string_literal223_tree=null;
        Object char_literal224_tree=null;
        Object char_literal226_tree=null;
        Object string_literal228_tree=null;
        Object string_literal231_tree=null;
        Object string_literal233_tree=null;
        Object char_literal235_tree=null;
        Object string_literal236_tree=null;
        Object string_literal239_tree=null;
        Object string_literal242_tree=null;
        Object string_literal244_tree=null;
        Object char_literal246_tree=null;
        Object char_literal248_tree=null;
        Object string_literal249_tree=null;
        Object string_literal252_tree=null;
        Object char_literal254_tree=null;
        Object string_literal255_tree=null;
        Object char_literal257_tree=null;
        Object string_literal258_tree=null;
        Object Identifier259_tree=null;
        Object char_literal260_tree=null;
        Object string_literal261_tree=null;
        Object Identifier262_tree=null;
        Object char_literal263_tree=null;
        Object char_literal264_tree=null;
        Object char_literal266_tree=null;
        Object Identifier267_tree=null;
        Object char_literal268_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:5: ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement )
            int alt115=16;
            alt115 = dfa115.predict(input);
            switch (alt115) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:7: block
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_block_in_statement4355);
                    block212=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block212.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:854:9: ASSERT expression ( ':' expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    ASSERT213=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement4365); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSERT213_tree = (Object)adaptor.create(ASSERT213);
                    adaptor.addChild(root_0, ASSERT213_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement4367);
                    expression214=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression214.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:854:27: ( ':' expression )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==75) ) {
                        alt109=1;
                    }
                    switch (alt109) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:854:28: ':' expression
                            {
                            char_literal215=(Token)match(input,75,FOLLOW_75_in_statement4370); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal215_tree = (Object)adaptor.create(char_literal215);
                            adaptor.addChild(root_0, char_literal215_tree);
                            }
                            pushFollow(FOLLOW_expression_in_statement4372);
                            expression216=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression216.getTree());

                            }
                            break;

                    }

                    char_literal217=(Token)match(input,26,FOLLOW_26_in_statement4376); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal217_tree = (Object)adaptor.create(char_literal217);
                    adaptor.addChild(root_0, char_literal217_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:9: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal218=(Token)match(input,76,FOLLOW_76_in_statement4386); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal218_tree = (Object)adaptor.create(string_literal218);
                    adaptor.addChild(root_0, string_literal218_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4388);
                    parExpression219=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression219.getTree());
                    pushFollow(FOLLOW_statement_in_statement4390);
                    statement220=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement220.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:38: ( options {k=1; } : 'else' statement )?
                    int alt110=2;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==77) ) {
                        int LA110_2 = input.LA(2);

                        if ( (synpred152_Java()) ) {
                            alt110=1;
                        }
                    }
                    switch (alt110) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:54: 'else' statement
                            {
                            string_literal221=(Token)match(input,77,FOLLOW_77_in_statement4400); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal221_tree = (Object)adaptor.create(string_literal221);
                            adaptor.addChild(root_0, string_literal221_tree);
                            }
                            pushFollow(FOLLOW_statement_in_statement4402);
                            statement222=statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, statement222.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:856:9: 'for' '(' forControl ')' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal223=(Token)match(input,78,FOLLOW_78_in_statement4414); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal223_tree = (Object)adaptor.create(string_literal223);
                    adaptor.addChild(root_0, string_literal223_tree);
                    }
                    char_literal224=(Token)match(input,66,FOLLOW_66_in_statement4416); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal224_tree = (Object)adaptor.create(char_literal224);
                    adaptor.addChild(root_0, char_literal224_tree);
                    }
                    pushFollow(FOLLOW_forControl_in_statement4418);
                    forControl225=forControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, forControl225.getTree());
                    char_literal226=(Token)match(input,67,FOLLOW_67_in_statement4420); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal226_tree = (Object)adaptor.create(char_literal226);
                    adaptor.addChild(root_0, char_literal226_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4422);
                    statement227=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement227.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:857:9: 'while' parExpression statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal228=(Token)match(input,79,FOLLOW_79_in_statement4432); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal228_tree = (Object)adaptor.create(string_literal228);
                    adaptor.addChild(root_0, string_literal228_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4434);
                    parExpression229=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression229.getTree());
                    pushFollow(FOLLOW_statement_in_statement4436);
                    statement230=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement230.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:858:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal231=(Token)match(input,80,FOLLOW_80_in_statement4446); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal231_tree = (Object)adaptor.create(string_literal231);
                    adaptor.addChild(root_0, string_literal231_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4448);
                    statement232=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement232.getTree());
                    string_literal233=(Token)match(input,79,FOLLOW_79_in_statement4450); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal233_tree = (Object)adaptor.create(string_literal233);
                    adaptor.addChild(root_0, string_literal233_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4452);
                    parExpression234=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression234.getTree());
                    char_literal235=(Token)match(input,26,FOLLOW_26_in_statement4454); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal235_tree = (Object)adaptor.create(char_literal235);
                    adaptor.addChild(root_0, char_literal235_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:859:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal236=(Token)match(input,81,FOLLOW_81_in_statement4464); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal236_tree = (Object)adaptor.create(string_literal236);
                    adaptor.addChild(root_0, string_literal236_tree);
                    }
                    pushFollow(FOLLOW_block_in_statement4466);
                    block237=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block237.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:9: ( catches 'finally' block | catches | 'finally' block )
                    int alt111=3;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==88) ) {
                        int LA111_1 = input.LA(2);

                        if ( (synpred157_Java()) ) {
                            alt111=1;
                        }
                        else if ( (synpred158_Java()) ) {
                            alt111=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 111, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA111_0==82) ) {
                        alt111=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 111, 0, input);

                        throw nvae;
                    }
                    switch (alt111) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:11: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement4478);
                            catches238=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches238.getTree());
                            string_literal239=(Token)match(input,82,FOLLOW_82_in_statement4480); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal239_tree = (Object)adaptor.create(string_literal239);
                            adaptor.addChild(root_0, string_literal239_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement4482);
                            block240=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block240.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:861:11: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement4494);
                            catches241=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches241.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:862:13: 'finally' block
                            {
                            string_literal242=(Token)match(input,82,FOLLOW_82_in_statement4508); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal242_tree = (Object)adaptor.create(string_literal242);
                            adaptor.addChild(root_0, string_literal242_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement4510);
                            block243=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block243.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal244=(Token)match(input,83,FOLLOW_83_in_statement4530); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal244_tree = (Object)adaptor.create(string_literal244);
                    adaptor.addChild(root_0, string_literal244_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4532);
                    parExpression245=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression245.getTree());
                    char_literal246=(Token)match(input,44,FOLLOW_44_in_statement4534); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal246_tree = (Object)adaptor.create(char_literal246);
                    adaptor.addChild(root_0, char_literal246_tree);
                    }
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4536);
                    switchBlockStatementGroups247=switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups247.getTree());
                    char_literal248=(Token)match(input,45,FOLLOW_45_in_statement4538); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal248_tree = (Object)adaptor.create(char_literal248);
                    adaptor.addChild(root_0, char_literal248_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:865:9: 'synchronized' parExpression block
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal249=(Token)match(input,53,FOLLOW_53_in_statement4548); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal249_tree = (Object)adaptor.create(string_literal249);
                    adaptor.addChild(root_0, string_literal249_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4550);
                    parExpression250=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression250.getTree());
                    pushFollow(FOLLOW_block_in_statement4552);
                    block251=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block251.getTree());

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:866:9: 'return' ( expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal252=(Token)match(input,84,FOLLOW_84_in_statement4562); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal252_tree = (Object)adaptor.create(string_literal252);
                    adaptor.addChild(root_0, string_literal252_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:866:18: ( expression )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier||(LA112_0>=FloatingPointLiteral && LA112_0<=DecimalLiteral)||LA112_0==47||(LA112_0>=56 && LA112_0<=63)||(LA112_0>=65 && LA112_0<=66)||(LA112_0>=69 && LA112_0<=72)||(LA112_0>=105 && LA112_0<=106)||(LA112_0>=109 && LA112_0<=113)) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement4564);
                            expression253=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression253.getTree());

                            }
                            break;

                    }

                    char_literal254=(Token)match(input,26,FOLLOW_26_in_statement4567); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal254_tree = (Object)adaptor.create(char_literal254);
                    adaptor.addChild(root_0, char_literal254_tree);
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:867:9: 'throw' expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal255=(Token)match(input,85,FOLLOW_85_in_statement4577); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal255_tree = (Object)adaptor.create(string_literal255);
                    adaptor.addChild(root_0, string_literal255_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement4579);
                    expression256=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression256.getTree());
                    char_literal257=(Token)match(input,26,FOLLOW_26_in_statement4581); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal257_tree = (Object)adaptor.create(char_literal257);
                    adaptor.addChild(root_0, char_literal257_tree);
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:9: 'break' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal258=(Token)match(input,86,FOLLOW_86_in_statement4591); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal258_tree = (Object)adaptor.create(string_literal258);
                    adaptor.addChild(root_0, string_literal258_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:17: ( Identifier )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==Identifier) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier259=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement4593); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier259_tree = (Object)adaptor.create(Identifier259);
                            adaptor.addChild(root_0, Identifier259_tree);
                            }

                            }
                            break;

                    }

                    char_literal260=(Token)match(input,26,FOLLOW_26_in_statement4596); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal260_tree = (Object)adaptor.create(char_literal260);
                    adaptor.addChild(root_0, char_literal260_tree);
                    }

                    }
                    break;
                case 13 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:869:9: 'continue' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal261=(Token)match(input,87,FOLLOW_87_in_statement4606); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal261_tree = (Object)adaptor.create(string_literal261);
                    adaptor.addChild(root_0, string_literal261_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:869:20: ( Identifier )?
                    int alt114=2;
                    int LA114_0 = input.LA(1);

                    if ( (LA114_0==Identifier) ) {
                        alt114=1;
                    }
                    switch (alt114) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier262=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement4608); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier262_tree = (Object)adaptor.create(Identifier262);
                            adaptor.addChild(root_0, Identifier262_tree);
                            }

                            }
                            break;

                    }

                    char_literal263=(Token)match(input,26,FOLLOW_26_in_statement4611); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal263_tree = (Object)adaptor.create(char_literal263);
                    adaptor.addChild(root_0, char_literal263_tree);
                    }

                    }
                    break;
                case 14 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:870:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal264=(Token)match(input,26,FOLLOW_26_in_statement4621); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal264_tree = (Object)adaptor.create(char_literal264);
                    adaptor.addChild(root_0, char_literal264_tree);
                    }

                    }
                    break;
                case 15 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:871:9: statementExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statementExpression_in_statement4632);
                    statementExpression265=statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementExpression265.getTree());
                    char_literal266=(Token)match(input,26,FOLLOW_26_in_statement4634); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal266_tree = (Object)adaptor.create(char_literal266);
                    adaptor.addChild(root_0, char_literal266_tree);
                    }

                    }
                    break;
                case 16 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:9: Identifier ':' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier267=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement4644); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier267_tree = (Object)adaptor.create(Identifier267);
                    adaptor.addChild(root_0, Identifier267_tree);
                    }
                    char_literal268=(Token)match(input,75,FOLLOW_75_in_statement4646); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal268_tree = (Object)adaptor.create(char_literal268);
                    adaptor.addChild(root_0, char_literal268_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4648);
                    statement269=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement269.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:875:1: catches : catchClause ( catchClause )* ;
    public final JavaParser.catches_return catches() throws RecognitionException {
        JavaParser.catches_return retval = new JavaParser.catches_return();
        retval.start = input.LT(1);
        int catches_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.catchClause_return catchClause270 = null;

        JavaParser.catchClause_return catchClause271 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:5: ( catchClause ( catchClause )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:9: catchClause ( catchClause )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_catchClause_in_catches4671);
            catchClause270=catchClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause270.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:21: ( catchClause )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==88) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:22: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches4674);
            	    catchClause271=catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause271.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final JavaParser.catchClause_return catchClause() throws RecognitionException {
        JavaParser.catchClause_return retval = new JavaParser.catchClause_return();
        retval.start = input.LT(1);
        int catchClause_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal272=null;
        Token char_literal273=null;
        Token char_literal275=null;
        JavaParser.formalParameter_return formalParameter274 = null;

        JavaParser.block_return block276 = null;


        Object string_literal272_tree=null;
        Object char_literal273_tree=null;
        Object char_literal275_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:5: ( 'catch' '(' formalParameter ')' block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (Object)adaptor.nil();

            string_literal272=(Token)match(input,88,FOLLOW_88_in_catchClause4699); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal272_tree = (Object)adaptor.create(string_literal272);
            adaptor.addChild(root_0, string_literal272_tree);
            }
            char_literal273=(Token)match(input,66,FOLLOW_66_in_catchClause4701); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal273_tree = (Object)adaptor.create(char_literal273);
            adaptor.addChild(root_0, char_literal273_tree);
            }
            pushFollow(FOLLOW_formalParameter_in_catchClause4703);
            formalParameter274=formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameter274.getTree());
            char_literal275=(Token)match(input,67,FOLLOW_67_in_catchClause4705); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal275_tree = (Object)adaptor.create(char_literal275);
            adaptor.addChild(root_0, char_literal275_tree);
            }
            pushFollow(FOLLOW_block_in_catchClause4707);
            block276=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block276.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:1: formalParameter : variableModifiers type variableDeclaratorId ;
    public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
        JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers277 = null;

        JavaParser.type_return type278 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId279 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:5: ( variableModifiers type variableDeclaratorId )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:9: variableModifiers type variableDeclaratorId
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameter4726);
            variableModifiers277=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers277.getTree());
            pushFollow(FOLLOW_type_in_formalParameter4728);
            type278=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type278.getTree());
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter4730);
            variableDeclaratorId279=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId279.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:887:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        JavaParser.switchBlockStatementGroups_return retval = new JavaParser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroups_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup280 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:5: ( ( switchBlockStatementGroup )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:9: ( switchBlockStatementGroup )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:9: ( switchBlockStatementGroup )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==74||LA117_0==89) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4758);
            	    switchBlockStatementGroup280=switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup280.getTree());

            	    }
            	    break;

            	default :
            	    break loop117;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:895:1: switchBlockStatementGroup : ( switchLabel )+ ( blockStatement )* ;
    public final JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        JavaParser.switchBlockStatementGroup_return retval = new JavaParser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroup_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchLabel_return switchLabel281 = null;

        JavaParser.blockStatement_return blockStatement282 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:5: ( ( switchLabel )+ ( blockStatement )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: ( switchLabel )+ ( blockStatement )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: ( switchLabel )+
            int cnt118=0;
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( (LA118_0==89) ) {
                    int LA118_2 = input.LA(2);

                    if ( (synpred173_Java()) ) {
                        alt118=1;
                    }


                }
                else if ( (LA118_0==74) ) {
                    int LA118_3 = input.LA(2);

                    if ( (synpred173_Java()) ) {
                        alt118=1;
                    }


                }


                switch (alt118) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: switchLabel
            	    {
            	    pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4785);
            	    switchLabel281=switchLabel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchLabel281.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt118 >= 1 ) break loop118;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(118, input);
                        throw eee;
                }
                cnt118++;
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:22: ( blockStatement )*
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( ((LA119_0>=Identifier && LA119_0<=ASSERT)||LA119_0==26||LA119_0==28||(LA119_0>=31 && LA119_0<=37)||LA119_0==44||(LA119_0>=46 && LA119_0<=47)||LA119_0==53||(LA119_0>=56 && LA119_0<=63)||(LA119_0>=65 && LA119_0<=66)||(LA119_0>=69 && LA119_0<=73)||LA119_0==76||(LA119_0>=78 && LA119_0<=81)||(LA119_0>=83 && LA119_0<=87)||(LA119_0>=105 && LA119_0<=106)||(LA119_0>=109 && LA119_0<=113)) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4788);
            	    blockStatement282=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement282.getTree());

            	    }
            	    break;

            	default :
            	    break loop119;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:899:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final JavaParser.switchLabel_return switchLabel() throws RecognitionException {
        JavaParser.switchLabel_return retval = new JavaParser.switchLabel_return();
        retval.start = input.LT(1);
        int switchLabel_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal283=null;
        Token char_literal285=null;
        Token string_literal286=null;
        Token char_literal288=null;
        Token string_literal289=null;
        Token char_literal290=null;
        JavaParser.constantExpression_return constantExpression284 = null;

        JavaParser.enumConstantName_return enumConstantName287 = null;


        Object string_literal283_tree=null;
        Object char_literal285_tree=null;
        Object string_literal286_tree=null;
        Object char_literal288_tree=null;
        Object string_literal289_tree=null;
        Object char_literal290_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
            int alt120=3;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==89) ) {
                int LA120_1 = input.LA(2);

                if ( (LA120_1==Identifier) ) {
                    int LA120_3 = input.LA(3);

                    if ( (LA120_3==75) ) {
                        int LA120_5 = input.LA(4);

                        if ( (synpred175_Java()) ) {
                            alt120=1;
                        }
                        else if ( (synpred176_Java()) ) {
                            alt120=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 120, 5, input);

                            throw nvae;
                        }
                    }
                    else if ( ((LA120_3>=29 && LA120_3<=30)||LA120_3==40||(LA120_3>=42 && LA120_3<=43)||LA120_3==48||LA120_3==51||LA120_3==64||LA120_3==66||(LA120_3>=90 && LA120_3<=110)) ) {
                        alt120=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 120, 3, input);

                        throw nvae;
                    }
                }
                else if ( ((LA120_1>=FloatingPointLiteral && LA120_1<=DecimalLiteral)||LA120_1==47||(LA120_1>=56 && LA120_1<=63)||(LA120_1>=65 && LA120_1<=66)||(LA120_1>=69 && LA120_1<=72)||(LA120_1>=105 && LA120_1<=106)||(LA120_1>=109 && LA120_1<=113)) ) {
                    alt120=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 120, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA120_0==74) ) {
                alt120=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 120, 0, input);

                throw nvae;
            }
            switch (alt120) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:9: 'case' constantExpression ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal283=(Token)match(input,89,FOLLOW_89_in_switchLabel4812); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal283_tree = (Object)adaptor.create(string_literal283);
                    adaptor.addChild(root_0, string_literal283_tree);
                    }
                    pushFollow(FOLLOW_constantExpression_in_switchLabel4814);
                    constantExpression284=constantExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantExpression284.getTree());
                    char_literal285=(Token)match(input,75,FOLLOW_75_in_switchLabel4816); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal285_tree = (Object)adaptor.create(char_literal285);
                    adaptor.addChild(root_0, char_literal285_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:901:9: 'case' enumConstantName ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal286=(Token)match(input,89,FOLLOW_89_in_switchLabel4826); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal286_tree = (Object)adaptor.create(string_literal286);
                    adaptor.addChild(root_0, string_literal286_tree);
                    }
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel4828);
                    enumConstantName287=enumConstantName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstantName287.getTree());
                    char_literal288=(Token)match(input,75,FOLLOW_75_in_switchLabel4830); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal288_tree = (Object)adaptor.create(char_literal288);
                    adaptor.addChild(root_0, char_literal288_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:902:9: 'default' ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal289=(Token)match(input,74,FOLLOW_74_in_switchLabel4840); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal289_tree = (Object)adaptor.create(string_literal289);
                    adaptor.addChild(root_0, string_literal289_tree);
                    }
                    char_literal290=(Token)match(input,75,FOLLOW_75_in_switchLabel4842); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal290_tree = (Object)adaptor.create(char_literal290);
                    adaptor.addChild(root_0, char_literal290_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:905:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final JavaParser.forControl_return forControl() throws RecognitionException {
        JavaParser.forControl_return retval = new JavaParser.forControl_return();
        retval.start = input.LT(1);
        int forControl_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal293=null;
        Token char_literal295=null;
        JavaParser.enhancedForControl_return enhancedForControl291 = null;

        JavaParser.forInit_return forInit292 = null;

        JavaParser.expression_return expression294 = null;

        JavaParser.forUpdate_return forUpdate296 = null;


        Object char_literal293_tree=null;
        Object char_literal295_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:5: ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt124=2;
            alt124 = dfa124.predict(input);
            switch (alt124) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:9: enhancedForControl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enhancedForControl_in_forControl4873);
                    enhancedForControl291=enhancedForControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enhancedForControl291.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:9: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:9: ( forInit )?
                    int alt121=2;
                    int LA121_0 = input.LA(1);

                    if ( (LA121_0==Identifier||(LA121_0>=FloatingPointLiteral && LA121_0<=DecimalLiteral)||LA121_0==35||LA121_0==47||(LA121_0>=56 && LA121_0<=63)||(LA121_0>=65 && LA121_0<=66)||(LA121_0>=69 && LA121_0<=73)||(LA121_0>=105 && LA121_0<=106)||(LA121_0>=109 && LA121_0<=113)) ) {
                        alt121=1;
                    }
                    switch (alt121) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl4883);
                            forInit292=forInit();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forInit292.getTree());

                            }
                            break;

                    }

                    char_literal293=(Token)match(input,26,FOLLOW_26_in_forControl4886); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal293_tree = (Object)adaptor.create(char_literal293);
                    adaptor.addChild(root_0, char_literal293_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:22: ( expression )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==47||(LA122_0>=56 && LA122_0<=63)||(LA122_0>=65 && LA122_0<=66)||(LA122_0>=69 && LA122_0<=72)||(LA122_0>=105 && LA122_0<=106)||(LA122_0>=109 && LA122_0<=113)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl4888);
                            expression294=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression294.getTree());

                            }
                            break;

                    }

                    char_literal295=(Token)match(input,26,FOLLOW_26_in_forControl4891); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal295_tree = (Object)adaptor.create(char_literal295);
                    adaptor.addChild(root_0, char_literal295_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:38: ( forUpdate )?
                    int alt123=2;
                    int LA123_0 = input.LA(1);

                    if ( (LA123_0==Identifier||(LA123_0>=FloatingPointLiteral && LA123_0<=DecimalLiteral)||LA123_0==47||(LA123_0>=56 && LA123_0<=63)||(LA123_0>=65 && LA123_0<=66)||(LA123_0>=69 && LA123_0<=72)||(LA123_0>=105 && LA123_0<=106)||(LA123_0>=109 && LA123_0<=113)) ) {
                        alt123=1;
                    }
                    switch (alt123) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl4893);
                            forUpdate296=forUpdate();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forUpdate296.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:911:1: forInit : ( localVariableDeclaration | expressionList );
    public final JavaParser.forInit_return forInit() throws RecognitionException {
        JavaParser.forInit_return retval = new JavaParser.forInit_return();
        retval.start = input.LT(1);
        int forInit_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclaration_return localVariableDeclaration297 = null;

        JavaParser.expressionList_return expressionList298 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:5: ( localVariableDeclaration | expressionList )
            int alt125=2;
            alt125 = dfa125.predict(input);
            switch (alt125) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:9: localVariableDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit4913);
                    localVariableDeclaration297=localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration297.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:913:9: expressionList
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expressionList_in_forInit4923);
                    expressionList298=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList298.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:916:1: enhancedForControl : variableModifiers type Identifier ':' expression ;
    public final JavaParser.enhancedForControl_return enhancedForControl() throws RecognitionException {
        JavaParser.enhancedForControl_return retval = new JavaParser.enhancedForControl_return();
        retval.start = input.LT(1);
        int enhancedForControl_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier301=null;
        Token char_literal302=null;
        JavaParser.variableModifiers_return variableModifiers299 = null;

        JavaParser.type_return type300 = null;

        JavaParser.expression_return expression303 = null;


        Object Identifier301_tree=null;
        Object char_literal302_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:917:5: ( variableModifiers type Identifier ':' expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:917:9: variableModifiers type Identifier ':' expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_enhancedForControl4946);
            variableModifiers299=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers299.getTree());
            pushFollow(FOLLOW_type_in_enhancedForControl4948);
            type300=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type300.getTree());
            Identifier301=(Token)match(input,Identifier,FOLLOW_Identifier_in_enhancedForControl4950); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier301_tree = (Object)adaptor.create(Identifier301);
            adaptor.addChild(root_0, Identifier301_tree);
            }
            char_literal302=(Token)match(input,75,FOLLOW_75_in_enhancedForControl4952); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal302_tree = (Object)adaptor.create(char_literal302);
            adaptor.addChild(root_0, char_literal302_tree);
            }
            pushFollow(FOLLOW_expression_in_enhancedForControl4954);
            expression303=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression303.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:920:1: forUpdate : expressionList ;
    public final JavaParser.forUpdate_return forUpdate() throws RecognitionException {
        JavaParser.forUpdate_return retval = new JavaParser.forUpdate_return();
        retval.start = input.LT(1);
        int forUpdate_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expressionList_return expressionList304 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:921:5: ( expressionList )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:921:9: expressionList
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expressionList_in_forUpdate4973);
            expressionList304=expressionList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList304.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:926:1: parExpression : '(' expression ')' ;
    public final JavaParser.parExpression_return parExpression() throws RecognitionException {
        JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal305=null;
        Token char_literal307=null;
        JavaParser.expression_return expression306 = null;


        Object char_literal305_tree=null;
        Object char_literal307_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:5: ( '(' expression ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:9: '(' expression ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal305=(Token)match(input,66,FOLLOW_66_in_parExpression4994); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal305_tree = (Object)adaptor.create(char_literal305);
            adaptor.addChild(root_0, char_literal305_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression4996);
            expression306=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression306.getTree());
            char_literal307=(Token)match(input,67,FOLLOW_67_in_parExpression4998); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal307_tree = (Object)adaptor.create(char_literal307);
            adaptor.addChild(root_0, char_literal307_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:930:1: expressionList : expression ( ',' expression )* ;
    public final JavaParser.expressionList_return expressionList() throws RecognitionException {
        JavaParser.expressionList_return retval = new JavaParser.expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal309=null;
        JavaParser.expression_return expression308 = null;

        JavaParser.expression_return expression310 = null;


        Object char_literal309_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:931:5: ( expression ( ',' expression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:931:9: expression ( ',' expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList5021);
            expression308=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression308.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:931:20: ( ',' expression )*
            loop126:
            do {
                int alt126=2;
                int LA126_0 = input.LA(1);

                if ( (LA126_0==41) ) {
                    alt126=1;
                }


                switch (alt126) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:931:21: ',' expression
            	    {
            	    char_literal309=(Token)match(input,41,FOLLOW_41_in_expressionList5024); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal309_tree = (Object)adaptor.create(char_literal309);
            	    adaptor.addChild(root_0, char_literal309_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList5026);
            	    expression310=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression310.getTree());

            	    }
            	    break;

            	default :
            	    break loop126;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:1: statementExpression : expression ;
    public final JavaParser.statementExpression_return statementExpression() throws RecognitionException {
        JavaParser.statementExpression_return retval = new JavaParser.statementExpression_return();
        retval.start = input.LT(1);
        int statementExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression311 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:935:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:935:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_statementExpression5047);
            expression311=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression311.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:1: constantExpression : expression ;
    public final JavaParser.constantExpression_return constantExpression() throws RecognitionException {
        JavaParser.constantExpression_return retval = new JavaParser.constantExpression_return();
        retval.start = input.LT(1);
        int constantExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression312 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:939:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:939:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_constantExpression5070);
            expression312=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression312.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:942:1: expression returns [Expression element] : conditionalExpression ( assignmentOperator expression )? ;
    public final JavaParser.expression_return expression() throws RecognitionException {
        JavaParser.expression_return retval = new JavaParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression313 = null;

        JavaParser.assignmentOperator_return assignmentOperator314 = null;

        JavaParser.expression_return expression315 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:5: ( conditionalExpression ( assignmentOperator expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:9: conditionalExpression ( assignmentOperator expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression5097);
            conditionalExpression313=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression313.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:31: ( assignmentOperator expression )?
            int alt127=2;
            alt127 = dfa127.predict(input);
            switch (alt127) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:32: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression5100);
                    assignmentOperator314=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentOperator314.getTree());
                    pushFollow(FOLLOW_expression_in_expression5102);
                    expression315=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression315.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:946:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);
    public final JavaParser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        JavaParser.assignmentOperator_return retval = new JavaParser.assignmentOperator_return();
        retval.start = input.LT(1);
        int assignmentOperator_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token char_literal316=null;
        Token string_literal317=null;
        Token string_literal318=null;
        Token string_literal319=null;
        Token string_literal320=null;
        Token string_literal321=null;
        Token string_literal322=null;
        Token string_literal323=null;
        Token string_literal324=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object t3_tree=null;
        Object t4_tree=null;
        Object char_literal316_tree=null;
        Object string_literal317_tree=null;
        Object string_literal318_tree=null;
        Object string_literal319_tree=null;
        Object string_literal320_tree=null;
        Object string_literal321_tree=null;
        Object string_literal322_tree=null;
        Object string_literal323_tree=null;
        Object string_literal324_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:947:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?)
            int alt128=12;
            alt128 = dfa128.predict(input);
            switch (alt128) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:947:9: '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal316=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5127); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal316_tree = (Object)adaptor.create(char_literal316);
                    adaptor.addChild(root_0, char_literal316_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:948:9: '+='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal317=(Token)match(input,90,FOLLOW_90_in_assignmentOperator5137); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal317_tree = (Object)adaptor.create(string_literal317);
                    adaptor.addChild(root_0, string_literal317_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:949:9: '-='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal318=(Token)match(input,91,FOLLOW_91_in_assignmentOperator5147); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal318_tree = (Object)adaptor.create(string_literal318);
                    adaptor.addChild(root_0, string_literal318_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:950:9: '*='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal319=(Token)match(input,92,FOLLOW_92_in_assignmentOperator5157); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal319_tree = (Object)adaptor.create(string_literal319);
                    adaptor.addChild(root_0, string_literal319_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:951:9: '/='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal320=(Token)match(input,93,FOLLOW_93_in_assignmentOperator5167); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal320_tree = (Object)adaptor.create(string_literal320);
                    adaptor.addChild(root_0, string_literal320_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:9: '&='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal321=(Token)match(input,94,FOLLOW_94_in_assignmentOperator5177); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal321_tree = (Object)adaptor.create(string_literal321);
                    adaptor.addChild(root_0, string_literal321_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:953:9: '|='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal322=(Token)match(input,95,FOLLOW_95_in_assignmentOperator5187); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal322_tree = (Object)adaptor.create(string_literal322);
                    adaptor.addChild(root_0, string_literal322_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:954:9: '^='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal323=(Token)match(input,96,FOLLOW_96_in_assignmentOperator5197); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal323_tree = (Object)adaptor.create(string_literal323);
                    adaptor.addChild(root_0, string_literal323_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:955:9: '%='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal324=(Token)match(input,97,FOLLOW_97_in_assignmentOperator5207); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal324_tree = (Object)adaptor.create(string_literal324);
                    adaptor.addChild(root_0, string_literal324_tree);
                    }

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:956:9: ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_assignmentOperator5228); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_assignmentOperator5232); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5236); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:961:9: ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5270); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5274); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5278); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    t4=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5282); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:9: ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5313); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator5317); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator5321); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:975:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final JavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JavaParser.conditionalExpression_return retval = new JavaParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal326=null;
        Token char_literal328=null;
        JavaParser.conditionalOrExpression_return conditionalOrExpression325 = null;

        JavaParser.expression_return expression327 = null;

        JavaParser.expression_return expression329 = null;


        Object char_literal326_tree=null;
        Object char_literal328_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5350);
            conditionalOrExpression325=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression325.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:33: ( '?' expression ':' expression )?
            int alt129=2;
            int LA129_0 = input.LA(1);

            if ( (LA129_0==64) ) {
                alt129=1;
            }
            switch (alt129) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:35: '?' expression ':' expression
                    {
                    char_literal326=(Token)match(input,64,FOLLOW_64_in_conditionalExpression5354); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal326_tree = (Object)adaptor.create(char_literal326);
                    adaptor.addChild(root_0, char_literal326_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression5356);
                    expression327=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression327.getTree());
                    char_literal328=(Token)match(input,75,FOLLOW_75_in_conditionalExpression5358); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal328_tree = (Object)adaptor.create(char_literal328);
                    adaptor.addChild(root_0, char_literal328_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression5360);
                    expression329=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression329.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:979:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final JavaParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JavaParser.conditionalOrExpression_return retval = new JavaParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal331=null;
        JavaParser.conditionalAndExpression_return conditionalAndExpression330 = null;

        JavaParser.conditionalAndExpression_return conditionalAndExpression332 = null;


        Object string_literal331_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5382);
            conditionalAndExpression330=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression330.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:34: ( '||' conditionalAndExpression )*
            loop130:
            do {
                int alt130=2;
                int LA130_0 = input.LA(1);

                if ( (LA130_0==98) ) {
                    alt130=1;
                }


                switch (alt130) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:36: '||' conditionalAndExpression
            	    {
            	    string_literal331=(Token)match(input,98,FOLLOW_98_in_conditionalOrExpression5386); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal331_tree = (Object)adaptor.create(string_literal331);
            	    adaptor.addChild(root_0, string_literal331_tree);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5388);
            	    conditionalAndExpression332=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression332.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:983:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final JavaParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JavaParser.conditionalAndExpression_return retval = new JavaParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal334=null;
        JavaParser.inclusiveOrExpression_return inclusiveOrExpression333 = null;

        JavaParser.inclusiveOrExpression_return inclusiveOrExpression335 = null;


        Object string_literal334_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5410);
            inclusiveOrExpression333=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression333.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:31: ( '&&' inclusiveOrExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==99) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:33: '&&' inclusiveOrExpression
            	    {
            	    string_literal334=(Token)match(input,99,FOLLOW_99_in_conditionalAndExpression5414); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal334_tree = (Object)adaptor.create(string_literal334);
            	    adaptor.addChild(root_0, string_literal334_tree);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5416);
            	    inclusiveOrExpression335=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression335.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:987:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final JavaParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JavaParser.inclusiveOrExpression_return retval = new JavaParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal337=null;
        JavaParser.exclusiveOrExpression_return exclusiveOrExpression336 = null;

        JavaParser.exclusiveOrExpression_return exclusiveOrExpression338 = null;


        Object char_literal337_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5438);
            exclusiveOrExpression336=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression336.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:31: ( '|' exclusiveOrExpression )*
            loop132:
            do {
                int alt132=2;
                int LA132_0 = input.LA(1);

                if ( (LA132_0==100) ) {
                    alt132=1;
                }


                switch (alt132) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:33: '|' exclusiveOrExpression
            	    {
            	    char_literal337=(Token)match(input,100,FOLLOW_100_in_inclusiveOrExpression5442); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal337_tree = (Object)adaptor.create(char_literal337);
            	    adaptor.addChild(root_0, char_literal337_tree);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5444);
            	    exclusiveOrExpression338=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression338.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:991:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final JavaParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JavaParser.exclusiveOrExpression_return retval = new JavaParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal340=null;
        JavaParser.andExpression_return andExpression339 = null;

        JavaParser.andExpression_return andExpression341 = null;


        Object char_literal340_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:5: ( andExpression ( '^' andExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5466);
            andExpression339=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression339.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:23: ( '^' andExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==101) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:992:25: '^' andExpression
            	    {
            	    char_literal340=(Token)match(input,101,FOLLOW_101_in_exclusiveOrExpression5470); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal340_tree = (Object)adaptor.create(char_literal340);
            	    adaptor.addChild(root_0, char_literal340_tree);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5472);
            	    andExpression341=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression341.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:995:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final JavaParser.andExpression_return andExpression() throws RecognitionException {
        JavaParser.andExpression_return retval = new JavaParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal343=null;
        JavaParser.equalityExpression_return equalityExpression342 = null;

        JavaParser.equalityExpression_return equalityExpression344 = null;


        Object char_literal343_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression5494);
            equalityExpression342=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression342.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:28: ( '&' equalityExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( (LA134_0==43) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:996:30: '&' equalityExpression
            	    {
            	    char_literal343=(Token)match(input,43,FOLLOW_43_in_andExpression5498); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal343_tree = (Object)adaptor.create(char_literal343);
            	    adaptor.addChild(root_0, char_literal343_tree);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5500);
            	    equalityExpression344=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression344.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final JavaParser.equalityExpression_return equalityExpression() throws RecognitionException {
        JavaParser.equalityExpression_return retval = new JavaParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set346=null;
        JavaParser.instanceOfExpression_return instanceOfExpression345 = null;

        JavaParser.instanceOfExpression_return instanceOfExpression347 = null;


        Object set346_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5522);
            instanceOfExpression345=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression345.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop135:
            do {
                int alt135=2;
                int LA135_0 = input.LA(1);

                if ( ((LA135_0>=102 && LA135_0<=103)) ) {
                    alt135=1;
                }


                switch (alt135) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set346=(Token)input.LT(1);
            	    if ( (input.LA(1)>=102 && input.LA(1)<=103) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set346));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5534);
            	    instanceOfExpression347=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression347.getTree());

            	    }
            	    break;

            	default :
            	    break loop135;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1003:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final JavaParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JavaParser.instanceOfExpression_return retval = new JavaParser.instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal349=null;
        JavaParser.relationalExpression_return relationalExpression348 = null;

        JavaParser.type_return type350 = null;


        Object string_literal349_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1004:5: ( relationalExpression ( 'instanceof' type )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1004:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5556);
            relationalExpression348=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression348.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1004:30: ( 'instanceof' type )?
            int alt136=2;
            int LA136_0 = input.LA(1);

            if ( (LA136_0==104) ) {
                alt136=1;
            }
            switch (alt136) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1004:31: 'instanceof' type
                    {
                    string_literal349=(Token)match(input,104,FOLLOW_104_in_instanceOfExpression5559); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal349_tree = (Object)adaptor.create(string_literal349);
                    adaptor.addChild(root_0, string_literal349_tree);
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression5561);
                    type350=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type350.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1007:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final JavaParser.relationalExpression_return relationalExpression() throws RecognitionException {
        JavaParser.relationalExpression_return retval = new JavaParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.shiftExpression_return shiftExpression351 = null;

        JavaParser.relationalOp_return relationalOp352 = null;

        JavaParser.shiftExpression_return shiftExpression353 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1008:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1008:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression5582);
            shiftExpression351=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression351.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1008:25: ( relationalOp shiftExpression )*
            loop137:
            do {
                int alt137=2;
                int LA137_0 = input.LA(1);

                if ( (LA137_0==40) ) {
                    int LA137_2 = input.LA(2);

                    if ( (LA137_2==Identifier||(LA137_2>=FloatingPointLiteral && LA137_2<=DecimalLiteral)||LA137_2==47||LA137_2==51||(LA137_2>=56 && LA137_2<=63)||(LA137_2>=65 && LA137_2<=66)||(LA137_2>=69 && LA137_2<=72)||(LA137_2>=105 && LA137_2<=106)||(LA137_2>=109 && LA137_2<=113)) ) {
                        alt137=1;
                    }


                }
                else if ( (LA137_0==42) ) {
                    int LA137_3 = input.LA(2);

                    if ( (LA137_3==Identifier||(LA137_3>=FloatingPointLiteral && LA137_3<=DecimalLiteral)||LA137_3==47||LA137_3==51||(LA137_3>=56 && LA137_3<=63)||(LA137_3>=65 && LA137_3<=66)||(LA137_3>=69 && LA137_3<=72)||(LA137_3>=105 && LA137_3<=106)||(LA137_3>=109 && LA137_3<=113)) ) {
                        alt137=1;
                    }


                }


                switch (alt137) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1008:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression5586);
            	    relationalOp352=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp352.getTree());
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression5588);
            	    shiftExpression353=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression353.getTree());

            	    }
            	    break;

            	default :
            	    break loop137;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1011:1: relationalOp : ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' );
    public final JavaParser.relationalOp_return relationalOp() throws RecognitionException {
        JavaParser.relationalOp_return retval = new JavaParser.relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token char_literal354=null;
        Token char_literal355=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object char_literal354_tree=null;
        Object char_literal355_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1012:5: ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' )
            int alt138=4;
            int LA138_0 = input.LA(1);

            if ( (LA138_0==40) ) {
                int LA138_1 = input.LA(2);

                if ( (LA138_1==51) && (synpred206_Java())) {
                    alt138=1;
                }
                else if ( (LA138_1==Identifier||(LA138_1>=FloatingPointLiteral && LA138_1<=DecimalLiteral)||LA138_1==47||(LA138_1>=56 && LA138_1<=63)||(LA138_1>=65 && LA138_1<=66)||(LA138_1>=69 && LA138_1<=72)||(LA138_1>=105 && LA138_1<=106)||(LA138_1>=109 && LA138_1<=113)) ) {
                    alt138=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 138, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA138_0==42) ) {
                int LA138_2 = input.LA(2);

                if ( (LA138_2==51) && (synpred207_Java())) {
                    alt138=2;
                }
                else if ( (LA138_2==Identifier||(LA138_2>=FloatingPointLiteral && LA138_2<=DecimalLiteral)||LA138_2==47||(LA138_2>=56 && LA138_2<=63)||(LA138_2>=65 && LA138_2<=66)||(LA138_2>=69 && LA138_2<=72)||(LA138_2>=105 && LA138_2<=106)||(LA138_2>=109 && LA138_2<=113)) ) {
                    alt138=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 138, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 138, 0, input);

                throw nvae;
            }
            switch (alt138) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1012:9: ( '<' '=' )=>t1= '<' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_relationalOp5623); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp5627); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1015:9: ( '>' '=' )=>t1= '>' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_relationalOp5657); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp5661); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1018:9: '<'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal354=(Token)match(input,40,FOLLOW_40_in_relationalOp5682); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal354_tree = (Object)adaptor.create(char_literal354);
                    adaptor.addChild(root_0, char_literal354_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1019:9: '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal355=(Token)match(input,42,FOLLOW_42_in_relationalOp5693); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal355_tree = (Object)adaptor.create(char_literal355);
                    adaptor.addChild(root_0, char_literal355_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1022:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final JavaParser.shiftExpression_return shiftExpression() throws RecognitionException {
        JavaParser.shiftExpression_return retval = new JavaParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.additiveExpression_return additiveExpression356 = null;

        JavaParser.shiftOp_return shiftOp357 = null;

        JavaParser.additiveExpression_return additiveExpression358 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1023:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1023:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression5713);
            additiveExpression356=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression356.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1023:28: ( shiftOp additiveExpression )*
            loop139:
            do {
                int alt139=2;
                int LA139_0 = input.LA(1);

                if ( (LA139_0==40) ) {
                    int LA139_1 = input.LA(2);

                    if ( (LA139_1==40) ) {
                        int LA139_4 = input.LA(3);

                        if ( (LA139_4==Identifier||(LA139_4>=FloatingPointLiteral && LA139_4<=DecimalLiteral)||LA139_4==47||(LA139_4>=56 && LA139_4<=63)||(LA139_4>=65 && LA139_4<=66)||(LA139_4>=69 && LA139_4<=72)||(LA139_4>=105 && LA139_4<=106)||(LA139_4>=109 && LA139_4<=113)) ) {
                            alt139=1;
                        }


                    }


                }
                else if ( (LA139_0==42) ) {
                    int LA139_2 = input.LA(2);

                    if ( (LA139_2==42) ) {
                        int LA139_5 = input.LA(3);

                        if ( (LA139_5==42) ) {
                            int LA139_7 = input.LA(4);

                            if ( (LA139_7==Identifier||(LA139_7>=FloatingPointLiteral && LA139_7<=DecimalLiteral)||LA139_7==47||(LA139_7>=56 && LA139_7<=63)||(LA139_7>=65 && LA139_7<=66)||(LA139_7>=69 && LA139_7<=72)||(LA139_7>=105 && LA139_7<=106)||(LA139_7>=109 && LA139_7<=113)) ) {
                                alt139=1;
                            }


                        }
                        else if ( (LA139_5==Identifier||(LA139_5>=FloatingPointLiteral && LA139_5<=DecimalLiteral)||LA139_5==47||(LA139_5>=56 && LA139_5<=63)||(LA139_5>=65 && LA139_5<=66)||(LA139_5>=69 && LA139_5<=72)||(LA139_5>=105 && LA139_5<=106)||(LA139_5>=109 && LA139_5<=113)) ) {
                            alt139=1;
                        }


                    }


                }


                switch (alt139) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1023:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression5717);
            	    shiftOp357=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp357.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression5719);
            	    additiveExpression358=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression358.getTree());

            	    }
            	    break;

            	default :
            	    break loop139;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1027:5: ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?)
            int alt140=3;
            alt140 = dfa140.predict(input);
            switch (alt140) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1027:9: ( '<' '<' )=>t1= '<' t2= '<' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_shiftOp5750); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_shiftOp5754); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:9: ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp5786); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp5790); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_shiftOp5794); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1035:9: ( '>' '>' )=>t1= '>' t2= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp5824); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp5828); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1041:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final JavaParser.additiveExpression_return additiveExpression() throws RecognitionException {
        JavaParser.additiveExpression_return retval = new JavaParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set360=null;
        JavaParser.multiplicativeExpression_return multiplicativeExpression359 = null;

        JavaParser.multiplicativeExpression_return multiplicativeExpression361 = null;


        Object set360_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1042:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1042:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5858);
            multiplicativeExpression359=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression359.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1042:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop141:
            do {
                int alt141=2;
                int LA141_0 = input.LA(1);

                if ( ((LA141_0>=105 && LA141_0<=106)) ) {
                    alt141=1;
                }


                switch (alt141) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1042:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set360=(Token)input.LT(1);
            	    if ( (input.LA(1)>=105 && input.LA(1)<=106) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set360));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5870);
            	    multiplicativeExpression361=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression361.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1045:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final JavaParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JavaParser.multiplicativeExpression_return retval = new JavaParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set363=null;
        JavaParser.unaryExpression_return unaryExpression362 = null;

        JavaParser.unaryExpression_return unaryExpression364 = null;


        Object set363_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1046:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1046:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5892);
            unaryExpression362=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression362.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1046:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop142:
            do {
                int alt142=2;
                int LA142_0 = input.LA(1);

                if ( (LA142_0==30||(LA142_0>=107 && LA142_0<=108)) ) {
                    alt142=1;
                }


                switch (alt142) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1046:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set363=(Token)input.LT(1);
            	    if ( input.LA(1)==30||(input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set363));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5910);
            	    unaryExpression364=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression364.getTree());

            	    }
            	    break;

            	default :
            	    break loop142;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1049:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final JavaParser.unaryExpression_return unaryExpression() throws RecognitionException {
        JavaParser.unaryExpression_return retval = new JavaParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal365=null;
        Token char_literal367=null;
        Token string_literal369=null;
        Token string_literal371=null;
        JavaParser.unaryExpression_return unaryExpression366 = null;

        JavaParser.unaryExpression_return unaryExpression368 = null;

        JavaParser.unaryExpression_return unaryExpression370 = null;

        JavaParser.unaryExpression_return unaryExpression372 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus373 = null;


        Object char_literal365_tree=null;
        Object char_literal367_tree=null;
        Object string_literal369_tree=null;
        Object string_literal371_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt143=5;
            switch ( input.LA(1) ) {
            case 105:
                {
                alt143=1;
                }
                break;
            case 106:
                {
                alt143=2;
                }
                break;
            case 109:
                {
                alt143=3;
                }
                break;
            case 110:
                {
                alt143=4;
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
                alt143=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 143, 0, input);

                throw nvae;
            }

            switch (alt143) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1050:9: '+' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal365=(Token)match(input,105,FOLLOW_105_in_unaryExpression5936); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal365_tree = (Object)adaptor.create(char_literal365);
                    adaptor.addChild(root_0, char_literal365_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5938);
                    unaryExpression366=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression366.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1051:9: '-' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal367=(Token)match(input,106,FOLLOW_106_in_unaryExpression5948); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal367_tree = (Object)adaptor.create(char_literal367);
                    adaptor.addChild(root_0, char_literal367_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5950);
                    unaryExpression368=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression368.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1052:9: '++' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal369=(Token)match(input,109,FOLLOW_109_in_unaryExpression5960); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal369_tree = (Object)adaptor.create(string_literal369);
                    adaptor.addChild(root_0, string_literal369_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5962);
                    unaryExpression370=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression370.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1053:9: '--' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal371=(Token)match(input,110,FOLLOW_110_in_unaryExpression5972); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal371_tree = (Object)adaptor.create(string_literal371);
                    adaptor.addChild(root_0, string_literal371_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5974);
                    unaryExpression372=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression372.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1054:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5984);
                    unaryExpressionNotPlusMinus373=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus373.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1057:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JavaParser.unaryExpressionNotPlusMinus_return retval = new JavaParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal374=null;
        Token char_literal376=null;
        Token set381=null;
        JavaParser.unaryExpression_return unaryExpression375 = null;

        JavaParser.unaryExpression_return unaryExpression377 = null;

        JavaParser.castExpression_return castExpression378 = null;

        JavaParser.primary_return primary379 = null;

        JavaParser.selector_return selector380 = null;


        Object char_literal374_tree=null;
        Object char_literal376_tree=null;
        Object set381_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1058:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt146=4;
            alt146 = dfa146.predict(input);
            switch (alt146) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1058:9: '~' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal374=(Token)match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus6003); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal374_tree = (Object)adaptor.create(char_literal374);
                    adaptor.addChild(root_0, char_literal374_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6005);
                    unaryExpression375=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression375.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1059:9: '!' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal376=(Token)match(input,112,FOLLOW_112_in_unaryExpressionNotPlusMinus6015); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal376_tree = (Object)adaptor.create(char_literal376);
                    adaptor.addChild(root_0, char_literal376_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6017);
                    unaryExpression377=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression377.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1060:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6027);
                    castExpression378=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression378.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1061:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus6037);
                    primary379=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary379.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1061:17: ( selector )*
                    loop144:
                    do {
                        int alt144=2;
                        int LA144_0 = input.LA(1);

                        if ( (LA144_0==29||LA144_0==48) ) {
                            alt144=1;
                        }


                        switch (alt144) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus6039);
                    	    selector380=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector380.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop144;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1061:27: ( '++' | '--' )?
                    int alt145=2;
                    int LA145_0 = input.LA(1);

                    if ( ((LA145_0>=109 && LA145_0<=110)) ) {
                        alt145=1;
                    }
                    switch (alt145) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
                            {
                            set381=(Token)input.LT(1);
                            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set381));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1064:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final JavaParser.castExpression_return castExpression() throws RecognitionException {
        JavaParser.castExpression_return retval = new JavaParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal382=null;
        Token char_literal384=null;
        Token char_literal386=null;
        Token char_literal389=null;
        JavaParser.primitiveType_return primitiveType383 = null;

        JavaParser.unaryExpression_return unaryExpression385 = null;

        JavaParser.type_return type387 = null;

        JavaParser.expression_return expression388 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus390 = null;


        Object char_literal382_tree=null;
        Object char_literal384_tree=null;
        Object char_literal386_tree=null;
        Object char_literal389_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
            int alt148=2;
            int LA148_0 = input.LA(1);

            if ( (LA148_0==66) ) {
                int LA148_1 = input.LA(2);

                if ( (synpred228_Java()) ) {
                    alt148=1;
                }
                else if ( (true) ) {
                    alt148=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 148, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 148, 0, input);

                throw nvae;
            }
            switch (alt148) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:8: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal382=(Token)match(input,66,FOLLOW_66_in_castExpression6065); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal382_tree = (Object)adaptor.create(char_literal382);
                    adaptor.addChild(root_0, char_literal382_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression6067);
                    primitiveType383=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType383.getTree());
                    char_literal384=(Token)match(input,67,FOLLOW_67_in_castExpression6069); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal384_tree = (Object)adaptor.create(char_literal384);
                    adaptor.addChild(root_0, char_literal384_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression6071);
                    unaryExpression385=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression385.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal386=(Token)match(input,66,FOLLOW_66_in_castExpression6080); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal386_tree = (Object)adaptor.create(char_literal386);
                    adaptor.addChild(root_0, char_literal386_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:12: ( type | expression )
                    int alt147=2;
                    alt147 = dfa147.predict(input);
                    switch (alt147) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression6083);
                            type387=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type387.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression6087);
                            expression388=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression388.getTree());

                            }
                            break;

                    }

                    char_literal389=(Token)match(input,67,FOLLOW_67_in_castExpression6090); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal389_tree = (Object)adaptor.create(char_literal389);
                    adaptor.addChild(root_0, char_literal389_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6092);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1069:1: primary : ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final JavaParser.primary_return primary() throws RecognitionException {
        JavaParser.primary_return retval = new JavaParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal392=null;
        Token char_literal393=null;
        Token Identifier394=null;
        Token string_literal396=null;
        Token string_literal399=null;
        Token Identifier401=null;
        Token char_literal402=null;
        Token Identifier403=null;
        Token char_literal406=null;
        Token char_literal407=null;
        Token char_literal408=null;
        Token string_literal409=null;
        Token string_literal410=null;
        Token char_literal411=null;
        Token string_literal412=null;
        JavaParser.parExpression_return parExpression391 = null;

        JavaParser.identifierSuffix_return identifierSuffix395 = null;

        JavaParser.superSuffix_return superSuffix397 = null;

        JavaParser.literal_return literal398 = null;

        JavaParser.creator_return creator400 = null;

        JavaParser.identifierSuffix_return identifierSuffix404 = null;

        JavaParser.primitiveType_return primitiveType405 = null;


        Object string_literal392_tree=null;
        Object char_literal393_tree=null;
        Object Identifier394_tree=null;
        Object string_literal396_tree=null;
        Object string_literal399_tree=null;
        Object Identifier401_tree=null;
        Object char_literal402_tree=null;
        Object Identifier403_tree=null;
        Object char_literal406_tree=null;
        Object char_literal407_tree=null;
        Object char_literal408_tree=null;
        Object string_literal409_tree=null;
        Object string_literal410_tree=null;
        Object char_literal411_tree=null;
        Object string_literal412_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1070:5: ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt154=8;
            switch ( input.LA(1) ) {
            case 66:
                {
                alt154=1;
                }
                break;
            case 69:
                {
                alt154=2;
                }
                break;
            case 65:
                {
                alt154=3;
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
                alt154=4;
                }
                break;
            case 113:
                {
                alt154=5;
                }
                break;
            case Identifier:
                {
                alt154=6;
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
                alt154=7;
                }
                break;
            case 47:
                {
                alt154=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 154, 0, input);

                throw nvae;
            }

            switch (alt154) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1070:9: parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary6111);
                    parExpression391=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression391.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal392=(Token)match(input,69,FOLLOW_69_in_primary6121); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal392_tree = (Object)adaptor.create(string_literal392);
                    adaptor.addChild(root_0, string_literal392_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:16: ( '.' Identifier )*
                    loop149:
                    do {
                        int alt149=2;
                        int LA149_0 = input.LA(1);

                        if ( (LA149_0==29) ) {
                            int LA149_2 = input.LA(2);

                            if ( (LA149_2==Identifier) ) {
                                int LA149_3 = input.LA(3);

                                if ( (synpred231_Java()) ) {
                                    alt149=1;
                                }


                            }


                        }


                        switch (alt149) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:17: '.' Identifier
                    	    {
                    	    char_literal393=(Token)match(input,29,FOLLOW_29_in_primary6124); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal393_tree = (Object)adaptor.create(char_literal393);
                    	    adaptor.addChild(root_0, char_literal393_tree);
                    	    }
                    	    Identifier394=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary6126); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier394_tree = (Object)adaptor.create(Identifier394);
                    	    adaptor.addChild(root_0, Identifier394_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop149;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:34: ( identifierSuffix )?
                    int alt150=2;
                    alt150 = dfa150.predict(input);
                    switch (alt150) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6130);
                            identifierSuffix395=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix395.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1072:9: 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal396=(Token)match(input,65,FOLLOW_65_in_primary6141); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal396_tree = (Object)adaptor.create(string_literal396);
                    adaptor.addChild(root_0, string_literal396_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_primary6143);
                    superSuffix397=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix397.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1073:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary6153);
                    literal398=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal398.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1074:9: 'new' creator
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal399=(Token)match(input,113,FOLLOW_113_in_primary6163); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal399_tree = (Object)adaptor.create(string_literal399);
                    adaptor.addChild(root_0, string_literal399_tree);
                    }
                    pushFollow(FOLLOW_creator_in_primary6165);
                    creator400=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator400.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:9: Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier401=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary6175); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier401_tree = (Object)adaptor.create(Identifier401);
                    adaptor.addChild(root_0, Identifier401_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:20: ( '.' Identifier )*
                    loop151:
                    do {
                        int alt151=2;
                        int LA151_0 = input.LA(1);

                        if ( (LA151_0==29) ) {
                            int LA151_2 = input.LA(2);

                            if ( (LA151_2==Identifier) ) {
                                int LA151_3 = input.LA(3);

                                if ( (synpred237_Java()) ) {
                                    alt151=1;
                                }


                            }


                        }


                        switch (alt151) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:21: '.' Identifier
                    	    {
                    	    char_literal402=(Token)match(input,29,FOLLOW_29_in_primary6178); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal402_tree = (Object)adaptor.create(char_literal402);
                    	    adaptor.addChild(root_0, char_literal402_tree);
                    	    }
                    	    Identifier403=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary6180); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier403_tree = (Object)adaptor.create(Identifier403);
                    	    adaptor.addChild(root_0, Identifier403_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop151;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:38: ( identifierSuffix )?
                    int alt152=2;
                    alt152 = dfa152.predict(input);
                    switch (alt152) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6184);
                            identifierSuffix404=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix404.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1076:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary6195);
                    primitiveType405=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType405.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1076:23: ( '[' ']' )*
                    loop153:
                    do {
                        int alt153=2;
                        int LA153_0 = input.LA(1);

                        if ( (LA153_0==48) ) {
                            alt153=1;
                        }


                        switch (alt153) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1076:24: '[' ']'
                    	    {
                    	    char_literal406=(Token)match(input,48,FOLLOW_48_in_primary6198); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal406_tree = (Object)adaptor.create(char_literal406);
                    	    adaptor.addChild(root_0, char_literal406_tree);
                    	    }
                    	    char_literal407=(Token)match(input,49,FOLLOW_49_in_primary6200); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal407_tree = (Object)adaptor.create(char_literal407);
                    	    adaptor.addChild(root_0, char_literal407_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop153;
                        }
                    } while (true);

                    char_literal408=(Token)match(input,29,FOLLOW_29_in_primary6204); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal408_tree = (Object)adaptor.create(char_literal408);
                    adaptor.addChild(root_0, char_literal408_tree);
                    }
                    string_literal409=(Token)match(input,37,FOLLOW_37_in_primary6206); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal409_tree = (Object)adaptor.create(string_literal409);
                    adaptor.addChild(root_0, string_literal409_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1077:9: 'void' '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal410=(Token)match(input,47,FOLLOW_47_in_primary6216); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal410_tree = (Object)adaptor.create(string_literal410);
                    adaptor.addChild(root_0, string_literal410_tree);
                    }
                    char_literal411=(Token)match(input,29,FOLLOW_29_in_primary6218); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal411_tree = (Object)adaptor.create(char_literal411);
                    adaptor.addChild(root_0, char_literal411_tree);
                    }
                    string_literal412=(Token)match(input,37,FOLLOW_37_in_primary6220); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal412_tree = (Object)adaptor.create(string_literal412);
                    adaptor.addChild(root_0, string_literal412_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1080:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );
    public final JavaParser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        JavaParser.identifierSuffix_return retval = new JavaParser.identifierSuffix_return();
        retval.start = input.LT(1);
        int identifierSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal413=null;
        Token char_literal414=null;
        Token char_literal415=null;
        Token string_literal416=null;
        Token char_literal417=null;
        Token char_literal419=null;
        Token char_literal421=null;
        Token string_literal422=null;
        Token char_literal423=null;
        Token char_literal425=null;
        Token string_literal426=null;
        Token char_literal427=null;
        Token string_literal428=null;
        Token char_literal430=null;
        Token string_literal431=null;
        JavaParser.expression_return expression418 = null;

        JavaParser.arguments_return arguments420 = null;

        JavaParser.explicitGenericInvocation_return explicitGenericInvocation424 = null;

        JavaParser.arguments_return arguments429 = null;

        JavaParser.innerCreator_return innerCreator432 = null;


        Object char_literal413_tree=null;
        Object char_literal414_tree=null;
        Object char_literal415_tree=null;
        Object string_literal416_tree=null;
        Object char_literal417_tree=null;
        Object char_literal419_tree=null;
        Object char_literal421_tree=null;
        Object string_literal422_tree=null;
        Object char_literal423_tree=null;
        Object char_literal425_tree=null;
        Object string_literal426_tree=null;
        Object char_literal427_tree=null;
        Object string_literal428_tree=null;
        Object char_literal430_tree=null;
        Object string_literal431_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1081:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator )
            int alt157=8;
            alt157 = dfa157.predict(input);
            switch (alt157) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1081:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1081:9: ( '[' ']' )+
                    int cnt155=0;
                    loop155:
                    do {
                        int alt155=2;
                        int LA155_0 = input.LA(1);

                        if ( (LA155_0==48) ) {
                            alt155=1;
                        }


                        switch (alt155) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1081:10: '[' ']'
                    	    {
                    	    char_literal413=(Token)match(input,48,FOLLOW_48_in_identifierSuffix6240); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal413_tree = (Object)adaptor.create(char_literal413);
                    	    adaptor.addChild(root_0, char_literal413_tree);
                    	    }
                    	    char_literal414=(Token)match(input,49,FOLLOW_49_in_identifierSuffix6242); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal414_tree = (Object)adaptor.create(char_literal414);
                    	    adaptor.addChild(root_0, char_literal414_tree);
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

                    char_literal415=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6246); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal415_tree = (Object)adaptor.create(char_literal415);
                    adaptor.addChild(root_0, char_literal415_tree);
                    }
                    string_literal416=(Token)match(input,37,FOLLOW_37_in_identifierSuffix6248); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal416_tree = (Object)adaptor.create(string_literal416);
                    adaptor.addChild(root_0, string_literal416_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1082:9: ( '[' expression ']' )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1082:9: ( '[' expression ']' )+
                    int cnt156=0;
                    loop156:
                    do {
                        int alt156=2;
                        alt156 = dfa156.predict(input);
                        switch (alt156) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1082:10: '[' expression ']'
                    	    {
                    	    char_literal417=(Token)match(input,48,FOLLOW_48_in_identifierSuffix6259); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal417_tree = (Object)adaptor.create(char_literal417);
                    	    adaptor.addChild(root_0, char_literal417_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix6261);
                    	    expression418=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression418.getTree());
                    	    char_literal419=(Token)match(input,49,FOLLOW_49_in_identifierSuffix6263); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal419_tree = (Object)adaptor.create(char_literal419);
                    	    adaptor.addChild(root_0, char_literal419_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt156 >= 1 ) break loop156;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(156, input);
                                throw eee;
                        }
                        cnt156++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1083:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix6276);
                    arguments420=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments420.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1084:9: '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal421=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6286); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal421_tree = (Object)adaptor.create(char_literal421);
                    adaptor.addChild(root_0, char_literal421_tree);
                    }
                    string_literal422=(Token)match(input,37,FOLLOW_37_in_identifierSuffix6288); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal422_tree = (Object)adaptor.create(string_literal422);
                    adaptor.addChild(root_0, string_literal422_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1085:9: '.' explicitGenericInvocation
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal423=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6298); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal423_tree = (Object)adaptor.create(char_literal423);
                    adaptor.addChild(root_0, char_literal423_tree);
                    }
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix6300);
                    explicitGenericInvocation424=explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGenericInvocation424.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1086:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal425=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6310); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal425_tree = (Object)adaptor.create(char_literal425);
                    adaptor.addChild(root_0, char_literal425_tree);
                    }
                    string_literal426=(Token)match(input,69,FOLLOW_69_in_identifierSuffix6312); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal426_tree = (Object)adaptor.create(string_literal426);
                    adaptor.addChild(root_0, string_literal426_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1087:9: '.' 'super' arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal427=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6322); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal427_tree = (Object)adaptor.create(char_literal427);
                    adaptor.addChild(root_0, char_literal427_tree);
                    }
                    string_literal428=(Token)match(input,65,FOLLOW_65_in_identifierSuffix6324); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal428_tree = (Object)adaptor.create(string_literal428);
                    adaptor.addChild(root_0, string_literal428_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_identifierSuffix6326);
                    arguments429=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments429.getTree());

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1088:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal430=(Token)match(input,29,FOLLOW_29_in_identifierSuffix6336); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal430_tree = (Object)adaptor.create(char_literal430);
                    adaptor.addChild(root_0, char_literal430_tree);
                    }
                    string_literal431=(Token)match(input,113,FOLLOW_113_in_identifierSuffix6338); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal431_tree = (Object)adaptor.create(string_literal431);
                    adaptor.addChild(root_0, string_literal431_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix6340);
                    innerCreator432=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator432.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1091:1: creator : ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) );
    public final JavaParser.creator_return creator() throws RecognitionException {
        JavaParser.creator_return retval = new JavaParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments433 = null;

        JavaParser.createdName_return createdName434 = null;

        JavaParser.classCreatorRest_return classCreatorRest435 = null;

        JavaParser.createdName_return createdName436 = null;

        JavaParser.arrayCreatorRest_return arrayCreatorRest437 = null;

        JavaParser.classCreatorRest_return classCreatorRest438 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:5: ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) )
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( (LA159_0==40) ) {
                alt159=1;
            }
            else if ( (LA159_0==Identifier||(LA159_0>=56 && LA159_0<=63)) ) {
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1092:9: nonWildcardTypeArguments createdName classCreatorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator6359);
                    nonWildcardTypeArguments433=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments433.getTree());
                    pushFollow(FOLLOW_createdName_in_creator6361);
                    createdName434=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName434.getTree());
                    pushFollow(FOLLOW_classCreatorRest_in_creator6363);
                    classCreatorRest435=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest435.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1093:9: createdName ( arrayCreatorRest | classCreatorRest )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_createdName_in_creator6373);
                    createdName436=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName436.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1093:21: ( arrayCreatorRest | classCreatorRest )
                    int alt158=2;
                    int LA158_0 = input.LA(1);

                    if ( (LA158_0==48) ) {
                        alt158=1;
                    }
                    else if ( (LA158_0==66) ) {
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1093:22: arrayCreatorRest
                            {
                            pushFollow(FOLLOW_arrayCreatorRest_in_creator6376);
                            arrayCreatorRest437=arrayCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreatorRest437.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1093:41: classCreatorRest
                            {
                            pushFollow(FOLLOW_classCreatorRest_in_creator6380);
                            classCreatorRest438=classCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest438.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1096:1: createdName : ( classOrInterfaceType | primitiveType );
    public final JavaParser.createdName_return createdName() throws RecognitionException {
        JavaParser.createdName_return retval = new JavaParser.createdName_return();
        retval.start = input.LT(1);
        int createdName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType439 = null;

        JavaParser.primitiveType_return primitiveType440 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1097:5: ( classOrInterfaceType | primitiveType )
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==Identifier) ) {
                alt160=1;
            }
            else if ( ((LA160_0>=56 && LA160_0<=63)) ) {
                alt160=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 160, 0, input);

                throw nvae;
            }
            switch (alt160) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1097:9: classOrInterfaceType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName6400);
                    classOrInterfaceType439=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType439.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1098:9: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName6410);
                    primitiveType440=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType440.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1101:1: innerCreator : ( nonWildcardTypeArguments )? Identifier classCreatorRest ;
    public final JavaParser.innerCreator_return innerCreator() throws RecognitionException {
        JavaParser.innerCreator_return retval = new JavaParser.innerCreator_return();
        retval.start = input.LT(1);
        int innerCreator_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier442=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments441 = null;

        JavaParser.classCreatorRest_return classCreatorRest443 = null;


        Object Identifier442_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1102:5: ( ( nonWildcardTypeArguments )? Identifier classCreatorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1102:9: ( nonWildcardTypeArguments )? Identifier classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1102:9: ( nonWildcardTypeArguments )?
            int alt161=2;
            int LA161_0 = input.LA(1);

            if ( (LA161_0==40) ) {
                alt161=1;
            }
            switch (alt161) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator6433);
                    nonWildcardTypeArguments441=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments441.getTree());

                    }
                    break;

            }

            Identifier442=(Token)match(input,Identifier,FOLLOW_Identifier_in_innerCreator6436); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier442_tree = (Object)adaptor.create(Identifier442);
            adaptor.addChild(root_0, Identifier442_tree);
            }
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator6438);
            classCreatorRest443=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest443.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1105:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final JavaParser.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        JavaParser.arrayCreatorRest_return retval = new JavaParser.arrayCreatorRest_return();
        retval.start = input.LT(1);
        int arrayCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal444=null;
        Token char_literal445=null;
        Token char_literal446=null;
        Token char_literal447=null;
        Token char_literal450=null;
        Token char_literal451=null;
        Token char_literal453=null;
        Token char_literal454=null;
        Token char_literal455=null;
        JavaParser.arrayInitializer_return arrayInitializer448 = null;

        JavaParser.expression_return expression449 = null;

        JavaParser.expression_return expression452 = null;


        Object char_literal444_tree=null;
        Object char_literal445_tree=null;
        Object char_literal446_tree=null;
        Object char_literal447_tree=null;
        Object char_literal450_tree=null;
        Object char_literal451_tree=null;
        Object char_literal453_tree=null;
        Object char_literal454_tree=null;
        Object char_literal455_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1106:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1106:9: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            root_0 = (Object)adaptor.nil();

            char_literal444=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6457); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal444_tree = (Object)adaptor.create(char_literal444);
            adaptor.addChild(root_0, char_literal444_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1107:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt165=2;
            int LA165_0 = input.LA(1);

            if ( (LA165_0==49) ) {
                alt165=1;
            }
            else if ( (LA165_0==Identifier||(LA165_0>=FloatingPointLiteral && LA165_0<=DecimalLiteral)||LA165_0==47||(LA165_0>=56 && LA165_0<=63)||(LA165_0>=65 && LA165_0<=66)||(LA165_0>=69 && LA165_0<=72)||(LA165_0>=105 && LA165_0<=106)||(LA165_0>=109 && LA165_0<=113)) ) {
                alt165=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 165, 0, input);

                throw nvae;
            }
            switch (alt165) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1107:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    char_literal445=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6471); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal445_tree = (Object)adaptor.create(char_literal445);
                    adaptor.addChild(root_0, char_literal445_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1107:17: ( '[' ']' )*
                    loop162:
                    do {
                        int alt162=2;
                        int LA162_0 = input.LA(1);

                        if ( (LA162_0==48) ) {
                            alt162=1;
                        }


                        switch (alt162) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1107:18: '[' ']'
                    	    {
                    	    char_literal446=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6474); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal446_tree = (Object)adaptor.create(char_literal446);
                    	    adaptor.addChild(root_0, char_literal446_tree);
                    	    }
                    	    char_literal447=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6476); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal447_tree = (Object)adaptor.create(char_literal447);
                    	    adaptor.addChild(root_0, char_literal447_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop162;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest6480);
                    arrayInitializer448=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer448.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest6494);
                    expression449=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression449.getTree());
                    char_literal450=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6496); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal450_tree = (Object)adaptor.create(char_literal450);
                    adaptor.addChild(root_0, char_literal450_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:28: ( '[' expression ']' )*
                    loop163:
                    do {
                        int alt163=2;
                        alt163 = dfa163.predict(input);
                        switch (alt163) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:29: '[' expression ']'
                    	    {
                    	    char_literal451=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6499); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal451_tree = (Object)adaptor.create(char_literal451);
                    	    adaptor.addChild(root_0, char_literal451_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest6501);
                    	    expression452=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression452.getTree());
                    	    char_literal453=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6503); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal453_tree = (Object)adaptor.create(char_literal453);
                    	    adaptor.addChild(root_0, char_literal453_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop163;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:50: ( '[' ']' )*
                    loop164:
                    do {
                        int alt164=2;
                        int LA164_0 = input.LA(1);

                        if ( (LA164_0==48) ) {
                            int LA164_2 = input.LA(2);

                            if ( (LA164_2==49) ) {
                                alt164=1;
                            }


                        }


                        switch (alt164) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:51: '[' ']'
                    	    {
                    	    char_literal454=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest6508); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal454_tree = (Object)adaptor.create(char_literal454);
                    	    adaptor.addChild(root_0, char_literal454_tree);
                    	    }
                    	    char_literal455=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest6510); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal455_tree = (Object)adaptor.create(char_literal455);
                    	    adaptor.addChild(root_0, char_literal455_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop164;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1112:1: classCreatorRest : arguments ( classBody )? ;
    public final JavaParser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        JavaParser.classCreatorRest_return retval = new JavaParser.classCreatorRest_return();
        retval.start = input.LT(1);
        int classCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arguments_return arguments456 = null;

        JavaParser.classBody_return classBody457 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1113:5: ( arguments ( classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1113:9: arguments ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest6541);
            arguments456=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments456.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1113:19: ( classBody )?
            int alt166=2;
            int LA166_0 = input.LA(1);

            if ( (LA166_0==44) ) {
                alt166=1;
            }
            switch (alt166) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest6543);
                    classBody457=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody457.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1116:1: explicitGenericInvocation : nonWildcardTypeArguments Identifier arguments ;
    public final JavaParser.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        JavaParser.explicitGenericInvocation_return retval = new JavaParser.explicitGenericInvocation_return();
        retval.start = input.LT(1);
        int explicitGenericInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier459=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments458 = null;

        JavaParser.arguments_return arguments460 = null;


        Object Identifier459_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1117:5: ( nonWildcardTypeArguments Identifier arguments )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1117:9: nonWildcardTypeArguments Identifier arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6567);
            nonWildcardTypeArguments458=nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments458.getTree());
            Identifier459=(Token)match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocation6569); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier459_tree = (Object)adaptor.create(Identifier459);
            adaptor.addChild(root_0, Identifier459_tree);
            }
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation6571);
            arguments460=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments460.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1120:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        JavaParser.nonWildcardTypeArguments_return retval = new JavaParser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);
        int nonWildcardTypeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal461=null;
        Token char_literal463=null;
        JavaParser.typeList_return typeList462 = null;


        Object char_literal461_tree=null;
        Object char_literal463_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1121:5: ( '<' typeList '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1121:9: '<' typeList '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal461=(Token)match(input,40,FOLLOW_40_in_nonWildcardTypeArguments6594); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal461_tree = (Object)adaptor.create(char_literal461);
            adaptor.addChild(root_0, char_literal461_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments6596);
            typeList462=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList462.getTree());
            char_literal463=(Token)match(input,42,FOLLOW_42_in_nonWildcardTypeArguments6598); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal463_tree = (Object)adaptor.create(char_literal463);
            adaptor.addChild(root_0, char_literal463_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1124:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' );
    public final JavaParser.selector_return selector() throws RecognitionException {
        JavaParser.selector_return retval = new JavaParser.selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal464=null;
        Token Identifier465=null;
        Token char_literal467=null;
        Token string_literal468=null;
        Token char_literal469=null;
        Token string_literal470=null;
        Token char_literal472=null;
        Token string_literal473=null;
        Token char_literal475=null;
        Token char_literal477=null;
        JavaParser.arguments_return arguments466 = null;

        JavaParser.superSuffix_return superSuffix471 = null;

        JavaParser.innerCreator_return innerCreator474 = null;

        JavaParser.expression_return expression476 = null;


        Object char_literal464_tree=null;
        Object Identifier465_tree=null;
        Object char_literal467_tree=null;
        Object string_literal468_tree=null;
        Object char_literal469_tree=null;
        Object string_literal470_tree=null;
        Object char_literal472_tree=null;
        Object string_literal473_tree=null;
        Object char_literal475_tree=null;
        Object char_literal477_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1125:5: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' )
            int alt168=5;
            int LA168_0 = input.LA(1);

            if ( (LA168_0==29) ) {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    alt168=1;
                    }
                    break;
                case 69:
                    {
                    alt168=2;
                    }
                    break;
                case 65:
                    {
                    alt168=3;
                    }
                    break;
                case 113:
                    {
                    alt168=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 168, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA168_0==48) ) {
                alt168=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 168, 0, input);

                throw nvae;
            }
            switch (alt168) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1125:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal464=(Token)match(input,29,FOLLOW_29_in_selector6621); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal464_tree = (Object)adaptor.create(char_literal464);
                    adaptor.addChild(root_0, char_literal464_tree);
                    }
                    Identifier465=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector6623); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier465_tree = (Object)adaptor.create(Identifier465);
                    adaptor.addChild(root_0, Identifier465_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1125:24: ( arguments )?
                    int alt167=2;
                    int LA167_0 = input.LA(1);

                    if ( (LA167_0==66) ) {
                        alt167=1;
                    }
                    switch (alt167) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector6625);
                            arguments466=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments466.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1126:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal467=(Token)match(input,29,FOLLOW_29_in_selector6636); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal467_tree = (Object)adaptor.create(char_literal467);
                    adaptor.addChild(root_0, char_literal467_tree);
                    }
                    string_literal468=(Token)match(input,69,FOLLOW_69_in_selector6638); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal468_tree = (Object)adaptor.create(string_literal468);
                    adaptor.addChild(root_0, string_literal468_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1127:9: '.' 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal469=(Token)match(input,29,FOLLOW_29_in_selector6648); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal469_tree = (Object)adaptor.create(char_literal469);
                    adaptor.addChild(root_0, char_literal469_tree);
                    }
                    string_literal470=(Token)match(input,65,FOLLOW_65_in_selector6650); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal470_tree = (Object)adaptor.create(string_literal470);
                    adaptor.addChild(root_0, string_literal470_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_selector6652);
                    superSuffix471=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix471.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1128:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal472=(Token)match(input,29,FOLLOW_29_in_selector6662); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal472_tree = (Object)adaptor.create(char_literal472);
                    adaptor.addChild(root_0, char_literal472_tree);
                    }
                    string_literal473=(Token)match(input,113,FOLLOW_113_in_selector6664); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal473_tree = (Object)adaptor.create(string_literal473);
                    adaptor.addChild(root_0, string_literal473_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_selector6666);
                    innerCreator474=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator474.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1129:9: '[' expression ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal475=(Token)match(input,48,FOLLOW_48_in_selector6676); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal475_tree = (Object)adaptor.create(char_literal475);
                    adaptor.addChild(root_0, char_literal475_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector6678);
                    expression476=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression476.getTree());
                    char_literal477=(Token)match(input,49,FOLLOW_49_in_selector6680); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal477_tree = (Object)adaptor.create(char_literal477);
                    adaptor.addChild(root_0, char_literal477_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1132:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final JavaParser.superSuffix_return superSuffix() throws RecognitionException {
        JavaParser.superSuffix_return retval = new JavaParser.superSuffix_return();
        retval.start = input.LT(1);
        int superSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal479=null;
        Token Identifier480=null;
        JavaParser.arguments_return arguments478 = null;

        JavaParser.arguments_return arguments481 = null;


        Object char_literal479_tree=null;
        Object Identifier480_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 136) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1133:5: ( arguments | '.' Identifier ( arguments )? )
            int alt170=2;
            int LA170_0 = input.LA(1);

            if ( (LA170_0==66) ) {
                alt170=1;
            }
            else if ( (LA170_0==29) ) {
                alt170=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 170, 0, input);

                throw nvae;
            }
            switch (alt170) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1133:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix6703);
                    arguments478=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments478.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1134:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal479=(Token)match(input,29,FOLLOW_29_in_superSuffix6713); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal479_tree = (Object)adaptor.create(char_literal479);
                    adaptor.addChild(root_0, char_literal479_tree);
                    }
                    Identifier480=(Token)match(input,Identifier,FOLLOW_Identifier_in_superSuffix6715); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier480_tree = (Object)adaptor.create(Identifier480);
                    adaptor.addChild(root_0, Identifier480_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1134:24: ( arguments )?
                    int alt169=2;
                    int LA169_0 = input.LA(1);

                    if ( (LA169_0==66) ) {
                        alt169=1;
                    }
                    switch (alt169) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix6717);
                            arguments481=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments481.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1137:1: arguments returns [List<ActualParameter> element] : '(' ( expressionList )? ')' ;
    public final JavaParser.arguments_return arguments() throws RecognitionException {
        JavaParser.arguments_return retval = new JavaParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal482=null;
        Token char_literal484=null;
        JavaParser.expressionList_return expressionList483 = null;


        Object char_literal482_tree=null;
        Object char_literal484_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 137) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1138:5: ( '(' ( expressionList )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1138:9: '(' ( expressionList )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal482=(Token)match(input,66,FOLLOW_66_in_arguments6741); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal482_tree = (Object)adaptor.create(char_literal482);
            adaptor.addChild(root_0, char_literal482_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1138:13: ( expressionList )?
            int alt171=2;
            int LA171_0 = input.LA(1);

            if ( (LA171_0==Identifier||(LA171_0>=FloatingPointLiteral && LA171_0<=DecimalLiteral)||LA171_0==47||(LA171_0>=56 && LA171_0<=63)||(LA171_0>=65 && LA171_0<=66)||(LA171_0>=69 && LA171_0<=72)||(LA171_0>=105 && LA171_0<=106)||(LA171_0>=109 && LA171_0<=113)) ) {
                alt171=1;
            }
            switch (alt171) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments6743);
                    expressionList483=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList483.getTree());

                    }
                    break;

            }

            char_literal484=(Token)match(input,67,FOLLOW_67_in_arguments6746); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal484_tree = (Object)adaptor.create(char_literal484);
            adaptor.addChild(root_0, char_literal484_tree);
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


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:349:10: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:349:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
        {
        pushFollow(FOLLOW_annotations_in_synpred5_Java96);
        annotations();

        state._fsp--;
        if (state.failed) return ;
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
        int alt177=2;
        int LA177_0 = input.LA(1);

        if ( (LA177_0==25) ) {
            alt177=1;
        }
        else if ( (LA177_0==ENUM||LA177_0==28||(LA177_0>=31 && LA177_0<=37)||LA177_0==46||LA177_0==73) ) {
            alt177=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 177, 0, input);

            throw nvae;
        }
        switch (alt177) {
            case 1 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_packageDeclaration_in_synpred5_Java112);
                np=packageDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:90: (imp= importDeclaration )*
                loop174:
                do {
                    int alt174=2;
                    int LA174_0 = input.LA(1);

                    if ( (LA174_0==27) ) {
                        alt174=1;
                    }


                    switch (alt174) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:91: imp= importDeclaration
                	    {
                	    pushFollow(FOLLOW_importDeclaration_in_synpred5_Java118);
                	    imp=importDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop174;
                    }
                } while (true);

                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:159: (typech= typeDeclaration )*
                loop175:
                do {
                    int alt175=2;
                    int LA175_0 = input.LA(1);

                    if ( (LA175_0==ENUM||LA175_0==26||LA175_0==28||(LA175_0>=31 && LA175_0<=37)||LA175_0==46||LA175_0==73) ) {
                        alt175=1;
                    }


                    switch (alt175) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:160: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java126);
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
            case 2 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java145);
                cd=classOrInterfaceDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:85: (typech= typeDeclaration )*
                loop176:
                do {
                    int alt176=2;
                    int LA176_0 = input.LA(1);

                    if ( (LA176_0==ENUM||LA176_0==26||LA176_0==28||(LA176_0>=31 && LA176_0<=37)||LA176_0==46||LA176_0==73) ) {
                        alt176=1;
                    }


                    switch (alt176) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:86: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java151);
                	    typech=typeDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop176;
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


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:515:9: (method= methodDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:515:9: method= methodDeclaration
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


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:559:9: (cst= interfaceConstant )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:559:9: cst= interfaceConstant
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
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:646:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:646:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred81_Java2801);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred81_Java

    // $ANTLR start synpred82_Java
    public final void synpred82_Java_fragment() throws RecognitionException {   
        JavaParser.classOrInterfaceModifier_return mod = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:647:9: (mod= classOrInterfaceModifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:647:9: mod= classOrInterfaceModifier
        {
        pushFollow(FOLLOW_classOrInterfaceModifier_in_synpred82_Java2813);
        mod=classOrInterfaceModifier();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred82_Java

    // $ANTLR start synpred108_Java
    public final void synpred108_Java_fragment() throws RecognitionException {   
        JavaParser.explicitConstructorInvocation_return inv = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:46: (inv= explicitConstructorInvocation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:46: inv= explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred108_Java3429);
        inv=explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred108_Java

    // $ANTLR start synpred112_Java
    public final void synpred112_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: ( nonWildcardTypeArguments )?
        int alt185=2;
        int LA185_0 = input.LA(1);

        if ( (LA185_0==40) ) {
            alt185=1;
        }
        switch (alt185) {
            case 1 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred112_Java3461);
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

        pushFollow(FOLLOW_arguments_in_synpred112_Java3472);
        arguments();

        state._fsp--;
        if (state.failed) return ;
        match(input,26,FOLLOW_26_in_synpred112_Java3474); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred112_Java

    // $ANTLR start synpred123_Java
    public final void synpred123_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred123_Java3708);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred123_Java

    // $ANTLR start synpred146_Java
    public final void synpred146_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:9: ( localVariableDeclarationStatement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred146_Java4245);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred146_Java

    // $ANTLR start synpred147_Java
    public final void synpred147_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:836:9: ( classOrInterfaceDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:836:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred147_Java4255);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred147_Java

    // $ANTLR start synpred152_Java
    public final void synpred152_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:54: ( 'else' statement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:54: 'else' statement
        {
        match(input,77,FOLLOW_77_in_synpred152_Java4400); if (state.failed) return ;
        pushFollow(FOLLOW_statement_in_synpred152_Java4402);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred152_Java

    // $ANTLR start synpred157_Java
    public final void synpred157_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:11: ( catches 'finally' block )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:11: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred157_Java4478);
        catches();

        state._fsp--;
        if (state.failed) return ;
        match(input,82,FOLLOW_82_in_synpred157_Java4480); if (state.failed) return ;
        pushFollow(FOLLOW_block_in_synpred157_Java4482);
        block();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred157_Java

    // $ANTLR start synpred158_Java
    public final void synpred158_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:861:11: ( catches )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:861:11: catches
        {
        pushFollow(FOLLOW_catches_in_synpred158_Java4494);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred158_Java

    // $ANTLR start synpred173_Java
    public final void synpred173_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: ( switchLabel )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: switchLabel
        {
        pushFollow(FOLLOW_switchLabel_in_synpred173_Java4785);
        switchLabel();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred173_Java

    // $ANTLR start synpred175_Java
    public final void synpred175_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:9: ( 'case' constantExpression ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:9: 'case' constantExpression ':'
        {
        match(input,89,FOLLOW_89_in_synpred175_Java4812); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred175_Java4814);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred175_Java4816); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred175_Java

    // $ANTLR start synpred176_Java
    public final void synpred176_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:901:9: ( 'case' enumConstantName ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:901:9: 'case' enumConstantName ':'
        {
        match(input,89,FOLLOW_89_in_synpred176_Java4826); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred176_Java4828);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred176_Java4830); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred176_Java

    // $ANTLR start synpred177_Java
    public final void synpred177_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:9: ( enhancedForControl )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:9: enhancedForControl
        {
        pushFollow(FOLLOW_enhancedForControl_in_synpred177_Java4873);
        enhancedForControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred177_Java

    // $ANTLR start synpred181_Java
    public final void synpred181_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:9: ( localVariableDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred181_Java4913);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred181_Java

    // $ANTLR start synpred183_Java
    public final void synpred183_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:32: ( assignmentOperator expression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:32: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred183_Java5100);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred183_Java5102);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred183_Java

    // $ANTLR start synpred193_Java
    public final void synpred193_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:956:9: ( '<' '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:956:10: '<' '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred193_Java5218); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred193_Java5220); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred193_Java5222); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred193_Java

    // $ANTLR start synpred194_Java
    public final void synpred194_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:961:9: ( '>' '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:961:10: '>' '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred194_Java5258); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred194_Java5260); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred194_Java5262); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred194_Java5264); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred194_Java

    // $ANTLR start synpred195_Java
    public final void synpred195_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:9: ( '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:10: '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred195_Java5303); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred195_Java5305); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred195_Java5307); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred195_Java

    // $ANTLR start synpred206_Java
    public final void synpred206_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1012:9: ( '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1012:10: '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred206_Java5615); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred206_Java5617); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred206_Java

    // $ANTLR start synpred207_Java
    public final void synpred207_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1015:9: ( '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1015:10: '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred207_Java5649); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred207_Java5651); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred207_Java

    // $ANTLR start synpred210_Java
    public final void synpred210_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1027:9: ( '<' '<' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1027:10: '<' '<'
        {
        match(input,40,FOLLOW_40_in_synpred210_Java5742); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred210_Java5744); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred210_Java

    // $ANTLR start synpred211_Java
    public final void synpred211_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:9: ( '>' '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:10: '>' '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred211_Java5776); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred211_Java5778); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred211_Java5780); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred211_Java

    // $ANTLR start synpred212_Java
    public final void synpred212_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1035:9: ( '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1035:10: '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred212_Java5816); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred212_Java5818); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred212_Java

    // $ANTLR start synpred224_Java
    public final void synpred224_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1060:9: ( castExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1060:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred224_Java6027);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred224_Java

    // $ANTLR start synpred228_Java
    public final void synpred228_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:8: ( '(' primitiveType ')' unaryExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1065:8: '(' primitiveType ')' unaryExpression
        {
        match(input,66,FOLLOW_66_in_synpred228_Java6065); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred228_Java6067);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,67,FOLLOW_67_in_synpred228_Java6069); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred228_Java6071);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred228_Java

    // $ANTLR start synpred229_Java
    public final void synpred229_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:13: ( type )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1066:13: type
        {
        pushFollow(FOLLOW_type_in_synpred229_Java6083);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred229_Java

    // $ANTLR start synpred231_Java
    public final void synpred231_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:17: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:17: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred231_Java6124); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred231_Java6126); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred231_Java

    // $ANTLR start synpred232_Java
    public final void synpred232_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:34: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1071:34: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred232_Java6130);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred232_Java

    // $ANTLR start synpred237_Java
    public final void synpred237_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:21: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:21: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred237_Java6178); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred237_Java6180); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred237_Java

    // $ANTLR start synpred238_Java
    public final void synpred238_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:38: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1075:38: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred238_Java6184);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred238_Java

    // $ANTLR start synpred244_Java
    public final void synpred244_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1082:10: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1082:10: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred244_Java6259); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred244_Java6261);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred244_Java6263); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred244_Java

    // $ANTLR start synpred257_Java
    public final void synpred257_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:29: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1108:29: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred257_Java6499); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred257_Java6501);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred257_Java6503); if (state.failed) return ;

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
    protected DFA107 dfa107 = new DFA107(this);
    protected DFA115 dfa115 = new DFA115(this);
    protected DFA124 dfa124 = new DFA124(this);
    protected DFA125 dfa125 = new DFA125(this);
    protected DFA127 dfa127 = new DFA127(this);
    protected DFA128 dfa128 = new DFA128(this);
    protected DFA140 dfa140 = new DFA140(this);
    protected DFA146 dfa146 = new DFA146(this);
    protected DFA147 dfa147 = new DFA147(this);
    protected DFA150 dfa150 = new DFA150(this);
    protected DFA152 dfa152 = new DFA152(this);
    protected DFA157 dfa157 = new DFA157(this);
    protected DFA156 dfa156 = new DFA156(this);
    protected DFA163 dfa163 = new DFA163(this);
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
            return "347:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );";
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
        "\60\uffff";
    static final String DFA81_eofS =
        "\60\uffff";
    static final String DFA81_minS =
        "\1\4\1\uffff\16\0\40\uffff";
    static final String DFA81_maxS =
        "\1\161\1\uffff\16\0\40\uffff";
    static final String DFA81_acceptS =
        "\1\uffff\1\1\16\uffff\1\2\37\uffff";
    static final String DFA81_specialS =
        "\2\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\40\uffff}>";
    static final String[] DFA81_transitionS = {
            "\1\15\1\20\1\6\1\7\1\10\3\5\1\20\15\uffff\1\20\1\uffff\1\20"+
            "\2\uffff\7\20\2\uffff\1\1\3\uffff\3\20\1\17\5\uffff\1\20\2\uffff"+
            "\10\16\1\uffff\1\4\1\3\2\uffff\1\2\1\13\1\11\1\12\1\20\2\uffff"+
            "\1\20\1\uffff\4\20\1\uffff\5\20\21\uffff\2\20\2\uffff\4\20\1"+
            "\14",
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
            return "725:45: (inv= explicitConstructorInvocation )?";
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

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA81_3 = input.LA(1);

                         
                        int index81_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA81_4 = input.LA(1);

                         
                        int index81_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA81_5 = input.LA(1);

                         
                        int index81_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA81_6 = input.LA(1);

                         
                        int index81_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA81_7 = input.LA(1);

                         
                        int index81_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA81_8 = input.LA(1);

                         
                        int index81_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA81_9 = input.LA(1);

                         
                        int index81_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA81_10 = input.LA(1);

                         
                        int index81_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA81_11 = input.LA(1);

                         
                        int index81_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA81_12 = input.LA(1);

                         
                        int index81_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA81_13 = input.LA(1);

                         
                        int index81_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_13);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA81_14 = input.LA(1);

                         
                        int index81_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_14);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA81_15 = input.LA(1);

                         
                        int index81_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred108_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index81_15);
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
        "\20\uffff";
    static final String DFA85_eofS =
        "\20\uffff";
    static final String DFA85_minS =
        "\1\4\1\uffff\1\0\1\uffff\1\0\13\uffff";
    static final String DFA85_maxS =
        "\1\161\1\uffff\1\0\1\uffff\1\0\13\uffff";
    static final String DFA85_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\14\uffff";
    static final String DFA85_specialS =
        "\2\uffff\1\0\1\uffff\1\1\13\uffff}>";
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
            return "728:1: explicitConstructorInvocation returns [Expression element] : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );";
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
    static final String DFA107_eotS =
        "\57\uffff";
    static final String DFA107_eofS =
        "\57\uffff";
    static final String DFA107_minS =
        "\1\4\4\0\52\uffff";
    static final String DFA107_maxS =
        "\1\161\4\0\52\uffff";
    static final String DFA107_acceptS =
        "\5\uffff\1\2\10\uffff\1\3\37\uffff\1\1";
    static final String DFA107_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\52\uffff}>";
    static final String[] DFA107_transitionS = {
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
            "",
            ""
    };

    static final short[] DFA107_eot = DFA.unpackEncodedString(DFA107_eotS);
    static final short[] DFA107_eof = DFA.unpackEncodedString(DFA107_eofS);
    static final char[] DFA107_min = DFA.unpackEncodedStringToUnsignedChars(DFA107_minS);
    static final char[] DFA107_max = DFA.unpackEncodedStringToUnsignedChars(DFA107_maxS);
    static final short[] DFA107_accept = DFA.unpackEncodedString(DFA107_acceptS);
    static final short[] DFA107_special = DFA.unpackEncodedString(DFA107_specialS);
    static final short[][] DFA107_transition;

    static {
        int numStates = DFA107_transitionS.length;
        DFA107_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA107_transition[i] = DFA.unpackEncodedString(DFA107_transitionS[i]);
        }
    }

    class DFA107 extends DFA {

        public DFA107(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 107;
            this.eot = DFA107_eot;
            this.eof = DFA107_eof;
            this.min = DFA107_min;
            this.max = DFA107_max;
            this.accept = DFA107_accept;
            this.special = DFA107_special;
            this.transition = DFA107_transition;
        }
        public String getDescription() {
            return "834:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA107_1 = input.LA(1);

                         
                        int index107_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 46;}

                        else if ( (synpred147_Java()) ) {s = 5;}

                         
                        input.seek(index107_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA107_2 = input.LA(1);

                         
                        int index107_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 46;}

                        else if ( (synpred147_Java()) ) {s = 5;}

                         
                        input.seek(index107_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA107_3 = input.LA(1);

                         
                        int index107_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 46;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index107_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA107_4 = input.LA(1);

                         
                        int index107_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred146_Java()) ) {s = 46;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index107_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 107, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA115_eotS =
        "\22\uffff";
    static final String DFA115_eofS =
        "\22\uffff";
    static final String DFA115_minS =
        "\1\4\17\uffff\1\32\1\uffff";
    static final String DFA115_maxS =
        "\1\161\17\uffff\1\156\1\uffff";
    static final String DFA115_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\uffff\1\20";
    static final String DFA115_specialS =
        "\22\uffff}>";
    static final String[] DFA115_transitionS = {
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

    static final short[] DFA115_eot = DFA.unpackEncodedString(DFA115_eotS);
    static final short[] DFA115_eof = DFA.unpackEncodedString(DFA115_eofS);
    static final char[] DFA115_min = DFA.unpackEncodedStringToUnsignedChars(DFA115_minS);
    static final char[] DFA115_max = DFA.unpackEncodedStringToUnsignedChars(DFA115_maxS);
    static final short[] DFA115_accept = DFA.unpackEncodedString(DFA115_acceptS);
    static final short[] DFA115_special = DFA.unpackEncodedString(DFA115_specialS);
    static final short[][] DFA115_transition;

    static {
        int numStates = DFA115_transitionS.length;
        DFA115_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA115_transition[i] = DFA.unpackEncodedString(DFA115_transitionS[i]);
        }
    }

    class DFA115 extends DFA {

        public DFA115(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 115;
            this.eot = DFA115_eot;
            this.eof = DFA115_eof;
            this.min = DFA115_min;
            this.max = DFA115_max;
            this.accept = DFA115_accept;
            this.special = DFA115_special;
            this.transition = DFA115_transition;
        }
        public String getDescription() {
            return "852:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );";
        }
    }
    static final String DFA124_eotS =
        "\u008a\uffff";
    static final String DFA124_eofS =
        "\u008a\uffff";
    static final String DFA124_minS =
        "\5\4\23\uffff\10\4\1\32\30\uffff\1\61\1\uffff\1\32\21\0\2\uffff"+
        "\3\0\22\uffff\1\0\5\uffff\1\0\31\uffff\1\0\5\uffff";
    static final String DFA124_maxS =
        "\1\161\1\111\1\4\1\156\1\60\23\uffff\2\60\1\111\1\4\1\111\3\161"+
        "\1\113\30\uffff\1\61\1\uffff\1\113\21\0\2\uffff\3\0\22\uffff\1\0"+
        "\5\uffff\1\0\31\uffff\1\0\5\uffff";
    static final String DFA124_acceptS =
        "\5\uffff\1\2\171\uffff\1\1\12\uffff";
    static final String DFA124_specialS =
        "\74\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\2\uffff\1\21\1\22\1\23\22\uffff\1\24\5\uffff"+
        "\1\25\31\uffff\1\26\5\uffff}>";
    static final String[] DFA124_transitionS = {
            "\1\3\1\uffff\6\5\16\uffff\1\5\10\uffff\1\1\13\uffff\1\5\10\uffff"+
            "\10\4\1\uffff\2\5\2\uffff\4\5\1\2\37\uffff\2\5\2\uffff\5\5",
            "\1\30\36\uffff\1\32\24\uffff\10\31\11\uffff\1\33",
            "\1\34",
            "\1\40\25\uffff\1\5\2\uffff\1\36\1\5\11\uffff\1\35\3\5\4\uffff"+
            "\1\37\2\uffff\1\5\14\uffff\1\5\1\uffff\1\5\27\uffff\25\5",
            "\1\73\30\uffff\1\5\22\uffff\1\71",
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
            "\1\77\30\uffff\1\75\12\uffff\1\74\7\uffff\1\76",
            "\1\101\53\uffff\1\100",
            "\1\102\36\uffff\1\104\24\uffff\10\103\11\uffff\1\105",
            "\1\106",
            "\1\111\30\uffff\1\107\5\uffff\1\113\24\uffff\10\112\2\uffff"+
            "\1\110\6\uffff\1\114",
            "\1\117\1\uffff\6\5\34\uffff\1\5\6\uffff\1\5\3\uffff\1\5\4\uffff"+
            "\10\120\1\121\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "\1\144\40\uffff\1\5\2\uffff\1\5\30\uffff\1\5\3\uffff\1\5\53"+
            "\uffff\1\5",
            "\1\5\1\uffff\6\5\43\uffff\1\5\1\uffff\1\152\6\uffff\10\5\1"+
            "\uffff\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "\1\5\16\uffff\1\5\6\uffff\1\5\2\uffff\1\5\27\uffff\1\177",
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
            "\1\u0084",
            "",
            "\1\5\16\uffff\1\5\6\uffff\1\5\2\uffff\1\5\27\uffff\1\177",
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
            return "905:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA124_60 = input.LA(1);

                         
                        int index124_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_60);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA124_61 = input.LA(1);

                         
                        int index124_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_61);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA124_62 = input.LA(1);

                         
                        int index124_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_62);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA124_63 = input.LA(1);

                         
                        int index124_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_63);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA124_64 = input.LA(1);

                         
                        int index124_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_64);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA124_65 = input.LA(1);

                         
                        int index124_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_65);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA124_66 = input.LA(1);

                         
                        int index124_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_66);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA124_67 = input.LA(1);

                         
                        int index124_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_67);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA124_68 = input.LA(1);

                         
                        int index124_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_68);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA124_69 = input.LA(1);

                         
                        int index124_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_69);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA124_70 = input.LA(1);

                         
                        int index124_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_70);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA124_71 = input.LA(1);

                         
                        int index124_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_71);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA124_72 = input.LA(1);

                         
                        int index124_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_72);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA124_73 = input.LA(1);

                         
                        int index124_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_73);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA124_74 = input.LA(1);

                         
                        int index124_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_74);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA124_75 = input.LA(1);

                         
                        int index124_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_75);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA124_76 = input.LA(1);

                         
                        int index124_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_76);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA124_79 = input.LA(1);

                         
                        int index124_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_79);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA124_80 = input.LA(1);

                         
                        int index124_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_80);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA124_81 = input.LA(1);

                         
                        int index124_81 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_81);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA124_100 = input.LA(1);

                         
                        int index124_100 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_100);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA124_106 = input.LA(1);

                         
                        int index124_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_106);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA124_132 = input.LA(1);

                         
                        int index124_132 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 127;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_132);
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
    static final String DFA125_eotS =
        "\27\uffff";
    static final String DFA125_eofS =
        "\27\uffff";
    static final String DFA125_minS =
        "\1\4\2\uffff\2\0\22\uffff";
    static final String DFA125_maxS =
        "\1\161\2\uffff\2\0\22\uffff";
    static final String DFA125_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\21\uffff";
    static final String DFA125_specialS =
        "\3\uffff\1\0\1\1\22\uffff}>";
    static final String[] DFA125_transitionS = {
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
            "",
            ""
    };

    static final short[] DFA125_eot = DFA.unpackEncodedString(DFA125_eotS);
    static final short[] DFA125_eof = DFA.unpackEncodedString(DFA125_eofS);
    static final char[] DFA125_min = DFA.unpackEncodedStringToUnsignedChars(DFA125_minS);
    static final char[] DFA125_max = DFA.unpackEncodedStringToUnsignedChars(DFA125_maxS);
    static final short[] DFA125_accept = DFA.unpackEncodedString(DFA125_acceptS);
    static final short[] DFA125_special = DFA.unpackEncodedString(DFA125_specialS);
    static final short[][] DFA125_transition;

    static {
        int numStates = DFA125_transitionS.length;
        DFA125_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA125_transition[i] = DFA.unpackEncodedString(DFA125_transitionS[i]);
        }
    }

    class DFA125 extends DFA {

        public DFA125(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 125;
            this.eot = DFA125_eot;
            this.eof = DFA125_eof;
            this.min = DFA125_min;
            this.max = DFA125_max;
            this.accept = DFA125_accept;
            this.special = DFA125_special;
            this.transition = DFA125_transition;
        }
        public String getDescription() {
            return "911:1: forInit : ( localVariableDeclaration | expressionList );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA125_3 = input.LA(1);

                         
                        int index125_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred181_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index125_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA125_4 = input.LA(1);

                         
                        int index125_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred181_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index125_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 125, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA127_eotS =
        "\16\uffff";
    static final String DFA127_eofS =
        "\1\14\15\uffff";
    static final String DFA127_minS =
        "\1\32\13\0\2\uffff";
    static final String DFA127_maxS =
        "\1\141\13\0\2\uffff";
    static final String DFA127_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA127_specialS =
        "\1\uffff\1\10\1\6\1\3\1\1\1\11\1\4\1\7\1\12\1\2\1\0\1\5\2\uffff}>";
    static final String[] DFA127_transitionS = {
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
            return "943:31: ( assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA127_10 = input.LA(1);

                         
                        int index127_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA127_4 = input.LA(1);

                         
                        int index127_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA127_9 = input.LA(1);

                         
                        int index127_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA127_3 = input.LA(1);

                         
                        int index127_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA127_6 = input.LA(1);

                         
                        int index127_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA127_11 = input.LA(1);

                         
                        int index127_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_11);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA127_2 = input.LA(1);

                         
                        int index127_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_2);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA127_7 = input.LA(1);

                         
                        int index127_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA127_1 = input.LA(1);

                         
                        int index127_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_1);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA127_5 = input.LA(1);

                         
                        int index127_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_5);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA127_8 = input.LA(1);

                         
                        int index127_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index127_8);
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
    static final String DFA128_eotS =
        "\17\uffff";
    static final String DFA128_eofS =
        "\17\uffff";
    static final String DFA128_minS =
        "\1\50\12\uffff\2\52\2\uffff";
    static final String DFA128_maxS =
        "\1\141\12\uffff\1\52\1\63\2\uffff";
    static final String DFA128_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA128_specialS =
        "\1\1\13\uffff\1\0\2\uffff}>";
    static final String[] DFA128_transitionS = {
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

    static final short[] DFA128_eot = DFA.unpackEncodedString(DFA128_eotS);
    static final short[] DFA128_eof = DFA.unpackEncodedString(DFA128_eofS);
    static final char[] DFA128_min = DFA.unpackEncodedStringToUnsignedChars(DFA128_minS);
    static final char[] DFA128_max = DFA.unpackEncodedStringToUnsignedChars(DFA128_maxS);
    static final short[] DFA128_accept = DFA.unpackEncodedString(DFA128_acceptS);
    static final short[] DFA128_special = DFA.unpackEncodedString(DFA128_specialS);
    static final short[][] DFA128_transition;

    static {
        int numStates = DFA128_transitionS.length;
        DFA128_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA128_transition[i] = DFA.unpackEncodedString(DFA128_transitionS[i]);
        }
    }

    class DFA128 extends DFA {

        public DFA128(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 128;
            this.eot = DFA128_eot;
            this.eof = DFA128_eof;
            this.min = DFA128_min;
            this.max = DFA128_max;
            this.accept = DFA128_accept;
            this.special = DFA128_special;
            this.transition = DFA128_transition;
        }
        public String getDescription() {
            return "946:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA128_12 = input.LA(1);

                         
                        int index128_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA128_12==42) && (synpred194_Java())) {s = 13;}

                        else if ( (LA128_12==51) && (synpred195_Java())) {s = 14;}

                         
                        input.seek(index128_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA128_0 = input.LA(1);

                         
                        int index128_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA128_0==51) ) {s = 1;}

                        else if ( (LA128_0==90) ) {s = 2;}

                        else if ( (LA128_0==91) ) {s = 3;}

                        else if ( (LA128_0==92) ) {s = 4;}

                        else if ( (LA128_0==93) ) {s = 5;}

                        else if ( (LA128_0==94) ) {s = 6;}

                        else if ( (LA128_0==95) ) {s = 7;}

                        else if ( (LA128_0==96) ) {s = 8;}

                        else if ( (LA128_0==97) ) {s = 9;}

                        else if ( (LA128_0==40) && (synpred193_Java())) {s = 10;}

                        else if ( (LA128_0==42) ) {s = 11;}

                         
                        input.seek(index128_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 128, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA140_eotS =
        "\31\uffff";
    static final String DFA140_eofS =
        "\31\uffff";
    static final String DFA140_minS =
        "\1\50\1\uffff\1\52\1\4\25\uffff";
    static final String DFA140_maxS =
        "\1\52\1\uffff\1\52\1\161\25\uffff";
    static final String DFA140_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\24\3";
    static final String DFA140_specialS =
        "\1\0\2\uffff\1\1\25\uffff}>";
    static final String[] DFA140_transitionS = {
            "\1\1\1\uffff\1\2",
            "",
            "\1\3",
            "\1\26\1\uffff\1\17\1\20\1\21\3\16\36\uffff\1\4\4\uffff\1\30"+
            "\10\uffff\10\27\1\uffff\1\15\1\13\2\uffff\1\14\1\24\1\22\1\23"+
            "\40\uffff\1\5\1\6\2\uffff\1\7\1\10\1\11\1\12\1\25",
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

    static final short[] DFA140_eot = DFA.unpackEncodedString(DFA140_eotS);
    static final short[] DFA140_eof = DFA.unpackEncodedString(DFA140_eofS);
    static final char[] DFA140_min = DFA.unpackEncodedStringToUnsignedChars(DFA140_minS);
    static final char[] DFA140_max = DFA.unpackEncodedStringToUnsignedChars(DFA140_maxS);
    static final short[] DFA140_accept = DFA.unpackEncodedString(DFA140_acceptS);
    static final short[] DFA140_special = DFA.unpackEncodedString(DFA140_specialS);
    static final short[][] DFA140_transition;

    static {
        int numStates = DFA140_transitionS.length;
        DFA140_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA140_transition[i] = DFA.unpackEncodedString(DFA140_transitionS[i]);
        }
    }

    class DFA140 extends DFA {

        public DFA140(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 140;
            this.eot = DFA140_eot;
            this.eof = DFA140_eof;
            this.min = DFA140_min;
            this.max = DFA140_max;
            this.accept = DFA140_accept;
            this.special = DFA140_special;
            this.transition = DFA140_transition;
        }
        public String getDescription() {
            return "1026:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA140_0 = input.LA(1);

                         
                        int index140_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA140_0==40) && (synpred210_Java())) {s = 1;}

                        else if ( (LA140_0==42) ) {s = 2;}

                         
                        input.seek(index140_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA140_3 = input.LA(1);

                         
                        int index140_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA140_3==42) && (synpred211_Java())) {s = 4;}

                        else if ( (LA140_3==105) && (synpred212_Java())) {s = 5;}

                        else if ( (LA140_3==106) && (synpred212_Java())) {s = 6;}

                        else if ( (LA140_3==109) && (synpred212_Java())) {s = 7;}

                        else if ( (LA140_3==110) && (synpred212_Java())) {s = 8;}

                        else if ( (LA140_3==111) && (synpred212_Java())) {s = 9;}

                        else if ( (LA140_3==112) && (synpred212_Java())) {s = 10;}

                        else if ( (LA140_3==66) && (synpred212_Java())) {s = 11;}

                        else if ( (LA140_3==69) && (synpred212_Java())) {s = 12;}

                        else if ( (LA140_3==65) && (synpred212_Java())) {s = 13;}

                        else if ( ((LA140_3>=HexLiteral && LA140_3<=DecimalLiteral)) && (synpred212_Java())) {s = 14;}

                        else if ( (LA140_3==FloatingPointLiteral) && (synpred212_Java())) {s = 15;}

                        else if ( (LA140_3==CharacterLiteral) && (synpred212_Java())) {s = 16;}

                        else if ( (LA140_3==StringLiteral) && (synpred212_Java())) {s = 17;}

                        else if ( (LA140_3==71) && (synpred212_Java())) {s = 18;}

                        else if ( (LA140_3==72) && (synpred212_Java())) {s = 19;}

                        else if ( (LA140_3==70) && (synpred212_Java())) {s = 20;}

                        else if ( (LA140_3==113) && (synpred212_Java())) {s = 21;}

                        else if ( (LA140_3==Identifier) && (synpred212_Java())) {s = 22;}

                        else if ( ((LA140_3>=56 && LA140_3<=63)) && (synpred212_Java())) {s = 23;}

                        else if ( (LA140_3==47) && (synpred212_Java())) {s = 24;}

                         
                        input.seek(index140_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 140, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA146_eotS =
        "\22\uffff";
    static final String DFA146_eofS =
        "\22\uffff";
    static final String DFA146_minS =
        "\1\4\2\uffff\1\0\16\uffff";
    static final String DFA146_maxS =
        "\1\161\2\uffff\1\0\16\uffff";
    static final String DFA146_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\14\uffff\1\3";
    static final String DFA146_specialS =
        "\3\uffff\1\0\16\uffff}>";
    static final String[] DFA146_transitionS = {
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
            "",
            ""
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
            return "1057:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA146_3 = input.LA(1);

                         
                        int index146_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred224_Java()) ) {s = 17;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index146_3);
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
    static final String DFA147_eotS =
        "\7\uffff";
    static final String DFA147_eofS =
        "\7\uffff";
    static final String DFA147_minS =
        "\1\4\1\0\1\35\2\uffff\1\61\1\35";
    static final String DFA147_maxS =
        "\1\161\1\0\1\103\2\uffff\1\61\1\103";
    static final String DFA147_acceptS =
        "\3\uffff\1\2\1\1\2\uffff";
    static final String DFA147_specialS =
        "\1\uffff\1\0\5\uffff}>";
    static final String[] DFA147_transitionS = {
            "\1\1\1\uffff\6\3\43\uffff\1\3\10\uffff\10\2\1\uffff\2\3\2\uffff"+
            "\4\3\40\uffff\2\3\2\uffff\5\3",
            "\1\uffff",
            "\1\3\22\uffff\1\5\22\uffff\1\4",
            "",
            "",
            "\1\6",
            "\1\3\22\uffff\1\5\22\uffff\1\4"
    };

    static final short[] DFA147_eot = DFA.unpackEncodedString(DFA147_eotS);
    static final short[] DFA147_eof = DFA.unpackEncodedString(DFA147_eofS);
    static final char[] DFA147_min = DFA.unpackEncodedStringToUnsignedChars(DFA147_minS);
    static final char[] DFA147_max = DFA.unpackEncodedStringToUnsignedChars(DFA147_maxS);
    static final short[] DFA147_accept = DFA.unpackEncodedString(DFA147_acceptS);
    static final short[] DFA147_special = DFA.unpackEncodedString(DFA147_specialS);
    static final short[][] DFA147_transition;

    static {
        int numStates = DFA147_transitionS.length;
        DFA147_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA147_transition[i] = DFA.unpackEncodedString(DFA147_transitionS[i]);
        }
    }

    class DFA147 extends DFA {

        public DFA147(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 147;
            this.eot = DFA147_eot;
            this.eof = DFA147_eof;
            this.min = DFA147_min;
            this.max = DFA147_max;
            this.accept = DFA147_accept;
            this.special = DFA147_special;
            this.transition = DFA147_transition;
        }
        public String getDescription() {
            return "1066:12: ( type | expression )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA147_1 = input.LA(1);

                         
                        int index147_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred229_Java()) ) {s = 4;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index147_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 147, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA150_eotS =
        "\41\uffff";
    static final String DFA150_eofS =
        "\1\4\40\uffff";
    static final String DFA150_minS =
        "\1\32\1\0\1\uffff\1\0\35\uffff";
    static final String DFA150_maxS =
        "\1\156\1\0\1\uffff\1\0\35\uffff";
    static final String DFA150_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\34\uffff";
    static final String DFA150_specialS =
        "\1\uffff\1\0\1\uffff\1\1\35\uffff}>";
    static final String[] DFA150_transitionS = {
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

    static final short[] DFA150_eot = DFA.unpackEncodedString(DFA150_eotS);
    static final short[] DFA150_eof = DFA.unpackEncodedString(DFA150_eofS);
    static final char[] DFA150_min = DFA.unpackEncodedStringToUnsignedChars(DFA150_minS);
    static final char[] DFA150_max = DFA.unpackEncodedStringToUnsignedChars(DFA150_maxS);
    static final short[] DFA150_accept = DFA.unpackEncodedString(DFA150_acceptS);
    static final short[] DFA150_special = DFA.unpackEncodedString(DFA150_specialS);
    static final short[][] DFA150_transition;

    static {
        int numStates = DFA150_transitionS.length;
        DFA150_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA150_transition[i] = DFA.unpackEncodedString(DFA150_transitionS[i]);
        }
    }

    class DFA150 extends DFA {

        public DFA150(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 150;
            this.eot = DFA150_eot;
            this.eof = DFA150_eof;
            this.min = DFA150_min;
            this.max = DFA150_max;
            this.accept = DFA150_accept;
            this.special = DFA150_special;
            this.transition = DFA150_transition;
        }
        public String getDescription() {
            return "1071:34: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA150_1 = input.LA(1);

                         
                        int index150_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred232_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index150_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA150_3 = input.LA(1);

                         
                        int index150_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred232_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index150_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 150, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA152_eotS =
        "\41\uffff";
    static final String DFA152_eofS =
        "\1\4\40\uffff";
    static final String DFA152_minS =
        "\1\32\1\0\1\uffff\1\0\35\uffff";
    static final String DFA152_maxS =
        "\1\156\1\0\1\uffff\1\0\35\uffff";
    static final String DFA152_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\34\uffff";
    static final String DFA152_specialS =
        "\1\uffff\1\0\1\uffff\1\1\35\uffff}>";
    static final String[] DFA152_transitionS = {
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

    static final short[] DFA152_eot = DFA.unpackEncodedString(DFA152_eotS);
    static final short[] DFA152_eof = DFA.unpackEncodedString(DFA152_eofS);
    static final char[] DFA152_min = DFA.unpackEncodedStringToUnsignedChars(DFA152_minS);
    static final char[] DFA152_max = DFA.unpackEncodedStringToUnsignedChars(DFA152_maxS);
    static final short[] DFA152_accept = DFA.unpackEncodedString(DFA152_acceptS);
    static final short[] DFA152_special = DFA.unpackEncodedString(DFA152_specialS);
    static final short[][] DFA152_transition;

    static {
        int numStates = DFA152_transitionS.length;
        DFA152_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA152_transition[i] = DFA.unpackEncodedString(DFA152_transitionS[i]);
        }
    }

    class DFA152 extends DFA {

        public DFA152(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 152;
            this.eot = DFA152_eot;
            this.eof = DFA152_eof;
            this.min = DFA152_min;
            this.max = DFA152_max;
            this.accept = DFA152_accept;
            this.special = DFA152_special;
            this.transition = DFA152_transition;
        }
        public String getDescription() {
            return "1075:38: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA152_1 = input.LA(1);

                         
                        int index152_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index152_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA152_3 = input.LA(1);

                         
                        int index152_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index152_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 152, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA157_eotS =
        "\13\uffff";
    static final String DFA157_eofS =
        "\13\uffff";
    static final String DFA157_minS =
        "\1\35\1\4\1\uffff\1\45\7\uffff";
    static final String DFA157_maxS =
        "\1\102\1\161\1\uffff\1\161\7\uffff";
    static final String DFA157_acceptS =
        "\2\uffff\1\3\1\uffff\1\1\1\2\1\4\1\6\1\7\1\10\1\5";
    static final String DFA157_specialS =
        "\13\uffff}>";
    static final String[] DFA157_transitionS = {
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

    static final short[] DFA157_eot = DFA.unpackEncodedString(DFA157_eotS);
    static final short[] DFA157_eof = DFA.unpackEncodedString(DFA157_eofS);
    static final char[] DFA157_min = DFA.unpackEncodedStringToUnsignedChars(DFA157_minS);
    static final char[] DFA157_max = DFA.unpackEncodedStringToUnsignedChars(DFA157_maxS);
    static final short[] DFA157_accept = DFA.unpackEncodedString(DFA157_acceptS);
    static final short[] DFA157_special = DFA.unpackEncodedString(DFA157_specialS);
    static final short[][] DFA157_transition;

    static {
        int numStates = DFA157_transitionS.length;
        DFA157_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA157_transition[i] = DFA.unpackEncodedString(DFA157_transitionS[i]);
        }
    }

    class DFA157 extends DFA {

        public DFA157(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 157;
            this.eot = DFA157_eot;
            this.eof = DFA157_eof;
            this.min = DFA157_min;
            this.max = DFA157_max;
            this.accept = DFA157_accept;
            this.special = DFA157_special;
            this.transition = DFA157_transition;
        }
        public String getDescription() {
            return "1080:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );";
        }
    }
    static final String DFA156_eotS =
        "\41\uffff";
    static final String DFA156_eofS =
        "\1\1\40\uffff";
    static final String DFA156_minS =
        "\1\32\1\uffff\1\0\36\uffff";
    static final String DFA156_maxS =
        "\1\156\1\uffff\1\0\36\uffff";
    static final String DFA156_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA156_specialS =
        "\2\uffff\1\0\36\uffff}>";
    static final String[] DFA156_transitionS = {
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
            return "()+ loopback of 1082:9: ( '[' expression ']' )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA156_2 = input.LA(1);

                         
                        int index156_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred244_Java()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index156_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 156, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA163_eotS =
        "\41\uffff";
    static final String DFA163_eofS =
        "\1\2\40\uffff";
    static final String DFA163_minS =
        "\1\32\1\0\37\uffff";
    static final String DFA163_maxS =
        "\1\156\1\0\37\uffff";
    static final String DFA163_acceptS =
        "\2\uffff\1\2\35\uffff\1\1";
    static final String DFA163_specialS =
        "\1\uffff\1\0\37\uffff}>";
    static final String[] DFA163_transitionS = {
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

    static final short[] DFA163_eot = DFA.unpackEncodedString(DFA163_eotS);
    static final short[] DFA163_eof = DFA.unpackEncodedString(DFA163_eofS);
    static final char[] DFA163_min = DFA.unpackEncodedStringToUnsignedChars(DFA163_minS);
    static final char[] DFA163_max = DFA.unpackEncodedStringToUnsignedChars(DFA163_maxS);
    static final short[] DFA163_accept = DFA.unpackEncodedString(DFA163_acceptS);
    static final short[] DFA163_special = DFA.unpackEncodedString(DFA163_specialS);
    static final short[][] DFA163_transition;

    static {
        int numStates = DFA163_transitionS.length;
        DFA163_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA163_transition[i] = DFA.unpackEncodedString(DFA163_transitionS[i]);
        }
    }

    class DFA163 extends DFA {

        public DFA163(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 163;
            this.eot = DFA163_eot;
            this.eof = DFA163_eof;
            this.min = DFA163_min;
            this.max = DFA163_max;
            this.accept = DFA163_accept;
            this.special = DFA163_special;
            this.transition = DFA163_transition;
        }
        public String getDescription() {
            return "()* loopback of 1108:28: ( '[' expression ']' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA163_1 = input.LA(1);

                         
                        int index163_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 32;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index163_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 163, _s, input);
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
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest2206 = new BitSet(new long[]{0x0004100014000000L});
    public static final BitSet FOLLOW_50_in_voidMethodDeclaratorRest2220 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest2224 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest2244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_voidMethodDeclaratorRest2260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest2301 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_48_in_interfaceMethodDeclaratorRest2313 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_interfaceMethodDeclaratorRest2315 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_50_in_interfaceMethodDeclaratorRest2331 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest2335 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodDeclaratorRest2341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl2368 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl2371 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_interfaceGenericMethodDecl2375 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl2378 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest2413 = new BitSet(new long[]{0x0004000004000000L});
    public static final BitSet FOLLOW_50_in_voidInterfaceMethodDeclaratorRest2423 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest2427 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_voidInterfaceMethodDeclaratorRest2439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest2464 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_50_in_constructorDeclaratorRest2474 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest2478 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_constructorBody_in_constructorDeclaratorRest2491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator2522 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_48_in_constantDeclarator2526 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_constantDeclarator2528 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_51_in_constantDeclarator2534 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclarator2538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators2578 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclarators2582 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators2586 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_variableDeclarator2615 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_variableDeclarator2620 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId2667 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_variableDeclaratorId2670 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_variableDeclaratorId2672 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_arrayInitializer2750 = new BitSet(new long[]{0xFF00B00000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2757 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer2761 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2765 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer2771 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_arrayInitializer2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier2801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_modifier2813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier2825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier2837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier2849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_modifier2861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_packageOrTypeName2882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName2907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_typeName2934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2960 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2963 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2965 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type2978 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2981 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2983 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType3003 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType3005 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_classOrInterfaceType3009 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType3011 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType3013 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_variableModifier3126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeArguments3155 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments3157 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeArguments3160 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments3162 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeArguments3166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument3189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_typeArgument3199 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument3202 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument3210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3244 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_qualifiedNameList3249 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3253 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_66_in_formalParameters3284 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000208L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3289 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_formalParameters3295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameterDecls3322 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls3324 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000010L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls3326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3349 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_formalParameterDeclsRest3352 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest3354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_formalParameterDeclsRest3366 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody3397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_constructorBody3422 = new BitSet(new long[]{0xFF20F13F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody3429 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_constructorBody3435 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_constructorBody3438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3461 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation3464 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3472 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation3474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3484 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_explicitConstructorInvocation3486 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3488 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_explicitConstructorInvocation3491 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3493 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation3495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName3525 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_qualifiedName3529 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName3533 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal3561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal3571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal3581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal3591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal3601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_literal3611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_booleanLiteral3673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_booleanLiteral3685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations3708 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_annotation3730 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation3732 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation3736 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation3740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_elementValue_in_annotation3744 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotation3749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName3773 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_annotationName3776 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName3778 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3799 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_elementValuePairs3802 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3804 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair3825 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_elementValuePair3827 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue3872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_elementValueArrayInitializer3895 = new BitSet(new long[]{0xFF00B20000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3898 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer3901 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3903 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer3910 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_elementValueArrayInitializer3914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_annotationTypeDeclaration3941 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_annotationTypeDeclaration3943 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration3945 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_annotationTypeBody3970 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3973 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_annotationTypeBody3977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeElementDeclaration4000 = new BitSet(new long[]{0xFF00403F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration4002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest4025 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest4027 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest4029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementRest4039 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest4041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest4052 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest4054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest4065 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest4067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest4078 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest4080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest4104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest4114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest4137 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest4139 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotationMethodRest4141 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest4143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest4167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_defaultValue4190 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue4192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_block4217 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_block4219 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_block4222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement4245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement4255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement4265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4289 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_localVariableDeclarationStatement4291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4310 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration4312 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration4314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_variableModifiers4337 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_block_in_statement4355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4365 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4367 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement4370 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4372 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_statement4386 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4388 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4390 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_statement4400 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement4414 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement4416 = new BitSet(new long[]{0xFF00900804000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forControl_in_statement4418 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_statement4420 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement4432 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4434 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement4446 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4448 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement4450 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4452 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement4464 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4466 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_catches_in_statement4478 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_statement4480 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement4494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement4508 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement4530 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4532 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_statement4534 = new BitSet(new long[]{0x0000200000000000L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4536 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_statement4538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_statement4548 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement4550 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement4552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement4562 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4564 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement4577 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement4579 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement4591 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement4593 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_statement4606 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement4608 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_statement4621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement4632 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement4634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement4644 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement4646 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement4648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches4671 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_catchClause_in_catches4674 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_catchClause4699 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause4701 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause4703 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_catchClause4705 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_catchClause4707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter4726 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameter4728 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter4730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4758 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4785 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60002FBD7E6L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4788 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_89_in_switchLabel4812 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel4814 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_switchLabel4826 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel4828 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_switchLabel4840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_forControl4873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl4883 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl4886 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_forControl4888 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl4891 = new BitSet(new long[]{0xFF00900800000FD2L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forUpdate_in_forControl4893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit4913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit4923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_enhancedForControl4946 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_enhancedForControl4948 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enhancedForControl4950 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_enhancedForControl4952 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_enhancedForControl4954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate4973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_parExpression4994 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_parExpression4996 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_parExpression4998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList5021 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_expressionList5024 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expressionList5026 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression5047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression5070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression5097 = new BitSet(new long[]{0x0008050000000002L,0x00000003FC000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression5100 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expression5102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator5137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator5147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator5157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator5167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator5177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator5187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator5197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator5207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_assignmentOperator5228 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_assignmentOperator5232 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5270 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5274 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5278 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5313 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator5317 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator5321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5350 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_conditionalExpression5354 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5356 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_conditionalExpression5358 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5382 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalOrExpression5386 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5388 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5410 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_conditionalAndExpression5414 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5416 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5438 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_inclusiveOrExpression5442 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5444 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5466 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_exclusiveOrExpression5470 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5472 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5494 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_andExpression5498 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5500 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5522 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression5526 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5534 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5556 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_instanceOfExpression5559 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression5561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5582 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression5586 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5588 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp5623 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp5627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp5657 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp5661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp5682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp5693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5713 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression5717 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5719 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_shiftOp5750 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_shiftOp5754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp5786 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5790 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp5824 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5858 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression5862 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5870 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5892 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression5896 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5910 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_105_in_unaryExpression5936 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_unaryExpression5948 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression5960 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpression5972 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus6003 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_unaryExpressionNotPlusMinus6015 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus6037 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus6039 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus6042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression6065 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression6067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression6069 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression6071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression6080 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_type_in_castExpression6083 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_castExpression6087 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression6090 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary6111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_primary6121 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary6124 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary6126 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_primary6141 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_primary6143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary6153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary6163 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_creator_in_primary6165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary6175 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary6178 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary6180 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary6195 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_48_in_primary6198 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_primary6200 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_primary6204 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary6206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_primary6216 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_primary6218 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary6220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix6240 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix6242 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6246 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix6248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix6259 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix6261 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix6263 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6286 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix6288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6298 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix6300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6310 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_identifierSuffix6312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_identifierSuffix6324 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix6336 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix6338 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix6340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator6359 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_createdName_in_creator6361 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator6363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createdName_in_creator6373 = new BitSet(new long[]{0x0001000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator6376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator6380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName6400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName6410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator6433 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator6436 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator6438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6457 = new BitSet(new long[]{0xFF02900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6471 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6474 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6476 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest6480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6494 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6496 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6499 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6501 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6503 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest6508 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest6510 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest6541 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest6543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6567 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocation6569 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation6571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_nonWildcardTypeArguments6594 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments6596 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_nonWildcardTypeArguments6598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6621 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector6623 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_selector6625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6636 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_selector6638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6648 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_selector6650 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_selector6652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector6662 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector6664 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector6666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_selector6676 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_selector6678 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_selector6680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_superSuffix6713 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix6715 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_arguments6741 = new BitSet(new long[]{0xFF00900800000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_expressionList_in_arguments6743 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_arguments6746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred5_Java96 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred5_Java112 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_synpred5_Java118 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java126 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java145 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java151 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred53_Java1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceConstant_in_synpred61_Java1975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred81_Java2801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_synpred82_Java2813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred108_Java3429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred112_Java3461 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_synpred112_Java3464 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_synpred112_Java3472 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_synpred112_Java3474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred123_Java3708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred146_Java4245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred147_Java4255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_synpred152_Java4400 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_synpred152_Java4402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred157_Java4478 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_synpred157_Java4480 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_synpred157_Java4482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred158_Java4494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchLabel_in_synpred173_Java4785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred175_Java4812 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_synpred175_Java4814 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred175_Java4816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred176_Java4826 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred176_Java4828 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred176_Java4830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_synpred177_Java4873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred181_Java4913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred183_Java5100 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred183_Java5102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred193_Java5218 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred193_Java5220 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred193_Java5222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred194_Java5258 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred194_Java5260 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred194_Java5262 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred194_Java5264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred195_Java5303 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred195_Java5305 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred195_Java5307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred206_Java5615 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred206_Java5617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred207_Java5649 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred207_Java5651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred210_Java5742 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred210_Java5744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred211_Java5776 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred211_Java5778 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred211_Java5780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred212_Java5816 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred212_Java5818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred224_Java6027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_synpred228_Java6065 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred228_Java6067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_synpred228_Java6069 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred228_Java6071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred229_Java6083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred231_Java6124 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred231_Java6126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred232_Java6130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred237_Java6178 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred237_Java6180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred238_Java6184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred244_Java6259 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred244_Java6261 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred244_Java6263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred257_Java6499 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred257_Java6501 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred257_Java6503 = new BitSet(new long[]{0x0000000000000002L});

}