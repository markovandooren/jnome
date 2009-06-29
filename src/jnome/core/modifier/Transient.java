package jnome.core.modifier;

import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierContainer;
import chameleon.core.modifier.ModifierImpl;

/**
 * @author Marko van Dooren
 */
public class Transient extends ModifierImpl<Transient,ModifierContainer> {

  public Transient() {
  }

	@Override
	public Transient clone() {
		return new Transient();
	}

	public PropertySet<Element> impliedProperties() {
		return createSet(((Java)language()).TRANSIENT);
	}
  
}
