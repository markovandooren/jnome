// $ANTLR 3.1.1 /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g 2009-01-14 16:01:10

package jnome.input.parser;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.context.ContextFactory;
import chameleon.core.context.LookupException;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.language.Language;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.namespacepart.DemandImport;
import chameleon.core.namespacepart.Import;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
public class JavaWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Identifier", "ENUM", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "ASSERT", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'package'", "';'", "'import'", "'static'", "'.'", "'*'", "'public'", "'protected'", "'private'", "'abstract'", "'final'", "'strictfp'", "'class'", "'extends'", "'implements'", "'<'", "','", "'>'", "'&'", "'{'", "'}'", "'interface'", "'void'", "'['", "']'", "'throws'", "'='", "'native'", "'synchronized'", "'transient'", "'volatile'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'?'", "'super'", "'('", "')'", "'...'", "'this'", "'null'", "'true'", "'false'", "'@'", "'default'", "':'", "'if'", "'else'", "'for'", "'while'", "'do'", "'try'", "'finally'", "'switch'", "'return'", "'throw'", "'break'", "'continue'", "'catch'", "'case'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'||'", "'&&'", "'|'", "'^'", "'=='", "'!='", "'instanceof'", "'+'", "'-'", "'/'", "'%'", "'++'", "'--'", "'~'", "'!'", "'new'", "'qqqq'", "'error'"
    };
    public static final int T__42=42;
    public static final int HexDigit=13;
    public static final int T__109=109;
    public static final int T__47=47;
    public static final int T__73=73;
    public static final int T__115=115;
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
    public static final int T__48=48;
    public static final int T__54=54;
    public static final int FloatTypeSuffix=16;
    public static final int T__113=113;
    public static final int IntegerTypeSuffix=14;
    public static final int T__89=89;
    public static final int Identifier=4;
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
    public static final int T__114=114;
    public static final int T__63=63;
    public static final int T__110=110;
    public static final int T__91=91;
    public static final int T__43=43;
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
    public static final int T__29=29;
    public static final int T__45=45;
    public static final int T__55=55;
    public static final int T__103=103;
    public static final int T__84=84;
    public static final int JavaIDDigit=21;
    public static final int T__97=97;
    public static final int T__111=111;
    public static final int T__105=105;
    public static final int T__75=75;
    public static final int T__31=31;
    public static final int EOF=-1;
    public static final int T__53=53;
    public static final int T__32=32;
    public static final int T__38=38;
    public static final int T__76=76;
    public static final int T__37=37;
    public static final int T__82=82;
    public static final int OctalEscape=19;
    public static final int T__81=81;
    public static final int T__83=83;
    public static final int T__71=71;
    public static final int T__102=102;

    // delegates
    // delegators


        public JavaWalker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public JavaWalker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return JavaWalker.tokenNames; }
    public String getGrammarFileName() { return "/Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g"; }


      /* Sandbox the model until parsing is done. This way,
       * we are certain that the model will not throw LookupExceptions 
       * because the model contains multiple elements with the same name. This
       * can only happen if the model is corrupted by the parser or by methods of the
       * metamodel invoked by the parser. We convert these exceptions to ChameleonProgrammerExceptions
       */ 
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
      



    // $ANTLR start "compilationUnit"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:85:1: compilationUnit returns [CompilationUnit cu = new CompilationUnit(new NamespacePart(language().defaultNamespace()))] : ( ( '@' )=> annotations ( packageDeclaration (imp= importDeclaration )* (type= typeDeclaration )* | classOrInterfaceDeclaration (type= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (type= typeDeclaration )* );
    public final CompilationUnit compilationUnit() throws RecognitionException {
        CompilationUnit cu =  new CompilationUnit(new NamespacePart(language().defaultNamespace()));

        Import imp = null;

        Type type = null;

        NamespacePart np = null;


        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:86:5: ( ( '@' )=> annotations ( packageDeclaration (imp= importDeclaration )* (type= typeDeclaration )* | classOrInterfaceDeclaration (type= typeDeclaration )* ) | (np= packageDeclaration )? (imp= importDeclaration )* (type= typeDeclaration )* )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==73) && (synpred1_JavaWalker())) {
                alt8=1;
            }
            else if ( (LA8_0==EOF||(LA8_0>=25 && LA8_0<=27)||LA8_0==114) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return cu;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:86:9: ( '@' )=> annotations ( packageDeclaration (imp= importDeclaration )* (type= typeDeclaration )* | classOrInterfaceDeclaration (type= typeDeclaration )* )
                    {
                    pushFollow(FOLLOW_annotations_in_compilationUnit68);
                    annotations();

                    state._fsp--;
                    if (state.failed) return cu;
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:87:9: ( packageDeclaration (imp= importDeclaration )* (type= typeDeclaration )* | classOrInterfaceDeclaration (type= typeDeclaration )* )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==25) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==114) ) {
                        alt4=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return cu;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:87:13: packageDeclaration (imp= importDeclaration )* (type= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit83);
                            packageDeclaration();

                            state._fsp--;
                            if (state.failed) return cu;
                            if ( state.backtracking==0 ) {
                              processPackageDeclaration(cu,np);
                            }
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:87:67: (imp= importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==27) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:87:68: imp= importDeclaration
                            	    {
                            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit89);
                            	    imp=importDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return cu;
                            	    if ( state.backtracking==0 ) {
                            	      processImport(cu,imp);
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop1;
                                }
                            } while (true);

                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:87:116: (type= typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==26||LA2_0==114) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:87:117: type= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit97);
                            	    type=typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return cu;
                            	    if ( state.backtracking==0 ) {
                            	      processType(cu,type);
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
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:88:13: classOrInterfaceDeclaration (type= typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit115);
                            classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return cu;
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:88:41: (type= typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==26||LA3_0==114) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:88:42: type= typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit120);
                            	    type=typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return cu;
                            	    if ( state.backtracking==0 ) {
                            	      processType(cu,type);
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
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:92:8: (np= packageDeclaration )? (imp= importDeclaration )* (type= typeDeclaration )*
                    {
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:92:10: (np= packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==25) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:92:10: np= packageDeclaration
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit158);
                            np=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return cu;

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      processPackageDeclaration(cu,np);
                    }
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:93:8: (imp= importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==27) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:93:9: imp= importDeclaration
                    	    {
                    	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit172);
                    	    imp=importDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return cu;
                    	    if ( state.backtracking==0 ) {
                    	      processImport(cu,imp);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:94:8: (type= typeDeclaration )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==26||LA7_0==114) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:94:9: type= typeDeclaration
                    	    {
                    	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit188);
                    	    type=typeDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return cu;
                    	    if ( state.backtracking==0 ) {
                    	      processType(cu,type);
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
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return cu;
    }
    // $ANTLR end "compilationUnit"


    // $ANTLR start "packageDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:97:1: packageDeclaration returns [NamespacePart np=null] : 'package' qn= qualifiedName ';' ;
    public final NamespacePart packageDeclaration() throws RecognitionException {
        NamespacePart np = null;

        String qn = null;


        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:98:5: ( 'package' qn= qualifiedName ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:98:9: 'package' qn= qualifiedName ';'
            {
            match(input,25,FOLLOW_25_in_packageDeclaration215); if (state.failed) return np;
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration219);
            qn=qualifiedName();

            state._fsp--;
            if (state.failed) return np;
            match(input,26,FOLLOW_26_in_packageDeclaration221); if (state.failed) return np;
            if ( state.backtracking==0 ) {
              try{
                         np = new NamespacePart(root.getOrCreateNamespace(qn));
                       }
                       catch(LookupException exc) {
                         //this should not happen, something is wrong with the tree parser
                         throw new ChameleonProgrammerException(exc);
                       }
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return np;
    }
    // $ANTLR end "packageDeclaration"


    // $ANTLR start "importDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:109:1: importDeclaration returns [Import imp=null] : 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' ;
    public final Import importDeclaration() throws RecognitionException {
        Import imp = null;

        CommonTree st=null;
        CommonTree star=null;
        String qn = null;


        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:5: ( 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';' )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:9: 'import' (st= 'static' )? qn= qualifiedName (star= ( '.' '*' ) )? ';'
            {
            match(input,27,FOLLOW_27_in_importDeclaration259); if (state.failed) return imp;
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:20: (st= 'static' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==28) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:20: st= 'static'
                    {
                    st=(CommonTree)match(input,28,FOLLOW_28_in_importDeclaration263); if (state.failed) return imp;

                    }
                    break;

            }

            pushFollow(FOLLOW_qualifiedName_in_importDeclaration268);
            qn=qualifiedName();

            state._fsp--;
            if (state.failed) return imp;
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:52: (star= ( '.' '*' ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==29) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:52: star= ( '.' '*' )
                    {
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:53: ( '.' '*' )
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:110:54: '.' '*'
                    {
                    match(input,29,FOLLOW_29_in_importDeclaration273); if (state.failed) return imp;
                    match(input,30,FOLLOW_30_in_importDeclaration275); if (state.failed) return imp;

                    }


                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_importDeclaration279); if (state.failed) return imp;
            if ( state.backtracking==0 ) {

                       if(star==null) {
                         imp = new TypeImport(typeRef(qn));
                         // type import
                       } else {
                         // demand import
                         imp = new DemandImport(typeRef(qn));
                       }
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return imp;
    }
    // $ANTLR end "importDeclaration"


    // $ANTLR start "typeDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:122:1: typeDeclaration returns [Type t=null] : ( classOrInterfaceDeclaration | ';' );
    public final Type typeDeclaration() throws RecognitionException {
        Type t = null;

        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:123:5: ( classOrInterfaceDeclaration | ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==114) ) {
                alt11=1;
            }
            else if ( (LA11_0==26) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:123:8: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration315);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return t;

                    }
                    break;
                case 2 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:124:8: ';'
                    {
                    match(input,26,FOLLOW_26_in_typeDeclaration324); if (state.failed) return t;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return t;
    }
    // $ANTLR end "typeDeclaration"


    // $ANTLR start "classOrInterfaceDeclaration"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:127:1: classOrInterfaceDeclaration : 'qqqq' ;
    public final void classOrInterfaceDeclaration() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:128:5: ( 'qqqq' )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:128:8: 'qqqq'
            {
            match(input,114,FOLLOW_114_in_classOrInterfaceDeclaration342); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "classOrInterfaceDeclaration"


    // $ANTLR start "qualifiedName"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:132:1: qualifiedName returns [String result=null] : Identifier ( '.' Identifier )* ;
    public final String qualifiedName() throws RecognitionException {
        String result = null;

        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:133:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:133:9: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_qualifiedName367); if (state.failed) return result;
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:133:20: ( '.' Identifier )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==29) ) {
                    int LA12_2 = input.LA(2);

                    if ( (LA12_2==Identifier) ) {
                        alt12=1;
                    }


                }


                switch (alt12) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:133:21: '.' Identifier
            	    {
            	    match(input,29,FOLLOW_29_in_qualifiedName370); if (state.failed) return result;
            	    match(input,Identifier,FOLLOW_Identifier_in_qualifiedName372); if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "qualifiedName"


    // $ANTLR start "annotations"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:138:1: annotations : ( annotation )+ ;
    public final void annotations() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:139:5: ( ( annotation )+ )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:139:9: ( annotation )+
            {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:139:9: ( annotation )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==73) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:139:9: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations395);
            	    annotation();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotations"


    // $ANTLR start "annotation"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:142:1: annotation : '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final void annotation() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:5: ( '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:9: '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            match(input,73,FOLLOW_73_in_annotation415); if (state.failed) return ;
            pushFollow(FOLLOW_annotationName_in_annotation417);
            annotationName();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:28: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==66) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:30: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    match(input,66,FOLLOW_66_in_annotation421); if (state.failed) return ;
                    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:34: ( elementValuePairs | elementValue )?
                    int alt14=3;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==Identifier) ) {
                        alt14=1;
                    }
                    else if ( (LA14_0==115) ) {
                        alt14=2;
                    }
                    switch (alt14) {
                        case 1 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:36: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation425);
                            elementValuePairs();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:143:56: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation429);
                            elementValue();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,67,FOLLOW_67_in_annotation434); if (state.failed) return ;

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
        }
        return ;
    }
    // $ANTLR end "annotation"


    // $ANTLR start "annotationName"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:146:1: annotationName : Identifier ( '.' Identifier )* ;
    public final void annotationName() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:147:5: ( Identifier ( '.' Identifier )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:147:7: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationName458); if (state.failed) return ;
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:147:18: ( '.' Identifier )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==29) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:147:19: '.' Identifier
            	    {
            	    match(input,29,FOLLOW_29_in_annotationName461); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_annotationName463); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotationName"


    // $ANTLR start "elementValuePairs"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:150:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final void elementValuePairs() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:151:5: ( elementValuePair ( ',' elementValuePair )* )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:151:9: elementValuePair ( ',' elementValuePair )*
            {
            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs484);
            elementValuePair();

            state._fsp--;
            if (state.failed) return ;
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:151:26: ( ',' elementValuePair )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==41) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:151:27: ',' elementValuePair
            	    {
            	    match(input,41,FOLLOW_41_in_elementValuePairs487); if (state.failed) return ;
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs489);
            	    elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementValuePairs"


    // $ANTLR start "elementValuePair"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:154:1: elementValuePair : Identifier '=' elementValue ;
    public final void elementValuePair() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:155:5: ( Identifier '=' elementValue )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:155:9: Identifier '=' elementValue
            {
            match(input,Identifier,FOLLOW_Identifier_in_elementValuePair510); if (state.failed) return ;
            match(input,51,FOLLOW_51_in_elementValuePair512); if (state.failed) return ;
            pushFollow(FOLLOW_elementValue_in_elementValuePair514);
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
        }
        return ;
    }
    // $ANTLR end "elementValuePair"


    // $ANTLR start "elementValue"
    // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:158:1: elementValue : 'error' ;
    public final void elementValue() throws RecognitionException {
        try {
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:159:5: ( 'error' )
            // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:159:7: 'error'
            {
            match(input,115,FOLLOW_115_in_elementValue535); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementValue"

    // $ANTLR start synpred1_JavaWalker
    public final void synpred1_JavaWalker_fragment() throws RecognitionException {   
        // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:86:9: ( '@' )
        // /Users/marko/git/jnome/src/jnome/input/parser/JavaWalker.g:86:10: '@'
        {
        match(input,73,FOLLOW_73_in_synpred1_JavaWalker64); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_JavaWalker

    // Delegated rules

    public final boolean synpred1_JavaWalker() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_JavaWalker_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_annotations_in_compilationUnit68 = new BitSet(new long[]{0x0000000002000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit83 = new BitSet(new long[]{0x000000000E000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit89 = new BitSet(new long[]{0x000000000E000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit97 = new BitSet(new long[]{0x0000000006000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_compilationUnit115 = new BitSet(new long[]{0x0000000006000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit120 = new BitSet(new long[]{0x0000000006000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit158 = new BitSet(new long[]{0x000000000E000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit172 = new BitSet(new long[]{0x000000000E000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit188 = new BitSet(new long[]{0x0000000006000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_25_in_packageDeclaration215 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration219 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_packageDeclaration221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_importDeclaration259 = new BitSet(new long[]{0x0000000010000010L});
    public static final BitSet FOLLOW_28_in_importDeclaration263 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_importDeclaration268 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration273 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_importDeclaration275 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_importDeclaration279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_typeDeclaration324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_classOrInterfaceDeclaration342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName367 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_qualifiedName370 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName372 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_annotation_in_annotations395 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_annotation415 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation417 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation421 = new BitSet(new long[]{0x0000000000000010L,0x0008000000000008L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation425 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_elementValue_in_annotation429 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_annotation434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName458 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_annotationName461 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName463 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs484 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_elementValuePairs487 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs489 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair510 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_elementValuePair512 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_115_in_elementValue535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_synpred1_JavaWalker64 = new BitSet(new long[]{0x0000000000000002L});

}