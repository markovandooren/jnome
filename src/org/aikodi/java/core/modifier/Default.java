package org.aikodi.java.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.java.core.language.Java7;
import org.aikodi.rejuse.property.PropertySet;


/**
 * The default modifier. This modifier indicates that a method in an interface
 * has a default implementation.
 * 
 * @author Marko van Dooren
 */
public class Default extends ModifierImpl {

	@Override
	protected Default cloneSelf() {
	  return new Default();
	}

  @Override
  public PropertySet<Element, ChameleonProperty> impliedProperties() {
    return createSet(language(Java7.class).DEFAULT);
  }
}
