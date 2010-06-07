/**
 * 
 */
package jnome.core.expression.invocation;

import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ExtendsWildcardType;

public class JavaExtendsReference extends WildCardReference<JavaExtendsReference> {

	public JavaExtendsReference(TypeReference tref) {
		super(tref);
	}
	
	@Override
	public JavaExtendsReference clone() {
		return new JavaExtendsReference(typeReference().clone());
	}

	public Type getElement() throws LookupException {
		ExtendsWildcardType extendsWildcardType = new ExtendsWildcardType(typeReference().getElement());
		extendsWildcardType.setUniParent(this);
		return extendsWildcardType;
	}

}