package be.kuleuven.cs.distrinet.jnome.input;

import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.ExpressionFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NameExpression;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.JavaConditionalExpression;
import be.kuleuven.cs.distrinet.jnome.core.expression.JavaNameExpression;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaInfixOperatorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaMethodInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaPostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaPrefixOperatorInvocation;

public class JavaExpressionFactory extends ExpressionFactory {

	
	@Override
	public JavaExpressionFactory clone() {
		return new JavaExpressionFactory();
	}
	
	@Override
	public MethodInvocation createInvocation(String name, CrossReferenceTarget target) {
		return new JavaMethodInvocation(name, target);
	}

	@Override
	public InfixOperatorInvocation createInfixOperatorInvocation(String name, CrossReferenceTarget target) {
		return new JavaInfixOperatorInvocation(name, target);
	}
	
	@Override
	public PrefixOperatorInvocation createPrefixOperatorInvocation(String name, CrossReferenceTarget target) {
		return new JavaPrefixOperatorInvocation(name, target);
	}
	
	@Override
	public PostfixOperatorInvocation createPostfixOperatorInvocation(String name, CrossReferenceTarget target) {
		return new JavaPostfixOperatorInvocation(name, target);
	}

	@Override
	public Expression createConditionalExpression(Expression condition, Expression firstOperand, Expression secondOperand) {
		return new JavaConditionalExpression(condition, firstOperand, secondOperand);
	}
	
	@Override
	public NameExpression createNameExpression(String name) {
		return new JavaNameExpression(name);
	}
	
	@Override
	public NameExpression createNameExpression(String name,	CrossReferenceTarget target) {
		return new JavaNameExpression(name,target);
	}
}
