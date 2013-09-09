grammar Java;
options {
  backtrack=true; 
  memoize=true;
  output=AST;
  superClass = ChameleonParser;
}
import JavaL,JavaP;
@header {
package be.kuleuven.cs.distrinet.jnome.input.parser;

import be.kuleuven.cs.distrinet.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;

import be.kuleuven.cs.distrinet.chameleon.oo.member.DeclarationWithParametersHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.member.SimpleNameDeclarationWithParametersHeader;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.TargetDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.oo.type.*;
import be.kuleuven.cs.distrinet.jnome.core.type.*;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;

import be.kuleuven.cs.distrinet.chameleon.oo.expression.*;
import be.kuleuven.cs.distrinet.chameleon.core.reference.*;

import be.kuleuven.cs.distrinet.chameleon.core.language.Language;

import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;

import be.kuleuven.cs.distrinet.chameleon.oo.method.*;

import be.kuleuven.cs.distrinet.chameleon.oo.method.exception.*;

import be.kuleuven.cs.distrinet.chameleon.core.modifier.Modifier;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.NamespaceReference;

import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.Import;
import be.kuleuven.cs.distrinet.chameleon.oo.namespacedeclaration.TypeImport;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.DemandImport;

import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;

import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;

import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclaration;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclarator;

import be.kuleuven.cs.distrinet.chameleon.oo.modifier.AnnotationModifier;

import be.kuleuven.cs.distrinet.chameleon.oo.type.ClassBody;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ClassWithBody;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeElement;

import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;

import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;

import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;

import be.kuleuven.cs.distrinet.chameleon.input.InputProcessor;
import be.kuleuven.cs.distrinet.chameleon.input.Position2D;

import be.kuleuven.cs.distrinet.chameleon.support.expression.RegularLiteral;
import be.kuleuven.cs.distrinet.chameleon.support.expression.NullLiteral;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalAndExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalOrExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.InstanceofExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ThisLiteral;
import be.kuleuven.cs.distrinet.chameleon.support.expression.FilledArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.support.expression.EmptyArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ClassCastExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.SuperTarget;

import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;

import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.RegularMethodInvocation;

import be.kuleuven.cs.distrinet.chameleon.support.modifier.Abstract;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Final;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Private;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Protected;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Static;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Native;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Enum;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Interface;

import be.kuleuven.cs.distrinet.chameleon.support.statement.StatementExpression;
import be.kuleuven.cs.distrinet.chameleon.support.statement.LocalClassStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.AssertStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.IfThenElseStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ForStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ForControl;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ForInit;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SimpleForControl;
import be.kuleuven.cs.distrinet.chameleon.support.statement.EnhancedForControl;
import be.kuleuven.cs.distrinet.chameleon.support.statement.StatementExprList;
import be.kuleuven.cs.distrinet.chameleon.support.statement.TryStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.CatchClause;
import be.kuleuven.cs.distrinet.chameleon.support.statement.FinallyClause;
import be.kuleuven.cs.distrinet.chameleon.support.statement.DoStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.WhileStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchCase;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.CaseLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.DefaultLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.EnumLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ThrowStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.BreakStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ContinueStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SynchronizedStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.EmptyStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.LabeledStatement;

import be.kuleuven.cs.distrinet.chameleon.support.type.EmptyTypeElement;
import be.kuleuven.cs.distrinet.chameleon.support.type.StaticInitializer;

import be.kuleuven.cs.distrinet.chameleon.support.variable.LocalVariableDeclarator;

import be.kuleuven.cs.distrinet.chameleon.support.input.ChameleonParser;

import be.kuleuven.cs.distrinet.chameleon.util.Util;

import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;

import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.*;

import be.kuleuven.cs.distrinet.jnome.core.statement.*;

import be.kuleuven.cs.distrinet.jnome.core.expression.*;
import be.kuleuven.cs.distrinet.jnome.core.expression.operator.*;

import be.kuleuven.cs.distrinet.jnome.core.imports.SingleStaticImport;
import be.kuleuven.cs.distrinet.jnome.core.imports.StaticDemandImport;
import be.kuleuven.cs.distrinet.jnome.core.imports.JavaDemandImport;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;

import be.kuleuven.cs.distrinet.jnome.core.modifier.StrictFP;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Transient;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Volatile;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Synchronized;
import be.kuleuven.cs.distrinet.jnome.core.modifier.JavaConstructor;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Implements;
import be.kuleuven.cs.distrinet.jnome.core.modifier.AnnotationType;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaIntersectionTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildcard;

import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;

import be.kuleuven.cs.distrinet.jnome.core.enumeration.EnumConstant;

import be.kuleuven.cs.distrinet.jnome.core.variable.JavaVariableDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.variable.MultiFormalParameter;

import be.kuleuven.cs.distrinet.jnome.core.expression.*;
import be.kuleuven.cs.distrinet.jnome.input.*;

import java.util.List;
import java.util.ArrayList;
}
@members{

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

  public void setFactory(JavaFactory factory) {
    gJavaP.setFactory(factory);
  }
  
  public JavaFactory factory() {
    return gJavaP.factory();
  }

}

stuff	:	Identifier;
