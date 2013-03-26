package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;

public class JavaBasicTypeArgument extends BasicTypeArgument {
	
//	private CreationStackTrace _trace = new CreationStackTrace();
	
	public JavaBasicTypeArgument(TypeReference ref) {
		super(ref);
	}

//	public JavaTypeReference componentTypeReference() {
//		return this;
//	}
//
//	public JavaTypeReference erasedReference() {
//		return new JavaBasicTypeArgument(((JavaTypeReference)typeReference()).erasedReference());
//	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException();
	}

	@Override
	public JavaBasicTypeArgument clone() {
		return new JavaBasicTypeArgument(typeReference().clone());
	}

}
