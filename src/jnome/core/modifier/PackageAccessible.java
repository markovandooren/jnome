package jnome.core.modifier;

import java.util.Set;

import org.rejuse.property.Property;

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
  
//  public boolean atLeastAsVisibleAs(AccessModifier other) {
//    return ! (other instanceof Public) &&
//           ! (other instanceof Protected);
//  }
//
//  public AccessibilityDomain getAccessibilityDomain(Type type) {
//    return new NamespaceDomain(type.getNamespace());
//  }

	@Override
	public PackageAccessible clone() {
		return new PackageAccessible();
	}

	public Set<Property<Element>> impliedProperties() {
		return createSet(language().property(PackageProperty.ID));
	}  
}
