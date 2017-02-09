package org.aikodi.java.input;

import java.util.List;

import org.aikodi.chameleon.aspect.oo.weave.factory.OOFactory;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.statement.Statement;
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.UnionType;
import org.aikodi.chameleon.oo.type.generics.ConstrainedType;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.statement.FinallyClause;
import org.aikodi.chameleon.support.statement.ReturnStatement;
import org.aikodi.chameleon.support.statement.TryStatement;
import org.aikodi.java.core.enumeration.EnumType;
import org.aikodi.java.core.method.JavaMethod;
import org.aikodi.java.core.modifier.JavaConstructor;
import org.aikodi.java.core.namespacedeclaration.JavaNamespaceDeclaration;
import org.aikodi.java.core.type.JavaConstrainedType;
import org.aikodi.java.core.type.JavaInstantiatedParameterType;
import org.aikodi.java.core.type.JavaIntersectionType;
import org.aikodi.java.core.type.JavaLazyFormalAlias;
import org.aikodi.java.core.type.JavaLazyInstantiatedAlias;
import org.aikodi.java.core.type.JavaTypeVariable;
import org.aikodi.java.core.type.JavaUnionType;
import org.aikodi.java.core.type.RegularJavaType;

public class Java7Factory extends ObjectOrientedFactory implements OOFactory {
	
	public Java7Factory() {
		
	}
	
	public RegularType createRegularType(String name) {
		return new RegularJavaType(name);
	}
	
	public RegularType createEnumType(String name) {
		return new EnumType(name);
	}
	
	@Override
	public Method transformToConstructor(Method m) {
		m.addModifier(new JavaConstructor());
		return m;
	}
	
	@Override
	public NormalMethod createNormalMethod(MethodHeader header) {
		return new JavaMethod(header);
	}
	
	@Override
	public NamespaceDeclaration createNamespaceDeclaration(CrossReference<Namespace> cref) {
		return new JavaNamespaceDeclaration(cref);
	}
	
	@Override
	public Java7Factory clone() {
		return new Java7Factory();
	}

	@Override
	public Statement createReturn(Expression expression) {
		return new ReturnStatement(expression);
	}

	@Override
	public Statement createTryFinally(Statement tr, Statement fin) {
		TryStatement result = new TryStatement(tr);
		result.setFinallyClause(new FinallyClause(fin));
		return result;
	}
	
	@Override
	public InheritanceRelation createDefaultInheritanceRelation(Type type) {
		return null;
	}
	
	@Override
	public Type createTypeVariable(String name, Type upperBound, FormalTypeParameter formalTypeParameter) {
	  return new JavaTypeVariable(name, upperBound, formalTypeParameter);
	}
	
	@Override
	public Type createLazyTypeVariable(String name, FormalTypeParameter formalTypeParameter) {
    return new JavaLazyFormalAlias(name, formalTypeParameter);
	}
	
	@Override
  public Type createInstantiatedTypeVariable(String name, Type upperBound, TypeParameter capturedTypeParameter) {
	  return new JavaInstantiatedParameterType(name, upperBound, capturedTypeParameter);
	}
	
	@Override
	public Type createLazyInstantiatedTypeVariable(String name, TypeParameter capturedTypeParameter) {
    return new JavaLazyInstantiatedAlias(name, capturedTypeParameter);
	}
	
	@Override
	protected IntersectionType doCreateIntersectionType(List<Type> types) {
	  return new JavaIntersectionType(types);
	}
	
	@Override
	protected UnionType doCreateUnionType(List<Type> types) {
	  return new JavaUnionType(types);
	}
	
	public ConstrainedType reallyCreateConstrainedType(Type lowerBound, Type upperBound) {
		return new JavaConstrainedType(lowerBound, upperBound);
	}

}
