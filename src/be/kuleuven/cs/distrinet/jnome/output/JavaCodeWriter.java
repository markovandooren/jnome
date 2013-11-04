package be.kuleuven.cs.distrinet.jnome.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ElementWithModifiers;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.Modifier;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.NamespaceReference;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.DemandImport;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.Import;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.reference.ElementReference;
import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Literal;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NamedTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NameExpression;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.VariableReference;
import be.kuleuven.cs.distrinet.chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Implementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.method.NativeImplementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.RegularImplementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.exception.ExceptionClause;
import be.kuleuven.cs.distrinet.chameleon.oo.method.exception.ExceptionDeclaration;
import be.kuleuven.cs.distrinet.chameleon.oo.method.exception.TypeExceptionDeclaration;
import be.kuleuven.cs.distrinet.chameleon.oo.modifier.AnnotationModifier;
import be.kuleuven.cs.distrinet.chameleon.oo.namespacedeclaration.TypeImport;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeElement;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.AbstractInheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclaration;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclarator;
import be.kuleuven.cs.distrinet.chameleon.plugin.output.Syntax;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ClassCastExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalAndExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalOrExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.EmptyArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.support.expression.FilledArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.support.expression.InstanceofExpression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.SuperTarget;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ThisLiteral;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.RegularMethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Abstract;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Constructor;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Enum;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Final;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Interface;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Native;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Private;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Protected;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Static;
import be.kuleuven.cs.distrinet.chameleon.support.statement.AssertStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.BreakStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.CaseLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.CatchClause;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ContinueStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.DefaultLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.DoStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.EmptyStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.EnhancedForControl;
import be.kuleuven.cs.distrinet.chameleon.support.statement.FinallyClause;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ForStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.IfThenElseStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.LabeledStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.LocalClassStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SimpleForControl;
import be.kuleuven.cs.distrinet.chameleon.support.statement.StatementExprList;
import be.kuleuven.cs.distrinet.chameleon.support.statement.StatementExpression;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchCase;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SynchronizedStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ThrowStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.TryStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.WhileStatement;
import be.kuleuven.cs.distrinet.chameleon.support.tool.Arguments;
import be.kuleuven.cs.distrinet.chameleon.support.type.EmptyTypeElement;
import be.kuleuven.cs.distrinet.chameleon.support.type.StaticInitializer;
import be.kuleuven.cs.distrinet.chameleon.support.variable.LocalVariableDeclarator;
import be.kuleuven.cs.distrinet.jnome.core.expression.ArrayAccessExpression;
import be.kuleuven.cs.distrinet.jnome.core.expression.ArrayCreationExpression;
import be.kuleuven.cs.distrinet.jnome.core.expression.ArrayInitializer;
import be.kuleuven.cs.distrinet.jnome.core.expression.ClassLiteral;
import be.kuleuven.cs.distrinet.jnome.core.expression.DimensionInitializer;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.ConstructorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.SuperConstructorDelegation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.ThisConstructorDelegation;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Default;
import be.kuleuven.cs.distrinet.jnome.core.modifier.StrictFP;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Synchronized;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Volatile;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaIntersectionTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildcard;
import be.kuleuven.cs.distrinet.rejuse.java.collections.Visitor;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

/**
 * @author Marko van Dooren
 */
public class JavaCodeWriter extends Syntax {
  
  public String toCode(Element element)  {
    String result = null;
    if(isAnonymousClass(element)) {
      result = toCodeAnonymousClass((Type) element);
    } else if(isClass(element)) {
      result = toCodeClass((Type)element);
    } else if(isInterface(element)) {
      result = toCodeInterface((Type)element);
    } else if(isModifier(element)) {
      result = toCodeModifier((Modifier)element);
    } else if(isBreak(element)) {
      result = toCodeBreak((BreakStatement)element);
    } else if(isConstructorInvocation(element)) {
      result = toCodeConstructorInvocation((ConstructorInvocation)element);
    } else if(isCondAnd(element)) {
      result = toCodeCondAnd((ConditionalAndExpression)element);
	  } else if(isEmptyArrayIndex(element)) {
		result = toCodeEmptyArrayIndex((EmptyArrayIndex)element);
	  } else if(isFilledArrayIndex(element)) {
		result = toCodeFilledArrayIndex((FilledArrayIndex)element);
    } else if(isCondOr(element)) {
      result = toCodeCondOr((ConditionalOrExpression)element);
    } else if(isInfixInvocation(element)) {
      result = toCodeInfixInvocation((InfixOperatorInvocation)element);
    } else if(isPrefixInvocation(element)) {
      result = toCodePrefixInvocation((PrefixOperatorInvocation)element);
    } else if(isPostfixInvocation(element)) {
      result = toCodePostfixInvocation((PostfixOperatorInvocation)element);
    } else if(isClassLiteral(element)) {
      result = toCodeClassLiteral((ClassLiteral)element);
    } else if(isThisLiteral(element)) {
      result = toCodeThisLiteral((ThisLiteral)element);
    } else if(isLiteral(element)) {
      result = toCodeLiteral((Literal)element);
    } else if(isNamedTarget(element)) {
      result = toCodeNamedTarget((NamedTarget)element);
    } else if(isRegulaMethodInvocation(element)) {
      result = toCodeRegularMethodInvocation((RegularMethodInvocation)element);
    } else if(isNamedTargetRef(element)) {
      result = toCodeNamedTargetRef((NameExpression)element);
    } else if(isVarRef(element)) {
      result = toCodeVarRef((VariableReference)element);
    } else if(isThisConstructorDelegation(element)) {
      result = toCodeThisConstructorDelegation((ThisConstructorDelegation)element);
    } else if(isSuperTarget(element)) {
      result = toCodeSuperTarget((SuperTarget)element);
    } else if(isSuperConstructorDelegation(element)) {
      result = toCodeSuperConstructorDelegation((SuperConstructorDelegation)element);
    } else if(isInstanceOf(element)) {
      result = toCodeInstanceOf((InstanceofExpression)element);
    } else if(isDimInit(element)) {
      result = toCodeDimInit((DimensionInitializer)element);
    } else if(isCondExpr(element)) {
      result = toCodeCondExpr((ConditionalExpression)element);
    } else if(isCast(element)) {
      result = toCodeCast((ClassCastExpression)element);
    } else if(isArrayInit(element)) {
      result = toCodeArrayInit((ArrayInitializer)element);
    } else if(isArrayCreation(element)) {
      result = toCodeArrayCreation((ArrayCreationExpression)element);
    } else if(isArrayAccess(element)) {
      result = toCodeArrayAccess((ArrayAccessExpression)element);
    } else if(isIf(element)) {
      result = toCodeIf((IfThenElseStatement)element);
    } else if(isBlock(element)) {
      result = toCodeBlock((Block)element);
    } else if(isWhile(element)) {
      result = toCodeWhile((WhileStatement)element);
    } else if(isTry(element)) {
      result = toCodeTry((TryStatement)element);
    } else if(isThrow(element)) {
      result = toCodeThrow((ThrowStatement)element);
    } else if(isSynchronized(element)) {
      result = toCodeSynchronized((SynchronizedStatement)element);
    } else if(isSwitch(element)) {
      result = toCodeSwitch((SwitchStatement)element);
    } else if(isStatementExprList(element)) {
      result = toCodeStatementExprList((StatementExprList)element);
    } else if(isReturn(element)) {
      result = toCodeReturn((ReturnStatement)element);
    } else if(isMemberVariableDeclarator(element)) {
      result = toCodeMemberVariableDeclarator((MemberVariableDeclarator)element);
    } else if(isLocalVariableDeclarator(element)) {
      result = toCodeLocalVariableDeclarator((LocalVariableDeclarator)element);
    } else if(isLocalClass(element)) {
      result = toCodeLocalClass((LocalClassStatement)element);
    } else if(isLabeledStatement(element)) {
      result = toCodeLabeledStatement((LabeledStatement)element);
    } else if(isFor(element)) {
      result = toCodeFor((ForStatement)element);
    } else if(isContinue(element)) {
      result = toCodeContinue((ContinueStatement)element);
    } else if(isEmpty(element)) {
      result = toCodeEmptyStatement((EmptyStatement)element);
    } else if(isDo(element)) {
      result = toCodeDo((DoStatement)element);
    } else if(isStatementExpression(element)) {
      result = toCodeStatementExpression((StatementExpression)element);
    } else if(isAssignment(element)) {
      result = toCodeAssignment((AssignmentExpression)element);
    } else if(isMethod(element)) {
      result = toCodeMethod((Method)element);
    } else if(isStaticInitializer(element)) {
      result = toCodeStaticInitializer((StaticInitializer)element);
    } else if(isCompilationUnit(element)) {
      result = toCodeCompilationUnit((Document)element);
    } else if(isNamespaceReference(element)) {
      result = toCodeNamespaceReference((NamespaceReference)element);
    } else if(isBasicTypeReference(element)) {
      result = toCodeBasicTypeReference((BasicJavaTypeReference)element);
    } else if(isIntersectionTypeReference(element)) {
      result = toCodeIntersectionTypeReference((JavaIntersectionTypeReference)element);
    } else if(isArrayTypeReference(element)) {
      result = toCodeArrayTypeReference((ArrayTypeReference)element);
    } 
      // Specific reference MUST come after the other references.
      else if(isElementReference(element)) {
      result = toCodeElementReference((ElementReference)element);
    } else if(isNamespacePart(element)) {
    	result = toCodeNamespacePart((NamespaceDeclaration) element);
    } 
//    else if(isActualParameter(element)) {
//    	result = toCodeActualParameter((ActualArgument) element);
//    } 
    else if(isSimpleForControl(element)) {
    	result = toCodeSimpleForControl((SimpleForControl) element);
    } else if(isEnhancedForControl(element)) {
    	result = toCodeEnhancedForControl((EnhancedForControl) element);
    } else if(isBasicTypeArgument(element)) {
    	result = toCodeBasicTypeArgument((BasicTypeArgument) element);
    } else if(isFormalTypeParameter(element)) {
    	result = toCodeFormalTypeParameter((FormalTypeParameter) element);
    } else if(isExtendsConstraint(element)) {
    	result = toCodeExtendsConstraint((ExtendsConstraint) element);
    } else if(isAssert(element)) {
    	result = toCodeAssert((AssertStatement) element);
    } else if(isExtendsWildCard(element)) {
    	result = toCodeExtendsWildCard((ExtendsWildcard) element);
    } else if(isSuperWildCard(element)) {
    	result = toCodeSuperWildCard((SuperWildcard) element);
    } else if(isPureWildCard(element)) {
    	result = toCodePureWildCard((PureWildcard) element);
    } else if(isEmptyTypeElement(element)) {
    	result = toCodeEmptyTypeElement((EmptyTypeElement)element);
    } else if(isDemandImport(element)) {
    	result = toCodeDemandImport((DemandImport)element);
    } else if(isTypeImport(element)) {
    	result = toCodeTypeImport((TypeImport)element);
    }
    // /ASPECTS
    else if(element == null) {
      result = "";
    }
    else {
      throw new IllegalArgumentException("The given element is not know by the Java syntax: "+element.getClass().getName());
    }
    return result;
  }
  
  public boolean isEmptyTypeElement(Element element) {
  	return element instanceof EmptyTypeElement;
  }

  public String toCodeEmptyTypeElement(EmptyTypeElement element) {
  	return ";";
  }

  public boolean isExtendsWildCard(Element element) {
  	return element instanceof ExtendsWildcard;
  }
  
  public String toCodeExtendsWildCard(ExtendsWildcard element)  {
  	return "? extends " + toCode(element.typeReference());
  }
  
  public boolean isSuperWildCard(Element element) {
  	return element instanceof SuperWildcard;
  }
  
  public String toCodeSuperWildCard(SuperWildcard element)  {
  	return "? super " + toCode(element.typeReference());
  }
  
  public boolean isPureWildCard(Element element) {
  	return element instanceof PureWildcard;
  }
  
  public String toCodePureWildCard(PureWildcard element)  {
  	return "?";
  }
  
  public boolean isAssert(Element element) {
  	return element instanceof AssertStatement;
  }
  
  public String toCodeAssert(AssertStatement element)  {
  	return "assert(" + toCode(element.getExpression()) +");";
  }
  
  public String toCodeBasicTypeArgument(BasicTypeArgument element)  {
		return toCode(element.typeReference());
	}

	public boolean isBasicTypeArgument(Element element) {
		return element instanceof BasicTypeArgument;
	}

//	public boolean isActualParameter(Element element) {
//  	return element instanceof ActualArgument;
//  }
//  
//  public String toCodeActualParameter(ActualArgument parameter)  {
//  	return toCode(parameter.getExpression());
//  }
  
//  public boolean isNamespaceOrTypeReference(Element element) {
//    return element instanceof NamespaceOrTypeReference;
//  }
//  
//  public String toCodeNamespaceOrTypeReference(NamespaceOrTypeReference typeReference)  {
//    String result = toCode(typeReference.getTarget());
//    if(result.length() > 0) {
//      result = result + ".";
//    }
//    result = result + typeReference.signature();
//    return result;
//  }

  public boolean isElementReference(Element element) {
    return element instanceof ElementReference;
  }
  
  public String toCodeElementReference(ElementReference typeReference)  {
    String result = toCode(typeReference.getTarget());
    if(result.length() > 0) {
      result = result + ".";
    }
    result = result + typeReference.name();
    return result;
  }

  public boolean isNamespaceReference(Element element) {
    return element instanceof NamespaceReference;
  }
  
  public String toCodeNamespaceReference(NamespaceReference typeReference)  {
    String result = toCode(typeReference.getTarget());
    if(result.length() > 0) {
      result = result + ".";
    }
    result = result + typeReference.name();
    return result;
  }

  public boolean isIntersectionTypeReference(Element element) {
    return element instanceof JavaIntersectionTypeReference;
  }
  
  public String toCodeIntersectionTypeReference(JavaIntersectionTypeReference typeReference)  {
  	StringBuffer result = new StringBuffer();
  	Iterator<? extends TypeReference> iter = typeReference.typeReferences().iterator();
  	while(iter.hasNext()) {
  		result.append(toCode(iter.next()));
  		if(iter.hasNext()) {
  			result.append(" & ");
  		}
  	}
//  	return toCode(typeReference.leftHandSide())+" & "+toCode(typeReference.rightHandSide());
  	return result.toString();
  }
  
  public boolean isArrayTypeReference(Element element) {
    return element instanceof ArrayTypeReference;
  }
  
  public String toCodeArrayTypeReference(ArrayTypeReference typeReference)  {
  	return toCode(typeReference.elementTypeReference()) + "[]";
  }
  
  public boolean isBasicTypeReference(Element element) {
    return element instanceof BasicJavaTypeReference;
  }
  
  public String toCodeBasicTypeReference(BasicJavaTypeReference typeReference)  {
    String result = toCode(typeReference.getTarget());
    if(result.length() > 0) {
      result = result + ".";
    }
    result = result + typeReference.name();
    	List<ActualTypeArgument> typeArguments = typeReference.typeArguments();
    	if(! typeArguments.isEmpty()) {
    		result = result +"<";
    		Iterator<ActualTypeArgument> iter = typeArguments.iterator();
    		while(iter.hasNext()) {
    			result = result + toCode(iter.next());
    			if(iter.hasNext()) {
    				result = result +",";
    			}
    		}
    		result = result +">";
    	}
    return result;
  }

  public boolean isStaticInitializer(Element element) {
    return element instanceof StaticInitializer;
  }
  
  public String toCodeStaticInitializer(StaticInitializer init)  {
    return "static " + toCode(init.getBlock());
  }
  
 
  public JavaCodeWriter(){
	  _tabSize = 4;
  }
  
  public JavaCodeWriter(int tabSize) {
    _tabSize = tabSize;
  }
  
  private int _tabSize;
  
  public int getTabSize() {
    return _tabSize;
  }
  
  public int getIndent() {
    return _indent;
  }
  
  private int _indent;
  
  public void indent() {
    _indent += getTabSize();
  }
  
  public void undent() {
    _indent -= getTabSize();
  }
  
  public StringBuffer startLine() {
    StringBuffer result = new StringBuffer();
    int indent = getIndent();
    for(int i = 0; i < indent; i++) {
      result.append(' ');
    }
    return result;
  }
  
  public String wrapLine(String line) {
    return startLine() + line + "\n";
  }
  
  public boolean isCompilationUnit(Element element) {
    return element instanceof Document;
  }
  
  public boolean isNamespacePart(Element element) {
  	return element instanceof NamespaceDeclaration;
  }

  public boolean isDemandImport(Element element) {
  	return element instanceof DemandImport;
  }
  
  public String toCodeDemandImport(DemandImport imp)  {
    return "import "+toCode(imp.containerReference()) +".*;\n";
  }
  
  public boolean isTypeImport(Element element) {
  	return element instanceof TypeImport;
  }
  
  public String toCodeTypeImport(TypeImport imp) {
		return "import "+ toCode(((TypeImport)imp).getTypeReference()) +";\n";
  }
  
  public String toCodeNamespacePart(NamespaceDeclaration part)  {
    StringBuffer result = new StringBuffer();
    result.append("package "+part.namespace().getFullyQualifiedName() +";\n\n");
    for(Import imp: part.imports()) {
        result.append(toCode(imp));
    }
    result.append("\n");
    Collection<Type> types = part.declarations(Type.class);
    new SafePredicate<Type>() {
      public boolean eval(Type o) {
        return !(o instanceof ArrayType);
      }
    }.filter(types);
    Iterator iter = types.iterator();
    while(iter.hasNext()) {
      result.append(toCode((Element)iter.next()));
      if(iter.hasNext()) {
        result.append("\n\n");
      }
    }
    return result.toString();
  }
  
  public String toCodeCompilationUnit(Document cu)  {
    StringBuffer result = new StringBuffer();
  	for(NamespaceDeclaration part: cu.namespaceDeclarations()) {
  		result.append(toCodeNamespacePart(part));
  	}
  	return result.toString();
  }

  private String toCodeModifier(Modifier element) {
    if(element instanceof Public) {
      return "public";
    } else if(element instanceof Protected) {
      return "protected";
    } else if(element instanceof Private) {
      return "private";
    } else if(element instanceof Default) {
      return "";
    } else if(element instanceof Abstract) {
      return "abstract";
    } else if(element instanceof Static) {
      return "static";
    } else if(element instanceof Final) {
      return "final";
    } else if(element instanceof StrictFP) {
      return "strictfp";
    } else if(element instanceof Synchronized) {
      return "synchronized";
    } else if(element instanceof Constructor) {
    	return "";
    } else if(element instanceof Native) {
    	return "native";
    } else if(element instanceof Interface) {
    	return "";
    } else if(element instanceof AnnotationModifier) {
    	return "@" + toCode(((AnnotationModifier) element).typeReference());
    } else if(element instanceof Volatile) {
    	return "volatile";
    } else if(element instanceof Enum) {
    	return "enum";
    }
    else {
      throw new IllegalArgumentException("The given element is not know by the Java syntax: "+element.getClass().getName());
    }
  }

  public boolean isModifier(Element element) {
    return element instanceof Modifier;
  }

  public boolean isClass(Element element) {
  	if (element instanceof RegularType) {
//  		element.flushCache();
  		return ((Type)element).is(((Java)language()).INTERFACE) != Ternary.TRUE;
  	}
  	return false;
  }
  
  public boolean isInterface(Element element) {
    if(element instanceof Type){
    	return (element).is(((Java)language()).INTERFACE) == Ternary.TRUE;
    }
    return false;
  }
  
  public String toCodeClassBlock(Type type)  {
    final StringBuffer result = new StringBuffer();
    result.append("{\n");
    indent();
    
    List<? extends TypeElement> members = type.directlyDeclaredElements();
    // Members
    for(Element element: members) {
      result.append(toCode((Element)element));
      result.append("\n\n");
    }
    undent();
    result.append(startLine());
    result.append("}");
    
    return result.toString();
  }
  
  public boolean isAnonymousClass(Element element) {
    return (element instanceof Type) && (element.parent() instanceof ConstructorInvocation);
  }
  
  public String toCodeAnonymousClass(Type type)  {
    return toCodeClassBlock(type);
  }
  
  public String toCodeClass(Type type)  {
    final StringBuffer result = startLine();
    
    //Modifiers
    for(Modifier element:type.modifiers()) {
      result.append((toCodeModifier((Modifier)element)));
      result.append(" ");
    }
    //Name
    result.append("class ");
    result.append(type.name());
    appendTypeParameters(type.parameters(TypeParameter.class), result);
    List<SubtypeRelation> superTypes = type.nonMemberInheritanceRelations(SubtypeRelation.class);
    final List<TypeReference> classRefs = new ArrayList<TypeReference>();
    final List<TypeReference> interfaceRefs = new ArrayList<TypeReference>();
    for(SubtypeRelation relation:superTypes) {
    	TypeReference typeRef = relation.superClassReference();
      if(relation.is(relation.language(Java.class).IMPLEMENTS_RELATION) == Ternary.TRUE) {
        interfaceRefs.add(typeRef);
      } else {
        classRefs.add(typeRef);
      }
    }
    if(classRefs.size() > 0) {
      result.append(" extends ");
      result.append(toCode(classRefs.get(0)));
    }
    
    if(interfaceRefs.size() > 0) {
      result.append(" implements ");
      Iterator<TypeReference> iter = interfaceRefs.iterator();
      while(iter.hasNext()) {
        result.append(toCode(iter.next()));
        if(iter.hasNext()) {
          result.append(", ");
        }
      }
    }
    result.append(" ");
    result.append(toCodeClassBlock(type));
    
    return result.toString();
  }

	private void appendTypeParameters(List<? extends Element> parameters, final StringBuffer result)  {
		if(! parameters.isEmpty()) {
    	result.append("<");
    	Iterator<? extends Element> iter = parameters.iterator();
    	while(iter.hasNext()) {
    		result.append(toCode(iter.next()));
    		if(iter.hasNext()) {
    			result.append(",");
    		}
    	}
    	result.append(">");
    }
	}
  
  public boolean isFormalTypeParameter(Element element) {
  	return element instanceof FormalTypeParameter;
  }
  
  public String toCodeFormalTypeParameter(FormalTypeParameter param)  {
  	StringBuffer result = new StringBuffer();
  	result.append(param.signature().name());
  	List<TypeConstraint> constraints = param.constraints();
  	if(! constraints.isEmpty()) {
    	result.append(" ");
  	}
  	Iterator<TypeConstraint> iter = constraints.iterator();
  	while(iter.hasNext()) {
  		result.append(toCode(iter.next()));
  		if(iter.hasNext()) {
  			result.append(",");
  		}
  	}
  	return result.toString();
  }
  
  public boolean isExtendsConstraint(Element element) {
  	return element instanceof ExtendsConstraint;
  }
  
  public String toCodeExtendsConstraint(ExtendsConstraint constraint)  {
  	StringBuffer result = new StringBuffer();
  	result.append("extends ");
  	result.append(toCode(constraint.typeReference()));
  	return result.toString();
  }

  public String toCodeInterface(Type type)  {
    final StringBuffer result = startLine();
    
    //Modifiers
    for(Modifier element: type.modifiers()) {
      result.append((toCodeModifier((Modifier)element)));
      result.append(" ");
    }
    
    //Name
    result.append("interface ");
    result.append(type.name());
    appendTypeParameters(type.parameters(TypeParameter.class), result);
    List<SubtypeRelation> superTypes = new ArrayList<>(type.nonMemberInheritanceRelations(SubtypeRelation.class));
    new SafePredicate<SubtypeRelation>() {
      public boolean eval(SubtypeRelation rel)  {
        return ! toCode((rel).superClassReference()).equals("java.lang.Object");
      }
    }.filter(superTypes);
    if(superTypes.size() > 0) {
      result.append(" extends ");
      Iterator iter = superTypes.iterator();
      while(iter.hasNext()) {
        TypeReference tr = ((AbstractInheritanceRelation)iter.next()).superClassReference();
          result.append(toCode(tr));
          if (iter.hasNext()) {
            result.append(", ");
          }
      }
    }
    result.append(" ");
    result.append(toCodeClassBlock(type));
    
    return result.toString();
    
  }

  public String toCodeMethod(Method method)  {
	    final StringBuffer result = startLine();
	    
	    addModifiers(method, result);
	    
	    appendTypeParameters(method.typeParameters(), result);
	    
	    if(! (method.is(method.language(Java.class).CONSTRUCTOR) == Ternary.TRUE)) {
	        result.append(toCode(method.returnTypeReference()));
	        result.append(" ");
	      }
	    
	    result.append(((SimpleNameDeclarationWithParametersSignature)method.signature()).name());
//	    result.append(method.name());
	    result.append("(");
	    Iterator iter = method.formalParameters().iterator();
	    while(iter.hasNext()) {
	      FormalParameter param = (FormalParameter)iter.next();
	      result.append(toCodeVariable(param));
	      if(iter.hasNext()) {
	        result.append(", ");
	      }
	    }
	    result.append(") ");
	    result.append(toCodeExceptionClause(method.getExceptionClause()));
	    if(! method.getExceptionClause().exceptionDeclarations().isEmpty()) {
	      result.append(" ");
	    }
	    result.append(toCodeImplementation(method.implementation()));
	    indent();
	    undent();
	    return result.toString();
  }
//	 final StringBuffer result = startLine();
//    //Modifiers
//    
//    new Visitor() {
//      public void visit(Object element) {
//        result.append((toCodeModifier((Modifier)element)));
//        result.append(" ");
//      }
//    }.applyTo(method.getModifiers());
//    
//    if(method instanceof NonConstructor) {
//      result.append(toCode(((NonConstructor)method).getReturnTypeReference()));
//      result.append(" ");
//    }
//    
//    result.append(method.getName());
//    result.append("(");
//    Iterator iter = method.getParameters().iterator();
//    while(iter.hasNext()) {
//      FormalParameter param = (FormalParameter)iter.next();
//      result.append(toCodeVariable(param));
//      if(iter.hasNext()) {
//        result.append(", ");
//      }
//    }
//    result.append(") ");
//    result.append(toCodeExceptionClause(method.getExceptionClause()));
//    if(! method.getExceptionClause().getDeclarations().isEmpty()) {
//      result.append(" ");
//    }
//    result.append(toCodeImplementation(method.getImplementation()));
//    indent();
//    undent();
//    return result.toString();
//  }


	protected void addModifiers(ElementWithModifiers element, final StringBuffer result) {
		new Visitor() {
		  public void visit(Object element) {
		    result.append((toCodeModifier((Modifier)element)));
		    result.append(" ");
		  }
		}.applyTo(element.modifiers());
	}
  
  public String toCodeImplementation(Implementation impl)  {
    if((impl == null) || (impl instanceof NativeImplementation)) {
      return ";";
    }
    else {
      return toCode(((RegularImplementation)impl).getBody());
    }
  }
  
  public boolean isMethod(Element element) {
    return element instanceof Method;
  }
  
  public String toCodeExceptionClause(ExceptionClause ec)  {
    final StringBuffer result = new StringBuffer();
    List decls = ec.exceptionDeclarations();
    if(! decls.isEmpty()) {
      result.append("throws ");
    }
    Iterator iter = (decls.iterator());
    while(iter.hasNext()) {
      result.append(toCodeExceptionDeclaration((ExceptionDeclaration)iter.next()));
      if(iter.hasNext()) {
        result.append(", ");
      }
    }
    return result.toString();
  }
  
  public String toCodeExceptionDeclaration(ExceptionDeclaration ed)  {
    if(ed instanceof TypeExceptionDeclaration) {
      return toCode(((TypeExceptionDeclaration)ed).getTypeReference());
    }
    else {
      throw new IllegalArgumentException("The given element is not know by the Java syntax.");
    }
  }
  
  /********************
   * MEMBER VARIABLES *
   ********************/
  
//  public String toCodeMemberVariable(RegularMemberVariable var)  {
//    return startLine() + toCodeVariable(var);
//  }
  
  public String toCodeVariable(FormalParameter var)  {
    final StringBuffer result = new StringBuffer();
    new Visitor() {
      public void visit(Object element) {
        result.append((toCodeModifier((Modifier)element)));
        result.append(" ");
      }
    }.applyTo(var.modifiers());
    result.append(toCode(var.getTypeReference()));
    result.append(" ");
    result.append(var.name());
//      if(var.getInitialization() != null) {
//      result.append(" = ");
//      result.append(toCode(var.getInitialization()));
//      }
//      result.append(";");
    return result.toString();
  }
  
  /**************
   * STATEMENTS *
   **************/
  
  public boolean isStatementExpression(Element element) {
    return element instanceof StatementExpression;
  }
  
  public String toCodeStatementExpression(StatementExpression stat)  {
    return toCode(stat.getExpression())+";";
  }
  
  public boolean isBreak(Element element) {
    return element instanceof BreakStatement;
  }
  
  public String toCodeBreak(BreakStatement st) {
    return "break" + (st.getLabel() == null ? "":" "+st.getLabel()) + ";";
  }
  
  public boolean isContinue(Element element) {
    return element instanceof ContinueStatement;
  }
  
  public String toCodeContinue(ContinueStatement st) {
    return "continue" + (st.getLabel() == null ? "":" "+st.getLabel()) + ";";
  }
  
  public boolean isBlock(Element element) {
    return element instanceof Block;
  }
  
  public String toCodeBlock(Block block)  {
    StringBuffer result = new StringBuffer();
    result.append("{\n");
    indent();
    Iterator iter = block.statements().iterator();
    while(iter.hasNext()) {
      result.append(startLine());
      result.append(toCode((Element)iter.next()));
      result.append("\n");
    }
    undent();
    result.append(startLine());
    result.append("}");
    return result.toString();
  }
  
  public boolean isIf(Element element) {
    return element instanceof IfThenElseStatement;
  }
  
  public String toCodeIf(IfThenElseStatement stat)  {
    StringBuffer result = new StringBuffer();
    result.append("if(");
    result.append(toCode(stat.getExpression()));
    result.append(") ");
    result.append(toCode(stat.getIfStatement()));
    if(stat.getElseStatement() != null) {
      result.append("\n");
      result.append(startLine());
      result.append("else ");
      result.append(toCode(stat.getElseStatement()));
    }
    return result.toString();
  }
  
  public boolean isWhile(Element element) {
    return element instanceof WhileStatement;
  }
  
  public String toCodeWhile(WhileStatement element)  {
    StringBuffer result = new StringBuffer();
    result.append("while (");
    result.append(toCode(element.condition()));
    result.append(") ");
    result.append(toCode(element.getStatement()));
    return result.toString();
  }
  
  public boolean isTry(Element element) {
    return element instanceof TryStatement;
  }
  
  public String toCodeTry(TryStatement statement)  {
    StringBuffer result = new StringBuffer();
    result.append("try ");
    result.append(toCode(statement.getStatement()));
    Iterator iter = statement.getCatchClauses().iterator();
    while(iter.hasNext()) {
      result.append("\n");
      result.append(startLine());
      result.append(toCodeCatchClause((CatchClause)iter.next()));
    }
    if(statement.getFinallyClause() != null) {
      result.append(toCodeFinally(statement.getFinallyClause()));
    }
    return result.toString();
  }
  
  public String toCodeCatchClause(CatchClause cc)  {
    return "catch ("+toCodeVariable(cc.getExceptionParameter()) + ") " + toCode(cc.statement());
  }
  
  public String toCodeFinally(FinallyClause cc)  {
    return "finally " + toCode(cc.statement());
  }
  
  public boolean isThrow(Element element) {
    return element instanceof ThrowStatement;
  }
  
  public String toCodeThrow(ThrowStatement ts)  {
    return "throw "+toCode(ts.getExpression())+";";
  }
  
  public boolean isSynchronized(Element element) {
    return element instanceof SynchronizedStatement;
  }
  
  public String toCodeSynchronized(SynchronizedStatement ts)  {
    return "synchronized("+toCode(ts.expression())+") "+toCode(ts.getStatement());
  }
  
  public boolean isSwitch(Element element) {
    return element instanceof SwitchStatement;
  }
  
  public String toCodeSwitch(SwitchStatement st)  {
    final StringBuffer result = new StringBuffer();
    result.append("switch(" + toCode(st.getExpression()) + ") {\n");
    indent();
    for(SwitchCase o:st.getSwitchCases()) {
      result.append(startLine());
      result.append(toCodeSwitchCase((SwitchCase)o));
      result.append("\n");
    }
    undent();
    result.append(startLine());
    result.append("}");
    return result.toString();
  }
  
  public String toCodeSwitchCase(SwitchCase sc)  {
    final StringBuffer result = new StringBuffer();
    result.append(startLine());
    result.append(toCodeSwitchLabel(sc.getLabel()));
    result.append("\n");
    indent();
    for(Element o: sc.statements()) {
      result.append(toCode((Element)o));
    }
    undent();
    return result.toString();
  }
  
  public String toCodeSwitchLabel(SwitchLabel sl)  {
    if(sl instanceof DefaultLabel) {
      return "default:";
    }
    else {
      return "case " + toCode(((CaseLabel)sl).getExpression())+":";
    }
  }
  
  public boolean isStatementExprList(Element element) {
    return element instanceof StatementExprList;
  }
  
  public String toCodeStatementExprList(StatementExprList sel)  {
    StringBuffer result = new StringBuffer();
    Iterator iter = sel.statements().iterator();
    while(iter.hasNext()) {
      result.append(toCode(((StatementExpression)iter.next()).getExpression()));
      if(iter.hasNext() ) {
        result.append(", ");
      }
    }
    return result.toString();
  }
  
  public boolean isReturn(Element element) {
    return element instanceof ReturnStatement;
  }
  
  public String toCodeReturn(ReturnStatement ts)  {
    return "return "+toCode(ts.getExpression())+";";
  }
  
  public boolean isLocalVariableDeclarator(Element element) {
    return element instanceof LocalVariableDeclarator;
  }

  public String toCodeLocalVariableDeclarator(LocalVariableDeclarator local)  {
    return toCodeVariableDeclarator(local) + ";";
  }
  
  public boolean isMemberVariableDeclarator(Element element) {
    return element instanceof MemberVariableDeclarator;
  }

  public String toCodeMemberVariableDeclarator(MemberVariableDeclarator local)  {
    return startLine()+toCodeVariableDeclarator(local) + ";";
  }

  public String toCodeVariableDeclarator(VariableDeclarator local)  {
    final StringBuffer result = new StringBuffer();
    List modifiers = local.modifiers();
    if (modifiers.size() != 0) {
      new Visitor() {
        public void visit(Object o) {
          result.append(toCodeModifier((Modifier)o) + " ");
        }
      }.applyTo(modifiers);
    }
    result.append(toCode(local.typeReference()));
    result.append(" ");
    boolean first = true;
    for(VariableDeclaration element: local.variableDeclarations()) {
    	//    LocalVariable variable = (LocalVariable)element;
    	if (!first) {
    		result.append(", ");
    	}
    	else {
    		first = false;
    	}
    	result.append(element.name());
    	Expression initCode = element.initialization();
    	if (initCode != null) {
    		result.append(" = ");
    		result.append(toCode(initCode));
    	}

    }
    return result.toString();
  }
  
  public boolean isLocalClass(Element element) {
    return element instanceof LocalClassStatement;
  }
  
  public String toCodeLocalClass(LocalClassStatement local)  {
    return toCode(local.getType());
  }
  
  public boolean isLabeledStatement(Element element) {
    return element instanceof LabeledStatement;
  }
  
  public String toCodeLabeledStatement(LabeledStatement local)  {
    return local.getLabel() +": "+toCode(local.getStatement());
  }
  
  public boolean isFor(Element element) {
    return element instanceof ForStatement;
  }
  
  public String toCodeFor(ForStatement statement)  {
    return "for "+toCode(statement.forControl()) + toCode(statement.getStatement());
  }
  
  public boolean isSimpleForControl(Element element) {
  	return element instanceof SimpleForControl;
  }
  
  public String toCodeSimpleForControl(SimpleForControl control)  {
  	return "("+toCodeForInit((Element)control.getForInit())+"; "+toCode(control.condition())+"; "+toCode(control.update())+") ";
  }
  
  public boolean isEnhancedForControl(Element element) {
  	return element instanceof EnhancedForControl;
  }

  public String toCodeEnhancedForControl(EnhancedForControl control)  {
  	return "("+toCodeForInit((Element)control.variableDeclarator())+": "+toCode(control.collection())+") ";
  }

  public String toCodeForInit(Element element)  {
    if(element instanceof LocalVariableDeclarator) {
      return toCodeVariableDeclarator((LocalVariableDeclarator)element);
    } else {
      return toCode(element);
    }
  }
  
  public boolean isEmpty(Element element) {
    return element instanceof EmptyStatement;
  }
  
  public String toCodeEmptyStatement(EmptyStatement empty) {
    return ";";
  }
  
  public boolean isDo(Element element) {
    return element instanceof DoStatement;
  }
  
  public String toCodeDo(DoStatement statement)  {
    return "do "+toCode(statement.getStatement())+ "while("+toCode(statement.condition())+");";
  }
  
  
  /***************
   * EXPRESSIONS *
   ***************/
  
  public boolean isConstructorInvocation(Element element) {
    return element instanceof ConstructorInvocation;
  }
  
  public String toCodeConstructorInvocation(ConstructorInvocation inv)  {
    StringBuffer result = new StringBuffer();
    if(inv.getTarget() != null) {
      result.append(toCode(inv.getTarget()));
      result.append(".");
    }
    result.append("new ");
    result.append(toCode(inv.getTypeReference()));
    result.append(getActualArgs(inv));
    if(inv.getAnonymousInnerType() != null) {
      result.append(toCode(inv.getAnonymousInnerType()));
    }
    return result.toString();
  }

  protected String getActualArgsOpeningBrace() {
	  return "(";
  }

  protected String getActualArgsClosingBrace() {
	  return ")";
  }
  
  public String getActualArgs(MethodInvocation inv)  {
    StringBuffer result = new StringBuffer();
    result.append(getActualArgsOpeningBrace());
    Iterator<Expression> iter = inv.getActualParameters().iterator();
    while(iter.hasNext()) {
      Expression expr = iter.next();
      result.append(toCode(expr));
      if(iter.hasNext()) {
        result.append(", ");
      }
    }
    result.append(getActualArgsClosingBrace());
    return result.toString();
  }
  
  public boolean isCondAnd(Element element) {
    return element instanceof ConditionalAndExpression;
  }
  
  public String toCodeCondAnd(ConditionalAndExpression cae)  {
    return "(" + toCode(cae.getFirst())+" && " + toCode(cae.getSecond()) +")";
  }
  
  public boolean isCondOr(Element element) {
    return element instanceof ConditionalOrExpression;
  }
  
  public String toCodeCondOr(ConditionalOrExpression cae)  {
    return "(" + toCode(cae.getFirst())+" || " + toCode(cae.getSecond()) +")";
  }
  
  public boolean isInfixInvocation(Element element) {
    return element instanceof InfixOperatorInvocation;
  }
  
  public String toCodeInfixInvocation(InfixOperatorInvocation inv)  {
    return "(" + toCode(inv.getTarget())+") " + inv.name()+ " (" + toCode((Element)inv.getActualParameters().get(0)) +")";
  }
  
  public boolean isPrefixInvocation(Element element) {
    return element instanceof PrefixOperatorInvocation;
  }
  
  public String toCodePrefixInvocation(PrefixOperatorInvocation inv)  {
    return inv.name()+"("+toCode(inv.getTarget())+")";
  }
  
  public boolean isPostfixInvocation(Element element) {
    return element instanceof PostfixOperatorInvocation;
  }
  
  public String toCodePostfixInvocation(PostfixOperatorInvocation inv)  {
    return toCode(inv.getTarget()) + inv.name();
  }
  
  public boolean isClassLiteral(Element element) {
    return element instanceof ClassLiteral;
  }
  
  public String toCodeClassLiteral(ClassLiteral literal)  {
    return toCode(literal.target())+".class";
  }
  
  public boolean isThisLiteral(Element element) {
    return element instanceof ThisLiteral;
  }
  
  public String toCodeThisLiteral(ThisLiteral literal)  {
    if(literal.getTypeReference() != null) {
      return toCode(literal.getTypeReference())+".this";
    } else {
      return "this";
    }
  }
  
  public boolean isLiteral(Element element) {
    return element instanceof Literal;
  }
  
  public String toCodeLiteral(Literal literal) {
    return literal.getValue();
  }
  
  public boolean isNamedTarget(Element element) {
    return element instanceof NamedTarget;
  }
  
  public String toCodeNamedTarget(NamedTarget nt)  {
    StringBuffer result = new StringBuffer();
    if(nt.getTarget() != null) {
      result.append(toCode(nt.getTarget()));
      result.append(".");
    }
    result.append(nt.name());
    return result.toString();
  }
  
  public boolean isRegulaMethodInvocation(Element element) {
    return element instanceof RegularMethodInvocation;
  }
  
  public String toCodeRegularMethodInvocation(RegularMethodInvocation inv)  {
    StringBuffer result = new StringBuffer();
    if(inv.getTarget() != null) {
      result.append(toCode(inv.getTarget()));
      result.append(".");
    }
    appendTypeParameters(inv.typeArguments(), result);
    result.append(inv.name());
    result.append(getActualArgs(inv));
    return result.toString();
  }
  
  public boolean isNamedTargetRef(Element element) {
    return element instanceof NameExpression;
  }
  
  public String toCodeNamedTargetRef(NameExpression var)  {
    CrossReferenceTarget target = var.getTarget();
    if(target != null) {
		  return toCode(target)+"."+var.name();
    } else {
    	return var.name();
    }
  }

  public boolean isVarRef(Element element) {
    return element instanceof VariableReference;
  }
  
  public String toCodeVarRef(VariableReference var)  {
    return toCode(var.getTarget());
  }
  
  /*********
   * JNOME *
   *********/
  
  public boolean isThisConstructorDelegation(Element element) {
    return element instanceof ThisConstructorDelegation;
  }
  
  public String toCodeThisConstructorDelegation(ThisConstructorDelegation deleg)  {
    return "this" + getActualArgs(deleg);
  }
  
  public boolean isSuperTarget(Element element) {
    return element instanceof SuperTarget;
  }
  
  public String toCodeSuperTarget(SuperTarget nt)  {
    StringBuffer result = new StringBuffer();
    CrossReferenceTarget target = nt.getTarget();
		if(target != null) {
      result.append(toCode(target));
      result.append(".");
    }
    result.append("super");
    return result.toString();
  }
  
  public boolean isSuperConstructorDelegation(Element element) {
    return element instanceof SuperConstructorDelegation;
  }
  
  public String toCodeSuperConstructorDelegation(SuperConstructorDelegation deleg)  {
    return "super" + getActualArgs(deleg);
  }
  
  public boolean isInstanceOf(Element element) {
    return element instanceof InstanceofExpression;
  }
  
  public String toCodeInstanceOf(InstanceofExpression ioe)  {
    return "(" + toCode(ioe.getExpression()) + " instanceof " + toCode(ioe.getTypeReference())+")";
  }
  
  public boolean isDimInit(Element element) {
    return element instanceof DimensionInitializer;
  }
  
  public String toCodeDimInit(DimensionInitializer init)  {
    return "["+(init.getExpression() != null ? toCode(init.getExpression()) : "")+"]";
  }
  
  public boolean isCondExpr(Element element) {
    return element instanceof ConditionalExpression;
  }
  
  public String toCodeCondExpr(ConditionalExpression ce)  {
    return "(" +toCode(ce.getCondition())+" ? "+toCode(ce.getFirst()) + " : "+ toCode(ce.getSecond())+")";
  }
  
  public boolean isCast(Element element) {
    return element instanceof ClassCastExpression;
  }
  
  public String toCodeCast(ClassCastExpression cc)  {
    return "(("+toCode(cc.getTypeReference())+")" + toCode(cc.getExpression()) +")";
  }
  
  public boolean isArrayInit(Element element) {
    return element instanceof ArrayInitializer;
  }
  
  public String toCodeArrayInit(ArrayInitializer init)  {
    StringBuffer result = new StringBuffer();
    result.append("{");
    Iterator iter = init.getVariableInitializers().iterator();
    while(iter.hasNext()) {
      result.append(toCode((Element)iter.next()));
      if(iter.hasNext()) {
        result.append(", ");
      }
    }
    result.append("}");
    return result.toString();
  }
  
  public boolean isArrayCreation(Element element) {
    return element instanceof ArrayCreationExpression;
  }
  
  public String toCodeArrayCreation(ArrayCreationExpression expr)  {
    StringBuffer result = new StringBuffer();
    result.append("new ");
    result.append(toCode(expr.getTypeReference()));
    Collection inits = expr.getDimensionInitializers();
    Iterator iter = inits.iterator();
    while(iter.hasNext()) {
      //result.append(toCodeDimInit((DimensionInitializer)iter.next()));
    	result.append(toCode((ArrayIndex)iter.next()));
    }
    if(expr.getInitializer() != null) {
      result.append(toCodeArrayInit(expr.getInitializer()));
    }
    return result.toString();
  }
  
  public boolean isArrayAccess(Element element) {
    return element instanceof ArrayAccessExpression;
  }
  
  public String toCodeArrayAccess(ArrayAccessExpression expr)  {
    StringBuffer result = new StringBuffer();
    result.append(toCode(expr.getTarget()));
    Iterator iter = expr.getIndices().iterator();
    while(iter.hasNext()) {
      //result.append("[");
      result.append(toCode((Element)iter.next()));
      //result.append("]");
    }
    return result.toString();
  }
  
  public boolean isAssignment(Element element) {
    return element instanceof AssignmentExpression;
  }
  
  public String toCodeAssignment(AssignmentExpression expr)  {
    return toCode((Element)expr.getVariableExpression()) + " = " + toCode(expr.getValue());
  }
  
	public boolean isEmptyArrayIndex(Element element) {
		return element instanceof EmptyArrayIndex;
	}
	
	public String toCodeEmptyArrayIndex(EmptyArrayIndex ai){
		StringBuffer result = new StringBuffer();
		int dimension = ai.getDimension();
		result.append("[");
		while(dimension > 1){
			result.append(",");
			dimension--;
		}
		result.append("]");
		return result.toString();
	}
	
	public boolean isFilledArrayIndex(Element element) {
		return element instanceof FilledArrayIndex;
	}
	
	
	public String toCodeFilledArrayIndex(FilledArrayIndex ai) {
		StringBuffer result = new StringBuffer();
		result.append("[");
		List expressions = ai.getIndices();
		result.append(toCode((Expression)expressions.get(0)));
		expressions.remove(0);
		while(expressions.size() > 0){
			result.append(",");
			result.append(toCode((Expression)expressions.get(0)));
			expressions.remove(0);
		}
		result.append("]");
		return result.toString();
	}


  public static void writeCode(Arguments arguments) throws IOException {
    JavaCodeWriter writer = new JavaCodeWriter(2);
    List<Type> types = arguments.getTypes();
    new SafePredicate<Type>() {
    	public boolean eval(Type t) {
    		return t.is(((Java)t.language()).PUBLIC) == Ternary.TRUE;
    	}
    }.filter(types);
    int i = 1;
    for(Type type:types) {
      String fileName = type.name()+".java";
      String packageFQN = type.namespace().getFullyQualifiedName();
      String relDirName = packageFQN.replace('.', File.separatorChar);
      File out = new File(arguments.getOutputDirName()+File.separatorChar + relDirName + File.separatorChar + fileName);
      
      System.out.println(i + " Writing: "+out.getAbsolutePath());
      
      File parent = out.getParentFile();
      parent.mkdirs();
      out.createNewFile();
      FileWriter fw = new FileWriter(out);
      fw.write(writer.toCode((Element)type.parent()));
      fw.close();
      
      
      i++;
    }
  }

	@Override
	public JavaCodeWriter clone() {
		return new JavaCodeWriter(_tabSize);
	}
}
