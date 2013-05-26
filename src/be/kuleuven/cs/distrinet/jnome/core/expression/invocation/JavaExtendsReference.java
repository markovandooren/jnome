/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcardType;

public class JavaExtendsReference extends WildCardReference {

	public JavaExtendsReference(TypeReference tref) {
		super(tref);
	}
	
	@Override
	protected JavaExtendsReference cloneSelf() {
		return new JavaExtendsReference(null);
	}

	public Type getElement() throws LookupException {
		ExtendsWildcardType extendsWildcardType = new ExtendsWildcardType(typeReference().getElement());
		extendsWildcardType.setUniParent(this);
		return extendsWildcardType;
	}

	@Override
	public String toString() {
		return "? extends "+typeReference().toString();
	}

}
