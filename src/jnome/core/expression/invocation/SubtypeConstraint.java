/**
 * 
 */
package jnome.core.expression.invocation;

import jnome.core.type.JavaTypeReference;
import chameleon.core.type.generics.TypeParameter;

public class SubtypeConstraint extends SecondPhaseConstraint {

	public SubtypeConstraint(TypeParameter param, JavaTypeReference type) {
		super(param,type);
	}
	
}