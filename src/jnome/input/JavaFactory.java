package jnome.input;

import jnome.core.method.JavaNormalMethod;
import jnome.core.type.RegularJavaType;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.method.MethodHeader;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.oo.type.RegularType;
import chameleon.plugin.Plugin;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaFactory extends ObjectOrientedFactory {
	
	public JavaFactory() {
		
	}
	
	public RegularType createRegularType(SimpleNameSignature signature) {
		return new RegularJavaType(signature);
	}
	
	public NormalMethod createNormalMethod(MethodHeader header) {
		return new JavaNormalMethod(header);
	}

	@Override
	public Plugin clone() {
		return new JavaFactory();
	}
}
