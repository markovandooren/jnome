package jnome.core.expression.invocation;

import chameleon.oo.type.Type;
import chameleon.oo.type.generics.TypeParameter;

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
