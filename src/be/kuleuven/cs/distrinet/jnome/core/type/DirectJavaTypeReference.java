package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.IntersectionTypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.rejuse.contract.Contracts;

public class DirectJavaTypeReference extends ElementImpl implements JavaTypeReference {

	public DirectJavaTypeReference(Type type) {
	  Contracts.notNull(type);
		_type = type;
	}
	
	private Type _type;
	
	@Override
	public Type getElement() {
		return _type;
	}

	@Override
	public JavaTypeReference toArray(int dimension) {
		return new ArrayTypeReference(this, dimension);
	}

	@Override
	public JavaTypeReference erasedReference() {
		Java7 java = language(Java7.class);
		return java.reference(java.erasure(_type));
	}

	@Override
	public JavaTypeReference componentTypeReference() {
		if(_type instanceof ArrayType) {
			return language(Java7.class).reference(_type).componentTypeReference();
		} else {
			return this;
		}
	}

	@Override
	protected Element cloneSelf() {
		return new DirectJavaTypeReference(_type);
	}

}
