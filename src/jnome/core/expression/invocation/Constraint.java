/**
 * 
 */
package jnome.core.expression.invocation;


import jnome.core.language.Java;

import org.rejuse.association.SingleAssociation;

public class Constraint<C extends Constraint> {
	
	private SingleAssociation<C, ConstraintSet<C>> _parentLink = new SingleAssociation<C, ConstraintSet<C>>((C) this);
	
	public SingleAssociation<C, ConstraintSet<C>> parentLink() {
		return _parentLink;
	}
	
	public ConstraintSet<C> parent() {
		return _parentLink.getOtherEnd();
	}
	
	// resolve()
}