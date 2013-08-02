package be.kuleuven.cs.distrinet.jnome.core.variable;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

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
	
	private MultiFormalParameter(SimpleNameSignature sig, JavaTypeReference T, boolean dummy) {
		super(sig,T);
	}

	public static MultiFormalParameter createUnsafe(SimpleNameSignature sig, JavaTypeReference T) {
		return new MultiFormalParameter(sig, T, false);
	}
}
