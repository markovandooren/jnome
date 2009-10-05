package jnome.core.modifier;

import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class Transient extends ModifierImpl<Transient,Element> {

  public Transient() {
  }

	@Override
	public Transient clone() {
		return new Transient();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java)language()).TRANSIENT);
	}
  
}
