package jnome.core.modifier;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierContainer;
import chameleon.core.modifier.ModifierImpl;

/**
 * @author Marko van Dooren
 * @author Tim Laeremans
 */
public class PackageAccessible extends ModifierImpl<PackageAccessible,ModifierContainer> {

  public PackageAccessible() {
  }
  

	@Override
	public PackageAccessible clone() {
		return new PackageAccessible();
	}

	public PropertySet<Element> impliedProperties() {
		return createSet(language().property(PackageProperty.ID));
	}  
}
