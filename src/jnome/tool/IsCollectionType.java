package jnome.tool;

import jnome.core.language.Java;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;

public class IsCollectionType extends UnsafePredicate<Type, LookupException> {

	@Override
	public boolean eval(Type type) throws LookupException {
		Java language = type.language(Java.class);
		Type collection = language.erasure(language.findType("java.util.Collection"));
		return type.subTypeOf(collection);
	}
	
}