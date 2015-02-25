/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

public class SubtypeConstraint extends SecondPhaseConstraint {

	public SubtypeConstraint(TypeParameter param, JavaTypeReference type) {
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
