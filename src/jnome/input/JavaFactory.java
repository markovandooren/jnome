package jnome.input;

import jnome.core.method.JavaNormalMethod;
import jnome.core.type.RegularJavaType;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeReference;
import chameleon.plugin.Plugin;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaFactory extends ObjectOrientedFactory {
	
	public JavaFactory() {
		
	}
	
	public RegularType createRegularType(SimpleNameSignature signature) {
		return new RegularJavaType(signature);
	}
	
	public NormalMethod createNormalMethod(DeclarationWithParametersHeader header, TypeReference returnType) {
		return new JavaNormalMethod(header, returnType);
	}

	@Override
	public Plugin clone() {
		return new JavaFactory();
	}
}
