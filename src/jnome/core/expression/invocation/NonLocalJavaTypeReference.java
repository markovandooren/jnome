/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import org.rejuse.association.SingleAssociation;
import org.rejuse.predicate.UnsafePredicate;

import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.reference.CrossReference;
import chameleon.core.type.generics.TypeParameter;

public class NonLocalJavaTypeReference extends BasicJavaTypeReference {

	public NonLocalJavaTypeReference(BasicJavaTypeReference tref) {
		super(tref.getTarget(), (SimpleNameSignature)tref.signature().clone());
		setArrayDimension(tref.arrayDimension());
		_lookupParent = tref.parent();
	}
	
	public NonLocalJavaTypeReference(String fqn, Element lookupParent) {
		super(fqn);
		_lookupParent = lookupParent;
	}
	
	

	public NonLocalJavaTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, SimpleNameSignature signature, int arrayDimension, Element lookupParent) {
		super(target,signature);
		_lookupParent = lookupParent;
		setArrayDimension(arrayDimension);
	}

	@Override
	public LookupStrategy lexicalLookupStrategy() throws LookupException {
		return lookupParent().lexicalLookupStrategy();
	}
	
	public Element lookupParent() {
		return _lookupParent;
	}
	
	@Override
	public NonLocalJavaTypeReference clone() {
		return new NonLocalJavaTypeReference(getTarget().clone(),(SimpleNameSignature) signature().clone(), arrayDimension(), lookupParent());
	}
	
	public static void replace(JavaTypeReference replacement, final TypeParameter parameterToBeReplaced, JavaTypeReference<?> in) throws LookupException {
		List<CrossReference> crefs = in.descendants(CrossReference.class, 
				new UnsafePredicate<CrossReference, LookupException>() {
			@Override
			public boolean eval(CrossReference object) throws LookupException {
				return object.getElement().sameAs(parameterToBeReplaced);
			}
		});
		for(CrossReference cref: crefs) {
			JavaTypeReference substitute = new NonLocalJavaTypeReference((BasicJavaTypeReference) replacement);
			SingleAssociation crefParentLink = cref.parentLink();
			crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
		}
	}

	private Element _lookupParent;
	
}