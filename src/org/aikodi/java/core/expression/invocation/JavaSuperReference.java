/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.SuperWildcardType;

public class JavaSuperReference extends WildCardReference<JavaSuperReference> {

	public JavaSuperReference(TypeReference tref) {
		super(tref);
	}
	
	@Override
	protected JavaSuperReference cloneSelf() {
		return new JavaSuperReference(null);
	}

	public Type getElement() throws LookupException {
		SuperWildcardType superWildcardType = new SuperWildcardType(typeReference().getElement());
		superWildcardType.setUniParent(this);
		return superWildcardType;
	}

	@Override
	public String toString(Set<Element> visited) {
		return "? super "+typeReference().toString(visited);
	}
}
