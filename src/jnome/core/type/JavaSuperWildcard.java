package jnome.core.type;

import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.SuperWildcard;

public class JavaSuperWildcard extends SuperWildcard<JavaSuperWildcard> implements JavaTypeReference<JavaSuperWildcard> {

	public JavaSuperWildcard(TypeReference ref) {
		super(ref);
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}

	public JavaTypeReference erasedReference() {
		return this;
	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException();
	}

	@Override
	public JavaSuperWildcard clone() {
		return new JavaSuperWildcard(typeReference().clone());
	}
	
	
}
