package jnome.core.expression.invocation;

import chameleon.core.lookup.LookupException;


public class FirstPhaseConstraintSet extends ConstraintSet<FirstPhaseConstraint> {



	public SecondPhaseConstraintSet process() throws LookupException {
		SecondPhaseConstraintSet result = new SecondPhaseConstraintSet();
		for(FirstPhaseConstraint constraint: constraints()) {
			result.addAll(constraint.process());
		}
		return result;
	}
}
