package be.kuleuven.cs.distrinet.jnome.core.enumeration;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
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
		BasicJavaTypeReference result = language(Java.class).createTypeReference(nearestAncestor.name());
		result.setUniParent(nearestAncestor);
		return result;
	}
	
	@Override
	public SimpleNameSignature signature() {
		SimpleNameSignature result = (SimpleNameSignature) nearestAncestor(EnumConstant.class).signature();
		return result;
	}
	
	@Override
	protected RegularType cloneThis() {
		return new EnumConstantType();
	}
}
