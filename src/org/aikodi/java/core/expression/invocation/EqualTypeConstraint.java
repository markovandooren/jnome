/**
 * 
 */
package org.aikodi.java.core.expression.invocation;


import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.type.JavaTypeReference;

public class EqualTypeConstraint extends SecondPhaseConstraint {

	public EqualTypeConstraint(TypeParameter param, TypeReference type) {
		super(param,type);
	}
	
	public void process() throws LookupException {
		Type Utype = U();
		if(Utype instanceof TypeVariable && parent().typeParameters().contains(((TypeVariable)Utype).parameter())) {
			TypeVariable U = (TypeVariable) Utype;
			FormalTypeParameter parameter = U.parameter();
			if(parameter.sameAs(typeParameter())) {
				// Otherwise, if U is Tj, then this constraint carries no information and may be discarded.
			} else {
				// Otherwise, the constraint is of the form T j = T k for j ≠ k. Then all constraints
				// involving T j are rewritten such that T j is replaced with T k , and processing
				// continues with the next type variable.
				JavaTypeReference tref = typeParameter().language(Java7.class).createTypeReference(parameter.signature().name());
				tref.setUniParent(parameter);
				substituteRHS(tref);
				substitute(U.parameter());
				parent().add(new IndirectTypeAssignment(typeParameter(), U.parameter()));
			}
		} else {
      // If U is not one of the type parameters of the method, then U is the type inferred
			// for T j . Then all remaining constraints involving T j are rewritten such that T j is
      // replaced with U . There are necessarily no further equality constraints involving
      // T j , and processing continues with the next type parameter, if any.
			substituteRHS(URef());
			parent().add(new ActualTypeAssignment(typeParameter(), Utype));
		}
		parent().remove(this);
	}
	
	/**
	 * Replace the typeParameter() of this constraint with a clone of the given type reference in the other constraints.
	 * The clone will direct its lookup to the parent of the given type reference to avoid name capture.
	 */
	private void substituteRHS(TypeReference tref) throws LookupException {
		parent().substituteRHS(tref, this);
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
	
	@Override
	public String toString() {
		return this.typeParameter().name() + " = " + this.URef().toString();
	}

}
