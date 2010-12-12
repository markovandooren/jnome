/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.Collection;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.predicate.TypePredicate;

import chameleon.core.expression.MethodInvocation;
import chameleon.core.method.Method;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.TypeParameter;

public abstract class ConstraintSet<C extends Constraint> {
	
	public ConstraintSet(MethodInvocation invocation, Method invokedMethod) {
		_invocation = invocation;
		_invokedGenericMethod = invokedMethod;
		List<TypeParameter> typeParameters = invokedMethod.typeParameters();
		new TypePredicate<TypeParameter, FormalTypeParameter>(FormalTypeParameter.class).filter(typeParameters);
		_typeParameters = typeParameters;
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
	
  public MethodInvocation invocation() {
  	return _invocation;
  }
  
  private MethodInvocation _invocation;
  
  public Method invokedGenericMethod() {
  	return _invokedGenericMethod;
  }
  
  private Method _invokedGenericMethod;

}