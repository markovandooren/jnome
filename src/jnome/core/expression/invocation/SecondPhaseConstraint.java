/**
 * 
 */
package jnome.core.expression.invocation;

import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public abstract class SecondPhaseConstraint extends Constraint {
	
	public SecondPhaseConstraint(TypeParameter param, Type type) {
	  _type = type;	
	  _typeParameter = param;
	}
	
	private Type _type;
	
	public Type type() {
		return _type;
	}
	
	private TypeParameter _typeParameter;
	
	public TypeParameter typeParameter() {
		return _typeParameter;
	}
	
}