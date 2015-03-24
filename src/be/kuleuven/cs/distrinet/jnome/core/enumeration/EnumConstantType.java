package be.kuleuven.cs.distrinet.jnome.core.enumeration;

import org.aikodi.chameleon.core.declaration.SimpleNameSignature;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.AbstractAnonymousInnerClass;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

public class EnumConstantType extends AbstractAnonymousInnerClass {

	public EnumConstantType() {
	}

	@Override
	public Type erasure() {
		return this;
	}

	@Override
	protected TypeReference typeReference() {
		EnumType nearestAncestor = nearestAncestor(EnumType.class);
		BasicJavaTypeReference result = language(Java7.class).createTypeReference(nearestAncestor.name());
		result.setUniParent(nearestAncestor);
		return result;
	}
	
	@Override
	public SimpleNameSignature signature() {
		SimpleNameSignature result = (SimpleNameSignature) nearestAncestor(EnumConstant.class).signature();
		return result;
	}
	
	@Override
	protected RegularType cloneSelf() {
		EnumConstantType enumConstantType = new EnumConstantType();
		enumConstantType.parameterBlock(TypeParameter.class).disconnect();
		return enumConstantType;
	}
}
