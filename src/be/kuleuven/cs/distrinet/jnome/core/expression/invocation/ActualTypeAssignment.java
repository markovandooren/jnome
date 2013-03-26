package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;

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
