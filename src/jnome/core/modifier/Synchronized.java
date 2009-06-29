package jnome.core.modifier;

import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierContainer;
import chameleon.core.modifier.ModifierImpl;

/**
 * @author Marko van Dooren
 */
public class Synchronized extends ModifierImpl<Synchronized,ModifierContainer> {
	
	public Synchronized() {
	}

	@Override
	public Synchronized clone() {
		return new Synchronized();
	}

	public PropertySet<Element> impliedProperties() {
		return createSet(((Java)language()).SYNCHRONIZED);
	}
	
}
