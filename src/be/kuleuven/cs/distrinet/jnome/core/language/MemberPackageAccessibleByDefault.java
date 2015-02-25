package be.kuleuven.cs.distrinet.jnome.core.language;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.property.PropertyRule;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.type.TypeElement;
import org.aikodi.chameleon.support.member.simplename.variable.MemberVariableDeclarator;

import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

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
