package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.oo.type.BoxableTypeReference;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.java.core.type.JavaTypeReference;

public class SubtypeConstraint extends SecondPhaseConstraint {

	public SubtypeConstraint(TypeParameter param, BoxableTypeReference type) {
		super(param,type);
	}

	/**
	 * Do nothing for a subtype constraint.
	 */
	@Override
	public void process() {
		
	}
	
	@Override
	public String toString() {
		return this.typeParameter().name() + " <: " + this.URef().toString();
	}
	
}
