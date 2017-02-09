package org.aikodi.java.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.java.core.language.Java7;
import org.aikodi.rejuse.property.PropertySet;

public class Implements extends ModifierImpl {

	public Implements() {}
	
	@Override
	protected Implements cloneSelf() {
		return new Implements();
	}

	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language(Java7.class).IMPLEMENTS_RELATION);
	}

}
