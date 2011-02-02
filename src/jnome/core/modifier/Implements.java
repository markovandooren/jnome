package jnome.core.modifier;

import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

public class Implements extends ModifierImpl<Implements> {

	@Override
	public Implements clone() {
		return new Implements();
	}

	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language(Java.class).IMPLEMENTS_RELATION);
	}

}
