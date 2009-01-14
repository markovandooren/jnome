// $ANTLR 3.1.1 /Users/marko/git/jnome/src/jnome/input/parser/Java.g 2009-01-14 17:48:00

package jnome.input.parser;

import chameleon.core.MetamodelException;

import chameleon.core.element.ChameleonProgrammerException;

import chameleon.core.compilationunit.CompilationUnit;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;

import chameleon.core.context.ContextFactory;

import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.Import;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.namespacepart.DemandImport;

import chameleon.core.language.Language;

import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

import jnome.core.language.Java;

import jnome.core.type.JavaTypeReference;



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
            this.state.ruleMemo = new HashMap[407+1];
             
             
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:247:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:249:5: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:249:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotations_in_compilationUnit84);
                    annotations1=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit100);
                            np=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, np.getTree());
                            if ( state.backtracking==0 ) {
                              processPackageDeclaration(retval.element,np.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:90: (imp= importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==27) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:91: imp= importDeclaration
                            	    {
                            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit106);
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

                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:159: (typech= typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==ENUM||LA2_0==26||LA2_0==28||(LA2_0>=31 && LA2_0<=37)||LA2_0==46||LA2_0==73) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:160: typech= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit114);
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:251:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit133);
                            cd=classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, cd.getTree());
                            if ( state.backtracking==0 ) {
                              processType(retval.element,cd.element);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:251:85: (typech= typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==ENUM||LA3_0==26||LA3_0==28||(LA3_0>=31 && LA3_0<=37)||LA3_0==46||LA3_0==73) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:251:86: typech= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit139);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:9: (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )*
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:9: (np= packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==25) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:10: np= packageDeclaration
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit165);
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

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:89: (imp= importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==27) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:90: imp= importDeclaration
                    	    {
                    	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit173);
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

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:158: (typech= typeDeclaration )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ENUM||LA7_0==26||LA7_0==28||(LA7_0>=31 && LA7_0<=37)||LA7_0==46||LA7_0==73) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:253:159: typech= typeDeclaration
                    	    {
                    	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit181);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:256:1: packageDeclaration returns [NamespacePart element] : 'package' qualifiedName ';' ;
    public final JavaParser.packageDeclaration_return packageDeclaration() throws RecognitionException {
        JavaParser.packageDeclaration_return retval = new JavaParser.packageDeclaration_return();
        retval.start = input.LT(1);
        int packageDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal2=null;
        Token char_literal4=null;
        JavaParser.qualifiedName_return qualifiedName3 = null;


        Object string_literal2_tree=null;
        Object char_literal4_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:257:5: ( 'package' qualifiedName ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:257:9: 'package' qualifiedName ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal2=(Token)match(input,25,FOLLOW_25_in_packageDeclaration207); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal2_tree = (Object)adaptor.create(string_literal2);
            adaptor.addChild(root_0, string_literal2_tree);
            }
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration209);
            qualifiedName3=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName3.getTree());
            char_literal4=(Token)match(input,26,FOLLOW_26_in_packageDeclaration211); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal4_tree = (Object)adaptor.create(char_literal4);
            adaptor.addChild(root_0, char_literal4_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:260:1: importDeclaration returns [Import element] : 'import' ( 'static' )? qualifiedName ( '.' '*' )? ';' ;
    public final JavaParser.importDeclaration_return importDeclaration() throws RecognitionException {
        JavaParser.importDeclaration_return retval = new JavaParser.importDeclaration_return();
        retval.start = input.LT(1);
        int importDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal5=null;
        Token string_literal6=null;
        Token char_literal8=null;
        Token char_literal9=null;
        Token char_literal10=null;
        JavaParser.qualifiedName_return qualifiedName7 = null;


        Object string_literal5_tree=null;
        Object string_literal6_tree=null;
        Object char_literal8_tree=null;
        Object char_literal9_tree=null;
        Object char_literal10_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:261:5: ( 'import' ( 'static' )? qualifiedName ( '.' '*' )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:261:9: 'import' ( 'static' )? qualifiedName ( '.' '*' )? ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal5=(Token)match(input,27,FOLLOW_27_in_importDeclaration238); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal5_tree = (Object)adaptor.create(string_literal5);
            adaptor.addChild(root_0, string_literal5_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:261:18: ( 'static' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==28) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: 'static'
                    {
                    string_literal6=(Token)match(input,28,FOLLOW_28_in_importDeclaration240); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal6_tree = (Object)adaptor.create(string_literal6);
                    adaptor.addChild(root_0, string_literal6_tree);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_qualifiedName_in_importDeclaration243);
            qualifiedName7=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName7.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:261:42: ( '.' '*' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==29) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:261:43: '.' '*'
                    {
                    char_literal8=(Token)match(input,29,FOLLOW_29_in_importDeclaration246); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal8_tree = (Object)adaptor.create(char_literal8);
                    adaptor.addChild(root_0, char_literal8_tree);
                    }
                    char_literal9=(Token)match(input,30,FOLLOW_30_in_importDeclaration248); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal9_tree = (Object)adaptor.create(char_literal9);
                    adaptor.addChild(root_0, char_literal9_tree);
                    }

                    }
                    break;

            }

            char_literal10=(Token)match(input,26,FOLLOW_26_in_importDeclaration252); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal10_tree = (Object)adaptor.create(char_literal10);
            adaptor.addChild(root_0, char_literal10_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:264:1: typeDeclaration returns [Type element] : ( classOrInterfaceDeclaration | ';' );
    public final JavaParser.typeDeclaration_return typeDeclaration() throws RecognitionException {
        JavaParser.typeDeclaration_return retval = new JavaParser.typeDeclaration_return();
        retval.start = input.LT(1);
        int typeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal12=null;
        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration11 = null;


        Object char_literal12_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:265:5: ( classOrInterfaceDeclaration | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:265:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration279);
                    classOrInterfaceDeclaration11=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration11.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:266:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal12=(Token)match(input,26,FOLLOW_26_in_typeDeclaration289); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal12_tree = (Object)adaptor.create(char_literal12);
                    adaptor.addChild(root_0, char_literal12_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:269:1: classOrInterfaceDeclaration returns [Type element] : classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration ) ;
    public final JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        JavaParser.classOrInterfaceDeclaration_return retval = new JavaParser.classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int classOrInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifiers_return classOrInterfaceModifiers13 = null;

        JavaParser.classDeclaration_return classDeclaration14 = null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration15 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:270:5: ( classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:270:9: classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration316);
            classOrInterfaceModifiers13=classOrInterfaceModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceModifiers13.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:270:35: ( classDeclaration | interfaceDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:270:36: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration319);
                    classDeclaration14=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration14.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:270:55: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration323);
                    interfaceDeclaration15=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration15.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceModifiers"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:273:1: classOrInterfaceModifiers : ( classOrInterfaceModifier )* ;
    public final JavaParser.classOrInterfaceModifiers_return classOrInterfaceModifiers() throws RecognitionException {
        JavaParser.classOrInterfaceModifiers_return retval = new JavaParser.classOrInterfaceModifiers_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceModifier_return classOrInterfaceModifier16 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:274:5: ( ( classOrInterfaceModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:274:9: ( classOrInterfaceModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:274:9: ( classOrInterfaceModifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classOrInterfaceModifier
            	    {
            	    pushFollow(FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers347);
            	    classOrInterfaceModifier16=classOrInterfaceModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceModifier16.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classOrInterfaceModifier"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:277:1: classOrInterfaceModifier : ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' );
    public final JavaParser.classOrInterfaceModifier_return classOrInterfaceModifier() throws RecognitionException {
        JavaParser.classOrInterfaceModifier_return retval = new JavaParser.classOrInterfaceModifier_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal18=null;
        Token string_literal19=null;
        Token string_literal20=null;
        Token string_literal21=null;
        Token string_literal22=null;
        Token string_literal23=null;
        Token string_literal24=null;
        JavaParser.annotation_return annotation17 = null;


        Object string_literal18_tree=null;
        Object string_literal19_tree=null;
        Object string_literal20_tree=null;
        Object string_literal21_tree=null;
        Object string_literal22_tree=null;
        Object string_literal23_tree=null;
        Object string_literal24_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:278:5: ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:278:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_classOrInterfaceModifier367);
                    annotation17=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation17.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:279:9: 'public'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal18=(Token)match(input,31,FOLLOW_31_in_classOrInterfaceModifier380); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal18_tree = (Object)adaptor.create(string_literal18);
                    adaptor.addChild(root_0, string_literal18_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:280:9: 'protected'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal19=(Token)match(input,32,FOLLOW_32_in_classOrInterfaceModifier395); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal19_tree = (Object)adaptor.create(string_literal19);
                    adaptor.addChild(root_0, string_literal19_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:281:9: 'private'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal20=(Token)match(input,33,FOLLOW_33_in_classOrInterfaceModifier407); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal20_tree = (Object)adaptor.create(string_literal20);
                    adaptor.addChild(root_0, string_literal20_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:282:9: 'abstract'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal21=(Token)match(input,34,FOLLOW_34_in_classOrInterfaceModifier421); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal21_tree = (Object)adaptor.create(string_literal21);
                    adaptor.addChild(root_0, string_literal21_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:283:9: 'static'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal22=(Token)match(input,28,FOLLOW_28_in_classOrInterfaceModifier434); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal22_tree = (Object)adaptor.create(string_literal22);
                    adaptor.addChild(root_0, string_literal22_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:284:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal23=(Token)match(input,35,FOLLOW_35_in_classOrInterfaceModifier449); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal23_tree = (Object)adaptor.create(string_literal23);
                    adaptor.addChild(root_0, string_literal23_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:285:9: 'strictfp'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal24=(Token)match(input,36,FOLLOW_36_in_classOrInterfaceModifier465); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal24_tree = (Object)adaptor.create(string_literal24);
                    adaptor.addChild(root_0, string_literal24_tree);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifiers"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:288:1: modifiers : ( modifier )* ;
    public final JavaParser.modifiers_return modifiers() throws RecognitionException {
        JavaParser.modifiers_return retval = new JavaParser.modifiers_return();
        retval.start = input.LT(1);
        int modifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifier_return modifier25 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:5: ( ( modifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:9: ( modifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:289:9: ( modifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_modifiers487);
            	    modifier25=modifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifier25.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:292:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final JavaParser.classDeclaration_return classDeclaration() throws RecognitionException {
        JavaParser.classDeclaration_return retval = new JavaParser.classDeclaration_return();
        retval.start = input.LT(1);
        int classDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration26 = null;

        JavaParser.enumDeclaration_return enumDeclaration27 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:293:5: ( normalClassDeclaration | enumDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:293:9: normalClassDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration507);
                    normalClassDeclaration26=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration26.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:294:9: enumDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration517);
                    enumDeclaration27=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration27.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "normalClassDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:297:1: normalClassDeclaration : 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final JavaParser.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        JavaParser.normalClassDeclaration_return retval = new JavaParser.normalClassDeclaration_return();
        retval.start = input.LT(1);
        int normalClassDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal28=null;
        Token Identifier29=null;
        Token string_literal31=null;
        Token string_literal33=null;
        JavaParser.typeParameters_return typeParameters30 = null;

        JavaParser.type_return type32 = null;

        JavaParser.typeList_return typeList34 = null;

        JavaParser.classBody_return classBody35 = null;


        Object string_literal28_tree=null;
        Object Identifier29_tree=null;
        Object string_literal31_tree=null;
        Object string_literal33_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:298:5: ( 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:298:9: 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            root_0 = (Object)adaptor.nil();

            string_literal28=(Token)match(input,37,FOLLOW_37_in_normalClassDeclaration540); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal28_tree = (Object)adaptor.create(string_literal28);
            adaptor.addChild(root_0, string_literal28_tree);
            }
            Identifier29=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration542); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier29_tree = (Object)adaptor.create(Identifier29);
            adaptor.addChild(root_0, Identifier29_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:298:28: ( typeParameters )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==40) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration544);
                    typeParameters30=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters30.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:299:9: ( 'extends' type )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==38) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:299:10: 'extends' type
                    {
                    string_literal31=(Token)match(input,38,FOLLOW_38_in_normalClassDeclaration556); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal31_tree = (Object)adaptor.create(string_literal31);
                    adaptor.addChild(root_0, string_literal31_tree);
                    }
                    pushFollow(FOLLOW_type_in_normalClassDeclaration558);
                    type32=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type32.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:300:9: ( 'implements' typeList )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==39) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:300:10: 'implements' typeList
                    {
                    string_literal33=(Token)match(input,39,FOLLOW_39_in_normalClassDeclaration571); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal33_tree = (Object)adaptor.create(string_literal33);
                    adaptor.addChild(root_0, string_literal33_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration573);
                    typeList34=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList34.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration585);
            classBody35=classBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody35.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeParameters"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:304:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final JavaParser.typeParameters_return typeParameters() throws RecognitionException {
        JavaParser.typeParameters_return retval = new JavaParser.typeParameters_return();
        retval.start = input.LT(1);
        int typeParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal36=null;
        Token char_literal38=null;
        Token char_literal40=null;
        JavaParser.typeParameter_return typeParameter37 = null;

        JavaParser.typeParameter_return typeParameter39 = null;


        Object char_literal36_tree=null;
        Object char_literal38_tree=null;
        Object char_literal40_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:9: '<' typeParameter ( ',' typeParameter )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal36=(Token)match(input,40,FOLLOW_40_in_typeParameters608); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal36_tree = (Object)adaptor.create(char_literal36);
            adaptor.addChild(root_0, char_literal36_tree);
            }
            pushFollow(FOLLOW_typeParameter_in_typeParameters610);
            typeParameter37=typeParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameter37.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:27: ( ',' typeParameter )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==41) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:305:28: ',' typeParameter
            	    {
            	    char_literal38=(Token)match(input,41,FOLLOW_41_in_typeParameters613); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal38_tree = (Object)adaptor.create(char_literal38);
            	    adaptor.addChild(root_0, char_literal38_tree);
            	    }
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters615);
            	    typeParameter39=typeParameter();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameter39.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            char_literal40=(Token)match(input,42,FOLLOW_42_in_typeParameters619); if (state.failed) return retval;
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
            if ( state.backtracking>0 ) { memoize(input, 11, typeParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameters"

    public static class typeParameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeParameter"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:308:1: typeParameter : Identifier ( 'extends' typeBound )? ;
    public final JavaParser.typeParameter_return typeParameter() throws RecognitionException {
        JavaParser.typeParameter_return retval = new JavaParser.typeParameter_return();
        retval.start = input.LT(1);
        int typeParameter_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier41=null;
        Token string_literal42=null;
        JavaParser.typeBound_return typeBound43 = null;


        Object Identifier41_tree=null;
        Object string_literal42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:309:5: ( Identifier ( 'extends' typeBound )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:309:9: Identifier ( 'extends' typeBound )?
            {
            root_0 = (Object)adaptor.nil();

            Identifier41=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeParameter638); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier41_tree = (Object)adaptor.create(Identifier41);
            adaptor.addChild(root_0, Identifier41_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:309:20: ( 'extends' typeBound )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==38) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:309:21: 'extends' typeBound
                    {
                    string_literal42=(Token)match(input,38,FOLLOW_38_in_typeParameter641); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal42_tree = (Object)adaptor.create(string_literal42);
                    adaptor.addChild(root_0, string_literal42_tree);
                    }
                    pushFollow(FOLLOW_typeBound_in_typeParameter643);
                    typeBound43=typeBound();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeBound43.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:312:1: typeBound : type ( '&' type )* ;
    public final JavaParser.typeBound_return typeBound() throws RecognitionException {
        JavaParser.typeBound_return retval = new JavaParser.typeBound_return();
        retval.start = input.LT(1);
        int typeBound_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal45=null;
        JavaParser.type_return type44 = null;

        JavaParser.type_return type46 = null;


        Object char_literal45_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:313:5: ( type ( '&' type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:313:9: type ( '&' type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeBound672);
            type44=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type44.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:313:14: ( '&' type )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==43) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:313:15: '&' type
            	    {
            	    char_literal45=(Token)match(input,43,FOLLOW_43_in_typeBound675); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal45_tree = (Object)adaptor.create(char_literal45);
            	    adaptor.addChild(root_0, char_literal45_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeBound677);
            	    type46=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type46.getTree());

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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:316:1: enumDeclaration : ENUM Identifier ( 'implements' typeList )? enumBody ;
    public final JavaParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        JavaParser.enumDeclaration_return retval = new JavaParser.enumDeclaration_return();
        retval.start = input.LT(1);
        int enumDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token ENUM47=null;
        Token Identifier48=null;
        Token string_literal49=null;
        JavaParser.typeList_return typeList50 = null;

        JavaParser.enumBody_return enumBody51 = null;


        Object ENUM47_tree=null;
        Object Identifier48_tree=null;
        Object string_literal49_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:317:5: ( ENUM Identifier ( 'implements' typeList )? enumBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:317:9: ENUM Identifier ( 'implements' typeList )? enumBody
            {
            root_0 = (Object)adaptor.nil();

            ENUM47=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration698); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ENUM47_tree = (Object)adaptor.create(ENUM47);
            adaptor.addChild(root_0, ENUM47_tree);
            }
            Identifier48=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration700); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier48_tree = (Object)adaptor.create(Identifier48);
            adaptor.addChild(root_0, Identifier48_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:317:25: ( 'implements' typeList )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==39) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:317:26: 'implements' typeList
                    {
                    string_literal49=(Token)match(input,39,FOLLOW_39_in_enumDeclaration703); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal49_tree = (Object)adaptor.create(string_literal49);
                    adaptor.addChild(root_0, string_literal49_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_enumDeclaration705);
                    typeList50=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList50.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration709);
            enumBody51=enumBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBody51.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:320:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final JavaParser.enumBody_return enumBody() throws RecognitionException {
        JavaParser.enumBody_return retval = new JavaParser.enumBody_return();
        retval.start = input.LT(1);
        int enumBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal52=null;
        Token char_literal54=null;
        Token char_literal56=null;
        JavaParser.enumConstants_return enumConstants53 = null;

        JavaParser.enumBodyDeclarations_return enumBodyDeclarations55 = null;


        Object char_literal52_tree=null;
        Object char_literal54_tree=null;
        Object char_literal56_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:321:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:321:9: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal52=(Token)match(input,44,FOLLOW_44_in_enumBody728); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal52_tree = (Object)adaptor.create(char_literal52);
            adaptor.addChild(root_0, char_literal52_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:321:13: ( enumConstants )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==Identifier||LA24_0==73) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody730);
                    enumConstants53=enumConstants();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstants53.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:321:28: ( ',' )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==41) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ','
                    {
                    char_literal54=(Token)match(input,41,FOLLOW_41_in_enumBody733); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal54_tree = (Object)adaptor.create(char_literal54);
                    adaptor.addChild(root_0, char_literal54_tree);
                    }

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:321:33: ( enumBodyDeclarations )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==26) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody736);
                    enumBodyDeclarations55=enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBodyDeclarations55.getTree());

                    }
                    break;

            }

            char_literal56=(Token)match(input,45,FOLLOW_45_in_enumBody739); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal56_tree = (Object)adaptor.create(char_literal56);
            adaptor.addChild(root_0, char_literal56_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:324:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final JavaParser.enumConstants_return enumConstants() throws RecognitionException {
        JavaParser.enumConstants_return retval = new JavaParser.enumConstants_return();
        retval.start = input.LT(1);
        int enumConstants_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal58=null;
        JavaParser.enumConstant_return enumConstant57 = null;

        JavaParser.enumConstant_return enumConstant59 = null;


        Object char_literal58_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:325:5: ( enumConstant ( ',' enumConstant )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:325:9: enumConstant ( ',' enumConstant )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enumConstant_in_enumConstants758);
            enumConstant57=enumConstant();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstant57.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:325:22: ( ',' enumConstant )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:325:23: ',' enumConstant
            	    {
            	    char_literal58=(Token)match(input,41,FOLLOW_41_in_enumConstants761); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal58_tree = (Object)adaptor.create(char_literal58);
            	    adaptor.addChild(root_0, char_literal58_tree);
            	    }
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants763);
            	    enumConstant59=enumConstant();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstant59.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:328:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final JavaParser.enumConstant_return enumConstant() throws RecognitionException {
        JavaParser.enumConstant_return retval = new JavaParser.enumConstant_return();
        retval.start = input.LT(1);
        int enumConstant_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier61=null;
        JavaParser.annotations_return annotations60 = null;

        JavaParser.arguments_return arguments62 = null;

        JavaParser.classBody_return classBody63 = null;


        Object Identifier61_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:329:5: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:329:9: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:329:9: ( annotations )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==73) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant788);
                    annotations60=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations60.getTree());

                    }
                    break;

            }

            Identifier61=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstant791); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier61_tree = (Object)adaptor.create(Identifier61);
            adaptor.addChild(root_0, Identifier61_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:329:33: ( arguments )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==66) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant793);
                    arguments62=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments62.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:329:44: ( classBody )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==44) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant796);
                    classBody63=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody63.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:332:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final JavaParser.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        JavaParser.enumBodyDeclarations_return retval = new JavaParser.enumBodyDeclarations_return();
        retval.start = input.LT(1);
        int enumBodyDeclarations_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal64=null;
        JavaParser.classBodyDeclaration_return classBodyDeclaration65 = null;


        Object char_literal64_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:333:5: ( ';' ( classBodyDeclaration )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:333:9: ';' ( classBodyDeclaration )*
            {
            root_0 = (Object)adaptor.nil();

            char_literal64=(Token)match(input,26,FOLLOW_26_in_enumBodyDeclarations820); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal64_tree = (Object)adaptor.create(char_literal64);
            adaptor.addChild(root_0, char_literal64_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:333:13: ( classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=Identifier && LA31_0<=ENUM)||LA31_0==26||LA31_0==28||(LA31_0>=31 && LA31_0<=37)||LA31_0==40||LA31_0==44||(LA31_0>=46 && LA31_0<=47)||(LA31_0>=52 && LA31_0<=63)||LA31_0==73) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:333:14: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations823);
            	    classBodyDeclaration65=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration65.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:336:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final JavaParser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        JavaParser.interfaceDeclaration_return retval = new JavaParser.interfaceDeclaration_return();
        retval.start = input.LT(1);
        int interfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration66 = null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration67 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:337:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:337:9: normalInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration848);
                    normalInterfaceDeclaration66=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration66.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:338:9: annotationTypeDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration858);
                    annotationTypeDeclaration67=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration67.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:341:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        JavaParser.normalInterfaceDeclaration_return retval = new JavaParser.normalInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int normalInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal68=null;
        Token Identifier69=null;
        Token string_literal71=null;
        JavaParser.typeParameters_return typeParameters70 = null;

        JavaParser.typeList_return typeList72 = null;

        JavaParser.interfaceBody_return interfaceBody73 = null;


        Object string_literal68_tree=null;
        Object Identifier69_tree=null;
        Object string_literal71_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:5: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:9: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            root_0 = (Object)adaptor.nil();

            string_literal68=(Token)match(input,46,FOLLOW_46_in_normalInterfaceDeclaration881); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal68_tree = (Object)adaptor.create(string_literal68);
            adaptor.addChild(root_0, string_literal68_tree);
            }
            Identifier69=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration883); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier69_tree = (Object)adaptor.create(Identifier69);
            adaptor.addChild(root_0, Identifier69_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:32: ( typeParameters )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==40) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration885);
                    typeParameters70=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters70.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:48: ( 'extends' typeList )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==38) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:342:49: 'extends' typeList
                    {
                    string_literal71=(Token)match(input,38,FOLLOW_38_in_normalInterfaceDeclaration889); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal71_tree = (Object)adaptor.create(string_literal71);
                    adaptor.addChild(root_0, string_literal71_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration891);
                    typeList72=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList72.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration895);
            interfaceBody73=interfaceBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBody73.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:345:1: typeList : type ( ',' type )* ;
    public final JavaParser.typeList_return typeList() throws RecognitionException {
        JavaParser.typeList_return retval = new JavaParser.typeList_return();
        retval.start = input.LT(1);
        int typeList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal75=null;
        JavaParser.type_return type74 = null;

        JavaParser.type_return type76 = null;


        Object char_literal75_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:5: ( type ( ',' type )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:9: type ( ',' type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeList918);
            type74=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type74.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:14: ( ',' type )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==41) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:346:15: ',' type
            	    {
            	    char_literal75=(Token)match(input,41,FOLLOW_41_in_typeList921); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal75_tree = (Object)adaptor.create(char_literal75);
            	    adaptor.addChild(root_0, char_literal75_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeList923);
            	    type76=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type76.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:349:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final JavaParser.classBody_return classBody() throws RecognitionException {
        JavaParser.classBody_return retval = new JavaParser.classBody_return();
        retval.start = input.LT(1);
        int classBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal77=null;
        Token char_literal79=null;
        JavaParser.classBodyDeclaration_return classBodyDeclaration78 = null;


        Object char_literal77_tree=null;
        Object char_literal79_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:5: ( '{' ( classBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:9: '{' ( classBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal77=(Token)match(input,44,FOLLOW_44_in_classBody948); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal77_tree = (Object)adaptor.create(char_literal77);
            adaptor.addChild(root_0, char_literal77_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:350:13: ( classBodyDeclaration )*
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
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody950);
            	    classBodyDeclaration78=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration78.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            char_literal79=(Token)match(input,45,FOLLOW_45_in_classBody953); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:353:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final JavaParser.interfaceBody_return interfaceBody() throws RecognitionException {
        JavaParser.interfaceBody_return retval = new JavaParser.interfaceBody_return();
        retval.start = input.LT(1);
        int interfaceBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal80=null;
        Token char_literal82=null;
        JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration81 = null;


        Object char_literal80_tree=null;
        Object char_literal82_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:354:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:354:9: '{' ( interfaceBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal80=(Token)match(input,44,FOLLOW_44_in_interfaceBody976); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal80_tree = (Object)adaptor.create(char_literal80);
            adaptor.addChild(root_0, char_literal80_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:354:13: ( interfaceBodyDeclaration )*
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
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody978);
            	    interfaceBodyDeclaration81=interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBodyDeclaration81.getTree());

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            char_literal82=(Token)match(input,45,FOLLOW_45_in_interfaceBody981); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal82_tree = (Object)adaptor.create(char_literal82);
            adaptor.addChild(root_0, char_literal82_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:357:1: classBodyDeclaration : ( ';' | ( 'static' )? block | modifiers memberDecl );
    public final JavaParser.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        JavaParser.classBodyDeclaration_return retval = new JavaParser.classBodyDeclaration_return();
        retval.start = input.LT(1);
        int classBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal83=null;
        Token string_literal84=null;
        JavaParser.block_return block85 = null;

        JavaParser.modifiers_return modifiers86 = null;

        JavaParser.memberDecl_return memberDecl87 = null;


        Object char_literal83_tree=null;
        Object string_literal84_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:358:5: ( ';' | ( 'static' )? block | modifiers memberDecl )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:358:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal83=(Token)match(input,26,FOLLOW_26_in_classBodyDeclaration1000); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal83_tree = (Object)adaptor.create(char_literal83);
                    adaptor.addChild(root_0, char_literal83_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:359:9: ( 'static' )? block
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:359:9: ( 'static' )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==28) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: 'static'
                            {
                            string_literal84=(Token)match(input,28,FOLLOW_28_in_classBodyDeclaration1010); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal84_tree = (Object)adaptor.create(string_literal84);
                            adaptor.addChild(root_0, string_literal84_tree);
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration1013);
                    block85=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block85.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:360:9: modifiers memberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_classBodyDeclaration1023);
                    modifiers86=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers86.getTree());
                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1025);
                    memberDecl87=memberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, memberDecl87.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:363:1: memberDecl : ( genericMethodOrConstructorDecl | memberDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final JavaParser.memberDecl_return memberDecl() throws RecognitionException {
        JavaParser.memberDecl_return retval = new JavaParser.memberDecl_return();
        retval.start = input.LT(1);
        int memberDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal90=null;
        Token Identifier91=null;
        Token Identifier93=null;
        JavaParser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl88 = null;

        JavaParser.memberDeclaration_return memberDeclaration89 = null;

        JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest92 = null;

        JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest94 = null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration95 = null;

        JavaParser.classDeclaration_return classDeclaration96 = null;


        Object string_literal90_tree=null;
        Object Identifier91_tree=null;
        Object Identifier93_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:364:5: ( genericMethodOrConstructorDecl | memberDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:364:9: genericMethodOrConstructorDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl1048);
                    genericMethodOrConstructorDecl88=genericMethodOrConstructorDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, genericMethodOrConstructorDecl88.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:365:9: memberDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_memberDeclaration_in_memberDecl1058);
                    memberDeclaration89=memberDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, memberDeclaration89.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:366:9: 'void' Identifier voidMethodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal90=(Token)match(input,47,FOLLOW_47_in_memberDecl1068); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal90_tree = (Object)adaptor.create(string_literal90);
                    adaptor.addChild(root_0, string_literal90_tree);
                    }
                    Identifier91=(Token)match(input,Identifier,FOLLOW_Identifier_in_memberDecl1070); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier91_tree = (Object)adaptor.create(Identifier91);
                    adaptor.addChild(root_0, Identifier91_tree);
                    }
                    pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl1072);
                    voidMethodDeclaratorRest92=voidMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, voidMethodDeclaratorRest92.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:367:9: Identifier constructorDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier93=(Token)match(input,Identifier,FOLLOW_Identifier_in_memberDecl1082); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier93_tree = (Object)adaptor.create(Identifier93);
                    adaptor.addChild(root_0, Identifier93_tree);
                    }
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl1084);
                    constructorDeclaratorRest94=constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorDeclaratorRest94.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:368:9: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1094);
                    interfaceDeclaration95=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration95.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:369:9: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1104);
                    classDeclaration96=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration96.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:372:1: memberDeclaration : type ( methodDeclaration | fieldDeclaration ) ;
    public final JavaParser.memberDeclaration_return memberDeclaration() throws RecognitionException {
        JavaParser.memberDeclaration_return retval = new JavaParser.memberDeclaration_return();
        retval.start = input.LT(1);
        int memberDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.type_return type97 = null;

        JavaParser.methodDeclaration_return methodDeclaration98 = null;

        JavaParser.fieldDeclaration_return fieldDeclaration99 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:5: ( type ( methodDeclaration | fieldDeclaration ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:9: type ( methodDeclaration | fieldDeclaration )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_memberDeclaration1127);
            type97=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type97.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:14: ( methodDeclaration | fieldDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:15: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDeclaration1130);
                    methodDeclaration98=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaration98.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:373:35: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDeclaration1134);
                    fieldDeclaration99=fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, fieldDeclaration99.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:376:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final JavaParser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl() throws RecognitionException {
        JavaParser.genericMethodOrConstructorDecl_return retval = new JavaParser.genericMethodOrConstructorDecl_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.typeParameters_return typeParameters100 = null;

        JavaParser.genericMethodOrConstructorRest_return genericMethodOrConstructorRest101 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:5: ( typeParameters genericMethodOrConstructorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:377:9: typeParameters genericMethodOrConstructorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1154);
            typeParameters100=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters100.getTree());
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1156);
            genericMethodOrConstructorRest101=genericMethodOrConstructorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, genericMethodOrConstructorRest101.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:380:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final JavaParser.genericMethodOrConstructorRest_return genericMethodOrConstructorRest() throws RecognitionException {
        JavaParser.genericMethodOrConstructorRest_return retval = new JavaParser.genericMethodOrConstructorRest_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal103=null;
        Token Identifier104=null;
        Token Identifier106=null;
        JavaParser.type_return type102 = null;

        JavaParser.methodDeclaratorRest_return methodDeclaratorRest105 = null;

        JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest107 = null;


        Object string_literal103_tree=null;
        Object Identifier104_tree=null;
        Object Identifier106_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:5: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:9: ( type | 'void' ) Identifier methodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:9: ( type | 'void' )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:10: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest1180);
                            type102=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type102.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:381:17: 'void'
                            {
                            string_literal103=(Token)match(input,47,FOLLOW_47_in_genericMethodOrConstructorRest1184); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal103_tree = (Object)adaptor.create(string_literal103);
                            adaptor.addChild(root_0, string_literal103_tree);
                            }

                            }
                            break;

                    }

                    Identifier104=(Token)match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1187); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier104_tree = (Object)adaptor.create(Identifier104);
                    adaptor.addChild(root_0, Identifier104_tree);
                    }
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1189);
                    methodDeclaratorRest105=methodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaratorRest105.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:382:9: Identifier constructorDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier106=(Token)match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1199); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier106_tree = (Object)adaptor.create(Identifier106);
                    adaptor.addChild(root_0, Identifier106_tree);
                    }
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1201);
                    constructorDeclaratorRest107=constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorDeclaratorRest107.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:385:1: methodDeclaration : Identifier methodDeclaratorRest ;
    public final JavaParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        JavaParser.methodDeclaration_return retval = new JavaParser.methodDeclaration_return();
        retval.start = input.LT(1);
        int methodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier108=null;
        JavaParser.methodDeclaratorRest_return methodDeclaratorRest109 = null;


        Object Identifier108_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:386:5: ( Identifier methodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:386:9: Identifier methodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            Identifier108=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration1220); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier108_tree = (Object)adaptor.create(Identifier108);
            adaptor.addChild(root_0, Identifier108_tree);
            }
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration1222);
            methodDeclaratorRest109=methodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaratorRest109.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:389:1: fieldDeclaration : variableDeclarators ';' ;
    public final JavaParser.fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        JavaParser.fieldDeclaration_return retval = new JavaParser.fieldDeclaration_return();
        retval.start = input.LT(1);
        int fieldDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal111=null;
        JavaParser.variableDeclarators_return variableDeclarators110 = null;


        Object char_literal111_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:390:5: ( variableDeclarators ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:390:9: variableDeclarators ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration1241);
            variableDeclarators110=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators110.getTree());
            char_literal111=(Token)match(input,26,FOLLOW_26_in_fieldDeclaration1243); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal111_tree = (Object)adaptor.create(char_literal111);
            adaptor.addChild(root_0, char_literal111_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:393:1: interfaceBodyDeclaration : ( modifiers interfaceMemberDecl | ';' );
    public final JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        JavaParser.interfaceBodyDeclaration_return retval = new JavaParser.interfaceBodyDeclaration_return();
        retval.start = input.LT(1);
        int interfaceBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal114=null;
        JavaParser.modifiers_return modifiers112 = null;

        JavaParser.interfaceMemberDecl_return interfaceMemberDecl113 = null;


        Object char_literal114_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:394:5: ( modifiers interfaceMemberDecl | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:394:9: modifiers interfaceMemberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_interfaceBodyDeclaration1270);
                    modifiers112=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers112.getTree());
                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1272);
                    interfaceMemberDecl113=interfaceMemberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMemberDecl113.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:395:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal114=(Token)match(input,26,FOLLOW_26_in_interfaceBodyDeclaration1282); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal114_tree = (Object)adaptor.create(char_literal114);
                    adaptor.addChild(root_0, char_literal114_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:398:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final JavaParser.interfaceMemberDecl_return interfaceMemberDecl() throws RecognitionException {
        JavaParser.interfaceMemberDecl_return retval = new JavaParser.interfaceMemberDecl_return();
        retval.start = input.LT(1);
        int interfaceMemberDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal117=null;
        Token Identifier118=null;
        JavaParser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl115 = null;

        JavaParser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl116 = null;

        JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest119 = null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration120 = null;

        JavaParser.classDeclaration_return classDeclaration121 = null;


        Object string_literal117_tree=null;
        Object Identifier118_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:5: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:399:9: interfaceMethodOrFieldDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1301);
                    interfaceMethodOrFieldDecl115=interfaceMethodOrFieldDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodOrFieldDecl115.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:400:9: interfaceGenericMethodDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1311);
                    interfaceGenericMethodDecl116=interfaceGenericMethodDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceGenericMethodDecl116.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:401:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal117=(Token)match(input,47,FOLLOW_47_in_interfaceMemberDecl1321); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal117_tree = (Object)adaptor.create(string_literal117);
                    adaptor.addChild(root_0, string_literal117_tree);
                    }
                    Identifier118=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl1323); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier118_tree = (Object)adaptor.create(Identifier118);
                    adaptor.addChild(root_0, Identifier118_tree);
                    }
                    pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl1325);
                    voidInterfaceMethodDeclaratorRest119=voidInterfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, voidInterfaceMethodDeclaratorRest119.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:402:9: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1335);
                    interfaceDeclaration120=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration120.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:403:9: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl1345);
                    classDeclaration121=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration121.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:406:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final JavaParser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl() throws RecognitionException {
        JavaParser.interfaceMethodOrFieldDecl_return retval = new JavaParser.interfaceMethodOrFieldDecl_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier123=null;
        JavaParser.type_return type122 = null;

        JavaParser.interfaceMethodOrFieldRest_return interfaceMethodOrFieldRest124 = null;


        Object Identifier123_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:407:5: ( type Identifier interfaceMethodOrFieldRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:407:9: type Identifier interfaceMethodOrFieldRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl1368);
            type122=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type122.getTree());
            Identifier123=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl1370); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier123_tree = (Object)adaptor.create(Identifier123);
            adaptor.addChild(root_0, Identifier123_tree);
            }
            pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl1372);
            interfaceMethodOrFieldRest124=interfaceMethodOrFieldRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodOrFieldRest124.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:410:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
    public final JavaParser.interfaceMethodOrFieldRest_return interfaceMethodOrFieldRest() throws RecognitionException {
        JavaParser.interfaceMethodOrFieldRest_return retval = new JavaParser.interfaceMethodOrFieldRest_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal126=null;
        JavaParser.constantDeclaratorsRest_return constantDeclaratorsRest125 = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest127 = null;


        Object char_literal126_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:411:5: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:411:9: constantDeclaratorsRest ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest1395);
                    constantDeclaratorsRest125=constantDeclaratorsRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclaratorsRest125.getTree());
                    char_literal126=(Token)match(input,26,FOLLOW_26_in_interfaceMethodOrFieldRest1397); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal126_tree = (Object)adaptor.create(char_literal126);
                    adaptor.addChild(root_0, char_literal126_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:412:9: interfaceMethodDeclaratorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1407);
                    interfaceMethodDeclaratorRest127=interfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest127.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:415:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final JavaParser.methodDeclaratorRest_return methodDeclaratorRest() throws RecognitionException {
        JavaParser.methodDeclaratorRest_return retval = new JavaParser.methodDeclaratorRest_return();
        retval.start = input.LT(1);
        int methodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal129=null;
        Token char_literal130=null;
        Token string_literal131=null;
        Token char_literal134=null;
        JavaParser.formalParameters_return formalParameters128 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList132 = null;

        JavaParser.methodBody_return methodBody133 = null;


        Object char_literal129_tree=null;
        Object char_literal130_tree=null;
        Object string_literal131_tree=null;
        Object char_literal134_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:416:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:416:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest1430);
            formalParameters128=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters128.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:416:26: ( '[' ']' )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==48) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:416:27: '[' ']'
            	    {
            	    char_literal129=(Token)match(input,48,FOLLOW_48_in_methodDeclaratorRest1433); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal129_tree = (Object)adaptor.create(char_literal129);
            	    adaptor.addChild(root_0, char_literal129_tree);
            	    }
            	    char_literal130=(Token)match(input,49,FOLLOW_49_in_methodDeclaratorRest1435); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal130_tree = (Object)adaptor.create(char_literal130);
            	    adaptor.addChild(root_0, char_literal130_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:9: ( 'throws' qualifiedNameList )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==50) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:417:10: 'throws' qualifiedNameList
                    {
                    string_literal131=(Token)match(input,50,FOLLOW_50_in_methodDeclaratorRest1448); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal131_tree = (Object)adaptor.create(string_literal131);
                    adaptor.addChild(root_0, string_literal131_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest1450);
                    qualifiedNameList132=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList132.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:9: ( methodBody | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:418:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest1466);
                    methodBody133=methodBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodBody133.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:419:13: ';'
                    {
                    char_literal134=(Token)match(input,26,FOLLOW_26_in_methodDeclaratorRest1480); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal134_tree = (Object)adaptor.create(char_literal134);
                    adaptor.addChild(root_0, char_literal134_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:423:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final JavaParser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidMethodDeclaratorRest_return retval = new JavaParser.voidMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal136=null;
        Token char_literal139=null;
        JavaParser.formalParameters_return formalParameters135 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList137 = null;

        JavaParser.methodBody_return methodBody138 = null;


        Object string_literal136_tree=null;
        Object char_literal139_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:5: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:9: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest1513);
            formalParameters135=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters135.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:26: ( 'throws' qualifiedNameList )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==50) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:424:27: 'throws' qualifiedNameList
                    {
                    string_literal136=(Token)match(input,50,FOLLOW_50_in_voidMethodDeclaratorRest1516); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal136_tree = (Object)adaptor.create(string_literal136);
                    adaptor.addChild(root_0, string_literal136_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1518);
                    qualifiedNameList137=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList137.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:9: ( methodBody | ';' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:425:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest1534);
                    methodBody138=methodBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodBody138.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:426:13: ';'
                    {
                    char_literal139=(Token)match(input,26,FOLLOW_26_in_voidMethodDeclaratorRest1548); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal139_tree = (Object)adaptor.create(char_literal139);
                    adaptor.addChild(root_0, char_literal139_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:430:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.interfaceMethodDeclaratorRest_return retval = new JavaParser.interfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal141=null;
        Token char_literal142=null;
        Token string_literal143=null;
        Token char_literal145=null;
        JavaParser.formalParameters_return formalParameters140 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList144 = null;


        Object char_literal141_tree=null;
        Object char_literal142_tree=null;
        Object string_literal143_tree=null;
        Object char_literal145_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:431:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:431:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1581);
            formalParameters140=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters140.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:431:26: ( '[' ']' )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==48) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:431:27: '[' ']'
            	    {
            	    char_literal141=(Token)match(input,48,FOLLOW_48_in_interfaceMethodDeclaratorRest1584); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal141_tree = (Object)adaptor.create(char_literal141);
            	    adaptor.addChild(root_0, char_literal141_tree);
            	    }
            	    char_literal142=(Token)match(input,49,FOLLOW_49_in_interfaceMethodDeclaratorRest1586); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal142_tree = (Object)adaptor.create(char_literal142);
            	    adaptor.addChild(root_0, char_literal142_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:431:37: ( 'throws' qualifiedNameList )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==50) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:431:38: 'throws' qualifiedNameList
                    {
                    string_literal143=(Token)match(input,50,FOLLOW_50_in_interfaceMethodDeclaratorRest1591); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal143_tree = (Object)adaptor.create(string_literal143);
                    adaptor.addChild(root_0, string_literal143_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1593);
                    qualifiedNameList144=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList144.getTree());

                    }
                    break;

            }

            char_literal145=(Token)match(input,26,FOLLOW_26_in_interfaceMethodDeclaratorRest1597); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal145_tree = (Object)adaptor.create(char_literal145);
            adaptor.addChild(root_0, char_literal145_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:434:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final JavaParser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl() throws RecognitionException {
        JavaParser.interfaceGenericMethodDecl_return retval = new JavaParser.interfaceGenericMethodDecl_return();
        retval.start = input.LT(1);
        int interfaceGenericMethodDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal148=null;
        Token Identifier149=null;
        JavaParser.typeParameters_return typeParameters146 = null;

        JavaParser.type_return type147 = null;

        JavaParser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest150 = null;


        Object string_literal148_tree=null;
        Object Identifier149_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:435:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:435:9: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl1620);
            typeParameters146=typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters146.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:435:24: ( type | 'void' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:435:25: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl1623);
                    type147=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type147.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:435:32: 'void'
                    {
                    string_literal148=(Token)match(input,47,FOLLOW_47_in_interfaceGenericMethodDecl1627); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal148_tree = (Object)adaptor.create(string_literal148);
                    adaptor.addChild(root_0, string_literal148_tree);
                    }

                    }
                    break;

            }

            Identifier149=(Token)match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl1630); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier149_tree = (Object)adaptor.create(Identifier149);
            adaptor.addChild(root_0, Identifier149_tree);
            }
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1640);
            interfaceMethodDeclaratorRest150=interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaratorRest150.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:439:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        JavaParser.voidInterfaceMethodDeclaratorRest_return retval = new JavaParser.voidInterfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal152=null;
        Token char_literal154=null;
        JavaParser.formalParameters_return formalParameters151 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList153 = null;


        Object string_literal152_tree=null;
        Object char_literal154_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:440:5: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:440:9: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1663);
            formalParameters151=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters151.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:440:26: ( 'throws' qualifiedNameList )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==50) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:440:27: 'throws' qualifiedNameList
                    {
                    string_literal152=(Token)match(input,50,FOLLOW_50_in_voidInterfaceMethodDeclaratorRest1666); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal152_tree = (Object)adaptor.create(string_literal152);
                    adaptor.addChild(root_0, string_literal152_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1668);
                    qualifiedNameList153=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList153.getTree());

                    }
                    break;

            }

            char_literal154=(Token)match(input,26,FOLLOW_26_in_voidInterfaceMethodDeclaratorRest1672); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal154_tree = (Object)adaptor.create(char_literal154);
            adaptor.addChild(root_0, char_literal154_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:443:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? constructorBody ;
    public final JavaParser.constructorDeclaratorRest_return constructorDeclaratorRest() throws RecognitionException {
        JavaParser.constructorDeclaratorRest_return retval = new JavaParser.constructorDeclaratorRest_return();
        retval.start = input.LT(1);
        int constructorDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal156=null;
        JavaParser.formalParameters_return formalParameters155 = null;

        JavaParser.qualifiedNameList_return qualifiedNameList157 = null;

        JavaParser.constructorBody_return constructorBody158 = null;


        Object string_literal156_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:444:5: ( formalParameters ( 'throws' qualifiedNameList )? constructorBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:444:9: formalParameters ( 'throws' qualifiedNameList )? constructorBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1695);
            formalParameters155=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters155.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:444:26: ( 'throws' qualifiedNameList )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==50) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:444:27: 'throws' qualifiedNameList
                    {
                    string_literal156=(Token)match(input,50,FOLLOW_50_in_constructorDeclaratorRest1698); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal156_tree = (Object)adaptor.create(string_literal156);
                    adaptor.addChild(root_0, string_literal156_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1700);
                    qualifiedNameList157=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList157.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_constructorBody_in_constructorDeclaratorRest1704);
            constructorBody158=constructorBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constructorBody158.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:447:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final JavaParser.constantDeclarator_return constantDeclarator() throws RecognitionException {
        JavaParser.constantDeclarator_return retval = new JavaParser.constantDeclarator_return();
        retval.start = input.LT(1);
        int constantDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier159=null;
        JavaParser.constantDeclaratorRest_return constantDeclaratorRest160 = null;


        Object Identifier159_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:448:5: ( Identifier constantDeclaratorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:448:9: Identifier constantDeclaratorRest
            {
            root_0 = (Object)adaptor.nil();

            Identifier159=(Token)match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator1723); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier159_tree = (Object)adaptor.create(Identifier159);
            adaptor.addChild(root_0, Identifier159_tree);
            }
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator1725);
            constantDeclaratorRest160=constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclaratorRest160.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:451:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
    public final JavaParser.variableDeclarators_return variableDeclarators() throws RecognitionException {
        JavaParser.variableDeclarators_return retval = new JavaParser.variableDeclarators_return();
        retval.start = input.LT(1);
        int variableDeclarators_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal162=null;
        JavaParser.variableDeclarator_return variableDeclarator161 = null;

        JavaParser.variableDeclarator_return variableDeclarator163 = null;


        Object char_literal162_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:452:5: ( variableDeclarator ( ',' variableDeclarator )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:452:9: variableDeclarator ( ',' variableDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1748);
            variableDeclarator161=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator161.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:452:28: ( ',' variableDeclarator )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==41) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:452:29: ',' variableDeclarator
            	    {
            	    char_literal162=(Token)match(input,41,FOLLOW_41_in_variableDeclarators1751); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal162_tree = (Object)adaptor.create(char_literal162);
            	    adaptor.addChild(root_0, char_literal162_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1753);
            	    variableDeclarator163=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator163.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:455:1: variableDeclarator : variableDeclaratorId ( '=' variableInitializer )? ;
    public final JavaParser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        JavaParser.variableDeclarator_return retval = new JavaParser.variableDeclarator_return();
        retval.start = input.LT(1);
        int variableDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal165=null;
        JavaParser.variableDeclaratorId_return variableDeclaratorId164 = null;

        JavaParser.variableInitializer_return variableInitializer166 = null;


        Object char_literal165_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:456:5: ( variableDeclaratorId ( '=' variableInitializer )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:456:9: variableDeclaratorId ( '=' variableInitializer )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclaratorId_in_variableDeclarator1774);
            variableDeclaratorId164=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId164.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:456:30: ( '=' variableInitializer )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==51) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:456:31: '=' variableInitializer
                    {
                    char_literal165=(Token)match(input,51,FOLLOW_51_in_variableDeclarator1777); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal165_tree = (Object)adaptor.create(char_literal165);
                    adaptor.addChild(root_0, char_literal165_tree);
                    }
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator1779);
                    variableInitializer166=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer166.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:459:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
    public final JavaParser.constantDeclaratorsRest_return constantDeclaratorsRest() throws RecognitionException {
        JavaParser.constantDeclaratorsRest_return retval = new JavaParser.constantDeclaratorsRest_return();
        retval.start = input.LT(1);
        int constantDeclaratorsRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal168=null;
        JavaParser.constantDeclaratorRest_return constantDeclaratorRest167 = null;

        JavaParser.constantDeclarator_return constantDeclarator169 = null;


        Object char_literal168_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:460:5: ( constantDeclaratorRest ( ',' constantDeclarator )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:460:9: constantDeclaratorRest ( ',' constantDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1804);
            constantDeclaratorRest167=constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclaratorRest167.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:460:32: ( ',' constantDeclarator )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:460:33: ',' constantDeclarator
            	    {
            	    char_literal168=(Token)match(input,41,FOLLOW_41_in_constantDeclaratorsRest1807); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal168_tree = (Object)adaptor.create(char_literal168);
            	    adaptor.addChild(root_0, char_literal168_tree);
            	    }
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1809);
            	    constantDeclarator169=constantDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantDeclarator169.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:463:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
    public final JavaParser.constantDeclaratorRest_return constantDeclaratorRest() throws RecognitionException {
        JavaParser.constantDeclaratorRest_return retval = new JavaParser.constantDeclaratorRest_return();
        retval.start = input.LT(1);
        int constantDeclaratorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal170=null;
        Token char_literal171=null;
        Token char_literal172=null;
        JavaParser.variableInitializer_return variableInitializer173 = null;


        Object char_literal170_tree=null;
        Object char_literal171_tree=null;
        Object char_literal172_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:464:5: ( ( '[' ']' )* '=' variableInitializer )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:464:9: ( '[' ']' )* '=' variableInitializer
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:464:9: ( '[' ']' )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==48) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:464:10: '[' ']'
            	    {
            	    char_literal170=(Token)match(input,48,FOLLOW_48_in_constantDeclaratorRest1831); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal170_tree = (Object)adaptor.create(char_literal170);
            	    adaptor.addChild(root_0, char_literal170_tree);
            	    }
            	    char_literal171=(Token)match(input,49,FOLLOW_49_in_constantDeclaratorRest1833); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal171_tree = (Object)adaptor.create(char_literal171);
            	    adaptor.addChild(root_0, char_literal171_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop60;
                }
            } while (true);

            char_literal172=(Token)match(input,51,FOLLOW_51_in_constantDeclaratorRest1837); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal172_tree = (Object)adaptor.create(char_literal172);
            adaptor.addChild(root_0, char_literal172_tree);
            }
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1839);
            variableInitializer173=variableInitializer();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer173.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:467:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
    public final JavaParser.variableDeclaratorId_return variableDeclaratorId() throws RecognitionException {
        JavaParser.variableDeclaratorId_return retval = new JavaParser.variableDeclaratorId_return();
        retval.start = input.LT(1);
        int variableDeclaratorId_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier174=null;
        Token char_literal175=null;
        Token char_literal176=null;

        Object Identifier174_tree=null;
        Object char_literal175_tree=null;
        Object char_literal176_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:468:5: ( Identifier ( '[' ']' )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:468:9: Identifier ( '[' ']' )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier174=(Token)match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1862); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier174_tree = (Object)adaptor.create(Identifier174);
            adaptor.addChild(root_0, Identifier174_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:468:20: ( '[' ']' )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==48) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:468:21: '[' ']'
            	    {
            	    char_literal175=(Token)match(input,48,FOLLOW_48_in_variableDeclaratorId1865); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal175_tree = (Object)adaptor.create(char_literal175);
            	    adaptor.addChild(root_0, char_literal175_tree);
            	    }
            	    char_literal176=(Token)match(input,49,FOLLOW_49_in_variableDeclaratorId1867); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal176_tree = (Object)adaptor.create(char_literal176);
            	    adaptor.addChild(root_0, char_literal176_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:471:1: variableInitializer : ( arrayInitializer | expression );
    public final JavaParser.variableInitializer_return variableInitializer() throws RecognitionException {
        JavaParser.variableInitializer_return retval = new JavaParser.variableInitializer_return();
        retval.start = input.LT(1);
        int variableInitializer_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arrayInitializer_return arrayInitializer177 = null;

        JavaParser.expression_return expression178 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:472:5: ( arrayInitializer | expression )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:472:9: arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1888);
                    arrayInitializer177=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer177.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:473:9: expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer1898);
                    expression178=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression178.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:476:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final JavaParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        JavaParser.arrayInitializer_return retval = new JavaParser.arrayInitializer_return();
        retval.start = input.LT(1);
        int arrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal179=null;
        Token char_literal181=null;
        Token char_literal183=null;
        Token char_literal184=null;
        JavaParser.variableInitializer_return variableInitializer180 = null;

        JavaParser.variableInitializer_return variableInitializer182 = null;


        Object char_literal179_tree=null;
        Object char_literal181_tree=null;
        Object char_literal183_tree=null;
        Object char_literal184_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:5: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:9: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal179=(Token)match(input,44,FOLLOW_44_in_arrayInitializer1925); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal179_tree = (Object)adaptor.create(char_literal179);
            adaptor.addChild(root_0, char_literal179_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:13: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==Identifier||(LA65_0>=FloatingPointLiteral && LA65_0<=DecimalLiteral)||LA65_0==44||LA65_0==47||(LA65_0>=56 && LA65_0<=63)||(LA65_0>=65 && LA65_0<=66)||(LA65_0>=69 && LA65_0<=72)||(LA65_0>=105 && LA65_0<=106)||(LA65_0>=109 && LA65_0<=113)) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:14: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1928);
                    variableInitializer180=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer180.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:34: ( ',' variableInitializer )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:35: ',' variableInitializer
                    	    {
                    	    char_literal181=(Token)match(input,41,FOLLOW_41_in_arrayInitializer1931); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal181_tree = (Object)adaptor.create(char_literal181);
                    	    adaptor.addChild(root_0, char_literal181_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1933);
                    	    variableInitializer182=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer182.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:61: ( ',' )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==41) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:477:62: ','
                            {
                            char_literal183=(Token)match(input,41,FOLLOW_41_in_arrayInitializer1938); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal183_tree = (Object)adaptor.create(char_literal183);
                            adaptor.addChild(root_0, char_literal183_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            char_literal184=(Token)match(input,45,FOLLOW_45_in_arrayInitializer1945); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal184_tree = (Object)adaptor.create(char_literal184);
            adaptor.addChild(root_0, char_literal184_tree);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifier"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:480:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );
    public final JavaParser.modifier_return modifier() throws RecognitionException {
        JavaParser.modifier_return retval = new JavaParser.modifier_return();
        retval.start = input.LT(1);
        int modifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal186=null;
        Token string_literal187=null;
        Token string_literal188=null;
        Token string_literal189=null;
        Token string_literal190=null;
        Token string_literal191=null;
        Token string_literal192=null;
        Token string_literal193=null;
        Token string_literal194=null;
        Token string_literal195=null;
        Token string_literal196=null;
        JavaParser.annotation_return annotation185 = null;


        Object string_literal186_tree=null;
        Object string_literal187_tree=null;
        Object string_literal188_tree=null;
        Object string_literal189_tree=null;
        Object string_literal190_tree=null;
        Object string_literal191_tree=null;
        Object string_literal192_tree=null;
        Object string_literal193_tree=null;
        Object string_literal194_tree=null;
        Object string_literal195_tree=null;
        Object string_literal196_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:481:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )
            int alt66=12;
            switch ( input.LA(1) ) {
            case 73:
                {
                alt66=1;
                }
                break;
            case 31:
                {
                alt66=2;
                }
                break;
            case 32:
                {
                alt66=3;
                }
                break;
            case 33:
                {
                alt66=4;
                }
                break;
            case 28:
                {
                alt66=5;
                }
                break;
            case 34:
                {
                alt66=6;
                }
                break;
            case 35:
                {
                alt66=7;
                }
                break;
            case 52:
                {
                alt66=8;
                }
                break;
            case 53:
                {
                alt66=9;
                }
                break;
            case 54:
                {
                alt66=10;
                }
                break;
            case 55:
                {
                alt66=11;
                }
                break;
            case 36:
                {
                alt66=12;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:481:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_modifier1964);
                    annotation185=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation185.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:482:9: 'public'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal186=(Token)match(input,31,FOLLOW_31_in_modifier1974); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal186_tree = (Object)adaptor.create(string_literal186);
                    adaptor.addChild(root_0, string_literal186_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:483:9: 'protected'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal187=(Token)match(input,32,FOLLOW_32_in_modifier1984); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal187_tree = (Object)adaptor.create(string_literal187);
                    adaptor.addChild(root_0, string_literal187_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:484:9: 'private'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal188=(Token)match(input,33,FOLLOW_33_in_modifier1994); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal188_tree = (Object)adaptor.create(string_literal188);
                    adaptor.addChild(root_0, string_literal188_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:485:9: 'static'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal189=(Token)match(input,28,FOLLOW_28_in_modifier2004); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal189_tree = (Object)adaptor.create(string_literal189);
                    adaptor.addChild(root_0, string_literal189_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:486:9: 'abstract'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal190=(Token)match(input,34,FOLLOW_34_in_modifier2014); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal190_tree = (Object)adaptor.create(string_literal190);
                    adaptor.addChild(root_0, string_literal190_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:487:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal191=(Token)match(input,35,FOLLOW_35_in_modifier2024); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal191_tree = (Object)adaptor.create(string_literal191);
                    adaptor.addChild(root_0, string_literal191_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:488:9: 'native'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal192=(Token)match(input,52,FOLLOW_52_in_modifier2034); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal192_tree = (Object)adaptor.create(string_literal192);
                    adaptor.addChild(root_0, string_literal192_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:489:9: 'synchronized'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal193=(Token)match(input,53,FOLLOW_53_in_modifier2044); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal193_tree = (Object)adaptor.create(string_literal193);
                    adaptor.addChild(root_0, string_literal193_tree);
                    }

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:490:9: 'transient'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal194=(Token)match(input,54,FOLLOW_54_in_modifier2054); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal194_tree = (Object)adaptor.create(string_literal194);
                    adaptor.addChild(root_0, string_literal194_tree);
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:491:9: 'volatile'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal195=(Token)match(input,55,FOLLOW_55_in_modifier2064); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal195_tree = (Object)adaptor.create(string_literal195);
                    adaptor.addChild(root_0, string_literal195_tree);
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:492:9: 'strictfp'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal196=(Token)match(input,36,FOLLOW_36_in_modifier2074); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal196_tree = (Object)adaptor.create(string_literal196);
                    adaptor.addChild(root_0, string_literal196_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:495:1: packageOrTypeName : qualifiedName ;
    public final JavaParser.packageOrTypeName_return packageOrTypeName() throws RecognitionException {
        JavaParser.packageOrTypeName_return retval = new JavaParser.packageOrTypeName_return();
        retval.start = input.LT(1);
        int packageOrTypeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName197 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:496:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:496:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_packageOrTypeName2093);
            qualifiedName197=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName197.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:499:1: enumConstantName : Identifier ;
    public final JavaParser.enumConstantName_return enumConstantName() throws RecognitionException {
        JavaParser.enumConstantName_return retval = new JavaParser.enumConstantName_return();
        retval.start = input.LT(1);
        int enumConstantName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier198=null;

        Object Identifier198_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:5: ( Identifier )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:500:9: Identifier
            {
            root_0 = (Object)adaptor.nil();

            Identifier198=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumConstantName2112); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier198_tree = (Object)adaptor.create(Identifier198);
            adaptor.addChild(root_0, Identifier198_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:503:1: typeName : qualifiedName ;
    public final JavaParser.typeName_return typeName() throws RecognitionException {
        JavaParser.typeName_return retval = new JavaParser.typeName_return();
        retval.start = input.LT(1);
        int typeName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.qualifiedName_return qualifiedName199 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:5: ( qualifiedName )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:504:9: qualifiedName
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_typeName2131);
            qualifiedName199=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName199.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:507:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final JavaParser.type_return type() throws RecognitionException {
        JavaParser.type_return retval = new JavaParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal201=null;
        Token char_literal202=null;
        Token char_literal204=null;
        Token char_literal205=null;
        JavaParser.classOrInterfaceType_return classOrInterfaceType200 = null;

        JavaParser.primitiveType_return primitiveType203 = null;


        Object char_literal201_tree=null;
        Object char_literal202_tree=null;
        Object char_literal204_tree=null;
        Object char_literal205_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:2: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:4: classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_type2145);
                    classOrInterfaceType200=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType200.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:25: ( '[' ']' )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==48) ) {
                            alt67=1;
                        }


                        switch (alt67) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:508:26: '[' ']'
                    	    {
                    	    char_literal201=(Token)match(input,48,FOLLOW_48_in_type2148); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal201_tree = (Object)adaptor.create(char_literal201);
                    	    adaptor.addChild(root_0, char_literal201_tree);
                    	    }
                    	    char_literal202=(Token)match(input,49,FOLLOW_49_in_type2150); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal202_tree = (Object)adaptor.create(char_literal202);
                    	    adaptor.addChild(root_0, char_literal202_tree);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:509:4: primitiveType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_type2157);
                    primitiveType203=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType203.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:509:18: ( '[' ']' )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( (LA68_0==48) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:509:19: '[' ']'
                    	    {
                    	    char_literal204=(Token)match(input,48,FOLLOW_48_in_type2160); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal204_tree = (Object)adaptor.create(char_literal204);
                    	    adaptor.addChild(root_0, char_literal204_tree);
                    	    }
                    	    char_literal205=(Token)match(input,49,FOLLOW_49_in_type2162); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal205_tree = (Object)adaptor.create(char_literal205);
                    	    adaptor.addChild(root_0, char_literal205_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:512:1: classOrInterfaceType : Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ;
    public final JavaParser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        JavaParser.classOrInterfaceType_return retval = new JavaParser.classOrInterfaceType_return();
        retval.start = input.LT(1);
        int classOrInterfaceType_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier206=null;
        Token char_literal208=null;
        Token Identifier209=null;
        JavaParser.typeArguments_return typeArguments207 = null;

        JavaParser.typeArguments_return typeArguments210 = null;


        Object Identifier206_tree=null;
        Object char_literal208_tree=null;
        Object Identifier209_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier206=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2175); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier206_tree = (Object)adaptor.create(Identifier206);
            adaptor.addChild(root_0, Identifier206_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:15: ( typeArguments )?
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
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2177);
                    typeArguments207=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments207.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:30: ( '.' Identifier ( typeArguments )? )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==29) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:31: '.' Identifier ( typeArguments )?
            	    {
            	    char_literal208=(Token)match(input,29,FOLLOW_29_in_classOrInterfaceType2181); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal208_tree = (Object)adaptor.create(char_literal208);
            	    adaptor.addChild(root_0, char_literal208_tree);
            	    }
            	    Identifier209=(Token)match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2183); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier209_tree = (Object)adaptor.create(Identifier209);
            	    adaptor.addChild(root_0, Identifier209_tree);
            	    }
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:513:46: ( typeArguments )?
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
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2185);
            	            typeArguments210=typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments210.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:516:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final JavaParser.primitiveType_return primitiveType() throws RecognitionException {
        JavaParser.primitiveType_return retval = new JavaParser.primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        Object root_0 = null;

        Token set211=null;

        Object set211_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:517:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set211=(Token)input.LT(1);
            if ( (input.LA(1)>=56 && input.LA(1)<=63) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set211));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:527:1: variableModifier : ( 'final' | annotation );
    public final JavaParser.variableModifier_return variableModifier() throws RecognitionException {
        JavaParser.variableModifier_return retval = new JavaParser.variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal212=null;
        JavaParser.annotation_return annotation213 = null;


        Object string_literal212_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:5: ( 'final' | annotation )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:528:9: 'final'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal212=(Token)match(input,35,FOLLOW_35_in_variableModifier2294); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal212_tree = (Object)adaptor.create(string_literal212);
                    adaptor.addChild(root_0, string_literal212_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:529:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_variableModifier2304);
                    annotation213=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation213.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:532:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final JavaParser.typeArguments_return typeArguments() throws RecognitionException {
        JavaParser.typeArguments_return retval = new JavaParser.typeArguments_return();
        retval.start = input.LT(1);
        int typeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal214=null;
        Token char_literal216=null;
        Token char_literal218=null;
        JavaParser.typeArgument_return typeArgument215 = null;

        JavaParser.typeArgument_return typeArgument217 = null;


        Object char_literal214_tree=null;
        Object char_literal216_tree=null;
        Object char_literal218_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal214=(Token)match(input,40,FOLLOW_40_in_typeArguments2323); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal214_tree = (Object)adaptor.create(char_literal214);
            adaptor.addChild(root_0, char_literal214_tree);
            }
            pushFollow(FOLLOW_typeArgument_in_typeArguments2325);
            typeArgument215=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument215.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:26: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==41) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:533:27: ',' typeArgument
            	    {
            	    char_literal216=(Token)match(input,41,FOLLOW_41_in_typeArguments2328); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal216_tree = (Object)adaptor.create(char_literal216);
            	    adaptor.addChild(root_0, char_literal216_tree);
            	    }
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2330);
            	    typeArgument217=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument217.getTree());

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            char_literal218=(Token)match(input,42,FOLLOW_42_in_typeArguments2334); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:536:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final JavaParser.typeArgument_return typeArgument() throws RecognitionException {
        JavaParser.typeArgument_return retval = new JavaParser.typeArgument_return();
        retval.start = input.LT(1);
        int typeArgument_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal220=null;
        Token set221=null;
        JavaParser.type_return type219 = null;

        JavaParser.type_return type222 = null;


        Object char_literal220_tree=null;
        Object set221_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:537:9: type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_typeArgument2357);
                    type219=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type219.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:538:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal220=(Token)match(input,64,FOLLOW_64_in_typeArgument2367); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal220_tree = (Object)adaptor.create(char_literal220);
                    adaptor.addChild(root_0, char_literal220_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:538:13: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==38||LA75_0==65) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:538:14: ( 'extends' | 'super' ) type
                            {
                            set221=(Token)input.LT(1);
                            if ( input.LA(1)==38||input.LA(1)==65 ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set221));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument2378);
                            type222=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type222.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:541:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final JavaParser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        JavaParser.qualifiedNameList_return retval = new JavaParser.qualifiedNameList_return();
        retval.start = input.LT(1);
        int qualifiedNameList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal224=null;
        JavaParser.qualifiedName_return qualifiedName223 = null;

        JavaParser.qualifiedName_return qualifiedName225 = null;


        Object char_literal224_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:5: ( qualifiedName ( ',' qualifiedName )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:9: qualifiedName ( ',' qualifiedName )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2403);
            qualifiedName223=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName223.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:23: ( ',' qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==41) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:542:24: ',' qualifiedName
            	    {
            	    char_literal224=(Token)match(input,41,FOLLOW_41_in_qualifiedNameList2406); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal224_tree = (Object)adaptor.create(char_literal224);
            	    adaptor.addChild(root_0, char_literal224_tree);
            	    }
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2408);
            	    qualifiedName225=qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName225.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:545:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final JavaParser.formalParameters_return formalParameters() throws RecognitionException {
        JavaParser.formalParameters_return retval = new JavaParser.formalParameters_return();
        retval.start = input.LT(1);
        int formalParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal226=null;
        Token char_literal228=null;
        JavaParser.formalParameterDecls_return formalParameterDecls227 = null;


        Object char_literal226_tree=null;
        Object char_literal228_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:5: ( '(' ( formalParameterDecls )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:9: '(' ( formalParameterDecls )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal226=(Token)match(input,66,FOLLOW_66_in_formalParameters2429); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal226_tree = (Object)adaptor.create(char_literal226);
            adaptor.addChild(root_0, char_literal226_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:546:13: ( formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==35||(LA78_0>=56 && LA78_0<=63)||LA78_0==73) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters2431);
                    formalParameterDecls227=formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls227.getTree());

                    }
                    break;

            }

            char_literal228=(Token)match(input,67,FOLLOW_67_in_formalParameters2434); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal228_tree = (Object)adaptor.create(char_literal228);
            adaptor.addChild(root_0, char_literal228_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:549:1: formalParameterDecls : variableModifiers type formalParameterDeclsRest ;
    public final JavaParser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        JavaParser.formalParameterDecls_return retval = new JavaParser.formalParameterDecls_return();
        retval.start = input.LT(1);
        int formalParameterDecls_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers229 = null;

        JavaParser.type_return type230 = null;

        JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest231 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:550:5: ( variableModifiers type formalParameterDeclsRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:550:9: variableModifiers type formalParameterDeclsRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameterDecls2457);
            variableModifiers229=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers229.getTree());
            pushFollow(FOLLOW_type_in_formalParameterDecls2459);
            type230=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type230.getTree());
            pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2461);
            formalParameterDeclsRest231=formalParameterDeclsRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDeclsRest231.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:553:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final JavaParser.formalParameterDeclsRest_return formalParameterDeclsRest() throws RecognitionException {
        JavaParser.formalParameterDeclsRest_return retval = new JavaParser.formalParameterDeclsRest_return();
        retval.start = input.LT(1);
        int formalParameterDeclsRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal233=null;
        Token string_literal235=null;
        JavaParser.variableDeclaratorId_return variableDeclaratorId232 = null;

        JavaParser.formalParameterDecls_return formalParameterDecls234 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId236 = null;


        Object char_literal233_tree=null;
        Object string_literal235_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:554:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:554:9: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2484);
                    variableDeclaratorId232=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId232.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:554:30: ( ',' formalParameterDecls )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==41) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:554:31: ',' formalParameterDecls
                            {
                            char_literal233=(Token)match(input,41,FOLLOW_41_in_formalParameterDeclsRest2487); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal233_tree = (Object)adaptor.create(char_literal233);
                            adaptor.addChild(root_0, char_literal233_tree);
                            }
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2489);
                            formalParameterDecls234=formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls234.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:555:9: '...' variableDeclaratorId
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal235=(Token)match(input,68,FOLLOW_68_in_formalParameterDeclsRest2501); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal235_tree = (Object)adaptor.create(string_literal235);
                    adaptor.addChild(root_0, string_literal235_tree);
                    }
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2503);
                    variableDeclaratorId236=variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId236.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:558:1: methodBody : block ;
    public final JavaParser.methodBody_return methodBody() throws RecognitionException {
        JavaParser.methodBody_return retval = new JavaParser.methodBody_return();
        retval.start = input.LT(1);
        int methodBody_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.block_return block237 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:559:5: ( block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:559:9: block
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_block_in_methodBody2526);
            block237=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block237.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:562:1: constructorBody : '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' ;
    public final JavaParser.constructorBody_return constructorBody() throws RecognitionException {
        JavaParser.constructorBody_return retval = new JavaParser.constructorBody_return();
        retval.start = input.LT(1);
        int constructorBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal238=null;
        Token char_literal241=null;
        JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation239 = null;

        JavaParser.blockStatement_return blockStatement240 = null;


        Object char_literal238_tree=null;
        Object char_literal241_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:5: ( '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:9: '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal238=(Token)match(input,44,FOLLOW_44_in_constructorBody2545); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal238_tree = (Object)adaptor.create(char_literal238);
            adaptor.addChild(root_0, char_literal238_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:13: ( explicitConstructorInvocation )?
            int alt81=2;
            alt81 = dfa81.predict(input);
            switch (alt81) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: explicitConstructorInvocation
                    {
                    pushFollow(FOLLOW_explicitConstructorInvocation_in_constructorBody2547);
                    explicitConstructorInvocation239=explicitConstructorInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitConstructorInvocation239.getTree());

                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:44: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_constructorBody2550);
            	    blockStatement240=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement240.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            char_literal241=(Token)match(input,45,FOLLOW_45_in_constructorBody2553); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal241_tree = (Object)adaptor.create(char_literal241);
            adaptor.addChild(root_0, char_literal241_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:566:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        JavaParser.explicitConstructorInvocation_return retval = new JavaParser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);
        int explicitConstructorInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token set243=null;
        Token char_literal245=null;
        Token char_literal247=null;
        Token string_literal249=null;
        Token char_literal251=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments242 = null;

        JavaParser.arguments_return arguments244 = null;

        JavaParser.primary_return primary246 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments248 = null;

        JavaParser.arguments_return arguments250 = null;


        Object set243_tree=null;
        Object char_literal245_tree=null;
        Object char_literal247_tree=null;
        Object string_literal249_tree=null;
        Object char_literal251_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt85=2;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:9: ( nonWildcardTypeArguments )?
                    int alt83=2;
                    int LA83_0 = input.LA(1);

                    if ( (LA83_0==40) ) {
                        alt83=1;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2572);
                            nonWildcardTypeArguments242=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments242.getTree());

                            }
                            break;

                    }

                    set243=(Token)input.LT(1);
                    if ( input.LA(1)==65||input.LA(1)==69 ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set243));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation2583);
                    arguments244=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments244.getTree());
                    char_literal245=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation2585); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal245_tree = (Object)adaptor.create(char_literal245);
                    adaptor.addChild(root_0, char_literal245_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation2595);
                    primary246=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary246.getTree());
                    char_literal247=(Token)match(input,29,FOLLOW_29_in_explicitConstructorInvocation2597); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal247_tree = (Object)adaptor.create(char_literal247);
                    adaptor.addChild(root_0, char_literal247_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:568:21: ( nonWildcardTypeArguments )?
                    int alt84=2;
                    int LA84_0 = input.LA(1);

                    if ( (LA84_0==40) ) {
                        alt84=1;
                    }
                    switch (alt84) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2599);
                            nonWildcardTypeArguments248=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments248.getTree());

                            }
                            break;

                    }

                    string_literal249=(Token)match(input,65,FOLLOW_65_in_explicitConstructorInvocation2602); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal249_tree = (Object)adaptor.create(string_literal249);
                    adaptor.addChild(root_0, string_literal249_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation2604);
                    arguments250=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments250.getTree());
                    char_literal251=(Token)match(input,26,FOLLOW_26_in_explicitConstructorInvocation2606); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal251_tree = (Object)adaptor.create(char_literal251);
                    adaptor.addChild(root_0, char_literal251_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:572:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final JavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
        JavaParser.qualifiedName_return retval = new JavaParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier252=null;
        Token char_literal253=null;
        Token Identifier254=null;

        Object Identifier252_tree=null;
        Object char_literal253_tree=null;
        Object Identifier254_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:573:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:573:9: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier252=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2626); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier252_tree = (Object)adaptor.create(Identifier252);
            adaptor.addChild(root_0, Identifier252_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:573:20: ( '.' Identifier )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:573:21: '.' Identifier
            	    {
            	    char_literal253=(Token)match(input,29,FOLLOW_29_in_qualifiedName2629); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal253_tree = (Object)adaptor.create(char_literal253);
            	    adaptor.addChild(root_0, char_literal253_tree);
            	    }
            	    Identifier254=(Token)match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2631); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier254_tree = (Object)adaptor.create(Identifier254);
            	    adaptor.addChild(root_0, Identifier254_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:576:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final JavaParser.literal_return literal() throws RecognitionException {
        JavaParser.literal_return retval = new JavaParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token FloatingPointLiteral256=null;
        Token CharacterLiteral257=null;
        Token StringLiteral258=null;
        Token string_literal260=null;
        JavaParser.integerLiteral_return integerLiteral255 = null;

        JavaParser.booleanLiteral_return booleanLiteral259 = null;


        Object FloatingPointLiteral256_tree=null;
        Object CharacterLiteral257_tree=null;
        Object StringLiteral258_tree=null;
        Object string_literal260_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:577:9: integerLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_integerLiteral_in_literal2657);
                    integerLiteral255=integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, integerLiteral255.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:578:9: FloatingPointLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    FloatingPointLiteral256=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal2667); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FloatingPointLiteral256_tree = (Object)adaptor.create(FloatingPointLiteral256);
                    adaptor.addChild(root_0, FloatingPointLiteral256_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:579:9: CharacterLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    CharacterLiteral257=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal2677); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CharacterLiteral257_tree = (Object)adaptor.create(CharacterLiteral257);
                    adaptor.addChild(root_0, CharacterLiteral257_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:580:9: StringLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    StringLiteral258=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal2687); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    StringLiteral258_tree = (Object)adaptor.create(StringLiteral258);
                    adaptor.addChild(root_0, StringLiteral258_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:581:9: booleanLiteral
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_booleanLiteral_in_literal2697);
                    booleanLiteral259=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, booleanLiteral259.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:582:9: 'null'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal260=(Token)match(input,70,FOLLOW_70_in_literal2707); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal260_tree = (Object)adaptor.create(string_literal260);
                    adaptor.addChild(root_0, string_literal260_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:585:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final JavaParser.integerLiteral_return integerLiteral() throws RecognitionException {
        JavaParser.integerLiteral_return retval = new JavaParser.integerLiteral_return();
        retval.start = input.LT(1);
        int integerLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set261=null;

        Object set261_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:586:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set261=(Token)input.LT(1);
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set261));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:591:1: booleanLiteral : ( 'true' | 'false' );
    public final JavaParser.booleanLiteral_return booleanLiteral() throws RecognitionException {
        JavaParser.booleanLiteral_return retval = new JavaParser.booleanLiteral_return();
        retval.start = input.LT(1);
        int booleanLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token set262=null;

        Object set262_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:592:5: ( 'true' | 'false' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set262=(Token)input.LT(1);
            if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set262));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:598:1: annotations : ( annotation )+ ;
    public final JavaParser.annotations_return annotations() throws RecognitionException {
        JavaParser.annotations_return retval = new JavaParser.annotations_return();
        retval.start = input.LT(1);
        int annotations_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotation_return annotation263 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:5: ( ( annotation )+ )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:9: ( annotation )+
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:9: ( annotation )+
            int cnt88=0;
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==73) ) {
                    int LA88_2 = input.LA(2);

                    if ( (LA88_2==Identifier) ) {
                        int LA88_3 = input.LA(3);

                        if ( (synpred128_Java()) ) {
                            alt88=1;
                        }


                    }


                }


                switch (alt88) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations2796);
            	    annotation263=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation263.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:602:1: annotation : '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final JavaParser.annotation_return annotation() throws RecognitionException {
        JavaParser.annotation_return retval = new JavaParser.annotation_return();
        retval.start = input.LT(1);
        int annotation_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal264=null;
        Token char_literal266=null;
        Token char_literal269=null;
        JavaParser.annotationName_return annotationName265 = null;

        JavaParser.elementValuePairs_return elementValuePairs267 = null;

        JavaParser.elementValue_return elementValue268 = null;


        Object char_literal264_tree=null;
        Object char_literal266_tree=null;
        Object char_literal269_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:5: ( '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:9: '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (Object)adaptor.nil();

            char_literal264=(Token)match(input,73,FOLLOW_73_in_annotation2816); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal264_tree = (Object)adaptor.create(char_literal264);
            adaptor.addChild(root_0, char_literal264_tree);
            }
            pushFollow(FOLLOW_annotationName_in_annotation2818);
            annotationName265=annotationName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationName265.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:28: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==66) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:30: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal266=(Token)match(input,66,FOLLOW_66_in_annotation2822); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal266_tree = (Object)adaptor.create(char_literal266);
                    adaptor.addChild(root_0, char_literal266_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:34: ( elementValuePairs | elementValue )?
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:36: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation2826);
                            elementValuePairs267=elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePairs267.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:603:56: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation2830);
                            elementValue268=elementValue();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue268.getTree());

                            }
                            break;

                    }

                    char_literal269=(Token)match(input,67,FOLLOW_67_in_annotation2835); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal269_tree = (Object)adaptor.create(char_literal269);
                    adaptor.addChild(root_0, char_literal269_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:606:1: annotationName : Identifier ( '.' Identifier )* ;
    public final JavaParser.annotationName_return annotationName() throws RecognitionException {
        JavaParser.annotationName_return retval = new JavaParser.annotationName_return();
        retval.start = input.LT(1);
        int annotationName_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier270=null;
        Token char_literal271=null;
        Token Identifier272=null;

        Object Identifier270_tree=null;
        Object char_literal271_tree=null;
        Object Identifier272_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:7: Identifier ( '.' Identifier )*
            {
            root_0 = (Object)adaptor.nil();

            Identifier270=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName2859); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier270_tree = (Object)adaptor.create(Identifier270);
            adaptor.addChild(root_0, Identifier270_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:18: ( '.' Identifier )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==29) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:607:19: '.' Identifier
            	    {
            	    char_literal271=(Token)match(input,29,FOLLOW_29_in_annotationName2862); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal271_tree = (Object)adaptor.create(char_literal271);
            	    adaptor.addChild(root_0, char_literal271_tree);
            	    }
            	    Identifier272=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationName2864); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    Identifier272_tree = (Object)adaptor.create(Identifier272);
            	    adaptor.addChild(root_0, Identifier272_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:610:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final JavaParser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        JavaParser.elementValuePairs_return retval = new JavaParser.elementValuePairs_return();
        retval.start = input.LT(1);
        int elementValuePairs_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal274=null;
        JavaParser.elementValuePair_return elementValuePair273 = null;

        JavaParser.elementValuePair_return elementValuePair275 = null;


        Object char_literal274_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:5: ( elementValuePair ( ',' elementValuePair )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2885);
            elementValuePair273=elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair273.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:26: ( ',' elementValuePair )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==41) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:611:27: ',' elementValuePair
            	    {
            	    char_literal274=(Token)match(input,41,FOLLOW_41_in_elementValuePairs2888); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal274_tree = (Object)adaptor.create(char_literal274);
            	    adaptor.addChild(root_0, char_literal274_tree);
            	    }
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2890);
            	    elementValuePair275=elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair275.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:614:1: elementValuePair : Identifier '=' elementValue ;
    public final JavaParser.elementValuePair_return elementValuePair() throws RecognitionException {
        JavaParser.elementValuePair_return retval = new JavaParser.elementValuePair_return();
        retval.start = input.LT(1);
        int elementValuePair_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier276=null;
        Token char_literal277=null;
        JavaParser.elementValue_return elementValue278 = null;


        Object Identifier276_tree=null;
        Object char_literal277_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:615:5: ( Identifier '=' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:615:9: Identifier '=' elementValue
            {
            root_0 = (Object)adaptor.nil();

            Identifier276=(Token)match(input,Identifier,FOLLOW_Identifier_in_elementValuePair2911); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier276_tree = (Object)adaptor.create(Identifier276);
            adaptor.addChild(root_0, Identifier276_tree);
            }
            char_literal277=(Token)match(input,51,FOLLOW_51_in_elementValuePair2913); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal277_tree = (Object)adaptor.create(char_literal277);
            adaptor.addChild(root_0, char_literal277_tree);
            }
            pushFollow(FOLLOW_elementValue_in_elementValuePair2915);
            elementValue278=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue278.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:618:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final JavaParser.elementValue_return elementValue() throws RecognitionException {
        JavaParser.elementValue_return retval = new JavaParser.elementValue_return();
        retval.start = input.LT(1);
        int elementValue_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression279 = null;

        JavaParser.annotation_return annotation280 = null;

        JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer281 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:619:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:619:9: conditionalExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_conditionalExpression_in_elementValue2938);
                    conditionalExpression279=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression279.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:620:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_elementValue2948);
                    annotation280=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation280.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:621:9: elementValueArrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue2958);
                    elementValueArrayInitializer281=elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer281.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:624:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        JavaParser.elementValueArrayInitializer_return retval = new JavaParser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);
        int elementValueArrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal282=null;
        Token char_literal284=null;
        Token char_literal286=null;
        Token char_literal287=null;
        JavaParser.elementValue_return elementValue283 = null;

        JavaParser.elementValue_return elementValue285 = null;


        Object char_literal282_tree=null;
        Object char_literal284_tree=null;
        Object char_literal286_tree=null;
        Object char_literal287_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal282=(Token)match(input,44,FOLLOW_44_in_elementValueArrayInitializer2981); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal282_tree = (Object)adaptor.create(char_literal282);
            adaptor.addChild(root_0, char_literal282_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:13: ( elementValue ( ',' elementValue )* )?
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==Identifier||(LA95_0>=FloatingPointLiteral && LA95_0<=DecimalLiteral)||LA95_0==44||LA95_0==47||(LA95_0>=56 && LA95_0<=63)||(LA95_0>=65 && LA95_0<=66)||(LA95_0>=69 && LA95_0<=73)||(LA95_0>=105 && LA95_0<=106)||(LA95_0>=109 && LA95_0<=113)) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:14: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2984);
                    elementValue283=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue283.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:27: ( ',' elementValue )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:28: ',' elementValue
                    	    {
                    	    char_literal284=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer2987); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal284_tree = (Object)adaptor.create(char_literal284);
                    	    adaptor.addChild(root_0, char_literal284_tree);
                    	    }
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2989);
                    	    elementValue285=elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue285.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop94;
                        }
                    } while (true);


                    }
                    break;

            }

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:49: ( ',' )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==41) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:625:50: ','
                    {
                    char_literal286=(Token)match(input,41,FOLLOW_41_in_elementValueArrayInitializer2996); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal286_tree = (Object)adaptor.create(char_literal286);
                    adaptor.addChild(root_0, char_literal286_tree);
                    }

                    }
                    break;

            }

            char_literal287=(Token)match(input,45,FOLLOW_45_in_elementValueArrayInitializer3000); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal287_tree = (Object)adaptor.create(char_literal287);
            adaptor.addChild(root_0, char_literal287_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:628:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
    public final JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        JavaParser.annotationTypeDeclaration_return retval = new JavaParser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal288=null;
        Token string_literal289=null;
        Token Identifier290=null;
        JavaParser.annotationTypeBody_return annotationTypeBody291 = null;


        Object char_literal288_tree=null;
        Object string_literal289_tree=null;
        Object Identifier290_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:629:5: ( '@' 'interface' Identifier annotationTypeBody )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:629:9: '@' 'interface' Identifier annotationTypeBody
            {
            root_0 = (Object)adaptor.nil();

            char_literal288=(Token)match(input,73,FOLLOW_73_in_annotationTypeDeclaration3023); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal288_tree = (Object)adaptor.create(char_literal288);
            adaptor.addChild(root_0, char_literal288_tree);
            }
            string_literal289=(Token)match(input,46,FOLLOW_46_in_annotationTypeDeclaration3025); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal289_tree = (Object)adaptor.create(string_literal289);
            adaptor.addChild(root_0, string_literal289_tree);
            }
            Identifier290=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration3027); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier290_tree = (Object)adaptor.create(Identifier290);
            adaptor.addChild(root_0, Identifier290_tree);
            }
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3029);
            annotationTypeBody291=annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody291.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:632:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final JavaParser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        JavaParser.annotationTypeBody_return retval = new JavaParser.annotationTypeBody_return();
        retval.start = input.LT(1);
        int annotationTypeBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal292=null;
        Token char_literal294=null;
        JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration293 = null;


        Object char_literal292_tree=null;
        Object char_literal294_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal292=(Token)match(input,44,FOLLOW_44_in_annotationTypeBody3052); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal292_tree = (Object)adaptor.create(char_literal292);
            adaptor.addChild(root_0, char_literal292_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:13: ( annotationTypeElementDeclaration )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( ((LA97_0>=Identifier && LA97_0<=ENUM)||LA97_0==28||(LA97_0>=31 && LA97_0<=37)||LA97_0==40||(LA97_0>=46 && LA97_0<=47)||(LA97_0>=52 && LA97_0<=63)||LA97_0==73) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:633:14: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3055);
            	    annotationTypeElementDeclaration293=annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration293.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            char_literal294=(Token)match(input,45,FOLLOW_45_in_annotationTypeBody3059); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal294_tree = (Object)adaptor.create(char_literal294);
            adaptor.addChild(root_0, char_literal294_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:636:1: annotationTypeElementDeclaration : modifiers annotationTypeElementRest ;
    public final JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        JavaParser.annotationTypeElementDeclaration_return retval = new JavaParser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeElementDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.modifiers_return modifiers295 = null;

        JavaParser.annotationTypeElementRest_return annotationTypeElementRest296 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:637:5: ( modifiers annotationTypeElementRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:637:9: modifiers annotationTypeElementRest
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationTypeElementDeclaration3082);
            modifiers295=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers295.getTree());
            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3084);
            annotationTypeElementRest296=annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementRest296.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:640:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final JavaParser.annotationTypeElementRest_return annotationTypeElementRest() throws RecognitionException {
        JavaParser.annotationTypeElementRest_return retval = new JavaParser.annotationTypeElementRest_return();
        retval.start = input.LT(1);
        int annotationTypeElementRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal299=null;
        Token char_literal301=null;
        Token char_literal303=null;
        Token char_literal305=null;
        Token char_literal307=null;
        JavaParser.type_return type297 = null;

        JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest298 = null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration300 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration302 = null;

        JavaParser.enumDeclaration_return enumDeclaration304 = null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration306 = null;


        Object char_literal299_tree=null;
        Object char_literal301_tree=null;
        Object char_literal303_tree=null;
        Object char_literal305_tree=null;
        Object char_literal307_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:641:5: ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:641:9: type annotationMethodOrConstantRest ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_annotationTypeElementRest3107);
                    type297=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type297.getTree());
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3109);
                    annotationMethodOrConstantRest298=annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodOrConstantRest298.getTree());
                    char_literal299=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3111); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal299_tree = (Object)adaptor.create(char_literal299);
                    adaptor.addChild(root_0, char_literal299_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:9: normalClassDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3121);
                    normalClassDeclaration300=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration300.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:642:32: ( ';' )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==26) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal301=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3123); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal301_tree = (Object)adaptor.create(char_literal301);
                            adaptor.addChild(root_0, char_literal301_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:643:9: normalInterfaceDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3134);
                    normalInterfaceDeclaration302=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration302.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:643:36: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==26) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal303=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3136); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal303_tree = (Object)adaptor.create(char_literal303);
                            adaptor.addChild(root_0, char_literal303_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:644:9: enumDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest3147);
                    enumDeclaration304=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration304.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:644:25: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==26) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal305=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3149); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal305_tree = (Object)adaptor.create(char_literal305);
                            adaptor.addChild(root_0, char_literal305_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:645:9: annotationTypeDeclaration ( ';' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3160);
                    annotationTypeDeclaration306=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration306.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:645:35: ( ';' )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==26) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: ';'
                            {
                            char_literal307=(Token)match(input,26,FOLLOW_26_in_annotationTypeElementRest3162); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal307_tree = (Object)adaptor.create(char_literal307);
                            adaptor.addChild(root_0, char_literal307_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:648:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final JavaParser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest() throws RecognitionException {
        JavaParser.annotationMethodOrConstantRest_return retval = new JavaParser.annotationMethodOrConstantRest_return();
        retval.start = input.LT(1);
        int annotationMethodOrConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.annotationMethodRest_return annotationMethodRest308 = null;

        JavaParser.annotationConstantRest_return annotationConstantRest309 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:649:5: ( annotationMethodRest | annotationConstantRest )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:649:9: annotationMethodRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3186);
                    annotationMethodRest308=annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodRest308.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:650:9: annotationConstantRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3196);
                    annotationConstantRest309=annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationConstantRest309.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:653:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final JavaParser.annotationMethodRest_return annotationMethodRest() throws RecognitionException {
        JavaParser.annotationMethodRest_return retval = new JavaParser.annotationMethodRest_return();
        retval.start = input.LT(1);
        int annotationMethodRest_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier310=null;
        Token char_literal311=null;
        Token char_literal312=null;
        JavaParser.defaultValue_return defaultValue313 = null;


        Object Identifier310_tree=null;
        Object char_literal311_tree=null;
        Object char_literal312_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:5: ( Identifier '(' ')' ( defaultValue )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:9: Identifier '(' ')' ( defaultValue )?
            {
            root_0 = (Object)adaptor.nil();

            Identifier310=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest3219); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier310_tree = (Object)adaptor.create(Identifier310);
            adaptor.addChild(root_0, Identifier310_tree);
            }
            char_literal311=(Token)match(input,66,FOLLOW_66_in_annotationMethodRest3221); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal311_tree = (Object)adaptor.create(char_literal311);
            adaptor.addChild(root_0, char_literal311_tree);
            }
            char_literal312=(Token)match(input,67,FOLLOW_67_in_annotationMethodRest3223); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal312_tree = (Object)adaptor.create(char_literal312);
            adaptor.addChild(root_0, char_literal312_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:654:28: ( defaultValue )?
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==74) ) {
                alt104=1;
            }
            switch (alt104) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest3225);
                    defaultValue313=defaultValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, defaultValue313.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:657:1: annotationConstantRest : variableDeclarators ;
    public final JavaParser.annotationConstantRest_return annotationConstantRest() throws RecognitionException {
        JavaParser.annotationConstantRest_return retval = new JavaParser.annotationConstantRest_return();
        retval.start = input.LT(1);
        int annotationConstantRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableDeclarators_return variableDeclarators314 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:5: ( variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:658:9: variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest3249);
            variableDeclarators314=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators314.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:661:1: defaultValue : 'default' elementValue ;
    public final JavaParser.defaultValue_return defaultValue() throws RecognitionException {
        JavaParser.defaultValue_return retval = new JavaParser.defaultValue_return();
        retval.start = input.LT(1);
        int defaultValue_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal315=null;
        JavaParser.elementValue_return elementValue316 = null;


        Object string_literal315_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:5: ( 'default' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:662:9: 'default' elementValue
            {
            root_0 = (Object)adaptor.nil();

            string_literal315=(Token)match(input,74,FOLLOW_74_in_defaultValue3272); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal315_tree = (Object)adaptor.create(string_literal315);
            adaptor.addChild(root_0, string_literal315_tree);
            }
            pushFollow(FOLLOW_elementValue_in_defaultValue3274);
            elementValue316=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue316.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:667:1: block : '{' ( blockStatement )* '}' ;
    public final JavaParser.block_return block() throws RecognitionException {
        JavaParser.block_return retval = new JavaParser.block_return();
        retval.start = input.LT(1);
        int block_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal317=null;
        Token char_literal319=null;
        JavaParser.blockStatement_return blockStatement318 = null;


        Object char_literal317_tree=null;
        Object char_literal319_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:5: ( '{' ( blockStatement )* '}' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:9: '{' ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal317=(Token)match(input,44,FOLLOW_44_in_block3295); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal317_tree = (Object)adaptor.create(char_literal317);
            adaptor.addChild(root_0, char_literal317_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:668:13: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_block3297);
            	    blockStatement318=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement318.getTree());

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);

            char_literal319=(Token)match(input,45,FOLLOW_45_in_block3300); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal319_tree = (Object)adaptor.create(char_literal319);
            adaptor.addChild(root_0, char_literal319_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:671:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final JavaParser.blockStatement_return blockStatement() throws RecognitionException {
        JavaParser.blockStatement_return retval = new JavaParser.blockStatement_return();
        retval.start = input.LT(1);
        int blockStatement_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement320 = null;

        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration321 = null;

        JavaParser.statement_return statement322 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:672:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt106=3;
            alt106 = dfa106.predict(input);
            switch (alt106) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:672:9: localVariableDeclarationStatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement3323);
                    localVariableDeclarationStatement320=localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement320.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement3333);
                    classOrInterfaceDeclaration321=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration321.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:674:9: statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statement_in_blockStatement3343);
                    statement322=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement322.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:677:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        JavaParser.localVariableDeclarationStatement_return retval = new JavaParser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);
        int localVariableDeclarationStatement_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal324=null;
        JavaParser.localVariableDeclaration_return localVariableDeclaration323 = null;


        Object char_literal324_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:678:5: ( localVariableDeclaration ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:678:10: localVariableDeclaration ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement3367);
            localVariableDeclaration323=localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration323.getTree());
            char_literal324=(Token)match(input,26,FOLLOW_26_in_localVariableDeclarationStatement3369); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:681:1: localVariableDeclaration : variableModifiers type variableDeclarators ;
    public final JavaParser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        JavaParser.localVariableDeclaration_return retval = new JavaParser.localVariableDeclaration_return();
        retval.start = input.LT(1);
        int localVariableDeclaration_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers325 = null;

        JavaParser.type_return type326 = null;

        JavaParser.variableDeclarators_return variableDeclarators327 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:682:5: ( variableModifiers type variableDeclarators )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:682:9: variableModifiers type variableDeclarators
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration3388);
            variableModifiers325=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers325.getTree());
            pushFollow(FOLLOW_type_in_localVariableDeclaration3390);
            type326=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type326.getTree());
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration3392);
            variableDeclarators327=variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarators327.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:685:1: variableModifiers : ( variableModifier )* ;
    public final JavaParser.variableModifiers_return variableModifiers() throws RecognitionException {
        JavaParser.variableModifiers_return retval = new JavaParser.variableModifiers_return();
        retval.start = input.LT(1);
        int variableModifiers_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifier_return variableModifier328 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:686:5: ( ( variableModifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:686:9: ( variableModifier )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:686:9: ( variableModifier )*
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
            	    pushFollow(FOLLOW_variableModifier_in_variableModifiers3415);
            	    variableModifier328=variableModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifier328.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:689:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );
    public final JavaParser.statement_return statement() throws RecognitionException {
        JavaParser.statement_return retval = new JavaParser.statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        Object root_0 = null;

        Token ASSERT330=null;
        Token char_literal332=null;
        Token char_literal334=null;
        Token string_literal335=null;
        Token string_literal338=null;
        Token string_literal340=null;
        Token char_literal341=null;
        Token char_literal343=null;
        Token string_literal345=null;
        Token string_literal348=null;
        Token string_literal350=null;
        Token char_literal352=null;
        Token string_literal353=null;
        Token string_literal356=null;
        Token string_literal359=null;
        Token string_literal361=null;
        Token char_literal363=null;
        Token char_literal365=null;
        Token string_literal366=null;
        Token string_literal369=null;
        Token char_literal371=null;
        Token string_literal372=null;
        Token char_literal374=null;
        Token string_literal375=null;
        Token Identifier376=null;
        Token char_literal377=null;
        Token string_literal378=null;
        Token Identifier379=null;
        Token char_literal380=null;
        Token char_literal381=null;
        Token char_literal383=null;
        Token Identifier384=null;
        Token char_literal385=null;
        JavaParser.block_return block329 = null;

        JavaParser.expression_return expression331 = null;

        JavaParser.expression_return expression333 = null;

        JavaParser.parExpression_return parExpression336 = null;

        JavaParser.statement_return statement337 = null;

        JavaParser.statement_return statement339 = null;

        JavaParser.forControl_return forControl342 = null;

        JavaParser.statement_return statement344 = null;

        JavaParser.parExpression_return parExpression346 = null;

        JavaParser.statement_return statement347 = null;

        JavaParser.statement_return statement349 = null;

        JavaParser.parExpression_return parExpression351 = null;

        JavaParser.block_return block354 = null;

        JavaParser.catches_return catches355 = null;

        JavaParser.block_return block357 = null;

        JavaParser.catches_return catches358 = null;

        JavaParser.block_return block360 = null;

        JavaParser.parExpression_return parExpression362 = null;

        JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups364 = null;

        JavaParser.parExpression_return parExpression367 = null;

        JavaParser.block_return block368 = null;

        JavaParser.expression_return expression370 = null;

        JavaParser.expression_return expression373 = null;

        JavaParser.statementExpression_return statementExpression382 = null;

        JavaParser.statement_return statement386 = null;


        Object ASSERT330_tree=null;
        Object char_literal332_tree=null;
        Object char_literal334_tree=null;
        Object string_literal335_tree=null;
        Object string_literal338_tree=null;
        Object string_literal340_tree=null;
        Object char_literal341_tree=null;
        Object char_literal343_tree=null;
        Object string_literal345_tree=null;
        Object string_literal348_tree=null;
        Object string_literal350_tree=null;
        Object char_literal352_tree=null;
        Object string_literal353_tree=null;
        Object string_literal356_tree=null;
        Object string_literal359_tree=null;
        Object string_literal361_tree=null;
        Object char_literal363_tree=null;
        Object char_literal365_tree=null;
        Object string_literal366_tree=null;
        Object string_literal369_tree=null;
        Object char_literal371_tree=null;
        Object string_literal372_tree=null;
        Object char_literal374_tree=null;
        Object string_literal375_tree=null;
        Object Identifier376_tree=null;
        Object char_literal377_tree=null;
        Object string_literal378_tree=null;
        Object Identifier379_tree=null;
        Object char_literal380_tree=null;
        Object char_literal381_tree=null;
        Object char_literal383_tree=null;
        Object Identifier384_tree=null;
        Object char_literal385_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:690:5: ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement )
            int alt114=16;
            alt114 = dfa114.predict(input);
            switch (alt114) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:690:7: block
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_block_in_statement3433);
                    block329=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block329.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:691:9: ASSERT expression ( ':' expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    ASSERT330=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement3443); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSERT330_tree = (Object)adaptor.create(ASSERT330);
                    adaptor.addChild(root_0, ASSERT330_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement3445);
                    expression331=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression331.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:691:27: ( ':' expression )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==75) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:691:28: ':' expression
                            {
                            char_literal332=(Token)match(input,75,FOLLOW_75_in_statement3448); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal332_tree = (Object)adaptor.create(char_literal332);
                            adaptor.addChild(root_0, char_literal332_tree);
                            }
                            pushFollow(FOLLOW_expression_in_statement3450);
                            expression333=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression333.getTree());

                            }
                            break;

                    }

                    char_literal334=(Token)match(input,26,FOLLOW_26_in_statement3454); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal334_tree = (Object)adaptor.create(char_literal334);
                    adaptor.addChild(root_0, char_literal334_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:9: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal335=(Token)match(input,76,FOLLOW_76_in_statement3464); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal335_tree = (Object)adaptor.create(string_literal335);
                    adaptor.addChild(root_0, string_literal335_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3466);
                    parExpression336=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression336.getTree());
                    pushFollow(FOLLOW_statement_in_statement3468);
                    statement337=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement337.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:38: ( options {k=1; } : 'else' statement )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==77) ) {
                        int LA109_2 = input.LA(2);

                        if ( (synpred157_Java()) ) {
                            alt109=1;
                        }
                    }
                    switch (alt109) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:54: 'else' statement
                            {
                            string_literal338=(Token)match(input,77,FOLLOW_77_in_statement3478); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal338_tree = (Object)adaptor.create(string_literal338);
                            adaptor.addChild(root_0, string_literal338_tree);
                            }
                            pushFollow(FOLLOW_statement_in_statement3480);
                            statement339=statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, statement339.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:693:9: 'for' '(' forControl ')' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal340=(Token)match(input,78,FOLLOW_78_in_statement3492); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal340_tree = (Object)adaptor.create(string_literal340);
                    adaptor.addChild(root_0, string_literal340_tree);
                    }
                    char_literal341=(Token)match(input,66,FOLLOW_66_in_statement3494); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal341_tree = (Object)adaptor.create(char_literal341);
                    adaptor.addChild(root_0, char_literal341_tree);
                    }
                    pushFollow(FOLLOW_forControl_in_statement3496);
                    forControl342=forControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, forControl342.getTree());
                    char_literal343=(Token)match(input,67,FOLLOW_67_in_statement3498); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal343_tree = (Object)adaptor.create(char_literal343);
                    adaptor.addChild(root_0, char_literal343_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement3500);
                    statement344=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement344.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:694:9: 'while' parExpression statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal345=(Token)match(input,79,FOLLOW_79_in_statement3510); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal345_tree = (Object)adaptor.create(string_literal345);
                    adaptor.addChild(root_0, string_literal345_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3512);
                    parExpression346=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression346.getTree());
                    pushFollow(FOLLOW_statement_in_statement3514);
                    statement347=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement347.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:695:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal348=(Token)match(input,80,FOLLOW_80_in_statement3524); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal348_tree = (Object)adaptor.create(string_literal348);
                    adaptor.addChild(root_0, string_literal348_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement3526);
                    statement349=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement349.getTree());
                    string_literal350=(Token)match(input,79,FOLLOW_79_in_statement3528); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal350_tree = (Object)adaptor.create(string_literal350);
                    adaptor.addChild(root_0, string_literal350_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3530);
                    parExpression351=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression351.getTree());
                    char_literal352=(Token)match(input,26,FOLLOW_26_in_statement3532); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal352_tree = (Object)adaptor.create(char_literal352);
                    adaptor.addChild(root_0, char_literal352_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:696:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal353=(Token)match(input,81,FOLLOW_81_in_statement3542); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal353_tree = (Object)adaptor.create(string_literal353);
                    adaptor.addChild(root_0, string_literal353_tree);
                    }
                    pushFollow(FOLLOW_block_in_statement3544);
                    block354=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block354.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:9: ( catches 'finally' block | catches | 'finally' block )
                    int alt110=3;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==88) ) {
                        int LA110_1 = input.LA(2);

                        if ( (synpred162_Java()) ) {
                            alt110=1;
                        }
                        else if ( (synpred163_Java()) ) {
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:11: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement3556);
                            catches355=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches355.getTree());
                            string_literal356=(Token)match(input,82,FOLLOW_82_in_statement3558); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal356_tree = (Object)adaptor.create(string_literal356);
                            adaptor.addChild(root_0, string_literal356_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement3560);
                            block357=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block357.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:11: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement3572);
                            catches358=catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, catches358.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:699:13: 'finally' block
                            {
                            string_literal359=(Token)match(input,82,FOLLOW_82_in_statement3586); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal359_tree = (Object)adaptor.create(string_literal359);
                            adaptor.addChild(root_0, string_literal359_tree);
                            }
                            pushFollow(FOLLOW_block_in_statement3588);
                            block360=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block360.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:701:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal361=(Token)match(input,83,FOLLOW_83_in_statement3608); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal361_tree = (Object)adaptor.create(string_literal361);
                    adaptor.addChild(root_0, string_literal361_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3610);
                    parExpression362=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression362.getTree());
                    char_literal363=(Token)match(input,44,FOLLOW_44_in_statement3612); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal363_tree = (Object)adaptor.create(char_literal363);
                    adaptor.addChild(root_0, char_literal363_tree);
                    }
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement3614);
                    switchBlockStatementGroups364=switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups364.getTree());
                    char_literal365=(Token)match(input,45,FOLLOW_45_in_statement3616); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal365_tree = (Object)adaptor.create(char_literal365);
                    adaptor.addChild(root_0, char_literal365_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:702:9: 'synchronized' parExpression block
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal366=(Token)match(input,53,FOLLOW_53_in_statement3626); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal366_tree = (Object)adaptor.create(string_literal366);
                    adaptor.addChild(root_0, string_literal366_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement3628);
                    parExpression367=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression367.getTree());
                    pushFollow(FOLLOW_block_in_statement3630);
                    block368=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block368.getTree());

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:9: 'return' ( expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal369=(Token)match(input,84,FOLLOW_84_in_statement3640); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal369_tree = (Object)adaptor.create(string_literal369);
                    adaptor.addChild(root_0, string_literal369_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:703:18: ( expression )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==Identifier||(LA111_0>=FloatingPointLiteral && LA111_0<=DecimalLiteral)||LA111_0==47||(LA111_0>=56 && LA111_0<=63)||(LA111_0>=65 && LA111_0<=66)||(LA111_0>=69 && LA111_0<=72)||(LA111_0>=105 && LA111_0<=106)||(LA111_0>=109 && LA111_0<=113)) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement3642);
                            expression370=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression370.getTree());

                            }
                            break;

                    }

                    char_literal371=(Token)match(input,26,FOLLOW_26_in_statement3645); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal371_tree = (Object)adaptor.create(char_literal371);
                    adaptor.addChild(root_0, char_literal371_tree);
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:704:9: 'throw' expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal372=(Token)match(input,85,FOLLOW_85_in_statement3655); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal372_tree = (Object)adaptor.create(string_literal372);
                    adaptor.addChild(root_0, string_literal372_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement3657);
                    expression373=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression373.getTree());
                    char_literal374=(Token)match(input,26,FOLLOW_26_in_statement3659); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal374_tree = (Object)adaptor.create(char_literal374);
                    adaptor.addChild(root_0, char_literal374_tree);
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:705:9: 'break' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal375=(Token)match(input,86,FOLLOW_86_in_statement3669); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal375_tree = (Object)adaptor.create(string_literal375);
                    adaptor.addChild(root_0, string_literal375_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:705:17: ( Identifier )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier376=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement3671); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier376_tree = (Object)adaptor.create(Identifier376);
                            adaptor.addChild(root_0, Identifier376_tree);
                            }

                            }
                            break;

                    }

                    char_literal377=(Token)match(input,26,FOLLOW_26_in_statement3674); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal377_tree = (Object)adaptor.create(char_literal377);
                    adaptor.addChild(root_0, char_literal377_tree);
                    }

                    }
                    break;
                case 13 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:706:9: 'continue' ( Identifier )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal378=(Token)match(input,87,FOLLOW_87_in_statement3684); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal378_tree = (Object)adaptor.create(string_literal378);
                    adaptor.addChild(root_0, string_literal378_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:706:20: ( Identifier )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==Identifier) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            Identifier379=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement3686); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Identifier379_tree = (Object)adaptor.create(Identifier379);
                            adaptor.addChild(root_0, Identifier379_tree);
                            }

                            }
                            break;

                    }

                    char_literal380=(Token)match(input,26,FOLLOW_26_in_statement3689); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal380_tree = (Object)adaptor.create(char_literal380);
                    adaptor.addChild(root_0, char_literal380_tree);
                    }

                    }
                    break;
                case 14 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:707:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal381=(Token)match(input,26,FOLLOW_26_in_statement3699); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal381_tree = (Object)adaptor.create(char_literal381);
                    adaptor.addChild(root_0, char_literal381_tree);
                    }

                    }
                    break;
                case 15 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:708:9: statementExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statementExpression_in_statement3710);
                    statementExpression382=statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementExpression382.getTree());
                    char_literal383=(Token)match(input,26,FOLLOW_26_in_statement3712); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal383_tree = (Object)adaptor.create(char_literal383);
                    adaptor.addChild(root_0, char_literal383_tree);
                    }

                    }
                    break;
                case 16 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:709:9: Identifier ':' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier384=(Token)match(input,Identifier,FOLLOW_Identifier_in_statement3722); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier384_tree = (Object)adaptor.create(Identifier384);
                    adaptor.addChild(root_0, Identifier384_tree);
                    }
                    char_literal385=(Token)match(input,75,FOLLOW_75_in_statement3724); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal385_tree = (Object)adaptor.create(char_literal385);
                    adaptor.addChild(root_0, char_literal385_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement3726);
                    statement386=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement386.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:712:1: catches : catchClause ( catchClause )* ;
    public final JavaParser.catches_return catches() throws RecognitionException {
        JavaParser.catches_return retval = new JavaParser.catches_return();
        retval.start = input.LT(1);
        int catches_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.catchClause_return catchClause387 = null;

        JavaParser.catchClause_return catchClause388 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:5: ( catchClause ( catchClause )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:9: catchClause ( catchClause )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_catchClause_in_catches3749);
            catchClause387=catchClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause387.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:21: ( catchClause )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==88) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:713:22: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches3752);
            	    catchClause388=catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause388.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:716:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final JavaParser.catchClause_return catchClause() throws RecognitionException {
        JavaParser.catchClause_return retval = new JavaParser.catchClause_return();
        retval.start = input.LT(1);
        int catchClause_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal389=null;
        Token char_literal390=null;
        Token char_literal392=null;
        JavaParser.formalParameter_return formalParameter391 = null;

        JavaParser.block_return block393 = null;


        Object string_literal389_tree=null;
        Object char_literal390_tree=null;
        Object char_literal392_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:717:5: ( 'catch' '(' formalParameter ')' block )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:717:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (Object)adaptor.nil();

            string_literal389=(Token)match(input,88,FOLLOW_88_in_catchClause3777); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal389_tree = (Object)adaptor.create(string_literal389);
            adaptor.addChild(root_0, string_literal389_tree);
            }
            char_literal390=(Token)match(input,66,FOLLOW_66_in_catchClause3779); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal390_tree = (Object)adaptor.create(char_literal390);
            adaptor.addChild(root_0, char_literal390_tree);
            }
            pushFollow(FOLLOW_formalParameter_in_catchClause3781);
            formalParameter391=formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameter391.getTree());
            char_literal392=(Token)match(input,67,FOLLOW_67_in_catchClause3783); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal392_tree = (Object)adaptor.create(char_literal392);
            adaptor.addChild(root_0, char_literal392_tree);
            }
            pushFollow(FOLLOW_block_in_catchClause3785);
            block393=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block393.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:720:1: formalParameter : variableModifiers type variableDeclaratorId ;
    public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
        JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.variableModifiers_return variableModifiers394 = null;

        JavaParser.type_return type395 = null;

        JavaParser.variableDeclaratorId_return variableDeclaratorId396 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:721:5: ( variableModifiers type variableDeclaratorId )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:721:9: variableModifiers type variableDeclaratorId
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameter3804);
            variableModifiers394=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers394.getTree());
            pushFollow(FOLLOW_type_in_formalParameter3806);
            type395=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type395.getTree());
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter3808);
            variableDeclaratorId396=variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclaratorId396.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:724:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        JavaParser.switchBlockStatementGroups_return retval = new JavaParser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroups_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup397 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:5: ( ( switchBlockStatementGroup )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:9: ( switchBlockStatementGroup )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:9: ( switchBlockStatementGroup )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==74||LA116_0==89) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:725:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups3836);
            	    switchBlockStatementGroup397=switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup397.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:732:1: switchBlockStatementGroup : ( switchLabel )+ ( blockStatement )* ;
    public final JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        JavaParser.switchBlockStatementGroup_return retval = new JavaParser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroup_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.switchLabel_return switchLabel398 = null;

        JavaParser.blockStatement_return blockStatement399 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:5: ( ( switchLabel )+ ( blockStatement )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:9: ( switchLabel )+ ( blockStatement )*
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:9: ( switchLabel )+
            int cnt117=0;
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==89) ) {
                    int LA117_2 = input.LA(2);

                    if ( (synpred178_Java()) ) {
                        alt117=1;
                    }


                }
                else if ( (LA117_0==74) ) {
                    int LA117_3 = input.LA(2);

                    if ( (synpred178_Java()) ) {
                        alt117=1;
                    }


                }


                switch (alt117) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: switchLabel
            	    {
            	    pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup3863);
            	    switchLabel398=switchLabel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchLabel398.getTree());

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

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:22: ( blockStatement )*
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
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup3866);
            	    blockStatement399=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement399.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:736:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final JavaParser.switchLabel_return switchLabel() throws RecognitionException {
        JavaParser.switchLabel_return retval = new JavaParser.switchLabel_return();
        retval.start = input.LT(1);
        int switchLabel_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal400=null;
        Token char_literal402=null;
        Token string_literal403=null;
        Token char_literal405=null;
        Token string_literal406=null;
        Token char_literal407=null;
        JavaParser.constantExpression_return constantExpression401 = null;

        JavaParser.enumConstantName_return enumConstantName404 = null;


        Object string_literal400_tree=null;
        Object char_literal402_tree=null;
        Object string_literal403_tree=null;
        Object char_literal405_tree=null;
        Object string_literal406_tree=null;
        Object char_literal407_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:737:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
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

                        if ( (synpred180_Java()) ) {
                            alt119=1;
                        }
                        else if ( (synpred181_Java()) ) {
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:737:9: 'case' constantExpression ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal400=(Token)match(input,89,FOLLOW_89_in_switchLabel3890); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal400_tree = (Object)adaptor.create(string_literal400);
                    adaptor.addChild(root_0, string_literal400_tree);
                    }
                    pushFollow(FOLLOW_constantExpression_in_switchLabel3892);
                    constantExpression401=constantExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constantExpression401.getTree());
                    char_literal402=(Token)match(input,75,FOLLOW_75_in_switchLabel3894); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal402_tree = (Object)adaptor.create(char_literal402);
                    adaptor.addChild(root_0, char_literal402_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:738:9: 'case' enumConstantName ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal403=(Token)match(input,89,FOLLOW_89_in_switchLabel3904); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal403_tree = (Object)adaptor.create(string_literal403);
                    adaptor.addChild(root_0, string_literal403_tree);
                    }
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel3906);
                    enumConstantName404=enumConstantName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstantName404.getTree());
                    char_literal405=(Token)match(input,75,FOLLOW_75_in_switchLabel3908); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal405_tree = (Object)adaptor.create(char_literal405);
                    adaptor.addChild(root_0, char_literal405_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:739:9: 'default' ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal406=(Token)match(input,74,FOLLOW_74_in_switchLabel3918); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal406_tree = (Object)adaptor.create(string_literal406);
                    adaptor.addChild(root_0, string_literal406_tree);
                    }
                    char_literal407=(Token)match(input,75,FOLLOW_75_in_switchLabel3920); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal407_tree = (Object)adaptor.create(char_literal407);
                    adaptor.addChild(root_0, char_literal407_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:742:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final JavaParser.forControl_return forControl() throws RecognitionException {
        JavaParser.forControl_return retval = new JavaParser.forControl_return();
        retval.start = input.LT(1);
        int forControl_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal410=null;
        Token char_literal412=null;
        JavaParser.enhancedForControl_return enhancedForControl408 = null;

        JavaParser.forInit_return forInit409 = null;

        JavaParser.expression_return expression411 = null;

        JavaParser.forUpdate_return forUpdate413 = null;


        Object char_literal410_tree=null;
        Object char_literal412_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:744:5: ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt123=2;
            alt123 = dfa123.predict(input);
            switch (alt123) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:744:9: enhancedForControl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enhancedForControl_in_forControl3951);
                    enhancedForControl408=enhancedForControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enhancedForControl408.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:9: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:9: ( forInit )?
                    int alt120=2;
                    int LA120_0 = input.LA(1);

                    if ( (LA120_0==Identifier||(LA120_0>=FloatingPointLiteral && LA120_0<=DecimalLiteral)||LA120_0==35||LA120_0==47||(LA120_0>=56 && LA120_0<=63)||(LA120_0>=65 && LA120_0<=66)||(LA120_0>=69 && LA120_0<=73)||(LA120_0>=105 && LA120_0<=106)||(LA120_0>=109 && LA120_0<=113)) ) {
                        alt120=1;
                    }
                    switch (alt120) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl3961);
                            forInit409=forInit();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forInit409.getTree());

                            }
                            break;

                    }

                    char_literal410=(Token)match(input,26,FOLLOW_26_in_forControl3964); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal410_tree = (Object)adaptor.create(char_literal410);
                    adaptor.addChild(root_0, char_literal410_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:22: ( expression )?
                    int alt121=2;
                    int LA121_0 = input.LA(1);

                    if ( (LA121_0==Identifier||(LA121_0>=FloatingPointLiteral && LA121_0<=DecimalLiteral)||LA121_0==47||(LA121_0>=56 && LA121_0<=63)||(LA121_0>=65 && LA121_0<=66)||(LA121_0>=69 && LA121_0<=72)||(LA121_0>=105 && LA121_0<=106)||(LA121_0>=109 && LA121_0<=113)) ) {
                        alt121=1;
                    }
                    switch (alt121) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl3966);
                            expression411=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression411.getTree());

                            }
                            break;

                    }

                    char_literal412=(Token)match(input,26,FOLLOW_26_in_forControl3969); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal412_tree = (Object)adaptor.create(char_literal412);
                    adaptor.addChild(root_0, char_literal412_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:745:38: ( forUpdate )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==47||(LA122_0>=56 && LA122_0<=63)||(LA122_0>=65 && LA122_0<=66)||(LA122_0>=69 && LA122_0<=72)||(LA122_0>=105 && LA122_0<=106)||(LA122_0>=109 && LA122_0<=113)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl3971);
                            forUpdate413=forUpdate();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forUpdate413.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:748:1: forInit : ( localVariableDeclaration | expressionList );
    public final JavaParser.forInit_return forInit() throws RecognitionException {
        JavaParser.forInit_return retval = new JavaParser.forInit_return();
        retval.start = input.LT(1);
        int forInit_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.localVariableDeclaration_return localVariableDeclaration414 = null;

        JavaParser.expressionList_return expressionList415 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:5: ( localVariableDeclaration | expressionList )
            int alt124=2;
            alt124 = dfa124.predict(input);
            switch (alt124) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:9: localVariableDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit3991);
                    localVariableDeclaration414=localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration414.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:750:9: expressionList
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expressionList_in_forInit4001);
                    expressionList415=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList415.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:753:1: enhancedForControl : variableModifiers type Identifier ':' expression ;
    public final JavaParser.enhancedForControl_return enhancedForControl() throws RecognitionException {
        JavaParser.enhancedForControl_return retval = new JavaParser.enhancedForControl_return();
        retval.start = input.LT(1);
        int enhancedForControl_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier418=null;
        Token char_literal419=null;
        JavaParser.variableModifiers_return variableModifiers416 = null;

        JavaParser.type_return type417 = null;

        JavaParser.expression_return expression420 = null;


        Object Identifier418_tree=null;
        Object char_literal419_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:5: ( variableModifiers type Identifier ':' expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:754:9: variableModifiers type Identifier ':' expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_enhancedForControl4024);
            variableModifiers416=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers416.getTree());
            pushFollow(FOLLOW_type_in_enhancedForControl4026);
            type417=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type417.getTree());
            Identifier418=(Token)match(input,Identifier,FOLLOW_Identifier_in_enhancedForControl4028); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier418_tree = (Object)adaptor.create(Identifier418);
            adaptor.addChild(root_0, Identifier418_tree);
            }
            char_literal419=(Token)match(input,75,FOLLOW_75_in_enhancedForControl4030); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal419_tree = (Object)adaptor.create(char_literal419);
            adaptor.addChild(root_0, char_literal419_tree);
            }
            pushFollow(FOLLOW_expression_in_enhancedForControl4032);
            expression420=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression420.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:757:1: forUpdate : expressionList ;
    public final JavaParser.forUpdate_return forUpdate() throws RecognitionException {
        JavaParser.forUpdate_return retval = new JavaParser.forUpdate_return();
        retval.start = input.LT(1);
        int forUpdate_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expressionList_return expressionList421 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:5: ( expressionList )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:758:9: expressionList
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expressionList_in_forUpdate4051);
            expressionList421=expressionList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList421.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:763:1: parExpression : '(' expression ')' ;
    public final JavaParser.parExpression_return parExpression() throws RecognitionException {
        JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal422=null;
        Token char_literal424=null;
        JavaParser.expression_return expression423 = null;


        Object char_literal422_tree=null;
        Object char_literal424_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:764:5: ( '(' expression ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:764:9: '(' expression ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal422=(Token)match(input,66,FOLLOW_66_in_parExpression4072); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal422_tree = (Object)adaptor.create(char_literal422);
            adaptor.addChild(root_0, char_literal422_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression4074);
            expression423=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression423.getTree());
            char_literal424=(Token)match(input,67,FOLLOW_67_in_parExpression4076); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal424_tree = (Object)adaptor.create(char_literal424);
            adaptor.addChild(root_0, char_literal424_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:767:1: expressionList : expression ( ',' expression )* ;
    public final JavaParser.expressionList_return expressionList() throws RecognitionException {
        JavaParser.expressionList_return retval = new JavaParser.expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal426=null;
        JavaParser.expression_return expression425 = null;

        JavaParser.expression_return expression427 = null;


        Object char_literal426_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:5: ( expression ( ',' expression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:9: expression ( ',' expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList4099);
            expression425=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression425.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:20: ( ',' expression )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( (LA125_0==41) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:768:21: ',' expression
            	    {
            	    char_literal426=(Token)match(input,41,FOLLOW_41_in_expressionList4102); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal426_tree = (Object)adaptor.create(char_literal426);
            	    adaptor.addChild(root_0, char_literal426_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList4104);
            	    expression427=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression427.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:771:1: statementExpression : expression ;
    public final JavaParser.statementExpression_return statementExpression() throws RecognitionException {
        JavaParser.statementExpression_return retval = new JavaParser.statementExpression_return();
        retval.start = input.LT(1);
        int statementExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression428 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:772:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_statementExpression4125);
            expression428=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression428.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:775:1: constantExpression : expression ;
    public final JavaParser.constantExpression_return constantExpression() throws RecognitionException {
        JavaParser.constantExpression_return retval = new JavaParser.constantExpression_return();
        retval.start = input.LT(1);
        int constantExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.expression_return expression429 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:776:5: ( expression )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:776:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_constantExpression4148);
            expression429=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression429.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:779:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final JavaParser.expression_return expression() throws RecognitionException {
        JavaParser.expression_return retval = new JavaParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression430 = null;

        JavaParser.assignmentOperator_return assignmentOperator431 = null;

        JavaParser.expression_return expression432 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:5: ( conditionalExpression ( assignmentOperator expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:9: conditionalExpression ( assignmentOperator expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression4171);
            conditionalExpression430=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression430.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:31: ( assignmentOperator expression )?
            int alt126=2;
            alt126 = dfa126.predict(input);
            switch (alt126) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:32: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression4174);
                    assignmentOperator431=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentOperator431.getTree());
                    pushFollow(FOLLOW_expression_in_expression4176);
                    expression432=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression432.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:783:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);
    public final JavaParser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        JavaParser.assignmentOperator_return retval = new JavaParser.assignmentOperator_return();
        retval.start = input.LT(1);
        int assignmentOperator_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token char_literal433=null;
        Token string_literal434=null;
        Token string_literal435=null;
        Token string_literal436=null;
        Token string_literal437=null;
        Token string_literal438=null;
        Token string_literal439=null;
        Token string_literal440=null;
        Token string_literal441=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object t3_tree=null;
        Object t4_tree=null;
        Object char_literal433_tree=null;
        Object string_literal434_tree=null;
        Object string_literal435_tree=null;
        Object string_literal436_tree=null;
        Object string_literal437_tree=null;
        Object string_literal438_tree=null;
        Object string_literal439_tree=null;
        Object string_literal440_tree=null;
        Object string_literal441_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:784:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?)
            int alt127=12;
            alt127 = dfa127.predict(input);
            switch (alt127) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:784:9: '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal433=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4201); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal433_tree = (Object)adaptor.create(char_literal433);
                    adaptor.addChild(root_0, char_literal433_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:785:9: '+='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal434=(Token)match(input,90,FOLLOW_90_in_assignmentOperator4211); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal434_tree = (Object)adaptor.create(string_literal434);
                    adaptor.addChild(root_0, string_literal434_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:786:9: '-='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal435=(Token)match(input,91,FOLLOW_91_in_assignmentOperator4221); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal435_tree = (Object)adaptor.create(string_literal435);
                    adaptor.addChild(root_0, string_literal435_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:787:9: '*='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal436=(Token)match(input,92,FOLLOW_92_in_assignmentOperator4231); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal436_tree = (Object)adaptor.create(string_literal436);
                    adaptor.addChild(root_0, string_literal436_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:788:9: '/='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal437=(Token)match(input,93,FOLLOW_93_in_assignmentOperator4241); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal437_tree = (Object)adaptor.create(string_literal437);
                    adaptor.addChild(root_0, string_literal437_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:789:9: '&='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal438=(Token)match(input,94,FOLLOW_94_in_assignmentOperator4251); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal438_tree = (Object)adaptor.create(string_literal438);
                    adaptor.addChild(root_0, string_literal438_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:790:9: '|='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal439=(Token)match(input,95,FOLLOW_95_in_assignmentOperator4261); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal439_tree = (Object)adaptor.create(string_literal439);
                    adaptor.addChild(root_0, string_literal439_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:791:9: '^='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal440=(Token)match(input,96,FOLLOW_96_in_assignmentOperator4271); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal440_tree = (Object)adaptor.create(string_literal440);
                    adaptor.addChild(root_0, string_literal440_tree);
                    }

                    }
                    break;
                case 9 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:792:9: '%='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal441=(Token)match(input,97,FOLLOW_97_in_assignmentOperator4281); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal441_tree = (Object)adaptor.create(string_literal441);
                    adaptor.addChild(root_0, string_literal441_tree);
                    }

                    }
                    break;
                case 10 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:793:9: ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_assignmentOperator4302); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_assignmentOperator4306); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4310); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:798:9: ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4344); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4348); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4352); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t3_tree = (Object)adaptor.create(t3);
                    adaptor.addChild(root_0, t3_tree);
                    }
                    t4=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4356); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:9: ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4387); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4391); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4395); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:812:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final JavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JavaParser.conditionalExpression_return retval = new JavaParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal443=null;
        Token char_literal445=null;
        JavaParser.conditionalOrExpression_return conditionalOrExpression442 = null;

        JavaParser.expression_return expression444 = null;

        JavaParser.expression_return expression446 = null;


        Object char_literal443_tree=null;
        Object char_literal445_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression4424);
            conditionalOrExpression442=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression442.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:33: ( '?' expression ':' expression )?
            int alt128=2;
            int LA128_0 = input.LA(1);

            if ( (LA128_0==64) ) {
                alt128=1;
            }
            switch (alt128) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:813:35: '?' expression ':' expression
                    {
                    char_literal443=(Token)match(input,64,FOLLOW_64_in_conditionalExpression4428); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal443_tree = (Object)adaptor.create(char_literal443);
                    adaptor.addChild(root_0, char_literal443_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression4430);
                    expression444=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression444.getTree());
                    char_literal445=(Token)match(input,75,FOLLOW_75_in_conditionalExpression4432); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal445_tree = (Object)adaptor.create(char_literal445);
                    adaptor.addChild(root_0, char_literal445_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression4434);
                    expression446=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression446.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:816:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final JavaParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JavaParser.conditionalOrExpression_return retval = new JavaParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal448=null;
        JavaParser.conditionalAndExpression_return conditionalAndExpression447 = null;

        JavaParser.conditionalAndExpression_return conditionalAndExpression449 = null;


        Object string_literal448_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4456);
            conditionalAndExpression447=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression447.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:34: ( '||' conditionalAndExpression )*
            loop129:
            do {
                int alt129=2;
                int LA129_0 = input.LA(1);

                if ( (LA129_0==98) ) {
                    alt129=1;
                }


                switch (alt129) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:817:36: '||' conditionalAndExpression
            	    {
            	    string_literal448=(Token)match(input,98,FOLLOW_98_in_conditionalOrExpression4460); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal448_tree = (Object)adaptor.create(string_literal448);
            	    adaptor.addChild(root_0, string_literal448_tree);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4462);
            	    conditionalAndExpression449=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression449.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:820:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final JavaParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JavaParser.conditionalAndExpression_return retval = new JavaParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal451=null;
        JavaParser.inclusiveOrExpression_return inclusiveOrExpression450 = null;

        JavaParser.inclusiveOrExpression_return inclusiveOrExpression452 = null;


        Object string_literal451_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4484);
            inclusiveOrExpression450=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression450.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:31: ( '&&' inclusiveOrExpression )*
            loop130:
            do {
                int alt130=2;
                int LA130_0 = input.LA(1);

                if ( (LA130_0==99) ) {
                    alt130=1;
                }


                switch (alt130) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:821:33: '&&' inclusiveOrExpression
            	    {
            	    string_literal451=(Token)match(input,99,FOLLOW_99_in_conditionalAndExpression4488); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal451_tree = (Object)adaptor.create(string_literal451);
            	    adaptor.addChild(root_0, string_literal451_tree);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4490);
            	    inclusiveOrExpression452=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression452.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:824:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final JavaParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JavaParser.inclusiveOrExpression_return retval = new JavaParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal454=null;
        JavaParser.exclusiveOrExpression_return exclusiveOrExpression453 = null;

        JavaParser.exclusiveOrExpression_return exclusiveOrExpression455 = null;


        Object char_literal454_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4512);
            exclusiveOrExpression453=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression453.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:31: ( '|' exclusiveOrExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==100) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:825:33: '|' exclusiveOrExpression
            	    {
            	    char_literal454=(Token)match(input,100,FOLLOW_100_in_inclusiveOrExpression4516); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal454_tree = (Object)adaptor.create(char_literal454);
            	    adaptor.addChild(root_0, char_literal454_tree);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4518);
            	    exclusiveOrExpression455=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression455.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:828:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final JavaParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JavaParser.exclusiveOrExpression_return retval = new JavaParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal457=null;
        JavaParser.andExpression_return andExpression456 = null;

        JavaParser.andExpression_return andExpression458 = null;


        Object char_literal457_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:829:5: ( andExpression ( '^' andExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:829:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression4540);
            andExpression456=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression456.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:829:23: ( '^' andExpression )*
            loop132:
            do {
                int alt132=2;
                int LA132_0 = input.LA(1);

                if ( (LA132_0==101) ) {
                    alt132=1;
                }


                switch (alt132) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:829:25: '^' andExpression
            	    {
            	    char_literal457=(Token)match(input,101,FOLLOW_101_in_exclusiveOrExpression4544); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal457_tree = (Object)adaptor.create(char_literal457);
            	    adaptor.addChild(root_0, char_literal457_tree);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression4546);
            	    andExpression458=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression458.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:832:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final JavaParser.andExpression_return andExpression() throws RecognitionException {
        JavaParser.andExpression_return retval = new JavaParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal460=null;
        JavaParser.equalityExpression_return equalityExpression459 = null;

        JavaParser.equalityExpression_return equalityExpression461 = null;


        Object char_literal460_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression4568);
            equalityExpression459=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression459.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:28: ( '&' equalityExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==43) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:833:30: '&' equalityExpression
            	    {
            	    char_literal460=(Token)match(input,43,FOLLOW_43_in_andExpression4572); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal460_tree = (Object)adaptor.create(char_literal460);
            	    adaptor.addChild(root_0, char_literal460_tree);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression4574);
            	    equalityExpression461=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression461.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:836:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final JavaParser.equalityExpression_return equalityExpression() throws RecognitionException {
        JavaParser.equalityExpression_return retval = new JavaParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set463=null;
        JavaParser.instanceOfExpression_return instanceOfExpression462 = null;

        JavaParser.instanceOfExpression_return instanceOfExpression464 = null;


        Object set463_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression4596);
            instanceOfExpression462=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression462.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( ((LA134_0>=102 && LA134_0<=103)) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:837:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set463=(Token)input.LT(1);
            	    if ( (input.LA(1)>=102 && input.LA(1)<=103) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set463));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression4608);
            	    instanceOfExpression464=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression464.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:840:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final JavaParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JavaParser.instanceOfExpression_return retval = new JavaParser.instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal466=null;
        JavaParser.relationalExpression_return relationalExpression465 = null;

        JavaParser.type_return type467 = null;


        Object string_literal466_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:5: ( relationalExpression ( 'instanceof' type )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression4630);
            relationalExpression465=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression465.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:30: ( 'instanceof' type )?
            int alt135=2;
            int LA135_0 = input.LA(1);

            if ( (LA135_0==104) ) {
                alt135=1;
            }
            switch (alt135) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:841:31: 'instanceof' type
                    {
                    string_literal466=(Token)match(input,104,FOLLOW_104_in_instanceOfExpression4633); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal466_tree = (Object)adaptor.create(string_literal466);
                    adaptor.addChild(root_0, string_literal466_tree);
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression4635);
                    type467=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type467.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:844:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final JavaParser.relationalExpression_return relationalExpression() throws RecognitionException {
        JavaParser.relationalExpression_return retval = new JavaParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.shiftExpression_return shiftExpression468 = null;

        JavaParser.relationalOp_return relationalOp469 = null;

        JavaParser.shiftExpression_return shiftExpression470 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression4656);
            shiftExpression468=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression468.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:25: ( relationalOp shiftExpression )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:845:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression4660);
            	    relationalOp469=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp469.getTree());
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression4662);
            	    shiftExpression470=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression470.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:848:1: relationalOp : ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' );
    public final JavaParser.relationalOp_return relationalOp() throws RecognitionException {
        JavaParser.relationalOp_return retval = new JavaParser.relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Object root_0 = null;

        Token t1=null;
        Token t2=null;
        Token char_literal471=null;
        Token char_literal472=null;

        Object t1_tree=null;
        Object t2_tree=null;
        Object char_literal471_tree=null;
        Object char_literal472_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:5: ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' )
            int alt137=4;
            int LA137_0 = input.LA(1);

            if ( (LA137_0==40) ) {
                int LA137_1 = input.LA(2);

                if ( (LA137_1==51) && (synpred211_Java())) {
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

                if ( (LA137_2==51) && (synpred212_Java())) {
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:9: ( '<' '=' )=>t1= '<' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_relationalOp4697); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp4701); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:852:9: ( '>' '=' )=>t1= '>' t2= '=' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_relationalOp4731); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp4735); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:855:9: '<'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal471=(Token)match(input,40,FOLLOW_40_in_relationalOp4756); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal471_tree = (Object)adaptor.create(char_literal471);
                    adaptor.addChild(root_0, char_literal471_tree);
                    }

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:856:9: '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal472=(Token)match(input,42,FOLLOW_42_in_relationalOp4767); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal472_tree = (Object)adaptor.create(char_literal472);
                    adaptor.addChild(root_0, char_literal472_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:859:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final JavaParser.shiftExpression_return shiftExpression() throws RecognitionException {
        JavaParser.shiftExpression_return retval = new JavaParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.additiveExpression_return additiveExpression473 = null;

        JavaParser.shiftOp_return shiftOp474 = null;

        JavaParser.additiveExpression_return additiveExpression475 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression4787);
            additiveExpression473=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression473.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:28: ( shiftOp additiveExpression )*
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
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:860:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression4791);
            	    shiftOp474=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp474.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression4793);
            	    additiveExpression475=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression475.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:863:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);
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
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:5: ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?)
            int alt139=3;
            alt139 = dfa139.predict(input);
            switch (alt139) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:9: ( '<' '<' )=>t1= '<' t2= '<' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,40,FOLLOW_40_in_shiftOp4824); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,40,FOLLOW_40_in_shiftOp4828); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:867:9: ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp4860); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp4864); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t2_tree = (Object)adaptor.create(t2);
                    adaptor.addChild(root_0, t2_tree);
                    }
                    t3=(Token)match(input,42,FOLLOW_42_in_shiftOp4868); if (state.failed) return retval;
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:9: ( '>' '>' )=>t1= '>' t2= '>' {...}?
                    {
                    root_0 = (Object)adaptor.nil();

                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp4898); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    t1_tree = (Object)adaptor.create(t1);
                    adaptor.addChild(root_0, t1_tree);
                    }
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp4902); if (state.failed) return retval;
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:878:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final JavaParser.additiveExpression_return additiveExpression() throws RecognitionException {
        JavaParser.additiveExpression_return retval = new JavaParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set477=null;
        JavaParser.multiplicativeExpression_return multiplicativeExpression476 = null;

        JavaParser.multiplicativeExpression_return multiplicativeExpression478 = null;


        Object set477_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression4932);
            multiplicativeExpression476=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression476.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop140:
            do {
                int alt140=2;
                int LA140_0 = input.LA(1);

                if ( ((LA140_0>=105 && LA140_0<=106)) ) {
                    alt140=1;
                }


                switch (alt140) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:879:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set477=(Token)input.LT(1);
            	    if ( (input.LA(1)>=105 && input.LA(1)<=106) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set477));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression4944);
            	    multiplicativeExpression478=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression478.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:882:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final JavaParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JavaParser.multiplicativeExpression_return retval = new JavaParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set480=null;
        JavaParser.unaryExpression_return unaryExpression479 = null;

        JavaParser.unaryExpression_return unaryExpression481 = null;


        Object set480_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression4966);
            unaryExpression479=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression479.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop141:
            do {
                int alt141=2;
                int LA141_0 = input.LA(1);

                if ( (LA141_0==30||(LA141_0>=107 && LA141_0<=108)) ) {
                    alt141=1;
                }


                switch (alt141) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:883:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set480=(Token)input.LT(1);
            	    if ( input.LA(1)==30||(input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set480));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression4984);
            	    unaryExpression481=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression481.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:886:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final JavaParser.unaryExpression_return unaryExpression() throws RecognitionException {
        JavaParser.unaryExpression_return retval = new JavaParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal482=null;
        Token char_literal484=null;
        Token string_literal486=null;
        Token string_literal488=null;
        JavaParser.unaryExpression_return unaryExpression483 = null;

        JavaParser.unaryExpression_return unaryExpression485 = null;

        JavaParser.unaryExpression_return unaryExpression487 = null;

        JavaParser.unaryExpression_return unaryExpression489 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus490 = null;


        Object char_literal482_tree=null;
        Object char_literal484_tree=null;
        Object string_literal486_tree=null;
        Object string_literal488_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:887:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:887:9: '+' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal482=(Token)match(input,105,FOLLOW_105_in_unaryExpression5010); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal482_tree = (Object)adaptor.create(char_literal482);
                    adaptor.addChild(root_0, char_literal482_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5012);
                    unaryExpression483=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression483.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:888:9: '-' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal484=(Token)match(input,106,FOLLOW_106_in_unaryExpression5022); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal484_tree = (Object)adaptor.create(char_literal484);
                    adaptor.addChild(root_0, char_literal484_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5024);
                    unaryExpression485=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression485.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:889:9: '++' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal486=(Token)match(input,109,FOLLOW_109_in_unaryExpression5034); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal486_tree = (Object)adaptor.create(string_literal486);
                    adaptor.addChild(root_0, string_literal486_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5036);
                    unaryExpression487=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression487.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:890:9: '--' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal488=(Token)match(input,110,FOLLOW_110_in_unaryExpression5046); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal488_tree = (Object)adaptor.create(string_literal488);
                    adaptor.addChild(root_0, string_literal488_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5048);
                    unaryExpression489=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression489.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:891:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5058);
                    unaryExpressionNotPlusMinus490=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus490.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:894:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JavaParser.unaryExpressionNotPlusMinus_return retval = new JavaParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal491=null;
        Token char_literal493=null;
        Token set498=null;
        JavaParser.unaryExpression_return unaryExpression492 = null;

        JavaParser.unaryExpression_return unaryExpression494 = null;

        JavaParser.castExpression_return castExpression495 = null;

        JavaParser.primary_return primary496 = null;

        JavaParser.selector_return selector497 = null;


        Object char_literal491_tree=null;
        Object char_literal493_tree=null;
        Object set498_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:895:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt145=4;
            alt145 = dfa145.predict(input);
            switch (alt145) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:895:9: '~' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal491=(Token)match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus5077); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal491_tree = (Object)adaptor.create(char_literal491);
                    adaptor.addChild(root_0, char_literal491_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5079);
                    unaryExpression492=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression492.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:896:9: '!' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal493=(Token)match(input,112,FOLLOW_112_in_unaryExpressionNotPlusMinus5089); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal493_tree = (Object)adaptor.create(char_literal493);
                    adaptor.addChild(root_0, char_literal493_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5091);
                    unaryExpression494=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression494.getTree());

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:897:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5101);
                    castExpression495=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression495.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:898:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus5111);
                    primary496=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary496.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:898:17: ( selector )*
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
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus5113);
                    	    selector497=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector497.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop143;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:898:27: ( '++' | '--' )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( ((LA144_0>=109 && LA144_0<=110)) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:
                            {
                            set498=(Token)input.LT(1);
                            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set498));
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:901:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final JavaParser.castExpression_return castExpression() throws RecognitionException {
        JavaParser.castExpression_return retval = new JavaParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal499=null;
        Token char_literal501=null;
        Token char_literal503=null;
        Token char_literal506=null;
        JavaParser.primitiveType_return primitiveType500 = null;

        JavaParser.unaryExpression_return unaryExpression502 = null;

        JavaParser.type_return type504 = null;

        JavaParser.expression_return expression505 = null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus507 = null;


        Object char_literal499_tree=null;
        Object char_literal501_tree=null;
        Object char_literal503_tree=null;
        Object char_literal506_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:902:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
            int alt147=2;
            int LA147_0 = input.LA(1);

            if ( (LA147_0==66) ) {
                int LA147_1 = input.LA(2);

                if ( (synpred233_Java()) ) {
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:902:8: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal499=(Token)match(input,66,FOLLOW_66_in_castExpression5139); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal499_tree = (Object)adaptor.create(char_literal499);
                    adaptor.addChild(root_0, char_literal499_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression5141);
                    primitiveType500=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType500.getTree());
                    char_literal501=(Token)match(input,67,FOLLOW_67_in_castExpression5143); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal501_tree = (Object)adaptor.create(char_literal501);
                    adaptor.addChild(root_0, char_literal501_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression5145);
                    unaryExpression502=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression502.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal503=(Token)match(input,66,FOLLOW_66_in_castExpression5154); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal503_tree = (Object)adaptor.create(char_literal503);
                    adaptor.addChild(root_0, char_literal503_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:12: ( type | expression )
                    int alt146=2;
                    alt146 = dfa146.predict(input);
                    switch (alt146) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression5157);
                            type504=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type504.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression5161);
                            expression505=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression505.getTree());

                            }
                            break;

                    }

                    char_literal506=(Token)match(input,67,FOLLOW_67_in_castExpression5164); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal506_tree = (Object)adaptor.create(char_literal506);
                    adaptor.addChild(root_0, char_literal506_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5166);
                    unaryExpressionNotPlusMinus507=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus507.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:906:1: primary : ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final JavaParser.primary_return primary() throws RecognitionException {
        JavaParser.primary_return retval = new JavaParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal509=null;
        Token char_literal510=null;
        Token Identifier511=null;
        Token string_literal513=null;
        Token string_literal516=null;
        Token Identifier518=null;
        Token char_literal519=null;
        Token Identifier520=null;
        Token char_literal523=null;
        Token char_literal524=null;
        Token char_literal525=null;
        Token string_literal526=null;
        Token string_literal527=null;
        Token char_literal528=null;
        Token string_literal529=null;
        JavaParser.parExpression_return parExpression508 = null;

        JavaParser.identifierSuffix_return identifierSuffix512 = null;

        JavaParser.superSuffix_return superSuffix514 = null;

        JavaParser.literal_return literal515 = null;

        JavaParser.creator_return creator517 = null;

        JavaParser.identifierSuffix_return identifierSuffix521 = null;

        JavaParser.primitiveType_return primitiveType522 = null;


        Object string_literal509_tree=null;
        Object char_literal510_tree=null;
        Object Identifier511_tree=null;
        Object string_literal513_tree=null;
        Object string_literal516_tree=null;
        Object Identifier518_tree=null;
        Object char_literal519_tree=null;
        Object Identifier520_tree=null;
        Object char_literal523_tree=null;
        Object char_literal524_tree=null;
        Object char_literal525_tree=null;
        Object string_literal526_tree=null;
        Object string_literal527_tree=null;
        Object char_literal528_tree=null;
        Object string_literal529_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:5: ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:907:9: parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary5185);
                    parExpression508=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression508.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal509=(Token)match(input,69,FOLLOW_69_in_primary5195); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal509_tree = (Object)adaptor.create(string_literal509);
                    adaptor.addChild(root_0, string_literal509_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:16: ( '.' Identifier )*
                    loop148:
                    do {
                        int alt148=2;
                        int LA148_0 = input.LA(1);

                        if ( (LA148_0==29) ) {
                            int LA148_2 = input.LA(2);

                            if ( (LA148_2==Identifier) ) {
                                int LA148_3 = input.LA(3);

                                if ( (synpred236_Java()) ) {
                                    alt148=1;
                                }


                            }


                        }


                        switch (alt148) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:17: '.' Identifier
                    	    {
                    	    char_literal510=(Token)match(input,29,FOLLOW_29_in_primary5198); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal510_tree = (Object)adaptor.create(char_literal510);
                    	    adaptor.addChild(root_0, char_literal510_tree);
                    	    }
                    	    Identifier511=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5200); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier511_tree = (Object)adaptor.create(Identifier511);
                    	    adaptor.addChild(root_0, Identifier511_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:34: ( identifierSuffix )?
                    int alt149=2;
                    alt149 = dfa149.predict(input);
                    switch (alt149) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5204);
                            identifierSuffix512=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix512.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:909:9: 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal513=(Token)match(input,65,FOLLOW_65_in_primary5215); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal513_tree = (Object)adaptor.create(string_literal513);
                    adaptor.addChild(root_0, string_literal513_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_primary5217);
                    superSuffix514=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix514.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:910:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary5227);
                    literal515=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal515.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:911:9: 'new' creator
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal516=(Token)match(input,113,FOLLOW_113_in_primary5237); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal516_tree = (Object)adaptor.create(string_literal516);
                    adaptor.addChild(root_0, string_literal516_tree);
                    }
                    pushFollow(FOLLOW_creator_in_primary5239);
                    creator517=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator517.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:9: Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    Identifier518=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5249); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier518_tree = (Object)adaptor.create(Identifier518);
                    adaptor.addChild(root_0, Identifier518_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:20: ( '.' Identifier )*
                    loop150:
                    do {
                        int alt150=2;
                        int LA150_0 = input.LA(1);

                        if ( (LA150_0==29) ) {
                            int LA150_2 = input.LA(2);

                            if ( (LA150_2==Identifier) ) {
                                int LA150_3 = input.LA(3);

                                if ( (synpred242_Java()) ) {
                                    alt150=1;
                                }


                            }


                        }


                        switch (alt150) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:21: '.' Identifier
                    	    {
                    	    char_literal519=(Token)match(input,29,FOLLOW_29_in_primary5252); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal519_tree = (Object)adaptor.create(char_literal519);
                    	    adaptor.addChild(root_0, char_literal519_tree);
                    	    }
                    	    Identifier520=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5254); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    Identifier520_tree = (Object)adaptor.create(Identifier520);
                    	    adaptor.addChild(root_0, Identifier520_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop150;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:38: ( identifierSuffix )?
                    int alt151=2;
                    alt151 = dfa151.predict(input);
                    switch (alt151) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5258);
                            identifierSuffix521=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix521.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:913:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary5269);
                    primitiveType522=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType522.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:913:23: ( '[' ']' )*
                    loop152:
                    do {
                        int alt152=2;
                        int LA152_0 = input.LA(1);

                        if ( (LA152_0==48) ) {
                            alt152=1;
                        }


                        switch (alt152) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:913:24: '[' ']'
                    	    {
                    	    char_literal523=(Token)match(input,48,FOLLOW_48_in_primary5272); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal523_tree = (Object)adaptor.create(char_literal523);
                    	    adaptor.addChild(root_0, char_literal523_tree);
                    	    }
                    	    char_literal524=(Token)match(input,49,FOLLOW_49_in_primary5274); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal524_tree = (Object)adaptor.create(char_literal524);
                    	    adaptor.addChild(root_0, char_literal524_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop152;
                        }
                    } while (true);

                    char_literal525=(Token)match(input,29,FOLLOW_29_in_primary5278); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal525_tree = (Object)adaptor.create(char_literal525);
                    adaptor.addChild(root_0, char_literal525_tree);
                    }
                    string_literal526=(Token)match(input,37,FOLLOW_37_in_primary5280); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal526_tree = (Object)adaptor.create(string_literal526);
                    adaptor.addChild(root_0, string_literal526_tree);
                    }

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:914:9: 'void' '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal527=(Token)match(input,47,FOLLOW_47_in_primary5290); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal527_tree = (Object)adaptor.create(string_literal527);
                    adaptor.addChild(root_0, string_literal527_tree);
                    }
                    char_literal528=(Token)match(input,29,FOLLOW_29_in_primary5292); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal528_tree = (Object)adaptor.create(char_literal528);
                    adaptor.addChild(root_0, char_literal528_tree);
                    }
                    string_literal529=(Token)match(input,37,FOLLOW_37_in_primary5294); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal529_tree = (Object)adaptor.create(string_literal529);
                    adaptor.addChild(root_0, string_literal529_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:917:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );
    public final JavaParser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        JavaParser.identifierSuffix_return retval = new JavaParser.identifierSuffix_return();
        retval.start = input.LT(1);
        int identifierSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal530=null;
        Token char_literal531=null;
        Token char_literal532=null;
        Token string_literal533=null;
        Token char_literal534=null;
        Token char_literal536=null;
        Token char_literal538=null;
        Token string_literal539=null;
        Token char_literal540=null;
        Token char_literal542=null;
        Token string_literal543=null;
        Token char_literal544=null;
        Token string_literal545=null;
        Token char_literal547=null;
        Token string_literal548=null;
        JavaParser.expression_return expression535 = null;

        JavaParser.arguments_return arguments537 = null;

        JavaParser.explicitGenericInvocation_return explicitGenericInvocation541 = null;

        JavaParser.arguments_return arguments546 = null;

        JavaParser.innerCreator_return innerCreator549 = null;


        Object char_literal530_tree=null;
        Object char_literal531_tree=null;
        Object char_literal532_tree=null;
        Object string_literal533_tree=null;
        Object char_literal534_tree=null;
        Object char_literal536_tree=null;
        Object char_literal538_tree=null;
        Object string_literal539_tree=null;
        Object char_literal540_tree=null;
        Object char_literal542_tree=null;
        Object string_literal543_tree=null;
        Object char_literal544_tree=null;
        Object string_literal545_tree=null;
        Object char_literal547_tree=null;
        Object string_literal548_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:918:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator )
            int alt156=8;
            alt156 = dfa156.predict(input);
            switch (alt156) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:918:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:918:9: ( '[' ']' )+
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:918:10: '[' ']'
                    	    {
                    	    char_literal530=(Token)match(input,48,FOLLOW_48_in_identifierSuffix5314); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal530_tree = (Object)adaptor.create(char_literal530);
                    	    adaptor.addChild(root_0, char_literal530_tree);
                    	    }
                    	    char_literal531=(Token)match(input,49,FOLLOW_49_in_identifierSuffix5316); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal531_tree = (Object)adaptor.create(char_literal531);
                    	    adaptor.addChild(root_0, char_literal531_tree);
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

                    char_literal532=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5320); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal532_tree = (Object)adaptor.create(char_literal532);
                    adaptor.addChild(root_0, char_literal532_tree);
                    }
                    string_literal533=(Token)match(input,37,FOLLOW_37_in_identifierSuffix5322); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal533_tree = (Object)adaptor.create(string_literal533);
                    adaptor.addChild(root_0, string_literal533_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:9: ( '[' expression ']' )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:9: ( '[' expression ']' )+
                    int cnt155=0;
                    loop155:
                    do {
                        int alt155=2;
                        alt155 = dfa155.predict(input);
                        switch (alt155) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:10: '[' expression ']'
                    	    {
                    	    char_literal534=(Token)match(input,48,FOLLOW_48_in_identifierSuffix5333); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal534_tree = (Object)adaptor.create(char_literal534);
                    	    adaptor.addChild(root_0, char_literal534_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix5335);
                    	    expression535=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression535.getTree());
                    	    char_literal536=(Token)match(input,49,FOLLOW_49_in_identifierSuffix5337); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal536_tree = (Object)adaptor.create(char_literal536);
                    	    adaptor.addChild(root_0, char_literal536_tree);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:920:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix5350);
                    arguments537=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments537.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:921:9: '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal538=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5360); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal538_tree = (Object)adaptor.create(char_literal538);
                    adaptor.addChild(root_0, char_literal538_tree);
                    }
                    string_literal539=(Token)match(input,37,FOLLOW_37_in_identifierSuffix5362); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal539_tree = (Object)adaptor.create(string_literal539);
                    adaptor.addChild(root_0, string_literal539_tree);
                    }

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:922:9: '.' explicitGenericInvocation
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal540=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5372); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal540_tree = (Object)adaptor.create(char_literal540);
                    adaptor.addChild(root_0, char_literal540_tree);
                    }
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix5374);
                    explicitGenericInvocation541=explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGenericInvocation541.getTree());

                    }
                    break;
                case 6 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:923:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal542=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5384); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal542_tree = (Object)adaptor.create(char_literal542);
                    adaptor.addChild(root_0, char_literal542_tree);
                    }
                    string_literal543=(Token)match(input,69,FOLLOW_69_in_identifierSuffix5386); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal543_tree = (Object)adaptor.create(string_literal543);
                    adaptor.addChild(root_0, string_literal543_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:924:9: '.' 'super' arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal544=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5396); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal544_tree = (Object)adaptor.create(char_literal544);
                    adaptor.addChild(root_0, char_literal544_tree);
                    }
                    string_literal545=(Token)match(input,65,FOLLOW_65_in_identifierSuffix5398); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal545_tree = (Object)adaptor.create(string_literal545);
                    adaptor.addChild(root_0, string_literal545_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_identifierSuffix5400);
                    arguments546=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments546.getTree());

                    }
                    break;
                case 8 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:925:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal547=(Token)match(input,29,FOLLOW_29_in_identifierSuffix5410); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal547_tree = (Object)adaptor.create(char_literal547);
                    adaptor.addChild(root_0, char_literal547_tree);
                    }
                    string_literal548=(Token)match(input,113,FOLLOW_113_in_identifierSuffix5412); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal548_tree = (Object)adaptor.create(string_literal548);
                    adaptor.addChild(root_0, string_literal548_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix5414);
                    innerCreator549=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator549.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:928:1: creator : ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) );
    public final JavaParser.creator_return creator() throws RecognitionException {
        JavaParser.creator_return retval = new JavaParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments550 = null;

        JavaParser.createdName_return createdName551 = null;

        JavaParser.classCreatorRest_return classCreatorRest552 = null;

        JavaParser.createdName_return createdName553 = null;

        JavaParser.arrayCreatorRest_return arrayCreatorRest554 = null;

        JavaParser.classCreatorRest_return classCreatorRest555 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:929:5: ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:929:9: nonWildcardTypeArguments createdName classCreatorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator5433);
                    nonWildcardTypeArguments550=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments550.getTree());
                    pushFollow(FOLLOW_createdName_in_creator5435);
                    createdName551=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName551.getTree());
                    pushFollow(FOLLOW_classCreatorRest_in_creator5437);
                    classCreatorRest552=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest552.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:930:9: createdName ( arrayCreatorRest | classCreatorRest )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_createdName_in_creator5447);
                    createdName553=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName553.getTree());
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:930:21: ( arrayCreatorRest | classCreatorRest )
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:930:22: arrayCreatorRest
                            {
                            pushFollow(FOLLOW_arrayCreatorRest_in_creator5450);
                            arrayCreatorRest554=arrayCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreatorRest554.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:930:41: classCreatorRest
                            {
                            pushFollow(FOLLOW_classCreatorRest_in_creator5454);
                            classCreatorRest555=classCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest555.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:933:1: createdName : ( classOrInterfaceType | primitiveType );
    public final JavaParser.createdName_return createdName() throws RecognitionException {
        JavaParser.createdName_return retval = new JavaParser.createdName_return();
        retval.start = input.LT(1);
        int createdName_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType556 = null;

        JavaParser.primitiveType_return primitiveType557 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:5: ( classOrInterfaceType | primitiveType )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:934:9: classOrInterfaceType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName5474);
                    classOrInterfaceType556=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType556.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:935:9: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName5484);
                    primitiveType557=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType557.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:938:1: innerCreator : ( nonWildcardTypeArguments )? Identifier classCreatorRest ;
    public final JavaParser.innerCreator_return innerCreator() throws RecognitionException {
        JavaParser.innerCreator_return retval = new JavaParser.innerCreator_return();
        retval.start = input.LT(1);
        int innerCreator_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier559=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments558 = null;

        JavaParser.classCreatorRest_return classCreatorRest560 = null;


        Object Identifier559_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:939:5: ( ( nonWildcardTypeArguments )? Identifier classCreatorRest )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:939:9: ( nonWildcardTypeArguments )? Identifier classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:939:9: ( nonWildcardTypeArguments )?
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==40) ) {
                alt160=1;
            }
            switch (alt160) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator5507);
                    nonWildcardTypeArguments558=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments558.getTree());

                    }
                    break;

            }

            Identifier559=(Token)match(input,Identifier,FOLLOW_Identifier_in_innerCreator5510); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier559_tree = (Object)adaptor.create(Identifier559);
            adaptor.addChild(root_0, Identifier559_tree);
            }
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator5512);
            classCreatorRest560=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest560.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:942:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final JavaParser.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        JavaParser.arrayCreatorRest_return retval = new JavaParser.arrayCreatorRest_return();
        retval.start = input.LT(1);
        int arrayCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal561=null;
        Token char_literal562=null;
        Token char_literal563=null;
        Token char_literal564=null;
        Token char_literal567=null;
        Token char_literal568=null;
        Token char_literal570=null;
        Token char_literal571=null;
        Token char_literal572=null;
        JavaParser.arrayInitializer_return arrayInitializer565 = null;

        JavaParser.expression_return expression566 = null;

        JavaParser.expression_return expression569 = null;


        Object char_literal561_tree=null;
        Object char_literal562_tree=null;
        Object char_literal563_tree=null;
        Object char_literal564_tree=null;
        Object char_literal567_tree=null;
        Object char_literal568_tree=null;
        Object char_literal570_tree=null;
        Object char_literal571_tree=null;
        Object char_literal572_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:943:9: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            root_0 = (Object)adaptor.nil();

            char_literal561=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5531); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal561_tree = (Object)adaptor.create(char_literal561);
            adaptor.addChild(root_0, char_literal561_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:944:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:944:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    char_literal562=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5545); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal562_tree = (Object)adaptor.create(char_literal562);
                    adaptor.addChild(root_0, char_literal562_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:944:17: ( '[' ']' )*
                    loop161:
                    do {
                        int alt161=2;
                        int LA161_0 = input.LA(1);

                        if ( (LA161_0==48) ) {
                            alt161=1;
                        }


                        switch (alt161) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:944:18: '[' ']'
                    	    {
                    	    char_literal563=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5548); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal563_tree = (Object)adaptor.create(char_literal563);
                    	    adaptor.addChild(root_0, char_literal563_tree);
                    	    }
                    	    char_literal564=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5550); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal564_tree = (Object)adaptor.create(char_literal564);
                    	    adaptor.addChild(root_0, char_literal564_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop161;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest5554);
                    arrayInitializer565=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer565.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest5568);
                    expression566=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression566.getTree());
                    char_literal567=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5570); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal567_tree = (Object)adaptor.create(char_literal567);
                    adaptor.addChild(root_0, char_literal567_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:28: ( '[' expression ']' )*
                    loop162:
                    do {
                        int alt162=2;
                        alt162 = dfa162.predict(input);
                        switch (alt162) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:29: '[' expression ']'
                    	    {
                    	    char_literal568=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5573); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal568_tree = (Object)adaptor.create(char_literal568);
                    	    adaptor.addChild(root_0, char_literal568_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest5575);
                    	    expression569=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression569.getTree());
                    	    char_literal570=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5577); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal570_tree = (Object)adaptor.create(char_literal570);
                    	    adaptor.addChild(root_0, char_literal570_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop162;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:50: ( '[' ']' )*
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
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:51: '[' ']'
                    	    {
                    	    char_literal571=(Token)match(input,48,FOLLOW_48_in_arrayCreatorRest5582); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal571_tree = (Object)adaptor.create(char_literal571);
                    	    adaptor.addChild(root_0, char_literal571_tree);
                    	    }
                    	    char_literal572=(Token)match(input,49,FOLLOW_49_in_arrayCreatorRest5584); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal572_tree = (Object)adaptor.create(char_literal572);
                    	    adaptor.addChild(root_0, char_literal572_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:949:1: classCreatorRest : arguments ( classBody )? ;
    public final JavaParser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        JavaParser.classCreatorRest_return retval = new JavaParser.classCreatorRest_return();
        retval.start = input.LT(1);
        int classCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        JavaParser.arguments_return arguments573 = null;

        JavaParser.classBody_return classBody574 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:950:5: ( arguments ( classBody )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:950:9: arguments ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest5615);
            arguments573=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments573.getTree());
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:950:19: ( classBody )?
            int alt165=2;
            int LA165_0 = input.LA(1);

            if ( (LA165_0==44) ) {
                alt165=1;
            }
            switch (alt165) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest5617);
                    classBody574=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody574.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:953:1: explicitGenericInvocation : nonWildcardTypeArguments Identifier arguments ;
    public final JavaParser.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        JavaParser.explicitGenericInvocation_return retval = new JavaParser.explicitGenericInvocation_return();
        retval.start = input.LT(1);
        int explicitGenericInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier576=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments575 = null;

        JavaParser.arguments_return arguments577 = null;


        Object Identifier576_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:954:5: ( nonWildcardTypeArguments Identifier arguments )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:954:9: nonWildcardTypeArguments Identifier arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation5641);
            nonWildcardTypeArguments575=nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments575.getTree());
            Identifier576=(Token)match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocation5643); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier576_tree = (Object)adaptor.create(Identifier576);
            adaptor.addChild(root_0, Identifier576_tree);
            }
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation5645);
            arguments577=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments577.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:957:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        JavaParser.nonWildcardTypeArguments_return retval = new JavaParser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);
        int nonWildcardTypeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal578=null;
        Token char_literal580=null;
        JavaParser.typeList_return typeList579 = null;


        Object char_literal578_tree=null;
        Object char_literal580_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:5: ( '<' typeList '>' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:958:9: '<' typeList '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal578=(Token)match(input,40,FOLLOW_40_in_nonWildcardTypeArguments5668); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal578_tree = (Object)adaptor.create(char_literal578);
            adaptor.addChild(root_0, char_literal578_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments5670);
            typeList579=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList579.getTree());
            char_literal580=(Token)match(input,42,FOLLOW_42_in_nonWildcardTypeArguments5672); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal580_tree = (Object)adaptor.create(char_literal580);
            adaptor.addChild(root_0, char_literal580_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:961:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' );
    public final JavaParser.selector_return selector() throws RecognitionException {
        JavaParser.selector_return retval = new JavaParser.selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal581=null;
        Token Identifier582=null;
        Token char_literal584=null;
        Token string_literal585=null;
        Token char_literal586=null;
        Token string_literal587=null;
        Token char_literal589=null;
        Token string_literal590=null;
        Token char_literal592=null;
        Token char_literal594=null;
        JavaParser.arguments_return arguments583 = null;

        JavaParser.superSuffix_return superSuffix588 = null;

        JavaParser.innerCreator_return innerCreator591 = null;

        JavaParser.expression_return expression593 = null;


        Object char_literal581_tree=null;
        Object Identifier582_tree=null;
        Object char_literal584_tree=null;
        Object string_literal585_tree=null;
        Object char_literal586_tree=null;
        Object string_literal587_tree=null;
        Object char_literal589_tree=null;
        Object string_literal590_tree=null;
        Object char_literal592_tree=null;
        Object char_literal594_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:962:5: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:962:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal581=(Token)match(input,29,FOLLOW_29_in_selector5695); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal581_tree = (Object)adaptor.create(char_literal581);
                    adaptor.addChild(root_0, char_literal581_tree);
                    }
                    Identifier582=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector5697); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier582_tree = (Object)adaptor.create(Identifier582);
                    adaptor.addChild(root_0, Identifier582_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:962:24: ( arguments )?
                    int alt166=2;
                    int LA166_0 = input.LA(1);

                    if ( (LA166_0==66) ) {
                        alt166=1;
                    }
                    switch (alt166) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector5699);
                            arguments583=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments583.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:963:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal584=(Token)match(input,29,FOLLOW_29_in_selector5710); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal584_tree = (Object)adaptor.create(char_literal584);
                    adaptor.addChild(root_0, char_literal584_tree);
                    }
                    string_literal585=(Token)match(input,69,FOLLOW_69_in_selector5712); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal585_tree = (Object)adaptor.create(string_literal585);
                    adaptor.addChild(root_0, string_literal585_tree);
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:964:9: '.' 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal586=(Token)match(input,29,FOLLOW_29_in_selector5722); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal586_tree = (Object)adaptor.create(char_literal586);
                    adaptor.addChild(root_0, char_literal586_tree);
                    }
                    string_literal587=(Token)match(input,65,FOLLOW_65_in_selector5724); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal587_tree = (Object)adaptor.create(string_literal587);
                    adaptor.addChild(root_0, string_literal587_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_selector5726);
                    superSuffix588=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix588.getTree());

                    }
                    break;
                case 4 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:965:9: '.' 'new' innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal589=(Token)match(input,29,FOLLOW_29_in_selector5736); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal589_tree = (Object)adaptor.create(char_literal589);
                    adaptor.addChild(root_0, char_literal589_tree);
                    }
                    string_literal590=(Token)match(input,113,FOLLOW_113_in_selector5738); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal590_tree = (Object)adaptor.create(string_literal590);
                    adaptor.addChild(root_0, string_literal590_tree);
                    }
                    pushFollow(FOLLOW_innerCreator_in_selector5740);
                    innerCreator591=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator591.getTree());

                    }
                    break;
                case 5 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:966:9: '[' expression ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal592=(Token)match(input,48,FOLLOW_48_in_selector5750); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal592_tree = (Object)adaptor.create(char_literal592);
                    adaptor.addChild(root_0, char_literal592_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector5752);
                    expression593=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression593.getTree());
                    char_literal594=(Token)match(input,49,FOLLOW_49_in_selector5754); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal594_tree = (Object)adaptor.create(char_literal594);
                    adaptor.addChild(root_0, char_literal594_tree);
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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:969:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final JavaParser.superSuffix_return superSuffix() throws RecognitionException {
        JavaParser.superSuffix_return retval = new JavaParser.superSuffix_return();
        retval.start = input.LT(1);
        int superSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal596=null;
        Token Identifier597=null;
        JavaParser.arguments_return arguments595 = null;

        JavaParser.arguments_return arguments598 = null;


        Object char_literal596_tree=null;
        Object Identifier597_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:970:5: ( arguments | '.' Identifier ( arguments )? )
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:970:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix5777);
                    arguments595=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments595.getTree());

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:971:9: '.' Identifier ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal596=(Token)match(input,29,FOLLOW_29_in_superSuffix5787); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal596_tree = (Object)adaptor.create(char_literal596);
                    adaptor.addChild(root_0, char_literal596_tree);
                    }
                    Identifier597=(Token)match(input,Identifier,FOLLOW_Identifier_in_superSuffix5789); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Identifier597_tree = (Object)adaptor.create(Identifier597);
                    adaptor.addChild(root_0, Identifier597_tree);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:971:24: ( arguments )?
                    int alt168=2;
                    int LA168_0 = input.LA(1);

                    if ( (LA168_0==66) ) {
                        alt168=1;
                    }
                    switch (alt168) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix5791);
                            arguments598=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments598.getTree());

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
    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:974:1: arguments : '(' ( expressionList )? ')' ;
    public final JavaParser.arguments_return arguments() throws RecognitionException {
        JavaParser.arguments_return retval = new JavaParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal599=null;
        Token char_literal601=null;
        JavaParser.expressionList_return expressionList600 = null;


        Object char_literal599_tree=null;
        Object char_literal601_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return retval; }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:975:5: ( '(' ( expressionList )? ')' )
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:975:9: '(' ( expressionList )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal599=(Token)match(input,66,FOLLOW_66_in_arguments5811); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal599_tree = (Object)adaptor.create(char_literal599);
            adaptor.addChild(root_0, char_literal599_tree);
            }
            // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:975:13: ( expressionList )?
            int alt170=2;
            int LA170_0 = input.LA(1);

            if ( (LA170_0==Identifier||(LA170_0>=FloatingPointLiteral && LA170_0<=DecimalLiteral)||LA170_0==47||(LA170_0>=56 && LA170_0<=63)||(LA170_0>=65 && LA170_0<=66)||(LA170_0>=69 && LA170_0<=72)||(LA170_0>=105 && LA170_0<=106)||(LA170_0>=109 && LA170_0<=113)) ) {
                alt170=1;
            }
            switch (alt170) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments5813);
                    expressionList600=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList600.getTree());

                    }
                    break;

            }

            char_literal601=(Token)match(input,67,FOLLOW_67_in_arguments5816); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal601_tree = (Object)adaptor.create(char_literal601);
            adaptor.addChild(root_0, char_literal601_tree);
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


        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:249:10: ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:249:10: annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
        {
        pushFollow(FOLLOW_annotations_in_synpred5_Java84);
        annotations();

        state._fsp--;
        if (state.failed) return ;
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:9: (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* )
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
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:13: np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_packageDeclaration_in_synpred5_Java100);
                np=packageDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:90: (imp= importDeclaration )*
                loop173:
                do {
                    int alt173=2;
                    int LA173_0 = input.LA(1);

                    if ( (LA173_0==27) ) {
                        alt173=1;
                    }


                    switch (alt173) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:91: imp= importDeclaration
                	    {
                	    pushFollow(FOLLOW_importDeclaration_in_synpred5_Java106);
                	    imp=importDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop173;
                    }
                } while (true);

                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:159: (typech= typeDeclaration )*
                loop174:
                do {
                    int alt174=2;
                    int LA174_0 = input.LA(1);

                    if ( (LA174_0==ENUM||LA174_0==26||LA174_0==28||(LA174_0>=31 && LA174_0<=37)||LA174_0==46||LA174_0==73) ) {
                        alt174=1;
                    }


                    switch (alt174) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:250:160: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java114);
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
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:251:13: cd= classOrInterfaceDeclaration (typech= typeDeclaration )*
                {
                pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java133);
                cd=classOrInterfaceDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:251:85: (typech= typeDeclaration )*
                loop175:
                do {
                    int alt175=2;
                    int LA175_0 = input.LA(1);

                    if ( (LA175_0==ENUM||LA175_0==26||LA175_0==28||(LA175_0>=31 && LA175_0<=37)||LA175_0==46||LA175_0==73) ) {
                        alt175=1;
                    }


                    switch (alt175) {
                	case 1 :
                	    // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:251:86: typech= typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java139);
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

    // $ANTLR start synpred113_Java
    public final void synpred113_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:13: ( explicitConstructorInvocation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:563:13: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred113_Java2547);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred113_Java

    // $ANTLR start synpred117_Java
    public final void synpred117_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:567:9: ( nonWildcardTypeArguments )?
        int alt184=2;
        int LA184_0 = input.LA(1);

        if ( (LA184_0==40) ) {
            alt184=1;
        }
        switch (alt184) {
            case 1 :
                // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred117_Java2572);
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

        pushFollow(FOLLOW_arguments_in_synpred117_Java2583);
        arguments();

        state._fsp--;
        if (state.failed) return ;
        match(input,26,FOLLOW_26_in_synpred117_Java2585); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred117_Java

    // $ANTLR start synpred128_Java
    public final void synpred128_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:9: ( annotation )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:599:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred128_Java2796);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred128_Java

    // $ANTLR start synpred151_Java
    public final void synpred151_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:672:9: ( localVariableDeclarationStatement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:672:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred151_Java3323);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred151_Java

    // $ANTLR start synpred152_Java
    public final void synpred152_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:9: ( classOrInterfaceDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:673:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred152_Java3333);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred152_Java

    // $ANTLR start synpred157_Java
    public final void synpred157_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:54: ( 'else' statement )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:692:54: 'else' statement
        {
        match(input,77,FOLLOW_77_in_synpred157_Java3478); if (state.failed) return ;
        pushFollow(FOLLOW_statement_in_synpred157_Java3480);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred157_Java

    // $ANTLR start synpred162_Java
    public final void synpred162_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:11: ( catches 'finally' block )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:697:11: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred162_Java3556);
        catches();

        state._fsp--;
        if (state.failed) return ;
        match(input,82,FOLLOW_82_in_synpred162_Java3558); if (state.failed) return ;
        pushFollow(FOLLOW_block_in_synpred162_Java3560);
        block();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred162_Java

    // $ANTLR start synpred163_Java
    public final void synpred163_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:11: ( catches )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:698:11: catches
        {
        pushFollow(FOLLOW_catches_in_synpred163_Java3572);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred163_Java

    // $ANTLR start synpred178_Java
    public final void synpred178_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:9: ( switchLabel )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:733:9: switchLabel
        {
        pushFollow(FOLLOW_switchLabel_in_synpred178_Java3863);
        switchLabel();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred178_Java

    // $ANTLR start synpred180_Java
    public final void synpred180_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:737:9: ( 'case' constantExpression ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:737:9: 'case' constantExpression ':'
        {
        match(input,89,FOLLOW_89_in_synpred180_Java3890); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred180_Java3892);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred180_Java3894); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred180_Java

    // $ANTLR start synpred181_Java
    public final void synpred181_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:738:9: ( 'case' enumConstantName ':' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:738:9: 'case' enumConstantName ':'
        {
        match(input,89,FOLLOW_89_in_synpred181_Java3904); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred181_Java3906);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred181_Java3908); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred181_Java

    // $ANTLR start synpred182_Java
    public final void synpred182_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:744:9: ( enhancedForControl )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:744:9: enhancedForControl
        {
        pushFollow(FOLLOW_enhancedForControl_in_synpred182_Java3951);
        enhancedForControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred182_Java

    // $ANTLR start synpred186_Java
    public final void synpred186_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:9: ( localVariableDeclaration )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:749:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred186_Java3991);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred186_Java

    // $ANTLR start synpred188_Java
    public final void synpred188_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:32: ( assignmentOperator expression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:780:32: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred188_Java4174);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred188_Java4176);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred188_Java

    // $ANTLR start synpred198_Java
    public final void synpred198_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:793:9: ( '<' '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:793:10: '<' '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred198_Java4292); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred198_Java4294); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred198_Java4296); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred198_Java

    // $ANTLR start synpred199_Java
    public final void synpred199_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:798:9: ( '>' '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:798:10: '>' '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred199_Java4332); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred199_Java4334); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred199_Java4336); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred199_Java4338); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred199_Java

    // $ANTLR start synpred200_Java
    public final void synpred200_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:9: ( '>' '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:805:10: '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred200_Java4377); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred200_Java4379); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred200_Java4381); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred200_Java

    // $ANTLR start synpred211_Java
    public final void synpred211_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:9: ( '<' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:849:10: '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred211_Java4689); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred211_Java4691); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred211_Java

    // $ANTLR start synpred212_Java
    public final void synpred212_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:852:9: ( '>' '=' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:852:10: '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred212_Java4723); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred212_Java4725); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred212_Java

    // $ANTLR start synpred215_Java
    public final void synpred215_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:9: ( '<' '<' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:864:10: '<' '<'
        {
        match(input,40,FOLLOW_40_in_synpred215_Java4816); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred215_Java4818); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred215_Java

    // $ANTLR start synpred216_Java
    public final void synpred216_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:867:9: ( '>' '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:867:10: '>' '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred216_Java4850); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred216_Java4852); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred216_Java4854); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred216_Java

    // $ANTLR start synpred217_Java
    public final void synpred217_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:9: ( '>' '>' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:872:10: '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred217_Java4890); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred217_Java4892); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred217_Java

    // $ANTLR start synpred229_Java
    public final void synpred229_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:897:9: ( castExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:897:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred229_Java5101);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred229_Java

    // $ANTLR start synpred233_Java
    public final void synpred233_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:902:8: ( '(' primitiveType ')' unaryExpression )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:902:8: '(' primitiveType ')' unaryExpression
        {
        match(input,66,FOLLOW_66_in_synpred233_Java5139); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred233_Java5141);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,67,FOLLOW_67_in_synpred233_Java5143); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred233_Java5145);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred233_Java

    // $ANTLR start synpred234_Java
    public final void synpred234_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:13: ( type )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:903:13: type
        {
        pushFollow(FOLLOW_type_in_synpred234_Java5157);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred234_Java

    // $ANTLR start synpred236_Java
    public final void synpred236_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:17: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:17: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred236_Java5198); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred236_Java5200); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred236_Java

    // $ANTLR start synpred237_Java
    public final void synpred237_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:34: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:908:34: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred237_Java5204);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred237_Java

    // $ANTLR start synpred242_Java
    public final void synpred242_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:21: ( '.' Identifier )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:21: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred242_Java5252); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred242_Java5254); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred242_Java

    // $ANTLR start synpred243_Java
    public final void synpred243_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:38: ( identifierSuffix )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:912:38: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred243_Java5258);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred243_Java

    // $ANTLR start synpred249_Java
    public final void synpred249_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:10: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:919:10: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred249_Java5333); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred249_Java5335);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred249_Java5337); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred249_Java

    // $ANTLR start synpred262_Java
    public final void synpred262_Java_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:29: ( '[' expression ']' )
        // /Users/marko/git/jnome/src/jnome/input/parser/Java.g:945:29: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred262_Java5573); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred262_Java5575);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred262_Java5577); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred262_Java

    // Delegated rules

    public final boolean synpred199_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred199_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred128_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred128_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred182_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred182_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred249_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred249_Java_fragment(); // can never throw exception
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
    public final boolean synpred217_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred217_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred178_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred178_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred262_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred262_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred162_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred162_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred236_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred236_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred188_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred188_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred186_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred186_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred198_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred198_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred216_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred216_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred151_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred151_Java_fragment(); // can never throw exception
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
    public final boolean synpred215_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred215_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred234_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred234_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred117_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred117_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred180_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred180_Java_fragment(); // can never throw exception
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
    public final boolean synpred113_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred113_Java_fragment(); // can never throw exception
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
    public final boolean synpred243_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred243_Java_fragment(); // can never throw exception
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
    public final boolean synpred242_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred242_Java_fragment(); // can never throw exception
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
    public final boolean synpred200_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred200_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred233_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred233_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred163_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred163_Java_fragment(); // can never throw exception
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
            return "247:1: compilationUnit returns [CompilationUnit element] : ( annotations (np= packageDeclaration (imp= importDeclaration )* (typech= typeDeclaration )* | cd= classOrInterfaceDeclaration (typech= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (typech= typeDeclaration )* );";
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
            return "563:13: ( explicitConstructorInvocation )?";
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
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA81_3 = input.LA(1);

                         
                        int index81_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA81_4 = input.LA(1);

                         
                        int index81_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA81_5 = input.LA(1);

                         
                        int index81_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA81_6 = input.LA(1);

                         
                        int index81_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA81_7 = input.LA(1);

                         
                        int index81_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA81_8 = input.LA(1);

                         
                        int index81_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA81_9 = input.LA(1);

                         
                        int index81_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA81_10 = input.LA(1);

                         
                        int index81_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA81_11 = input.LA(1);

                         
                        int index81_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA81_12 = input.LA(1);

                         
                        int index81_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA81_13 = input.LA(1);

                         
                        int index81_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index81_13);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA81_14 = input.LA(1);

                         
                        int index81_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred113_Java()) ) {s = 1;}

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
            return "566:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );";
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
                        if ( (synpred117_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index85_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA85_4 = input.LA(1);

                         
                        int index85_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_Java()) ) {s = 1;}

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
            return "671:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );";
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
                        if ( (synpred151_Java()) ) {s = 45;}

                        else if ( (synpred152_Java()) ) {s = 5;}

                         
                        input.seek(index106_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA106_2 = input.LA(1);

                         
                        int index106_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred151_Java()) ) {s = 45;}

                        else if ( (synpred152_Java()) ) {s = 5;}

                         
                        input.seek(index106_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA106_3 = input.LA(1);

                         
                        int index106_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred151_Java()) ) {s = 45;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index106_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA106_4 = input.LA(1);

                         
                        int index106_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred151_Java()) ) {s = 45;}

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
            return "689:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );";
        }
    }
    static final String DFA123_eotS =
        "\u0087\uffff";
    static final String DFA123_eofS =
        "\u0087\uffff";
    static final String DFA123_minS =
        "\5\4\22\uffff\10\4\1\32\30\uffff\1\61\1\uffff\1\32\21\0\2\uffff"+
        "\3\0\21\uffff\1\0\5\uffff\1\0\30\uffff\1\0\5\uffff";
    static final String DFA123_maxS =
        "\1\161\1\111\1\4\1\156\1\60\22\uffff\2\60\1\111\1\4\1\111\3\161"+
        "\1\113\30\uffff\1\61\1\uffff\1\113\21\0\2\uffff\3\0\21\uffff\1\0"+
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
            return "742:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
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
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_59);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA123_60 = input.LA(1);

                         
                        int index123_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_60);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA123_61 = input.LA(1);

                         
                        int index123_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_61);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA123_62 = input.LA(1);

                         
                        int index123_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_62);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA123_63 = input.LA(1);

                         
                        int index123_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_63);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA123_64 = input.LA(1);

                         
                        int index123_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_64);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA123_65 = input.LA(1);

                         
                        int index123_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_65);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA123_66 = input.LA(1);

                         
                        int index123_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_66);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA123_67 = input.LA(1);

                         
                        int index123_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_67);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA123_68 = input.LA(1);

                         
                        int index123_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_68);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA123_69 = input.LA(1);

                         
                        int index123_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_69);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA123_70 = input.LA(1);

                         
                        int index123_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_70);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA123_71 = input.LA(1);

                         
                        int index123_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_71);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA123_72 = input.LA(1);

                         
                        int index123_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_72);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA123_73 = input.LA(1);

                         
                        int index123_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_73);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA123_74 = input.LA(1);

                         
                        int index123_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_74);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA123_75 = input.LA(1);

                         
                        int index123_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_75);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA123_78 = input.LA(1);

                         
                        int index123_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_78);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA123_79 = input.LA(1);

                         
                        int index123_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_79);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA123_80 = input.LA(1);

                         
                        int index123_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_80);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA123_98 = input.LA(1);

                         
                        int index123_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_98);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA123_104 = input.LA(1);

                         
                        int index123_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_104);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA123_129 = input.LA(1);

                         
                        int index123_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

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
            return "748:1: forInit : ( localVariableDeclaration | expressionList );";
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
                        if ( (synpred186_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index124_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA124_4 = input.LA(1);

                         
                        int index124_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred186_Java()) ) {s = 1;}

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
        "\1\uffff\1\11\1\0\1\5\1\7\1\12\1\1\1\6\1\10\1\3\1\2\1\4\2\uffff}>";
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
            return "780:31: ( assignmentOperator expression )?";
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
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA126_6 = input.LA(1);

                         
                        int index126_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA126_10 = input.LA(1);

                         
                        int index126_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_10);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA126_9 = input.LA(1);

                         
                        int index126_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_9);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA126_11 = input.LA(1);

                         
                        int index126_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_11);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA126_3 = input.LA(1);

                         
                        int index126_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_3);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA126_7 = input.LA(1);

                         
                        int index126_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA126_4 = input.LA(1);

                         
                        int index126_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_4);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA126_8 = input.LA(1);

                         
                        int index126_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA126_1 = input.LA(1);

                         
                        int index126_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_1);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA126_5 = input.LA(1);

                         
                        int index126_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_5);
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
            return "783:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);";
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
                        if ( (LA127_12==42) && (synpred199_Java())) {s = 13;}

                        else if ( (LA127_12==51) && (synpred200_Java())) {s = 14;}

                         
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

                        else if ( (LA127_0==40) && (synpred198_Java())) {s = 10;}

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
            return "863:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);";
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
                        if ( (LA139_3==42) && (synpred216_Java())) {s = 4;}

                        else if ( (LA139_3==105) && (synpred217_Java())) {s = 5;}

                        else if ( (LA139_3==106) && (synpred217_Java())) {s = 6;}

                        else if ( (LA139_3==109) && (synpred217_Java())) {s = 7;}

                        else if ( (LA139_3==110) && (synpred217_Java())) {s = 8;}

                        else if ( (LA139_3==111) && (synpred217_Java())) {s = 9;}

                        else if ( (LA139_3==112) && (synpred217_Java())) {s = 10;}

                        else if ( (LA139_3==66) && (synpred217_Java())) {s = 11;}

                        else if ( (LA139_3==69) && (synpred217_Java())) {s = 12;}

                        else if ( (LA139_3==65) && (synpred217_Java())) {s = 13;}

                        else if ( ((LA139_3>=HexLiteral && LA139_3<=DecimalLiteral)) && (synpred217_Java())) {s = 14;}

                        else if ( (LA139_3==FloatingPointLiteral) && (synpred217_Java())) {s = 15;}

                        else if ( (LA139_3==CharacterLiteral) && (synpred217_Java())) {s = 16;}

                        else if ( (LA139_3==StringLiteral) && (synpred217_Java())) {s = 17;}

                        else if ( ((LA139_3>=71 && LA139_3<=72)) && (synpred217_Java())) {s = 18;}

                        else if ( (LA139_3==70) && (synpred217_Java())) {s = 19;}

                        else if ( (LA139_3==113) && (synpred217_Java())) {s = 20;}

                        else if ( (LA139_3==Identifier) && (synpred217_Java())) {s = 21;}

                        else if ( ((LA139_3>=56 && LA139_3<=63)) && (synpred217_Java())) {s = 22;}

                        else if ( (LA139_3==47) && (synpred217_Java())) {s = 23;}

                         
                        input.seek(index139_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA139_0 = input.LA(1);

                         
                        int index139_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA139_0==40) && (synpred215_Java())) {s = 1;}

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
            return "894:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );";
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
                        if ( (synpred229_Java()) ) {s = 16;}

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
            return "903:12: ( type | expression )";
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
                        if ( (synpred234_Java()) ) {s = 4;}

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
            return "908:34: ( identifierSuffix )?";
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
                        if ( (synpred237_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index149_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA149_3 = input.LA(1);

                         
                        int index149_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237_Java()) ) {s = 2;}

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
            return "912:38: ( identifierSuffix )?";
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
                        if ( (synpred243_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index151_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA151_3 = input.LA(1);

                         
                        int index151_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred243_Java()) ) {s = 2;}

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
            return "917:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );";
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
            return "()+ loopback of 919:9: ( '[' expression ']' )+";
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
                        if ( (synpred249_Java()) ) {s = 32;}

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
            return "()* loopback of 945:28: ( '[' expression ']' )*";
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
                        if ( (synpred262_Java()) ) {s = 32;}

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
 

    public static final BitSet FOLLOW_annotations_in_compilationUnit84 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit100 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit106 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit114 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_compilationUnit133 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit139 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit165 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit173 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit181 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_25_in_packageDeclaration207 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration209 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_packageDeclaration211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_importDeclaration238 = new BitSet(new long[]{0x0000000010000010L});
    public static final BitSet FOLLOW_28_in_importDeclaration240 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_importDeclaration243 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration246 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_importDeclaration248 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_importDeclaration252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_typeDeclaration289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration316 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers347 = new BitSet(new long[]{0x0000001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotation_in_classOrInterfaceModifier367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_classOrInterfaceModifier380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_classOrInterfaceModifier395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_classOrInterfaceModifier407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_classOrInterfaceModifier421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classOrInterfaceModifier434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_classOrInterfaceModifier449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_classOrInterfaceModifier465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_modifiers487 = new BitSet(new long[]{0x00F0001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_normalClassDeclaration540 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration542 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration544 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_38_in_normalClassDeclaration556 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration558 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_39_in_normalClassDeclaration571 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration573 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeParameters608 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters610 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeParameters613 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters615 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeParameters619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter638 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_38_in_typeParameter641 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound672 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_typeBound675 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeBound677 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration698 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration700 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_39_in_enumDeclaration703 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration705 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_enumBody728 = new BitSet(new long[]{0x0000220004000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody730 = new BitSet(new long[]{0x0000220004000000L});
    public static final BitSet FOLLOW_41_in_enumBody733 = new BitSet(new long[]{0x0000200004000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody736 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_enumBody739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants758 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_enumConstants761 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants763 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant788 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant791 = new BitSet(new long[]{0x000011C000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_enumConstant793 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_enumBodyDeclarations820 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations823 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_normalInterfaceDeclaration881 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration883 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration885 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_38_in_normalInterfaceDeclaration889 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration891 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList918 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_typeList921 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeList923 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_44_in_classBody948 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody950 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_classBody953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_interfaceBody976 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody978 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_interfaceBody981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_classBodyDeclaration1000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classBodyDeclaration1010 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classBodyDeclaration1023 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDeclaration_in_memberDecl1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_memberDecl1068 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl1070 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl1072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl1082 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_memberDeclaration1127 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDeclaration1130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDeclaration1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1154 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest1180 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_genericMethodOrConstructorRest1184 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1199 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration1220 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration1241 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_fieldDeclaration1243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceBodyDeclaration1270 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_interfaceBodyDeclaration1282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_interfaceMemberDecl1321 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl1323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl1325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl1345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl1368 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl1370 = new BitSet(new long[]{0x0009000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl1372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest1395 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodOrFieldRest1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest1430 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_48_in_methodDeclaratorRest1433 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_methodDeclaratorRest1435 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_50_in_methodDeclaratorRest1448 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest1450 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest1466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_methodDeclaratorRest1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest1513 = new BitSet(new long[]{0x0004100014000000L});
    public static final BitSet FOLLOW_50_in_voidMethodDeclaratorRest1516 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1518 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest1534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_voidMethodDeclaratorRest1548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1581 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_48_in_interfaceMethodDeclaratorRest1584 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_interfaceMethodDeclaratorRest1586 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_50_in_interfaceMethodDeclaratorRest1591 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1593 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodDeclaratorRest1597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl1620 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl1623 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_interfaceGenericMethodDecl1627 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl1630 = new BitSet(new long[]{0x0009000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1663 = new BitSet(new long[]{0x0004000004000000L});
    public static final BitSet FOLLOW_50_in_voidInterfaceMethodDeclaratorRest1666 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1668 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_voidInterfaceMethodDeclaratorRest1672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest1695 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_50_in_constructorDeclaratorRest1698 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1700 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_constructorBody_in_constructorDeclaratorRest1704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator1723 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator1725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1748 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclarators1751 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1753 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_variableDeclarator1774 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_variableDeclarator1777 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator1779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1804 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_constantDeclaratorsRest1807 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1809 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_48_in_constantDeclaratorRest1831 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_constantDeclaratorRest1833 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_51_in_constantDeclaratorRest1837 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1862 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_variableDeclaratorId1865 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_variableDeclaratorId1867 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_arrayInitializer1925 = new BitSet(new long[]{0xFF00B00000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1928 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer1931 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1933 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer1938 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_arrayInitializer1945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier1964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_modifier1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_modifier1984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_modifier1994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_modifier2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_modifier2014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_modifier2024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier2034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier2044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_modifier2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_modifier2074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_packageOrTypeName2093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName2112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_typeName2131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2145 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2148 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2150 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type2157 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2160 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2162 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2175 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2177 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_classOrInterfaceType2181 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2183 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2185 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_variableModifier2294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier2304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeArguments2323 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2325 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeArguments2328 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2330 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeArguments2334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument2357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_typeArgument2367 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument2370 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument2378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2403 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_qualifiedNameList2406 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2408 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_66_in_formalParameters2429 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000208L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters2431 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_formalParameters2434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameterDecls2457 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls2459 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000010L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2484 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_formalParameterDeclsRest2487 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_formalParameterDeclsRest2501 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody2526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_constructorBody2545 = new BitSet(new long[]{0xFF20F13F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody2547 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_constructorBody2550 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_constructorBody2553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2572 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation2575 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation2583 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation2585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation2595 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_explicitConstructorInvocation2597 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2599 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_explicitConstructorInvocation2602 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation2604 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation2606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2626 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_qualifiedName2629 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2631 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal2657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal2667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal2677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal2687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal2697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_literal2707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations2796 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_annotation2816 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation2818 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation2822 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation2826 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_elementValue_in_annotation2830 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotation2835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2859 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_annotationName2862 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2864 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2885 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_elementValuePairs2888 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2890 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair2911 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_elementValuePair2913 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair2915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue2938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue2958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_elementValueArrayInitializer2981 = new BitSet(new long[]{0xFF00B20000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2984 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer2987 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2989 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer2996 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_elementValueArrayInitializer3000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_annotationTypeDeclaration3023 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_annotationTypeDeclaration3025 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration3027 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_annotationTypeBody3052 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3055 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_annotationTypeBody3059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeElementDeclaration3082 = new BitSet(new long[]{0xFF00403F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest3107 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3109 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3121 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3134 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest3147 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3160 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest3219 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest3221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotationMethodRest3223 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest3225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest3249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_defaultValue3272 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue3274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_block3295 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_block3297 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_block3300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement3323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement3333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement3343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement3367 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_localVariableDeclarationStatement3369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration3388 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration3390 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration3392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_variableModifiers3415 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_block_in_statement3433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement3443 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3445 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement3448 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3450 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_statement3464 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3466 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3468 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_statement3478 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement3492 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement3494 = new BitSet(new long[]{0xFF00900804000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forControl_in_statement3496 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_statement3498 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement3510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3512 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement3524 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement3528 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3530 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement3542 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3544 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_catches_in_statement3556 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_statement3558 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement3572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement3586 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement3608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3610 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_statement3612 = new BitSet(new long[]{0x0000200000000000L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement3614 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_statement3616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_statement3626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3628 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement3640 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3642 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement3655 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3657 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement3669 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3671 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_statement3684 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3686 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_statement3699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement3710 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement3722 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement3724 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches3749 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_catchClause_in_catches3752 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_catchClause3777 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause3779 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause3781 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_catchClause3783 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_catchClause3785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter3804 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameter3806 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter3808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups3836 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup3863 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60002FBD7E6L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup3866 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_89_in_switchLabel3890 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel3892 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_switchLabel3904 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel3906 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_switchLabel3918 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_forControl3951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl3961 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl3964 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_forControl3966 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl3969 = new BitSet(new long[]{0xFF00900800000FD2L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forUpdate_in_forControl3971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit3991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit4001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_enhancedForControl4024 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_enhancedForControl4026 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enhancedForControl4028 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_enhancedForControl4030 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_enhancedForControl4032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate4051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_parExpression4072 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_parExpression4074 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_parExpression4076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4099 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_expressionList4102 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expressionList4104 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression4125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression4148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression4171 = new BitSet(new long[]{0x0008050000000002L,0x00000003FC000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression4174 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expression4176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator4211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator4221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator4231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator4241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator4251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator4261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator4271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator4281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_assignmentOperator4302 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_assignmentOperator4306 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4344 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4348 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4352 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4387 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4391 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression4424 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_conditionalExpression4428 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_conditionalExpression4432 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4456 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalOrExpression4460 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4462 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4484 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_conditionalAndExpression4488 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4490 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4512 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_inclusiveOrExpression4516 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4518 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression4540 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_exclusiveOrExpression4544 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression4546 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression4568 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_andExpression4572 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression4574 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression4596 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression4600 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression4608 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression4630 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_instanceOfExpression4633 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression4635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression4656 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression4660 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression4662 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp4697 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp4701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp4731 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp4735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp4756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp4767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression4787 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression4791 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression4793 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_shiftOp4824 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_shiftOp4828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp4860 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4864 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp4898 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression4932 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression4936 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression4944 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression4966 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression4970 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression4984 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_105_in_unaryExpression5010 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_unaryExpression5022 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression5034 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpression5046 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus5077 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_unaryExpressionNotPlusMinus5089 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus5111 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus5113 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus5116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5139 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression5141 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5143 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression5145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5154 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_type_in_castExpression5157 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_castExpression5161 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5164 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary5185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_primary5195 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5198 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5200 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_primary5215 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_primary5217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary5227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary5237 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_creator_in_primary5239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary5249 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5252 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5254 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary5269 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_48_in_primary5272 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_primary5274 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_primary5278 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary5280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_primary5290 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_primary5292 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary5294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix5314 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix5316 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5320 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix5322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix5333 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix5335 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix5337 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5360 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix5362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5372 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix5374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5384 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_identifierSuffix5386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5396 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_identifierSuffix5398 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5410 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix5412 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix5414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator5433 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_createdName_in_creator5435 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createdName_in_creator5447 = new BitSet(new long[]{0x0001000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator5450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName5474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName5484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator5507 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator5510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator5512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5531 = new BitSet(new long[]{0xFF02900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5545 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5548 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5550 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest5554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest5568 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5570 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5573 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest5575 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5577 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5582 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5584 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest5615 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest5617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation5641 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocation5643 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation5645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_nonWildcardTypeArguments5668 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments5670 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_nonWildcardTypeArguments5672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5695 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector5697 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_selector5699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5710 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_selector5712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5722 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_selector5724 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_selector5726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5736 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector5738 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector5740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_selector5750 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_selector5752 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_selector5754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix5777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_superSuffix5787 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix5789 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_superSuffix5791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_arguments5811 = new BitSet(new long[]{0xFF00900800000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_expressionList_in_arguments5813 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_arguments5816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred5_Java84 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred5_Java100 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_synpred5_Java106 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java114 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java133 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java139 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred113_Java2547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred117_Java2572 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_synpred117_Java2575 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_synpred117_Java2583 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_synpred117_Java2585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred128_Java2796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred151_Java3323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred152_Java3333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_synpred157_Java3478 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_synpred157_Java3480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred162_Java3556 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_synpred162_Java3558 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_synpred162_Java3560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred163_Java3572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchLabel_in_synpred178_Java3863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred180_Java3890 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_synpred180_Java3892 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred180_Java3894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred181_Java3904 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred181_Java3906 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred181_Java3908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_synpred182_Java3951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred186_Java3991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred188_Java4174 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred188_Java4176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred198_Java4292 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred198_Java4294 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred198_Java4296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred199_Java4332 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred199_Java4334 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred199_Java4336 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred199_Java4338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred200_Java4377 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred200_Java4379 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred200_Java4381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred211_Java4689 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred211_Java4691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred212_Java4723 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred212_Java4725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred215_Java4816 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred215_Java4818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred216_Java4850 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred216_Java4852 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred216_Java4854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred217_Java4890 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred217_Java4892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred229_Java5101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_synpred233_Java5139 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred233_Java5141 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_synpred233_Java5143 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred233_Java5145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred234_Java5157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred236_Java5198 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred236_Java5200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred237_Java5204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred242_Java5252 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred242_Java5254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred243_Java5258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred249_Java5333 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred249_Java5335 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred249_Java5337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred262_Java5573 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred262_Java5575 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred262_Java5577 = new BitSet(new long[]{0x0000000000000002L});

}