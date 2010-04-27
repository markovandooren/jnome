/**
 * 
 */
package jnome.core.expression.invocation;


import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;


import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.expression.NamedTarget;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.ConstructedType;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.TypeParameter;

public class EqualTypeConstraint extends SecondPhaseConstraint {

	public EqualTypeConstraint(TypeParameter param, JavaTypeReference type) {
		super(param,type);
	}
	
	public void process() throws LookupException {
		Type Utype = U();
		if(Utype instanceof ConstructedType && parent().typeParameters().contains(((ConstructedType)Utype).parameter())) {
			ConstructedType U = (ConstructedType) Utype;
			FormalTypeParameter parameter = U.parameter();
			if(parameter.sameAs(typeParameter())) {
				// Otherwise, if U is Tj, then this constraint carries no information and may be discarded.
			} else {
				JavaTypeReference tref = typeParameter().language(Java.class).createTypeReference(parameter.signature().name());
				tref.setUniParent(parameter);
				substituteRHS(tref);
				substitute(U.parameter());
				parent().add(new IndirectTypeAssignment(typeParameter(), U.parameter()));
			}
		} else {
			substituteRHS(URef());
			parent().add(new ActualTypeAssignment(typeParameter(), Utype));
		}
		parent().remove(this);
	}
	
	/**
	 * Replace the typeParameter() of this constraint with a clone of the given type reference in the other constraints.
	 * The clone will direct its lookup to the parent of the given type reference to avoid name capture.
	 */
	private void substituteRHS(JavaTypeReference tref) throws LookupException {
		for(SecondPhaseConstraint constraint: parent().constraints()) {
			if(constraint != this) {
				if(constraint.typeParameter().sameAs(typeParameter())) {
					parent().remove(constraint);
				} else {
					final TypeParameter tp = typeParameter();
					JavaTypeReference uRef = constraint.URef();
					NonLocalJavaTypeReference.replace(tref, tp, uRef);
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