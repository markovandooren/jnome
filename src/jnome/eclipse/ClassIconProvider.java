package jnome.eclipse;

import jnome.core.language.Java;
import chameleon.core.element.Element;
import chameleon.eclipse.presentation.treeview.AbstractIconProvider;
import chameleon.eclipse.presentation.treeview.NameBasedIconDecorator;
import chameleon.oo.type.Type;

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
