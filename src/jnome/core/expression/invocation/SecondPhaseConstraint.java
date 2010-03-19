/**
 * 
 */
package jnome.core.expression.invocation;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public abstract class SecondPhaseConstraint extends Constraint<SecondPhaseConstraint> {
	
	public SecondPhaseConstraint(TypeParameter param, JavaTypeReference type) {
	  _type = type;	
	  _typeParameter = param;
	}
	
	private JavaTypeReference _type;
	
	public JavaTypeReference URef() {
		return _type;
	}
	
	public Type U() throws LookupException {
		return URef().getElement();
	}
	
	private TypeParameter _typeParameter;
	
	public TypeParameter typeParameter() {
		return _typeParameter;
	}
	
	protected void setTypeParameter(TypeParameter parameter) {
		_typeParameter = parameter;
	}
	
	public Java language() {
		return URef().language(Java.class);
	}
	//public abstract void process();
	
}