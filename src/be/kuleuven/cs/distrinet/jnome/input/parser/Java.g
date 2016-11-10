grammar Java;
options {
  backtrack=true;
  memoize=true;
  output=AST;
  superClass = ChameleonANTLR3Parser;
}
import JavaL,JavaP;
@header {
package be.kuleuven.cs.distrinet.jnome.input.parser;

import org.aikodi.chameleon.workspace.View;

import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;

import org.aikodi.chameleon.core.document.Document;

import org.aikodi.chameleon.core.declaration.Declarator;
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

import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;
import org.aikodi.chameleon.oo.type.generics.ExtendsConstraint;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;

import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;

import org.aikodi.chameleon.oo.variable.*;

import org.aikodi.chameleon.input.InputProcessor;
import org.aikodi.chameleon.input.TextPosition;

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

  public void setFactory(Java7Factory factory) {
    gJavaP.setFactory(factory);
  }

  public Java7Factory factory() {
    return gJavaP.factory();
  }

}

stuff	:	Identifier;
