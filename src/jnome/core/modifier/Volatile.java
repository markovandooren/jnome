package jnome.core.modifier;



import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierContainer;
import chameleon.core.modifier.ModifierImpl;

/**
 * @author Marko van Dooren
 */
public class Volatile extends ModifierImpl<Volatile,ModifierContainer> {

  public Volatile() {
  }

	@Override
	public Volatile clone() {
		return new Volatile();
	}

	public PropertySet<Element> impliedProperties() {
		return createSet(((Java)language()).VOLATILE);
	}
}
