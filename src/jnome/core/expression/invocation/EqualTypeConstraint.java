/**
 * 
 */
package jnome.core.expression.invocation;

import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public class EqualTypeConstraint extends SecondPhaseConstraint {

	public EqualTypeConstraint(TypeParameter param, Type type) {
		super(param,type);
	}
	
}