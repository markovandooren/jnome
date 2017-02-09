package org.aikodi.java.input;

import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.expression.ExpressionFactory;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.expression.NameExpression;
import org.aikodi.chameleon.oo.expression.NamedTarget;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.chameleon.support.expression.ConditionalExpression;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import org.aikodi.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import org.aikodi.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import org.aikodi.java.core.expression.JavaAssignmentExpression;
import org.aikodi.java.core.expression.JavaClassCastExpression;
import org.aikodi.java.core.expression.JavaConditionalExpression;
import org.aikodi.java.core.expression.JavaNameExpression;
import org.aikodi.java.core.expression.JavaNamedTarget;
import org.aikodi.java.core.expression.invocation.JavaInfixOperatorInvocation;
import org.aikodi.java.core.expression.invocation.JavaMethodInvocation;
import org.aikodi.java.core.expression.invocation.JavaPostfixOperatorInvocation;
import org.aikodi.java.core.expression.invocation.JavaPrefixOperatorInvocation;

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
	public ConditionalExpression createConditionalExpression(Expression condition, Expression firstOperand, Expression secondOperand) {
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
	
	@Override
	public NamedTarget createNamedTarget(String fqn) {
		return new JavaNamedTarget(fqn, this);
	}
	
	@Override
	public NamedTarget createNamedTarget(String fqn, CrossReferenceTarget target) {
		return new JavaNamedTarget(fqn, target);
	}
	@Override
	public AssignmentExpression createAssignmentExpression(Expression variableReference, Expression value) {
	  return new JavaAssignmentExpression(variableReference, value);
	}
	
	public Expression createClassCastExpression(TypeReference tref, Expression casted) {
		return new JavaClassCastExpression(tref, casted);
	}
}
