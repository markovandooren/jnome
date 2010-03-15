/**
 * 
 */
package jnome.core.expression.invocation;

import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public class SubtypeConstraint extends SecondPhaseConstraint {

	public SubtypeConstraint(TypeParameter param, Type type) {
		super(param,type);
	}
	
}