package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.modifier.AnnotationModifier;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

public class NoAnnotationOfType extends SafePredicate<Pair<Type, Set<Type>>> {

	public NoAnnotationOfType(Type type) {
		if(type == null) {
			throw new IllegalArgumentException("The type of the annotation is null.");
		}
		_type = type;
	}
	
	@Override
	public boolean eval(Pair<Type, Set<Type>> object) {
		for(AnnotationModifier mod: object.first().descendants(AnnotationModifier.class)) {
			try {
				if(mod.type().subTypeOf(_type)) {
					return false;
				}
			} catch (LookupException e) {
				// If we can't determine the subtype relation
				// we return true such that it will show up
				// in the analysis result.
			}
		}
		return true;
	}
	
	private Type _type;

}
