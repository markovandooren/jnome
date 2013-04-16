package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.association.OrderedMultiAssociation;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;

public class TypeAssignmentSet {

	public TypeAssignmentSet(List<TypeParameter> typeParameters) {
		_completeList = new ArrayList<TypeParameter>(typeParameters);
	}
	
	private OrderedMultiAssociation<TypeAssignmentSet, TypeAssignment> _assignments = new OrderedMultiAssociation<TypeAssignmentSet, TypeAssignment>(this);
	
	public List<TypeAssignment> assignments() {
		return _assignments.getOtherEnds();
	}
	
	public int nbAssignments() {
		return _assignments.size();
	}
	
	public boolean hasAssignments() {
		return nbAssignments() > 0;
	}
	
	public void add(TypeAssignment assignment) {
		if(assignment != null) {
			_assignments.add(assignment.parentLink());
		}
	}
	
	public void addAll(Collection<TypeAssignment> assignments) {
		for(TypeAssignment constraint: assignments) {
			add(constraint);
		}
	}
	
	public void remove(TypeAssignment assignment) {
		if(assignment != null) {
			_assignments.remove(assignment.parentLink());
		}
	}
	
	public boolean valid() throws LookupException {
		boolean result = unassigned().isEmpty();
		if(result) {
			List<TypeAssignment> assignments = assignments();
			for(TypeAssignment assignment: assignments) {
				TypeReference upperBoundReference = assignment.parameter().upperBoundReference();
				Java language = upperBoundReference.language(Java.class);
				JavaTypeReference bound = (JavaTypeReference) upperBoundReference.clone();
				bound.setUniParent(upperBoundReference);
				for(TypeAssignment nested: assignments) {
					NonLocalJavaTypeReference.replace(language.reference(nested.type()), nested.parameter(), bound);
				}
				Type type = assignment.type();
				if(! type.subTypeOf(bound.getElement())) {
					result = false;
					break;
				}
			}
		}
		return result;
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
	
	public TypeAssignment assignment(TypeParameter parameter) throws LookupException {
		for(TypeAssignment assignment: assignments()) {
			if(assignment.parameter().sameAs(parameter)) {
				return assignment;
			}
		}
		return null;
	}
	
	public List<TypeParameter> assigned() throws LookupException {
		List<TypeParameter> result = typeParameters();
		result.removeAll(unassigned());
		return result;
	}
	
	public List<TypeParameter> unassigned() throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		List<TypeAssignment> local = assignments();
		for(TypeParameter parameter: typeParameters()) {
			int position = -1;
			int index = 0;
			for(TypeAssignment assignment: local) {
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

	public void substitute(TypeParameter oldParameter, TypeParameter newParameter) {
		for(TypeAssignment assignment: assignments()) {
			assignment.substitute(oldParameter, newParameter);
		}
		int size = _completeList.size();
		for(int i = 0; i < size; i++) {
			if(_completeList.get(i).equals(oldParameter)) {
				_completeList.set(i, newParameter);
			}
		}
	}

}