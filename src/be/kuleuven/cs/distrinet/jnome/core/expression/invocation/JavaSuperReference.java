/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcardType;

public class JavaSuperReference extends WildCardReference<JavaSuperReference> {

	public JavaSuperReference(TypeReference tref) {
		super(tref);
	}
	
	@Override
	protected JavaSuperReference cloneSelf() {
		return new JavaSuperReference(null);
	}

	public Type getElement() throws LookupException {
		SuperWildcardType superWildcardType = new SuperWildcardType(typeReference().getElement());
		superWildcardType.setUniParent(this);
		return superWildcardType;
	}

	@Override
	public String toString() {
		return "? super "+typeReference().toString();
	}
}
