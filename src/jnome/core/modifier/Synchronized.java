package jnome.core.modifier;

import jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class Synchronized extends ModifierImpl {
	
	public Synchronized() {
	}

	@Override
	public Synchronized clone() {
		return new Synchronized();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java)language()).SYNCHRONIZED);
	}
	
}
