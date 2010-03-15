/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;


import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.type.generics.TypeParameter;

public class ConstraintSet {
	
	private OrderedMultiAssociation<ConstraintSet, Constraint> _constraints = new OrderedMultiAssociation<ConstraintSet, Constraint>(this);
	
	public List<Constraint> constraints() {
		return _constraints.getOtherEnds();
	}
	
	public void add(Constraint constraint) {
		if(constraint != null) {
			_constraints.add(constraint.parentLink());
		}
	}
	
	public void remove(Constraint constraint) {
		if(constraint != null) {
			_constraints.remove(constraint.parentLink());
		}
	}
	
	
	public void replace(Constraint oldConstraint, Constraint newConstraint) {
		if(oldConstraint != null && newConstraint != null) {
			_constraints.replace(oldConstraint.parentLink(), newConstraint.parentLink());
		}
	}
	
	public List<TypeParameter> typeParameters() {
		return _typeParameters;
	}
	
	private List<TypeParameter> _typeParameters;
}