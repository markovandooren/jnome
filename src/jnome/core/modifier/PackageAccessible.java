package jnome.core.modifier;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 * @author Tim Laeremans
 */
public class PackageAccessible extends ModifierImpl<PackageAccessible> {

  public PackageAccessible() {
  }
  

	@Override
	public PackageAccessible clone() {
		return new PackageAccessible();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(language().property(PackageProperty.ID));
	}  
}
