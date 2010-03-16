/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;

public class EQConstraint extends FirstPhaseConstraint {

	public EQConstraint(Type type, Type tref) {
		super(type,tref);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		return null;
	}
	
	
	public EqualTypeConstraint FequalsTj(TypeParameter declarator, Type type) {
		return new EqualTypeConstraint(declarator, type);
	}
	
	public FirstPhaseConstraint Array(Type componentType, Type componentTypeReference) {
		return new EQConstraint(componentType, componentTypeReference);
	}

	@Override
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		processCaseSSFormalExtends(result, U, index, BasicTypeArgument.class);
	}

	@Override
	public void caseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U, int index)
			throws LookupException {
		processCaseSSFormalExtends(result, U, index, ExtendsWildCard.class);
	}

	@Override
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		processCaseSSFormalExtends(result, U, index, SuperWildCard.class);
	}

	private void processCaseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U, int index, Class<? extends ActualTypeArgument> t)
	throws LookupException {
		try {
			TypeParameter ithTypeParameterOfA = A().parameters().get(index);

			if(ithTypeParameterOfA instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfA).argument();
				if(t.isInstance(arg)) {
					Type V = arg.type();
					EQConstraint recursive = new EQConstraint(V, U.getElement());
					result.addAll(recursive.process());
				}

			}
		}
		catch(IndexOutOfBoundsException exc) {
			return;
		}
	}


}