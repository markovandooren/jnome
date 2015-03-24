package be.kuleuven.cs.distrinet.jnome.eclipse;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ElementWithModifiers;
import org.aikodi.chameleon.eclipse.presentation.treeview.PrefixIconDecorator;
import org.aikodi.chameleon.exception.ModelException;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class AccessIconDecorator extends PrefixIconDecorator {

	@Override
	public boolean appliesTo(Element element) throws ModelException {
		return element instanceof ElementWithModifiers;
	}

	@Override
	public String prefix(Element element) throws ModelException {
		Java7 language = element.language(Java7.class);
		String result;
		if(element.isTrue(language.PRIVATE)) {
			result = "private";
		} else if(element.isTrue(language.PROTECTED)) {
			result = "protected";
		} else if(element.isTrue(language.PUBLIC)) {
			result = "public";
		} else {
			result = "default";
		}
		return result;

	}

}
