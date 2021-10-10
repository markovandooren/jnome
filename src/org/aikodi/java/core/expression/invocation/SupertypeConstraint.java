/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.oo.type.BoxableTypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

public class SupertypeConstraint extends SecondPhaseConstraint {

	public SupertypeConstraint(TypeParameter param, BoxableTypeReference type) {
		super(param,type);
	}

	/**
	 * Do nothing for a supertype constraint
	 */
	@Override
	public void process() {
	}
	
	@Override
	public String toString() {
		return this.typeParameter().name() + " :> " + this.URef().toString();
	}

}
