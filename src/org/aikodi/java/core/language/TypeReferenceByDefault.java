package org.aikodi.java.core.language;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.property.PropertyRule;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.rejuse.property.PropertySet;

public class TypeReferenceByDefault extends PropertyRule<Type> {

	public TypeReferenceByDefault() {
		super(Type.class);
	}

	@Override
	public PropertySet<Element, ChameleonProperty> suggestedProperties(Type element) {
		return createSet(language(ObjectOrientedLanguage.class).REFERENCE_TYPE);
	}

}
