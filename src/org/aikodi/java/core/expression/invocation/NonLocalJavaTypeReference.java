/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.BasicTypeReference;
import org.aikodi.chameleon.oo.type.NonLocalTypeReference;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;
import org.aikodi.java.core.type.ArrayTypeReference;
import org.aikodi.java.core.type.JavaTypeReference;
import org.aikodi.java.input.OldExtractor;
import org.aikodi.rejuse.association.SingleAssociation;
import org.aikodi.rejuse.predicate.AbstractPredicate;
import org.aikodi.rejuse.predicate.Predicate;

public class NonLocalJavaTypeReference extends NonLocalTypeReference implements JavaTypeReference {

//	private CreationStackTrace _trace = new CreationStackTrace();
	
	public NonLocalJavaTypeReference(JavaTypeReference tref) {
    super(tref);
	}
	
	public NonLocalJavaTypeReference(JavaTypeReference tref, Element lookupParent) {
	   super(tref,lookupParent);
	}
	
	@Override
	protected NonLocalJavaTypeReference cloneSelf() {
		return new NonLocalJavaTypeReference(null,lookupParent());
	}
	
	/**
	 * Replace all references to 'declarator' in 'in' with copies of 'replacement'.
	 * @param replacement
	 * @param declarator
	 * @param in
	 * @param kind
	 * @return
	 * @throws LookupException
	 */
	public static <E extends Element> E replace(TypeReference replacement, final Declaration declarator, E in, Class<E> kind) throws LookupException {
		ObjectOrientedLanguage lang = in.language(ObjectOrientedLanguage.class);
		E result = in;
		Predicate<BasicTypeReference, LookupException> predicate = object -> object.getDeclarator().sameAs(declarator);
		List<BasicTypeReference> crefs = in.descendants(BasicTypeReference.class, 
				predicate);
		if(in instanceof BasicTypeReference) {
			BasicTypeReference in2 = (BasicTypeReference) in;
			if(predicate.eval(in2)) {
				crefs.add(in2);
			}
		}
		for(BasicTypeReference cref: crefs) {
			TypeReference substitute;
			Element oldParent = replacement.parent();
			if(replacement.isDerived()) {
//				replacement.setUniParent(null);
			  substitute = lang.createNonLocalTypeReference(Util.clone(replacement),oldParent);
			  substitute.setOrigin(replacement);
			} else {
			  substitute = lang.createNonLocalTypeReference(Util.clone(replacement),oldParent);
			}
			if(! cref.isDerived()) {
				SingleAssociation crefParentLink = cref.parentLink();
				crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
			} else {
				substitute.setUniParent(in.parent());
			}
			if(cref == in) {
				if(kind.isInstance(substitute)) {
				  result = (E) substitute;
				} else {
					throw new ChameleonProgrammerException("The type reference passed to replace must be replaced as a whole, but the kind that was given is more specific than the newly created type reference.");
				}
			}
		}
		return result;
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
	  public JavaTypeReference box() throws LookupException {
	    return componentTypeReference().box(); 
	  }


}
