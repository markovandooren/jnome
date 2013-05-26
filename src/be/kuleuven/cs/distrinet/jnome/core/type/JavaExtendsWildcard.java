package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;

public class JavaExtendsWildcard extends ExtendsWildcard {

	public JavaExtendsWildcard(TypeReference ref) {
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
	protected JavaExtendsWildcard cloneSelf() {
		return new JavaExtendsWildcard(null);
	}
}
