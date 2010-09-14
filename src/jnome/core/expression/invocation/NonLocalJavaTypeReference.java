/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import jnome.core.type.ArrayTypeReference;
import jnome.core.type.JavaTypeReference;

import org.rejuse.association.SingleAssociation;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.NonLocalTypeReference;
import chameleon.oo.type.TypeReference;

public class NonLocalJavaTypeReference extends NonLocalTypeReference<NonLocalJavaTypeReference> implements JavaTypeReference<NonLocalJavaTypeReference>{

	public NonLocalJavaTypeReference(JavaTypeReference tref) {
    super(tref);
	}
	
	public NonLocalJavaTypeReference(JavaTypeReference tref, Element lookupParent) {
	   super(tref,lookupParent);
	}
	
	@Override
	public NonLocalJavaTypeReference clone() {
		return new NonLocalJavaTypeReference((JavaTypeReference) actualReference().clone(),lookupParent());
	}
	
	public static <E extends Element<?,?>> E replace(TypeReference replacement, final Declaration declarator, E in, Class<E> kind) throws LookupException {
		ObjectOrientedLanguage lang = in.language(ObjectOrientedLanguage.class);
		E result = in;
		UnsafePredicate<BasicTypeReference, LookupException> predicate = new UnsafePredicate<BasicTypeReference, LookupException>() {
			@Override
			public boolean eval(BasicTypeReference object) throws LookupException {
				return object.getDeclarator().sameAs(declarator);
			}
		};
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
			if(replacement.isDerived()) {
				Element oldParent = replacement.parent();
//				replacement.setUniParent(null);
			  substitute = lang.createNonLocalTypeReference(replacement.clone(),oldParent);
			  substitute.setOrigin(replacement);
			} else {
			  substitute = lang.createNonLocalTypeReference(replacement.clone());
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

	
	public static TypeReference replace(TypeReference replacement, final Declaration declarator, TypeReference<?> in) throws LookupException {
		return replace(replacement, declarator,in,TypeReference.class);
//		ObjectOrientedLanguage lang = in.language(ObjectOrientedLanguage.class);
//		TypeReference result = in;
//		UnsafePredicate<BasicTypeReference, LookupException> predicate = new UnsafePredicate<BasicTypeReference, LookupException>() {
//			@Override
//			public boolean eval(BasicTypeReference object) throws LookupException {
//				return object.getDeclarator().sameAs(declarator);
//			}
//		};
//		List<BasicTypeReference> crefs = in.descendants(BasicTypeReference.class, 
//				predicate);
//		if(in instanceof BasicTypeReference) {
//			BasicTypeReference in2 = (BasicTypeReference) in;
//			if(predicate.eval(in2)) {
//				crefs.add(in2);
//			}
//		}
//		for(BasicTypeReference cref: crefs) {
//			TypeReference substitute;
//			if(replacement.isDerived()) {
//				Element oldParent = replacement.parent();
////				replacement.setUniParent(null);
//			  substitute = lang.createNonLocalTypeReference(replacement.clone(),oldParent);
//			  substitute.setOrigin(replacement);
//			} else {
//			  substitute = lang.createNonLocalTypeReference(replacement.clone());
//			}
//			if(! cref.isDerived()) {
//				SingleAssociation crefParentLink = cref.parentLink();
//				crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
//			} else {
//				substitute.setUniParent(in.parent());
//			}
//			if(cref == in) {
//				result = substitute;
//			}
//		}
//		return result;
	}

	public JavaTypeReference componentTypeReference() {
		return ((JavaTypeReference)actualReference()).componentTypeReference();
	}

	public JavaTypeReference erasedReference() {
		JavaTypeReference erasedReference = ((JavaTypeReference)actualReference()).erasedReference();
		NonLocalJavaTypeReference result = new NonLocalJavaTypeReference(erasedReference, lookupParent());
		return result;
	}

//	public Type erasure() throws LookupException {
//		JavaTypeReference actualReference = actualReference();
//		return actualReference.erasure();
//	}

	public JavaTypeReference toArray(int arrayDimension) {
  	JavaTypeReference result = new ArrayTypeReference(clone(), arrayDimension);
  	return result;
  }

}