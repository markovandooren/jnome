package be.kuleuven.cs.distrinet.jnome.eclipse;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ElementWithModifiers;
import be.kuleuven.cs.distrinet.chameleon.eclipse.presentation.treeview.PrefixIconDecorator;
import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;

public class AccessIconDecorator extends PrefixIconDecorator {

	@Override
	public boolean appliesTo(Element element) throws ModelException {
		return element instanceof ElementWithModifiers;
	}

	@Override
	public String prefix(Element element) throws ModelException {
		Java language = element.language(Java.class);
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
