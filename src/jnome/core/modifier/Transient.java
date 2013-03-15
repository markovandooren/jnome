package jnome.core.modifier;

import jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class Transient extends ModifierImpl {

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
