/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.NonLocalTypeReference;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.java.core.type.ArrayTypeReference;
import org.aikodi.java.core.type.JavaTypeReference;

public class NonLocalJavaTypeReference extends NonLocalTypeReference implements JavaTypeReference {

	public NonLocalJavaTypeReference(TypeReference tref, Element lookupParent) {
	   super(tref,lookupParent);
	}
	
	@Override
	protected NonLocalJavaTypeReference cloneSelf() {
		return new NonLocalJavaTypeReference(null,lookupParent());
	}

	public static TypeReference replace(TypeReference replacement, final Declaration declarator, TypeReference in) throws LookupException {
		return replace(replacement, declarator,in,TypeReference.class);
	}

	public JavaTypeReference erasedReference() {
		TypeReference erasedReference = ((JavaTypeReference)actualReference()).erasedReference();
		NonLocalJavaTypeReference result = new NonLocalJavaTypeReference(erasedReference, lookupParent());
		return result;
	}

	public JavaTypeReference toArray(int arrayDimension) {
  	JavaTypeReference result = new ArrayTypeReference(clone(this), arrayDimension);
  	return result;
  }

	@Override
	public String toString() {
		return actualReference().toString();
	}

}
