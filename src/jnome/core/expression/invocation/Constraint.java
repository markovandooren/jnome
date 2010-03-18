/**
 * 
 */
package jnome.core.expression.invocation;


import org.rejuse.association.SingleAssociation;

public class Constraint<C extends Constraint> {
	
	private SingleAssociation<C, ConstraintSet<C>> _parentLink = new SingleAssociation<C, ConstraintSet<C>>((C) this);
	
	public SingleAssociation<C, ConstraintSet<C>> parentLink() {
		return _parentLink;
	}
	
	public ConstraintSet parent() {
		return _parentLink.getOtherEnd();
	}
	// resolve()
}