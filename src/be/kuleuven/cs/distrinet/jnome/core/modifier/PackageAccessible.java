package be.kuleuven.cs.distrinet.jnome.core.modifier;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

/**
 * @author Marko van Dooren
 * @author Tim Laeremans
 */
public class PackageAccessible extends ModifierImpl {

  public PackageAccessible() {
  }
  

	@Override
	protected PackageAccessible cloneSelf() {
		return new PackageAccessible();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(language().property(PackageProperty.ID));
	}  
}
