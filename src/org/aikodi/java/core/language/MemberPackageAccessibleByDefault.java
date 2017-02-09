package org.aikodi.java.core.language;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Declarator;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.property.PropertyRule;
import org.aikodi.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import org.aikodi.rejuse.property.PropertySet;

public class MemberPackageAccessibleByDefault extends PropertyRule<Declarator> {

	public MemberPackageAccessibleByDefault() {
		super(Declarator.class);
	}

	@Override
	public PropertySet<Element, ChameleonProperty> suggestedProperties(Declarator element) {
		PropertySet<Element,ChameleonProperty> result;
		if(element instanceof Declaration || element instanceof MemberVariableDeclarator) {
			result = createSet(language(Java7.class).PACKAGE_ACCESSIBLE);
		} else {
			result = new PropertySet<Element,ChameleonProperty>();
		}
		return result;
	}

}
