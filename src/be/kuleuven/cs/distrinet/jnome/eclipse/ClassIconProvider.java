package be.kuleuven.cs.distrinet.jnome.eclipse;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.eclipse.presentation.treeview.AbstractIconProvider;
import org.aikodi.chameleon.eclipse.presentation.treeview.NameBasedIconDecorator;
import org.aikodi.chameleon.oo.type.Type;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;

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
