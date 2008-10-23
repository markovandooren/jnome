package jnome.core.modifier;

import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertyUniverse;

import chameleon.core.MetamodelException;
import chameleon.core.accessibility.AccessibilityDomain;
import chameleon.core.accessibility.AccessibilityProperty;
import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceDomain;
import chameleon.core.type.Type;

public class PackageProperty extends AccessibilityProperty {
	
	public final static String ID = "accessibility.package";
	
	public PackageProperty(PropertyUniverse<Element> universe, PropertyMutex<Element> family) {
		super(ID, universe, family);
	}

	public PackageProperty(String name, PropertyUniverse<Element> universe, PropertyMutex<Element> family) {
		super(name, universe, family);
	}

	public AccessibilityDomain accessibilityDomain(Element element) throws MetamodelException {
		try {
			return new NamespaceDomain(((Type)element).getNamespace());
		} catch (ClassCastException exc) {
			throw new MetamodelException();
		}
	}
}
