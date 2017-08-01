package org.aikodi.java.tool.dependency;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.modifier.AnnotationModifier;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.predicate.UniversalPredicate;

public class NoAnnotationOfType extends UniversalPredicate<Type,Nothing> {

	public NoAnnotationOfType(Type type) {
		super(Type.class);
		if(type == null) {
			throw new IllegalArgumentException("The type of the annotation is null.");
		}
		_type = type;
	}
	
	@Override
	public boolean uncheckedEval(Type object) {
		for(AnnotationModifier mod: object.lexical().descendants(AnnotationModifier.class)) {
			try {
				if(mod.type().subtypeOf(_type)) {
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
