/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.java.core.language.Java7;

public abstract class SecondPhaseConstraint extends Constraint<SecondPhaseConstraint, SecondPhaseConstraintSet> {
	
	public SecondPhaseConstraint(TypeParameter param, TypeReference typeReference) {
	  _UReference = typeReference;	
	  _typeParameter = param;
	}
	
	private TypeReference _UReference;
	
	public TypeReference URef() {
		return _UReference;
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
	
	public Java7 language() {
		return URef().language(Java7.class);
	}
	
	public abstract void process() throws LookupException;
	
}
