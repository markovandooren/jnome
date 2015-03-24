/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;


import org.aikodi.chameleon.exception.ChameleonProgrammerException;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.rejuse.association.SingleAssociation;

public class Constraint<C extends Constraint, S extends ConstraintSet<C>> {
	
	private SingleAssociation<C, S> _parentLink = new SingleAssociation<C, S>((C) this);
	
	public SingleAssociation<C, S> parentLink() {
  	if(_parentLink != null) {
      return _parentLink;
  	} else {
  		throw new ChameleonProgrammerException("Invoking parentLink() on automatic derivation");
  	}
	}
	
	public S parent() {
  	if(_parentLink != null) {
      return _parentLink.getOtherEnd();
  	} else {
  		return _parent;
  	}
	}
	
  private S _parent;
	
	public final void setUniParent(S parent) {
  	if(_parentLink != null) {
  		_parentLink.connectTo(null);
  	}
  	if(parent != null) {
  	  _parentLink = null;
  	} else {
  		_parentLink = new SingleAssociation<C, S>((C) this);
  	}
  	_parent = parent;
  }
}
