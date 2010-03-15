/**
 * 
 */
package jnome.core.expression.invocation;

import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public class SupertypeConstraint extends SecondPhaseConstraint {

	public SupertypeConstraint(TypeParameter param, Type type) {
		super(param,type);
	}
	
}