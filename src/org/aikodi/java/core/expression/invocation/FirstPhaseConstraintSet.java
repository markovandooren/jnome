package org.aikodi.java.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.method.MethodHeader;

import com.google.common.collect.ImmutableList;


public class FirstPhaseConstraintSet extends ConstraintSet<FirstPhaseConstraint> {


	public FirstPhaseConstraintSet(MethodInvocation invocation, MethodHeader invokedMethod) {
		super(invocation,invokedMethod); 
	}

	public SecondPhaseConstraintSet secondPhase() throws LookupException {
		SecondPhaseConstraintSet result = new SecondPhaseConstraintSet(invocation(), invokedGenericMethod(),this);
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
	
	private List<EQConstraint> _generatedEQ = new ArrayList<>();
	
	private List<GGConstraint> _generatedGG = new ArrayList<>();
	
	public List<GGConstraint> generatedGG() {
		return ImmutableList.copyOf(_generatedGG);
	}

	public List<EQConstraint> generatedEQ() {
		return ImmutableList.copyOf(_generatedEQ);
	}
	
	void addGenerated(GGConstraint constraint) {
		_generatedGG.add(constraint);
	}
	
	void addGenerated(EQConstraint constraint) {
		_generatedEQ.add(constraint);
	}
	
}
