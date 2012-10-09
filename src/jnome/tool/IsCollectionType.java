package jnome.tool;

import jnome.core.language.Java;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;
import chameleon.workspace.View;

public class IsCollectionType extends UnsafePredicate<Type, LookupException> {

	@Override
	public boolean eval(Type type) throws LookupException {
		View view = type.view();
		Java language = view.language(Java.class);
		Type collection = language.erasure(language.findType("java.util.Collection",view.namespace()));
		return type.subTypeOf(collection);
	}
	
}