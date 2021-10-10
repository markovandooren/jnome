package org.aikodi.java.core.type;

import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.java.core.language.Java7;
import org.aikodi.rejuse.contract.Contracts;

/**
 * <p>A class of reference that point directly to a given {@link Type}.
 * No lookup is performed.</p>
 * 
 * <p>Direct references should be used only in generated elements and
 * not be part of the source model.</p>
 * 
 * @author Marko van Dooren
 */
public class DirectJavaTypeReference extends ElementImpl implements JavaTypeReference {

	public DirectJavaTypeReference(Type type) {
	  Contracts.notNull(type);
		_type = type;
	}
	
	private Type _type;
	
	@Override
	public Type getElement() {
		return _type;
	}

	@Override
	public JavaTypeReference erasedReference() {
		Java7 java = language(Java7.class);
		return java.reference(java.erasure(_type));
	}

	@Override
	public JavaTypeReference componentTypeReference() {
		if(_type instanceof ArrayType) {
			return language(Java7.class).reference(_type).componentTypeReference();
		} else {
			return this;
		}
	}

	@Override
	protected Element cloneSelf() {
		return new DirectJavaTypeReference(_type);
	}

	/* (non-Javadoc)
	 * @see org.aikodi.chameleon.oo.type.TypeReference#toString(java.util.Set)
	 */
	@Override
	public String toString(Set<Element> visited) {
		return language(Java7.class).reference(getElement()).toString(visited);
	}
}
