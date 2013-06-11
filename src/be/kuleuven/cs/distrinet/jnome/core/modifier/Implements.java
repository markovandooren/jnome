package be.kuleuven.cs.distrinet.jnome.core.modifier;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;

public class Implements extends ModifierImpl {

	public Implements() {}
	
	@Override
	protected Implements cloneSelf() {
		return new Implements();
	}

	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language(Java.class).IMPLEMENTS_RELATION);
	}

}
