package be.kuleuven.cs.distrinet.jnome.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;

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
