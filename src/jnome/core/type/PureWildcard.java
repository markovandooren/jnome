package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.CapturedTypeParameter;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.TypeConstraint;
import chameleon.oo.type.generics.TypeParameter;

public class PureWildcard<E extends PureWildcard> extends ActualTypeArgument<E> {

	public PureWildcard() {
		
	}
	
	public TypeParameter capture(FormalTypeParameter formal, List<TypeConstraint> accumulator) {
		CapturedTypeParameter newParameter = new CapturedTypeParameter(formal.signature().clone());
		for(TypeConstraint constraint: formal.constraints()) {
//			newParameter.addConstraint(constraint.clone());
			TypeConstraint clone = cloneAndResetTypeReference(constraint,constraint);
			newParameter.addConstraint(clone);
			accumulator.add(clone);
		}
   return newParameter;
	}

	@Override
	public E clone() {
		return (E) new PureWildcard();
	}

	// TypeVariable concept invoeren, en lowerbound,... verplaatsen naar daar? Deze is context sensitive. Hoewel, dat
	// wordt toch nooit direct vergeleken. Er moet volgens mij altijd eerst gecaptured worden, dus dan moet dit inderdaad
	// verplaatst worden. NOPE, niet altijd eerst capturen.
	@Override
	public Type lowerBound() throws LookupException {
		return language(ObjectOrientedLanguage.class).getNullType();
	}

	@Override
	public Type type() throws LookupException {
		PureWildCardType pureWildCardType = new PureWildCardType(parameterBound());
		pureWildCardType.setUniParent(this);
		return pureWildCardType;
	}
	
	public Type parameterBound() throws LookupException {
		BasicJavaTypeReference nearestAncestor = nearestAncestor(BasicJavaTypeReference.class);
		List<ActualTypeArgument> args = nearestAncestor.typeArguments();
		int index = args.indexOf(this);
		Type base = nearestAncestor.typeConstructor();
		TypeParameter parameter = base.parameter(TypeParameter.class,index+1);
		Type result = parameter.upperBound();
		return result;
	}

	@Override
	public boolean uniSameAs(Element other) throws LookupException {
		return other instanceof PureWildcard;
	}
	
	@Override
	public Type upperBound() throws LookupException {
		//return language(ObjectOrientedLanguage.class).getDefaultSuperClass();
		return parameterBound();
	}

	public List<Element> children() {
		return new ArrayList<Element>();
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}
