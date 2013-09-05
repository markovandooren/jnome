package be.kuleuven.cs.distrinet.jnome.core.language;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.property.PropertyRule;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

public class TypeReferenceByDefault extends PropertyRule<Type> {

	public TypeReferenceByDefault() {
		super(Type.class);
	}

	@Override
	public PropertySet<Element, ChameleonProperty> suggestedProperties(Type element) {
		return createSet(language(ObjectOrientedLanguage.class).REFERENCE_TYPE);
	}

}
