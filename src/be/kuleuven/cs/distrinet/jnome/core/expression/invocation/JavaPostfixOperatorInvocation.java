package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;

public class JavaPostfixOperatorInvocation extends PostfixOperatorInvocation {

	public JavaPostfixOperatorInvocation(String name, CrossReferenceTarget target) {
		super(name,target);
	}
	
	@Override
	public DeclarationSelector<PostfixOperator> createSelector() {
  	return new JavaMethodSelector<PostfixOperator>(this,PostfixOperator.class);
	}
}