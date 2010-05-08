package jnome.core.type;

import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ExtendsWildcard;

public class JavaExtendsWildcard extends ExtendsWildcard<JavaExtendsWildcard> implements JavaTypeReference<JavaExtendsWildcard>{

	public JavaExtendsWildcard(TypeReference ref) {
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
	public JavaExtendsWildcard clone() {
		return new JavaExtendsWildcard(typeReference().clone());
	}
	
	

}
