package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;

public class JavaPrefixOperatorInvocation extends PrefixOperatorInvocation {

	public JavaPrefixOperatorInvocation(String name, CrossReferenceTarget target) {
		super(name,target);
	}
	
	@Override
	public DeclarationSelector<PrefixOperator> createSelector() {
  	return new JavaMethodSelector<PrefixOperator>(this,PrefixOperator.class);
	}
}