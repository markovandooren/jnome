package jnome.core.modifier;

import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertyUniverse;

import chameleon.core.MetamodelException;
import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceScope;
import chameleon.core.scope.ScopeProperty;
import chameleon.core.scope.Scope;
import chameleon.core.type.Type;

public class PackageProperty extends ScopeProperty {
	
	public final static String ID = "accessibility.package";
	
	public PackageProperty(PropertyUniverse<Element> universe, PropertyMutex<Element> family) {
		super(ID, universe, family);
	}

	public PackageProperty(String name, PropertyUniverse<Element> universe, PropertyMutex<Element> family) {
		super(name, universe, family);
	}

	public Scope scope(Element element) throws MetamodelException {
		try {
			return new NamespaceScope(((Type)element).getNamespace());
		} catch (ClassCastException exc) {
			throw new MetamodelException();
		}
	}
}
