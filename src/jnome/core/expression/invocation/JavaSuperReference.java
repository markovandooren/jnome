/**
 * 
 */
package jnome.core.expression.invocation;

import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.SuperWildcardType;

public class JavaSuperReference extends WildCardReference<JavaSuperReference> {

	public JavaSuperReference(TypeReference tref) {
		super(tref);
	}
	
	@Override
	public JavaSuperReference clone() {
		return new JavaSuperReference(typeReference().clone());
	}

	public Type getElement() throws LookupException {
		SuperWildcardType superWildcardType = new SuperWildcardType(typeReference().getElement());
		superWildcardType.setUniParent(this);
		return superWildcardType;
	}

}