package be.kuleuven.cs.distrinet.jnome.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.rejuse.property.PropertySet;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

/**
 * @author Marko van Dooren
 */
public class Transient extends ModifierImpl {

  public Transient() {
  }

	@Override
	protected Transient cloneSelf() {
		return new Transient();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java7)language()).TRANSIENT);
	}
  
}
