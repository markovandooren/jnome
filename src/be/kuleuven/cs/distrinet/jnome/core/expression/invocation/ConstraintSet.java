/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.Collection;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.rejuse.association.OrderedMultiAssociation;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class ConstraintSet<C extends Constraint> {
	
	public ConstraintSet(MethodInvocation invocation, MethodHeader invokedMethod) {
		_invocation = invocation;
		_invokedGenericMethod = invokedMethod;
		List<TypeParameter> typeParameters = invokedMethod.typeParameters();
		new TypePredicate<FormalTypeParameter>(FormalTypeParameter.class).filter(typeParameters);
		_typeParameters = ImmutableList.copyOf(typeParameters);
	}
	
	private OrderedMultiAssociation<ConstraintSet<C>, C> _constraints = new OrderedMultiAssociation<ConstraintSet<C>, C>(this);
	
	public List<? extends C> constraints() {
		return _constraints.getOtherEnds();
	}
	
	public <T extends C> T first(Class<T> kind) {
		int size = _constraints.size();
		for(int i = 1; i <= size;i++) {
		  C elementAt = _constraints.elementAt(i);
			if(kind.isInstance(elementAt)) {
		  	return (T) elementAt;
		  }
		}
		return null;
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
		// Immutable so no need to clone.
		return _typeParameters;
	}
	
	private ImmutableList<TypeParameter> _typeParameters;
	
  public MethodInvocation invocation() {
  	return _invocation;
  }
  
  private MethodInvocation _invocation;
  
  public MethodHeader invokedGenericMethod() {
  	return _invokedGenericMethod;
  }
  
  private MethodHeader _invokedGenericMethod;

}
