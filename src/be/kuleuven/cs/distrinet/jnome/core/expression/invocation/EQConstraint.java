/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;

public class EQConstraint extends FirstPhaseConstraint {

	public EQConstraint(JavaTypeReference A, Type F) {
		super(A,F);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		return new ArrayList<SecondPhaseConstraint>();
	}
	
	@Override
	public EqualTypeConstraint FequalsTj(TypeParameter declarator, JavaTypeReference type) {
		return new EqualTypeConstraint(declarator, type);
	}
	
	@Override
	public FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference) {
		EQConstraint eqConstraint = new EQConstraint(componentType, componentTypeReference);
		eqConstraint.setUniParent(parent());
		return eqConstraint;
	}

	@Override
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		processCaseSSFormalExtends(result, U, index, BasicTypeArgument.class);
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

	private void processCaseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U, int index, Class<? extends ActualTypeArgumentWithTypeReference> t)
	throws LookupException {
		try {
			TypeParameter ithTypeParameterOfA = A().parameters(TypeParameter.class).get(index);

			if(ithTypeParameterOfA instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfA).argument();
				if(t.isInstance(arg)) {
					JavaTypeReference V = (JavaTypeReference) ((ActualTypeArgumentWithTypeReference)arg).typeReference();
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


}
