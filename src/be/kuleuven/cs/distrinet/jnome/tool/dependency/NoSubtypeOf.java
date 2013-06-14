package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

public class NoSubtypeOf extends SafePredicate<Type> {

	public NoSubtypeOf(Type superType) {
		_superType = superType;
	}
	
	private Type _superType;
	
	@Override
	public boolean eval(Type object) {
		try {
			boolean subTypeOf = object.subTypeOf(_superType);
			return ! subTypeOf;
		} catch (LookupException e) {
			return false;
		}
	}
	
}