package jnome.core.expression.invocation;

import chameleon.oo.type.Type;
import chameleon.oo.type.generics.TypeParameter;

public class IndirectTypeAssignment extends TypeAssignment {

	private TypeParameter _source;

	public IndirectTypeAssignment(TypeParameter parameter, TypeParameter source) {
		super(parameter);
		_source = source;
	}

	@Override
	public Type type() {
		for(TypeAssignment assignment:parent().assignments()) {
			if(assignment.parameter().equals(source())) {
				return assignment.type();
			}
		}
		return null;
	}
	
	public TypeParameter source() {
		return _source;
	}

}
