package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;

public class JavaSuperWildcard extends SuperWildcard {

	public JavaSuperWildcard(TypeReference ref) {
		super(ref);
	}

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
	public JavaSuperWildcard clone() {
		return new JavaSuperWildcard(typeReference().clone());
	}
	
	
}
