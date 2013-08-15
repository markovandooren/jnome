package be.kuleuven.cs.distrinet.jnome.core.language;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.property.PropertyRule;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeElement;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
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
