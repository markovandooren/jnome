package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.association.OrderedMultiAssociation;

public class TypeAssignmentSet {

	public TypeAssignmentSet(List<TypeParameter> typeParameters) {
		_completeList = new ArrayList<TypeParameter>(typeParameters);
	}
	
	private OrderedMultiAssociation<TypeAssignmentSet, TypeAssignment> _assignments = new OrderedMultiAssociation<TypeAssignmentSet, TypeAssignment>(this);
	{
		_assignments.enableCache();
	}
	
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
				Java7 language = upperBoundReference.language(Java7.class);
				JavaTypeReference bound = (JavaTypeReference) upperBoundReference.clone();
				bound.setUniParent(upperBoundReference);
				for(TypeAssignment nested: assignments) {
					bound = (JavaTypeReference) NonLocalJavaTypeReference.replace(language.reference(nested.type()), nested.parameter(), bound);
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
		//FIXME Avoid cloning.
		//PERFORMANCE
		List<TypeAssignment> local = new ArrayList<>(assignments());
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
	
	public TypeAssignmentSet updatedTo(List<TypeParameter> newParameters) {
		TypeAssignmentSet result = new TypeAssignmentSet(_completeList);
		Map<String, TypeParameter> map = new HashMap<>();
		for(TypeParameter parameter: newParameters) {
			map.put(parameter.name(),parameter);
		}
		for(TypeAssignment assignment: assignments()) {
			String name = assignment.parameter().name();
			result.add(assignment.updatedTo(map.get(name)));
		}
		return result;
	}
	
	public TypeAssignmentSet clone() {
		TypeAssignmentSet result = new TypeAssignmentSet(_completeList);
		for(TypeAssignment assignment: assignments()) {
			result.add(assignment.clone());
		}
		return result;
	}

}
