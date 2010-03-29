/**
 * 
 */
package jnome.core.expression.invocation;


import jnome.core.language.Java;

import org.rejuse.association.SingleAssociation;

public class Constraint<C extends Constraint, S extends ConstraintSet<C>> {
	
	private SingleAssociation<C, S> _parentLink = new SingleAssociation<C, S>((C) this);
	
	public SingleAssociation<C, S> parentLink() {
		return _parentLink;
	}
	
	public S parent() {
		return _parentLink.getOtherEnd();
	}
	
	// resolve()
}