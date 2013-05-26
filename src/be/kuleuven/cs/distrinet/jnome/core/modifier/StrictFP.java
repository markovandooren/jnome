package be.kuleuven.cs.distrinet.jnome.core.modifier;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class StrictFP extends ModifierImpl {

  public StrictFP() {
  }

	@Override
	protected StrictFP cloneSelf() {
		return new StrictFP();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(((Java)language()).STRICTFP);
	}
  
}
