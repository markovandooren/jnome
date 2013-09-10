/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;

public abstract class SecondPhaseConstraint extends Constraint<SecondPhaseConstraint, SecondPhaseConstraintSet> {
	
	public SecondPhaseConstraint(TypeParameter param, JavaTypeReference typeReference) {
	  _UReference = typeReference;	
	  _typeParameter = param;
	}
	
	private JavaTypeReference _UReference;
	
	public JavaTypeReference URef() {
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
	
	public Java language() {
		return URef().language(Java.class);
	}
	
	public abstract void process() throws LookupException;
	
}
