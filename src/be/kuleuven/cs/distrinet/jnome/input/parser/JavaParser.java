// $ANTLR 3.3 Nov 30, 2010 12:50:56 be/kuleuven/cs/distrinet/jnome/input/parser/Java.g 2015-05-28 11:17:25

package be.kuleuven.cs.distrinet.jnome.input.parser;

import org.aikodi.chameleon.workspace.View;

import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;

import org.aikodi.chameleon.core.document.Document;

import org.aikodi.chameleon.oo.member.DeclarationWithParametersHeader;
import org.aikodi.chameleon.oo.member.SimpleNameDeclarationWithParametersHeader;
import org.aikodi.chameleon.core.declaration.SimpleNameSignature;
import org.aikodi.chameleon.core.declaration.TargetDeclaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.oo.type.*;
import be.kuleuven.cs.distrinet.jnome.core.type.*;

import org.aikodi.chameleon.core.element.Element;

import org.aikodi.chameleon.oo.expression.*;
import org.aikodi.chameleon.core.reference.*;

import org.aikodi.chameleon.core.language.Language;

import org.aikodi.chameleon.oo.member.Member;

import org.aikodi.chameleon.oo.method.*;

import org.aikodi.chameleon.oo.method.exception.*;

import org.aikodi.chameleon.core.modifier.Modifier;

import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespace.RootNamespace;
import org.aikodi.chameleon.core.namespace.NamespaceReference;

import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.core.namespacedeclaration.Import;
import org.aikodi.chameleon.oo.namespacedeclaration.TypeImport;
import org.aikodi.chameleon.core.namespacedeclaration.DemandImport;

import org.aikodi.chameleon.core.reference.CrossReference;

import org.aikodi.chameleon.oo.statement.Block;
import org.aikodi.chameleon.oo.statement.Statement;

import org.aikodi.chameleon.oo.variable.VariableDeclaration;
import org.aikodi.chameleon.oo.variable.VariableDeclarator;

import org.aikodi.chameleon.oo.modifier.AnnotationModifier;

import org.aikodi.chameleon.oo.type.ClassBody;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.ClassWithBody;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.TypeElement;

import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.ActualTypeArgument;
import org.aikodi.chameleon.oo.type.generics.BasicTypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;
import org.aikodi.chameleon.oo.type.generics.ExtendsConstraint;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;

import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;

import org.aikodi.chameleon.oo.variable.*;

import org.aikodi.chameleon.input.InputProcessor;
import org.aikodi.chameleon.input.Position2D;

import org.aikodi.chameleon.support.expression.RegularLiteral;
import org.aikodi.chameleon.support.expression.NullLiteral;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.chameleon.support.expression.ConditionalExpression;
import org.aikodi.chameleon.support.expression.ConditionalAndExpression;
import org.aikodi.chameleon.support.expression.ConditionalOrExpression;
import org.aikodi.chameleon.support.expression.InstanceofExpression;
import org.aikodi.chameleon.support.expression.ThisLiteral;
import org.aikodi.chameleon.support.expression.FilledArrayIndex;
import org.aikodi.chameleon.support.expression.EmptyArrayIndex;
import org.aikodi.chameleon.support.expression.ArrayIndex;
import org.aikodi.chameleon.support.expression.ClassCastExpression;
import org.aikodi.chameleon.support.expression.SuperTarget;

import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

import org.aikodi.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import org.aikodi.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import org.aikodi.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import org.aikodi.chameleon.support.member.simplename.method.RegularMethodInvocation;

import org.aikodi.chameleon.support.input.*;

import org.aikodi.chameleon.support.modifier.Abstract;
import org.aikodi.chameleon.support.modifier.Final;
import org.aikodi.chameleon.support.modifier.Private;
import org.aikodi.chameleon.support.modifier.Protected;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.chameleon.support.modifier.Static;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Enum;
import org.aikodi.chameleon.support.modifier.Interface;

import org.aikodi.chameleon.support.statement.StatementExpression;
import org.aikodi.chameleon.support.statement.LocalClassStatement;
import org.aikodi.chameleon.support.statement.AssertStatement;
import org.aikodi.chameleon.support.statement.IfThenElseStatement;
import org.aikodi.chameleon.support.statement.ForStatement;
import org.aikodi.chameleon.support.statement.ForControl;
import org.aikodi.chameleon.support.statement.ForInit;
import org.aikodi.chameleon.support.statement.SimpleForControl;
import org.aikodi.chameleon.support.statement.EnhancedForControl;
import org.aikodi.chameleon.support.statement.StatementExprList;
import org.aikodi.chameleon.support.statement.TryStatement;
import org.aikodi.chameleon.support.statement.CatchClause;
import org.aikodi.chameleon.support.statement.FinallyClause;
import org.aikodi.chameleon.support.statement.DoStatement;
import org.aikodi.chameleon.support.statement.WhileStatement;
import org.aikodi.chameleon.support.statement.SwitchStatement;
import org.aikodi.chameleon.support.statement.SwitchCase;
import org.aikodi.chameleon.support.statement.SwitchLabel;
import org.aikodi.chameleon.support.statement.CaseLabel;
import org.aikodi.chameleon.support.statement.DefaultLabel;
import org.aikodi.chameleon.support.statement.EnumLabel;
import org.aikodi.chameleon.support.statement.ReturnStatement;
import org.aikodi.chameleon.support.statement.ThrowStatement;
import org.aikodi.chameleon.support.statement.BreakStatement;
import org.aikodi.chameleon.support.statement.ContinueStatement;
import org.aikodi.chameleon.support.statement.SynchronizedStatement;
import org.aikodi.chameleon.support.statement.EmptyStatement;
import org.aikodi.chameleon.support.statement.LabeledStatement;

import org.aikodi.chameleon.support.type.EmptyTypeElement;
import org.aikodi.chameleon.support.type.StaticInitializer;

import org.aikodi.chameleon.support.variable.LocalVariableDeclarator;

import org.aikodi.chameleon.util.Util;

import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;

import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.*;

import be.kuleuven.cs.distrinet.jnome.core.statement.*;

import be.kuleuven.cs.distrinet.jnome.core.expression.*;
import be.kuleuven.cs.distrinet.jnome.core.expression.operator.*;

import be.kuleuven.cs.distrinet.jnome.core.imports.SingleStaticImport;
import be.kuleuven.cs.distrinet.jnome.core.imports.StaticDemandImport;
import be.kuleuven.cs.distrinet.jnome.core.imports.JavaDemandImport;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

import be.kuleuven.cs.distrinet.jnome.core.modifier.*;

import be.kuleuven.cs.distrinet.jnome.core.type.*;

import org.aikodi.chameleon.core.reference.*;

import be.kuleuven.cs.distrinet.jnome.core.enumeration.EnumConstant;

import be.kuleuven.cs.distrinet.jnome.core.variable.JavaVariableDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.variable.MultiFormalParameter;

import be.kuleuven.cs.distrinet.jnome.core.expression.*;
import be.kuleuven.cs.distrinet.jnome.input.*;

import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class JavaParser extends ChameleonANTLR3Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DecimalIntegerLiteral", "HexIntegerLiteral", "OctalIntegerLiteral", "BinaryIntegerLiteral", "IntegerLiteral", "DecimalNumeral", "IntegerTypeSuffix", "NonZeroDigit", "Digits", "Digit", "OctalNumeral", "OctalDigits", "OctalDigit", "HexNumeral", "HexDigits", "HexDigit", "BinaryNumeral", "BinaryDigits", "BinaryDigit", "DecimalFloatingPointLiteral", "HexadecimalFloatingPointLiteral", "FloatingPointLiteral", "ExponentPart", "FloatTypeSuffix", "ExponentIndicator", "SignedInteger", "Sign", "HexSignificand", "BinaryExponent", "BinaryExponentIndicator", "EscapeSequence", "CharacterLiteral", "StringLiteral", "UnicodeEscape", "OctalEscape", "ENUM", "ASSERT", "Letter", "JavaIDDigit", "Identifier", "WS", "COMMENT", "LINE_COMMENT", "'package'", "';'", "'import'", "'static'", "'.'", "'*'", "'public'", "'protected'", "'private'", "'abstract'", "'final'", "'strictfp'", "'class'", "'extends'", "'implements'", "'<'", "','", "'>'", "'&'", "'{'", "'}'", "'interface'", "'void'", "'['", "']'", "'throws'", "'='", "'native'", "'synchronized'", "'transient'", "'volatile'", "'default'", "'|'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'?'", "'super'", "'('", "')'", "'...'", "'this'", "'null'", "'true'", "'false'", "'@'", "':'", "'if'", "'else'", "'for'", "'while'", "'do'", "'try'", "'finally'", "'switch'", "'return'", "'throw'", "'break'", "'continue'", "'catch'", "'case'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'||'", "'&&'", "'^'", "'=='", "'!='", "'instanceof'", "'+'", "'-'", "'/'", "'%'", "'++'", "'--'", "'~'", "'!'", "'new'"
    };
    public static final int EOF=-1;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__50=50;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__59=59;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__73=73;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int T__84=84;
    public static final int T__85=85;
    public static final int T__86=86;
    public static final int T__87=87;
    public static final int T__88=88;
    public static final int T__89=89;
    public static final int T__90=90;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__94=94;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__99=99;
    public static final int T__100=100;
    public static final int T__101=101;
    public static final int T__102=102;
    public static final int T__103=103;
    public static final int T__104=104;
    public static final int T__105=105;
    public static final int T__106=106;
    public static final int T__107=107;
    public static final int T__108=108;
    public static final int T__109=109;
    public static final int T__110=110;
    public static final int T__111=111;
    public static final int T__112=112;
    public static final int T__113=113;
    public static final int T__114=114;
    public static final int T__115=115;
    public static final int T__116=116;
    public static final int T__117=117;
    public static final int T__118=118;
    public static final int T__119=119;
    public static final int T__120=120;
    public static final int T__121=121;
    public static final int T__122=122;
    public static final int T__123=123;
    public static final int T__124=124;
    public static final int T__125=125;
    public static final int T__126=126;
    public static final int T__127=127;
    public static final int T__128=128;
    public static final int T__129=129;
    public static final int T__130=130;
    public static final int T__131=131;
    public static final int T__132=132;
    public static final int T__133=133;
    public static final int T__134=134;
    public static final int T__135=135;
    public static final int DecimalIntegerLiteral=4;
    public static final int HexIntegerLiteral=5;
    public static final int OctalIntegerLiteral=6;
    public static final int BinaryIntegerLiteral=7;
    public static final int IntegerLiteral=8;
    public static final int DecimalNumeral=9;
    public static final int IntegerTypeSuffix=10;
    public static final int NonZeroDigit=11;
    public static final int Digits=12;
    public static final int Digit=13;
    public static final int OctalNumeral=14;
    public static final int OctalDigits=15;
    public static final int OctalDigit=16;
    public static final int HexNumeral=17;
    public static final int HexDigits=18;
    public static final int HexDigit=19;
    public static final int BinaryNumeral=20;
    public static final int BinaryDigits=21;
    public static final int BinaryDigit=22;
    public static final int DecimalFloatingPointLiteral=23;
    public static final int HexadecimalFloatingPointLiteral=24;
    public static final int FloatingPointLiteral=25;
    public static final int ExponentPart=26;
    public static final int FloatTypeSuffix=27;
    public static final int ExponentIndicator=28;
    public static final int SignedInteger=29;
    public static final int Sign=30;
    public static final int HexSignificand=31;
    public static final int BinaryExponent=32;
    public static final int BinaryExponentIndicator=33;
    public static final int EscapeSequence=34;
    public static final int CharacterLiteral=35;
    public static final int StringLiteral=36;
    public static final int UnicodeEscape=37;
    public static final int OctalEscape=38;
    public static final int ENUM=39;
    public static final int ASSERT=40;
    public static final int Letter=41;
    public static final int JavaIDDigit=42;
    public static final int Identifier=43;
    public static final int WS=44;
    public static final int COMMENT=45;
    public static final int LINE_COMMENT=46;

    // delegates
    public Java_JavaP gJavaP;
    // delegators


        public JavaParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JavaParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[419+1];
             
            gJavaP = new Java_JavaP(input, state, this);         
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
        gJavaP.setTreeAdaptor(this.adaptor);
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return JavaParser.tokenNames; }
    public String getGrammarFileName() { return "be/kuleuven/cs/distrinet/jnome/input/parser/Java.g"; }



    //  @Override
    //  public void setLanguage(Language language) {
     //   gJavaP.setLanguage(language);
     // }

     // @Override
     // public Language language() {
      //  return gJavaP.language();
     // }

      public void setView(View view) {
        super.setView(view);
        gJavaP.setView(view);
      }

      public Document getDocument() {
        return gJavaP.getDocument();
      }

      public void setDocument(Document compilationUnit) {
        gJavaP.setDocument(compilationUnit);
      }

      public Namespace getDefaultNamespace() {
        return gJavaP.getDefaultNamespace();
      }

      public void setFactory(Java7Factory factory) {
        gJavaP.setFactory(factory);
      }

      public Java7Factory factory() {
        return gJavaP.factory();
      }



    public static class stuff_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "stuff"
    // be/kuleuven/cs/distrinet/jnome/input/parser/Java.g:225:1: stuff : Identifier ;
    public final JavaParser.stuff_return stuff() throws RecognitionException {
        JavaParser.stuff_return retval = new JavaParser.stuff_return();
        retval.start = input.LT(1);
        int stuff_StartIndex = input.index();
        Object root_0 = null;

        Token Identifier1=null;

        Object Identifier1_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 419) ) { return retval; }
            // be/kuleuven/cs/distrinet/jnome/input/parser/Java.g:225:7: ( Identifier )
            // be/kuleuven/cs/distrinet/jnome/input/parser/Java.g:225:9: Identifier
            {
            root_0 = (Object)adaptor.nil();

            Identifier1=(Token)match(input,Identifier,FOLLOW_Identifier_in_stuff60); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Identifier1_tree = (Object)adaptor.create(Identifier1);
            adaptor.addChild(root_0, Identifier1_tree);
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
            if ( state.backtracking>0 ) { memoize(input, 419, stuff_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "stuff"

    // Delegated rules
    public Java_JavaP.primary_return primary() throws RecognitionException { return gJavaP.primary(); }
    public Java_JavaP.methodBody_return methodBody() throws RecognitionException { return gJavaP.methodBody(); }
    public Java_JavaP.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException { return gJavaP.classOrInterfaceType(); }
    public Java_JavaP.instanceOfExpression_return instanceOfExpression() throws RecognitionException { return gJavaP.instanceOfExpression(); }
    public Java_JavaP.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest() throws RecognitionException { return gJavaP.interfaceMethodDeclaratorRest(); }
    public Java_JavaP.typeDeclaration_return typeDeclaration() throws RecognitionException { return gJavaP.typeDeclaration(); }
    public Java_JavaP.innerCreator_return innerCreator() throws RecognitionException { return gJavaP.innerCreator(); }
    public Java_JavaP.voidMethodDeclaration_return voidMethodDeclaration() throws RecognitionException { return gJavaP.voidMethodDeclaration(); }
    public Java_JavaP.catches_return catches() throws RecognitionException { return gJavaP.catches(); }
    public Java_JavaP.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl() throws RecognitionException { return gJavaP.interfaceMethodOrFieldDecl(); }
    public Java_JavaP.typeList_return typeList() throws RecognitionException { return gJavaP.typeList(); }
    public Java_JavaP.elementValuePair_return elementValuePair() throws RecognitionException { return gJavaP.elementValuePair(); }
    public Java_JavaP.annotationTypeBody_return annotationTypeBody() throws RecognitionException { return gJavaP.annotationTypeBody(); }
    public Java_JavaP.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException { return gJavaP.exclusiveOrExpression(); }
    public Java_JavaP.interfaceMemberDecl_return interfaceMemberDecl() throws RecognitionException { return gJavaP.interfaceMemberDecl(); }
    public Java_JavaP.enhancedForControl_return enhancedForControl() throws RecognitionException { return gJavaP.enhancedForControl(); }
    public Java_JavaP.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException { return gJavaP.inclusiveOrExpression(); }
    public Java_JavaP.forControl_return forControl() throws RecognitionException { return gJavaP.forControl(); }
    public Java_JavaP.qualifiedNameList_return qualifiedNameList() throws RecognitionException { return gJavaP.qualifiedNameList(); }
    public Java_JavaP.constructorDeclaration_return constructorDeclaration() throws RecognitionException { return gJavaP.constructorDeclaration(); }
    public Java_JavaP.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException { return gJavaP.normalInterfaceDeclaration(); }
    public Java_JavaP.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl() throws RecognitionException { return gJavaP.genericMethodOrConstructorDecl(); }
    public Java_JavaP.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException { return gJavaP.localVariableDeclaration(); }
    public Java_JavaP.nonTargetPrimary_return nonTargetPrimary() throws RecognitionException { return gJavaP.nonTargetPrimary(); }
    public Java_JavaP.argumentsSuffixRubbish_return argumentsSuffixRubbish() throws RecognitionException { return gJavaP.argumentsSuffixRubbish(); }
    public Java_JavaP.annotations_return annotations() throws RecognitionException { return gJavaP.annotations(); }
    public Java_JavaP.catchParameter_return catchParameter() throws RecognitionException { return gJavaP.catchParameter(); }
    public Java_JavaP.additiveExpression_return additiveExpression() throws RecognitionException { return gJavaP.additiveExpression(); }
    public Java_JavaP.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException { return gJavaP.unaryExpressionNotPlusMinus(); }
    public Java_JavaP.annotationTypeElementRest_return annotationTypeElementRest() throws RecognitionException { return gJavaP.annotationTypeElementRest(); }
    public Java_JavaP.assignmentOperator_return assignmentOperator() throws RecognitionException { return gJavaP.assignmentOperator(); }
    public Java_JavaP.typeParameters_return typeParameters() throws RecognitionException { return gJavaP.typeParameters(); }
    public Java_JavaP.castExpression_return castExpression() throws RecognitionException { return gJavaP.castExpression(); }
    public Java_JavaP.arguments_return arguments() throws RecognitionException { return gJavaP.arguments(); }
    public Java_JavaP.equalityExpression_return equalityExpression() throws RecognitionException { return gJavaP.equalityExpression(); }
    public Java_JavaP.elementValue_return elementValue() throws RecognitionException { return gJavaP.elementValue(); }
    public Java_JavaP.variableModifiers_return variableModifiers() throws RecognitionException { return gJavaP.variableModifiers(); }
    public Java_JavaP.importDeclaration_return importDeclaration() throws RecognitionException { return gJavaP.importDeclaration(); }
    public Java_JavaP.resources_return resources() throws RecognitionException { return gJavaP.resources(); }
    public Java_JavaP.annotationConstantRest_return annotationConstantRest(TypeReference type) throws RecognitionException { return gJavaP.annotationConstantRest(type); }
    public Java_JavaP.expressionList_return expressionList() throws RecognitionException { return gJavaP.expressionList(); }
    public Java_JavaP.statementExpression_return statementExpression() throws RecognitionException { return gJavaP.statementExpression(); }
    public Java_JavaP.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException { return gJavaP.interfaceBodyDeclaration(); }
    public Java_JavaP.voidInterfaceMethodDeclaration_return voidInterfaceMethodDeclaration() throws RecognitionException { return gJavaP.voidInterfaceMethodDeclaration(); }
    public Java_JavaP.modifier_return modifier() throws RecognitionException { return gJavaP.modifier(); }
    public Java_JavaP.superSuffix_return superSuffix() throws RecognitionException { return gJavaP.superSuffix(); }
    public Java_JavaP.annotationMethodRest_return annotationMethodRest(TypeReference type) throws RecognitionException { return gJavaP.annotationMethodRest(type); }
    public Java_JavaP.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException { return gJavaP.nonWildcardTypeArguments(); }
    public Java_JavaP.variableDeclaratorId_return variableDeclaratorId() throws RecognitionException { return gJavaP.variableDeclaratorId(); }
    public Java_JavaP.selector_return selector() throws RecognitionException { return gJavaP.selector(); }
    public Java_JavaP.variableDeclarator_return variableDeclarator() throws RecognitionException { return gJavaP.variableDeclarator(); }
    public Java_JavaP.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException { return gJavaP.conditionalAndExpression(); }
    public Java_JavaP.nameAndParams_return nameAndParams() throws RecognitionException { return gJavaP.nameAndParams(); }
    public Java_JavaP.typeParameter_return typeParameter() throws RecognitionException { return gJavaP.typeParameter(); }
    public Java_JavaP.memberDecl_return memberDecl() throws RecognitionException { return gJavaP.memberDecl(); }
    public Java_JavaP.voidType_return voidType() throws RecognitionException { return gJavaP.voidType(); }
    public Java_JavaP.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest() throws RecognitionException { return gJavaP.voidInterfaceMethodDeclaratorRest(); }
    public Java_JavaP.classBody_return classBody() throws RecognitionException { return gJavaP.classBody(); }
    public Java_JavaP.methodDeclaration_return methodDeclaration() throws RecognitionException { return gJavaP.methodDeclaration(); }
    public Java_JavaP.enumBody_return enumBody() throws RecognitionException { return gJavaP.enumBody(); }
    public Java_JavaP.classOrInterfaceModifiers_return classOrInterfaceModifiers() throws RecognitionException { return gJavaP.classOrInterfaceModifiers(); }
    public Java_JavaP.genericMethodOrConstructorRest_return genericMethodOrConstructorRest() throws RecognitionException { return gJavaP.genericMethodOrConstructorRest(); }
    public Java_JavaP.interfaceConstant_return interfaceConstant() throws RecognitionException { return gJavaP.interfaceConstant(); }
    public Java_JavaP.variableInitializer_return variableInitializer() throws RecognitionException { return gJavaP.variableInitializer(); }
    public Java_JavaP.classOrInterfaceModifier_return classOrInterfaceModifier() throws RecognitionException { return gJavaP.classOrInterfaceModifier(); }
    public Java_JavaP.arrayAccessSuffixRubbish_return arrayAccessSuffixRubbish() throws RecognitionException { return gJavaP.arrayAccessSuffixRubbish(); }
    public Java_JavaP.compilationUnit_return compilationUnit() throws RecognitionException { return gJavaP.compilationUnit(); }
    public Java_JavaP.andExpression_return andExpression() throws RecognitionException { return gJavaP.andExpression(); }
    public Java_JavaP.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException { return gJavaP.annotationTypeElementDeclaration(); }
    public Java_JavaP.formalParameterDecls_return formalParameterDecls() throws RecognitionException { return gJavaP.formalParameterDecls(); }
    public Java_JavaP.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException { return gJavaP.classBodyDeclaration(); }
    public Java_JavaP.literal_return literal() throws RecognitionException { return gJavaP.literal(); }
    public Java_JavaP.annotationMethodOrConstantRest_return annotationMethodOrConstantRest(TypeReference type) throws RecognitionException { return gJavaP.annotationMethodOrConstantRest(type); }
    public Java_JavaP.createdName_return createdName() throws RecognitionException { return gJavaP.createdName(); }
    public Java_JavaP.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException { return gJavaP.classOrInterfaceDeclaration(); }
    public Java_JavaP.annotation_return annotation() throws RecognitionException { return gJavaP.annotation(); }
    public Java_JavaP.blockStatement_return blockStatement() throws RecognitionException { return gJavaP.blockStatement(); }
    public Java_JavaP.creator_return creator() throws RecognitionException { return gJavaP.creator(); }
    public Java_JavaP.block_return block() throws RecognitionException { return gJavaP.block(); }
    public Java_JavaP.defaultValue_return defaultValue() throws RecognitionException { return gJavaP.defaultValue(); }
    public Java_JavaP.interfaceGenericMethodDecl_return interfaceGenericMethodDecl() throws RecognitionException { return gJavaP.interfaceGenericMethodDecl(); }
    public Java_JavaP.constructorBody_return constructorBody() throws RecognitionException { return gJavaP.constructorBody(); }
    public Java_JavaP.relationalExpression_return relationalExpression() throws RecognitionException { return gJavaP.relationalExpression(); }
    public Java_JavaP.switchCase_return switchCase() throws RecognitionException { return gJavaP.switchCase(); }
    public Java_JavaP.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException { return gJavaP.multiplicativeExpression(); }
    public Java_JavaP.identifierSuffixRubbush_return identifierSuffixRubbush() throws RecognitionException { return gJavaP.identifierSuffixRubbush(); }
    public Java_JavaP.identifierRule_return identifierRule() throws RecognitionException { return gJavaP.identifierRule(); }
    public Java_JavaP.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException { return gJavaP.enumBodyDeclarations(); }
    public Java_JavaP.moreidentifierRuleSuffixRubbish_return moreidentifierRuleSuffixRubbish() throws RecognitionException { return gJavaP.moreidentifierRuleSuffixRubbish(); }
    public Java_JavaP.statement_return statement() throws RecognitionException { return gJavaP.statement(); }
    public Java_JavaP.type_return type() throws RecognitionException { return gJavaP.type(); }
    public Java_JavaP.enumDeclaration_return enumDeclaration() throws RecognitionException { return gJavaP.enumDeclaration(); }
    public Java_JavaP.shiftExpression_return shiftExpression() throws RecognitionException { return gJavaP.shiftExpression(); }
    public Java_JavaP.typeName_return typeName() throws RecognitionException { return gJavaP.typeName(); }
    public Java_JavaP.arrayInitializer_return arrayInitializer() throws RecognitionException { return gJavaP.arrayInitializer(); }
    public Java_JavaP.formalParameters_return formalParameters() throws RecognitionException { return gJavaP.formalParameters(); }
    public Java_JavaP.classCreatorRest_return classCreatorRest() throws RecognitionException { return gJavaP.classCreatorRest(); }
    public Java_JavaP.booleanLiteral_return booleanLiteral() throws RecognitionException { return gJavaP.booleanLiteral(); }
    public Java_JavaP.enumConstants_return enumConstants() throws RecognitionException { return gJavaP.enumConstants(); }
    public Java_JavaP.enumConstant_return enumConstant() throws RecognitionException { return gJavaP.enumConstant(); }
    public Java_JavaP.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException { return gJavaP.localVariableDeclarationStatement(); }
    public Java_JavaP.integerLiteral_return integerLiteral() throws RecognitionException { return gJavaP.integerLiteral(); }
    public Java_JavaP.memberDeclaration_return memberDeclaration() throws RecognitionException { return gJavaP.memberDeclaration(); }
    public Java_JavaP.enumConstantName_return enumConstantName() throws RecognitionException { return gJavaP.enumConstantName(); }
    public Java_JavaP.constructorDeclaratorRest_return constructorDeclaratorRest() throws RecognitionException { return gJavaP.constructorDeclaratorRest(); }
    public Java_JavaP.typeArguments_return typeArguments() throws RecognitionException { return gJavaP.typeArguments(); }
    public Java_JavaP.packageDeclaration_return packageDeclaration() throws RecognitionException { return gJavaP.packageDeclaration(); }
    public Java_JavaP.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException { return gJavaP.explicitConstructorInvocation(); }
    public Java_JavaP.unaryExpression_return unaryExpression() throws RecognitionException { return gJavaP.unaryExpression(); }
    public Java_JavaP.relationalOp_return relationalOp() throws RecognitionException { return gJavaP.relationalOp(); }
    public Java_JavaP.constantDeclarator_return constantDeclarator() throws RecognitionException { return gJavaP.constantDeclarator(); }
    public Java_JavaP.possibleUnionType_return possibleUnionType() throws RecognitionException { return gJavaP.possibleUnionType(); }
    public Java_JavaP.forInit_return forInit() throws RecognitionException { return gJavaP.forInit(); }
    public Java_JavaP.classDeclaration_return classDeclaration() throws RecognitionException { return gJavaP.classDeclaration(); }
    public Java_JavaP.shiftOp_return shiftOp() throws RecognitionException { return gJavaP.shiftOp(); }
    public Java_JavaP.parExpression_return parExpression() throws RecognitionException { return gJavaP.parExpression(); }
    public Java_JavaP.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException { return gJavaP.explicitGenericInvocation(); }
    public Java_JavaP.typeArgument_return typeArgument() throws RecognitionException { return gJavaP.typeArgument(); }
    public Java_JavaP.modifiers_return modifiers() throws RecognitionException { return gJavaP.modifiers(); }
    public Java_JavaP.createClassHereBecauseANTLRisAnnoying_return createClassHereBecauseANTLRisAnnoying() throws RecognitionException { return gJavaP.createClassHereBecauseANTLRisAnnoying(); }
    public Java_JavaP.qualifiedName_return qualifiedName() throws RecognitionException { return gJavaP.qualifiedName(); }
    public Java_JavaP.elementValuePairs_return elementValuePairs() throws RecognitionException { return gJavaP.elementValuePairs(); }
    public Java_JavaP.catchClause_return catchClause() throws RecognitionException { return gJavaP.catchClause(); }
    public Java_JavaP.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException { return gJavaP.conditionalOrExpression(); }
    public Java_JavaP.primitiveType_return primitiveType() throws RecognitionException { return gJavaP.primitiveType(); }
    public Java_JavaP.fieldDeclaration_return fieldDeclaration() throws RecognitionException { return gJavaP.fieldDeclaration(); }
    public Java_JavaP.variableModifier_return variableModifier() throws RecognitionException { return gJavaP.variableModifier(); }
    public Java_JavaP.variableDeclarators_return variableDeclarators() throws RecognitionException { return gJavaP.variableDeclarators(); }
    public Java_JavaP.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException { return gJavaP.interfaceDeclaration(); }
    public Java_JavaP.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException { return gJavaP.normalClassDeclaration(); }
    public Java_JavaP.formalParameter_return formalParameter() throws RecognitionException { return gJavaP.formalParameter(); }
    public Java_JavaP.typeBound_return typeBound() throws RecognitionException { return gJavaP.typeBound(); }
    public Java_JavaP.expression_return expression() throws RecognitionException { return gJavaP.expression(); }
    public Java_JavaP.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException { return gJavaP.annotationTypeDeclaration(); }
    public Java_JavaP.interfaceMethod_return interfaceMethod() throws RecognitionException { return gJavaP.interfaceMethod(); }
    public Java_JavaP.annotationName_return annotationName() throws RecognitionException { return gJavaP.annotationName(); }
    public Java_JavaP.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException { return gJavaP.elementValueArrayInitializer(); }
    public Java_JavaP.interfaceBody_return interfaceBody() throws RecognitionException { return gJavaP.interfaceBody(); }
    public Java_JavaP.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException { return gJavaP.switchBlockStatementGroups(); }
    public Java_JavaP.voidMethodDeclaratorRest_return voidMethodDeclaratorRest() throws RecognitionException { return gJavaP.voidMethodDeclaratorRest(); }
    public Java_JavaP.forUpdate_return forUpdate() throws RecognitionException { return gJavaP.forUpdate(); }
    public Java_JavaP.methodDeclaratorRest_return methodDeclaratorRest() throws RecognitionException { return gJavaP.methodDeclaratorRest(); }
    public Java_JavaP.switchLabel_return switchLabel() throws RecognitionException { return gJavaP.switchLabel(); }
    public Java_JavaP.constantExpression_return constantExpression() throws RecognitionException { return gJavaP.constantExpression(); }
    public Java_JavaP.conditionalExpression_return conditionalExpression() throws RecognitionException { return gJavaP.conditionalExpression(); }


 

    public static final BitSet FOLLOW_Identifier_in_stuff60 = new BitSet(new long[]{0x0000000000000002L});

}