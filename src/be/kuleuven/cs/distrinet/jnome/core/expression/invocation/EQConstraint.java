/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeArgumentWithTypeReference;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

public class EQConstraint extends FirstPhaseConstraint {

	public EQConstraint(JavaTypeReference A, Type F) {
		super(A,F);
	}

//	@Override
//	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
//		return new ArrayList<SecondPhaseConstraint>();
//	}
	
	@Override
	public EqualTypeConstraint FequalsTj(TypeParameter declarator, JavaTypeReference type) {
		return new EqualTypeConstraint(declarator, type);
	}
	
	@Override
	public FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference) {
		EQConstraint eqConstraint = new EQConstraint(componentType, componentTypeReference);
		parent().addGenerated(eqConstraint);
		eqConstraint.setUniParent(parent());
		return eqConstraint;
	}

	@Override
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		processCaseSSFormalExtends(result, U, index, EqualityTypeArgument.class);
	}

	@Override
	public void caseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U, int index)
			throws LookupException {
		processCaseSSFormalExtends(result, U, index, ExtendsWildcard.class);
	}

	@Override
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		processCaseSSFormalExtends(result, U, index, SuperWildcard.class);
	}

	private void processCaseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U, int index, Class<? extends TypeArgumentWithTypeReference> t)
	throws LookupException {
		try {
			TypeParameter ithTypeParameterOfA = A().parameters(TypeParameter.class).get(index);

			if(ithTypeParameterOfA instanceof InstantiatedTypeParameter) {
				TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfA).argument();
				if(t.isInstance(arg)) {
					JavaTypeReference V = (JavaTypeReference) ((TypeArgumentWithTypeReference)arg).typeReference();
					EQConstraint recursive = new EQConstraint(V, U.getElement());
					recursive.setUniParent(parent());
					result.addAll(recursive.process());
				}

			}
		}
		catch(IndexOutOfBoundsException exc) {
			return;
		}
	}

	@Override
	public String toString() {
		return this.ARef().toString() +" = " +this.F().toString();
	}

}
