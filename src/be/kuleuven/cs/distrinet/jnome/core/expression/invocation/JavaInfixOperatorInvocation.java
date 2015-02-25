package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperator;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;

public class JavaInfixOperatorInvocation extends InfixOperatorInvocation {

	public JavaInfixOperatorInvocation(String name, CrossReferenceTarget target) {
		super(name,target);
	}
	
	@Override
	public DeclarationSelector<InfixOperator> createSelector() {
  	return new JavaMethodSelector<InfixOperator>(this,InfixOperator.class);
	}

}
