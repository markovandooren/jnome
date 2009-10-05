package jnome.core.modifier;

import jnome.core.language.Java;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class StrictFP extends ModifierImpl<StrictFP,Element> {

  public StrictFP() {
  }

	@Override
	public StrictFP clone() {
		return new StrictFP();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java)language()).STRICTFP);
	}
  
}
