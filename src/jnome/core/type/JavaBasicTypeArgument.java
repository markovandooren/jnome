package jnome.core.type;

import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.util.CreationStackTrace;

public class JavaBasicTypeArgument extends BasicTypeArgument<JavaBasicTypeArgument> implements JavaTypeReference<JavaBasicTypeArgument> {

	public CreationStackTrace _trace;
	
	public JavaBasicTypeArgument(TypeReference ref) {
		super(ref);
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}

	public JavaTypeReference erasedReference() {
		return new JavaBasicTypeArgument(((JavaTypeReference)typeReference()).erasedReference());
	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException();
	}

	@Override
	public JavaBasicTypeArgument clone() {
		return new JavaBasicTypeArgument(typeReference().clone());
	}

}
