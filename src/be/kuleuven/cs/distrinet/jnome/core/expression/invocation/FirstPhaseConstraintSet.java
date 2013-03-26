package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;


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
