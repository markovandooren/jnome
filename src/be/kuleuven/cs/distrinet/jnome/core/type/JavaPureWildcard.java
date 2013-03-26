package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;

public class JavaPureWildcard extends PureWildcard {

//	public JavaTypeReference componentTypeReference() {
//		return this;
//	}
//
//	public JavaTypeReference erasedReference() {
//		return this;
//	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException();
	}

	@Override
	public JavaPureWildcard clone() {
		return new JavaPureWildcard();
	}
	
	
}
