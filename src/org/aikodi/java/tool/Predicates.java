package org.aikodi.java.tool;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.view.ObjectOrientedView;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.type.ArrayType;
import org.aikodi.rejuse.predicate.Predicate;

public class Predicates {

	public final static Predicate<Type, LookupException> COLLECTION = type -> {
		ObjectOrientedView view = (ObjectOrientedView) type.view();
		Java7 language = view.language(Java7.class);
		Type collection = language.erasure(view.findType("java.util.Collection"));
		Type map = language.erasure(view.findType("java.util.Map"));
		return type instanceof ArrayType || type.subtypeOf(collection) || type.subtypeOf(map);
	};

	public final static Predicate<Type, LookupException> IMMUTABLE_COLLECTION = type -> {
		ObjectOrientedView view = (ObjectOrientedView) type.view();
		Java7 language = view.language(Java7.class);
		boolean result = false;
		try {
			Type collection = language.erasure(view.findType("com.google.common.collect.ImmutableCollection"));
			Type map = language.erasure(view.findType("com.google.common.collect.ImmutableMap"));
			result = type.subtypeOf(collection) || type.subtypeOf(map);
		} catch(LookupException exc) {
			// The guava library is not part of the project libraries.
		}
		return result;
	};

	public final static Predicate<Declaration, LookupException> EXTERNALLY_ACCESSIBLE = member -> {
		Java7 language = member.language(Java7.class);
		return member.isTrue(language.PUBLIC) ||
				member.isTrue(language.PACKAGE_ACCESSIBLE);
	};

}
