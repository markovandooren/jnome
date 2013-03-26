package be.kuleuven.cs.distrinet.jnome.core.variable;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;

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
