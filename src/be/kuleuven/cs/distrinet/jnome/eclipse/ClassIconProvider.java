package be.kuleuven.cs.distrinet.jnome.eclipse;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.eclipse.presentation.treeview.AbstractIconProvider;
import be.kuleuven.cs.distrinet.chameleon.eclipse.presentation.treeview.NameBasedIconDecorator;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;

public class ClassIconProvider extends AbstractIconProvider {

	public ClassIconProvider(NameBasedIconDecorator decorator) {
		super(Type.class, decorator);
	}

	public ClassIconProvider() {
		super(Type.class);
	}

	@Override
	public String baseIconName(Element element) {
		String result;
		Java language = element.language(Java.class);
		if(element.isTrue(language.INTERFACE)) {
			result = "interface";
		} else {
			result = "class";
		}
		return result;
	}

}
