package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.WildCardType;

public class PureWildCardType extends WildCardType {

	public PureWildCardType(Type type) throws LookupException {
		super("?",type ,type.language(ObjectOrientedLanguage.class).getNullType(type.view().namespace()));
	}

	@Override
	public String getFullyQualifiedName() {
		return "?";
	}

}
