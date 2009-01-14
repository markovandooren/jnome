// $ANTLR 3.1.1 /Users/marko/git/jnome/src/jnome/input/parser/Java.g 2009-01-14 21:34:07

package jnome.input.parser;

import chameleon.core.MetamodelException;

import chameleon.core.context.ContextFactory;

import chameleon.core.compilationunit.CompilationUnit;

import chameleon.core.declaration.SimpleNameSignature;

import chameleon.core.element.ChameleonProgrammerException;

import chameleon.core.language.Language;

import chameleon.core.modifier.Modifier;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;

import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.Import;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.namespacepart.DemandImport;

import chameleon.core.type.Type;
import chameleon.core.type.RegularType;
import chameleon.core.type.TypeReference;

import chameleon.core.type.generics.GenericParameter;

import chameleon.core.type.inheritance.SubtypeRelation;

import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Protected;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.modifier.Native;

import jnome.core.language.Java;

import jnome.core.type.JavaTypeReference;

import jnome.core.modifier.StrictFP;
import jnome.core.modifier.Transient;
import jnome.core.modifier.Volatile;
import jnome.core.modifier.Synchronized;

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


        public JavaParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JavaParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[402+1];
             
             
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:283:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:285:5: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:285:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotations_in_compilationUnit89);
                    annotations1=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit105);
                            np=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, np.getTree());
                            if ( state.backtracking==0 ) {
                              processPackageDeclaration(retval.element,np.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:90: (imp= importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==27) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:91: imp= importDeclaration
                            	    {
                            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit111);
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

                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:159: (typech= typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==ENUM||LA2_0==26||LA2_0==28||(LA2_0>=31 && LA2_0<=37)||LA2_0==46||LA2_0==73) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:160: typech= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit119);
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:287:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit138);
                            cd=classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                            if ( state.backtracking==0 ) {
                              processType(retval.element,cd.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:287:85: (typech= typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==ENUM||LA3_0==26||LA3_0==28||(LA3_0>=31 && LA3_0<=37)||LA3_0==46||LA3_0==73) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:287:86: typech= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit144);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:9: (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )*
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:9: (np= packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==25) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:10: np= packageDeclaration
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit170);
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

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:89: (imp= importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==27) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:90: imp= importDeclaration
                    	    {
                    	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit178);
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

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:158: (typech= typeDeclaration )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ENUM||LA7_0==26||LA7_0==28||(LA7_0>=31 && LA7_0<=37)||LA7_0==46||LA7_0==73) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:159: typech= typeDeclaration
                    	    {
                    	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit186);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:292:1: packageDeclaration returns [NamespacePart element] : 'package' qn= qualifiedName ';' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:293:5: ( 'package' qn= qualifiedName ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:293:9: 'package' qn= qualifiedName ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal2=(Token)match(input,25,FOLLOW_25_in_packageDeclaration212); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal2_tree = (Object)adaptor.create(string_literal2);
            adaptor.addChild(root_0, string_literal2_tree);
            }
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration216);
            qn=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qn.getTree());
            char_literal3=(Token)match(input,26,FOLLOW_26_in_packageDeclaration218); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:304:1: importDeclaration returns [Import element] : 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' ;
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:5: ( 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:9: 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal4=(Token)match(input,27,FOLLOW_27_in_importDeclaration256); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal4_tree = (Object)adaptor.create(string_literal4);
            adaptor.addChild(root_0, string_literal4_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:20: (st= 'static' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==28) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: st= 'static'
                    {
                    st=(Token)match(input,28,FOLLOW_28_in_importDeclaration260); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    st_tree = (Object)adaptor.create(st);
                    adaptor.addChild(root_0, st_tree);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_qualifiedName_in_importDeclaration265);
            qn=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qn.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:52: (star= ( '.' '*' ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==29) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: star= ( '.' '*' )
                    {
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:53: ( '.' '*' )
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:54: '.' '*'
                    {
                    char_literal5=(Token)match(input,29,FOLLOW_29_in_importDeclaration270); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal5_tree = (Object)adaptor.create(char_literal5);
                    adaptor.addChild(root_0, char_literal5_tree);
                    }
                    char_literal6=(Token)match(input,30,FOLLOW_30_in_importDeclaration272); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal6_tree = (Object)adaptor.create(char_literal6);
                    adaptor.addChild(root_0, char_literal6_tree);
                    }

                    }


                    }
                    break;

            }

            char_literal7=(Token)match(input,26,FOLLOW_26_in_importDeclaration276); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:318:1: typeDeclaration returns [Type element] : (cd= classOrInterfaceDeclaration | ';' );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:319:5: (cd= classOrInterfaceDeclaration | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:319:9: cd= classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration312);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:320:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal8=(Token)match(input,26,FOLLOW_26_in_typeDeclaration324); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:326:1: classOrInterfaceDeclaration returns [Type element] : classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration ) ;
    public final JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        JavaParser.classOrInterfaceDeclaration_return retval = new JavaParser.classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int classOrInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifiers_return classOrInterfaceModifiers9 = null;

        JavaParser.classDeclaration_return classDeclaration10 = null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration11 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:327:5: ( classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:327:9: classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration354);
            classOrInterfaceModifiers9=classOrInterfaceModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceModifiers9.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:327:35: ( classDeclaration | interfaceDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:327:36: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration357);
                    classDeclaration10=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration10.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:327:55: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration361);
                    interfaceDeclaration11=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration11.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:330:1: classOrInterfaceModifiers returns [List<Modifier> element] : (mod= classOrInterfaceModifier )* ;
    public final JavaParser.classOrInterfaceModifiers_return classOrInterfaceModifiers() throws RecognitionException {
        JavaParser.classOrInterfaceModifiers_return retval = new JavaParser.classOrInterfaceModifiers_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifier_return mod = null;



        retval.element = new ArrayList<Modifier>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:332:5: ( (mod= classOrInterfaceModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:332:9: (mod= classOrInterfaceModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:332:9: (mod= classOrInterfaceModifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:332:10: mod= classOrInterfaceModifier
            	    {
            	    pushFollow(FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers397);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:335:1: classOrInterfaceModifier returns [Modifier element] : ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' );
    public final JavaParser.classOrInterfaceModifier_return classOrInterfaceModifier() throws RecognitionException {
        JavaParser.classOrInterfaceModifier_return retval = new JavaParser.classOrInterfaceModifier_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal13=null;
        Token string_literal14=null;
        Token string_literal15=null;
        Token string_literal16=null;
        Token string_literal17=null;
        Token string_literal18=null;
        Token string_literal19=null;
        JavaParser.annotation_return annotation12 = null;


        Object string_literal13_tree=null;
        Object string_literal14_tree=null;
        Object string_literal15_tree=null;
        Object string_literal16_tree=null;
        Object string_literal17_tree=null;
        Object string_literal18_tree=null;
        Object string_literal19_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:336:5: ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:336:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_classOrInterfaceModifier424);
                    annotation12=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation12.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:337:9: 'public'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal13=(Token)match(input,31,FOLLOW_31_in_classOrInterfaceModifier437); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal13_tree = (Object)adaptor.create(string_literal13);
                    adaptor.addChild(root_0, string_literal13_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Public();
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:338:9: 'protected'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal14=(Token)match(input,32,FOLLOW_32_in_classOrInterfaceModifier453); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal14_tree = (Object)adaptor.create(string_literal14);
                    adaptor.addChild(root_0, string_literal14_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Protected();
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:339:9: 'private'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal15=(Token)match(input,33,FOLLOW_33_in_classOrInterfaceModifier466); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal15_tree = (Object)adaptor.create(string_literal15);
                    adaptor.addChild(root_0, string_literal15_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Private();
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:340:9: 'abstract'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal16=(Token)match(input,34,FOLLOW_34_in_classOrInterfaceModifier481); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal16_tree = (Object)adaptor.create(string_literal16);
                    adaptor.addChild(root_0, string_literal16_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Abstract();
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:341:9: 'static'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal17=(Token)match(input,28,FOLLOW_28_in_classOrInterfaceModifier495); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal17_tree = (Object)adaptor.create(string_literal17);
                    adaptor.addChild(root_0, string_literal17_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Static();
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal18=(Token)match(input,35,FOLLOW_35_in_classOrInterfaceModifier511); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal18_tree = (Object)adaptor.create(string_literal18);
                    adaptor.addChild(root_0, string_literal18_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Final();
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:343:9: 'strictfp'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal19=(Token)match(input,36,FOLLOW_36_in_classOrInterfaceModifier528); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal19_tree = (Object)adaptor.create(string_literal19);
                    adaptor.addChild(root_0, string_literal19_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:1: modifiers returns [List<Modifier> element] : (mod= modifier )* ;
    public final JavaParser.modifiers_return modifiers() throws RecognitionException {
        JavaParser.modifiers_return retval = new JavaParser.modifiers_return();
        retval.start = input.LT(1);
        int modifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifier_return mod = null;



        retval.element = new ArrayList<Modifier>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:348:5: ( (mod= modifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:348:9: (mod= modifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:348:9: (mod= modifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:348:10: mod= modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_modifiers563);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:351:1: classDeclaration returns [Type element] : (cd= normalClassDeclaration | ed= enumDeclaration );
    public final JavaParser.classDeclaration_return classDeclaration() throws RecognitionException {
        JavaParser.classDeclaration_return retval = new JavaParser.classDeclaration_return();
        retval.start = input.LT(1);
        int classDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalClassDeclaration_return cd = null;

        JavaParser.enumDeclaration_return ed = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:352:5: (cd= normalClassDeclaration | ed= enumDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:352:9: cd= normalClassDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration591);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:9: ed= enumDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration605);
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
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "normalClassDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:356:1: normalClassDeclaration returns [Type element] : 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' ifs= typeList )? body= classBody ;
    public final JavaParser.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        JavaParser.normalClassDeclaration_return retval = new JavaParser.normalClassDeclaration_return();
        retval.start = input.LT(1);
        int normalClassDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token string_literal20=null;
        Token string_literal21=null;
        Token string_literal22=null;
        JavaParser.typeParameters_return params = null;

        JavaParser.type_return sc = null;

        JavaParser.typeList_return ifs = null;

        JavaParser.classBody_return body = null;


        Object name_tree=null;
        Object string_literal20_tree=null;
        Object string_literal21_tree=null;
        Object string_literal22_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:5: ( 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' ifs= typeList )? body= classBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:9: 'class' name= Identifier (params= typeParameters )? ( 'extends' sc= type )? ( 'implements' ifs= typeList )? body= classBody
            {
            root_0 = (Object)adaptor.nil();

            string_literal20=(Token)match(input,37,FOLLOW_37_in_normalClassDeclaration634); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal20_tree = (Object)adaptor.create(string_literal20);
            adaptor.addChild(root_0, string_literal20_tree);
            }
            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration638); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            if ( state.backtracking==0 ) {
              retval.element = new RegularType(new SimpleNameSignature((name!=null?name.getText():null)));
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:106: (params= typeParameters )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==40) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:107: params= typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration645);
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:358:9: ( 'extends' sc= type )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==38) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:358:10: 'extends' sc= type
                    {
                    string_literal21=(Token)match(input,38,FOLLOW_38_in_normalClassDeclaration659); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal21_tree = (Object)adaptor.create(string_literal21);
                    adaptor.addChild(root_0, string_literal21_tree);
                    }
                    pushFollow(FOLLOW_type_in_normalClassDeclaration663);
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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:359:9: ( 'implements' ifs= typeList )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==39) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:359:10: 'implements' ifs= typeList
                    {
                    string_literal22=(Token)match(input,39,FOLLOW_39_in_normalClassDeclaration677); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal22_tree = (Object)adaptor.create(string_literal22);
                    adaptor.addChild(root_0, string_literal22_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration681);
                    ifs=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ifs.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration695);
            body=classBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, body.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:363:1: typeParameters returns [List<GenericParameter> element] : '<' par= typeParameter ( ',' par= typeParameter )* '>' ;
    public final JavaParser.typeParameters_return typeParameters() throws RecognitionException {
        JavaParser.typeParameters_return retval = new JavaParser.typeParameters_return();
        retval.start = input.LT(1);
        int typeParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal23=null;
        Token char_literal24=null;
        Token char_literal25=null;
        JavaParser.typeParameter_return par = null;


        Object char_literal23_tree=null;
        Object char_literal24_tree=null;
        Object char_literal25_tree=null;

        retval.element = new ArrayList<GenericParameter>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:365:5: ( '<' par= typeParameter ( ',' par= typeParameter )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:365:9: '<' par= typeParameter ( ',' par= typeParameter )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal23=(Token)match(input,40,FOLLOW_40_in_typeParameters726); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal23_tree = (Object)adaptor.create(char_literal23);
            adaptor.addChild(root_0, char_literal23_tree);
            }
            pushFollow(FOLLOW_typeParameter_in_typeParameters730);
            par=typeParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par.getTree());
            if ( state.backtracking==0 ) {
              retval.element.add(par.element);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:365:65: ( ',' par= typeParameter )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==41) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:365:66: ',' par= typeParameter
            	    {
            	    char_literal24=(Token)match(input,41,FOLLOW_41_in_typeParameters734); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal24_tree = (Object)adaptor.create(char_literal24);
            	    adaptor.addChild(root_0, char_literal24_tree);
            	    }
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters738);
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

            char_literal25=(Token)match(input,42,FOLLOW_42_in_typeParameters743); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal25_tree = (Object)adaptor.create(char_literal25);
            adaptor.addChild(root_0, char_literal25_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:368:1: typeParameter returns [GenericParameter element] : name= Identifier ( 'extends' typeBound )? ;
    public final JavaParser.typeParameter_return typeParameter() throws RecognitionException {
        JavaParser.typeParameter_return retval = new JavaParser.typeParameter_return();
        retval.start = input.LT(1);
        int typeParameter_StartIndex = input.index();
        Object root_0 = null;

        Token name=null;
        Token string_literal26=null;
        JavaParser.typeBound_return typeBound27 = null;


        Object name_tree=null;
        Object string_literal26_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:5: (name= Identifier ( 'extends' typeBound )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:9: name= Identifier ( 'extends' typeBound )?
            {
            root_0 = (Object)adaptor.nil();

            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeParameter768); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = (Object)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:25: ( 'extends' typeBound )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==38) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:26: 'extends' typeBound
                    {
                    string_literal26=(Token)match(input,38,FOLLOW_38_in_typeParameter771); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal26_tree = (Object)adaptor.create(string_literal26);
                    adaptor.addChild(root_0, string_literal26_tree);
                    }
                    pushFollow(FOLLOW_typeBound_in_typeParameter773);
                    typeBound27=typeBound();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeBound27.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeBound"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:372:1: typeBound : type ( '&' type )* ;
    public final JavaParser.typeBound_return typeBound() throws RecognitionException {
        JavaParser.typeBound_return retval = new JavaParser.typeBound_return();
        retval.start = input.LT(1);
        int typeBound_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal29=null;
        JavaParser.type_return type28 = null;

        JavaParser.type_return type30 = null;


        Object char_literal29_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:5: ( type ( '&' type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:9: type ( '&' type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeBound802);
            type28=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type28.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:14: ( '&' type )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==43) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:15: '&' type
            	    {
            	    char_literal29=(Token)match(input,43,FOLLOW_43_in_typeBound805); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal29_tree = (Object)adaptor.create(char_literal29);
            	    adaptor.addChild(root_0, char_literal29_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeBound807);
            	    type30=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type30.getTree());

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

    public static class enumDeclaration_return extends ParserRuleReturnScope {
        public Type element;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:376:1: enumDeclaration returns [Type element] : ENUM Identifier ( 'implements' typeList )? enumBody ;
    public final JavaParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        JavaParser.enumDeclaration_return retval = new JavaParser.enumDeclaration_return();
        retval.start = input.LT(1);
        int enumDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token ENUM31=null;
        Token Identifier32=null;
        Token string_literal33=null;
        JavaParser.typeList_return typeList34 = null;

        JavaParser.enumBody_return enumBody35 = null;


        Object ENUM31_tree=null;
        Object Identifier32_tree=null;
        Object string_literal33_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:5: ( ENUM Identifier ( 'implements' typeList )? enumBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:9: ENUM Identifier ( 'implements' typeList )? enumBody
            {
            root_0 = (Object)adaptor.nil();

            ENUM31=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration832); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ENUM31_tree = (Object)adaptor.create(ENUM31);
            adaptor.addChild(root_0, ENUM31_tree);
            }
            Identifier32=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration834); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier32_tree = (Object)adaptor.create(Identifier32);
            adaptor.addChild(root_0, Identifier32_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:25: ( 'implements' typeList )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==39) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:26: 'implements' typeList
                    {
                    string_literal33=(Token)match(input,39,FOLLOW_39_in_enumDeclaration837); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal33_tree = (Object)adaptor.create(string_literal33);
                    adaptor.addChild(root_0, string_literal33_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_enumDeclaration839);
                    typeList34=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList34.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration843);
            enumBody35=enumBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBody35.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
        }
        return retval;
    }
    // $ANTLR end "enumDeclaration"

    public static class enumBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:380:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final JavaParser.enumBody_return enumBody() throws RecognitionException {
        JavaParser.enumBody_return retval = new JavaParser.enumBody_return();
        retval.start = input.LT(1);
        int enumBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal36=null;
        Token char_literal38=null;
        Token char_literal40=null;
        JavaParser.enumConstants_return enumConstants37 = null;

        JavaParser.enumBodyDeclarations_return enumBodyDeclarations39 = null;


        Object char_literal36_tree=null;
        Object char_literal38_tree=null;
        Object char_literal40_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:9: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal36=(Token)match(input,44,FOLLOW_44_in_enumBody862); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal36_tree = (Object)adaptor.create(char_literal36);
            adaptor.addChild(root_0, char_literal36_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:13: ( enumConstants )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==Identifier||LA24_0==73) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody864);
                    enumConstants37=enumConstants();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstants37.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:28: ( ',' )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==41) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ','
                    {
                    char_literal38=(Token)match(input,41,FOLLOW_41_in_enumBody867); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal38_tree = (Object)adaptor.create(char_literal38);
                    adaptor.addChild(root_0, char_literal38_tree);
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:33: ( enumBodyDeclarations )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==26) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody870);
                    enumBodyDeclarations39=enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBodyDeclarations39.getTree());

                    }
                    break;

            }

            char_literal40=(Token)match(input,45,FOLLOW_45_in_enumBody873); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal40_tree = (Object)adaptor.create(char_literal40);
            adaptor.addChild(root_0, char_literal40_tree);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstants"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:384:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final JavaParser.enumConstants_return enumConstants() throws RecognitionException {
        JavaParser.enumConstants_return retval = new JavaParser.enumConstants_return();
        retval.start = input.LT(1);
        int enumConstants_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal42=null;
        JavaParser.enumConstant_return enumConstant41 = null;

        JavaParser.enumConstant_return enumConstant43 = null;


        Object char_literal42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:385:5: ( enumConstant ( ',' enumConstant )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:385:9: enumConstant ( ',' enumConstant )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enumConstant_in_enumConstants892);
            enumConstant41=enumConstant();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstant41.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:385:22: ( ',' enumConstant )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:385:23: ',' enumConstant
            	    {
            	    char_literal42=(Token)match(input,41,FOLLOW_41_in_enumConstants895); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal42_tree = (Object)adaptor.create(char_literal42);
            	    adaptor.addChild(root_0, char_literal42_tree);
            	    }
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants897);
            	    enumConstant43=enumConstant();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstant43.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstant"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:388:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final JavaParser.enumConstant_return enumConstant() throws RecognitionException {
        JavaParser.enumConstant_return retval = new JavaParser.enumConstant_return();
        retval.start = input.LT(1);
        int enumConstant_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier45=null;
        JavaParser.annotations_return annotations44 = null;

        JavaParser.arguments_return arguments46 = null;

        JavaParser.classBody_return classBody47 = null;


        Object Identifier45_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:389:5: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:389:9: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:389:9: ( annotations )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==73) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant922);
                    annotations44=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations44.getTree());

                    }
                    break;

            }

            Identifier45=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstant925); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier45_tree = (Object)adaptor.create(Identifier45);
            adaptor.addChild(root_0, Identifier45_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:389:33: ( arguments )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==66) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant927);
                    arguments46=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments46.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:389:44: ( classBody )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==44) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant930);
                    classBody47=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody47.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumBodyDeclarations"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:392:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final JavaParser.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        JavaParser.enumBodyDeclarations_return retval = new JavaParser.enumBodyDeclarations_return();
        retval.start = input.LT(1);
        int enumBodyDeclarations_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal48=null;
        JavaParser.classBodyDeclaration_return classBodyDeclaration49 = null;


        Object char_literal48_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:393:5: ( ';' ( classBodyDeclaration )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:393:9: ';' ( classBodyDeclaration )*
            {
            root_0 = (Object)adaptor.nil();

            char_literal48=(Token)match(input,26,FOLLOW_26_in_enumBodyDeclarations954); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal48_tree = (Object)adaptor.create(char_literal48);
            adaptor.addChild(root_0, char_literal48_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:393:13: ( classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=Identifier && LA31_0<=ENUM)||LA31_0==26||LA31_0==28||(LA31_0>=31 && LA31_0<=37)||LA31_0==40||LA31_0==44||(LA31_0>=46 && LA31_0<=47)||(LA31_0>=52 && LA31_0<=63)||LA31_0==73) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:393:14: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations957);
            	    classBodyDeclaration49=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration49.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:396:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final JavaParser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        JavaParser.interfaceDeclaration_return retval = new JavaParser.interfaceDeclaration_return();
        retval.start = input.LT(1);
        int interfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration50 = null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration51 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:397:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:397:9: normalInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration982);
                    normalInterfaceDeclaration50=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration50.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:398:9: annotationTypeDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration992);
                    annotationTypeDeclaration51=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration51.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "normalInterfaceDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:401:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        JavaParser.normalInterfaceDeclaration_return retval = new JavaParser.normalInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int normalInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal52=null;
        Token Identifier53=null;
        Token string_literal55=null;
        JavaParser.typeParameters_return typeParameters54 = null;

        JavaParser.typeList_return typeList56 = null;

        JavaParser.interfaceBody_return interfaceBody57 = null;


        Object string_literal52_tree=null;
        Object Identifier53_tree=null;
        Object string_literal55_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:5: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:9: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            root_0 = (Object)adaptor.nil();

            string_literal52=(Token)match(input,46,FOLLOW_46_in_normalInterfaceDeclaration1015); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal52_tree = (Object)adaptor.create(string_literal52);
            adaptor.addChild(root_0, string_literal52_tree);
            }
            Identifier53=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration1017); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier53_tree = (Object)adaptor.create(Identifier53);
            adaptor.addChild(root_0, Identifier53_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:32: ( typeParameters )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==40) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1019);
                    typeParameters54=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters54.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:48: ( 'extends' typeList )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==38) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:49: 'extends' typeList
                    {
                    string_literal55=(Token)match(input,38,FOLLOW_38_in_normalInterfaceDeclaration1023); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal55_tree = (Object)adaptor.create(string_literal55);
                    adaptor.addChild(root_0, string_literal55_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1025);
                    typeList56=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList56.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration1029);
            interfaceBody57=interfaceBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBody57.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeList"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:405:1: typeList : type ( ',' type )* ;
    public final JavaParser.typeList_return typeList() throws RecognitionException {
        JavaParser.typeList_return retval = new JavaParser.typeList_return();
        retval.start = input.LT(1);
        int typeList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal59=null;
        JavaParser.type_return type58 = null;

        JavaParser.type_return type60 = null;


        Object char_literal59_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:5: ( type ( ',' type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:9: type ( ',' type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeList1052);
            type58=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type58.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:14: ( ',' type )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==41) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:15: ',' type
            	    {
            	    char_literal59=(Token)match(input,41,FOLLOW_41_in_typeList1055); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal59_tree = (Object)adaptor.create(char_literal59);
            	    adaptor.addChild(root_0, char_literal59_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeList1057);
            	    type60=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type60.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:409:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final JavaParser.classBody_return classBody() throws RecognitionException {
        JavaParser.classBody_return retval = new JavaParser.classBody_return();
        retval.start = input.LT(1);
        int classBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal61=null;
        Token char_literal63=null;
        JavaParser.classBodyDeclaration_return classBodyDeclaration62 = null;


        Object char_literal61_tree=null;
        Object char_literal63_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:410:5: ( '{' ( classBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:410:9: '{' ( classBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal61=(Token)match(input,44,FOLLOW_44_in_classBody1082); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal61_tree = (Object)adaptor.create(char_literal61);
            adaptor.addChild(root_0, char_literal61_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:410:13: ( classBodyDeclaration )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=Identifier && LA36_0<=ENUM)||LA36_0==26||LA36_0==28||(LA36_0>=31 && LA36_0<=37)||LA36_0==40||LA36_0==44||(LA36_0>=46 && LA36_0<=47)||(LA36_0>=52 && LA36_0<=63)||LA36_0==73) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody1084);
            	    classBodyDeclaration62=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration62.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            char_literal63=(Token)match(input,45,FOLLOW_45_in_classBody1087); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal63_tree = (Object)adaptor.create(char_literal63);
            adaptor.addChild(root_0, char_literal63_tree);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:413:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final JavaParser.interfaceBody_return interfaceBody() throws RecognitionException {
        JavaParser.interfaceBody_return retval = new JavaParser.interfaceBody_return();
        retval.start = input.LT(1);
        int interfaceBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal64=null;
        Token char_literal66=null;
        JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration65 = null;


        Object char_literal64_tree=null;
        Object char_literal66_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:414:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:414:9: '{' ( interfaceBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal64=(Token)match(input,44,FOLLOW_44_in_interfaceBody1110); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal64_tree = (Object)adaptor.create(char_literal64);
            adaptor.addChild(root_0, char_literal64_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:414:13: ( interfaceBodyDeclaration )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=Identifier && LA37_0<=ENUM)||LA37_0==26||LA37_0==28||(LA37_0>=31 && LA37_0<=37)||LA37_0==40||(LA37_0>=46 && LA37_0<=47)||(LA37_0>=52 && LA37_0<=63)||LA37_0==73) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1112);
            	    interfaceBodyDeclaration65=interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBodyDeclaration65.getTree());

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            char_literal66=(Token)match(input,45,FOLLOW_45_in_interfaceBody1115); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal66_tree = (Object)adaptor.create(char_literal66);
            adaptor.addChild(root_0, char_literal66_tree);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classBodyDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:1: classBodyDeclaration : ( ';' | ( 'static' )? block | modifiers memberDecl );
    public final JavaParser.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        JavaParser.classBodyDeclaration_return retval = new JavaParser.classBodyDeclaration_return();
        retval.start = input.LT(1);
        int classBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal67=null;
        Token string_literal68=null;
        JavaParser.block_return block69 = null;

        JavaParser.modifiers_return modifiers70 = null;

        JavaParser.memberDecl_return memberDecl71 = null;


        Object char_literal67_tree=null;
        Object string_literal68_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:5: ( ';' | ( 'static' )? block | modifiers memberDecl )
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

                if ( ((LA39_2>=Identifier && LA39_2<=ENUM)||LA39_2==28||(LA39_2>=31 && LA39_2<=37)||LA39_2==40||(LA39_2>=46 && LA39_2<=47)||(LA39_2>=52 && LA39_2<=63)||LA39_2==73) ) {
                    alt39=3;
                }
                else if ( (LA39_2==44) ) {
                    alt39=2;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal67=(Token)match(input,26,FOLLOW_26_in_classBodyDeclaration1134); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal67_tree = (Object)adaptor.create(char_literal67);
                    adaptor.addChild(root_0, char_literal67_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:9: ( 'static' )? block
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:9: ( 'static' )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==28) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: 'static'
                            {
                            string_literal68=(Token)match(input,28,FOLLOW_28_in_classBodyDeclaration1144); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal68_tree = (Object)adaptor.create(string_literal68);
                            adaptor.addChild(root_0, string_literal68_tree);
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration1147);
                    block69=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block69.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:420:9: modifiers memberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_classBodyDeclaration1157);
                    modifiers70=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers70.getTree());
                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1159);
                    memberDecl71=memberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, memberDecl71.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "memberDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:423:1: memberDecl : ( genericMethodOrConstructorDecl | memberDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final JavaParser.memberDecl_return memberDecl() throws RecognitionException {
        JavaParser.memberDecl_return retval = new JavaParser.memberDecl_return();
        retval.start = input.LT(1);
        int memberDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal74=null;
        Token Identifier75=null;
        Token Identifier77=null;
        JavaParser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl72 = null;

        JavaParser.memberDeclaration_return memberDeclaration73 = null;

        JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest76 = null;

        JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest78 = null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration79 = null;

        JavaParser.classDeclaration_return classDeclaration80 = null;


        Object string_literal74_tree=null;
        Object Identifier75_tree=null;
        Object Identifier77_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:5: ( genericMethodOrConstructorDecl | memberDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
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

                if ( (LA40_2==Identifier||LA40_2==29||LA40_2==40||LA40_2==48) ) {
                    alt40=2;
                }
                else if ( (LA40_2==66) ) {
                    alt40=4;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:9: genericMethodOrConstructorDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl1182);
                    genericMethodOrConstructorDecl72=genericMethodOrConstructorDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, genericMethodOrConstructorDecl72.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:9: memberDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_memberDeclaration_in_memberDecl1192);
                    memberDeclaration73=memberDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, memberDeclaration73.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:426:9: 'void' Identifier voidMethodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal74=(Token)match(input,47,FOLLOW_47_in_memberDecl1202); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal74_tree = (Object)adaptor.create(string_literal74);
                    adaptor.addChild(root_0, string_literal74_tree);
                    }
                    Identifier75=(Token)match(input,Identifier,FOLLOW_Identifier_in_memberDecl1204); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier75_tree = (Object)adaptor.create(Identifier75);
                    adaptor.addChild(root_0, Identifier75_tree);
                    }
                    pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl1206);
                    voidMethodDeclaratorRest76=voidMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, voidMethodDeclaratorRest76.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:427:9: Identifier constructorDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier77=(Token)match(input,Identifier,FOLLOW_Identifier_in_memberDecl1216); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier77_tree = (Object)adaptor.create(Identifier77);
                    adaptor.addChild(root_0, Identifier77_tree);
                    }
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl1218);
                    constructorDeclaratorRest78=constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorDeclaratorRest78.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:428:9: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1228);
                    interfaceDeclaration79=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration79.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:429:9: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1238);
                    classDeclaration80=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration80.getTree());

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

    public static class memberDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "memberDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:432:1: memberDeclaration : type ( methodDeclaration | fieldDeclaration ) ;
    public final JavaParser.memberDeclaration_return memberDeclaration() throws RecognitionException {
        JavaParser.memberDeclaration_return retval = new JavaParser.memberDeclaration_return();
        retval.start = input.LT(1);
        int memberDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.type_return type81 = null;

        JavaParser.methodDeclaration_return methodDeclaration82 = null;

        JavaParser.fieldDeclaration_return fieldDeclaration83 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:433:5: ( type ( methodDeclaration | fieldDeclaration ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:433:9: type ( methodDeclaration | fieldDeclaration )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_memberDeclaration1261);
            type81=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type81.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:433:14: ( methodDeclaration | fieldDeclaration )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==Identifier) ) {
                int LA41_1 = input.LA(2);

                if ( (LA41_1==26||LA41_1==41||LA41_1==48||LA41_1==51) ) {
                    alt41=2;
                }
                else if ( (LA41_1==66) ) {
                    alt41=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 1, input);

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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:433:15: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDeclaration1264);
                    methodDeclaration82=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaration82.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:433:35: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDeclaration1268);
                    fieldDeclaration83=fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, fieldDeclaration83.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 26, memberDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "memberDeclaration"

    public static class genericMethodOrConstructorDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "genericMethodOrConstructorDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:436:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final JavaParser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl() throws RecognitionException {
        JavaParser.genericMethodOrConstructorDecl_return retval = new JavaParser.genericMethodOrConstructorDecl_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.typeParameters_return typeParameters84 = null;

        JavaParser.genericMethodOrConstructorRest_return genericMethodOrConstructorRest85 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:437:5: ( typeParameters genericMethodOrConstructorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:437:9: typeParameters genericMethodOrConstructorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1288);
            typeParameters84=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters84.getTree());
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1290);
            genericMethodOrConstructorRest85=genericMethodOrConstructorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, genericMethodOrConstructorRest85.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, genericMethodOrConstructorDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "genericMethodOrConstructorDecl"

    public static class genericMethodOrConstructorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "genericMethodOrConstructorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:440:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final JavaParser.genericMethodOrConstructorRest_return genericMethodOrConstructorRest() throws RecognitionException {
        JavaParser.genericMethodOrConstructorRest_return retval = new JavaParser.genericMethodOrConstructorRest_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal87=null;
        Token Identifier88=null;
        Token Identifier90=null;
        JavaParser.type_return type86 = null;

        JavaParser.methodDeclaratorRest_return methodDeclaratorRest89 = null;

        JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest91 = null;


        Object string_literal87_tree=null;
        Object Identifier88_tree=null;
        Object Identifier90_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:5: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:9: ( type | 'void' ) Identifier methodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:9: ( type | 'void' )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:10: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest1314);
                            type86=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type86.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:441:17: 'void'
                            {
                            string_literal87=(Token)match(input,47,FOLLOW_47_in_genericMethodOrConstructorRest1318); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal87_tree = (Object)adaptor.create(string_literal87);
                            adaptor.addChild(root_0, string_literal87_tree);
                            }

                            }
                            break;

                    }

                    Identifier88=(Token)match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1321); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier88_tree = (Object)adaptor.create(Identifier88);
                    adaptor.addChild(root_0, Identifier88_tree);
                    }
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1323);
                    methodDeclaratorRest89=methodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaratorRest89.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:442:9: Identifier constructorDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier90=(Token)match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1333); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier90_tree = (Object)adaptor.create(Identifier90);
                    adaptor.addChild(root_0, Identifier90_tree);
                    }
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1335);
                    constructorDeclaratorRest91=constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorDeclaratorRest91.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 28, genericMethodOrConstructorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "genericMethodOrConstructorRest"

    public static class methodDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:445:1: methodDeclaration : Identifier methodDeclaratorRest ;
    public final JavaParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        JavaParser.methodDeclaration_return retval = new JavaParser.methodDeclaration_return();
        retval.start = input.LT(1);
        int methodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier92=null;
        JavaParser.methodDeclaratorRest_return methodDeclaratorRest93 = null;


        Object Identifier92_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:5: ( Identifier methodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:446:9: Identifier methodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            Identifier92=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration1354); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier92_tree = (Object)adaptor.create(Identifier92);
            adaptor.addChild(root_0, Identifier92_tree);
            }
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration1356);
            methodDeclaratorRest93=methodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaratorRest93.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 29, methodDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodDeclaration"

    public static class fieldDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fieldDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:449:1: fieldDeclaration : variableDeclarators ';' ;
    public final JavaParser.fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        JavaParser.fieldDeclaration_return retval = new JavaParser.fieldDeclaration_return();
        retval.start = input.LT(1);
        int fieldDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal95=null;
        JavaParser.variableDeclarators_return variableDeclarators94 = null;


        Object char_literal95_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:450:5: ( variableDeclarators ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:450:9: variableDeclarators ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration1375);
            variableDeclarators94=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators94.getTree());
            char_literal95=(Token)match(input,26,FOLLOW_26_in_fieldDeclaration1377); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal95_tree = (Object)adaptor.create(char_literal95);
            adaptor.addChild(root_0, char_literal95_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 30, fieldDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "fieldDeclaration"

    public static class interfaceBodyDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceBodyDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:453:1: interfaceBodyDeclaration : ( modifiers interfaceMemberDecl | ';' );
    public final JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        JavaParser.interfaceBodyDeclaration_return retval = new JavaParser.interfaceBodyDeclaration_return();
        retval.start = input.LT(1);
        int interfaceBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal98=null;
        JavaParser.modifiers_return modifiers96 = null;

        JavaParser.interfaceMemberDecl_return interfaceMemberDecl97 = null;


        Object char_literal98_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:5: ( modifiers interfaceMemberDecl | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:454:9: modifiers interfaceMemberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_interfaceBodyDeclaration1404);
                    modifiers96=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers96.getTree());
                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1406);
                    interfaceMemberDecl97=interfaceMemberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMemberDecl97.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:455:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal98=(Token)match(input,26,FOLLOW_26_in_interfaceBodyDeclaration1416); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal98_tree = (Object)adaptor.create(char_literal98);
                    adaptor.addChild(root_0, char_literal98_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 31, interfaceBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceBodyDeclaration"

    public static class interfaceMemberDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMemberDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:458:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final JavaParser.interfaceMemberDecl_return interfaceMemberDecl() throws RecognitionException {
        JavaParser.interfaceMemberDecl_return retval = new JavaParser.interfaceMemberDecl_return();
        retval.start = input.LT(1);
        int interfaceMemberDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal101=null;
        Token Identifier102=null;
        JavaParser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl99 = null;

        JavaParser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl100 = null;

        JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest103 = null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration104 = null;

        JavaParser.classDeclaration_return classDeclaration105 = null;


        Object string_literal101_tree=null;
        Object Identifier102_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:459:5: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:459:9: interfaceMethodOrFieldDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1435);
                    interfaceMethodOrFieldDecl99=interfaceMethodOrFieldDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodOrFieldDecl99.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:460:9: interfaceGenericMethodDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1445);
                    interfaceGenericMethodDecl100=interfaceGenericMethodDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceGenericMethodDecl100.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:461:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal101=(Token)match(input,47,FOLLOW_47_in_interfaceMemberDecl1455); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal101_tree = (Object)adaptor.create(string_literal101);
                    adaptor.addChild(root_0, string_literal101_tree);
                    }
                    Identifier102=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl1457); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier102_tree = (Object)adaptor.create(Identifier102);
                    adaptor.addChild(root_0, Identifier102_tree);
                    }
                    pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl1459);
                    voidInterfaceMethodDeclaratorRest103=voidInterfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, voidInterfaceMethodDeclaratorRest103.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:462:9: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1469);
                    interfaceDeclaration104=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration104.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:463:9: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl1479);
                    classDeclaration105=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration105.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 32, interfaceMemberDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMemberDecl"

    public static class interfaceMethodOrFieldDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMethodOrFieldDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:466:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final JavaParser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl() throws RecognitionException {
        JavaParser.interfaceMethodOrFieldDecl_return retval = new JavaParser.interfaceMethodOrFieldDecl_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier107=null;
        JavaParser.type_return type106 = null;

        JavaParser.interfaceMethodOrFieldRest_return interfaceMethodOrFieldRest108 = null;


        Object Identifier107_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:5: ( type Identifier interfaceMethodOrFieldRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:9: type Identifier interfaceMethodOrFieldRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl1502);
            type106=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type106.getTree());
            Identifier107=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl1504); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier107_tree = (Object)adaptor.create(Identifier107);
            adaptor.addChild(root_0, Identifier107_tree);
            }
            pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl1506);
            interfaceMethodOrFieldRest108=interfaceMethodOrFieldRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodOrFieldRest108.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, interfaceMethodOrFieldDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodOrFieldDecl"

    public static class interfaceMethodOrFieldRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMethodOrFieldRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:470:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
    public final JavaParser.interfaceMethodOrFieldRest_return interfaceMethodOrFieldRest() throws RecognitionException {
        JavaParser.interfaceMethodOrFieldRest_return retval = new JavaParser.interfaceMethodOrFieldRest_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal110=null;
        JavaParser.constantDeclaratorsRest_return constantDeclaratorsRest109 = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest111 = null;


        Object char_literal110_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:5: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==48||LA46_0==51) ) {
                alt46=1;
            }
            else if ( (LA46_0==66) ) {
                alt46=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:9: constantDeclaratorsRest ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest1529);
                    constantDeclaratorsRest109=constantDeclaratorsRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclaratorsRest109.getTree());
                    char_literal110=(Token)match(input,26,FOLLOW_26_in_interfaceMethodOrFieldRest1531); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal110_tree = (Object)adaptor.create(char_literal110);
                    adaptor.addChild(root_0, char_literal110_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:472:9: interfaceMethodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1541);
                    interfaceMethodDeclaratorRest111=interfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest111.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 34, interfaceMethodOrFieldRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodOrFieldRest"

    public static class methodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:475:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final JavaParser.methodDeclaratorRest_return methodDeclaratorRest() throws RecognitionException {
        JavaParser.methodDeclaratorRest_return retval = new JavaParser.methodDeclaratorRest_return();
        retval.start = input.LT(1);
        int methodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal113=null;
        Token char_literal114=null;
        Token string_literal115=null;
        Token char_literal118=null;
        JavaParser.formalParameters_return formalParameters112 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList116 = null;

        JavaParser.methodBody_return methodBody117 = null;


        Object char_literal113_tree=null;
        Object char_literal114_tree=null;
        Object string_literal115_tree=null;
        Object char_literal118_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:476:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:476:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest1564);
            formalParameters112=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters112.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:476:26: ( '[' ']' )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==48) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:476:27: '[' ']'
            	    {
            	    char_literal113=(Token)match(input,48,FOLLOW_48_in_methodDeclaratorRest1567); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal113_tree = (Object)adaptor.create(char_literal113);
            	    adaptor.addChild(root_0, char_literal113_tree);
            	    }
            	    char_literal114=(Token)match(input,49,FOLLOW_49_in_methodDeclaratorRest1569); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal114_tree = (Object)adaptor.create(char_literal114);
            	    adaptor.addChild(root_0, char_literal114_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:9: ( 'throws' qualifiedNameList )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==50) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:10: 'throws' qualifiedNameList
                    {
                    string_literal115=(Token)match(input,50,FOLLOW_50_in_methodDeclaratorRest1582); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal115_tree = (Object)adaptor.create(string_literal115);
                    adaptor.addChild(root_0, string_literal115_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest1584);
                    qualifiedNameList116=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList116.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:9: ( methodBody | ';' )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==44) ) {
                alt49=1;
            }
            else if ( (LA49_0==26) ) {
                alt49=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:478:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest1600);
                    methodBody117=methodBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodBody117.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:479:13: ';'
                    {
                    char_literal118=(Token)match(input,26,FOLLOW_26_in_methodDeclaratorRest1614); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal118_tree = (Object)adaptor.create(char_literal118);
                    adaptor.addChild(root_0, char_literal118_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 35, methodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodDeclaratorRest"

    public static class voidMethodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "voidMethodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:483:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidMethodDeclaratorRest_return retval = new JavaParser.voidMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal120=null;
        Token char_literal123=null;
        JavaParser.formalParameters_return formalParameters119 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList121 = null;

        JavaParser.methodBody_return methodBody122 = null;


        Object string_literal120_tree=null;
        Object char_literal123_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:5: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:9: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest1647);
            formalParameters119=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters119.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:26: ( 'throws' qualifiedNameList )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==50) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:27: 'throws' qualifiedNameList
                    {
                    string_literal120=(Token)match(input,50,FOLLOW_50_in_voidMethodDeclaratorRest1650); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal120_tree = (Object)adaptor.create(string_literal120);
                    adaptor.addChild(root_0, string_literal120_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1652);
                    qualifiedNameList121=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList121.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:485:9: ( methodBody | ';' )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==44) ) {
                alt51=1;
            }
            else if ( (LA51_0==26) ) {
                alt51=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:485:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest1668);
                    methodBody122=methodBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodBody122.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:486:13: ';'
                    {
                    char_literal123=(Token)match(input,26,FOLLOW_26_in_voidMethodDeclaratorRest1682); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal123_tree = (Object)adaptor.create(char_literal123);
                    adaptor.addChild(root_0, char_literal123_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 36, voidMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "voidMethodDeclaratorRest"

    public static class interfaceMethodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceMethodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:490:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.interfaceMethodDeclaratorRest_return retval = new JavaParser.interfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal125=null;
        Token char_literal126=null;
        Token string_literal127=null;
        Token char_literal129=null;
        JavaParser.formalParameters_return formalParameters124 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList128 = null;


        Object char_literal125_tree=null;
        Object char_literal126_tree=null;
        Object string_literal127_tree=null;
        Object char_literal129_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1715);
            formalParameters124=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters124.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:26: ( '[' ']' )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==48) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:27: '[' ']'
            	    {
            	    char_literal125=(Token)match(input,48,FOLLOW_48_in_interfaceMethodDeclaratorRest1718); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal125_tree = (Object)adaptor.create(char_literal125);
            	    adaptor.addChild(root_0, char_literal125_tree);
            	    }
            	    char_literal126=(Token)match(input,49,FOLLOW_49_in_interfaceMethodDeclaratorRest1720); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal126_tree = (Object)adaptor.create(char_literal126);
            	    adaptor.addChild(root_0, char_literal126_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:37: ( 'throws' qualifiedNameList )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==50) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:38: 'throws' qualifiedNameList
                    {
                    string_literal127=(Token)match(input,50,FOLLOW_50_in_interfaceMethodDeclaratorRest1725); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal127_tree = (Object)adaptor.create(string_literal127);
                    adaptor.addChild(root_0, string_literal127_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1727);
                    qualifiedNameList128=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList128.getTree());

                    }
                    break;

            }

            char_literal129=(Token)match(input,26,FOLLOW_26_in_interfaceMethodDeclaratorRest1731); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal129_tree = (Object)adaptor.create(char_literal129);
            adaptor.addChild(root_0, char_literal129_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 37, interfaceMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodDeclaratorRest"

    public static class interfaceGenericMethodDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interfaceGenericMethodDecl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:494:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final JavaParser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl() throws RecognitionException {
        JavaParser.interfaceGenericMethodDecl_return retval = new JavaParser.interfaceGenericMethodDecl_return();
        retval.start = input.LT(1);
        int interfaceGenericMethodDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal132=null;
        Token Identifier133=null;
        JavaParser.typeParameters_return typeParameters130 = null;

        JavaParser.type_return type131 = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest134 = null;


        Object string_literal132_tree=null;
        Object Identifier133_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:9: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl1754);
            typeParameters130=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters130.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:24: ( type | 'void' )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==Identifier||(LA54_0>=56 && LA54_0<=63)) ) {
                alt54=1;
            }
            else if ( (LA54_0==47) ) {
                alt54=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:25: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl1757);
                    type131=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type131.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:32: 'void'
                    {
                    string_literal132=(Token)match(input,47,FOLLOW_47_in_interfaceGenericMethodDecl1761); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal132_tree = (Object)adaptor.create(string_literal132);
                    adaptor.addChild(root_0, string_literal132_tree);
                    }

                    }
                    break;

            }

            Identifier133=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl1764); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier133_tree = (Object)adaptor.create(Identifier133);
            adaptor.addChild(root_0, Identifier133_tree);
            }
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1774);
            interfaceMethodDeclaratorRest134=interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest134.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, interfaceGenericMethodDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceGenericMethodDecl"

    public static class voidInterfaceMethodDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "voidInterfaceMethodDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:499:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidInterfaceMethodDeclaratorRest_return retval = new JavaParser.voidInterfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal136=null;
        Token char_literal138=null;
        JavaParser.formalParameters_return formalParameters135 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList137 = null;


        Object string_literal136_tree=null;
        Object char_literal138_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:5: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:9: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1797);
            formalParameters135=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters135.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:26: ( 'throws' qualifiedNameList )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==50) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:27: 'throws' qualifiedNameList
                    {
                    string_literal136=(Token)match(input,50,FOLLOW_50_in_voidInterfaceMethodDeclaratorRest1800); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal136_tree = (Object)adaptor.create(string_literal136);
                    adaptor.addChild(root_0, string_literal136_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1802);
                    qualifiedNameList137=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList137.getTree());

                    }
                    break;

            }

            char_literal138=(Token)match(input,26,FOLLOW_26_in_voidInterfaceMethodDeclaratorRest1806); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal138_tree = (Object)adaptor.create(char_literal138);
            adaptor.addChild(root_0, char_literal138_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 39, voidInterfaceMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "voidInterfaceMethodDeclaratorRest"

    public static class constructorDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructorDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:503:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? constructorBody ;
    public final JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest() throws RecognitionException {
        JavaParser.constructorDeclaratorRest_return retval = new JavaParser.constructorDeclaratorRest_return();
        retval.start = input.LT(1);
        int constructorDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal140=null;
        JavaParser.formalParameters_return formalParameters139 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList141 = null;

        JavaParser.constructorBody_return constructorBody142 = null;


        Object string_literal140_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:5: ( formalParameters ( 'throws' qualifiedNameList )? constructorBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:9: formalParameters ( 'throws' qualifiedNameList )? constructorBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1829);
            formalParameters139=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters139.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:26: ( 'throws' qualifiedNameList )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==50) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:27: 'throws' qualifiedNameList
                    {
                    string_literal140=(Token)match(input,50,FOLLOW_50_in_constructorDeclaratorRest1832); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal140_tree = (Object)adaptor.create(string_literal140);
                    adaptor.addChild(root_0, string_literal140_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1834);
                    qualifiedNameList141=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList141.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_constructorBody_in_constructorDeclaratorRest1838);
            constructorBody142=constructorBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorBody142.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 40, constructorDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constructorDeclaratorRest"

    public static class constantDeclarator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constantDeclarator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:507:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final JavaParser.constantDeclarator_return constantDeclarator() throws RecognitionException {
        JavaParser.constantDeclarator_return retval = new JavaParser.constantDeclarator_return();
        retval.start = input.LT(1);
        int constantDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier143=null;
        JavaParser.constantDeclaratorRest_return constantDeclaratorRest144 = null;


        Object Identifier143_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:5: ( Identifier constantDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:9: Identifier constantDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            Identifier143=(Token)match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator1857); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier143_tree = (Object)adaptor.create(Identifier143);
            adaptor.addChild(root_0, Identifier143_tree);
            }
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator1859);
            constantDeclaratorRest144=constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclaratorRest144.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 41, constantDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclarator"

    public static class variableDeclarators_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableDeclarators"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:511:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
    public final JavaParser.variableDeclarators_return variableDeclarators() throws RecognitionException {
        JavaParser.variableDeclarators_return retval = new JavaParser.variableDeclarators_return();
        retval.start = input.LT(1);
        int variableDeclarators_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal146=null;
        JavaParser.variableDeclarator_return variableDeclarator145 = null;

        JavaParser.variableDeclarator_return variableDeclarator147 = null;


        Object char_literal146_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:512:5: ( variableDeclarator ( ',' variableDeclarator )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:512:9: variableDeclarator ( ',' variableDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1882);
            variableDeclarator145=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator145.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:512:28: ( ',' variableDeclarator )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==41) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:512:29: ',' variableDeclarator
            	    {
            	    char_literal146=(Token)match(input,41,FOLLOW_41_in_variableDeclarators1885); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal146_tree = (Object)adaptor.create(char_literal146);
            	    adaptor.addChild(root_0, char_literal146_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1887);
            	    variableDeclarator147=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator147.getTree());

            	    }
            	    break;

            	default :
            	    break loop57;
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
            if ( state.backtracking>0 ) { memoize(input, 42, variableDeclarators_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclarators"

    public static class variableDeclarator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableDeclarator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:515:1: variableDeclarator : variableDeclaratorId ( '=' variableInitializer )? ;
    public final JavaParser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        JavaParser.variableDeclarator_return retval = new JavaParser.variableDeclarator_return();
        retval.start = input.LT(1);
        int variableDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal149=null;
        JavaParser.variableDeclaratorId_return variableDeclaratorId148 = null;

        JavaParser.variableInitializer_return variableInitializer150 = null;


        Object char_literal149_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:516:5: ( variableDeclaratorId ( '=' variableInitializer )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:516:9: variableDeclaratorId ( '=' variableInitializer )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclaratorId_in_variableDeclarator1908);
            variableDeclaratorId148=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId148.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:516:30: ( '=' variableInitializer )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==51) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:516:31: '=' variableInitializer
                    {
                    char_literal149=(Token)match(input,51,FOLLOW_51_in_variableDeclarator1911); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal149_tree = (Object)adaptor.create(char_literal149);
                    adaptor.addChild(root_0, char_literal149_tree);
                    }
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator1913);
                    variableInitializer150=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer150.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 43, variableDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclarator"

    public static class constantDeclaratorsRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constantDeclaratorsRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:519:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
    public final JavaParser.constantDeclaratorsRest_return constantDeclaratorsRest() throws RecognitionException {
        JavaParser.constantDeclaratorsRest_return retval = new JavaParser.constantDeclaratorsRest_return();
        retval.start = input.LT(1);
        int constantDeclaratorsRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal152=null;
        JavaParser.constantDeclaratorRest_return constantDeclaratorRest151 = null;

        JavaParser.constantDeclarator_return constantDeclarator153 = null;


        Object char_literal152_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:5: ( constantDeclaratorRest ( ',' constantDeclarator )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:9: constantDeclaratorRest ( ',' constantDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1938);
            constantDeclaratorRest151=constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclaratorRest151.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:32: ( ',' constantDeclarator )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:520:33: ',' constantDeclarator
            	    {
            	    char_literal152=(Token)match(input,41,FOLLOW_41_in_constantDeclaratorsRest1941); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal152_tree = (Object)adaptor.create(char_literal152);
            	    adaptor.addChild(root_0, char_literal152_tree);
            	    }
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1943);
            	    constantDeclarator153=constantDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclarator153.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 44, constantDeclaratorsRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclaratorsRest"

    public static class constantDeclaratorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constantDeclaratorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:523:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
    public final JavaParser.constantDeclaratorRest_return constantDeclaratorRest() throws RecognitionException {
        JavaParser.constantDeclaratorRest_return retval = new JavaParser.constantDeclaratorRest_return();
        retval.start = input.LT(1);
        int constantDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal154=null;
        Token char_literal155=null;
        Token char_literal156=null;
        JavaParser.variableInitializer_return variableInitializer157 = null;


        Object char_literal154_tree=null;
        Object char_literal155_tree=null;
        Object char_literal156_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:524:5: ( ( '[' ']' )* '=' variableInitializer )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:524:9: ( '[' ']' )* '=' variableInitializer
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:524:9: ( '[' ']' )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==48) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:524:10: '[' ']'
            	    {
            	    char_literal154=(Token)match(input,48,FOLLOW_48_in_constantDeclaratorRest1965); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal154_tree = (Object)adaptor.create(char_literal154);
            	    adaptor.addChild(root_0, char_literal154_tree);
            	    }
            	    char_literal155=(Token)match(input,49,FOLLOW_49_in_constantDeclaratorRest1967); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal155_tree = (Object)adaptor.create(char_literal155);
            	    adaptor.addChild(root_0, char_literal155_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop60;
                }
            } while (true);

            char_literal156=(Token)match(input,51,FOLLOW_51_in_constantDeclaratorRest1971); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal156_tree = (Object)adaptor.create(char_literal156);
            adaptor.addChild(root_0, char_literal156_tree);
            }
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1973);
            variableInitializer157=variableInitializer();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer157.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, constantDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclaratorRest"

    public static class variableDeclaratorId_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableDeclaratorId"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
    public final JavaParser.variableDeclaratorId_return variableDeclaratorId() throws RecognitionException {
        JavaParser.variableDeclaratorId_return retval = new JavaParser.variableDeclaratorId_return();
        retval.start = input.LT(1);
        int variableDeclaratorId_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier158=null;
        Token char_literal159=null;
        Token char_literal160=null;

        Object Identifier158_tree=null;
        Object char_literal159_tree=null;
        Object char_literal160_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:5: ( Identifier ( '[' ']' )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:9: Identifier ( '[' ']' )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier158=(Token)match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1996); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier158_tree = (Object)adaptor.create(Identifier158);
            adaptor.addChild(root_0, Identifier158_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:20: ( '[' ']' )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==48) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:21: '[' ']'
            	    {
            	    char_literal159=(Token)match(input,48,FOLLOW_48_in_variableDeclaratorId1999); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal159_tree = (Object)adaptor.create(char_literal159);
            	    adaptor.addChild(root_0, char_literal159_tree);
            	    }
            	    char_literal160=(Token)match(input,49,FOLLOW_49_in_variableDeclaratorId2001); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal160_tree = (Object)adaptor.create(char_literal160);
            	    adaptor.addChild(root_0, char_literal160_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop61;
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
            if ( state.backtracking>0 ) { memoize(input, 46, variableDeclaratorId_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclaratorId"

    public static class variableInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:531:1: variableInitializer : ( arrayInitializer | expression );
    public final JavaParser.variableInitializer_return variableInitializer() throws RecognitionException {
        JavaParser.variableInitializer_return retval = new JavaParser.variableInitializer_return();
        retval.start = input.LT(1);
        int variableInitializer_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arrayInitializer_return arrayInitializer161 = null;

        JavaParser.expression_return expression162 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:532:5: ( arrayInitializer | expression )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:532:9: arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2022);
                    arrayInitializer161=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer161.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:9: expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer2032);
                    expression162=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression162.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 47, variableInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableInitializer"

    public static class arrayInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:536:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final JavaParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        JavaParser.arrayInitializer_return retval = new JavaParser.arrayInitializer_return();
        retval.start = input.LT(1);
        int arrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal163=null;
        Token char_literal165=null;
        Token char_literal167=null;
        Token char_literal168=null;
        JavaParser.variableInitializer_return variableInitializer164 = null;

        JavaParser.variableInitializer_return variableInitializer166 = null;


        Object char_literal163_tree=null;
        Object char_literal165_tree=null;
        Object char_literal167_tree=null;
        Object char_literal168_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:5: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:9: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal163=(Token)match(input,44,FOLLOW_44_in_arrayInitializer2059); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal163_tree = (Object)adaptor.create(char_literal163);
            adaptor.addChild(root_0, char_literal163_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:13: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==Identifier||(LA65_0>=FloatingPointLiteral && LA65_0<=DecimalLiteral)||LA65_0==44||LA65_0==47||(LA65_0>=56 && LA65_0<=63)||(LA65_0>=65 && LA65_0<=66)||(LA65_0>=69 && LA65_0<=72)||(LA65_0>=105 && LA65_0<=106)||(LA65_0>=109 && LA65_0<=113)) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:14: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2062);
                    variableInitializer164=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer164.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:34: ( ',' variableInitializer )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:35: ',' variableInitializer
                    	    {
                    	    char_literal165=(Token)match(input,41,FOLLOW_41_in_arrayInitializer2065); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal165_tree = (Object)adaptor.create(char_literal165);
                    	    adaptor.addChild(root_0, char_literal165_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2067);
                    	    variableInitializer166=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer166.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:61: ( ',' )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==41) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:62: ','
                            {
                            char_literal167=(Token)match(input,41,FOLLOW_41_in_arrayInitializer2072); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal167_tree = (Object)adaptor.create(char_literal167);
                            adaptor.addChild(root_0, char_literal167_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            char_literal168=(Token)match(input,45,FOLLOW_45_in_arrayInitializer2079); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal168_tree = (Object)adaptor.create(char_literal168);
            adaptor.addChild(root_0, char_literal168_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 48, arrayInitializer_StartIndex); }
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:540:1: modifier returns [Modifier element] : ( annotation | mod= classOrInterfaceModifier | 'native' | 'synchronized' | 'transient' | 'volatile' );
    public final JavaParser.modifier_return modifier() throws RecognitionException {
        JavaParser.modifier_return retval = new JavaParser.modifier_return();
        retval.start = input.LT(1);
        int modifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal170=null;
        Token string_literal171=null;
        Token string_literal172=null;
        Token string_literal173=null;
        JavaParser.classOrInterfaceModifier_return mod = null;

        JavaParser.annotation_return annotation169 = null;


        Object string_literal170_tree=null;
        Object string_literal171_tree=null;
        Object string_literal172_tree=null;
        Object string_literal173_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:5: ( annotation | mod= classOrInterfaceModifier | 'native' | 'synchronized' | 'transient' | 'volatile' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_modifier2102);
                    annotation169=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation169.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:9: mod= classOrInterfaceModifier
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceModifier_in_modifier2114);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:543:9: 'native'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal170=(Token)match(input,52,FOLLOW_52_in_modifier2126); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal170_tree = (Object)adaptor.create(string_literal170);
                    adaptor.addChild(root_0, string_literal170_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Native();
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:544:9: 'synchronized'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal171=(Token)match(input,53,FOLLOW_53_in_modifier2138); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal171_tree = (Object)adaptor.create(string_literal171);
                    adaptor.addChild(root_0, string_literal171_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Synchronized();
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:545:9: 'transient'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal172=(Token)match(input,54,FOLLOW_54_in_modifier2150); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal172_tree = (Object)adaptor.create(string_literal172);
                    adaptor.addChild(root_0, string_literal172_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.element = new Transient();
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:9: 'volatile'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal173=(Token)match(input,55,FOLLOW_55_in_modifier2162); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal173_tree = (Object)adaptor.create(string_literal173);
                    adaptor.addChild(root_0, string_literal173_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 49, modifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "modifier"

    public static class packageOrTypeName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "packageOrTypeName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:549:1: packageOrTypeName : qualifiedName ;
    public final JavaParser.packageOrTypeName_return packageOrTypeName() throws RecognitionException {
        JavaParser.packageOrTypeName_return retval = new JavaParser.packageOrTypeName_return();
        retval.start = input.LT(1);
        int packageOrTypeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName174 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:550:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:550:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_packageOrTypeName2183);
            qualifiedName174=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName174.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 50, packageOrTypeName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "packageOrTypeName"

    public static class enumConstantName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumConstantName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:553:1: enumConstantName : Identifier ;
    public final JavaParser.enumConstantName_return enumConstantName() throws RecognitionException {
        JavaParser.enumConstantName_return retval = new JavaParser.enumConstantName_return();
        retval.start = input.LT(1);
        int enumConstantName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier175=null;

        Object Identifier175_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:554:5: ( Identifier )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:554:9: Identifier
            {
            root_0 = (Object)adaptor.nil();

            Identifier175=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstantName2202); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier175_tree = (Object)adaptor.create(Identifier175);
            adaptor.addChild(root_0, Identifier175_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 51, enumConstantName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstantName"

    public static class typeName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:557:1: typeName : qualifiedName ;
    public final JavaParser.typeName_return typeName() throws RecognitionException {
        JavaParser.typeName_return retval = new JavaParser.typeName_return();
        retval.start = input.LT(1);
        int typeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName176 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_typeName2221);
            qualifiedName176=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName176.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 52, typeName_StartIndex); }
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:561:1: type returns [TypeReference element] : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final JavaParser.type_return type() throws RecognitionException {
        JavaParser.type_return retval = new JavaParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal178=null;
        Token char_literal179=null;
        Token char_literal181=null;
        Token char_literal182=null;
        JavaParser.classOrInterfaceType_return classOrInterfaceType177 = null;

        JavaParser.primitiveType_return primitiveType180 = null;


        Object char_literal178_tree=null;
        Object char_literal179_tree=null;
        Object char_literal181_tree=null;
        Object char_literal182_tree=null;

        int dimension=0;
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:2: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:4: classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_type2243);
                    classOrInterfaceType177=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType177.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:25: ( '[' ']' )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==48) ) {
                            alt67=1;
                        }


                        switch (alt67) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:26: '[' ']'
                    	    {
                    	    char_literal178=(Token)match(input,48,FOLLOW_48_in_type2246); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal178_tree = (Object)adaptor.create(char_literal178);
                    	    adaptor.addChild(root_0, char_literal178_tree);
                    	    }
                    	    char_literal179=(Token)match(input,49,FOLLOW_49_in_type2248); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal179_tree = (Object)adaptor.create(char_literal179);
                    	    adaptor.addChild(root_0, char_literal179_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop67;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:564:4: primitiveType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_type2255);
                    primitiveType180=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType180.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:564:18: ( '[' ']' )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( (LA68_0==48) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:564:19: '[' ']'
                    	    {
                    	    char_literal181=(Token)match(input,48,FOLLOW_48_in_type2258); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal181_tree = (Object)adaptor.create(char_literal181);
                    	    adaptor.addChild(root_0, char_literal181_tree);
                    	    }
                    	    char_literal182=(Token)match(input,49,FOLLOW_49_in_type2260); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal182_tree = (Object)adaptor.create(char_literal182);
                    	    adaptor.addChild(root_0, char_literal182_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop68;
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
            if ( state.backtracking>0 ) { memoize(input, 53, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class classOrInterfaceType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceType"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:1: classOrInterfaceType : Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ;
    public final JavaParser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        JavaParser.classOrInterfaceType_return retval = new JavaParser.classOrInterfaceType_return();
        retval.start = input.LT(1);
        int classOrInterfaceType_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier183=null;
        Token char_literal185=null;
        Token Identifier186=null;
        JavaParser.typeArguments_return typeArguments184 = null;

        JavaParser.typeArguments_return typeArguments187 = null;


        Object Identifier183_tree=null;
        Object char_literal185_tree=null;
        Object Identifier186_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier183=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2273); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier183_tree = (Object)adaptor.create(Identifier183);
            adaptor.addChild(root_0, Identifier183_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:15: ( typeArguments )?
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
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2275);
                    typeArguments184=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments184.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:30: ( '.' Identifier ( typeArguments )? )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==29) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:31: '.' Identifier ( typeArguments )?
            	    {
            	    char_literal185=(Token)match(input,29,FOLLOW_29_in_classOrInterfaceType2279); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal185_tree = (Object)adaptor.create(char_literal185);
            	    adaptor.addChild(root_0, char_literal185_tree);
            	    }
            	    Identifier186=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2281); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier186_tree = (Object)adaptor.create(Identifier186);
            	    adaptor.addChild(root_0, Identifier186_tree);
            	    }
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:46: ( typeArguments )?
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
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2283);
            	            typeArguments187=typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments187.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 54, classOrInterfaceType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceType"

    public static class primitiveType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primitiveType"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:571:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final JavaParser.primitiveType_return primitiveType() throws RecognitionException {
        JavaParser.primitiveType_return retval = new JavaParser.primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        Object root_0 = null;

        Token set188=null;

        Object set188_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:572:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set188=(Token)input.LT(1);
            if ( (input.LA(1)>=56 && input.LA(1)<=63) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set188));
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
            if ( state.backtracking>0 ) { memoize(input, 55, primitiveType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primitiveType"

    public static class variableModifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableModifier"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:582:1: variableModifier : ( 'final' | annotation );
    public final JavaParser.variableModifier_return variableModifier() throws RecognitionException {
        JavaParser.variableModifier_return retval = new JavaParser.variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal189=null;
        JavaParser.annotation_return annotation190 = null;


        Object string_literal189_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:583:5: ( 'final' | annotation )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:583:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal189=(Token)match(input,35,FOLLOW_35_in_variableModifier2392); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal189_tree = (Object)adaptor.create(string_literal189);
                    adaptor.addChild(root_0, string_literal189_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:584:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_variableModifier2402);
                    annotation190=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation190.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 56, variableModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifier"

    public static class typeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeArguments"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:587:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final JavaParser.typeArguments_return typeArguments() throws RecognitionException {
        JavaParser.typeArguments_return retval = new JavaParser.typeArguments_return();
        retval.start = input.LT(1);
        int typeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal191=null;
        Token char_literal193=null;
        Token char_literal195=null;
        JavaParser.typeArgument_return typeArgument192 = null;

        JavaParser.typeArgument_return typeArgument194 = null;


        Object char_literal191_tree=null;
        Object char_literal193_tree=null;
        Object char_literal195_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal191=(Token)match(input,40,FOLLOW_40_in_typeArguments2421); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal191_tree = (Object)adaptor.create(char_literal191);
            adaptor.addChild(root_0, char_literal191_tree);
            }
            pushFollow(FOLLOW_typeArgument_in_typeArguments2423);
            typeArgument192=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument192.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:26: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==41) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:588:27: ',' typeArgument
            	    {
            	    char_literal193=(Token)match(input,41,FOLLOW_41_in_typeArguments2426); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal193_tree = (Object)adaptor.create(char_literal193);
            	    adaptor.addChild(root_0, char_literal193_tree);
            	    }
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2428);
            	    typeArgument194=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument194.getTree());

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            char_literal195=(Token)match(input,42,FOLLOW_42_in_typeArguments2432); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal195_tree = (Object)adaptor.create(char_literal195);
            adaptor.addChild(root_0, char_literal195_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 57, typeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeArguments"

    public static class typeArgument_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeArgument"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:591:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final JavaParser.typeArgument_return typeArgument() throws RecognitionException {
        JavaParser.typeArgument_return retval = new JavaParser.typeArgument_return();
        retval.start = input.LT(1);
        int typeArgument_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal197=null;
        Token set198=null;
        JavaParser.type_return type196 = null;

        JavaParser.type_return type199 = null;


        Object char_literal197_tree=null;
        Object set198_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:592:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:592:9: type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_typeArgument2455);
                    type196=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type196.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal197=(Token)match(input,64,FOLLOW_64_in_typeArgument2465); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal197_tree = (Object)adaptor.create(char_literal197);
                    adaptor.addChild(root_0, char_literal197_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:13: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==38||LA75_0==65) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:593:14: ( 'extends' | 'super' ) type
                            {
                            set198=(Token)input.LT(1);
                            if ( input.LA(1)==38||input.LA(1)==65 ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set198));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument2476);
                            type199=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type199.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 58, typeArgument_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeArgument"

    public static class qualifiedNameList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedNameList"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:596:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final JavaParser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        JavaParser.qualifiedNameList_return retval = new JavaParser.qualifiedNameList_return();
        retval.start = input.LT(1);
        int qualifiedNameList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal201=null;
        JavaParser.qualifiedName_return qualifiedName200 = null;

        JavaParser.qualifiedName_return qualifiedName202 = null;


        Object char_literal201_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:5: ( qualifiedName ( ',' qualifiedName )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:9: qualifiedName ( ',' qualifiedName )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2501);
            qualifiedName200=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName200.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:23: ( ',' qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==41) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:597:24: ',' qualifiedName
            	    {
            	    char_literal201=(Token)match(input,41,FOLLOW_41_in_qualifiedNameList2504); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal201_tree = (Object)adaptor.create(char_literal201);
            	    adaptor.addChild(root_0, char_literal201_tree);
            	    }
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2506);
            	    qualifiedName202=qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName202.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 59, qualifiedNameList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedNameList"

    public static class formalParameters_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameters"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:600:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final JavaParser.formalParameters_return formalParameters() throws RecognitionException {
        JavaParser.formalParameters_return retval = new JavaParser.formalParameters_return();
        retval.start = input.LT(1);
        int formalParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal203=null;
        Token char_literal205=null;
        JavaParser.formalParameterDecls_return formalParameterDecls204 = null;


        Object char_literal203_tree=null;
        Object char_literal205_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:601:5: ( '(' ( formalParameterDecls )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:601:9: '(' ( formalParameterDecls )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal203=(Token)match(input,66,FOLLOW_66_in_formalParameters2527); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal203_tree = (Object)adaptor.create(char_literal203);
            adaptor.addChild(root_0, char_literal203_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:601:13: ( formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==35||(LA78_0>=56 && LA78_0<=63)||LA78_0==73) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters2529);
                    formalParameterDecls204=formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls204.getTree());

                    }
                    break;

            }

            char_literal205=(Token)match(input,67,FOLLOW_67_in_formalParameters2532); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal205_tree = (Object)adaptor.create(char_literal205);
            adaptor.addChild(root_0, char_literal205_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 60, formalParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameters"

    public static class formalParameterDecls_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameterDecls"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:604:1: formalParameterDecls : variableModifiers type formalParameterDeclsRest ;
    public final JavaParser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        JavaParser.formalParameterDecls_return retval = new JavaParser.formalParameterDecls_return();
        retval.start = input.LT(1);
        int formalParameterDecls_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers206 = null;

        JavaParser.type_return type207 = null;

        JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest208 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:605:5: ( variableModifiers type formalParameterDeclsRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:605:9: variableModifiers type formalParameterDeclsRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameterDecls2555);
            variableModifiers206=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers206.getTree());
            pushFollow(FOLLOW_type_in_formalParameterDecls2557);
            type207=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type207.getTree());
            pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2559);
            formalParameterDeclsRest208=formalParameterDeclsRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDeclsRest208.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 61, formalParameterDecls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameterDecls"

    public static class formalParameterDeclsRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameterDeclsRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:608:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest() throws RecognitionException {
        JavaParser.formalParameterDeclsRest_return retval = new JavaParser.formalParameterDeclsRest_return();
        retval.start = input.LT(1);
        int formalParameterDeclsRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal210=null;
        Token string_literal212=null;
        JavaParser.variableDeclaratorId_return variableDeclaratorId209 = null;

        JavaParser.formalParameterDecls_return formalParameterDecls211 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId213 = null;


        Object char_literal210_tree=null;
        Object string_literal212_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:609:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:609:9: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2582);
                    variableDeclaratorId209=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId209.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:609:30: ( ',' formalParameterDecls )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==41) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:609:31: ',' formalParameterDecls
                            {
                            char_literal210=(Token)match(input,41,FOLLOW_41_in_formalParameterDeclsRest2585); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal210_tree = (Object)adaptor.create(char_literal210);
                            adaptor.addChild(root_0, char_literal210_tree);
                            }
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2587);
                            formalParameterDecls211=formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls211.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:610:9: '...' variableDeclaratorId
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal212=(Token)match(input,68,FOLLOW_68_in_formalParameterDeclsRest2599); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal212_tree = (Object)adaptor.create(string_literal212);
                    adaptor.addChild(root_0, string_literal212_tree);
                    }
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2601);
                    variableDeclaratorId213=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId213.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 62, formalParameterDeclsRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameterDeclsRest"

    public static class methodBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:613:1: methodBody : block ;
    public final JavaParser.methodBody_return methodBody() throws RecognitionException {
        JavaParser.methodBody_return retval = new JavaParser.methodBody_return();
        retval.start = input.LT(1);
        int methodBody_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.block_return block214 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:614:5: ( block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:614:9: block
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_block_in_methodBody2624);
            block214=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block214.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 63, methodBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodBody"

    public static class constructorBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructorBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:617:1: constructorBody : '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' ;
    public final JavaParser.constructorBody_return constructorBody() throws RecognitionException {
        JavaParser.constructorBody_return retval = new JavaParser.constructorBody_return();
        retval.start = input.LT(1);
        int constructorBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal215=null;
        Token char_literal218=null;
        JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation216 = null;

        JavaParser.blockStatement_return blockStatement217 = null;


        Object char_literal215_tree=null;
        Object char_literal218_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:5: ( '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:9: '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal215=(Token)match(input,44,FOLLOW_44_in_constructorBody2643); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal215_tree = (Object)adaptor.create(char_literal215);
            adaptor.addChild(root_0, char_literal215_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:13: ( explicitConstructorInvocation )?
            int alt81=2;
            alt81 = dfa81.predict(input);
            switch (alt81) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: explicitConstructorInvocation
                    {
                    pushFollow(FOLLOW_explicitConstructorInvocation_in_constructorBody2645);
                    explicitConstructorInvocation216=explicitConstructorInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitConstructorInvocation216.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:44: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_constructorBody2648);
            	    blockStatement217=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement217.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            char_literal218=(Token)match(input,45,FOLLOW_45_in_constructorBody2651); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal218_tree = (Object)adaptor.create(char_literal218);
            adaptor.addChild(root_0, char_literal218_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 64, constructorBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constructorBody"

    public static class explicitConstructorInvocation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitConstructorInvocation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:621:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        JavaParser.explicitConstructorInvocation_return retval = new JavaParser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);
        int explicitConstructorInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token set220=null;
        Token char_literal222=null;
        Token char_literal224=null;
        Token string_literal226=null;
        Token char_literal228=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments219 = null;

        JavaParser.arguments_return arguments221 = null;

        JavaParser.primary_return primary223 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments225 = null;

        JavaParser.arguments_return arguments227 = null;


        Object set220_tree=null;
        Object char_literal222_tree=null;
        Object char_literal224_tree=null;
        Object string_literal226_tree=null;
        Object char_literal228_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt85=2;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: ( nonWildcardTypeArguments )?
                    int alt83=2;
                    int LA83_0 = input.LA(1);

                    if ( (LA83_0==40) ) {
                        alt83=1;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2670);
                            nonWildcardTypeArguments219=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments219.getTree());

                            }
                            break;

                    }

                    set220=(Token)input.LT(1);
                    if ( input.LA(1)==65||input.LA(1)==69 ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set220));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation2681);
                    arguments221=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments221.getTree());
                    char_literal222=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation2683); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal222_tree = (Object)adaptor.create(char_literal222);
                    adaptor.addChild(root_0, char_literal222_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:623:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation2693);
                    primary223=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary223.getTree());
                    char_literal224=(Token)match(input,29,FOLLOW_29_in_explicitConstructorInvocation2695); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal224_tree = (Object)adaptor.create(char_literal224);
                    adaptor.addChild(root_0, char_literal224_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:623:21: ( nonWildcardTypeArguments )?
                    int alt84=2;
                    int LA84_0 = input.LA(1);

                    if ( (LA84_0==40) ) {
                        alt84=1;
                    }
                    switch (alt84) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2697);
                            nonWildcardTypeArguments225=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments225.getTree());

                            }
                            break;

                    }

                    string_literal226=(Token)match(input,65,FOLLOW_65_in_explicitConstructorInvocation2700); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal226_tree = (Object)adaptor.create(string_literal226);
                    adaptor.addChild(root_0, string_literal226_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation2702);
                    arguments227=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments227.getTree());
                    char_literal228=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation2704); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal228_tree = (Object)adaptor.create(char_literal228);
                    adaptor.addChild(root_0, char_literal228_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 65, explicitConstructorInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "explicitConstructorInvocation"

    public static class qualifiedName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:627:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final JavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
        JavaParser.qualifiedName_return retval = new JavaParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier229=null;
        Token char_literal230=null;
        Token Identifier231=null;

        Object Identifier229_tree=null;
        Object char_literal230_tree=null;
        Object Identifier231_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:628:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:628:9: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier229=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2724); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier229_tree = (Object)adaptor.create(Identifier229);
            adaptor.addChild(root_0, Identifier229_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:628:20: ( '.' Identifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:628:21: '.' Identifier
            	    {
            	    char_literal230=(Token)match(input,29,FOLLOW_29_in_qualifiedName2727); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal230_tree = (Object)adaptor.create(char_literal230);
            	    adaptor.addChild(root_0, char_literal230_tree);
            	    }
            	    Identifier231=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2729); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier231_tree = (Object)adaptor.create(Identifier231);
            	    adaptor.addChild(root_0, Identifier231_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 66, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedName"

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:631:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final JavaParser.literal_return literal() throws RecognitionException {
        JavaParser.literal_return retval = new JavaParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token FloatingPointLiteral233=null;
        Token CharacterLiteral234=null;
        Token StringLiteral235=null;
        Token string_literal237=null;
        JavaParser.integerLiteral_return integerLiteral232 = null;

        JavaParser.booleanLiteral_return booleanLiteral236 = null;


        Object FloatingPointLiteral233_tree=null;
        Object CharacterLiteral234_tree=null;
        Object StringLiteral235_tree=null;
        Object string_literal237_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:632:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:632:9: integerLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_integerLiteral_in_literal2755);
                    integerLiteral232=integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, integerLiteral232.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:9: FloatingPointLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    FloatingPointLiteral233=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal2765); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FloatingPointLiteral233_tree = (Object)adaptor.create(FloatingPointLiteral233);
                    adaptor.addChild(root_0, FloatingPointLiteral233_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:634:9: CharacterLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    CharacterLiteral234=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal2775); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CharacterLiteral234_tree = (Object)adaptor.create(CharacterLiteral234);
                    adaptor.addChild(root_0, CharacterLiteral234_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:635:9: StringLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    StringLiteral235=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal2785); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    StringLiteral235_tree = (Object)adaptor.create(StringLiteral235);
                    adaptor.addChild(root_0, StringLiteral235_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:636:9: booleanLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_booleanLiteral_in_literal2795);
                    booleanLiteral236=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, booleanLiteral236.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:637:9: 'null'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal237=(Token)match(input,70,FOLLOW_70_in_literal2805); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal237_tree = (Object)adaptor.create(string_literal237);
                    adaptor.addChild(root_0, string_literal237_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 67, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class integerLiteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "integerLiteral"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:640:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final JavaParser.integerLiteral_return integerLiteral() throws RecognitionException {
        JavaParser.integerLiteral_return retval = new JavaParser.integerLiteral_return();
        retval.start = input.LT(1);
        int integerLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set238=null;

        Object set238_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:641:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set238=(Token)input.LT(1);
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set238));
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
            if ( state.backtracking>0 ) { memoize(input, 68, integerLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "integerLiteral"

    public static class booleanLiteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "booleanLiteral"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:646:1: booleanLiteral : ( 'true' | 'false' );
    public final JavaParser.booleanLiteral_return booleanLiteral() throws RecognitionException {
        JavaParser.booleanLiteral_return retval = new JavaParser.booleanLiteral_return();
        retval.start = input.LT(1);
        int booleanLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set239=null;

        Object set239_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:647:5: ( 'true' | 'false' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set239=(Token)input.LT(1);
            if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set239));
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
            if ( state.backtracking>0 ) { memoize(input, 69, booleanLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "booleanLiteral"

    public static class annotations_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotations"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:653:1: annotations : ( annotation )+ ;
    public final JavaParser.annotations_return annotations() throws RecognitionException {
        JavaParser.annotations_return retval = new JavaParser.annotations_return();
        retval.start = input.LT(1);
        int annotations_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotation_return annotation240 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:5: ( ( annotation )+ )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:9: ( annotation )+
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:9: ( annotation )+
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
            	    pushFollow(FOLLOW_annotation_in_annotations2894);
            	    annotation240=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation240.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 70, annotations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotations"

    public static class annotation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:657:1: annotation : '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final JavaParser.annotation_return annotation() throws RecognitionException {
        JavaParser.annotation_return retval = new JavaParser.annotation_return();
        retval.start = input.LT(1);
        int annotation_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal241=null;
        Token char_literal243=null;
        Token char_literal246=null;
        JavaParser.annotationName_return annotationName242 = null;

        JavaParser.elementValuePairs_return elementValuePairs244 = null;

        JavaParser.elementValue_return elementValue245 = null;


        Object char_literal241_tree=null;
        Object char_literal243_tree=null;
        Object char_literal246_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:5: ( '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:9: '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (Object)adaptor.nil();

            char_literal241=(Token)match(input,73,FOLLOW_73_in_annotation2914); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal241_tree = (Object)adaptor.create(char_literal241);
            adaptor.addChild(root_0, char_literal241_tree);
            }
            pushFollow(FOLLOW_annotationName_in_annotation2916);
            annotationName242=annotationName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationName242.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:28: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==66) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:30: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal243=(Token)match(input,66,FOLLOW_66_in_annotation2920); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal243_tree = (Object)adaptor.create(char_literal243);
                    adaptor.addChild(root_0, char_literal243_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:34: ( elementValuePairs | elementValue )?
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:36: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation2924);
                            elementValuePairs244=elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePairs244.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:56: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation2928);
                            elementValue245=elementValue();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue245.getTree());

                            }
                            break;

                    }

                    char_literal246=(Token)match(input,67,FOLLOW_67_in_annotation2933); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal246_tree = (Object)adaptor.create(char_literal246);
                    adaptor.addChild(root_0, char_literal246_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 71, annotation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotation"

    public static class annotationName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:661:1: annotationName : Identifier ( '.' Identifier )* ;
    public final JavaParser.annotationName_return annotationName() throws RecognitionException {
        JavaParser.annotationName_return retval = new JavaParser.annotationName_return();
        retval.start = input.LT(1);
        int annotationName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier247=null;
        Token char_literal248=null;
        Token Identifier249=null;

        Object Identifier247_tree=null;
        Object char_literal248_tree=null;
        Object Identifier249_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:7: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier247=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName2957); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier247_tree = (Object)adaptor.create(Identifier247);
            adaptor.addChild(root_0, Identifier247_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:18: ( '.' Identifier )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==29) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:19: '.' Identifier
            	    {
            	    char_literal248=(Token)match(input,29,FOLLOW_29_in_annotationName2960); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal248_tree = (Object)adaptor.create(char_literal248);
            	    adaptor.addChild(root_0, char_literal248_tree);
            	    }
            	    Identifier249=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName2962); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier249_tree = (Object)adaptor.create(Identifier249);
            	    adaptor.addChild(root_0, Identifier249_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 72, annotationName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationName"

    public static class elementValuePairs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValuePairs"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:665:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final JavaParser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        JavaParser.elementValuePairs_return retval = new JavaParser.elementValuePairs_return();
        retval.start = input.LT(1);
        int elementValuePairs_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal251=null;
        JavaParser.elementValuePair_return elementValuePair250 = null;

        JavaParser.elementValuePair_return elementValuePair252 = null;


        Object char_literal251_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:666:5: ( elementValuePair ( ',' elementValuePair )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:666:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2983);
            elementValuePair250=elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair250.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:666:26: ( ',' elementValuePair )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==41) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:666:27: ',' elementValuePair
            	    {
            	    char_literal251=(Token)match(input,41,FOLLOW_41_in_elementValuePairs2986); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal251_tree = (Object)adaptor.create(char_literal251);
            	    adaptor.addChild(root_0, char_literal251_tree);
            	    }
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2988);
            	    elementValuePair252=elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair252.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 73, elementValuePairs_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValuePairs"

    public static class elementValuePair_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValuePair"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:669:1: elementValuePair : Identifier '=' elementValue ;
    public final JavaParser.elementValuePair_return elementValuePair() throws RecognitionException {
        JavaParser.elementValuePair_return retval = new JavaParser.elementValuePair_return();
        retval.start = input.LT(1);
        int elementValuePair_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier253=null;
        Token char_literal254=null;
        JavaParser.elementValue_return elementValue255 = null;


        Object Identifier253_tree=null;
        Object char_literal254_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:670:5: ( Identifier '=' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:670:9: Identifier '=' elementValue
            {
            root_0 = (Object)adaptor.nil();

            Identifier253=(Token)match(input,Identifier,FOLLOW_Identifier_in_elementValuePair3009); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier253_tree = (Object)adaptor.create(Identifier253);
            adaptor.addChild(root_0, Identifier253_tree);
            }
            char_literal254=(Token)match(input,51,FOLLOW_51_in_elementValuePair3011); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal254_tree = (Object)adaptor.create(char_literal254);
            adaptor.addChild(root_0, char_literal254_tree);
            }
            pushFollow(FOLLOW_elementValue_in_elementValuePair3013);
            elementValue255=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue255.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 74, elementValuePair_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValuePair"

    public static class elementValue_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValue"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final JavaParser.elementValue_return elementValue() throws RecognitionException {
        JavaParser.elementValue_return retval = new JavaParser.elementValue_return();
        retval.start = input.LT(1);
        int elementValue_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression256 = null;

        JavaParser.annotation_return annotation257 = null;

        JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer258 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:674:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:674:9: conditionalExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3036);
                    conditionalExpression256=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression256.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:675:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_elementValue3046);
                    annotation257=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation257.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:676:9: elementValueArrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue3056);
                    elementValueArrayInitializer258=elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer258.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 75, elementValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValue"

    public static class elementValueArrayInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementValueArrayInitializer"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:679:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        JavaParser.elementValueArrayInitializer_return retval = new JavaParser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);
        int elementValueArrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal259=null;
        Token char_literal261=null;
        Token char_literal263=null;
        Token char_literal264=null;
        JavaParser.elementValue_return elementValue260 = null;

        JavaParser.elementValue_return elementValue262 = null;


        Object char_literal259_tree=null;
        Object char_literal261_tree=null;
        Object char_literal263_tree=null;
        Object char_literal264_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal259=(Token)match(input,44,FOLLOW_44_in_elementValueArrayInitializer3079); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal259_tree = (Object)adaptor.create(char_literal259);
            adaptor.addChild(root_0, char_literal259_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:13: ( elementValue ( ',' elementValue )* )?
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==Identifier||(LA95_0>=FloatingPointLiteral && LA95_0<=DecimalLiteral)||LA95_0==44||LA95_0==47||(LA95_0>=56 && LA95_0<=63)||(LA95_0>=65 && LA95_0<=66)||(LA95_0>=69 && LA95_0<=73)||(LA95_0>=105 && LA95_0<=106)||(LA95_0>=109 && LA95_0<=113)) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:14: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3082);
                    elementValue260=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue260.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:27: ( ',' elementValue )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:28: ',' elementValue
                    	    {
                    	    char_literal261=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer3085); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal261_tree = (Object)adaptor.create(char_literal261);
                    	    adaptor.addChild(root_0, char_literal261_tree);
                    	    }
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3087);
                    	    elementValue262=elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue262.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop94;
                        }
                    } while (true);


                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:49: ( ',' )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==41) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:680:50: ','
                    {
                    char_literal263=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer3094); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal263_tree = (Object)adaptor.create(char_literal263);
                    adaptor.addChild(root_0, char_literal263_tree);
                    }

                    }
                    break;

            }

            char_literal264=(Token)match(input,45,FOLLOW_45_in_elementValueArrayInitializer3098); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal264_tree = (Object)adaptor.create(char_literal264);
            adaptor.addChild(root_0, char_literal264_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 76, elementValueArrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValueArrayInitializer"

    public static class annotationTypeDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:683:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
    public final JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        JavaParser.annotationTypeDeclaration_return retval = new JavaParser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal265=null;
        Token string_literal266=null;
        Token Identifier267=null;
        JavaParser.annotationTypeBody_return annotationTypeBody268 = null;


        Object char_literal265_tree=null;
        Object string_literal266_tree=null;
        Object Identifier267_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:684:5: ( '@' 'interface' Identifier annotationTypeBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:684:9: '@' 'interface' Identifier annotationTypeBody
            {
            root_0 = (Object)adaptor.nil();

            char_literal265=(Token)match(input,73,FOLLOW_73_in_annotationTypeDeclaration3121); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal265_tree = (Object)adaptor.create(char_literal265);
            adaptor.addChild(root_0, char_literal265_tree);
            }
            string_literal266=(Token)match(input,46,FOLLOW_46_in_annotationTypeDeclaration3123); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal266_tree = (Object)adaptor.create(string_literal266);
            adaptor.addChild(root_0, string_literal266_tree);
            }
            Identifier267=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration3125); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier267_tree = (Object)adaptor.create(Identifier267);
            adaptor.addChild(root_0, Identifier267_tree);
            }
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3127);
            annotationTypeBody268=annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody268.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 77, annotationTypeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeDeclaration"

    public static class annotationTypeBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeBody"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:687:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final JavaParser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        JavaParser.annotationTypeBody_return retval = new JavaParser.annotationTypeBody_return();
        retval.start = input.LT(1);
        int annotationTypeBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal269=null;
        Token char_literal271=null;
        JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration270 = null;


        Object char_literal269_tree=null;
        Object char_literal271_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal269=(Token)match(input,44,FOLLOW_44_in_annotationTypeBody3150); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal269_tree = (Object)adaptor.create(char_literal269);
            adaptor.addChild(root_0, char_literal269_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:13: ( annotationTypeElementDeclaration )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( ((LA97_0>=Identifier && LA97_0<=ENUM)||LA97_0==28||(LA97_0>=31 && LA97_0<=37)||LA97_0==40||(LA97_0>=46 && LA97_0<=47)||(LA97_0>=52 && LA97_0<=63)||LA97_0==73) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:688:14: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3153);
            	    annotationTypeElementDeclaration270=annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration270.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            char_literal271=(Token)match(input,45,FOLLOW_45_in_annotationTypeBody3157); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal271_tree = (Object)adaptor.create(char_literal271);
            adaptor.addChild(root_0, char_literal271_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 78, annotationTypeBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeBody"

    public static class annotationTypeElementDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeElementDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:691:1: annotationTypeElementDeclaration : modifiers annotationTypeElementRest ;
    public final JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        JavaParser.annotationTypeElementDeclaration_return retval = new JavaParser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeElementDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifiers_return modifiers272 = null;

        JavaParser.annotationTypeElementRest_return annotationTypeElementRest273 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:5: ( modifiers annotationTypeElementRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:9: modifiers annotationTypeElementRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationTypeElementDeclaration3180);
            modifiers272=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers272.getTree());
            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3182);
            annotationTypeElementRest273=annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementRest273.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 79, annotationTypeElementDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementDeclaration"

    public static class annotationTypeElementRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationTypeElementRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:695:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final JavaParser.annotationTypeElementRest_return annotationTypeElementRest() throws RecognitionException {
        JavaParser.annotationTypeElementRest_return retval = new JavaParser.annotationTypeElementRest_return();
        retval.start = input.LT(1);
        int annotationTypeElementRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal276=null;
        Token char_literal278=null;
        Token char_literal280=null;
        Token char_literal282=null;
        Token char_literal284=null;
        JavaParser.type_return type274 = null;

        JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest275 = null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration277 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration279 = null;

        JavaParser.enumDeclaration_return enumDeclaration281 = null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration283 = null;


        Object char_literal276_tree=null;
        Object char_literal278_tree=null;
        Object char_literal280_tree=null;
        Object char_literal282_tree=null;
        Object char_literal284_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:696:5: ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:696:9: type annotationMethodOrConstantRest ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_annotationTypeElementRest3205);
                    type274=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type274.getTree());
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3207);
                    annotationMethodOrConstantRest275=annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodOrConstantRest275.getTree());
                    char_literal276=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3209); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal276_tree = (Object)adaptor.create(char_literal276);
                    adaptor.addChild(root_0, char_literal276_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:9: normalClassDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3219);
                    normalClassDeclaration277=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration277.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:32: ( ';' )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==26) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal278=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3221); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal278_tree = (Object)adaptor.create(char_literal278);
                            adaptor.addChild(root_0, char_literal278_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:9: normalInterfaceDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3232);
                    normalInterfaceDeclaration279=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration279.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:36: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==26) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal280=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3234); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal280_tree = (Object)adaptor.create(char_literal280);
                            adaptor.addChild(root_0, char_literal280_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:699:9: enumDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest3245);
                    enumDeclaration281=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration281.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:699:25: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==26) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal282=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3247); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal282_tree = (Object)adaptor.create(char_literal282);
                            adaptor.addChild(root_0, char_literal282_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:700:9: annotationTypeDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3258);
                    annotationTypeDeclaration283=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration283.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:700:35: ( ';' )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==26) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal284=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3260); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal284_tree = (Object)adaptor.create(char_literal284);
                            adaptor.addChild(root_0, char_literal284_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 80, annotationTypeElementRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementRest"

    public static class annotationMethodOrConstantRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationMethodOrConstantRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest() throws RecognitionException {
        JavaParser.annotationMethodOrConstantRest_return retval = new JavaParser.annotationMethodOrConstantRest_return();
        retval.start = input.LT(1);
        int annotationMethodOrConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotationMethodRest_return annotationMethodRest285 = null;

        JavaParser.annotationConstantRest_return annotationConstantRest286 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:704:5: ( annotationMethodRest | annotationConstantRest )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:704:9: annotationMethodRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3284);
                    annotationMethodRest285=annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodRest285.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:705:9: annotationConstantRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3294);
                    annotationConstantRest286=annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationConstantRest286.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 81, annotationMethodOrConstantRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationMethodOrConstantRest"

    public static class annotationMethodRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationMethodRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:708:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final JavaParser.annotationMethodRest_return annotationMethodRest() throws RecognitionException {
        JavaParser.annotationMethodRest_return retval = new JavaParser.annotationMethodRest_return();
        retval.start = input.LT(1);
        int annotationMethodRest_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier287=null;
        Token char_literal288=null;
        Token char_literal289=null;
        JavaParser.defaultValue_return defaultValue290 = null;


        Object Identifier287_tree=null;
        Object char_literal288_tree=null;
        Object char_literal289_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:709:5: ( Identifier '(' ')' ( defaultValue )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:709:9: Identifier '(' ')' ( defaultValue )?
            {
            root_0 = (Object)adaptor.nil();

            Identifier287=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest3317); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier287_tree = (Object)adaptor.create(Identifier287);
            adaptor.addChild(root_0, Identifier287_tree);
            }
            char_literal288=(Token)match(input,66,FOLLOW_66_in_annotationMethodRest3319); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal288_tree = (Object)adaptor.create(char_literal288);
            adaptor.addChild(root_0, char_literal288_tree);
            }
            char_literal289=(Token)match(input,67,FOLLOW_67_in_annotationMethodRest3321); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal289_tree = (Object)adaptor.create(char_literal289);
            adaptor.addChild(root_0, char_literal289_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:709:28: ( defaultValue )?
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==74) ) {
                alt104=1;
            }
            switch (alt104) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest3323);
                    defaultValue290=defaultValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, defaultValue290.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 82, annotationMethodRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationMethodRest"

    public static class annotationConstantRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotationConstantRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:712:1: annotationConstantRest : variableDeclarators ;
    public final JavaParser.annotationConstantRest_return annotationConstantRest() throws RecognitionException {
        JavaParser.annotationConstantRest_return retval = new JavaParser.annotationConstantRest_return();
        retval.start = input.LT(1);
        int annotationConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableDeclarators_return variableDeclarators291 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:5: ( variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:9: variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest3347);
            variableDeclarators291=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators291.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 83, annotationConstantRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationConstantRest"

    public static class defaultValue_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "defaultValue"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:716:1: defaultValue : 'default' elementValue ;
    public final JavaParser.defaultValue_return defaultValue() throws RecognitionException {
        JavaParser.defaultValue_return retval = new JavaParser.defaultValue_return();
        retval.start = input.LT(1);
        int defaultValue_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal292=null;
        JavaParser.elementValue_return elementValue293 = null;


        Object string_literal292_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:717:5: ( 'default' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:717:9: 'default' elementValue
            {
            root_0 = (Object)adaptor.nil();

            string_literal292=(Token)match(input,74,FOLLOW_74_in_defaultValue3370); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal292_tree = (Object)adaptor.create(string_literal292);
            adaptor.addChild(root_0, string_literal292_tree);
            }
            pushFollow(FOLLOW_elementValue_in_defaultValue3372);
            elementValue293=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue293.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 84, defaultValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "defaultValue"

    public static class block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:722:1: block : '{' ( blockStatement )* '}' ;
    public final JavaParser.block_return block() throws RecognitionException {
        JavaParser.block_return retval = new JavaParser.block_return();
        retval.start = input.LT(1);
        int block_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal294=null;
        Token char_literal296=null;
        JavaParser.blockStatement_return blockStatement295 = null;


        Object char_literal294_tree=null;
        Object char_literal296_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:723:5: ( '{' ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:723:9: '{' ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal294=(Token)match(input,44,FOLLOW_44_in_block3393); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal294_tree = (Object)adaptor.create(char_literal294);
            adaptor.addChild(root_0, char_literal294_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:723:13: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_block3395);
            	    blockStatement295=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement295.getTree());

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);

            char_literal296=(Token)match(input,45,FOLLOW_45_in_block3398); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal296_tree = (Object)adaptor.create(char_literal296);
            adaptor.addChild(root_0, char_literal296_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 85, block_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class blockStatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blockStatement"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:726:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final JavaParser.blockStatement_return blockStatement() throws RecognitionException {
        JavaParser.blockStatement_return retval = new JavaParser.blockStatement_return();
        retval.start = input.LT(1);
        int blockStatement_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement297 = null;

        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration298 = null;

        JavaParser.statement_return statement299 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:727:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt106=3;
            alt106 = dfa106.predict(input);
            switch (alt106) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:727:9: localVariableDeclarationStatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement3421);
                    localVariableDeclarationStatement297=localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement297.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:728:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement3431);
                    classOrInterfaceDeclaration298=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration298.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:729:9: statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statement_in_blockStatement3441);
                    statement299=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement299.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 86, blockStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "blockStatement"

    public static class localVariableDeclarationStatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "localVariableDeclarationStatement"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:732:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        JavaParser.localVariableDeclarationStatement_return retval = new JavaParser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);
        int localVariableDeclarationStatement_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal301=null;
        JavaParser.localVariableDeclaration_return localVariableDeclaration300 = null;


        Object char_literal301_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:5: ( localVariableDeclaration ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:10: localVariableDeclaration ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement3465);
            localVariableDeclaration300=localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration300.getTree());
            char_literal301=(Token)match(input,26,FOLLOW_26_in_localVariableDeclarationStatement3467); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal301_tree = (Object)adaptor.create(char_literal301);
            adaptor.addChild(root_0, char_literal301_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 87, localVariableDeclarationStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "localVariableDeclarationStatement"

    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "localVariableDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:736:1: localVariableDeclaration : variableModifiers type variableDeclarators ;
    public final JavaParser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        JavaParser.localVariableDeclaration_return retval = new JavaParser.localVariableDeclaration_return();
        retval.start = input.LT(1);
        int localVariableDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers302 = null;

        JavaParser.type_return type303 = null;

        JavaParser.variableDeclarators_return variableDeclarators304 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:737:5: ( variableModifiers type variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:737:9: variableModifiers type variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration3486);
            variableModifiers302=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers302.getTree());
            pushFollow(FOLLOW_type_in_localVariableDeclaration3488);
            type303=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type303.getTree());
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration3490);
            variableDeclarators304=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators304.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 88, localVariableDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "localVariableDeclaration"

    public static class variableModifiers_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableModifiers"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:740:1: variableModifiers : ( variableModifier )* ;
    public final JavaParser.variableModifiers_return variableModifiers() throws RecognitionException {
        JavaParser.variableModifiers_return retval = new JavaParser.variableModifiers_return();
        retval.start = input.LT(1);
        int variableModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifier_return variableModifier305 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:741:5: ( ( variableModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:741:9: ( variableModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:741:9: ( variableModifier )*
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
            	    pushFollow(FOLLOW_variableModifier_in_variableModifiers3513);
            	    variableModifier305=variableModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifier305.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 89, variableModifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifiers"

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statement"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:744:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );
    public final JavaParser.statement_return statement() throws RecognitionException {
        JavaParser.statement_return retval = new JavaParser.statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        Object root_0 = null;

        Token ASSERT307=null;
        Token char_literal309=null;
        Token char_literal311=null;
        Token string_literal312=null;
        Token string_literal315=null;
        Token string_literal317=null;
        Token char_literal318=null;
        Token char_literal320=null;
        Token string_literal322=null;
        Token string_literal325=null;
        Token string_literal327=null;
        Token char_literal329=null;
        Token string_literal330=null;
        Token string_literal333=null;
        Token string_literal336=null;
        Token string_literal338=null;
        Token char_literal340=null;
        Token char_literal342=null;
        Token string_literal343=null;
        Token string_literal346=null;
        Token char_literal348=null;
        Token string_literal349=null;
        Token char_literal351=null;
        Token string_literal352=null;
        Token Identifier353=null;
        Token char_literal354=null;
        Token string_literal355=null;
        Token Identifier356=null;
        Token char_literal357=null;
        Token char_literal358=null;
        Token char_literal360=null;
        Token Identifier361=null;
        Token char_literal362=null;
        JavaParser.block_return block306 = null;

        JavaParser.expression_return expression308 = null;

        JavaParser.expression_return expression310 = null;

        JavaParser.parExpression_return parExpression313 = null;

        JavaParser.statement_return statement314 = null;

        JavaParser.statement_return statement316 = null;

        JavaParser.forControl_return forControl319 = null;

        JavaParser.statement_return statement321 = null;

        JavaParser.parExpression_return parExpression323 = null;

        JavaParser.statement_return statement324 = null;

        JavaParser.statement_return statement326 = null;

        JavaParser.parExpression_return parExpression328 = null;

        JavaParser.block_return block331 = null;

        JavaParser.catches_return catches332 = null;

        JavaParser.block_return block334 = null;

        JavaParser.catches_return catches335 = null;

        JavaParser.block_return block337 = null;

        JavaParser.parExpression_return parExpression339 = null;

        JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups341 = null;

        JavaParser.parExpression_return parExpression344 = null;

        JavaParser.block_return block345 = null;

        JavaParser.expression_return expression347 = null;

        JavaParser.expression_return expression350 = null;

        JavaParser.statementExpression_return statementExpression359 = null;

        JavaParser.statement_return statement363 = null;


        Object ASSERT307_tree=null;
        Object char_literal309_tree=null;
        Object char_literal311_tree=null;
        Object string_literal312_tree=null;
        Object string_literal315_tree=null;
        Object string_literal317_tree=null;
        Object char_literal318_tree=null;
        Object char_literal320_tree=null;
        Object string_literal322_tree=null;
        Object string_literal325_tree=null;
        Object string_literal327_tree=null;
        Object char_literal329_tree=null;
        Object string_literal330_tree=null;
        Object string_literal333_tree=null;
        Object string_literal336_tree=null;
        Object string_literal338_tree=null;
        Object char_literal340_tree=null;
        Object char_literal342_tree=null;
        Object string_literal343_tree=null;
        Object string_literal346_tree=null;
        Object char_literal348_tree=null;
        Object string_literal349_tree=null;
        Object char_literal351_tree=null;
        Object string_literal352_tree=null;
        Object Identifier353_tree=null;
        Object char_literal354_tree=null;
        Object string_literal355_tree=null;
        Object Identifier356_tree=null;
        Object char_literal357_tree=null;
        Object char_literal358_tree=null;
        Object char_literal360_tree=null;
        Object Identifier361_tree=null;
        Object char_literal362_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:5: ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement )
            int alt114=16;
            alt114 = dfa114.predict(input);
            switch (alt114) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:7: block
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_block_in_statement3531);
                    block306=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block306.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:9: ASSERT expression ( ':' expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    ASSERT307=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement3541); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSERT307_tree = (Object)adaptor.create(ASSERT307);
                    adaptor.addChild(root_0, ASSERT307_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement3543);
                    expression308=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression308.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:27: ( ':' expression )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==75) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:746:28: ':' expression
                            {
                            char_literal309=(Token)match(input,75,FOLLOW_75_in_statement3546); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal309_tree = (Object)adaptor.create(char_literal309);
                            adaptor.addChild(root_0, char_literal309_tree);
                            }
                            pushFollow(FOLLOW_expression_in_statement3548);
                            expression310=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression310.getTree());

                            }
                            break;

                    }

                    char_literal311=(Token)match(input,26,FOLLOW_26_in_statement3552); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal311_tree = (Object)adaptor.create(char_literal311);
                    adaptor.addChild(root_0, char_literal311_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:747:9: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal312=(Token)match(input,76,FOLLOW_76_in_statement3562); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal312_tree = (Object)adaptor.create(string_literal312);
                    adaptor.addChild(root_0, string_literal312_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3564);
                    parExpression313=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression313.getTree());
                    pushFollow(FOLLOW_statement_in_statement3566);
                    statement314=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement314.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:747:38: ( options {k=1; } : 'else' statement )?
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:747:54: 'else' statement
                            {
                            string_literal315=(Token)match(input,77,FOLLOW_77_in_statement3576); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal315_tree = (Object)adaptor.create(string_literal315);
                            adaptor.addChild(root_0, string_literal315_tree);
                            }
                            pushFollow(FOLLOW_statement_in_statement3578);
                            statement316=statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, statement316.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:748:9: 'for' '(' forControl ')' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal317=(Token)match(input,78,FOLLOW_78_in_statement3590); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal317_tree = (Object)adaptor.create(string_literal317);
                    adaptor.addChild(root_0, string_literal317_tree);
                    }
                    char_literal318=(Token)match(input,66,FOLLOW_66_in_statement3592); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal318_tree = (Object)adaptor.create(char_literal318);
                    adaptor.addChild(root_0, char_literal318_tree);
                    }
                    pushFollow(FOLLOW_forControl_in_statement3594);
                    forControl319=forControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, forControl319.getTree());
                    char_literal320=(Token)match(input,67,FOLLOW_67_in_statement3596); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal320_tree = (Object)adaptor.create(char_literal320);
                    adaptor.addChild(root_0, char_literal320_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement3598);
                    statement321=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement321.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:9: 'while' parExpression statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal322=(Token)match(input,79,FOLLOW_79_in_statement3608); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal322_tree = (Object)adaptor.create(string_literal322);
                    adaptor.addChild(root_0, string_literal322_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3610);
                    parExpression323=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression323.getTree());
                    pushFollow(FOLLOW_statement_in_statement3612);
                    statement324=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement324.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal325=(Token)match(input,80,FOLLOW_80_in_statement3622); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal325_tree = (Object)adaptor.create(string_literal325);
                    adaptor.addChild(root_0, string_literal325_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement3624);
                    statement326=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement326.getTree());
                    string_literal327=(Token)match(input,79,FOLLOW_79_in_statement3626); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal327_tree = (Object)adaptor.create(string_literal327);
                    adaptor.addChild(root_0, string_literal327_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3628);
                    parExpression328=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression328.getTree());
                    char_literal329=(Token)match(input,26,FOLLOW_26_in_statement3630); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal329_tree = (Object)adaptor.create(char_literal329);
                    adaptor.addChild(root_0, char_literal329_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:751:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal330=(Token)match(input,81,FOLLOW_81_in_statement3640); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal330_tree = (Object)adaptor.create(string_literal330);
                    adaptor.addChild(root_0, string_literal330_tree);
                    }
                    pushFollow(FOLLOW_block_in_statement3642);
                    block331=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block331.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:752:9: ( catches 'finally' block | catches | 'finally' block )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:752:11: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement3654);
                            catches332=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches332.getTree());
                            string_literal333=(Token)match(input,82,FOLLOW_82_in_statement3656); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal333_tree = (Object)adaptor.create(string_literal333);
                            adaptor.addChild(root_0, string_literal333_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement3658);
                            block334=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block334.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:753:11: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement3670);
                            catches335=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches335.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:13: 'finally' block
                            {
                            string_literal336=(Token)match(input,82,FOLLOW_82_in_statement3684); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal336_tree = (Object)adaptor.create(string_literal336);
                            adaptor.addChild(root_0, string_literal336_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement3686);
                            block337=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block337.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:756:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal338=(Token)match(input,83,FOLLOW_83_in_statement3706); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal338_tree = (Object)adaptor.create(string_literal338);
                    adaptor.addChild(root_0, string_literal338_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3708);
                    parExpression339=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression339.getTree());
                    char_literal340=(Token)match(input,44,FOLLOW_44_in_statement3710); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal340_tree = (Object)adaptor.create(char_literal340);
                    adaptor.addChild(root_0, char_literal340_tree);
                    }
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement3712);
                    switchBlockStatementGroups341=switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups341.getTree());
                    char_literal342=(Token)match(input,45,FOLLOW_45_in_statement3714); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal342_tree = (Object)adaptor.create(char_literal342);
                    adaptor.addChild(root_0, char_literal342_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:757:9: 'synchronized' parExpression block
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal343=(Token)match(input,53,FOLLOW_53_in_statement3724); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal343_tree = (Object)adaptor.create(string_literal343);
                    adaptor.addChild(root_0, string_literal343_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3726);
                    parExpression344=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression344.getTree());
                    pushFollow(FOLLOW_block_in_statement3728);
                    block345=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block345.getTree());

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:9: 'return' ( expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal346=(Token)match(input,84,FOLLOW_84_in_statement3738); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal346_tree = (Object)adaptor.create(string_literal346);
                    adaptor.addChild(root_0, string_literal346_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:18: ( expression )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==Identifier||(LA111_0>=FloatingPointLiteral && LA111_0<=DecimalLiteral)||LA111_0==47||(LA111_0>=56 && LA111_0<=63)||(LA111_0>=65 && LA111_0<=66)||(LA111_0>=69 && LA111_0<=72)||(LA111_0>=105 && LA111_0<=106)||(LA111_0>=109 && LA111_0<=113)) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement3740);
                            expression347=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression347.getTree());

                            }
                            break;

                    }

                    char_literal348=(Token)match(input,26,FOLLOW_26_in_statement3743); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal348_tree = (Object)adaptor.create(char_literal348);
                    adaptor.addChild(root_0, char_literal348_tree);
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:759:9: 'throw' expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal349=(Token)match(input,85,FOLLOW_85_in_statement3753); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal349_tree = (Object)adaptor.create(string_literal349);
                    adaptor.addChild(root_0, string_literal349_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement3755);
                    expression350=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression350.getTree());
                    char_literal351=(Token)match(input,26,FOLLOW_26_in_statement3757); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal351_tree = (Object)adaptor.create(char_literal351);
                    adaptor.addChild(root_0, char_literal351_tree);
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:760:9: 'break' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal352=(Token)match(input,86,FOLLOW_86_in_statement3767); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal352_tree = (Object)adaptor.create(string_literal352);
                    adaptor.addChild(root_0, string_literal352_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:760:17: ( Identifier )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier353=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement3769); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier353_tree = (Object)adaptor.create(Identifier353);
                            adaptor.addChild(root_0, Identifier353_tree);
                            }

                            }
                            break;

                    }

                    char_literal354=(Token)match(input,26,FOLLOW_26_in_statement3772); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal354_tree = (Object)adaptor.create(char_literal354);
                    adaptor.addChild(root_0, char_literal354_tree);
                    }

                    }
                    break;
                case 13 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:761:9: 'continue' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal355=(Token)match(input,87,FOLLOW_87_in_statement3782); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal355_tree = (Object)adaptor.create(string_literal355);
                    adaptor.addChild(root_0, string_literal355_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:761:20: ( Identifier )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==Identifier) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier356=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement3784); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier356_tree = (Object)adaptor.create(Identifier356);
                            adaptor.addChild(root_0, Identifier356_tree);
                            }

                            }
                            break;

                    }

                    char_literal357=(Token)match(input,26,FOLLOW_26_in_statement3787); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal357_tree = (Object)adaptor.create(char_literal357);
                    adaptor.addChild(root_0, char_literal357_tree);
                    }

                    }
                    break;
                case 14 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:762:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal358=(Token)match(input,26,FOLLOW_26_in_statement3797); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal358_tree = (Object)adaptor.create(char_literal358);
                    adaptor.addChild(root_0, char_literal358_tree);
                    }

                    }
                    break;
                case 15 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:763:9: statementExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statementExpression_in_statement3808);
                    statementExpression359=statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementExpression359.getTree());
                    char_literal360=(Token)match(input,26,FOLLOW_26_in_statement3810); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal360_tree = (Object)adaptor.create(char_literal360);
                    adaptor.addChild(root_0, char_literal360_tree);
                    }

                    }
                    break;
                case 16 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:764:9: Identifier ':' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier361=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement3820); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier361_tree = (Object)adaptor.create(Identifier361);
                    adaptor.addChild(root_0, Identifier361_tree);
                    }
                    char_literal362=(Token)match(input,75,FOLLOW_75_in_statement3822); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal362_tree = (Object)adaptor.create(char_literal362);
                    adaptor.addChild(root_0, char_literal362_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement3824);
                    statement363=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement363.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 90, statement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statement"

    public static class catches_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "catches"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:767:1: catches : catchClause ( catchClause )* ;
    public final JavaParser.catches_return catches() throws RecognitionException {
        JavaParser.catches_return retval = new JavaParser.catches_return();
        retval.start = input.LT(1);
        int catches_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.catchClause_return catchClause364 = null;

        JavaParser.catchClause_return catchClause365 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:5: ( catchClause ( catchClause )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:9: catchClause ( catchClause )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_catchClause_in_catches3847);
            catchClause364=catchClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause364.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:21: ( catchClause )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==88) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:22: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches3850);
            	    catchClause365=catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause365.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 91, catches_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "catches"

    public static class catchClause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "catchClause"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:771:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final JavaParser.catchClause_return catchClause() throws RecognitionException {
        JavaParser.catchClause_return retval = new JavaParser.catchClause_return();
        retval.start = input.LT(1);
        int catchClause_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal366=null;
        Token char_literal367=null;
        Token char_literal369=null;
        JavaParser.formalParameter_return formalParameter368 = null;

        JavaParser.block_return block370 = null;


        Object string_literal366_tree=null;
        Object char_literal367_tree=null;
        Object char_literal369_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:5: ( 'catch' '(' formalParameter ')' block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (Object)adaptor.nil();

            string_literal366=(Token)match(input,88,FOLLOW_88_in_catchClause3875); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal366_tree = (Object)adaptor.create(string_literal366);
            adaptor.addChild(root_0, string_literal366_tree);
            }
            char_literal367=(Token)match(input,66,FOLLOW_66_in_catchClause3877); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal367_tree = (Object)adaptor.create(char_literal367);
            adaptor.addChild(root_0, char_literal367_tree);
            }
            pushFollow(FOLLOW_formalParameter_in_catchClause3879);
            formalParameter368=formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameter368.getTree());
            char_literal369=(Token)match(input,67,FOLLOW_67_in_catchClause3881); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal369_tree = (Object)adaptor.create(char_literal369);
            adaptor.addChild(root_0, char_literal369_tree);
            }
            pushFollow(FOLLOW_block_in_catchClause3883);
            block370=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block370.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 92, catchClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "catchClause"

    public static class formalParameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "formalParameter"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:775:1: formalParameter : variableModifiers type variableDeclaratorId ;
    public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
        JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers371 = null;

        JavaParser.type_return type372 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId373 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:776:5: ( variableModifiers type variableDeclaratorId )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:776:9: variableModifiers type variableDeclaratorId
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameter3902);
            variableModifiers371=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers371.getTree());
            pushFollow(FOLLOW_type_in_formalParameter3904);
            type372=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type372.getTree());
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter3906);
            variableDeclaratorId373=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId373.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 93, formalParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameter"

    public static class switchBlockStatementGroups_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "switchBlockStatementGroups"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:779:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        JavaParser.switchBlockStatementGroups_return retval = new JavaParser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroups_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup374 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:5: ( ( switchBlockStatementGroup )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:9: ( switchBlockStatementGroup )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:9: ( switchBlockStatementGroup )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==74||LA116_0==89) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups3934);
            	    switchBlockStatementGroup374=switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup374.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 94, switchBlockStatementGroups_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroups"

    public static class switchBlockStatementGroup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "switchBlockStatementGroup"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:787:1: switchBlockStatementGroup : ( switchLabel )+ ( blockStatement )* ;
    public final JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        JavaParser.switchBlockStatementGroup_return retval = new JavaParser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroup_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchLabel_return switchLabel375 = null;

        JavaParser.blockStatement_return blockStatement376 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:5: ( ( switchLabel )+ ( blockStatement )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: ( switchLabel )+ ( blockStatement )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: ( switchLabel )+
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
            	    pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup3961);
            	    switchLabel375=switchLabel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchLabel375.getTree());

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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:22: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup3964);
            	    blockStatement376=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement376.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 95, switchBlockStatementGroup_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroup"

    public static class switchLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "switchLabel"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:791:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final JavaParser.switchLabel_return switchLabel() throws RecognitionException {
        JavaParser.switchLabel_return retval = new JavaParser.switchLabel_return();
        retval.start = input.LT(1);
        int switchLabel_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal377=null;
        Token char_literal379=null;
        Token string_literal380=null;
        Token char_literal382=null;
        Token string_literal383=null;
        Token char_literal384=null;
        JavaParser.constantExpression_return constantExpression378 = null;

        JavaParser.enumConstantName_return enumConstantName381 = null;


        Object string_literal377_tree=null;
        Object char_literal379_tree=null;
        Object string_literal380_tree=null;
        Object char_literal382_tree=null;
        Object string_literal383_tree=null;
        Object char_literal384_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
            int alt119=3;
            int LA119_0 = input.LA(1);

            if ( (LA119_0==89) ) {
                int LA119_1 = input.LA(2);

                if ( ((LA119_1>=FloatingPointLiteral && LA119_1<=DecimalLiteral)||LA119_1==47||(LA119_1>=56 && LA119_1<=63)||(LA119_1>=65 && LA119_1<=66)||(LA119_1>=69 && LA119_1<=72)||(LA119_1>=105 && LA119_1<=106)||(LA119_1>=109 && LA119_1<=113)) ) {
                    alt119=1;
                }
                else if ( (LA119_1==Identifier) ) {
                    int LA119_4 = input.LA(3);

                    if ( (LA119_4==75) ) {
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
                    else if ( ((LA119_4>=29 && LA119_4<=30)||LA119_4==40||(LA119_4>=42 && LA119_4<=43)||LA119_4==48||LA119_4==51||LA119_4==64||LA119_4==66||(LA119_4>=90 && LA119_4<=110)) ) {
                        alt119=1;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:9: 'case' constantExpression ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal377=(Token)match(input,89,FOLLOW_89_in_switchLabel3988); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal377_tree = (Object)adaptor.create(string_literal377);
                    adaptor.addChild(root_0, string_literal377_tree);
                    }
                    pushFollow(FOLLOW_constantExpression_in_switchLabel3990);
                    constantExpression378=constantExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantExpression378.getTree());
                    char_literal379=(Token)match(input,75,FOLLOW_75_in_switchLabel3992); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal379_tree = (Object)adaptor.create(char_literal379);
                    adaptor.addChild(root_0, char_literal379_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:793:9: 'case' enumConstantName ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal380=(Token)match(input,89,FOLLOW_89_in_switchLabel4002); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal380_tree = (Object)adaptor.create(string_literal380);
                    adaptor.addChild(root_0, string_literal380_tree);
                    }
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel4004);
                    enumConstantName381=enumConstantName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstantName381.getTree());
                    char_literal382=(Token)match(input,75,FOLLOW_75_in_switchLabel4006); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal382_tree = (Object)adaptor.create(char_literal382);
                    adaptor.addChild(root_0, char_literal382_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:794:9: 'default' ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal383=(Token)match(input,74,FOLLOW_74_in_switchLabel4016); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal383_tree = (Object)adaptor.create(string_literal383);
                    adaptor.addChild(root_0, string_literal383_tree);
                    }
                    char_literal384=(Token)match(input,75,FOLLOW_75_in_switchLabel4018); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal384_tree = (Object)adaptor.create(char_literal384);
                    adaptor.addChild(root_0, char_literal384_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 96, switchLabel_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchLabel"

    public static class forControl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forControl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:797:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final JavaParser.forControl_return forControl() throws RecognitionException {
        JavaParser.forControl_return retval = new JavaParser.forControl_return();
        retval.start = input.LT(1);
        int forControl_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal387=null;
        Token char_literal389=null;
        JavaParser.enhancedForControl_return enhancedForControl385 = null;

        JavaParser.forInit_return forInit386 = null;

        JavaParser.expression_return expression388 = null;

        JavaParser.forUpdate_return forUpdate390 = null;


        Object char_literal387_tree=null;
        Object char_literal389_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:799:5: ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt123=2;
            alt123 = dfa123.predict(input);
            switch (alt123) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:799:9: enhancedForControl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enhancedForControl_in_forControl4049);
                    enhancedForControl385=enhancedForControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enhancedForControl385.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:9: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:9: ( forInit )?
                    int alt120=2;
                    int LA120_0 = input.LA(1);

                    if ( (LA120_0==Identifier||(LA120_0>=FloatingPointLiteral && LA120_0<=DecimalLiteral)||LA120_0==35||LA120_0==47||(LA120_0>=56 && LA120_0<=63)||(LA120_0>=65 && LA120_0<=66)||(LA120_0>=69 && LA120_0<=73)||(LA120_0>=105 && LA120_0<=106)||(LA120_0>=109 && LA120_0<=113)) ) {
                        alt120=1;
                    }
                    switch (alt120) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl4059);
                            forInit386=forInit();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forInit386.getTree());

                            }
                            break;

                    }

                    char_literal387=(Token)match(input,26,FOLLOW_26_in_forControl4062); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal387_tree = (Object)adaptor.create(char_literal387);
                    adaptor.addChild(root_0, char_literal387_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:22: ( expression )?
                    int alt121=2;
                    int LA121_0 = input.LA(1);

                    if ( (LA121_0==Identifier||(LA121_0>=FloatingPointLiteral && LA121_0<=DecimalLiteral)||LA121_0==47||(LA121_0>=56 && LA121_0<=63)||(LA121_0>=65 && LA121_0<=66)||(LA121_0>=69 && LA121_0<=72)||(LA121_0>=105 && LA121_0<=106)||(LA121_0>=109 && LA121_0<=113)) ) {
                        alt121=1;
                    }
                    switch (alt121) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl4064);
                            expression388=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression388.getTree());

                            }
                            break;

                    }

                    char_literal389=(Token)match(input,26,FOLLOW_26_in_forControl4067); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal389_tree = (Object)adaptor.create(char_literal389);
                    adaptor.addChild(root_0, char_literal389_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:800:38: ( forUpdate )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==47||(LA122_0>=56 && LA122_0<=63)||(LA122_0>=65 && LA122_0<=66)||(LA122_0>=69 && LA122_0<=72)||(LA122_0>=105 && LA122_0<=106)||(LA122_0>=109 && LA122_0<=113)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl4069);
                            forUpdate390=forUpdate();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forUpdate390.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 97, forControl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forControl"

    public static class forInit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forInit"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:803:1: forInit : ( localVariableDeclaration | expressionList );
    public final JavaParser.forInit_return forInit() throws RecognitionException {
        JavaParser.forInit_return retval = new JavaParser.forInit_return();
        retval.start = input.LT(1);
        int forInit_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclaration_return localVariableDeclaration391 = null;

        JavaParser.expressionList_return expressionList392 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:5: ( localVariableDeclaration | expressionList )
            int alt124=2;
            alt124 = dfa124.predict(input);
            switch (alt124) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:9: localVariableDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit4089);
                    localVariableDeclaration391=localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration391.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:9: expressionList
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expressionList_in_forInit4099);
                    expressionList392=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList392.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 98, forInit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forInit"

    public static class enhancedForControl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enhancedForControl"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:808:1: enhancedForControl : variableModifiers type Identifier ':' expression ;
    public final JavaParser.enhancedForControl_return enhancedForControl() throws RecognitionException {
        JavaParser.enhancedForControl_return retval = new JavaParser.enhancedForControl_return();
        retval.start = input.LT(1);
        int enhancedForControl_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier395=null;
        Token char_literal396=null;
        JavaParser.variableModifiers_return variableModifiers393 = null;

        JavaParser.type_return type394 = null;

        JavaParser.expression_return expression397 = null;


        Object Identifier395_tree=null;
        Object char_literal396_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:809:5: ( variableModifiers type Identifier ':' expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:809:9: variableModifiers type Identifier ':' expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_enhancedForControl4122);
            variableModifiers393=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers393.getTree());
            pushFollow(FOLLOW_type_in_enhancedForControl4124);
            type394=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type394.getTree());
            Identifier395=(Token)match(input,Identifier,FOLLOW_Identifier_in_enhancedForControl4126); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier395_tree = (Object)adaptor.create(Identifier395);
            adaptor.addChild(root_0, Identifier395_tree);
            }
            char_literal396=(Token)match(input,75,FOLLOW_75_in_enhancedForControl4128); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal396_tree = (Object)adaptor.create(char_literal396);
            adaptor.addChild(root_0, char_literal396_tree);
            }
            pushFollow(FOLLOW_expression_in_enhancedForControl4130);
            expression397=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression397.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 99, enhancedForControl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enhancedForControl"

    public static class forUpdate_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forUpdate"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:812:1: forUpdate : expressionList ;
    public final JavaParser.forUpdate_return forUpdate() throws RecognitionException {
        JavaParser.forUpdate_return retval = new JavaParser.forUpdate_return();
        retval.start = input.LT(1);
        int forUpdate_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expressionList_return expressionList398 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:5: ( expressionList )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:9: expressionList
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expressionList_in_forUpdate4149);
            expressionList398=expressionList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList398.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 100, forUpdate_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forUpdate"

    public static class parExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "parExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:818:1: parExpression : '(' expression ')' ;
    public final JavaParser.parExpression_return parExpression() throws RecognitionException {
        JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal399=null;
        Token char_literal401=null;
        JavaParser.expression_return expression400 = null;


        Object char_literal399_tree=null;
        Object char_literal401_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:819:5: ( '(' expression ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:819:9: '(' expression ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal399=(Token)match(input,66,FOLLOW_66_in_parExpression4170); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal399_tree = (Object)adaptor.create(char_literal399);
            adaptor.addChild(root_0, char_literal399_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression4172);
            expression400=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression400.getTree());
            char_literal401=(Token)match(input,67,FOLLOW_67_in_parExpression4174); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal401_tree = (Object)adaptor.create(char_literal401);
            adaptor.addChild(root_0, char_literal401_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 101, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "parExpression"

    public static class expressionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expressionList"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:822:1: expressionList : expression ( ',' expression )* ;
    public final JavaParser.expressionList_return expressionList() throws RecognitionException {
        JavaParser.expressionList_return retval = new JavaParser.expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal403=null;
        JavaParser.expression_return expression402 = null;

        JavaParser.expression_return expression404 = null;


        Object char_literal403_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:823:5: ( expression ( ',' expression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:823:9: expression ( ',' expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList4197);
            expression402=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression402.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:823:20: ( ',' expression )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( (LA125_0==41) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:823:21: ',' expression
            	    {
            	    char_literal403=(Token)match(input,41,FOLLOW_41_in_expressionList4200); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal403_tree = (Object)adaptor.create(char_literal403);
            	    adaptor.addChild(root_0, char_literal403_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList4202);
            	    expression404=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression404.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 102, expressionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    public static class statementExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statementExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:826:1: statementExpression : expression ;
    public final JavaParser.statementExpression_return statementExpression() throws RecognitionException {
        JavaParser.statementExpression_return retval = new JavaParser.statementExpression_return();
        retval.start = input.LT(1);
        int statementExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression405 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:827:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:827:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_statementExpression4223);
            expression405=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression405.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 103, statementExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statementExpression"

    public static class constantExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constantExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:830:1: constantExpression : expression ;
    public final JavaParser.constantExpression_return constantExpression() throws RecognitionException {
        JavaParser.constantExpression_return retval = new JavaParser.constantExpression_return();
        retval.start = input.LT(1);
        int constantExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression406 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:831:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:831:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_constantExpression4246);
            expression406=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression406.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 104, constantExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantExpression"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:834:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final JavaParser.expression_return expression() throws RecognitionException {
        JavaParser.expression_return retval = new JavaParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression407 = null;

        JavaParser.assignmentOperator_return assignmentOperator408 = null;

        JavaParser.expression_return expression409 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:5: ( conditionalExpression ( assignmentOperator expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:9: conditionalExpression ( assignmentOperator expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression4269);
            conditionalExpression407=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression407.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:31: ( assignmentOperator expression )?
            int alt126=2;
            alt126 = dfa126.predict(input);
            switch (alt126) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:32: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression4272);
                    assignmentOperator408=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentOperator408.getTree());
                    pushFollow(FOLLOW_expression_in_expression4274);
                    expression409=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression409.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 105, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class assignmentOperator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignmentOperator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:838:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);
    public final JavaParser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        JavaParser.assignmentOperator_return retval = new JavaParser.assignmentOperator_return();
        retval.start = input.LT(1);
        int assignmentOperator_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token char_literal410=null;
        Token string_literal411=null;
        Token string_literal412=null;
        Token string_literal413=null;
        Token string_literal414=null;
        Token string_literal415=null;
        Token string_literal416=null;
        Token string_literal417=null;
        Token string_literal418=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object t3_tree=null;
        Object t4_tree=null;
        Object char_literal410_tree=null;
        Object string_literal411_tree=null;
        Object string_literal412_tree=null;
        Object string_literal413_tree=null;
        Object string_literal414_tree=null;
        Object string_literal415_tree=null;
        Object string_literal416_tree=null;
        Object string_literal417_tree=null;
        Object string_literal418_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?)
            int alt127=12;
            alt127 = dfa127.predict(input);
            switch (alt127) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:839:9: '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal410=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4299); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal410_tree = (Object)adaptor.create(char_literal410);
                    adaptor.addChild(root_0, char_literal410_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:840:9: '+='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal411=(Token)match(input,90,FOLLOW_90_in_assignmentOperator4309); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal411_tree = (Object)adaptor.create(string_literal411);
                    adaptor.addChild(root_0, string_literal411_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:9: '-='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal412=(Token)match(input,91,FOLLOW_91_in_assignmentOperator4319); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal412_tree = (Object)adaptor.create(string_literal412);
                    adaptor.addChild(root_0, string_literal412_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:842:9: '*='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal413=(Token)match(input,92,FOLLOW_92_in_assignmentOperator4329); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal413_tree = (Object)adaptor.create(string_literal413);
                    adaptor.addChild(root_0, string_literal413_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:843:9: '/='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal414=(Token)match(input,93,FOLLOW_93_in_assignmentOperator4339); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal414_tree = (Object)adaptor.create(string_literal414);
                    adaptor.addChild(root_0, string_literal414_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:9: '&='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal415=(Token)match(input,94,FOLLOW_94_in_assignmentOperator4349); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal415_tree = (Object)adaptor.create(string_literal415);
                    adaptor.addChild(root_0, string_literal415_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:9: '|='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal416=(Token)match(input,95,FOLLOW_95_in_assignmentOperator4359); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal416_tree = (Object)adaptor.create(string_literal416);
                    adaptor.addChild(root_0, string_literal416_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:846:9: '^='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal417=(Token)match(input,96,FOLLOW_96_in_assignmentOperator4369); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal417_tree = (Object)adaptor.create(string_literal417);
                    adaptor.addChild(root_0, string_literal417_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:847:9: '%='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal418=(Token)match(input,97,FOLLOW_97_in_assignmentOperator4379); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal418_tree = (Object)adaptor.create(string_literal418);
                    adaptor.addChild(root_0, string_literal418_tree);
                    }

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:848:9: ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_assignmentOperator4400); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_assignmentOperator4404); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4408); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:9: ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4442); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4446); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4450); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    t4=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4454); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:9: ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4485); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4489); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4493); if (state.failed) return retval;
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
            if ( state.backtracking>0 ) { memoize(input, 106, assignmentOperator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:867:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final JavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JavaParser.conditionalExpression_return retval = new JavaParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal420=null;
        Token char_literal422=null;
        JavaParser.conditionalOrExpression_return conditionalOrExpression419 = null;

        JavaParser.expression_return expression421 = null;

        JavaParser.expression_return expression423 = null;


        Object char_literal420_tree=null;
        Object char_literal422_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression4522);
            conditionalOrExpression419=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression419.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:33: ( '?' expression ':' expression )?
            int alt128=2;
            int LA128_0 = input.LA(1);

            if ( (LA128_0==64) ) {
                alt128=1;
            }
            switch (alt128) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:868:35: '?' expression ':' expression
                    {
                    char_literal420=(Token)match(input,64,FOLLOW_64_in_conditionalExpression4526); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal420_tree = (Object)adaptor.create(char_literal420);
                    adaptor.addChild(root_0, char_literal420_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression4528);
                    expression421=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression421.getTree());
                    char_literal422=(Token)match(input,75,FOLLOW_75_in_conditionalExpression4530); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal422_tree = (Object)adaptor.create(char_literal422);
                    adaptor.addChild(root_0, char_literal422_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression4532);
                    expression423=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression423.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 107, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:871:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final JavaParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JavaParser.conditionalOrExpression_return retval = new JavaParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal425=null;
        JavaParser.conditionalAndExpression_return conditionalAndExpression424 = null;

        JavaParser.conditionalAndExpression_return conditionalAndExpression426 = null;


        Object string_literal425_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4554);
            conditionalAndExpression424=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression424.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:34: ( '||' conditionalAndExpression )*
            loop129:
            do {
                int alt129=2;
                int LA129_0 = input.LA(1);

                if ( (LA129_0==98) ) {
                    alt129=1;
                }


                switch (alt129) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:36: '||' conditionalAndExpression
            	    {
            	    string_literal425=(Token)match(input,98,FOLLOW_98_in_conditionalOrExpression4558); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal425_tree = (Object)adaptor.create(string_literal425);
            	    adaptor.addChild(root_0, string_literal425_tree);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4560);
            	    conditionalAndExpression426=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression426.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 108, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:875:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final JavaParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JavaParser.conditionalAndExpression_return retval = new JavaParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal428=null;
        JavaParser.inclusiveOrExpression_return inclusiveOrExpression427 = null;

        JavaParser.inclusiveOrExpression_return inclusiveOrExpression429 = null;


        Object string_literal428_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4582);
            inclusiveOrExpression427=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression427.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:31: ( '&&' inclusiveOrExpression )*
            loop130:
            do {
                int alt130=2;
                int LA130_0 = input.LA(1);

                if ( (LA130_0==99) ) {
                    alt130=1;
                }


                switch (alt130) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:876:33: '&&' inclusiveOrExpression
            	    {
            	    string_literal428=(Token)match(input,99,FOLLOW_99_in_conditionalAndExpression4586); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal428_tree = (Object)adaptor.create(string_literal428);
            	    adaptor.addChild(root_0, string_literal428_tree);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4588);
            	    inclusiveOrExpression429=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression429.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 109, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inclusiveOrExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final JavaParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JavaParser.inclusiveOrExpression_return retval = new JavaParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal431=null;
        JavaParser.exclusiveOrExpression_return exclusiveOrExpression430 = null;

        JavaParser.exclusiveOrExpression_return exclusiveOrExpression432 = null;


        Object char_literal431_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4610);
            exclusiveOrExpression430=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression430.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:31: ( '|' exclusiveOrExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==100) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:880:33: '|' exclusiveOrExpression
            	    {
            	    char_literal431=(Token)match(input,100,FOLLOW_100_in_inclusiveOrExpression4614); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal431_tree = (Object)adaptor.create(char_literal431);
            	    adaptor.addChild(root_0, char_literal431_tree);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4616);
            	    exclusiveOrExpression432=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression432.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 110, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exclusiveOrExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final JavaParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JavaParser.exclusiveOrExpression_return retval = new JavaParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal434=null;
        JavaParser.andExpression_return andExpression433 = null;

        JavaParser.andExpression_return andExpression435 = null;


        Object char_literal434_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:5: ( andExpression ( '^' andExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression4638);
            andExpression433=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression433.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:23: ( '^' andExpression )*
            loop132:
            do {
                int alt132=2;
                int LA132_0 = input.LA(1);

                if ( (LA132_0==101) ) {
                    alt132=1;
                }


                switch (alt132) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:884:25: '^' andExpression
            	    {
            	    char_literal434=(Token)match(input,101,FOLLOW_101_in_exclusiveOrExpression4642); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal434_tree = (Object)adaptor.create(char_literal434);
            	    adaptor.addChild(root_0, char_literal434_tree);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression4644);
            	    andExpression435=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression435.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 111, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:887:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final JavaParser.andExpression_return andExpression() throws RecognitionException {
        JavaParser.andExpression_return retval = new JavaParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal437=null;
        JavaParser.equalityExpression_return equalityExpression436 = null;

        JavaParser.equalityExpression_return equalityExpression438 = null;


        Object char_literal437_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression4666);
            equalityExpression436=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression436.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:28: ( '&' equalityExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==43) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:30: '&' equalityExpression
            	    {
            	    char_literal437=(Token)match(input,43,FOLLOW_43_in_andExpression4670); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal437_tree = (Object)adaptor.create(char_literal437);
            	    adaptor.addChild(root_0, char_literal437_tree);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression4672);
            	    equalityExpression438=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression438.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 112, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:891:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final JavaParser.equalityExpression_return equalityExpression() throws RecognitionException {
        JavaParser.equalityExpression_return retval = new JavaParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set440=null;
        JavaParser.instanceOfExpression_return instanceOfExpression439 = null;

        JavaParser.instanceOfExpression_return instanceOfExpression441 = null;


        Object set440_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression4694);
            instanceOfExpression439=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression439.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( ((LA134_0>=102 && LA134_0<=103)) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:892:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set440=(Token)input.LT(1);
            	    if ( (input.LA(1)>=102 && input.LA(1)<=103) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set440));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression4706);
            	    instanceOfExpression441=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression441.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 113, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "instanceOfExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:895:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final JavaParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JavaParser.instanceOfExpression_return retval = new JavaParser.instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal443=null;
        JavaParser.relationalExpression_return relationalExpression442 = null;

        JavaParser.type_return type444 = null;


        Object string_literal443_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:5: ( relationalExpression ( 'instanceof' type )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression4728);
            relationalExpression442=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression442.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:30: ( 'instanceof' type )?
            int alt135=2;
            int LA135_0 = input.LA(1);

            if ( (LA135_0==104) ) {
                alt135=1;
            }
            switch (alt135) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:31: 'instanceof' type
                    {
                    string_literal443=(Token)match(input,104,FOLLOW_104_in_instanceOfExpression4731); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal443_tree = (Object)adaptor.create(string_literal443);
                    adaptor.addChild(root_0, string_literal443_tree);
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression4733);
                    type444=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type444.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 114, instanceOfExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:899:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final JavaParser.relationalExpression_return relationalExpression() throws RecognitionException {
        JavaParser.relationalExpression_return retval = new JavaParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.shiftExpression_return shiftExpression445 = null;

        JavaParser.relationalOp_return relationalOp446 = null;

        JavaParser.shiftExpression_return shiftExpression447 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression4754);
            shiftExpression445=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression445.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:25: ( relationalOp shiftExpression )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:900:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression4758);
            	    relationalOp446=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp446.getTree());
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression4760);
            	    shiftExpression447=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression447.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 115, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class relationalOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalOp"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:1: relationalOp : ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' );
    public final JavaParser.relationalOp_return relationalOp() throws RecognitionException {
        JavaParser.relationalOp_return retval = new JavaParser.relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token char_literal448=null;
        Token char_literal449=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object char_literal448_tree=null;
        Object char_literal449_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:904:5: ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:904:9: ( '<' '=' )=>t1= '<' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_relationalOp4795); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp4799); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:9: ( '>' '=' )=>t1= '>' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_relationalOp4829); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp4833); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:910:9: '<'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal448=(Token)match(input,40,FOLLOW_40_in_relationalOp4854); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal448_tree = (Object)adaptor.create(char_literal448);
                    adaptor.addChild(root_0, char_literal448_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:911:9: '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal449=(Token)match(input,42,FOLLOW_42_in_relationalOp4865); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal449_tree = (Object)adaptor.create(char_literal449);
                    adaptor.addChild(root_0, char_literal449_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 116, relationalOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalOp"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:914:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final JavaParser.shiftExpression_return shiftExpression() throws RecognitionException {
        JavaParser.shiftExpression_return retval = new JavaParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.additiveExpression_return additiveExpression450 = null;

        JavaParser.shiftOp_return shiftOp451 = null;

        JavaParser.additiveExpression_return additiveExpression452 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression4885);
            additiveExpression450=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression450.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:28: ( shiftOp additiveExpression )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:915:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression4889);
            	    shiftOp451=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp451.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression4891);
            	    additiveExpression452=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression452.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 117, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class shiftOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftOp"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:918:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);
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
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:5: ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?)
            int alt139=3;
            alt139 = dfa139.predict(input);
            switch (alt139) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:9: ( '<' '<' )=>t1= '<' t2= '<' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_shiftOp4922); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_shiftOp4926); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:922:9: ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp4958); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp4962); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_shiftOp4966); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:9: ( '>' '>' )=>t1= '>' t2= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp4996); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp5000); if (state.failed) return retval;
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
            if ( state.backtracking>0 ) { memoize(input, 118, shiftOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftOp"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:933:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final JavaParser.additiveExpression_return additiveExpression() throws RecognitionException {
        JavaParser.additiveExpression_return retval = new JavaParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set454=null;
        JavaParser.multiplicativeExpression_return multiplicativeExpression453 = null;

        JavaParser.multiplicativeExpression_return multiplicativeExpression455 = null;


        Object set454_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5030);
            multiplicativeExpression453=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression453.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop140:
            do {
                int alt140=2;
                int LA140_0 = input.LA(1);

                if ( ((LA140_0>=105 && LA140_0<=106)) ) {
                    alt140=1;
                }


                switch (alt140) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set454=(Token)input.LT(1);
            	    if ( (input.LA(1)>=105 && input.LA(1)<=106) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set454));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5042);
            	    multiplicativeExpression455=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression455.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 119, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:937:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final JavaParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JavaParser.multiplicativeExpression_return retval = new JavaParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set457=null;
        JavaParser.unaryExpression_return unaryExpression456 = null;

        JavaParser.unaryExpression_return unaryExpression458 = null;


        Object set457_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5064);
            unaryExpression456=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression456.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop141:
            do {
                int alt141=2;
                int LA141_0 = input.LA(1);

                if ( (LA141_0==30||(LA141_0>=107 && LA141_0<=108)) ) {
                    alt141=1;
                }


                switch (alt141) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set457=(Token)input.LT(1);
            	    if ( input.LA(1)==30||(input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set457));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5082);
            	    unaryExpression458=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression458.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 120, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:941:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final JavaParser.unaryExpression_return unaryExpression() throws RecognitionException {
        JavaParser.unaryExpression_return retval = new JavaParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal459=null;
        Token char_literal461=null;
        Token string_literal463=null;
        Token string_literal465=null;
        JavaParser.unaryExpression_return unaryExpression460 = null;

        JavaParser.unaryExpression_return unaryExpression462 = null;

        JavaParser.unaryExpression_return unaryExpression464 = null;

        JavaParser.unaryExpression_return unaryExpression466 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus467 = null;


        Object char_literal459_tree=null;
        Object char_literal461_tree=null;
        Object string_literal463_tree=null;
        Object string_literal465_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:942:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:942:9: '+' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal459=(Token)match(input,105,FOLLOW_105_in_unaryExpression5108); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal459_tree = (Object)adaptor.create(char_literal459);
                    adaptor.addChild(root_0, char_literal459_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5110);
                    unaryExpression460=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression460.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:9: '-' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal461=(Token)match(input,106,FOLLOW_106_in_unaryExpression5120); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal461_tree = (Object)adaptor.create(char_literal461);
                    adaptor.addChild(root_0, char_literal461_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5122);
                    unaryExpression462=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression462.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:944:9: '++' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal463=(Token)match(input,109,FOLLOW_109_in_unaryExpression5132); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal463_tree = (Object)adaptor.create(string_literal463);
                    adaptor.addChild(root_0, string_literal463_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5134);
                    unaryExpression464=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression464.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:9: '--' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal465=(Token)match(input,110,FOLLOW_110_in_unaryExpression5144); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal465_tree = (Object)adaptor.create(string_literal465);
                    adaptor.addChild(root_0, string_literal465_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5146);
                    unaryExpression466=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression466.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:946:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5156);
                    unaryExpressionNotPlusMinus467=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus467.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 121, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:949:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JavaParser.unaryExpressionNotPlusMinus_return retval = new JavaParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal468=null;
        Token char_literal470=null;
        Token set475=null;
        JavaParser.unaryExpression_return unaryExpression469 = null;

        JavaParser.unaryExpression_return unaryExpression471 = null;

        JavaParser.castExpression_return castExpression472 = null;

        JavaParser.primary_return primary473 = null;

        JavaParser.selector_return selector474 = null;


        Object char_literal468_tree=null;
        Object char_literal470_tree=null;
        Object set475_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:950:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt145=4;
            alt145 = dfa145.predict(input);
            switch (alt145) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:950:9: '~' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal468=(Token)match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus5175); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal468_tree = (Object)adaptor.create(char_literal468);
                    adaptor.addChild(root_0, char_literal468_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5177);
                    unaryExpression469=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression469.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:951:9: '!' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal470=(Token)match(input,112,FOLLOW_112_in_unaryExpressionNotPlusMinus5187); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal470_tree = (Object)adaptor.create(char_literal470);
                    adaptor.addChild(root_0, char_literal470_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5189);
                    unaryExpression471=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression471.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5199);
                    castExpression472=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression472.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:953:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus5209);
                    primary473=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary473.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:953:17: ( selector )*
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
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus5211);
                    	    selector474=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector474.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop143;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:953:27: ( '++' | '--' )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( ((LA144_0>=109 && LA144_0<=110)) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
                            {
                            set475=(Token)input.LT(1);
                            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set475));
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
            if ( state.backtracking>0 ) { memoize(input, 122, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "castExpression"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:956:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final JavaParser.castExpression_return castExpression() throws RecognitionException {
        JavaParser.castExpression_return retval = new JavaParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal476=null;
        Token char_literal478=null;
        Token char_literal480=null;
        Token char_literal483=null;
        JavaParser.primitiveType_return primitiveType477 = null;

        JavaParser.unaryExpression_return unaryExpression479 = null;

        JavaParser.type_return type481 = null;

        JavaParser.expression_return expression482 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus484 = null;


        Object char_literal476_tree=null;
        Object char_literal478_tree=null;
        Object char_literal480_tree=null;
        Object char_literal483_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:957:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:957:8: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal476=(Token)match(input,66,FOLLOW_66_in_castExpression5237); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal476_tree = (Object)adaptor.create(char_literal476);
                    adaptor.addChild(root_0, char_literal476_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression5239);
                    primitiveType477=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType477.getTree());
                    char_literal478=(Token)match(input,67,FOLLOW_67_in_castExpression5241); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal478_tree = (Object)adaptor.create(char_literal478);
                    adaptor.addChild(root_0, char_literal478_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression5243);
                    unaryExpression479=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression479.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal480=(Token)match(input,66,FOLLOW_66_in_castExpression5252); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal480_tree = (Object)adaptor.create(char_literal480);
                    adaptor.addChild(root_0, char_literal480_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:12: ( type | expression )
                    int alt146=2;
                    alt146 = dfa146.predict(input);
                    switch (alt146) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression5255);
                            type481=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type481.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression5259);
                            expression482=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression482.getTree());

                            }
                            break;

                    }

                    char_literal483=(Token)match(input,67,FOLLOW_67_in_castExpression5262); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal483_tree = (Object)adaptor.create(char_literal483);
                    adaptor.addChild(root_0, char_literal483_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5264);
                    unaryExpressionNotPlusMinus484=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus484.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 123, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:961:1: primary : ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final JavaParser.primary_return primary() throws RecognitionException {
        JavaParser.primary_return retval = new JavaParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal486=null;
        Token char_literal487=null;
        Token Identifier488=null;
        Token string_literal490=null;
        Token string_literal493=null;
        Token Identifier495=null;
        Token char_literal496=null;
        Token Identifier497=null;
        Token char_literal500=null;
        Token char_literal501=null;
        Token char_literal502=null;
        Token string_literal503=null;
        Token string_literal504=null;
        Token char_literal505=null;
        Token string_literal506=null;
        JavaParser.parExpression_return parExpression485 = null;

        JavaParser.identifierSuffix_return identifierSuffix489 = null;

        JavaParser.superSuffix_return superSuffix491 = null;

        JavaParser.literal_return literal492 = null;

        JavaParser.creator_return creator494 = null;

        JavaParser.identifierSuffix_return identifierSuffix498 = null;

        JavaParser.primitiveType_return primitiveType499 = null;


        Object string_literal486_tree=null;
        Object char_literal487_tree=null;
        Object Identifier488_tree=null;
        Object string_literal490_tree=null;
        Object string_literal493_tree=null;
        Object Identifier495_tree=null;
        Object char_literal496_tree=null;
        Object Identifier497_tree=null;
        Object char_literal500_tree=null;
        Object char_literal501_tree=null;
        Object char_literal502_tree=null;
        Object string_literal503_tree=null;
        Object string_literal504_tree=null;
        Object char_literal505_tree=null;
        Object string_literal506_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:962:5: ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:962:9: parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary5283);
                    parExpression485=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression485.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal486=(Token)match(input,69,FOLLOW_69_in_primary5293); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal486_tree = (Object)adaptor.create(string_literal486);
                    adaptor.addChild(root_0, string_literal486_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:16: ( '.' Identifier )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:17: '.' Identifier
                    	    {
                    	    char_literal487=(Token)match(input,29,FOLLOW_29_in_primary5296); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal487_tree = (Object)adaptor.create(char_literal487);
                    	    adaptor.addChild(root_0, char_literal487_tree);
                    	    }
                    	    Identifier488=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5298); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier488_tree = (Object)adaptor.create(Identifier488);
                    	    adaptor.addChild(root_0, Identifier488_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:34: ( identifierSuffix )?
                    int alt149=2;
                    alt149 = dfa149.predict(input);
                    switch (alt149) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5302);
                            identifierSuffix489=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix489.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:964:9: 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal490=(Token)match(input,65,FOLLOW_65_in_primary5313); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal490_tree = (Object)adaptor.create(string_literal490);
                    adaptor.addChild(root_0, string_literal490_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_primary5315);
                    superSuffix491=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix491.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:965:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary5325);
                    literal492=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal492.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:966:9: 'new' creator
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal493=(Token)match(input,113,FOLLOW_113_in_primary5335); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal493_tree = (Object)adaptor.create(string_literal493);
                    adaptor.addChild(root_0, string_literal493_tree);
                    }
                    pushFollow(FOLLOW_creator_in_primary5337);
                    creator494=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator494.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:9: Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier495=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5347); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier495_tree = (Object)adaptor.create(Identifier495);
                    adaptor.addChild(root_0, Identifier495_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:20: ( '.' Identifier )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:21: '.' Identifier
                    	    {
                    	    char_literal496=(Token)match(input,29,FOLLOW_29_in_primary5350); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal496_tree = (Object)adaptor.create(char_literal496);
                    	    adaptor.addChild(root_0, char_literal496_tree);
                    	    }
                    	    Identifier497=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5352); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier497_tree = (Object)adaptor.create(Identifier497);
                    	    adaptor.addChild(root_0, Identifier497_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop150;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:38: ( identifierSuffix )?
                    int alt151=2;
                    alt151 = dfa151.predict(input);
                    switch (alt151) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5356);
                            identifierSuffix498=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix498.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary5367);
                    primitiveType499=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType499.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:23: ( '[' ']' )*
                    loop152:
                    do {
                        int alt152=2;
                        int LA152_0 = input.LA(1);

                        if ( (LA152_0==48) ) {
                            alt152=1;
                        }


                        switch (alt152) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:968:24: '[' ']'
                    	    {
                    	    char_literal500=(Token)match(input,48,FOLLOW_48_in_primary5370); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal500_tree = (Object)adaptor.create(char_literal500);
                    	    adaptor.addChild(root_0, char_literal500_tree);
                    	    }
                    	    char_literal501=(Token)match(input,49,FOLLOW_49_in_primary5372); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal501_tree = (Object)adaptor.create(char_literal501);
                    	    adaptor.addChild(root_0, char_literal501_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop152;
                        }
                    } while (true);

                    char_literal502=(Token)match(input,29,FOLLOW_29_in_primary5376); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal502_tree = (Object)adaptor.create(char_literal502);
                    adaptor.addChild(root_0, char_literal502_tree);
                    }
                    string_literal503=(Token)match(input,37,FOLLOW_37_in_primary5378); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal503_tree = (Object)adaptor.create(string_literal503);
                    adaptor.addChild(root_0, string_literal503_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:969:9: 'void' '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal504=(Token)match(input,47,FOLLOW_47_in_primary5388); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal504_tree = (Object)adaptor.create(string_literal504);
                    adaptor.addChild(root_0, string_literal504_tree);
                    }
                    char_literal505=(Token)match(input,29,FOLLOW_29_in_primary5390); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal505_tree = (Object)adaptor.create(char_literal505);
                    adaptor.addChild(root_0, char_literal505_tree);
                    }
                    string_literal506=(Token)match(input,37,FOLLOW_37_in_primary5392); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal506_tree = (Object)adaptor.create(string_literal506);
                    adaptor.addChild(root_0, string_literal506_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 124, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class identifierSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "identifierSuffix"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:972:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );
    public final JavaParser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        JavaParser.identifierSuffix_return retval = new JavaParser.identifierSuffix_return();
        retval.start = input.LT(1);
        int identifierSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal507=null;
        Token char_literal508=null;
        Token char_literal509=null;
        Token string_literal510=null;
        Token char_literal511=null;
        Token char_literal513=null;
        Token char_literal515=null;
        Token string_literal516=null;
        Token char_literal517=null;
        Token char_literal519=null;
        Token string_literal520=null;
        Token char_literal521=null;
        Token string_literal522=null;
        Token char_literal524=null;
        Token string_literal525=null;
        JavaParser.expression_return expression512 = null;

        JavaParser.arguments_return arguments514 = null;

        JavaParser.explicitGenericInvocation_return explicitGenericInvocation518 = null;

        JavaParser.arguments_return arguments523 = null;

        JavaParser.innerCreator_return innerCreator526 = null;


        Object char_literal507_tree=null;
        Object char_literal508_tree=null;
        Object char_literal509_tree=null;
        Object string_literal510_tree=null;
        Object char_literal511_tree=null;
        Object char_literal513_tree=null;
        Object char_literal515_tree=null;
        Object string_literal516_tree=null;
        Object char_literal517_tree=null;
        Object char_literal519_tree=null;
        Object string_literal520_tree=null;
        Object char_literal521_tree=null;
        Object string_literal522_tree=null;
        Object char_literal524_tree=null;
        Object string_literal525_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:973:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator )
            int alt156=8;
            alt156 = dfa156.predict(input);
            switch (alt156) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:973:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:973:9: ( '[' ']' )+
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:973:10: '[' ']'
                    	    {
                    	    char_literal507=(Token)match(input,48,FOLLOW_48_in_identifierSuffix5412); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal507_tree = (Object)adaptor.create(char_literal507);
                    	    adaptor.addChild(root_0, char_literal507_tree);
                    	    }
                    	    char_literal508=(Token)match(input,49,FOLLOW_49_in_identifierSuffix5414); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal508_tree = (Object)adaptor.create(char_literal508);
                    	    adaptor.addChild(root_0, char_literal508_tree);
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

                    char_literal509=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5418); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal509_tree = (Object)adaptor.create(char_literal509);
                    adaptor.addChild(root_0, char_literal509_tree);
                    }
                    string_literal510=(Token)match(input,37,FOLLOW_37_in_identifierSuffix5420); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal510_tree = (Object)adaptor.create(string_literal510);
                    adaptor.addChild(root_0, string_literal510_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:974:9: ( '[' expression ']' )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:974:9: ( '[' expression ']' )+
                    int cnt155=0;
                    loop155:
                    do {
                        int alt155=2;
                        alt155 = dfa155.predict(input);
                        switch (alt155) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:974:10: '[' expression ']'
                    	    {
                    	    char_literal511=(Token)match(input,48,FOLLOW_48_in_identifierSuffix5431); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal511_tree = (Object)adaptor.create(char_literal511);
                    	    adaptor.addChild(root_0, char_literal511_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix5433);
                    	    expression512=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression512.getTree());
                    	    char_literal513=(Token)match(input,49,FOLLOW_49_in_identifierSuffix5435); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal513_tree = (Object)adaptor.create(char_literal513);
                    	    adaptor.addChild(root_0, char_literal513_tree);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:975:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix5448);
                    arguments514=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments514.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:976:9: '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal515=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5458); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal515_tree = (Object)adaptor.create(char_literal515);
                    adaptor.addChild(root_0, char_literal515_tree);
                    }
                    string_literal516=(Token)match(input,37,FOLLOW_37_in_identifierSuffix5460); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal516_tree = (Object)adaptor.create(string_literal516);
                    adaptor.addChild(root_0, string_literal516_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:977:9: '.' explicitGenericInvocation
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal517=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5470); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal517_tree = (Object)adaptor.create(char_literal517);
                    adaptor.addChild(root_0, char_literal517_tree);
                    }
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix5472);
                    explicitGenericInvocation518=explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGenericInvocation518.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:978:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal519=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5482); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal519_tree = (Object)adaptor.create(char_literal519);
                    adaptor.addChild(root_0, char_literal519_tree);
                    }
                    string_literal520=(Token)match(input,69,FOLLOW_69_in_identifierSuffix5484); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal520_tree = (Object)adaptor.create(string_literal520);
                    adaptor.addChild(root_0, string_literal520_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:979:9: '.' 'super' arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal521=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5494); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal521_tree = (Object)adaptor.create(char_literal521);
                    adaptor.addChild(root_0, char_literal521_tree);
                    }
                    string_literal522=(Token)match(input,65,FOLLOW_65_in_identifierSuffix5496); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal522_tree = (Object)adaptor.create(string_literal522);
                    adaptor.addChild(root_0, string_literal522_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_identifierSuffix5498);
                    arguments523=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments523.getTree());

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:980:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal524=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5508); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal524_tree = (Object)adaptor.create(char_literal524);
                    adaptor.addChild(root_0, char_literal524_tree);
                    }
                    string_literal525=(Token)match(input,113,FOLLOW_113_in_identifierSuffix5510); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal525_tree = (Object)adaptor.create(string_literal525);
                    adaptor.addChild(root_0, string_literal525_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix5512);
                    innerCreator526=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator526.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 125, identifierSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "identifierSuffix"

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "creator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:983:1: creator : ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) );
    public final JavaParser.creator_return creator() throws RecognitionException {
        JavaParser.creator_return retval = new JavaParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments527 = null;

        JavaParser.createdName_return createdName528 = null;

        JavaParser.classCreatorRest_return classCreatorRest529 = null;

        JavaParser.createdName_return createdName530 = null;

        JavaParser.arrayCreatorRest_return arrayCreatorRest531 = null;

        JavaParser.classCreatorRest_return classCreatorRest532 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:5: ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:984:9: nonWildcardTypeArguments createdName classCreatorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator5531);
                    nonWildcardTypeArguments527=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments527.getTree());
                    pushFollow(FOLLOW_createdName_in_creator5533);
                    createdName528=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName528.getTree());
                    pushFollow(FOLLOW_classCreatorRest_in_creator5535);
                    classCreatorRest529=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest529.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:985:9: createdName ( arrayCreatorRest | classCreatorRest )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_createdName_in_creator5545);
                    createdName530=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName530.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:985:21: ( arrayCreatorRest | classCreatorRest )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:985:22: arrayCreatorRest
                            {
                            pushFollow(FOLLOW_arrayCreatorRest_in_creator5548);
                            arrayCreatorRest531=arrayCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreatorRest531.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:985:41: classCreatorRest
                            {
                            pushFollow(FOLLOW_classCreatorRest_in_creator5552);
                            classCreatorRest532=classCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest532.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 126, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class createdName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "createdName"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:988:1: createdName : ( classOrInterfaceType | primitiveType );
    public final JavaParser.createdName_return createdName() throws RecognitionException {
        JavaParser.createdName_return retval = new JavaParser.createdName_return();
        retval.start = input.LT(1);
        int createdName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType533 = null;

        JavaParser.primitiveType_return primitiveType534 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:989:5: ( classOrInterfaceType | primitiveType )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:989:9: classOrInterfaceType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName5572);
                    classOrInterfaceType533=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType533.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:990:9: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName5582);
                    primitiveType534=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType534.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 127, createdName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "createdName"

    public static class innerCreator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "innerCreator"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:993:1: innerCreator : ( nonWildcardTypeArguments )? Identifier classCreatorRest ;
    public final JavaParser.innerCreator_return innerCreator() throws RecognitionException {
        JavaParser.innerCreator_return retval = new JavaParser.innerCreator_return();
        retval.start = input.LT(1);
        int innerCreator_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier536=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments535 = null;

        JavaParser.classCreatorRest_return classCreatorRest537 = null;


        Object Identifier536_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:994:5: ( ( nonWildcardTypeArguments )? Identifier classCreatorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:994:9: ( nonWildcardTypeArguments )? Identifier classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:994:9: ( nonWildcardTypeArguments )?
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==40) ) {
                alt160=1;
            }
            switch (alt160) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator5605);
                    nonWildcardTypeArguments535=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments535.getTree());

                    }
                    break;

            }

            Identifier536=(Token)match(input,Identifier,FOLLOW_Identifier_in_innerCreator5608); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier536_tree = (Object)adaptor.create(Identifier536);
            adaptor.addChild(root_0, Identifier536_tree);
            }
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator5610);
            classCreatorRest537=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest537.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 128, innerCreator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "innerCreator"

    public static class arrayCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayCreatorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:997:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final JavaParser.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        JavaParser.arrayCreatorRest_return retval = new JavaParser.arrayCreatorRest_return();
        retval.start = input.LT(1);
        int arrayCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal538=null;
        Token char_literal539=null;
        Token char_literal540=null;
        Token char_literal541=null;
        Token char_literal544=null;
        Token char_literal545=null;
        Token char_literal547=null;
        Token char_literal548=null;
        Token char_literal549=null;
        JavaParser.arrayInitializer_return arrayInitializer542 = null;

        JavaParser.expression_return expression543 = null;

        JavaParser.expression_return expression546 = null;


        Object char_literal538_tree=null;
        Object char_literal539_tree=null;
        Object char_literal540_tree=null;
        Object char_literal541_tree=null;
        Object char_literal544_tree=null;
        Object char_literal545_tree=null;
        Object char_literal547_tree=null;
        Object char_literal548_tree=null;
        Object char_literal549_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:998:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:998:9: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            root_0 = (Object)adaptor.nil();

            char_literal538=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5629); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal538_tree = (Object)adaptor.create(char_literal538);
            adaptor.addChild(root_0, char_literal538_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    char_literal539=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5643); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal539_tree = (Object)adaptor.create(char_literal539);
                    adaptor.addChild(root_0, char_literal539_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:17: ( '[' ']' )*
                    loop161:
                    do {
                        int alt161=2;
                        int LA161_0 = input.LA(1);

                        if ( (LA161_0==48) ) {
                            alt161=1;
                        }


                        switch (alt161) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:999:18: '[' ']'
                    	    {
                    	    char_literal540=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5646); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal540_tree = (Object)adaptor.create(char_literal540);
                    	    adaptor.addChild(root_0, char_literal540_tree);
                    	    }
                    	    char_literal541=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5648); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal541_tree = (Object)adaptor.create(char_literal541);
                    	    adaptor.addChild(root_0, char_literal541_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop161;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest5652);
                    arrayInitializer542=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer542.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest5666);
                    expression543=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression543.getTree());
                    char_literal544=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5668); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal544_tree = (Object)adaptor.create(char_literal544);
                    adaptor.addChild(root_0, char_literal544_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:28: ( '[' expression ']' )*
                    loop162:
                    do {
                        int alt162=2;
                        alt162 = dfa162.predict(input);
                        switch (alt162) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:29: '[' expression ']'
                    	    {
                    	    char_literal545=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5671); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal545_tree = (Object)adaptor.create(char_literal545);
                    	    adaptor.addChild(root_0, char_literal545_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest5673);
                    	    expression546=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression546.getTree());
                    	    char_literal547=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5675); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal547_tree = (Object)adaptor.create(char_literal547);
                    	    adaptor.addChild(root_0, char_literal547_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop162;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:50: ( '[' ']' )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:51: '[' ']'
                    	    {
                    	    char_literal548=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5680); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal548_tree = (Object)adaptor.create(char_literal548);
                    	    adaptor.addChild(root_0, char_literal548_tree);
                    	    }
                    	    char_literal549=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5682); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal549_tree = (Object)adaptor.create(char_literal549);
                    	    adaptor.addChild(root_0, char_literal549_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 129, arrayCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arrayCreatorRest"

    public static class classCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classCreatorRest"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1004:1: classCreatorRest : arguments ( classBody )? ;
    public final JavaParser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        JavaParser.classCreatorRest_return retval = new JavaParser.classCreatorRest_return();
        retval.start = input.LT(1);
        int classCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arguments_return arguments550 = null;

        JavaParser.classBody_return classBody551 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1005:5: ( arguments ( classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1005:9: arguments ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest5713);
            arguments550=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments550.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1005:19: ( classBody )?
            int alt165=2;
            int LA165_0 = input.LA(1);

            if ( (LA165_0==44) ) {
                alt165=1;
            }
            switch (alt165) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest5715);
                    classBody551=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody551.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 130, classCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classCreatorRest"

    public static class explicitGenericInvocation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitGenericInvocation"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1008:1: explicitGenericInvocation : nonWildcardTypeArguments Identifier arguments ;
    public final JavaParser.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        JavaParser.explicitGenericInvocation_return retval = new JavaParser.explicitGenericInvocation_return();
        retval.start = input.LT(1);
        int explicitGenericInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier553=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments552 = null;

        JavaParser.arguments_return arguments554 = null;


        Object Identifier553_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1009:5: ( nonWildcardTypeArguments Identifier arguments )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1009:9: nonWildcardTypeArguments Identifier arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation5739);
            nonWildcardTypeArguments552=nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments552.getTree());
            Identifier553=(Token)match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocation5741); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier553_tree = (Object)adaptor.create(Identifier553);
            adaptor.addChild(root_0, Identifier553_tree);
            }
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation5743);
            arguments554=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments554.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 131, explicitGenericInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "explicitGenericInvocation"

    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nonWildcardTypeArguments"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1012:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        JavaParser.nonWildcardTypeArguments_return retval = new JavaParser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);
        int nonWildcardTypeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal555=null;
        Token char_literal557=null;
        JavaParser.typeList_return typeList556 = null;


        Object char_literal555_tree=null;
        Object char_literal557_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1013:5: ( '<' typeList '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1013:9: '<' typeList '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal555=(Token)match(input,40,FOLLOW_40_in_nonWildcardTypeArguments5766); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal555_tree = (Object)adaptor.create(char_literal555);
            adaptor.addChild(root_0, char_literal555_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments5768);
            typeList556=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList556.getTree());
            char_literal557=(Token)match(input,42,FOLLOW_42_in_nonWildcardTypeArguments5770); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal557_tree = (Object)adaptor.create(char_literal557);
            adaptor.addChild(root_0, char_literal557_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 132, nonWildcardTypeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "nonWildcardTypeArguments"

    public static class selector_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selector"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1016:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' );
    public final JavaParser.selector_return selector() throws RecognitionException {
        JavaParser.selector_return retval = new JavaParser.selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal558=null;
        Token Identifier559=null;
        Token char_literal561=null;
        Token string_literal562=null;
        Token char_literal563=null;
        Token string_literal564=null;
        Token char_literal566=null;
        Token string_literal567=null;
        Token char_literal569=null;
        Token char_literal571=null;
        JavaParser.arguments_return arguments560 = null;

        JavaParser.superSuffix_return superSuffix565 = null;

        JavaParser.innerCreator_return innerCreator568 = null;

        JavaParser.expression_return expression570 = null;


        Object char_literal558_tree=null;
        Object Identifier559_tree=null;
        Object char_literal561_tree=null;
        Object string_literal562_tree=null;
        Object char_literal563_tree=null;
        Object string_literal564_tree=null;
        Object char_literal566_tree=null;
        Object string_literal567_tree=null;
        Object char_literal569_tree=null;
        Object char_literal571_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1017:5: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1017:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal558=(Token)match(input,29,FOLLOW_29_in_selector5793); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal558_tree = (Object)adaptor.create(char_literal558);
                    adaptor.addChild(root_0, char_literal558_tree);
                    }
                    Identifier559=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector5795); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier559_tree = (Object)adaptor.create(Identifier559);
                    adaptor.addChild(root_0, Identifier559_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1017:24: ( arguments )?
                    int alt166=2;
                    int LA166_0 = input.LA(1);

                    if ( (LA166_0==66) ) {
                        alt166=1;
                    }
                    switch (alt166) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector5797);
                            arguments560=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments560.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1018:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal561=(Token)match(input,29,FOLLOW_29_in_selector5808); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal561_tree = (Object)adaptor.create(char_literal561);
                    adaptor.addChild(root_0, char_literal561_tree);
                    }
                    string_literal562=(Token)match(input,69,FOLLOW_69_in_selector5810); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal562_tree = (Object)adaptor.create(string_literal562);
                    adaptor.addChild(root_0, string_literal562_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1019:9: '.' 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal563=(Token)match(input,29,FOLLOW_29_in_selector5820); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal563_tree = (Object)adaptor.create(char_literal563);
                    adaptor.addChild(root_0, char_literal563_tree);
                    }
                    string_literal564=(Token)match(input,65,FOLLOW_65_in_selector5822); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal564_tree = (Object)adaptor.create(string_literal564);
                    adaptor.addChild(root_0, string_literal564_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_selector5824);
                    superSuffix565=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix565.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1020:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal566=(Token)match(input,29,FOLLOW_29_in_selector5834); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal566_tree = (Object)adaptor.create(char_literal566);
                    adaptor.addChild(root_0, char_literal566_tree);
                    }
                    string_literal567=(Token)match(input,113,FOLLOW_113_in_selector5836); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal567_tree = (Object)adaptor.create(string_literal567);
                    adaptor.addChild(root_0, string_literal567_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_selector5838);
                    innerCreator568=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator568.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1021:9: '[' expression ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal569=(Token)match(input,48,FOLLOW_48_in_selector5848); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal569_tree = (Object)adaptor.create(char_literal569);
                    adaptor.addChild(root_0, char_literal569_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector5850);
                    expression570=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression570.getTree());
                    char_literal571=(Token)match(input,49,FOLLOW_49_in_selector5852); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal571_tree = (Object)adaptor.create(char_literal571);
                    adaptor.addChild(root_0, char_literal571_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 133, selector_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "selector"

    public static class superSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "superSuffix"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1024:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final JavaParser.superSuffix_return superSuffix() throws RecognitionException {
        JavaParser.superSuffix_return retval = new JavaParser.superSuffix_return();
        retval.start = input.LT(1);
        int superSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal573=null;
        Token Identifier574=null;
        JavaParser.arguments_return arguments572 = null;

        JavaParser.arguments_return arguments575 = null;


        Object char_literal573_tree=null;
        Object Identifier574_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1025:5: ( arguments | '.' Identifier ( arguments )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1025:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix5875);
                    arguments572=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments572.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal573=(Token)match(input,29,FOLLOW_29_in_superSuffix5885); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal573_tree = (Object)adaptor.create(char_literal573);
                    adaptor.addChild(root_0, char_literal573_tree);
                    }
                    Identifier574=(Token)match(input,Identifier,FOLLOW_Identifier_in_superSuffix5887); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier574_tree = (Object)adaptor.create(Identifier574);
                    adaptor.addChild(root_0, Identifier574_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1026:24: ( arguments )?
                    int alt168=2;
                    int LA168_0 = input.LA(1);

                    if ( (LA168_0==66) ) {
                        alt168=1;
                    }
                    switch (alt168) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix5889);
                            arguments575=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments575.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 134, superSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "superSuffix"

    public static class arguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arguments"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1029:1: arguments : '(' ( expressionList )? ')' ;
    public final JavaParser.arguments_return arguments() throws RecognitionException {
        JavaParser.arguments_return retval = new JavaParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal576=null;
        Token char_literal578=null;
        JavaParser.expressionList_return expressionList577 = null;


        Object char_literal576_tree=null;
        Object char_literal578_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:5: ( '(' ( expressionList )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:9: '(' ( expressionList )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal576=(Token)match(input,66,FOLLOW_66_in_arguments5909); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal576_tree = (Object)adaptor.create(char_literal576);
            adaptor.addChild(root_0, char_literal576_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1030:13: ( expressionList )?
            int alt170=2;
            int LA170_0 = input.LA(1);

            if ( (LA170_0==Identifier||(LA170_0>=FloatingPointLiteral && LA170_0<=DecimalLiteral)||LA170_0==47||(LA170_0>=56 && LA170_0<=63)||(LA170_0>=65 && LA170_0<=66)||(LA170_0>=69 && LA170_0<=72)||(LA170_0>=105 && LA170_0<=106)||(LA170_0>=109 && LA170_0<=113)) ) {
                alt170=1;
            }
            switch (alt170) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments5911);
                    expressionList577=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList577.getTree());

                    }
                    break;

            }

            char_literal578=(Token)match(input,67,FOLLOW_67_in_arguments5914); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal578_tree = (Object)adaptor.create(char_literal578);
            adaptor.addChild(root_0, char_literal578_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 135, arguments_StartIndex); }
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


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:285:10: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:285:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
        {
        pushFollow(FOLLOW_annotations_in_synpred5_Java89);
        annotations();

        state._fsp--;
        if (state.failed) return ;
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
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
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_packageDeclaration_in_synpred5_Java105);
                np=packageDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:90: (imp= importDeclaration )*
                loop173:
                do {
                    int alt173=2;
                    int LA173_0 = input.LA(1);

                    if ( (LA173_0==27) ) {
                        alt173=1;
                    }


                    switch (alt173) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:91: imp= importDeclaration
                	    {
                	    pushFollow(FOLLOW_importDeclaration_in_synpred5_Java111);
                	    imp=importDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop173;
                    }
                } while (true);

                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:159: (typech= typeDeclaration )*
                loop174:
                do {
                    int alt174=2;
                    int LA174_0 = input.LA(1);

                    if ( (LA174_0==ENUM||LA174_0==26||LA174_0==28||(LA174_0>=31 && LA174_0<=37)||LA174_0==46||LA174_0==73) ) {
                        alt174=1;
                    }


                    switch (alt174) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:286:160: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java119);
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
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:287:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java138);
                cd=classOrInterfaceDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:287:85: (typech= typeDeclaration )*
                loop175:
                do {
                    int alt175=2;
                    int LA175_0 = input.LA(1);

                    if ( (LA175_0==ENUM||LA175_0==26||LA175_0==28||(LA175_0>=31 && LA175_0<=37)||LA175_0==46||LA175_0==73) ) {
                        alt175=1;
                    }


                    switch (alt175) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:287:86: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java144);
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

    // $ANTLR start synpred81_Java
    public final void synpred81_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred81_Java2102);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred81_Java

    // $ANTLR start synpred82_Java
    public final void synpred82_Java_fragment() throws RecognitionException {   
        JavaParser.classOrInterfaceModifier_return mod = null;


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:9: (mod= classOrInterfaceModifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:9: mod= classOrInterfaceModifier
        {
        pushFollow(FOLLOW_classOrInterfaceModifier_in_synpred82_Java2114);
        mod=classOrInterfaceModifier();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred82_Java

    // $ANTLR start synpred108_Java
    public final void synpred108_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:13: ( explicitConstructorInvocation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:13: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred108_Java2645);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred108_Java

    // $ANTLR start synpred112_Java
    public final void synpred112_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:622:9: ( nonWildcardTypeArguments )?
        int alt184=2;
        int LA184_0 = input.LA(1);

        if ( (LA184_0==40) ) {
            alt184=1;
        }
        switch (alt184) {
            case 1 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred112_Java2670);
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

        pushFollow(FOLLOW_arguments_in_synpred112_Java2681);
        arguments();

        state._fsp--;
        if (state.failed) return ;
        match(input,26,FOLLOW_26_in_synpred112_Java2683); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred112_Java

    // $ANTLR start synpred123_Java
    public final void synpred123_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred123_Java2894);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred123_Java

    // $ANTLR start synpred146_Java
    public final void synpred146_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:727:9: ( localVariableDeclarationStatement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:727:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred146_Java3421);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred146_Java

    // $ANTLR start synpred147_Java
    public final void synpred147_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:728:9: ( classOrInterfaceDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:728:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred147_Java3431);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred147_Java

    // $ANTLR start synpred152_Java
    public final void synpred152_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:747:54: ( 'else' statement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:747:54: 'else' statement
        {
        match(input,77,FOLLOW_77_in_synpred152_Java3576); if (state.failed) return ;
        pushFollow(FOLLOW_statement_in_synpred152_Java3578);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred152_Java

    // $ANTLR start synpred157_Java
    public final void synpred157_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:752:11: ( catches 'finally' block )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:752:11: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred157_Java3654);
        catches();

        state._fsp--;
        if (state.failed) return ;
        match(input,82,FOLLOW_82_in_synpred157_Java3656); if (state.failed) return ;
        pushFollow(FOLLOW_block_in_synpred157_Java3658);
        block();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred157_Java

    // $ANTLR start synpred158_Java
    public final void synpred158_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:753:11: ( catches )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:753:11: catches
        {
        pushFollow(FOLLOW_catches_in_synpred158_Java3670);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred158_Java

    // $ANTLR start synpred173_Java
    public final void synpred173_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: ( switchLabel )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: switchLabel
        {
        pushFollow(FOLLOW_switchLabel_in_synpred173_Java3961);
        switchLabel();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred173_Java

    // $ANTLR start synpred175_Java
    public final void synpred175_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:9: ( 'case' constantExpression ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:9: 'case' constantExpression ':'
        {
        match(input,89,FOLLOW_89_in_synpred175_Java3988); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred175_Java3990);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred175_Java3992); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred175_Java

    // $ANTLR start synpred176_Java
    public final void synpred176_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:793:9: ( 'case' enumConstantName ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:793:9: 'case' enumConstantName ':'
        {
        match(input,89,FOLLOW_89_in_synpred176_Java4002); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred176_Java4004);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred176_Java4006); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred176_Java

    // $ANTLR start synpred177_Java
    public final void synpred177_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:799:9: ( enhancedForControl )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:799:9: enhancedForControl
        {
        pushFollow(FOLLOW_enhancedForControl_in_synpred177_Java4049);
        enhancedForControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred177_Java

    // $ANTLR start synpred181_Java
    public final void synpred181_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:9: ( localVariableDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:804:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred181_Java4089);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred181_Java

    // $ANTLR start synpred183_Java
    public final void synpred183_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:32: ( assignmentOperator expression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:835:32: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred183_Java4272);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred183_Java4274);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred183_Java

    // $ANTLR start synpred193_Java
    public final void synpred193_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:848:9: ( '<' '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:848:10: '<' '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred193_Java4390); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred193_Java4392); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred193_Java4394); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred193_Java

    // $ANTLR start synpred194_Java
    public final void synpred194_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:9: ( '>' '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:853:10: '>' '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred194_Java4430); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred194_Java4432); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred194_Java4434); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred194_Java4436); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred194_Java

    // $ANTLR start synpred195_Java
    public final void synpred195_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:9: ( '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:10: '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred195_Java4475); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred195_Java4477); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred195_Java4479); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred195_Java

    // $ANTLR start synpred206_Java
    public final void synpred206_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:904:9: ( '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:904:10: '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred206_Java4787); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred206_Java4789); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred206_Java

    // $ANTLR start synpred207_Java
    public final void synpred207_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:9: ( '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:10: '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred207_Java4821); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred207_Java4823); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred207_Java

    // $ANTLR start synpred210_Java
    public final void synpred210_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:9: ( '<' '<' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:10: '<' '<'
        {
        match(input,40,FOLLOW_40_in_synpred210_Java4914); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred210_Java4916); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred210_Java

    // $ANTLR start synpred211_Java
    public final void synpred211_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:922:9: ( '>' '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:922:10: '>' '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred211_Java4948); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred211_Java4950); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred211_Java4952); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred211_Java

    // $ANTLR start synpred212_Java
    public final void synpred212_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:9: ( '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:927:10: '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred212_Java4988); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred212_Java4990); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred212_Java

    // $ANTLR start synpred224_Java
    public final void synpred224_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:9: ( castExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:952:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred224_Java5199);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred224_Java

    // $ANTLR start synpred228_Java
    public final void synpred228_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:957:8: ( '(' primitiveType ')' unaryExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:957:8: '(' primitiveType ')' unaryExpression
        {
        match(input,66,FOLLOW_66_in_synpred228_Java5237); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred228_Java5239);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,67,FOLLOW_67_in_synpred228_Java5241); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred228_Java5243);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred228_Java

    // $ANTLR start synpred229_Java
    public final void synpred229_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:13: ( type )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:13: type
        {
        pushFollow(FOLLOW_type_in_synpred229_Java5255);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred229_Java

    // $ANTLR start synpred231_Java
    public final void synpred231_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:17: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:17: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred231_Java5296); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred231_Java5298); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred231_Java

    // $ANTLR start synpred232_Java
    public final void synpred232_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:34: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:34: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred232_Java5302);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred232_Java

    // $ANTLR start synpred237_Java
    public final void synpred237_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:21: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:21: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred237_Java5350); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred237_Java5352); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred237_Java

    // $ANTLR start synpred238_Java
    public final void synpred238_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:38: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:967:38: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred238_Java5356);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred238_Java

    // $ANTLR start synpred244_Java
    public final void synpred244_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:974:10: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:974:10: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred244_Java5431); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred244_Java5433);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred244_Java5435); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred244_Java

    // $ANTLR start synpred257_Java
    public final void synpred257_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:29: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:1000:29: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred257_Java5671); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred257_Java5673);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred257_Java5675); if (state.failed) return ;

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
            return "283:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );";
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
            return "618:13: ( explicitConstructorInvocation )?";
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
            return "621:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );";
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
            return "726:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );";
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
            return "744:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );";
        }
    }
    static final String DFA123_eotS =
        "\u0087\uffff";
    static final String DFA123_eofS =
        "\u0087\uffff";
    static final String DFA123_minS =
        "\5\4\22\uffff\10\4\1\32\30\uffff\1\61\1\32\1\uffff\21\0\2\uffff"+
        "\3\0\21\uffff\1\0\5\uffff\1\0\30\uffff\1\0\5\uffff";
    static final String DFA123_maxS =
        "\1\161\1\111\1\4\1\156\1\60\22\uffff\2\60\1\111\1\4\1\111\3\161"+
        "\1\113\30\uffff\1\61\1\113\1\uffff\21\0\2\uffff\3\0\21\uffff\1\0"+
        "\5\uffff\1\0\30\uffff\1\0\5\uffff";
    static final String DFA123_acceptS =
        "\5\uffff\1\2\166\uffff\1\1\12\uffff";
    static final String DFA123_specialS =
        "\73\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\2\uffff\1\21\1\22\1\23\21\uffff\1\24\5\uffff"+
        "\1\25\30\uffff\1\26\5\uffff}>";
    static final String[] DFA123_transitionS = {
            "\1\3\1\uffff\6\5\16\uffff\1\5\10\uffff\1\1\13\uffff\1\5\10\uffff"+
            "\10\4\1\uffff\2\5\2\uffff\4\5\1\2\37\uffff\2\5\2\uffff\5\5",
            "\1\27\36\uffff\1\31\24\uffff\10\30\11\uffff\1\32",
            "\1\33",
            "\1\37\25\uffff\1\5\2\uffff\1\35\1\5\11\uffff\1\34\3\5\4\uffff"+
            "\1\36\2\uffff\1\5\14\uffff\1\5\1\uffff\1\5\27\uffff\25\5",
            "\1\71\30\uffff\1\5\22\uffff\1\70",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "\1\116\1\uffff\6\5\34\uffff\1\5\6\uffff\1\5\3\uffff\1\5\4\uffff"+
            "\10\117\1\120\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "\1\142\40\uffff\1\5\2\uffff\1\5\30\uffff\1\5\3\uffff\1\5\53"+
            "\uffff\1\5",
            "\1\5\1\uffff\6\5\43\uffff\1\5\1\uffff\1\150\6\uffff\10\5\1"+
            "\uffff\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "\1\5\16\uffff\1\5\6\uffff\1\5\2\uffff\1\5\27\uffff\1\174",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u0081",
            "\1\5\16\uffff\1\5\6\uffff\1\5\2\uffff\1\5\27\uffff\1\174",
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
            return "797:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
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
                        int LA123_78 = input.LA(1);

                         
                        int index123_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_78);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA123_79 = input.LA(1);

                         
                        int index123_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_79);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA123_80 = input.LA(1);

                         
                        int index123_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_80);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA123_98 = input.LA(1);

                         
                        int index123_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_98);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA123_104 = input.LA(1);

                         
                        int index123_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_104);
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
            return "803:1: forInit : ( localVariableDeclaration | expressionList );";
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
        "\1\uffff\1\4\1\0\1\10\1\3\1\5\1\1\1\11\1\2\1\12\1\6\1\7\2\uffff}>";
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
            return "835:31: ( assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA126_2 = input.LA(1);

                         
                        int index126_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA126_6 = input.LA(1);

                         
                        int index126_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA126_8 = input.LA(1);

                         
                        int index126_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_8);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA126_4 = input.LA(1);

                         
                        int index126_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA126_1 = input.LA(1);

                         
                        int index126_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_1);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA126_5 = input.LA(1);

                         
                        int index126_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA126_10 = input.LA(1);

                         
                        int index126_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_10);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA126_11 = input.LA(1);

                         
                        int index126_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_11);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA126_3 = input.LA(1);

                         
                        int index126_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_3);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA126_7 = input.LA(1);

                         
                        int index126_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_7);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA126_9 = input.LA(1);

                         
                        int index126_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred183_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_9);
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
            return "838:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);";
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
        "\1\0\2\uffff\1\1\24\uffff}>";
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
            return "918:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA139_0 = input.LA(1);

                         
                        int index139_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA139_0==40) && (synpred210_Java())) {s = 1;}

                        else if ( (LA139_0==42) ) {s = 2;}

                         
                        input.seek(index139_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
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
            return "949:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );";
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
            return "958:12: ( type | expression )";
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
            return "963:34: ( identifierSuffix )?";
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
            return "967:38: ( identifierSuffix )?";
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
            return "972:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );";
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
            return "()+ loopback of 974:9: ( '[' expression ']' )+";
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
            return "()* loopback of 1000:28: ( '[' expression ']' )*";
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
 

    public static final BitSet FOLLOW_annotations_in_compilationUnit89 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit105 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit111 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit119 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_compilationUnit138 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit144 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit170 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit178 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit186 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_25_in_packageDeclaration212 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration216 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_packageDeclaration218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_importDeclaration256 = new BitSet(new long[]{0x0000000010000010L});
    public static final BitSet FOLLOW_28_in_importDeclaration260 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_importDeclaration265 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration270 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_importDeclaration272 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_importDeclaration276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_typeDeclaration324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration354 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers397 = new BitSet(new long[]{0x0000001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotation_in_classOrInterfaceModifier424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_classOrInterfaceModifier437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_classOrInterfaceModifier453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_classOrInterfaceModifier466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_classOrInterfaceModifier481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classOrInterfaceModifier495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_classOrInterfaceModifier511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_classOrInterfaceModifier528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_modifiers563 = new BitSet(new long[]{0x00F0001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_normalClassDeclaration634 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration638 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration645 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_38_in_normalClassDeclaration659 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration663 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_39_in_normalClassDeclaration677 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration681 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeParameters726 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters730 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeParameters734 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters738 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeParameters743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter768 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_38_in_typeParameter771 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound802 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_typeBound805 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeBound807 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration832 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration834 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_39_in_enumDeclaration837 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration839 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_enumBody862 = new BitSet(new long[]{0x0000220004000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody864 = new BitSet(new long[]{0x0000220004000000L});
    public static final BitSet FOLLOW_41_in_enumBody867 = new BitSet(new long[]{0x0000200004000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody870 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_enumBody873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants892 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_enumConstants895 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants897 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant922 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant925 = new BitSet(new long[]{0x000011C000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_enumConstant927 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_enumBodyDeclarations954 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations957 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_normalInterfaceDeclaration1015 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration1017 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1019 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_38_in_normalInterfaceDeclaration1023 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1025 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1052 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_typeList1055 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeList1057 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_44_in_classBody1082 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1084 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_classBody1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_interfaceBody1110 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1112 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_interfaceBody1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_classBodyDeclaration1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classBodyDeclaration1144 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classBodyDeclaration1157 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl1182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDeclaration_in_memberDecl1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_memberDecl1202 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl1204 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl1206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl1216 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_memberDeclaration1261 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDeclaration1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDeclaration1268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1288 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest1314 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_genericMethodOrConstructorRest1318 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1321 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1333 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration1354 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration1356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration1375 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_fieldDeclaration1377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceBodyDeclaration1404 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_interfaceBodyDeclaration1416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_interfaceMemberDecl1455 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl1457 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl1459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl1479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl1502 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl1504 = new BitSet(new long[]{0x0009000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest1529 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodOrFieldRest1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest1564 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_48_in_methodDeclaratorRest1567 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_methodDeclaratorRest1569 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_50_in_methodDeclaratorRest1582 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest1584 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest1600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_methodDeclaratorRest1614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest1647 = new BitSet(new long[]{0x0004100014000000L});
    public static final BitSet FOLLOW_50_in_voidMethodDeclaratorRest1650 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1652 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest1668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_voidMethodDeclaratorRest1682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1715 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_48_in_interfaceMethodDeclaratorRest1718 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_interfaceMethodDeclaratorRest1720 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_50_in_interfaceMethodDeclaratorRest1725 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1727 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodDeclaratorRest1731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl1754 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl1757 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_interfaceGenericMethodDecl1761 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl1764 = new BitSet(new long[]{0x0009000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1797 = new BitSet(new long[]{0x0004000004000000L});
    public static final BitSet FOLLOW_50_in_voidInterfaceMethodDeclaratorRest1800 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1802 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_voidInterfaceMethodDeclaratorRest1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest1829 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_50_in_constructorDeclaratorRest1832 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1834 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_constructorBody_in_constructorDeclaratorRest1838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator1857 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator1859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1882 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclarators1885 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1887 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_variableDeclarator1908 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_variableDeclarator1911 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator1913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1938 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_constantDeclaratorsRest1941 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1943 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_48_in_constantDeclaratorRest1965 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_constantDeclaratorRest1967 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_51_in_constantDeclaratorRest1971 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1996 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_variableDeclaratorId1999 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_variableDeclaratorId2001 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_arrayInitializer2059 = new BitSet(new long[]{0xFF00B00000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2062 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer2065 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2067 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer2072 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_arrayInitializer2079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier2102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_modifier2114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier2126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier2138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier2150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_modifier2162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_packageOrTypeName2183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName2202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_typeName2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2243 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2246 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2248 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type2255 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2258 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2260 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2273 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2275 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_classOrInterfaceType2279 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2281 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2283 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_variableModifier2392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier2402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeArguments2421 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2423 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeArguments2426 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2428 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeArguments2432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument2455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_typeArgument2465 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument2468 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument2476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2501 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_qualifiedNameList2504 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2506 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_66_in_formalParameters2527 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000208L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters2529 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_formalParameters2532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameterDecls2555 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls2557 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000010L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2582 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_formalParameterDeclsRest2585 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_formalParameterDeclsRest2599 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody2624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_constructorBody2643 = new BitSet(new long[]{0xFF20F13F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody2645 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_constructorBody2648 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_constructorBody2651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2670 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation2673 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation2681 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation2683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation2693 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_explicitConstructorInvocation2695 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2697 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_explicitConstructorInvocation2700 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation2702 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation2704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2724 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_qualifiedName2727 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2729 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal2755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal2765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal2775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal2785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal2795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_literal2805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations2894 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_annotation2914 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation2916 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation2920 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation2924 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_elementValue_in_annotation2928 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotation2933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2957 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_annotationName2960 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2962 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2983 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_elementValuePairs2986 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2988 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair3009 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_elementValuePair3011 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue3056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_elementValueArrayInitializer3079 = new BitSet(new long[]{0xFF00B20000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3082 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer3085 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3087 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer3094 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_elementValueArrayInitializer3098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_annotationTypeDeclaration3121 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_annotationTypeDeclaration3123 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration3125 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_annotationTypeBody3150 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3153 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_annotationTypeBody3157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeElementDeclaration3180 = new BitSet(new long[]{0xFF00403F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest3205 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3207 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3219 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3232 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest3245 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3258 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest3317 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest3319 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotationMethodRest3321 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest3323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest3347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_defaultValue3370 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue3372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_block3393 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_block3395 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_block3398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement3421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement3431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement3441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement3465 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_localVariableDeclarationStatement3467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration3486 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration3488 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration3490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_variableModifiers3513 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_block_in_statement3531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement3541 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3543 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement3546 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3548 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_statement3562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3564 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3566 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_statement3576 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement3590 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement3592 = new BitSet(new long[]{0xFF00900804000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forControl_in_statement3594 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_statement3596 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement3608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3610 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement3622 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3624 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement3626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3628 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement3640 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3642 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_catches_in_statement3654 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_statement3656 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement3670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement3684 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement3706 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3708 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_statement3710 = new BitSet(new long[]{0x0000200000000000L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement3712 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_statement3714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_statement3724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3726 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement3738 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3740 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement3753 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3755 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement3767 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3769 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_statement3782 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3784 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_statement3797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement3808 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement3820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement3822 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches3847 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_catchClause_in_catches3850 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_catchClause3875 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause3877 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause3879 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_catchClause3881 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_catchClause3883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter3902 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameter3904 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter3906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups3934 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup3961 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60002FBD7E6L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup3964 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_89_in_switchLabel3988 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel3990 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_switchLabel4002 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel4004 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_switchLabel4016 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel4018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_forControl4049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl4059 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl4062 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_forControl4064 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl4067 = new BitSet(new long[]{0xFF00900800000FD2L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forUpdate_in_forControl4069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit4089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit4099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_enhancedForControl4122 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_enhancedForControl4124 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enhancedForControl4126 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_enhancedForControl4128 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_enhancedForControl4130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate4149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_parExpression4170 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_parExpression4172 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_parExpression4174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4197 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_expressionList4200 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expressionList4202 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression4223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression4246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression4269 = new BitSet(new long[]{0x0008050000000002L,0x00000003FC000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression4272 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expression4274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator4309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator4319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator4329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator4339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator4349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator4359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator4369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator4379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_assignmentOperator4400 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_assignmentOperator4404 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4442 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4446 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4450 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4485 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4489 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression4522 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_conditionalExpression4526 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4528 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_conditionalExpression4530 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4554 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalOrExpression4558 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4560 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4582 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_conditionalAndExpression4586 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4588 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4610 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_inclusiveOrExpression4614 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4616 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression4638 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_exclusiveOrExpression4642 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression4644 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression4666 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_andExpression4670 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression4672 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression4694 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression4698 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression4706 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression4728 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_instanceOfExpression4731 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression4733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression4754 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression4758 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression4760 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp4795 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp4799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp4829 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp4833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp4854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp4865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression4885 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression4889 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression4891 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_shiftOp4922 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_shiftOp4926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp4958 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4962 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp4996 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp5000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5030 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression5034 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5042 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5064 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression5068 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5082 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_105_in_unaryExpression5108 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_unaryExpression5120 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression5132 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpression5144 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus5175 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_unaryExpressionNotPlusMinus5187 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus5209 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus5211 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus5214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5237 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression5239 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5241 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression5243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5252 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_type_in_castExpression5255 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_castExpression5259 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5262 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary5283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_primary5293 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5296 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5298 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_primary5313 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_primary5315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary5325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary5335 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_creator_in_primary5337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary5347 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5350 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5352 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary5367 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_48_in_primary5370 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_primary5372 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_primary5376 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary5378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_primary5388 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_primary5390 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary5392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix5412 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix5414 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5418 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix5420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix5431 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix5433 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix5435 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5458 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix5460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5470 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix5472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5482 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_identifierSuffix5484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5494 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_identifierSuffix5496 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5508 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix5510 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix5512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator5531 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_createdName_in_creator5533 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createdName_in_creator5545 = new BitSet(new long[]{0x0001000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator5548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName5572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName5582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator5605 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator5608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator5610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5629 = new BitSet(new long[]{0xFF02900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5643 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5646 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5648 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest5652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest5666 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5668 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5671 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest5673 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5675 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5680 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5682 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest5713 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest5715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation5739 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocation5741 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation5743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_nonWildcardTypeArguments5766 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments5768 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_nonWildcardTypeArguments5770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5793 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector5795 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_selector5797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5808 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_selector5810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_selector5822 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_selector5824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5834 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector5836 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector5838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_selector5848 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_selector5850 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_selector5852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix5875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_superSuffix5885 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix5887 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_superSuffix5889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_arguments5909 = new BitSet(new long[]{0xFF00900800000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_expressionList_in_arguments5911 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_arguments5914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred5_Java89 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred5_Java105 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_synpred5_Java111 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java119 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java138 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java144 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotation_in_synpred81_Java2102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_synpred82_Java2114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred108_Java2645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred112_Java2670 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_synpred112_Java2673 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_synpred112_Java2681 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_synpred112_Java2683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred123_Java2894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred146_Java3421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred147_Java3431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_synpred152_Java3576 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_synpred152_Java3578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred157_Java3654 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_synpred157_Java3656 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_synpred157_Java3658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred158_Java3670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchLabel_in_synpred173_Java3961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred175_Java3988 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_synpred175_Java3990 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred175_Java3992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred176_Java4002 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred176_Java4004 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred176_Java4006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_synpred177_Java4049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred181_Java4089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred183_Java4272 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred183_Java4274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred193_Java4390 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred193_Java4392 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred193_Java4394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred194_Java4430 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred194_Java4432 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred194_Java4434 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred194_Java4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred195_Java4475 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred195_Java4477 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred195_Java4479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred206_Java4787 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred206_Java4789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred207_Java4821 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred207_Java4823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred210_Java4914 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred210_Java4916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred211_Java4948 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred211_Java4950 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred211_Java4952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred212_Java4988 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred212_Java4990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred224_Java5199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_synpred228_Java5237 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred228_Java5239 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_synpred228_Java5241 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred228_Java5243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred229_Java5255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred231_Java5296 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred231_Java5298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred232_Java5302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred237_Java5350 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred237_Java5352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred238_Java5356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred244_Java5431 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred244_Java5433 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred244_Java5435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred257_Java5671 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred257_Java5673 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred257_Java5675 = new BitSet(new long[]{0x0000000000000002L});

}