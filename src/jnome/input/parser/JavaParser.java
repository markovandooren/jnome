// $ANTLR 3.1.1 /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g 2008-10-16 18:34:56

package jnome.input.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
        

    public String[] getTokenNames() { return JavaParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g"; }



    // $ANTLR start "compilationUnit"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:184:1: compilationUnit : ( annotations ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) | ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* );
    public final void compilationUnit() throws RecognitionException {
        int compilationUnit_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:185:5: ( annotations ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) | ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:185:9: annotations ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
                    {
                    pushFollow(FOLLOW_annotations_in_compilationUnit61);
                    annotations();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:9: ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==25) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==ENUM||LA4_0==28||(LA4_0>=31 && LA4_0<=37)||LA4_0==46||LA4_0==73) ) {
                        alt4=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:13: packageDeclaration ( importDeclaration )* ( typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit75);
                            packageDeclaration();

                            state._fsp--;
                            if (state.failed) return ;
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:32: ( importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==27) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: importDeclaration
                            	    {
                            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit77);
                            	    importDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    break loop1;
                                }
                            } while (true);

                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:51: ( typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==ENUM||LA2_0==26||LA2_0==28||(LA2_0>=31 && LA2_0<=37)||LA2_0==46||LA2_0==73) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit80);
                            	    typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    break loop2;
                                }
                            } while (true);


                            }
                            break;
                        case 2 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:187:13: classOrInterfaceDeclaration ( typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit95);
                            classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return ;
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:187:41: ( typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==ENUM||LA3_0==26||LA3_0==28||(LA3_0>=31 && LA3_0<=37)||LA3_0==46||LA3_0==73) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit97);
                            	    typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return ;

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
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:189:9: ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:189:9: ( packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==25) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: packageDeclaration
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit118);
                            packageDeclaration();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:189:29: ( importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==27) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: importDeclaration
                    	    {
                    	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit121);
                    	    importDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:189:48: ( typeDeclaration )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ENUM||LA7_0==26||LA7_0==28||(LA7_0>=31 && LA7_0<=37)||LA7_0==46||LA7_0==73) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeDeclaration
                    	    {
                    	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit124);
                    	    typeDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "compilationUnit"


    // $ANTLR start "packageDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:192:1: packageDeclaration : 'package' qualifiedName ';' ;
    public final void packageDeclaration() throws RecognitionException {
        int packageDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:193:5: ( 'package' qualifiedName ';' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:193:9: 'package' qualifiedName ';'
            {
            match(input,25,FOLLOW_25_in_packageDeclaration144); if (state.failed) return ;
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration146);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;
            match(input,26,FOLLOW_26_in_packageDeclaration148); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "packageDeclaration"


    // $ANTLR start "importDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:196:1: importDeclaration : 'import' ( 'static' )? qualifiedName ( '.' '*' )? ';' ;
    public final void importDeclaration() throws RecognitionException {
        int importDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:197:5: ( 'import' ( 'static' )? qualifiedName ( '.' '*' )? ';' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:197:9: 'import' ( 'static' )? qualifiedName ( '.' '*' )? ';'
            {
            match(input,27,FOLLOW_27_in_importDeclaration171); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:197:18: ( 'static' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==28) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: 'static'
                    {
                    match(input,28,FOLLOW_28_in_importDeclaration173); if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_qualifiedName_in_importDeclaration176);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:197:42: ( '.' '*' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==29) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:197:43: '.' '*'
                    {
                    match(input,29,FOLLOW_29_in_importDeclaration179); if (state.failed) return ;
                    match(input,30,FOLLOW_30_in_importDeclaration181); if (state.failed) return ;

                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_importDeclaration185); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "importDeclaration"


    // $ANTLR start "typeDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:200:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final void typeDeclaration() throws RecognitionException {
        int typeDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:201:5: ( classOrInterfaceDeclaration | ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ENUM||LA11_0==28||(LA11_0>=31 && LA11_0<=37)||LA11_0==46||LA11_0==73) ) {
                alt11=1;
            }
            else if ( (LA11_0==26) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:201:9: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration208);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:202:9: ';'
                    {
                    match(input,26,FOLLOW_26_in_typeDeclaration218); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, typeDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeDeclaration"


    // $ANTLR start "classOrInterfaceDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:205:1: classOrInterfaceDeclaration : classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration ) ;
    public final void classOrInterfaceDeclaration() throws RecognitionException {
        int classOrInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:206:5: ( classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration ) )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:206:9: classOrInterfaceModifiers ( classDeclaration | interfaceDeclaration )
            {
            pushFollow(FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration241);
            classOrInterfaceModifiers();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:206:35: ( classDeclaration | interfaceDeclaration )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ENUM||LA12_0==37) ) {
                alt12=1;
            }
            else if ( (LA12_0==46||LA12_0==73) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:206:36: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration244);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:206:55: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration248);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 5, classOrInterfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classOrInterfaceDeclaration"


    // $ANTLR start "classOrInterfaceModifiers"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:209:1: classOrInterfaceModifiers : ( classOrInterfaceModifier )* ;
    public final void classOrInterfaceModifiers() throws RecognitionException {
        int classOrInterfaceModifiers_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:210:5: ( ( classOrInterfaceModifier )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:210:9: ( classOrInterfaceModifier )*
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:210:9: ( classOrInterfaceModifier )*
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: classOrInterfaceModifier
            	    {
            	    pushFollow(FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers272);
            	    classOrInterfaceModifier();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, classOrInterfaceModifiers_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classOrInterfaceModifiers"


    // $ANTLR start "classOrInterfaceModifier"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:213:1: classOrInterfaceModifier : ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' );
    public final void classOrInterfaceModifier() throws RecognitionException {
        int classOrInterfaceModifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:214:5: ( annotation | 'public' | 'protected' | 'private' | 'abstract' | 'static' | 'final' | 'strictfp' )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:214:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_classOrInterfaceModifier292);
                    annotation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:215:9: 'public'
                    {
                    match(input,31,FOLLOW_31_in_classOrInterfaceModifier305); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:216:9: 'protected'
                    {
                    match(input,32,FOLLOW_32_in_classOrInterfaceModifier320); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:217:9: 'private'
                    {
                    match(input,33,FOLLOW_33_in_classOrInterfaceModifier332); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:218:9: 'abstract'
                    {
                    match(input,34,FOLLOW_34_in_classOrInterfaceModifier346); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:219:9: 'static'
                    {
                    match(input,28,FOLLOW_28_in_classOrInterfaceModifier359); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:220:9: 'final'
                    {
                    match(input,35,FOLLOW_35_in_classOrInterfaceModifier374); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:221:9: 'strictfp'
                    {
                    match(input,36,FOLLOW_36_in_classOrInterfaceModifier390); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, classOrInterfaceModifier_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classOrInterfaceModifier"


    // $ANTLR start "modifiers"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:224:1: modifiers : ( modifier )* ;
    public final void modifiers() throws RecognitionException {
        int modifiers_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:225:5: ( ( modifier )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:225:9: ( modifier )*
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:225:9: ( modifier )*
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_modifiers412);
            	    modifier();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, modifiers_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "modifiers"


    // $ANTLR start "classDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:228:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final void classDeclaration() throws RecognitionException {
        int classDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:229:5: ( normalClassDeclaration | enumDeclaration )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==37) ) {
                alt16=1;
            }
            else if ( (LA16_0==ENUM) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:229:9: normalClassDeclaration
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration432);
                    normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:230:9: enumDeclaration
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration442);
                    enumDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, classDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classDeclaration"


    // $ANTLR start "normalClassDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:233:1: normalClassDeclaration : 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final void normalClassDeclaration() throws RecognitionException {
        int normalClassDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:234:5: ( 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:234:9: 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            match(input,37,FOLLOW_37_in_normalClassDeclaration465); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration467); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:234:28: ( typeParameters )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==40) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration469);
                    typeParameters();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:235:9: ( 'extends' type )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==38) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:235:10: 'extends' type
                    {
                    match(input,38,FOLLOW_38_in_normalClassDeclaration481); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_normalClassDeclaration483);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:236:9: ( 'implements' typeList )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==39) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:236:10: 'implements' typeList
                    {
                    match(input,39,FOLLOW_39_in_normalClassDeclaration496); if (state.failed) return ;
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration498);
                    typeList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration510);
            classBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, normalClassDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "normalClassDeclaration"


    // $ANTLR start "typeParameters"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:240:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final void typeParameters() throws RecognitionException {
        int typeParameters_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:241:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:241:9: '<' typeParameter ( ',' typeParameter )* '>'
            {
            match(input,40,FOLLOW_40_in_typeParameters533); if (state.failed) return ;
            pushFollow(FOLLOW_typeParameter_in_typeParameters535);
            typeParameter();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:241:27: ( ',' typeParameter )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==41) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:241:28: ',' typeParameter
            	    {
            	    match(input,41,FOLLOW_41_in_typeParameters538); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters540);
            	    typeParameter();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            match(input,42,FOLLOW_42_in_typeParameters544); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, typeParameters_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeParameters"


    // $ANTLR start "typeParameter"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:244:1: typeParameter : Identifier ( 'extends' typeBound )? ;
    public final void typeParameter() throws RecognitionException {
        int typeParameter_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:245:5: ( Identifier ( 'extends' typeBound )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:245:9: Identifier ( 'extends' typeBound )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_typeParameter563); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:245:20: ( 'extends' typeBound )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==38) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:245:21: 'extends' typeBound
                    {
                    match(input,38,FOLLOW_38_in_typeParameter566); if (state.failed) return ;
                    pushFollow(FOLLOW_typeBound_in_typeParameter568);
                    typeBound();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 12, typeParameter_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeParameter"


    // $ANTLR start "typeBound"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:248:1: typeBound : type ( '&' type )* ;
    public final void typeBound() throws RecognitionException {
        int typeBound_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:249:5: ( type ( '&' type )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:249:9: type ( '&' type )*
            {
            pushFollow(FOLLOW_type_in_typeBound597);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:249:14: ( '&' type )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==43) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:249:15: '&' type
            	    {
            	    match(input,43,FOLLOW_43_in_typeBound600); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeBound602);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 13, typeBound_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeBound"


    // $ANTLR start "enumDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:252:1: enumDeclaration : ENUM Identifier ( 'implements' typeList )? enumBody ;
    public final void enumDeclaration() throws RecognitionException {
        int enumDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:253:5: ( ENUM Identifier ( 'implements' typeList )? enumBody )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:253:9: ENUM Identifier ( 'implements' typeList )? enumBody
            {
            match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration623); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration625); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:253:25: ( 'implements' typeList )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==39) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:253:26: 'implements' typeList
                    {
                    match(input,39,FOLLOW_39_in_enumDeclaration628); if (state.failed) return ;
                    pushFollow(FOLLOW_typeList_in_enumDeclaration630);
                    typeList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration634);
            enumBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 14, enumDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumDeclaration"


    // $ANTLR start "enumBody"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:256:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final void enumBody() throws RecognitionException {
        int enumBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:257:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:257:9: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            match(input,44,FOLLOW_44_in_enumBody653); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:257:13: ( enumConstants )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==Identifier||LA24_0==73) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody655);
                    enumConstants();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:257:28: ( ',' )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==41) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: ','
                    {
                    match(input,41,FOLLOW_41_in_enumBody658); if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:257:33: ( enumBodyDeclarations )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==26) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody661);
                    enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,45,FOLLOW_45_in_enumBody664); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 15, enumBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumBody"


    // $ANTLR start "enumConstants"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:260:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final void enumConstants() throws RecognitionException {
        int enumConstants_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:261:5: ( enumConstant ( ',' enumConstant )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:261:9: enumConstant ( ',' enumConstant )*
            {
            pushFollow(FOLLOW_enumConstant_in_enumConstants683);
            enumConstant();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:261:22: ( ',' enumConstant )*
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:261:23: ',' enumConstant
            	    {
            	    match(input,41,FOLLOW_41_in_enumConstants686); if (state.failed) return ;
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants688);
            	    enumConstant();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, enumConstants_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumConstants"


    // $ANTLR start "enumConstant"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:264:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final void enumConstant() throws RecognitionException {
        int enumConstant_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:265:5: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:265:9: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:265:9: ( annotations )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==73) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant713);
                    annotations();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_enumConstant716); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:265:33: ( arguments )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==66) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant718);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:265:44: ( classBody )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==44) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant721);
                    classBody();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 17, enumConstant_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumConstant"


    // $ANTLR start "enumBodyDeclarations"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:268:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final void enumBodyDeclarations() throws RecognitionException {
        int enumBodyDeclarations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:269:5: ( ';' ( classBodyDeclaration )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:269:9: ';' ( classBodyDeclaration )*
            {
            match(input,26,FOLLOW_26_in_enumBodyDeclarations745); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:269:13: ( classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=Identifier && LA31_0<=ENUM)||LA31_0==26||LA31_0==28||(LA31_0>=31 && LA31_0<=37)||LA31_0==40||LA31_0==44||(LA31_0>=46 && LA31_0<=47)||(LA31_0>=52 && LA31_0<=63)||LA31_0==73) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:269:14: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations748);
            	    classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 18, enumBodyDeclarations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumBodyDeclarations"


    // $ANTLR start "interfaceDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:272:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final void interfaceDeclaration() throws RecognitionException {
        int interfaceDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:273:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==46) ) {
                alt32=1;
            }
            else if ( (LA32_0==73) ) {
                alt32=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:273:9: normalInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration773);
                    normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:274:9: annotationTypeDeclaration
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration783);
                    annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, interfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceDeclaration"


    // $ANTLR start "normalInterfaceDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:277:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final void normalInterfaceDeclaration() throws RecognitionException {
        int normalInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:278:5: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:278:9: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            match(input,46,FOLLOW_46_in_normalInterfaceDeclaration806); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration808); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:278:32: ( typeParameters )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==40) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration810);
                    typeParameters();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:278:48: ( 'extends' typeList )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==38) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:278:49: 'extends' typeList
                    {
                    match(input,38,FOLLOW_38_in_normalInterfaceDeclaration814); if (state.failed) return ;
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration816);
                    typeList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration820);
            interfaceBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, normalInterfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "normalInterfaceDeclaration"


    // $ANTLR start "typeList"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:281:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        int typeList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:282:5: ( type ( ',' type )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:282:9: type ( ',' type )*
            {
            pushFollow(FOLLOW_type_in_typeList843);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:282:14: ( ',' type )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==41) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:282:15: ',' type
            	    {
            	    match(input,41,FOLLOW_41_in_typeList846); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList848);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, typeList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeList"


    // $ANTLR start "classBody"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:285:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final void classBody() throws RecognitionException {
        int classBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:286:5: ( '{' ( classBodyDeclaration )* '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:286:9: '{' ( classBodyDeclaration )* '}'
            {
            match(input,44,FOLLOW_44_in_classBody873); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:286:13: ( classBodyDeclaration )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=Identifier && LA36_0<=ENUM)||LA36_0==26||LA36_0==28||(LA36_0>=31 && LA36_0<=37)||LA36_0==40||LA36_0==44||(LA36_0>=46 && LA36_0<=47)||(LA36_0>=52 && LA36_0<=63)||LA36_0==73) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody875);
            	    classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            match(input,45,FOLLOW_45_in_classBody878); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 22, classBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classBody"


    // $ANTLR start "interfaceBody"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:289:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final void interfaceBody() throws RecognitionException {
        int interfaceBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:290:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:290:9: '{' ( interfaceBodyDeclaration )* '}'
            {
            match(input,44,FOLLOW_44_in_interfaceBody901); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:290:13: ( interfaceBodyDeclaration )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=Identifier && LA37_0<=ENUM)||LA37_0==26||LA37_0==28||(LA37_0>=31 && LA37_0<=37)||LA37_0==40||(LA37_0>=46 && LA37_0<=47)||(LA37_0>=52 && LA37_0<=63)||LA37_0==73) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody903);
            	    interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            match(input,45,FOLLOW_45_in_interfaceBody906); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, interfaceBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceBody"


    // $ANTLR start "classBodyDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:293:1: classBodyDeclaration : ( ';' | ( 'static' )? block | modifiers memberDecl );
    public final void classBodyDeclaration() throws RecognitionException {
        int classBodyDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:294:5: ( ';' | ( 'static' )? block | modifiers memberDecl )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:294:9: ';'
                    {
                    match(input,26,FOLLOW_26_in_classBodyDeclaration925); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:295:9: ( 'static' )? block
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:295:9: ( 'static' )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==28) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: 'static'
                            {
                            match(input,28,FOLLOW_28_in_classBodyDeclaration935); if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration938);
                    block();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:296:9: modifiers memberDecl
                    {
                    pushFollow(FOLLOW_modifiers_in_classBodyDeclaration948);
                    modifiers();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration950);
                    memberDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, classBodyDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classBodyDeclaration"


    // $ANTLR start "memberDecl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:299:1: memberDecl : ( genericMethodOrConstructorDecl | memberDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void memberDecl() throws RecognitionException {
        int memberDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:300:5: ( genericMethodOrConstructorDecl | memberDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:300:9: genericMethodOrConstructorDecl
                    {
                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl973);
                    genericMethodOrConstructorDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:301:9: memberDeclaration
                    {
                    pushFollow(FOLLOW_memberDeclaration_in_memberDecl983);
                    memberDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:302:9: 'void' Identifier voidMethodDeclaratorRest
                    {
                    match(input,47,FOLLOW_47_in_memberDecl993); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl995); if (state.failed) return ;
                    pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl997);
                    voidMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:303:9: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl1007); if (state.failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl1009);
                    constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:304:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1019);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:305:9: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1029);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, memberDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "memberDecl"


    // $ANTLR start "memberDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:308:1: memberDeclaration : type ( methodDeclaration | fieldDeclaration ) ;
    public final void memberDeclaration() throws RecognitionException {
        int memberDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:309:5: ( type ( methodDeclaration | fieldDeclaration ) )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:309:9: type ( methodDeclaration | fieldDeclaration )
            {
            pushFollow(FOLLOW_type_in_memberDeclaration1052);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:309:14: ( methodDeclaration | fieldDeclaration )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==Identifier) ) {
                int LA41_1 = input.LA(2);

                if ( (LA41_1==66) ) {
                    alt41=1;
                }
                else if ( (LA41_1==26||LA41_1==41||LA41_1==48||LA41_1==51) ) {
                    alt41=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:309:15: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDeclaration1055);
                    methodDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:309:35: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDeclaration1059);
                    fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 26, memberDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "memberDeclaration"


    // $ANTLR start "genericMethodOrConstructorDecl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:312:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final void genericMethodOrConstructorDecl() throws RecognitionException {
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:313:5: ( typeParameters genericMethodOrConstructorRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:313:9: typeParameters genericMethodOrConstructorRest
            {
            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1079);
            typeParameters();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1081);
            genericMethodOrConstructorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, genericMethodOrConstructorDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "genericMethodOrConstructorDecl"


    // $ANTLR start "genericMethodOrConstructorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:316:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final void genericMethodOrConstructorRest() throws RecognitionException {
        int genericMethodOrConstructorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:317:5: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 43, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA43_0==47||(LA43_0>=56 && LA43_0<=63)) ) {
                alt43=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:317:9: ( type | 'void' ) Identifier methodDeclaratorRest
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:317:9: ( type | 'void' )
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==Identifier||(LA42_0>=56 && LA42_0<=63)) ) {
                        alt42=1;
                    }
                    else if ( (LA42_0==47) ) {
                        alt42=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 42, 0, input);

                        throw nvae;
                    }
                    switch (alt42) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:317:10: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest1105);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:317:17: 'void'
                            {
                            match(input,47,FOLLOW_47_in_genericMethodOrConstructorRest1109); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1112); if (state.failed) return ;
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1114);
                    methodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:318:9: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest1124); if (state.failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1126);
                    constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 28, genericMethodOrConstructorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "genericMethodOrConstructorRest"


    // $ANTLR start "methodDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:321:1: methodDeclaration : Identifier methodDeclaratorRest ;
    public final void methodDeclaration() throws RecognitionException {
        int methodDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:322:5: ( Identifier methodDeclaratorRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:322:9: Identifier methodDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration1145); if (state.failed) return ;
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration1147);
            methodDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 29, methodDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "methodDeclaration"


    // $ANTLR start "fieldDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:325:1: fieldDeclaration : variableDeclarators ';' ;
    public final void fieldDeclaration() throws RecognitionException {
        int fieldDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:326:5: ( variableDeclarators ';' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:326:9: variableDeclarators ';'
            {
            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration1166);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return ;
            match(input,26,FOLLOW_26_in_fieldDeclaration1168); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 30, fieldDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "fieldDeclaration"


    // $ANTLR start "interfaceBodyDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:329:1: interfaceBodyDeclaration : ( modifiers interfaceMemberDecl | ';' );
    public final void interfaceBodyDeclaration() throws RecognitionException {
        int interfaceBodyDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:330:5: ( modifiers interfaceMemberDecl | ';' )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( ((LA44_0>=Identifier && LA44_0<=ENUM)||LA44_0==28||(LA44_0>=31 && LA44_0<=37)||LA44_0==40||(LA44_0>=46 && LA44_0<=47)||(LA44_0>=52 && LA44_0<=63)||LA44_0==73) ) {
                alt44=1;
            }
            else if ( (LA44_0==26) ) {
                alt44=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:330:9: modifiers interfaceMemberDecl
                    {
                    pushFollow(FOLLOW_modifiers_in_interfaceBodyDeclaration1195);
                    modifiers();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1197);
                    interfaceMemberDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:331:9: ';'
                    {
                    match(input,26,FOLLOW_26_in_interfaceBodyDeclaration1207); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 31, interfaceBodyDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceBodyDeclaration"


    // $ANTLR start "interfaceMemberDecl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:334:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void interfaceMemberDecl() throws RecognitionException {
        int interfaceMemberDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:335:5: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:335:9: interfaceMethodOrFieldDecl
                    {
                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1226);
                    interfaceMethodOrFieldDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:336:9: interfaceGenericMethodDecl
                    {
                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1236);
                    interfaceGenericMethodDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:337:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
                    {
                    match(input,47,FOLLOW_47_in_interfaceMemberDecl1246); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl1248); if (state.failed) return ;
                    pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl1250);
                    voidInterfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:338:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1260);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:339:9: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl1270);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 32, interfaceMemberDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMemberDecl"


    // $ANTLR start "interfaceMethodOrFieldDecl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:342:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final void interfaceMethodOrFieldDecl() throws RecognitionException {
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:343:5: ( type Identifier interfaceMethodOrFieldRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:343:9: type Identifier interfaceMethodOrFieldRest
            {
            pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl1293);
            type();

            state._fsp--;
            if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl1295); if (state.failed) return ;
            pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl1297);
            interfaceMethodOrFieldRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, interfaceMethodOrFieldDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMethodOrFieldDecl"


    // $ANTLR start "interfaceMethodOrFieldRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:346:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
    public final void interfaceMethodOrFieldRest() throws RecognitionException {
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:347:5: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==48||LA46_0==51) ) {
                alt46=1;
            }
            else if ( (LA46_0==66) ) {
                alt46=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:347:9: constantDeclaratorsRest ';'
                    {
                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest1320);
                    constantDeclaratorsRest();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_interfaceMethodOrFieldRest1322); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:348:9: interfaceMethodDeclaratorRest
                    {
                    pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1332);
                    interfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, interfaceMethodOrFieldRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMethodOrFieldRest"


    // $ANTLR start "methodDeclaratorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:351:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void methodDeclaratorRest() throws RecognitionException {
        int methodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:352:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:352:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest1355);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:352:26: ( '[' ']' )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==48) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:352:27: '[' ']'
            	    {
            	    match(input,48,FOLLOW_48_in_methodDeclaratorRest1358); if (state.failed) return ;
            	    match(input,49,FOLLOW_49_in_methodDeclaratorRest1360); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:353:9: ( 'throws' qualifiedNameList )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==50) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:353:10: 'throws' qualifiedNameList
                    {
                    match(input,50,FOLLOW_50_in_methodDeclaratorRest1373); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest1375);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:354:9: ( methodBody | ';' )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==44) ) {
                alt49=1;
            }
            else if ( (LA49_0==26) ) {
                alt49=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:354:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest1391);
                    methodBody();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:355:13: ';'
                    {
                    match(input,26,FOLLOW_26_in_methodDeclaratorRest1405); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 35, methodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "methodDeclaratorRest"


    // $ANTLR start "voidMethodDeclaratorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:359:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void voidMethodDeclaratorRest() throws RecognitionException {
        int voidMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:360:5: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:360:9: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest1438);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:360:26: ( 'throws' qualifiedNameList )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==50) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:360:27: 'throws' qualifiedNameList
                    {
                    match(input,50,FOLLOW_50_in_voidMethodDeclaratorRest1441); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1443);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:361:9: ( methodBody | ';' )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==44) ) {
                alt51=1;
            }
            else if ( (LA51_0==26) ) {
                alt51=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:361:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest1459);
                    methodBody();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:362:13: ';'
                    {
                    match(input,26,FOLLOW_26_in_voidMethodDeclaratorRest1473); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 36, voidMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "voidMethodDeclaratorRest"


    // $ANTLR start "interfaceMethodDeclaratorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:366:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final void interfaceMethodDeclaratorRest() throws RecognitionException {
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:367:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:367:9: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1506);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:367:26: ( '[' ']' )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==48) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:367:27: '[' ']'
            	    {
            	    match(input,48,FOLLOW_48_in_interfaceMethodDeclaratorRest1509); if (state.failed) return ;
            	    match(input,49,FOLLOW_49_in_interfaceMethodDeclaratorRest1511); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:367:37: ( 'throws' qualifiedNameList )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==50) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:367:38: 'throws' qualifiedNameList
                    {
                    match(input,50,FOLLOW_50_in_interfaceMethodDeclaratorRest1516); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1518);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_interfaceMethodDeclaratorRest1522); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, interfaceMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMethodDeclaratorRest"


    // $ANTLR start "interfaceGenericMethodDecl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:370:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final void interfaceGenericMethodDecl() throws RecognitionException {
        int interfaceGenericMethodDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:371:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:371:9: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl1545);
            typeParameters();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:371:24: ( type | 'void' )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==Identifier||(LA54_0>=56 && LA54_0<=63)) ) {
                alt54=1;
            }
            else if ( (LA54_0==47) ) {
                alt54=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:371:25: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl1548);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:371:32: 'void'
                    {
                    match(input,47,FOLLOW_47_in_interfaceGenericMethodDecl1552); if (state.failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl1555); if (state.failed) return ;
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1565);
            interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, interfaceGenericMethodDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceGenericMethodDecl"


    // $ANTLR start "voidInterfaceMethodDeclaratorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:375:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final void voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:376:5: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:376:9: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1588);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:376:26: ( 'throws' qualifiedNameList )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==50) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:376:27: 'throws' qualifiedNameList
                    {
                    match(input,50,FOLLOW_50_in_voidInterfaceMethodDeclaratorRest1591); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1593);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_voidInterfaceMethodDeclaratorRest1597); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 39, voidInterfaceMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "voidInterfaceMethodDeclaratorRest"


    // $ANTLR start "constructorDeclaratorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:379:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? constructorBody ;
    public final void constructorDeclaratorRest() throws RecognitionException {
        int constructorDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:380:5: ( formalParameters ( 'throws' qualifiedNameList )? constructorBody )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:380:9: formalParameters ( 'throws' qualifiedNameList )? constructorBody
            {
            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1620);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:380:26: ( 'throws' qualifiedNameList )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==50) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:380:27: 'throws' qualifiedNameList
                    {
                    match(input,50,FOLLOW_50_in_constructorDeclaratorRest1623); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1625);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_constructorBody_in_constructorDeclaratorRest1629);
            constructorBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 40, constructorDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constructorDeclaratorRest"


    // $ANTLR start "constantDeclarator"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:383:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final void constantDeclarator() throws RecognitionException {
        int constantDeclarator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:384:5: ( Identifier constantDeclaratorRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:384:9: Identifier constantDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator1648); if (state.failed) return ;
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator1650);
            constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 41, constantDeclarator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantDeclarator"


    // $ANTLR start "variableDeclarators"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:387:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
    public final void variableDeclarators() throws RecognitionException {
        int variableDeclarators_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:388:5: ( variableDeclarator ( ',' variableDeclarator )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:388:9: variableDeclarator ( ',' variableDeclarator )*
            {
            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1673);
            variableDeclarator();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:388:28: ( ',' variableDeclarator )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==41) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:388:29: ',' variableDeclarator
            	    {
            	    match(input,41,FOLLOW_41_in_variableDeclarators1676); if (state.failed) return ;
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1678);
            	    variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop57;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, variableDeclarators_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableDeclarators"


    // $ANTLR start "variableDeclarator"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:391:1: variableDeclarator : variableDeclaratorId ( '=' variableInitializer )? ;
    public final void variableDeclarator() throws RecognitionException {
        int variableDeclarator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:392:5: ( variableDeclaratorId ( '=' variableInitializer )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:392:9: variableDeclaratorId ( '=' variableInitializer )?
            {
            pushFollow(FOLLOW_variableDeclaratorId_in_variableDeclarator1699);
            variableDeclaratorId();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:392:30: ( '=' variableInitializer )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==51) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:392:31: '=' variableInitializer
                    {
                    match(input,51,FOLLOW_51_in_variableDeclarator1702); if (state.failed) return ;
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator1704);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 43, variableDeclarator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableDeclarator"


    // $ANTLR start "constantDeclaratorsRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:395:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
    public final void constantDeclaratorsRest() throws RecognitionException {
        int constantDeclaratorsRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:396:5: ( constantDeclaratorRest ( ',' constantDeclarator )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:396:9: constantDeclaratorRest ( ',' constantDeclarator )*
            {
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1729);
            constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:396:32: ( ',' constantDeclarator )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:396:33: ',' constantDeclarator
            	    {
            	    match(input,41,FOLLOW_41_in_constantDeclaratorsRest1732); if (state.failed) return ;
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1734);
            	    constantDeclarator();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 44, constantDeclaratorsRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantDeclaratorsRest"


    // $ANTLR start "constantDeclaratorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:399:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
    public final void constantDeclaratorRest() throws RecognitionException {
        int constantDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:400:5: ( ( '[' ']' )* '=' variableInitializer )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:400:9: ( '[' ']' )* '=' variableInitializer
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:400:9: ( '[' ']' )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==48) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:400:10: '[' ']'
            	    {
            	    match(input,48,FOLLOW_48_in_constantDeclaratorRest1756); if (state.failed) return ;
            	    match(input,49,FOLLOW_49_in_constantDeclaratorRest1758); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop60;
                }
            } while (true);

            match(input,51,FOLLOW_51_in_constantDeclaratorRest1762); if (state.failed) return ;
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1764);
            variableInitializer();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, constantDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantDeclaratorRest"


    // $ANTLR start "variableDeclaratorId"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:403:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
    public final void variableDeclaratorId() throws RecognitionException {
        int variableDeclaratorId_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:404:5: ( Identifier ( '[' ']' )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:404:9: Identifier ( '[' ']' )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1787); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:404:20: ( '[' ']' )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==48) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:404:21: '[' ']'
            	    {
            	    match(input,48,FOLLOW_48_in_variableDeclaratorId1790); if (state.failed) return ;
            	    match(input,49,FOLLOW_49_in_variableDeclaratorId1792); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 46, variableDeclaratorId_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableDeclaratorId"


    // $ANTLR start "variableInitializer"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:407:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        int variableInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:408:5: ( arrayInitializer | expression )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==44) ) {
                alt62=1;
            }
            else if ( (LA62_0==Identifier||(LA62_0>=FloatingPointLiteral && LA62_0<=DecimalLiteral)||LA62_0==47||(LA62_0>=56 && LA62_0<=63)||(LA62_0>=65 && LA62_0<=66)||(LA62_0>=69 && LA62_0<=72)||(LA62_0>=105 && LA62_0<=106)||(LA62_0>=109 && LA62_0<=113)) ) {
                alt62=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:408:9: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1813);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:409:9: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer1823);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 47, variableInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableInitializer"


    // $ANTLR start "arrayInitializer"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:412:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final void arrayInitializer() throws RecognitionException {
        int arrayInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:5: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:9: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            match(input,44,FOLLOW_44_in_arrayInitializer1850); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:13: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==Identifier||(LA65_0>=FloatingPointLiteral && LA65_0<=DecimalLiteral)||LA65_0==44||LA65_0==47||(LA65_0>=56 && LA65_0<=63)||(LA65_0>=65 && LA65_0<=66)||(LA65_0>=69 && LA65_0<=72)||(LA65_0>=105 && LA65_0<=106)||(LA65_0>=109 && LA65_0<=113)) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:14: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1853);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:34: ( ',' variableInitializer )*
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
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:35: ',' variableInitializer
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayInitializer1856); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1858);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:61: ( ',' )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==41) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:413:62: ','
                            {
                            match(input,41,FOLLOW_41_in_arrayInitializer1863); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,45,FOLLOW_45_in_arrayInitializer1870); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 48, arrayInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arrayInitializer"


    // $ANTLR start "modifier"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:416:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );
    public final void modifier() throws RecognitionException {
        int modifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:417:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:417:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_modifier1889);
                    annotation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:418:9: 'public'
                    {
                    match(input,31,FOLLOW_31_in_modifier1899); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:419:9: 'protected'
                    {
                    match(input,32,FOLLOW_32_in_modifier1909); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:420:9: 'private'
                    {
                    match(input,33,FOLLOW_33_in_modifier1919); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:421:9: 'static'
                    {
                    match(input,28,FOLLOW_28_in_modifier1929); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:422:9: 'abstract'
                    {
                    match(input,34,FOLLOW_34_in_modifier1939); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:423:9: 'final'
                    {
                    match(input,35,FOLLOW_35_in_modifier1949); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:424:9: 'native'
                    {
                    match(input,52,FOLLOW_52_in_modifier1959); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:425:9: 'synchronized'
                    {
                    match(input,53,FOLLOW_53_in_modifier1969); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:426:9: 'transient'
                    {
                    match(input,54,FOLLOW_54_in_modifier1979); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:427:9: 'volatile'
                    {
                    match(input,55,FOLLOW_55_in_modifier1989); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:428:9: 'strictfp'
                    {
                    match(input,36,FOLLOW_36_in_modifier1999); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 49, modifier_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "modifier"


    // $ANTLR start "packageOrTypeName"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:431:1: packageOrTypeName : qualifiedName ;
    public final void packageOrTypeName() throws RecognitionException {
        int packageOrTypeName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:432:5: ( qualifiedName )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:432:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_packageOrTypeName2018);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 50, packageOrTypeName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "packageOrTypeName"


    // $ANTLR start "enumConstantName"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:435:1: enumConstantName : Identifier ;
    public final void enumConstantName() throws RecognitionException {
        int enumConstantName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:436:5: ( Identifier )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:436:9: Identifier
            {
            match(input,Identifier,FOLLOW_Identifier_in_enumConstantName2037); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 51, enumConstantName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumConstantName"


    // $ANTLR start "typeName"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:439:1: typeName : qualifiedName ;
    public final void typeName() throws RecognitionException {
        int typeName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:440:5: ( qualifiedName )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:440:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_typeName2056);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 52, typeName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeName"


    // $ANTLR start "type"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:443:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final void type() throws RecognitionException {
        int type_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:444:2: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==Identifier) ) {
                alt69=1;
            }
            else if ( ((LA69_0>=56 && LA69_0<=63)) ) {
                alt69=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:444:4: classOrInterfaceType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_type2070);
                    classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:444:25: ( '[' ']' )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==48) ) {
                            alt67=1;
                        }


                        switch (alt67) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:444:26: '[' ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_type2073); if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_type2075); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop67;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:445:4: primitiveType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type2082);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:445:18: ( '[' ']' )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( (LA68_0==48) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:445:19: '[' ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_type2085); if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_type2087); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop68;
                        }
                    } while (true);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 53, type_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "type"


    // $ANTLR start "classOrInterfaceType"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:448:1: classOrInterfaceType : Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ;
    public final void classOrInterfaceType() throws RecognitionException {
        int classOrInterfaceType_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:449:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:449:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2100); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:449:15: ( typeArguments )?
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
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2102);
                    typeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:449:30: ( '.' Identifier ( typeArguments )? )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==29) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:449:31: '.' Identifier ( typeArguments )?
            	    {
            	    match(input,29,FOLLOW_29_in_classOrInterfaceType2106); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType2108); if (state.failed) return ;
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:449:46: ( typeArguments )?
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
            	            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2110);
            	            typeArguments();

            	            state._fsp--;
            	            if (state.failed) return ;

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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 54, classOrInterfaceType_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classOrInterfaceType"


    // $ANTLR start "primitiveType"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:452:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        int primitiveType_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:453:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:
            {
            if ( (input.LA(1)>=56 && input.LA(1)<=63) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 55, primitiveType_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "primitiveType"


    // $ANTLR start "variableModifier"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:463:1: variableModifier : ( 'final' | annotation );
    public final void variableModifier() throws RecognitionException {
        int variableModifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:464:5: ( 'final' | annotation )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==35) ) {
                alt73=1;
            }
            else if ( (LA73_0==73) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:464:9: 'final'
                    {
                    match(input,35,FOLLOW_35_in_variableModifier2219); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:465:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_variableModifier2229);
                    annotation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 56, variableModifier_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableModifier"


    // $ANTLR start "typeArguments"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:468:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        int typeArguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:469:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:469:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            match(input,40,FOLLOW_40_in_typeArguments2248); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments2250);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:469:26: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==41) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:469:27: ',' typeArgument
            	    {
            	    match(input,41,FOLLOW_41_in_typeArguments2253); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2255);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            match(input,42,FOLLOW_42_in_typeArguments2259); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 57, typeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeArguments"


    // $ANTLR start "typeArgument"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:472:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        int typeArgument_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:473:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==Identifier||(LA76_0>=56 && LA76_0<=63)) ) {
                alt76=1;
            }
            else if ( (LA76_0==64) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:473:9: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument2282);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:474:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    match(input,64,FOLLOW_64_in_typeArgument2292); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:474:13: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==38||LA75_0==65) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:474:14: ( 'extends' | 'super' ) type
                            {
                            if ( input.LA(1)==38||input.LA(1)==65 ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument2303);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 58, typeArgument_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeArgument"


    // $ANTLR start "qualifiedNameList"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:477:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final void qualifiedNameList() throws RecognitionException {
        int qualifiedNameList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:478:5: ( qualifiedName ( ',' qualifiedName )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:478:9: qualifiedName ( ',' qualifiedName )*
            {
            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2328);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:478:23: ( ',' qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==41) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:478:24: ',' qualifiedName
            	    {
            	    match(input,41,FOLLOW_41_in_qualifiedNameList2331); if (state.failed) return ;
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2333);
            	    qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 59, qualifiedNameList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "qualifiedNameList"


    // $ANTLR start "formalParameters"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:481:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final void formalParameters() throws RecognitionException {
        int formalParameters_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:482:5: ( '(' ( formalParameterDecls )? ')' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:482:9: '(' ( formalParameterDecls )? ')'
            {
            match(input,66,FOLLOW_66_in_formalParameters2354); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:482:13: ( formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==35||(LA78_0>=56 && LA78_0<=63)||LA78_0==73) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters2356);
                    formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,67,FOLLOW_67_in_formalParameters2359); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 60, formalParameters_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameters"


    // $ANTLR start "formalParameterDecls"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:485:1: formalParameterDecls : variableModifiers type formalParameterDeclsRest ;
    public final void formalParameterDecls() throws RecognitionException {
        int formalParameterDecls_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:486:5: ( variableModifiers type formalParameterDeclsRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:486:9: variableModifiers type formalParameterDeclsRest
            {
            pushFollow(FOLLOW_variableModifiers_in_formalParameterDecls2382);
            variableModifiers();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_type_in_formalParameterDecls2384);
            type();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2386);
            formalParameterDeclsRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 61, formalParameterDecls_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameterDecls"


    // $ANTLR start "formalParameterDeclsRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:489:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final void formalParameterDeclsRest() throws RecognitionException {
        int formalParameterDeclsRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:490:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==Identifier) ) {
                alt80=1;
            }
            else if ( (LA80_0==68) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:490:9: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2409);
                    variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:490:30: ( ',' formalParameterDecls )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==41) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:490:31: ',' formalParameterDecls
                            {
                            match(input,41,FOLLOW_41_in_formalParameterDeclsRest2412); if (state.failed) return ;
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2414);
                            formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:491:9: '...' variableDeclaratorId
                    {
                    match(input,68,FOLLOW_68_in_formalParameterDeclsRest2426); if (state.failed) return ;
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2428);
                    variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 62, formalParameterDeclsRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameterDeclsRest"


    // $ANTLR start "methodBody"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:494:1: methodBody : block ;
    public final void methodBody() throws RecognitionException {
        int methodBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:495:5: ( block )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:495:9: block
            {
            pushFollow(FOLLOW_block_in_methodBody2451);
            block();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 63, methodBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "methodBody"


    // $ANTLR start "constructorBody"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:498:1: constructorBody : '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' ;
    public final void constructorBody() throws RecognitionException {
        int constructorBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:499:5: ( '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:499:9: '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
            {
            match(input,44,FOLLOW_44_in_constructorBody2470); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:499:13: ( explicitConstructorInvocation )?
            int alt81=2;
            alt81 = dfa81.predict(input);
            switch (alt81) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: explicitConstructorInvocation
                    {
                    pushFollow(FOLLOW_explicitConstructorInvocation_in_constructorBody2472);
                    explicitConstructorInvocation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:499:44: ( blockStatement )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( ((LA82_0>=Identifier && LA82_0<=ASSERT)||LA82_0==26||LA82_0==28||(LA82_0>=31 && LA82_0<=37)||LA82_0==44||(LA82_0>=46 && LA82_0<=47)||LA82_0==53||(LA82_0>=56 && LA82_0<=63)||(LA82_0>=65 && LA82_0<=66)||(LA82_0>=69 && LA82_0<=73)||LA82_0==76||(LA82_0>=78 && LA82_0<=81)||(LA82_0>=83 && LA82_0<=87)||(LA82_0>=105 && LA82_0<=106)||(LA82_0>=109 && LA82_0<=113)) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_constructorBody2475);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            match(input,45,FOLLOW_45_in_constructorBody2478); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 64, constructorBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constructorBody"


    // $ANTLR start "explicitConstructorInvocation"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:502:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final void explicitConstructorInvocation() throws RecognitionException {
        int explicitConstructorInvocation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:503:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt85=2;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:503:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:503:9: ( nonWildcardTypeArguments )?
                    int alt83=2;
                    int LA83_0 = input.LA(1);

                    if ( (LA83_0==40) ) {
                        alt83=1;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2497);
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

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation2508);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_explicitConstructorInvocation2510); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:504:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation2520);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,29,FOLLOW_29_in_explicitConstructorInvocation2522); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:504:21: ( nonWildcardTypeArguments )?
                    int alt84=2;
                    int LA84_0 = input.LA(1);

                    if ( (LA84_0==40) ) {
                        alt84=1;
                    }
                    switch (alt84) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2524);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,65,FOLLOW_65_in_explicitConstructorInvocation2527); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation2529);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_explicitConstructorInvocation2531); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 65, explicitConstructorInvocation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "explicitConstructorInvocation"


    // $ANTLR start "qualifiedName"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:508:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final void qualifiedName() throws RecognitionException {
        int qualifiedName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:509:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:509:9: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2551); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:509:20: ( '.' Identifier )*
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:509:21: '.' Identifier
            	    {
            	    match(input,29,FOLLOW_29_in_qualifiedName2554); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2556); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 66, qualifiedName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "qualifiedName"


    // $ANTLR start "literal"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:512:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final void literal() throws RecognitionException {
        int literal_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:513:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }

            switch (alt87) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:513:9: integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal2582);
                    integerLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:514:9: FloatingPointLiteral
                    {
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal2592); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:515:9: CharacterLiteral
                    {
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal2602); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:516:9: StringLiteral
                    {
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal2612); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:517:9: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_literal2622);
                    booleanLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:518:9: 'null'
                    {
                    match(input,70,FOLLOW_70_in_literal2632); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 67, literal_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "literal"


    // $ANTLR start "integerLiteral"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:521:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final void integerLiteral() throws RecognitionException {
        int integerLiteral_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:522:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:
            {
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 68, integerLiteral_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "integerLiteral"


    // $ANTLR start "booleanLiteral"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:527:1: booleanLiteral : ( 'true' | 'false' );
    public final void booleanLiteral() throws RecognitionException {
        int booleanLiteral_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:528:5: ( 'true' | 'false' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:
            {
            if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 69, booleanLiteral_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "booleanLiteral"


    // $ANTLR start "annotations"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:534:1: annotations : ( annotation )+ ;
    public final void annotations() throws RecognitionException {
        int annotations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:535:5: ( ( annotation )+ )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:535:9: ( annotation )+
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:535:9: ( annotation )+
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations2721);
            	    annotation();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt88 >= 1 ) break loop88;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(88, input);
                        throw eee;
                }
                cnt88++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 70, annotations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotations"


    // $ANTLR start "annotation"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:538:1: annotation : '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final void annotation() throws RecognitionException {
        int annotation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:5: ( '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:9: '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            match(input,73,FOLLOW_73_in_annotation2741); if (state.failed) return ;
            pushFollow(FOLLOW_annotationName_in_annotation2743);
            annotationName();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:28: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==66) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:30: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    match(input,66,FOLLOW_66_in_annotation2747); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:34: ( elementValuePairs | elementValue )?
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
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:36: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation2751);
                            elementValuePairs();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:539:56: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation2755);
                            elementValue();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,67,FOLLOW_67_in_annotation2760); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 71, annotation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotation"


    // $ANTLR start "annotationName"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:542:1: annotationName : Identifier ( '.' Identifier )* ;
    public final void annotationName() throws RecognitionException {
        int annotationName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:543:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:543:7: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationName2784); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:543:18: ( '.' Identifier )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==29) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:543:19: '.' Identifier
            	    {
            	    match(input,29,FOLLOW_29_in_annotationName2787); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_annotationName2789); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 72, annotationName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationName"


    // $ANTLR start "elementValuePairs"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:546:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final void elementValuePairs() throws RecognitionException {
        int elementValuePairs_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:547:5: ( elementValuePair ( ',' elementValuePair )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:547:9: elementValuePair ( ',' elementValuePair )*
            {
            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2810);
            elementValuePair();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:547:26: ( ',' elementValuePair )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==41) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:547:27: ',' elementValuePair
            	    {
            	    match(input,41,FOLLOW_41_in_elementValuePairs2813); if (state.failed) return ;
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2815);
            	    elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 73, elementValuePairs_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValuePairs"


    // $ANTLR start "elementValuePair"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:550:1: elementValuePair : Identifier '=' elementValue ;
    public final void elementValuePair() throws RecognitionException {
        int elementValuePair_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:551:5: ( Identifier '=' elementValue )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:551:9: Identifier '=' elementValue
            {
            match(input,Identifier,FOLLOW_Identifier_in_elementValuePair2836); if (state.failed) return ;
            match(input,51,FOLLOW_51_in_elementValuePair2838); if (state.failed) return ;
            pushFollow(FOLLOW_elementValue_in_elementValuePair2840);
            elementValue();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 74, elementValuePair_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValuePair"


    // $ANTLR start "elementValue"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:554:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final void elementValue() throws RecognitionException {
        int elementValue_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:555:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }

            switch (alt93) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:555:9: conditionalExpression
                    {
                    pushFollow(FOLLOW_conditionalExpression_in_elementValue2863);
                    conditionalExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:556:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_elementValue2873);
                    annotation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:557:9: elementValueArrayInitializer
                    {
                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue2883);
                    elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 75, elementValue_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValue"


    // $ANTLR start "elementValueArrayInitializer"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:560:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final void elementValueArrayInitializer() throws RecognitionException {
        int elementValueArrayInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            match(input,44,FOLLOW_44_in_elementValueArrayInitializer2906); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:13: ( elementValue ( ',' elementValue )* )?
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==Identifier||(LA95_0>=FloatingPointLiteral && LA95_0<=DecimalLiteral)||LA95_0==44||LA95_0==47||(LA95_0>=56 && LA95_0<=63)||(LA95_0>=65 && LA95_0<=66)||(LA95_0>=69 && LA95_0<=73)||(LA95_0>=105 && LA95_0<=106)||(LA95_0>=109 && LA95_0<=113)) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:14: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2909);
                    elementValue();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:27: ( ',' elementValue )*
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
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:28: ',' elementValue
                    	    {
                    	    match(input,41,FOLLOW_41_in_elementValueArrayInitializer2912); if (state.failed) return ;
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2914);
                    	    elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop94;
                        }
                    } while (true);


                    }
                    break;

            }

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:49: ( ',' )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==41) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:561:50: ','
                    {
                    match(input,41,FOLLOW_41_in_elementValueArrayInitializer2921); if (state.failed) return ;

                    }
                    break;

            }

            match(input,45,FOLLOW_45_in_elementValueArrayInitializer2925); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 76, elementValueArrayInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValueArrayInitializer"


    // $ANTLR start "annotationTypeDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:564:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
    public final void annotationTypeDeclaration() throws RecognitionException {
        int annotationTypeDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:565:5: ( '@' 'interface' Identifier annotationTypeBody )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:565:9: '@' 'interface' Identifier annotationTypeBody
            {
            match(input,73,FOLLOW_73_in_annotationTypeDeclaration2948); if (state.failed) return ;
            match(input,46,FOLLOW_46_in_annotationTypeDeclaration2950); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration2952); if (state.failed) return ;
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2954);
            annotationTypeBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 77, annotationTypeDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeDeclaration"


    // $ANTLR start "annotationTypeBody"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:568:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final void annotationTypeBody() throws RecognitionException {
        int annotationTypeBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:569:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:569:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            match(input,44,FOLLOW_44_in_annotationTypeBody2977); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:569:13: ( annotationTypeElementDeclaration )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( ((LA97_0>=Identifier && LA97_0<=ENUM)||LA97_0==28||(LA97_0>=31 && LA97_0<=37)||LA97_0==40||(LA97_0>=46 && LA97_0<=47)||(LA97_0>=52 && LA97_0<=63)||LA97_0==73) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:569:14: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody2980);
            	    annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            match(input,45,FOLLOW_45_in_annotationTypeBody2984); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 78, annotationTypeBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeBody"


    // $ANTLR start "annotationTypeElementDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:572:1: annotationTypeElementDeclaration : modifiers annotationTypeElementRest ;
    public final void annotationTypeElementDeclaration() throws RecognitionException {
        int annotationTypeElementDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:573:5: ( modifiers annotationTypeElementRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:573:9: modifiers annotationTypeElementRest
            {
            pushFollow(FOLLOW_modifiers_in_annotationTypeElementDeclaration3007);
            modifiers();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3009);
            annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 79, annotationTypeElementDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeElementDeclaration"


    // $ANTLR start "annotationTypeElementRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:576:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final void annotationTypeElementRest() throws RecognitionException {
        int annotationTypeElementRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:577:5: ( type annotationMethodOrConstantRest ';' | normalClassDeclaration ( ';' )? | normalInterfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;
            }

            switch (alt102) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:577:9: type annotationMethodOrConstantRest ';'
                    {
                    pushFollow(FOLLOW_type_in_annotationTypeElementRest3032);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3034);
                    annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_annotationTypeElementRest3036); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:578:9: normalClassDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3046);
                    normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:578:32: ( ';' )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==26) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: ';'
                            {
                            match(input,26,FOLLOW_26_in_annotationTypeElementRest3048); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:579:9: normalInterfaceDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3059);
                    normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:579:36: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==26) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: ';'
                            {
                            match(input,26,FOLLOW_26_in_annotationTypeElementRest3061); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:580:9: enumDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest3072);
                    enumDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:580:25: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==26) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: ';'
                            {
                            match(input,26,FOLLOW_26_in_annotationTypeElementRest3074); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:581:9: annotationTypeDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3085);
                    annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:581:35: ( ';' )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==26) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: ';'
                            {
                            match(input,26,FOLLOW_26_in_annotationTypeElementRest3087); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 80, annotationTypeElementRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeElementRest"


    // $ANTLR start "annotationMethodOrConstantRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:584:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final void annotationMethodOrConstantRest() throws RecognitionException {
        int annotationMethodOrConstantRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:585:5: ( annotationMethodRest | annotationConstantRest )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 103, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 103, 0, input);

                throw nvae;
            }
            switch (alt103) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:585:9: annotationMethodRest
                    {
                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3111);
                    annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:586:9: annotationConstantRest
                    {
                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3121);
                    annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 81, annotationMethodOrConstantRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationMethodOrConstantRest"


    // $ANTLR start "annotationMethodRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:589:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final void annotationMethodRest() throws RecognitionException {
        int annotationMethodRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:590:5: ( Identifier '(' ')' ( defaultValue )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:590:9: Identifier '(' ')' ( defaultValue )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest3144); if (state.failed) return ;
            match(input,66,FOLLOW_66_in_annotationMethodRest3146); if (state.failed) return ;
            match(input,67,FOLLOW_67_in_annotationMethodRest3148); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:590:28: ( defaultValue )?
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==74) ) {
                alt104=1;
            }
            switch (alt104) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest3150);
                    defaultValue();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 82, annotationMethodRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationMethodRest"


    // $ANTLR start "annotationConstantRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:593:1: annotationConstantRest : variableDeclarators ;
    public final void annotationConstantRest() throws RecognitionException {
        int annotationConstantRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:594:5: ( variableDeclarators )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:594:9: variableDeclarators
            {
            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest3174);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 83, annotationConstantRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationConstantRest"


    // $ANTLR start "defaultValue"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:597:1: defaultValue : 'default' elementValue ;
    public final void defaultValue() throws RecognitionException {
        int defaultValue_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:598:5: ( 'default' elementValue )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:598:9: 'default' elementValue
            {
            match(input,74,FOLLOW_74_in_defaultValue3197); if (state.failed) return ;
            pushFollow(FOLLOW_elementValue_in_defaultValue3199);
            elementValue();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 84, defaultValue_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "defaultValue"


    // $ANTLR start "block"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:603:1: block : '{' ( blockStatement )* '}' ;
    public final void block() throws RecognitionException {
        int block_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:604:5: ( '{' ( blockStatement )* '}' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:604:9: '{' ( blockStatement )* '}'
            {
            match(input,44,FOLLOW_44_in_block3220); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:604:13: ( blockStatement )*
            loop105:
            do {
                int alt105=2;
                int LA105_0 = input.LA(1);

                if ( ((LA105_0>=Identifier && LA105_0<=ASSERT)||LA105_0==26||LA105_0==28||(LA105_0>=31 && LA105_0<=37)||LA105_0==44||(LA105_0>=46 && LA105_0<=47)||LA105_0==53||(LA105_0>=56 && LA105_0<=63)||(LA105_0>=65 && LA105_0<=66)||(LA105_0>=69 && LA105_0<=73)||LA105_0==76||(LA105_0>=78 && LA105_0<=81)||(LA105_0>=83 && LA105_0<=87)||(LA105_0>=105 && LA105_0<=106)||(LA105_0>=109 && LA105_0<=113)) ) {
                    alt105=1;
                }


                switch (alt105) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block3222);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);

            match(input,45,FOLLOW_45_in_block3225); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 85, block_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "block"


    // $ANTLR start "blockStatement"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:607:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final void blockStatement() throws RecognitionException {
        int blockStatement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:608:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt106=3;
            alt106 = dfa106.predict(input);
            switch (alt106) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:608:9: localVariableDeclarationStatement
                    {
                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement3248);
                    localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:609:9: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement3258);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:610:9: statement
                    {
                    pushFollow(FOLLOW_statement_in_blockStatement3268);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 86, blockStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "blockStatement"


    // $ANTLR start "localVariableDeclarationStatement"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:613:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final void localVariableDeclarationStatement() throws RecognitionException {
        int localVariableDeclarationStatement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:614:5: ( localVariableDeclaration ';' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:614:10: localVariableDeclaration ';'
            {
            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement3292);
            localVariableDeclaration();

            state._fsp--;
            if (state.failed) return ;
            match(input,26,FOLLOW_26_in_localVariableDeclarationStatement3294); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 87, localVariableDeclarationStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "localVariableDeclarationStatement"


    // $ANTLR start "localVariableDeclaration"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:617:1: localVariableDeclaration : variableModifiers type variableDeclarators ;
    public final void localVariableDeclaration() throws RecognitionException {
        int localVariableDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:618:5: ( variableModifiers type variableDeclarators )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:618:9: variableModifiers type variableDeclarators
            {
            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration3313);
            variableModifiers();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_type_in_localVariableDeclaration3315);
            type();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration3317);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 88, localVariableDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "localVariableDeclaration"


    // $ANTLR start "variableModifiers"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:621:1: variableModifiers : ( variableModifier )* ;
    public final void variableModifiers() throws RecognitionException {
        int variableModifiers_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:622:5: ( ( variableModifier )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:622:9: ( variableModifier )*
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:622:9: ( variableModifier )*
            loop107:
            do {
                int alt107=2;
                int LA107_0 = input.LA(1);

                if ( (LA107_0==35||LA107_0==73) ) {
                    alt107=1;
                }


                switch (alt107) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_variableModifiers3340);
            	    variableModifier();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop107;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 89, variableModifiers_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableModifiers"


    // $ANTLR start "statement"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:625:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );
    public final void statement() throws RecognitionException {
        int statement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:626:5: ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement )
            int alt114=16;
            alt114 = dfa114.predict(input);
            switch (alt114) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:626:7: block
                    {
                    pushFollow(FOLLOW_block_in_statement3358);
                    block();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:627:9: ASSERT expression ( ':' expression )? ';'
                    {
                    match(input,ASSERT,FOLLOW_ASSERT_in_statement3368); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_statement3370);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:627:27: ( ':' expression )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==75) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:627:28: ':' expression
                            {
                            match(input,75,FOLLOW_75_in_statement3373); if (state.failed) return ;
                            pushFollow(FOLLOW_expression_in_statement3375);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,26,FOLLOW_26_in_statement3379); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:628:9: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    match(input,76,FOLLOW_76_in_statement3389); if (state.failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement3391);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_statement_in_statement3393);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:628:38: ( options {k=1; } : 'else' statement )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==77) ) {
                        int LA109_1 = input.LA(2);

                        if ( (synpred157_Java()) ) {
                            alt109=1;
                        }
                    }
                    switch (alt109) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:628:54: 'else' statement
                            {
                            match(input,77,FOLLOW_77_in_statement3403); if (state.failed) return ;
                            pushFollow(FOLLOW_statement_in_statement3405);
                            statement();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:629:9: 'for' '(' forControl ')' statement
                    {
                    match(input,78,FOLLOW_78_in_statement3417); if (state.failed) return ;
                    match(input,66,FOLLOW_66_in_statement3419); if (state.failed) return ;
                    pushFollow(FOLLOW_forControl_in_statement3421);
                    forControl();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,67,FOLLOW_67_in_statement3423); if (state.failed) return ;
                    pushFollow(FOLLOW_statement_in_statement3425);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:630:9: 'while' parExpression statement
                    {
                    match(input,79,FOLLOW_79_in_statement3435); if (state.failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement3437);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_statement_in_statement3439);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:631:9: 'do' statement 'while' parExpression ';'
                    {
                    match(input,80,FOLLOW_80_in_statement3449); if (state.failed) return ;
                    pushFollow(FOLLOW_statement_in_statement3451);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,79,FOLLOW_79_in_statement3453); if (state.failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement3455);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_statement3457); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:632:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    match(input,81,FOLLOW_81_in_statement3467); if (state.failed) return ;
                    pushFollow(FOLLOW_block_in_statement3469);
                    block();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:633:9: ( catches 'finally' block | catches | 'finally' block )
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
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 110, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA110_0==82) ) {
                        alt110=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 110, 0, input);

                        throw nvae;
                    }
                    switch (alt110) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:633:11: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement3481);
                            catches();

                            state._fsp--;
                            if (state.failed) return ;
                            match(input,82,FOLLOW_82_in_statement3483); if (state.failed) return ;
                            pushFollow(FOLLOW_block_in_statement3485);
                            block();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:634:11: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement3497);
                            catches();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 3 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:635:13: 'finally' block
                            {
                            match(input,82,FOLLOW_82_in_statement3511); if (state.failed) return ;
                            pushFollow(FOLLOW_block_in_statement3513);
                            block();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:637:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    match(input,83,FOLLOW_83_in_statement3533); if (state.failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement3535);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_statement3537); if (state.failed) return ;
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement3539);
                    switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,45,FOLLOW_45_in_statement3541); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:638:9: 'synchronized' parExpression block
                    {
                    match(input,53,FOLLOW_53_in_statement3551); if (state.failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement3553);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_block_in_statement3555);
                    block();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:639:9: 'return' ( expression )? ';'
                    {
                    match(input,84,FOLLOW_84_in_statement3565); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:639:18: ( expression )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==Identifier||(LA111_0>=FloatingPointLiteral && LA111_0<=DecimalLiteral)||LA111_0==47||(LA111_0>=56 && LA111_0<=63)||(LA111_0>=65 && LA111_0<=66)||(LA111_0>=69 && LA111_0<=72)||(LA111_0>=105 && LA111_0<=106)||(LA111_0>=109 && LA111_0<=113)) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement3567);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,26,FOLLOW_26_in_statement3570); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:640:9: 'throw' expression ';'
                    {
                    match(input,85,FOLLOW_85_in_statement3580); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_statement3582);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_statement3584); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:641:9: 'break' ( Identifier )? ';'
                    {
                    match(input,86,FOLLOW_86_in_statement3594); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:641:17: ( Identifier )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement3596); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,26,FOLLOW_26_in_statement3599); if (state.failed) return ;

                    }
                    break;
                case 13 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:642:9: 'continue' ( Identifier )? ';'
                    {
                    match(input,87,FOLLOW_87_in_statement3609); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:642:20: ( Identifier )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==Identifier) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement3611); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,26,FOLLOW_26_in_statement3614); if (state.failed) return ;

                    }
                    break;
                case 14 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:643:9: ';'
                    {
                    match(input,26,FOLLOW_26_in_statement3624); if (state.failed) return ;

                    }
                    break;
                case 15 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:644:9: statementExpression ';'
                    {
                    pushFollow(FOLLOW_statementExpression_in_statement3635);
                    statementExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,26,FOLLOW_26_in_statement3637); if (state.failed) return ;

                    }
                    break;
                case 16 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:645:9: Identifier ':' statement
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_statement3647); if (state.failed) return ;
                    match(input,75,FOLLOW_75_in_statement3649); if (state.failed) return ;
                    pushFollow(FOLLOW_statement_in_statement3651);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 90, statement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "statement"


    // $ANTLR start "catches"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:648:1: catches : catchClause ( catchClause )* ;
    public final void catches() throws RecognitionException {
        int catches_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:649:5: ( catchClause ( catchClause )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:649:9: catchClause ( catchClause )*
            {
            pushFollow(FOLLOW_catchClause_in_catches3674);
            catchClause();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:649:21: ( catchClause )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==88) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:649:22: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches3677);
            	    catchClause();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 91, catches_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "catches"


    // $ANTLR start "catchClause"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:652:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final void catchClause() throws RecognitionException {
        int catchClause_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:653:5: ( 'catch' '(' formalParameter ')' block )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:653:9: 'catch' '(' formalParameter ')' block
            {
            match(input,88,FOLLOW_88_in_catchClause3702); if (state.failed) return ;
            match(input,66,FOLLOW_66_in_catchClause3704); if (state.failed) return ;
            pushFollow(FOLLOW_formalParameter_in_catchClause3706);
            formalParameter();

            state._fsp--;
            if (state.failed) return ;
            match(input,67,FOLLOW_67_in_catchClause3708); if (state.failed) return ;
            pushFollow(FOLLOW_block_in_catchClause3710);
            block();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 92, catchClause_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "catchClause"


    // $ANTLR start "formalParameter"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:656:1: formalParameter : variableModifiers type variableDeclaratorId ;
    public final void formalParameter() throws RecognitionException {
        int formalParameter_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:657:5: ( variableModifiers type variableDeclaratorId )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:657:9: variableModifiers type variableDeclaratorId
            {
            pushFollow(FOLLOW_variableModifiers_in_formalParameter3729);
            variableModifiers();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_type_in_formalParameter3731);
            type();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter3733);
            variableDeclaratorId();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 93, formalParameter_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameter"


    // $ANTLR start "switchBlockStatementGroups"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:660:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final void switchBlockStatementGroups() throws RecognitionException {
        int switchBlockStatementGroups_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:661:5: ( ( switchBlockStatementGroup )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:661:9: ( switchBlockStatementGroup )*
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:661:9: ( switchBlockStatementGroup )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==74||LA116_0==89) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:661:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups3761);
            	    switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop116;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 94, switchBlockStatementGroups_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "switchBlockStatementGroups"


    // $ANTLR start "switchBlockStatementGroup"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:668:1: switchBlockStatementGroup : ( switchLabel )+ ( blockStatement )* ;
    public final void switchBlockStatementGroup() throws RecognitionException {
        int switchBlockStatementGroup_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:669:5: ( ( switchLabel )+ ( blockStatement )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:669:9: ( switchLabel )+ ( blockStatement )*
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:669:9: ( switchLabel )+
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: switchLabel
            	    {
            	    pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup3788);
            	    switchLabel();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt117 >= 1 ) break loop117;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(117, input);
                        throw eee;
                }
                cnt117++;
            } while (true);

            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:669:22: ( blockStatement )*
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( ((LA118_0>=Identifier && LA118_0<=ASSERT)||LA118_0==26||LA118_0==28||(LA118_0>=31 && LA118_0<=37)||LA118_0==44||(LA118_0>=46 && LA118_0<=47)||LA118_0==53||(LA118_0>=56 && LA118_0<=63)||(LA118_0>=65 && LA118_0<=66)||(LA118_0>=69 && LA118_0<=73)||LA118_0==76||(LA118_0>=78 && LA118_0<=81)||(LA118_0>=83 && LA118_0<=87)||(LA118_0>=105 && LA118_0<=106)||(LA118_0>=109 && LA118_0<=113)) ) {
                    alt118=1;
                }


                switch (alt118) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup3791);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop118;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 95, switchBlockStatementGroup_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "switchBlockStatementGroup"


    // $ANTLR start "switchLabel"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:672:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final void switchLabel() throws RecognitionException {
        int switchLabel_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:673:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
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

                        if ( (synpred180_Java()) ) {
                            alt119=1;
                        }
                        else if ( (synpred181_Java()) ) {
                            alt119=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 119, 5, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 119, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 119, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA119_0==74) ) {
                alt119=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 119, 0, input);

                throw nvae;
            }
            switch (alt119) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:673:9: 'case' constantExpression ':'
                    {
                    match(input,89,FOLLOW_89_in_switchLabel3815); if (state.failed) return ;
                    pushFollow(FOLLOW_constantExpression_in_switchLabel3817);
                    constantExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,75,FOLLOW_75_in_switchLabel3819); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:674:9: 'case' enumConstantName ':'
                    {
                    match(input,89,FOLLOW_89_in_switchLabel3829); if (state.failed) return ;
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel3831);
                    enumConstantName();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,75,FOLLOW_75_in_switchLabel3833); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:675:9: 'default' ':'
                    {
                    match(input,74,FOLLOW_74_in_switchLabel3843); if (state.failed) return ;
                    match(input,75,FOLLOW_75_in_switchLabel3845); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 96, switchLabel_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "switchLabel"


    // $ANTLR start "forControl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:678:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final void forControl() throws RecognitionException {
        int forControl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:680:5: ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt123=2;
            alt123 = dfa123.predict(input);
            switch (alt123) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:680:9: enhancedForControl
                    {
                    pushFollow(FOLLOW_enhancedForControl_in_forControl3876);
                    enhancedForControl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:681:9: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:681:9: ( forInit )?
                    int alt120=2;
                    int LA120_0 = input.LA(1);

                    if ( (LA120_0==Identifier||(LA120_0>=FloatingPointLiteral && LA120_0<=DecimalLiteral)||LA120_0==35||LA120_0==47||(LA120_0>=56 && LA120_0<=63)||(LA120_0>=65 && LA120_0<=66)||(LA120_0>=69 && LA120_0<=73)||(LA120_0>=105 && LA120_0<=106)||(LA120_0>=109 && LA120_0<=113)) ) {
                        alt120=1;
                    }
                    switch (alt120) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl3886);
                            forInit();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,26,FOLLOW_26_in_forControl3889); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:681:22: ( expression )?
                    int alt121=2;
                    int LA121_0 = input.LA(1);

                    if ( (LA121_0==Identifier||(LA121_0>=FloatingPointLiteral && LA121_0<=DecimalLiteral)||LA121_0==47||(LA121_0>=56 && LA121_0<=63)||(LA121_0>=65 && LA121_0<=66)||(LA121_0>=69 && LA121_0<=72)||(LA121_0>=105 && LA121_0<=106)||(LA121_0>=109 && LA121_0<=113)) ) {
                        alt121=1;
                    }
                    switch (alt121) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl3891);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,26,FOLLOW_26_in_forControl3894); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:681:38: ( forUpdate )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==47||(LA122_0>=56 && LA122_0<=63)||(LA122_0>=65 && LA122_0<=66)||(LA122_0>=69 && LA122_0<=72)||(LA122_0>=105 && LA122_0<=106)||(LA122_0>=109 && LA122_0<=113)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl3896);
                            forUpdate();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 97, forControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "forControl"


    // $ANTLR start "forInit"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:684:1: forInit : ( localVariableDeclaration | expressionList );
    public final void forInit() throws RecognitionException {
        int forInit_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:685:5: ( localVariableDeclaration | expressionList )
            int alt124=2;
            alt124 = dfa124.predict(input);
            switch (alt124) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:685:9: localVariableDeclaration
                    {
                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit3916);
                    localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:686:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_forInit3926);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 98, forInit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "forInit"


    // $ANTLR start "enhancedForControl"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:689:1: enhancedForControl : variableModifiers type Identifier ':' expression ;
    public final void enhancedForControl() throws RecognitionException {
        int enhancedForControl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:690:5: ( variableModifiers type Identifier ':' expression )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:690:9: variableModifiers type Identifier ':' expression
            {
            pushFollow(FOLLOW_variableModifiers_in_enhancedForControl3949);
            variableModifiers();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_type_in_enhancedForControl3951);
            type();

            state._fsp--;
            if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_enhancedForControl3953); if (state.failed) return ;
            match(input,75,FOLLOW_75_in_enhancedForControl3955); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_enhancedForControl3957);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 99, enhancedForControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enhancedForControl"


    // $ANTLR start "forUpdate"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:693:1: forUpdate : expressionList ;
    public final void forUpdate() throws RecognitionException {
        int forUpdate_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:694:5: ( expressionList )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:694:9: expressionList
            {
            pushFollow(FOLLOW_expressionList_in_forUpdate3976);
            expressionList();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 100, forUpdate_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "forUpdate"


    // $ANTLR start "parExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:699:1: parExpression : '(' expression ')' ;
    public final void parExpression() throws RecognitionException {
        int parExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:700:5: ( '(' expression ')' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:700:9: '(' expression ')'
            {
            match(input,66,FOLLOW_66_in_parExpression3997); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_parExpression3999);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,67,FOLLOW_67_in_parExpression4001); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 101, parExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "parExpression"


    // $ANTLR start "expressionList"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:703:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        int expressionList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:704:5: ( expression ( ',' expression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:704:9: expression ( ',' expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList4024);
            expression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:704:20: ( ',' expression )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( (LA125_0==41) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:704:21: ',' expression
            	    {
            	    match(input,41,FOLLOW_41_in_expressionList4027); if (state.failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList4029);
            	    expression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop125;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 102, expressionList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "expressionList"


    // $ANTLR start "statementExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:707:1: statementExpression : expression ;
    public final void statementExpression() throws RecognitionException {
        int statementExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:708:5: ( expression )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:708:9: expression
            {
            pushFollow(FOLLOW_expression_in_statementExpression4050);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 103, statementExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "statementExpression"


    // $ANTLR start "constantExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:711:1: constantExpression : expression ;
    public final void constantExpression() throws RecognitionException {
        int constantExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:712:5: ( expression )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:712:9: expression
            {
            pushFollow(FOLLOW_expression_in_constantExpression4073);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 104, constantExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantExpression"


    // $ANTLR start "expression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:715:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final void expression() throws RecognitionException {
        int expression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:716:5: ( conditionalExpression ( assignmentOperator expression )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:716:9: conditionalExpression ( assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression4096);
            conditionalExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:716:31: ( assignmentOperator expression )?
            int alt126=2;
            alt126 = dfa126.predict(input);
            switch (alt126) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:716:32: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression4099);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_expression4101);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 105, expression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "expression"


    // $ANTLR start "assignmentOperator"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:719:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);
    public final void assignmentOperator() throws RecognitionException {
        int assignmentOperator_StartIndex = input.index();
        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:720:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?)
            int alt127=12;
            alt127 = dfa127.predict(input);
            switch (alt127) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:720:9: '='
                    {
                    match(input,51,FOLLOW_51_in_assignmentOperator4126); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:721:9: '+='
                    {
                    match(input,90,FOLLOW_90_in_assignmentOperator4136); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:722:9: '-='
                    {
                    match(input,91,FOLLOW_91_in_assignmentOperator4146); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:723:9: '*='
                    {
                    match(input,92,FOLLOW_92_in_assignmentOperator4156); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:724:9: '/='
                    {
                    match(input,93,FOLLOW_93_in_assignmentOperator4166); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:725:9: '&='
                    {
                    match(input,94,FOLLOW_94_in_assignmentOperator4176); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:726:9: '|='
                    {
                    match(input,95,FOLLOW_95_in_assignmentOperator4186); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:727:9: '^='
                    {
                    match(input,96,FOLLOW_96_in_assignmentOperator4196); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:728:9: '%='
                    {
                    match(input,97,FOLLOW_97_in_assignmentOperator4206); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:729:9: ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}?
                    {
                    t1=(Token)match(input,40,FOLLOW_40_in_assignmentOperator4227); if (state.failed) return ;
                    t2=(Token)match(input,40,FOLLOW_40_in_assignmentOperator4231); if (state.failed) return ;
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4235); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() &&
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() && 
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() &&\n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && \n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 11 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:734:9: ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}?
                    {
                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4269); if (state.failed) return ;
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4273); if (state.failed) return ;
                    t3=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4277); if (state.failed) return ;
                    t4=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4281); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() &&
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() &&
                              t3.getLine() == t4.getLine() && 
                              t3.getCharPositionInLine() + 1 == t4.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&\n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&\n          $t3.getLine() == $t4.getLine() && \n          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 12 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:741:9: ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?
                    {
                    t1=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4312); if (state.failed) return ;
                    t2=(Token)match(input,42,FOLLOW_42_in_assignmentOperator4316); if (state.failed) return ;
                    t3=(Token)match(input,51,FOLLOW_51_in_assignmentOperator4320); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() && 
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && \n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 106, assignmentOperator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "assignmentOperator"


    // $ANTLR start "conditionalExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:748:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        int conditionalExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:749:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:749:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression4349);
            conditionalOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:749:33: ( '?' expression ':' expression )?
            int alt128=2;
            int LA128_0 = input.LA(1);

            if ( (LA128_0==64) ) {
                alt128=1;
            }
            switch (alt128) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:749:35: '?' expression ':' expression
                    {
                    match(input,64,FOLLOW_64_in_conditionalExpression4353); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression4355);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,75,FOLLOW_75_in_conditionalExpression4357); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression4359);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 107, conditionalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "conditionalExpression"


    // $ANTLR start "conditionalOrExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:752:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        int conditionalOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:753:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:753:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4381);
            conditionalAndExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:753:34: ( '||' conditionalAndExpression )*
            loop129:
            do {
                int alt129=2;
                int LA129_0 = input.LA(1);

                if ( (LA129_0==98) ) {
                    alt129=1;
                }


                switch (alt129) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:753:36: '||' conditionalAndExpression
            	    {
            	    match(input,98,FOLLOW_98_in_conditionalOrExpression4385); if (state.failed) return ;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4387);
            	    conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop129;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 108, conditionalOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:756:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        int conditionalAndExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:757:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:757:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4409);
            inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:757:31: ( '&&' inclusiveOrExpression )*
            loop130:
            do {
                int alt130=2;
                int LA130_0 = input.LA(1);

                if ( (LA130_0==99) ) {
                    alt130=1;
                }


                switch (alt130) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:757:33: '&&' inclusiveOrExpression
            	    {
            	    match(input,99,FOLLOW_99_in_conditionalAndExpression4413); if (state.failed) return ;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4415);
            	    inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop130;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 109, conditionalAndExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:760:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        int inclusiveOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:761:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:761:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4437);
            exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:761:31: ( '|' exclusiveOrExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==100) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:761:33: '|' exclusiveOrExpression
            	    {
            	    match(input,100,FOLLOW_100_in_inclusiveOrExpression4441); if (state.failed) return ;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4443);
            	    exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop131;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 110, inclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "inclusiveOrExpression"


    // $ANTLR start "exclusiveOrExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:764:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        int exclusiveOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:765:5: ( andExpression ( '^' andExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:765:9: andExpression ( '^' andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression4465);
            andExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:765:23: ( '^' andExpression )*
            loop132:
            do {
                int alt132=2;
                int LA132_0 = input.LA(1);

                if ( (LA132_0==101) ) {
                    alt132=1;
                }


                switch (alt132) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:765:25: '^' andExpression
            	    {
            	    match(input,101,FOLLOW_101_in_exclusiveOrExpression4469); if (state.failed) return ;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression4471);
            	    andExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop132;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 111, exclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "exclusiveOrExpression"


    // $ANTLR start "andExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:768:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        int andExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:769:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:769:9: equalityExpression ( '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression4493);
            equalityExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:769:28: ( '&' equalityExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==43) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:769:30: '&' equalityExpression
            	    {
            	    match(input,43,FOLLOW_43_in_andExpression4497); if (state.failed) return ;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression4499);
            	    equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop133;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 112, andExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "andExpression"


    // $ANTLR start "equalityExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:772:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        int equalityExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:773:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:773:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression4521);
            instanceOfExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:773:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( ((LA134_0>=102 && LA134_0<=103)) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:773:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    if ( (input.LA(1)>=102 && input.LA(1)<=103) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression4533);
            	    instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop134;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 113, equalityExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "equalityExpression"


    // $ANTLR start "instanceOfExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:776:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        int instanceOfExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:777:5: ( relationalExpression ( 'instanceof' type )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:777:9: relationalExpression ( 'instanceof' type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression4555);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:777:30: ( 'instanceof' type )?
            int alt135=2;
            int LA135_0 = input.LA(1);

            if ( (LA135_0==104) ) {
                alt135=1;
            }
            switch (alt135) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:777:31: 'instanceof' type
                    {
                    match(input,104,FOLLOW_104_in_instanceOfExpression4558); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_instanceOfExpression4560);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 114, instanceOfExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "instanceOfExpression"


    // $ANTLR start "relationalExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:780:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        int relationalExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:781:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:781:9: shiftExpression ( relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression4581);
            shiftExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:781:25: ( relationalOp shiftExpression )*
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:781:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression4585);
            	    relationalOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression4587);
            	    shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop136;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 115, relationalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "relationalExpression"


    // $ANTLR start "relationalOp"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:784:1: relationalOp : ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' );
    public final void relationalOp() throws RecognitionException {
        int relationalOp_StartIndex = input.index();
        Token t1=null;
        Token t2=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:785:5: ( ( '<' '=' )=>t1= '<' t2= '=' {...}? | ( '>' '=' )=>t1= '>' t2= '=' {...}? | '<' | '>' )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 137, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 137, 0, input);

                throw nvae;
            }
            switch (alt137) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:785:9: ( '<' '=' )=>t1= '<' t2= '=' {...}?
                    {
                    t1=(Token)match(input,40,FOLLOW_40_in_relationalOp4622); if (state.failed) return ;
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp4626); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "relationalOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:788:9: ( '>' '=' )=>t1= '>' t2= '=' {...}?
                    {
                    t1=(Token)match(input,42,FOLLOW_42_in_relationalOp4656); if (state.failed) return ;
                    t2=(Token)match(input,51,FOLLOW_51_in_relationalOp4660); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "relationalOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:791:9: '<'
                    {
                    match(input,40,FOLLOW_40_in_relationalOp4681); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:792:9: '>'
                    {
                    match(input,42,FOLLOW_42_in_relationalOp4692); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 116, relationalOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "relationalOp"


    // $ANTLR start "shiftExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:795:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        int shiftExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:796:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:796:9: additiveExpression ( shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression4712);
            additiveExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:796:28: ( shiftOp additiveExpression )*
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
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:796:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression4716);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression4718);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop138;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 117, shiftExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "shiftExpression"


    // $ANTLR start "shiftOp"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:799:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);
    public final void shiftOp() throws RecognitionException {
        int shiftOp_StartIndex = input.index();
        Token t1=null;
        Token t2=null;
        Token t3=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:800:5: ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?)
            int alt139=3;
            alt139 = dfa139.predict(input);
            switch (alt139) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:800:9: ( '<' '<' )=>t1= '<' t2= '<' {...}?
                    {
                    t1=(Token)match(input,40,FOLLOW_40_in_shiftOp4749); if (state.failed) return ;
                    t2=(Token)match(input,40,FOLLOW_40_in_shiftOp4753); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:803:9: ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}?
                    {
                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp4785); if (state.failed) return ;
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp4789); if (state.failed) return ;
                    t3=(Token)match(input,42,FOLLOW_42_in_shiftOp4793); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() &&
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&\n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:808:9: ( '>' '>' )=>t1= '>' t2= '>' {...}?
                    {
                    t1=(Token)match(input,42,FOLLOW_42_in_shiftOp4823); if (state.failed) return ;
                    t2=(Token)match(input,42,FOLLOW_42_in_shiftOp4827); if (state.failed) return ;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 118, shiftOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "shiftOp"


    // $ANTLR start "additiveExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:814:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        int additiveExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:815:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:815:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression4857);
            multiplicativeExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:815:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop140:
            do {
                int alt140=2;
                int LA140_0 = input.LA(1);

                if ( ((LA140_0>=105 && LA140_0<=106)) ) {
                    alt140=1;
                }


                switch (alt140) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:815:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=105 && input.LA(1)<=106) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression4869);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop140;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 119, additiveExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:818:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        int multiplicativeExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:819:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:819:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression4891);
            unaryExpression();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:819:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop141:
            do {
                int alt141=2;
                int LA141_0 = input.LA(1);

                if ( (LA141_0==30||(LA141_0>=107 && LA141_0<=108)) ) {
                    alt141=1;
                }


                switch (alt141) {
            	case 1 :
            	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:819:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    if ( input.LA(1)==30||(input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression4909);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop141;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 120, multiplicativeExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:822:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        int unaryExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:823:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 142, 0, input);

                throw nvae;
            }

            switch (alt142) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:823:9: '+' unaryExpression
                    {
                    match(input,105,FOLLOW_105_in_unaryExpression4935); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression4937);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:824:9: '-' unaryExpression
                    {
                    match(input,106,FOLLOW_106_in_unaryExpression4947); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression4949);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:825:9: '++' unaryExpression
                    {
                    match(input,109,FOLLOW_109_in_unaryExpression4959); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression4961);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:826:9: '--' unaryExpression
                    {
                    match(input,110,FOLLOW_110_in_unaryExpression4971); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression4973);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:827:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression4983);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 121, unaryExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "unaryExpression"


    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:830:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:831:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt145=4;
            alt145 = dfa145.predict(input);
            switch (alt145) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:831:9: '~' unaryExpression
                    {
                    match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus5002); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5004);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:832:9: '!' unaryExpression
                    {
                    match(input,112,FOLLOW_112_in_unaryExpressionNotPlusMinus5014); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5016);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:833:9: castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5026);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:834:9: primary ( selector )* ( '++' | '--' )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus5036);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:834:17: ( selector )*
                    loop143:
                    do {
                        int alt143=2;
                        int LA143_0 = input.LA(1);

                        if ( (LA143_0==29||LA143_0==48) ) {
                            alt143=1;
                        }


                        switch (alt143) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus5038);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop143;
                        }
                    } while (true);

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:834:27: ( '++' | '--' )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( ((LA144_0>=109 && LA144_0<=110)) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:
                            {
                            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 122, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"


    // $ANTLR start "castExpression"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:837:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        int castExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:838:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 147, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 147, 0, input);

                throw nvae;
            }
            switch (alt147) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:838:8: '(' primitiveType ')' unaryExpression
                    {
                    match(input,66,FOLLOW_66_in_castExpression5064); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression5066);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,67,FOLLOW_67_in_castExpression5068); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression5070);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:839:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    match(input,66,FOLLOW_66_in_castExpression5079); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:839:12: ( type | expression )
                    int alt146=2;
                    alt146 = dfa146.predict(input);
                    switch (alt146) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:839:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression5082);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:839:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression5086);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,67,FOLLOW_67_in_castExpression5089); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5091);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 123, castExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "castExpression"


    // $ANTLR start "primary"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:842:1: primary : ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        int primary_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:843:5: ( parExpression | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 153, 0, input);

                throw nvae;
            }

            switch (alt153) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:843:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary5110);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    match(input,69,FOLLOW_69_in_primary5120); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:16: ( '.' Identifier )*
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
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:17: '.' Identifier
                    	    {
                    	    match(input,29,FOLLOW_29_in_primary5123); if (state.failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary5125); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:34: ( identifierSuffix )?
                    int alt149=2;
                    alt149 = dfa149.predict(input);
                    switch (alt149) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5129);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:845:9: 'super' superSuffix
                    {
                    match(input,65,FOLLOW_65_in_primary5140); if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_primary5142);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:846:9: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary5152);
                    literal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:847:9: 'new' creator
                    {
                    match(input,113,FOLLOW_113_in_primary5162); if (state.failed) return ;
                    pushFollow(FOLLOW_creator_in_primary5164);
                    creator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:9: Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_primary5174); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:20: ( '.' Identifier )*
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
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:21: '.' Identifier
                    	    {
                    	    match(input,29,FOLLOW_29_in_primary5177); if (state.failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary5179); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop150;
                        }
                    } while (true);

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:38: ( identifierSuffix )?
                    int alt151=2;
                    alt151 = dfa151.predict(input);
                    switch (alt151) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5183);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:849:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary5194);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:849:23: ( '[' ']' )*
                    loop152:
                    do {
                        int alt152=2;
                        int LA152_0 = input.LA(1);

                        if ( (LA152_0==48) ) {
                            alt152=1;
                        }


                        switch (alt152) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:849:24: '[' ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_primary5197); if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_primary5199); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop152;
                        }
                    } while (true);

                    match(input,29,FOLLOW_29_in_primary5203); if (state.failed) return ;
                    match(input,37,FOLLOW_37_in_primary5205); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:850:9: 'void' '.' 'class'
                    {
                    match(input,47,FOLLOW_47_in_primary5215); if (state.failed) return ;
                    match(input,29,FOLLOW_29_in_primary5217); if (state.failed) return ;
                    match(input,37,FOLLOW_37_in_primary5219); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 124, primary_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "primary"


    // $ANTLR start "identifierSuffix"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:853:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );
    public final void identifierSuffix() throws RecognitionException {
        int identifierSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:854:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator )
            int alt156=8;
            alt156 = dfa156.predict(input);
            switch (alt156) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:854:9: ( '[' ']' )+ '.' 'class'
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:854:9: ( '[' ']' )+
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
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:854:10: '[' ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_identifierSuffix5239); if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_identifierSuffix5241); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt154 >= 1 ) break loop154;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(154, input);
                                throw eee;
                        }
                        cnt154++;
                    } while (true);

                    match(input,29,FOLLOW_29_in_identifierSuffix5245); if (state.failed) return ;
                    match(input,37,FOLLOW_37_in_identifierSuffix5247); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:855:9: ( '[' expression ']' )+
                    {
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:855:9: ( '[' expression ']' )+
                    int cnt155=0;
                    loop155:
                    do {
                        int alt155=2;
                        alt155 = dfa155.predict(input);
                        switch (alt155) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:855:10: '[' expression ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_identifierSuffix5258); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix5260);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_identifierSuffix5262); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt155 >= 1 ) break loop155;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(155, input);
                                throw eee;
                        }
                        cnt155++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:856:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix5275);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:857:9: '.' 'class'
                    {
                    match(input,29,FOLLOW_29_in_identifierSuffix5285); if (state.failed) return ;
                    match(input,37,FOLLOW_37_in_identifierSuffix5287); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:858:9: '.' explicitGenericInvocation
                    {
                    match(input,29,FOLLOW_29_in_identifierSuffix5297); if (state.failed) return ;
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix5299);
                    explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:859:9: '.' 'this'
                    {
                    match(input,29,FOLLOW_29_in_identifierSuffix5309); if (state.failed) return ;
                    match(input,69,FOLLOW_69_in_identifierSuffix5311); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:860:9: '.' 'super' arguments
                    {
                    match(input,29,FOLLOW_29_in_identifierSuffix5321); if (state.failed) return ;
                    match(input,65,FOLLOW_65_in_identifierSuffix5323); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_identifierSuffix5325);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:861:9: '.' 'new' innerCreator
                    {
                    match(input,29,FOLLOW_29_in_identifierSuffix5335); if (state.failed) return ;
                    match(input,113,FOLLOW_113_in_identifierSuffix5337); if (state.failed) return ;
                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix5339);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 125, identifierSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "identifierSuffix"


    // $ANTLR start "creator"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:864:1: creator : ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) );
    public final void creator() throws RecognitionException {
        int creator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:865:5: ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) )
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==40) ) {
                alt158=1;
            }
            else if ( (LA158_0==Identifier||(LA158_0>=56 && LA158_0<=63)) ) {
                alt158=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 158, 0, input);

                throw nvae;
            }
            switch (alt158) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:865:9: nonWildcardTypeArguments createdName classCreatorRest
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator5358);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_createdName_in_creator5360);
                    createdName();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_classCreatorRest_in_creator5362);
                    classCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:866:9: createdName ( arrayCreatorRest | classCreatorRest )
                    {
                    pushFollow(FOLLOW_createdName_in_creator5372);
                    createdName();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:866:21: ( arrayCreatorRest | classCreatorRest )
                    int alt157=2;
                    int LA157_0 = input.LA(1);

                    if ( (LA157_0==48) ) {
                        alt157=1;
                    }
                    else if ( (LA157_0==66) ) {
                        alt157=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 157, 0, input);

                        throw nvae;
                    }
                    switch (alt157) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:866:22: arrayCreatorRest
                            {
                            pushFollow(FOLLOW_arrayCreatorRest_in_creator5375);
                            arrayCreatorRest();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:866:41: classCreatorRest
                            {
                            pushFollow(FOLLOW_classCreatorRest_in_creator5379);
                            classCreatorRest();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 126, creator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "creator"


    // $ANTLR start "createdName"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:869:1: createdName : ( classOrInterfaceType | primitiveType );
    public final void createdName() throws RecognitionException {
        int createdName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:870:5: ( classOrInterfaceType | primitiveType )
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( (LA159_0==Identifier) ) {
                alt159=1;
            }
            else if ( ((LA159_0>=56 && LA159_0<=63)) ) {
                alt159=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 159, 0, input);

                throw nvae;
            }
            switch (alt159) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:870:9: classOrInterfaceType
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName5399);
                    classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:871:9: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName5409);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 127, createdName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "createdName"


    // $ANTLR start "innerCreator"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:874:1: innerCreator : ( nonWildcardTypeArguments )? Identifier classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        int innerCreator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:875:5: ( ( nonWildcardTypeArguments )? Identifier classCreatorRest )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:875:9: ( nonWildcardTypeArguments )? Identifier classCreatorRest
            {
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:875:9: ( nonWildcardTypeArguments )?
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==40) ) {
                alt160=1;
            }
            switch (alt160) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator5432);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_innerCreator5435); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator5437);
            classCreatorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 128, innerCreator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "innerCreator"


    // $ANTLR start "arrayCreatorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:878:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        int arrayCreatorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:879:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:879:9: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            match(input,48,FOLLOW_48_in_arrayCreatorRest5456); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:880:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt164=2;
            int LA164_0 = input.LA(1);

            if ( (LA164_0==49) ) {
                alt164=1;
            }
            else if ( (LA164_0==Identifier||(LA164_0>=FloatingPointLiteral && LA164_0<=DecimalLiteral)||LA164_0==47||(LA164_0>=56 && LA164_0<=63)||(LA164_0>=65 && LA164_0<=66)||(LA164_0>=69 && LA164_0<=72)||(LA164_0>=105 && LA164_0<=106)||(LA164_0>=109 && LA164_0<=113)) ) {
                alt164=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 164, 0, input);

                throw nvae;
            }
            switch (alt164) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:880:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    match(input,49,FOLLOW_49_in_arrayCreatorRest5470); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:880:17: ( '[' ']' )*
                    loop161:
                    do {
                        int alt161=2;
                        int LA161_0 = input.LA(1);

                        if ( (LA161_0==48) ) {
                            alt161=1;
                        }


                        switch (alt161) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:880:18: '[' ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_arrayCreatorRest5473); if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_arrayCreatorRest5475); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop161;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest5479);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest5493);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,49,FOLLOW_49_in_arrayCreatorRest5495); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:28: ( '[' expression ']' )*
                    loop162:
                    do {
                        int alt162=2;
                        alt162 = dfa162.predict(input);
                        switch (alt162) {
                    	case 1 :
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:29: '[' expression ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_arrayCreatorRest5498); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest5500);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_arrayCreatorRest5502); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop162;
                        }
                    } while (true);

                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:50: ( '[' ']' )*
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
                    	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:51: '[' ']'
                    	    {
                    	    match(input,48,FOLLOW_48_in_arrayCreatorRest5507); if (state.failed) return ;
                    	    match(input,49,FOLLOW_49_in_arrayCreatorRest5509); if (state.failed) return ;

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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 129, arrayCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arrayCreatorRest"


    // $ANTLR start "classCreatorRest"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:885:1: classCreatorRest : arguments ( classBody )? ;
    public final void classCreatorRest() throws RecognitionException {
        int classCreatorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:886:5: ( arguments ( classBody )? )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:886:9: arguments ( classBody )?
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest5540);
            arguments();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:886:19: ( classBody )?
            int alt165=2;
            int LA165_0 = input.LA(1);

            if ( (LA165_0==44) ) {
                alt165=1;
            }
            switch (alt165) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest5542);
                    classBody();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 130, classCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classCreatorRest"


    // $ANTLR start "explicitGenericInvocation"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:889:1: explicitGenericInvocation : nonWildcardTypeArguments Identifier arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        int explicitGenericInvocation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:890:5: ( nonWildcardTypeArguments Identifier arguments )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:890:9: nonWildcardTypeArguments Identifier arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation5566);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocation5568); if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation5570);
            arguments();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 131, explicitGenericInvocation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "explicitGenericInvocation"


    // $ANTLR start "nonWildcardTypeArguments"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:893:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        int nonWildcardTypeArguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:894:5: ( '<' typeList '>' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:894:9: '<' typeList '>'
            {
            match(input,40,FOLLOW_40_in_nonWildcardTypeArguments5593); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments5595);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,42,FOLLOW_42_in_nonWildcardTypeArguments5597); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 132, nonWildcardTypeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "nonWildcardTypeArguments"


    // $ANTLR start "selector"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:897:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' );
    public final void selector() throws RecognitionException {
        int selector_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:898:5: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' innerCreator | '[' expression ']' )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 167, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA167_0==48) ) {
                alt167=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 167, 0, input);

                throw nvae;
            }
            switch (alt167) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:898:9: '.' Identifier ( arguments )?
                    {
                    match(input,29,FOLLOW_29_in_selector5620); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_selector5622); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:898:24: ( arguments )?
                    int alt166=2;
                    int LA166_0 = input.LA(1);

                    if ( (LA166_0==66) ) {
                        alt166=1;
                    }
                    switch (alt166) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector5624);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:899:9: '.' 'this'
                    {
                    match(input,29,FOLLOW_29_in_selector5635); if (state.failed) return ;
                    match(input,69,FOLLOW_69_in_selector5637); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:900:9: '.' 'super' superSuffix
                    {
                    match(input,29,FOLLOW_29_in_selector5647); if (state.failed) return ;
                    match(input,65,FOLLOW_65_in_selector5649); if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector5651);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:901:9: '.' 'new' innerCreator
                    {
                    match(input,29,FOLLOW_29_in_selector5661); if (state.failed) return ;
                    match(input,113,FOLLOW_113_in_selector5663); if (state.failed) return ;
                    pushFollow(FOLLOW_innerCreator_in_selector5665);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:902:9: '[' expression ']'
                    {
                    match(input,48,FOLLOW_48_in_selector5675); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_selector5677);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,49,FOLLOW_49_in_selector5679); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 133, selector_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "selector"


    // $ANTLR start "superSuffix"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:905:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        int superSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:906:5: ( arguments | '.' Identifier ( arguments )? )
            int alt169=2;
            int LA169_0 = input.LA(1);

            if ( (LA169_0==66) ) {
                alt169=1;
            }
            else if ( (LA169_0==29) ) {
                alt169=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 169, 0, input);

                throw nvae;
            }
            switch (alt169) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:906:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix5702);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:907:9: '.' Identifier ( arguments )?
                    {
                    match(input,29,FOLLOW_29_in_superSuffix5712); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_superSuffix5714); if (state.failed) return ;
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:907:24: ( arguments )?
                    int alt168=2;
                    int LA168_0 = input.LA(1);

                    if ( (LA168_0==66) ) {
                        alt168=1;
                    }
                    switch (alt168) {
                        case 1 :
                            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix5716);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 134, superSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "superSuffix"


    // $ANTLR start "arguments"
    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:910:1: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        int arguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return ; }
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:911:5: ( '(' ( expressionList )? ')' )
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:911:9: '(' ( expressionList )? ')'
            {
            match(input,66,FOLLOW_66_in_arguments5736); if (state.failed) return ;
            // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:911:13: ( expressionList )?
            int alt170=2;
            int LA170_0 = input.LA(1);

            if ( (LA170_0==Identifier||(LA170_0>=FloatingPointLiteral && LA170_0<=DecimalLiteral)||LA170_0==47||(LA170_0>=56 && LA170_0<=63)||(LA170_0>=65 && LA170_0<=66)||(LA170_0>=69 && LA170_0<=72)||(LA170_0>=105 && LA170_0<=106)||(LA170_0>=109 && LA170_0<=113)) ) {
                alt170=1;
            }
            switch (alt170) {
                case 1 :
                    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments5738);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,67,FOLLOW_67_in_arguments5741); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 135, arguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arguments"

    // $ANTLR start synpred5_Java
    public final void synpred5_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:185:9: ( annotations ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:185:9: annotations ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
        {
        pushFollow(FOLLOW_annotations_in_synpred5_Java61);
        annotations();

        state._fsp--;
        if (state.failed) return ;
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:9: ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
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
                // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:13: packageDeclaration ( importDeclaration )* ( typeDeclaration )*
                {
                pushFollow(FOLLOW_packageDeclaration_in_synpred5_Java75);
                packageDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:32: ( importDeclaration )*
                loop173:
                do {
                    int alt173=2;
                    int LA173_0 = input.LA(1);

                    if ( (LA173_0==27) ) {
                        alt173=1;
                    }


                    switch (alt173) {
                	case 1 :
                	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: importDeclaration
                	    {
                	    pushFollow(FOLLOW_importDeclaration_in_synpred5_Java77);
                	    importDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop173;
                    }
                } while (true);

                // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:186:51: ( typeDeclaration )*
                loop174:
                do {
                    int alt174=2;
                    int LA174_0 = input.LA(1);

                    if ( (LA174_0==ENUM||LA174_0==26||LA174_0==28||(LA174_0>=31 && LA174_0<=37)||LA174_0==46||LA174_0==73) ) {
                        alt174=1;
                    }


                    switch (alt174) {
                	case 1 :
                	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java80);
                	    typeDeclaration();

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
                // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:187:13: classOrInterfaceDeclaration ( typeDeclaration )*
                {
                pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java95);
                classOrInterfaceDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:187:41: ( typeDeclaration )*
                loop175:
                do {
                    int alt175=2;
                    int LA175_0 = input.LA(1);

                    if ( (LA175_0==ENUM||LA175_0==26||LA175_0==28||(LA175_0>=31 && LA175_0<=37)||LA175_0==46||LA175_0==73) ) {
                        alt175=1;
                    }


                    switch (alt175) {
                	case 1 :
                	    // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_Java97);
                	    typeDeclaration();

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
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:499:13: ( explicitConstructorInvocation )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:499:13: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred113_Java2472);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred113_Java

    // $ANTLR start synpred117_Java
    public final void synpred117_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:503:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:503:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:503:9: ( nonWildcardTypeArguments )?
        int alt184=2;
        int LA184_0 = input.LA(1);

        if ( (LA184_0==40) ) {
            alt184=1;
        }
        switch (alt184) {
            case 1 :
                // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:0:0: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred117_Java2497);
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

        pushFollow(FOLLOW_arguments_in_synpred117_Java2508);
        arguments();

        state._fsp--;
        if (state.failed) return ;
        match(input,26,FOLLOW_26_in_synpred117_Java2510); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred117_Java

    // $ANTLR start synpred128_Java
    public final void synpred128_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:535:9: ( annotation )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:535:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred128_Java2721);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred128_Java

    // $ANTLR start synpred151_Java
    public final void synpred151_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:608:9: ( localVariableDeclarationStatement )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:608:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred151_Java3248);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred151_Java

    // $ANTLR start synpred152_Java
    public final void synpred152_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:609:9: ( classOrInterfaceDeclaration )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:609:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred152_Java3258);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred152_Java

    // $ANTLR start synpred157_Java
    public final void synpred157_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:628:54: ( 'else' statement )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:628:54: 'else' statement
        {
        match(input,77,FOLLOW_77_in_synpred157_Java3403); if (state.failed) return ;
        pushFollow(FOLLOW_statement_in_synpred157_Java3405);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred157_Java

    // $ANTLR start synpred162_Java
    public final void synpred162_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:633:11: ( catches 'finally' block )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:633:11: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred162_Java3481);
        catches();

        state._fsp--;
        if (state.failed) return ;
        match(input,82,FOLLOW_82_in_synpred162_Java3483); if (state.failed) return ;
        pushFollow(FOLLOW_block_in_synpred162_Java3485);
        block();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred162_Java

    // $ANTLR start synpred163_Java
    public final void synpred163_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:634:11: ( catches )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:634:11: catches
        {
        pushFollow(FOLLOW_catches_in_synpred163_Java3497);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred163_Java

    // $ANTLR start synpred178_Java
    public final void synpred178_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:669:9: ( switchLabel )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:669:9: switchLabel
        {
        pushFollow(FOLLOW_switchLabel_in_synpred178_Java3788);
        switchLabel();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred178_Java

    // $ANTLR start synpred180_Java
    public final void synpred180_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:673:9: ( 'case' constantExpression ':' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:673:9: 'case' constantExpression ':'
        {
        match(input,89,FOLLOW_89_in_synpred180_Java3815); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred180_Java3817);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred180_Java3819); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred180_Java

    // $ANTLR start synpred181_Java
    public final void synpred181_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:674:9: ( 'case' enumConstantName ':' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:674:9: 'case' enumConstantName ':'
        {
        match(input,89,FOLLOW_89_in_synpred181_Java3829); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred181_Java3831);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,75,FOLLOW_75_in_synpred181_Java3833); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred181_Java

    // $ANTLR start synpred182_Java
    public final void synpred182_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:680:9: ( enhancedForControl )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:680:9: enhancedForControl
        {
        pushFollow(FOLLOW_enhancedForControl_in_synpred182_Java3876);
        enhancedForControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred182_Java

    // $ANTLR start synpred186_Java
    public final void synpred186_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:685:9: ( localVariableDeclaration )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:685:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred186_Java3916);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred186_Java

    // $ANTLR start synpred188_Java
    public final void synpred188_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:716:32: ( assignmentOperator expression )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:716:32: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred188_Java4099);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred188_Java4101);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred188_Java

    // $ANTLR start synpred198_Java
    public final void synpred198_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:729:9: ( '<' '<' '=' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:729:10: '<' '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred198_Java4217); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred198_Java4219); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred198_Java4221); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred198_Java

    // $ANTLR start synpred199_Java
    public final void synpred199_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:734:9: ( '>' '>' '>' '=' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:734:10: '>' '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred199_Java4257); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred199_Java4259); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred199_Java4261); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred199_Java4263); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred199_Java

    // $ANTLR start synpred200_Java
    public final void synpred200_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:741:9: ( '>' '>' '=' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:741:10: '>' '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred200_Java4302); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred200_Java4304); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred200_Java4306); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred200_Java

    // $ANTLR start synpred211_Java
    public final void synpred211_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:785:9: ( '<' '=' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:785:10: '<' '='
        {
        match(input,40,FOLLOW_40_in_synpred211_Java4614); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred211_Java4616); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred211_Java

    // $ANTLR start synpred212_Java
    public final void synpred212_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:788:9: ( '>' '=' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:788:10: '>' '='
        {
        match(input,42,FOLLOW_42_in_synpred212_Java4648); if (state.failed) return ;
        match(input,51,FOLLOW_51_in_synpred212_Java4650); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred212_Java

    // $ANTLR start synpred215_Java
    public final void synpred215_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:800:9: ( '<' '<' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:800:10: '<' '<'
        {
        match(input,40,FOLLOW_40_in_synpred215_Java4741); if (state.failed) return ;
        match(input,40,FOLLOW_40_in_synpred215_Java4743); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred215_Java

    // $ANTLR start synpred216_Java
    public final void synpred216_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:803:9: ( '>' '>' '>' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:803:10: '>' '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred216_Java4775); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred216_Java4777); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred216_Java4779); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred216_Java

    // $ANTLR start synpred217_Java
    public final void synpred217_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:808:9: ( '>' '>' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:808:10: '>' '>'
        {
        match(input,42,FOLLOW_42_in_synpred217_Java4815); if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred217_Java4817); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred217_Java

    // $ANTLR start synpred229_Java
    public final void synpred229_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:833:9: ( castExpression )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:833:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred229_Java5026);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred229_Java

    // $ANTLR start synpred233_Java
    public final void synpred233_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:838:8: ( '(' primitiveType ')' unaryExpression )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:838:8: '(' primitiveType ')' unaryExpression
        {
        match(input,66,FOLLOW_66_in_synpred233_Java5064); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred233_Java5066);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,67,FOLLOW_67_in_synpred233_Java5068); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred233_Java5070);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred233_Java

    // $ANTLR start synpred234_Java
    public final void synpred234_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:839:13: ( type )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:839:13: type
        {
        pushFollow(FOLLOW_type_in_synpred234_Java5082);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred234_Java

    // $ANTLR start synpred236_Java
    public final void synpred236_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:17: ( '.' Identifier )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:17: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred236_Java5123); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred236_Java5125); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred236_Java

    // $ANTLR start synpred237_Java
    public final void synpred237_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:34: ( identifierSuffix )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:844:34: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred237_Java5129);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred237_Java

    // $ANTLR start synpred242_Java
    public final void synpred242_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:21: ( '.' Identifier )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:21: '.' Identifier
        {
        match(input,29,FOLLOW_29_in_synpred242_Java5177); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred242_Java5179); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred242_Java

    // $ANTLR start synpred243_Java
    public final void synpred243_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:38: ( identifierSuffix )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:848:38: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred243_Java5183);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred243_Java

    // $ANTLR start synpred249_Java
    public final void synpred249_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:855:10: ( '[' expression ']' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:855:10: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred249_Java5258); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred249_Java5260);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred249_Java5262); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred249_Java

    // $ANTLR start synpred262_Java
    public final void synpred262_Java_fragment() throws RecognitionException {   
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:29: ( '[' expression ']' )
        // /Users/marko/Documents/workspace/Jnome Generics/src/org/jnome/input/parser/Java.g:881:29: '[' expression ']'
        {
        match(input,48,FOLLOW_48_in_synpred262_Java5498); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred262_Java5500);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,49,FOLLOW_49_in_synpred262_Java5502); if (state.failed) return ;

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
            return "184:1: compilationUnit : ( annotations ( packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) | ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* );";
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
            return "499:13: ( explicitConstructorInvocation )?";
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
            return "502:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );";
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
            return "607:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );";
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
            return "625:1: statement : ( block | ASSERT expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );";
        }
    }
    static final String DFA123_eotS =
        "\u0087\uffff";
    static final String DFA123_eofS =
        "\u0087\uffff";
    static final String DFA123_minS =
        "\5\4\22\uffff\7\4\4\uffff\1\4\24\uffff\1\32\1\61\1\uffff\1\32\22"+
        "\0\5\uffff\1\0\45\uffff\2\0\1\uffff\1\0\5\uffff\1\0\5\uffff";
    static final String DFA123_maxS =
        "\1\161\1\111\1\4\1\156\1\60\22\uffff\2\60\1\111\1\4\1\111\2\161"+
        "\4\uffff\1\161\24\uffff\1\113\1\61\1\uffff\1\113\22\0\5\uffff\1"+
        "\0\45\uffff\2\0\1\uffff\1\0\5\uffff\1\0\5\uffff";
    static final String DFA123_acceptS =
        "\5\uffff\1\2\166\uffff\1\1\12\uffff";
    static final String DFA123_specialS =
        "\73\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\5\uffff\1\22\45\uffff\1\23\1\24\1\uffff"+
        "\1\25\5\uffff\1\26\5\uffff}>";
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
            "\1\170\1\uffff\6\5\34\uffff\1\5\6\uffff\1\5\3\uffff\1\5\4\uffff"+
            "\10\171\1\173\2\5\2\uffff\4\5\40\uffff\2\5\2\uffff\5\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "678:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
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
                        int LA123_76 = input.LA(1);

                         
                        int index123_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_76);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA123_82 = input.LA(1);

                         
                        int index123_82 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_82);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA123_120 = input.LA(1);

                         
                        int index123_120 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_120);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA123_121 = input.LA(1);

                         
                        int index123_121 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_121);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA123_123 = input.LA(1);

                         
                        int index123_123 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred182_Java()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index123_123);
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
            return "684:1: forInit : ( localVariableDeclaration | expressionList );";
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
        "\1\uffff\1\0\1\10\1\4\1\2\1\6\1\7\1\3\1\1\1\12\1\11\1\5\2\uffff}>";
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
            return "716:31: ( assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA126_1 = input.LA(1);

                         
                        int index126_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA126_8 = input.LA(1);

                         
                        int index126_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_8);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA126_4 = input.LA(1);

                         
                        int index126_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA126_7 = input.LA(1);

                         
                        int index126_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_7);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA126_3 = input.LA(1);

                         
                        int index126_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_3);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA126_11 = input.LA(1);

                         
                        int index126_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_11);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA126_5 = input.LA(1);

                         
                        int index126_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_5);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA126_6 = input.LA(1);

                         
                        int index126_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA126_2 = input.LA(1);

                         
                        int index126_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_2);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA126_10 = input.LA(1);

                         
                        int index126_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index126_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA126_9 = input.LA(1);

                         
                        int index126_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 13;}

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
            return "719:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | ( '<' '<' '=' )=>t1= '<' t2= '<' t3= '=' {...}? | ( '>' '>' '>' '=' )=>t1= '>' t2= '>' t3= '>' t4= '=' {...}? | ( '>' '>' '=' )=>t1= '>' t2= '>' t3= '=' {...}?);";
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
            return "799:1: shiftOp : ( ( '<' '<' )=>t1= '<' t2= '<' {...}? | ( '>' '>' '>' )=>t1= '>' t2= '>' t3= '>' {...}? | ( '>' '>' )=>t1= '>' t2= '>' {...}?);";
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
                        if ( (LA139_0==40) && (synpred215_Java())) {s = 1;}

                        else if ( (LA139_0==42) ) {s = 2;}

                         
                        input.seek(index139_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
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
            return "830:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );";
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
            return "839:12: ( type | expression )";
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
            return "844:34: ( identifierSuffix )?";
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
            return "848:38: ( identifierSuffix )?";
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
            return "853:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' innerCreator );";
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
            return "()+ loopback of 855:9: ( '[' expression ']' )+";
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
            return "()* loopback of 881:28: ( '[' expression ']' )*";
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
 

    public static final BitSet FOLLOW_annotations_in_compilationUnit61 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit75 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit77 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit80 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_compilationUnit95 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit97 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit118 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit121 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit124 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_25_in_packageDeclaration144 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration146 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_packageDeclaration148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_importDeclaration171 = new BitSet(new long[]{0x0000000010000010L});
    public static final BitSet FOLLOW_28_in_importDeclaration173 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_importDeclaration176 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration179 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_importDeclaration181 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_importDeclaration185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_typeDeclaration218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration241 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers272 = new BitSet(new long[]{0x0000001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotation_in_classOrInterfaceModifier292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_classOrInterfaceModifier305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_classOrInterfaceModifier320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_classOrInterfaceModifier332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_classOrInterfaceModifier346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classOrInterfaceModifier359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_classOrInterfaceModifier374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_classOrInterfaceModifier390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_modifiers412 = new BitSet(new long[]{0x00F0001F90000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_normalClassDeclaration465 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration467 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration469 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_38_in_normalClassDeclaration481 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration483 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_39_in_normalClassDeclaration496 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration498 = new BitSet(new long[]{0x000011C000000000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeParameters533 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters535 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeParameters538 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters540 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeParameters544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter563 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_38_in_typeParameter566 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound597 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_typeBound600 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeBound602 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration623 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration625 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_39_in_enumDeclaration628 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration630 = new BitSet(new long[]{0x0000108000000000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_enumBody653 = new BitSet(new long[]{0x0000220004000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody655 = new BitSet(new long[]{0x0000220004000000L});
    public static final BitSet FOLLOW_41_in_enumBody658 = new BitSet(new long[]{0x0000200004000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody661 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_enumBody664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants683 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_enumConstants686 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants688 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant713 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant716 = new BitSet(new long[]{0x000011C000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_enumConstant718 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_enumBodyDeclarations745 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations748 = new BitSet(new long[]{0x00F0101F94000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_normalInterfaceDeclaration806 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration808 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration810 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_38_in_normalInterfaceDeclaration814 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration816 = new BitSet(new long[]{0x0000114000000000L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList843 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_typeList846 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeList848 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_44_in_classBody873 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody875 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_classBody878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_interfaceBody901 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody903 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_interfaceBody906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_classBodyDeclaration925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_classBodyDeclaration935 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classBodyDeclaration948 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDeclaration_in_memberDecl983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_memberDecl993 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl995 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl1007 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl1009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_memberDeclaration1052 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDeclaration1055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDeclaration1059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl1079 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest1105 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_genericMethodOrConstructorRest1109 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1112 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest1114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest1124 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration1145 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration1147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration1166 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_fieldDeclaration1168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceBodyDeclaration1195 = new BitSet(new long[]{0xFF00C13F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration1197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_interfaceBodyDeclaration1207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl1236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_interfaceMemberDecl1246 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl1248 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl1250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl1260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl1293 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl1295 = new BitSet(new long[]{0x0009000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest1320 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodOrFieldRest1322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest1355 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_48_in_methodDeclaratorRest1358 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_methodDeclaratorRest1360 = new BitSet(new long[]{0x0005100014000000L});
    public static final BitSet FOLLOW_50_in_methodDeclaratorRest1373 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest1375 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_methodDeclaratorRest1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest1438 = new BitSet(new long[]{0x0004100014000000L});
    public static final BitSet FOLLOW_50_in_voidMethodDeclaratorRest1441 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1443 = new BitSet(new long[]{0x0000100014000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest1459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_voidMethodDeclaratorRest1473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1506 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_48_in_interfaceMethodDeclaratorRest1509 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_interfaceMethodDeclaratorRest1511 = new BitSet(new long[]{0x0005000004000000L});
    public static final BitSet FOLLOW_50_in_interfaceMethodDeclaratorRest1516 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1518 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_interfaceMethodDeclaratorRest1522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl1545 = new BitSet(new long[]{0xFF00800000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl1548 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_47_in_interfaceGenericMethodDecl1552 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl1555 = new BitSet(new long[]{0x0009000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1588 = new BitSet(new long[]{0x0004000004000000L});
    public static final BitSet FOLLOW_50_in_voidInterfaceMethodDeclaratorRest1591 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1593 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_voidInterfaceMethodDeclaratorRest1597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest1620 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_50_in_constructorDeclaratorRest1623 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1625 = new BitSet(new long[]{0x0004100000000000L});
    public static final BitSet FOLLOW_constructorBody_in_constructorDeclaratorRest1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator1648 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator1650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1673 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclarators1676 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1678 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_variableDeclarator1699 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_variableDeclarator1702 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator1704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1729 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_constantDeclaratorsRest1732 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1734 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_48_in_constantDeclaratorRest1756 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_constantDeclaratorRest1758 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_51_in_constantDeclaratorRest1762 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1787 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_variableDeclaratorId1790 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_variableDeclaratorId1792 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_arrayInitializer1850 = new BitSet(new long[]{0xFF00B00000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1853 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer1856 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1858 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_arrayInitializer1863 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_arrayInitializer1870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier1889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_modifier1899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_modifier1909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_modifier1919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_modifier1929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_modifier1939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_modifier1949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier1959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier1969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier1979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_modifier1989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_modifier1999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_packageOrTypeName2018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName2037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_typeName2056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2070 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2073 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2075 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type2082 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_type2085 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_type2087 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2100 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2102 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_classOrInterfaceType2106 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType2108 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2110 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_variableModifier2219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier2229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_typeArguments2248 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2250 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_41_in_typeArguments2253 = new BitSet(new long[]{0xFF00000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2255 = new BitSet(new long[]{0x0000060000000000L});
    public static final BitSet FOLLOW_42_in_typeArguments2259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument2282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_typeArgument2292 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument2295 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument2303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2328 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_qualifiedNameList2331 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2333 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_66_in_formalParameters2354 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000208L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters2356 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_formalParameters2359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameterDecls2382 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls2384 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000010L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2409 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_formalParameterDeclsRest2412 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_formalParameterDeclsRest2426 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody2451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_constructorBody2470 = new BitSet(new long[]{0xFF20F13F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody2472 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_constructorBody2475 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_constructorBody2478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2497 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation2500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation2508 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation2510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation2520 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_explicitConstructorInvocation2522 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation2524 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_explicitConstructorInvocation2527 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation2529 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_explicitConstructorInvocation2531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2551 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_qualifiedName2554 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2556 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal2582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal2592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal2602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal2612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal2622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_literal2632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations2721 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_annotation2741 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation2743 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation2747 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation2751 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_elementValue_in_annotation2755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotation2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2784 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_annotationName2787 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2789 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2810 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_elementValuePairs2813 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2815 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair2836 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_elementValuePair2838 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair2840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue2863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue2873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue2883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_elementValueArrayInitializer2906 = new BitSet(new long[]{0xFF00B20000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2909 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer2912 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2914 = new BitSet(new long[]{0x0000220000000000L});
    public static final BitSet FOLLOW_41_in_elementValueArrayInitializer2921 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_elementValueArrayInitializer2925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_annotationTypeDeclaration2948 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_annotationTypeDeclaration2950 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration2952 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_annotationTypeBody2977 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody2980 = new BitSet(new long[]{0x00F0301F94000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_45_in_annotationTypeBody2984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeElementDeclaration3007 = new BitSet(new long[]{0xFF00403F92000030L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration3009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest3032 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest3034 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementRest3046 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest3059 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest3072 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest3085 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_annotationTypeElementRest3087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest3111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest3121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest3144 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest3146 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotationMethodRest3148 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest3150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest3174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_defaultValue3197 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue3199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_block3220 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_blockStatement_in_block3222 = new BitSet(new long[]{0xFF20F03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_45_in_block3225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement3248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement3258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement3268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement3292 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_localVariableDeclarationStatement3294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration3313 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration3315 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration3317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_variableModifiers3340 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_block_in_statement3358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement3368 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3370 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement3373 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3375 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_statement3389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3391 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3393 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_statement3403 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement3417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement3419 = new BitSet(new long[]{0xFF00900804000FD0L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forControl_in_statement3421 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_statement3423 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement3435 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3437 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement3449 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3451 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement3453 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3455 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement3467 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3469 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_catches_in_statement3481 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_statement3483 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement3497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement3511 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement3533 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3535 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_statement3537 = new BitSet(new long[]{0x0000200000000000L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement3539 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_statement3541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_statement3551 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_parExpression_in_statement3553 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_statement3555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement3565 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3567 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement3580 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_statement3582 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement3594 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3596 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_statement3609 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3611 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_statement3624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement3635 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_statement3637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement3647 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement3649 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_statement3651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches3674 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_catchClause_in_catches3677 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_catchClause3702 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause3704 = new BitSet(new long[]{0xFF00000800000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause3706 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_catchClause3708 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_catchClause3710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter3729 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_formalParameter3731 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter3733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups3761 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000400L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup3788 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60002FBD7E6L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup3791 = new BitSet(new long[]{0xFF20D03F96001FF2L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_89_in_switchLabel3815 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel3817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_switchLabel3829 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel3831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_switchLabel3843 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_switchLabel3845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_forControl3876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl3886 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl3889 = new BitSet(new long[]{0xFF00900004000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_forControl3891 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_forControl3894 = new BitSet(new long[]{0xFF00900800000FD2L,0x0003E600000003E6L});
    public static final BitSet FOLLOW_forUpdate_in_forControl3896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit3926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_enhancedForControl3949 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_enhancedForControl3951 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enhancedForControl3953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_enhancedForControl3955 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_enhancedForControl3957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate3976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_parExpression3997 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_parExpression3999 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_parExpression4001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4024 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_expressionList4027 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expressionList4029 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression4050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression4073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression4096 = new BitSet(new long[]{0x0008050000000002L,0x00000003FC000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression4099 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_expression4101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator4136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator4146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator4156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator4166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator4176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator4186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator4196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator4206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_assignmentOperator4227 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_assignmentOperator4231 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4269 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4273 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4277 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4312 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignmentOperator4316 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_assignmentOperator4320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression4349 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_conditionalExpression4353 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4355 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_conditionalExpression4357 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4381 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalOrExpression4385 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4387 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4409 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_conditionalAndExpression4413 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression4415 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4437 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_inclusiveOrExpression4441 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression4443 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression4465 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_exclusiveOrExpression4469 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression4471 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression4493 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_andExpression4497 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression4499 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression4521 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression4525 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression4533 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression4555 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_instanceOfExpression4558 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression4560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression4581 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression4585 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression4587 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp4622 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp4626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp4656 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_relationalOp4660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_relationalOp4681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_relationalOp4692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression4712 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression4716 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression4718 = new BitSet(new long[]{0x0000050000000002L});
    public static final BitSet FOLLOW_40_in_shiftOp4749 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_shiftOp4753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp4785 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4789 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_shiftOp4823 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_shiftOp4827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression4857 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression4861 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression4869 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression4891 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression4895 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression4909 = new BitSet(new long[]{0x0000000040000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_105_in_unaryExpression4935 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression4937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_unaryExpression4947 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression4949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression4959 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression4961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpression4971 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression4973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression4983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus5002 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_unaryExpressionNotPlusMinus5014 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus5036 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus5038 = new BitSet(new long[]{0x0001000020000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus5041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5064 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression5066 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5068 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression5070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_castExpression5079 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_type_in_castExpression5082 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_castExpression5086 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_castExpression5089 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary5110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_primary5120 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5123 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5125 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_primary5140 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_primary5142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary5152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary5162 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_creator_in_primary5164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary5174 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_29_in_primary5177 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5179 = new BitSet(new long[]{0x0001000020000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary5194 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_48_in_primary5197 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_primary5199 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_primary5203 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary5205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_primary5215 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_primary5217 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_primary5219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix5239 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix5241 = new BitSet(new long[]{0x0001000020000000L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5245 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix5247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifierSuffix5258 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix5260 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_identifierSuffix5262 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5285 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_identifierSuffix5287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5297 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix5299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5309 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_identifierSuffix5311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5321 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_identifierSuffix5323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifierSuffix5335 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix5337 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix5339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator5358 = new BitSet(new long[]{0xFF00010000000010L});
    public static final BitSet FOLLOW_createdName_in_creator5360 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createdName_in_creator5372 = new BitSet(new long[]{0x0001000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator5375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName5399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName5409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator5432 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator5435 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator5437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5456 = new BitSet(new long[]{0xFF02900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5470 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5473 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5475 = new BitSet(new long[]{0x0001100000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest5479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest5493 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5495 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5498 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest5500 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5502 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_arrayCreatorRest5507 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_arrayCreatorRest5509 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest5540 = new BitSet(new long[]{0x000011C000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest5542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation5566 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocation5568 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation5570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_nonWildcardTypeArguments5593 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments5595 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_nonWildcardTypeArguments5597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5620 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector5622 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_selector5624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5635 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_selector5637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5647 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_selector5649 = new BitSet(new long[]{0x0000000020000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_superSuffix_in_selector5651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_selector5661 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector5663 = new BitSet(new long[]{0x0000010000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector5665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_selector5675 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_selector5677 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_selector5679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix5702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_superSuffix5712 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix5714 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_superSuffix5716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_arguments5736 = new BitSet(new long[]{0xFF00900800000FD0L,0x0003E600000003EEL});
    public static final BitSet FOLLOW_expressionList_in_arguments5738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_arguments5741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred5_Java61 = new BitSet(new long[]{0x0000403F92000020L,0x0000000000000200L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred5_Java75 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_importDeclaration_in_synpred5_Java77 = new BitSet(new long[]{0x0000403F9E000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java80 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred5_Java95 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_Java97 = new BitSet(new long[]{0x0000403F96000022L,0x0000000000000200L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred113_Java2472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred117_Java2497 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000022L});
    public static final BitSet FOLLOW_set_in_synpred117_Java2500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_synpred117_Java2508 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_synpred117_Java2510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred128_Java2721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred151_Java3248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred152_Java3258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_synpred157_Java3403 = new BitSet(new long[]{0xFF20D03F96001FF0L,0x0003E60000FBD3E6L});
    public static final BitSet FOLLOW_statement_in_synpred157_Java3405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred162_Java3481 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_synpred162_Java3483 = new BitSet(new long[]{0x0000100010000000L});
    public static final BitSet FOLLOW_block_in_synpred162_Java3485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred163_Java3497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchLabel_in_synpred178_Java3788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred180_Java3815 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_constantExpression_in_synpred180_Java3817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred180_Java3819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred181_Java3829 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred181_Java3831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_synpred181_Java3833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_synpred182_Java3876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred186_Java3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred188_Java4099 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred188_Java4101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred198_Java4217 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred198_Java4219 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred198_Java4221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred199_Java4257 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred199_Java4259 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred199_Java4261 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred199_Java4263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred200_Java4302 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred200_Java4304 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred200_Java4306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred211_Java4614 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred211_Java4616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred212_Java4648 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_synpred212_Java4650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_synpred215_Java4741 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_synpred215_Java4743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred216_Java4775 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred216_Java4777 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred216_Java4779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_synpred217_Java4815 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred217_Java4817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred229_Java5026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_synpred233_Java5064 = new BitSet(new long[]{0xFF00000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred233_Java5066 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_synpred233_Java5068 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred233_Java5070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred234_Java5082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred236_Java5123 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred236_Java5125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred237_Java5129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_synpred242_Java5177 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred242_Java5179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred243_Java5183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred249_Java5258 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred249_Java5260 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred249_Java5262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_synpred262_Java5498 = new BitSet(new long[]{0xFF00900000000FD0L,0x0003E600000001E6L});
    public static final BitSet FOLLOW_expression_in_synpred262_Java5500 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_synpred262_Java5502 = new BitSet(new long[]{0x0000000000000002L});

}