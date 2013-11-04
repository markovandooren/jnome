package be.kuleuven.cs.distrinet.jnome.input;

import be.kuleuven.cs.distrinet.chameleon.aspect.oo.weave.factory.OOFactory;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.statement.FinallyClause;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.TryStatement;
import be.kuleuven.cs.distrinet.jnome.core.enumeration.EnumType;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaInfixOperatorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaMethodInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaPostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaPrefixOperatorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaNormalMethod;
import be.kuleuven.cs.distrinet.jnome.core.modifier.JavaConstructor;
import be.kuleuven.cs.distrinet.jnome.core.namespacedeclaration.JavaNamespaceDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;

public class JavaFactory extends ObjectOrientedFactory implements OOFactory {
	
	public JavaFactory() {
		
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
		return new JavaNormalMethod(header);
	}
	
	@Override
	public NamespaceDeclaration createNamespaceDeclaration(CrossReference<Namespace> cref) {
		return new JavaNamespaceDeclaration(cref);
	}
	
	@Override
	public JavaFactory clone() {
		return new JavaFactory();
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
	
}
