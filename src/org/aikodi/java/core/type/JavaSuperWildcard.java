package org.aikodi.java.core.type;

import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;

public class JavaSuperWildcard extends SuperWildcard {

	public JavaSuperWildcard(TypeReference ref) {
		super(ref);
	}

	@Override
	protected JavaSuperWildcard cloneSelf() {
		return new JavaSuperWildcard(null);
	}
	
	
}
