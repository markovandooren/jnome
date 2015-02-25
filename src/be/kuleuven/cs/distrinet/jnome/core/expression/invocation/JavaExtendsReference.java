/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcardType;

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
