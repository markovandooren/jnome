/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcardType;

public class ExtendsReference extends WildCardReference {

	public ExtendsReference(TypeReference tref) {
		super(tref);
	}
	
	@Override
	protected ExtendsReference cloneSelf() {
		return new ExtendsReference(null);
	}

	public Type getElement() throws LookupException {
		ExtendsWildcardType extendsWildcardType = new ExtendsWildcardType(typeReference().getElement());
		extendsWildcardType.setUniParent(this);
		return extendsWildcardType;
	}

	@Override
	public String toString(Set<Element> visited) {
		return "? extends "+typeReference().toString(visited);
	}

}
