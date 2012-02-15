package jnome.core.language;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.PropertyRule;
import chameleon.oo.member.Member;
import chameleon.oo.type.TypeElement;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;

public class MemberPackageAccessibleByDefault extends PropertyRule<TypeElement> {

	public MemberPackageAccessibleByDefault() {
		super(TypeElement.class);
	}

	@Override
	public PropertySet<Element, ChameleonProperty> suggestedProperties(TypeElement element) {
		PropertySet<Element,ChameleonProperty> result;
		if(element instanceof Member || element instanceof MemberVariableDeclarator) {
			result = createSet(language(Java.class).PACKAGE_ACCESSIBLE);
		} else {
			result = new PropertySet<Element,ChameleonProperty>();
		}
		return result;
	}

}