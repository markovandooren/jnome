package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.rejuse.association.SingleAssociation;

public abstract class TypeAssignment {
	
	private TypeParameter _parameter;
	
	public TypeAssignment(TypeParameter parameter) {
		_parameter = parameter;
	}

	public TypeParameter parameter() {
		return _parameter;
	}
	
	public abstract Type type();

	private SingleAssociation<TypeAssignment, TypeAssignmentSet> _parentLink = new SingleAssociation<TypeAssignment, TypeAssignmentSet>(this);
	
	public SingleAssociation<TypeAssignment, TypeAssignmentSet> parentLink() {
		return _parentLink;
	}
	
	public TypeAssignmentSet parent() {
		return _parentLink.getOtherEnd();
	}

	public void substitute(TypeParameter oldParameter, TypeParameter newParameter) {
		if(parameter().equals(oldParameter)) {
			_parameter = newParameter; 
		}
	}

	public abstract TypeAssignment clone();
	
	public abstract TypeAssignment updatedTo(TypeParameter newParameter);
	
}
