/**
 * 
 */
package jnome.core.expression.invocation;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.TypeParameter;

public abstract class SecondPhaseConstraint extends Constraint<SecondPhaseConstraint, SecondPhaseConstraintSet> {
	
	public SecondPhaseConstraint(TypeParameter param, JavaTypeReference type) {
	  _UReference = type;	
	  _typeParameter = param;
	}
	
	private JavaTypeReference _UReference;
	
	public JavaTypeReference<?> URef() {
		return _UReference;
	}
	
	public Type U() throws LookupException {
		return URef().getElement();
	}
	
	private TypeParameter _typeParameter;
	
	public TypeParameter<?> typeParameter() {
		return _typeParameter;
	}
	
	protected void setTypeParameter(TypeParameter parameter) {
		_typeParameter = parameter;
	}
	
	public Java language() {
		return URef().language(Java.class);
	}
	
	public abstract void process() throws LookupException;
	
}