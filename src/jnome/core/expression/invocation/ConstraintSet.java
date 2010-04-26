/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.expression.Invocation;
import chameleon.core.method.Method;
import chameleon.oo.type.generics.TypeParameter;

public abstract class ConstraintSet<C extends Constraint> {
	
	public ConstraintSet(Invocation invocation, Method invokedMethod) {
		_invocation = invocation;
		_invokedGenericMethod = invokedMethod;
		_typeParameters = invokedMethod.typeParameters();
	}
	
	private OrderedMultiAssociation<ConstraintSet<C>, C> _constraints = new OrderedMultiAssociation<ConstraintSet<C>, C>(this);
	
	public List<? extends C> constraints() {
		return _constraints.getOtherEnds();
	}
	
	public void add(C constraint) {
		if(constraint != null) {
			_constraints.add(constraint.parentLink());
		}
	}
	
	public void addAll(Collection<C> constraints) {
		for(C constraint: constraints) {
			add(constraint);
		}
	}
	
	public void remove(C constraint) {
		if(constraint != null) {
			_constraints.remove(constraint.parentLink());
		}
	}
	
	
//	public void replace(C oldConstraint, C newConstraint) {
//		if(oldConstraint != null && newConstraint != null) {
//			_constraints.replace(oldConstraint.parentLink(), newConstraint.parentLink());
//		}
//	}
	
	public List<TypeParameter> typeParameters() {
		return _typeParameters;
	}
	
	private List<TypeParameter> _typeParameters;
	
  public Invocation invocation() {
  	return _invocation;
  }
  
  private Invocation _invocation;
  
  public Method invokedGenericMethod() {
  	return _invokedGenericMethod;
  }
  
  private Method _invokedGenericMethod;

}