/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;

import org.rejuse.association.SingleAssociation;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.oo.type.NonLocalTypeReference;
import chameleon.util.CreationStackTrace;

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
	
	public static JavaTypeReference replace(JavaTypeReference replacement, final Declaration declarator, JavaTypeReference<?> in) throws LookupException {
		JavaTypeReference result = in;
		UnsafePredicate<BasicJavaTypeReference, LookupException> predicate = new UnsafePredicate<BasicJavaTypeReference, LookupException>() {
			@Override
			public boolean eval(BasicJavaTypeReference object) throws LookupException {
				return object.getDeclarator().sameAs(declarator);
			}
		};
		List<BasicJavaTypeReference> crefs = in.descendants(BasicJavaTypeReference.class, 
				predicate);
		if(in instanceof BasicJavaTypeReference) {
			BasicJavaTypeReference in2 = (BasicJavaTypeReference) in;
			if(predicate.eval(in2)) {
				crefs.add(in2);
			}
		}
		for(BasicJavaTypeReference cref: crefs) {
			JavaTypeReference substitute;
			if(replacement.isDerived()) {
				Element oldParent = replacement.parent();
//				replacement.setUniParent(null);
			  substitute = new NonLocalJavaTypeReference(replacement.clone(),oldParent);
			  substitute.setOrigin(replacement);
			} else {
			  substitute = new NonLocalJavaTypeReference(replacement.clone());
			}
			if(! cref.isDerived()) {
				SingleAssociation crefParentLink = cref.parentLink();
				crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
			} else {
				substitute.setUniParent(in.parent());
			}
			if(cref == in) {
				result = substitute;
			}
		}
		return result;
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