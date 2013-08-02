package be.kuleuven.cs.distrinet.jnome.tool;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;

public class IsCollectionType extends AbstractPredicate<Type, LookupException> {

	@Override
	public boolean eval(Type type) throws LookupException {
		View view = type.view();
		Java language = view.language(Java.class);
		Type collection = language.erasure(language.findType("java.util.Collection",view.namespace()));
		Type map = language.erasure(language.findType("java.util.Map",view.namespace()));
		return type instanceof ArrayType || type.subTypeOf(collection) || type.subTypeOf(map);
	}
	
}
