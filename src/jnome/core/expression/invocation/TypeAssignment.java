package jnome.core.expression.invocation;

import org.rejuse.association.SingleAssociation;

import chameleon.oo.type.Type;
import chameleon.oo.type.generics.TypeParameter;

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

}
