package be.kuleuven.cs.distrinet.jnome.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

public class AnnotationType extends ModifierImpl {

	@Override
	protected AnnotationType cloneSelf() {
		return new AnnotationType();
	}

	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language(Java.class).ANNOTATION_TYPE);
	}

}
