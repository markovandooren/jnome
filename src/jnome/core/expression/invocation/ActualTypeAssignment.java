package jnome.core.expression.invocation;

import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public class ActualTypeAssignment extends TypeAssignment {

	public ActualTypeAssignment(TypeParameter parameter, Type type) {
		super(parameter);
		_type = type;
	}
	
	@Override
	public Type type() {
		return _type;
	}
	
	private Type _type;

}
