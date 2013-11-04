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
	public MultiFormalParameter(String name, JavaTypeReference T) {
		super(name,T.toArray(1));
	}
	
	private MultiFormalParameter(String name, JavaTypeReference T, boolean dummy) {
		super(name,T);
	}

	public static MultiFormalParameter createUnsafe(String name, JavaTypeReference T) {
		return new MultiFormalParameter(name, T, false);
	}
	
	@Override
	protected MultiFormalParameter cloneSelf() {
		return new MultiFormalParameter(name(),null,false);
	}
}
