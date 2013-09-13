package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.CapturedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;

public class PureWildcard extends ActualTypeArgument {

	public PureWildcard() {
		
	}
	
	public TypeParameter capture(FormalTypeParameter formal, List<TypeConstraint> accumulator) {
		CapturedTypeParameter newParameter = new CapturedTypeParameter(clone(formal.signature()));
		for(TypeConstraint constraint: formal.constraints()) {
			TypeConstraint clone = cloneAndResetTypeReference(constraint,constraint);
			newParameter.addConstraint(clone);
			accumulator.add(clone);
		}
   return newParameter;
	}

	@Override
	protected PureWildcard cloneSelf() {
		return new PureWildcard();
	}

	// TypeVariable concept invoeren, en lowerbound,... verplaatsen naar daar? Deze is context sensitive. Hoewel, dat
	// wordt toch nooit direct vergeleken. Er moet volgens mij altijd eerst gecaptured worden, dus dan moet dit inderdaad
	// verplaatst worden. NOPE, niet altijd eerst capturen.
	@Override
	public Type lowerBound() throws LookupException {
		View view = view();
		ObjectOrientedLanguage l = view.language(ObjectOrientedLanguage.class);
		return l.getNullType(view.namespace());
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

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

	public String toString() {
		return "?";
	}
}
