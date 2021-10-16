/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguageImpl;
import org.aikodi.chameleon.oo.type.BasicTypeReference;
import org.aikodi.chameleon.oo.type.BoxableTypeReference;
import org.aikodi.chameleon.oo.type.NonLocalTypeReference;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.Util;
import org.aikodi.java.core.type.ArrayTypeReference;
import org.aikodi.java.core.type.JavaTypeReference;
import org.aikodi.rejuse.association.SingleAssociation;
import org.aikodi.rejuse.predicate.Predicate;

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

	public JavaTypeReference componentTypeReference() {
		return ((JavaTypeReference)actualReference()).componentTypeReference();
	}

	public JavaTypeReference erasedReference() {
		JavaTypeReference erasedReference = ((JavaTypeReference)actualReference()).erasedReference();
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

	@Override
	  public BoxableTypeReference box() throws LookupException {
	    return componentTypeReference().box(); 
	  }


}
