package org.aikodi.java.eclipse;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.eclipse.presentation.treeview.AbstractIconProvider;
import org.aikodi.chameleon.eclipse.presentation.treeview.NameBasedIconDecorator;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.java.core.language.Java7;

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
		Java7 language = element.language(Java7.class);
		if(element.isTrue(language.INTERFACE())) {
			result = "interface";
		} else {
			result = "class";
		}
		return result;
	}

}
