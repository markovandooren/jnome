package jnome.core.modifier;

import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

public class AnnotationType extends ModifierImpl<AnnotationType> {

	@Override
	public AnnotationType clone() {
		return new AnnotationType();
	}

	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language(Java.class).ANNOTATION_TYPE);
	}

}
