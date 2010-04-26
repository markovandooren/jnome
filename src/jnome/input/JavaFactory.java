package jnome.input;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.oo.type.RegularType;

public class JavaFactory {
	
	public RegularType createRegularType(SimpleNameSignature signature) {
		return new RegularType(signature);
	}
}
