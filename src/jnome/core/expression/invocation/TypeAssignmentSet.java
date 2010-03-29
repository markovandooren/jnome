package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public class TypeAssignmentSet {

	private OrderedMultiAssociation<TypeAssignmentSet, TypeAssignment> _constraints = new OrderedMultiAssociation<TypeAssignmentSet, TypeAssignment>(this);
	
	public List<TypeAssignment> assignments() {
		return _constraints.getOtherEnds();
	}
	
	public void add(TypeAssignment assignment) {
		if(assignment != null) {
			_constraints.add(assignment.parentLink());
		}
	}
	
	public void addAll(Collection<TypeAssignment> assignments) {
		for(TypeAssignment constraint: assignments) {
			add(constraint);
		}
	}
	
	public void remove(TypeAssignment assignment) {
		if(assignment != null) {
			_constraints.remove(assignment.parentLink());
		}
	}
	
	private List<TypeParameter> _completeList;
	
	public List<TypeParameter> typeParameters() {
		return new ArrayList<TypeParameter>(_completeList);
	}
	
	public Type type(TypeParameter parameter) throws LookupException {
		for(TypeAssignment assignment: assignments()) {
			if(assignment.parameter().sameAs(parameter)) {
				return assignment.type();
			}
		}
		return null;
	}
	
	public List<TypeParameter> unassigned() throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		List<TypeAssignment> local = assignments();
		for(TypeParameter parameter: typeParameters()) {
			int position = -1;
			int index = 0;
			for(TypeAssignment assignment: assignments()) {
				if(assignment.parameter().sameAs(parameter)) {
					position = index;
					break;
				}
				index++;
			}
			if(position >= 0) {
				local.remove(position);
			} else {
				result.add(parameter);
			}
		}
		return result;
	}

}
