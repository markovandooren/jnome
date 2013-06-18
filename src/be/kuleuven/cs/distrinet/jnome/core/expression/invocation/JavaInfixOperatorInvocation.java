package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperator;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;

public class JavaInfixOperatorInvocation extends InfixOperatorInvocation {

	public JavaInfixOperatorInvocation(String name, CrossReferenceTarget target) {
		super(name,target);
	}
	
	@Override
	public DeclarationSelector<InfixOperator> createSelector() {
  	return new JavaMethodSelector<InfixOperator>(this,InfixOperator.class);
	}

}
