/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import java.util.HashSet;
import java.util.Set;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.java.core.type.JavaTypeReference;

public class SupertypeConstraint extends SecondPhaseConstraint {

	public SupertypeConstraint(TypeParameter param, JavaTypeReference type) {
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
