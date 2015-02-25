package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import org.aikodi.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;

public class JavaPostfixOperatorInvocation extends PostfixOperatorInvocation {

	public JavaPostfixOperatorInvocation(String name, CrossReferenceTarget target) {
		super(name,target);
	}
	
	@Override
	public DeclarationSelector<PostfixOperator> createSelector() {
  	return new JavaMethodSelector<PostfixOperator>(this,PostfixOperator.class);
	}
}