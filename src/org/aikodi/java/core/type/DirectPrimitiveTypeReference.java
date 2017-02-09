package org.aikodi.java.core.type;

import org.aikodi.chameleon.oo.type.Type;

public class DirectPrimitiveTypeReference extends DirectJavaTypeReference {

	public DirectPrimitiveTypeReference(Type type) {
		super(type);
	}
	
	@Override
	protected DirectPrimitiveTypeReference cloneSelf() {
		return new DirectPrimitiveTypeReference(getElement());
	}
	
	@Override
	public DirectPrimitiveTypeReference erasedReference() {
		return cloneSelf();
	}
	
	public JavaTypeReference componentTypeReference() {
		return this;
	}

}
