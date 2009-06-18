package jnome.core.variable;

import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.type.TypeReference;
import chameleon.core.variable.FormalParameter;

public class MultiFormalParameter extends FormalParameter {

	public MultiFormalParameter(SimpleNameSignature sig, JavaTypeReference type) {
		super(sig,type.toArray(1));
	}

}
