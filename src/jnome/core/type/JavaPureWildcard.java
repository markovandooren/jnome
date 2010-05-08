package jnome.core.type;

import chameleon.exception.ChameleonProgrammerException;

public class JavaPureWildcard extends PureWildcard<JavaPureWildcard> implements JavaTypeReference<JavaPureWildcard> {

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
	public JavaPureWildcard clone() {
		return new JavaPureWildcard();
	}
	
	
}
