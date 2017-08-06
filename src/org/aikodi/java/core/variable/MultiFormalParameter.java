package org.aikodi.java.core.variable;

import org.aikodi.chameleon.core.declaration.Name;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.java.core.type.JavaTypeReference;

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
