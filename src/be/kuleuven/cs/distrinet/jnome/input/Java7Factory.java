package be.kuleuven.cs.distrinet.jnome.input;

import org.aikodi.chameleon.aspect.oo.weave.factory.OOFactory;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.statement.Statement;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.statement.FinallyClause;
import org.aikodi.chameleon.support.statement.ReturnStatement;
import org.aikodi.chameleon.support.statement.TryStatement;

import be.kuleuven.cs.distrinet.jnome.core.enumeration.EnumType;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaMethod;
import be.kuleuven.cs.distrinet.jnome.core.modifier.JavaConstructor;
import be.kuleuven.cs.distrinet.jnome.core.namespacedeclaration.JavaNamespaceDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaFormalParameterType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaLazyFormalAlias;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;

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
	  return new JavaFormalParameterType(name, upperBound, formalTypeParameter);
	}
	
	@Override
	public Type createLazyTypeVariable(String name, FormalTypeParameter formalTypeParameter) {
    return new JavaLazyFormalAlias(name, formalTypeParameter);
	}
}
