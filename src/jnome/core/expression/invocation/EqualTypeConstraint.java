/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import org.rejuse.predicate.UnsafePredicate;

import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.type.ConstructedType;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.TypeParameter;

public class EqualTypeConstraint extends SecondPhaseConstraint {

	public EqualTypeConstraint(TypeParameter param, JavaTypeReference type) {
		super(param,type);
	}
	
	public void process() throws LookupException {
		if(U() instanceof ConstructedType) {
			ConstructedType U = (ConstructedType) U();
			FormalTypeParameter parameter = U.parameter();
			if(parameter.sameAs(typeParameter())) {
				// Otherwise, if U is Tj, then this constraint carries no information and may be discarded.
				parent().remove(this);
			} else {
				JavaTypeReference tref = new JavaTypeReference(parameter.signature().name());
				tref.setUniParent(parameter);
				substitute(tref);
				substitute(U.parameter());
			}
		} else {
			JavaTypeReference tref = URef().clone();
			tref.setUniParent(URef().parent());
			substitute(tref);
			// perform substitution
		}
	}
	
	private void substitute(JavaTypeReference tref) throws LookupException {
		for(SecondPhaseConstraint constraint: parent().constraints()) {
			if(constraint != this) {
				if(constraint.typeParameter().sameAs(typeParameter())) {
					parent().remove(constraint);
				} else {
				List<CrossReference> crefs = constraint.URef().descendants(CrossReference.class, 
              new UnsafePredicate<CrossReference, LookupException>() {
								@Override
								public boolean eval(CrossReference object) throws LookupException {
									return object.getElement().sameAs(EqualTypeConstraint.this.typeParameter());
								}
							});
				}
			}
		}
	}
	
	private void substitute(TypeParameter param) throws LookupException {
		for(SecondPhaseConstraint constraint: parent().constraints()) {
			if(constraint != this) {
				if(constraint.typeParameter().sameAs(typeParameter())) {
					constraint.setTypeParameter(param);
				}
			}
		}
	}
}