package be.kuleuven.cs.distrinet.jnome.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.rejuse.property.PropertySet;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class AnnotationType extends ModifierImpl {

	@Override
	protected AnnotationType cloneSelf() {
		return new AnnotationType();
	}

	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language(Java7.class).ANNOTATION_TYPE);
	}

}
