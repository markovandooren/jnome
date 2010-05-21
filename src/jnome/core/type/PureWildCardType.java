package jnome.core.type;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.lookup.LookupException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.WildCardType;

public class PureWildCardType extends WildCardType {

	public PureWildCardType(Type type) throws LookupException {
		super(new SimpleNameSignature("?"),type ,type.language(ObjectOrientedLanguage.class).getNullType());
	}

	@Override
	public String getFullyQualifiedName() {
		return "?";
	}
	
}
