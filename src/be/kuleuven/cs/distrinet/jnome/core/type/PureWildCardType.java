package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.WildCardType;

public class PureWildCardType extends WildCardType {

	public PureWildCardType(Type type) throws LookupException {
		super("?",type ,type.language(ObjectOrientedLanguage.class).getNullType(type.view().namespace()));
	}

	@Override
	public String getFullyQualifiedName() {
		return "?";
	}

}
