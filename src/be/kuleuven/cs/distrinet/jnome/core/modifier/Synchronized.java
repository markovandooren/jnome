package be.kuleuven.cs.distrinet.jnome.core.modifier;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

/**
 * @author Marko van Dooren
 */
public class Synchronized extends ModifierImpl {
	
	public Synchronized() {
	}

	@Override
	protected Synchronized cloneSelf() {
		return new Synchronized();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java)language()).SYNCHRONIZED);
	}
	
}
