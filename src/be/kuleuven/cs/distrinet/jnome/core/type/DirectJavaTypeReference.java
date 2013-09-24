package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.element.ElementImpl;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;

public class DirectJavaTypeReference extends ElementImpl implements JavaTypeReference {

	public DirectJavaTypeReference(Type type) {
		_type = type;
	}
	
	private Type _type;
	
	@Override
	public Type getType() {
		return _type;
	}

	@Override
	public Type getElement() {
		return _type;
	}

	@Override
	public TypeReference intersection(TypeReference other) {
		return other.intersectionDoubleDispatch(this);
	}

	@Override
	public TypeReference intersectionDoubleDispatch(TypeReference other) {
		return language(ObjectOrientedLanguage.class).createIntersectionReference(clone(this), clone(other));
	}

	@Override
	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference other) {
		IntersectionTypeReference result = clone(other);
		result.add(clone(this));
		return result;
	}

	@Override
	public Declaration getDeclarator() throws LookupException {
		return _type;
	}

	@Override
	public LookupContext targetContext() throws LookupException {
		return getType().targetContext();
	}

	@Override
	public JavaTypeReference toArray(int dimension) {
		return new ArrayTypeReference(this, dimension);
	}

	@Override
	public JavaTypeReference erasedReference() {
		Java java = language(Java.class);
		return java.reference(java.erasure(_type));
	}

	@Override
	public JavaTypeReference componentTypeReference() {
		if(_type instanceof ArrayType) {
			return language(Java.class).reference(_type).componentTypeReference();
		} else {
			return this;
		}
	}

	@Override
	protected Element cloneSelf() {
		return new DirectJavaTypeReference(_type);
	}

}
