package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.exception.ChameleonProgrammerException;

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
	protected JavaPureWildcard cloneSelf() {
		return new JavaPureWildcard();
	}
	
	
}
