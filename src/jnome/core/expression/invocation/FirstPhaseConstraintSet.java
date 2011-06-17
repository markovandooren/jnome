package jnome.core.expression.invocation;

import chameleon.core.expression.MethodInvocation;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.MethodHeader;


public class FirstPhaseConstraintSet extends ConstraintSet<FirstPhaseConstraint> {


	public FirstPhaseConstraintSet(MethodInvocation invocation, MethodHeader invokedMethod) {
		super(invocation,invokedMethod); 
	}

	public SecondPhaseConstraintSet secondPhase() throws LookupException {
		SecondPhaseConstraintSet result = new SecondPhaseConstraintSet(invocation(), invokedGenericMethod());
		for(FirstPhaseConstraint constraint: constraints()) {
			result.addAll(constraint.process());
		}
		return result;
	}
	
	public TypeAssignmentSet resolve() throws LookupException {
		SecondPhaseConstraintSet second = secondPhase();
		second.process();
		return second.assignments();
	}
}
