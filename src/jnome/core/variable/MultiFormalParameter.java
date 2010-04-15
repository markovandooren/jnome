package jnome.core.variable;

import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.variable.FormalParameter;

public class MultiFormalParameter extends FormalParameter {

	/**
	 * Create a new formal parameter representing T...
	 * The given type will be wrapped in an array.
	 * @param sig
	 * @param T
	 */
	public MultiFormalParameter(SimpleNameSignature sig, JavaTypeReference T) {
		super(sig,T.toArray(1));
	}

}
