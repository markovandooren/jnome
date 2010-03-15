/**
 * 
 */
package jnome.core.expression.invocation;


import org.rejuse.association.SingleAssociation;

public class Constraint {
	
	private SingleAssociation<Constraint, ConstraintSet> _parentLink = new SingleAssociation<Constraint, ConstraintSet>(this);
	
	public SingleAssociation<Constraint, ConstraintSet> parentLink() {
		return _parentLink;
	}
	
	public ConstraintSet parent() {
		return _parentLink.getOtherEnd();
	}
	// resolve()
}