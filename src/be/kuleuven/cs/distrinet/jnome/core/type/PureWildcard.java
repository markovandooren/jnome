package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.CapturedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.ExtendsConstraint;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class PureWildcard extends TypeArgument {

	public PureWildcard() {

	}

	public TypeParameter capture(FormalTypeParameter formal, List<TypeConstraint> accumulator) {
		CapturedTypeParameter newParameter = new CapturedTypeParameter(formal.name());
		List<TypeConstraint> constraints = formal.constraints();
		for(TypeConstraint constraint: constraints) {
			TypeConstraint clone = cloneAndResetTypeReference(constraint,constraint);
			newParameter.addConstraint(clone);
			accumulator.add(clone);
		}

		//FIXME This should actually be determined by the type parameter itself.
		//      perhaps it should compute its own upper bound reference
		if(constraints.size() == 0) {
			Java7 java = language(Java7.class);
			BasicJavaTypeReference objectRef = java.createTypeReference(java.getDefaultSuperClassFQN());
			TypeReference tref = java.createNonLocalTypeReference(objectRef,namespace().defaultNamespace());
			newParameter.addConstraint(new ExtendsConstraint(tref));
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
		List<TypeArgument> args = nearestAncestor.typeArguments();
		int index = args.indexOf(this);
		// Wrong, this should not be the type constructor, we need to take into account the 
		// type instance
		Type typeConstructor = nearestAncestor.typeConstructor();
		Type typeInstance = nearestAncestor.getElement();
		TypeParameter formalParameter = typeConstructor.parameter(TypeParameter.class,index);
		TypeParameter actualParameter = typeInstance.parameter(TypeParameter.class, index);
		TypeReference formalUpperBoundReference = formalParameter.upperBoundReference();
		TypeReference clonedUpperBoundReference = clone(formalUpperBoundReference);
		clonedUpperBoundReference.setUniParent(actualParameter);
		
//		Type result = formalParameter.upperBound(); // This fixes testGenericRejuse
		Type result = clonedUpperBoundReference.getElement();
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

	public String toString(java.util.Set<Element> visited) {
		return "?";
	}

	@Override
	public boolean isWildCardBound() {
		return true;
	}

}
