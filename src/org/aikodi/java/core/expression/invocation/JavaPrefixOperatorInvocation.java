package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import org.aikodi.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;

public class JavaPrefixOperatorInvocation extends PrefixOperatorInvocation {

	public JavaPrefixOperatorInvocation(String name, CrossReferenceTarget target) {
		super(name,target);
	}
	
	@Override
	public DeclarationSelector<PrefixOperator> createSelector() {
  	return new JavaMethodSelector<PrefixOperator>(this,PrefixOperator.class);
	}
}