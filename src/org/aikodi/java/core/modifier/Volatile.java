package org.aikodi.java.core.modifier;



import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.java.core.language.Java7;
import org.aikodi.rejuse.property.PropertySet;

/**
 * @author Marko van Dooren
 */
public class Volatile extends ModifierImpl {

  public Volatile() {
  }

	@Override
	protected Volatile cloneSelf() {
		return new Volatile();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java7)language()).VOLATILE);
	}
}
