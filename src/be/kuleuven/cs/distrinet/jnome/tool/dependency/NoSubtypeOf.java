package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

public class NoSubtypeOf extends SafePredicate<Pair<Type,Set<Type>>> {

	public NoSubtypeOf(Type superType) {
		_superType = superType;
	}
	
	private Type _superType;
	
	@Override
	public boolean eval(Pair<Type,Set<Type>> object) {
		try {
			return ! object.first().subTypeOf(_superType);
		} catch (LookupException e) {
			return false;
		}
	}
	
}