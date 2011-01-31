package jnome.input;

import jnome.core.method.JavaNormalMethod;
import jnome.core.type.RegularJavaType;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaFactory {
	
	public JavaFactory() {
		
	}
	
	public RegularType createRegularType(SimpleNameSignature signature) {
		return new RegularJavaType(signature);
	}
	
	public NormalMethod createNormalMethod(DeclarationWithParametersHeader header, TypeReference returnType) {
		return new JavaNormalMethod(header, returnType);
	}
}
